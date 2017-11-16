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
import static java.util.Collections.unmodifiableMap;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder.NETWORK;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstMemberNode.NULL_DEFAULT;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BIT_UTIL_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BYTE_ARRAY;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.UNSAFE_BUFFER_TYPE;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;

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
    private static final Set<String> RESERVED_METHOD_NAMES = new HashSet<>(Arrays.asList(new String[]
    {
        "offset", "buffer", "limit", "sizeof", "maxLimit", "wrap", "checkLimit", "build"
    }));

    private static final ClassName INT_ITERATOR_CLASS_NAME = ClassName.get(PrimitiveIterator.OfInt.class);

    private static final ClassName LONG_ITERATOR_CLASS_NAME = ClassName.get(PrimitiveIterator.OfLong.class);

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
        this.builder = classBuilder(structName).superclass(flyweightName).addModifiers(PUBLIC, FINAL);
        this.typeId = new TypeIdGenerator(structName, builder);
        this.memberSizeConstant = new MemberSizeConstantGenerator(structName, builder);
        this.memberOffsetConstant = new MemberOffsetConstantGenerator(structName, builder);
        this.memberField = new MemberFieldGenerator(structName, builder);
        this.memberAccessor = new MemberAccessorGenerator(structName, builder);
        this.wrapMethod = new WrapMethodGenerator(structName);
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
        boolean usedAsSize,
        Object defaultValue,
        AstByteOrder byteOrder)
    {
        memberOffsetConstant.addMember(name, type, unsignedType, size, sizeName);
        memberSizeConstant.addMember(name, type, unsignedType, size);
        memberField.addMember(name, type, unsignedType, size, sizeName, byteOrder, defaultValue);
        memberAccessor.addMember(name, type, unsignedType, byteOrder, size, sizeName);
        limitMethod.addMember(name, type, unsignedType, size, sizeName);
        wrapMethod.addMember(name, type, unsignedType, size, sizeName, defaultValue);
        toStringMethod.addMember(name, type, unsignedType, size, sizeName);
        builderClass.addMember(name, type, unsignedType, size, sizeName, usedAsSize, defaultValue, byteOrder);

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
            TypeName unsignedType,
            int size)
        {
            if (type.isPrimitive())
            {
                builder.addField(
                        FieldSpec.builder(int.class, size(name), PRIVATE, STATIC, FINAL)
                                 .initializer("$T.SIZE_OF_$L", BIT_UTIL_TYPE, SIZEOF_BY_NAME.get(type))
                                 .build());
                if (size != -1)
                {
                    builder.addField(
                            FieldSpec.builder(int.class, arraySize(name), PRIVATE, STATIC, FINAL)
                                .initializer("$L", Integer.toString(size))
                                .build());
                }
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
        private int previousSize = -1;

        private MemberOffsetConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MemberOffsetConstantGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            int size,
            String sizeName)
        {
            String initializer;
            if (previousName == null)
            {
                initializer = "0";
            }
            else if (previousSize == -1)
            {
                initializer = String.format("%s + %s", offset(previousName), size(previousName));
            }
            else
            {
                initializer = String.format("%s + (%s * %s)", offset(previousName), size(previousName),
                        arraySize(previousName));
            }
            builder.addField(
                    FieldSpec.builder(int.class, offset(name), PUBLIC, STATIC, FINAL)
                             .initializer(initializer)
                             .build());

            boolean isFixedSize = type.isPrimitive() && sizeName == null;
            previousName = isFixedSize ? name : null;
            previousSize = size;

            return this;
        }
    }

    private static final class MemberFieldGenerator extends ClassSpecMixinGenerator
    {
        private boolean generateIntPrimitiveIterator;
        private boolean generateLongPrimitiveIterator;

        private MemberFieldGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public MemberFieldGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            int size,
            String sizeName,
            AstByteOrder byteOrder,
            Object defaultValue)
        {
            if (!type.isPrimitive())
            {
                addNonPrimitiveMember(name, type, unsignedType, byteOrder, defaultValue);
            }
            else if (size != -1 || sizeName != null)
            {
                addIntegerArrayMember(name, type, unsignedType, sizeName != null);
            }
            return this;
        }

        private void addIntegerArrayMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            boolean variableLength)
        {
            if (variableLength)
            {
                builder.addField(TypeName.INT, dynamicLimit(name), PRIVATE);
            }
            ClassName iteratorClass = iteratorClass(thisType, type, unsignedType);
            builder.addField(iteratorClass, iterator(name), PRIVATE);
            TypeName generateType = (unsignedType != null) ? unsignedType : type;
            if (generateType == TypeName.LONG)
            {
                generateLongPrimitiveIterator = true;
            }
            else
            {
                generateIntPrimitiveIterator = true;
            }
        }

        private MemberFieldGenerator addNonPrimitiveMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            AstByteOrder byteOrder,
            Object defaultValue)
        {
            String fieldRO = String.format("%sRO", name);
            Builder fieldBuilder = FieldSpec.builder(type, fieldRO, PRIVATE);
            if (defaultValue == null)
            {
                fieldBuilder.addModifiers(FINAL);
            }

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
            else if (type instanceof ClassName && isString16Type((ClassName) type) && byteOrder == NETWORK)
            {
                fieldBuilder.initializer("new $T($T.BIG_ENDIAN)", type, ByteOrder.class);
            }
            else
            {
                fieldBuilder.initializer("new $T()", type);
            }

            builder.addField(fieldBuilder.build());
            return this;
        }

        @Override
        public TypeSpec.Builder build()
        {
            if (generateIntPrimitiveIterator)
            {
                generateIntPrimitiveIteratorInnerClass();
            }
            if (generateLongPrimitiveIterator)
            {
                generateLongPrimitiveIteratorInnerClass();
            }
            return super.build();
        }

        private void generateIntPrimitiveIteratorInnerClass()
        {
            ClassName intIterator = thisType.nestedClass("IntPrimitiveIterator");
            TypeSpec.Builder builder = classBuilder(intIterator.simpleName())
                    .addModifiers(PRIVATE, FINAL)
                    .addSuperinterface(INT_ITERATOR_CLASS_NAME);
            builder.addField(String.class, "fieldName", PRIVATE, FINAL);
            builder.addField(int.class, "offset", PRIVATE, FINAL);
            builder.addField(int.class, "fieldSize", PRIVATE, FINAL);
            builder.addField(int.class, "count", PRIVATE, FINAL);
            builder.addField(IntUnaryOperator.class, "accessor", PRIVATE, FINAL);
            builder.addField(int.class, "index", PRIVATE);

            builder.addMethod(constructorBuilder()
                    .addParameter(String.class, "fieldName")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "fieldSize")
                    .addParameter(int.class, "count")
                    .addParameter(IntUnaryOperator.class, "accessor")
                    .addStatement("this.fieldName = fieldName")
                    .addStatement("this.offset = offset")
                    .addStatement("this.fieldSize = fieldSize")
                    .addStatement("this.count = count")
                    .addStatement("this.accessor = accessor")
                    .build());

            builder.addMethod(MethodSpec.methodBuilder("hasNext")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(boolean.class)
                    .addStatement("return index < count")
                    .build());

            builder.addMethod(MethodSpec.methodBuilder("nextInt")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(int.class)
                    .beginControlFlow("if (!hasNext())")
                    .addStatement("throw new $T(fieldName + \": \" + index)", NoSuchElementException.class)
                    .endControlFlow()
                    .addStatement("return accessor.applyAsInt(offset + fieldSize * index++)")
                    .build());

            builder.addMethod(MethodSpec.methodBuilder("toString")
                            .addAnnotation(Override.class)
                            .addModifiers(PUBLIC)
                            .returns(String.class)
                            .addStatement("StringBuffer result = new StringBuffer().append($S)", "[")
                            .addStatement("boolean first = true")
                            .beginControlFlow("while(hasNext())")
                            .beginControlFlow("if (!first)")
                            .addStatement("result.append($S)", ", ")
                            .endControlFlow()
                            .addStatement("result.append(nextInt())")
                            .addStatement("first = false")
                            .endControlFlow()
                            .addStatement("result.append($S)", "]")
                            .addStatement("return result.toString()")
                            .build());

            MemberFieldGenerator.this.builder.addType(builder.build());
        }

        private void generateLongPrimitiveIteratorInnerClass()
        {
            ClassName longIterator = thisType.nestedClass("LongPrimitiveIterator");
            TypeSpec.Builder builder = classBuilder(longIterator.simpleName())
                    .addModifiers(PRIVATE, FINAL)
                    .addSuperinterface(LONG_ITERATOR_CLASS_NAME);
            builder.addField(String.class, "fieldName", PRIVATE, FINAL);
            builder.addField(int.class, "offset", PRIVATE, FINAL);
            builder.addField(int.class, "fieldSize", PRIVATE, FINAL);
            builder.addField(int.class, "count", PRIVATE, FINAL);
            builder.addField(IntToLongFunction.class, "accessor", PRIVATE, FINAL);
            builder.addField(int.class, "index", PRIVATE);

            builder.addMethod(constructorBuilder()
                    .addParameter(String.class, "fieldName")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "fieldSize")
                    .addParameter(int.class, "count")
                    .addParameter(IntToLongFunction.class, "accessor")
                    .addStatement("this.fieldName = fieldName")
                    .addStatement("this.offset = offset")
                    .addStatement("this.fieldSize = fieldSize")
                    .addStatement("this.count = count")
                    .addStatement("this.accessor = accessor")
                    .build());

            builder.addMethod(MethodSpec.methodBuilder("hasNext")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(boolean.class)
                    .addStatement("return index < count")
                    .build());

            builder.addMethod(MethodSpec.methodBuilder("nextLong")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(long.class)
                    .beginControlFlow("if (!hasNext())")
                    .addStatement("throw new $T(fieldName + \": \" + index)", NoSuchElementException.class)
                    .endControlFlow()
                    .addStatement("return accessor.applyAsLong(offset + fieldSize * index++)")
                    .build());

            builder.addMethod(MethodSpec.methodBuilder("toString")
                            .addAnnotation(Override.class)
                            .addModifiers(PUBLIC)
                            .returns(String.class)
                            .addStatement("StringBuffer result = new StringBuffer().append($S)", "[")
                            .addStatement("boolean first = true")
                            .beginControlFlow("while(hasNext())")
                            .beginControlFlow("if (!first)")
                            .addStatement("result.append($S)", ", ")
                            .endControlFlow()
                            .addStatement("result.append(nextLong())")
                            .addStatement("first = false")
                            .endControlFlow()
                            .addStatement("result.append($S)", "]")
                            .addStatement("return result.toString()")
                            .build());

            MemberFieldGenerator.this.builder.addType(builder.build());
        }
    }

    private static final class MemberAccessorGenerator extends ClassSpecMixinGenerator
    {
        private String anchorLimit;

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
            AstByteOrder byteOrder,
            int size,
            String sizeName)
        {
            if (type.isPrimitive())
            {
                if (size != -1 || sizeName != null)
                {
                    addIntegerArrayMember(name, type, unsignedType, byteOrder, sizeName);
                }
                else
                {
                    addPrimitiveMember(name, type, unsignedType, byteOrder);
                }
            }
            else
            {
                addNonPrimitiveMember(name, type, unsignedType);
            }
            return this;
        }

        private void addIntegerArrayMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            AstByteOrder byteOrder,
            String sizeName)
        {
            TypeName generateType = (unsignedType != null) ? unsignedType : type;
            generateType = generateType == TypeName.LONG ? LONG_ITERATOR_CLASS_NAME
                    : INT_ITERATOR_CLASS_NAME;
            builder.addMethod(methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(generateType)
                    .beginControlFlow("if ($L != null)", iterator(name))
                    .addStatement("$L.index = 0", iterator(name))
                    .endControlFlow()
                    .addStatement("return $L",  iterator(name))
                    .build());
            if (sizeName != null)
            {
                anchorLimit = dynamicLimit(name);
            }
        }

        private void addNonPrimitiveMember(
            String name,
            TypeName type,
            TypeName unsignedType)
        {
            CodeBlock.Builder codeBlock = CodeBlock.builder();

            if (DIRECT_BUFFER_TYPE.equals(type))
            {
                MethodSpec.Builder consumerMethod = methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .addParameter(IntBinaryOperator.class, "accessor")
                        .returns(type);

                if (anchorLimit != null)
                {
                    consumerMethod.addStatement("accessor.applyAsInt($L + $L, $LRO.capacity())",
                            anchorLimit, offset(name), name);
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

            anchorLimit = methodName(name) + "()." + (DIRECT_BUFFER_TYPE.equals(type) ? "capacity()" : "limit()");

            builder.addMethod(methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(type)
                    .addCode(codeBlock.build())
                    .build());
        }

        private void addPrimitiveMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            AstByteOrder byteOrder)
        {
            TypeName generateType = (unsignedType != null) ? unsignedType : type;

            CodeBlock.Builder codeBlock = CodeBlock.builder();

            String getterName = GETTER_NAMES.get(type);
            if (getterName == null)
            {
                throw new IllegalStateException("member type not supported: " + type);
            }

            codeBlock.add("$[").add("return ");

            if (generateType != type)
            {
                codeBlock.add("($T)(", generateType);
            }

            if (anchorLimit != null)
            {
                codeBlock.add("buffer().$L($L + $L", getterName, anchorLimit, offset(name));
            }
            else
            {
                codeBlock.add("buffer().$L(offset() + $L", getterName, offset(name));
            }

            if (byteOrder == AstByteOrder.NETWORK)
            {
                if (type == TypeName.SHORT || type == TypeName.INT || type == TypeName.LONG)
                {
                    codeBlock.add(", $T.BIG_ENDIAN", ByteOrder.class);
                }
            }

            if (generateType != type)
            {
                if (type == TypeName.BYTE)
                {
                    codeBlock.add(") & 0xFF)");
                }
                else if (type == TypeName.SHORT)
                {
                    codeBlock.add(") & 0xFFFF)");
                }
                else if (type == TypeName.INT)
                {
                    codeBlock.add(") & 0xFFFF_FFFFL)");
                }
                else if (type == TypeName.LONG)
                {
                    codeBlock.add(") & 0xFFFF_FFFF)");
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

            builder.addMethod(methodBuilder(methodName(name))
                    .addModifiers(PUBLIC)
                    .returns(generateType)
                    .addCode(codeBlock.build())
                    .build());
        }
    }

    private final class LimitMethodGenerator extends MethodSpecGenerator
    {
        private String anchorName;
        private TypeName anchorType;
        private String lastName;
        private TypeName lastType;
        private int lastSize;

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
            TypeName unsignedType,
            int size,
            String sizeName)
        {
            if (!type.isPrimitive() || type.isPrimitive() && sizeName != null)
            {
                anchorName = name;
                anchorType = type;
            }

            lastName = name;
            lastType = type;
            lastSize = size;

            return this;
        }

        @Override
        public MethodSpec generate()
        {
            if (lastName == null)
            {
                builder.addStatement("return offset()");
            }
            else
            {
                CodeBlock.Builder code = CodeBlock.builder();
                code.add("$[");
                if (anchorName != null)
                {
                    if (TypeNames.DIRECT_BUFFER_TYPE.equals(anchorType))
                    {
                        code.add("return $L().capacity()", methodName(anchorName));
                    }
                    else if (anchorType.isPrimitive()) // variable length array
                    {
                        code.add("return $L", dynamicLimit(anchorName));
                    }
                    else
                    {
                        code.add("return $L().limit()", methodName(anchorName));
                    }
                }
                else
                {
                    code.add("return offset()");
                }
                if (lastType.isPrimitive())
                {
                    if (lastSize != -1) // fixed size array
                    {
                        code.add(" + $L + ($L * $L)", offset(lastName), size(lastName), arraySize(lastName));
                    }
                    else if (anchorName == null) // not an array
                    {
                        code.add(" + $L + $L", offset(lastName), size(lastName));
                    }
                }
                code.add(";\n$]");
                builder.addCode(code.build());
            }

            return builder.build();
        }

    }

    private final class WrapMethodGenerator extends MethodSpecGenerator
    {
        private final ClassName thisType;
        private String anchorLimit;

        private WrapMethodGenerator(
            ClassName thisType)
        {
            super(methodBuilder("wrap")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .addParameter(int.class, "maxLimit")
                    .returns(thisName)
                    .addStatement("super.wrap(buffer, offset, maxLimit)"));
            this.thisType = thisType;
        }

        public WrapMethodGenerator addMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            int size,
            String sizeName,
            Object defaultValue)
        {
            if (DIRECT_BUFFER_TYPE.equals(type))
            {
                builder.addStatement("$LRO.wrap(buffer, offset + $L, maxLimit - (offset + $L))",
                        name, offset(name), offset(name));
            }
            else if (!type.isPrimitive())
            {
                addNonPrimitiveMember(name, type, unsignedType, size, sizeName, defaultValue);
            }
            else if (size != -1)
            {
                addFixedIntegerArrayMember(name, type, unsignedType, size);
            }
            else if (sizeName != null)
            {
                addVariableIntegerArrayMember(name, type, unsignedType, sizeName);
            }
            return this;
        }

        private void addFixedIntegerArrayMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            int size)
        {
            ClassName iteratorClass = iteratorClass(thisType, type, unsignedType);
            TypeName targetType = (unsignedType != null) ? unsignedType : type;
            targetType = targetType == TypeName.LONG ? targetType : TypeName.INT;
            CodeBlock.Builder code = CodeBlock.builder();
            String offsetName;
            if (anchorLimit != null)
            {
                offsetName = "offset" + initCap(name);
                code.addStatement("final int $L = $L + $L", offsetName, anchorLimit, offset(name));
            }
            else
            {
                offsetName = offset(name);
            }
            code.add("$[")
                .add("$L = new $T($S, $L, $L, $L, o -> ",
                        iterator(name), iteratorClass, name, offsetName, size(name), arraySize(name));
            addBufferGet(code, targetType, type, unsignedType, "o");
            code.add(")")
                .add(";\n$]");

            builder.addCode(code.build());
        }

        private void addVariableIntegerArrayMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            String sizeName)
        {
            ClassName iteratorClass = iteratorClass(thisType, type, unsignedType);
            String offsetName = "offset" + initCap(name);
            String limitName = "limit" + initCap(name);
            TypeName targetType = (unsignedType != null) ? unsignedType : type;
            targetType = targetType == TypeName.LONG ? targetType : TypeName.INT;
            CodeBlock.Builder code = CodeBlock.builder();
            if (anchorLimit != null)
            {
                code.addStatement("final int $L = $L + $L", offsetName, anchorLimit, offset(name));
            }
            else
            {
                code.addStatement("final int $L = offset + $L", offsetName, offset(name));
            }
            code.add("$[")
                .add("$L = $L() == -1 ? null : new $T($S, $L, $L, (int) $L(), o -> ",
                    iterator(name), methodName(sizeName), iteratorClass,
                    name, offsetName, size(name), methodName(sizeName));
            addBufferGet(code, targetType, type, unsignedType, "o");
            code.add(")")
                .add(";\n$]")
                .addStatement("$L = $L() == -1 ? $L : $L + $L * $L()", limitName, methodName(sizeName),
                        offsetName, offsetName, size(name), methodName(sizeName));
            builder.addCode(code.build());
            anchorLimit = limitName;
        }

        private void addNonPrimitiveMember(
            String name,
            TypeName type,
            TypeName unsignedType,
            int size,
            String sizeName,
            Object defaultValue)
        {
            if (anchorLimit != null)
            {
                if (size >= 0)
                {
                    builder.addStatement("$LRO.wrap(buffer, $L + $L, $L + $L + $L)",
                            name, anchorLimit, offset(name), anchorLimit, offset(name), size);
                }
                else if (sizeName != null)
                {
                    CodeBlock.Builder code = CodeBlock.builder();
                    code.add("$[");
                    if (defaultValue == NULL_DEFAULT)
                    {
                        code.add("$LRO = $L() == -1 ? null : ", name, methodName(sizeName));
                    }
                    code.add("$LRO.wrap(buffer, $L + $L, $L + $L + (int) $L())",
                            name, anchorLimit, offset(name), anchorLimit, offset(name), methodName(sizeName));
                    code.add(";\n$]");
                    builder.addCode(code.build());
                }
                else
                {
                    builder.addStatement("$LRO.wrap(buffer, $L + $L, maxLimit)",
                            name, anchorLimit, offset(name));
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
                    CodeBlock.Builder code = CodeBlock.builder();
                    code.add("$[");
                    if (defaultValue == NULL_DEFAULT)
                    {
                        code.add("$LRO = $L() == -1 ? null : ", name, methodName(sizeName));
                    }
                    code.add("$LRO.wrap(buffer, offset + $L, offset + $L + (int) $L())",
                            name, offset(name), offset(name), methodName(sizeName));
                    code.add(";\n$]");
                    builder.addCode(code.build());
                }
                else
                {
                    builder.addStatement("$LRO.wrap(buffer, offset + $L, maxLimit)",
                            name, offset(name));
                }
            }
            anchorLimit = methodName(name) + "().limit()";
        }

        private void addBufferGet(
            CodeBlock.Builder codeBlock,
            TypeName targetType,
            TypeName type,
            TypeName unsignedType,
            String offset)
        {
            String getterName = GETTER_NAMES.get(type);
            if (getterName == null)
            {
                throw new IllegalStateException("member type not supported: " + type);
            }
            if (targetType != type)
            {
                codeBlock.add("($T)(", targetType);
            }

            codeBlock.add("buffer().$L($L", getterName, offset);

            if (targetType != type  && unsignedType != null)
            {
                if (type == TypeName.BYTE)
                {
                    codeBlock.add(") & 0xFF)");
                }
                else if (type == TypeName.SHORT)
                {
                    codeBlock.add(") & 0xFFFF)", ByteOrder.class);
                }
                else if (type == TypeName.INT)
                {
                    codeBlock.add(") & 0xFFFF_FFFFL)", ByteOrder.class);
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
            if (targetType != type && unsignedType == null)
            {
                codeBlock.add(")");
            }
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
            TypeName unsignedType,
            int size,
            String sizeName)
        {
            boolean isArray = size != -1 || sizeName != null;
            formats.add(String.format("%s=%%%s", name, type.isPrimitive() && !isArray ? "d" : "s"));
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
            boolean usedAsSize,
            Object defaultValue,
            AstByteOrder byteOrder)
        {
            if (usedAsSize)
            {
                defaultValue = 0;
            }
            memberConstant.addMember(name, type, unsignedType, size, sizeName, defaultValue);
            memberField.addMember(name, type, unsignedType, size, sizeName, usedAsSize, byteOrder);
            memberAccessor.addMember(name, type, unsignedType, size, sizeName, priorDefaulted, priorDefaultedIsPrimitive);
            memberMutator.addMember(name, type, unsignedType, usedAsSize, size, sizeName, byteOrder, defaultValue,
                    priorDefaulted, priorDefaultedIsPrimitive);
            if (defaultValue != null || isImplicitlyDefaulted(type, size, sizeName))
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

        private static String appendMethodName(
            String fieldName)
        {
            return String.format("append%s", initCap(fieldName));
        }

        private static String defaultName(
            String fieldName)
        {
            return String.format("DEFAULT_%s", constant(fieldName));
        }

        private static boolean isImplicitlyDefaulted(
            TypeName type,
            int size,
            String sizeName)
        {
            boolean result = false;
            if (type instanceof ClassName && !isStringType((ClassName) type))
            {
                ClassName classType = (ClassName) type;
                if ("OctetsFW".equals(classType.simpleName()))
                {
                    result = (size == -1 && sizeName == null);
                }
                else
                {
                    result = true;
                }
            }
            if (type instanceof ParameterizedTypeName)
            {
                ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
                if ("ListFW".equals(parameterizedType.rawType.simpleName()))
                {
                    result = true;
                }
            }
            return result;
        }

        private static String dynamicOffset(
            String fieldName)
        {
            return String.format("dynamicOffset%s", initCap(fieldName));
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
                int size,
                String sizeName,
                Object defaultValue)
            {
                builder.addField(
                        FieldSpec.builder(int.class, index(name), PRIVATE, STATIC, FINAL)
                                 .initializer(Integer.toString(nextIndex++))
                                 .build());
                fieldNames.add(name);
                boolean isOctetsType = type instanceof ClassName && "OctetsFW".equals(((ClassName) type).simpleName());
                if (defaultValue != null)
                {
                    Object defaultValueToSet = defaultValue == NULL_DEFAULT ? null : defaultValue;
                    TypeName generateType = (unsignedType != null) ? unsignedType : type;
                    if (size != -1 || sizeName != null)
                    {
                        generateType = generateType == TypeName.LONG ? LONG_ITERATOR_CLASS_NAME
                                : INT_ITERATOR_CLASS_NAME;
                    }
                    builder.addField(
                            FieldSpec.builder(generateType, defaultName(name), PRIVATE, STATIC, FINAL)
                                     .initializer(Objects.toString(defaultValueToSet))
                                     .build());
                    fieldsWithDefaultsInitializer.addStatement("set($L)", index(name));
                }
                else if ((isOctetsType && size < 0 && sizeName == null)
                         || (type instanceof ParameterizedTypeName && "ListFW".equals(
                                  ((ParameterizedTypeName) type).rawType.simpleName()))
                        )
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
                TypeName unsignedType,
                int size,
                String sizeName,
                boolean usedAsSize,
                AstByteOrder byteOrder)
            {
                if (usedAsSize)
                {
                    builder.addField(FieldSpec.builder(TypeName.INT, dynamicOffset(name), PRIVATE)
                            .build());
                }
                else if (type.isPrimitive())
                {
                    if (size != -1 || sizeName != null)
                    {
                        builder.addField(FieldSpec.builder(TypeName.INT, dynamicOffset(name), PRIVATE)
                                .initializer("-1")
                                .build());
                    }
                }
                else
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

                        if (isString16Type(classType) && byteOrder == NETWORK)
                        {
                            builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                                    .initializer("new $T($T.BIG_ENDIAN)", builderType, ByteOrder.class)
                                    .build());
                        }
                        else
                        {
                            builder.addField(FieldSpec.builder(builderType, fieldRW, PRIVATE, FINAL)
                                    .initializer("new $T()", builderType)
                                    .build());
                        }
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

                if (size >= 0)
                {
                    code.addStatement("int newLimit = limit() + $L", size);
                    code.addStatement("checkLimit(newLimit, maxLimit())");
                    code.addStatement("return $L.wrap(buffer(), limit(), newLimit)", fieldRW);
                }
                else
                {
                    code.addStatement("return $L.wrap(buffer(), limit(), maxLimit())", fieldRW);
                }
                builder.addMethod(methodBuilder(methodName(name))
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
                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PRIVATE)
                        .returns(builderType)
                        .addCode(code.build())
                        .build());
            }
        }

        private static final class MemberMutatorGenerator extends ClassSpecMixinGenerator
        {
            private static final Map<TypeName, String> PUTTER_NAMES;
            private static final Map<TypeName, String[]> UNSIGNED_INT_RANGES;

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

                Map<TypeName, String[]> unsigned = new HashMap<>();
                unsigned.put(TypeName.BYTE, new String[]{"0", "0XFF"});
                unsigned.put(TypeName.SHORT, new String[]{"0", "0xFFFF"});
                unsigned.put(TypeName.INT, new String[]{"0", "0xFFFFFFFFL"});
                unsigned.put(TypeName.LONG, new String[]{"0L", null});
                UNSIGNED_INT_RANGES = unmodifiableMap(unsigned);
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
                boolean usedAsSize,
                int size,
                String sizeName,
                AstByteOrder byteOrder,
                Object defaultValue,
                String priorDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                if (type.isPrimitive())
                {
                    if (sizeName != null)
                    {
                        addIntegerVariableArrayIteratorMutator(name, type, unsignedType, sizeName, defaultValue, priorDefaulted,
                                priorDefaultedIsPrimitive);
                        addIntegerVariableArrayAppendMutator(name, type, unsignedType, byteOrder, sizeName, priorDefaulted,
                                priorDefaultedIsPrimitive);
                    }
                    else if (size != -1)
                    {
                        addIntegerFixedArrayIteratorMutator(name, type, unsignedType, size, priorDefaulted,
                                priorDefaultedIsPrimitive);
                        addIntegerFixedArrayAppendMutator(name, type, unsignedType, byteOrder, size, priorDefaulted,
                                priorDefaultedIsPrimitive);
                    }
                    else
                    {
                        addPrimitiveMember(name, type, unsignedType, usedAsSize, byteOrder, priorDefaulted,
                                priorDefaultedIsPrimitive);
                    }

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
                boolean usedAsSize,
                AstByteOrder byteOrder,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                String putterName = PUTTER_NAMES.get(type);
                if (putterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }

                TypeName generateType = (unsignedType != null) ? unsignedType : type;
                CodeBlock.Builder code = CodeBlock.builder();
                if (!usedAsSize)
                {
                    code.addStatement("checkFieldNotSet($L)", index(name));
                }
                if (unsignedType != null)
                {
                    String[] range = UNSIGNED_INT_RANGES.get(type);
                    code.beginControlFlow("if (value < $L)", range[0])
                    .addStatement("throw new IllegalArgumentException(String.format($S, value))",
                            format("Value %%d too low for field \"%s\"", name))
                    .endControlFlow();
                    if (range[1] != null)
                    {
                        code.beginControlFlow("if (value > $L)", range[1])
                        .addStatement("throw new IllegalArgumentException(String.format($S, value))",
                            format("Value %%d too high for field \"%s\"", name))
                        .endControlFlow();
                    }
                }

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
                if (generateType != type)
                {
                    code.add("($T)", type);

                    if (type == TypeName.BYTE)
                    {
                        code.add("(value & 0xFF)");
                    }
                    else if (type == TypeName.SHORT)
                    {
                        code.add("(value & 0xFFFF)");
                    }
                    else if (type == TypeName.INT)
                    {
                        code.add("(value & 0xFFFF_FFFFL)");
                    }
                    else
                    {
                        code.add("value");
                    }
                }
                else
                {
                    code.add("value");
                }
                if (byteOrder == NETWORK)
                {
                    code.add(", $T.BIG_ENDIAN", ByteOrder.class);
                }
                code.add(");\n$]");
                if (usedAsSize)
                {
                    code.addStatement("$L = limit()", dynamicOffset(name));
                }
                code.addStatement("fieldsSet.set($L)", index(name))
                    .addStatement("limit(newLimit)")
                    .addStatement("return this");

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(usedAsSize ? PRIVATE : PUBLIC)
                        .addParameter(generateType, "value")
                        .returns(thisType)
                        .addCode(code.build())
                        .build());
            }

            private void addIntegerFixedArrayIteratorMutator(
                String name,
                TypeName type,
                TypeName unsignedType,
                int size,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                CodeBlock.Builder code = CodeBlock.builder();
                TypeName inputType = (unsignedType != null) ? unsignedType : type;
                TypeName valueType = inputType == TypeName.LONG ? TypeName.LONG : TypeName.INT;
                TypeName iteratorType = inputType == TypeName.LONG ? LONG_ITERATOR_CLASS_NAME
                        : INT_ITERATOR_CLASS_NAME;
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
                code.beginControlFlow("if (values == null)");
                code.addStatement("throw new $T($S)",
                        IllegalArgumentException.class, format("fixed size array %s cannot be set to null", name));
                code.endControlFlow();
                code.addStatement("int count = 0");
                code.beginControlFlow("while (values.hasNext())");
                code.addStatement("$T value = values.next$L()", valueType,
                        valueType == TypeName.LONG ? "Long" : "Int");
                code.add("$[");
                code.add("$L(", appendMethodName(name));
                if (valueType != type)
                {
                    code.add("($T)", inputType);

                    if (unsignedType != null)
                    {
                        if (type == TypeName.BYTE)
                        {
                            code.add("(value & 0xFF))");
                        }
                        else if (type == TypeName.SHORT)
                        {
                            code.add("(value & 0xFFFF))");
                        }
                        else if (type == TypeName.INT)
                        {
                            code.add("(value & 0xFFFF_FFFFL))");
                        }
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
                code.add(";\n$]");
                code.addStatement("count++");
                code.endControlFlow();
                code.beginControlFlow("if (count < $L)", arraySize(name));
                code.addStatement("throw new $T($S)",
                        IllegalArgumentException.class, format("Not enough values for %s", name));
                code.endControlFlow();
                code.addStatement("return this");

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .addParameter(iteratorType, "values")
                        .returns(thisType)
                        .addCode(code.build())
                        .build());
            }

            private void addIntegerFixedArrayAppendMutator(
                String name,
                TypeName type,
                TypeName unsignedType,
                AstByteOrder byteOrder,
                int size,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                String putterName = PUTTER_NAMES.get(type);
                if (putterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }

                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("checkFieldNotSet($L)", index(name));
                if (unsignedType != null)
                {
                    String[] range = UNSIGNED_INT_RANGES.get(type);
                    code.beginControlFlow("if (value < $L)", range[0])
                        .addStatement("throw new IllegalArgumentException(String.format($S, value))",
                                format("Value %%d too low for field \"%s\"", name))
                    .endControlFlow();
                    if (range[1] != null)
                    {
                        code.beginControlFlow("if (value > $L)", range[1])
                        .addStatement("throw new IllegalArgumentException(String.format($S, value))",
                                format("Value %%d too high for field \"%s\"", name))
                        .endControlFlow();
                    }
                }

                code.beginControlFlow("if ($L == -1)", dynamicOffset(name));

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
                    .addStatement("$L = limit()", dynamicOffset(name))
                .endControlFlow();

                code.addStatement("int newLimit = limit() + $L", size(name))
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .addStatement("int newSize = (newLimit - $L) / $L",  dynamicOffset(name), size(name))
                    .beginControlFlow("if (newSize == $L)", arraySize(name))
                        .addStatement("fieldsSet.set($L)", index(name))
                    .endControlFlow()
                    .add("$[")
                    .add("buffer().$L(limit(), ", putterName);

                TypeName inputType = (unsignedType != null) ? unsignedType : type;
                if (inputType != type)
                {
                    code.add("($T)", type);

                    if (unsignedType != null)
                    {
                        if (type == TypeName.BYTE)
                        {
                            code.add("(value & 0xFF)");
                        }
                        else if (type == TypeName.SHORT)
                        {
                            code.add("(value & 0xFFFF)");
                        }
                        else if (type == TypeName.INT)
                        {
                            code.add("(value & 0xFFFF_FFFFL)", ByteOrder.class);
                        }
                        else
                        {
                            code.add("value");
                        }
                    }
                    else
                    {
                        code.add("value");
                    }
                }
                else
                {
                    code.add("value");
                }
                if (byteOrder == NETWORK)
                {
                    code.add(", $T.BIG_ENDIAN", ByteOrder.class);
                }
                code.add(");\n$]");
                code.addStatement("limit(newLimit)")
                    .addStatement("return this");

                builder.addMethod(methodBuilder(appendMethodName(name))
                        .addModifiers(PUBLIC)
                        .addParameter(inputType, "value")
                        .returns(thisType)
                        .addCode(code.build())
                        .build());
            }

            private void addIntegerVariableArrayIteratorMutator(
                String name,
                TypeName type,
                TypeName unsignedType,
                String sizeName,
                Object defaultValue,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                CodeBlock.Builder code = CodeBlock.builder();
                code.addStatement("checkFieldNotSet($L)", index(name));
                TypeName inputType = (unsignedType != null) ? unsignedType : type;
                TypeName valueType = inputType == TypeName.LONG ? TypeName.LONG : TypeName.INT;
                TypeName iteratorType = inputType == TypeName.LONG ? LONG_ITERATOR_CLASS_NAME
                        : INT_ITERATOR_CLASS_NAME;
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
                if (defaultValue != null)
                {
                    code.beginControlFlow("if (values == null || !values.hasNext())");
                    code.addStatement("int limit = limit()");
                    code.addStatement("limit($L)", dynamicOffset(sizeName));
                    code.addStatement("$L(values == null ? -1 : 0)", methodName(sizeName));
                }
                else
                {
                    code.beginControlFlow("if (values == null)");
                    code.addStatement("throw new $T($S + $S)",
                            IllegalArgumentException.class, name, " does not default to null so cannot be set to null");
                    code.endControlFlow();
                    code.beginControlFlow("if (!values.hasNext())");
                    code.addStatement("int limit = limit()");
                    code.addStatement("limit($L)", dynamicOffset(sizeName));
                    code.addStatement("$L(0)", methodName(sizeName));
                }
                code.addStatement("limit(limit)");
                code.addStatement("checkFieldsSet(0, $L)", index(name));
                code.addStatement("fieldsSet.set($L)", index(name));
                code.nextControlFlow("else");
                code.beginControlFlow("while (values.hasNext())");
                code.addStatement("$T value = values.next$L()", valueType,
                        valueType == TypeName.LONG ? "Long" : "Int");
                code.add("$[");
                code.add("$L(", appendMethodName(name));
                if (valueType != type)
                {
                    code.add("($T)", inputType);

                    if (unsignedType != null)
                    {
                        if (type == TypeName.BYTE)
                        {
                            code.add("(value & 0xFF))");
                        }
                        else if (type == TypeName.SHORT)
                        {
                            code.add("(value & 0xFFFF))");
                        }
                        else if (type == TypeName.INT)
                        {
                            code.add("(value & 0xFFFF_FFFFL))");
                        }
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
                code.add(";\n$]");
                code.endControlFlow();
                code.endControlFlow();
                code.addStatement("return this");

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .addParameter(iteratorType, "values")
                        .returns(thisType)
                        .addCode(code.build())
                        .build());
            }

            private void addIntegerVariableArrayAppendMutator(
                String name,
                TypeName type,
                TypeName unsignedType,
                AstByteOrder byteOrder,
                String sizeName,
                String priorFieldIfDefaulted,
                boolean priorDefaultedIsPrimitive)
            {
                String putterName = PUTTER_NAMES.get(type);
                if (putterName == null)
                {
                    throw new IllegalStateException("member type not supported: " + type);
                }

                CodeBlock.Builder code = CodeBlock.builder();
                if (unsignedType != null)
                {
                    String[] range = UNSIGNED_INT_RANGES.get(type);
                    code.beginControlFlow("if (value < $L)", range[0])
                        .addStatement("throw new IllegalArgumentException(String.format($S, value))",
                                format("Value %%d too low for field \"%s\"", name))
                    .endControlFlow();
                    if (range[1] != null)
                    {
                        code.beginControlFlow("if (value > $L)", range[1])
                        .addStatement("throw new IllegalArgumentException(String.format($S, value))",
                                format("Value %%d too high for field \"%s\"", name))
                        .endControlFlow();
                    }
                }

                code.beginControlFlow("if (!fieldsSet.get($L))", index(name));

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
                code.addStatement("fieldsSet.set($L)", index(sizeName))
                    .addStatement("checkFieldsSet(0, $L)", index(name))
                    .addStatement("$L = limit()", dynamicOffset(name))
                    .addStatement("fieldsSet.set($L)", index(name))
                .endControlFlow();

                code.addStatement("int newLimit = limit() + $L", size(name))
                    .addStatement("checkLimit(newLimit, maxLimit())")
                    .add("$[")
                    .add("buffer().$L(limit(), ", putterName);

                TypeName inputType = (unsignedType != null) ? unsignedType : type;
                if (inputType != type)
                {
                    code.add("($T)", type);

                    if (type == TypeName.BYTE)
                    {
                        code.add("(value & 0xFF)");
                    }
                    else if (type == TypeName.SHORT)
                    {
                        code.add("(value & 0xFFFF)");
                    }
                    else if (type == TypeName.INT)
                    {
                        code.add("(value & 0xFFFF_FFFFL)");
                    }
                    else
                    {
                        code.add("value");
                    }
                }
                else
                {
                    code.add("value");
                }
                if (byteOrder == NETWORK)
                {
                    code.add(", $T.BIG_ENDIAN", ByteOrder.class);
                }
                code.add(");\n$]");
                code.addStatement("fieldsSet.set($L)", index(name))
                    .addStatement("limit($L)", dynamicOffset(sizeName))
                    .addStatement("int newSize = (newLimit - $L) / $L", dynamicOffset(name), size(name))
                    .addStatement("$L(newSize)", methodName(sizeName))
                    .addStatement("limit(newLimit)")
                    .addStatement("return this");

                builder.addMethod(methodBuilder(appendMethodName(name))
                        .addModifiers(PUBLIC)
                        .addParameter(inputType, "value")
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
                    builder.addMethod(methodBuilder(methodName(name))
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
                        .addStatement("$T $LRW = this.$LRW.wrap(buffer(), limit(), maxLimit())", builderType, name, name)
                        .addStatement("mutator.accept($LRW)", name)
                        .addStatement("limit($LRW.build().limit())", name)
                        .addStatement("fieldsSet.set($L)", index(name))
                        .addStatement("return this");

                    builder.addMethod(methodBuilder(methodName(name))
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
                code.addStatement("$T $LRW = $L()", builderType, name, methodName(name))
                    .addStatement("mutator.accept($LRW)", name);
                if (size >= 0)
                {
                    code.addStatement("int expectedLimit = $LRW.maxLimit()", name)
                        .addStatement("int actualLimit = $LRW.build().limit()", name)
                        .beginControlFlow("if (actualLimit != expectedLimit)")
                        .addStatement("throw new IllegalStateException(String.format($S, " +
                                      "actualLimit - limit(), expectedLimit - limit()))",
                            format("%%d instead of %%d bytes have been set for field \"%s\"", name))
                        .endControlFlow();
                    code.addStatement("limit($LRW.maxLimit())", name);
                }
                else if (sizeName != null)
                {
                    code.addStatement("int newLimit = $LRW.build().limit()", name)
                        .addStatement("int size$$ = newLimit - limit()")
                        .addStatement("limit($L)", dynamicOffset(sizeName))
                        .addStatement("$L(size$$)", sizeName)
                        .addStatement("limit(newLimit)");
                }
                else
                {
                    code.addStatement("limit($LRW.build().limit())", name);
                }
                code.addStatement("fieldsSet.set($L)", index(name))
                    .addStatement("return this");

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(mutatorType, "mutator")
                        .addCode(code.build())
                        .build());

                    CodeBlock.Builder code2 = CodeBlock.builder();
                    code2.addStatement("$T $LRW = $L()", builderType, name, methodName(name));
                    if (size >= 0)
                    {
                        code2.addStatement("int fieldSize = $LRW.maxLimit() - limit()", name)
                             .beginControlFlow("if (length != fieldSize)")
                             .addStatement("throw new IllegalArgumentException(String.format($S, length, fieldSize))",
                                format("Invalid length %%d for field \"%s\", expected %%d", name))
                             .endControlFlow();
                    }
                    code2.addStatement("$LRW.set(buffer, offset, length)", name);
                    if (sizeName != null)
                    {
                        code2.addStatement("int newLimit = $LRW.build().limit()", name)
                             .addStatement("int size$$ = newLimit - limit()")
                             .addStatement("limit($L)", dynamicOffset(sizeName))
                             .addStatement("$L(size$$)", sizeName)
                             .addStatement("limit(newLimit)");
                    }
                    else
                    {
                        code2.addStatement("limit($LRW.build().limit())", name);
                    }
                    code2.addStatement("fieldsSet.set($L)", index(name))
                         .addStatement("return this");

                    builder.addMethod(methodBuilder(methodName(name))
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

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(String.class, "value")
                        .addStatement("$T $LRW = $L()", builderType, name, methodName(name))
                        .addStatement("$LRW.set(value, $T.UTF_8)", name, StandardCharsets.class)
                        .addStatement("fieldsSet.set($L)", index(name))
                        .addStatement("limit($LRW.build().limit())", name)
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(className, "value")
                        .addStatement("$T $LRW = $L()", builderType, name, methodName(name))
                        .addStatement("$LRW.set(value)", name)
                        .addStatement("fieldsSet.set($L)", index(name))
                        .addStatement("limit($LRW.build().limit())", name)
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(DIRECT_BUFFER_TYPE, "buffer")
                        .addParameter(int.class, "offset")
                        .addParameter(int.class, "length")
                        .addStatement("$T $LRW = $L()", builderType, name, methodName(name))
                        .addStatement("$LRW.set(buffer, offset, length)", name)
                        .addStatement("fieldsSet.set($L)", index(name))
                        .addStatement("limit($LRW.build().limit())", name)
                        .addStatement("return this")
                        .build());
            }

            private void addDirectBufferType(
                String name)
            {
                // TODO: revise/remove this once I understand when/if this would get called
                builder.addMethod(methodBuilder(methodName(name))
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

                builder.addMethod(methodBuilder(methodName(name))
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

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .addParameter(DIRECT_BUFFER_TYPE, "value")
                        .addParameter(int.class, "offset")
                        .addParameter(int.class, "length")
                        .returns(thisType)
                        .addStatement("buffer().putBytes(offset() + $L, value, offset, length)", offset(name))
                        .addStatement("limit(offset() + $L + length)", offset(name))
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .addParameter(DIRECT_BUFFER_TYPE, "value")
                        .returns(thisType)
                        .addStatement("buffer().putBytes(offset() + $L, value, 0, value.capacity())", offset(name))
                        .addStatement("limit(offset() + $L + value.capacity())", offset(name))
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .addParameter(BYTE_ARRAY, "value")
                        .addParameter(int.class, "offset")
                        .addParameter(int.class, "length")
                        .returns(thisType)
                        .addStatement("buffer().putBytes(offset() + $L, value, offset, length)", offset(name))
                        .addStatement("limit(offset() + $L + length)", offset(name))
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .addParameter(BYTE_ARRAY, "value")
                        .returns(thisType)
                        .addStatement("buffer().putBytes(offset() + $L, value, 0, value.length)", offset(name))
                        .addStatement("limit(offset() + $L + value.length)", offset(name))
                        .addStatement("return this")
                        .build());

                builder.addMethod(methodBuilder(methodName(name))
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
                    .addStatement("$T $LRW = this.$LRW.wrap(buffer(), limit(), maxLimit())", builderType, name, name)
                    .addStatement("mutator.accept($LRW)", name)
                    .addStatement("limit($LRW.build().limit())", name)
                    .addStatement("fieldsSet.set($L)", index(name))
                    .addStatement("return this");

                builder.addMethod(methodBuilder(methodName(name))
                        .addModifiers(PUBLIC)
                        .returns(thisType)
                        .addParameter(mutatorType, "mutator")
                        .addCode(code.build())
                        .build());

                if ("ListFW".equals(rawType.simpleName()))
                {
                    // Add a method to append list items
                    code = CodeBlock.builder();
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
                        .addStatement("$T $LRW = this.$LRW.wrap(buffer(), limit(), maxLimit())", builderType, name, name)
                        .addStatement("$LRW.item(mutator)", name)
                        .addStatement("limit($LRW.build().limit())", name)
                        .addStatement("fieldsSet.set($L)", index(name))
                        .addStatement("return this");

                    TypeName itemMutatorType = ParameterizedTypeName.get(consumerType, itemBuilderType);
                    builder.addMethod(methodBuilder(methodName(name + "Item"))
                            .addModifiers(PUBLIC)
                            .returns(thisType)
                            .addParameter(itemMutatorType, "mutator")
                            .addCode(code.build())
                            .build());
                }
            }
        }
    }

    private static boolean isStringType(
        ClassName classType)
    {
        String name = classType.simpleName();
        return ("StringFW".equals(name) || isString16Type(classType));
    }

    private static boolean isString16Type(
        ClassName classType)
    {
        String name = classType.simpleName();
        return "String16FW".equals(name);
    }

    private static String index(
        String fieldName)
    {
        return String.format("INDEX_%s", constant(fieldName));
    }

    private static String initCap(String value)
    {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private static String arraySize(
        String fieldName)
    {
        return String.format("ARRAY_SIZE_%s", constant(fieldName));
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

    private static String dynamicLimit(String fieldName)
    {
        return "limit" + initCap(fieldName);
    }

    private static String iterator(String fieldName)
    {
        return "iterator" + initCap(fieldName);
    }

    private static ClassName iteratorClass(
        ClassName structName,
        TypeName type,
        TypeName unsignedType)
    {
        TypeName generateType = (unsignedType != null) ? unsignedType : type;
        return generateType == TypeName.LONG ? structName.nestedClass("LongPrimitiveIterator")
                : structName.nestedClass("IntPrimitiveIterator");
    }

    private static String methodName(String name)
    {
        return RESERVED_METHOD_NAMES.contains(name) ? name + "$" : name;
    }
}
