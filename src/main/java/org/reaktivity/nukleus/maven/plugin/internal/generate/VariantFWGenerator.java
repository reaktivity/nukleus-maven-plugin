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

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.interfaceBuilder;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class VariantFWGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder interfaceBuilder;
    private final TypeVariableName typeVarO;
    private final TypeVariableName typeVarK;
    private final BuilderInterfaceBuilder builderInterfaceBuilder;

    public VariantFWGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("VariantFW"));
        typeVarO = TypeVariableName.get("O", flyweightType);
        typeVarK = TypeVariableName.get("K");

        this.interfaceBuilder = interfaceBuilder(thisName)
            .addModifiers(PUBLIC)
            .addTypeVariable(typeVarO)
            .addTypeVariable(typeVarK);
        this.builderInterfaceBuilder = new BuilderInterfaceBuilder(thisName, flyweightType);
    }

    @Override
    public TypeSpec generate()
    {
        return interfaceBuilder
            .addMethod(kindMethod())
            .addMethod(getMethod())
            .addMethod(getAsMethod())
            .addMethod(wrapArrayElementMethod())
            .addType(builderInterfaceBuilder.build())
            .build();
    }

    private MethodSpec kindMethod()
    {
        return methodBuilder("kind")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(typeVarK)
            .build();
    }

    private MethodSpec getMethod()
    {
        return methodBuilder("get")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(typeVarO)
            .build();
    }

    private MethodSpec getAsMethod()
    {
        return methodBuilder("getAs")
            .addModifiers(PUBLIC, DEFAULT)
            .addParameter(typeVarK, "kind")
            .addParameter(int.class, "kindPadding")
            .addStatement("return null")
            .returns(typeVarO)
            .build();
    }

    private MethodSpec wrapArrayElementMethod()
    {
        return methodBuilder("wrapArrayElement")
            .addModifiers(PUBLIC, DEFAULT)
            .addParameter(DIRECT_BUFFER_TYPE, "buffer")
            .addParameter(int.class, "elementsOffset")
            .addParameter(int.class, "maxLimit")
            .addParameter(int.class, "kindPadding")
            .addStatement("return this")
            .returns(thisName)
            .build();
    }

    private static final class BuilderInterfaceBuilder
    {
        private final TypeSpec.Builder interfaceBuilder;
        private final ClassName builderType;
        private final TypeVariableName typeVarT;
        private final TypeVariableName typeVarO;
        private final TypeVariableName typeVarK;

        private BuilderInterfaceBuilder(
            ClassName variantType,
            ClassName flyweightType)
        {
            this.typeVarO = TypeVariableName.get("O", flyweightType);
            this.typeVarK = TypeVariableName.get("K");
            this.typeVarT = TypeVariableName.get("T", ParameterizedTypeName.get(variantType, typeVarO, typeVarK));

            this.builderType = variantType.nestedClass("Builder");
            this.interfaceBuilder = interfaceBuilder(builderType.simpleName())
                .addModifiers(PUBLIC, STATIC)
                .addTypeVariable(typeVarT)
                .addTypeVariable(typeVarO)
                .addTypeVariable(typeVarK);
        }

        public TypeSpec build()
        {
            return interfaceBuilder
                .addMethod(setAsMethod())
                .addMethod(maxKindMethod())
                .addMethod(sizeMethod())
                .addMethod(kindFromLengthMethod())
                .addMethod(kindMethod())
                .addMethod(buildMethod())
                .build();
        }

        private MethodSpec setAsMethod()
        {
            return methodBuilder("setAs")
                .addModifiers(PUBLIC, DEFAULT)
                .addParameter(typeVarK, "kind")
                .addParameter(typeVarO, "value")
                .addParameter(int.class, "kindPadding")
                .returns(builderType)
                .addStatement("return this")
                .build();
        }

        private MethodSpec maxKindMethod()
        {
            return methodBuilder("maxKind")
                .addModifiers(PUBLIC, DEFAULT)
                .returns(typeVarK)
                .addStatement("return null")
                .build();
        }

        private MethodSpec sizeMethod()
        {
            return methodBuilder("size")
                .addModifiers(PUBLIC, DEFAULT)
                .returns(int.class)
                .addStatement("return 0")
                .build();
        }

        private MethodSpec kindFromLengthMethod()
        {
            return methodBuilder("kindFromLength")
                .addModifiers(PUBLIC, DEFAULT)
                .addParameter(int.class, "length")
                .returns(typeVarK)
                .addStatement("return null")
                .build();
        }

        private MethodSpec kindMethod()
        {
            return methodBuilder("kind")
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(typeVarK, "kind")
                .returns(builderType)
                .build();
        }

        private MethodSpec buildMethod()
        {
            return methodBuilder("build")
                .addModifiers(PUBLIC, DEFAULT)
                .addParameter(int.class, "maxLimit")
                .returns(typeVarT)
                .addStatement("return null")
                .build();
        }
    }
}
