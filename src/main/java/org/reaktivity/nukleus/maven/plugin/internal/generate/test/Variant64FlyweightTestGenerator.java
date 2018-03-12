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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ClassSpecGenerator;

import java.nio.ByteBuffer;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.*;

public final class Variant64FlyweightTestGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final ClassName octetsFlyweightClassName;
    private final ClassName octetsFlyweightBuilderClassName;

    public Variant64FlyweightTestGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("Varint64FWTest"));
        octetsFlyweightClassName = flyweightType.peerClass("Varint64FW");
        octetsFlyweightBuilderClassName = flyweightType.peerClass("Varint64FW.Builder");

        this.classBuilder = classBuilder(thisName).addModifiers(PUBLIC, FINAL);
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder.addField(fieldBuffer())
                .addField(fieldExpected())
                .addField(fieldOctetsBuilder())
                .addField(fieldOctetsReadOnly())
                .addField(fieldExpectedException())
                .addMethod(shouldNotWrapZeroLengthBuffer())
                .addMethod(shouldNotWrapValueWith65bits())
                .addMethod(shouldReadOneByteValue())
                .addMethod(shouldFailToGetValueWithExceedingSize())
                .addMethod(shouldFailToBuildWithoutSettingValue())
                .addMethod(shouldReadTwoByteValue())
                .addMethod(shouldReadFiveBytePositiveValue())
                .addMethod(shouldReadFiveByteNegativeValue())
                .addMethod(shouldReadMostPositiveValue())
                .addMethod(shouldReadMostNegativeValue())
                .addMethod(shouldSetMostPositiveValue())
                .addMethod(shouldSetMostNegativeValue())
                .addMethod(shouldSetOneByteValue())
                .addMethod(shouldSetTwoByteValue())
                .addMethod(shouldReportAsString())
                .addMethod(shouldNotBuildWithZeroLengthBuffer())
                .addMethod(shouldNotSetValueWithInsufficientSpace())
                .build();
    }

    private FieldSpec fieldOctetsBuilder()
    {
        return FieldSpec.builder(octetsFlyweightBuilderClassName, "varintRW", PRIVATE, FINAL)
                .initializer("new $T()", octetsFlyweightBuilderClassName)
                .build();
    }

    private FieldSpec fieldOctetsReadOnly()
    {
        return FieldSpec.builder(octetsFlyweightClassName, "varintRO", PRIVATE, FINAL)
                .initializer("new $T()", octetsFlyweightClassName)
                .build();
    }

    private FieldSpec fieldBuffer()
    {
        return FieldSpec.builder(MutableDirectBuffer.class, "buffer", PRIVATE, FINAL)
            .initializer("new $T($T.allocateDirect(100)); \n" +
                "{\n" +
                "    buffer.setMemory(0, buffer.capacity(), (byte) 0xF);\n" +
                "}", UnsafeBuffer.class, ByteBuffer.class)
            .build();
    }

    private FieldSpec fieldExpected()
    {
        return FieldSpec.builder(MutableDirectBuffer.class, "expected", PRIVATE, FINAL)
            .initializer("new $T($T.allocateDirect(100)); \n" +
                    "{\n" +
                    "    expected.setMemory(0, expected.capacity(), (byte) 0xF);\n" +
                    "}", UnsafeBuffer.class, ByteBuffer.class)
            .build();
    }

    private FieldSpec fieldExpectedException()
    {
        return FieldSpec.builder(ExpectedException.class, "expectedException", PUBLIC)
                .addAnnotation(Rule.class)
                .initializer("$T.none()", ExpectedException.class)
                .build();
    }

    private MethodSpec shouldNotWrapZeroLengthBuffer()
    {
        return MethodSpec.methodBuilder("shouldNotWrapZeroLengthBuffer")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("buffer.putByte(10,  (byte) 0x18)")
                .addStatement("varintRO.wrap(buffer,  10,  10)")
                .build();
    }

    private MethodSpec shouldNotWrapValueWith65bits()
    {
        return MethodSpec.methodBuilder("shouldNotWrapValueWith65bits")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                .addStatement("expectedException.expectMessage(\"offset 37 exceeds 64 bits\")")
                .addStatement("$T offset = 37", int.class)
                .addStatement("buffer.putByte(offset, (byte) 0xfe)")
                .beginControlFlow("for ($T i=0; i < 9; i++)", int.class)
                .addStatement("buffer.putByte(offset + i, (byte) 0xff)")
                .endControlFlow()
                .addStatement("buffer.putByte(offset + 9, (byte) 0x02)")
                .addStatement("varintRO.wrap(buffer,  offset,  buffer.capacity())")
                .build();
    }

    private MethodSpec shouldFailToGetValueWithExceedingSize()
    {
        return MethodSpec.methodBuilder("shouldFailToGetValueWithExceedingSize")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                .addStatement("expectedException.expectMessage(\"varint32 value too long\")")
                .addStatement("buffer.putByte(50, (byte) 0xff)")
                .addStatement("buffer.putByte(51, (byte) 0xff)")
                .addStatement("buffer.putByte(52, (byte) 0xff)")
                .addStatement("buffer.putByte(53, (byte) 0xff)")
                .addStatement("buffer.putByte(54, (byte) 0xff)")
                .addStatement("buffer.putByte(55, (byte) 0x0f)")
                .addStatement("buffer.putByte(56, (byte) 0xff)")
                .addStatement("buffer.putByte(57, (byte) 0xff)")
                .addStatement("varintRO.wrap(buffer,  50,  buffer.capacity())")
                .addStatement("varintRO.value()")
                .build();
    }

    private MethodSpec shouldFailToBuildWithoutSettingValue()
    {
        return MethodSpec.methodBuilder("shouldFailToBuildWithoutSettingValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                .addStatement("expectedException.expectMessage(\"value not set\")")
                .addStatement("expected.putByte(50, (byte) 0xfe)")
                .addStatement("expected.putByte(51, (byte) 0xff)")
                .addStatement("varintRW.wrap(buffer,  50,  buffer.capacity())\n" +
                        "                .build()")
                .build();
    }

    private MethodSpec shouldReadOneByteValue()
    {
        return MethodSpec.methodBuilder("shouldReadOneByteValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("buffer.putByte(10,  (byte) 0x18)")
                .addStatement("$T.assertEquals(11, varintRO.wrap(buffer,  10,  21).limit())",
                        Assert.class)
                .addStatement("$T.assertEquals(12L, varintRO.value())", Assert.class)
                .build();
    }

    private MethodSpec shouldReadTwoByteValue()
    {
        return MethodSpec.methodBuilder("shouldReadTwoByteValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("buffer.putByte(50, (byte) 0x83)")
                .addStatement("buffer.putByte(51, (byte) 0x01)")
                .addStatement("$T.assertEquals(52, varintRO.wrap(buffer,  50,  buffer.capacity()).limit())",
                        Assert.class)
                .addStatement("$T.assertEquals(-66L, varintRO.value())", Assert.class)
                .build();
    }

    private MethodSpec shouldReadFiveBytePositiveValue()
    {
        return MethodSpec.methodBuilder("shouldReadFiveBytePositiveValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("buffer.putByte(50, (byte) 0xfe)")
                .addStatement("buffer.putByte(51, (byte) 0xff)")
                .addStatement("buffer.putByte(52, (byte) 0xff)")
                .addStatement("buffer.putByte(53, (byte) 0xff)")
                .addStatement("buffer.putByte(54, (byte) 0x0f)")
                .addStatement("varintRO.wrap(buffer,  50,  buffer.capacity())")
                .addStatement("$T.assertEquals($T.MAX_VALUE, varintRO.value())", Assert.class, Integer.class)
                .build();
    }

    private MethodSpec shouldReadFiveByteNegativeValue()
    {
        return MethodSpec.methodBuilder("shouldReadFiveByteNegativeValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("buffer.putByte(50, (byte) 0xff)")
                .addStatement("buffer.putByte(51, (byte) 0xff);")
                .addStatement("buffer.putByte(52, (byte) 0xff)")
                .addStatement("buffer.putByte(53, (byte) 0xff)")
                .addStatement("buffer.putByte(54, (byte) 0x0f)")
                .addStatement("varintRO.wrap(buffer,  50,  buffer.capacity())")
                .addStatement("$T.assertEquals($T.MIN_VALUE, varintRO.value())", Assert.class, Integer.class)
                .build();
    }

    private MethodSpec shouldReadMostPositiveValue()
    {
        return MethodSpec.methodBuilder("shouldReadMostPositiveValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("$T offset = 37", int.class)
                .addStatement("buffer.putByte(offset, (byte) 0xfe)")
                .beginControlFlow("for ($T i=1; i < 9; i++)", int.class)
                .addStatement("buffer.putByte(offset + i, (byte) 0xff)")
                .endControlFlow()
                .addStatement("buffer.putByte(offset + 9, (byte) 0x01)")
                .addStatement("varintRO.wrap(buffer,  offset,  buffer.capacity())")
                .addStatement("$T.assertEquals($T.MAX_VALUE, varintRO.value())", Assert.class, Long.class)
                .build();
    }

    private MethodSpec shouldReadMostNegativeValue()
    {
        return MethodSpec.methodBuilder("shouldReadMostNegativeValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("$T offset = 10", int.class)
                .beginControlFlow("for ($T i=0; i < 9; i++)", int.class)
                .addStatement("buffer.putByte(offset + i, (byte) 0xff)")
                .endControlFlow()
                .addStatement("buffer.putByte(offset + 9, (byte) 0x01)")
                .addStatement("varintRO.wrap(buffer,  offset,  buffer.capacity())")
                .addStatement("$T.assertEquals($T.MIN_VALUE, varintRO.value())", Assert.class, Long.class)
                .build();
    }

    private MethodSpec shouldSetMostPositiveValue()
    {
        return MethodSpec.methodBuilder("shouldSetMostPositiveValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("int offset = 0")
                .addStatement("expected.putByte(offset, (byte) 0xfe)")
                .beginControlFlow("for (int i=1; i < 9; i++)")
                .addStatement("expected.putByte(offset + i, (byte) 0xff)")
                .endControlFlow()
                .addStatement("expected.putByte(offset + 9, (byte) 0x01)")
                .addStatement("varintRW.wrap(buffer, offset, buffer.capacity())\n" +
                        "            .set(Long.MAX_VALUE)\n" +
                        "            .build()")
                .addStatement("$T.assertEquals(expected.byteBuffer(), buffer.byteBuffer())", Assert.class)
                .build();
    }

    private MethodSpec shouldSetMostNegativeValue()
    {
        return MethodSpec.methodBuilder("shouldSetMostNegativeValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("int offset = 0")
                .beginControlFlow("for (int i=0; i < 9; i++)")
                .addStatement("expected.putByte(offset + i, (byte) 0xff)")
                .endControlFlow()
                .addStatement("expected.putByte(offset + 9, (byte) 0x01)")
                .addStatement("varintRW.wrap(buffer, offset, buffer.capacity())\n" +
                        "            .set(Long.MIN_VALUE)\n" +
                        "            .build()")
                .addStatement("$T.assertEquals(expected.byteBuffer(), buffer.byteBuffer())", Assert.class)
                .build();
    }

    private MethodSpec shouldSetOneByteValue()
    {
        return MethodSpec.methodBuilder("shouldSetOneByteValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expected.putByte(10, (byte) 0x18)")
                .addStatement("varintRW.wrap(buffer, 10, 21)\n" +
                        "            .set(12)\n" +
                        "            .build()")
                .addStatement("$T.assertEquals(expected.byteBuffer(), buffer.byteBuffer())", Assert.class)
                .build();
    }

    private MethodSpec shouldSetTwoByteValue()
    {
        return MethodSpec.methodBuilder("shouldSetTwoByteValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expected.putByte(0, (byte) 0x83)")
                .addStatement("expected.putByte(1, (byte) 0x01)")
                .addStatement("varintRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "            .set(-66)\n" +
                        "            .build()")
                .addStatement("$T.assertEquals(expected.byteBuffer(), buffer.byteBuffer())", Assert.class)
                .build();
    }

    private MethodSpec shouldReportAsString()
    {
        return MethodSpec.methodBuilder("shouldReportAsString")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("buffer.putByte(50, (byte) 0xfe)")
                .addStatement("buffer.putByte(51, (byte) 0xff)")
                .addStatement("buffer.putByte(52, (byte) 0xff)")
                .addStatement("buffer.putByte(53, (byte) 0xff)")
                .addStatement("varintRO.wrap(buffer,  50,  buffer.capacity())")
                .addStatement("$T.assertEquals($T.toString($T.MAX_VALUE), varintRO.toString())",
                        Assert.class, Integer.class, Integer.class)
                .build();
    }

    private MethodSpec shouldNotBuildWithZeroLengthBuffer()
    {
        return MethodSpec.methodBuilder("shouldNotBuildWithZeroLengthBuffer")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("expected.putByte(10, (byte) 0x18)")
                .addStatement("varintRW.wrap(buffer, 10, 10)")
                .build();
    }

    private MethodSpec shouldNotSetValueWithInsufficientSpace()
    {
        return MethodSpec.methodBuilder("shouldNotSetValueWithInsufficientSpace")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("expected.putByte(10, (byte) 0x18)")
                .addStatement("varintRW.wrap(buffer, 10, 11)\n" +
                        "            .set(70)")
                .build();
    }
}
