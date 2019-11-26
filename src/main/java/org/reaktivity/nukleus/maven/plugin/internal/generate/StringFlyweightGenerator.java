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
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
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
            .addMethod(asStringMethod())
            .addType(builderClassBuilder.build())
            .build();
    }

    private MethodSpec asStringMethod()
    {
        return methodBuilder("asString")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(String.class)
            .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final ClassName classType;
        private final ClassName stringType;
        private final TypeName genericType;

        private BuilderClassBuilder(
            ClassName stringType,
            ClassName builderRawType)
        {
            genericType = TypeVariableName.get("T", stringType);
            TypeName builderType = ParameterizedTypeName.get(builderRawType, genericType);

            this.stringType = stringType;
            this.classType = stringType.nestedClass("Builder");
            this.classBuilder = classBuilder(classType.simpleName())
                .addModifiers(PUBLIC, ABSTRACT, STATIC)
                .superclass(builderType)
                .addTypeVariable(TypeVariableName.get("T", stringType));
        }

        public TypeSpec build()
        {
            return classBuilder
                .addField(fieldValueSet())
                .addMethod(constructor())
                .addMethod(wrapMethod())
                .addMethod(setMethod())
                .addMethod(setDirectBufferMethod())
                .addMethod(setStringMethod())
                .addMethod(buildMethod())
                .build();
        }

        private FieldSpec fieldValueSet()
        {
            return FieldSpec.builder(boolean.class, "valueSet", PRIVATE)
                .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(genericType, "flyweight")
                .addStatement("super(flyweight)")
                .build();
        }

        private MethodSpec wrapMethod()
        {
            return methodBuilder("wrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(ParameterizedTypeName.get(classType, genericType))
                .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .addStatement("super.wrap(buffer, offset, maxLimit)")
                .addStatement("this.valueSet = false")
                .addStatement("return this")
                .build();
        }

        private MethodSpec setMethod()
        {
            return methodBuilder("set")
                .addModifiers(PUBLIC)
                .returns(classType)
                .addParameter(genericType, "value")
                .addStatement("valueSet = true")
                .addStatement("return this")
                .build();
        }

        private MethodSpec setDirectBufferMethod()
        {
            return methodBuilder("set")
                .addModifiers(PUBLIC)
                .returns(classType)
                .addParameter(DIRECT_BUFFER_TYPE, "srcBuffer")
                .addParameter(int.class, "srcOffset")
                .addParameter(int.class, "length")
                .addStatement("valueSet = true")
                .addStatement("return this")
                .build();
        }

        private MethodSpec setStringMethod()
        {
            return methodBuilder("set")
                .addModifiers(PUBLIC)
                .returns(classType)
                .addParameter(String.class, "value")
                .addParameter(Charset.class, "charset")
                .addStatement("valueSet = true")
                .addStatement("return this")
                .build();
        }

        private MethodSpec buildMethod()
        {
            return methodBuilder("build")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .beginControlFlow("if (!valueSet)")
                .addStatement("set(null, $T.UTF_8)", StandardCharsets.class)
                .endControlFlow()
                .addStatement("return super.build()")
                .returns(genericType)
                .build();
        }
    }
}
