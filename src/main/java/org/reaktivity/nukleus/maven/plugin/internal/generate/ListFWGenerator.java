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
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public final class ListFWGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final BuilderClassBuilder builderClassBuilder;

    public ListFWGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("ListFW"));
        this.classBuilder = classBuilder(thisName).superclass(flyweightType).addModifiers(PUBLIC, ABSTRACT);
        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightType.nestedClass("Builder"));
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder.addMethod(lengthMethod())
            .addMethod(fieldCountMethod())
            .addMethod(fieldsMethod())
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

    public MethodSpec fieldCountMethod()
    {
        return methodBuilder("fieldCount")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(int.class)
            .build();
    }

    public MethodSpec fieldsMethod()
    {
        return methodBuilder("fields")
            .addModifiers(PUBLIC, ABSTRACT)
            .returns(DIRECT_BUFFER_TYPE)
            .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final ClassName classType;
        private final ClassName listType;
        private final ClassName visitorType;
        private final TypeVariableName typeVarT;

        private BuilderClassBuilder(
            ClassName listType,
            ClassName builderRawType)
        {
            this.typeVarT = TypeVariableName.get("T");
            TypeName builderType = ParameterizedTypeName.get(builderRawType, typeVarT);
            this.listType = listType;
            this.classType = listType.nestedClass("Builder");
            this.classBuilder = classBuilder(classType.simpleName())
                .addModifiers(PUBLIC, ABSTRACT, STATIC)
                .addTypeVariable(TypeVariableName.get("T", listType))
                .superclass(builderType);
            this.visitorType = builderRawType.nestedClass("Visitor");
        }

        public TypeSpec build()
        {
            return classBuilder.addField(fieldCountField())
                .addMethod(constructor())
                .addMethod(wrapMethod())
                .addMethod(fieldMethod())
                .addMethod(fieldsMethodViaVisitor())
                .addMethod(fieldsMethodViaBuffer())
                .addMethod(fieldCountMethod())
                .build();
        }

        private FieldSpec fieldCountField()
        {
            return FieldSpec.builder(int.class, "fieldCount", PRIVATE).build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(typeVarT, "flyweight")
                .addStatement("super(flyweight)")
                .build();
        }

        private MethodSpec wrapMethod()
        {
            return methodBuilder("wrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(listType.nestedClass("Builder"))
                .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .addStatement("super.wrap(buffer, offset, maxLimit)")
                .addStatement("fieldCount = 0")
                .addStatement("return this")
                .build();
        }

        private MethodSpec fieldMethod()
        {
            return methodBuilder("field")
                .addModifiers(PUBLIC)
                .returns(listType.nestedClass("Builder"))
                .addParameter(visitorType, "visitor")
                .addStatement("int length = visitor.visit(buffer(), limit(), maxLimit())")
                .addStatement("fieldCount++")
                .addStatement("int newLimit = limit() + length")
                .addStatement("checkLimit(newLimit, maxLimit())")
                .addStatement("limit(newLimit)")
                .addStatement("return this")
                .build();
        }

        private MethodSpec fieldsMethodViaVisitor()
        {
            return methodBuilder("fields")
                .addModifiers(PUBLIC)
                .returns(listType.nestedClass("Builder"))
                .addParameter(int.class, "fieldCount")
                .addParameter(visitorType, "visitor")
                .addStatement("int length = visitor.visit(buffer(), limit(), maxLimit())")
                .addStatement("this.fieldCount += fieldCount")
                .addStatement("int newLimit = limit() + length")
                .addStatement("checkLimit(newLimit, maxLimit())")
                .addStatement("limit(newLimit)")
                .addStatement("return this")
                .build();
        }

        private MethodSpec fieldsMethodViaBuffer()
        {
            return methodBuilder("fields")
                .addModifiers(PUBLIC)
                .returns(listType.nestedClass("Builder"))
                .addParameter(int.class, "fieldCount")
                .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                .addParameter(int.class, "index")
                .addParameter(int.class, "length")
                .addStatement("this.fieldCount += fieldCount")
                .addStatement("int newLimit = limit() + length")
                .addStatement("checkLimit(newLimit, maxLimit())")
                .addStatement("buffer().putBytes(limit(), buffer, index, length)")
                .addStatement("limit(newLimit)")
                .addStatement("return this")
                .build();
        }

        private MethodSpec fieldCountMethod()
        {
            return methodBuilder("fieldCount")
                .addModifiers(PROTECTED)
                .returns(int.class)
                .addStatement("return fieldCount")
                .build();
        }
    }
}