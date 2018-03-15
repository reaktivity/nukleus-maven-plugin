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

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.lang.Long.highestOneBit;
import static java.lang.Long.numberOfTrailingZeros;
import static java.util.Collections.unmodifiableMap;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstMemberNode.NULL_DEFAULT;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.squareup.javapoet.*;
import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNodeLocator;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.generate.*;

public final class StructFlyweightTestGenerator extends ClassSpecGenerator
{
    private enum StringSetterVariant
    {
       STRINGFW,
       BUFFER,
       STRING
    }

    private enum OctetsSetterVariant
    {
        OCTETSFW,
        BUFFER,
        ARRAY,
        VISITOR
    }

    private static final Set<String> RESERVED_METHOD_NAMES = new HashSet<>(Arrays.asList(new String[]
            {
                    "offset", "buffer", "limit", "sizeof", "maxLimit", "wrap", "checkLimit", "build"
            }));

    private static final ClassName INT_STREAM_CLASS_NAME = ClassName.get(IntStream.class);
    private static final ClassName LONG_STREAM_CLASS_NAME = ClassName.get(LongStream.class);
    private static final ClassName LONG_ITERATOR_CLASS_NAME = ClassName.get(PrimitiveIterator.OfLong.class);

    private final String baseName;
    private final TypeSpec.Builder builder;
    private final MemberConstantGenerator memberConstant;
    private final TypeIdTestGenerator typeIdTestGenerator;
    private final BufferGenerator buffer;
    private final ExpectedBufferGenerator expectedBuffer;
    private final ExpectedExceptionGenerator expectedException;
    private final FieldRWGenerator fieldRW;
    private final FieldROGenerator fieldRO;
    private final SetBufferValuesMethodGenerator setAllBufferValuesMethodGenerator;
    private final SetBufferValuesMethodGenerator setRequiredBufferValuesMethodGenerator;

    private final SetAllBuilderValuesMethodGenerator setAllBuilderValuesMethodGeneratorPrimary;
    private final SetAllBuilderValuesMethodGenerator setAllBuilderValuesMethodGeneratorStringFW;
    private final SetAllBuilderValuesMethodGenerator setAllBuilderValuesMethodGeneratorBuffer;
    private final SetAllBuilderValuesMethodGenerator setAllBuilderValuesMethodGeneratorArray;


    private final SetAllFieldsValueTestMethodGenerator setAllFieldsValueTestMethodGeneratorPrimary;
    private final SetAllFieldsValueTestMethodGenerator setAllFieldsValueTestMethodGeneratorStringFW;
    private final SetAllFieldsValueTestMethodGenerator setAllFieldsValueTestMethodGeneratorBuffer;
    private final SetAllFieldsValueTestMethodGenerator setAllFieldsValueTestMethodGeneratorArray;

    private final ExceptionTestMethodGenerator exceptionTestMethodGenerator;
    private final DefaultValuesTestMethodGenerator defaultValuesTestMethodGenerator;
    private final ToStringTestMethodGenerator toStringTestMethodGenerator;
    private final ReadDefaultedValuesTestMethodGenerator readDefaultedValuesTestMethodGenerator;
    private final SetFieldUpToIndexMethodGenerator setFieldUpToIndexMethodGenerator;
    private final SetRequiredBuilderFieldMethodGenerator setRequiredBuilderFieldMethodGenerator;
    private final AssertRequiredValuesAndDefaultsMethodGenerator assertRequiredValuesAndDefaultsMethodGenerator;

