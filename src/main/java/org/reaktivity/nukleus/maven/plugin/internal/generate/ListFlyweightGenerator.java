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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;

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
    private final FieldIndexConstantGenerator fieldIndexConstant;
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
        TypeName logicalLengthType)
    {
        super(listName);

        this.baseName = baseName;
        this.builder = classBuilder(listName).superclass(flyweightName).addModifiers(PUBLIC, FINAL);
        this.memberSizeConstant = new MemberSizeConstantGenerator(listName, builder, physicalLengthType, logicalLengthType);
        this.memberOffsetConstant = new MemberOffsetConstantGenerator(listName, builder);
        this.fieldIndexConstant = new FieldIndexConstantGenerator(listName, builder);
        this.memberField = new MemberFieldGenerator(listName, builder);
        this.optionalOffsets = new OptionalOffsetsFieldGenerator(listName, builder);
        this.memberAccessor = new MemberAccessorGenerator(listName, builder);
        this.wrapMethod = new WrapMethodGenerator();
        this.tryWrapMethod = new TryWrapMethodGenerator();
        this.limitMethod = new LimitMethodGenerator();
        this.toStringMethod = new ToStringMethodGenerator();
        this.builderClass = new BuilderClassGenerator(listName, flyweightName);
    }

    public ListFlyweightGenerator addMember(
        String name,
        TypeName type,
        TypeName unsignedType,
        int size,
        String sizeName,
        TypeName sizeType,
        boolean usedAsSize,
        Object defaultValue,
        AstByteOrder byteOrder,
        boolean isRequired)
    {
        memberSizeConstant.addMember(name, type);
        fieldIndexConstant.addMember(name);
        memberField.addMember(name, type, byteOrder, defaultValue);
        optionalOffsets.addMember(name);
        memberAccessor.addMember(name, type, unsignedType, byteOrder, isRequired);
        wrapMethod.addMember(name, type, isRequired);
        tryWrapMethod.addMember(name, type, isRequired);
        toStringMethod.addMember(name, type, unsignedType, size, sizeName);
        builderClass.addMember(name, type, unsignedType, size, sizeName, sizeType, usedAsSize, defaultValue, byteOrder,
            isRequired);
        return this;
    }

    @Override
    public TypeSpec generate()
    {
        memberSizeConstant.build();
        memberOffsetConstant.build();
        fieldIndexConstant.build();
        memberField.build();
        optionalOffsets.build();
        memberAccessor.build();
        return builder.addMethod(wrapMethod.generate())
            .addMethod(tryWrapMethod.generate())
            .addMethod(limitMethod.generate())
            .addMethod(toStringMethod.generate())
            .addType(builderClass.generate())
            .build();
    }

    private static final class MemberOffsetConstantGenerator extends ClassSpecMixinGenerator
    {
        private MemberOffsetConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);

            builder.addField(FieldSpec.builder(int.class, offset(PHYSICAL_LENGTH), PRIVATE, STATIC, FINAL)
                .initializer("0")
                .build());
            builder.addField(FieldSpec.builder(int.class, offset(LOGICAL_LENGTH), PRIVATE, STATIC, FINAL)
                .initializer(String.format("%s + %s", offset(PHYSICAL_LENGTH), size(PHYSICAL_LENGTH)))
                .build());
            builder.addField(FieldSpec.builder(int.class, offset(BIT_MASK), PRIVATE, STATIC, FINAL)
                .initializer(String.format("%s + %s", offset(LOGICAL_LENGTH), size(LOGICAL_LENGTH)))
                .build());
            builder.addField(FieldSpec.builder(int.class, offset(FIRST_FIELD), PRIVATE, STATIC, FINAL)
                .initializer(String.format("%s + %s", offset(BIT_MASK), size(BIT_MASK)))
                .build());
        }
    }

    private static final class MemberSizeConstantGenerator extends ClassSpecMixinGenerator
    {
        private MemberSizeConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder,
            TypeName physicalLengthType,
            TypeName logicalLengthType)
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
            builder.addField(
                FieldSpec.builder(int.class, size(BIT_MASK), PRIVATE, STATIC, FINAL)
                    .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, TYPE_NAMES.get(physicalLengthType))
                    .build());
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

        protected OptionalOffsetsFieldGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
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
            builder.addField(FieldSpec.builder(int[].class, "optionalOffsets", PRIVATE, FINAL)
                .initializer(String.format("new int[%s + 1]", fieldIndex(memberName)))
                .build());
            return super.build();
        }
    }

    private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
    {
        private MemberAccessorGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            builder.addMethod(methodBuilder("length")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return buffer().getInt(offset() + $L) & 0xFFFF", offset(LOGICAL_LENGTH))
                .build());
            builder.addMethod(methodBuilder("bitmask")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return buffer().getInt(offset() + $L) & 0xFFFF", offset(BIT_MASK))
                .build());
        }

        public MemberAccessorGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            AstByteOrder byteOrder,
            boolean isRequired)
        {
            if (type.isPrimitive())
            {
                addPrimitiveMember(name, type, unsignedType, byteOrder, isRequired);
            }
            else
            {
                addNonPrimitiveMember(name, type, isRequired);
            }
            return this;
        }

        private void addPrimitiveMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            AstByteOrder byteOrder,
            boolean isRequired)
        {
            TypeName generateType = (unsignedType != null) ? unsignedType : type;

            CodeBlock.Builder codeBlock = CodeBlock.builder();

            String getterName = GETTER_NAMES.get(type);
            if (getterName == null)
            {
                throw new IllegalStateException("member type not supported: " + type);
            }

            if (!isRequired)
            {
                codeBlock.addStatement("assert (bitmask() & (1 << $L)) != 0 : " +
                    "\"Field \\\"$L\\\" is not set\";", fieldIndex(name), name);
            }

            codeBlock.add("$[").add("return ");

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
            TypeName type,
            boolean isRequired)
        {
            CodeBlock.Builder codeBlock = CodeBlock.builder();

            if (!isRequired)
            {
                codeBlock.addStatement("assert (bitmask() & (1 << $L)) != 0 : " +
                    "\"Field \\\"$L\\\" is not set\";", fieldIndex(name), name);
            }
            codeBlock.addStatement("return $LRO", name);

            builder.addMethod(methodBuilder(methodName(name))
                .addModifiers(PUBLIC)
                .returns(type)
                .addCode(codeBlock.build())
                .build());
        }
    }

    private final class WrapMethodGenerator extends MethodSpecGenerator
    {
        private final List<ListField> fields = new ArrayList<>();

        private WrapMethodGenerator()
        {
            super(methodBuilder("wrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName));
        }

        public WrapMethodGenerator addMember(
            String name,
            TypeName type,
            boolean isRequired)
        {
            fields.add(new ListField(name, type, isRequired));
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            builder.addStatement("super.wrap(buffer, offset, maxLimit)")
                   .addStatement("final int bitmask = bitmask()")
                   .addStatement("int fieldLimit = offset + $L", offset(FIRST_FIELD))
                   .beginControlFlow("for (int field = $L; field < $L + 1; field++)",
                       fieldIndex(fields.get(0).fieldName()), fieldIndex(fields.get(fields.size() - 1).fieldName()))
                   .beginControlFlow("switch (field)");

            for (ListField field : fields)
            {
                String fieldName = field.fieldName();
                builder.beginControlFlow("case $L:", fieldIndex(fieldName));
                if (field.isRequired())
                {
                    builder.beginControlFlow("if ((bitmask & (1 << $L)) == 0)", fieldIndex(fieldName))
                        .addStatement("throw new IllegalArgumentException(\"Field \\\"$L\\\" is required but not set\")",
                            fieldName)
                        .endControlFlow();
                    if (field.type().isPrimitive())
                    {
                        builder.addStatement("optionalOffsets[$L] = fieldLimit", fieldIndex(fieldName))
                            .addStatement("fieldLimit += $L", fieldIndex(fieldName));
                    }
                    else
                    {
                        builder.addStatement("$LRO.wrap(buffer, fieldLimit, maxLimit)", fieldName)
                            .addStatement("fieldLimit = $LRO.limit()", fieldName);
                    }
                }
                else
                {
                    builder.beginControlFlow("if ((bitmask & (1 << $L)) != 0)", fieldIndex(fieldName));
                    if (field.type().isPrimitive())
                    {
                        builder.addStatement("optionalOffsets[$L] = fieldLimit", fieldIndex(fieldName))
                            .addStatement("fieldLimit += $L", fieldIndex(fieldName));
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

            return builder.endControlFlow()
                .endControlFlow()
                .addStatement("checkLimit(limit(), maxLimit)")
                .addStatement("return this")
                .build();
        }
    }

    private final class TryWrapMethodGenerator extends MethodSpecGenerator
    {
        private final List<ListField> fields = new ArrayList<>();

        private TryWrapMethodGenerator()
        {
            super(methodBuilder("tryWrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName));
        }

        public TryWrapMethodGenerator addMember(
            String name,
            TypeName type,
            boolean isRequired)
        {
            fields.add(new ListField(name, type, isRequired));
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            builder.beginControlFlow("if (null == super.tryWrap(buffer, offset, maxLimit))")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("final long bitmask = bitmask()")
                .addStatement("int fieldLimit = offset + $L", offset(FIRST_FIELD))
                .beginControlFlow("for (int field = $L; field < $L + 1; field++)",
                    fieldIndex(fields.get(0).fieldName()), fieldIndex(fields.get(fields.size() - 1).fieldName()))
                .beginControlFlow("switch (field)");

            for (ListField field : fields)
            {
                String fieldName = field.fieldName();
                builder.beginControlFlow("case $L:", fieldIndex(fieldName));
                if (field.isRequired())
                {
                    builder.beginControlFlow("if ((bitmask & (1 << $L)) == 0)", fieldIndex(fieldName))
                        .addStatement("return null")
                        .endControlFlow();
                    if (field.type().isPrimitive())
                    {
                        builder.addStatement("optionalOffsets[$L] = fieldLimit", fieldIndex(fieldName))
                            .addStatement("fieldLimit += $L", fieldIndex(fieldName));
                    }
                    else
                    {
                        builder.beginControlFlow("if (null == $LRO.tryWrap(buffer, fieldLimit, maxLimit))", fieldName)
                            .addStatement("return null")
                            .endControlFlow()
                            .addStatement("fieldLimit = $LRO.limit()", fieldName);
                    }
                }
                else
                {
                    builder.beginControlFlow("if ((bitmask & (1 << $L)) != 0)", fieldIndex(fieldName));
                    if (field.type().isPrimitive())
                    {
                        builder.addStatement("optionalOffsets[$L] = fieldLimit", fieldIndex(fieldName))
                            .addStatement("fieldLimit += $L", fieldIndex(fieldName));
                    }
                    else
                    {
                        builder.beginControlFlow("if (null == $LRO.tryWrap(buffer, fieldLimit, maxLimit))", fieldName)
                            .addStatement("return null")
                            .endControlFlow()
                            .addStatement("fieldLimit = $LRO.limit()", fieldName);
                    }
                    builder.endControlFlow();
                }
                builder.addStatement("break")
                    .endControlFlow();
            }
            return builder.endControlFlow()
                .endControlFlow()
                .beginControlFlow("if (limit() > maxLimit)")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("return this")
                .build();
        }
    }

    private final class LimitMethodGenerator extends MethodSpecGenerator
    {
        private LimitMethodGenerator()
        {
            super(methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class));
        }

        @Override
        public MethodSpec generate()
        {
            return builder.addStatement("return offset() + buffer().getInt(offset() + $L) & 0xFFFF", offset(PHYSICAL_LENGTH))
                .build();
        }
    }

    private final class ToStringMethodGenerator extends MethodSpecGenerator
    {
        private final List<String> formats = new ArrayList<>();
        private final List<String> args = new ArrayList<>();

        private ToStringMethodGenerator()
        {
            super(methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(String.class));
        }

        public ToStringMethodGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            int size,
            String sizeName)
        {
            boolean isArray = size != -1 || sizeName != null;
            formats.add(String.format("%s=%%%s", name, type.isPrimitive() && !isArray ? "d" : "s"));
            if (type instanceof ClassName && isStringType((ClassName) type))
            {
                args.add(String.format("%s() != null ? %sRO.asString() : null", name, name));
            }
            else
            {
                args.add(String.format("%s()", name));
            }
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            String typeName = constant(baseName);
            if (formats.isEmpty())
            {
                builder.addStatement("return $S", typeName);
            }
            else
            {
                String format = String.format("%s [%s]", typeName, String.join(", ", formats));
                builder.addStatement("return String.format($S, $L)", format, String.join(", ", args));
            }
            return builder.build();
        }

    }

    private static final class BuilderClassGenerator extends ClassSpecGenerator
    {
        private final TypeSpec.Builder builder;
        private final ClassName listType;
        private final MemberFieldGenerator memberField;
        private final MemberAccessorGenerator memberAccessor;
        private final MemberMutatorGenerator memberMutator;
        private final WrapMethodGenerator wrapMethod;
        private final BuildMethodGenerator buildMethod;

        private BuilderClassGenerator(
            ClassName listType,
            ClassName flyweightType)
        {
            this(listType.nestedClass("Builder"), flyweightType.nestedClass("Builder"), listType);
        }

        private BuilderClassGenerator(
            ClassName thisType,
            ClassName builderRawType,
            ClassName listType)
        {
            super(thisType);
            this.builder = classBuilder(thisType.simpleName())
                .addModifiers(PUBLIC, STATIC, FINAL)
                .superclass(ParameterizedTypeName.get(builderRawType, listType));
            this.listType = listType;
            this.memberField = new MemberFieldGenerator(thisType, builder);
            this.memberAccessor = new MemberAccessorGenerator(thisType, builder);
            this.memberMutator = new MemberMutatorGenerator(thisType, builder);
            this.wrapMethod = new WrapMethodGenerator(thisType, builder);
            this.buildMethod = new BuildMethodGenerator(thisType, builder);
        }

        private void addMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            int size,
            String sizeName,
            TypeName sizeType,
            boolean usedAsSize,
            Object defaultValue,
            AstByteOrder byteOrder,
            boolean isRequired)
        {
            memberField.addMember(name, type, byteOrder);
            memberAccessor.addMember(name, type);
            memberMutator.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType, byteOrder, defaultValue,
                isRequired);
            buildMethod.addMember(name, isRequired);
        }

        @Override
        public TypeSpec generate()
        {
            memberField.build();
            memberAccessor.build();
            memberMutator.build();
            return builder.addField(fieldsMask())
                .addMethod(constructor())
                .addMethod(wrapMethod.generate())
                .addMethod(buildMethod.generate())
                .build();
        }

        private FieldSpec fieldsMask()
        {
            return FieldSpec.builder(int.class, "fieldsMask")
                            .addModifiers(PRIVATE)
                            .build();
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
            private MemberAccessorGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
            }

            public MemberAccessorGenerator addMember(
                String name,
                TypeName type)
            {
                if (type instanceof ClassName)
                {
                    ClassName classType = (ClassName) type;
                    if (isStringType(classType))
                    {
                        TypeName builderType = classType.nestedClass("Builder");
                        builder.addMethod(methodBuilder(methodName(name))
                            .addModifiers(PRIVATE)
                            .returns(builderType)
                            .addStatement("return $LRW.wrap(buffer(), limit(), maxLimit())", name)
                            .build());
                    }
                }
                return this;
            }
        }

        private static final class MemberMutatorGenerator extends ClassSpecMixinGenerator
        {
            private int bitsOfOnes;
            private String priorRequiredField = null;
            private Map<String, Integer> bitsOfOnesByFieldName;

            private MemberMutatorGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
                bitsOfOnesByFieldName = new HashMap<>();
            }

            public MemberMutatorGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue,
                boolean isRequired)
            {
                if (type.isPrimitive())
                {
                    addPrimitiveMember(name, type, unsignedType, usedAsSize, byteOrder);
                }
                else
                {
                    addNonPrimitiveMember(name, type);
                }
                bitsOfOnes = (bitsOfOnes << 1) | 1;
                if (isRequired)
                {
                    bitsOfOnesByFieldName.put(name, bitsOfOnes);
                    priorRequiredField = name;
                }
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
                code.addStatement("assert (fieldsMask & ~$L) == 0 : \"Field \\\"$L\\\" is already set or subsequent fields " +
                    "are already set\"", String.format("0x%02X", bitsOfOnes), name);
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
                TypeName type)
            {
                if (type instanceof ClassName)
                {
                    ClassName className = (ClassName) type;
                    addClassType(name, className);
                }
            }

            private void addClassType(
                String name,
                ClassName className)
            {
                if (isStringType(className))
                {
                    addStringType(className, name);
                }
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
                if (priorRequiredField != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", bitsOfOnesByFieldName.get(priorRequiredField)), priorRequiredField);
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
                if (priorRequiredField != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", bitsOfOnesByFieldName.get(priorRequiredField)), priorRequiredField);
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
                if (priorRequiredField != null)
                {
                    methodBuilder.addStatement("assert (fieldsMask & $L) != 0 : \"Prior required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", bitsOfOnesByFieldName.get(priorRequiredField)), priorRequiredField);
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
                bitsOfOnes = 0;
                return super.build();
            }
        }

        private final class WrapMethodGenerator extends MethodSpecGenerator
        {
            private WrapMethodGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(methodBuilder("wrap")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .returns(thisName));
            }

            @Override
            public MethodSpec generate()
            {
                return builder.addStatement("super.wrap(buffer, offset, maxLimit)")
                              .addStatement("fieldsMask = 0")
                              .addStatement("int newLimit = limit() + $L", offset(FIRST_FIELD))
                              .addStatement("checkLimit(newLimit, maxLimit())")
                              .addStatement("limit(newLimit)")
                              .addStatement("return this")
                              .build();
            }
        }

        private final class BuildMethodGenerator extends MethodSpecGenerator
        {
            private int bitsOfOnes;
            private Map<String, Integer> bitsOfOnesByFieldName;

            private BuildMethodGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(methodBuilder("build")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(listType));
                bitsOfOnesByFieldName = new HashMap<>();
            }

            public BuildMethodGenerator addMember(
                String name,
                boolean isRequired)
            {
                bitsOfOnes = (bitsOfOnes << 1) | 1;
                if (isRequired)
                {
                    bitsOfOnesByFieldName.put(name, bitsOfOnes);
                    builder.addStatement("assert (fieldsMask & $L) != 0 : \"Required field \\\"$L\\\" is not " +
                        "set\"", String.format("0x%02X", bitsOfOnesByFieldName.get(name)), name);
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                return builder.addStatement("buffer().putInt(offset() + $L, limit() - offset() & 0xFFFF)",
                    offset(PHYSICAL_LENGTH))
                    .addStatement("buffer().putInt(offset() + $L, Integer.bitCount(fieldsMask) & 0xFFFF)", offset(LOGICAL_LENGTH))
                    .addStatement("buffer().putInt(offset() + $L, fieldsMask & 0xFFFF)", offset(BIT_MASK))
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

        ListField(
            String fieldName,
            TypeName type,
            boolean isRequired)
        {
            this.fieldName = fieldName;
            this.type = type;
            this.isRequired = isRequired;
        }

        public String fieldName()
        {
            return fieldName;
        }

        public boolean isRequired()
        {
            return isRequired;
        }

        public TypeName type()
        {
            return type;
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
        return String.format("FIELD_INDEX_%s", constant(fieldName));
    }

    private static String constant(
        String fieldName)
    {
        return fieldName.replaceAll("([^_A-Z])([A-Z])", "$1_$2").toUpperCase();
    }

    private static String methodName(String name)
    {
        return RESERVED_METHOD_NAMES.contains(name) ? name + "$" : name;
    }
}
