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
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class VariantFWGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final TypeVariableName typeVarK;
    private final BuilderClassBuilder builderClassBuilder;

    public VariantFWGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("VariantFW"));
        typeVarK = TypeVariableName.get("K");

        this.classBuilder = classBuilder(thisName)
            .addModifiers(PUBLIC, ABSTRACT)
            .superclass(flyweightType)
            .addTypeVariable(typeVarK);
        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightType);
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder
            .addMethod(kindMethod())
            .addType(builderClassBuilder.build())
            .build();
    }

    private MethodSpec kindMethod()
    {
        return methodBuilder("kind")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(typeVarK)
            .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final TypeVariableName typeVarV;
        private final TypeVariableName typeVarK;

        private BuilderClassBuilder(
            ClassName variantType,
            ClassName flyweightType)
        {
            ClassName variantBuilderRawType = variantType.nestedClass("Builder");
            this.typeVarK = TypeVariableName.get("K");
            this.typeVarV = TypeVariableName.get("V", ParameterizedTypeName.get(variantType, typeVarK));
            TypeName flyweightBuilderType = ParameterizedTypeName.get(flyweightType.nestedClass("Builder"), typeVarV);
            this.classBuilder = classBuilder(variantBuilderRawType.simpleName())
                .addModifiers(PUBLIC, ABSTRACT, STATIC)
                .superclass(flyweightBuilderType)
                .addTypeVariable(typeVarV)
                .addTypeVariable(typeVarK);
        }

        public TypeSpec build()
        {
            return classBuilder
                .addMethod(constructor())
                .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PROTECTED)
                .addParameter(typeVarV, "flyweight")
                .addStatement("super(flyweight)")
                .build();
        }
    }
}
