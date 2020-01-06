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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.FieldSpec.Builder;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class VariantFlyweightGenerator extends ClassSpecGenerator
{
    private static final Map<String, String> NUMBER_WORDS;
    private static final Map<TypeName, String> TYPE_NAMES;
    private static final Map<String, Long> BIT_MASK_LONG;
    private static final Map<String, Integer> BIT_MASK_INT;
    private static final Map<TypeName, String> CLASS_NAMES;
    private static final Set<TypeName> INTEGER_TYPES;

    private final String baseName;
    private final TypeSpec.Builder builder;
    private final MemberFieldGenerator memberField;
    private final KindConstantGenerator memberKindConstant;
    private final MemberSizeConstantGenerator memberSizeConstant;
    private final MemberOffsetConstantGenerator memberOffsetConstant;
    private final MemberFieldValueConstantGenerator memberFieldValueConstant;
    private final MissingFieldPlaceholderConstantGenerator missingFieldPlaceholderConstant;
    private final ConstructorGenerator constructor;
    private final TryWrapMethodGenerator tryWrapMethod;
    private final WrapMethodGenerator wrapMethod;
    private final WrapWithKindPaddingMethodGenerator wrapArrayElement;
    private final ToStringMethodGenerator toStringMethod;
    private final KindAccessorGenerator kindAccessor;
    private final MemberAccessorGenerator memberAccessor;
    private final LimitMethodGenerator limitMethod;
    private final BuilderClassGenerator builderClass;
    private final GetMethodGenerator getMethod;
    private final GetAsMethodGenerator getAsMethod;
    private final BitMaskConstantGenerator bitMaskConstant;
    private final TypeVariableName typeVarV;
    private final TypeVariableName typeVarO;
    private final TypeVariableName anyType;

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
        longBitMaskValues.put("int8", 0xffff_ffff_ffff_ff80L);
        longBitMaskValues.put("int16", 0xffff_ffff_ffff_8000L);
        longBitMaskValues.put("int24", 0xffff_ffff_ff80_0000L);
        longBitMaskValues.put("int32", 0xffff_ffff_8000_0000L);
        BIT_MASK_LONG = unmodifiableMap(longBitMaskValues);

        Map<String, Integer> intBitMaskValues = new HashMap<>();
        intBitMaskValues.put("int8", 0xffff_ff80);
        intBitMaskValues.put("int16", 0xffff_8000);
        intBitMaskValues.put("int24", 0xff80_0000);
        BIT_MASK_INT = unmodifiableMap(intBitMaskValues);

        Map<TypeName, String> classNames = new HashMap<>();
        classNames.put(TypeName.BYTE, "Byte");
        classNames.put(TypeName.SHORT, "Short");
        classNames.put(TypeName.INT, "Integer");
        classNames.put(TypeName.FLOAT, "Float");
        classNames.put(TypeName.LONG, "Long");
        classNames.put(TypeName.DOUBLE, "Double");
        CLASS_NAMES = unmodifiableMap(classNames);

        Set<TypeName> integerTypes = new HashSet<>();
        integerTypes.add(TypeName.INT);
        integerTypes.add(TypeName.SHORT);
        integerTypes.add(TypeName.BYTE);
        INTEGER_TYPES = integerTypes;
    }

    public VariantFlyweightGenerator(
        ClassName variantName,
        ClassName flyweightName,
        String baseName,
        TypeName kindTypeName,
        AstType ofType,
        TypeName ofTypeName,
        TypeName unsignedOfTypeName,
        TypeResolver resolver)
    {
        super(variantName);
        this.anyType = TypeVariableName.get("?");
        this.typeVarO = TypeVariableName.get("O", flyweightName);
        ClassName variantFWType = resolver.resolveClass(AstType.VARIANT);
        TypeName parameterizedVariantFWType =  ParameterizedTypeName.get(variantFWType, anyType, typeVarO);
        this.typeVarV = TypeVariableName.get("V", parameterizedVariantFWType);
        this.baseName = baseName;
        this.builder = builder(variantName, flyweightName, kindTypeName, ofType, resolver);
        this.memberField = new MemberFieldGenerator(variantName, kindTypeName, ofType, typeVarV, builder);
        this.memberKindConstant = new KindConstantGenerator(variantName, kindTypeName, builder);
        this.memberSizeConstant = new MemberSizeConstantGenerator(variantName, kindTypeName, builder);
        this.memberOffsetConstant = new MemberOffsetConstantGenerator(variantName, kindTypeName, builder);
        this.memberFieldValueConstant = new MemberFieldValueConstantGenerator(variantName, builder);
        this.missingFieldPlaceholderConstant = new MissingFieldPlaceholderConstantGenerator(variantName, builder);
        this.constructor = new ConstructorGenerator(ofType, typeVarV);
        this.tryWrapMethod = new TryWrapMethodGenerator(kindTypeName, ofType);
        this.wrapMethod = new WrapMethodGenerator(kindTypeName, ofType);
        this.wrapArrayElement = new WrapWithKindPaddingMethodGenerator(kindTypeName, ofType);
        this.toStringMethod = new ToStringMethodGenerator(kindTypeName, ofType);
        this.kindAccessor = new KindAccessorGenerator(variantName, kindTypeName, ofType, builder);
        this.memberAccessor = new MemberAccessorGenerator(variantName, kindTypeName, ofType, builder);
        this.limitMethod = new LimitMethodGenerator(kindTypeName, ofType);
        this.getMethod = new GetMethodGenerator(kindTypeName, ofType, ofTypeName, unsignedOfTypeName, resolver);
        this.getAsMethod = new GetAsMethodGenerator(kindTypeName, ofType, ofTypeName, resolver);
        this.bitMaskConstant = new BitMaskConstantGenerator(variantName, ofTypeName, builder);
        this.builderClass = new BuilderClassGenerator(variantName, flyweightName, kindTypeName, ofType, ofTypeName,
            unsignedOfTypeName, resolver, typeVarO);
    }

    public VariantFlyweightGenerator addMember(
        Object kindValue,
        String memberName,
        AstType memberType,
        TypeName memberTypeName,
        TypeName unsignedMemberTypeName,
        int missingFieldValue)
    {
        memberKindConstant.addMember(kindValue, memberName);
        memberOffsetConstant.addMember(memberName, memberTypeName);
        memberFieldValueConstant.addMember(memberName, memberTypeName);
        memberField.addMember(memberName, memberTypeName);
        memberSizeConstant.addMember(memberName, memberTypeName, unsignedMemberTypeName);
        missingFieldPlaceholderConstant.addMember(memberType, missingFieldValue);
        constructor.addMember(memberName, memberTypeName);
        wrapMethod.addMember(kindValue, memberName, memberType);
        wrapArrayElement.addMember(kindValue, memberName);
        tryWrapMethod.addMember(kindValue, memberName, memberType);
        toStringMethod.addMember(kindValue, memberName, memberType, memberTypeName);
        memberAccessor.addMember(memberName, memberTypeName, unsignedMemberTypeName);
        limitMethod.addMember(kindValue, memberName, memberTypeName);
        getMethod.addMember(memberName, kindValue, memberTypeName);
        getAsMethod.addMember(memberName, kindValue);
        bitMaskConstant.addMember(memberName, memberTypeName, unsignedMemberTypeName);
        builderClass.addMember(kindValue, memberName, memberType, memberTypeName, unsignedMemberTypeName);
        return this;
    }

    @Override
    public TypeSpec generate()
    {
        memberKindConstant.build();
        memberSizeConstant.build();
        missingFieldPlaceholderConstant.build();
        memberField.build();
        kindAccessor.build();
        memberAccessor.build();
        bitMaskConstant.build();
        constructor.mixin(builder);
        getMethod.mixin(builder);
        getAsMethod.mixin(builder);
        tryWrapMethod.mixin(builder);
        wrapMethod.mixin(builder);
        wrapArrayElement.mixin(builder);
        toStringMethod.mixin(builder);
        limitMethod.mixin(builder);
        return builder.addType(builderClass.generate())
                      .build();
    }

    private TypeSpec.Builder builder(
        ClassName variantName,
        ClassName flyweightName,
        TypeName kindTypeName,
        AstType ofType,
        TypeResolver resolver)
    {
        if ((isListType(ofType) || isStringType(ofType)) && !kindTypeName.isPrimitive())
        {
            ClassName variantType = resolver.resolveClass(AstType.VARIANT);
            TypeName ofTypeName = resolver.resolveClass(isStringType(ofType) ? AstType.STRING : AstType.LIST);

            ClassName kindName = enumClassName(kindTypeName);
            TypeName superClassType = ParameterizedTypeName.get(variantType, kindName, ofTypeName);
            return classBuilder(variantName)
                .superclass(superClassType)
                .addModifiers(PUBLIC, FINAL);
        }
        if (isArrayType(ofType))
        {
            TypeName ofTypeName = ParameterizedTypeName.get(resolver.resolveClass(AstType.VARIANT_ARRAY), typeVarV);
            return classBuilder(variantName)
                .addTypeVariable(typeVarV)
                .addTypeVariable(typeVarO)
                .superclass(ParameterizedTypeName.get(resolver.resolveClass(AstType.VARIANT), enumClassName(kindTypeName),
                    ofTypeName))
                .addModifiers(PUBLIC, FINAL);
        }
        return classBuilder(variantName)
            .superclass(resolver.flyweightName())
            .addModifiers(PUBLIC, FINAL);
    }

    private static final class MemberFieldGenerator extends ClassSpecMixinGenerator
    {
        private final AstType ofType;
        private final TypeVariableName typeVarV;

        private MemberFieldGenerator(
            ClassName thisType,
            TypeName kindName,
            AstType ofType,
            TypeVariableName typeVarV,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            this.ofType = ofType;
            this.typeVarV = typeVarV;
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
                Builder fieldBuilder;
                if (isArrayType(ofType))
                {
                    TypeName parameterizedArrayType = ParameterizedTypeName.get((ClassName) type, typeVarV);
                    fieldBuilder = FieldSpec.builder(parameterizedArrayType, fieldRO, PRIVATE, FINAL);
                }
                else
                {
                    fieldBuilder = FieldSpec.builder(type, fieldRO, PRIVATE, FINAL);
                    fieldBuilder.initializer("new $T()", type);
                }
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
                                 TYPE_NAMES.get(unsignedMemberTypeName == null ? memberTypeName :
                                     memberTypeName.equals(TypeName.BYTE) ? TypeName.SHORT : unsignedMemberTypeName)
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

    private static final class MissingFieldPlaceholderConstantGenerator extends ClassSpecMixinGenerator
    {
        private int missingFieldValue;

        private MissingFieldPlaceholderConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MissingFieldPlaceholderConstantGenerator addMember(
            AstType memberType,
            int missingFieldValue)
        {
            if (AstType.LIST32.equals(memberType) || AstType.LIST8.equals(memberType))
            {
                if (missingFieldValue != 0)
                {
                    this.missingFieldValue = missingFieldValue;
                }
            }
            return this;
        }

        @Override
        public TypeSpec.Builder build()
        {
            if (missingFieldValue != 0)
            {
                builder.addField(
                    FieldSpec.builder(byte.class, "MISSING_FIELD_PLACEHOLDER", PUBLIC, STATIC, FINAL)
                        .initializer(String.valueOf(missingFieldValue))
                        .build());
            }
            return super.build();
        }
    }

    private static final class ConstructorGenerator extends MethodSpecGenerator
    {
        private final AstType ofType;
        private final TypeVariableName typeVarV;

        private ConstructorGenerator(
            AstType ofType,
            TypeVariableName typeVarV)
        {
            super(constructorBuilder()
                .addModifiers(PUBLIC));
            this.ofType = ofType;
            this.typeVarV = typeVarV;
            builder.addParameter(typeVarV, "type");
        }

        public ConstructorGenerator addMember(
            String memberName,
            TypeName memberTypeName)
        {
            if (isArrayType(ofType))
            {
                builder.addStatement("$LRO = new $T<>(type)", memberName, memberTypeName);
            }
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            return builder.build();
        }

        @Override
        public void mixin(
            TypeSpec.Builder builder)
        {
            if (isArrayType(ofType))
            {
                super.mixin(builder);
            }
        }
    }

    private final class TryWrapMethodGenerator extends MethodSpecGenerator
    {
        private final TypeName kindTypeName;
        private final AstType ofType;

        private TryWrapMethodGenerator(
            TypeName kindTypeName,
            AstType ofType)
        {
            super(methodBuilder("tryWrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(thisName)
                .beginControlFlow("if (super.tryWrap(buffer, offset, maxLimit) == null)")
                .addStatement("return null")
                .endControlFlow());
            this.kindTypeName = kindTypeName;
            this.ofType = ofType;
            if (!kindTypeName.isPrimitive())
            {
                builder.addStatement("$L $L = $L.tryWrap(buffer, offset, maxLimit)", ((ClassName) kindTypeName).simpleName(),
                    enumFWName(kindTypeName), enumRO(kindTypeName))
                    .beginControlFlow("if ($L == null)", enumFWName(kindTypeName))
                    .addStatement("return null")
                    .endControlFlow();
            }
            if (isArrayType(ofType))
            {
                builder.returns(ParameterizedTypeName.get(thisName, typeVarV, typeVarO));
            }
            builder.beginControlFlow("switch (kind())");
        }

        public TryWrapMethodGenerator addMember(
            Object kindValue,
            String memberName,
            AstType memberType)
        {
            builder.beginControlFlow("case $L:", kindTypeName.isPrimitive() ? kind(memberName) : kindValue);
            if (isNonPrimitiveType(ofType))
            {
                builder.beginControlFlow("if ($LRO.tryWrap(buffer, offset + $L, maxLimit) == null)", memberName,
                    kindTypeName.isPrimitive() ? offset(memberName) : String.format("%s.sizeof()", enumFWName(kindTypeName)))
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
        private final TypeName kindTypeName;
        private final AstType ofType;

        private WrapMethodGenerator(
            TypeName kindTypeName,
            AstType ofType)
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
            this.ofType = ofType;
            if (!kindTypeName.isPrimitive())
            {
                builder.addStatement("$L $L = $L.wrap(buffer, offset, maxLimit)", ((ClassName) kindTypeName).simpleName(),
                    enumFWName(kindTypeName), enumRO(kindTypeName));
            }
            if (isArrayType(ofType))
            {
                builder.returns(ParameterizedTypeName.get(thisName, typeVarV, typeVarO));
            }
            builder.beginControlFlow("switch (kind())");
        }

        public WrapMethodGenerator addMember(
            Object kindValue,
            String memberName,
            AstType memberType)
        {
            builder.beginControlFlow("case $L:", kindTypeName.isPrimitive() ? kind(memberName) : kindValue);
            if (isNonPrimitiveType(ofType))
            {
                builder.addStatement("$LRO.wrap(buffer, offset + $L, maxLimit)", memberName,
                    kindTypeName.isPrimitive() ? offset(memberName) : String.format("%s.sizeof()", enumFWName(kindTypeName)));
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

    private final class WrapWithKindPaddingMethodGenerator extends MethodSpecGenerator
    {
        private final TypeName kindTypeName;
        private final AstType ofType;

        private WrapWithKindPaddingMethodGenerator(
            TypeName kindTypeName,
            AstType ofType)
        {
            super(methodBuilder("wrapWithKindPadding")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "elementsOffset")
                .addParameter(int.class, "maxLimit")
                .addParameter(int.class, "kindPadding")
                .returns(thisName));
            this.kindTypeName = kindTypeName;
            this.ofType = ofType;
            if (isStringType(ofType) && !kindTypeName.isPrimitive())
            {
                builder.addStatement("super.wrap(buffer, elementsOffset, maxLimit)")
                    .addStatement("$L $L = $L.wrap(buffer, elementsOffset, maxLimit)", ((ClassName) kindTypeName).simpleName(),
                        enumFWName(kindTypeName), enumRO(kindTypeName))
                    .beginControlFlow("switch (kind())");
            }
            if (isArrayType(ofType))
            {
                builder.returns(ParameterizedTypeName.get(thisName, typeVarV, typeVarO));
            }
        }

        public WrapWithKindPaddingMethodGenerator addMember(
            Object kindValue,
            String memberName)
        {
            if (isStringType(ofType) && !kindTypeName.isPrimitive())
            {
                builder.beginControlFlow("case $L:", kindValue)
                    .addStatement("$LRO.wrap(buffer, $L.limit() + kindPadding, maxLimit)", memberName,
                        kindTypeName.isPrimitive() ? offset(memberName) : enumFWName(kindTypeName))
                    .addStatement("break")
                    .endControlFlow();
            }
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            if (isListType(ofType) || isArrayType(ofType))
            {
                return builder
                    .addStatement("throw new UnsupportedOperationException()")
                    .build();
            }
            return builder.beginControlFlow("default:")
                .addStatement("break")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return this")
                .build();
        }

        @Override
        public void mixin(TypeSpec.Builder builder)
        {
            if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
            {
                super.mixin(builder);
            }
        }
    }

    private final class ToStringMethodGenerator extends MethodSpecGenerator
    {
        private final TypeName kindTypeName;
        private final AstType ofType;

        private ToStringMethodGenerator(
            TypeName kindTypeName,
            AstType ofType)
        {
            super(methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(String.class));
            this.kindTypeName = kindTypeName;
            this.ofType = ofType;
            if (!isListType(ofType) && !isArrayType(ofType))
            {
                builder.beginControlFlow("switch (kind())");
            }
        }

        public ToStringMethodGenerator addMember(
            Object kindValue,
            String memberName,
            AstType memberType,
            TypeName memberTypeName)
        {
            if (!isListType(ofType) && !isArrayType(ofType))
            {
                builder.beginControlFlow("case $L:", kindTypeName.isPrimitive() ? kind(memberName) : kindValue);
                if (isStringType(memberType))
                {
                    builder.addStatement("return String.format(\"$L [$L=%s]\", $LRO.asString())", baseName.toUpperCase(),
                        memberName, memberName);
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
            }
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            if (isListType(ofType) || isArrayType(ofType))
            {
                builder.addStatement("return get().toString()");
            }
            else
            {
                builder.beginControlFlow("default:")
                    .addStatement("return String.format(\"$L [unknown]\")", baseName.toUpperCase())
                    .endControlFlow()
                    .endControlFlow();
            }
            return builder.build();
        }
    }

    private static final class KindAccessorGenerator extends ClassSpecMixinGenerator
    {
        private final TypeName kindTypeName;
        private final AstType ofType;

        private KindAccessorGenerator(
            ClassName thisType,
            TypeName kindTypeName,
            AstType ofType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            this.kindTypeName = kindTypeName;
            this.ofType = ofType;
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

                MethodSpec.Builder kindMethodBuilder = methodBuilder("kind")
                    .addModifiers(PUBLIC)
                    .returns(enumName)
                    .addStatement("return $L.get()", enumRO(kindTypeName));
                if (isNonPrimitiveType(ofType))
                {
                    kindMethodBuilder.addAnnotation(Override.class);
                }
                builder.addMethod(kindMethodBuilder.build());
            }


            return super.build();
        }
    }

    private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
    {
        private final TypeName kindTypeName;
        private final AstType ofType;

        private MemberAccessorGenerator(
            ClassName thisType,
            TypeName kindTypeName,
            AstType ofType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            this.kindTypeName = kindTypeName;
            this.ofType = ofType;
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
                    memberTypeName.equals(TypeName.BYTE) ? TypeName.SHORT : unsignedMemberTypeName));
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
                if (memberTypeName != null && isStringType((ClassName) memberTypeName))
                {
                    codeBlock.addStatement("return $LRO", name);
                }
                else
                {
                    codeBlock.addStatement("return $L", value(name));
                }
            }

            TypeName returnType = TypeName.INT;
            if (memberTypeName != null)
            {
                if (!memberTypeName.equals(TypeName.BYTE) && !memberTypeName.equals(TypeName.SHORT))
                {
                    returnType = Objects.requireNonNullElse(unsignedMemberTypeName, memberTypeName);
                }
            }

            if (!isNonPrimitiveType(ofType))
            {
                builder.addMethod(methodBuilder(getAs(name))
                    .addModifiers(PUBLIC)
                    .returns(returnType)
                    .addCode(codeBlock.build())
                    .build());
            }
            return this;
        }
    }

    private final class GetMethodGenerator extends MethodSpecGenerator
    {
        private final TypeName kindTypeName;
        private final AstType ofType;
        private final TypeName ofTypeName;
        private final TypeName unsignedOfType;
        private final TypeResolver resolver;

        private GetMethodGenerator(
            TypeName kindTypeName,
            AstType ofType,
            TypeName ofTypeName,
            TypeName unsignedOfType,
            TypeResolver resolver)
        {
            super(methodBuilder("get")
                .addModifiers(PUBLIC));
            this.kindTypeName = kindTypeName;
            this.ofType = ofType;
            this.ofTypeName = ofTypeName;
            this.unsignedOfType = unsignedOfType;
            this.resolver = resolver;
            builder.beginControlFlow("switch (kind())");
        }

        public GetMethodGenerator addMember(
            String name,
            Object kindValue,
            TypeName memberTypeName)
        {
            Object kind = kindTypeName.isPrimitive() ? kind(name) : kindValue;
            builder.beginControlFlow("case $L:", kind);
            if (isNonPrimitiveType(ofType))
            {
                builder.addStatement("return $LRO", name);
            }
            else
            {
                builder.addStatement(memberTypeName == null || memberTypeName.isPrimitive() ?
                    "return $L()" : "return $L().asString()", getAs(name));
            }
            builder.endControlFlow();
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            TypeName primitiveReturnType = ofTypeName.equals(TypeName.BYTE) || ofTypeName.equals(TypeName.SHORT) ||
                ofTypeName.equals(TypeName.INT) ? TypeName.INT : TypeName.LONG;
            TypeName returnType;

            if (isListType(ofType))
            {
                returnType = ofTypeName;
            }
            else if (isStringType(ofType))
            {
                returnType = resolver.resolveClass(AstType.STRING);
            }
            else if (isArrayType(ofType))
            {
                returnType = ParameterizedTypeName.get(resolver.resolveClass(AstType.VARIANT_ARRAY), typeVarV);
            }
            else
            {
                returnType = Objects.requireNonNullElseGet(unsignedOfType, () -> ofTypeName.isPrimitive() ? primitiveReturnType :
                    ClassName.bestGuess("String"));
            }
            if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
            {
                builder.addAnnotation(Override.class);
            }
            return builder.beginControlFlow("default:")
                .addStatement("throw new IllegalStateException(\"Unrecognized kind: \" + kind())")
                .endControlFlow()
                .endControlFlow()
                .returns(returnType)
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

    private final class GetAsMethodGenerator extends MethodSpecGenerator
    {
        private final TypeName kindTypeName;
        private final AstType ofType;
        private final TypeName ofTypeName;
        private final TypeResolver resolver;

        private GetAsMethodGenerator(
            TypeName kindTypeName,
            AstType ofType,
            TypeName ofTypeName,
            TypeResolver resolver)
        {
            super(methodBuilder("getAs")
                .addModifiers(PUBLIC)
                .addAnnotation(Override.class));
            this.kindTypeName = kindTypeName;
            this.ofType = ofType;
            this.ofTypeName = ofTypeName;
            this.resolver = resolver;

            if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
            {
                ClassName kindName = enumClassName(kindTypeName);
                builder.addParameter(kindName, "kind")
                    .addParameter(int.class, "kindPadding");
                if (isStringType(ofType))
                {
                    builder.beginControlFlow("switch (kind)");
                }
            }
        }

        public GetAsMethodGenerator addMember(
            String name,
            Object kindValue)
        {
            if (isStringType(ofType) && !kindTypeName.isPrimitive())
            {
                builder.beginControlFlow("case $L:", kindValue)
                    .addStatement("return $LRO.wrap(buffer(), $L.limit() + kindPadding, maxLimit())", name, enumRO(kindTypeName))
                    .endControlFlow();
            }
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            TypeName returnType = isStringType(ofType) ? resolver.resolveClass(AstType.STRING) : isArrayType(ofType) ?
                ParameterizedTypeName.get(resolver.resolveClass(AstType.VARIANT_ARRAY), typeVarV) : ofTypeName;
            if (isListType(ofType) || isArrayType(ofType))
            {
                return builder.returns(returnType)
                    .addStatement("throw new UnsupportedOperationException()")
                    .build();
            }
            return builder.beginControlFlow("default:")
                .addStatement("throw new IllegalStateException(\"Unrecognized kind: \" + kind)")
                .endControlFlow()
                .endControlFlow()
                .returns(returnType)
                .build();
        }

        @Override
        public void mixin(
            TypeSpec.Builder builder)
        {
            if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
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
                boolean isTypeInt = INTEGER_TYPES.contains(ofTypeName);
                TypeName bitMaskType = isTypeInt ? TypeName.INT : TypeName.LONG;
                FieldSpec.Builder bitMaskField = FieldSpec.builder(bitMaskType, bitMask(kindTypeName), PRIVATE,
                    STATIC, FINAL);
                if (isTypeInt)
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
        private final TypeName kindTypeName;
        private final AstType ofType;
        private LimitMethodGenerator(
            TypeName kindTypeName,
            AstType ofType)
        {
            super(methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class));
            this.kindTypeName = kindTypeName;
            this.ofType = ofType;
            if (!isNonPrimitiveType(ofType))
            {
                builder.beginControlFlow("switch (kind())");
            }
        }

        public LimitMethodGenerator addMember(
            Object kindValue,
            String memberName,
            TypeName memberTypeName)
        {
            if (!isNonPrimitiveType(ofType))
            {
                builder.beginControlFlow("case $L:", kindTypeName.isPrimitive() ? kind(memberName) : kindValue);

                if (memberTypeName == null)
                {
                    if (kindTypeName.isPrimitive())
                    {
                        builder.addStatement("return offset()");
                    }
                    else
                    {
                        builder.addStatement("return $L.limit()", enumRO(kindTypeName));
                    }
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
            }
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            if (isNonPrimitiveType(ofType))
            {
                return builder.addStatement("return get().limit()").build();
            }
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
        private final ClassName variantType;
        private final SizeFieldGenerator sizeField;
        private final KindMethodGenerator kindMethod;
        private final ItemMethodGenerator itemMethod;
        private final MaxKindMethodGenerator maxKind;
        private final SizeMethodGenerator sizeMethod;
        private final KindFromLengthMethodGenerator kindFromLengthMethod;
        private final BuildMethodWithMaxLimitGenerator buildMethodWithMaxLimit;
        private final FieldMethodGenerator fieldMethod;
        private final ConstructorGenerator constructor;
        private final SetAsFieldMethodGenerator setAsFieldMethod;
        private final SetWithSpecificKindMethodGenerator setWithSpecificKindMethod;
        private final MemberFieldGenerator memberField;
        private final WrapMethodGenerator wrapMethod;
        private final SetMethodGenerator setMethod;
        private final BuildMethodGenerator buildMethod;
        private final SetList32FieldsMethodGenerator setList32FieldsMethod;
        private final TypeVariableName typeVarB;
        private final TypeVariableName typeVarV;
        private final TypeVariableName typeVarK;
        private final TypeVariableName typeVarO;

        private BuilderClassGenerator(
            ClassName thisVariantType,
            ClassName flyweightType,
            TypeName kindTypeName,
            AstType ofType,
            TypeName ofTypeName,
            TypeName unsignedOfTypeName,
            TypeResolver resolver,
            TypeVariableName typeVarO)
        {
            this(thisVariantType.nestedClass("Builder"), flyweightType.nestedClass("Builder"), thisVariantType, kindTypeName,
                ofType, ofTypeName, unsignedOfTypeName, resolver, typeVarO);
        }

        private BuilderClassGenerator(
            ClassName thisVariantBuilderType,
            ClassName flyweightBuilderRawType,
            ClassName thisVariantType,
            TypeName kindTypeName,
            AstType ofType,
            TypeName ofTypeName,
            TypeName unsignedOfTypeName,
            TypeResolver resolver,
            TypeVariableName typeVarO)
        {
            super(thisVariantBuilderType);

            this.builder = classBuilder(thisVariantBuilderType.simpleName())
                .addModifiers(PUBLIC, STATIC, FINAL);
            this.typeVarO = typeVarO;
            ClassName variantFWType = resolver.resolveClass(AstType.VARIANT);
            ClassName variantBuilderType = variantFWType.nestedClass("Builder");
            this.typeVarK = TypeVariableName.get("K");
            this.typeVarV = TypeVariableName.get("V", ParameterizedTypeName.get(variantFWType, typeVarK, typeVarO));
            this.typeVarB = TypeVariableName.get("B", ParameterizedTypeName.get(variantBuilderType, typeVarV, typeVarK,
                typeVarO));
            if ((isStringType(ofType) || isListType(ofType)) && !kindTypeName.isPrimitive())
            {
                ClassName ofTypeClassName = resolver.resolveClass(isStringType(ofType) ? AstType.STRING : AstType.LIST);
                ClassName kindName = enumClassName(kindTypeName);
                TypeName superClassType = ParameterizedTypeName.get(variantFWType, kindName, ofTypeClassName);
                classBuilder(thisVariantBuilderType).superclass(superClassType).addModifiers(PUBLIC, FINAL);
                builder.superclass(ParameterizedTypeName.get(variantBuilderType, thisVariantType, kindName, ofTypeClassName));
            }
            else if (isArrayType(ofType))
            {
                TypeName parameterizedOfTypeName = ParameterizedTypeName.get(resolver.resolveClass(AstType.VARIANT_ARRAY),
                    typeVarV);
                ClassName kindName = enumClassName(kindTypeName);
                TypeName parameterizedVariantType = ParameterizedTypeName.get(thisVariantType, typeVarV, typeVarO);
                TypeName parameterizedSuperType = ParameterizedTypeName.get(variantBuilderType, parameterizedVariantType,
                    kindName, parameterizedOfTypeName);
                builder.addTypeVariable(typeVarB)
                    .addTypeVariable(typeVarV)
                    .addTypeVariable(typeVarK)
                    .addTypeVariable(typeVarO)
                    .superclass(parameterizedSuperType);
            }
            else
            {
                builder.superclass(ParameterizedTypeName.get(flyweightBuilderRawType, thisVariantType));
            }

            this.variantType = thisVariantType;
            this.sizeField = new SizeFieldGenerator(thisVariantBuilderType, kindTypeName, ofType, builder);
            this.kindMethod = new KindMethodGenerator(kindTypeName, ofType);
            this.maxKind = new MaxKindMethodGenerator(kindTypeName, ofType);
            this.sizeMethod = new SizeMethodGenerator(kindTypeName, ofType);
            this.kindFromLengthMethod = new KindFromLengthMethodGenerator(kindTypeName, ofType);
            this.itemMethod = new ItemMethodGenerator(ofType);
            this.buildMethodWithMaxLimit = new BuildMethodWithMaxLimitGenerator(kindTypeName, thisVariantType, ofType);
            this.fieldMethod = new FieldMethodGenerator(flyweightBuilderRawType, ofType);
            this.wrapMethod = new WrapMethodGenerator(ofType);
            this.setAsFieldMethod = new SetAsFieldMethodGenerator(thisVariantBuilderType, kindTypeName, ofType, ofTypeName,
                builder, resolver);
            this.setWithSpecificKindMethod = new SetWithSpecificKindMethodGenerator(kindTypeName, ofType, resolver);
            this.memberField = new MemberFieldGenerator(thisVariantBuilderType, kindTypeName, typeVarB, typeVarV, typeVarK,
                typeVarO, ofType, builder);
            this.constructor = new ConstructorGenerator(thisVariantType, ofType, typeVarB, typeVarV);
            this.setMethod = new SetMethodGenerator(ofType, ofTypeName, unsignedOfTypeName, kindTypeName, resolver);
            this.buildMethod = new BuildMethodGenerator(kindTypeName, thisVariantType, ofType);
            this.setList32FieldsMethod = new SetList32FieldsMethodGenerator(ofType);
        }

        private void addMember(
            Object kindValue,
            String memberName,
            AstType memberType,
            TypeName memberTypeName,
            TypeName unsignedMemberTypeName)
        {
            maxKind.addMember(memberName);
            kindFromLengthMethod.addMember(memberName, memberTypeName, unsignedMemberTypeName);
            itemMethod.addMember(memberType);
            fieldMethod.addMember(memberType);
            wrapMethod.addMember(memberType);
            setAsFieldMethod.addMember(memberName, memberTypeName, unsignedMemberTypeName);
            setWithSpecificKindMethod.addMember(kindValue, memberName);
            memberField.addMember(memberName, memberTypeName);
            constructor.addMember(memberName, memberTypeName);
            setMethod.addMember(memberName, memberType, memberTypeName, unsignedMemberTypeName);
            buildMethod.addMember(memberType);
            setList32FieldsMethod.addMember(memberType);
        }

        @Override
        public TypeSpec generate()
        {
            maxKind.mixin(builder);
            sizeMethod.mixin(builder);
            kindFromLengthMethod.mixin(builder);
            itemMethod.mixin(builder);
            buildMethodWithMaxLimit.mixin(builder);
            sizeField.build();
            fieldMethod.mixin(builder);
            setAsFieldMethod.build();
            setWithSpecificKindMethod.mixin(builder);
            memberField.build();
            setMethod.mixin(builder);
            wrapMethod.mixin(builder);
            buildMethod.mixin(builder);
            setList32FieldsMethod.mixin(builder);
            return builder.addMethod(kindMethod.generate())
                .addMethod(constructor.generate())
                .build();
        }

        private static final class ConstructorGenerator extends MethodSpecGenerator
        {
            private final ClassName thisVariantType;
            private final AstType ofType;

            private ConstructorGenerator(
                ClassName thisVariantType,
                AstType ofType,
                TypeVariableName typeVarB,
                TypeVariableName typeVarV)
            {
                super(constructorBuilder());
                this.thisVariantType = thisVariantType;
                this.ofType = ofType;
                if (isArrayType(ofType))
                {
                    builder.addParameter(typeVarB, "itemRW")
                        .addParameter(typeVarV, "itemRO")
                        .addStatement("super(new $T<>(itemRO))", thisVariantType);
                }
            }

            public ConstructorGenerator addMember(
                String memberName,
                TypeName memberTypeName)
            {
                if (isArrayType(ofType))
                {
                    builder.addStatement("$LRW = new $T.Builder<>(itemRW, itemRO)", memberName, memberTypeName);
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                if (!isArrayType(ofType))
                {
                    return builder
                        .addModifiers(PUBLIC)
                        .addStatement("super(new $T())", thisVariantType)
                        .build();
                }
                return builder.addModifiers(PUBLIC).build();
            }

        }

        private static final class SizeFieldGenerator extends ClassSpecMixinGenerator
        {
            private SizeFieldGenerator(
                ClassName thisType,
                TypeName kindTypeName,
                AstType ofType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);

                if (isStringType(ofType) && !kindTypeName.isPrimitive())
                {
                    builder.addField(FieldSpec.builder(int.class, "size", PRIVATE)
                        .build());
                }
            }
        }

        private final class SetMethodGenerator extends MethodSpecGenerator
        {
            private final Set<TypeWidth> kindTypeSet = new TreeSet<>();
            private final AstType ofType;
            private final TypeName ofTypeName;
            private final TypeName unsignedOfType;
            private final TypeResolver resolver;
            private final TypeName kindTypeName;
            boolean isList0Type = false;

            private SetMethodGenerator(
                AstType ofType,
                TypeName ofTypeName,
                TypeName unsignedOfType,
                TypeName kindTypeName,
                TypeResolver resolver)
            {
                super(methodBuilder("set")
                    .addModifiers(PUBLIC)
                    .returns(thisName));
                this.ofType = ofType;
                this.ofTypeName = ofTypeName;
                this.unsignedOfType = unsignedOfType;
                this.kindTypeName = kindTypeName;
                this.resolver = resolver;
                if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
                {
                    builder.addAnnotation(Override.class);
                }
            }

            public SetMethodGenerator addMember(
                String kindTypeName,
                AstType memberType,
                TypeName kindType,
                TypeName unsignedKindType)
            {
                if (Character.isDigit(kindTypeName.charAt(0)))
                {
                    String constantDigit = kindTypeName.substring(0, 1);
                    TypeWidth currentType = new TypeWidth(kindType, unsignedKindType, kindTypeName,
                        "0".equals(constantDigit) ? 0 : 8, Integer.parseInt(constantDigit));
                    kindTypeSet.add(currentType);
                }
                else
                {
                    if (AstType.LIST0.equals(memberType))
                    {
                        isList0Type = true;
                    }
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
                if (ofTypeName != null)
                {
                    super.mixin(builder);
                }
            }

            @Override
            public MethodSpec generate()
            {
                if (isArrayType(ofType))
                {
                    TypeName parameterizedVariantArrayName =
                        ParameterizedTypeName.get(resolver.resolveClass(AstType.VARIANT_ARRAY), typeVarV);
                    return builder.returns(ParameterizedTypeName.get(thisName, typeVarB, typeVarV, typeVarK, typeVarO))
                        .addParameter(parameterizedVariantArrayName, "value")
                        .addStatement("throw new UnsupportedOperationException()")
                        .build();
                }
                boolean hasConstant = false;
                boolean isParameterTypeLong = TypeName.LONG.equals(ofTypeName) || TypeName.LONG.equals(unsignedOfType);
                addVariableDefinitions();

                builder.beginControlFlow("switch (highestByteIndex)");
                int lastCaseSet = 0;
                for (TypeWidth type : kindTypeSet)
                {
                    int width = type.width();
                    switch (width)
                    {
                    case 8:
                        hasConstant = addCase8(type, isParameterTypeLong, hasConstant);
                        break;
                    case 16:
                        hasConstant = addCase16(type, hasConstant, isParameterTypeLong);
                        lastCaseSet = 1;
                        break;
                    case 32:
                        hasConstant = addCase32(type, hasConstant, isParameterTypeLong, lastCaseSet);
                        lastCaseSet = 3;
                        break;
                    case 64:
                        hasConstant = addCase64(type, hasConstant, isParameterTypeLong, lastCaseSet);
                        break;
                    }
                }

                if (ofTypeName.isPrimitive())
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

                if (isList0Type)
                {
                    builder.beginControlFlow("case 8:")
                        .addStatement("setAsList0(list)")
                        .addStatement("break")
                        .endControlFlow();
                }

                String parameterName = isListType(ofType) ? "list" : "value";
                builder.beginControlFlow("default:")
                       .addStatement("throw new IllegalArgumentException(\"Illegal $L: \" + $L)", parameterName,
                           parameterName)
                       .endControlFlow()
                       .endControlFlow();
                TypeName parameterType = Objects.requireNonNullElseGet(unsignedOfType, () -> ofTypeName.isPrimitive() ?
                    ofTypeName.equals(TypeName.LONG) ? TypeName.LONG : TypeName.INT : isListType(ofType) ? ofTypeName :
                    resolver.resolveClass(AstType.STRING));
                return builder.addParameter(parameterType, isListType(ofType) ? "list" : "value")
                              .addStatement("return this")
                              .build();
            }

            private boolean addCase8(
                TypeWidth type,
                boolean isParameterTypeLong,
                boolean hasConstant)
            {
                if (type.value() != Integer.MAX_VALUE)
                {
                    builder.beginControlFlow("case 0:")
                        .beginControlFlow("switch ($Lvalue)", isParameterTypeLong ? "(int) " : "")
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
                        addDefaultCase(type, isParameterTypeLong);
                        hasConstant = false;
                    }
                    else
                    {
                        builder.beginControlFlow("case 0:");
                        if (isListType(ofType))
                        {
                            builder.addStatement("$L(list)", setAs(type.kindTypeName()));
                        }
                        else if (isStringType(ofType) && !kindTypeName.isPrimitive())
                        {
                            builder.addStatement("$L(value, 0)", setAs(type.kindTypeName()));
                        }
                        else
                        {
                            builder.addStatement(String.format("$L(%svalue)", isParameterTypeLong ? "(int) " : ""),
                                setAs(type.kindTypeName()));
                        }
                        builder.addStatement("break")
                            .endControlFlow();
                    }
                }
                return hasConstant;
            }

            private boolean addCase16(
                TypeWidth type,
                boolean hasConstant,
                boolean isParameterTypeLong)
            {
                if (hasConstant)
                {
                    addDefaultCase(type, isParameterTypeLong);
                    hasConstant = false;
                }
                builder.beginControlFlow("case 1:");
                if (isStringType(ofType) && !kindTypeName.isPrimitive())
                {
                    builder.addStatement("$L(value, 0)", setAs(type.kindTypeName()));
                }
                else
                {
                    builder.addStatement(String.format("$L(%svalue)", isParameterTypeLong ? "(int) " : ""),
                        setAs(type.kindTypeName()));
                }
                builder.addStatement("break")
                    .endControlFlow();
                return hasConstant;
            }

            private boolean addCase32(
                TypeWidth type,
                boolean hasConstant,
                boolean isParameterTypeLong,
                int lastCaseSet)
            {
                if (hasConstant)
                {
                    addDefaultCase(type, isParameterTypeLong);
                    hasConstant = false;
                }
                if (lastCaseSet < 1)
                {
                    builder.beginControlFlow("case 1:")
                        .endControlFlow();
                }
                builder.beginControlFlow("case 2:")
                    .endControlFlow()
                    .beginControlFlow("case 3:");
                if (isListType(ofType))
                {
                    builder.addStatement("$L(list)", setAs(type.kindTypeName()));
                }
                else if (isStringType(ofType) && !kindTypeName.isPrimitive())
                {
                    builder.addStatement("$L(value, 0)", setAs(type.kindTypeName()));
                }
                else
                {
                    builder.addStatement(String.format("$L(%svalue)",
                        ofTypeName.equals(TypeName.LONG) ? type.unsignedKindType() == null ? "(int) " : "" : ""),
                        setAs(type.kindTypeName()));
                }
                builder.addStatement("break")
                    .endControlFlow();
                return hasConstant;
            }

            private boolean addCase64(
                TypeWidth type,
                boolean hasConstant,
                boolean isParameterTypeLong,
                int lastCaseSet)
            {
                if (hasConstant)
                {
                    addDefaultCase(type, isParameterTypeLong);
                    hasConstant = false;
                }
                if (lastCaseSet < 3)
                {
                    for (int i = lastCaseSet + 1; i <= 3; i++)
                    {
                        builder.beginControlFlow("case $L:", i)
                            .endControlFlow();
                    }
                }
                builder.beginControlFlow("case 4:")
                    .endControlFlow()
                    .beginControlFlow("case 5:")
                    .endControlFlow()
                    .beginControlFlow("case 6:")
                    .endControlFlow()
                    .beginControlFlow("case 7:")
                    .addStatement("$L(value)", setAs(type.kindTypeName()))
                    .addStatement("break")
                    .endControlFlow();
                return hasConstant;
            }

            private void addDefaultCase(
                TypeWidth type,
                boolean isParameterTypeLong)
            {
                builder.beginControlFlow("default:")
                       .addStatement("$L($Lvalue)", setAs(type.kindTypeName()), isParameterTypeLong ? "(int) " : "")
                       .addStatement("break")
                       .endControlFlow()
                       .endControlFlow()
                       .addStatement("break")
                       .endControlFlow();
            }

            private void addVariableDefinitions()
            {
                if (isListType(ofType))
                {
                    builder.addStatement("int length = Math.max(list.length(), list.fieldCount())")
                        .addStatement("int highestByteIndex = Long.numberOfTrailingZeros(Long.highestOneBit(length)) >> 3");
                }
                else if (!ofTypeName.isPrimitive())
                {
                    builder.addStatement("int length = value.length()")
                        .addStatement("int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3");
                }
                else
                {
                    if (unsignedOfType == null)
                    {
                        TypeName className = ofTypeName.equals(TypeName.LONG) ? TypeName.LONG : TypeName.INT;
                        builder.addStatement("int highestByteIndex = ($L.numberOfTrailingZeros($L.highestOneBit(value)) " +
                            "+ 1)  >> 3", CLASS_NAMES.get(className), CLASS_NAMES.get(className));
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
                    builder.beginControlFlow(String.format("case %s:", unsignedOfType.equals(TypeName.LONG) ? "8" : "4"))
                           .addStatement("$L()", setAs(typeZero.kindTypeName()))
                           .addStatement("break")
                           .endControlFlow();
                }
            }

            private void addSignedNegativeIntBlock()
            {
                builder.beginControlFlow(String.format("case %s:", ofTypeName.equals(TypeName.LONG) ? "8" : "4"));
                Iterator<TypeWidth> iterator = kindTypeSet.iterator();
                int i = 0;
                while (iterator.hasNext())
                {
                    TypeWidth currentType = iterator.next();
                    String kindTypeName = currentType.kindTypeName();
                    if (i == 0)
                    {
                        if (currentType.width() == 0)
                        {
                            builder.beginControlFlow("if (value == 0)")
                                   .addStatement("$L()", setAs(kindTypeName))
                                   .endControlFlow();
                        }
                        else
                        {
                            builder.beginControlFlow("if ((value & $L) == $L)", bitMask(kindTypeName),
                                bitMask(kindTypeName))
                                   .addStatement(String.format("$L(%svalue)",
                                       currentType.kindType().equals(TypeName.LONG) ? "" :
                                           ofTypeName.equals(TypeName.LONG) ? "(int) " : ""), setAs(currentType.kindTypeName()))
                                   .endControlFlow();
                        }

                    }
                    else if (!iterator.hasNext() && currentType.value() == Integer.MAX_VALUE)
                    {
                        builder.beginControlFlow("else")
                               .addStatement(String.format("$L(%svalue)",
                                   currentType.kindType().equals(TypeName.LONG) ? "" :
                                       ofTypeName.equals(TypeName.LONG) ? "(int) " : ""), setAs(kindTypeName))
                               .endControlFlow();
                    }
                    else if (currentType.value() == Integer.MAX_VALUE)
                    {
                        builder.beginControlFlow("else if ((value & $L) == $L)",
                            bitMask(kindTypeName), bitMask(kindTypeName))
                               .addStatement(String.format("$L(%svalue)", ofTypeName.equals(TypeName.LONG) ? "(int) " : ""),
                                   setAs(kindTypeName))
                               .endControlFlow();

                    }
                    i++;
                }
                builder.addStatement("break");
                builder.endControlFlow();
            }
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

        private static final class SetAsFieldMethodGenerator extends ClassSpecMixinGenerator
        {
            private final TypeName kindTypeName;
            private final AstType ofType;
            private final TypeName ofTypeName;
            private final TypeResolver resolver;

            private SetAsFieldMethodGenerator(
                ClassName thisType,
                TypeName kindTypeName,
                AstType ofType,
                TypeName ofTypeName,
                TypeSpec.Builder builder,
                TypeResolver resolver)
            {
                super(thisType, builder);
                this.kindTypeName = kindTypeName;
                this.ofType = ofType;
                this.ofTypeName = ofTypeName;
                this.resolver = resolver;
            }

            public SetAsFieldMethodGenerator addMember(
                String memberName,
                TypeName memberTypeName,
                TypeName unsignedMemberTypeName)
            {
                if (!isArrayType(ofType))
                {
                    if (memberTypeName != null)
                    {
                        CodeBlock.Builder code = memberTypeName.isPrimitive() ?
                            addPrimitiveMember(memberName, memberTypeName, unsignedMemberTypeName) :
                            addNonPrimitiveMember(memberName, memberTypeName);
                        TypeName parameterType = TypeName.INT;
                        if (isListType(ofType))
                        {
                            parameterType = ofTypeName;
                        }
                        else if (memberTypeName.isPrimitive())
                        {
                            if (!memberTypeName.equals(TypeName.BYTE) && !memberTypeName.equals(TypeName.SHORT))
                            {
                                parameterType = Objects.requireNonNullElse(unsignedMemberTypeName, memberTypeName);
                            }
                        }
                        else
                        {
                            parameterType = resolver.resolveType(AstType.STRING);
                        }

                        MethodSpec.Builder setAsMethodBuilder = methodBuilder(setAs(memberName))
                            .addModifiers(PUBLIC)
                            .addParameter(parameterType, isListType(ofType) ? "list" : "value");
                        if (isStringType(ofType) && !kindTypeName.isPrimitive())
                        {
                            setAsMethodBuilder.addParameter(int.class, "kindPadding");
                        }
                        setAsMethodBuilder.returns(thisType)
                            .addCode(code.build())
                            .addStatement("return this");
                        builder.addMethod(setAsMethodBuilder.build());
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
                TypeName type = unsignedMemberTypeName == null ? memberTypeName :
                    memberTypeName.equals(TypeName.BYTE) ? TypeName.SHORT : unsignedMemberTypeName;
                String putterName = String.format("put%s", TYPE_NAMES.get(type));
                if (putterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }

                String castType = "";
                if (unsignedMemberTypeName == null)
                {
                    if (memberTypeName.equals(TypeName.BYTE))
                    {
                        castType = "(byte) ";
                    }
                    else if (memberTypeName.equals(TypeName.SHORT))
                    {
                        castType = "(short) ";
                    }
                }
                else if (memberTypeName.equals(TypeName.BYTE))
                {
                    castType = "(short) ";
                }

                String primitiveTypeMemberPutStatement = "buffer().%s(offset() + $L, %svalue)";
                String nonPrimitiveTypeMemberPutStatement = "buffer().%s(limit(), %svalue)";

                String putStatement = String.format(kindTypeName.isPrimitive() ? primitiveTypeMemberPutStatement :
                    nonPrimitiveTypeMemberPutStatement, putterName, castType);
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
                    if (isStringType(ofType))
                    {
                        code.addStatement("$T.Builder $L = $LRW.wrap(buffer(), limit() + kindPadding, maxLimit())",
                            memberTypeName, memberName, memberName);
                    }
                    else
                    {
                        code.addStatement("$T.Builder $L = $LRW.wrap(buffer(), limit(), maxLimit())", memberTypeName, memberName,
                            memberName);
                    }
                }

                if (isListType(ofType))
                {
                    code.addStatement("final DirectBuffer fields = list.fields()")
                        .addStatement("$L.fields(list.fieldCount(), fields, 0, fields.capacity())", memberName)
                        .addStatement("limit($L.build().limit())", memberName);
                }
                else
                {
                    code.addStatement("$L.set(value.asString(), $T.UTF_8)", memberName, StandardCharsets.class)
                        .addStatement("$T $LRO = $L.build()", memberTypeName, memberName, memberName);
                    if (!kindTypeName.isPrimitive())
                    {
                        code.addStatement("size = $LRO.sizeof()", memberName);
                    }
                    code.addStatement("limit($LRO.limit())", memberName);
                }
                return code;
            }

            public CodeBlock.Builder addConstantValueMember(
                String memberName)
            {
                CodeBlock.Builder code = CodeBlock.builder();
                if (kindTypeName.isPrimitive())
                {
                    code.addStatement("int newLimit = offset() + $L", size("kind"))
                        .addStatement("checkLimit(newLimit, maxLimit())")
                        .addStatement("kind($L)", kind(memberName))
                        .addStatement("limit(newLimit)");
                }
                else
                {
                    code.addStatement("kind($L)", kind(memberName));
                }
                return code;
            }

            @Override
            public TypeSpec.Builder build()
            {
                return super.build();
            }
        }

        private final class SetWithSpecificKindMethodGenerator extends MethodSpecGenerator
        {
            private final Set<TypeWidth> kindTypeSet = new TreeSet<>();
            private final AstType ofType;
            private final TypeName kindTypeName;

            private SetWithSpecificKindMethodGenerator(
                TypeName kindTypeName,
                AstType ofType,
                TypeResolver resolver)
            {
                super(methodBuilder("setAs")
                    .addModifiers(PUBLIC)
                    .returns(thisName)
                    .addAnnotation(Override.class));
                this.ofType = ofType;
                this.kindTypeName = kindTypeName;

                if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
                {
                    ClassName kindName = enumClassName(kindTypeName);
                    TypeName ofTypeClassName = isArrayType(ofType) ?
                        ParameterizedTypeName.get(resolver.resolveClass(AstType.VARIANT_ARRAY), typeVarV) :
                        resolver.resolveClass(isStringType(ofType) ? AstType.STRING : AstType.LIST);
                    builder.addParameter(kindName, "kind")
                        .addParameter(ofTypeClassName, "value")
                        .addParameter(int.class, "kindPadding");
                    if (isStringType(ofType))
                    {
                        builder.beginControlFlow("switch (kind)");
                    }
                    else if (isArrayType(ofType))
                    {
                        builder.returns(ParameterizedTypeName.get(thisName, typeVarB, typeVarV, typeVarK, typeVarO));
                    }
                }
            }

            public SetWithSpecificKindMethodGenerator addMember(
                Object kindValue,
                String memberName)
            {
                if (isStringType(ofType) && !kindTypeName.isPrimitive())
                {
                    builder.beginControlFlow("case $L:", kindValue)
                        .addStatement("$L(value, kindPadding)", setAs(memberName))
                        .addStatement("break")
                        .endControlFlow();
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                if (isListType(ofType) || isArrayType(ofType))
                {
                    return builder.addStatement("throw new UnsupportedOperationException()")
                        .build();
                }
                return builder.endControlFlow()
                    .addStatement("return this")
                    .build();
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
                {
                    super.mixin(builder);
                }
            }
        }

        private final class BuildMethodGenerator extends MethodSpecGenerator
        {
            private final List<Integer> sizes = new ArrayList<>();
            private final TypeName kindTypeName;
            private final AstType ofType;
            private AstType largestListTypeName;

            private BuildMethodGenerator(
                TypeName kindTypeName,
                ClassName variantType,
                AstType ofType)
            {
                super(methodBuilder("build")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(variantType));
                this.kindTypeName = kindTypeName;
                this.ofType = ofType;
                this.largestListTypeName = AstType.LIST0;
                if (isArrayType(ofType))
                {
                    builder.returns(ParameterizedTypeName.get(variantType, typeVarV, typeVarO));
                }
            }

            public BuildMethodGenerator addMember(
                AstType memberType)
            {
                if (isListType(ofType) || isArrayType(ofType))
                {
                    if (typeSize(memberType) > typeSize(largestListTypeName))
                    {
                        largestListTypeName = memberType;
                    }
                    sizes.add(typeSize(memberType));
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                if (isListType(ofType))
                {
                    generateListType();
                }
                else if (isArrayType(ofType))
                {
                    generateArrayType();
                }
                builder.beginControlFlow("default:")
                    .addStatement("throw new IllegalArgumentException(\"Illegal length: \" + length)")
                    .endControlFlow()
                    .endControlFlow();
                if (isListType(ofType))
                {
                    builder.endControlFlow();
                }
                return  builder.addStatement("return super.build()").build();
            }

            private void generateArrayType()
            {
                Collections.sort(sizes);
                int largestSize = sizes.get(sizes.size() - 1);

                builder.addStatement("VariantArray$LFW array$L = variantArray$LRW.build()", largestSize, largestSize,
                    largestSize)
                    .addStatement("long length = Math.max(array$L.length(), array$L.fieldCount())", largestSize, largestSize)
                    .addStatement("int highestByteIndex = Long.numberOfTrailingZeros(Long.highestOneBit(length)) >> 3")
                    .beginControlFlow("switch (highestByteIndex)");
                int priorSize = -1;
                for (int size : sizes)
                {
                    switch (size)
                    {
                    case 8:
                        builder.beginControlFlow("case 0:")
                            .endControlFlow()
                            .beginControlFlow("case 8:")
                            .addStatement("$L.wrap(buffer(), offset(), maxLimit())", enumRW(kindTypeName))
                            .addStatement("$L.set(KIND_VARIANT_ARRAY8)", enumRW(kindTypeName))
                            .addStatement("int fieldCount = array$L.fieldCount()", largestSize)
                            .addStatement("variantArray8RW.wrap(buffer(), $L.limit(), maxLimit())", enumRW(kindTypeName))
                            .addStatement("variantArray8RW.items(array$L.items(), 0, array$L.items().capacity(), fieldCount)",
                                largestSize, largestSize)
                            .addStatement("limit(variantArray8RW.build().limit())")
                            .addStatement("break")
                            .endControlFlow();
                        priorSize = 8;
                        break;
                    case 16:
                        if (priorSize < 8)
                        {
                            builder.beginControlFlow("case 0:")
                                .endControlFlow()
                                .beginControlFlow("case 8:")
                                .endControlFlow();
                        }
                        builder.beginControlFlow("case 1:");
                        if (largestSize == 16)
                        {
                            builder.addStatement("limit(array16.limit())");
                        }
                        else
                        {
                            builder.addStatement("$L.wrap(buffer(), offset(), maxLimit())", enumRW(kindTypeName))
                                .addStatement("$L.set(KIND_VARIANT_ARRAY16)", enumRW(kindTypeName))
                                .addStatement("int fieldCount = array$L.fieldCount()", largestSize)
                                .addStatement("variantArray16RW.wrap(buffer(), $L.limit(), maxLimit())", enumRW(kindTypeName))
                                .addStatement("variantArray16RW.items(array$L.items(), 0, array$L.items().capacity(), " +
                                        "fieldCount)", largestSize, largestSize)
                                .addStatement("limit(variantArray16RW.build().limit())");
                        }
                        builder.addStatement("break")
                            .endControlFlow();
                        priorSize = 16;
                        break;
                    case 32:
                        switch (priorSize)
                        {
                        case -1:
                            builder.beginControlFlow("case 0:")
                                .endControlFlow()
                                .beginControlFlow("case 8:")
                                .endControlFlow()
                                .beginControlFlow("case 1:")
                                .endControlFlow();
                            break;
                        case 8:
                            builder.beginControlFlow("case 1:")
                                .endControlFlow();
                            break;
                        }
                        builder.beginControlFlow("case 2:")
                            .endControlFlow()
                            .beginControlFlow("case 3:")
                            .addStatement("limit(array$L.limit())", largestSize)
                            .addStatement("break")
                            .endControlFlow();
                        break;
                    }
                }
            }

            private void generateListType()
            {
                builder.addStatement("$LFW kind = $L.build()", enumClassName(kindTypeName), enumRW(kindTypeName))
                    .beginControlFlow("if (kind.get() == $L)", kind(largestListTypeName.name()))
                    .addStatement("$L $L = $LRW.build()", listClassName(largestListTypeName.name()), largestListTypeName,
                        largestListTypeName)
                    .addStatement("long length = Math.max($L.length(), $L.fieldCount())", largestListTypeName,
                        largestListTypeName)
                    .addStatement("int highestByteIndex = Long.numberOfTrailingZeros(Long.highestOneBit(length)) >> 3")
                    .beginControlFlow("switch (highestByteIndex)");
                Collections.sort(sizes);
                for (int size : sizes)
                {
                    switch (size)
                    {
                    case 8:
                        builder.beginControlFlow("case 0:");
                        if (largestListTypeName.equals(AstType.LIST8))
                        {
                            builder.addStatement("limit(list8.limit())");
                        }
                        else
                        {
                            builder.addStatement("$L.wrap(buffer(), offset(), maxLimit())", enumRW(kindTypeName))
                                .addStatement("$L.set(KIND_LIST8)", enumRW(kindTypeName))
                                .addStatement("list8RW.wrap(buffer(), $L.limit(), maxLimit())", enumRW(kindTypeName))
                                .addStatement("list8RW.fields(list32.fieldCount(), this::setList32Fields)")
                                .addStatement("limit(list8RW.build().limit())")
                                .addStatement("break");
                        }
                        builder.endControlFlow();
                        break;
                    case 32:
                        builder.beginControlFlow("case 1:")
                            .endControlFlow()
                            .beginControlFlow("case 2:")
                            .endControlFlow()
                            .beginControlFlow("case 3:")
                            .addStatement("limit(list32.limit())")
                            .addStatement("break")
                            .endControlFlow();
                        break;
                    }
                }

                if (sizes.get(0) == 0)
                {
                    if (largestListTypeName.equals(AstType.LIST0))
                    {
                        builder.addStatement("limit(list0.limit())");
                    }
                    else
                    {
                        builder.beginControlFlow("case 8:")
                            .addStatement("$L.wrap(buffer(), offset(), maxLimit())", enumRW(kindTypeName))
                            .addStatement("$L.set(KIND_LIST0)", enumRW(kindTypeName))
                            .addStatement("list0RW.wrap(buffer(), $L.limit(), maxLimit())", enumRW(kindTypeName))
                            .addStatement("limit(list0RW.build().limit())")
                            .addStatement("break")
                            .endControlFlow();
                    }
                }
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (isListType(ofType) || isArrayType(ofType))
                {
                    super.mixin(builder);
                }
            }
        }

        private final class FieldMethodGenerator extends MethodSpecGenerator
        {
            private final AstType ofType;
            private final List<Integer> listSize = new ArrayList<>();

            private FieldMethodGenerator(
                ClassName builderRawType,
                AstType ofType)
            {
                super(methodBuilder("field")
                    .addModifiers(PUBLIC)
                    .returns(thisName)
                    .addParameter(builderRawType.nestedClass("Visitor"), "mutator"));
                this.ofType = ofType;
            }

            public FieldMethodGenerator addMember(
                AstType memberType)
            {
                if (isListType(ofType))
                {
                    listSize.add(typeSize(memberType));
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                Collections.sort(listSize);
                int largestListSize = listSize.get(listSize.size() - 1);
                return builder.addStatement("list$LRW.field(mutator)", largestListSize)
                    .addStatement("limit(list$LRW.limit())", largestListSize)
                    .addStatement("return this")
                    .build();
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (isListType(ofType))
                {
                    super.mixin(builder);
                }
            }
        }

        private final class KindMethodGenerator extends MethodSpecGenerator
        {
            private KindMethodGenerator(
                TypeName kindTypeName,
                AstType ofType)
            {
                super(methodBuilder("kind")
                    .addModifiers(PUBLIC)
                    .returns(thisName)
                );
                if (kindTypeName.isPrimitive())
                {
                    builder.addParameter(int.class, "value")
                        .addStatement("buffer().putByte(offset() + $L, (byte)(value & 0xFF))", offset("kind"));
                }
                else
                {
                    if (isNonPrimitiveType(ofType))
                    {
                        builder.addAnnotation(Override.class);
                        if (isArrayType(ofType))
                        {
                            builder.returns(ParameterizedTypeName.get(thisName, typeVarB, typeVarV, typeVarK, typeVarO));
                        }
                    }
                    builder.addParameter(enumClassName(kindTypeName), "value")
                        .addStatement("$L.wrap(buffer(), offset(), maxLimit())", enumRW(kindTypeName))
                        .addStatement("$L.set(value)", enumRW(kindTypeName))
                        .addStatement("limit($L.build().limit())", enumRW(kindTypeName));
                }
                builder.addStatement("return this");
            }

            @Override
            public MethodSpec generate()
            {
                return builder.build();
            }
        }

        private final class MaxKindMethodGenerator extends MethodSpecGenerator
        {
            private final TypeName kindTypeName;
            private final AstType ofType;
            private int maxKindSize;
            private String maxMemberName;

            private MaxKindMethodGenerator(
                TypeName kindTypeName,
                AstType ofType)
            {
                super(methodBuilder("maxKind")
                    .addModifiers(PUBLIC)
                    .addAnnotation(Override.class));
                this.kindTypeName = kindTypeName;
                this.ofType = ofType;
            }

            public MaxKindMethodGenerator addMember(
                String memberName)
            {
                if (!kindTypeName.isPrimitive())
                {
                    if (!Character.isDigit(memberName.charAt(0)))
                    {
                        int kindSize = Integer.parseInt(memberName.replaceAll("\\D+", ""));
                        if (maxKindSize < kindSize)
                        {
                            maxMemberName = memberName;
                            maxKindSize = kindSize;
                        }
                    }
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                if (isListType(ofType) || isArrayType(ofType))
                {
                    return builder.returns(enumClassName(kindTypeName))
                        .addStatement("throw new UnsupportedOperationException()")
                        .build();
                }
                return builder.returns(enumClassName(kindTypeName))
                    .addStatement("return $L", kind(maxMemberName))
                    .build();
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
                {
                    super.mixin(builder);
                }
            }
        }

        private final class SizeMethodGenerator extends MethodSpecGenerator
        {
            private final TypeName kindTypeName;
            private final AstType ofType;

            private SizeMethodGenerator(
                TypeName kindTypeName,
                AstType ofType)
            {
                super(methodBuilder("size")
                    .addModifiers(PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(int.class));
                this.kindTypeName = kindTypeName;
                this.ofType = ofType;
            }

            @Override
            public MethodSpec generate()
            {
                if (isListType(ofType) || isArrayType(ofType))
                {
                    return builder.addStatement("throw new UnsupportedOperationException()")
                        .build();
                }
                return builder.addStatement("return size")
                    .build();
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
                {
                    super.mixin(builder);
                }
            }
        }

        private final class KindFromLengthMethodGenerator extends MethodSpecGenerator
        {
            private final Set<TypeWidth> kindTypeSet = new TreeSet<>();
            private final TypeName kindTypeName;
            private final AstType ofType;

            private KindFromLengthMethodGenerator(
                TypeName kindTypeName,
                AstType ofType)
            {
                super(methodBuilder("kindFromLength")
                    .addModifiers(PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(int.class, "length"));
                this.kindTypeName = kindTypeName;
                this.ofType = ofType;
            }

            public KindFromLengthMethodGenerator addMember(
                String kindTypeName,
                TypeName kindType,
                TypeName unsignedKindType)
            {
                if (isStringType(ofType))
                {
                    int memberWidth = Integer.parseInt(kindTypeName.replaceAll("\\D+", ""));
                    kindTypeSet.add(new TypeWidth(kindType, unsignedKindType, kindTypeName, memberWidth, Integer.MAX_VALUE));
                }

                return this;
            }

            @Override
            public MethodSpec generate()
            {
                if (isListType(ofType) || isArrayType(ofType))
                {
                    return builder.returns(enumClassName(kindTypeName))
                        .addStatement("throw new UnsupportedOperationException()")
                        .build();
                }
                builder.addStatement("int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3")
                    .beginControlFlow("switch (highestByteIndex)");
                int lastCaseSet = -1;
                for (TypeWidth type : kindTypeSet)
                {
                    int width = type.width();
                    switch (width)
                    {
                    case 8:
                        addCase8(type);
                        lastCaseSet = 0;
                        break;
                    case 16:
                        addCase16(type, lastCaseSet);
                        lastCaseSet = 1;
                        break;
                    case 32:
                        addCase32(type, lastCaseSet);
                        lastCaseSet = 3;
                        break;
                    }
                }
                return builder.beginControlFlow("default:")
                    .addStatement("throw new IllegalArgumentException(\"Illegal length: \" + length)")
                    .endControlFlow()
                    .endControlFlow()
                    .returns(enumClassName(kindTypeName))
                    .build();
            }

            private void addCase8(
                TypeWidth type)
            {
                builder.beginControlFlow("case 0:")
                    .addStatement("return $L", kind(type.kindTypeName()))
                    .endControlFlow();
            }

            private void addCase16(
                TypeWidth type,
                int lastCaseSet)
            {
                if (lastCaseSet < 0)
                {
                    builder.beginControlFlow("case 0:")
                        .endControlFlow();
                }
                builder.beginControlFlow("case 1:")
                    .addStatement("return $L", kind(type.kindTypeName()))
                    .endControlFlow();
            }

            private void addCase32(
                TypeWidth type,
                int lastCaseSet)
            {
                if (lastCaseSet < 0)
                {
                    builder.beginControlFlow("case 0:")
                        .endControlFlow();
                }
                if (lastCaseSet < 1)
                {
                    builder.beginControlFlow("case 1:")
                        .endControlFlow();
                }
                builder.beginControlFlow("case 2:")
                    .endControlFlow()
                    .beginControlFlow("case 3:")
                    .addStatement("return $L", kind(type.kindTypeName()))
                    .endControlFlow();
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
                {
                    super.mixin(builder);
                }
            }
        }

        private final class ItemMethodGenerator extends MethodSpecGenerator
        {
            private final AstType ofType;
            private final List<Integer> size = new ArrayList<>();

            private ItemMethodGenerator(
                AstType ofType)
            {
                super(methodBuilder("item"));
                this.ofType = ofType;
            }

            public ItemMethodGenerator addMember(
                AstType memberType)
            {
                if (isArrayType(ofType))
                {
                    size.add(typeSize(memberType));
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                Collections.sort(size);
                int largestListSize = size.get(size.size() - 1);
                return builder.addModifiers(PUBLIC)
                    .returns(ParameterizedTypeName.get(thisName, typeVarB, typeVarV, typeVarK, typeVarO))
                    .addParameter(typeVarO, "item")
                    .addStatement("variantArray$LRW.item(item)", largestListSize)
                    .addStatement("limit(variantArray$LRW.limit())", largestListSize)
                    .addStatement("return this")
                    .build();
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (isArrayType(ofType))
                {
                    super.mixin(builder);
                }
            }
        }

        private final class BuildMethodWithMaxLimitGenerator extends MethodSpecGenerator
        {
            private final TypeName kindTypeName;
            private final AstType ofType;

            private BuildMethodWithMaxLimitGenerator(
                TypeName kindTypeName,
                ClassName thisVariantType,
                AstType ofType)
            {
                super(methodBuilder("build").addModifiers(PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(int.class, "maxLimit")
                    .returns(thisVariantType));
                this.kindTypeName = kindTypeName;
                this.ofType = ofType;
                if (isArrayType(ofType))
                {
                    builder.returns(ParameterizedTypeName.get(variantType, typeVarV, typeVarO));
                }
            }

            @Override
            public MethodSpec generate()
            {
                if (isListType(ofType) || isArrayType(ofType))
                {
                    return builder.addStatement("throw new UnsupportedOperationException()")
                        .build();
                }
                return builder.addStatement("flyweight().wrap(buffer(), offset(), maxLimit)")
                    .addStatement("return flyweight()")
                    .build();
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (isNonPrimitiveType(ofType) && !kindTypeName.isPrimitive())
                {
                    super.mixin(builder);
                }
            }
        }

        private final class WrapMethodGenerator extends MethodSpecGenerator
        {
            private final AstType ofType;
            private final List<Integer> size = new ArrayList<>();

            private WrapMethodGenerator(
                AstType ofType)
            {
                super(methodBuilder("wrap")
                    .addModifiers(PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(thisName)
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .addStatement("super.wrap(buffer, offset, maxLimit)"));
                this.ofType = ofType;
            }

            public WrapMethodGenerator addMember(
                AstType memberType)
            {
                if (isListType(ofType) || isArrayType(ofType))
                {
                    size.add(typeSize(memberType));
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                if (!isListType(ofType) && !isArrayType(ofType))
                {
                    return builder.addStatement("return this").build();
                }
                Collections.sort(size);
                int largestListSize = size.get(size.size() - 1);
                if (isArrayType(ofType))
                {
                    builder.returns(ParameterizedTypeName.get(thisName, typeVarB, typeVarV, typeVarK, typeVarO))
                        .addStatement("kind($L)", kind(String.format("variant_array%d", largestListSize)))
                        .addStatement("variantArray$LRW.wrap(buffer, limit(), maxLimit)", largestListSize);
                }
                else
                {
                    builder.addStatement("kind($L)", kind(String.format("list%d", largestListSize)))
                        .addStatement("list$LRW.wrap(buffer, limit(), maxLimit)", largestListSize);
                }
                return builder.addStatement("return this")
                    .build();
            }

        }

        private static final class SetList32FieldsMethodGenerator extends MethodSpecGenerator
        {
            private final AstType ofType;

            private SetList32FieldsMethodGenerator(
                AstType ofType)
            {
                super(methodBuilder("setList32Fields")
                    .addModifiers(PRIVATE)
                    .returns(int.class)
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit"));
                this.ofType = ofType;
            }

            public SetList32FieldsMethodGenerator addMember(
                AstType memberType)
            {
                if (AstType.LIST32.equals(memberType))
                {
                    builder.addStatement("List32FW list32 = list32RW.build()")
                        .addStatement("final DirectBuffer fields = list32.fields()")
                        .addStatement("buffer.putBytes(offset, fields, 0, fields.capacity())")
                        .addStatement("return fields.capacity()");
                }
                return this;
            }

            @Override
            public MethodSpec generate()
            {
                return builder.build();
            }

            @Override
            public void mixin(
                TypeSpec.Builder builder)
            {
                if (isListType(ofType))
                {
                    super.mixin(builder);
                }
            }
        }

        private static final class MemberFieldGenerator extends ClassSpecMixinGenerator
        {
            private final AstType ofType;
            private final TypeVariableName typeVarB;
            private final TypeVariableName typeVarV;
            private final TypeVariableName typeVarK;
            private final TypeVariableName typeVarO;

            private MemberFieldGenerator(
                ClassName thisType,
                TypeName kindTypeName,
                TypeVariableName typeVarB,
                TypeVariableName typeVarV,
                TypeVariableName typeVarK,
                TypeVariableName typeVarO,
                AstType ofType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
                this.ofType = ofType;
                this.typeVarB = typeVarB;
                this.typeVarV = typeVarV;
                this.typeVarK = typeVarK;
                this.typeVarO = typeVarO;
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
                    ClassName builderType = classType.nestedClass("Builder");
                    if (isArrayType(ofType))
                    {
                        TypeName parameterizedBuilderType = ParameterizedTypeName.get(builderType, typeVarB,
                            typeVarV, typeVarK, typeVarO);
                        builder.addField(FieldSpec.builder(parameterizedBuilderType, fieldRW, PRIVATE, FINAL).build());
                    }
                    else
                    {
                        builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                            .initializer("new $T()", builderType)
                            .build());
                    }
                }
                return this;
            }
        }
    }

    private static boolean isNonPrimitiveType(
        AstType type)
    {
        return isListType(type) || isStringType(type) || isArrayType(type);
    }

    private static boolean isListType(
        AstType type)
    {
        return AstType.LIST.equals(type);
    }

    private static boolean isArrayType(
        AstType type)
    {
        return AstType.VARIANT_ARRAY.equals(type) || AstType.VARIANT_ARRAY8.equals(type) ||
            AstType.VARIANT_ARRAY16.equals(type) || AstType.VARIANT_ARRAY32.equals(type);
    }

    private static boolean isStringType(
        AstType type)
    {
        return AstType.STRING.equals(type) || AstType.STRING8.equals(type) || AstType.STRING16.equals(type) ||
            AstType.STRING32.equals(type);
    }

    private static boolean isStringType(
        ClassName classType)
    {
        return isString8Type(classType) || isString16Type(classType) || isString32Type(classType);
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

    private static ClassName enumClassName(
        TypeName enumFWTypeName)
    {
        String enumFWName = ((ClassName) enumFWTypeName).simpleName();
        return ClassName.bestGuess(enumFWName.substring(0, enumFWName.length() - 2));
    }

    private static String enumFWName(
        TypeName enumFWTypeName)
    {
        String enumFWName = ((ClassName) enumFWTypeName).simpleName();
        return String.format("%s%s", Character.toLowerCase(enumFWName.charAt(0)),
            enumFWName.substring(1, enumFWName.length() - 2));
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

    private static String listClassName(
        String listTypeName)
    {
        return String.format("%s%sFW", Character.toUpperCase(listTypeName.charAt(0)), listTypeName.substring(1));
    }

    private static int typeSize(
        AstType type)
    {
        return Integer.parseInt(type.name().replaceAll("\\D+", ""));
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
