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

public final class Variant32FlyweightTestGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final ClassName octetsFlyweightClassName;
    private final ClassName octetsFlyweightBuilderClassName;

    public Variant32FlyweightTestGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("Varint32FWTest"));
        octetsFlyweightClassName = flyweightType.peerClass("Varint32FW");
        octetsFlyweightBuilderClassName = flyweightType.peerClass("Varint32FW.Builder");

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
                .addMethod(shouldNotWrapValueWith33bits())
                .addMethod(shouldReadOneByteValue())
                .addMethod(shouldFailToGetValueWithExceedingSize())
                .addMethod(shouldFailToBuildWithoutSettingValue())
                .addMethod(shouldReadTwoByteValue())
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
        return FieldSpec.builder(octetsFlyweightBuilderClassName, "varint32RW", PRIVATE, FINAL)
                .initializer("new $T()", octetsFlyweightBuilderClassName)
                .build();
    }

    private FieldSpec fieldOctetsReadOnly()
    {
        return FieldSpec.builder(octetsFlyweightClassName, "varint32RO", PRIVATE, FINAL)
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
                .addStatement("varint32RO.wrap(buffer,  10,  10)")
                .build();
    }

    private MethodSpec shouldNotWrapValueWith33bits()
    {
        return MethodSpec.methodBuilder("shouldNotWrapValueWith33bits")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                .addStatement("buffer.putByte(50, (byte) 0xfe)")
                .addStatement("buffer.putByte(51, (byte) 0xff)")
                .addStatement("buffer.putByte(52, (byte) 0xff)")
                .addStatement("buffer.putByte(53, (byte) 0xff)")
                .addStatement("buffer.putByte(54, (byte) 0x1f)")
                .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                .addStatement("expectedException.expectMessage(\"offset 50 exceeds 32 bits\")")
                .addStatement("varint32RO.wrap(buffer,  50,  buffer.capacity())")
                .addStatement("$T.assertEquals(Integer.MAX_VALUE, varint32RO.value())", Assert.class)
                .build();
    }

    private MethodSpec shouldReadOneByteValue()
    {
        return MethodSpec.methodBuilder("shouldReadOneByteValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("$T offset = 13", int.class)
                .addStatement("buffer.putByte(offset,  (byte) 0x18)")
                .addStatement("$T.assertEquals(offset + 1, varint32RO.wrap(buffer,  offset,  21).limit())",
                        Assert.class)
                .addStatement("$T.assertEquals(12, varint32RO.value())", Assert.class)
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
                .addStatement("varint32RO.wrap(buffer,  50,  buffer.capacity())")
                .addStatement("varint32RO.value()")
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
                .addStatement("varint32RW.wrap(buffer,  50,  buffer.capacity())\n" +
                        "                .build()")
                .build();
    }

    private MethodSpec shouldReadTwoByteValue()
    {
        return MethodSpec.methodBuilder("shouldReadTwoByteValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("buffer.putByte(50, (byte) 0x83)")
                .addStatement("buffer.putByte(51, (byte) 0x01)")
                .addStatement("$T.assertEquals(52, varint32RO.wrap(buffer,  50,  buffer.capacity()).limit())",
                        Assert.class)
                .addStatement("$T.assertEquals(-66, varint32RO.value())", Assert.class)
                .build();
    }

    private MethodSpec shouldReadMostPositiveValue()
    {
        return MethodSpec.methodBuilder("shouldReadMostPositiveValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("buffer.putByte(50, (byte) 0xfe)")
                .addStatement("buffer.putByte(51, (byte) 0xff)")
                .addStatement("buffer.putByte(52, (byte) 0xff)")
                .addStatement("buffer.putByte(53, (byte) 0xff)")
                .addStatement("buffer.putByte(54, (byte) 0x0f)")
                .addStatement("$T.assertEquals(55, varint32RO.wrap(buffer,  50,  buffer.capacity()).limit())",
                        Assert.class)
                .addStatement("$T.assertEquals($T.MAX_VALUE, varint32RO.value())", Assert.class, Integer.class)
                .build();
    }

    private MethodSpec shouldReadMostNegativeValue()
    {
        return MethodSpec.methodBuilder("shouldReadMostNegativeValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("buffer.putByte(50, (byte) 0xff)")
                .addStatement("buffer.putByte(51, (byte) 0xff);")
                .addStatement("buffer.putByte(52, (byte) 0xff)")
                .addStatement("buffer.putByte(53, (byte) 0xff)")
                .addStatement("buffer.putByte(54, (byte) 0x0f)")
                .addStatement("varint32RO.wrap(buffer,  50,  buffer.capacity())")
                .addStatement("$T.assertEquals($T.MIN_VALUE, varint32RO.value())", Assert.class, Integer.class)
                .build();
    }

    private MethodSpec shouldSetMostPositiveValue()
    {
        return MethodSpec.methodBuilder("shouldSetMostPositiveValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expected.putByte(50, (byte) 0xfe)")
                .addStatement("expected.putByte(51, (byte) 0xff)")
                .addStatement("expected.putByte(52, (byte) 0xff)")
                .addStatement("expected.putByte(53, (byte) 0xff)")
                .addStatement("expected.putByte(54, (byte) 0x0f)")
                .addStatement("varint32RW.wrap(buffer,  50,  buffer.capacity())\n" +
                        "            .set($T.MAX_VALUE)\n" +
                        "            .build()", Integer.class)
                .addStatement("$T.assertEquals(expected.byteBuffer(), buffer.byteBuffer())", Assert.class)
                .build();
    }

    private MethodSpec shouldSetMostNegativeValue()
    {
        return MethodSpec.methodBuilder("shouldSetMostNegativeValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expected.putByte(50, (byte) 0xff)")
                .addStatement("expected.putByte(51, (byte) 0xff)")
                .addStatement("expected.putByte(52, (byte) 0xff)")
                .addStatement("expected.putByte(53, (byte) 0xff)")
                .addStatement("expected.putByte(54, (byte) 0x0f)")
                .addStatement("varint32RW.wrap(buffer,  50,  buffer.capacity())\n" +
                        "            .set($T.MIN_VALUE)\n" +
                        "            .build()", Integer.class)
                .addStatement("$T.assertEquals(expected.byteBuffer(), buffer.byteBuffer())", Assert.class)
                .build();
    }

    private MethodSpec shouldSetOneByteValue()
    {
        return MethodSpec.methodBuilder("shouldSetOneByteValue")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expected.putByte(10, (byte) 0x18)")
                .addStatement("varint32RW.wrap(buffer, 10, 21)\n" +
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
                .addStatement("varint32RW.wrap(buffer, 0, buffer.capacity())\n" +
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
                .addStatement("varint32RO.wrap(buffer,  50,  buffer.capacity())")
                .addStatement("$T.assertEquals($T.toString($T.MAX_VALUE), varint32RO.toString())",
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
                .addStatement("varint32RW.wrap(buffer, 10, 10)")
                .build();
    }

    private MethodSpec shouldNotSetValueWithInsufficientSpace()
    {
        return MethodSpec.methodBuilder("shouldNotSetValueWithInsufficientSpace")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("expected.putByte(10, (byte) 0x18)")
                .addStatement("varint32RW.wrap(buffer, 10, 11)\n" +
                        "            .set(70)")
                .build();
    }
}
