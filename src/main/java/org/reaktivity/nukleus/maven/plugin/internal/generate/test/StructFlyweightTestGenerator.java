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
import static java.util.Collections.unmodifiableMap;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstMemberNode.NULL_DEFAULT;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.MUTABLE_DIRECT_BUFFER_TYPE;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.squareup.javapoet.*;
import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
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

    private static final ClassName INT_ITERATOR_CLASS_NAME = ClassName.get(PrimitiveIterator.OfInt.class);

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
    private final SetRequiredBuilderFieldMethodGenerator setRequiredBuilderFieldMethodGenerator;
    private final AssertRequiredValuesAndDefaultsMethodGenerator assertRequiredValuesAndDefaultsMethodGenerator;
    private final ToStringTestMethodGenerator toStringTestMethodGenerator;
    private final AstNodeLocator astNodeLocator;

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
        this.setAllBuilderValuesMethodGeneratorPrimary = new SetAllBuilderValuesMethodGenerator(structName, builder, baseName,
                StringSetterVariant.STRING,
                OctetsSetterVariant.OCTETSFW);
        this.setAllBuilderValuesMethodGeneratorStringFW = new SetAllBuilderValuesMethodGenerator(structName, builder, baseName,
                StringSetterVariant.STRINGFW,
                OctetsSetterVariant.VISITOR);
        this.setAllBuilderValuesMethodGeneratorBuffer = new SetAllBuilderValuesMethodGenerator(structName, builder, baseName,
                StringSetterVariant.BUFFER,
                OctetsSetterVariant.BUFFER);
        this.setRequiredBuilderFieldMethodGenerator = new SetRequiredBuilderFieldMethodGenerator(structName, builder, baseName);
        this.assertRequiredValuesAndDefaultsMethodGenerator = new AssertRequiredValuesAndDefaultsMethodGenerator(structName,
                builder, baseName);
        this.toStringTestMethodGenerator = new ToStringTestMethodGenerator(baseName);
        this.astNodeLocator = astNodeLocator;
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
            setRequiredBuilderFieldMethodGenerator.addMember(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                    byteOrder, defaultValue);
            assertRequiredValuesAndDefaultsMethodGenerator.addMember(name, type, unsignedType, usedAsSize, size, sizeName,
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

        if(setAllBuilderValuesMethodGeneratorStringFW.hasVariant())
        {
            builder.addMethod(setAllBuilderValuesMethodGeneratorStringFW.generate());
        }
        if(setAllBuilderValuesMethodGeneratorBuffer.hasVariant())
        {
            builder.addMethod(setAllBuilderValuesMethodGeneratorBuffer.generate());
        }

        return builder
                .addMethod(toStringTestMethodGenerator.generate())
                .addMethod(setAllBufferValuesMethodGenerator.generate())
                .addMethod(setRequiredBufferValuesMethodGenerator.generate())
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
                        .addStatement("$T limit = setRequiredFields(fieldRW.wrap(buffer, 0, buffer.capacity()), $T.MAX_VALUE)" +
                                ".build()" +
                                ".limit()", int.class, Integer.class)
                        .addStatement("fieldRO.wrap(buffer,  0,  limit)")
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
                    .initializer("new $T($T.allocateDirect(100)); \n" +
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
                    .initializer("new $T($T.allocateDirect(100)); \n" +
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
                    generateType = generateType == TypeName.LONG ? LONG_ITERATOR_CLASS_NAME
                            : INT_ITERATOR_CLASS_NAME;
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
        private String priorValueSize = "0";
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
                    //TODO: Add IntegerVariableArray
                } else if (size != -1)
                {
                    //TODO: Add integerFixedArrayIterator member
                }
                else
                {
                    //TODO: Add primitive member
                    if(allFields)
                    {
                        addSetPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                                byteOrder, null);
                    }
                    else
                    {
                        addSetPrimitiveValue(name, type, unsignedType, usedAsSize, size, sizeName, sizeType,
                                byteOrder, defaultValue);
                    }
                }
            }
            else
            {
                //TODO: Add nonprimative member
                addSetNonPrimitiveValue();
            }

            return this;
        }

        private void addSetPrimitiveValue(
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
                builder.addStatement("buffer.$L(offset += $L, ($L) $L)",
                        putterName,
                        priorValueSize,
                        SIZEOF_BY_NAME.get(type).toLowerCase(),
                        defaultValue.toString());
            }
            else
            {
                builder.addStatement("buffer.$L(offset += $L, ($L) $L0)",
                        putterName,
                        priorValueSize,
                        SIZEOF_BY_NAME.get(type).toLowerCase(),
                        valueIncrement);
            }

            priorValueSize = TYPE_SIZE.get(type);
        }

        private void addSetNonPrimitiveValue()
        {
            builder.addStatement("buffer.putByte(offset += $L, (byte) \"value$L\".length())",
                    priorValueSize,
                    valueIncrement);
            builder.addStatement("buffer.putBytes(offset += 1, \"value$L\".getBytes($T.UTF_8))",
                    valueIncrement,
                    StandardCharsets.class);
            priorValueSize = Integer.toString(("value" + valueIncrement).length());
        }

        @Override
        public MethodSpec generate()
        {
            return builder.addStatement("return offset + " + priorValueSize).build();
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
            super(methodBuilder("setAllValues" + stringSetterVariant + "_" + octetsSetterVariant)
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
                    //TODO: Add IntegerVariableArray
                } else if (size != -1)
                {
                    //TODO: Add integerFixedArrayIterator member
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
            builder.addStatement("builder.$L(($L)$L0)",
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
            else if (DIRECT_BUFFER_TYPE.equals(className))
            {
                // TODO: What IDL type does this correspond to? I don't see it in TypeResolver
                // so I suspect this is dead code and should be removed
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
                    .addParameter(int.class, "toFieldIndex")
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

            if(defaultValue != null || RESERVED_METHOD_NAMES.contains(name) || usedAsSize)
            {
                return this;
            }
            name = methodName(name);
            priorDefaulted = name;

            if (type.isPrimitive())
            {
                if (sizeName != null)
                {
                    //TODO: Add IntegerVariableArray
                } else if (size != -1)
                {
                    //TODO: Add integerFixedArrayIterator member
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
            builder.beginControlFlow("if(toFieldIndex > $L)", index(name));
            builder.addStatement("builder.$L($L0)",
                    name,
                    valueIncrement);
            builder.endControlFlow();
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
                builder.beginControlFlow("if(toFieldIndex > $L)", index(name));
                builder.addStatement("builder.$L(\"value$L\")",
                        name,
                        valueIncrement);
                builder.endControlFlow();

            }
            else if (DIRECT_BUFFER_TYPE.equals(className))
            {
                // TODO: What IDL type does this correspond to? I don't see it in TypeResolver
                // so I suspect this is dead code and should be removed
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

            name = methodName(name);
            valueIncrement++;
            priorDefaulted = name;

            if (type.isPrimitive())
            {
                if (sizeName != null)
                {
                    //TODO: Add IntegerVariableArray
                } else if (size != -1)
                {
                    //TODO: Add integerFixedArrayIterator member
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
                        name);
            }
            else
            {
                builder.addStatement("$T.assertEquals($L0, flyweight.$L())",
                        Assert.class,
                        valueIncrement,
                        name);
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
            else if (DIRECT_BUFFER_TYPE.equals(className))
            {
                // TODO: What IDL type does this correspond to? I don't see it in TypeResolver
                // so I suspect this is dead code and should be removed
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
        private ToStringTestMethodGenerator(String baseName)
        {
            super(methodBuilder("shouldReportAllFieldValuesInToString")
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC));
        }

        @Override
        public MethodSpec generate()
        {
            return builder.addStatement("final int offset = 11")
                    .addStatement("int limit = setAllBufferValues(buffer, offset)")
                    .addStatement("String result = fieldRO.wrap(buffer, offset, limit).toString()")
                    .addStatement("$T.assertNotNull(result)", Assert.class)
                    .addStatement(String.format("for (String fieldName : FIELD_NAMES)" +
                                    "\n{" +
                                    "\n  %s.%s" +
                                    "\n}",
                            Assert.class.getName(),
                            "assertTrue(String.format(\"toString is missing %s\", fieldName), result.contains(fieldName));"))
                    .build();
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

    private static String dynamicOffset(
            String fieldName)
    {
        return String.format("dynamicOffset%s", initCap(fieldName));
    }

    private static String dynamicValue(
            String fieldName)
    {
        return String.format("dynamicValue%s", initCap(fieldName));
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
