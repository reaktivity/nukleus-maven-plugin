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
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;

import java.util.function.Consumer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class ArrayOfVariantFWGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final BuilderClassBuilder builderClassBuilder;
    private final TypeVariableName typeVarT;
    private final TypeVariableName typeVarA;

    public ArrayOfVariantFWGenerator(
        ClassName flyweightType,
        ClassName variantType)
    {
        super(flyweightType.peerClass("ArrayOfVariantFW"));

        typeVarT = TypeVariableName.get("T", flyweightType, variantType);
        typeVarA = TypeVariableName.get("A", thisName);

        this.classBuilder = classBuilder(thisName)
            .superclass(flyweightType)
            .addModifiers(PUBLIC, ABSTRACT)
            .addTypeVariable(typeVarT)
            .addTypeVariable(typeVarA);

        this.builderClassBuilder = new BuilderClassBuilder(thisName, variantType, flyweightType);
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder
            .addMethod(lengthMethod())
            .addMethod(fieldCountMethod())
            .addMethod(forEachMethod())
            .addMethod(itemsMethod())
            .addType(builderClassBuilder.build())
            .build();
    }

    private MethodSpec lengthMethod()
    {
        return methodBuilder("length")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(int.class)
            .build();
    }

    private MethodSpec fieldCountMethod()
    {
        return methodBuilder("fieldCount")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(int.class)
            .build();
    }

    private MethodSpec forEachMethod()
    {
        TypeName consumerType = ParameterizedTypeName.get(ClassName.get(Consumer.class), typeVarT);
        return methodBuilder("forEach")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(typeVarA)
            .addParameter(consumerType, "consumer")
            .build();
    }

    private MethodSpec itemsMethod()
    {
        return methodBuilder("items")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(DIRECT_BUFFER_TYPE)
            .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final ClassName builderType;
        private final TypeVariableName typeVarT;
        private final TypeVariableName typeVarB;
        private final TypeVariableName typeVarO;
        private final TypeVariableName typeVarK;
        private final TypeVariableName typeVarA;

        private BuilderClassBuilder(
            ClassName arrayType,
            ClassName variantType,
            ClassName flyweightType)
        {
            this.typeVarO = TypeVariableName.get("O", flyweightType);
            this.typeVarK = TypeVariableName.get("K");
            this.typeVarA = TypeVariableName.get("A", arrayType);
            this.typeVarT = TypeVariableName.get("T", flyweightType,
                ParameterizedTypeName.get(variantType, typeVarO, typeVarK));

            ClassName flyweightBuilderType = flyweightType.nestedClass("Builder");
            ClassName variantBuilderType = variantType.nestedClass("Builder");
            this.typeVarB = TypeVariableName.get("B", flyweightBuilderType,
                ParameterizedTypeName.get(variantBuilderType, typeVarT, typeVarO, typeVarK));

            this.builderType = arrayType.nestedClass("Builder");
            this.classBuilder = classBuilder(builderType.simpleName())
                .addModifiers(PUBLIC, ABSTRACT, STATIC)
                .superclass(ParameterizedTypeName.get(flyweightBuilderType, typeVarA))
                .addTypeVariable(typeVarT)
                .addTypeVariable(typeVarB)
                .addTypeVariable(typeVarO)
                .addTypeVariable(typeVarK)
                .addTypeVariable(typeVarA);
        }

        public TypeSpec build()
        {
            return classBuilder
                .addField(itemRWField())
                .addField(maxLengthField())
                .addField(fieldCountField())
                .addMethod(constructor())
                .addMethod(itemMethod())
                .addMethod(itemsMethod())
                .addMethod(itemRWMethod())
                .addMethod(fieldCountMethod())
                .addMethod(maxLengthMethod())
                .build();
        }

        private FieldSpec itemRWField()
        {
            return FieldSpec.builder(typeVarB, "itemRW", PRIVATE, FINAL).build();
        }

        private FieldSpec maxLengthField()
        {
            return FieldSpec.builder(int.class, "maxLength", PRIVATE).build();
        }

        private FieldSpec fieldCountField()
        {
            return FieldSpec.builder(int.class, "fieldCount", PRIVATE).build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(typeVarA, "flyweight")
                .addParameter(typeVarB, "itemRW")
                .addStatement("super(flyweight)")
                .addStatement("this.itemRW = itemRW")
                .build();
        }

        private MethodSpec itemMethod()
        {
            return methodBuilder("item")
                .addModifiers(PUBLIC)
                .addParameter(typeVarO, "item")
                .returns(builderType)
                .addStatement("maxLength = Math.max(maxLength, item.sizeof())")
                .addStatement("checkLimit(itemRW.limit(), maxLimit())")
                .addStatement("limit(itemRW.limit())")
                .addStatement("fieldCount++")
                .addStatement("return this")
                .build();
        }

        private MethodSpec itemsMethod()
        {
            return methodBuilder("items")
                .addModifiers(PUBLIC)
                .returns(builderType)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "srcOffset")
                .addParameter(int.class, "length")
                .addParameter(int.class, "fieldCount")
                .addStatement("this.fieldCount = fieldCount")
                .addStatement("return this")
                .build();
        }

        private MethodSpec itemRWMethod()
        {
            return methodBuilder("itemRW")
                .addModifiers(PUBLIC)
                .returns(typeVarB)
                .addStatement("return itemRW")
                .build();
        }

        private MethodSpec fieldCountMethod()
        {
            return methodBuilder("fieldCount")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return fieldCount")
                .build();
        }

        private MethodSpec maxLengthMethod()
        {
            return methodBuilder("maxLength")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return maxLength")
                .build();
        }
    }
}
