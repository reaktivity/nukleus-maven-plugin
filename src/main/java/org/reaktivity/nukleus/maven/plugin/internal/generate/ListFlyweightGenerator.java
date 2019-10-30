/**
 * Copyright 2016-2019 The Reaktivity Project
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
    private static final String PHYSICAL_LENGTH = "PHYSICAL_LENGTH";
    private static final String LOGICAL_LENGTH = "LOGICAL_LENGTH";
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
    private final NullValueConstantGenerator nullValueConstant;
    private final MemberFieldGenerator memberField;
    private final OptionalOffsetsFieldGenerator optionalOffsets;
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
        TypeName physicalLengthType,
        TypeName logicalLengthType,
        Byte nullValue,
        TypeResolver resolver)
    {
        super(listName);
        this.baseName = baseName;
        this.builder = classBuilder(listName).superclass(flyweightName).addModifiers(PUBLIC, FINAL);
        this.memberSizeConstant = new MemberSizeConstantGenerator(listName, builder, physicalLengthType, logicalLengthType,
            nullValue);
        this.memberOffsetConstant = new MemberOffsetConstantGenerator(listName, builder, nullValue);
        this.maskConstant = new MaskConstantGenerator(listName, builder);
        this.fieldIndexConstant = new FieldIndexConstantGenerator(listName, builder);
        this.defaultValueConstant = new DefaultValueConstantGenerator(listName, builder, resolver);
        this.nullValueConstant = new NullValueConstantGenerator(listName, builder, nullValue);
        this.memberField = new MemberFieldGenerator(listName, builder);
        this.optionalOffsets = new OptionalOffsetsFieldGenerator(listName, builder, nullValue);
        this.memberAccessor = new MemberAccessorGenerator(listName, builder, logicalLengthType, resolver, nullValue);
        this.wrapMethod = new WrapMethodGenerator(nullValue);
        this.tryWrapMethod = new TryWrapMethodGenerator(nullValue);
        this.limitMethod = new LimitMethodGenerator(physicalLengthType);
        this.toStringMethod = new ToStringMethodGenerator(nullValue);
        this.builderClass = new BuilderClassGenerator(listName, flyweightName, physicalLengthType, logicalLengthType, resolver,
            nullValue);
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
        boolean isRequired)
    {
        memberSizeConstant.addMember(name, typeName);
        fieldIndexConstant.addMember(name);
        maskConstant.addMember(name);
        defaultValueConstant.addMember(name, type, typeName, unsignedTypeName, defaultValue);
        memberField.addMember(name, typeName, byteOrder, defaultValue);
        optionalOffsets.addMember(name);
        memberAccessor.addMember(name, type, typeName, unsignedTypeName, byteOrder, isRequired, defaultValue);
        wrapMethod.addMember(name, typeName, defaultValue, isRequired);
        tryWrapMethod.addMember(name, typeName, defaultValue, isRequired);
        toStringMethod.addMember(name, typeName, defaultValue, isRequired);
        builderClass.addMember(name, type, typeName, unsignedTypeName, size, sizeType, usedAsSize, defaultValue,
            byteOrder, isRequired);
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
        memberField.build();
        optionalOffsets.build();
        memberAccessor.build();
        return builder.addField(bitmask())
            .addMethod(wrapMethod.generate())
            .addMethod(tryWrapMethod.generate())
            .addMethod(limitMethod.generate())
            .addMethod(toStringMethod.generate())
            .addType(builderClass.generate())
            .build();
    }

    private FieldSpec bitmask()
    {
        return FieldSpec.builder(long.class, "bitmask", PRIVATE).build();
    }

    private static final class MemberOffsetConstantGenerator extends ClassSpecMixinGenerator
    {
        private MemberOffsetConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            Byte nullValue)
        {
            super(thisType, builder);

            builder.addField(FieldSpec.builder(int.class, offset(PHYSICAL_LENGTH), PRIVATE, STATIC, FINAL)
                .initializer("0")
                .build());
            builder.addField(FieldSpec.builder(int.class, offset(LOGICAL_LENGTH), PRIVATE, STATIC, FINAL)
                .initializer(String.format("%s + %s", offset(PHYSICAL_LENGTH), size(PHYSICAL_LENGTH)))
                .build());
            if (nullValue == null)
            {
                builder.addField(FieldSpec.builder(int.class, offset(BIT_MASK), PRIVATE, STATIC, FINAL)
                    .initializer(String.format("%s + %s", offset(LOGICAL_LENGTH), size(LOGICAL_LENGTH)))
                    .build());
                builder.addField(FieldSpec.builder(int.class, offset(FIRST_FIELD), PRIVATE, STATIC, FINAL)
                    .initializer(String.format("%s + %s", offset(BIT_MASK), size(BIT_MASK)))
                    .build());
            }
            else
            {
                builder.addField(FieldSpec.builder(int.class, offset(FIRST_FIELD), PRIVATE, STATIC, FINAL)
                    .initializer(String.format("%s + %s", offset(LOGICAL_LENGTH), size(LOGICAL_LENGTH)))
                    .build());
            }
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
        private MemberSizeConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            TypeName physicalLengthType,
            TypeName logicalLengthType,
            Byte nullValue)
        {
            super(thisType, builder);

            builder.addField(
                FieldSpec.builder(int.class, size(PHYSICAL_LENGTH), PRIVATE, STATIC, FINAL)
                    .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, TYPE_NAMES.get(physicalLengthType))
                    .build());
            builder.addField(
                FieldSpec.builder(int.class, size(LOGICAL_LENGTH), PRIVATE, STATIC, FINAL)
                    .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, TYPE_NAMES.get(logicalLengthType))
                    .build());
            if (nullValue == null)
            {
                builder.addField(
                    FieldSpec.builder(int.class, size(BIT_MASK), PRIVATE, STATIC, FINAL)
                        .initializer("$T.SIZE_OF_LONG", BIT_UTIL_TYPE)
                        .build());
            }
        }

        public MemberSizeConstantGenerator addMember(
            String name,
            TypeName type)
        {
            if (type.isPrimitive())
            {
                builder.addField(
                    FieldSpec.builder(int.class, fieldSize(name), PRIVATE, STATIC, FINAL)
                        .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, TYPE_NAMES.get(type))
                        .build());
            }
            return this;
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
                else if (node instanceof AstVariantNode)
                {
                    AstVariantNode variantNode = (AstVariantNode) node;
                    AstType ofType = variantNode.of();
                    TypeName typeOfConstant = Objects.requireNonNullElse(resolver.resolveUnsignedType(ofType),
                        resolver.resolveType(ofType));
                    defaultValueBuilder = FieldSpec.builder(typeOfConstant, defaultConstant(fieldName), PRIVATE,
                        STATIC, FINAL);
                    if (ofType.equals(AstType.STRING) || ofType.equals(AstType.STRING16) || ofType.equals(AstType.STRING32))
                    {
                        defaultValueBuilder.initializer("\"$L\"", defaultValue);
                    }
                    else
                    {
                        defaultValueBuilder.initializer("$L", defaultValue);
                    }
                }
                else if (node instanceof AstEnumNode)
                {
                    AstEnumNode enumNode = (AstEnumNode) node;
                    ClassName enumFlyweightName = (ClassName) typeName;
                    ClassName enumName = enumFlyweightName.peerClass(enumNode.name());
                    defaultValueBuilder = FieldSpec.builder(enumName, defaultConstant(fieldName), PRIVATE, STATIC, FINAL)
                        .initializer("$T.$L", enumName, defaultValue);
                }
                builder.addField(defaultValueBuilder.build());
            }
            return this;
        }
    }

    private static final class NullValueConstantGenerator extends ClassSpecMixinGenerator
    {

        protected NullValueConstantGenerator(
            ClassName thisType,
            Builder builder,
            Byte nullValue)
        {
            super(thisType, builder);
            if (nullValue != null)
            {
                builder.addField(FieldSpec.builder(byte.class, "NULL_VALUE", PRIVATE, STATIC, FINAL)
                    .initializer(String.valueOf(nullValue))
                    .build());
                builder.addField(FieldSpec.builder(int.class, "NULL_VALUE_SIZE", PRIVATE, STATIC, FINAL)
                    .initializer("$T.SIZE_OF_BYTE", BIT_UTIL_TYPE)
                    .build());
            }
        }
    }

    private static final class MemberFieldGenerator extends ClassSpecMixinGenerator
    {
        private MemberFieldGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MemberFieldGenerator addMember(
            String name,
            TypeName type,
            AstByteOrder byteOrder,
            Object defaultValue)
        {
            if (!type.isPrimitive())
            {
                addNonPrimitiveMember(name, type, byteOrder, defaultValue);
            }
            return this;
        }

        private MemberFieldGenerator addNonPrimitiveMember(
            String name,
            TypeName type,
            AstByteOrder byteOrder,
            Object defaultValue)
        {
            String fieldRO = String.format("%sRO", name);
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(type, fieldRO, PRIVATE);
            if (type instanceof ClassName && (isString16Type((ClassName) type) ||
                isString32Type((ClassName) type)) && byteOrder == NETWORK)
            {
                fieldBuilder.initializer("new $T($T.BIG_ENDIAN)", type, ByteOrder.class);
            }
            else
            {
                fieldBuilder.initializer("new $T()", type);
            }

            builder.addField(fieldBuilder.build());
            return this;
        }
    }

    private static final class OptionalOffsetsFieldGenerator extends ClassSpecMixinGenerator
    {
        private String memberName;
        private Byte nullValue;

        protected OptionalOffsetsFieldGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            Byte nullValue)
        {
            super(thisType, builder);
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
            if (nullValue == null)
            {
                builder.addField(FieldSpec.builder(int[].class, "optionalOffsets", PRIVATE, FINAL)
                    .initializer(String.format("new int[%s + 1]", fieldIndex(memberName)))
                    .build());
            }
            return super.build();
        }
    }

    private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
    {
        private final TypeResolver resolver;
        private final Byte nullValue;
        private MemberAccessorGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            TypeName logicalLengthType,
            TypeResolver resolver,
            Byte nullValue)
        {
            super(thisType, builder);
            builder.addMethod(methodBuilder("length")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return buffer().$L(offset() + $L)", GETTER_NAMES.get(logicalLengthType), offset(LOGICAL_LENGTH))
                .build());
            if (nullValue == null)
            {
                builder.addMethod(methodBuilder("bitmask")
                    .addModifiers(PRIVATE)
                    .returns(long.class)
                    .addStatement("return buffer().getLong(offset() + $L)", offset(BIT_MASK))
                    .build());
            }
            this.nullValue = nullValue;
            this.resolver = resolver;
        }

        public MemberAccessorGenerator addMember(
            String name,
            AstType type,
            TypeName typeName,
            TypeName unsignedType,
            AstByteOrder byteOrder,
            boolean isRequired,
            Object defaultValue)
        {
            if (typeName.isPrimitive())
            {
                addPrimitiveMember(name, typeName, unsignedType, byteOrder, isRequired, defaultValue);
            }
            else
            {
                addNonPrimitiveMember(name, type, typeName, isRequired, defaultValue);
            }
            return this;
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
            Object defaultValue)
        {
            CodeBlock.Builder codeBlock = CodeBlock.builder();
            TypeName returnType = typeName;
            AstNamedNode namedNode = resolver.resolve(type.name());
            if (namedNode == null)
            {
                addMember(defaultValue, codeBlock, name, isRequired, "$LRO");
            }
            else if (Kind.ENUM.equals(namedNode.getKind()))
            {
                returnType = addEnumMember(defaultValue, codeBlock, name, type, typeName, isRequired);
            }
            else if (Kind.VARIANT.equals(namedNode.getKind()))
            {
                returnType = addVariantMember(defaultValue, codeBlock, name, type, typeName, isRequired);
            }
            else
            {
                addMember(defaultValue, codeBlock, name, isRequired, "$LRO");
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
            boolean isRequired)
        {
            AstVariantNode variantNode = (AstVariantNode) resolver.resolve(type.name());
            AstType ofType = variantNode.of();
            TypeName ofTypeName = resolver.resolveType(ofType);
            TypeName primitiveReturnType = ofTypeName.equals(TypeName.BYTE) || ofTypeName.equals(TypeName.SHORT) ||
                ofTypeName.equals(TypeName.INT) ? TypeName.INT : TypeName.LONG;
            TypeName returnType = Objects.requireNonNullElse(resolver.resolveUnsignedType(ofType),
                ofTypeName.isPrimitive() ? primitiveReturnType : ClassName.get(String.class));
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
            String bitmask = nullValue == null ? "bitmask()" : "bitmask";
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
        private final Byte nullValue;

        private WrapMethodGenerator(
            Byte nullValue)
        {
            super(methodBuilder("wrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName));
            this.nullValue = nullValue;
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
            builder.addStatement("final int length = length()")
                .addStatement("bitmask = 0")
                .addStatement("int fieldLimit = offset + $L + $L", offset(LOGICAL_LENGTH), size(LOGICAL_LENGTH))
                .beginControlFlow("for (int field = $L; field < length; field++)",
                    fieldIndex(fields.get(0).fieldName()))
                .addStatement("checkLimit(fieldLimit + $T.SIZE_OF_BYTE, limit)", BIT_UTIL_TYPE)
                .beginControlFlow("switch (field)");

            for (ListField field : fields)
            {
                String fieldName = field.fieldName();
                builder.beginControlFlow("case $L:", fieldIndex(fieldName));
                if (field.isRequired())
                {
                    builder.addStatement("$LRO.wrap(buffer, fieldLimit, maxLimit)", fieldName)
                        .addStatement("fieldLimit = $LRO.limit()", fieldName)
                        .addStatement("bitmask |= 1 << $L", fieldIndex(fieldName));
                }
                else
                {
                    builder.beginControlFlow("if (buffer().getByte(fieldLimit) != NULL_VALUE)")
                        .addStatement("$LRO.wrap(buffer, fieldLimit, maxLimit)", fieldName)
                        .addStatement("fieldLimit = $LRO.limit()", fieldName)
                        .addStatement("bitmask |= 1 << $L", fieldIndex(fieldName))
                        .endControlFlow()
                        .beginControlFlow("else")
                        .addStatement("fieldLimit += NULL_VALUE_SIZE")
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
            builder.addStatement("super.wrap(buffer, offset, maxLimit)")
                .addStatement("checkLimit(offset + $L + $L, maxLimit)", offset(PHYSICAL_LENGTH), size(PHYSICAL_LENGTH))
                .addStatement("final int limit = limit()")
                .addStatement("checkLimit(limit, maxLimit)");
            if (nullValue == null)
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
        private final Byte nullValue;

        private TryWrapMethodGenerator(
            Byte nullValue)
        {
            super(methodBuilder("tryWrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName));
            this.nullValue = nullValue;
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
            builder.beginControlFlow("if (super.tryWrap(buffer, offset, maxLimit) == null)")
                .addStatement("return null")
                .endControlFlow()
                .beginControlFlow("if (offset + $L + $L > maxLimit)", offset(PHYSICAL_LENGTH), size(PHYSICAL_LENGTH))
                .addStatement("return null")
                .endControlFlow()
                .addStatement("final int limit = limit()")
                .beginControlFlow("if (limit > maxLimit)")
                .addStatement("return null")
                .endControlFlow();

            if (nullValue == null)
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
            builder.addStatement("final int length = length()")
                .addStatement("bitmask = 0")
                .addStatement("int fieldLimit = offset + $L + $L", offset(LOGICAL_LENGTH), size(LOGICAL_LENGTH))
                .beginControlFlow("for (int field = $L; field < length; field++)",
                    fieldIndex(fields.get(0).fieldName()))
                .beginControlFlow("if (fieldLimit + $T.SIZE_OF_BYTE > limit)", BIT_UTIL_TYPE)
                .addStatement("return null")
                .endControlFlow()
                .beginControlFlow("switch (field)");
            for (ListField field : fields)
            {
                String fieldName = field.fieldName();
                builder.beginControlFlow("case $L:", fieldIndex(fieldName));
                if (field.isRequired())
                {
                    builder.beginControlFlow("if ($LRO.tryWrap(buffer, fieldLimit, maxLimit) == null)", fieldName)
                        .addStatement("return null")
                        .endControlFlow()
                        .addStatement("fieldLimit = $LRO.limit()", fieldName)
                        .addStatement("bitmask |= 1 << $L", fieldIndex(fieldName));
                }
                else
                {
                    builder.beginControlFlow("if (buffer().getByte(fieldLimit) != NULL_VALUE)")
                        .beginControlFlow("if ($LRO.tryWrap(buffer, fieldLimit, maxLimit) == null)", fieldName)
                        .addStatement("return null")
                        .endControlFlow()
                        .addStatement("fieldLimit = $LRO.limit()", fieldName)
                        .addStatement("bitmask |= 1 << $L", fieldIndex(fieldName))
                        .endControlFlow()
                        .beginControlFlow("else")
                        .addStatement("fieldLimit += NULL_VALUE_SIZE")
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
        private LimitMethodGenerator(
            TypeName physicalLengthType)
        {
            super(methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class));
            builder.addStatement("return offset() + buffer().$L(offset() + $L)", GETTER_NAMES.get(physicalLengthType),
                offset(PHYSICAL_LENGTH));
        }

        @Override
        public MethodSpec generate()
        {
            return builder.build();
        }
    }

    private final class ToStringMethodGenerator extends MethodSpecGenerator
    {
        private final List<ListField> fields = new ArrayList<>();
        private final Byte nullValue;

        private ToStringMethodGenerator(
            Byte nullValue)
        {
            super(methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(String.class));
            this.nullValue = nullValue;
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
            if (nullValue == null)
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
        private final MemberAccessorGenerator memberAccessor;
        private final MemberMutatorGenerator memberMutator;
        private final WrapMethodGenerator wrapMethod;
        private final BuildMethodGenerator buildMethod;

        private BuilderClassGenerator(
            ClassName listType,
            ClassName flyweightType,
            TypeName physicalLengthType,
            TypeName logicalLengthType,
            TypeResolver resolver,
            Byte nullValue)
        {
            this(listType.nestedClass("Builder"), flyweightType.nestedClass("Builder"), listType, physicalLengthType,
                logicalLengthType, resolver, nullValue);
        }

        private BuilderClassGenerator(
            ClassName thisType,
            ClassName builderRawType,
            ClassName listType,
            TypeName physicalLengthType,
            TypeName logicalLengthType,
            TypeResolver resolver,
            Byte nullValue)
        {
            super(thisType);
            this.builder = classBuilder(thisType.simpleName())
                .addModifiers(PUBLIC, STATIC, FINAL)
                .superclass(ParameterizedTypeName.get(builderRawType, listType));
            this.listType = listType;
            this.fieldsMask = new FieldsMaskGenerator(thisType, builder, nullValue);
            this.memberField = new MemberFieldGenerator(thisType, builder);
            this.memberAccessor = new MemberAccessorGenerator(thisType, builder, resolver, nullValue);
            this.memberMutator = new MemberMutatorGenerator(thisType, builder, resolver, nullValue);
            this.wrapMethod = new WrapMethodGenerator(nullValue);
            this.buildMethod = new BuildMethodGenerator(physicalLengthType, logicalLengthType, nullValue);
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
            boolean isRequired)
        {
            memberField.addMember(name, typeName, byteOrder);
            memberAccessor.addMember(name, type, typeName, isRequired);
            memberMutator.addMember(name, type, typeName, unsignedType, usedAsSize, size, sizeType, byteOrder,
                defaultValue,
                isRequired);
            buildMethod.addMember(name, isRequired);
        }

        @Override
        public TypeSpec generate()
        {
            fieldsMask.build();
            memberField.build();
            memberAccessor.build();
            memberMutator.build();
            return builder.addMethod(constructor())
                .addMethod(wrapMethod.generate())
                .addMethod(buildMethod.generate())
                .build();
        }

        private static final class FieldsMaskGenerator extends ClassSpecMixinGenerator
        {
            final Byte nullValue;
            protected FieldsMaskGenerator(
                ClassName thisType,
                Builder builder,
                Byte nullValue)
            {
                super(thisType, builder);
                this.nullValue = nullValue;
            }

            @Override
            public Builder build()
            {
                if (nullValue == null)
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
            private MemberFieldGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
            }

            public MemberFieldGenerator addMember(
                String name,
                TypeName type,
                AstByteOrder byteOrder)
            {
                if (!type.isPrimitive())
                {
                    String fieldRW = String.format("%sRW", name);

                    if (type instanceof ClassName)
                    {
                        ClassName classType = (ClassName) type;
                        TypeName builderType = classType.nestedClass("Builder");

                        if ((isString16Type(classType) || isString32Type(classType)) && byteOrder == NETWORK)
                        {
                            builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                                .initializer("new $T($T.BIG_ENDIAN)", builderType, ByteOrder.class)
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

        private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
        {
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
                TypeResolver resolver,
                Byte nullValue)
            {
                super(thisType, builder);
                requiredFieldPosition = new HashMap<>();
                this.resolver = resolver;
                this.nullValue = nullValue;
            }

            public MemberAccessorGenerator addMember(
                String name,
                AstType type,
                TypeName typeName,
                boolean isRequired)
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
                TypeResolver resolver,
                Byte nullValue)
            {
                super(thisType, builder);
                this.requiredFieldPosition = new HashMap<>();
                this.defaultNullMutators = new ArrayList<>();
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
                boolean isRequired)
            {
                if (typeName.isPrimitive())
                {
                    addPrimitiveMember(name, typeName, unsignedType, usedAsSize, byteOrder);
                }
                else
                {
                    addNonPrimitiveMember(name, type, typeName, isRequired);
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
                boolean isRequired)
            {
                ClassName className = (ClassName) typeName;
                AstNamedNode namedNode = resolver.resolve(type.name());
                if (isStringType(className))
                {
                    addStringType(className, name);
                }
                else if (Kind.ENUM.equals(namedNode.getKind()))
                {
                    addEnumType(name, type, className);
                }
                else if (Kind.VARIANT.equals(namedNode.getKind()))
                {
                    addVariantType(name, type, className, isRequired);
                }
                else
                {
                    addUnionType(name, className);
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
                boolean isRequired)
            {
                AstVariantNode variantNode = (AstVariantNode) resolver.resolve(type.name());
                ClassName builderType = variantFlyweightName.nestedClass("Builder");
                AstType ofType = variantNode.of();
                TypeName ofTypeName = resolver.resolveType(ofType);
                TypeName primitiveReturnType = ofTypeName.equals(TypeName.BYTE) || ofTypeName.equals(TypeName.SHORT) ||
                    ofTypeName.equals(TypeName.INT) ? TypeName.INT : TypeName.LONG;
                TypeName returnType = Objects.requireNonNullElse(resolver.resolveUnsignedType(variantNode.of()),
                    ofTypeName.isPrimitive() ? primitiveReturnType : ClassName.get(String.class));
                MethodSpec.Builder methodBuilder = methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(thisType)
                    .addParameter(returnType, "value");
                methodBuilder.addStatement("$T $LRW = $L()", builderType, name, name)
                    .addStatement("$LRW.set(value)", name);
                if (nullValue == null)
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
                methodBuilder.addStatement("limit($LRW.build().limit())", name)
                    .addStatement("return this");
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
                defaultNullMutators.add(defaultNullMethod
                    .addStatement("int newLimit = limit() + $T.SIZE_OF_BYTE", BIT_UTIL_TYPE)
                    .addStatement("checkLimit(limit(), newLimit)")
                    .addStatement("buffer().putByte(limit(), NULL_VALUE)")
                    .addStatement("lastFieldSet = $L", fieldIndex(name))
                    .addStatement("limit(newLimit)")
                    .addStatement("return this")
                    .build());
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
                if (AstType.STRING.equals(enumNode.valueType()) || AstType.STRING16.equals(enumNode.valueType()) ||
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
            final Byte nullValue;
            private WrapMethodGenerator(
                Byte nullValue)
            {
                super(methodBuilder("wrap")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .returns(thisName));
                this.nullValue = nullValue;
            }

            @Override
            public MethodSpec generate()
            {
                return builder.addStatement("super.wrap(buffer, offset, maxLimit)")
                              .addStatement(nullValue == null ? "fieldsMask = 0" : "lastFieldSet = -1")
                              .addStatement("int newLimit = limit() + $L", offset(FIRST_FIELD))
                              .addStatement("checkLimit(newLimit, maxLimit())")
                              .addStatement("limit(newLimit)")
                              .addStatement("return this")
                              .build();
            }
        }

        private final class BuildMethodGenerator extends MethodSpecGenerator
        {
            private final TypeName physicalLengthType;
            private final TypeName logicalLengthType;
            private final Byte nullValue;
            private int position;
            private Map<String, Integer> requiredFieldPosition;

            private BuildMethodGenerator(
                TypeName physicalLengthType,
                TypeName logicalLengthType,
                Byte nullValue)
            {
                super(methodBuilder("build")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(listType));
                requiredFieldPosition = new HashMap<>();
                this.physicalLengthType = physicalLengthType;
                this.logicalLengthType = logicalLengthType;
                this.nullValue = nullValue;
            }

            public BuildMethodGenerator addMember(
                String name,
                boolean isRequired)
            {
                if (isRequired)
                {
                    if (nullValue == null)
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
                final String putPhysicalLength = physicalLengthType.equals(TypeName.BYTE) ? "buffer().$L(offset()" +
                    " + $L, (byte) (limit() - offset()))" : "buffer().$L(offset() + $L, limit() - offset())";
                if (nullValue == null)
                {
                    return generateBuild(putPhysicalLength);
                }
                return generateBuildWithDefaultNull(putPhysicalLength);
            }

            private MethodSpec generateBuild(
                String putPhysicalLength)
            {
                final String putLogicalLength = logicalLengthType.equals(TypeName.BYTE) ? "buffer().$L(offset() " +
                    "+ $L, (byte) (Long.bitCount(fieldsMask)))" : "buffer().$L(offset() + $L, Long.bitCount(fieldsMask))";
                return builder.addStatement(putPhysicalLength, PUTTER_NAMES.get(physicalLengthType), offset(PHYSICAL_LENGTH))
                    .addStatement(putLogicalLength, PUTTER_NAMES.get(logicalLengthType), offset(LOGICAL_LENGTH))
                    .addStatement("buffer().putLong(offset() + $L, fieldsMask)", offset(BIT_MASK))
                    .addStatement("return super.build()")
                    .build();
            }

            private MethodSpec generateBuildWithDefaultNull(
                String putPhysicalLength)
            {
                final String putLogicalLength = logicalLengthType.equals(TypeName.BYTE) ? "buffer().$L(offset() " +
                    "+ $L, (byte) (lastFieldSet + 1))" : "buffer().$L(offset() + $L, lastFieldSet + 1)";
                return builder.addStatement(putPhysicalLength, PUTTER_NAMES.get(physicalLengthType), offset(PHYSICAL_LENGTH))
                    .addStatement(putLogicalLength, PUTTER_NAMES.get(logicalLengthType), offset(LOGICAL_LENGTH))
                    .addStatement("return super.build()")
                    .build();
            }
        }
    }

    private class ListField
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
        String name = classType.simpleName();
        return "StringFW".equals(name) || isString16Type(classType) || isString32Type(classType);
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
