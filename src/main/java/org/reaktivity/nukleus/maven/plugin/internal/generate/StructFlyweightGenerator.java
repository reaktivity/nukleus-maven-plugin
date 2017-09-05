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
import static java.util.Collections.unmodifiableMap;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BIT_UTIL_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BYTE_ARRAY;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.UNSAFE_BUFFER_TYPE;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.FieldSpec.Builder;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public final class StructFlyweightGenerator extends ClassSpecGenerator
{
    private final String baseName;
    private final TypeSpec.Builder builder;
    private final TypeIdGenerator typeId;
    private final MemberFieldGenerator memberField;
    private final MemberSizeConstantGenerator memberSizeConstant;
    private final MemberOffsetConstantGenerator memberOffsetConstant;
    private final MemberAccessorGenerator memberAccessor;
    private final WrapMethodGenerator wrapMethod;
    private final LimitMethodGenerator limitMethod;
    private final ToStringMethodGenerator toStringMethod;
    private final BuilderClassGenerator builderClass;

    public StructFlyweightGenerator(
        ClassName structName,
        ClassName flyweightName,
        String baseName)
    {
        super(structName);

        this.baseName = baseName;
        this.builder = classBuilder(structName).superclass(flyweightName).addModifiers(PUBLIC, FINAL)
                .addAnnotation(GENERATED_ANNOTATION);
        this.typeId = new TypeIdGenerator(structName, builder);
        this.memberSizeConstant = new MemberSizeConstantGenerator(structName, builder);
        this.memberOffsetConstant = new MemberOffsetConstantGenerator(structName, builder);
        this.memberField = new MemberFieldGenerator(structName, builder);
        this.memberAccessor = new MemberAccessorGenerator(structName, builder);
        this.wrapMethod = new WrapMethodGenerator();
        this.limitMethod = new LimitMethodGenerator();
        this.toStringMethod = new ToStringMethodGenerator();
        this.builderClass = new BuilderClassGenerator(structName, flyweightName);
    }

    public StructFlyweightGenerator typeId(
        int typeId)
    {
        this.typeId.typeId(typeId);
        return this;
    }

    public StructFlyweightGenerator addMember(
        String name,
        TypeName type,
        TypeName unsignedType,
        int size,
        String sizeName,
        Object defaultValue)
    {
        memberOffsetConstant.addMember(name, type, unsignedType);
        memberSizeConstant.addMember(name, type, unsignedType);
        memberField.addMember(name, type, unsignedType);
        memberAccessor.addMember(name, type, unsignedType);

        limitMethod.addMember(name, type, unsignedType);
        wrapMethod.addMember(name, type, unsignedType, size, sizeName);
        toStringMethod.addMember(name, type, unsignedType);
        builderClass.addMember(name, type, unsignedType, size, sizeName, defaultValue);

        return this;
    }

    @Override
    public TypeSpec generate()
    {
        typeId.build();
        memberOffsetConstant.build();
        memberSizeConstant.build();
        memberField.build();
        memberAccessor.build();

        return builder.addMethod(wrapMethod.generate())
                      .addMethod(limitMethod.generate())
                      .addMethod(toStringMethod.generate())
                      .addType(builderClass.generate())
                      .build();
    }

    private static final class TypeIdGenerator extends ClassSpecMixinGenerator
    {
        private int typeId;

        private TypeIdGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public void typeId(
            int typeId)
        {
            this.typeId = typeId;
        }

        @Override
        public TypeSpec.Builder build()
        {
            if (typeId != 0)
            {
                builder.addField(FieldSpec.builder(int.class, "TYPE_ID", PUBLIC, STATIC, FINAL)
                        .initializer("$L", String.format("0x%08x", typeId))
                        .build());

                builder.addMethod(methodBuilder("typeId")
                        .addModifiers(PUBLIC)
                        .returns(int.class)
                        .addStatement("return TYPE_ID")
                        .build());
            }
            return builder;
        }
    }

    private static final class MemberSizeConstantGenerator extends ClassSpecMixinGenerator
    {
        private static final Map<TypeName, String> SIZEOF_BY_NAME = initSizeofByName();

        private MemberSizeConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MemberSizeConstantGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType)
        {
            if (type.isPrimitive())
            {
                builder.addField(
                        FieldSpec.builder(int.class, size(name), PRIVATE, STATIC, FINAL)
                                 .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, SIZEOF_BY_NAME.get(type))
                                 .build());
            }
            return this;
        }

        private static Map<TypeName, String> initSizeofByName()
        {
            Map<TypeName, String> sizeofByName = new HashMap<>();
            sizeofByName.put(TypeName.BOOLEAN, "BOOLEAN");
            sizeofByName.put(TypeName.BYTE, "BYTE");
            sizeofByName.put(TypeName.CHAR, "CHAR");
            sizeofByName.put(TypeName.SHORT, "SHORT");
            sizeofByName.put(TypeName.INT, "INT");
            sizeofByName.put(TypeName.FLOAT, "FLOAT");
            sizeofByName.put(TypeName.LONG, "LONG");
            sizeofByName.put(TypeName.DOUBLE, "DOUBLE");
            return sizeofByName;
        }
    }

    private static final class MemberOffsetConstantGenerator extends ClassSpecMixinGenerator
    {
        private String previousName;

        private MemberOffsetConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MemberOffsetConstantGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType)
        {
            builder.addField(
                    FieldSpec.builder(int.class, offset(name), PUBLIC, STATIC, FINAL)
                             .initializer((previousName == null)
                                     ? "0" : String.format("%s + %s", offset(previousName), size(previousName)))
                             .build());

            previousName = type.isPrimitive() ? name : null;

            return this;
        }
    }

    private static final class MemberFieldGenerator extends ClassSpecMixinGenerator
    {
        private MemberFieldGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MemberFieldGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType)
        {
            if (!type.isPrimitive())
            {
                String fieldRO = String.format("%sRO", name);
                Builder fieldBuilder = FieldSpec.builder(type, fieldRO, PRIVATE, FINAL);

                if (TypeNames.DIRECT_BUFFER_TYPE.equals(type))
                {
                    fieldBuilder.initializer("new $T(new byte[0])", UNSAFE_BUFFER_TYPE);
                }
                else if (type instanceof ParameterizedTypeName)
                {
                    ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
                    TypeName typeArgument = parameterizedType.typeArguments.get(0);
                    fieldBuilder.initializer("new $T(new $T())", type, typeArgument);
                }
                else
                {
                    fieldBuilder.initializer("new $T()", type);
                }

                builder.addField(fieldBuilder.build());
            }
            return this;
        }
    }

    private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
    {
        private static final Map<TypeName, String> GETTER_NAMES;

        static
        {
            Map<TypeName, String> getterNames = new HashMap<>();
            getterNames.put(TypeName.BYTE, "getByte");
            getterNames.put(TypeName.CHAR, "getChar");
            getterNames.put(TypeName.SHORT, "getShort");
            getterNames.put(TypeName.FLOAT, "getFloat");
            getterNames.put(TypeName.INT, "getInt");
            getterNames.put(TypeName.DOUBLE, "getDouble");
            getterNames.put(TypeName.LONG, "getLong");
            GETTER_NAMES = unmodifiableMap(getterNames);
        }

        private String anchorName;
        private TypeName anchorType;

        private MemberAccessorGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MemberAccessorGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType)
        {
            TypeName publicType = (unsignedType != null) ? unsignedType : type;

            CodeBlock.Builder codeBlock = CodeBlock.builder();

            if (type.isPrimitive())
            {
                String getterName = GETTER_NAMES.get(type);
                if (getterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }

                codeBlock.add("$[").add("return ");

                if (publicType != type)
                {
                    codeBlock.add("($T)(", publicType);
                }

                if (anchorName != null)
                {
                    if (DIRECT_BUFFER_TYPE.equals(anchorType))
                    {
                        codeBlock.add("buffer().$L($L().capacity() + $L", getterName, anchorName, offset(name));
                    }
                    else
                    {
                        codeBlock.add("buffer().$L($L().limit() + $L", getterName, anchorName, offset(name));
                    }
                }
                else
                {
                    codeBlock.add("buffer().$L(offset() + $L", getterName, offset(name));
                }

                if (publicType != type)
                {
                    if (type == TypeName.BYTE)
                    {
                        codeBlock.add(") & 0xFF)");
                    }
                    else if (type == TypeName.SHORT)
                    {
                        codeBlock.add(", $T.BIG_ENDIAN) & 0xFFFF)", ByteOrder.class);
                    }
                    else if (type == TypeName.INT)
                    {
                        codeBlock.add(", $T.BIG_ENDIAN) & 0xFFFF_FFFF)", ByteOrder.class);
                    }
                    else
                    {
                        codeBlock.add(")");
                    }
                }
                else
                {
                    codeBlock.add(")");
                }

                codeBlock.add(";\n$]");
            }
            else
            {
                if (DIRECT_BUFFER_TYPE.equals(type))
                {
                    MethodSpec.Builder consumerMethod = methodBuilder(name)
                            .addModifiers(PUBLIC)
                            .addParameter(IntBinaryOperator.class, "accessor")
                            .returns(type);

                    if (anchorName != null)
                    {
                        consumerMethod.addStatement("accessor.applyAsInt($L().limit() + $L, $LRO.capacity())",
                                anchorName, offset(name), name);
                    }
                    else
                    {
                        consumerMethod.addStatement("accessor.applyAsInt(offset() + $L, $LRO.capacity())", offset(name), name);
                    }

                    builder.addMethod(consumerMethod
                            .addStatement("return $LRO", name)
                            .build());
                }

                codeBlock.addStatement("return $LRO", name);

                anchorName = name;
                anchorType = type;
            }

            builder.addMethod(methodBuilder(name)
                    .addModifiers(PUBLIC)
                    .returns(publicType)
                    .addCode(codeBlock.build())
                    .build());

            return this;
        }
    }

    private final class LimitMethodGenerator extends MethodSpecGenerator
    {
        private String anchorName;
        private TypeName anchorType;
        private String lastName;
        private TypeName lastType;

        private LimitMethodGenerator()
        {
            super(methodBuilder("limit")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(int.class));
        }

        public LimitMethodGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType)
        {
            if (!type.isPrimitive())
            {
                anchorName = name;
                anchorType = type;
            }

            lastName = name;
            lastType = type;

            return this;
        }

        @Override
        public MethodSpec generate()
        {
            if (lastName == null)
            {
                builder.addStatement("return offset()");
            }
            else if (anchorName != null)
            {
                if (lastType.isPrimitive())
                {
                    if (TypeNames.DIRECT_BUFFER_TYPE.equals(anchorType))
                    {
                        builder.addStatement("return $L().capacity() + $L + $L", anchorName, offset(lastName), size(lastName));
                    }
                    else
                    {
                        builder.addStatement("return $L().limit() + $L + $L", anchorName, offset(lastName), size(lastName));
                    }
                }
                else if (TypeNames.DIRECT_BUFFER_TYPE.equals(lastType))
                {
                    builder.addStatement("return offset() + $L + $L().capacity()", offset(lastName), lastName);
                }
                else
                {
                    builder.addStatement("return $L().limit()", lastName);
                }
            }
            else
            {
                builder.addStatement("return offset() + $L + $L", offset(lastName), size(lastName));
            }

            return builder.build();
        }

    }

    private final class WrapMethodGenerator extends MethodSpecGenerator
    {
        private String anchorName;

        private WrapMethodGenerator()
        {
            super(methodBuilder("wrap")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .returns(thisName)
                    .addStatement("super.wrap(buffer, offset, maxLimit)"));
        }

        public WrapMethodGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            int size,
            String sizeName)
        {
            if (DIRECT_BUFFER_TYPE.equals(type))
            {
                builder.addStatement("$LRO.wrap(buffer, offset + $L, maxLimit - (offset + $L))",
                        name, offset(name), offset(name));
            }
            else if (!type.isPrimitive())
            {
                if (anchorName != null)
                {
                    if (size >= 0)
                    {
                        builder.addStatement("$LRO.wrap(buffer, $LRO.limit() + $L, $LRO.limit() + $L + $L)",
                                name, anchorName, offset(name), anchorName, offset(name), size);
                    }
                    else if (sizeName != null)
                    {
                        builder.addStatement("$LRO.wrap(buffer, $LRO.limit() + $L, $LRO.limit() + $L + $L())",
                                name, anchorName, offset(name), anchorName, offset(name), sizeName);
                    }
                    else
                    {
                        builder.addStatement("$LRO.wrap(buffer, $LRO.limit() + $L, maxLimit)",
                                name, anchorName, offset(name));
                    }
                }
                else
                {
                    if (size >= 0)
                    {
                        builder.addStatement("$LRO.wrap(buffer, offset + $L, offset + $L + $L)",
                                name, offset(name), offset(name), size);
                    }
                    else if (sizeName != null)
                    {
                        builder.addStatement("$LRO.wrap(buffer, offset + $L, offset + $L + $L())",
                                name, offset(name), offset(name), sizeName);
                    }
                    else
                    {
                        builder.addStatement("$LRO.wrap(buffer, offset + $L, maxLimit)",
                                name, offset(name));
                    }
                }
                anchorName = name;
            }
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            return builder.addStatement("checkLimit(limit(), maxLimit)")
                          .addStatement("return this")
                          .build();
        }
    }

    private final class ToStringMethodGenerator extends MethodSpecGenerator
    {
        private final List<String> formats = new LinkedList<>();
        private final List<String> args = new LinkedList<>();

        private ToStringMethodGenerator()
        {
            super(methodBuilder("toString")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(String.class));
        }

        public ToStringMethodGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType)
        {
            formats.add(String.format("%s=%%%s", name, type.isPrimitive() ? "d" : "s"));
            if (type instanceof ClassName && isStringType((ClassName) type))
            {
                args.add(String.format("%sRO.asString()", name));
            }
            else
            {
                args.add(String.format("%s()", name));
            }
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            String typeName = constant(baseName);
            if (formats.isEmpty())
            {
                builder.addStatement("return $S", typeName);
            }
            else
            {
                String format = String.format("%s [%s]", typeName, String.join(", ", formats));
                builder.addStatement("return String.format($S, $L)", format, String.join(", ", args));
            }
            return builder.build();
        }

    }

    private static final class BuilderClassGenerator extends ClassSpecGenerator
    {
        private final TypeSpec.Builder builder;
        private final ClassName structType;
        private final MemberConstantGenerator memberConstant;
        private final MemberFieldGenerator memberField;
        private final MemberAccessorGenerator memberAccessor;
        private final MemberMutatorGenerator memberMutator;
        private String priorDefaulted;
        private boolean priorDefaultedIsPrimitive;

        private BuilderClassGenerator(
            ClassName structType,
            ClassName flyweightType)
        {
            this(structType.nestedClass("Builder"), flyweightType.nestedClass("Builder"), structType);
        }

        private BuilderClassGenerator(
            ClassName thisType,
            ClassName builderRawType,
            ClassName structType)
        {
            super(thisType);
            this.builder = classBuilder(thisType.simpleName())
                    .addModifiers(PUBLIC, STATIC, FINAL)
                    .superclass(ParameterizedTypeName.get(builderRawType, structType));
            this.structType = structType;
            this.memberConstant = new MemberConstantGenerator(thisType, builder);
            this.memberField = new MemberFieldGenerator(thisType, builder);
            this.memberAccessor = new MemberAccessorGenerator(thisType, builder);
            this.memberMutator = new MemberMutatorGenerator(thisType, builder);
        }

        private void addMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            int size,
            String sizeName,
            Object defaultValue)
        {
            memberConstant.addMember(name, type, unsignedType, defaultValue);
            memberField.addMember(name, type, unsignedType);
            memberAccessor.addMember(name, type, unsignedType, size, sizeName, priorDefaulted, priorDefaultedIsPrimitive);
            memberMutator.addMember(name, type, unsignedType, size, sizeName, priorDefaulted, priorDefaultedIsPrimitive);
            if (defaultValue != null || type instanceof ClassName && !isStringType((ClassName) type))
            {
                priorDefaulted = name;
                priorDefaultedIsPrimitive = type.isPrimitive();
            }
            else
            {
                priorDefaulted = null;
            }
        }

        @Override
        public TypeSpec generate()
        {
            memberConstant.build();
            memberField.build();
            memberAccessor.build();
            memberMutator.build();
            return builder.addMethod(constructor())
                          .addMethod(wrapMethod())
                          .addMethod(buildMethod(priorDefaulted, priorDefaultedIsPrimitive))
                          .addMethod(checkFieldNotSetMethod())
                          .addMethod(checkFieldsSetMethod())
                          .build();
        }

        private MethodSpec constructor()
        {
            return constructorBuilder()
                    .addModifiers(PUBLIC)
                    .addStatement("super(new $T())", structType)
                    .build();
        }

        private MethodSpec wrapMethod()
        {
            return methodBuilder("wrap")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .returns(thisName)
                    .addStatement("fieldsSet.clear()")
                    .addStatement("super.wrap(buffer, offset, maxLimit)")
                    .addStatement("limit(offset)")
                    .addStatement("return this")
                    .build();
        }

        private MethodSpec buildMethod(String priorFieldIfDefaulted, boolean priorDefaultedIsPrimitive)
        {
            MethodSpec.Builder builder = methodBuilder("build")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(structType);
            if (priorFieldIfDefaulted != null)
            {
                builder.beginControlFlow("if (!fieldsSet.get($L))", index(priorFieldIfDefaulted));
                if (priorDefaultedIsPrimitive)
                {
                    builder.addStatement("$L($L)", priorFieldIfDefaulted, defaultName(priorFieldIfDefaulted));
                }
                else
                {
                    //  Attempt to default the entire object. This will fail if it has any required fields.
                    builder.addStatement("$L(b -> { });", priorFieldIfDefaulted);
                }
                builder.endControlFlow();
            }
            return builder.addStatement("checkFieldsSet(0, FIELD_COUNT)")
                          .addStatement("fieldsSet.clear()")
                          .addStatement("return super.build()")
                          .build();
        }

        private MethodSpec checkFieldNotSetMethod()
        {
            return methodBuilder("checkFieldNotSet")
                    .addModifiers(PRIVATE)
                    .addParameter(int.class, "index")
                    .beginControlFlow("if (fieldsSet.get(index))")
                    .addStatement("throw new IllegalStateException(String.format($S, FIELD_NAMES[index]))",
                                  "Field \"%s\" has already been set")
                    .endControlFlow()
                    .build();
        }

        private MethodSpec checkFieldsSetMethod()
        {
            return methodBuilder("checkFieldsSet")
                    .addModifiers(PRIVATE)
                    .addParameter(int.class, "fromIndex")
                    .addParameter(int.class, "toIndex")
                    .addStatement("int fieldNotSet = fromIndex - 1")
                    .beginControlFlow("do")
                    .addStatement("fieldNotSet = fieldsSet.nextClearBit(fieldNotSet + 1)")
                    .endControlFlow("while (fieldNotSet < toIndex && FIELDS_WITH_DEFAULTS.get(fieldNotSet))")
                    .beginControlFlow("if (fieldNotSet < toIndex)")
                    .addStatement("throw new IllegalStateException(String.format($S, FIELD_NAMES[fieldNotSet]))",
                                  "Required field \"%s\" is not set")
                    .endControlFlow()
                    .build();
        }

        private static final class MemberConstantGenerator extends ClassSpecMixinGenerator
        {
            private int nextIndex;
            private CodeBlock.Builder fieldsWithDefaultsInitializer = CodeBlock.builder();
            private List<String> fieldNames = new ArrayList<>();

            private MemberConstantGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
            }

            public MemberConstantGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                Object defaultValue)
            {
                builder.addField(
                        FieldSpec.builder(int.class, index(name), PRIVATE, STATIC, FINAL)
                                 .initializer(Integer.toString(nextIndex++))
                                 .build());
                fieldNames.add(name);
                if (defaultValue != null)
                {
                    TypeName publicType = (unsignedType != null) ? unsignedType : type;
                    builder.addField(
                            FieldSpec.builder(publicType, defaultName(name), PRIVATE, STATIC, FINAL)
                                     .initializer(defaultValue.toString())
                                     .build());
                    fieldsWithDefaultsInitializer.addStatement("set($L)", index(name));
                }
                else if (type instanceof ClassName && "OctetsFW".equals(((ClassName) type).simpleName()))
                {
                    fieldsWithDefaultsInitializer.addStatement("set($L)", index(name));
                }
                return this;
            }

            @Override
            public TypeSpec.Builder build()
            {
                builder.addField(
                        FieldSpec.builder(int.class, "FIELD_COUNT", PRIVATE, STATIC, FINAL)
                                 .initializer(Integer.toString(nextIndex))
                                 .build())
                        .addField(
                        FieldSpec.builder(BitSet.class, "FIELDS_WITH_DEFAULTS", PRIVATE, STATIC, FINAL)
                                 .addAnnotation(SUPPRESS_WARNINGS_SERIAL)
                                 .initializer(CodeBlock.builder()
                                             .add("new BitSet(FIELD_COUNT)")
                                             .beginControlFlow(" ")
                                             .beginControlFlow(" ")
                                             .add(fieldsWithDefaultsInitializer.build())
                                             .endControlFlow()
                                             .endControlFlow()
                                             .build())
                                 .build())
                        .addField(
                        FieldSpec.builder(String[].class, "FIELD_NAMES", PRIVATE, STATIC, FINAL)
                                 .initializer("{\n  \"" + String.join("\",\n  \"", fieldNames) + "\"\n}")
                                 .build())
                        .addField(
                        FieldSpec.builder(BitSet.class, "fieldsSet", PRIVATE, FINAL)
                                 .initializer("new BitSet(FIELD_COUNT)")
                                 .build());
                return super.build();
            }
        }


        private static final class MemberFieldGenerator extends ClassSpecMixinGenerator
        {
            private MemberFieldGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
            }

            public MemberFieldGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType)
            {
                if (!type.isPrimitive())
                {
                    String fieldRW = String.format("%sRW", name);

                    if (TypeNames.DIRECT_BUFFER_TYPE.equals(type))
                    {
                        // skip
                    }
                    else if (type instanceof ParameterizedTypeName)
                    {
                        ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
                        ClassName rawType = parameterizedType.rawType;
                        ClassName itemType = (ClassName) parameterizedType.typeArguments.get(0);
                        ClassName builderRawType = rawType.nestedClass("Builder");
                        ClassName itemBuilderType = itemType.nestedClass("Builder");
                        ParameterizedTypeName builderType = ParameterizedTypeName.get(builderRawType, itemBuilderType, itemType);

                        builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                                .initializer("new $T(new $T(), new $T())", builderType, itemBuilderType, itemType)
                                .build());
                    }
                    else if (type instanceof ClassName)
                    {
                        ClassName classType = (ClassName) type;
                        TypeName builderType = classType.nestedClass("Builder");
                        builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                                .initializer("new $T()", builderType)
                                .build());
                    }
                    else
                    {
                        throw new IllegalArgumentException("Unsupported member type: " + type);
                    }
                }
                return this;
            }
        }

        private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
        {
            private MemberAccessorGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
            }

            public MemberAccessorGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                int size,
                String sizeName,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                if (type instanceof ClassName)
                {
                    ClassName classType = (ClassName) type;
                    if (isStringType(classType))
                    {
                        addStringType(name,
                                      classType,
                                      priorFieldIfDefaulted,
                                      priorDefaultedIsPrimitive);
                    }
                    else if ("OctetsFW".equals(classType.simpleName()))
                    {
                        addOctetsType(name,
                                classType,
                                size,
                                sizeName,
                                priorFieldIfDefaulted,
                                priorDefaultedIsPrimitive);
                    }
                }
                return this;
            }

            private void addOctetsType(
                String name,
                ClassName className,
                int size,
                String sizeName,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                String fieldRW = String.format("%sRW", name);
                ClassName builderType = className.nestedClass("Builder");

                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("checkFieldNotSet($L)", index(name));
                if (priorFieldIfDefaulted != null)
                {
                    code.beginControlFlow("if (!fieldsSet.get($L))", index(priorFieldIfDefaulted));
                    if (priorDefaultedIsPrimitive)
                    {
                        code.addStatement("$L($L)", priorFieldIfDefaulted, defaultName(priorFieldIfDefaulted));
                    }
                    else
                    {
                        //  Attempt to default the entire object. This will fail if it has any required fields.
                        code.addStatement("$L(b -> { });", priorFieldIfDefaulted);
                    }
                    code.endControlFlow();
                }
                code.addStatement("checkFieldsSet(0, $L)", index(name));

                if (size >= 0 || sizeName != null)
                {
                    if (size >= 0)
                    {
                        code.addStatement("int newLimit = limit() + $L", size);
                    }
                    else if (sizeName != null)
                    {
                        code.addStatement("int newLimit = limit() + value$L", name);
                    }
                    code.addStatement("checkLimit(newLimit, maxLimit())");
                    code.addStatement("return $L.wrap(buffer(), limit(), newLimit).reset()", fieldRW);
                }
                else
                {
                    code.addStatement("return $L.wrap(buffer(), limit(), maxLimit()).reset()", fieldRW);
                }
                builder.addMethod(methodBuilder(name)
                        .addModifiers(PRIVATE)
                        .returns(builderType)
                        .addCode(code.build())
                        .build());
            }

            private void addStringType(
                String name,
                ClassName classType,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                String fieldRW = String.format("%sRW", name);
                TypeName builderType = classType.nestedClass("Builder");
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("checkFieldNotSet($L)", index(name));
                if (priorFieldIfDefaulted != null)
                {
                    code.beginControlFlow("if (!fieldsSet.get($L))", index(priorFieldIfDefaulted));
                    if (priorDefaultedIsPrimitive)
                    {
                        code.addStatement("$L($L)", priorFieldIfDefaulted, defaultName(priorFieldIfDefaulted));
                    }
                    else
                    {
                        //  Attempt to default the entire object. This will fail if it has any required fields.
                        code.addStatement("$L(b -> { });", priorFieldIfDefaulted);
                    }
                    code.endControlFlow();
                }
                code.addStatement("checkFieldsSet(0, $L)", index(name))
                    .addStatement("return $L.wrap(buffer(), limit(), maxLimit())", fieldRW);
                builder.addMethod(methodBuilder(name)
                        .addModifiers(PRIVATE)
                        .returns(builderType)
                        .addCode(code.build())
                        .build());
            }
        }

        private static final class MemberMutatorGenerator extends ClassSpecMixinGenerator
        {
            private static final Map<TypeName, String> PUTTER_NAMES;

            static
            {
                Map<TypeName, String> putterNames = new HashMap<>();
                putterNames.put(TypeName.BYTE, "putByte");
                putterNames.put(TypeName.CHAR, "putChar");
                putterNames.put(TypeName.SHORT, "putShort");
                putterNames.put(TypeName.FLOAT, "putFloat");
                putterNames.put(TypeName.INT, "putInt");
                putterNames.put(TypeName.DOUBLE, "putDouble");
                putterNames.put(TypeName.LONG, "putLong");
                PUTTER_NAMES = unmodifiableMap(putterNames);
            }

            private MemberMutatorGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
            {
                super(thisType, builder);
            }

            public MemberMutatorGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                int size,
                String sizeName,
                String priorDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                if (type.isPrimitive())
                {
                    addPrimitiveMember(name, type, unsignedType, priorDefaulted, priorDefaultedIsPrimitive);
                }
                else
                {
                    addNonPrimitiveMember(name, type, size, sizeName, priorDefaulted, priorDefaultedIsPrimitive);
                }
                return this;
            }

            @Override
            public TypeSpec.Builder build()
            {
                return super.build();
            }

            private void addPrimitiveMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                String putterName = PUTTER_NAMES.get(type);
                if (putterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }

                TypeName publicType = (unsignedType != null) ? unsignedType : type;
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("checkFieldNotSet($L)", index(name));
                if (priorFieldIfDefaulted != null)
                {
                    code.beginControlFlow("if (!fieldsSet.get($L))", index(priorFieldIfDefaulted));
                    if (priorDefaultedIsPrimitive)
                    {
                        code.addStatement("$L($L)", priorFieldIfDefaulted, defaultName(priorFieldIfDefaulted));
                    }
                    else
                    {
                        //  Attempt to default the entire object. This will fail if it has any required fields.
                        code.addStatement("$L(b -> { });", priorFieldIfDefaulted);
                    }
                    code.endControlFlow();
                }
                code.addStatement("checkFieldsSet(0, $L)", index(name))
                    .addStatement("int newLimit = limit() + $L", size(name))
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .add("$[")
                    .add("buffer().$L(limit(), ", putterName);
                if (publicType != type)
                {
                    code.add("($T)", type);

                    if (type == TypeName.BYTE)
                    {
                        code.add("(value & 0xFF))");
                    }
                    else if (type == TypeName.SHORT)
                    {
                        code.add("(value & 0xFFFF), $T.BIG_ENDIAN)", ByteOrder.class);
                    }
                    else if (type == TypeName.INT)
                    {
                        code.add("(value & 0xFFFF_FFFF), $T.BIG_ENDIAN)", ByteOrder.class);
                    }
                    else
                    {
                        code.add("value)");
                    }
                }
                else
                {
                    code.add("value)");
                }
                code.add(";\n$]")
                    .addStatement("fieldsSet.set($L)", index(name))
                    .addStatement("limit(newLimit)")
                    .addStatement("return this");

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .addParameter(publicType, "value")
                        .returns(thisType)
                        .addCode(code.build())
                        .build());
            }

            private void addNonPrimitiveMember(
                String name,
                TypeName type,
                int size,
                String sizeName,
                String priorDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                if (type instanceof ClassName)
                {
                    ClassName className = (ClassName) type;
                    addClassType(name, className, size, sizeName, priorDefaulted, priorDefaultedIsPrimitive);
                }
                else if (type instanceof ParameterizedTypeName)
                {
                    ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
                    addParameterizedType(name, parameterizedType, priorDefaulted, priorDefaultedIsPrimitive);
                }
                else
                {
                    // TODO: throw exception? I don't think we should ever get here
                    builder.addMethod(methodBuilder(name)
                            .addModifiers(PUBLIC)
                            .returns(thisType)
                            .addParameter(type, "value")
                            .addStatement("$LRW.set(value)", name)
                            .addStatement("return this")
                            .build());
                }
            }

            private void addClassType(
                String name,
                ClassName className,
                int size,
                String sizeName,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                if (isStringType(className))
                {
                    addStringType(className, name);
                }
                else if (DIRECT_BUFFER_TYPE.equals(className))
                {
                    // TODO: What IDL type does this correspond to? I don't see it in TypeResolver
                    // so I suspect this is dead code and should be removed
                    addDirectBufferType(name);
                }
                else if ("OctetsFW".equals(className.simpleName()))
                {
                    addOctetsType(className, name, size, sizeName);
                }
                else
                {
                    ClassName consumerType = ClassName.get(Consumer.class);
                    ClassName builderType = className.nestedClass("Builder");
                    TypeName mutatorType = ParameterizedTypeName.get(consumerType, builderType);

                    CodeBlock.Builder code = CodeBlock.builder();
                    code.addStatement("checkFieldNotSet($L)", index(name));
                    if (priorFieldIfDefaulted != null)
                    {
                        code.beginControlFlow("if (!fieldsSet.get($L))", index(priorFieldIfDefaulted));
                        if (priorDefaultedIsPrimitive)
                        {
                            code.addStatement("$L($L)", priorFieldIfDefaulted, defaultName(priorFieldIfDefaulted));
                        }
                        else
                        {
                            //  Attempt to default the entire object. This will fail if it has any required fields.
                            code.addStatement("$L(b -> { });", priorFieldIfDefaulted);
                        }
                        code.endControlFlow();
                    }
                    code.addStatement("checkFieldsSet(0, $L)", index(name))
                        .addStatement("$T $L = $LRW.wrap(buffer(), limit(), maxLimit())", builderType, name, name)
                        .addStatement("mutator.accept($L)", name)
                        .addStatement("limit($L.build().limit())", name)
                        .addStatement("fieldsSet.set($L)", index(name))
                        .addStatement("return this");

                    builder.addMethod(methodBuilder(name)
                            .addModifiers(PUBLIC)
                            .returns(thisType)
                            .addParameter(mutatorType, "mutator")
                            .addCode(code.build())
                            .build());
                }
            }

            private void addOctetsType(
                ClassName className,
                String name,
                int size,
                String sizeName)
            {
                ClassName consumerType = ClassName.get(Consumer.class);
                ClassName builderType = className.nestedClass("Builder");
                TypeName mutatorType = ParameterizedTypeName.get(consumerType, builderType);
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("$T $L = $L()", builderType, name, name)
                    .addStatement("mutator.accept($L)", name);
                if (size >= 0 || sizeName != null)
                {
                    code.addStatement("int expectedLimit = $L.maxLimit()", name)
                        .addStatement("int actualLimit = $L.build().limit()", name)
                        .beginControlFlow("if (actualLimit != expectedLimit)")
                        .addStatement("throw new IllegalStateException(format($S, name, actualLimit, expectedLimit)",
                            "Only %d out of %d bytes have been set for field \"%s\"")
                        .endControlFlow();
                    code.addStatement("limit($L.maxLimit())");
                }
                else
                {
                    code.addStatement("limit($L.build().limit())", name);
                }
                code.addStatement("fieldsSet.set($L)", index(name))
                    .addStatement("return this");

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(mutatorType, "mutator")
                        .addCode(code.build())
                        .build());

                    CodeBlock.Builder code2 = CodeBlock.builder();
                    code2.addStatement("$T $L = $L()", builderType, name, name);
                    if (size >= 0 || sizeName != null)
                    {
                        code2.addStatement("int fieldSize = $L.maxLimit()", name)
                             .beginControlFlow("if (length != fieldSize)")
                             .addStatement("throw new IllegalArgumentException(format($S, name, length, fieldSize)",
                                "Invalid length %d for field \"%s\", expected %d")
                             .endControlFlow();
                    }
                    code2.addStatement("$L.set(buffer, offset, length)", name)
                         .addStatement("limit($L.build().limit())", name)
                         .addStatement("fieldsSet.set($L)", index(name))
                         .addStatement("return this");

                    builder.addMethod(methodBuilder(name)
                            .addModifiers(PUBLIC)
                            .returns(thisType)
                            .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                            .addParameter(int.class, "offset")
                            .addParameter(int.class, "length")
                            .addCode(code2.build())
                            .build());
            }

            private void addStringType(
                ClassName className,
                String name)
            {
                ClassName builderType = className.nestedClass("Builder");
                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(String.class, "value")
                        .addStatement("$T $L = $L()", builderType, name, name)
                        .addStatement("$L.set(value, $T.UTF_8)", name, StandardCharsets.class)
                        .addStatement("fieldsSet.set($L)", index(name))
                        .addStatement("limit($L.build().limit())", name)
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(className, "value")
                        .addStatement("$T $L = $L()", builderType, name, name)
                        .addStatement("$L.set(value)", name)
                        .addStatement("fieldsSet.set($L)", index(name))
                        .addStatement("limit($L.build().limit())", name)
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                        .addParameter(int.class, "offset")
                        .addParameter(int.class, "length")
                        .addStatement("$T $L = $L()", builderType, name, name)
                        .addStatement("$L.set(buffer, offset, length)", name)
                        .addStatement("fieldsSet.set($L)", index(name))
                        .addStatement("limit($L.build().limit())", name)
                        .addStatement("return this")
                        .build());
            }

            private void addDirectBufferType(
                String name)
            {
                // TODO: revise/remove this once I understand when/if this would get called
                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .addParameter(IntUnaryOperator.class, "mutator")
                        .addParameter(IntConsumer.class, "error")
                        .returns(thisType)
                        .addStatement("int length = mutator.applyAsInt(offset() + $L)", offset(name))
                        .beginControlFlow("if (length < 0)")
                        .addStatement("error.accept(length)")
                        .addStatement("limit(offset() + $L)", offset(name))
                        .nextControlFlow("else")
                        .addStatement("limit(offset() + $L + length)", offset(name))
                        .endControlFlow()
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .addParameter(IntUnaryOperator.class, "mutator")
                        .returns(thisType)
                        .addStatement("int length = mutator.applyAsInt(offset() + $L)", offset(name))
                        .beginControlFlow("if (length < 0)")
                        .addStatement("throw new IllegalStateException()")
                        .endControlFlow()
                        .addStatement("limit(offset() + $L + length)", offset(name))
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .addParameter(DIRECT_BUFFER_TYPE, "value")
                        .addParameter(int.class, "offset")
                        .addParameter(int.class, "length")
                        .returns(thisType)
                        .addStatement("buffer().putBytes(offset() + $L, value, offset, length)", offset(name))
                        .addStatement("limit(offset() + $L + length)", offset(name))
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .addParameter(DIRECT_BUFFER_TYPE, "value")
                        .returns(thisType)
                        .addStatement("buffer().putBytes(offset() + $L, value, 0, value.capacity())", offset(name))
                        .addStatement("limit(offset() + $L + value.capacity())", offset(name))
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .addParameter(BYTE_ARRAY, "value")
                        .addParameter(int.class, "offset")
                        .addParameter(int.class, "length")
                        .returns(thisType)
                        .addStatement("buffer().putBytes(offset() + $L, value, offset, length)", offset(name))
                        .addStatement("limit(offset() + $L + length)", offset(name))
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .addParameter(BYTE_ARRAY, "value")
                        .returns(thisType)
                        .addStatement("buffer().putBytes(offset() + $L, value, 0, value.length)", offset(name))
                        .addStatement("limit(offset() + $L + value.length)", offset(name))
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .addParameter(String.class, "value")
                        .returns(thisType)
                        .addStatement("int length = buffer().putStringWithoutLengthUtf8(offset() + $L, value)", offset(name))
                        .addStatement("limit(offset() + $L + length)", offset(name))
                        .addStatement("return this")
                        .build());
            }

            private void addParameterizedType(
                String name,
                ParameterizedTypeName parameterizedType,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                ClassName rawType = parameterizedType.rawType;
                ClassName itemType = (ClassName) parameterizedType.typeArguments.get(0);
                ClassName builderRawType = rawType.nestedClass("Builder");
                ClassName itemBuilderType = itemType.nestedClass("Builder");
                ParameterizedTypeName builderType = ParameterizedTypeName.get(builderRawType, itemBuilderType, itemType);

                ClassName consumerType = ClassName.get(Consumer.class);
                TypeName mutatorType = ParameterizedTypeName.get(consumerType, builderType);

                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("checkFieldNotSet($L)", index(name));
                if (priorFieldIfDefaulted != null)
                {
                    code.beginControlFlow("if (!fieldsSet.get($L))", index(priorFieldIfDefaulted));
                    if (priorDefaultedIsPrimitive)
                    {
                        code.addStatement("$L($L)", priorFieldIfDefaulted, defaultName(priorFieldIfDefaulted));
                    }
                    else
                    {
                        //  Attempt to default the entire object. This will fail if it has any required fields.
                        code.addStatement("$L(b -> { });", priorFieldIfDefaulted);
                    }
                    code.endControlFlow();
                }
                code.addStatement("checkFieldsSet(0, $L)", index(name))
                    .addStatement("$T $L = $LRW.wrap(buffer(), limit(), maxLimit())", builderType, name, name)
                    .addStatement("mutator.accept($L)", name)
                    .addStatement("limit($L.build().limit())", name)
                    .addStatement("fieldsSet.set($L)", index(name))
                    .addStatement("return this");

                builder.addMethod(methodBuilder(name)
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(mutatorType, "mutator")
                        .addCode(code.build())
                        .build());
            }
        }
    }

    private static boolean isStringType(
        ClassName classType)
    {
        String name = classType.simpleName();
        return ("StringFW".equals(name) || "String16FW".equals(name));
    }

    private static String defaultName(
        String fieldName)
    {
        return String.format("DEFAULT_%s", constant(fieldName));
    }

    private static String index(
        String fieldName)
    {
        return String.format("INDEX_%s", constant(fieldName));
    }

    private static String offset(
        String fieldName)
    {
        return String.format("FIELD_OFFSET_%s", constant(fieldName));
    }

    private static String size(
        String fieldName)
    {
        return String.format("FIELD_SIZE_%s", constant(fieldName));
    }

    private static String constant(
        String fieldName)
    {
        return fieldName.replaceAll("([^_A-Z])([A-Z])", "$1_$2").toUpperCase();
    }
}
