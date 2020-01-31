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
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class VariantOfFWGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final TypeVariableName typeVarK;
    private final TypeVariableName typeVarV;
    private final BuilderClassBuilder builderClassBuilder;
    private final TypeName className;


    public VariantOfFWGenerator(
        ClassName flyweightType,
        ClassName variantFlyweightType)
    {
        super(flyweightType.peerClass("VariantOfFW"));
        typeVarK = TypeVariableName.get("K");
        typeVarV = TypeVariableName.get("V", flyweightType);

        this.classBuilder = classBuilder(thisName)
            .addModifiers(PUBLIC, ABSTRACT)
            .superclass(ParameterizedTypeName.get(variantFlyweightType, typeVarK))
            .addTypeVariable(typeVarK)
            .addTypeVariable(typeVarV);
        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightType, variantFlyweightType);
        this.className = ParameterizedTypeName.get(thisName, typeVarK, typeVarV);
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder
            .addMethod(getMethod())
            .addMethod(getAsMethod())
            .addMethod(wrapWithKindPaddingMethod())
            .addType(builderClassBuilder.build())
            .build();
    }

    private MethodSpec getMethod()
    {
        return methodBuilder("get")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(typeVarV)
            .build();
    }

    private MethodSpec getAsMethod()
    {
        return methodBuilder("getAs")
            .addModifiers(PUBLIC)
            .addParameter(typeVarK, "kind")
            .addParameter(int.class, "kindPadding")
            .addStatement("return null")
            .returns(typeVarV)
            .build();
    }

    private MethodSpec wrapWithKindPaddingMethod()
    {
        return methodBuilder("wrapWithKindPadding")
            .addModifiers(PUBLIC)
            .addParameter(DIRECT_BUFFER_TYPE, "buffer")
            .addParameter(int.class, "elementsOffset")
            .addParameter(int.class, "maxLimit")
            .addParameter(int.class, "kindPadding")
            .addStatement("return this")
            .returns(className)
            .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final TypeVariableName typeVarT;
        private final TypeVariableName typeVarV;
        private final TypeVariableName typeVarK;
        private final TypeName variantBuilderType;

        private BuilderClassBuilder(
            ClassName variantOfFlyweightType,
            ClassName flyweightType,
            ClassName variantFlyweightType)
        {
            ClassName variantOfBuilderRawType = variantOfFlyweightType.nestedClass("Builder");
            this.typeVarK = TypeVariableName.get("K");
            this.typeVarV = TypeVariableName.get("V", flyweightType);
            this.typeVarT = TypeVariableName.get("T", ParameterizedTypeName.get(variantOfFlyweightType, typeVarK, typeVarV));
            TypeName flyweightBuilderType = ParameterizedTypeName.get(variantFlyweightType.nestedClass("Builder"), typeVarT,
                typeVarK);
            this.variantBuilderType =  ParameterizedTypeName.get(variantOfBuilderRawType, typeVarT, typeVarK, typeVarV);
            this.classBuilder = classBuilder(variantOfBuilderRawType.simpleName())
                .addModifiers(PUBLIC, ABSTRACT, STATIC)
                .superclass(flyweightBuilderType)
                .addTypeVariable(typeVarT)
                .addTypeVariable(typeVarK)
                .addTypeVariable(typeVarV);
        }


        public TypeSpec build()
        {
            return classBuilder
                .addMethod(constructor())
                .addMethod(setAsMethod())
                .addMethod(setMethod())
                .addMethod(maxKindMethod())
                .addMethod(sizeMethod())
                .addMethod(kindFromLengthMethod())
                .addMethod(kindMethod())
                .addMethod(buildMethod())
                .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PROTECTED)
                .addParameter(typeVarT, "flyweight")
                .addStatement("super(flyweight)")
                .build();
        }

        private MethodSpec setAsMethod()
        {
            return methodBuilder("setAs")
                .addModifiers(PUBLIC)
                .addParameter(typeVarK, "kind")
                .addParameter(typeVarV, "value")
                .addParameter(int.class, "kindPadding")
                .returns(variantBuilderType)
                .addStatement("return this")
                .build();
        }

        private MethodSpec setMethod()
        {
            return methodBuilder("set")
                .addModifiers(PUBLIC)
                .addParameter(typeVarV, "value")
                .returns(variantBuilderType)
                .addStatement("return this")
                .build();
        }

        private MethodSpec maxKindMethod()
        {
            return methodBuilder("maxKind")
                .addModifiers(PUBLIC)
                .returns(typeVarK)
                .addStatement("return null")
                .build();
        }

        private MethodSpec sizeMethod()
        {
            return methodBuilder("size")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return 0")
                .build();
        }

        private MethodSpec kindFromLengthMethod()
        {
            return methodBuilder("kindFromLength")
                .addModifiers(PUBLIC)
                .addParameter(int.class, "length")
                .returns(typeVarK)
                .addStatement("return null")
                .build();
        }

        private MethodSpec kindMethod()
        {
            return methodBuilder("kind")
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(typeVarK, "value")
                .returns(variantBuilderType)
                .build();
        }

        private MethodSpec buildMethod()
        {
            return methodBuilder("build")
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(int.class, "maxLimit")
                .returns(typeVarT)
                .build();
        }
    }
}
