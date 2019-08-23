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
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class VariantFlyweightGenerator extends ClassSpecGenerator
{
    private static final Map<String, String> NUMBER_WORDS;
    private static final Map<TypeName, String> TYPE_NAMES;
    private static final Map<String, Long> BIT_MASK_LONG;
    private static final Map<String, Integer> BIT_MASK_INT;
    private static final Map<TypeName, String> CLASS_NAMES;
    private final String baseName;
    private final TypeSpec.Builder builder;
    private final MemberFieldGenerator memberField;
    private final KindConstantGenerator memberKindConstant;
    private final MemberSizeConstantGenerator memberSizeConstant;
    private final MemberOffsetConstantGenerator memberOffsetConstant;
    private final MemberFieldValueConstantGenerator memberFieldValueConstant;
    private final TryWrapMethodGenerator tryWrapMethod;
    private final WrapMethodGenerator wrapMethod;
    private final ToStringMethodGenerator toStringMethod;
    private final KindAccessorGenerator kindAccessor;
    private final MemberAccessorGenerator memberAccessor;
    private final LimitMethodGenerator limitMethod;
    private final BuilderClassGenerator builderClass;
    private final GetMethodGenerator getMethod;
    private final BitMaskConstantGenerator bitMaskConstant;

    static
    {
        Map<String, String> numberByWord = new HashMap<>();
        numberByWord.put("0", "zero");
        numberByWord.put("1", "one");
        NUMBER_WORDS = unmodifiableMap(numberByWord);

        Map<TypeName, String> typeNames = new HashMap<>();
        typeNames.put(TypeName.BYTE, "Byte");
        typeNames.put(TypeName.CHAR, "Char");
        typeNames.put(TypeName.SHORT, "Short");
        typeNames.put(TypeName.INT, "Int");
        typeNames.put(TypeName.FLOAT, "Float");
        typeNames.put(TypeName.LONG, "Long");
        typeNames.put(TypeName.DOUBLE, "Double");
        TYPE_NAMES = unmodifiableMap(typeNames);

        Map<String, Long> longBitMaskValues = new HashMap<>();
        longBitMaskValues.put("int8", 0xffffffffffffff00L);
        longBitMaskValues.put("int16", 0xffffffffffff0000L);
        longBitMaskValues.put("int32", 0xffffffff00000000L);
        BIT_MASK_LONG = unmodifiableMap(longBitMaskValues);

        Map<String, Integer> intBitMaskValues = new HashMap<>();
        intBitMaskValues.put("int8", 0xffffff00);
        intBitMaskValues.put("int16", 0xffff0000);
        BIT_MASK_INT = unmodifiableMap(intBitMaskValues);

        Map<TypeName, String> classNames = new HashMap<>();
        classNames.put(TypeName.BYTE, "Byte");
        classNames.put(TypeName.SHORT, "Short");
        classNames.put(TypeName.INT, "Integer");
        classNames.put(TypeName.FLOAT, "Float");
        classNames.put(TypeName.LONG, "Long");
        classNames.put(TypeName.DOUBLE, "Double");
        CLASS_NAMES = unmodifiableMap(classNames);
    }

    public VariantFlyweightGenerator(
        ClassName variantName,
        ClassName flyweightName,
        String baseName,
        TypeName kindTypeName,
        TypeName ofTypeName,
        TypeName unsignedOfTypeName)
    {
        super(variantName);

        this.baseName = baseName;
        this.builder = classBuilder(variantName).superclass(flyweightName).addModifiers(PUBLIC, FINAL);
        this.memberField = new MemberFieldGenerator(variantName, kindTypeName, builder);
        this.memberKindConstant = new KindConstantGenerator(variantName, kindTypeName, builder);
        this.memberSizeConstant = new MemberSizeConstantGenerator(variantName, kindTypeName, builder);
        this.memberOffsetConstant = new MemberOffsetConstantGenerator(variantName, kindTypeName, builder);
        this.memberFieldValueConstant = new MemberFieldValueConstantGenerator(variantName, builder);
        this.tryWrapMethod = new TryWrapMethodGenerator(kindTypeName);
        this.wrapMethod = new WrapMethodGenerator(kindTypeName);
        this.toStringMethod = new ToStringMethodGenerator(kindTypeName);
        this.kindAccessor = new KindAccessorGenerator(variantName, kindTypeName, builder);
        this.memberAccessor = new MemberAccessorGenerator(variantName, kindTypeName, builder);
        this.limitMethod = new LimitMethodGenerator(kindTypeName);
        this.getMethod = new GetMethodGenerator(kindTypeName, ofTypeName, unsignedOfTypeName);
        this.bitMaskConstant = new BitMaskConstantGenerator(variantName, ofTypeName, builder);
        this.builderClass = new BuilderClassGenerator(variantName, flyweightName, kindTypeName, ofTypeName, unsignedOfTypeName);
    }

    public VariantFlyweightGenerator addMember(
        Object kindValue,
        String memberName,
        TypeName memberTypeName,
        TypeName unsignedMemberTypeName)
    {
        memberKindConstant.addMember(kindValue, memberName);
        memberOffsetConstant.addMember(memberName, memberTypeName);
        memberFieldValueConstant.addMember(memberName, memberTypeName);
        memberField.addMember(memberName, memberTypeName);
        memberSizeConstant.addMember(memberName, memberTypeName, unsignedMemberTypeName);
        wrapMethod.addMember(kindValue, memberName, memberTypeName);
        tryWrapMethod.addMember(kindValue, memberName, memberTypeName);
        toStringMethod.addMember(kindValue, memberName, memberTypeName);
        memberAccessor.addMember(memberName, memberTypeName, unsignedMemberTypeName);
        limitMethod.addMember(kindValue, memberName, memberTypeName);
        getMethod.addMember(memberName, kindValue, memberTypeName);
        bitMaskConstant.addMember(memberName, memberTypeName, unsignedMemberTypeName);
        builderClass.addMember(memberName, memberTypeName, unsignedMemberTypeName);
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
        bitMaskConstant.build();
        getMethod.mixin(builder);
        tryWrapMethod.mixin(builder);
        wrapMethod.mixin(builder);
        toStringMethod.mixin(builder);
        limitMethod.mixin(builder);
        return builder.addType(builderClass.generate())
                      .build();
    }

    private static final class MemberFieldGenerator extends ClassSpecMixinGenerator
    {
        private MemberFieldGenerator(
            ClassName thisType,
            TypeName kindName,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            if (!kindName.isPrimitive())
            {
                String kindTypeVariableName = enumRO(kindName);
                builder.addField(FieldSpec.builder(kindName, kindTypeVariableName, PRIVATE, FINAL)
                                          .initializer("new $T()", kindName)
                                          .build());
            }
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
        private final TypeName kindName;
        private KindConstantGenerator(
            ClassName thisType,
            TypeName kindName,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            this.kindName = kindName;
        }

        public KindConstantGenerator addMember(
            Object kindValue,
            String name)
        {
            FieldSpec field;
            if (kindName.isPrimitive())
            {
                field = FieldSpec.builder(int.class, kind(name), PUBLIC, STATIC, FINAL)
                                 .initializer("$L", kindValue)
                                 .build();
            }
            else
            {
                ClassName enumTypeName = enumClassName(kindName);
                field = FieldSpec.builder(enumTypeName, kind(name), PUBLIC, STATIC, FINAL)
                                 .initializer("$L.$L", enumTypeName.simpleName(), kindValue)
                                 .build();
            }
            builder.addField(field);
            return this;
        }
    }

    private static final class MemberSizeConstantGenerator extends ClassSpecMixinGenerator
    {
        private MemberSizeConstantGenerator(
            ClassName thisType,
            TypeName kindName,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            if (kindName.isPrimitive())
            {
                builder.addField(FieldSpec.builder(int.class, size("kind"), PRIVATE, STATIC, FINAL)
                                          .initializer("$T.SIZE_OF_BYTE", BIT_UTIL_TYPE)
                                          .build());
            }
        }

        public MemberSizeConstantGenerator addMember(
            String name,
            TypeName memberTypeName,
            TypeName unsignedMemberTypeName)
        {
            if (memberTypeName != null && memberTypeName.isPrimitive())
            {
                builder.addField(
                    FieldSpec.builder(int.class, size(name), PRIVATE, STATIC, FINAL)
                             .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE,
                                 TYPE_NAMES.get(unsignedMemberTypeName == null ? memberTypeName : unsignedMemberTypeName)
                                     .toUpperCase())
                             .build());
            }
            return this;
        }
    }

    private static final class MemberOffsetConstantGenerator extends ClassSpecMixinGenerator
    {
        private final TypeName kindName;
        private MemberOffsetConstantGenerator(
            ClassName thisType,
            TypeName kindName,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            this.kindName = kindName;
            if (kindName.isPrimitive())
            {
                builder.addField(FieldSpec.builder(int.class, offset("kind"), PRIVATE, STATIC, FINAL)
                       .initializer("0")
                       .build());
            }
        }

        public MemberOffsetConstantGenerator addMember(
            String memberName,
            TypeName memberTypeName)
        {
            if (kindName.isPrimitive() && memberTypeName != null)
            {
                builder.addField(
                    FieldSpec.builder(int.class, offset(memberName), PRIVATE, STATIC, FINAL)
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
            String memberName,
            TypeName memberTypeName)
        {
            if (memberTypeName == null)
            {
                builder.addField(
                    FieldSpec.builder(int.class, value(memberName), PRIVATE, STATIC, FINAL)
                             .initializer(memberName)
                             .build());
            }
            return this;
        }
    }

    private final class TryWrapMethodGenerator extends MethodSpecGenerator
    {
        private TypeName kindTypeName;
        private TryWrapMethodGenerator(TypeName kindTypeName)
        {
            super(methodBuilder("tryWrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName)
                .addStatement("super.wrap(buffer, offset, maxLimit)"));
            this.kindTypeName = kindTypeName;
            if (!kindTypeName.isPrimitive())
            {
                builder.addStatement("$L.tryWrap(buffer, offset, maxLimit)", enumRO(kindTypeName));
            }
            builder.beginControlFlow("switch (kind())");
        }

        public TryWrapMethodGenerator addMember(
            Object kindValue,
            String memberName,
            TypeName memberTypeName)
        {
            builder.beginControlFlow("case $L:", kindTypeName.isPrimitive() ? kind(memberName) : kindValue);
            if (memberTypeName instanceof ClassName && "StringFW".equals(((ClassName) memberTypeName).simpleName())
                || memberTypeName instanceof ClassName && "String16FW".equals(((ClassName) memberTypeName).simpleName())
                || memberTypeName instanceof ClassName && "String32FW".equals(((ClassName) memberTypeName).simpleName()))
            {
                builder.beginControlFlow("if (null == $LRO.tryWrap(buffer, offset + $L, maxLimit))", memberName,
                    kindTypeName.isPrimitive() ? offset(memberName) : String.format("%s.sizeof()", enumRO(kindTypeName)))
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
        private TypeName kindTypeName;
        private WrapMethodGenerator(TypeName kindTypeName)
        {
            super(methodBuilder("wrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName)
                .addStatement("super.wrap(buffer, offset, maxLimit)"));
            this.kindTypeName = kindTypeName;
            if (!kindTypeName.isPrimitive())
            {
                builder.addStatement("$L.wrap(buffer, offset, maxLimit)", enumRO(kindTypeName));
            }
            builder.beginControlFlow("switch (kind())");
        }

        public WrapMethodGenerator addMember(
            Object kindValue,
            String memberName,
            TypeName memberTypeName)
        {
            builder.beginControlFlow("case $L:", kindTypeName.isPrimitive() ? kind(memberName) : kindValue);
            if (memberTypeName instanceof ClassName && "StringFW".equals(((ClassName) memberTypeName).simpleName())
                || memberTypeName instanceof ClassName && "String16FW".equals(((ClassName) memberTypeName).simpleName())
                || memberTypeName instanceof ClassName && "String32FW".equals(((ClassName) memberTypeName).simpleName()))
            {
                builder.addStatement("$LRO.wrap(buffer, offset + $L, maxLimit)", memberName,
                    kindTypeName.isPrimitive() ? offset(memberName) : String.format("%s.sizeof()", enumRO(kindTypeName)));
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
        private TypeName kindTypeName;
        private ToStringMethodGenerator(TypeName kindTypeName)
        {
            super(methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(String.class)
                .beginControlFlow("switch (kind())"));
            this.kindTypeName = kindTypeName;
        }

        public ToStringMethodGenerator addMember(
            Object kindValue,
            String memberName,
            TypeName memberTypeName)
        {
            builder.beginControlFlow("case $L:", kindTypeName.isPrimitive() ? kind(memberName) : kindValue);
            if (memberTypeName instanceof ClassName && "StringFW".equals(((ClassName) memberTypeName).simpleName())
                || memberTypeName instanceof ClassName && "String16FW".equals(((ClassName) memberTypeName).simpleName())
                || memberTypeName instanceof ClassName && "String32FW".equals(((ClassName) memberTypeName).simpleName()))
            {
                builder.addStatement("return String.format(\"$L [$L=%s]\", $LRO.asString())", baseName.toUpperCase(), memberName,
                    memberName);
            }
            else if (memberTypeName == null || memberTypeName.isPrimitive())
            {
                builder.addStatement("return String.format(\"$L [$L=%d]\", $L())", baseName.toUpperCase(),
                    NUMBER_WORDS.get(memberName) == null ? memberName : NUMBER_WORDS.get(memberName), getAs(memberName));
            }
            else
            {
                builder.addStatement("return String.format(\"$L [$L=%s]\", $L())", baseName.toUpperCase(), memberName,
                    getAs(memberName));
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
        private final TypeName kindTypeName;
        private KindAccessorGenerator(
            ClassName thisType,
            TypeName kindTypeName,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            this.kindTypeName = kindTypeName;
        }

        @Override
        public TypeSpec.Builder build()
        {
            if (kindTypeName.isPrimitive())
            {
                builder.addMethod(
                    methodBuilder("kind")
                       .addModifiers(PUBLIC)
                       .returns(int.class)
                       .addStatement("return buffer().getByte(offset() + $L) & 0xFF", offset("kind"))
                       .build());
            }
            else
            {
                String enumFWName = ((ClassName) kindTypeName).simpleName();
                ClassName enumName = ClassName.bestGuess(enumFWName.substring(0, enumFWName.length() - 2));

                builder.addMethod(
                    methodBuilder("kind")
                       .addModifiers(PUBLIC)
                       .returns(enumName)
                       .addStatement("return $L.get()", enumRO(kindTypeName))
                       .build());
            }


            return super.build();
        }
    }

    private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
    {
        private final TypeName kindTypeName;

        private MemberAccessorGenerator(
            ClassName thisType,
            TypeName kindTypeName,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            this.kindTypeName = kindTypeName;
        }

        public MemberAccessorGenerator addMember(
            String name,
            TypeName memberTypeName,
            TypeName unsignedMemberTypeName)
        {
            CodeBlock.Builder codeBlock = CodeBlock.builder();
            if (memberTypeName != null && memberTypeName.isPrimitive())
            {
                String getterName = String.format("get%s", TYPE_NAMES.get(unsignedMemberTypeName == null ? memberTypeName :
                    unsignedMemberTypeName));
                if (getterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + memberTypeName);
                }

                if (kindTypeName.isPrimitive())
                {
                    codeBlock.addStatement("return buffer().$L(offset() + $L)", getterName, offset(name));
                }
                else
                {
                    codeBlock.addStatement("return buffer().$L($L.limit())", getterName, enumRO(kindTypeName));
                }
            }
            else
            {
                if (memberTypeName instanceof ClassName && "StringFW".equals(((ClassName) memberTypeName).simpleName())
                    || memberTypeName instanceof ClassName && "String16FW".equals(((ClassName) memberTypeName).simpleName())
                    || memberTypeName instanceof ClassName && "String32FW".equals(((ClassName) memberTypeName).simpleName()))
                {
                    codeBlock.addStatement("return $LRO", name);
                }
                else
                {
                    codeBlock.addStatement("return $L", value(name));
                }
            }

            TypeName returnType = TypeName.BYTE;
            if (memberTypeName != null)
            {
                returnType = Objects.requireNonNullElse(unsignedMemberTypeName, memberTypeName);
            }

            builder.addMethod(methodBuilder(getAs(name))
                   .addModifiers(PUBLIC)
                   .returns(returnType)
                   .addCode(codeBlock.build())
                   .build());
            return this;
        }
    }

    private final class GetMethodGenerator extends MethodSpecGenerator
    {
        private final TypeName kindTypeName;
        private final TypeName ofTypeName;
        private final TypeName unsignedOfTypeName;

        private GetMethodGenerator(
            TypeName kindTypeName,
            TypeName ofTypeName,
            TypeName unsignedOfTypeName)
        {
            super(methodBuilder("get")
                .addModifiers(PUBLIC));
            this.kindTypeName = kindTypeName;
            this.ofTypeName = ofTypeName;
            this.unsignedOfTypeName = unsignedOfTypeName;
            builder.beginControlFlow("switch (kind())");
        }

        public GetMethodGenerator addMember(
            String name,
            Object kindValue,
            TypeName memberTypeName)
        {
            Object kind = kindTypeName.isPrimitive() ? kind(name) : kindValue;
            builder.beginControlFlow("case $L:", kind)
                   .addStatement(memberTypeName == null || memberTypeName.isPrimitive() ?
                       "return $L()" : "return $L().asString()", getAs(name))
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
                          .returns(Objects.requireNonNullElseGet(unsignedOfTypeName,
                              () -> ofTypeName.isPrimitive() ? ofTypeName : ClassName.bestGuess("String")))
                          .build();
        }

        @Override
        public void mixin(
            TypeSpec.Builder builder)
        {
            if (ofTypeName != null)
            {
                super.mixin(builder);
            }
        }
    }

    private static final class BitMaskConstantGenerator extends ClassSpecMixinGenerator
    {
        private final TypeName ofTypeName;

        private BitMaskConstantGenerator(
            ClassName thisType,
            TypeName ofTypeName,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            this.ofTypeName = ofTypeName;
        }

        public BitMaskConstantGenerator addMember(
            String kindTypeName,
            TypeName memberTypeName,
            TypeName unsignedMemberTypeName)
        {
            if (ofTypeName != null && unsignedMemberTypeName == null && memberTypeName != null)
            {
                FieldSpec.Builder bitMaskField = FieldSpec.builder(ofTypeName, bitMask(kindTypeName), PRIVATE,
                    STATIC, FINAL);
                if (ofTypeName.equals(TypeName.INT))
                {
                    if (BIT_MASK_INT.get(kindTypeName) != null)
                    {
                        bitMaskField.initializer("$L", BIT_MASK_INT.get(kindTypeName));
                        builder.addField(bitMaskField.build());
                    }
                }
                else
                {
                    if (BIT_MASK_LONG.get(kindTypeName) != null)
                    {
                        bitMaskField.initializer("$LL", BIT_MASK_LONG.get(kindTypeName));
                        builder.addField(bitMaskField.build());
                    }
                }
            }
            return this;
        }
    }

    private final class LimitMethodGenerator extends MethodSpecGenerator
    {
        private TypeName kindTypeName;
        private LimitMethodGenerator(TypeName kindTypeName)
        {
            super(methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .beginControlFlow("switch (kind())"));
            this.kindTypeName = kindTypeName;
        }

        public LimitMethodGenerator addMember(
            Object kindValue,
            String memberName,
            TypeName memberTypeName)
        {
            builder.beginControlFlow("case $L:", kindTypeName.isPrimitive() ? kind(memberName) : kindValue);

            if (memberTypeName == null)
            {
                builder.addStatement("return offset()");
            }
            else if (DIRECT_BUFFER_TYPE.equals(memberTypeName) || memberTypeName.isPrimitive())
            {
                if (kindTypeName.isPrimitive())
                {
                    builder.addStatement("return offset() + $L + $L", offset(memberName), size(memberName));
                }
                else
                {
                    builder.addStatement("return $L.limit() + $L", enumRO(kindTypeName), size(memberName));
                }
            }
            else
            {
                builder.addStatement("return $L().limit()", getAs(memberName));
            }
            builder.endControlFlow();
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            builder.beginControlFlow("default:");
            if (kindTypeName.isPrimitive())
            {
                builder.addStatement("return offset()");
            }
            else
            {
                builder.addStatement("return $L.limit()", enumRO(kindTypeName));
            }
            return builder.endControlFlow()
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
        private final SetMethodGenerator setMethod;

        private BuilderClassGenerator(
            ClassName structType,
            ClassName flyweightType,
            TypeName kindTypeName,
            TypeName ofTypeName,
            TypeName unsignedOfTypeName)
        {
            this(structType.nestedClass("Builder"), flyweightType.nestedClass("Builder"), structType, kindTypeName, ofTypeName,
                unsignedOfTypeName);
        }

        private BuilderClassGenerator(
            ClassName thisType,
            ClassName builderRawType,
            ClassName structType,
            TypeName kindTypeName,
            TypeName ofTypeName,
            TypeName unsignedOfTypeName)
        {
            super(thisType);
            this.builder = classBuilder(thisType.simpleName())
                .addModifiers(PUBLIC, STATIC, FINAL)
                .superclass(ParameterizedTypeName.get(builderRawType, structType));
            this.structType = structType;
            this.wrapMethod = new WrapMethodGenerator();
            this.memberMutator = new MemberMutatorGenerator(thisType, kindTypeName, builder);
            this.memberField = new MemberFieldGenerator(thisType, kindTypeName, builder);
            this.setMethod = new SetMethodGenerator(ofTypeName, unsignedOfTypeName);
        }

        private void addMember(
            String memberName,
            TypeName memberTypeName,
            TypeName unsignedMemberTypeName)
        {
            memberMutator.addMember(memberName, memberTypeName, unsignedMemberTypeName);
            memberField.addMember(memberName, memberTypeName);
            setMethod.addMember(memberName, memberTypeName, unsignedMemberTypeName);
        }

        @Override
        public TypeSpec generate()
        {
            memberMutator.build();
            memberField.build();
            setMethod.mixin(builder);
            wrapMethod.mixin(builder);
            return builder.addMethod(constructor())
                          .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addStatement("super(new $T())", structType)
                .build();
        }

        private final class SetMethodGenerator extends MethodSpecGenerator
        {
            private final Set<TypeWidth> kindTypeSet = new TreeSet<>();
            private final TypeName ofType;
            private final TypeName unsignedOfType;

            private SetMethodGenerator(
                TypeName ofType,
                TypeName unsignedOfType)
            {
                super(methodBuilder("set")
                    .addModifiers(PUBLIC)
                    .returns(thisName));

                this.ofType = ofType;
                this.unsignedOfType = unsignedOfType;
            }

            public SetMethodGenerator addMember(
                String kindTypeName,
                TypeName kindType,
                TypeName unsignedKindType)
            {
                if (Character.isDigit(kindTypeName.charAt(0)))
                {
                    String constantDigit = kindTypeName.substring(0, 1);
                    TypeWidth currentType = new TypeWidth(kindType, unsignedKindType, kindTypeName,
                        constantDigit.equals("0") ? 0 : 8, Integer.parseInt(constantDigit));
                    kindTypeSet.add(currentType);
                }
                else
                {
                    String typeSize = kindTypeName.replaceAll("\\D+", "");
                    int memberWidth = !typeSize.isEmpty() ? Integer.parseInt(typeSize) : 8;
                    kindTypeSet.add(new TypeWidth(kindType, unsignedKindType, kindTypeName, memberWidth, Integer.MAX_VALUE));
                }
                return this;
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (ofType != null)
                {
                    super.mixin(builder);
                }
            }

            @Override
            public MethodSpec generate()
            {
                boolean hasConstant = false;
                addVariableDefinitions();

                builder.beginControlFlow("switch (highestByteIndex)");
                for (TypeWidth type : kindTypeSet)
                {
                    int width = type.width();
                    switch (width)
                    {
                    case 8:
                        if (type.value() != Integer.MAX_VALUE)
                        {
                            builder.beginControlFlow("case 0:")
                                   .beginControlFlow("switch ((int) value)")
                                   .beginControlFlow("case $L:", type.value())
                                   .addStatement("$L()", setAs(type.kindTypeName()))
                                   .addStatement("break")
                                   .endControlFlow();
                            hasConstant = true;
                        }
                        else
                        {
                            if (hasConstant)
                            {
                                builder.beginControlFlow("default:")
                                       .addStatement("$L(($L) value)", setAs(type.kindTypeName()),
                                           type.unsignedKindType() == null ? type.kindType() : type.unsignedKindType())
                                       .addStatement("break")
                                       .endControlFlow()
                                       .endControlFlow()
                                       .addStatement("break")
                                       .endControlFlow();
                            }
                            else
                            {
                                builder.beginControlFlow("case 0:")
                                       .addStatement(String.format("$L(%svalue)", ofType.isPrimitive() ?
                                           type.unsignedKindType() == null ? "(byte) " : "(int) " : ""),
                                           setAs(type.kindTypeName()))
                                       .addStatement("break")
                                       .endControlFlow();
                            }
                        }
                        break;
                    case 16:
                        builder.beginControlFlow("case 1:")
                               .addStatement(String.format("$L(%svalue)", ofType.isPrimitive() ?
                                   type.unsignedKindType() == null ? "(short) " : "(int) " : ""), setAs(type.kindTypeName()))
                               .addStatement("break")
                               .endControlFlow();
                        break;
                    case 32:
                        builder.beginControlFlow("case 2:")
                               .endControlFlow()
                               .beginControlFlow("case 3:")
                               .addStatement(String.format("$L(%svalue)", ofType.isPrimitive() ?
                                   type.unsignedKindType() == null ? "(int) " : "" : ""), setAs(type.kindTypeName()))
                               .addStatement("break")
                               .endControlFlow();
                        break;
                    case 64:
                        builder.beginControlFlow("case 4:")
                               .endControlFlow()
                               .beginControlFlow("case 5:")
                               .endControlFlow()
                               .beginControlFlow("case 6:")
                               .endControlFlow()
                               .beginControlFlow("case 7:")
                               .addStatement("$L((long) value)", setAs(type.kindTypeName()))
                               .addStatement("break")
                               .endControlFlow();
                        break;
                    }
                }

                if (ofType.isPrimitive())
                {
                    if (unsignedOfType == null)
                    {
                        addSignedNegativeIntBlock();
                    }
                    else
                    {
                        addUnsignedIntZeroCase();
                    }
                }

                builder.beginControlFlow("default:")
                       .addStatement("throw new IllegalArgumentException(\"Illegal value: \" + value)")
                       .endControlFlow()
                       .endControlFlow();
                return builder.addParameter(Objects.requireNonNullElseGet(unsignedOfType, () -> ofType.isPrimitive() ?
                    ofType : ClassName.bestGuess("String")), "value")
                              .addStatement("return this")
                              .build();
            }

            private void addVariableDefinitions()
            {
                if (!ofType.isPrimitive())
                {
                    builder.addStatement("byte[] charBytes = value.getBytes($T.UTF_8)", StandardCharsets.class)
                           .addStatement("int byteLength = charBytes.length")
                           .addStatement("int highestByteIndex = " +
                            "Integer.numberOfTrailingZeros(Integer.highestOneBit(byteLength)) >> 3");
                }
                else
                {
                    if (unsignedOfType == null)
                    {
                        builder.addStatement("int highestByteIndex = ($L.numberOfTrailingZeros($L.highestOneBit(value)) " +
                            "+ 1)  >> 3", CLASS_NAMES.get(ofType), CLASS_NAMES.get(ofType));
                    }
                    else
                    {
                        builder.addStatement("int highestByteIndex = $L.numberOfTrailingZeros($L.highestOneBit(value)) >> 3",
                            CLASS_NAMES.get(unsignedOfType), CLASS_NAMES.get(unsignedOfType));
                    }
                }
            }

            private void addUnsignedIntZeroCase()
            {
                TypeWidth typeZero = kindTypeSet.iterator().next();
                if (typeZero.width() == 0)
                {
                    builder.beginControlFlow(String.format("case %s:", ofType.equals(TypeName.LONG) ? "8" : "4"))
                           .addStatement("$L()", setAs(typeZero.kindTypeName()))
                           .addStatement("break")
                           .endControlFlow();
                }
            }

            private void addSignedNegativeIntBlock()
            {
                builder.beginControlFlow(String.format("case %s:", ofType.equals(TypeName.LONG) ? "8" : "4"));
                Iterator<TypeWidth> iterator = kindTypeSet.iterator();
                int i = 0;
                while (iterator.hasNext())
                {
                    TypeWidth currentType = iterator.next();
                    if (i == 0)
                    {
                        if (currentType.width() == 0)
                        {
                            builder.beginControlFlow("if (value == 0)")
                                .addStatement("$L()", setAs(currentType.kindTypeName));
                        }
                        else
                        {
                            builder.beginControlFlow("if ((value & $L) == value)",
                                bitMask(currentType.kindTypeName()))
                                .addStatement("$L(($L) value)", setAs(currentType.kindTypeName()), currentType.kindType());
                        }

                    }
                    else if (!iterator.hasNext())
                    {
                        builder.beginControlFlow("else")
                            .addStatement("$L(($L) value)", setAs(currentType.kindTypeName()), currentType.kindType());
                    }
                    else
                    {
                        builder.beginControlFlow("else if ((value & $L) == value)",
                            bitMask(currentType.kindTypeName()))
                            .addStatement("$L(($L) value)", setAs(currentType.kindTypeName()), currentType.kindType());
                    }
                    builder.endControlFlow();
                    i++;
                }
                builder.addStatement("break");
                builder.endControlFlow();
            }

            private class TypeWidth implements Comparable<TypeWidth>
            {
                private TypeName kindType;
                private TypeName unsignedKindType;
                private String kindTypeName;
                private int width;
                private int value;

                TypeWidth(
                    TypeName kindType,
                    TypeName unsignedKindType,
                    String kindTypeName,
                    int width,
                    int value)
                {
                    this.kindType = kindType;
                    this.unsignedKindType = unsignedKindType;
                    this.kindTypeName = kindTypeName;
                    this.width = width;
                    this.value = value;
                }

                public TypeName kindType()
                {
                    return kindType;
                }

                public TypeName unsignedKindType()
                {
                    return unsignedKindType;
                }

                public String kindTypeName()
                {
                    return kindTypeName;
                }

                public int width()
                {
                    return width;
                }

                public int value()
                {
                    return value;
                }

                @Override
                public int compareTo(
                    TypeWidth anotherType)
                {
                    return this.width != anotherType.width() ?
                        this.width - anotherType.width() : this.value - anotherType.value();
                }
            }
        }

        private static final class MemberMutatorGenerator extends ClassSpecMixinGenerator
        {
            private final TypeName kindTypeName;

            private MemberMutatorGenerator(
                ClassName thisType,
                TypeName kindTypeName,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
                MethodSpec.Builder kind = MethodSpec.methodBuilder("kind")
                                                    .addModifiers(PRIVATE)
                                                    .returns(thisType);
                this.kindTypeName = kindTypeName;
                if (kindTypeName.isPrimitive())
                {
                    kind.addParameter(int.class, "value")
                        .addStatement("buffer().putByte(offset() + $L, (byte)(value & 0xFF))", offset("kind"));
                }
                else
                {
                    kind.addParameter(enumClassName(kindTypeName), "value")
                        .addStatement("$L.wrap(buffer(), offset(), maxLimit())", enumRW(kindTypeName))
                        .addStatement("$L.set(value)", enumRW(kindTypeName))
                        .addStatement("limit($L.build().limit())", enumRW(kindTypeName));
                }
                kind.addStatement("return this");
                builder.addMethod(kind.build());
            }

            public MemberMutatorGenerator addMember(
                String memberName,
                TypeName memberTypeName,
                TypeName unsignedMemberTypeName)
            {
                if (memberTypeName != null)
                {
                    CodeBlock.Builder code = memberTypeName.isPrimitive() ?
                        addPrimitiveMember(memberName, memberTypeName, unsignedMemberTypeName) :
                        addNonPrimitiveMember(memberName, memberTypeName);

                    builder.addMethod(methodBuilder(setAs(memberName))
                           .addModifiers(PUBLIC)
                           .addParameter(memberTypeName.isPrimitive() ?
                               unsignedMemberTypeName == null ? memberTypeName : unsignedMemberTypeName :
                                   ClassName.bestGuess("String"), "value")
                           .returns(thisType)
                           .addCode(code.build())
                           .addStatement("return this")
                           .build());
                }
                else
                {
                    CodeBlock.Builder code = addConstantValueMember(memberName);
                    builder.addMethod(methodBuilder(setAs(memberName))
                           .addModifiers(PUBLIC)
                           .returns(thisType)
                           .addCode(code.build())
                           .addStatement("return this")
                           .build());
                }
                return this;
            }

            public CodeBlock.Builder addPrimitiveMember(
                String memberName,
                TypeName memberTypeName,
                TypeName unsignedMemberTypeName)
            {
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("kind($L)", kind(memberName));
                if (kindTypeName.isPrimitive())
                {
                    code.addStatement("int newLimit = offset() + $L + $L", offset(memberName), size(memberName));
                }
                else
                {
                    code.addStatement("int newLimit = limit() + $L", size(memberName));
                }
                code.addStatement("checkLimit(newLimit, maxLimit())");
                TypeName type = unsignedMemberTypeName == null ? memberTypeName : unsignedMemberTypeName;
                String putterName = String.format("put%s", TYPE_NAMES.get(type));
                if (putterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }
                String putStatement = String.format(kindTypeName.isPrimitive() ? "buffer().%s(offset() + $L, value)" :
                    "buffer().%s(limit(), value)", putterName);
                if (kindTypeName.isPrimitive())
                {
                    code.addStatement(putStatement, offset(memberName));
                }
                else
                {
                    code.addStatement(putStatement);
                }
                code.addStatement("limit(newLimit)");
                return code;
            }

            public CodeBlock.Builder addNonPrimitiveMember(
                String memberName,
                TypeName memberTypeName)
            {
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("kind($L)", kind(memberName));
                if (kindTypeName.isPrimitive())
                {
                    code.addStatement("$T.Builder $L = $LRW.wrap(buffer(), offset() + $L, maxLimit())", memberTypeName,
                        memberName, memberName, offset(memberName));
                }
                else
                {
                    code.addStatement("$T.Builder $L = $LRW.wrap(buffer(), limit(), maxLimit())", memberTypeName, memberName,
                        memberName);
                }
                code.addStatement("$L.set(value, $T.UTF_8)", memberName, StandardCharsets.class)
                    .addStatement("limit($L.build().limit())", memberName);
                return code;
            }

            public CodeBlock.Builder addConstantValueMember(
                String memberName)
            {
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("int newLimit = offset() + $L", size("kind"))
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .addStatement("kind($L)", kind(memberName))
                    .addStatement("limit(newLimit)");
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
                TypeName kindTypeName,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
                if (!kindTypeName.isPrimitive())
                {
                    ClassName classType = (ClassName) kindTypeName;
                    TypeName builderType = classType.nestedClass("Builder");
                    builder.addField(FieldSpec.builder(builderType, enumRW(kindTypeName), PRIVATE, FINAL)
                           .initializer("new $T()", builderType)
                           .build());
                }
            }

            public MemberFieldGenerator addMember(
                String memberName,
                TypeName memberTypeName)
            {
                if (memberTypeName != null && !memberTypeName.isPrimitive())
                {
                    String fieldRW = String.format("%sRW", memberName);
                    ClassName classType = (ClassName) memberTypeName;
                    TypeName builderType = classType.nestedClass("Builder");
                    builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                                              .initializer("new $T()", builderType)
                                              .build());
                }
                return this;
            }
        }
    }


    private static ClassName enumClassName(
        TypeName enumFWTypeName)
    {
        String enumFWName = ((ClassName) enumFWTypeName).simpleName();
        return ClassName.bestGuess(enumFWName.substring(0, enumFWName.length() - 2));
    }

    private static String enumRO(
        TypeName enumFWTypeName)
    {
        String enumFWName = ((ClassName) enumFWTypeName).simpleName();
        return String.format("%s%sRO", Character.toLowerCase(enumFWName.charAt(0)),
            enumFWName.substring(1, enumFWName.length() - 2));
    }

    private static String enumRW(
        TypeName enumFWTypeName)
    {
        String enumFWName = ((ClassName) enumFWTypeName).simpleName();
        return String.format("%s%sRW", Character.toLowerCase(enumFWName.charAt(0)),
            enumFWName.substring(1, enumFWName.length() - 2));
    }

    private static String bitMask(
        String fieldName)
    {
        return String.format("BIT_MASK_%s", constant(fieldName));
    }

    private static String value(
        String fieldName)
    {
        String filteredName = NUMBER_WORDS.get(fieldName) == null ? fieldName : NUMBER_WORDS.get(fieldName);
        return String.format("FIELD_VALUE_%s", constant(filteredName));
    }

    private static String kind(
        String fieldName)
    {
        String filteredName = NUMBER_WORDS.get(fieldName) == null ? fieldName : NUMBER_WORDS.get(fieldName);
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
        String filteredName = NUMBER_WORDS.get(fieldName) == null ? fieldName : NUMBER_WORDS.get(fieldName);
        return String.format("FIELD_SIZE_%s", constant(filteredName));
    }

    private static String getAs(
        String fieldName)
    {
        String filteredName = NUMBER_WORDS.get(fieldName) == null ? fieldName : NUMBER_WORDS.get(fieldName);
        return String.format("getAs%s%s", Character.toUpperCase(filteredName.charAt(0)), filteredName.substring(1));
    }

    private static String setAs(
        String fieldName)
    {
        String filteredName = NUMBER_WORDS.get(fieldName) == null ? fieldName : NUMBER_WORDS.get(fieldName);
        return String.format("setAs%s%s", Character.toUpperCase(filteredName.charAt(0)), filteredName.substring(1));
    }

    private static String constant(
        String fieldName)
    {
        return fieldName.replaceAll("([^_A-Z])([A-Z])", "$1_$2").toUpperCase();
    }
}
