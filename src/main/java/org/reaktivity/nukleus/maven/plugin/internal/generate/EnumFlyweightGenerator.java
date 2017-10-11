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
import static java.lang.String.format;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BIT_UTIL_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public final class EnumFlyweightGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final BuilderClassBuilder builderClassBuilder;
    private final ClassName enumTypeName;

    public EnumFlyweightGenerator(
        ClassName enumName,
        ClassName flyweightName,
        ClassName enumTypeName)
    {
        super(enumName);

        this.enumTypeName = enumTypeName;
        this.classBuilder = classBuilder(thisName).superclass(flyweightName).addModifiers(PUBLIC, FINAL);
        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightName.nestedClass("Builder"), enumTypeName);
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder.addField(fieldOffsetValueConstant())
                            .addField(fieldSizeValueConstant())
                            .addMethod(limitMethod())
                            .addMethod(getMethod())
                            .addMethod(wrapMethod())
                            .addMethod(toStringMethod())
                            .addType(builderClassBuilder.build())
                            .build();
    }


    private FieldSpec fieldOffsetValueConstant()
    {
        return FieldSpec.builder(int.class, "FIELD_OFFSET_VALUE", PRIVATE, STATIC, FINAL)
                .initializer("0")
                .build();
    }

    private FieldSpec fieldSizeValueConstant()
    {
        return FieldSpec.builder(int.class, "FIELD_SIZE_VALUE", PRIVATE, STATIC, FINAL)
                .initializer("$T.SIZE_OF_BYTE", BIT_UTIL_TYPE)
                .build();
    }

    private MethodSpec limitMethod()
    {
        return methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return maxLimit() == offset() ? offset() : offset() + FIELD_SIZE_VALUE")
                .build();
    }

    private MethodSpec getMethod()
    {
        return methodBuilder("get")
                .addModifiers(PUBLIC)
                .returns(enumTypeName)
                .beginControlFlow("if (maxLimit() == offset())")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("return $T.valueOf(buffer().getByte(offset() + FIELD_OFFSET_VALUE))", enumTypeName)
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
                .addStatement("return maxLimit() == offset() ? \"null\" : get().toString()")
                .build();
    }

    private static final class BuilderClassBuilder
    {
        private final TypeSpec.Builder classBuilder;
        private final ClassName enumTypeName;
        private final ClassName classType;
        private final ClassName enumName;

        private BuilderClassBuilder(
            ClassName enumName,
            ClassName builderRawType,
            ClassName enumTypeName)
        {
            TypeName builderType = ParameterizedTypeName.get(builderRawType, enumName);

            this.enumName = enumName;
            this.enumTypeName = enumTypeName;
            this.classType = enumName.nestedClass("Builder");
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
                    .addMethod(setEnumMethod())
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
                    .addStatement("super(new $T())", enumName)
                    .build();
        }

        private MethodSpec wrapMethod()
        {
            return methodBuilder("wrap")
                    .addModifiers(PUBLIC)
                    .returns(enumName.nestedClass("Builder"))
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
                    .returns(enumName.nestedClass("Builder"))
                    .addParameter(enumName, "value")
                    .addStatement("int newLimit = offset() + value.sizeof()")
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .addStatement("buffer().putBytes(offset(), value.buffer(), value.offset(), value.sizeof())")
                    .addStatement("limit(newLimit)")
                    .addStatement("valueSet = true")
                    .addStatement("return this")
                    .build();
        }

        private MethodSpec setEnumMethod()
        {
            return methodBuilder("set")
                    .addModifiers(PUBLIC)
                    .returns(enumName.nestedClass("Builder"))
                    .addParameter(enumTypeName, "value")
                    .addStatement("MutableDirectBuffer buffer = buffer()")
                    .addStatement("int offset = offset()")
                    .addStatement("int newLimit = offset + BitUtil.SIZE_OF_BYTE")
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .addStatement("buffer.putByte(offset, (byte) value.ordinal())")
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
                    .addStatement("throw new IllegalStateException($S)",
                                  format("%s not set", enumTypeName.simpleName()))
                    .endControlFlow()
                    .addStatement("return super.build()")
                    .returns(enumName)
                    .build();
        }
    }
}
