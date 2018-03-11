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
import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ClassSpecGenerator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.*;

public final class StringFlyweightTestGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final ClassName stringFlyweightClassName;
    private final ClassName stringFlyweightBuilderClassName;


    private enum SetterVariant
    {
        STRING,
        STRINGFW,
        BUFFER
    }

    public StringFlyweightTestGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("StringFWTest"));
        stringFlyweightClassName = flyweightType.peerClass("StringFW");
        stringFlyweightBuilderClassName = flyweightType.peerClass("StringFW.Builder");

        this.classBuilder = classBuilder(thisName).addModifiers(PUBLIC, FINAL);
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder.addField(fieldConstantLengthSize())
                .addField(fieldBuffer())
                .addField(fieldExpected())
                .addField(fieldOctetsBuilder())
                .addField(fieldOctetsReadOnly())
                .addField(fieldExpectedException())
                .addMethod(shouldBuildStringWithMaximumLength())
                .addMethod(shouldSetUsing(SetterVariant.STRING))
                .addMethod(shouldSetUsing(SetterVariant.STRINGFW))
                .addMethod(shouldSetUsing(SetterVariant.BUFFER))
                .addMethod(shouldDefaultAfterRewrap())
                .addMethod(shouldDefaultToEmpty())
                .addMethod(shouldSetToNullWithoutCharacterSet())
                .addMethod(shouldSetToNull())
                .addMethod(shouldFailToWrapWithInsufficientLength())
                .addMethod(shouldFailToSetWWhenExceedsMaxLimit(SetterVariant.STRING))
                .addMethod(shouldFailToSetWWhenExceedsMaxLimit(SetterVariant.STRINGFW))
                .addMethod(shouldFailToSetWWhenExceedsMaxLimit(SetterVariant.BUFFER))
                .addMethod(shouldSetToNullUsingStringSetter())
                .addMethod(shouldFailToBuildLargeString())
                .addMethod(shouldReturnString())
                .addMethod(asBuffer())
                .addMethod(asStringFW())
                .addMethod(setFieldValue())
                .addMethod(setBufferValue())
                .addMethod(assertLengthSize())
                .addMethod(assertStringValue())
                .build();
    }

    private FieldSpec fieldConstantLengthSize()
    {
        return FieldSpec.builder(int.class, "LENGTH_SIZE", PRIVATE, STATIC, FINAL)
                .initializer("1")
                .build();
    }

    private FieldSpec fieldOctetsBuilder()
    {
        return FieldSpec.builder(stringFlyweightBuilderClassName, "stringRW", PRIVATE, FINAL)
                .initializer("new $T()", stringFlyweightBuilderClassName)
                .build();
    }

    private FieldSpec fieldOctetsReadOnly()
    {
        return FieldSpec.builder(stringFlyweightClassName, "stringRO", PRIVATE, FINAL)
                .initializer("new $T()", stringFlyweightClassName)
                .build();
    }

    private FieldSpec fieldBuffer()
    {
        return FieldSpec.builder(MutableDirectBuffer.class, "buffer", PRIVATE, FINAL)
            .initializer("new $T($T.allocateDirect($T.MAX_LENGTH+1)); \n" +
                "{\n" +
                "    buffer.setMemory(0, buffer.capacity(), (byte) 0xF);\n" +
                "}", UnsafeBuffer.class, ByteBuffer.class, stringFlyweightClassName)
            .build();
    }

    private FieldSpec fieldExpected()
    {
        return FieldSpec.builder(MutableDirectBuffer.class, "expected", PRIVATE, FINAL)
            .initializer("new $T($T.allocateDirect($T.MAX_LENGTH+1)); \n" +
                    "{\n" +
                    "    expected.setMemory(0, expected.capacity(), (byte) 0xF);\n" +
                    "}", UnsafeBuffer.class, ByteBuffer.class, stringFlyweightClassName)
            .build();
    }

    private FieldSpec fieldExpectedException()
    {
        return FieldSpec.builder(ExpectedException.class, "expectedException", PUBLIC)
                .addAnnotation(Rule.class)
                .initializer("$T.none()", ExpectedException.class)
                .build();
    }

    private MethodSpec shouldBuildStringWithMaximumLength()
    {
        return MethodSpec.methodBuilder("shouldBuildStringWithMaximumLength")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T tooLong = $T.MAX_LENGTH", int.class, stringFlyweightClassName)
                .addStatement("$T str = $T.format(\"%\" + $T.toString(tooLong) + \"s\", \"0\")", String.class,
                        String.class, Integer.class)
                .addStatement("$N(str)", setFieldValue())
                .build();
    }

    private MethodSpec shouldSetUsing(SetterVariant setterVariant)
    {
        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("shouldSetUsing" + setterVariant)
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T offset = 0", int.class)
                .addStatement("$T expectedLimit = $N(expected, offset)", int.class, setBufferValue());
        switch(setterVariant)
        {
            case STRING:
                methodSpec.addStatement("$T limit = $N(\"value1\")", int.class, setFieldValue());
                break;
            case STRINGFW:
                methodSpec.addStatement("$T limit = stringRW.wrap(buffer, offset, 50)\n" +
                        "                .set($N(\"value1\"))\n" +
                        "                .build()\n" +
                        "                .limit()", int.class, asStringFW());
                break;
            case BUFFER:
                methodSpec.addStatement("$T limit = stringRW.wrap(buffer, offset, 50)\n" +
                        "                .set($N(\"value1\"), 0, 6)\n" +
                        "                .build()\n" +
                        "                .limit()", int.class, asBuffer());
                break;
        }
        methodSpec.addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$T.assertEquals(expectedLimit, limit)", Assert.class)
                .addStatement("$T.assertEquals(expected, buffer)", Assert.class)
                .addStatement("assertStringValue(stringRO)");

        return methodSpec.build();
    }

    private MethodSpec shouldDefaultAfterRewrap()
    {
        return MethodSpec.methodBuilder("shouldDefaultAfterRewrap")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T limit = $N(\"Hello World\")", int.class, setFieldValue())
                .addStatement("$T string = stringRW.wrap(buffer, 0, limit)\n" +
                        "                .build()", stringFlyweightClassName)
                .addStatement("$N(string)", assertLengthSize())
                .build();
    }

    private MethodSpec shouldDefaultToEmpty()
    {
        return MethodSpec.methodBuilder("shouldDefaultToEmpty")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T limit = stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "                .build()\n" +
                        "                .limit()", int.class)
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
                .addStatement("$T limit = stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "                .set(null)\n" +
                        "                .build()\n" +
                        "                .limit()", int.class)
                .addStatement("$T.assertEquals(1, limit)", Assert.class)
                .addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$N(stringRO)", assertLengthSize())
                .build();
    }

    private MethodSpec shouldSetToNull()
    {
        return MethodSpec.methodBuilder("shouldSetToNull")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T limit = setFieldValue(null)", int.class)
                .addStatement("$T.assertEquals(1, limit)", Assert.class)
                .addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$N(stringRO)", assertLengthSize())
                .build();
    }

    private MethodSpec shouldFailToWrapWithInsufficientLength()
    {
        return MethodSpec.methodBuilder("shouldFailToWrapWithInsufficientLength")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("stringRW.wrap(buffer, 10, 10)")
                .build();
    }

    private MethodSpec  shouldFailToSetWWhenExceedsMaxLimit(SetterVariant setterVariant)
    {
        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("shouldFailToSetUsing" + setterVariant +
                "WhenExceedsMaxLimit")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("buffer.setMemory(0,  buffer.capacity(), (byte) 0x00)")
                .beginControlFlow("try");
        switch(setterVariant)
        {
            case STRING:
                methodSpec.addStatement("stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)\n" +
                        "                .set(\"1\", $T.UTF_8)", StandardCharsets.class);
                break;
            case STRINGFW:
                methodSpec.addStatement("stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)\n" +
                        "                .set($N(\"1\"))", asStringFW());
                break;
            case BUFFER:
                methodSpec.addStatement("stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)\n" +
                        "                .set(buffer, 0, 1)");
                break;
        }

        methodSpec.endControlFlow()
                .beginControlFlow("finally")
                .addStatement("$T[] bytes = new $T[1 + LENGTH_SIZE]", byte.class, byte.class)
                .addStatement("buffer.getBytes(10, bytes)")
                .addStatement("$T.assertEquals(\"Buffer shows memory was written beyond maxLimit: \" + $T.toHex(bytes),\n" +
                        "                         0, buffer.getByte(10 + LENGTH_SIZE))", Assert.class, BitUtil.class)
                .endControlFlow();

        return methodSpec.build();
    }

    private MethodSpec shouldSetToNullUsingStringSetter()
    {
        return MethodSpec.methodBuilder("shouldSetToNullUsingStringSetter")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T limit = stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "                .set(null, $T.UTF_8)\n" +
                        "                .build()\n" +
                        "                .limit()", int.class, StandardCharsets.class)
                .addStatement("$T.assertEquals(1, limit)", Assert.class)
                .addStatement("stringRO.wrap(buffer,  0,  limit)")
                .addStatement("$T.assertEquals(LENGTH_SIZE, stringRO.limit())", Assert.class)
                .addStatement("$T.assertEquals(LENGTH_SIZE, stringRO.sizeof())", Assert.class)
                .addStatement("$T.assertNull(stringRO.asString())", Assert.class)
                .build();
    }

    private MethodSpec shouldFailToBuildLargeString()
    {
        return MethodSpec.methodBuilder("shouldFailToBuildLargeString")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                .addStatement("$T str = String.format(\"%270s\", \"0\")", String.class)
                .addStatement("stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "                .set(str, $T.UTF_8)", StandardCharsets.class)
                .build();
    }

    private MethodSpec shouldReturnString()
    {
        return MethodSpec.methodBuilder("shouldReturnString")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("$T.assertNotNull(stringRO.toString())", Assert.class)
                .build();
    }

    private MethodSpec asBuffer()
    {
        return MethodSpec.methodBuilder("asBuffer")
                .addModifiers(PRIVATE, STATIC)
                .returns(MutableDirectBuffer.class)
                .addParameter(String.class, "value")
                .addStatement("$T buffer = new $T($T.allocateDirect(value.length()))", MutableDirectBuffer.class,
                        UnsafeBuffer.class, ByteBuffer.class)
                .addStatement("buffer.putStringWithoutLengthUtf8(0, value)")
                .addStatement("return buffer")
                .build();
    }

    private MethodSpec asStringFW()
    {
        return MethodSpec.methodBuilder("asStringFW")
                .addModifiers(PRIVATE, STATIC)
                .returns(stringFlyweightClassName)
                .addParameter(String.class, "value")
                .addStatement("$T buffer = new $T($T.allocateDirect(Byte.SIZE + value.length()))",
                        MutableDirectBuffer.class, UnsafeBuffer.class, ByteBuffer.class)
                .addStatement("return new $T().wrap(buffer, 0, buffer.capacity()).set(value, $T.UTF_8).build()",
                        stringFlyweightBuilderClassName, StandardCharsets.class)
                .build();
    }

    private MethodSpec setFieldValue()
    {
        return MethodSpec.methodBuilder("setFieldValue")
                .returns(int.class)
                .addParameter(String.class, "value")
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
                .addStatement("buffer.putByte(offset, ($T) \"value1\".length())", byte.class)
                .addStatement("buffer.putBytes(offset +=1, \"value1\".getBytes($T.UTF_8))", StandardCharsets.class)
                .addStatement("return offset + 6")
                .build();
    }

    private MethodSpec assertLengthSize()
    {
        return MethodSpec.methodBuilder("assertLengthSize")
                .addModifiers(STATIC)
                .addParameter(stringFlyweightClassName, "stringFW")
                .addStatement("$T.assertNull(stringFW.asString())", Assert.class)
                .addStatement("$T.assertEquals(LENGTH_SIZE, stringFW.limit())", Assert.class)
                .addStatement("$T.assertEquals(LENGTH_SIZE, stringFW.sizeof())", Assert.class)
                .build();
    }

    private MethodSpec assertStringValue()
    {
        return MethodSpec.methodBuilder("assertStringValue")
                .addModifiers(STATIC)
                .addParameter(stringFlyweightClassName, "stringFW")
                .addStatement("$T.assertEquals(6 + LENGTH_SIZE, stringFW.limit())", Assert.class)
                .addStatement("$T.assertEquals(6 + LENGTH_SIZE, stringFW.sizeof())", Assert.class)
                .addStatement("$T.assertEquals(\"value1\", stringFW.asString())", Assert.class)
                .build();
    }
}