    public StructFlyweightTestGenerator(
            ClassName structName,
            String baseName,
            AstNodeLocator astNodeLocator)
    {
        super(structName);
        this.baseName = baseName + "Test";
        this.builder = classBuilder(structName).addModifiers(PUBLIC, FINAL);
        this.memberConstant = new MemberConstantGenerator(structName, builder);
        this.typeIdTestGenerator = new TypeIdTestGenerator(structName, builder, baseName);
        this.buffer = new BufferGenerator(structName, builder); // should add tests for correct type set
        this.expectedBuffer = new ExpectedBufferGenerator(structName, builder);
        this.expectedException = new ExpectedExceptionGenerator(structName, builder);
        this.fieldRW = new FieldRWGenerator(structName, builder, baseName);
        this.fieldRO = new FieldROGenerator(structName, builder, baseName);
        this.setAllBufferValuesMethodGenerator = new SetBufferValuesMethodGenerator(true);
        this.setRequiredBufferValuesMethodGenerator = new SetBufferValuesMethodGenerator(false);

        this.setFieldUpToIndexMethodGenerator = new SetFieldUpToIndexMethodGenerator(structName, builder, baseName);
        this.setRequiredBuilderFieldMethodGenerator = new SetRequiredBuilderFieldMethodGenerator(structName, builder, baseName);
        this.assertRequiredValuesAndDefaultsMethodGenerator = new AssertRequiredValuesAndDefaultsMethodGenerator(structName,
                builder, baseName);
        this.toStringTestMethodGenerator = new ToStringTestMethodGenerator();
        this.defaultValuesTestMethodGenerator = new DefaultValuesTestMethodGenerator(structName, builder, baseName);
        this.readDefaultedValuesTestMethodGenerator = new ReadDefaultedValuesTestMethodGenerator();
        this.exceptionTestMethodGenerator = new ExceptionTestMethodGenerator(structName, builder, baseName);

        this.setAllBuilderValuesMethodGeneratorPrimary = new SetAllBuilderValuesMethodGenerator(structName, builder, baseName,
                StringSetterVariant.STRING,
                OctetsSetterVariant.OCTETSFW);
        this.setAllBuilderValuesMethodGeneratorStringFW = new SetAllBuilderValuesMethodGenerator(structName, builder, baseName,
                StringSetterVariant.STRINGFW,
                OctetsSetterVariant.VISITOR);
        this.setAllBuilderValuesMethodGeneratorBuffer = new SetAllBuilderValuesMethodGenerator(structName, builder, baseName,
                StringSetterVariant.BUFFER,
                OctetsSetterVariant.BUFFER);
        this.setAllBuilderValuesMethodGeneratorArray = new SetAllBuilderValuesMethodGenerator(structName, builder, baseName,
                StringSetterVariant.STRING,
                OctetsSetterVariant.ARRAY);


        this.setAllFieldsValueTestMethodGeneratorPrimary = new SetAllFieldsValueTestMethodGenerator(
                structName, builder, baseName,
                StringSetterVariant.STRING,
                OctetsSetterVariant.OCTETSFW);
        this.setAllFieldsValueTestMethodGeneratorStringFW = new SetAllFieldsValueTestMethodGenerator(
                structName, builder, baseName,
                StringSetterVariant.STRINGFW,
                OctetsSetterVariant.VISITOR);
        this.setAllFieldsValueTestMethodGeneratorBuffer = new SetAllFieldsValueTestMethodGenerator(
                structName, builder, baseName,
                StringSetterVariant.BUFFER,
                OctetsSetterVariant.BUFFER);
        this.setAllFieldsValueTestMethodGeneratorArray = new SetAllFieldsValueTestMethodGenerator(structName, builder, baseName,
                StringSetterVariant.STRING,
                OctetsSetterVariant.ARRAY);
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

        try
        {
            memberConstant.addMember(name, type, unsignedType, size, sizeName, defaultValue);
            setAllBufferValuesMethodGenerator.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                    byteOrder, defaultValue);
            setRequiredBufferValuesMethodGenerator.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                    byteOrder, defaultValue);
            setAllBuilderValuesMethodGeneratorPrimary.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                    byteOrder, defaultValue);
            setAllBuilderValuesMethodGeneratorStringFW.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                    byteOrder, defaultValue);
            setAllBuilderValuesMethodGeneratorBuffer.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                    byteOrder, defaultValue);
            setAllBuilderValuesMethodGeneratorArray.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                byteOrder, defaultValue);
            setFieldUpToIndexMethodGenerator.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                    byteOrder, defaultValue);
            setRequiredBuilderFieldMethodGenerator.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                    byteOrder, defaultValue);
            assertRequiredValuesAndDefaultsMethodGenerator.addMember(name, type, unsignedType, usedAsSize, size, sizeName,
                    sizeType, byteOrder, defaultValue);
            exceptionTestMethodGenerator.addMember(name, type, unsignedType, usedAsSize, size, sizeName,
                    sizeType, byteOrder, defaultValue);
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
        memberConstant.build();
        buffer.build();
        expectedBuffer.build();
        fieldRW.build();
        fieldRO.build();
        expectedException.build();
        typeIdTestGenerator.build();
        exceptionTestMethodGenerator.build();

        if(setAllBuilderValuesMethodGeneratorStringFW.hasVariant())
        {
            builder.addMethod(setAllBuilderValuesMethodGeneratorStringFW.generate());
            builder.addMethod(setAllFieldsValueTestMethodGeneratorStringFW.generate());
        }
        if(setAllBuilderValuesMethodGeneratorBuffer.hasVariant())
        {
            builder.addMethod(setAllBuilderValuesMethodGeneratorBuffer.generate());
            builder.addMethod(setAllFieldsValueTestMethodGeneratorBuffer.generate());
        }
        if(setAllBuilderValuesMethodGeneratorArray.hasVariant())
        {
            builder.addMethod(setAllBuilderValuesMethodGeneratorArray.generate());
            builder.addMethod(setAllFieldsValueTestMethodGeneratorArray.generate());
        }

        return builder
                .addMethod(setAllFieldsValueTestMethodGeneratorPrimary.generate())
                .addMethod(defaultValuesTestMethodGenerator.generate())
                .addMethod(readDefaultedValuesTestMethodGenerator.generate())
                .addMethod(toStringTestMethodGenerator.generate())
                .addMethod(setAllBufferValuesMethodGenerator.generate())
                .addMethod(setRequiredBufferValuesMethodGenerator.generate())
                .addMethod(setFieldUpToIndexMethodGenerator.generate())
                .addMethod(setRequiredBuilderFieldMethodGenerator.generate())
                .addMethod(setAllBuilderValuesMethodGeneratorPrimary.generate())
                .addMethod(assertRequiredValuesAndDefaultsMethodGenerator.generate())
                .build();
    }

    public StructFlyweightTestGenerator typeId(
            int typeId)
    {
        this.typeIdTestGenerator.setTypeId(typeId);
        return this;
    }

    private static final class TypeIdTestGenerator extends ClassSpecMixinGenerator
    {
        private int typeId;
        private final String baseName;

        private TypeIdTestGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName)
        {
            super(thisType, builder);
            this.baseName = baseName;
        }

        public void setTypeId(
                int typeId)
        {
            this.typeId = typeId;
        }


        @Override
        public TypeSpec.Builder build()
        {
            if (typeId != 0)
            {
                builder.addMethod(methodBuilder("shouldProvideTypeId")
                        .addModifiers(PUBLIC)
                        .addAnnotation(Test.class)
                        .addStatement("$T limit = setRequiredFields(fieldRW.wrap(buffer, 0, buffer.capacity()))" +
                                ".build()" +
                                ".limit()", int.class)
                        .addStatement("fieldRO.wrap(buffer, 0, limit)")
                        .addStatement("$T.assertEquals($L, $LFW.TYPE_ID)", Assert.class, String.format("0x%08x", typeId),
                                baseName)
                        .addStatement("$T.assertEquals($L, fieldRO.typeId())",
                                Assert.class, String.format("0x%08x", typeId))
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
                    .initializer("new $T($T.allocateDirect(150)); \n" +
                            "{\n" +
                            "    buffer.setMemory(0, buffer.capacity(), (byte) 0xF);\n" +
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
                    .initializer("new $T($T.allocateDirect(150)); \n" +
                            "{\n" +
                            "    expectedBuffer.setMemory(0, expectedBuffer.capacity(), (byte) 0xF);\n" +
                            "}", UnsafeBuffer.class, ByteBuffer.class)
                    .build();
            builder.addField(expectedBufferFieldSpec);

            return builder;
        }
    }

    private static final class ExpectedExceptionGenerator extends ClassSpecMixinGenerator
    {

        private ExpectedExceptionGenerator(
                ClassName thisType,
                TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        @Override
        public TypeSpec.Builder build()
        {
            FieldSpec expectedBufferFieldSpec = FieldSpec.builder(ExpectedException.class, "expectedException", PUBLIC)
                    .addAnnotation(Rule.class)
                    .initializer("$T.none()", ExpectedException.class)
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
            FieldSpec fieldRWFieldSpec = FieldSpec.builder(fieldFWClassName, "fieldRO", PRIVATE, FINAL)
                    .initializer("new $T()", fieldFWClassName)
                    .build();
            builder.addField(fieldRWFieldSpec);
            return builder;
        }
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
            boolean isOctetsType = isOctetsType(type);
            if (defaultValue != null && !isOctetsType)
            {
                Object defaultValueToSet = defaultValue == NULL_DEFAULT ? null : defaultValue;
                TypeName generateType = (unsignedType != null) ? unsignedType : type;
                if (size != -1 || sizeName != null)
                {
                    generateType = generateType == TypeName.LONG ? LONG_STREAM_CLASS_NAME
                            : INT_STREAM_CLASS_NAME;
                }
                if (isVarint32Type(type))
                {
                    generateType = TypeName.INT;
                }
                else if (isVarint64Type(type))
                {
                    generateType = TypeName.LONG;
                }
                builder.addField(
                        FieldSpec.builder(generateType, defaultName(name), PRIVATE, STATIC, FINAL)
                                .initializer(Objects.toString(defaultValueToSet))
                                .build());
                fieldsWithDefaultsInitializer.addStatement("set($L)", index(name));
            }
            else if (isImplicitlyDefaulted(type, size, sizeName))
            {
                fieldsWithDefaultsInitializer.addStatement("set($L)", index(name));
            }
            return this;
        }

        @Override
        public TypeSpec.Builder build()
        {
            builder.addField(FieldSpec.builder(String[].class, "FIELD_NAMES", PRIVATE, STATIC, FINAL)
                    .initializer("{\n  \"" + String.join("\",\n  \"", fieldNames) + "\"\n}")
                    .build());

            return super.build();
        }
    }

    private static final class SetBufferValuesMethodGenerator extends MethodSpecGenerator
    {
        private static final Map<TypeName, String> PUTTER_NAMES;
        private static final Map<TypeName, String> TYPE_SIZE;
        private static final Map<TypeName, String> SIZEOF_BY_NAME = initSizeofByName();
        private final CodeBlock.Builder defaultValueBuilder;
        private final CodeBlock.Builder codeBuilder;

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

        static
        {
            HashMap<TypeName, String> sizeNames = new HashMap<>();
            sizeNames.put(TypeName.BYTE, Integer.toString(BitUtil.SIZE_OF_BYTE));
            sizeNames.put(TypeName.CHAR, Integer.toString(BitUtil.SIZE_OF_CHAR));
            sizeNames.put(TypeName.SHORT, Integer.toString(BitUtil.SIZE_OF_SHORT));
            sizeNames.put(TypeName.FLOAT, Integer.toString(BitUtil.SIZE_OF_FLOAT));
            sizeNames.put(TypeName.INT, Integer.toString(BitUtil.SIZE_OF_INT));
            sizeNames.put(TypeName.DOUBLE, Integer.toString(BitUtil.SIZE_OF_DOUBLE));
            sizeNames.put(TypeName.LONG, Integer.toString(BitUtil.SIZE_OF_LONG));
            TYPE_SIZE = unmodifiableMap(sizeNames);
        }

        private int valueIncrement = 0;
        private int priorValueSize = 0;
        private final boolean allFields;

        private SetBufferValuesMethodGenerator(
                boolean allFields)
        {
            super(methodBuilder(allFields ? "setAllBufferValues" : "setRequiredBufferValues")
                    .addModifiers(STATIC)
                    .addParameter(MUTABLE_DIRECT_BUFFER_TYPE, "buffer")
                    .addParameter(int.class, "offset")
                    .returns(int.class)
                    .addModifiers(PUBLIC));
            this.allFields = allFields;
            this.defaultValueBuilder = CodeBlock.builder();
            this.codeBuilder = CodeBlock.builder();
        }

        public SetBufferValuesMethodGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            valueIncrement++;

            if (type.isPrimitive())
            {
                if (sizeName != null)
                {
                    addIntegerVariableArray(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);

                }
                else if (size != -1)
                {
                    addIntegerFixedArray(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);
                }
                else
                {
                    if(allFields)
                    {
                        addPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                                byteOrder, null);
                    }
                    else
                    {
                        addPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                                byteOrder, defaultValue);
                    }
                }
            }
            else
            {
                addNonPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                        byteOrder, defaultValue);
            }

            return this;
        }

        private void addPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            String putterName = PUTTER_NAMES.get(type);
            if (putterName == null)
            {
                throw new IllegalStateException("member type not supported: " + type);
            }

            if (defaultValue != null)
            {
                codeBuilder.addStatement("buffer.$L(offset += $L, ($L) $L)",
                        putterName,
                        priorValueSize,
                        SIZEOF_BY_NAME.get(type).toLowerCase(),
                        defaultValue.toString());
                priorValueSize = Integer.parseInt(TYPE_SIZE.get(type));
            }
            else
            {
                if(usedAsSize)
                {
                    codeBuilder.addStatement("buffer.$L(offset += $L, ($L) $L)",
                            putterName,
                            priorValueSize,
                            SIZEOF_BY_NAME.get(type).toLowerCase(),
                            dynamicValue(name));
                }
                else
                {
                    codeBuilder.addStatement("buffer.$L(offset += $L, ($L) $L)",
                            putterName,
                            priorValueSize,
                            SIZEOF_BY_NAME.get(type).toLowerCase(),
                            valueIncrement);
                }
                priorValueSize = Integer.parseInt(TYPE_SIZE.get(type));
            }
        }

        private void addIntegerFixedArray(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            String putterName = PUTTER_NAMES.get(type);
            if (putterName == null)
            {
                throw new IllegalStateException("member type not supported: " + type);
            }

            for(int i = 0; i < size; i++)
            {
                codeBuilder.addStatement("buffer.$L(offset += $L, ($L) $L)",
                        putterName,
                        priorValueSize,
                        SIZEOF_BY_NAME.get(type).toLowerCase(),
                        valueIncrement);
                priorValueSize = Integer.parseInt(TYPE_SIZE.get(type));
            }
        }

        private void addIntegerVariableArray(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            String putterName = PUTTER_NAMES.get(type);
            if (putterName == null)
            {
                throw new IllegalStateException("member type not supported: " + type);
            }

            TypeName inputType = (unsignedType != null) ? unsignedType : type;
            if (defaultValue == NULL_DEFAULT && !allFields)
            {
                size = -1;
                defaultValueBuilder.addStatement("$T $L = $L", sizeType, dynamicValue(sizeName), size);
            }
            else
            {
                size = 2;
                defaultValueBuilder.addStatement("$T $L = $L", sizeType, dynamicValue(sizeName), size);
            }

            for(int i = 0; i < size; i++)
            {
                codeBuilder.addStatement("buffer.$L(offset += $L, ($L) $L)",
                        putterName,
                        priorValueSize,
                        inputType,
                        valueIncrement);
                priorValueSize = Integer.parseInt(TYPE_SIZE.get(type));
            }
        }

        private void addNonPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            if (type instanceof ClassName)
            {
                ClassName className = (ClassName) type;
                addClassType(name, className, usedAsSize, size, sizeName, sizeType, defaultValue);
            }
            else if (type instanceof ParameterizedTypeName)
            {
                ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
                addParameterizedType(name, parameterizedType, defaultValue);
            }

        }

        private void addParameterizedType(
                String name,
                ParameterizedTypeName parameterizedType,
                Object defaultValue)
        {
            ClassName rawType = parameterizedType.rawType;
            ClassName itemType = (ClassName) parameterizedType.typeArguments.get(0);
            ClassName builderRawType = rawType.nestedClass("Builder");
            ClassName itemBuilderType = itemType.nestedClass("Builder");
            ParameterizedTypeName builderType = ParameterizedTypeName.get(builderRawType, itemBuilderType, itemType);

            ClassName consumerType = ClassName.get(Consumer.class);
            TypeName mutatorType = ParameterizedTypeName.get(consumerType, builderType);

            if ("ListFW".equals(rawType.simpleName()))
            {
                // Add a method to append list items
                if ("StringFW".equals(itemType.simpleName()))
                {
                    if (allFields)
                    {
                        codeBuilder.addStatement("buffer.putInt(offset += $L, 14)", priorValueSize)
                                .addStatement("buffer.putByte(offset += 4, (byte) \"value$L\".length())",
                                        valueIncrement)
                                .addStatement("buffer.putBytes(offset += 1, \"value$L\".getBytes($T.UTF_8))",
                                        valueIncrement,
                                        StandardCharsets.class)
                                .addStatement("buffer.putByte(offset += 6, (byte) \"value$L\".length())",
                                        valueIncrement)
                                .addStatement("buffer.putBytes(offset += 1, \"value$L\".getBytes($T.UTF_8))",
                                        valueIncrement,
                                        StandardCharsets.class);
                        priorValueSize = ("value" + valueIncrement).length();
                    }
                    else
                    {
                        codeBuilder.addStatement("buffer.putInt(offset += $L, 0)", priorValueSize);
                        priorValueSize = 4;
                    }
                }
            }
            else if ("ArrayFW".equals(rawType.simpleName()))
            {
                if (itemType.simpleName().contains("Varint"))
                {
                    codeBuilder.addStatement("buffer.putInt(offset += $L, 2)", priorValueSize)
                            .addStatement("buffer.putByte(offset += 4, (byte) 2)")
                            .addStatement("buffer.putByte(offset += 1, (byte) 4)");
                    priorValueSize = 1;
                }
            }
        }

        private void addClassType(
                String name,
                ClassName className,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                Object defaultValue)
        {
            if (isStringType(className))
            {
                codeBuilder.addStatement("buffer.putByte(offset += $L, (byte) \"value$L\".length())",
                        priorValueSize,
                        valueIncrement)
                    .addStatement("buffer.putBytes(offset += 1, \"value$L\".getBytes($T.UTF_8))",
                        valueIncrement,
                        StandardCharsets.class);
                priorValueSize = ("value" + valueIncrement).length();
            }
            else if ("OctetsFW".equals(className.simpleName()))
            {
                if (size >= 0)
                {
                    String value = "";
                    for (int i = 0; i < size; i++)
                    {
                        value += i;
                    }

                    codeBuilder.addStatement("buffer.putBytes(offset += $L, \"$L\".getBytes($T.UTF_8))",
                            priorValueSize, value, StandardCharsets.class);
                    priorValueSize = size;
                }
                else if (sizeName != null)
                {
                    if (defaultValue == NULL_DEFAULT && !allFields)
                    {
                        if(isVarintType(sizeType))
                        {
                            defaultValueBuilder.addStatement("int $L = 1", dynamicValue(sizeName));
                        }
                        else
                        {
                            defaultValueBuilder.addStatement("int $L = -1", dynamicValue(sizeName));
                        }

                    }
                    else
                    {
                        if (isVarintType(sizeType) && allFields)
                        {
                            defaultValueBuilder.addStatement("$T $L = 4", int.class, dynamicValue(sizeName));
                            codeBuilder.addStatement("buffer.putByte(offset += $L, ($T) 'x')", priorValueSize, byte.class)
                                    .addStatement("buffer.putByte(offset += 1, ($T) 'y')", byte.class);
                            priorValueSize = 1;
                        }
                        else
                        {
                            defaultValueBuilder.addStatement("$T $L = 2", sizeType, dynamicValue(sizeName));
                            codeBuilder.addStatement("buffer.putByte(offset += $L, ($T) 'x')",
                                            priorValueSize, byte.class)
                                    .addStatement("buffer.putByte(offset += 1, ($T) 'y')", byte.class);
                            priorValueSize = 1;
                        }
                    }
                }
                else
                {
                    codeBuilder.addStatement("buffer.putByte(offset += $L, ($T) 'x')",
                            priorValueSize, byte.class);
                    priorValueSize = 1;
                }
            }
            else
            {
                if (isVarintType(className))
                {
                    if(usedAsSize)
                    {
                        codeBuilder.addStatement("buffer.putByte(offset += $L, ($T) $L)",
                                priorValueSize, byte.class, dynamicValue(name));
                    }
                    else
                    {
                        if(allFields)
                        {
                            codeBuilder.addStatement("buffer.putByte(offset += $L, ($T) $L)",
                                    priorValueSize, byte.class, 4);
                        }
                        else
                        {
                            codeBuilder.addStatement("buffer.putByte(offset += $L, ($T) $L)",
                                    priorValueSize, byte.class, defaultValue);
                        }

                    }

                    priorValueSize = 1;
                }
                else
                {
                    if(this.allFields)
                    {
                        codeBuilder.addStatement("offset = $TTest.setAllBufferValues(buffer, offset += $L) ",
                                className, priorValueSize);
                    }
                    else
                    {
                        codeBuilder.addStatement("offset = $TTest.setRequiredBufferValues(buffer, offset += $L)",
                                className, priorValueSize);
                    }
                    priorValueSize = 0;
                }
            }
        }

        @Override
        public MethodSpec generate()
        {
            return builder.addCode(defaultValueBuilder.build())
                    .addCode(codeBuilder.build())
                    .addStatement("return offset + " + priorValueSize).build();
        }
    }


    private static final class SetAllBuilderValuesMethodGenerator extends MethodSpecGenerator
    {
        private String priorDefaulted;
        private int valueIncrement = 0;
        private int stringBufferOffset = 0;
        private boolean hasOctetsType = false;
        private boolean hasStringType = false;
        private final StringSetterVariant stringSetterVariant;
        private final OctetsSetterVariant octetsSetterVariant;

        private SetAllBuilderValuesMethodGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName,
                StringSetterVariant stringSetterVariant,
                OctetsSetterVariant octetsSetterVariant)
        {
            super(methodBuilder("setAllFieldsValue" + stringSetterVariant + "_" + octetsSetterVariant)
                        .addModifiers(STATIC)
                        .addParameter(thisType.peerClass(baseName + "FW.Builder"), "builder")
                        .returns(thisType.peerClass(baseName + "FW.Builder")));
            this.stringSetterVariant = stringSetterVariant;
            this.octetsSetterVariant = octetsSetterVariant;
        }

        public boolean hasVariant()
        {
            return (hasStringType || hasOctetsType);
        }

        public SetAllBuilderValuesMethodGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            name = methodName(name);
            valueIncrement++;
            priorDefaulted = name;

            if (type.isPrimitive())
            {
                if (sizeName != null)
                {
                    addIntegerVariableArray(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);
                }
                else if (size != -1)
                {
                    addIntegerFixedArray(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);
                }
                else
                {
                    if(!usedAsSize)
                    {
                        setPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                                byteOrder, null);
                    }
                }
            }
            else
            {
                //TODO: Add nonprimative member
                setNonPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                        byteOrder, null, priorDefaulted);
            }

            return this;
        }

        private void addIntegerVariableArray(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            builder.addStatement("builder.$L(($L)$L)",
                        appendMethodName(name),
                        type.toString().toLowerCase(),
                        valueIncrement)
                    .addStatement("builder.$L(($L)$L)",
                        appendMethodName(name),
                        type.toString().toLowerCase(),
                        valueIncrement);
        }

        private void addIntegerFixedArray(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            for (int i = 0; i < size; i++)
            {
                builder.addStatement("builder.$L(($L)$L)",
                        appendMethodName(name),
                        type.toString().toLowerCase(),
                        valueIncrement);
            }
        }

        private void setPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            builder.addStatement("builder.$L(($L)$L)",
                    name,
                    type.toString().toLowerCase(),
                    valueIncrement);
        }

        private void setNonPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue,
                String priorDefaulted)
        {
            if (type instanceof ClassName)
            {
                ClassName className = (ClassName) type;
                addClassType(name, className, usedAsSize, size, sizeName, sizeType, defaultValue, priorDefaulted);

            }
            else if (type instanceof ParameterizedTypeName)
            {
                ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
                addParameterizedType(name, parameterizedType, defaultValue);
            }
        }

        private void addParameterizedType(
                String name,
                ParameterizedTypeName parameterizedType,
                Object defaultValue)
        {
            ClassName rawType = parameterizedType.rawType;
            ClassName itemType = (ClassName) parameterizedType.typeArguments.get(0);
            ClassName builderRawType = rawType.nestedClass("Builder");
            ClassName itemBuilderType = itemType.nestedClass("Builder");
            ParameterizedTypeName builderType = ParameterizedTypeName.get(builderRawType, itemBuilderType, itemType);

            ClassName consumerType = ClassName.get(Consumer.class);
            TypeName mutatorType = ParameterizedTypeName.get(consumerType, builderType);

            if ("ListFW".equals(rawType.simpleName()))
            {
                // Add a method to append list items
                if ("StringFW".equals(itemType.simpleName()))
                {
                    builder.addStatement("builder.$L(b -> b.item(i -> i.set(\"value$L\", $T.UTF_8)))",
                            methodName(name),
                            valueIncrement,
                            StandardCharsets.class)
                        .addStatement("builder.$LItem(b -> b.set(\"value$L\", $T.UTF_8))",
                            methodName(name),
                            valueIncrement,
                            StandardCharsets.class);
                }
            }
            else if ("ArrayFW".equals(rawType.simpleName()))
            {
                if (itemType.simpleName().contains("Varint32"))
                {
                    builder.addStatement("builder.$L(a -> a.item(b -> b.set(1))\n" +
                            "                        .item(b -> b.set(2)))", methodName(name));

                }
                else if (itemType.simpleName().contains("Varint64"))
                {
                    builder.addStatement("builder.$L(a -> a.item(b -> b.set(1L))\n" +
                            "                        .item(b -> b.set(2L)))", methodName(name));
                }
            }
        }

        private void addClassType(
                String name,
                ClassName className,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                Object defaultValue,
                String priorFieldIfDefaulted)
        {
            if (isStringType(className))
            {
                ClassName builderType = className.nestedClass("Builder");

                switch (stringSetterVariant)
                {
                    case STRINGFW:
                        if(!hasStringType)
                        {
                            hasStringType = true;
                            builder.addStatement("final $T stringRW = new $T()",
                                    builderType,
                                    builderType)
                                    .addStatement("final $T valueBuffer = new $T($T.allocateDirect(100))",
                                            MutableDirectBuffer.class,  UnsafeBuffer.class, ByteBuffer.class);
                        }
                        builder.addStatement("$T value$L = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())" +
                            ".set(\"value$L\", $T.UTF_8)" +
                            ".build()", className, valueIncrement, valueIncrement, StandardCharsets.class)
                            .addStatement("builder.$L(value$L)",
                                    name,
                                    valueIncrement);
                        break;
                    case BUFFER:
                        if(!hasStringType)
                        {
                            hasStringType = true;
                            builder.addStatement("final $T valueBuffer = new $T($T.allocateDirect(100))",
                                    MutableDirectBuffer.class, UnsafeBuffer.class, ByteBuffer.class);
                        }
                        builder.addStatement("valueBuffer.putStringWithoutLengthUtf8($L, \"value$L\")", stringBufferOffset,
                            valueIncrement)
                            .addStatement("builder.$L(valueBuffer, $L, 6)",
                                    name,
                                    stringBufferOffset);
                        stringBufferOffset+=10;
                        break;
                    case STRING:
                        builder.addStatement("builder.$L(\"value$L\")",
                                name,
                                valueIncrement);
                }
            }
            else if ("OctetsFW".equals(className.simpleName()))
            {
                hasOctetsType = true;
                String value = "";
                if (size >= 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        value += i;
                    }
                }
                else if (sizeName != null)
                {
                    value = "xy";
                }
                else
                {
                    value = "x";
                }
                switch (octetsSetterVariant)
                {
                    case OCTETSFW:
                        builder.addStatement("builder.$L($T.asOctetsFW(\"$L\"))", methodName(name),
                                className.peerClass("OctetsFWTest"), value);
                        break;
                    case BUFFER:
                        builder.addStatement("builder.$L($T.asBuffer(\"$L\"), 0, \"$L\".length())", methodName(name),
                                className.peerClass("OctetsFWTest"), value, value);
                        break;
                    case VISITOR:
                        builder.addStatement("builder.$L(b -> b.put(\"$L\".getBytes($T.UTF_8)))", methodName(name),
                                value, StandardCharsets.class);
                        break;
                    case ARRAY:
                        builder.addStatement("builder.$L(b -> b.set(\"$L\".getBytes($T.UTF_8)))", methodName(name),
                                value, StandardCharsets.class);
                        break;
                }
            }
            else
            {
                if (isVarintType(className))
                {
                    builder.addStatement("builder.$L(2)", methodName(name));
                }
                else
                {
                    builder.addStatement("builder.$L(field -> $TTest.setAllFieldsValue" + stringSetterVariant + "_" +
                                    octetsSetterVariant + "(field))",
                            methodName(name), className);
                }
            }
        }

        @Override
        public MethodSpec generate()
        {
            return builder.addStatement("return builder").build();
        }
    }

    private static final class SetRequiredBuilderFieldMethodGenerator extends MethodSpecGenerator
    {
        private String priorDefaulted;
        private int valueIncrement = 0;
        private int stringBufferOffset = 0;
        private boolean hasPrimiteve = false;
        private boolean hasStringType = false;

        private SetRequiredBuilderFieldMethodGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName)
        {
            super(methodBuilder("setRequiredFields")
                    .addModifiers(STATIC)
                    .addParameter(thisType.peerClass(baseName + "FW.Builder"), "builder")
                    .returns(thisType.peerClass(baseName + "FW.Builder")));
        }

        public SetRequiredBuilderFieldMethodGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            valueIncrement++;

            if(defaultValue != null || usedAsSize)
            {
                return this;
            }
            priorDefaulted = name;

            if (type.isPrimitive())
            {
                if (sizeName != null)
                {
                    addIntegerVariableArray(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);
                }
                else if (size != -1)
                {
                    addIntegerFixedArray(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);
                }
                else
                {
                    setPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, null);
                }
            }
            else
            {
                setNonPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                        byteOrder, null, priorDefaulted);
            }

            return this;
        }

        private void addIntegerVariableArray(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            builder.addStatement("builder.$L(($L)$L)",
                    appendMethodName(name),
                    type.toString().toLowerCase(),
                    valueIncrement)
                    .addStatement("builder.$L(($L)$L)",
                            appendMethodName(name),
                            type.toString().toLowerCase(),
                            valueIncrement);
        }

        private void setPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            builder.addStatement("builder.$L(($L)$L)",
                    methodName(name),
                    type.toString().toLowerCase(),
                    valueIncrement);
            hasPrimiteve = true;
        }

        private void addIntegerFixedArray(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            TypeName inputType = (unsignedType != null) ? unsignedType : type;
            TypeName iteratorType = inputType == TypeName.LONG ? LONG_STREAM_CLASS_NAME
                    : INT_STREAM_CLASS_NAME;

            String value = "";
            for (int i = 0; i < size; i++)
            {
                value += " " + valueIncrement + ",";
            }

            builder.addStatement("builder.$L($T.of($L).iterator())",
                    methodName(name),
                    iteratorType,
                    removeChars(value, 1));
        }

        private void setNonPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue,
                String priorDefaulted)
        {
            if (type instanceof ClassName)
            {
                ClassName className = (ClassName) type;
                addClassType(name, className, usedAsSize, size, sizeName, sizeType, defaultValue, priorDefaulted);

            }
            else if (type instanceof ParameterizedTypeName)
            {
                ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
                addParameterizedType(name, parameterizedType, defaultValue);
            }
        }

        private void addParameterizedType(
                String name,
                ParameterizedTypeName parameterizedType,
                Object defaultValue)
        {
            ClassName rawType = parameterizedType.rawType;
            ClassName itemType = (ClassName) parameterizedType.typeArguments.get(0);
            ClassName builderRawType = rawType.nestedClass("Builder");
            ClassName itemBuilderType = itemType.nestedClass("Builder");
            ParameterizedTypeName builderType = ParameterizedTypeName.get(builderRawType, itemBuilderType, itemType);

            ClassName consumerType = ClassName.get(Consumer.class);
            TypeName mutatorType = ParameterizedTypeName.get(consumerType, builderType);

            if ("ListFW".equals(rawType.simpleName()))
            {
                // Add a method to append list items
            }
        }

        private void addClassType(
                String name,
                ClassName className,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                Object defaultValue,
                String priorFieldIfDefaulted)
        {
            if (isStringType(className))
            {
                builder.addStatement("builder.$L(\"value$L\")",
                        methodName(name),
                        valueIncrement);
            }
            else if ("OctetsFW".equals(className.simpleName()))
            {
                String value = "";
                if (size >= 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        value += i;
                    }
                    builder.addStatement("builder.$L($T.asOctetsFW(\"$L\"))", methodName(name),
                            className.peerClass("OctetsFWTest"), value);
                }
                else if (sizeName != null)
                {
                    value = "xy";
                    if (defaultValue != NULL_DEFAULT)
                    {
                        builder.addStatement("builder.$L($T.asOctetsFW(\"$L\"))", methodName(name),
                                className.peerClass("OctetsFWTest"), value);

                    }
                }
                else
                {
                    value = "x";
                    builder.addStatement("builder.$L($T.asOctetsFW(\"$L\"))", methodName(name),
                            className.peerClass("OctetsFWTest"), value);
                }
            }
            else
            {
                if (!isVarintType(className))
                {
                    builder.addStatement("builder.$L(field -> $TTest.setRequiredFields(field))",
                            methodName(name), className);
                }
            }
        }

        @Override
        public MethodSpec generate()
        {
            return builder.addStatement("return builder").build();
        }
    }

    private static final class SetFieldUpToIndexMethodGenerator extends MethodSpecGenerator
    {
        private String priorDefaulted;
        private int valueIncrement = 0;

        private SetFieldUpToIndexMethodGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName)
        {
            super(methodBuilder("setFieldUpToIndex")
                    .addModifiers(STATIC)
                    .addParameter(thisType.peerClass(baseName + "FW.Builder"), "builder")
                    .addParameter(int.class, "toFieldIndex")
                    .returns(thisType.peerClass(baseName + "FW.Builder")));
        }

        public SetFieldUpToIndexMethodGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            valueIncrement++;
            priorDefaulted = name;

            if (type.isPrimitive())
            {
                if (sizeName != null)
                {
                    //TODO: Add IntegerVariableArray
                } else if (size != -1)
                {
                    addIntegerFixedArray(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);
                }
                else
                {
                    setPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, null);
                }
            }
            else
            {
                //TODO: Add nonprimative member
                setNonPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                        byteOrder, null, priorDefaulted);
            }

            return this;
        }

        private void addIntegerFixedArray(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            builder.beginControlFlow("if(toFieldIndex > $L)", index(name));
            for (int i = 0; i < size; i++)
            {
                builder.addStatement("builder.$L(($L)$L)",
                        appendMethodName(name),
                        type.toString().toLowerCase(),
                        valueIncrement);
            }
            builder.endControlFlow();
        }

        private void setPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            if(!usedAsSize)
            {
                builder.beginControlFlow("if(toFieldIndex > $L)", index(name));
                builder.addStatement("builder.$L(($L)$L)",
                        methodName(name),
                        type.toString().toLowerCase(),
                        valueIncrement);
                builder.endControlFlow();
            }
        }

        private void setNonPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue,
                String priorDefaulted)
        {
            if (type instanceof ClassName)
            {
                ClassName className = (ClassName) type;
                addClassType(name, className, usedAsSize, size, sizeName, sizeType, defaultValue, priorDefaulted);

            }
            else if (type instanceof ParameterizedTypeName)
            {
                ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
                addParameterizedType(name, parameterizedType, defaultValue);
            }
        }

        private void addParameterizedType(
                String name,
                ParameterizedTypeName parameterizedType,
                Object defaultValue)
        {
            ClassName rawType = parameterizedType.rawType;
            ClassName itemType = (ClassName) parameterizedType.typeArguments.get(0);
            ClassName builderRawType = rawType.nestedClass("Builder");
            ClassName itemBuilderType = itemType.nestedClass("Builder");
            ParameterizedTypeName builderType = ParameterizedTypeName.get(builderRawType, itemBuilderType, itemType);

            ClassName consumerType = ClassName.get(Consumer.class);
            TypeName mutatorType = ParameterizedTypeName.get(consumerType, builderType);

            if ("ListFW".equals(rawType.simpleName()))
            {
                // Add a method to append list items
                if ("StringFW".equals(itemType.simpleName()))
                {
                    builder.beginControlFlow("if(toFieldIndex > $L)", index(name))
                            .addStatement("builder.$L(b -> b.item(i -> i.set(\"value$L\", $T.UTF_8)))",
                                    methodName(name),
                                    valueIncrement,
                                    StandardCharsets.class)
                            .addStatement("builder.$LItem(b -> b.set(\"value$L\", $T.UTF_8))",
                                methodName(name),
                                valueIncrement,
                                StandardCharsets.class)
                            .endControlFlow();
                }
            }
        }

        private void addClassType(
                String name,
                ClassName className,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                Object defaultValue,
                String priorFieldIfDefaulted)
        {
            if (isStringType(className))
            {
                builder.beginControlFlow("if(toFieldIndex > $L)", index(name))
                    .addStatement("builder.$L(\"value$L\")",
                        methodName(name),
                        valueIncrement)
                    .endControlFlow();

            }
            else if ("OctetsFW".equals(className.simpleName()))
            {
                String value = "";
                if (size >= 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        value += i;
                    }

                    builder.beginControlFlow("if(toFieldIndex > $L)", index(name))
                            .addStatement("builder.$L($T.asOctetsFW(\"$L\"))", methodName(name),
                                className.peerClass("OctetsFWTest"), value)
                            .endControlFlow();
                }
                else if (sizeName != null)
                {
                    value = "xy";

                    if(isVarintType(sizeType))
                    {
                        builder.beginControlFlow("if(toFieldIndex > $L)", index(name))
                                .addStatement("builder.$L(2)", methodName(sizeName))
                                .addStatement("builder.$L($T.asOctetsFW(\"$L\"))", methodName(name),
                                        className.peerClass("OctetsFWTest"), value)
                                .endControlFlow();
                    }
                    else
                    {
                        builder.beginControlFlow("if(toFieldIndex > $L)", index(name))
                                .addStatement("builder.$L($T.asOctetsFW(\"$L\"))", methodName(name),
                                        className.peerClass("OctetsFWTest"), value)
                                .endControlFlow();
                    }


                }
                else
                {
                    value = "x";
                    builder.beginControlFlow("if(toFieldIndex > $L)", index(name))
                                .addStatement("builder.$L($T.asOctetsFW(\"$L\"))", methodName(name),
                            className.peerClass("OctetsFWTest"), value)
                            .endControlFlow();
                }
            }
            else
            {
                if (isVarintType(className))
                {
                    if(!usedAsSize)
                    {
                        builder.beginControlFlow("if(toFieldIndex > $L)", index(name))
                                .addStatement("builder.$L(2)", methodName(name))
                                .endControlFlow();
                    }
                }
                else
                {
                    builder.beginControlFlow("if(toFieldIndex > $L)", index(name))
                            .addStatement("builder.$L(field -> $TTest.setRequiredFields(field))",
                                    methodName(name),
                                    className)
                            .endControlFlow();
                }
            }
        }

        @Override
        public MethodSpec generate()
        {
            return builder.addStatement("return builder").build();
        }
    }

    private static final class AssertRequiredValuesAndDefaultsMethodGenerator extends MethodSpecGenerator
    {
        private String priorDefaulted;
        private int valueIncrement = 0;
        private int stringBufferOffset = 0;
        private boolean hasPrimiteve = false;
        private boolean hasStringType = false;

        private AssertRequiredValuesAndDefaultsMethodGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName)
        {
            super(methodBuilder("assertRequiredValuesAndDefaults")
                    .addModifiers(STATIC)
                    .addParameter(thisType.peerClass(baseName + "FW"), "flyweight"));
        }

        public AssertRequiredValuesAndDefaultsMethodGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {

            valueIncrement++;
            if(usedAsSize)
            {
                return this;
            }
            priorDefaulted = name;
            name = methodName(name);

            if (type.isPrimitive())
            {
                if (sizeName != null)
                {
                    //TODO: Add IntegerVariableArray
                } else if (size != -1)
                {
                    addIntegerFixedArray(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);
                }
                else
                {
                    setPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);
                }
            }
            else
            {
                setNonPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                        byteOrder, null, priorDefaulted);
            }

            return this;
        }

        private void addIntegerFixedArray(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {

            TypeName generateType = (unsignedType != null) ? unsignedType : type;
            if (generateType == TypeName.LONG)
            {
                builder.addStatement("$T.assertEquals($L, flyweight.$L().nextLong())",
                        Assert.class,
                        valueIncrement,
                        methodName(name));
            }
            else
            {
                builder.addStatement("$T.assertEquals($L, flyweight.$L().nextInt())",
                        Assert.class,
                        valueIncrement,
                        methodName(name));
            }
        }

        private void setPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            if(defaultValue != null)
            {
                builder.addStatement("$T.assertEquals($L, flyweight.$L())",
                        Assert.class,
                        defaultValue.toString(),
                        methodName(name));
            }
            else
            {
                builder.addStatement("$T.assertEquals($L, flyweight.$L())",
                        Assert.class,
                        valueIncrement,
                        methodName(name));
            }

            hasPrimiteve = true;
        }

        private void setNonPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue,
                String priorDefaulted)
        {
            if (type instanceof ClassName)
            {
                ClassName className = (ClassName) type;
                addClassType(name, className, usedAsSize, size, sizeName, sizeType, defaultValue, priorDefaulted);

            }
            else if (type instanceof ParameterizedTypeName)
            {

            }
        }

        private void addClassType(
                String name,
                ClassName className,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                Object defaultValue,
                String priorFieldIfDefaulted)
        {
            if (isStringType(className))
            {
                builder.addStatement("$T.assertEquals(\"value$L\", flyweight.$L().asString())",
                        Assert.class,
                        valueIncrement,
                        name);
            }
            else if ("OctetsFW".equals(className.simpleName()))
            {

            }
            else
            {

            }
        }

        @Override
        public MethodSpec generate()
        {
            return builder.build();
        }
    }

    private final class ToStringTestMethodGenerator extends MethodSpecGenerator
    {
        private ToStringTestMethodGenerator()
        {
            super(methodBuilder("shouldReportAllFieldValuesInToString")
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC)
                    .addStatement("final $T offset = 11", int.class)
                    .addStatement("int limit = setAllBufferValues(buffer, offset)")
                    .addStatement("$T result = fieldRO.wrap(buffer, offset, limit).toString()", String.class)
                    .addStatement("$T.assertNotNull(result)", Assert.class)
                    .beginControlFlow("for ($T fieldName : FIELD_NAMES)", String.class)
                    .addStatement("$T.assertTrue($T.format(\"toString is missing %s\", fieldName), " +
                            "result.contains(fieldName))", Assert.class, String.class)
                    .endControlFlow());
        }

        @Override
        public MethodSpec generate()
        {
            return builder.build();
        }
    }

    private final class DefaultValuesTestMethodGenerator extends MethodSpecGenerator
    {
        private DefaultValuesTestMethodGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName)
        {
            super(methodBuilder("shouldDefaultValues")
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC)
                    .addStatement("final $T offset = 11", int.class)
                    .addStatement("$T expectedLimit = setRequiredBufferValues(expectedBuffer, offset)", int.class)
                    .addStatement("$T builder = fieldRW.wrap(buffer, offset, buffer.capacity())",
                            thisType.peerClass(baseName + "FW.Builder"))
                    .addStatement("$T limit = setRequiredFields(builder).build().limit()",
                            int.class)
                    .addStatement("$T.assertEquals(expectedLimit, limit)", Assert.class)
                    .addStatement("$T.assertEquals(0, expectedBuffer.compareTo(buffer))", Assert.class)
                    .addStatement("$T.assertEquals(expectedBuffer, buffer)", Assert.class)
            );
        }

        @Override
        public MethodSpec generate()
        {
            return builder.build();
        }
    }

    private final class ReadDefaultedValuesTestMethodGenerator extends MethodSpecGenerator
    {
        private ReadDefaultedValuesTestMethodGenerator()
        {
            super(methodBuilder("shouldReadDefaultedValues")
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC)
                    .addStatement("final $T offset = 11", int.class)
                    .addStatement("$T limit = setRequiredBufferValues(buffer, offset)", int.class)
                    .addStatement("$T.assertSame(fieldRO, fieldRO.wrap(buffer,  offset,  limit))", Assert.class)
                    .addStatement("assertRequiredValuesAndDefaults(fieldRO)")
            );
        }

        @Override
        public MethodSpec generate()
        {
            return builder.build();
        }
    }

    private final class SetAllFieldsValueTestMethodGenerator extends MethodSpecGenerator
    {
        private SetAllFieldsValueTestMethodGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName,
                StringSetterVariant stringSetterVariant,
                OctetsSetterVariant octetsSetterVariant)
        {
            super(methodBuilder("shouldSetAllFieldsValues" + stringSetterVariant + "_" + octetsSetterVariant)
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC)
                    .addStatement("final $T offset = 11", int.class)
                    .addStatement("$T expectedLimit = setAllBufferValues(expectedBuffer, offset)", int.class)
                    .addStatement("$T builder = setAllFieldsValue$L_$L(fieldRW.wrap(buffer, offset, buffer.capacity()))",
                            thisType.peerClass(baseName + "FW.Builder"), stringSetterVariant, octetsSetterVariant)
                    .addStatement("$T limit = builder.build().limit()", int.class)
                    .addStatement("$T.assertEquals(expectedLimit, limit)", Assert.class)
                    .addStatement("$T.assertEquals(expectedBuffer, buffer)", Assert.class)
            );
        }

        @Override
        public MethodSpec generate()
        {
            return builder.build();
        }
    }

    private static final class ExceptionTestMethodGenerator extends ClassSpecMixinGenerator
    {
        private final String baseName;
        private String priorDefaulted;
        private static final Map<TypeName, String[]> UNSIGNED_INT_RANGES;

        static
        {
            Map<TypeName, String[]> unsigned = new HashMap<>();
            unsigned.put(TypeName.BYTE, new String[]{"0", "0XFF"});
            unsigned.put(TypeName.SHORT, new String[]{"0", "0xFFFF"});
            unsigned.put(TypeName.INT, new String[]{"0", "0xFFFFFFFFL"});
            unsigned.put(TypeName.LONG, new String[]{"0L", null});
            UNSIGNED_INT_RANGES = unmodifiableMap(unsigned);
        }

        private ExceptionTestMethodGenerator(
                ClassName thisType,
                TypeSpec.Builder builder,
                String baseName)
        {
            super(thisType, builder);
            this.baseName = baseName;
        }

        public ExceptionTestMethodGenerator addMember(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            priorDefaulted = name;

            if (type.isPrimitive())
            {
                if (sizeName != null)
                {
                    //TODO: Add IntegerVariableArray
                } else if (size != -1)
                {
                    addIntegerFixedArray(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                            byteOrder, defaultValue);
                }
                else
                {
                    if(!usedAsSize)
                    {
                        setPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                                byteOrder, defaultValue, priorDefaulted);
                    }
                }
            }
            else
            {
                setNonPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                        byteOrder, defaultValue, priorDefaulted);
            }

            return this;
        }

        private void addIntegerFixedArray(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue)
        {
            builder.addMethod(methodBuilder("shouldFailToSet"+name.toUpperCase()+"WithValueToNull")
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC)
                    .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                    .addStatement("expectedException.expectMessage(\"$L\")", name)
                    .addStatement("fieldRW.wrap(buffer, 10, 10).$L($L)", methodName(name), null)
                    .build());

            TypeName inputType = (unsignedType != null) ? unsignedType : type;
            TypeName iteratorType = inputType == TypeName.LONG ? LONG_STREAM_CLASS_NAME
                    : INT_STREAM_CLASS_NAME;

            String value = "";
            for (int i = 0; i < size - 1; i++)
            {
                value += " " + i + ",";
            }

            builder.addMethod(methodBuilder("shouldFailToSet"+name.toUpperCase()+"WithNotHavingEnoughValue")
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC)
                    .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                    .addStatement("expectedException.expectMessage(\"$L\")", name)
                    .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                            "                .$L($T.of($L).iterator())\n" +
                            "                .build()", index(name), methodName(name), iteratorType,
                            removeChars(value, 1))
                    .build());

            TypeName generateType = (unsignedType != null) ? unsignedType : type;
            if (generateType == TypeName.LONG)
            {
                MethodSpec.Builder methodBuilder = methodBuilder("shouldFailToRead"+name.toUpperCase()+"WhenThereNoData")
                        .addAnnotation(Test.class)
                        .addModifiers(PUBLIC)
                        .addStatement("expectedException.expect($T.class)", NoSuchElementException.class)
                        .addStatement("expectedException.expectMessage(\"$L\")", name)
                        .addStatement("setRequiredFields(fieldRW.wrap(buffer, 0, buffer.capacity())).build()")
                        .addStatement("$T $L = fieldRO.$L()", LONG_ITERATOR_CLASS_NAME, methodName(name), methodName(name));

                for (int i = 0; i < size + 1; i++)
                {
                    methodBuilder.addStatement("$L.nextLong()", methodName(name));
                }
                builder.addMethod(methodBuilder.build());
            }


            if (unsignedType != null)
            {
                String[] range = UNSIGNED_INT_RANGES.get(type);

                builder.addMethod(methodBuilder("shouldFailToSet"+name.toUpperCase()+"WithValueTooLow")
                        .addAnnotation(Test.class)
                        .addModifiers(PUBLIC)
                        .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                        .addStatement("expectedException.expectMessage(\"$L\")", name)
                        .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                "                .$L(($L) $L)\n" +
                                "                .build()", index(name), appendMethodName(name), inputType, range[0]+" - 0x1")
                        .build());

                if (range[1] != null)
                {
                    builder.addMethod(methodBuilder("shouldFailToSet"+name.toUpperCase()+"WithValueTooHigh")
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                            .addStatement("expectedException.expectMessage(\"$L\")", name)
                            .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                            "                .$L(($L) $L)\n" +
                                            "                .build()", index(name), appendMethodName(name), inputType,
                                    range[1]+" + 0x1")
                            .build());
                }
            }
        }

        private void setPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue,
                String priorDefaulted)
        {
            String[] range = UNSIGNED_INT_RANGES.get(type);
            if (unsignedType != null)
            {

                builder.addMethod(methodBuilder("shouldFailToSet"+name.toUpperCase()+"WithValueTooLow")
                        .addAnnotation(Test.class)
                        .addModifiers(PUBLIC)
                        .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                        .addStatement("fieldRW.wrap(buffer, 10, 10).$L($L)", methodName(name), range[0]+"-0x1")
                .build());
                if (range[1] != null)
                {
                    builder.addMethod(methodBuilder("shouldFailToSet"+name.toUpperCase()+"WithValueTooHigh")
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                            .addStatement("expectedException.expectMessage(\"$L\")", name)
                            .addStatement("fieldRW.wrap(buffer, 10, 10).$L($L)", methodName(name), range[1]+"+0x1")
                            .build());
                }
            }

            if (defaultValue == null)
            {
                builder.addMethod(methodBuilder("shouldFailToBuildWhen"+name.toUpperCase() + "NotSet")
                        .addAnnotation(Test.class)
                        .addModifiers(PUBLIC)
                        .addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                        .addStatement("expectedException.expectMessage(\"$L\")", name)
                        .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L).build()", index(name))
                        .build());
            }

            builder.addMethod(methodBuilder("shouldFailToReset"+name.toUpperCase())
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC)
                    .addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                    .addStatement("expectedException.expectMessage(\"$L\")", name)
                    .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L+1)\n" +
                            "                .$L(($L)$L)\n" +
                            "                .build()", index(name), methodName(name), type.toString().toLowerCase(), range[0])
                    .build());
        }

        private void setNonPrimitiveValue(
                String name,
                TypeName type,
                TypeName unsignedType,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                AstByteOrder byteOrder,
                Object defaultValue,
                String priorDefaulted)
        {
            if (type instanceof ClassName)
            {
                ClassName className = (ClassName) type;
                addClassType(name, className, usedAsSize, size, sizeName, sizeType, defaultValue, priorDefaulted);

            }
            else if (type instanceof ParameterizedTypeName)
            {
                ParameterizedTypeName parameterizedType = (ParameterizedTypeName) type;
                addParameterizedType(name, parameterizedType, defaultValue);
            }
        }

        private void addParameterizedType(
                String name,
                ParameterizedTypeName parameterizedType,
                Object defaultValue)
        {
            ClassName rawType = parameterizedType.rawType;
            ClassName itemType = (ClassName) parameterizedType.typeArguments.get(0);
            ClassName builderRawType = rawType.nestedClass("Builder");
            ClassName itemBuilderType = itemType.nestedClass("Builder");
            ParameterizedTypeName builderType = ParameterizedTypeName.get(builderRawType, itemBuilderType, itemType);

            ClassName consumerType = ClassName.get(Consumer.class);
            TypeName mutatorType = ParameterizedTypeName.get(consumerType, builderType);

            if ("ListFW".equals(rawType.simpleName()))
            {
                if ("StringFW".equals(itemType.simpleName()))
                {
                    // Add a method to append list items
                    builder.addMethod(methodBuilder("shouldFailToReset"+name.toUpperCase())
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                            .addStatement("expectedException.expectMessage(\"$L\")", name)
                            .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L+1)\n" +
                                    "                .$L(b -> b.item(i -> i.set(\"value\", $T.UTF_8)))" +
                                    "                .build()", index(name), methodName(name), StandardCharsets.class)
                            .build());

                    builder.addMethod(methodBuilder("shouldGenerateDefaultValueFor"+name.toUpperCase() +
                            "ItemIf" + name.toUpperCase() + "IsNotSet")
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("$T limit = setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                    "                .$LItem(b -> b.set(\"value\", $T.UTF_8)).build().limit()",
                                    int.class, index(name), methodName(name), StandardCharsets.class)
                            .addStatement("fieldRO.wrap(buffer,  0,  limit)")
                            .addStatement("$T.assertFalse(fieldRO.$L().isEmpty())", Assert.class, methodName(name))
                            .build());
                }
            }
        }

        private void addClassType(
                String name,
                ClassName className,
                boolean usedAsSize,
                int size,
                String sizeName,
                TypeName sizeType,
                Object defaultValue,
                String priorFieldIfDefaulted)
        {
            if (isStringType(className))
            {
                if (defaultValue == null)
                {
                    builder.addMethod(methodBuilder("shouldFailToBuildWhen"+name.toUpperCase() + "NotSet")
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                            .addStatement("expectedException.expectMessage(\"$L\")", name)
                            .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L).build()", index(name))
                            .build());
                }

                builder.addMethod(methodBuilder("shouldFailToReset"+name.toUpperCase())
                        .addAnnotation(Test.class)
                        .addModifiers(PUBLIC)
                        .addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                        .addStatement("expectedException.expectMessage(\"$L\")", name)
                        .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L+1)\n" +
                                "                .$L(\"value\")\n" +
                                "                .build()", index(name), methodName(name))
                        .build());

            }
            else if ("OctetsFW".equals(className.simpleName()))
            {

                String value = "";
                if (size >= 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        value += i;
                    }

                    addOctetsFixedSizeExceptionMethod(name, className, usedAsSize, size, sizeName, sizeType, defaultValue,
                            priorDefaulted, value);

                }
                else if (sizeName != null)
                {
                    value = "xy";
                    if(isVarintType(sizeType))
                    {
                        addOctetsVariantsDynamicSizeExceptionMethod(name, className, usedAsSize, size, sizeName, sizeType,
                                defaultValue, priorDefaulted, value);
                    }
                    addOctetsDynamicSizeExceptionMethod(name, className, usedAsSize, size, sizeName, sizeType, defaultValue,
                            priorDefaulted, value);
                }
                else
                {
                    value = "x";
                }

                builder.addMethod(methodBuilder("shouldFailToReset"+name.toUpperCase())
                        .addAnnotation(Test.class)
                        .addModifiers(PUBLIC)
                        .addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                        .addStatement("expectedException.expectMessage(\"$L\")", name)
                        .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L+1)\n" +
                                "                .$L($T.asOctetsFW(\"$L\"))\n" +
                                "                .build()", index(name), methodName(name),
                                className.peerClass("OctetsFWTest"), value)
                        .build());
            }
            else
            {
                if (isVarintType(className))
                {
                    builder.addMethod(methodBuilder("shouldFailToReset"+name.toUpperCase())
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                            .addStatement("expectedException.expectMessage(\"$L\")", name)
                            .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L+1)\n" +
                                            "                .$L(2)\n" +
                                            "                .build()", index(name), methodName(name))
                            .build());
                }
                else
                {
                    builder.addMethod(methodBuilder("shouldFailToReset"+name.toUpperCase())
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                            .addStatement("expectedException.expectMessage(\"$L\")", name)
                            .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L+1)" +
                                            ".$L(field -> $TTest.setRequiredFields(field)).build()",
                                    index(name), methodName(name), className)
                            .build());
                }
            }
        }

        private void addOctetsVariantsDynamicSizeExceptionMethod(String name,
                                                       ClassName className,
                                                       boolean usedAsSize,
                                                       int size,
                                                       String sizeName,
                                                       TypeName sizeType,
                                                       Object defaultValue,
                                                       String priorFieldIfDefaulted,
                                                       String value)
        {
            for (OctetsSetterVariant setterVariant : OctetsSetterVariant.values())
            {
                if(setterVariant != OctetsSetterVariant.ARRAY)
                {

                    MethodSpec.Builder methodBuilderLonger = MethodSpec.methodBuilder(
                            "shouldFailToSet" + name.toUpperCase() + "WithValueLongerThanSizeUsing" +
                                    setterVariant)
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("expectedException.expect($T.class)",
                                    IllegalStateException.class);

                    switch (setterVariant)
                    {
                        case OCTETSFW:
                            methodBuilderLonger.addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                                    "                .$L(4)\n" +
                                                    "                .$L($T.asOctetsFW(\"$L\"))\n" +
                                                    "                .build()", index(name), methodName(sizeName),
                                    methodName(name), className.peerClass("OctetsFWTest"), value);
                            break;
                        case BUFFER:
                            methodBuilderLonger.addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                                    "                .$L(4)\n" +
                                                    "                .$L($T.asBuffer(\"$L\"),  0, \"$L\".length())\n" +
                                                    "                .build()", index(name), methodName(sizeName),
                                    methodName(name), className.peerClass("OctetsFWTest"), value + "11", value);
                            break;
                        case VISITOR:
                            methodBuilderLonger.addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                                    "                .$L(4)\n" +
                                                    "                .$L(b -> b.set(\"$L\".getBytes($T.UTF_8)))\n" +
                                                    "                .build()", index(name), methodName(sizeName),
                                    methodName(name), value, StandardCharsets.class);
                            break;
                    }

                    builder.addMethod(methodBuilderLonger.build());
                }
            }
        }

        private void addOctetsFixedSizeExceptionMethod(String name,
                                                       ClassName className,
                                                       boolean usedAsSize,
                                                       int size,
                                                       String sizeName,
                                                       TypeName sizeType,
                                                       Object defaultValue,
                                                       String priorFieldIfDefaulted,
                                                       String value)
        {
            for (OctetsSetterVariant setterVariant : OctetsSetterVariant.values())
            {
                if(setterVariant != OctetsSetterVariant.ARRAY)
                {
                    MethodSpec.Builder methodBuilderShorter = MethodSpec.methodBuilder(
                            "shouldFailToSet" + name.toUpperCase() + "WithValueShorterThanSizeUsing" +
                            setterVariant)
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC);


                    MethodSpec.Builder methodBuilderLonger = MethodSpec.methodBuilder(
                            "shouldFailToSet" + name.toUpperCase() + "WithValueLongerThanSizeUsing" +
                            setterVariant)
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC);

                    switch (setterVariant)
                    {
                        case OCTETSFW:
                            methodBuilderShorter.addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                                    .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                            "                .$L($T.asOctetsFW(\"$L\"))\n" +
                                            "                .build()", index(name), methodName(name),
                                    className.peerClass("OctetsFWTest"), removeChars(value, 1));

                            methodBuilderLonger.addStatement("expectedException.expect($T.class)",
                                    IndexOutOfBoundsException.class)
                                    .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                            "                .$L($T.asOctetsFW(\"$L\"))\n" +
                                            "                .build()", index(name), methodName(name),
                                    className.peerClass("OctetsFWTest"), value + "11");
                            break;
                        case BUFFER:
                            methodBuilderShorter.addStatement("expectedException.expect($T.class)",
                                    IllegalArgumentException.class)
                                    .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                            "                .$L($T.asBuffer(\"$L\"),  0, \"$L\".length())\n" +
                                            "                .build()", index(name), methodName(name),
                                    className.peerClass("OctetsFWTest"), removeChars(value, 1), removeChars(value, 1));

                            methodBuilderLonger.addStatement("expectedException.expect($T.class)",
                                    IllegalArgumentException.class)
                                    .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                            "                .$L($T.asBuffer(\"$L\"),  0, \"$L\".length())\n" +
                                            "                .build()", index(name), methodName(name),
                                    className.peerClass("OctetsFWTest"), value + "11", value + "11");
                            break;
                        case VISITOR:
                            methodBuilderShorter.addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                                    .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                            "                .$L(b -> b.set(\"$L\".getBytes($T.UTF_8)))\n" +
                                            "                .build()", index(name), methodName(name), removeChars(value, 1),
                                    StandardCharsets.class);

                            methodBuilderLonger.addStatement("expectedException.expect($T.class)",
                                    IndexOutOfBoundsException.class)
                                    .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                            "                .$L(b -> b.set(\"$L\".getBytes($T.UTF_8)))\n" +
                                            "                .build()", index(name), methodName(name), value + "11",
                                    StandardCharsets.class);
                            break;
                    }

                    builder.addMethod(methodBuilderShorter.build());
                    builder.addMethod(methodBuilderLonger.build());
                }
            }
        }

        private void addOctetsDynamicSizeExceptionMethod(String name,
                                                       ClassName className,
                                                       boolean usedAsSize,
                                                       int size,
                                                       String sizeName,
                                                       TypeName sizeType,
                                                       Object defaultValue,
                                                       String priorFieldIfDefaulted,
                                                       String value)
        {
            if (defaultValue == NULL_DEFAULT)
            {
                if(isVarintType(sizeType))
                {
                    builder.addMethod(methodBuilder("shouldSetNull"+name.toUpperCase())
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                            "                .$L(-1)\n" +
                                            "                .$L(($T) null)\n" +
                                            "                .build()", index(name), methodName(sizeName),
                                    methodName(name), className.peerClass("OctetsFW"))
                            .addStatement("$T.assertNull(fieldRO.$L())", Assert.class, methodName(name))
                            .build());
                }
                else
                {
                    builder.addMethod(methodBuilder("shouldSetNull"+name.toUpperCase())
                            .addAnnotation(Test.class)
                            .addModifiers(PUBLIC)
                            .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                            "                .$L(($T) null)\n" +
                                            "                .build()", index(name), methodName(name),
                                    className.peerClass("OctetsFW"))
                            .addStatement("$T.assertNull(fieldRO.$L())", Assert.class, methodName(name))
                            .build());
                }
            }
            else
            {
                builder.addMethod(methodBuilder("shouldFailToSetNull"+name.toUpperCase())
                        .addAnnotation(Test.class)
                        .addModifiers(PUBLIC)
                        .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                        .addStatement("setFieldUpToIndex(fieldRW.wrap(buffer, 0, 100), $L)\n" +
                                        "                .$L(($T) null)\n" +
                                        "                .build()", index(name), methodName(name),
                                className.peerClass("OctetsFW"))
                        .build());
            }
        }

        @Override
        public TypeSpec.Builder build()
        {
            return builder;
        }
    }

    private static boolean isOctetsType(
            TypeName type)
    {
        return type instanceof ClassName && "OctetsFW".equals(((ClassName) type).simpleName());
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

    private static boolean isVarintType(
            TypeName type)
    {
        return type instanceof ClassName && "Varint32FW".equals(((ClassName) type).simpleName())
                || type instanceof ClassName && "Varint64FW".equals(((ClassName) type).simpleName());
    }

    private static boolean isVarint32Type(
            TypeName type)
    {
        return type instanceof ClassName && "Varint32FW".equals(((ClassName) type).simpleName());
    }

    private static boolean isVarint64Type(
            TypeName type)
    {
        return type instanceof ClassName && "Varint64FW".equals(((ClassName) type).simpleName());
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

    private static String dynamicValue(
            String fieldName)
    {
        return String.format("dynamicValue%s", initCap(fieldName));
    }

    private static String dynamicOffset(
            String fieldName)
    {
        return String.format("dynamicOffset%s", initCap(fieldName));
    }

    private static boolean isImplicitlyDefaulted(
            TypeName type,
            int size,
            String sizeName)
    {
        boolean result = false;
        if (type instanceof ClassName && !isStringType((ClassName) type) && !isVarintType(type))
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
            if ("ListFW".equals(parameterizedType.rawType.simpleName())
                    || "ArrayFW".equals(parameterizedType.rawType.simpleName()))
            {
                result = true;
            }
        }
        return result;
    }

    private static String removeChars(String s, int numberOfChars)
    {
        if (s == null || s.length() == 0)
        {
            return s;
        }
        return s.substring(0, s.length()-numberOfChars);
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

    public static byte[] varint(
            long value)
    {
        final long bits = (value << 1) ^ (value >> 63);

        switch (bits != 0L ? (int) Math.ceil((1 + numberOfTrailingZeros(highestOneBit(bits))) / 7.0) : 1)
        {
            case 1:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f)
                        };
            case 2:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f | 0x80),
                                (byte) ((bits >> 7) & 0x7f)
                        };
            case 3:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f | 0x80),
                                (byte) ((bits >> 7) & 0x7f | 0x80),
                                (byte) ((bits >> 14) & 0x7f)
                        };
            case 4:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f | 0x80),
                                (byte) ((bits >> 7) & 0x7f | 0x80),
                                (byte) ((bits >> 14) & 0x7f | 0x80),
                                (byte) ((bits >> 21) & 0x7f)
                        };
            case 5:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f | 0x80),
                                (byte) ((bits >> 7) & 0x7f | 0x80),
                                (byte) ((bits >> 14) & 0x7f | 0x80),
                                (byte) ((bits >> 21) & 0x7f | 0x80),
                                (byte) ((bits >> 28) & 0x7f)
                        };
            case 6:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f | 0x80),
                                (byte) ((bits >> 7) & 0x7f | 0x80),
                                (byte) ((bits >> 14) & 0x7f | 0x80),
                                (byte) ((bits >> 21) & 0x7f | 0x80),
                                (byte) ((bits >> 28) & 0x7f | 0x80),
                                (byte) ((bits >> 35) & 0x7f)
                        };
            case 7:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f | 0x80),
                                (byte) ((bits >> 7) & 0x7f | 0x80),
                                (byte) ((bits >> 14) & 0x7f | 0x80),
                                (byte) ((bits >> 21) & 0x7f | 0x80),
                                (byte) ((bits >> 28) & 0x7f | 0x80),
                                (byte) ((bits >> 35) & 0x7f | 0x80),
                                (byte) ((bits >> 42) & 0x7f)
                        };
            case 8:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f | 0x80),
                                (byte) ((bits >> 7) & 0x7f | 0x80),
                                (byte) ((bits >> 14) & 0x7f | 0x80),
                                (byte) ((bits >> 21) & 0x7f | 0x80),
                                (byte) ((bits >> 28) & 0x7f | 0x80),
                                (byte) ((bits >> 35) & 0x7f | 0x80),
                                (byte) ((bits >> 42) & 0x7f | 0x80),
                                (byte) ((bits >> 49) & 0x7f),
                        };
            case 9:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f | 0x80),
                                (byte) ((bits >> 7) & 0x7f | 0x80),
                                (byte) ((bits >> 14) & 0x7f | 0x80),
                                (byte) ((bits >> 21) & 0x7f | 0x80),
                                (byte) ((bits >> 28) & 0x7f | 0x80),
                                (byte) ((bits >> 35) & 0x7f | 0x80),
                                (byte) ((bits >> 42) & 0x7f | 0x80),
                                (byte) ((bits >> 49) & 0x7f | 0x80),
                                (byte) ((bits >> 56) & 0x7f),
                        };
            default:
                return new byte[]
                        {
                                (byte) ((bits >> 0) & 0x7f | 0x80),
                                (byte) ((bits >> 7) & 0x7f | 0x80),
                                (byte) ((bits >> 14) & 0x7f | 0x80),
                                (byte) ((bits >> 21) & 0x7f | 0x80),
                                (byte) ((bits >> 28) & 0x7f | 0x80),
                                (byte) ((bits >> 35) & 0x7f | 0x80),
                                (byte) ((bits >> 42) & 0x7f | 0x80),
                                (byte) ((bits >> 49) & 0x7f | 0x80),
                                (byte) ((bits >> 56) & 0x7f | 0x80),
                                (byte) ((bits >> 63) & 0x01)
                        };
        }
    }
}
