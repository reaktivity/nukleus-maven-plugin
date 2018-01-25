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

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ClassSpecGenerator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

public final class String16FlyweightTestGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final ClassName string16FlyweightClassName;
    private final ClassName string16FlyweightBuilderClassName;

    public String16FlyweightTestGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("String16FWTest"));
        string16FlyweightClassName = flyweightType.peerClass("String16FW");
        string16FlyweightBuilderClassName = flyweightType.peerClass("String16FW.Builder");

        AnnotationSpec runWithAnnotationSpec = AnnotationSpec
            .builder(RunWith.class)
            .addMember("value", "$T.class", Parameterized.class)
            .build();
        this.classBuilder = classBuilder(thisName).addAnnotation(runWithAnnotationSpec).addModifiers(PUBLIC, FINAL);
    }

    @Override
    public TypeSpec generate()
    {
        MethodSpec asBuffer = asBuffer();
        MethodSpec asStringFW = asStringFW();

        return classBuilder.addField(fieldConstantLengthSize())
                .addField(fieldBuffer())
                .addField(fieldExpected())
                .addField(fieldString16Builder())
                .addField(fieldString16ReadOnly())
                .addMethod(values())
                .addMethod(shouldBuildStringWithMaximumLength())
                .addMethod(shouldSetUsingString())
                .addMethod(shouldSetUsingStringFW(asStringFW))
                .addMethod(shouldSetUsingBuffer(asBuffer))
                .addMethod(shouldDefaultAfterRewrap())
                .addMethod(shouldDefaultToEmpty())
                .addMethod(shouldSetToNullUsingStringSetter())
                .addMethod(shouldSetToNullWithoutCharacterSet())
                .addMethod(shouldFailToWrapWithInsufficientLength())
                .addMethod(shouldWrapWithSufficientLength())
                .addMethod(shouldFailToSetUsingStringWhenExceedsMaxLimit())
                .addMethod(shouldFailToSetUsingStringFWWhenExceedsMaxLimit(asStringFW))
                .addMethod(shouldFailToSetUsingBufferWhenExceedsMaxLimit())
                .addMethod(shouldSetToNull())
                .addMethod(shouldFailToSetValueExceedingMaximumLength())
                .addMethod(shouldFailToBuildLargeString())
                .addMethod(shouldReturnString())
                .addMethod(setFieldValue())
                .addMethod(asBuffer)
                .addMethod(asStringFW)
                .addMethod(setBufferValue())
                .addMethod(assertStringValue())
                .addMethod(assertLengthSize())
                .build();
    }

    private FieldSpec fieldString16Builder()
    {
        AnnotationSpec paramAnnotationSpec = AnnotationSpec
            .builder(Parameterized.Parameter.class)
            .addMember("value", "0")
            .build();
        return FieldSpec
            .builder(string16FlyweightBuilderClassName, "stringRW", PUBLIC)
            .addAnnotation(paramAnnotationSpec)
            .build();
    }

    private FieldSpec fieldString16ReadOnly()
    {
        AnnotationSpec paramAnnotationSpec = AnnotationSpec
            .builder(Parameterized.Parameter.class)
            .addMember("value", "1")
            .build();
        return FieldSpec
            .builder(string16FlyweightClassName, "stringRO", PUBLIC)
            .addAnnotation(paramAnnotationSpec)
            .build();
    }

    private FieldSpec fieldBuffer()
    {
        return FieldSpec.builder(MutableDirectBuffer.class, "buffer", PRIVATE, FINAL)
            .initializer("new $T($T.allocateDirect(100000)); \n" +
                "{\n" +
                "    buffer.setMemory(0, buffer.capacity(), (byte) 0xF);\n" +
                "}", UnsafeBuffer.class, ByteBuffer.class)
            .build();
    }

    private FieldSpec fieldExpected()
    {
        return FieldSpec.builder(MutableDirectBuffer.class, "expected", PRIVATE, FINAL)
            .initializer("new $T($T.allocateDirect(100000)); \n" +
                    "{\n" +
                    "    expected.setMemory(0, expected.capacity(), (byte) 0xF);\n" +
                    "}", UnsafeBuffer.class, ByteBuffer.class)
            .build();
    }

    private FieldSpec fieldConstantLengthSize()
    {
        return FieldSpec.builder(int.class, "LENGTH_SIZE", PRIVATE, STATIC, FINAL)
            .initializer("2")
            .build();
    }

    private MethodSpec values()
    {
        return MethodSpec.methodBuilder("values")
            .addModifiers(PUBLIC, STATIC)
            .addAnnotation(Parameterized.Parameters.class)
            .returns(ParameterizedTypeName.get(Collection.class, Object[].class))
            .addStatement("return $T.asList(\n" +
                "        new Object[][]\n" +
                "    {\n" +
                "        { new String16FW.Builder(), new String16FW() },\n" +
                "        { new String16FW.Builder($T.LITTLE_ENDIAN), new String16FW($T.LITTLE_ENDIAN) },\n" +
                "        { new String16FW.Builder($T.BIG_ENDIAN), new String16FW($T.BIG_ENDIAN) }\n" +
                "    })", Arrays.class, ByteOrder.class, ByteOrder.class, ByteOrder.class, ByteOrder.class)
            .build();
    }

    private MethodSpec shouldDefaultAfterRewrap()
    {
        return MethodSpec.methodBuilder("shouldDefaultAfterRewrap")
            .addModifiers(PUBLIC)
            .addAnnotation(Test.class)
            .addException(Exception.class)
            .addStatement("$T limit = $N(\"Hello World\")", int.class, setFieldValue())
            .addStatement("String16FW string = stringRW.wrap(buffer, 0, limit).build()")
            .addStatement("$N(string)", assertLengthSize())
            .build();
    }

    private MethodSpec shouldDefaultToEmpty()
    {
        return MethodSpec.methodBuilder("shouldDefaultToEmpty")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("int limit = stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                              "    .build()\n" +
                              "    .limit()")
                .addStatement("stringRO.wrap(buffer, 0, limit)")
                .addStatement("$N(stringRO)", assertLengthSize())
                .build();
    }

    private MethodSpec shouldSetToNullUsingStringSetter()
    {
        return MethodSpec.methodBuilder("shouldSetToNullUsingStringSetter")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("int limit = $N(null)", setFieldValue())
                .addStatement("$T.assertEquals(2, limit);", Assert.class)
                .addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$N(stringRO)", assertLengthSize())
                .build();
    }

    private MethodSpec shouldSetToNullWithoutCharacterSet()
    {
        return MethodSpec.methodBuilder("shouldSetToNullWithoutCharacterSet")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("int limit = stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "                .set(null)\n" +
                        "                .build()\n" +
                        "                .limit()")
                .addStatement("$T.assertEquals(2, limit);", Assert.class)
                .addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$N(stringRO)", assertLengthSize())
                .build();
    }

    private MethodSpec shouldFailToWrapWithInsufficientLength()
    {
        AnnotationSpec testAnnotation = AnnotationSpec.builder(Test.class)
                .addMember("expected", "$T.class", IndexOutOfBoundsException.class)
                .build();

        return MethodSpec.methodBuilder("shouldFailToWrapWithInsufficientLength")
                .addModifiers(PUBLIC)
                .addAnnotation(testAnnotation)
                .addStatement("stringRW.wrap(buffer, 10, 10)")
                .build();
    }

    private MethodSpec shouldWrapWithSufficientLength()
    {
        return MethodSpec.methodBuilder("shouldWrapWithSufficientLength")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)")
                .build();
    }

    private MethodSpec shouldFailToSetUsingStringWhenExceedsMaxLimit()
    {
        AnnotationSpec testAnnotation = AnnotationSpec.builder(Test.class)
                .addMember("expected", "$T.class", IndexOutOfBoundsException.class)
                .build();

        return MethodSpec.methodBuilder("shouldFailToSetUsingStringWhenExceedsMaxLimit")
                .addModifiers(PUBLIC)
                .addAnnotation(testAnnotation)
                .addStatement("buffer.setMemory(0,  buffer.capacity(), ($T) 0x00)", byte.class)
                .beginControlFlow("try")
                .addStatement("stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)\n" +
                        "  .set(\"1\", $T.UTF_8)", StandardCharsets.class)
                .endControlFlow()
                .beginControlFlow("finally")
                .addStatement("$1T[] bytes = new $1T[1 + LENGTH_SIZE]", byte.class)
                .addStatement("buffer.getBytes(10, bytes)")
                .addStatement("$T.assertEquals(\"Buffer shows memory was written beyond maxLimit: \" + " +
                        "$T.toHex(bytes), 0, buffer.getByte(10 + LENGTH_SIZE))", Assert.class, BitUtil.class)
                .endControlFlow()
                .build();
    }

    private MethodSpec shouldFailToSetUsingStringFWWhenExceedsMaxLimit(MethodSpec asStringFW)
    {
        AnnotationSpec testAnnotation = AnnotationSpec.builder(Test.class)
                .addMember("expected", "$T.class", IndexOutOfBoundsException.class)
                .build();

        return MethodSpec.methodBuilder("shouldFailToSetUsingStringFWWhenExceedsMaxLimit")
                .addModifiers(PUBLIC)
                .addAnnotation(testAnnotation)
                .addStatement("buffer.setMemory(0,  buffer.capacity(), ($T) 0x00)", byte.class)
                .beginControlFlow("try")
                .addStatement("stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)\n" +
                        ".set($N(\"1\"))", asStringFW)
                .endControlFlow()
                .beginControlFlow("finally")
                .addStatement("$1T[] bytes = new $1T[1 + LENGTH_SIZE]", byte.class)
                .addStatement("buffer.getBytes(10, bytes)")
                .addStatement("$T.assertEquals(\"Buffer shows memory was written beyond maxLimit: \" + " +
                        "$T.toHex(bytes),\n0, buffer.getByte(10 + LENGTH_SIZE))", Assert.class, BitUtil.class)
                .endControlFlow()
                .build();
    }

    private MethodSpec shouldFailToSetUsingBufferWhenExceedsMaxLimit()
    {
        AnnotationSpec testAnnotation = AnnotationSpec.builder(Test.class)
                .addMember("expected", "$T.class", IndexOutOfBoundsException.class)
                .build();

        return MethodSpec.methodBuilder("shouldFailToSetUsingBufferWhenExceedsMaxLimit")
                .addModifiers(PUBLIC)
                .addAnnotation(testAnnotation)
                .addStatement("buffer.setMemory(0,  buffer.capacity(), ($T) 0x00)", byte.class)
                .addStatement("buffer.putStringWithoutLengthUtf8(0, \"1\")")
                .beginControlFlow("try")
                .addStatement("stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)\n.set(buffer, 0, 1)")
                .endControlFlow()
                .beginControlFlow("finally")
                .addStatement("$1T[] bytes = new $1T[1 + LENGTH_SIZE]", byte.class)
                .addStatement("buffer.getBytes(10, bytes)")
                .addStatement("$T.assertEquals(\"Buffer shows memory was written beyond maxLimit: \" + " +
                        "$T.toHex(bytes),\n0, buffer.getByte(10 + LENGTH_SIZE))", Assert.class, BitUtil.class)
                .endControlFlow()
                .build();
    }

    private MethodSpec shouldSetToNull()
    {
        return MethodSpec.methodBuilder("shouldSetToNull")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T limit = stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                        ".set(null, $T.UTF_8)\n" +
                        ".build()\n" +
                        ".limit()", int.class, StandardCharsets.class)
                .addStatement("$T.assertEquals(2, limit)", Assert.class)
                .addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$T.assertEquals(LENGTH_SIZE, stringRO.limit())", Assert.class)
                .addStatement("$T.assertEquals(LENGTH_SIZE, stringRO.sizeof())", Assert.class)
                .addStatement("$T.assertEquals(null, stringRO.asString())", Assert.class)
                .build();
    }

    private MethodSpec shouldReturnString()
    {
        return MethodSpec.methodBuilder("shouldReturnString")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T.assertNotNull(stringRO.toString())", Assert.class)
                .build();
    }

    private MethodSpec shouldFailToSetValueExceedingMaximumLength()
    {
        AnnotationSpec testAnnotation = AnnotationSpec.builder(Test.class)
                .addMember("expected", "$T.class", IllegalArgumentException.class)
                .build();


        return MethodSpec.methodBuilder("shouldFailToSetValueExceedingMaximumLength")
                .addModifiers(PUBLIC)
                .addAnnotation(testAnnotation)
                .addException(Exception.class)
                .addStatement("int tooLong = String16FW.MAX_LENGTH + 1")
                .addStatement("$1T str = $1T.format(\"%\" + Integer.toString(tooLong) + \"s\", \"0\")", String.class)
                .addStatement("stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                        ".set(str, $T.UTF_8)", StandardCharsets.class)
                .build();
    }

    private MethodSpec shouldBuildStringWithMaximumLength()
    {
        return MethodSpec.methodBuilder("shouldBuildStringWithMaximumLength")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("int tooLong = String16FW.MAX_LENGTH")
                .addStatement("$1T str = $1T.format(\"%\" + Integer.toString(tooLong) + \"s\", \"0\")", String.class)
                .addStatement("$N(str)", setFieldValue())
                .build();
    }

    private MethodSpec shouldSetUsingString()
    {
        return MethodSpec.methodBuilder("shouldSetUsingString")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T offset = 0", int.class)
                .addStatement("$T expectedLimit = $N(expected, offset)", int.class, setBufferValue())
                .addStatement("$T limit = $N(\"value1\")", int.class, setFieldValue())
                .addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$T.assertEquals(expectedLimit, limit)", Assert.class)
                .addStatement("$N(stringRO)", assertStringValue())
                .build();
    }

    private MethodSpec shouldSetUsingStringFW(MethodSpec asStringFW)
    {
        return MethodSpec.methodBuilder("shouldSetUsingStringFW")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T offset = 0", int.class)
                .addStatement("$T expectedLimit = $N(expected, offset)", int.class, setBufferValue())
                .addStatement("$T limit = stringRW.wrap(buffer, offset, buffer.capacity())\n" +
                        ".set($N(\"value1\"))\n" +
                        ".build()\n" +
                        ".limit()", int.class, asStringFW)
                .addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$T.assertEquals(expectedLimit, limit)", Assert.class)
                .addStatement("$N(stringRO)", assertStringValue())
                .build();
    }

    private MethodSpec shouldSetUsingBuffer(MethodSpec asBuffer)
    {
        return MethodSpec.methodBuilder("shouldSetUsingBuffer")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T offset = 0", int.class)
                .addStatement("$T expectedLimit = $N(expected, offset)", int.class, setBufferValue())
                .addStatement("$T limit = stringRW.wrap(buffer, 0, 50)\n" +
                        ".set($N(\"value1\"), 0, 6)\n" +
                        ".build()\n" +
                        ".limit()", int.class, asBuffer)
                .addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$T.assertEquals(expectedLimit, limit)", Assert.class)
                .addStatement("$N(stringRO)", assertStringValue())
                .build();
    }

    private MethodSpec asBuffer()
    {
        return MethodSpec.methodBuilder("asBuffer")
                .addModifiers(PRIVATE, STATIC)
                .addParameter(String.class, "value")
                .returns(MutableDirectBuffer.class)
                .addStatement("$T buffer = new $T($T.allocateDirect(value.length()))",
                        MutableDirectBuffer.class, UnsafeBuffer.class, java.nio.ByteBuffer.class)
                .addStatement("buffer.putStringWithoutLengthUtf8(0, value)")
                .addStatement("return buffer")
                .build();
    }

    private MethodSpec asStringFW()
    {
        return MethodSpec.methodBuilder("asStringFW")
                .addModifiers(PRIVATE, STATIC)
                .addParameter(String.class, "value")
                .returns(string16FlyweightClassName)
                .addStatement("$T buffer = new $T($T.allocateDirect($T.SIZE + value.length()))",
                        MutableDirectBuffer.class, UnsafeBuffer.class, java.nio.ByteBuffer.class, Byte.class)
                .addStatement("return new $T.Builder().wrap(buffer, 0, buffer.capacity()).set(value, $T.UTF_8).build()",
                        string16FlyweightClassName, StandardCharsets.class)
                .build();
    }

    private MethodSpec setFieldValue()
    {
        return MethodSpec.methodBuilder("setFieldValue")
                .addParameter(String.class, "value")
                .returns(int.class)
                .addStatement("return stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "                .set(value, $T.UTF_8)\n" +
                        "                .build()\n" +
                        "                .limit()", StandardCharsets.class)
                .build();
    }

    private MethodSpec setBufferValue()
    {
        return MethodSpec.methodBuilder("setBufferValue")
                .addModifiers(STATIC)
                .addParameter(MutableDirectBuffer.class, "buffer")
                .addParameter(int.class, "offset")
                .returns(int.class)
                .addStatement("buffer.putShort(offset, (short) \"value1\".length())")
                .addStatement("buffer.putBytes(offset +=8, \"value1\".getBytes($T.UTF_8))", StandardCharsets.class)
                .addStatement("return offset")
                .build();
    }

    private MethodSpec assertStringValue()
    {
        return MethodSpec.methodBuilder("assertStringValue")
                .addModifiers(STATIC)
                .addParameter(string16FlyweightClassName, "string16FW")
                .addStatement("$T.assertEquals(6 + LENGTH_SIZE, string16FW.limit())", Assert.class)
                .addStatement("$T.assertEquals(6 + LENGTH_SIZE, string16FW.sizeof())", Assert.class)
                .addStatement("$T.assertEquals(\"value1\", string16FW.asString())", Assert.class)
                .build();
    }

    private MethodSpec assertLengthSize()
    {
        return MethodSpec.methodBuilder("assertLengthSize")
                .addModifiers(STATIC)
                .addParameter(string16FlyweightClassName, "string16FW")
                .addStatement("$T.assertNull(string16FW.asString())", Assert.class)
                .addStatement("$T.assertEquals(LENGTH_SIZE, string16FW.limit())", Assert.class)
                .addStatement("$T.assertEquals(LENGTH_SIZE, string16FW.sizeof())", Assert.class)
                .build();
    }

}
