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
package org.reaktivity.nukleus.maven.plugin.internal.generate.test;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.Collections.unmodifiableMap;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder.NETWORK;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstMemberNode.NULL_DEFAULT;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.BIT_UTIL_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.UNSAFE_BUFFER_TYPE;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.IntBinaryOperator;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.antlr.v4.runtime.CharStream;
import org.junit.Assert;
import org.junit.Test;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNodeLocator;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ClassSpecGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ClassSpecMixinGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.MethodSpecGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.FieldSpec.Builder;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public final class StructFlyweightTestGenerator extends ClassSpecGenerator
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
    private final TypeIdTestGenerator typeId;
    private final BufferGenerator buffer;
    private final ExpectedBufferGenerator expectedBuffer;
    private final FieldRWGenerator fieldRW;
    private final FieldROGenerator fieldRO;
    private final MemberFieldGenerator memberField;
    private final MemberSizeConstantGenerator memberSizeConstant;
    private final MemberOffsetConstantGenerator memberOffsetConstant;
    private final MemberAccessorGenerator memberAccessor;
    private final WrapMethodGenerator wrapMethod;
    private final LimitMethodGenerator limitMethod;
    private final ToStringMethodGenerator toStringMethod;
    private final ShouldDefaultValuesMethodGenerator shouldDefaultValuesMethodGenerator;
    private final AstNodeLocator astNodeLocator;

    public StructFlyweightTestGenerator(
        ClassName structName,
        String baseName,
        AstNodeLocator astNodeLocator)
    {
        super(structName);
        this.baseName = baseName + "Test";
        this.builder = classBuilder(structName).addModifiers(PUBLIC, FINAL);
        this.buffer = new BufferGenerator(structName, builder); // should add tests for correct type set
        this.expectedBuffer = new ExpectedBufferGenerator(structName, builder);
        this.fieldRW = new FieldRWGenerator(structName, builder, baseName);
        this.fieldRO = new FieldROGenerator(structName, builder, baseName);
        this.typeId = new TypeIdTestGenerator(structName, builder); // should add tests for correct type set
        this.memberSizeConstant = new MemberSizeConstantGenerator(structName, builder);
        this.memberOffsetConstant = new MemberOffsetConstantGenerator(structName, builder);
        this.memberField = new MemberFieldGenerator(structName, builder);
        this.memberAccessor = new MemberAccessorGenerator(structName, builder);
        this.wrapMethod = new WrapMethodGenerator(structName);
        this.limitMethod = new LimitMethodGenerator();
        this.toStringMethod = new ToStringMethodGenerator();
        this.shouldDefaultValuesMethodGenerator = new ShouldDefaultValuesMethodGenerator();
        this.astNodeLocator = astNodeLocator;
    }

    public StructFlyweightTestGenerator typeId(
        int typeId)
    {
        this.typeId.typeId(typeId);
        return this;
    }

    public StructFlyweightTestGenerator addMember(
        AstType memberType,
        String name,
        TypeName type,
        TypeName unsignedType,
        int size,
        String sizeName,
        TypeName sizeType,
        boolean usedAsSize,
        Object defaultValue,
        AstByteOrder byteOrder)
    {
        if (memberType.isDynamic())
        {
            AstNode memberNode = astNodeLocator.locateNode(null, memberType.name(), null);
            System.out.println("memberNode for " + memberType.name() + " is " + memberNode);
        }

        try
        {
            shouldDefaultValuesMethodGenerator.addMember(name, type, unsignedType, size, sizeName, defaultValue);
        }
        catch (UnsupportedOperationException uoe)
        {
        }
        return this;
    }

    @Override
    public TypeSpec generate()
    {
        // TODO: build fields and methods here
        buffer.build();
        expectedBuffer.build();
        fieldRW.build();
        fieldRO.build();
        memberOffsetConstant.build();
        memberSizeConstant.build();
        memberField.build();
        memberAccessor.build();

        try
        {
            builder.addMethod(shouldDefaultValuesMethodGenerator.generate());
        }
        catch (UnsupportedOperationException uoe)
        {
        }
        builder.addMethod(toStringMethod.generate());

        return builder.build();
    }

    private static final class TypeIdTestGenerator extends ClassSpecMixinGenerator
    {
        private int typeId;

        private TypeIdTestGenerator(
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

    private static final class BufferGenerator extends ClassSpecMixinGenerator
    {

    private BufferGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
    {
        super(thisType, builder);
    }

    @Override
    public TypeSpec.Builder build()
    {
        FieldSpec bufferFieldSpec = FieldSpec.builder(MutableDirectBuffer.class, "buffer", PRIVATE, FINAL)
                .initializer("new $T($T.allocateDirect(100000)) \n" +
                        "{\n"+
                        "    {\n"+
                        "        // Make sure the code is not secretly relying upon memory being initialized to 0\n" +
                        "        setMemory(0, capacity(), (byte) 0xF);\n" +
                        "    }\n" +
                        "}", UnsafeBuffer.class, ByteBuffer.class)
                .build();
        builder.addField(bufferFieldSpec);

        return builder;
    }
}

    private static final class ExpectedBufferGenerator extends ClassSpecMixinGenerator
    {

        private ExpectedBufferGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        @Override
        public TypeSpec.Builder build()
        {
            FieldSpec expectedBufferFieldSpec = FieldSpec.builder(MutableDirectBuffer.class, "expectedBuffer", PRIVATE, FINAL)
                    .initializer("new $T($T.allocateDirect(100000)) \n" +
                            "{\n"+
                            "    {\n"+
                            "        // Make sure the code is not secretly relying upon memory being initialized to 0\n" +
                            "        setMemory(0, capacity(), (byte) 0xF);\n" +
                            "    }\n" +
                            "}", UnsafeBuffer.class, ByteBuffer.class)
                    .build();
            builder.addField(expectedBufferFieldSpec);

            return builder;
        }
    }

    private static final class FieldRWGenerator extends ClassSpecMixinGenerator
    {
        private final ClassName fieldFWBuilderClassName;

        private FieldRWGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName)
        {
            super(thisType, builder);
            fieldFWBuilderClassName = thisType.peerClass(baseName + "FW.Builder");
        }

        @Override
        public TypeSpec.Builder build()
        {
            FieldSpec fieldRWFieldSpec = FieldSpec.builder(fieldFWBuilderClassName, "fieldRW", PRIVATE, FINAL)
                    .initializer("new $T()", fieldFWBuilderClassName)
                    .build();
            builder.addField(fieldRWFieldSpec);

            return builder;
        }
    }

    private static final class FieldROGenerator extends ClassSpecMixinGenerator
    {
        private final ClassName fieldFWClassName;

        private FieldROGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName)
        {
            super(thisType, builder);
            fieldFWClassName = thisType.peerClass(baseName + "FW");
        }

        @Override
        public TypeSpec.Builder build()
        {
            FieldSpec fieldRWFieldSpec = FieldSpec.builder(fieldFWClassName,  "fieldRO", PRIVATE, FINAL)
                    .initializer("new $T()", fieldFWClassName)
                    .build();
            builder.addField(fieldRWFieldSpec);
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
            String sizeName,
            Object defaultValue)
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
                addNonPrimitiveMember(name, type, unsignedType, sizeName, defaultValue);
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
            TypeName unsignedType,
            String sizeName,
            Object defaultValue)
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

            if (defaultValue == NULL_DEFAULT && sizeName != null)
            {
                codeBlock.addStatement("return $L() == -1 ? null: $LRO", methodName(sizeName), name);
            }
            else
            {
                codeBlock.addStatement("return $LRO", name);
            }

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
                    if (defaultValue == NULL_DEFAULT)
                    {
                        builder.addStatement(
                            "$LRO.wrap(buffer, $L + $L, $L + $L + ((int) $L() == -1 ? 0 : (int) $L()))",
                            name, anchorLimit, offset(name), anchorLimit, offset(name), methodName(sizeName),
                            methodName(sizeName));
                    }
                    else
                    {
                        builder.addStatement("$LRO.wrap(buffer, $L + $L, $L + $L + (int) $L())",
                            name, anchorLimit, offset(name), anchorLimit, offset(name), methodName(sizeName));
                    }
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
            anchorLimit = name + "RO.limit()";
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

    private final class ShouldDefaultValuesMethodGenerator extends MethodSpecGenerator
    {
        private class FieldDefinition
        {
            String name;
            TypeName type;
            TypeName unsignedType;
            Object defaultValue;
            int size;
            String sizeName;

            FieldDefinition(String name, TypeName type, TypeName unsignedType, Object defaultValue, int size,
                                   String sizeName)
            {
                this.name = name;
                this.type = type;
                this.unsignedType = unsignedType;
                this.defaultValue = defaultValue;
                this.size = size;
                this.sizeName = sizeName;
            }
        }

        private boolean errorGenerating;

        private CodeBlock.Builder initializationsBlock;
        private CodeBlock.Builder expectationsBlock;
        private int expectedBufferIndex;
        private CodeBlock.Builder assertionsBlock;

        private ShouldDefaultValuesMethodGenerator()
        {
            super(methodBuilder("shouldDefaultValues")
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC)
                    .addException(Exception.class));

            this.errorGenerating = false;

            this.initializationsBlock = CodeBlock.builder().add("fieldRW.wrap(buffer, 0, 100)\n");
            this.expectationsBlock = CodeBlock.builder();
            this.expectedBufferIndex = 0;
            this.assertionsBlock = CodeBlock.builder()
                    .addStatement("$T.assertEquals(expectedBuffer.byteBuffer(), buffer.byteBuffer())", Assert.class);
        }

        private boolean isNumericType(FieldDefinition fd)
        {
            return fd.type.equals(TypeName.INT) ||
                    fd.type.equals(TypeName.LONG) ||
                    fd.type.equals(TypeName.SHORT) ||
                    fd.type.equals(TypeName.BYTE) ||
                    fd.type.equals(TypeName.DOUBLE) ||
                    fd.type.equals(TypeName.FLOAT);
        }

        private boolean isCharacterType(FieldDefinition fd)
        {
            return fd.type.equals(TypeName.CHAR);
        }

        private boolean isBooleanType(FieldDefinition fd)
        {
            return fd.type.equals(TypeName.BOOLEAN);
        }

        private boolean isArrayType(FieldDefinition fd)
        {
            return fd.name.contains("Array");
        }

        private CodeBlock generateArray(Class streamType, int numberOfEntries)
        {
            if (numberOfEntries == -1)
            {
                this.errorGenerating = true;
                throw new UnsupportedOperationException("Don't know how to generate variable length arrays");
            }

            CodeBlock.Builder builder = CodeBlock.builder();
            builder.add("$T.of(0", streamType);
            for (int i = 1; i < numberOfEntries; i++)
            {
                builder.add(", 0");
            }
            builder.add(").iterator()");

            return builder.build();
        }

        private CodeBlock generateNumericTypeValue(FieldDefinition fd)
        {
            CodeBlock.Builder builder = CodeBlock.builder();

            if (isArrayType(fd))
            {
                TypeName actualType = fd.unsignedType != null ? fd.unsignedType : fd.type;
                if (actualType.equals(TypeName.LONG))
                {
                    return generateArray(LongStream.class, fd.size);
                }
                else
                {
                    return generateArray(IntStream.class, fd.size);
                }
            }
            else
            {
                return builder.add("0").build();
            }
        }

        private CodeBlock generateCharacterTypeValue(FieldDefinition fd)
        {
            CodeBlock.Builder builder = CodeBlock.builder();

            if (isArrayType(fd))
            {
                return generateArray(CharStream.class, fd.size);
            }
            else
            {
                return builder.add("a").build();
            }
        }

        private CodeBlock generatePrimitiveValue(FieldDefinition fd)
        {
            if (isNumericType(fd))
            {
                return generateNumericTypeValue(fd);
            }
            else if (isCharacterType(fd))
            {
                return generateCharacterTypeValue(fd);
            }
            else if (isBooleanType(fd))
            {
                return CodeBlock.builder().add("true").build();
            }
            else
            {
                this.errorGenerating = true;
                throw new UnsupportedOperationException("Unknown Field Definition " + fd);
            }
        }

        private CodeBlock generateNonPrimitiveValue(FieldDefinition fd)
        {
            CodeBlock.Builder builder = CodeBlock.builder();

            // TODO: add more types here as they become supported
            if (!fd.type.toString().matches(".*\\.StringFW$"))
            {
                this.errorGenerating = true;
                throw new UnsupportedOperationException("Cannot build members of type " + fd.type);
            }

            return builder.add("($L)null", "String").build();
        }

        private CodeBlock generateValue(FieldDefinition fd)
        {
            if (fd.type.isPrimitive())
            {
                return generatePrimitiveValue(fd);
            }
            else
            {
                return generateNonPrimitiveValue(fd);
            }
        }

        private CodeBlock generateExpectationStatementPrimitiveValue(FieldDefinition fd)
        {
            CodeBlock.Builder builder = CodeBlock.builder().add("expectedBuffer.put");
            String typeLabel = "";

            int newBufferIndex = this.expectedBufferIndex;

            if (fd.type == TypeName.INT)
            {
                typeLabel = "Int";
                newBufferIndex += 4;
            }
            else if (fd.type == TypeName.LONG)
            {
                typeLabel = "Long";
                newBufferIndex += 8;
            }
            else if (fd.type == TypeName.SHORT)
            {
                typeLabel = "Short";
                newBufferIndex += 2;
            }
            else if (fd.type == TypeName.BYTE)
            {
                typeLabel = "Byte";
                newBufferIndex += 1;
            }
            else if (fd.type == TypeName.CHAR)
            {
                typeLabel = "Char";
                newBufferIndex += 1;
            }
            else
            {
                throw new UnsupportedOperationException("Unexpected primitive type " + fd.type);
            }

            Object defaultValue = fd.defaultValue != null ? fd.defaultValue : "0";
            builder.add("$L($L, ($L) $L); // $L\n", typeLabel, this.expectedBufferIndex, fd.type, defaultValue, fd.name);
            this.expectedBufferIndex = newBufferIndex;

            return builder.build();
        }

        private CodeBlock generateExpectationStatementPrimitiveArray(FieldDefinition fd)
        {
            CodeBlock.Builder builder = CodeBlock.builder();
            String typeLabel = "";

            int newBufferIndex = this.expectedBufferIndex;
            int unitSize;

            if (fd.type == TypeName.INT)
            {
                typeLabel = "Int";
                unitSize = 4;
            }
            else if (fd.type == TypeName.LONG)
            {
                typeLabel = "Long";
                unitSize = 8;
            }
            else if (fd.type == TypeName.SHORT)
            {
                typeLabel = "Short";
                unitSize = 2;
            }
            else if (fd.type == TypeName.BYTE)
            {
                typeLabel = "Byte";
                unitSize = 1;
            }
            else if (fd.type == TypeName.CHAR)
            {
                typeLabel = "Char";
                unitSize = 1;
            }
            else
            {
                throw new UnsupportedOperationException("Unexpected primitive array type " + fd.type);
            }

            for (int i = 0; i < fd.size; i++)
            {
                builder.add("expectedBuffer.put$L($L, ($L) $L); // $L $L\n", typeLabel, this.expectedBufferIndex, fd.type,
                        0, fd.name, fd.size);
                this.expectedBufferIndex += unitSize;
            }

            return builder.build();
        }

        private CodeBlock generateExpectationStatementPrimitive(FieldDefinition fd)
        {
            if (fd.name.matches(".*Array$"))
            {
                return generateExpectationStatementPrimitiveArray(fd);
            }
            else
            {
                return generateExpectationStatementPrimitiveValue(fd);
            }
        }

        private CodeBlock generateExpectationStatementNonPrimitive(FieldDefinition fd)
        {
            if (fd.defaultValue != null)
            {
                this.errorGenerating = true;
                throw new UnsupportedOperationException();
            }

            return CodeBlock.builder()
                    .add("expectedBuffer.putByte($L, (byte) -1); // $L, $T, $L\n", this.expectedBufferIndex++, fd.name,
                            fd.type, fd.size)
                    .build();
        }

        private CodeBlock generateExpectationStatement(FieldDefinition fd)
        {
            if (!fd.type.isPrimitive())
            {
                return generateExpectationStatementNonPrimitive(fd);
            }
            else
            {
                return generateExpectationStatementPrimitive(fd);
            }
        }

        public ShouldDefaultValuesMethodGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                int size,
                String sizeName,
                Object defaultValue)
        {
            FieldDefinition fd = new FieldDefinition(name, type, unsignedType, defaultValue, size, sizeName);

            this.expectationsBlock.add(generateExpectationStatement(fd));

            if (defaultValue == null)
            {
                this.initializationsBlock.add("    .$L(", fd.name).add(generateValue(fd)).add(")\n");
            }

            return this;
        }

        @Override
        public MethodSpec generate()
        {
            // TODO: once all types of members are supported, remove this exception throw
            if (this.errorGenerating)
            {
                throw new UnsupportedOperationException();
            }

            initializationsBlock.add("    .build()\n    .limit();\n");

            return builder
                    .addModifiers(PUBLIC)
                    .addException(Exception.class)
                    .addCode(initializationsBlock.build())
                    .addCode("\n")
                    .addCode(expectationsBlock.build())
                    .addCode("\n")
                    .addCode(assertionsBlock.build())
                    .build();
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
