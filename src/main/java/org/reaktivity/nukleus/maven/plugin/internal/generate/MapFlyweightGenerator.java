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
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;

import java.util.function.Consumer;
import java.util.function.Function;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class MapFlyweightGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final TypeVariableName typeVarK;
    private final TypeVariableName typeVarV;
    private final BuilderClassBuilder builderClassBuilder;

    public MapFlyweightGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("MapFW"));
        this.typeVarK = TypeVariableName.get("K", flyweightType);
        this.typeVarV = TypeVariableName.get("V", flyweightType);
        this.classBuilder = classBuilder(thisName)
            .superclass(flyweightType)
            .addModifiers(PUBLIC, ABSTRACT)
            .addTypeVariable(typeVarK)
            .addTypeVariable(typeVarV);

        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightType);
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder
            .addMethod(lengthMethod())
            .addMethod(fieldCountMethod())
            .addMethod(forEachMethod())
            .addMethod(entriesMethod())
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
        TypeName parameterizedConsumerType = ParameterizedTypeName.get(ClassName.get(Consumer.class), typeVarV);
        TypeName parameterizedFunctionType = ParameterizedTypeName.get(ClassName.get(Function.class), typeVarK,
            parameterizedConsumerType);
        return methodBuilder("forEach")
            .addModifiers(PUBLIC, ABSTRACT)
            .addParameter(parameterizedFunctionType, "consumer")
            .build();
    }

    private MethodSpec entriesMethod()
    {
        return methodBuilder("entries")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(DIRECT_BUFFER_TYPE)
            .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final TypeVariableName typeVarT;
        private final TypeVariableName typeVarKB;
        private final TypeVariableName typeVarVB;

        private final TypeName parameterizedBuilderType;

        private BuilderClassBuilder(
            ClassName mapType,
            ClassName flyweightType)
        {
            ClassName builderType = mapType.nestedClass("Builder");
            ClassName flyweightBuilderType = flyweightType.nestedClass("Builder");

            TypeVariableName typeVarK = TypeVariableName.get("K", flyweightType);
            this.typeVarKB = TypeVariableName.get("KB", ParameterizedTypeName.get(flyweightBuilderType, typeVarK));
            TypeVariableName typeVarV = TypeVariableName.get("V", flyweightType);
            this.typeVarVB = TypeVariableName.get("VB", ParameterizedTypeName.get(flyweightBuilderType, typeVarV));
            this.typeVarT = TypeVariableName.get("T", mapType);
            this.parameterizedBuilderType = ParameterizedTypeName.get(builderType, typeVarT, typeVarK, typeVarV, typeVarKB,
                typeVarVB);

            this.classBuilder = classBuilder(builderType.simpleName())
                .addModifiers(PUBLIC, ABSTRACT, STATIC)
                .superclass(ParameterizedTypeName.get(flyweightBuilderType, typeVarT))
                .addTypeVariable(typeVarT)
                .addTypeVariable(typeVarK)
                .addTypeVariable(typeVarV)
                .addTypeVariable(typeVarKB)
                .addTypeVariable(typeVarVB);
        }

        public TypeSpec build()
        {
            return classBuilder
                .addField(fieldCountField())
                .addField(keyRWField())
                .addField(valueRWField())
                .addMethod(constructor())
                .addMethod(entryMethod())
                .addMethod(entriesMethod())
                .addMethod(fieldCountMethod())
                .build();
        }

        private FieldSpec fieldCountField()
        {
            return FieldSpec.builder(int.class, "fieldCount", PRIVATE).build();
        }

        private FieldSpec keyRWField()
        {
            return FieldSpec.builder(typeVarKB, "keyRW", PROTECTED, FINAL).build();
        }

        private FieldSpec valueRWField()
        {
            return FieldSpec.builder(typeVarVB, "valueRW", PROTECTED, FINAL).build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(typeVarT, "flyweight")
                .addParameter(typeVarKB, "keyRW")
                .addParameter(typeVarVB, "valueRW")
                .addStatement("super(flyweight)")
                .addStatement("this.keyRW = keyRW")
                .addStatement("this.valueRW = valueRW")
                .build();
        }

        private MethodSpec entryMethod()
        {
            ClassName consumerRawType = ClassName.get(Consumer.class);
            TypeName consumerKeyType = ParameterizedTypeName.get(consumerRawType, typeVarKB);
            TypeName consumerValueType = ParameterizedTypeName.get(consumerRawType, typeVarVB);

            return methodBuilder("entry")
                .addModifiers(PUBLIC)
                .returns(parameterizedBuilderType)
                .addParameter(consumerKeyType, "key")
                .addParameter(consumerValueType, "value")
                .addStatement("keyRW.wrap(buffer(), limit(), maxLimit())")
                .addStatement("key.accept(keyRW)")
                .addStatement("checkLimit(keyRW.limit(), maxLimit())")
                .addStatement("limit(keyRW.limit())")
                .addStatement("fieldCount++")
                .addStatement("valueRW.wrap(buffer(), limit(), maxLimit())")
                .addStatement("value.accept(valueRW)")
                .addStatement("checkLimit(valueRW.limit(), maxLimit())")
                .addStatement("limit(valueRW.limit())")
                .addStatement("fieldCount++")
                .addStatement("return this")
                .build();
        }

        private MethodSpec entriesMethod()
        {
            return methodBuilder("entries")
                .addModifiers(PUBLIC)
                .returns(parameterizedBuilderType)
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "srcOffset")
                .addParameter(int.class, "length")
                .addParameter(int.class, "fieldCount")
                .addStatement("this.fieldCount = fieldCount")
                .addStatement("return this")
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
    }
}
