/**
 * Copyright 2016-2020 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.nukleus.maven.plugin.internal.generate;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder.NETWORK;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BIT_UTIL_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNamedNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNamedNode.Kind;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstTypedefNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstVariantNode;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public final class ListFlyweightGenerator extends ClassSpecGenerator
{
    private static final Map<TypeName, String> GETTER_NAMES;
    private static final Map<TypeName, String> PUTTER_NAMES;
    private static final Map<TypeName, String[]> UNSIGNED_INT_RANGES;
    private static final Map<TypeName, String> TYPE_NAMES;
    private static final Set<String> RESERVED_METHOD_NAMES;
    private static final String LENGTH = "LENGTH";
    private static final String FIELD_COUNT = "FIELD_COUNT";
    private static final String BIT_MASK = "BIT_MASK";
    private static final String FIRST_FIELD = "FIRST_FIELD";

    static
    {
        Map<TypeName, String> getterNames = new HashMap<>();
        getterNames.put(TypeName.BYTE, "getByte");
        getterNames.put(TypeName.SHORT, "getShort");
        getterNames.put(TypeName.INT, "getInt");
        getterNames.put(TypeName.LONG, "getLong");
        GETTER_NAMES = unmodifiableMap(getterNames);

        Map<TypeName, String> putterNames = new HashMap<>();
        putterNames.put(TypeName.BYTE, "putByte");
        putterNames.put(TypeName.SHORT, "putShort");
        putterNames.put(TypeName.INT, "putInt");
        putterNames.put(TypeName.LONG, "putLong");
        PUTTER_NAMES = unmodifiableMap(putterNames);

        Map<TypeName, String[]> unsigned = new HashMap<>();
        unsigned.put(TypeName.BYTE, new String[] {"0", "0xFFFF_FFFF_FFFF_FF00L"});
        unsigned.put(TypeName.SHORT, new String[] {"0", "0xFFFF_FFFF_FFFF_0000L"});
        unsigned.put(TypeName.INT, new String[] {"0", "0xFFFF_FFFF_0000_0000L"});
        unsigned.put(TypeName.LONG, new String[] {"0L", null});
        UNSIGNED_INT_RANGES = unmodifiableMap(unsigned);

        Map<TypeName, String> sizeofByName = new HashMap<>();
        sizeofByName.put(TypeName.BYTE, "BYTE");
        sizeofByName.put(TypeName.SHORT, "SHORT");
        sizeofByName.put(TypeName.INT, "INT");
        sizeofByName.put(TypeName.LONG, "LONG");
        TYPE_NAMES = unmodifiableMap(sizeofByName);

        RESERVED_METHOD_NAMES = new HashSet<>(Arrays.asList("offset", "buffer", "limit", "sizeof", "maxLimit", "wrap",
            "checkLimit", "build", "rewrap"));
    }

    private final String baseName;
    private final TypeSpec.Builder builder;
    private final MemberSizeConstantGenerator memberSizeConstant;
    private final MemberOffsetConstantGenerator memberOffsetConstant;
    private final MaskConstantGenerator maskConstant;
    private final FieldIndexConstantGenerator fieldIndexConstant;
    private final DefaultValueConstantGenerator defaultValueConstant;
    private final MissingFieldByteConstantGenerator nullValueConstant;
    private final TemplateTypeFieldGenerator templateTypeField;
    private final MemberFieldGenerator memberField;
    private final OptionalOffsetsFieldGenerator optionalOffsets;
    private final LengthMethodGenerator lengthMethod;
    private final FieldCountMethodGenerator fieldCountMethod;
    private final FieldsMethodGenerator fieldsMethod;
    private final MemberAccessorGenerator memberAccessor;
    private final WrapMethodGenerator wrapMethod;
    private final TryWrapMethodGenerator tryWrapMethod;
    private final LimitMethodGenerator limitMethod;
    private final ToStringMethodGenerator toStringMethod;
    private final BuilderClassGenerator builderClass;

    public ListFlyweightGenerator(
        ClassName listName,
        ClassName flyweightName,
        String baseName,
        AstType templateType,
        TypeName lengthTypeName,
        TypeName fieldCountTypeName,
        Byte missingFieldByte,
        TypeResolver resolver)
    {
        super(listName);
        this.baseName = baseName;
        this.builder = builder(listName, templateType, resolver);
        this.memberSizeConstant = new MemberSizeConstantGenerator(listName, builder, templateType, lengthTypeName,
            fieldCountTypeName, missingFieldByte);
        this.memberOffsetConstant = new MemberOffsetConstantGenerator(listName, builder, templateType, missingFieldByte);
        this.maskConstant = new MaskConstantGenerator(listName, builder);
        this.fieldIndexConstant = new FieldIndexConstantGenerator(listName, builder);
        this.defaultValueConstant = new DefaultValueConstantGenerator(listName, builder, resolver);
        this.nullValueConstant = new MissingFieldByteConstantGenerator(listName, builder, missingFieldByte, templateType,
            resolver);
        this.templateTypeField = new TemplateTypeFieldGenerator(listName, builder, templateType, resolver);
        this.memberField = new MemberFieldGenerator(listName, builder, resolver);
        this.optionalOffsets = new OptionalOffsetsFieldGenerator(listName, builder, templateType, missingFieldByte);
        this.lengthMethod = new LengthMethodGenerator(listName, builder, templateType, resolver);
        this.fieldCountMethod = new FieldCountMethodGenerator(listName, builder, templateType, fieldCountTypeName, resolver);
        this.fieldsMethod = new FieldsMethodGenerator(listName, builder, templateType, resolver);
        this.memberAccessor = new MemberAccessorGenerator(listName, builder, templateType, resolver, missingFieldByte);
        this.wrapMethod = new WrapMethodGenerator(missingFieldByte, templateType, resolver);
        this.tryWrapMethod = new TryWrapMethodGenerator(missingFieldByte, templateType, resolver);
        this.limitMethod = new LimitMethodGenerator(lengthTypeName, templateType, resolver);
        this.toStringMethod = new ToStringMethodGenerator(missingFieldByte, templateType);
        this.builderClass = new BuilderClassGenerator(listName, flyweightName, templateType, lengthTypeName,
            fieldCountTypeName, resolver, missingFieldByte);
    }

    public ListFlyweightGenerator addMember(
        String name,
        AstType type,
        TypeName typeName,
        TypeName unsignedTypeName,
        int size,
        TypeName sizeType,
        boolean usedAsSize,
        Object defaultValue,
        AstByteOrder byteOrder,
        boolean isRequired,
        AstType arrayItemType,
        AstType arrayItemTypeName,
        AstType arrayItemOfType,
        AstType arrayItemKindType,
        AstType mapKeyType,
        AstType mapKeyKindType,
        AstType mapKeyOfType,
        AstType mapValueType,
        AstType mapValueKindType,
        AstType mapValueOfType)
    {
        memberSizeConstant.addMember(name, typeName);
        fieldIndexConstant.addMember(name);
        maskConstant.addMember(name);
        defaultValueConstant.addMember(name, type, typeName, unsignedTypeName, defaultValue);
        memberField.addMember(name, type, typeName, byteOrder, arrayItemType, arrayItemTypeName, arrayItemOfType, mapKeyType,
            mapValueType);
        optionalOffsets.addMember(name);
        memberAccessor.addMember(name, type, typeName, unsignedTypeName, byteOrder, isRequired, defaultValue, arrayItemType,
            arrayItemTypeName, mapKeyType, mapValueType);
        wrapMethod.addMember(name, typeName, defaultValue, isRequired);
        tryWrapMethod.addMember(name, typeName, defaultValue, isRequired);
        toStringMethod.addMember(name, typeName, defaultValue, isRequired);
        builderClass.addMember(name, type, typeName, unsignedTypeName, size, sizeType, usedAsSize, defaultValue,
            byteOrder, isRequired, arrayItemType, arrayItemTypeName, arrayItemOfType, arrayItemKindType, mapKeyType,
            mapKeyKindType, mapKeyOfType, mapValueType, mapValueKindType, mapValueOfType);
        return this;
    }

    @Override
    public TypeSpec generate()
    {
        memberSizeConstant.build();
        memberOffsetConstant.build();
        maskConstant.build();
        fieldIndexConstant.build();
        defaultValueConstant.build();
        nullValueConstant.build();
        templateTypeField.build();
        memberField.build();
        optionalOffsets.build();
        lengthMethod.build();
        fieldCountMethod.build();
        fieldsMethod.build();
        memberAccessor.build();
        return builder.addField(bitmask())
            .addMethod(wrapMethod.generate())
            .addMethod(tryWrapMethod.generate())
            .addMethod(limitMethod.generate())
            .addMethod(toStringMethod.generate())
            .addType(builderClass.generate())
            .build();
    }

    private TypeSpec.Builder builder(
        ClassName listName,
        AstType templateType,
        TypeResolver resolver)
    {
        final ClassName flyweightName = templateType == null ? resolver.flyweightName() : resolver.resolveClass(AstType.LIST);
        return classBuilder(listName).superclass(flyweightName).addModifiers(PUBLIC, FINAL);
    }

    private FieldSpec bitmask()
    {
        return FieldSpec.builder(long.class, "bitmask", PRIVATE).build();
    }

    private static final class MemberOffsetConstantGenerator extends ClassSpecMixinGenerator
    {
        private final AstType templateType;
        private final Byte nullValue;

        private MemberOffsetConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            AstType templateType,
            Byte nullValue)
        {
            super(thisType, builder);
            this.templateType = templateType;
            this.nullValue = nullValue;
        }

        @Override
        public Builder build()
        {
            if (templateType == null)
            {
                builder.addField(FieldSpec.builder(int.class, offset(LENGTH), PRIVATE, STATIC, FINAL)
                    .initializer("0")
                    .build());
                builder.addField(FieldSpec.builder(int.class, offset(FIELD_COUNT), PRIVATE, STATIC, FINAL)
                    .initializer(String.format("%s + %s", offset(LENGTH), size(LENGTH)))
                    .build());
                if (nullValue == null)
                {
                    builder.addField(FieldSpec.builder(int.class, offset(BIT_MASK), PRIVATE, STATIC, FINAL)
                        .initializer(String.format("%s + %s", offset(FIELD_COUNT), size(FIELD_COUNT)))
                        .build());
                    builder.addField(FieldSpec.builder(int.class, offset(FIRST_FIELD), PRIVATE, STATIC, FINAL)
                        .initializer(String.format("%s + %s", offset(BIT_MASK), size(BIT_MASK)))
                        .build());
                }
                else
                {
                    builder.addField(FieldSpec.builder(int.class, offset(FIRST_FIELD), PRIVATE, STATIC, FINAL)
                        .initializer(String.format("%s + %s", offset(FIELD_COUNT), size(FIELD_COUNT)))
                        .build());
                }
            }
            return super.build();
        }
    }

    private static final class MaskConstantGenerator extends ClassSpecMixinGenerator
    {
        protected MaskConstantGenerator(
            ClassName thisType,
            Builder builder)
        {
            super(thisType, builder);
        }

        public MaskConstantGenerator addMember(
            String name)
        {
            builder.addField(
                FieldSpec.builder(long.class, maskConstant(name), PRIVATE, STATIC, FINAL)
                    .initializer("1 << $L", fieldIndex(name))
                    .build());
            return this;
        }
    }

    private static final class MemberSizeConstantGenerator extends ClassSpecMixinGenerator
    {
        private final List<ListField> fields = new ArrayList<>();
        private final AstType templateType;
        private final TypeName lengthTypeName;
        private final TypeName fieldCountTypeName;
        private final Byte nullValue;

        private MemberSizeConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            AstType templateType,
            TypeName lengthTypeName,
            TypeName fieldCountTypeName,
            Byte nullValue)
        {
            super(thisType, builder);
            this.templateType = templateType;
            this.lengthTypeName = lengthTypeName;
            this.fieldCountTypeName = fieldCountTypeName;
            this.nullValue = nullValue;
        }

        public MemberSizeConstantGenerator addMember(
            String name,
            TypeName type)
        {
            if (type.isPrimitive())
            {
                fields.add(new ListField(name, type, false, null));
            }
            return this;
        }

        @Override
        public Builder build()
        {
            if (templateType == null)
            {
                builder.addField(
                    FieldSpec.builder(int.class, size(LENGTH), PRIVATE, STATIC, FINAL)
                        .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, TYPE_NAMES.get(lengthTypeName))
                        .build());
                builder.addField(
                    FieldSpec.builder(int.class, size(FIELD_COUNT), PRIVATE, STATIC, FINAL)
                        .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, TYPE_NAMES.get(fieldCountTypeName))
                        .build());
                if (nullValue == null)
                {
                    builder.addField(
                        FieldSpec.builder(int.class, size(BIT_MASK), PRIVATE, STATIC, FINAL)
                            .initializer("$T.SIZE_OF_LONG", BIT_UTIL_TYPE)
                            .build());
                }
            }

            for (ListField field : fields)
            {
                builder.addField(
                    FieldSpec.builder(int.class, fieldSize(field.fieldName), PRIVATE, STATIC, FINAL)
                        .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, TYPE_NAMES.get(field.type))
                        .build());
            }
            return super.build();
        }
    }

    private static final class FieldIndexConstantGenerator extends ClassSpecMixinGenerator
    {
        private int index;

        protected FieldIndexConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public FieldIndexConstantGenerator addMember(
            String fieldName)
        {
            builder.addField(FieldSpec.builder(int.class, fieldIndex(fieldName), PRIVATE, STATIC, FINAL)
                .initializer("$L", index)
                .build());
            index++;
            return this;
        }

        @Override
        public Builder build()
        {
            index = 0;
            return super.build();
        }
    }

    private static final class DefaultValueConstantGenerator extends ClassSpecMixinGenerator
    {
        private final TypeResolver resolver;
        DefaultValueConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            TypeResolver resolver)
        {
            super(thisType, builder);
            this.resolver = resolver;
        }

        public DefaultValueConstantGenerator addMember(
            String fieldName,
            AstType type,
            TypeName typeName,
            TypeName unsignedTypeName,
            Object defaultValue)
        {
            TypeName generateType = (unsignedTypeName != null) ? unsignedTypeName : typeName;
            if (defaultValue != null)
            {
                FieldSpec.Builder defaultValueBuilder = FieldSpec.builder(generateType, defaultConstant(fieldName), PRIVATE,
                    STATIC, FINAL);
                AstNamedNode node = resolver.resolve(type.name());
                if (typeName.isPrimitive())
                {
                    defaultValueBuilder.initializer("$L", defaultValue);
                }
                else if (isStringType((ClassName) typeName))
                {
                    defaultValueBuilder.initializer("\"$L\"", defaultValue);
                }
                else
                {
                    if (isTypedefType(node.getKind()))
                    {
                        while (isTypedefType(node.getKind()))
                        {
                            type = ((AstTypedefNode) node).originalType();
                            node = resolver.resolve(type.name());
                        }
                    }
                    if (isVariantType(node.getKind()))
                    {
                        AstVariantNode variantNode = (AstVariantNode) node;
                        AstType ofType = variantNode.of();
                        TypeName typeOfConstant = Objects.requireNonNullElse(resolver.resolveUnsignedType(ofType),
                            resolver.resolveType(ofType));
                        defaultValueBuilder = FieldSpec.builder(typeOfConstant, defaultConstant(fieldName), PRIVATE,
                            STATIC, FINAL);
                        if (ofType.equals(AstType.STRING8) || ofType.equals(AstType.STRING16) || ofType.equals(AstType.STRING32))
                        {
                            defaultValueBuilder.initializer("\"$L\"", defaultValue);
                        }
                        else
                        {
                            defaultValueBuilder.initializer("$L", defaultValue);
                        }
                    }
                    else if (isEnumType(node.getKind()))
                    {
                        AstEnumNode enumNode = (AstEnumNode) node;
                        ClassName enumFlyweightName = (ClassName) typeName;
                        ClassName enumName = enumFlyweightName.peerClass(enumNode.name());
                        defaultValueBuilder = FieldSpec.builder(enumName, defaultConstant(fieldName), PRIVATE, STATIC, FINAL)
                            .initializer("$T.$L", enumName, defaultValue);
                    }
                }
                builder.addField(defaultValueBuilder.build());
            }
            return this;
        }
    }

    private static final class MissingFieldByteConstantGenerator extends ClassSpecMixinGenerator
    {
        private final Byte missingFieldByte;
        private final AstType templateType;
        private final TypeResolver resolver;

        private MissingFieldByteConstantGenerator(
            ClassName thisType,
            Builder builder,
            Byte missingFieldByte,
            AstType templateType,
            TypeResolver resolver)
        {
            super(thisType, builder);
            this.missingFieldByte = missingFieldByte;
            this.templateType = templateType;
            this.resolver = resolver;
        }

        @Override
        public Builder build()
        {
            if (missingFieldByte != null || templateType != null)
            {
                FieldSpec.Builder missingFieldByteBuilder = FieldSpec.builder(byte.class, "MISSING_FIELD_BYTE", PRIVATE,
                    STATIC, FINAL);
                if (templateType == null)
                {
                    missingFieldByteBuilder.initializer(String.valueOf(missingFieldByte));
                }
                else
                {
                    missingFieldByteBuilder.initializer("$T.MISSING_FIELD_PLACEHOLDER", resolver.resolveClass(templateType));
                }
                builder.addField(missingFieldByteBuilder.build())
                    .addField(FieldSpec.builder(int.class, "MISSING_FIELD_BYTE_SIZE", PRIVATE, STATIC, FINAL)
                        .initializer("$T.SIZE_OF_BYTE", BIT_UTIL_TYPE)
                        .build());
            }
            return super.build();
        }
    }

    private static final class TemplateTypeFieldGenerator extends ClassSpecMixinGenerator
    {
        private final AstType templateType;
        private final TypeResolver resolver;

        private TemplateTypeFieldGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            AstType templateType,
            TypeResolver resolver)
        {
            super(thisType, builder);
            this.templateType = templateType;
            this.resolver = resolver;
        }

        @Override
        public Builder build()
        {
            if (templateType != null)
            {
                ClassName templateClassName = resolver.resolveClass(templateType);
                builder.addField(FieldSpec.builder(templateClassName, variantRO(templateClassName), PRIVATE)
                    .initializer("new $T()", templateClassName)
                    .build());
            }
            return super.build();
        }
    }

    private static final class MemberFieldGenerator extends ClassSpecMixinGenerator
    {
        private final TypeResolver resolver;

        private MemberFieldGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            TypeResolver resolver)
        {
            super(thisType, builder);
            this.resolver = resolver;
        }

        public MemberFieldGenerator addMember(
            String name,
            AstType type,
            TypeName typeName,
            AstByteOrder byteOrder,
            AstType arrayItemType,
            AstType arrayItemTypeName,
            AstType arrayItemOfType,
            AstType mapKeyType,
            AstType mapValueType)
        {
            if (!typeName.isPrimitive())
            {
                addNonPrimitiveMember(name, type, typeName, byteOrder, arrayItemType, arrayItemTypeName, arrayItemOfType,
                    mapKeyType, mapValueType);
            }
            return this;
        }

        private MemberFieldGenerator addNonPrimitiveMember(
            String name,
            AstType type,
            TypeName typeName,
            AstByteOrder byteOrder,
            AstType arrayItemType,
            AstType arrayItemTypeName,
            AstType arrayItemOfType,
            AstType mapKeyType,
            AstType mapValueType)
        {
            String fieldRO = String.format("%sRO", name);
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(typeName, fieldRO, PRIVATE);
            if (typeName instanceof ClassName && (isString16Type((ClassName) typeName) ||
                isString32Type((ClassName) typeName)) && byteOrder == NETWORK)
            {
                fieldBuilder.initializer("new $T($T.BIG_ENDIAN)", typeName, ByteOrder.class);
            }
            else if (arrayItemType != null)
            {
                TypeName parameterizedArrayName = ParameterizedTypeName.get(resolver.resolveClass(type),
                    resolver.resolveClass(arrayItemTypeName),
                    resolver.resolveClass(arrayItemOfType));
                fieldBuilder = FieldSpec.builder(parameterizedArrayName, fieldRO, PRIVATE)
                    .initializer("new $T<>(new $T())", typeName, resolver.resolveClass(arrayItemTypeName));
            }
            else if (mapKeyType != null)
            {
                ClassName mapKeyClassName = resolver.resolveClass(mapKeyType);
                ClassName mapValueClassName = resolver.resolveClass(mapValueType);
                TypeName parameterizedMapName = ParameterizedTypeName.get(resolver.resolveClass(type), mapKeyClassName,
                    mapValueClassName);
                fieldBuilder = FieldSpec.builder(parameterizedMapName, fieldRO, PRIVATE)
                    .initializer("new $T<>(new $T(), new $T())", typeName, mapKeyClassName, mapValueClassName);
            }
            else
            {
                fieldBuilder.initializer("new $T()", typeName);
            }

            builder.addField(fieldBuilder.build());
            return this;
        }
    }

    private static final class OptionalOffsetsFieldGenerator extends ClassSpecMixinGenerator
    {
        private final AstType templateType;
        private final Byte nullValue;
        private String memberName;

        protected OptionalOffsetsFieldGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            AstType templateType,
            Byte nullValue)
        {
            super(thisType, builder);
            this.templateType = templateType;
            this.nullValue = nullValue;
        }

        public OptionalOffsetsFieldGenerator addMember(
            String memberName)
        {
            this.memberName = memberName;
            return this;
        }

        @Override
        public Builder build()
        {
            if (nullValue == null && templateType == null)
            {
                builder.addField(FieldSpec.builder(int[].class, "optionalOffsets", PRIVATE, FINAL)
                    .initializer(String.format("new int[%s + 1]", fieldIndex(memberName)))
                    .build());
            }
            return super.build();
        }
    }

    private static final class LengthMethodGenerator extends ClassSpecMixinGenerator
    {
        private final AstType templateType;
        private final TypeResolver resolver;

        private LengthMethodGenerator(
            ClassName thisType,
            Builder builder,
            AstType templateType,
            TypeResolver resolver)
        {
            super(thisType, builder);
            this.templateType = templateType;
            this.resolver = resolver;
        }

        @Override
        public Builder build()
        {
            if (templateType != null)
            {
                builder.addMethod(methodBuilder("length")
                    .addModifiers(PUBLIC)
                    .returns(int.class)
                    .addAnnotation(Override.class)
                    .addStatement("return $L.get().length()", variantRO(resolver.resolveClass(templateType)))
                    .build());
            }
            return super.build();
        }
    }

    private static final class FieldCountMethodGenerator extends ClassSpecMixinGenerator
    {
        private final AstType templateType;
        private final TypeName fieldCountType;
        private final TypeResolver resolver;

        private FieldCountMethodGenerator(
            ClassName thisType,
            Builder builder,
            AstType templateType,
            TypeName fieldCountType,
            TypeResolver resolver)
        {
            super(thisType, builder);
            this.templateType = templateType;
            this.fieldCountType = fieldCountType;
            this.resolver = resolver;
        }

        @Override
        public Builder build()
        {
            if (templateType == null)
            {
                builder.addMethod(methodBuilder("fieldCount")
                    .addModifiers(PUBLIC)
                    .returns(int.class)
                    .addStatement("return buffer().$L(offset() + $L)", GETTER_NAMES.get(fieldCountType),
                        offset(FIELD_COUNT))
                    .build());
            }
            else
            {
                builder.addMethod(methodBuilder("fieldCount")
                    .addModifiers(PUBLIC)
                    .returns(int.class)
                    .addAnnotation(Override.class)
                    .addStatement("return $L.get().fieldCount()", variantRO(resolver.resolveClass(templateType)))
                    .build());
            }
            return super.build();
        }
    }

    private static final class FieldsMethodGenerator extends ClassSpecMixinGenerator
    {
        private final AstType templateType;
        private final ClassName templateClassName;

        private FieldsMethodGenerator(
            ClassName thisType,
            Builder builder,
            AstType templateType,
            TypeResolver resolver)
        {
            super(thisType, builder);
            this.templateType = templateType;
            this.templateClassName = resolver.resolveClass(templateType);
        }

        @Override
        public Builder build()
        {
            if (templateType != null)
            {
                builder.addMethod(methodBuilder("fields")
                    .addModifiers(PUBLIC)
                    .returns(DIRECT_BUFFER_TYPE)
                    .addAnnotation(Override.class)
                    .addStatement("return $L.get().fields()", variantRO(templateClassName))
                    .build());
            }
            return super.build();
        }
    }

    private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
    {
        private final TypeResolver resolver;
        private final Byte nullValue;
        private final AstType templateType;

        private MemberAccessorGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            AstType templateType,
            TypeResolver resolver,
            Byte nullValue)
        {
            super(thisType, builder);
            this.resolver = resolver;
            this.nullValue = nullValue;
            this.templateType = templateType;
        }

        public MemberAccessorGenerator addMember(
            String name,
            AstType type,
            TypeName typeName,
            TypeName unsignedType,
            AstByteOrder byteOrder,
            boolean isRequired,
            Object defaultValue,
            AstType arrayItemType,
            AstType arrayItemTypeName,
            AstType mapKeyType,
            AstType mapValueType)
        {
            if (typeName.isPrimitive())
            {
                addPrimitiveMember(name, typeName, unsignedType, byteOrder, isRequired, defaultValue);
            }
            else
            {
                addNonPrimitiveMember(name, type, typeName, isRequired, defaultValue, arrayItemType, arrayItemTypeName,
                    mapKeyType, mapValueType);
            }
            return this;
        }

        @Override
        public Builder build()
        {
            if (nullValue == null && templateType == null)
            {
                builder.addMethod(methodBuilder("bitmask")
                    .addModifiers(PRIVATE)
                    .returns(long.class)
                    .addStatement("return buffer().getLong(offset() + $L)", offset(BIT_MASK))
                    .build());
            }
            return super.build();
        }

        private void addPrimitiveMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            AstByteOrder byteOrder,
            boolean isRequired,
            Object defaultValue)
        {
            TypeName generateType = (unsignedType != null) ? unsignedType : type;

            CodeBlock.Builder codeBlock = CodeBlock.builder();

            String getterName = GETTER_NAMES.get(type);
            if (getterName == null)
            {
                throw new IllegalStateException("member type not supported: " + type);
            }

            if (!isRequired && defaultValue == null)
            {
                codeBlock.addStatement("assert (bitmask() & (1 << $L)) != 0 : " +
                    "\"Field \\\"$L\\\" is not set\"", fieldIndex(name), name);
            }

            codeBlock.add("$[").add("return ");

            if (defaultValue != null)
            {
                codeBlock.add("(bitmask() & (1 << $L)) == 0 ? $L : ", fieldIndex(name), defaultConstant(name));
            }

            if (generateType != type)
            {
                codeBlock.add("($T)(", generateType);
            }

            codeBlock.add("buffer().$L(optionalOffsets[$L]", getterName, fieldIndex(name));

            if (byteOrder == AstByteOrder.NETWORK)
            {
                if (type == TypeName.SHORT || type == TypeName.INT || type == TypeName.LONG)
                {
                    codeBlock.add(", $T.BIG_ENDIAN", ByteOrder.class);
                }
            }

            if (generateType != type)
            {
                if (type == TypeName.BYTE)
                {
                    codeBlock.add(") & 0xFF)");
                }
                else if (type == TypeName.SHORT)
                {
                    codeBlock.add(") & 0xFFFF)");
                }
                else if (type == TypeName.INT)
                {
                    codeBlock.add(") & 0xFFFF_FFFFL)");
                }
                else if (type == TypeName.LONG)
                {
                    codeBlock.add(") & 0xFFFF_FFFF)");
                }
                else
                {
                    codeBlock.add(")");
                }
            }
            else
            {
                codeBlock.add(")");
            }

            codeBlock.add(";\n$]");

            builder.addMethod(methodBuilder(methodName(name))
                .addModifiers(PUBLIC)
                .returns(generateType)
                .addCode(codeBlock.build())
                .build());
        }

        private void addNonPrimitiveMember(
            String name,
            AstType type,
            TypeName typeName,
            boolean isRequired,
            Object defaultValue,
            AstType arrayItemType,
            AstType arrayItemTypeName,
            AstType mapKeyType,
            AstType mapValueType)
        {
            CodeBlock.Builder codeBlock = CodeBlock.builder();
            TypeName returnType = typeName;
            AstNamedNode namedNode = resolver.resolve(type.name());
            if (namedNode == null)
            {
                addMember(defaultValue, codeBlock, name, isRequired, "$LRO");
            }
            else
            {
                if (isTypedefType(namedNode.getKind()))
                {
                    while (isTypedefType(namedNode.getKind()))
                    {
                        type = ((AstTypedefNode) namedNode).originalType();
                        namedNode = resolver.resolve(type.name());
                    }
                }
                if (isEnumType(namedNode.getKind()))
                {
                    returnType = addEnumMember(defaultValue, codeBlock, name, type, typeName, isRequired);
                }
                else if (isVariantType(namedNode.getKind()))
                {
                    returnType = addVariantMember(defaultValue, codeBlock, name, type, typeName, isRequired, arrayItemType,
                        arrayItemTypeName, mapKeyType, mapValueType);
                }
                else
                {
                    addMember(defaultValue, codeBlock, name, isRequired, "$LRO");
                }
            }


            builder.addMethod(methodBuilder(methodName(name))
                .addModifiers(PUBLIC)
                .returns(returnType)
                .addCode(codeBlock.build())
                .build());
        }

        private TypeName addVariantMember(
            Object defaultValue,
            CodeBlock.Builder codeBlock,
            String name,
            AstType type,
            TypeName typeName,
            boolean isRequired,
            AstType arrayItemType,
            AstType arrayItemTypeName,
            AstType mapKeyType,
            AstType mapValueType)
        {
            AstVariantNode variantNode = (AstVariantNode) resolver.resolve(type.name());
            AstType ofType = variantNode.of();
            TypeName ofTypeName = resolver.resolveType(ofType);
            TypeName primitiveReturnType = ofTypeName.equals(TypeName.BYTE) || ofTypeName.equals(TypeName.SHORT) ||
                ofTypeName.equals(TypeName.INT) ? TypeName.INT : TypeName.LONG;
            TypeName returnType = Objects.requireNonNullElse(resolver.resolveUnsignedType(ofType),
                ofTypeName.isPrimitive() ? primitiveReturnType : arrayItemType != null ?
                    ParameterizedTypeName.get(resolver.resolveClass(AstType.VARIANT_ARRAY),
                        resolver.resolveType(arrayItemTypeName)) : mapKeyType != null ?
                    ParameterizedTypeName.get(resolver.resolveClass(AstType.MAP), resolver.resolveClass(mapKeyType), resolver
                    .resolveClass(mapValueType)) : resolver.resolveClass(AstType.STRING));
            addMember(defaultValue, codeBlock, name, isRequired, "$LRO.get()");
            return returnType;
        }

        private TypeName addEnumMember(
            Object defaultValue,
            CodeBlock.Builder codeBlock,
            String name,
            AstType type,
            TypeName typeName,
            boolean isRequired)
        {
            AstEnumNode enumNode = (AstEnumNode) resolver.resolve(type.name());
            ClassName enumFlyweightName = (ClassName) typeName;
            ClassName enumName = enumFlyweightName.peerClass(enumNode.name());
            addMember(defaultValue, codeBlock, name, isRequired, "$LRO.get()");
            return enumName;
        }

        private void addMember(
            Object defaultValue,
            CodeBlock.Builder codeBlock,
            String name,
            boolean isRequired,
            String returnValue)
        {
            String returnStatement = String.format("return %s", returnValue);
            String bitmask = nullValue == null && templateType == null ? "bitmask()" : "bitmask";
            if (defaultValue != null)
            {
                codeBlock.addStatement("return ($L & $L) != 0L ? $LRO.get() : $L", bitmask, maskConstant(name),
                    name, defaultConstant(name));
            }
            else
            {
                codeBlock.addStatement("assert ($L & $L) != 0L : \"Field \\\"$L\\\" is not set\"", bitmask,
                    maskConstant(name), name);
                codeBlock.addStatement(returnStatement, name);
            }
        }
    }

    private final class WrapMethodGenerator extends MethodSpecGenerator
    {
        private final List<ListField> fields = new ArrayList<>();
        private final Byte missingFieldByte;
        private final AstType templateType;
        private final ClassName templateTypeName;

        private WrapMethodGenerator(
            Byte missingFieldByte,
            AstType templateType,
            TypeResolver resolver)
        {
            super(methodBuilder("wrap"));
            this.missingFieldByte = missingFieldByte;
            this.templateType = templateType;
            this.templateTypeName = resolver.resolveClass(templateType);
        }

        public WrapMethodGenerator addMember(
            String name,
            TypeName type,
            Object defaultValue,
            boolean isRequired)
        {
            fields.add(new ListField(name, type, isRequired, defaultValue));
            return this;
        }

        private void generateWrapWithDefaultNull()
        {
            builder.addStatement("final int fieldCount = fieldCount()")
                .addStatement("bitmask = 0");
            if (templateType == null)
            {
                builder.addStatement("int fieldLimit = offset + $L + $L", offset(FIELD_COUNT), size(FIELD_COUNT));
            }
            else
            {
                builder.addStatement("DirectBuffer fieldsBuffer = fields()")
                    .addStatement("int fieldLimit = 0");
            }

            builder.beginControlFlow("for (int field = $L; field < fieldCount; field++)",
                    fieldIndex(fields.get(0).fieldName()))
                .addStatement("checkLimit(fieldLimit + $T.SIZE_OF_BYTE, limit)", BIT_UTIL_TYPE)
                .beginControlFlow("switch (field)");

            for (ListField field : fields)
            {
                String fieldName = field.fieldName();
                builder.beginControlFlow("case $L:", fieldIndex(fieldName));
                String buffer = templateType == null ? "buffer" : "fieldsBuffer";
                if (field.isRequired())
                {
                    builder.addStatement("$LRO.wrap($L, fieldLimit, maxLimit)", fieldName, buffer)
                        .addStatement("fieldLimit = $LRO.limit()", fieldName)
                        .addStatement("bitmask |= 1 << $L", fieldIndex(fieldName));
                }
                else
                {
                    builder.beginControlFlow("if ($L.getByte(fieldLimit) != MISSING_FIELD_BYTE)", buffer)
                        .addStatement("$LRO.wrap($L, fieldLimit, maxLimit)", fieldName, buffer)
                        .addStatement("fieldLimit = $LRO.limit()", fieldName)
                        .addStatement("bitmask |= 1 << $L", fieldIndex(fieldName))
                        .endControlFlow()
                        .beginControlFlow("else")
                        .addStatement("fieldLimit += MISSING_FIELD_BYTE_SIZE")
                        .endControlFlow();
                }
                builder.addStatement("break")
                    .endControlFlow();
            }
        }

        private void generateWrap()
        {
            builder.addStatement("final long bitmask = bitmask()")
                .addStatement("int fieldLimit = offset + $L + $L", offset(BIT_MASK), size(BIT_MASK))
                .beginControlFlow("for (int field = $L; field < $L + 1; field++)",
                    fieldIndex(fields.get(0).fieldName()), fieldIndex(fields.get(fields.size() - 1).fieldName()))
                .beginControlFlow("switch (field)");
            for (ListField field : fields)
            {
                String fieldName = field.fieldName();
                builder.beginControlFlow("case $L:", fieldIndex(fieldName));
                if (field.isRequired())
                {
                    builder.beginControlFlow("if ((bitmask & $L) == 0)", maskConstant(fieldName))
                        .addStatement("throw new IllegalArgumentException(\"Field \\\"$L\\\" is required but not set\")",
                            fieldName)
                        .endControlFlow();
                    if (field.type().isPrimitive())
                    {
                        builder.addStatement("optionalOffsets[$L] = fieldLimit", fieldIndex(fieldName))
                            .addStatement("fieldLimit += $L", fieldSize(fieldName));
                    }
                    else
                    {
                        builder.addStatement("$LRO.wrap(buffer, fieldLimit, maxLimit)", fieldName)
                            .addStatement("fieldLimit = $LRO.limit()", fieldName);
                    }
                }
                else
                {
                    builder.beginControlFlow("if ((bitmask & $L) != 0)", maskConstant(fieldName));
                    if (field.type().isPrimitive())
                    {
                        builder.addStatement("optionalOffsets[$L] = fieldLimit", fieldIndex(fieldName))
                            .addStatement("fieldLimit += $L", fieldSize(fieldName));
                    }
                    else
                    {
                        builder.addStatement("$LRO.wrap(buffer, fieldLimit, maxLimit)", fieldName)
                            .addStatement("fieldLimit = $LRO.limit()", fieldName);
                    }
                    builder.endControlFlow();
                }
                builder.addStatement("break")
                    .endControlFlow();
            }
        }

        @Override
        public MethodSpec generate()
        {
            builder.addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName)
                .addStatement("super.wrap(buffer, offset, maxLimit)");
            if (templateType == null)
            {
                builder.addStatement("checkLimit(offset + $L + $L, maxLimit)", offset(LENGTH), size(LENGTH));
            }
            else
            {
                builder.addStatement("$L.wrap(buffer, offset, maxLimit)", variantRO(templateTypeName));
            }
            builder.addStatement("final int limit = limit()")
                .addStatement("checkLimit(limit, maxLimit)");
            if (missingFieldByte == null && templateType == null)
            {
                generateWrap();
            }
            else
            {
                generateWrapWithDefaultNull();
            }
            return builder.endControlFlow()
                .endControlFlow()
                .addStatement("checkLimit(fieldLimit, limit)")
                .addStatement("return this")
                .build();
        }
    }

    private final class TryWrapMethodGenerator extends MethodSpecGenerator
    {
        private final List<ListField> fields = new ArrayList<>();
        private final Byte missingFieldByte;
        private final AstType templateType;
        private final ClassName templateTypeName;

        private TryWrapMethodGenerator(
            Byte missingFieldByte,
            AstType templateType,
            TypeResolver resolver)
        {
            super(methodBuilder("tryWrap"));
            this.missingFieldByte = missingFieldByte;
            this.templateType = templateType;
            this.templateTypeName = resolver.resolveClass(templateType);
        }

        public TryWrapMethodGenerator addMember(
            String name,
            TypeName type,
            Object defaultValue,
            boolean isRequired)
        {
            fields.add(new ListField(name, type, isRequired, defaultValue));
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            builder.addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName)
                .beginControlFlow("if (super.tryWrap(buffer, offset, maxLimit) == null)")
                .addStatement("return null")
                .endControlFlow();
            if (templateType == null)
            {
                builder.beginControlFlow("if (offset + $L + $L > maxLimit)", offset(LENGTH),
                    size(LENGTH));
            }
            else
            {
                builder.beginControlFlow("if ($L.tryWrap(buffer, offset, maxLimit) == null)", variantRO(templateTypeName));
            }
            builder.addStatement("return null")
                .endControlFlow()
                .addStatement("final int limit = limit()")
                .beginControlFlow("if (limit > maxLimit)")
                .addStatement("return null")
                .endControlFlow();

            if (missingFieldByte == null && templateType == null)
            {
                generateTryWrap();
            }
            else
            {
                generateTryWrapWithDefaultNull();
            }
            return builder.endControlFlow()
                .endControlFlow()
                .beginControlFlow("if (fieldLimit > limit)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("return this")
                .build();
        }

        private void generateTryWrapWithDefaultNull()
        {
            builder.addStatement("final int fieldCount = fieldCount()")
                .addStatement("bitmask = 0");
            if (templateType == null)
            {
                builder.addStatement("int fieldLimit = offset + $L + $L", offset(FIELD_COUNT), size(FIELD_COUNT));
            }
            else
            {
                builder.addStatement("DirectBuffer fieldsBuffer = fields()")
                    .addStatement("int fieldLimit = 0");
            }
            builder.beginControlFlow("for (int field = $L; field < fieldCount; field++)",
                    fieldIndex(fields.get(0).fieldName()))
                .beginControlFlow("if (fieldLimit + $T.SIZE_OF_BYTE > limit)", BIT_UTIL_TYPE)
                .addStatement("return null")
                .endControlFlow()
                .beginControlFlow("switch (field)");
            for (ListField field : fields)
            {
                String fieldName = field.fieldName();
                builder.beginControlFlow("case $L:", fieldIndex(fieldName));
                String buffer = templateType == null ? "buffer" : "fieldsBuffer";
                if (field.isRequired())
                {
                    builder.beginControlFlow("if ($LRO.tryWrap($L, fieldLimit, maxLimit) == null)", fieldName, buffer)
                        .addStatement("return null")
                        .endControlFlow()
                        .addStatement("fieldLimit = $LRO.limit()", fieldName)
                        .addStatement("bitmask |= 1 << $L", fieldIndex(fieldName));
                }
                else
                {
                    builder.beginControlFlow("if ($L.getByte(fieldLimit) != MISSING_FIELD_BYTE)", buffer)
                        .beginControlFlow("if ($LRO.tryWrap($L, fieldLimit, maxLimit) == null)", fieldName, buffer)
                        .addStatement("return null")
                        .endControlFlow()
                        .addStatement("fieldLimit = $LRO.limit()", fieldName)
                        .addStatement("bitmask |= 1 << $L", fieldIndex(fieldName))
                        .endControlFlow()
                        .beginControlFlow("else")
                        .addStatement("fieldLimit += MISSING_FIELD_BYTE_SIZE")
                        .endControlFlow();
                }
                builder.addStatement("break")
                    .endControlFlow();
            }
        }

        private void generateTryWrap()
        {
            builder.addStatement("final long bitmask = bitmask()")
                .addStatement("int fieldLimit = offset + $L + $L", offset(BIT_MASK), size(BIT_MASK))
                .beginControlFlow("for (int field = $L; field < $L + 1; field++)",
                    fieldIndex(fields.get(0).fieldName()), fieldIndex(fields.get(fields.size() - 1).fieldName()))
                .beginControlFlow("switch (field)");

            for (ListField field : fields)
            {
                String fieldName = field.fieldName();
                builder.beginControlFlow("case $L:", fieldIndex(fieldName));
                if (field.isRequired())
                {
                    builder.beginControlFlow("if ((bitmask & $L) == 0)", maskConstant(fieldName))
                        .addStatement("return null")
                        .endControlFlow();
                    if (field.type().isPrimitive())
                    {
                        builder.addStatement("optionalOffsets[$L] = fieldLimit", fieldIndex(fieldName))
                            .addStatement("fieldLimit += $L", fieldSize(fieldName));
                    }
                    else
                    {
                        builder.beginControlFlow("if ($LRO.tryWrap(buffer, fieldLimit, maxLimit) == null)", fieldName)
                            .addStatement("return null")
                            .endControlFlow()
                            .addStatement("fieldLimit = $LRO.limit()", fieldName);
                    }
                }
                else
                {
                    builder.beginControlFlow("if ((bitmask & $L) != 0)", maskConstant(fieldName));
                    if (field.type().isPrimitive())
                    {
                        builder.addStatement("optionalOffsets[$L] = fieldLimit", fieldIndex(fieldName))
                            .addStatement("fieldLimit += $L", fieldSize(fieldName));
                    }
                    else
                    {
                        builder.beginControlFlow("if ($LRO.tryWrap(buffer, fieldLimit, maxLimit) == null)", fieldName)
                            .addStatement("return null")
                            .endControlFlow()
                            .addStatement("fieldLimit = $LRO.limit()", fieldName);
                    }
                    builder.endControlFlow();
                }
                builder.addStatement("break")
                    .endControlFlow();
            }
        }
    }

    private final class LimitMethodGenerator extends MethodSpecGenerator
    {
        private final TypeName lengthTypeName;
        private final AstType templateType;
        private final TypeResolver resolver;

        private LimitMethodGenerator(
            TypeName lengthTypeName,
            AstType templateType,
            TypeResolver resolver)
        {
            super(methodBuilder("limit"));

            this.lengthTypeName = lengthTypeName;
            this.templateType = templateType;
            this.resolver = resolver;
        }

        @Override
        public MethodSpec generate()
        {
            builder.addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class);
            if (templateType == null)
            {
                builder.addStatement("return offset() + buffer().$L(offset() + $L)", GETTER_NAMES.get(lengthTypeName),
                    offset(LENGTH));
            }
            else
            {
                builder.addStatement("return $L.limit()", variantRO(resolver.resolveClass(templateType)));
            }
            return builder.build();
        }
    }

    private final class ToStringMethodGenerator extends MethodSpecGenerator
    {
        private final List<ListField> fields = new ArrayList<>();
        private final Byte nullValue;
        private final AstType templateType;

        private ToStringMethodGenerator(
            Byte nullValue,
            AstType templateType)
        {
            super(methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(String.class));
            this.nullValue = nullValue;
            this.templateType = templateType;
        }

        public ToStringMethodGenerator addMember(
            String name,
            TypeName type,
            Object defaultValue,
            boolean isRequired)
        {
            fields.add(new ListField(name, type, isRequired, defaultValue));
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            String typeName = constant(baseName);
            if (nullValue == null && templateType == null)
            {
                builder.addStatement("final long bitmask = bitmask()");
            }
            for (ListField field : fields)
            {
                if (!field.isRequired() && field.defaultValue() == null)
                {
                    builder.addStatement("Object $L = null", field.fieldName());
                }
            }
            builder.addStatement("StringBuilder format = new StringBuilder()")
                .addStatement("format.append(\"$L [bitmask={0}\")", typeName);
            int fieldIndex = 1;
            for (ListField field : fields)
            {
                String name = field.fieldName();
                if (field.isRequired() || field.defaultValue() != null)
                {
                    builder.addStatement("format.append(\", $L={$L}\")", name, fieldIndex);
                }
                else
                {
                    builder.beginControlFlow("if ((bitmask & $L) != 0L)", maskConstant(name))
                        .addStatement("format.append(\", $L={$L}\")", name, fieldIndex)
                        .addStatement("$L = $L()", name, name)
                        .endControlFlow();
                }
                fieldIndex++;
            }
            builder.addStatement("format.append(\"]\")");

            CodeBlock.Builder returnStatement = CodeBlock.builder()
                .add("$[").add("return $T.format(format.toString(), String.format(\"0x%16X\", bitmask)",
                    MessageFormat.class);
            for (ListField field : fields)
            {
                String name = field.fieldName();
                if (field.isRequired() || field.defaultValue() != null)
                {
                    returnStatement.add(", $L()", name);
                }
                else
                {
                    returnStatement.add(", $L", name);
                }
            }
            returnStatement.add(");\n$]");
            return builder.addCode(returnStatement.build())
                .build();
        }
    }

    private static final class BuilderClassGenerator extends ClassSpecGenerator
    {
        private final TypeSpec.Builder builder;
        private final ClassName listType;
        private final FieldsMaskGenerator fieldsMask;
        private final MemberFieldGenerator memberField;
        private final TemplateTypeRWGenerator templateTypeRW;
        private final MemberAccessorGenerator memberAccessor;
        private final MemberMutatorGenerator memberMutator;
        private final WrapMethodGenerator wrapMethod;
        private final BuildMethodGenerator buildMethod;

        private BuilderClassGenerator(
            ClassName listType,
            ClassName flyweightType,
            AstType templateType,
            TypeName lengthTypeName,
            TypeName fieldCountTypeName,
            TypeResolver resolver,
            Byte nullValue)
        {
            this(listType.nestedClass("Builder"), flyweightType.nestedClass("Builder"), listType, templateType,
                lengthTypeName, fieldCountTypeName, resolver, nullValue);
        }

        private BuilderClassGenerator(
            ClassName thisType,
            ClassName builderRawType,
            ClassName listType,
            AstType templateType,
            TypeName lengthTypeName,
            TypeName fieldCountTypeName,
            TypeResolver resolver,
            Byte nullValue)
        {
            super(thisType);
            this.builder = classBuilder(thisType.simpleName())
                .addModifiers(PUBLIC, STATIC, FINAL)
                .superclass(ParameterizedTypeName.get(builderRawType, listType));
            this.listType = listType;
            this.fieldsMask = new FieldsMaskGenerator(thisType, builder, nullValue, templateType);
            this.memberField = new MemberFieldGenerator(thisType, builder, resolver);
            this.templateTypeRW = new TemplateTypeRWGenerator(thisType, builder, templateType, resolver);
            this.memberAccessor = new MemberAccessorGenerator(thisType, builder, templateType, resolver, nullValue);
            this.memberMutator = new MemberMutatorGenerator(thisType, builder, templateType, resolver, nullValue);
            this.wrapMethod = new WrapMethodGenerator(nullValue, templateType, resolver);
            this.buildMethod = new BuildMethodGenerator(templateType, lengthTypeName, fieldCountTypeName, nullValue, resolver);
        }

        private void addMember(
            String name,
            AstType type,
            TypeName typeName,
            TypeName unsignedType,
            int size,
            TypeName sizeType,
            boolean usedAsSize,
            Object defaultValue,
            AstByteOrder byteOrder,
            boolean isRequired,
            AstType arrayItemType,
            AstType arrayItemTypeName,
            AstType arrayItemOfType,
            AstType arrayItemKindType,
            AstType mapKeyType,
            AstType mapKeyKindType,
            AstType mapKeyOfType,
            AstType mapValueType,
            AstType mapValueKindType,
            AstType mapValueOfType)
        {
            memberField.addMember(name, typeName, byteOrder, arrayItemType, arrayItemTypeName, arrayItemOfType,
                arrayItemKindType, mapKeyType, mapValueType);
            memberAccessor.addMember(name, type, typeName, isRequired);
            memberMutator.addMember(name, type, typeName, unsignedType, usedAsSize, size, sizeType, byteOrder, defaultValue,
                isRequired, arrayItemType, arrayItemTypeName, arrayItemOfType, arrayItemKindType, mapKeyType, mapKeyKindType,
                mapKeyOfType, mapValueType, mapValueKindType, mapValueOfType);
            buildMethod.addMember(name, isRequired);
        }

        @Override
        public TypeSpec generate()
        {
            fieldsMask.build();
            memberField.build();
            templateTypeRW.build();
            memberAccessor.build();
            memberMutator.build();
            return builder.addMethod(constructor())
                .addMethod(wrapMethod.generate())
                .addMethod(buildMethod.generate())
                .build();
        }

        private static final class FieldsMaskGenerator extends ClassSpecMixinGenerator
        {
            private final Byte nullValue;
            private final AstType templateType;

            protected FieldsMaskGenerator(
                ClassName thisType,
                Builder builder,
                Byte nullValue,
                AstType templateType)
            {
                super(thisType, builder);
                this.nullValue = nullValue;
                this.templateType = templateType;
            }

            @Override
            public Builder build()
            {
                if (nullValue == null && templateType == null)
                {
                    builder.addField(FieldSpec.builder(long.class, "fieldsMask")
                        .addModifiers(PRIVATE)
                        .build());
                }
                else
                {
                    builder.addField(FieldSpec.builder(int.class, "lastFieldSet")
                        .addModifiers(PRIVATE)
                        .initializer("-1")
                        .build());
                }
                return builder;
            }
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addStatement("super(new $T())", listType)
                .build();
        }

        private static final class MemberFieldGenerator extends ClassSpecMixinGenerator
        {
            private final TypeResolver resolver;

            private MemberFieldGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                TypeResolver resolver)
            {
                super(thisType, builder);
                this.resolver = resolver;
            }

            public MemberFieldGenerator addMember(
                String name,
                TypeName type,
                AstByteOrder byteOrder,
                AstType arrayItemType,
                AstType arrayItemTypeName,
                AstType arrayItemOfType,
                AstType arrayItemKindType,
                AstType mapKeyType,
                AstType mapValueType)
            {
                if (!type.isPrimitive())
                {
                    String fieldRW = String.format("%sRW", name);

                    if (type instanceof ClassName)
                    {
                        ClassName classType = (ClassName) type;
                        ClassName builderType = classType.nestedClass("Builder");

                        if ((isString16Type(classType) || isString32Type(classType)) && byteOrder == NETWORK)
                        {
                            builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                                .initializer("new $T($T.BIG_ENDIAN)", builderType, ByteOrder.class)
                                .build());
                        }
                        else if (arrayItemType != null)
                        {
                            ClassName arrayItemTypeClass = resolver.resolveClass(arrayItemTypeName);
                            ClassName arrayItemTypeBuilderClass = arrayItemTypeClass.nestedClass("Builder");
                            ClassName kindTypeClass = resolver.resolveClass(arrayItemKindType);
                            TypeName parameterizedArrayName = ParameterizedTypeName.get(builderType,
                                arrayItemTypeBuilderClass, arrayItemTypeClass, enumClassName(kindTypeClass),
                                resolver.resolveClass(arrayItemOfType));
                            builder.addField(FieldSpec.builder(parameterizedArrayName, fieldRW, PRIVATE, FINAL)
                                .initializer("new $T<>(new $T(), new $T())", builderType, arrayItemTypeBuilderClass,
                                    arrayItemTypeClass)
                                .build());
                        }
                        else if (mapKeyType != null)
                        {
                            ClassName mapKeyTypeClass = resolver.resolveClass(mapKeyType);
                            ClassName mapKeyTypeBuilderClass = mapKeyTypeClass.nestedClass("Builder");
                            ClassName mapValueTypeClass = resolver.resolveClass(mapValueType);
                            ClassName mapValueTypeBuilderClass = mapValueTypeClass.nestedClass("Builder");
                            TypeName parameterizedMapName = ParameterizedTypeName.get(builderType, mapKeyTypeClass,
                                mapValueTypeClass, mapKeyTypeBuilderClass, mapValueTypeBuilderClass);
                            builder.addField(FieldSpec.builder(parameterizedMapName, fieldRW, PRIVATE, FINAL)
                                .initializer("new $T<>(new $T(), new $T(), new $T(), new $T())", builderType,
                                    mapKeyTypeClass, mapValueTypeClass, mapKeyTypeBuilderClass, mapValueTypeBuilderClass)
                                .build());
                        }
                        else
                        {
                            builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                                .initializer("new $T()", builderType)
                                .build());
                        }
                    }
                    else
                    {
                        throw new IllegalArgumentException("Unsupported member type: " + type);
                    }
                }
                return this;
            }
        }

        private static final class TemplateTypeRWGenerator extends ClassSpecMixinGenerator
        {
            private final AstType templateType;
            private final TypeResolver resolver;

            private TemplateTypeRWGenerator(
                ClassName thisType,
                Builder builder,
                AstType templateType,
                TypeResolver resolver)
            {
                super(thisType, builder);

                this.templateType = templateType;
                this.resolver = resolver;
            }

            @Override
            public Builder build()
            {
                if (templateType != null)
                {
                    ClassName templateClassName = resolver.resolveClass(templateType);
                    ClassName builderClassName = templateClassName.nestedClass("Builder");
                    builder.addField(FieldSpec.builder(builderClassName, variantRW(templateClassName), PRIVATE,
                        FINAL)
                        .initializer("new $T()", builderClassName)
                        .build());
                }

                return super.build();
            }
        }

        private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
        {
            private final AstType templateType;
            private final TypeResolver resolver;
            private final Byte nullValue;
            private long bitsOfOnes;
            private int position;
            private String priorRequiredFieldName = null;
            private String priorFieldName = null;
            private Map<String, Integer> requiredFieldPosition;

            private MemberAccessorGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                AstType templateType,
                TypeResolver resolver,
                Byte nullValue)
            {
                super(thisType, builder);
                requiredFieldPosition = new HashMap<>();
                this.templateType = templateType;
                this.resolver = resolver;
                this.nullValue = nullValue;
            }

            public MemberAccessorGenerator addMember(
                String name,
                AstType type,
                TypeName typeName,
                boolean isRequired)
            {
                if (templateType == null)
                {
                    if (typeName instanceof ClassName)
                    {
                        ClassName className = (ClassName) typeName;
                        AstNamedNode namedNode = resolver.resolve(type.name());
                        if (isStringType(className))
                        {
                            addStringType(className, name);
                        }
                        else if (Kind.VARIANT.equals(namedNode.getKind()))
                        {
                            addVariantType(name, className);
                        }
                    }
                    bitsOfOnes = (bitsOfOnes << 1) | 1;
                    if (isRequired)
                    {
                        requiredFieldPosition.put(name, 1 << position);
                        priorRequiredFieldName = name;
                    }
                    priorFieldName = name;
                    position++;
                }
                return this;
            }

            private void addVariantType(
                String name,
                ClassName className)
            {
                TypeName builderType = className.nestedClass("Builder");
                MethodSpec.Builder methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PRIVATE)
                    .returns(builderType);
                String outOfOrderCheck = String.format("assert %s : \"Field \\\"$L\\\" cannot be set out of order\"",
                    nullValue == null ? "(fieldsMask & ~$L) == 0" : "lastFieldSet < $L");
                methodBuilder.addStatement(outOfOrderCheck,
                    nullValue == null ? String.format("0x%02X", bitsOfOnes) : fieldIndex(name), name);
                if (priorRequiredFieldName != null)
                {
                    int priorRequiredFieldPosition = requiredFieldPosition.get(priorRequiredFieldName);
                    if (nullValue == null)
                    {
                        methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field " +
                            "\\\"$L\\\" is not set\"", String.format("0x%02X", priorRequiredFieldPosition),
                            priorRequiredFieldName);
                    }
                    else
                    {
                        if (priorRequiredFieldPosition == ((1 << position) >> 1))
                        {
                            methodBuilder.addStatement("assert lastFieldSet == $L : \"Prior required field " +
                                "\\\"$L\\\" is not set\"", fieldIndex(priorRequiredFieldName), priorRequiredFieldName);
                        }
                        else
                        {
                            methodBuilder.beginControlFlow("if (lastFieldSet < $L)", fieldIndex(priorFieldName))
                                .addStatement("$L()", defaultMethodName(priorFieldName))
                                .endControlFlow();
                        }
                    }
                }
                else if (nullValue != null && priorFieldName != null)
                {
                    methodBuilder.beginControlFlow("if (lastFieldSet < $L)", fieldIndex(priorFieldName))
                        .addStatement(String.format("default%s%s()",
                            Character.toUpperCase(priorFieldName.charAt(0)), priorFieldName.substring(1)))
                        .endControlFlow();
                }
                methodBuilder.addStatement("return $LRW.wrap(buffer(), limit(), maxLimit())", name);
                builder.addMethod(methodBuilder.build());
            }

            private void addStringType(
                ClassName className,
                String name)
            {
                TypeName builderType = className.nestedClass("Builder");
                builder.addMethod(methodBuilder(methodName(name))
                    .addModifiers(PRIVATE)
                    .returns(builderType)
                    .addStatement("return $LRW.wrap(buffer(), limit(), maxLimit())", name)
                    .build());
            }
        }

        private static final class MemberMutatorGenerator extends ClassSpecMixinGenerator
        {
            private final AstType templateType;
            private final TypeResolver resolver;
            private final Byte nullValue;
            private int bitsOfOnes;
            private int position;
            private String priorRequiredFieldName = null;
            private String priorFieldName = null;
            private Map<String, Integer> requiredFieldPosition;
            private List<MethodSpec> defaultNullMutators;

            private MemberMutatorGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                AstType templateType,
                TypeResolver resolver,
                Byte nullValue)
            {
                super(thisType, builder);
                this.requiredFieldPosition = new HashMap<>();
                this.defaultNullMutators = new ArrayList<>();
                this.templateType = templateType;
                this.resolver = resolver;
                this.nullValue = nullValue;
            }

            public MemberMutatorGenerator addMember(
                String name,
                AstType type,
                TypeName typeName,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue,
                boolean isRequired,
                AstType arrayItemType,
                AstType arrayItemTypeName,
                AstType arrayItemOfType,
                AstType arrayItemKindType,
                AstType mapKeyType,
                AstType mapKeyKindType,
                AstType mapKeyOfType,
                AstType mapValueType,
                AstType mapValueKindType,
                AstType mapValueOfType)
            {
                if (typeName.isPrimitive())
                {
                    addPrimitiveMember(name, typeName, unsignedType, usedAsSize, byteOrder);
                }
                else
                {
                    addNonPrimitiveMember(name, type, typeName, isRequired, arrayItemType, arrayItemTypeName, arrayItemOfType,
                        arrayItemKindType, mapKeyType, mapKeyKindType, mapKeyOfType, mapValueType, mapValueKindType,
                        mapValueOfType);
                }
                bitsOfOnes = (bitsOfOnes << 1) | 1;
                if (isRequired)
                {
                    requiredFieldPosition.put(name, 1 << position);
                    priorRequiredFieldName = name;
                }
                priorFieldName = name;
                position++;
                return this;
            }

            private void addPrimitiveMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                AstByteOrder byteOrder)
            {
                String putterName = PUTTER_NAMES.get(type);
                if (putterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }

                TypeName generateType = (unsignedType != null) ? unsignedType : type;
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("assert (fieldsMask & ~$L) == 0 : \"Field \\\"$L\\\" cannot be set out of order\"",
                    String.format("0x%02X", bitsOfOnes), name);
                if (unsignedType != null)
                {
                    String[] range = UNSIGNED_INT_RANGES.get(type);
                    code.beginControlFlow("if (value < $L)", range[0])
                        .addStatement("throw new IllegalArgumentException(String.format($S, value))",
                            format("Value %%d too low for field \"%s\"", name))
                        .endControlFlow();
                    if (range[1] != null)
                    {
                        code.addStatement("assert (value & $L) == 0L : \"Value out of range for field \\\"$L\\\"\"", range[1],
                            name);
                    }
                }

                code.addStatement("int newLimit = limit() + $L", fieldSize(name))
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .add("$[")
                    .add("buffer().$L(limit(), ", putterName);
                if (generateType != type)
                {
                    code.add("($T)", type);

                    if (type == TypeName.BYTE)
                    {
                        code.add("(value & 0xFF)");
                    }
                    else if (type == TypeName.SHORT)
                    {
                        code.add("(value & 0xFFFF)");
                    }
                    else if (type == TypeName.INT)
                    {
                        code.add("(value & 0xFFFF_FFFFL)");
                    }
                    else
                    {
                        code.add("value");
                    }
                }
                else
                {
                    code.add("value");
                }
                if (byteOrder == NETWORK)
                {
                    if (type == TypeName.SHORT || type == TypeName.INT || type == TypeName.LONG)
                    {
                        code.add(", $T.BIG_ENDIAN", ByteOrder.class);
                    }
                }
                code.add(");\n$]");

                code.addStatement("fieldsMask |= 1 << $L", fieldIndex(name))
                    .addStatement("limit(newLimit)")
                    .addStatement("return this");

                builder.addMethod(methodBuilder(methodName(name))
                    .addModifiers(usedAsSize ? PRIVATE : PUBLIC)
                    .addParameter(generateType, "value")
                    .returns(thisType)
                    .addCode(code.build())
                    .build());
            }

            private void addNonPrimitiveMember(
                String name,
                AstType type,
                TypeName typeName,
                boolean isRequired,
                AstType arrayItemType,
                AstType arrayItemTypeName,
                AstType arrayItemOfType,
                AstType arrayItemKindType,
                AstType mapKeyType,
                AstType mapKeyKindType,
                AstType mapKeyOfType,
                AstType mapValueType,
                AstType mapValueKindType,
                AstType mapValueOfType)
            {
                ClassName className = (ClassName) typeName;
                AstNamedNode namedNode = resolver.resolve(type.name());
                if (isStringType(className))
                {
                    addStringType(className, name);
                }
                else
                {
                    Kind kind = namedNode.getKind();
                    if (isTypedefType(kind))
                    {
                        AstTypedefNode typedefNode = (AstTypedefNode) namedNode;
                        type = typedefNode.originalType();
                        className = resolver.resolveClass(type);
                        kind = resolver.resolve(type.name()).getKind();
                        if (isTypedefType(kind))
                        {
                            addNonPrimitiveMember(name, type, resolver.resolveType(type), isRequired, arrayItemType,
                                arrayItemTypeName, arrayItemOfType, arrayItemKindType, mapKeyType, mapKeyKindType,
                                mapKeyOfType, mapValueType, mapValueKindType, mapValueOfType);
                            return;
                        }
                    }
                    if (isEnumType(kind))
                    {
                        addEnumType(name, type, className);
                    }
                    else if (isVariantType(kind))
                    {
                        addVariantType(name, type, className, isRequired, arrayItemType, arrayItemTypeName, arrayItemOfType,
                            arrayItemKindType, mapKeyType, mapValueType);
                    }
                    else
                    {
                        addUnionType(name, className);
                    }
                }
            }

            private void addUnionType(
                String name,
                ClassName className)
            {
                ClassName consumerType = ClassName.get(Consumer.class);
                ClassName builderType = className.nestedClass("Builder");
                TypeName parameterType = isVarint32Type(className) ? TypeName.INT
                    : isVarint64Type(className) ? TypeName.LONG
                    : ParameterizedTypeName.get(consumerType, builderType);

                MethodSpec.Builder methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(thisType)
                    .addParameter(className, "value")
                    .addStatement("assert (fieldsMask & ~$L) == 0 : \"Field \\\"$L\\\" cannot be set out of order\"",
                        String.format("0x%02X", bitsOfOnes), name);
                if (priorRequiredFieldName != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", requiredFieldPosition.get(priorRequiredFieldName)),
                        priorRequiredFieldName);
                }
                methodBuilder.addStatement("int newLimit = limit() + value.sizeof()")
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .addStatement("buffer().putBytes(limit(), value.buffer(), value.offset(), value.sizeof())")
                    .addStatement("fieldsMask |= 1 << $L", fieldIndex(name))
                    .addStatement("limit(newLimit)")
                    .addStatement("return this");
                builder.addMethod(methodBuilder.build());

                methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(thisType)
                    .addParameter(parameterType, "mutator")
                    .addStatement("assert (fieldsMask & ~$L) == 0 : \"Field \\\"$L\\\" cannot be set out of order\"",
                        String.format("0x%02X", bitsOfOnes), name);
                if (priorRequiredFieldName != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", requiredFieldPosition.get(priorRequiredFieldName)),
                        priorRequiredFieldName);
                }
                methodBuilder.addStatement("$T $LRW = this.$LRW.wrap(buffer(), limit(), maxLimit())", builderType, name, name)
                    .addStatement("mutator.accept($LRW)", name)
                    .addStatement("fieldsMask |= 1 << $L", fieldIndex(name))
                    .addStatement("limit($LRW.build().limit())", name)
                    .addStatement("return this");
                builder.addMethod(methodBuilder.build());
            }

            private void addVariantType(
                String name,
                AstType type,
                ClassName variantFlyweightName,
                boolean isRequired,
                AstType arrayItemType,
                AstType arrayItemTypeName,
                AstType arrayItemOfType,
                AstType arrayItemKindType,
                AstType mapKeyType,
                AstType mapValueType)
            {
                AstVariantNode variantNode = (AstVariantNode) resolver.resolve(type.name());
                ClassName builderType = variantFlyweightName.nestedClass("Builder");
                AstType ofType = variantNode.of();
                TypeName ofTypeName = resolver.resolveType(ofType);
                TypeName primitiveReturnType = ofTypeName.equals(TypeName.BYTE) || ofTypeName.equals(TypeName.SHORT) ||
                    ofTypeName.equals(TypeName.INT) ? TypeName.INT : TypeName.LONG;
                TypeName parameterType = Objects.requireNonNullElse(resolver.resolveUnsignedType(variantNode.of()),
                    ofTypeName.isPrimitive() ? primitiveReturnType : arrayItemType != null ?
                        ParameterizedTypeName.get(resolver.resolveClass(AstType.VARIANT_ARRAY),
                            resolver.resolveType(arrayItemTypeName)) : mapKeyType != null ?
                        ParameterizedTypeName.get(resolver.resolveClass(AstType.MAP), resolver.resolveClass(mapKeyType),
                            resolver.resolveClass(mapValueType)) :
                        resolver.resolveClass(AstType.STRING));
                MethodSpec.Builder methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(thisType)
                    .addParameter(parameterType, "value");

                if (templateType == null)
                {
                    methodBuilder.addStatement("$T $LRW = $L()", builderType, name, name)
                        .addStatement("$LRW.set(value)", name);
                }
                else
                {
                    ClassName templateClassName = resolver.resolveClass(templateType);
                    String outOfOrderCheck = "assert lastFieldSet < $L : \"Field \\\"$L\\\" cannot be set out of order\"";
                    methodBuilder.addStatement(outOfOrderCheck, fieldIndex(name), name);

                    if (priorRequiredFieldName != null)
                    {
                        int priorRequiredFieldPosition = requiredFieldPosition.get(priorRequiredFieldName);
                        if (priorRequiredFieldPosition == ((1 << position) >> 1))
                        {
                            methodBuilder.addStatement("assert lastFieldSet == $L : \"Prior required field " +
                                "\\\"$L\\\" is not set\"", fieldIndex(priorRequiredFieldName), priorRequiredFieldName);
                        }
                        else
                        {
                            methodBuilder.beginControlFlow("if (lastFieldSet < $L)", fieldIndex(priorFieldName))
                                .addStatement("$L()", defaultMethodName(priorFieldName))
                                .endControlFlow();
                        }
                    }
                    else if (nullValue != null && priorFieldName != null)
                    {
                        methodBuilder.beginControlFlow("if (lastFieldSet < $L)", fieldIndex(priorFieldName))
                            .addStatement(String.format("default%s%s()",
                                Character.toUpperCase(priorFieldName.charAt(0)), priorFieldName.substring(1)))
                            .endControlFlow();
                    }

                    if (arrayItemType != null)
                    {
                        ClassName arrayItemTypeClass = resolver.resolveClass(arrayItemTypeName);
                        ClassName arrayItemTypeBuilderClass = arrayItemTypeClass.nestedClass("Builder");
                        ClassName kindTypeClass = resolver.resolveClass(arrayItemKindType);
                        TypeName parameterizedArrayName = ParameterizedTypeName.get(builderType,
                            arrayItemTypeBuilderClass, arrayItemTypeClass, enumClassName(kindTypeClass),
                            resolver.resolveClass(arrayItemOfType));

                        methodBuilder.addStatement("$L.field((b, o, m) ->\n{\n$T $L = $LRW.wrap(b, o, m);\n" +
                                "value.forEach(v -> $L.item(v.get()));\nreturn $LRW.build().sizeof();\n})",
                            variantRW(templateClassName), parameterizedArrayName, name, name, name, name);
                    }
                    else if (mapKeyType != null)
                    {
                        ClassName mapKeyTypeClass = resolver.resolveClass(mapKeyType);
                        ClassName mapKeyTypeBuilderClass = mapKeyTypeClass.nestedClass("Builder");
                        ClassName mapValueTypeClass = resolver.resolveClass(mapValueType);
                        ClassName mapValueTypeBuilderClass = mapValueTypeClass.nestedClass("Builder");

                        TypeName parameterizedMapBuilderName = ParameterizedTypeName.get(builderType, mapKeyTypeClass,
                            mapValueTypeClass, mapKeyTypeBuilderClass, mapValueTypeBuilderClass);
                        methodBuilder.addStatement("$L.field((b, o, m) ->\n{\n$T $L = $LRW.wrap(b, o, m);\nvalue.forEach(kv " +
                                "-> vv -> $L.entry(k -> k.set(kv.get()), v -> v.set(vv.get())));\nreturn $L.build().sizeof();" +
                                "\n})",
                            variantRW(templateClassName), parameterizedMapBuilderName, name, name, name, name);
                    }
                    else
                    {
                        methodBuilder.addStatement("$L.field((b, o, m) -> $LRW.wrap(b, o, m).set(value).build().sizeof())",
                            variantRW(templateClassName), name);
                    }
                }

                if (nullValue == null && templateType == null)
                {
                    methodBuilder.addStatement("fieldsMask |= $L", maskConstant(name));
                }
                else
                {
                    methodBuilder.addStatement("lastFieldSet = $L", fieldIndex(name));
                    if (!isRequired)
                    {
                        addDefaultNullMutator(name);
                    }
                }
                if (templateType == null)
                {
                    methodBuilder.addStatement("limit($LRW.build().limit())", name);
                }
                methodBuilder.addStatement("return this");
                builder.addMethod(methodBuilder.build());
            }

            private void addDefaultNullMutator(
                String name)
            {
                MethodSpec.Builder defaultNullMethod = methodBuilder(defaultMethodName(name))
                    .addModifiers(PRIVATE)
                    .returns(thisType);
                if (priorRequiredFieldName != null)
                {
                    int priorRequiredFieldPosition = requiredFieldPosition.get(priorRequiredFieldName);
                    if (priorRequiredFieldPosition == ((1 << position) >> 1))
                    {
                        defaultNullMethod.addStatement("assert lastFieldSet == $L : \"Prior required field " +
                            "\\\"$L\\\" is not set\";", fieldIndex(priorRequiredFieldName), priorRequiredFieldName);
                    }
                    else
                    {
                        defaultNullMethod.beginControlFlow("if (lastFieldSet < $L)", fieldIndex(priorFieldName))
                            .addStatement("$L()", defaultMethodName(priorFieldName))
                            .endControlFlow();
                    }
                }
                else if (priorFieldName != null)
                {
                    defaultNullMethod.beginControlFlow("if (lastFieldSet < $L)", fieldIndex(priorFieldName))
                        .addStatement("$L()", defaultMethodName(priorFieldName))
                        .endControlFlow();
                }

                if (templateType == null)
                {
                    defaultNullMethod
                        .addStatement("int newLimit = limit() + $T.SIZE_OF_BYTE", BIT_UTIL_TYPE)
                        .addStatement("checkLimit(limit(), newLimit)")
                        .addStatement("buffer().putByte(limit(), MISSING_FIELD_BYTE)")
                        .addStatement("lastFieldSet = $L", fieldIndex(name))
                        .addStatement("limit(newLimit)");
                }
                else
                {
                    defaultNullMethod
                        .addStatement("variantOfListRW.field((b, o, m) -> { b.putByte(o, MISSING_FIELD_BYTE); " +
                            "return MISSING_FIELD_BYTE_SIZE; })")
                        .addStatement("lastFieldSet = $L", fieldIndex(name));
                }
                defaultNullMutators.add(defaultNullMethod.addStatement("return this").build());
            }

            private void addEnumType(
                String name,
                AstType type,
                ClassName enumFlyweightName)
            {
                AstEnumNode enumNode = (AstEnumNode) resolver.resolve(type.name());
                ClassName enumName = enumFlyweightName.peerClass(enumNode.name());
                ClassName builderType = enumFlyweightName.nestedClass("Builder");

                MethodSpec.Builder methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(thisType)
                    .addParameter(enumFlyweightName, "value")
                    .addStatement("assert (fieldsMask & ~$L) == 0 : \"Field \\\"$L\\\" cannot be set out of order\"",
                        String.format("0x%02X", bitsOfOnes), name);
                if (priorRequiredFieldName != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", requiredFieldPosition.get(priorRequiredFieldName)),
                        priorRequiredFieldName);
                }
                methodBuilder.addStatement("int newLimit = limit() + value.sizeof()")
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .addStatement("buffer().putBytes(limit(), value.buffer(), value.offset(), value.sizeof())")
                    .addStatement("fieldsMask |= 1 << $L", fieldIndex(name))
                    .addStatement("limit(newLimit)")
                    .addStatement("return this");
                builder.addMethod(methodBuilder.build());

                methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(thisType)
                    .addParameter(enumName, "value")
                    .addStatement("assert (fieldsMask & ~$L) == 0 : \"Field \\\"$L\\\" cannot be set out of order\"",
                        String.format("0x%02X", bitsOfOnes), name);
                if (priorRequiredFieldName != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", requiredFieldPosition.get(priorRequiredFieldName)),
                        priorRequiredFieldName);
                }
                methodBuilder.addStatement("$T $LRW = this.$LRW.wrap(buffer(), limit(), maxLimit())", builderType, name, name);
                if (AstType.STRING8.equals(enumNode.valueType()) || AstType.STRING16.equals(enumNode.valueType()) ||
                    AstType.STRING32.equals(enumNode.valueType()))
                {
                    methodBuilder.addStatement("$LRW.set(value, $T.UTF_8)", name, StandardCharsets.class);
                }
                else
                {
                    methodBuilder.addStatement("$LRW.set(value)", name);
                }
                methodBuilder.addStatement("fieldsMask |= 1 << $L", fieldIndex(name))
                    .addStatement("limit($LRW.build().limit())", name)
                    .addStatement("return this");
                builder.addMethod(methodBuilder.build());
            }

            private void addStringType(
                ClassName className,
                String name)
            {
                ClassName builderType = className.nestedClass("Builder");

                MethodSpec.Builder methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(thisType)
                    .addParameter(String.class, "value")
                    .addStatement("assert (fieldsMask & ~$L) == 0 : \"Field \\\"$L\\\" is already set or subsequent fields " +
                        "are already set\"", String.format("0x%02X", bitsOfOnes), name);
                if (priorRequiredFieldName != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", requiredFieldPosition.get(priorRequiredFieldName)),
                        priorRequiredFieldName);
                }
                methodBuilder.addStatement("$T $LRW = $L()", builderType, name, methodName(name))
                    .addStatement("$LRW.set(value, $T.UTF_8)", name, StandardCharsets.class)
                    .addStatement("fieldsMask |= 1 << $L", fieldIndex(name))
                    .addStatement("limit($LRW.build().limit())", name)
                    .addStatement("return this");
                builder.addMethod(methodBuilder.build());


                methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(thisType)
                    .addParameter(className, "value")
                    .addStatement("assert (fieldsMask & ~$L) == 0 : \"Field \\\"$L\\\" is already set or subsequent fields " +
                        "are already set\"", String.format("0x%02X", bitsOfOnes), name);
                if (priorRequiredFieldName != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", requiredFieldPosition.get(priorRequiredFieldName)),
                        priorRequiredFieldName);
                }
                methodBuilder.addStatement("$T $LRW = $L()", builderType, name, methodName(name))
                    .addStatement("$LRW.set(value)", name)
                    .addStatement("fieldsMask |= 1 << $L", fieldIndex(name))
                    .addStatement("limit($LRW.build().limit())", name)
                    .addStatement("return this");
                builder.addMethod(methodBuilder.build());

                methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(thisType)
                    .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "length")
                    .addStatement("assert (fieldsMask & ~$L) == 0 : \"Field \\\"$L\\\" is already set or subsequent fields " +
                        "are already set\"", String.format("0x%02X", bitsOfOnes), name);
                if (priorRequiredFieldName != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", requiredFieldPosition.get(priorRequiredFieldName)),
                        priorRequiredFieldName);
                }
                methodBuilder.addStatement("$T $LRW = $L()", builderType, name, methodName(name))
                    .addStatement("$LRW.set(buffer, offset, length)", name)
                    .addStatement("fieldsMask |= 1 << $L", fieldIndex(name))
                    .addStatement("limit($LRW.build().limit())", name)
                    .addStatement("return this");
                builder.addMethod(methodBuilder.build());
            }

            @Override
            public Builder build()
            {
                for (int i = 0; i < defaultNullMutators.size() - 1; i++)
                {
                    builder.addMethod(defaultNullMutators.get(i));
                }
                bitsOfOnes = 0;
                position = 0;
                return super.build();
            }
        }

        private final class WrapMethodGenerator extends MethodSpecGenerator
        {
            private final Byte nullValue;
            private final AstType templateType;
            private final TypeResolver resolver;

            private WrapMethodGenerator(
                Byte nullValue,
                AstType templateType,
                TypeResolver resolver)
            {
                super(methodBuilder("wrap")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .returns(thisName));
                this.nullValue = nullValue;
                this.templateType = templateType;
                this.resolver = resolver;
            }

            @Override
            public MethodSpec generate()
            {
                builder.addStatement("super.wrap(buffer, offset, maxLimit)");
                if (templateType == null)
                {
                    builder.addStatement(nullValue == null ? "fieldsMask = 0" : "lastFieldSet = -1")
                        .addStatement("int newLimit = limit() + $L", offset(FIRST_FIELD))
                        .addStatement("checkLimit(newLimit, maxLimit())")
                        .addStatement("limit(newLimit)");
                }
                else
                {
                    builder.addStatement("lastFieldSet = -1")
                        .addStatement("$L.wrap(buffer, offset, maxLimit)", variantRW(resolver.resolveClass(templateType)));
                }
                return builder.addStatement("return this").build();
            }
        }

        private final class BuildMethodGenerator extends MethodSpecGenerator
        {
            private final TypeResolver resolver;
            private final AstType templateType;
            private final TypeName lengthTypeName;
            private final TypeName fieldCountTypeName;
            private final Byte nullValue;
            private int position;
            private Map<String, Integer> requiredFieldPosition;

            private BuildMethodGenerator(
                AstType templateType,
                TypeName lengthTypeName,
                TypeName fieldCountTypeName,
                Byte nullValue,
                TypeResolver resolver)
            {
                super(methodBuilder("build")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(listType));
                requiredFieldPosition = new HashMap<>();
                this.resolver = resolver;
                this.templateType = templateType;
                this.lengthTypeName = lengthTypeName;
                this.fieldCountTypeName = fieldCountTypeName;
                this.nullValue = nullValue;
            }

            public BuildMethodGenerator addMember(
                String name,
                boolean isRequired)
            {
                if (isRequired)
                {
                    if (nullValue == null && templateType == null)
                    {
                        requiredFieldPosition.put(name, 1 << position);
                        builder.addStatement("assert (fieldsMask & $L) != 0 : \"Required field \\\"$L\\\" is not " +
                            "set\"", String.format("0x%02X", requiredFieldPosition.get(name)), name);
                    }
                    else
                    {
                        builder.addStatement("assert lastFieldSet >= $L : \"Required field \\\"$L\\\" is not set\"",
                            fieldIndex(name), name);
                    }
                }
                position++;
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                if (templateType != null)
                {
                    ClassName templateTypeName = resolver.resolveClass(templateType);
                    return builder.addStatement("limit($L.build().limit())", variantRW(templateTypeName))
                        .addStatement("return super.build()")
                        .build();
                }
                final String putLength = lengthTypeName.equals(TypeName.BYTE) ? "buffer().$L(offset()" +
                    " + $L, (byte) (limit() - offset()))" : "buffer().$L(offset() + $L, limit() - offset())";
                if (nullValue == null)
                {
                    return generateBuild(putLength);
                }
                return generateBuildWithDefaultNull(putLength);
            }

            private MethodSpec generateBuild(
                String putLength)
            {
                final String putFieldCount = fieldCountTypeName.equals(TypeName.BYTE) ? "buffer().$L(offset() " +
                    "+ $L, (byte) (Long.bitCount(fieldsMask)))" : "buffer().$L(offset() + $L, Long.bitCount(fieldsMask))";
                return builder.addStatement(putLength, PUTTER_NAMES.get(lengthTypeName), offset(LENGTH))
                    .addStatement(putFieldCount, PUTTER_NAMES.get(fieldCountTypeName), offset(FIELD_COUNT))
                    .addStatement("buffer().putLong(offset() + $L, fieldsMask)", offset(BIT_MASK))
                    .addStatement("return super.build()")
                    .build();
            }

            private MethodSpec generateBuildWithDefaultNull(
                String putLength)
            {
                final String putFieldCount = fieldCountTypeName.equals(TypeName.BYTE) ? "buffer().$L(offset() " +
                    "+ $L, (byte) (lastFieldSet + 1))" : "buffer().$L(offset() + $L, lastFieldSet + 1)";
                return builder.addStatement(putLength, PUTTER_NAMES.get(lengthTypeName), offset(LENGTH))
                    .addStatement(putFieldCount, PUTTER_NAMES.get(fieldCountTypeName), offset(FIELD_COUNT))
                    .addStatement("return super.build()")
                    .build();
            }
        }
    }

    private static class ListField
    {
        private String fieldName;
        private TypeName type;
        private boolean isRequired;
        private Object defaultValue;

        ListField(
            String fieldName,
            TypeName type,
            boolean isRequired,
            Object defaultValue)
        {
            this.fieldName = fieldName;
            this.type = type;
            this.isRequired = isRequired;
            this.defaultValue = defaultValue;
        }

        public String fieldName()
        {
            return fieldName;
        }

        public TypeName type()
        {
            return type;
        }

        public boolean isRequired()
        {
            return isRequired;
        }

        public Object defaultValue()
        {
            return defaultValue;
        }
    }

    private static boolean isOctetsType(
        TypeName type)
    {
        return type instanceof ClassName && "OctetsFW".equals(((ClassName) type).simpleName());
    }

    private static boolean isStringType(
        ClassName classType)
    {
        return isString8Type(classType) || isString16Type(classType) || isString32Type(classType);
    }

    private static boolean isArrayType(
        AstType type)
    {
        return AstType.VARIANT_ARRAY.equals(type) || AstType.VARIANT_ARRAY8.equals(type) ||
            AstType.VARIANT_ARRAY16.equals(type) || AstType.VARIANT_ARRAY32.equals(type);
    }

    private static boolean isString8Type(
        ClassName classType)
    {
        String name = classType.simpleName();
        return "String8FW".equals(name);
    }

    private static boolean isString16Type(
        ClassName classType)
    {
        String name = classType.simpleName();
        return "String16FW".equals(name);
    }

    private static boolean isString32Type(
        ClassName classType)
    {
        String name = classType.simpleName();
        return "String32FW".equals(name);
    }

    private static boolean isEnumType(
        Kind kind)
    {
        return Kind.ENUM.equals(kind);
    }

    private static boolean isVariantType(
        Kind kind)
    {
        return Kind.VARIANT.equals(kind);
    }

    private static boolean isTypedefType(
        Kind kind)
    {
        return Kind.TYPEDEF.equals(kind);
    }

    private static boolean isVarintType(
        TypeName type)
    {
        return type instanceof ClassName && "Varint32FW".equals(((ClassName) type).simpleName()) ||
            type instanceof ClassName && "Varint64FW".equals(((ClassName) type).simpleName());
    }

    private static boolean isVarint32Type(
        TypeName type)
    {
        return type instanceof ClassName && "Varint32FW".equals(((ClassName) type).simpleName());
    }

    private static boolean isVarint64Type(
        TypeName type)
    {
        return type instanceof ClassName && "Varint64FW".equals(((ClassName) type).simpleName());
    }

    private static ClassName enumClassName(
        TypeName enumFWTypeName)
    {
        String enumFWName = ((ClassName) enumFWTypeName).simpleName();
        return ClassName.bestGuess(enumFWName.substring(0, enumFWName.length() - 2));
    }

    private static String variantRW(
        ClassName className)
    {
        String variantFWName = className.simpleName();
        return String.format("%s%sRW", Character.toLowerCase(variantFWName.charAt(0)),
            variantFWName.substring(1, variantFWName.length() - 2));
    }

    private static String variantRO(
        ClassName className)
    {
        String variantFWName = className.simpleName();
        return String.format("%s%sRO", Character.toLowerCase(variantFWName.charAt(0)),
            variantFWName.substring(1, variantFWName.length() - 2));
    }

    private static String arraySize(
        String fieldName)
    {
        return String.format("ARRAY_SIZE_%s", constant(fieldName));
    }

    private static String fieldOffset(
        String fieldName)
    {
        return String.format("FIELD_OFFSET_%s", constant(fieldName));
    }

    private static String defaultConstant(
        String fieldName)
    {
        return String.format("DEFAULT_VALUE_%s", constant(fieldName));
    }

    private static String maskConstant(
        String fieldName)
    {
        return String.format("MASK_%s", constant(fieldName));
    }

    private static String offset(
        String name)
    {
        return String.format("%s_OFFSET", constant(name));
    }

    private static String fieldSize(
        String fieldName)
    {
        return String.format("FIELD_SIZE_%s", constant(fieldName));
    }

    private static String size(
        String name)
    {
        return String.format("%s_SIZE", constant(name));
    }

    private static String fieldIndex(
        String fieldName)
    {
        return String.format("INDEX_%s", constant(fieldName));
    }

    private static String defaultMethodName(
        String name)
    {
        return String.format("default%s%s", Character.toUpperCase(name.charAt(0)), name.substring(1));
    }

    private static String constant(
        String fieldName)
    {
        return fieldName.replaceAll("([^_A-Z])([A-Z])", "$1_$2").toUpperCase();
    }

    private static String methodName(
        String name)
    {
        return RESERVED_METHOD_NAMES.contains(name) ? name + "$" : name;
    }
}
