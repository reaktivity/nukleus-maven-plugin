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
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BIT_UTIL_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.UNSAFE_BUFFER_TYPE;

import java.util.function.Consumer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class Array8OfVariantFWGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final TypeVariableName typeVarT;
    private final BuilderClassBuilder builderClassBuilder;

    public Array8OfVariantFWGenerator(
        ClassName flyweightType,
        ClassName arrayType,
        ClassName variantType)
    {
        super(flyweightType.peerClass("Array8OfVariantFW"));

        this.typeVarT = TypeVariableName.get("T", flyweightType, variantType);

        this.classBuilder = classBuilder(thisName)
            .superclass(ParameterizedTypeName.get(arrayType, typeVarT, thisName))
            .addModifiers(PUBLIC, FINAL)
            .addTypeVariable(typeVarT);
        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightType, variantType,
            arrayType.nestedClass("Builder"));
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
            .addField(itemField())
            .addField(itemsField())
            .addMethod(constructor())
            .addMethod(lengthMethod())
            .addMethod(fieldCountMethod())
            .addMethod(itemsMethod())
            .addMethod(forEachMethod())
            .addMethod(wrapMethod())
            .addMethod(tryWrapMethod())
            .addMethod(limitMethod())
            .addMethod(toStringMethod())
            .addType(builderClassBuilder.build())
            .build();
    }

    private FieldSpec lengthSizeConstant()
    {
        return FieldSpec.builder(int.class, "LENGTH_SIZE", PRIVATE, STATIC, FINAL)
            .initializer("$T.SIZE_OF_BYTE", BIT_UTIL_TYPE)
            .build();
    }

    private FieldSpec fieldCountSizeConstant()
    {
        return FieldSpec.builder(int.class, "FIELD_COUNT_SIZE", PRIVATE, STATIC, FINAL)
            .initializer("$T.SIZE_OF_BYTE", BIT_UTIL_TYPE)
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
        return FieldSpec.builder(int.class, "LENGTH_MAX_VALUE", PRIVATE, STATIC, FINAL)
            .initializer("0xFF")
            .build();
    }

    private FieldSpec itemField()
    {
        return FieldSpec.builder(typeVarT, "itemRO", PRIVATE, FINAL)
            .build();
    }

    private FieldSpec itemsField()
    {
        return FieldSpec.builder(DIRECT_BUFFER_TYPE, "itemsRO", PRIVATE, FINAL)
            .initializer("new $T(0L, 0)", UNSAFE_BUFFER_TYPE)
            .build();
    }

    private MethodSpec constructor()
    {
        return constructorBuilder()
            .addParameter(typeVarT, "itemRO")
            .addStatement("this.itemRO = itemRO")
            .build();
    }

    private MethodSpec lengthMethod()
    {
        return methodBuilder("length")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .returns(int.class)
            .addStatement("return buffer().getByte(offset() + LENGTH_OFFSET)")
            .build();
    }

    private MethodSpec fieldCountMethod()
    {
        return methodBuilder("fieldCount")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .returns(int.class)
            .addStatement("return buffer().getByte(offset() + FIELD_COUNT_OFFSET)")
            .build();
    }

    private MethodSpec itemsMethod()
    {
        return methodBuilder("items")
            .addAnnotation(Override.class)
            .addModifiers(PUBLIC)
            .returns(DIRECT_BUFFER_TYPE)
            .addStatement("return itemsRO")
            .build();
    }

    private MethodSpec forEachMethod()
    {
        ClassName consumerRawType = ClassName.get(Consumer.class);
        TypeName consumerType = ParameterizedTypeName.get(consumerRawType, typeVarT);

        return methodBuilder("forEach")
            .addModifiers(PUBLIC)
            .addParameter(consumerType, "consumer")
            .returns(thisName)
            .addStatement("int offset = offset() + FIELDS_OFFSET")
            .addStatement("int currentPudding = 0")
            .beginControlFlow("for (int i = 0; i < fieldCount(); i++)")
            .addStatement("itemRO.wrapArrayElement(buffer(), offset, limit(), currentPudding)")
            .addStatement("consumer.accept(itemRO)")
            .addStatement("currentPudding += itemRO.get().sizeof()")
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
            .addStatement("itemsRO.wrap(buffer, offset + FIELDS_OFFSET, itemsSize)")
            .addStatement("checkLimit(limit(), maxLimit)")
            .addStatement("return this")
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
            .addStatement("itemsRO.wrap(buffer, offset + FIELDS_OFFSET, itemsSize)")
            .beginControlFlow("if (limit() > maxLimit)")
            .addStatement("return null")
            .endControlFlow()
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
            .addStatement("return String.format(\"array8<%d, %d>\", length(), fieldCount())")
            .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final TypeVariableName typeVarB;
        private final TypeVariableName typeVarT;
        private final TypeVariableName typeVarO;
        private final TypeName array8BuilderType;
        private final ClassName array8Type;

        private BuilderClassBuilder(
            ClassName array8Type,
            ClassName flyweightType,
            ClassName variantType,
            ClassName arrayBuilderType)
        {
            ClassName flyweightBuilderType = flyweightType.nestedClass("Builder");
            ClassName variantBuilderRawType = variantType.nestedClass("Builder");
            ClassName array8BuilderRawType = array8Type.nestedClass("Builder");
            TypeVariableName typeVarK = TypeVariableName.get("K");
            this.array8Type = array8Type;
            this.typeVarO = TypeVariableName.get("O", flyweightType);
            this.typeVarT = TypeVariableName.get("T", flyweightType, ParameterizedTypeName.get(variantType,
                typeVarO, typeVarK));
            TypeName variantBuilderType = ParameterizedTypeName.get(variantBuilderRawType, typeVarT, typeVarO, typeVarK);

            this.typeVarB = TypeVariableName.get("B", flyweightBuilderType, variantBuilderType);
            this.array8BuilderType = ParameterizedTypeName.get(array8BuilderRawType, typeVarB, typeVarT, typeVarO, typeVarK);
            this.classBuilder = classBuilder(array8BuilderRawType.simpleName())
                .addModifiers(PUBLIC, STATIC, FINAL)
                .superclass(ParameterizedTypeName.get(arrayBuilderType, typeVarT, typeVarB, typeVarO, typeVarK, array8Type))
                .addTypeVariable(typeVarB)
                .addTypeVariable(typeVarT)
                .addTypeVariable(typeVarO)
                .addTypeVariable(typeVarK);
        }

        public TypeSpec build()
        {
            return classBuilder
                .addField(kindPaddingField())
                .addMethod(constructor())
                .addMethod(itemMethod())
                .addMethod(itemsMethod())
                .addMethod(wrapMethod())
                .addMethod(buildMethod())
                .build();
        }

        private FieldSpec kindPaddingField()
        {
            return FieldSpec.builder(int.class, "kindPadding", PRIVATE)
                .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(typeVarB, "itemRW")
                .addParameter(typeVarT, "itemRO")
                .addStatement("super(new Array8OfVariantFW<>(itemRO), itemRW)")
                .build();
        }

        private MethodSpec itemMethod()
        {
            return methodBuilder("item")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(array8BuilderType)
                .addParameter(typeVarO, "item")
                .addStatement("itemRW().wrap(buffer(), offset() + FIELDS_OFFSET, maxLimit())")
                .addStatement("itemRW().setAs(itemRW().maxKind(), item, kindPadding)")
                .addStatement("super.item(item)")
                .addStatement("kindPadding += itemRW().size()")
                .addStatement("return this")
                .build();
        }

        private MethodSpec itemsMethod()
        {
            return methodBuilder("items")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(array8BuilderType)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "srcOffset")
                .addParameter(int.class, "length")
                .addParameter(int.class, "fieldCount")
                .addStatement("buffer().putBytes(offset() + FIELDS_OFFSET, buffer, srcOffset, length)")
                .addStatement("int newLimit = offset() + FIELDS_OFFSET + length")
                .addStatement("checkLimit(newLimit, maxLimit())")
                .addStatement("limit(newLimit)")
                .addStatement("super.items(buffer, srcOffset, length, fieldCount)")
                .addStatement("return this")
                .build();
        }

        private MethodSpec wrapMethod()
        {
            return methodBuilder("wrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(array8BuilderType)
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

        private MethodSpec buildMethod()
        {
            return methodBuilder("build")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(array8Type)
                .beginControlFlow("if (maxLength() > 0 && !itemRW().maxKind().equals(itemRW()" +
                    ".kindFromLength(maxLength())))")
                .addStatement("K kind = itemRW().kindFromLength(maxLength())")
                .addStatement("int originalPadding = 0")
                .addStatement("int rearrangePadding = 0")
                .addStatement("int originalLimit = itemRW().limit()")
                .beginControlFlow("for (int i = 0; i < fieldCount(); i++)")
                .addStatement("T itemRO = itemRW().build(originalLimit)")
                .addStatement("O originalItem = itemRO.getAs(itemRW().maxKind(), originalPadding)")
                .addStatement("originalPadding += originalItem.sizeof()")
                .addStatement("itemRW().setAs(kind, originalItem, rearrangePadding)")
                .addStatement("O rearrangedItem = itemRO.getAs(kind, rearrangePadding)")
                .addStatement("rearrangePadding += rearrangedItem.sizeof()")
                .endControlFlow()
                .addStatement("limit(itemRW().limit())")
                .endControlFlow()
                .addStatement("int length = limit() - offset() - FIELD_COUNT_OFFSET")
                .addStatement("assert length <= LENGTH_MAX_VALUE : \"Length is too large\"")
                .addStatement("assert fieldCount() <= LENGTH_MAX_VALUE : \"Field count is too large\"")
                .addStatement("buffer().putByte(offset() + LENGTH_OFFSET, (byte) length)")
                .addStatement("buffer().putByte(offset() + FIELD_COUNT_OFFSET, (byte) fieldCount())")
                .addStatement("return super.build()")
                .build();
        }
    }
}
