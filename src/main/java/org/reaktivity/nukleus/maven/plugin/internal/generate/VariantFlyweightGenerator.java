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
import static java.util.Collections.unmodifiableMap;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BIT_UTIL_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.FieldSpec.Builder;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class VariantFlyweightGenerator extends ClassSpecGenerator
{
    private final String baseName;
    private final TypeSpec.Builder builder;
    private final MemberFieldGenerator memberField;
    private final KindConstantGenerator memberKindConstant;
    private final MemberSizeConstantGenerator memberSizeConstant;
    private final MemberOffsetConstantGenerator memberOffsetConstant;
    private final MemberFieldValueConstantGenerator memberFieldValueConstant;
    private final MemberFieldMaxValueConstantGenerator memberFieldMaxValueConstant;
    private final TryWrapMethodGenerator tryWrapMethod;
    private final WrapMethodGenerator wrapMethod;
    private final ToStringMethodGenerator toStringMethod;
    private final KindAccessorGenerator kindAccessor;
    private final MemberAccessorGenerator memberAccessor;
    private final LimitMethodGenerator limitMethod;
    private final BuilderClassGenerator builderClass;
    private static final Map<String, String> NUMBER_BY_WORD;
    private AllMemberAccessorGenerator allMemberAccessor;

    static
    {
        Map<String, String> numberByWord = new HashMap<>();
        numberByWord.put("0", "zero");
        numberByWord.put("1", "one");
        NUMBER_BY_WORD = unmodifiableMap(numberByWord);
    }

    public VariantFlyweightGenerator(
        ClassName variantName,
        ClassName flyweightName,
        String baseName)
    {
        super(variantName);

        this.baseName = baseName;
        this.builder = classBuilder(variantName).superclass(flyweightName).addModifiers(PUBLIC, FINAL);
        this.memberField = new MemberFieldGenerator(variantName, builder);
        this.memberKindConstant = new KindConstantGenerator(variantName, builder);
        this.memberSizeConstant = new MemberSizeConstantGenerator(variantName, builder);
        this.memberOffsetConstant = new MemberOffsetConstantGenerator(variantName, builder);
        this.memberFieldValueConstant = new MemberFieldValueConstantGenerator(variantName, builder);
        this.tryWrapMethod = new TryWrapMethodGenerator();
        this.wrapMethod = new WrapMethodGenerator();
        this.toStringMethod = new ToStringMethodGenerator();
        this.kindAccessor = new KindAccessorGenerator(variantName, builder);
        this.memberAccessor = new MemberAccessorGenerator(variantName, builder);
        this.limitMethod = new LimitMethodGenerator();
        this.builderClass = new BuilderClassGenerator(variantName, flyweightName);
        this.memberFieldMaxValueConstant = new MemberFieldMaxValueConstantGenerator(variantName, builder);
    }

    public VariantFlyweightGenerator setExplicitType(
        TypeName type)
    {
        this.allMemberAccessor = new AllMemberAccessorGenerator(type);
        builderClass.setExplicitType(type);
        return this;
    }

    public VariantFlyweightGenerator addMember(
        int kindValue,
        String name,
        TypeName type)
    {
        memberKindConstant.addMember(kindValue, name);
        memberOffsetConstant.addMember(name, type);
        memberFieldValueConstant.addMember(name, type);
        memberField.addMember(name, type);
        memberSizeConstant.addMember(name, type);
        wrapMethod.addMember(name, type);
        tryWrapMethod.addMember(name, type);
        toStringMethod.addMember(name, type);
        memberAccessor.addMember(name, type);
        limitMethod.addMember(name, type);
        builderClass.addMember(name, type);
        if (allMemberAccessor != null)
        {
            allMemberAccessor.addMember(name, type);
            memberFieldMaxValueConstant.addMember(name, type);
        }
        return this;
    }

    @Override
    public TypeSpec generate()
    {
        memberKindConstant.build();
        memberSizeConstant.build();
        memberField.build();
        kindAccessor.build();
        memberAccessor.build();
        if (allMemberAccessor != null)
        {
            builder.addMethod(allMemberAccessor.generate());
            memberFieldMaxValueConstant.build();
        }
        return builder.addMethod(tryWrapMethod.generate())
                      .addMethod(wrapMethod.generate())
                      .addMethod(toStringMethod.generate())
                      .addMethod(limitMethod.generate())
                      .addType(builderClass.generate())
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
            TypeName type)
        {
            if (type != null && !type.isPrimitive())
            {
                String fieldRO = String.format("%sRO", name);
                Builder fieldBuilder = FieldSpec.builder(type, fieldRO, PRIVATE, FINAL);
                fieldBuilder.initializer("new $T()", type);

                builder.addField(fieldBuilder.build());
            }
            return this;
        }
    }

    private static final class KindConstantGenerator extends ClassSpecMixinGenerator
    {
        private KindConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public KindConstantGenerator addMember(
            int value,
            String name)
        {
            builder.addField(
                FieldSpec.builder(int.class, kind(name), PUBLIC, STATIC, FINAL)
                    .initializer("$L", value)
                    .build());

            return this;
        }
    }

    private static final class MemberSizeConstantGenerator extends ClassSpecMixinGenerator
    {
        private static final Map<TypeName, String> SIZEOF_BY_NAME = initSizeofByName();

        private MemberSizeConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder.addField(
                    FieldSpec.builder(int.class, size("kind"), PRIVATE, STATIC, FINAL)
                    .initializer("$T.SIZE_OF_BYTE", BIT_UTIL_TYPE)
                    .build()));
        }

        public MemberSizeConstantGenerator addMember(
            String name,
            TypeName type)
        {
            if (type != null && type.isPrimitive())
            {
                builder.addField(
                        FieldSpec.builder(int.class, size(name), PRIVATE, STATIC, FINAL)
                                 .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, SIZEOF_BY_NAME.get(type))
                                 .build());
            }
            return this;
        }

        private static Map<TypeName, String> initSizeofByName()
        {
            Map<TypeName, String> sizeofByName = new HashMap<>();
            sizeofByName.put(TypeName.BOOLEAN, "BOOLEAN");
            sizeofByName.put(TypeName.BYTE, "BYTE");
            sizeofByName.put(TypeName.CHAR, "CHAR");
            sizeofByName.put(TypeName.SHORT, "SHORT");
            sizeofByName.put(TypeName.INT, "INT");
            sizeofByName.put(TypeName.FLOAT, "FLOAT");
            sizeofByName.put(TypeName.LONG, "LONG");
            sizeofByName.put(TypeName.DOUBLE, "DOUBLE");
            return sizeofByName;
        }
    }

    private static final class MemberOffsetConstantGenerator extends ClassSpecMixinGenerator
    {
        private MemberOffsetConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder.addField(
                FieldSpec.builder(int.class, offset("kind"), PRIVATE, STATIC, FINAL)
                         .initializer("0")
                         .build()));
        }

        public MemberOffsetConstantGenerator addMember(
            String name,
            TypeName type)
        {
            if (type != null)
            {
                builder.addField(
                    FieldSpec.builder(int.class, offset(name), PRIVATE, STATIC, FINAL)
                        .initializer(String.format("%s + %s", offset("kind"), size("kind")))
                        .build());
            }
            return this;
        }
    }

    private static final class MemberFieldValueConstantGenerator extends ClassSpecMixinGenerator
    {
        private MemberFieldValueConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MemberFieldValueConstantGenerator addMember(
            String name,
            TypeName type)
        {
            if (type == null)
            {
                builder.addField(
                    FieldSpec.builder(int.class, value(name), PRIVATE, STATIC, FINAL)
                        .initializer(name)
                        .build());
            }
            return this;
        }
    }

    private final class TryWrapMethodGenerator extends MethodSpecGenerator
    {
        private TryWrapMethodGenerator()
        {
            super(methodBuilder("tryWrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName)
                .addStatement("super.wrap(buffer, offset, maxLimit)")
                .beginControlFlow("switch (kind())"));
        }

        public TryWrapMethodGenerator addMember(
            String name,
            TypeName type)
        {
            builder.beginControlFlow("case $L:", kind(name));
            if (type instanceof ClassName && "StringFW".equals(((ClassName) type).simpleName())
                || type instanceof ClassName && "String16FW".equals(((ClassName) type).simpleName())
                || type instanceof ClassName && "String32FW".equals(((ClassName) type).simpleName()))
            {
                builder.beginControlFlow("if (null == $LRO.tryWrap(buffer, offset + $L, maxLimit))", name,
                    offset(name))
                       .addStatement("return null")
                       .endControlFlow();
            }
            builder.addStatement("break")
                   .endControlFlow();
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            return builder.beginControlFlow("default:")
                          .addStatement("break")
                          .endControlFlow()
                          .endControlFlow()
                          .beginControlFlow("if (limit() > maxLimit)")
                          .addStatement("return null")
                          .endControlFlow()
                          .addStatement("return this")
                          .build();
        }
    }

    private final class WrapMethodGenerator extends MethodSpecGenerator
    {
        private WrapMethodGenerator()
        {
            super(methodBuilder("wrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName)
                .addStatement("super.wrap(buffer, offset, maxLimit)")
                .beginControlFlow("switch (kind())"));
        }

        public WrapMethodGenerator addMember(
            String name,
            TypeName type)
        {
            builder.beginControlFlow("case $L:", kind(name));
            if (type instanceof ClassName && "StringFW".equals(((ClassName) type).simpleName())
                || type instanceof ClassName && "String16FW".equals(((ClassName) type).simpleName())
                || type instanceof ClassName && "String32FW".equals(((ClassName) type).simpleName()))
            {
                builder.addStatement("$LRO.wrap(buffer, offset + $L, maxLimit)", name, offset(name));
            }
            builder.addStatement("break")
                   .endControlFlow();
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            return builder.beginControlFlow("default:")
                          .addStatement("break")
                          .endControlFlow()
                          .endControlFlow()
                          .addStatement("checkLimit(limit(), maxLimit)")
                          .addStatement("return this")
                          .build();
        }
    }

    private final class ToStringMethodGenerator extends MethodSpecGenerator
    {
        private ToStringMethodGenerator()
        {
            super(methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(String.class)
                .beginControlFlow("switch (kind())"));
        }

        public ToStringMethodGenerator addMember(
            String name,
            TypeName type)
        {
            builder.beginControlFlow("case $L:", kind(name));
            if (type instanceof ClassName && "StringFW".equals(((ClassName) type).simpleName())
                || type instanceof ClassName && "String16FW".equals(((ClassName) type).simpleName())
                || type instanceof ClassName && "String32FW".equals(((ClassName) type).simpleName()))
            {
                builder.addStatement("return String.format(\"$L [$L=%s]\", $LRO.asString())", baseName.toUpperCase(), name, name);
            }
            else if (type == null || type.isPrimitive())
            {
                builder.addStatement("return String.format(\"$L [$L=%d]\", $L())", baseName.toUpperCase(),
                    NUMBER_BY_WORD.get(name) == null ? name : NUMBER_BY_WORD.get(name), getAs(name));
            }
            else
            {
                builder.addStatement("return String.format(\"$L [$L=%s]\", $L())", baseName.toUpperCase(), name, getAs(name));
            }
            builder.endControlFlow();
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            builder.beginControlFlow("default:")
                   .addStatement("return String.format(\"$L [unknown]\")", baseName.toUpperCase())
                   .endControlFlow();

            return builder.endControlFlow()
                          .build();
        }
    }

    private static final class KindAccessorGenerator extends ClassSpecMixinGenerator
    {
        private KindAccessorGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        @Override
        public TypeSpec.Builder build()
        {
            builder.addMethod(methodBuilder("kind")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return buffer().getByte(offset() + $L) & 0xFF", offset("kind"))
                .build());

            return super.build();
        }
    }

    private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
    {
        private static final Map<TypeName, String> GETTER_NAMES;

        static
        {
            Map<TypeName, String> getterNames = new HashMap<>();
            getterNames.put(TypeName.BYTE, "getByte");
            getterNames.put(TypeName.CHAR, "getChar");
            getterNames.put(TypeName.SHORT, "getShort");
            getterNames.put(TypeName.FLOAT, "getFloat");
            getterNames.put(TypeName.INT, "getInt");
            getterNames.put(TypeName.DOUBLE, "getDouble");
            getterNames.put(TypeName.LONG, "getLong");
            GETTER_NAMES = unmodifiableMap(getterNames);
        }

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
            CodeBlock.Builder codeBlock = CodeBlock.builder();
            if (type != null && type.isPrimitive())
            {
                String getterName = GETTER_NAMES.get(type);
                if (getterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }

                codeBlock.addStatement("return buffer().$L(offset() + $L)", getterName, offset(name));
            }
            else
            {
                if (type instanceof ClassName && "StringFW".equals(((ClassName) type).simpleName())
                    || type instanceof ClassName && "String16FW".equals(((ClassName) type).simpleName())
                    || type instanceof ClassName && "String32FW".equals(((ClassName) type).simpleName()))
                {
                    codeBlock.addStatement("return $LRO", name);
                }
                else
                {
                    codeBlock.addStatement("return $L", value(name));
                }
            }

            builder.addMethod(methodBuilder(getAs(name))
                .addModifiers(PUBLIC)
                .returns(type != null ? type : TypeName.BYTE)
                .addCode(codeBlock.build())
                .build());

            return this;
        }
    }

    private final class AllMemberAccessorGenerator extends MethodSpecGenerator
    {
        private AllMemberAccessorGenerator(
            TypeName name)
        {
            super(methodBuilder("get")
                .addModifiers(PUBLIC)
                .returns(name.isPrimitive() ? name : ClassName.bestGuess("String"))
                .beginControlFlow("switch (kind())"));
        }

        public AllMemberAccessorGenerator addMember(
            String name,
            TypeName type)
        {
            builder.beginControlFlow("case $L:", kind(name))
                   .addStatement(type == null || type.isPrimitive() ? "return $L()" : "return $L().asString()", getAs(name))
                   .endControlFlow();

            return this;
        }

        @Override
        public MethodSpec generate()
        {
            return builder.beginControlFlow("default:")
                .addStatement("throw new IllegalStateException(\"Unrecognized kind: \" + kind())")
                .endControlFlow()
                .endControlFlow()
                .build();
        }
    }

    private static final class MemberFieldMaxValueConstantGenerator extends ClassSpecMixinGenerator
    {
        private static final Map<String, Long> MAX_VALUES;

        static
        {
            Map<String, Long> maxValues = new HashMap<>();
            maxValues.put("uint8", 255L);
            maxValues.put("uint16", 65535L);
            maxValues.put("uint32", 4294967295L);
            maxValues.put("uint64", 9223372036854775807L);
            maxValues.put("int8", 127L);
            maxValues.put("int16", 32767L);
            maxValues.put("int32", 2147483647L);
            maxValues.put("int64", 9223372036854775807L);
            maxValues.put("string", (long) Byte.MAX_VALUE);
            maxValues.put("string16", (long) Short.MAX_VALUE);
            maxValues.put("string32", (long) Integer.MAX_VALUE);
            MAX_VALUES = unmodifiableMap(maxValues);
        }

        private MemberFieldMaxValueConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MemberFieldMaxValueConstantGenerator addMember(
            String name,
            TypeName type)
        {
            if (type != null)
            {
                builder.addField(FieldSpec.builder(long.class, max(name), PRIVATE, STATIC, FINAL)
                    .initializer("$LL", MAX_VALUES.get(name))
                    .build());
            }
            return this;
        }
    }

    private final class LimitMethodGenerator extends MethodSpecGenerator
    {
        private LimitMethodGenerator()
        {
            super(methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .beginControlFlow("switch (kind())"));
        }

        public LimitMethodGenerator addMember(
            String name,
            TypeName type)
        {
            builder.beginControlFlow("case $L:", kind(name));

            if (type == null)
            {
                builder.addStatement("return offset()");
            }
            else if (DIRECT_BUFFER_TYPE.equals(type) || type.isPrimitive())
            {
                builder.addStatement("return offset() + $L + $L", offset(name), size(name));
            }
            else
            {
                builder.addStatement("return $L().limit()", getAs(name));
            }

            builder.endControlFlow();

            return this;
        }

        @Override
        public MethodSpec generate()
        {
            return builder.beginControlFlow("default:")
                .addStatement("return offset()")
                .endControlFlow()
                .endControlFlow()
                .build();
        }
    }

    private static final class BuilderClassGenerator extends ClassSpecGenerator
    {
        private final TypeSpec.Builder builder;
        private final ClassName structType;
        private final MemberMutatorGenerator memberMutator;
        private final MemberFieldGenerator memberField;
        private final WrapMethodGenerator wrapMethod;
        private AllMemberMutatorGenerator allMemberMutator;

        private BuilderClassGenerator(
            ClassName structType,
            ClassName flyweightType)
        {
            this(structType.nestedClass("Builder"), flyweightType.nestedClass("Builder"), structType);
        }

        private void setExplicitType(
            TypeName type)
        {
            allMemberMutator = new AllMemberMutatorGenerator(type);
        }

        private BuilderClassGenerator(
            ClassName thisType,
            ClassName builderRawType,
            ClassName structType)
        {
            super(thisType);
            this.builder = classBuilder(thisType.simpleName())
                .addModifiers(PUBLIC, STATIC, FINAL)
                .superclass(ParameterizedTypeName.get(builderRawType, structType));
            this.structType = structType;
            this.wrapMethod = new WrapMethodGenerator();
            this.memberMutator = new MemberMutatorGenerator(thisType, builder);
            this.memberField = new MemberFieldGenerator(thisType, builder);
        }

        private void addMember(
            String name,
            TypeName type)
        {
            memberMutator.addMember(name, type);
            memberField.addMember(name, type);
            if (allMemberMutator != null)
            {
                allMemberMutator.addMember(name, type);
            }
        }

        @Override
        public TypeSpec generate()
        {
            memberMutator.build();
            memberField.build();
            if (allMemberMutator != null)
            {
                builder.addMethod(allMemberMutator.generate());
            }
            return builder.addMethod(constructor())
                          .addMethod(wrapMethod.generate())
                          .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addStatement("super(new $T())", structType)
                .build();
        }

        private final class AllMemberMutatorGenerator extends MethodSpecGenerator
        {
            private CodeBlock.Builder[] members;
            private AllMemberMutatorGenerator(
                TypeName type)
            {
                super(methodBuilder("set")
                    .addModifiers(PUBLIC)
                    .addParameter(type.isPrimitive() ? type : ClassName.bestGuess("String"), "value")
                    .returns(thisName));
                if (!type.isPrimitive())
                {
                    builder.addStatement("int length = value.length()");
                }
                members = new CodeBlock.Builder[5];
            }

            public AllMemberMutatorGenerator addMember(
                String name,
                TypeName type)
            {
                if (Character.isDigit(name.charAt(0)))
                {
                    builder.beginControlFlow("if (value == $L)", value(name))
                           .addStatement("$N()", setAs(name))
                           .addStatement("return this")
                           .endControlFlow();
                }
                else
                {
                    CodeBlock.Builder member = type.isPrimitive() ?
                        CodeBlock.builder()
                                 .beginControlFlow("if (value <= $L)", max(name))
                                 .addStatement("$N(($L) value)", setAs(name), type.toString())
                                 .addStatement("return this")
                                 .endControlFlow() :
                        CodeBlock.builder()
                                 .beginControlFlow("if (length <= $L)", max(name))
                        .addStatement("$N(value)", setAs(name))
                        .addStatement("return this")
                        .endControlFlow();
                    String typeSize = name.replaceAll("\\D+", "");
                    int memberSize = !typeSize.isEmpty() ? Integer.parseInt(typeSize) : -1;

                    switch (memberSize)
                    {
                    case -1:
                        members[0] = member;
                        break;
                    case 8:
                        members[1] = member;
                        break;
                    case 16:
                        members[2] = member;
                        break;
                    case 32:
                        members[3] = member;
                        break;
                    case 64:
                        members[4] = member;
                        break;
                    }
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                for (CodeBlock.Builder member : members)
                {
                    if (member != null)
                    {
                        builder.addCode(member.build());
                    }
                }
                return builder.addStatement("return this")
                              .build();
            }
        }

        private static final class MemberMutatorGenerator extends ClassSpecMixinGenerator
        {
            private static final Map<TypeName, String> PUTTER_NAMES;

            static
            {
                Map<TypeName, String> putterNames = new HashMap<>();
                putterNames.put(TypeName.BYTE, "putByte");
                putterNames.put(TypeName.CHAR, "putChar");
                putterNames.put(TypeName.SHORT, "putShort");
                putterNames.put(TypeName.FLOAT, "putFloat");
                putterNames.put(TypeName.INT, "putInt");
                putterNames.put(TypeName.DOUBLE, "putDouble");
                putterNames.put(TypeName.LONG, "putLong");
                PUTTER_NAMES = unmodifiableMap(putterNames);
            }

            private MemberMutatorGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder.addMethod(
                    MethodSpec.methodBuilder("kind")
                        .addModifiers(PRIVATE)
                        .addParameter(int.class, "value")
                        .returns(thisType)
                        .addStatement("buffer().putByte(offset() + $L, (byte)(value & 0xFF))", offset("kind"))
                        .addStatement("return this")
                        .build()));
            }

            public MemberMutatorGenerator addMember(
                String name,
                TypeName type)
            {
                if (type != null)
                {
                    CodeBlock.Builder code = type.isPrimitive() ? addPrimitiveMember(name, type) : addNonPrimitiveMember(name,
                        type);

                    builder.addMethod(methodBuilder(setAs(name))
                           .addModifiers(PUBLIC)
                           .addParameter(type.isPrimitive() ? type : ClassName.bestGuess("String"), "value")
                           .returns(thisType)
                           .addCode(code.build())
                           .addStatement("return this")
                           .build());
                }
                else
                {
                    CodeBlock.Builder code = addConstantValueMember(name);
                    builder.addMethod(methodBuilder(setAs(name))
                           .addModifiers(PUBLIC)
                           .returns(thisType)
                           .addCode(code.build())
                           .addStatement("return this")
                           .build());
                }
                return this;
            }

            public CodeBlock.Builder addPrimitiveMember(
                String name,
                TypeName type)
            {
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("int newLimit = offset() + $L + $L", offset(name), size(name));
                code.addStatement("checkLimit(newLimit, maxLimit())");
                code.addStatement("kind($L)", kind(name));
                String putterName = PUTTER_NAMES.get(type);
                if (putterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }

                String putStatement = String.format("buffer().%s(offset() + $L, value)", putterName);
                code.addStatement(putStatement, offset(name));
                code.addStatement("limit(newLimit)");
                return code;
            }

            public CodeBlock.Builder addNonPrimitiveMember(
                String name,
                TypeName type)
            {
                CodeBlock.Builder code = CodeBlock.builder();
                code.beginControlFlow("if (value == null)");
                code.addStatement("limit(offset() + $L)", offset(name));
                code.endControlFlow();
                code.beginControlFlow("else");
                code.addStatement("int newLimit = maxLimit()");
                code.addStatement("checkLimit(newLimit, maxLimit())");
                code.addStatement("kind($L)", kind(name));
                code.addStatement("$T.Builder $L = $LRW.wrap(buffer(), offset() + $L, newLimit);",
                    type, name, name, offset(name));
                code.addStatement("$L.set(value, $T.UTF_8)", name, StandardCharsets.class);
                code.addStatement("limit($L.build().limit())", name);
                code.endControlFlow();
                return code;
            }

            public CodeBlock.Builder addConstantValueMember(
                String name)
            {
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("int newLimit = offset() + $L", size("kind"));
                code.addStatement("checkLimit(newLimit, maxLimit())");
                code.addStatement("kind($L)", kind(name));
                code.addStatement("limit(newLimit)");
                return code;
            }

            @Override
            public TypeSpec.Builder build()
            {
                return super.build();
            }
        }

        private final class WrapMethodGenerator extends MethodSpecGenerator
        {
            private WrapMethodGenerator()
            {
                super(methodBuilder("wrap")
                    .addModifiers(PUBLIC)
                    .returns(thisName)
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .addStatement("super.wrap(buffer, offset, maxLimit)")
                    .addStatement("return this"));
            }

            @Override
            public MethodSpec generate()
            {
                return builder.build();
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
                TypeName type)
            {
                if (type != null && !type.isPrimitive())
                {
                    String fieldRW = String.format("%sRW", name);
                    ClassName classType = (ClassName) type;
                    TypeName builderType = classType.nestedClass("Builder");
                    builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                                              .initializer("new $T()", builderType)
                                              .build());
                }
                return this;
            }
        }
    }

    private static String max(
        String fieldName)
    {
        return String.format("%s_MAX", constant(fieldName));
    }

    private static String value(
        String fieldName)
    {
        String filteredName = NUMBER_BY_WORD.get(fieldName) == null ? fieldName : NUMBER_BY_WORD.get(fieldName);
        return String.format("FIELD_VALUE_%s", constant(filteredName));
    }

    private static String kind(
        String fieldName)
    {
        String filteredName = NUMBER_BY_WORD.get(fieldName) == null ? fieldName : NUMBER_BY_WORD.get(fieldName);
        return String.format("KIND_%s", constant(filteredName));
    }

    private static String offset(
        String fieldName)
    {
        return String.format("FIELD_OFFSET_%s", constant(fieldName));
    }

    private static String size(
        String fieldName)
    {
        String filteredName = NUMBER_BY_WORD.get(fieldName) == null ? fieldName : NUMBER_BY_WORD.get(fieldName);
        return String.format("FIELD_SIZE_%s", constant(filteredName));
    }

    private static String getAs(
        String fieldName)
    {
        String filteredName = NUMBER_BY_WORD.get(fieldName) == null ? fieldName : NUMBER_BY_WORD.get(fieldName);
        return String.format("getAs%s%s", Character.toUpperCase(filteredName.charAt(0)), filteredName.substring(1));
    }

    private static String setAs(
        String fieldName)
    {
        String filteredName = NUMBER_BY_WORD.get(fieldName) == null ? fieldName : NUMBER_BY_WORD.get(fieldName);
        return String.format("setAs%s%s", Character.toUpperCase(filteredName.charAt(0)), filteredName.substring(1));
    }

    private static String constant(
        String fieldName)
    {
        return fieldName.replaceAll("([^_A-Z])([A-Z])", "$1_$2").toUpperCase();
    }
}
