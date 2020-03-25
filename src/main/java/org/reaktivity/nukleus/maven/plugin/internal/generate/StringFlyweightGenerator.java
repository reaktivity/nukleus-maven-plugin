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
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;

import java.nio.charset.Charset;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class StringFlyweightGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final BuilderClassBuilder builderClassBuilder;

    public StringFlyweightGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("StringFW"));

        this.classBuilder = classBuilder(thisName).superclass(flyweightType).addModifiers(PUBLIC, ABSTRACT);
        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightType.nestedClass("Builder"));
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder
            .addMethod(fieldSizeLengthMethod())
            .addMethod(asStringMethod())
            .addMethod(lengthMethod())
            .addType(builderClassBuilder.build())
            .build();
    }

    private MethodSpec fieldSizeLengthMethod()
    {
        return methodBuilder("fieldSizeLength")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(int.class)
            .build();
    }

    private MethodSpec asStringMethod()
    {
        return methodBuilder("asString")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(String.class)
            .build();
    }

    private MethodSpec lengthMethod()
    {
        return methodBuilder("length")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(int.class)
            .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final ClassName classType;
        private final ClassName stringType;
        private final TypeVariableName parameterType;

        private BuilderClassBuilder(
            ClassName stringType,
            ClassName builderRawType)
        {
            parameterType = TypeVariableName.get("T", stringType);
            TypeName builderType = ParameterizedTypeName.get(builderRawType, parameterType);

            this.stringType = stringType;
            this.classType = stringType.nestedClass("Builder");
            this.classBuilder = classBuilder(classType.simpleName())
                .addModifiers(PUBLIC, ABSTRACT, STATIC)
                .superclass(builderType)
                .addTypeVariable(parameterType);
        }

        public TypeSpec build()
        {
            return classBuilder
                .addMethod(constructor())
                .addMethod(setMethod())
                .addMethod(setDirectBufferMethod())
                .addMethod(setStringMethod())
                .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(parameterType, "flyweight")
                .addStatement("super(flyweight)")
                .build();
        }

        private MethodSpec setMethod()
        {
            return methodBuilder("set")
                .addModifiers(PUBLIC, ABSTRACT)
                .returns(classType)
                .addParameter(stringType, "value")
                .build();
        }

        private MethodSpec setDirectBufferMethod()
        {
            return methodBuilder("set")
                .addModifiers(PUBLIC, ABSTRACT)
                .returns(classType)
                .addParameter(DIRECT_BUFFER_TYPE, "srcBuffer")
                .addParameter(int.class, "srcOffset")
                .addParameter(int.class, "length")
                .build();
        }

        private MethodSpec setStringMethod()
        {
            return methodBuilder("set")
                .addModifiers(PUBLIC, ABSTRACT)
                .returns(classType)
                .addParameter(String.class, "value")
                .addParameter(Charset.class, "charset")
                .build();
        }
    }
}
