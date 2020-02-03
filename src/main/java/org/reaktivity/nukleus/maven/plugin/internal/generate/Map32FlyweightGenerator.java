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
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BIT_UTIL_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.UNSAFE_BUFFER_TYPE;

import java.util.function.Consumer;
import java.util.function.Function;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class Map32FlyweightGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final TypeVariableName typeVarKV;
    private final TypeVariableName typeVarVV;
    private final Map32FlyweightGenerator.BuilderClassBuilder builderClassBuilder;

    public Map32FlyweightGenerator(
        ClassName flyweightType,
        ClassName mapType,
        ClassName variantType)
    {
        super(flyweightType.peerClass("Map32FW"));
        TypeName anyType = TypeVariableName.get("?");
        TypeName parameterizedVariantType = ParameterizedTypeName.get(variantType, anyType, anyType);
        this.typeVarKV = TypeVariableName.get("KV", parameterizedVariantType);
        this.typeVarVV = TypeVariableName.get("VV", parameterizedVariantType);
        TypeName parameterizedMapType = ParameterizedTypeName.get(mapType, typeVarKV, typeVarVV);
        this.classBuilder = classBuilder(thisName)
            .superclass(parameterizedMapType)
            .addModifiers(PUBLIC, FINAL)
            .addTypeVariable(typeVarKV)
            .addTypeVariable(typeVarVV);

        this.builderClassBuilder = new Map32FlyweightGenerator.BuilderClassBuilder(thisName, mapType, variantType, flyweightType);
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder
            .addField(lengthSizeConstant())
            .addField(fieldCountSizeConstant())
            .addField(lengthOffsetConstant())
            .addField(fieldCountOffsetConstant())
            .addField(fieldsOffsetConstant())
            .addField(lengthMaxValueConstant())
            .addField(keyField())
            .addField(valueField())
            .addField(entriesField())
            .addMethod(constructor())
            .addMethod(lengthMethod())
            .addMethod(fieldCountMethod())
            .addMethod(entriesMethod())
            .addMethod(forEachMethod())
            .addMethod(tryWrapMethod())
            .addMethod(wrapMethod())
            .addMethod(limitMethod())
            .addMethod(toStringMethod())
            .addType(builderClassBuilder.build())
            .build();
    }

    private FieldSpec lengthSizeConstant()
    {
        return FieldSpec.builder(int.class, "LENGTH_SIZE", PRIVATE, STATIC, FINAL)
            .initializer("$T.SIZE_OF_INT", BIT_UTIL_TYPE)
            .build();
    }

    private FieldSpec fieldCountSizeConstant()
    {
        return FieldSpec.builder(int.class, "FIELD_COUNT_SIZE", PRIVATE, STATIC, FINAL)
            .initializer("$T.SIZE_OF_INT", BIT_UTIL_TYPE)
            .build();
    }

    private FieldSpec lengthOffsetConstant()
    {
        return FieldSpec.builder(int.class, "LENGTH_OFFSET", PRIVATE, STATIC, FINAL)
            .initializer("0")
            .build();
    }

    private FieldSpec fieldCountOffsetConstant()
    {
        return FieldSpec.builder(int.class, "FIELD_COUNT_OFFSET", PRIVATE, STATIC, FINAL)
            .initializer("LENGTH_OFFSET + LENGTH_SIZE")
            .build();
    }

    private FieldSpec fieldsOffsetConstant()
    {
        return FieldSpec.builder(int.class, "FIELDS_OFFSET", PRIVATE, STATIC, FINAL)
            .initializer("FIELD_COUNT_OFFSET + FIELD_COUNT_SIZE")
            .build();
    }

    private FieldSpec lengthMaxValueConstant()
    {
        return FieldSpec.builder(long.class, "LENGTH_MAX_VALUE", PRIVATE, STATIC, FINAL)
            .initializer("0xFFFFFFFFL")
            .build();
    }

    private FieldSpec keyField()
    {
        return FieldSpec.builder(typeVarKV, "keyRO", PRIVATE, FINAL)
            .build();
    }

    private FieldSpec valueField()
    {
        return FieldSpec.builder(typeVarVV, "valueRO", PRIVATE, FINAL)
            .build();
    }

    private FieldSpec entriesField()
    {
        return FieldSpec.builder(DIRECT_BUFFER_TYPE, "entriesRO", PRIVATE, FINAL)
            .initializer("new $T(0L, 0)", UNSAFE_BUFFER_TYPE)
            .build();
    }

    private MethodSpec constructor()
    {
        return constructorBuilder()
            .addModifiers(PUBLIC)
            .addParameter(typeVarKV, "keyRO")
            .addParameter(typeVarVV, "valueRO")
            .addStatement("this.keyRO = keyRO")
            .addStatement("this.valueRO = valueRO")
            .build();
    }

    private MethodSpec lengthMethod()
    {
        return methodBuilder("length")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .returns(int.class)
            .addStatement("return buffer().getInt(offset() + LENGTH_OFFSET)")
            .build();
    }

    private MethodSpec fieldCountMethod()
    {
        return methodBuilder("fieldCount")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .returns(int.class)
            .addStatement("return buffer().getInt(offset() + FIELD_COUNT_OFFSET)")
            .build();
    }

    private MethodSpec entriesMethod()
    {
        return methodBuilder("entries")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .returns(DIRECT_BUFFER_TYPE)
            .addStatement("return entriesRO")
            .build();
    }

    private MethodSpec forEachMethod()
    {
        TypeName parameterizedConsumerType = ParameterizedTypeName.get(ClassName.get(Consumer.class), typeVarVV);
        TypeName parameterizedFunctionType = ParameterizedTypeName.get(ClassName.get(Function.class), typeVarKV,
            parameterizedConsumerType);

        return methodBuilder("forEach")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .addParameter(parameterizedFunctionType, "consumer")
            .addStatement("int offset = offset() + FIELDS_OFFSET")
            .addStatement("int fieldCount = fieldCount()")
            .beginControlFlow("for (int i = 0; i < fieldCount; i += 2)")
            .addStatement("keyRO.wrap(buffer(), offset, limit())")
            .addStatement("valueRO.wrap(buffer(), keyRO.limit(), limit())")
            .addStatement("offset = valueRO.limit()")
            .addStatement("consumer.apply(keyRO).accept(valueRO)")
            .endControlFlow()
            .build();
    }

    private MethodSpec tryWrapMethod()
    {
        return methodBuilder("tryWrap")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .addParameter(DIRECT_BUFFER_TYPE, "buffer")
            .addParameter(int.class, "offset")
            .addParameter(int.class, "maxLimit")
            .returns(thisName)
            .beginControlFlow("if (super.tryWrap(buffer, offset, maxLimit) == null)")
            .addStatement("return null")
            .endControlFlow()
            .addStatement("final int itemsSize = length() - FIELD_COUNT_SIZE")
            .addStatement("entriesRO.wrap(buffer, offset + FIELDS_OFFSET, itemsSize)")
            .beginControlFlow("if (limit() > maxLimit)")
            .addStatement("return null")
            .endControlFlow()
            .addStatement("return this")
            .build();
    }

    private MethodSpec wrapMethod()
    {
        return methodBuilder("wrap")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .addParameter(DIRECT_BUFFER_TYPE, "buffer")
            .addParameter(int.class, "offset")
            .addParameter(int.class, "maxLimit")
            .returns(thisName)
            .addStatement("super.wrap(buffer, offset, maxLimit)")
            .addStatement("final int itemsSize = length() - FIELD_COUNT_SIZE")
            .addStatement("entriesRO.wrap(buffer, offset + FIELDS_OFFSET, itemsSize)")
            .addStatement("checkLimit(limit(), maxLimit)")
            .addStatement("return this")
            .build();
    }

    private MethodSpec limitMethod()
    {
        return methodBuilder("limit")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .returns(int.class)
            .addStatement("return offset() + LENGTH_SIZE + length()")
            .build();
    }

    private MethodSpec toStringMethod()
    {
        return methodBuilder("toString")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .returns(String.class)
            .addStatement("return String.format(\"map32<%d, %d>\", length(), fieldCount())")
            .build();
    }

    private static final class BuilderClassBuilder
    {
        private final ClassName map32Type;
        private final TypeSpec.Builder classBuilder;
        private final TypeVariableName typeVarKB;
        private final TypeVariableName typeVarKV;
        private final TypeVariableName typeVarKK;
        private final TypeVariableName typeVarKO;
        private final TypeVariableName typeVarVB;
        private final TypeVariableName typeVarVV;
        private final TypeVariableName typeVarVK;
        private final TypeVariableName typeVarVO;
        private final TypeName parameterizedBuilderType;

        private BuilderClassBuilder(
            ClassName map32Type,
            ClassName mapType,
            ClassName variantType,
            ClassName flyweightType)
        {
            ClassName variantBuilderType = variantType.nestedClass("Builder");
            ClassName map32BuilderType = map32Type.nestedClass("Builder");
            ClassName mapBuilderType = mapType.nestedClass("Builder");
            this.map32Type = map32Type;
            this.typeVarKK = TypeVariableName.get("KK");
            this.typeVarKO = TypeVariableName.get("KO", flyweightType);
            this.typeVarKV = TypeVariableName.get("KV", ParameterizedTypeName.get(variantType, typeVarKK, typeVarKO));
            this.typeVarKB = TypeVariableName.get("KB", ParameterizedTypeName.get(variantBuilderType, typeVarKV, typeVarKK,
                typeVarKO));
            this.typeVarVK = TypeVariableName.get("VK");
            this.typeVarVO = TypeVariableName.get("VO", flyweightType);
            this.typeVarVV = TypeVariableName.get("VV", ParameterizedTypeName.get(variantType, typeVarVK, typeVarVO));
            this.typeVarVB = TypeVariableName.get("VB", ParameterizedTypeName.get(variantBuilderType, typeVarVV, typeVarVK,
                typeVarVO));
            TypeName parameterizedMapBuilderType = ParameterizedTypeName.get(mapBuilderType, map32Type, typeVarKB, typeVarKV,
                typeVarKK, typeVarKO, typeVarVB, typeVarVV, typeVarVK, typeVarVO);

            this.parameterizedBuilderType = ParameterizedTypeName.get(map32BuilderType, typeVarKB, typeVarKV, typeVarKK,
                typeVarKO, typeVarVB, typeVarVV, typeVarVK, typeVarVO);

            this.classBuilder = classBuilder(map32BuilderType.simpleName())
                .addModifiers(PUBLIC, STATIC, FINAL)
                .superclass(parameterizedMapBuilderType)
                .addTypeVariable(typeVarKB)
                .addTypeVariable(typeVarKV)
                .addTypeVariable(typeVarKK)
                .addTypeVariable(typeVarKO)
                .addTypeVariable(typeVarVB)
                .addTypeVariable(typeVarVV)
                .addTypeVariable(typeVarVK)
                .addTypeVariable(typeVarVO);
        }

        public TypeSpec build()
        {
            return classBuilder
                .addMethod(constructor())
                .addMethod(wrapMethod())
                .addMethod(entriesMethod())
                .addMethod(buildMethod())
                .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(typeVarKB, "keyRW")
                .addParameter(typeVarKV, "keyRO")
                .addParameter(typeVarVB, "valueRW")
                .addParameter(typeVarVV, "valueRO")
                .addStatement("super(new Map32FW<>(keyRO, valueRO), keyRW, valueRW)")
                .build();
        }

        private MethodSpec wrapMethod()
        {
            return methodBuilder("wrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(parameterizedBuilderType)
                .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .addStatement("super.wrap(buffer, offset, maxLimit)")
                .addStatement("int newLimit = offset + FIELDS_OFFSET")
                .addStatement("checkLimit(newLimit, maxLimit)")
                .addStatement("limit(newLimit)")
                .addStatement("return this")
                .build();
        }

        private MethodSpec entriesMethod()
        {
            return methodBuilder("entries")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(parameterizedBuilderType)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "srcOffset")
                .addParameter(int.class, "length")
                .addParameter(int.class, "fieldCount")
                .addStatement("buffer().putBytes(offset() + FIELDS_OFFSET, buffer, srcOffset, length)")
                .addStatement("int newLimit = offset() + FIELDS_OFFSET + length")
                .addStatement("checkLimit(newLimit, maxLimit())")
                .addStatement("limit(newLimit)")
                .addStatement("super.entries(buffer, srcOffset, length, fieldCount)")
                .addStatement("return this")
                .build();
        }

        private MethodSpec buildMethod()
        {
            return methodBuilder("build")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(map32Type)
                .addStatement("int length = limit() - offset() - FIELD_COUNT_OFFSET")
                .addStatement("buffer().putInt(offset() + LENGTH_OFFSET, length)")
                .addStatement("buffer().putInt(offset() + FIELD_COUNT_OFFSET, fieldCount())")
                .addStatement("return super.build()")
                .build();
        }
    }
}
