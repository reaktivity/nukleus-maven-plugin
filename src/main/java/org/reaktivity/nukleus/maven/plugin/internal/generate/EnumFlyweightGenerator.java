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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public final class EnumFlyweightGenerator extends ClassSpecGenerator
{
    private static Map<TypeName, String> stringValueTypeByTypeName = new HashMap<>();

    private final TypeSpec.Builder classBuilder;
    private final BuilderClassBuilder builderClassBuilder;
    private final ClassName enumTypeName;
    private final TypeName valueTypeName;
    private final TypeName unsignedValueTypeName;

    public EnumFlyweightGenerator(
        ClassName enumName,
        ClassName flyweightName,
        ClassName enumTypeName,
        TypeName valueTypeName,
        TypeName unsignedValueTypeName)
    {
        super(enumName);

        this.enumTypeName = enumTypeName;
        this.classBuilder = classBuilder(thisName).superclass(flyweightName).addModifiers(PUBLIC, FINAL);
        this.builderClassBuilder = new BuilderClassBuilder(thisName, flyweightName.nestedClass("Builder"), enumTypeName,
            valueTypeName, unsignedValueTypeName);
        this.valueTypeName = valueTypeName;
        this.unsignedValueTypeName = unsignedValueTypeName;
        stringValueTypeByTypeName = initstringValueTypeByTypeName();
    }

    @Override
    public TypeSpec generate()
    {
        if (isValueTypeString())
        {
            classBuilder.addField(stringROConstant())
                        .addMethod(stringMethod());
        }
        else
        {
            classBuilder.addField(fieldOffsetValueConstant())
                        .addField(fieldSizeValueConstant());
        }
        return classBuilder.addMethod(limitMethod())
                           .addMethod(getMethod())
                           .addMethod(tryWrapMethod())
                           .addMethod(wrapMethod())
                           .addMethod(toStringMethod())
                           .addType(builderClassBuilder.build())
                           .build();
    }

    private static Map<TypeName, String> initstringValueTypeByTypeName()
    {
        Map<TypeName, String> stringValueTypeByTypeName = new HashMap<>();
        stringValueTypeByTypeName.put(TypeName.BYTE, "Byte");
        stringValueTypeByTypeName.put(TypeName.SHORT, "Short");
        stringValueTypeByTypeName.put(TypeName.INT, "Int");
        stringValueTypeByTypeName.put(TypeName.LONG, "Long");
        return stringValueTypeByTypeName;
    }

    private boolean isValueTypeString()
    {
        return valueTypeName != null && !valueTypeName.isPrimitive();
    }

    private FieldSpec stringROConstant()
    {
        return FieldSpec.builder(valueTypeName, "stringRO", PRIVATE, FINAL)
                        .initializer("new $T()", valueTypeName)
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
        String constantType = valueTypeName == null ? "BYTE"
            : stringValueTypeByTypeName.get(unsignedValueTypeName == null ? valueTypeName :
            isTypeByte() ? TypeName.SHORT : unsignedValueTypeName).toUpperCase();
        return FieldSpec.builder(int.class, "FIELD_SIZE_VALUE", PRIVATE, STATIC, FINAL)
                .initializer(String.format("$T.SIZE_OF_%s", constantType), BIT_UTIL_TYPE)
                .build();
    }

    private MethodSpec stringMethod()
    {
        return methodBuilder("string")
                .addModifiers(PUBLIC)
                .returns(valueTypeName)
                .addStatement("return stringRO")
                .build();
    }

    private MethodSpec limitMethod()
    {
        String returnStatement =
            String.format("return %s", isValueTypeString() ? "stringRO.limit()" : "offset() + FIELD_SIZE_VALUE");
        return methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement(returnStatement)
                .build();
    }

    private MethodSpec getMethod()
    {
        String bufferType = valueTypeName == null ? "Byte"
            : stringValueTypeByTypeName.get(unsignedValueTypeName == null ? valueTypeName :
            isTypeByte() ? TypeName.SHORT : unsignedValueTypeName);
        String returnStatement = String.format("return %s", isValueTypeString() ?
            "stringRO.asString() != null ? $T.valueOf(stringRO.asString().toUpperCase()) : null" :
            String.format("$T.valueOf(buffer().get%s(offset() + FIELD_OFFSET_VALUE))", bufferType));
        return methodBuilder("get")
                .addModifiers(PUBLIC)
                .returns(enumTypeName)
                .addStatement(returnStatement, enumTypeName)
                .build();
    }

    private MethodSpec tryWrapMethod()
    {
        MethodSpec.Builder builder = methodBuilder("tryWrap");
        builder.addAnnotation(Override.class)
               .addModifiers(PUBLIC)
               .addParameter(DIRECT_BUFFER_TYPE, "buffer")
               .addParameter(int.class, "offset")
               .addParameter(int.class, "maxLimit")
               .returns(thisName);
        if (isValueTypeString())
        {
            builder.beginControlFlow("if (null == super.tryWrap(buffer, offset, maxLimit))")
                   .addStatement("return null")
                   .endControlFlow()
                   .beginControlFlow("if (null == stringRO.tryWrap(buffer, offset, maxLimit))")
                   .addStatement("return null")
                   .endControlFlow()
                   .beginControlFlow("if (limit() > maxLimit)")
                   .addStatement("return null")
                   .endControlFlow();
        }
        else
        {
            builder.beginControlFlow("if (null == super.tryWrap(buffer, offset, maxLimit) || limit() > maxLimit)")
                   .addStatement("return null")
                   .endControlFlow();
        }

        return builder.addStatement("return this")
                      .build();
    }

    private MethodSpec wrapMethod()
    {
        MethodSpec.Builder builder = methodBuilder("wrap");
        builder.addAnnotation(Override.class)
               .addModifiers(PUBLIC)
               .addParameter(DIRECT_BUFFER_TYPE, "buffer")
               .addParameter(int.class, "offset")
               .addParameter(int.class, "maxLimit")
               .returns(thisName)
               .addStatement("super.wrap(buffer, offset, maxLimit)");
        if (isValueTypeString())
        {
            builder.addStatement("stringRO.wrap(buffer, offset, maxLimit)");
        }
        return builder.addStatement("checkLimit(limit(), maxLimit)")
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
        private final TypeName valueTypeName;
        private final TypeName unsignedValueTypeName;

        private BuilderClassBuilder(
            ClassName enumName,
            ClassName builderRawType,
            ClassName enumTypeName,
            TypeName valueTypeName,
            TypeName unsignedValueTypeName)
        {
            TypeName builderType = ParameterizedTypeName.get(builderRawType, enumName);

            this.enumName = enumName;
            this.enumTypeName = enumTypeName;
            this.classType = enumName.nestedClass("Builder");
            this.classBuilder = classBuilder(classType.simpleName())
                    .addModifiers(PUBLIC, STATIC, FINAL)
                    .superclass(builderType);
            this.valueTypeName = valueTypeName;
            this.unsignedValueTypeName = unsignedValueTypeName;
        }

        public TypeSpec build()
        {
            classBuilder.addField(fieldValueSet())
                .addMethod(constructor())
                .addMethod(wrapMethod())
                .addMethod(setMethod())
                .addMethod(setEnumMethod());
            if (isValueTypeString())
            {
                classBuilder.addField(fieldStringRW());
            }
            return classBuilder.addMethod(buildMethod()).build();
        }

        private boolean isValueTypeString()
        {
            return valueTypeName != null && !valueTypeName.isPrimitive();
        }

        private FieldSpec fieldStringRW()
        {
            ClassName classType = (ClassName) valueTypeName;
            TypeName builderType = classType.nestedClass("Builder");
            return FieldSpec.builder(builderType, "stringRW", PRIVATE, FINAL)
                            .initializer("new $T.Builder()", valueTypeName)
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
            MethodSpec.Builder builder = methodBuilder("wrap");
            builder.addModifiers(PUBLIC)
                   .returns(enumName.nestedClass("Builder"))
                   .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                   .addParameter(int.class, "offset")
                   .addParameter(int.class, "maxLimit");
            if (isValueTypeString())
            {
                builder.addStatement("stringRW.wrap(buffer, offset, maxLimit)");
            }
            return builder
                    .addStatement("super.wrap(buffer, offset, maxLimit)")
                    .addStatement("return this")
                    .build();
        }

        private MethodSpec setMethod()
        {
            MethodSpec.Builder builder = methodBuilder("set");
            builder.addModifiers(PUBLIC)
                   .returns(enumName.nestedClass("Builder"))
                   .addParameter(enumName, "value");
            if (isValueTypeString())
            {
                builder.addStatement("stringRW.set(value.string())")
                       .addStatement("limit(stringRW.build().limit())");
            }
            else
            {
                builder.addStatement("int newLimit = offset() + value.sizeof()")
                       .addStatement("checkLimit(newLimit, maxLimit())")
                       .addStatement("buffer().putBytes(offset(), value.buffer(), value.offset(), value.sizeof())")
                       .addStatement("limit(newLimit)");
            }
            return builder.addStatement("valueSet = true")
                          .addStatement("return this")
                          .build();
        }

        private MethodSpec setEnumMethod()
        {
            MethodSpec.Builder builder = methodBuilder("set");
            builder.addModifiers(PUBLIC)
                   .returns(enumName.nestedClass("Builder"))
                   .addParameter(enumTypeName, "value");
            if (isValueTypeString())
            {
                builder.addParameter(Charset.class, "charset")
                       .addStatement("stringRW.set(value.value(), charset)")
                       .addStatement("limit(stringRW.build().limit())");
            }
            else
            {
                final String methodName = isParameterizedType() ? "value" : "ordinal";
                final String bufferType = isParameterizedType() ? stringValueTypeByTypeName.get(isTypeUnsignedInt() ?
                    valueTypeName.equals(TypeName.BYTE) && unsignedValueTypeName.equals(TypeName.INT) ? TypeName.SHORT :
                    unsignedValueTypeName : valueTypeName) : "Byte";
                final String castToByte = isParameterizedType() ? "" : "(byte) ";
                builder.addStatement("MutableDirectBuffer buffer = buffer()")
                       .addStatement("int offset = offset()")
                       .addStatement("int newLimit = offset + FIELD_SIZE_VALUE")
                       .addStatement("checkLimit(newLimit, maxLimit())")
                       .addStatement(String.format("buffer.put%s(offset, %svalue.%s())", bufferType, castToByte, methodName))
                       .addStatement("limit(newLimit)");
            }
            return builder.addStatement("valueSet = true")
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

        private boolean isParameterizedType()
        {
            return valueTypeName != null;
        }

        private boolean isTypeUnsignedInt()
        {
            return valueTypeName != null && unsignedValueTypeName != null;
        }
    }

    private boolean isTypeByte()
    {
        return valueTypeName.equals(TypeName.BYTE) && unsignedValueTypeName.equals(TypeName.INT);
    }
}
