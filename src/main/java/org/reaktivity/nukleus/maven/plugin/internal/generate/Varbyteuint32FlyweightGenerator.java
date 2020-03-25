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
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public final class Varbyteuint32FlyweightGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final BuilderClassBuilder builderClassBuilder;

    public Varbyteuint32FlyweightGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("Varbyteuint32FW"));

        this.classBuilder = classBuilder(thisName).superclass(flyweightType).addModifiers(PUBLIC, FINAL);
        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightType.nestedClass("Builder"));
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder.addField(fieldSize())
                           .addMethod(limitMethod())
                           .addMethod(valueMethod())
                           .addMethod(tryWrapMethod())
                           .addMethod(wrapMethod())
                           .addMethod(toStringMethod())
                           .addMethod(length0Method())
                           .addType(builderClassBuilder.build())
                           .build();
    }

    private FieldSpec fieldSize()
    {
        return FieldSpec.builder(int.class, "size", PRIVATE)
                .build();
    }

    private MethodSpec limitMethod()
    {
        return methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return offset() + size")
                .build();
    }

    private MethodSpec valueMethod()
    {
        return methodBuilder("value")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("final DirectBuffer buffer = buffer()")
                .addStatement("final int offset = offset()")
                .addStatement("final int limit = limit()")
                .addStatement("int value = 0")
                .addStatement("int index = 0")
                .addStatement("int multiplier = 1")
                .addStatement("int b")
                .beginControlFlow("do")
                    .addStatement("b = buffer.getByte(offset + index)")
                    .addStatement("value += (b & 0x7F) * multiplier")
                    .addStatement("multiplier *= 0x80")
                    .addStatement("index++")
                .endControlFlow("while (offset + index < limit && (b & 0x80) != 0)")
                .addStatement("return value")
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
                .beginControlFlow("if (null == super.tryWrap(buffer, offset, maxLimit) || maxLimit - offset < 1)")
                    .addStatement("return null")
                .endControlFlow()
                .addStatement("size = length0()")
                .beginControlFlow("if (size < 0 || size > 5 || limit() > maxLimit)")
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
                .addStatement("checkLimit(offset + 1, maxLimit)")
                .addStatement("size = length0()")
                .beginControlFlow("if (size < 0 || size > 5)")
                    .addStatement("throw new $T(String.format($S, offset))", IllegalArgumentException.class,
                            "varbyteuint32 value at offset %d exceeds 32 bits")
                .endControlFlow()
                .addStatement("checkLimit(limit(), maxLimit)")
                .addStatement("return this")
                .build();
    }

    private MethodSpec toStringMethod()
    {
        return methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(String.class)
                .addStatement("return Integer.toString(value())")
                .build();
    }

    private MethodSpec length0Method()
    {
        return methodBuilder("length0")
                .addModifiers(PRIVATE)
                .returns(int.class)
                .addStatement("final DirectBuffer buffer = buffer()")
                .addStatement("final int offset = offset()")
                .addStatement("final int maxPos = Math.min(offset + 5,  maxLimit())")
                .addStatement("int index = 0")
                .beginControlFlow("while (index + offset < maxPos && (buffer.getByte(index + offset) & 0x80) != 0)")
                    .addStatement("index++")
                .endControlFlow()
                .addStatement("int size = 1 + index")
                .addStatement("return size")
                .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final ClassName classType;
        private final ClassName flyweightType;

        private BuilderClassBuilder(
            ClassName flyweightType,
            ClassName builderRawType)
        {
            TypeName builderType = ParameterizedTypeName.get(builderRawType, flyweightType);

            this.flyweightType = flyweightType;
            this.classType = flyweightType.nestedClass("Builder");
            this.classBuilder = classBuilder(classType.simpleName())
                    .addModifiers(PUBLIC, STATIC, FINAL)
                    .superclass(builderType);
        }

        public TypeSpec build()
        {
            return classBuilder.addField(fieldValueSet())
                    .addMethod(constructor())
                    .addMethod(wrapMethod())
                    .addMethod(setMethod())
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
                    .addStatement("super(new $T())", flyweightType)
                    .build();
        }

        private MethodSpec wrapMethod()
        {
            return methodBuilder("wrap")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(flyweightType.nestedClass("Builder"))
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .addStatement("checkLimit(offset + 1, maxLimit)")
                    .addStatement("super.wrap(buffer, offset, maxLimit)")
                    .addStatement("this.valueSet = false")
                    .addStatement("return this")
                    .build();
        }

        private MethodSpec setMethod()
        {
            return methodBuilder("set")
                    .addModifiers(PUBLIC)
                    .returns(flyweightType.nestedClass("Builder"))
                    .addParameter(int.class, "value")
                    .beginControlFlow("if (value > 0x0FFFFFFF)")
                        .addStatement("throw new $T(String.format($S, value))", IllegalArgumentException.class,
                                "Input value %d too long")
                    .endControlFlow()
                    .addStatement("final MutableDirectBuffer buffer = buffer()")
                    .addStatement("int progress = offset()")
                    .addStatement("int varint = 0")
                    .addStatement("int i = 0")
                    .beginControlFlow("do")
                        .addStatement("int encodedByte = value % 0x80")
                        .addStatement("value /= 0x80")
                        .beginControlFlow("if (value > 0)")
                            .addStatement("encodedByte |= 0x80")
                        .endControlFlow()
                        .addStatement("varint |= ((varint & 0x80) > 0 ? encodedByte << (8 * i) : encodedByte) | varint")
                        .addStatement("buffer.putByte(progress++, (byte) (encodedByte & 0xFF))")
                        .addStatement("i++")
                    .endControlFlow("while (value > 0)")
                    .addStatement("int newLimit = progress")
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .addStatement("limit(newLimit)")
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
                        .addStatement("throw new $T($S)", IllegalArgumentException.class, "value not set")
                    .endControlFlow()
                    .addStatement("return super.build()")
                    .returns(flyweightType)
                    .build();
        }
    }
}
