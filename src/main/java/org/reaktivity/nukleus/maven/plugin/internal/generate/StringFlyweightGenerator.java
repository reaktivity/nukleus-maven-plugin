/**
 * Copyright 2016-2017 The Reaktivity Project
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

import java.nio.charset.Charset;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public final class StringFlyweightGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final BuilderClassBuilder builderClassBuilder;

    public StringFlyweightGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("StringFW"));

        this.classBuilder = classBuilder(thisName).superclass(flyweightType).addModifiers(PUBLIC, FINAL)
                .addAnnotation(GENERATED_ANNOTATION);
        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightType.nestedClass("Builder"));
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder.addField(fieldOffsetLengthConstant())
                            .addField(fieldSizeLengthConstant())
                            .addMethod(limitMethod())
                            .addMethod(asStringMethod())
                            .addMethod(wrapMethod())
                            .addMethod(toStringMethod())
                            .addMethod(length0Method())
                            .addType(builderClassBuilder.build())
                            .build();
    }

    private FieldSpec fieldOffsetLengthConstant()
    {
        return FieldSpec.builder(int.class, "FIELD_OFFSET_LENGTH", PRIVATE, STATIC, FINAL)
                .initializer("0")
                .build();
    }

    private FieldSpec fieldSizeLengthConstant()
    {
        return FieldSpec.builder(int.class, "FIELD_SIZE_LENGTH", PRIVATE, STATIC, FINAL)
                .initializer("$T.SIZE_OF_SHORT", BIT_UTIL_TYPE)
                .build();
    }

    private MethodSpec limitMethod()
    {
        return methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return maxLimit() == offset() ? offset() : offset() + FIELD_SIZE_LENGTH + length0()")
                .build();
    }

    private MethodSpec asStringMethod()
    {
        return methodBuilder("asString")
                .addModifiers(PUBLIC)
                .returns(String.class)
                .beginControlFlow("if (maxLimit() == offset())")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("return buffer().getStringWithoutLengthUtf8(offset() + FIELD_SIZE_LENGTH, length0())")
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
                .addStatement("return maxLimit() == offset() ? \"null\" : String.format(\"\\\"%s\\\"\", asString())")
                .build();
    }

    private MethodSpec length0Method()
    {
        return methodBuilder("length0")
                .addModifiers(PRIVATE)
                .returns(int.class)
                .addStatement("return buffer().getShort(offset() + FIELD_OFFSET_LENGTH) & 0xFFFF")
                .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final ClassName classType;
        private final ClassName stringType;

        private BuilderClassBuilder(
            ClassName stringType,
            ClassName builderRawType)
        {
            TypeName builderType = ParameterizedTypeName.get(builderRawType, stringType);

            this.stringType = stringType;
            this.classType = stringType.nestedClass("Builder");
            this.classBuilder = classBuilder(classType.simpleName())
                    .addModifiers(PUBLIC, STATIC, FINAL)
                    .superclass(builderType);
        }

        public TypeSpec build()
        {
            return classBuilder.addMethod(constructor())
                    .addMethod(wrapMethod())
                    .addMethod(setMethod())
                    .addMethod(setDirectBufferMethod())
                    .addMethod(setStringMethod())
                    .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                    .addModifiers(PUBLIC)
                    .addStatement("super(new $T())", stringType)
                    .build();
        }

        private MethodSpec wrapMethod()
        {
            return methodBuilder("wrap")
                    .addModifiers(PUBLIC)
                    .returns(stringType.nestedClass("Builder"))
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .addStatement("super.wrap(buffer, offset, maxLimit)")
                    .addStatement("return this")
                    .build();
        }

        private MethodSpec setMethod()
        {
            return methodBuilder("set")
                    .addModifiers(PUBLIC)
                    .returns(stringType.nestedClass("Builder"))
                    .addParameter(stringType, "value")
                    .addStatement("buffer().putBytes(offset(), value.buffer(), value.offset(), value.sizeof())")
                    .addStatement("return this")
                    .build();
        }

        private MethodSpec setDirectBufferMethod()
        {
            return methodBuilder("set")
                    .addModifiers(PUBLIC)
                    .returns(stringType.nestedClass("Builder"))
                    .addParameter(DIRECT_BUFFER_TYPE, "srcBuffer")
                    .addParameter(int.class, "srcOffset")
                    .addParameter(int.class, "length")
                    .addStatement("buffer().putShort(offset(), (short) length)")
                    .addStatement("buffer().putBytes(offset() + 2, srcBuffer, srcOffset, length)")
                    .addStatement("return this")
                    .build();
        }

        private MethodSpec setStringMethod()
        {
            return methodBuilder("set")
                    .addModifiers(PUBLIC)
                    .returns(stringType.nestedClass("Builder"))
                    .addParameter(String.class, "value")
                    .addParameter(Charset.class, "charset")
                    .addStatement("byte[] charBytes = value.getBytes(charset)")
                    .addStatement("MutableDirectBuffer buffer = buffer()")
                    .addStatement("int offset = offset()")
                    .addStatement("buffer.putShort(offset, (short) charBytes.length)")
                    .addStatement("buffer.putBytes(offset + 2, charBytes)")
                    .addStatement("return this")
                    .build();
        }
    }
}
