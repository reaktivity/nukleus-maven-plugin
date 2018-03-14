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

import com.squareup.javapoet.*;
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

public final class OctetsFlyweightTestGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final ClassName octetsFlyweightClassName;
    private final ClassName octetsFlyweightBuilderClassName;

    private enum SetterVariant
    {
        OCTETS,
        BYTE_ARRAY,
        BUFFER,
        VISITOR
    }

    public OctetsFlyweightTestGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("OctetsFWTest"));
        octetsFlyweightClassName = flyweightType.peerClass("OctetsFW");
        octetsFlyweightBuilderClassName = flyweightType.peerClass("OctetsFW.Builder");

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
                .addMethod(shouldDefaultToEmpty())
                .addMethod(shouldCreateWithZeroLength())
                .addMethod(shouldSetUsing(SetterVariant.OCTETS))
                .addMethod(shouldSetUsing(SetterVariant.BUFFER))
                .addMethod(shouldSetUsing(SetterVariant.BYTE_ARRAY))
                .addMethod(shouldSetUsing(SetterVariant.VISITOR))
                .addMethod(shouldGetUsingVisitor())
                .addMethod(shouldPutUsing(SetterVariant.OCTETS))
                .addMethod(shouldPutUsing(SetterVariant.BUFFER))
                .addMethod(shouldPutUsing(SetterVariant.BYTE_ARRAY))
                .addMethod(shouldPutUsing(SetterVariant.VISITOR))
                .addMethod(shouldFailToSetWhenExceedsMaxLimit(SetterVariant.OCTETS))
                .addMethod(shouldFailToSetWhenExceedsMaxLimit(SetterVariant.BUFFER))
                .addMethod(shouldFailToSetWhenExceedsMaxLimit(SetterVariant.BYTE_ARRAY))
                .addMethod(shouldFailToSetWhenExceedsMaxLimit(SetterVariant.VISITOR))
                .addMethod(shouldFailToPutWhenExceedsMaxLimit(SetterVariant.OCTETS))
                .addMethod(shouldFailToPutWhenExceedsMaxLimit(SetterVariant.BUFFER))
                .addMethod(shouldFailToPutWhenExceedsMaxLimit(SetterVariant.BYTE_ARRAY))
                .addMethod(shouldFailToPutWhenExceedsMaxLimit(SetterVariant.VISITOR))
                .addMethod(shouldReturnString())
                .addMethod(setBufferValue())
                .addMethod(asOctetsFW())
                .addMethod(asBuffer())
                .addMethod(asString())
                .addMethod(assertSetValue())
                .build();
    }

    private FieldSpec fieldOctetsBuilder()
    {
        return FieldSpec.builder(octetsFlyweightBuilderClassName, "octetsRW", PRIVATE, FINAL)
                .initializer("new $T()", octetsFlyweightBuilderClassName)
                .build();
    }

    private FieldSpec fieldOctetsReadOnly()
    {
        return FieldSpec.builder(octetsFlyweightClassName, "octetsRO", PRIVATE, FINAL)
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

    private MethodSpec shouldDefaultToEmpty()
    {
        return MethodSpec.methodBuilder("shouldDefaultToEmpty")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T limit = octetsRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "                .build()\n" +
                        "                .limit()", int.class)
                .addStatement("octetsRO.wrap(buffer,  0,  limit)")
                .addStatement("$T.assertEquals(0, octetsRO.sizeof())", Assert.class)
                .build();
    }

    private MethodSpec shouldCreateWithZeroLength()
    {
        return MethodSpec.methodBuilder("shouldCreateWithZeroLength")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("octetsRW.wrap(buffer, 10, 10)")
                .addStatement("octetsRO.wrap(buffer,  10, 10)")
                .addStatement("$T.assertEquals(0, octetsRO.sizeof())", Assert.class)
                .build();
    }

    private MethodSpec shouldSetUsing(SetterVariant setterVariant)
    {
         MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("shouldSetUsing"+setterVariant)
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T offset = 0", int.class)
                .addStatement("$T expectedLimit = $N(expected, offset)", int.class, setBufferValue())
                .addStatement("$T octetsFW = octetsRW.wrap(buffer, offset, buffer.capacity())", octetsFlyweightBuilderClassName);

         switch (setterVariant)
         {
             case OCTETS:
                 methodBuilder.addStatement("octetsFW.set($N(\"value1\"))", asOctetsFW());
                 break;
             case BUFFER:
                 methodBuilder.addStatement("octetsFW.set(asBuffer(\"value1\"), 0, \"value1\".length())", setBufferValue());
                 break;
             case BYTE_ARRAY:
                 methodBuilder.addStatement("octetsFW.set(\"value1\".getBytes($T.UTF_8))", StandardCharsets.class);
                 break;
             case VISITOR:
                 methodBuilder.addStatement("octetsFW.set((b, o, l) ->\n" +
                         "                {\n" +
                         "                    b.putBytes(o, \"value1\".getBytes($T.UTF_8));\n" +
                         "                    return 6;\n" +
                         "                })", StandardCharsets.class);
                 break;
         }

        methodBuilder.addStatement("$T limit = octetsFW.build().limit()", int.class)
                .addStatement("$T.assertEquals(expectedLimit, limit)", Assert.class)
                .addStatement("$T.assertEquals(expected, buffer)", Assert.class)
                .addStatement("octetsRO.wrap(buffer,  0,  limit)")
                .addStatement("$N(octetsRO)", assertSetValue());

        return methodBuilder.build();
    }

    private MethodSpec shouldGetUsingVisitor()
    {
        return MethodSpec.methodBuilder("shouldGetUsingVisitor")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T limit = octetsRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "                .set((b, o, l) ->\n" +
                        "                {\n" +
                        "                    b.putBytes(o, \"value1\".getBytes($T.UTF_8));\n" +
                        "                    return 1;\n" +
                        "                })\n" +
                        "                .build()\n" +
                        "                .limit()", int.class, StandardCharsets.class)
                .addStatement("octetsRO.wrap(buffer,0, limit)")
                .addStatement("$T.assertEquals(\"value1\", octetsRO.get((b, o, l) ->\n" +
                        "                b.getStringWithoutLengthUtf8(0, 6)).toString())", Assert.class)
                .build();
    }

    private MethodSpec shouldPutUsing(SetterVariant setterVariant)
    {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("shouldPutUsing"+setterVariant)
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T offset = 0", int.class)
                .addStatement("$T expectedLimit = $N(expected, offset)", int.class, setBufferValue())
                .addStatement("$T octetsFW = octetsRW.wrap(buffer, offset, buffer.capacity())", octetsFlyweightBuilderClassName);

        switch (setterVariant)
        {
            case OCTETS:
                methodBuilder.addStatement("octetsFW.put($N(\"val\"))", asOctetsFW())
                        .addStatement("octetsFW.put($N(\"ue1\"))", asOctetsFW());
                break;
            case BUFFER:
                methodBuilder.addStatement("octetsFW.put(asBuffer(\"val\"), 0, \"val\".length())", setBufferValue())
                        .addStatement("octetsFW.put(asBuffer(\"ue1\"), 0, \"ue1\".length())", setBufferValue());
                break;
            case BYTE_ARRAY:
                methodBuilder.addStatement("octetsFW.put(\"val\".getBytes($T.UTF_8))", StandardCharsets.class)
                        .addStatement("octetsFW.put(\"ue1\".getBytes($T.UTF_8))", StandardCharsets.class);
                break;
            case VISITOR:
                methodBuilder.addStatement("octetsFW.put((b, o, l) ->\n" +
                        "                {\n" +
                        "                    b.putBytes(o, \"val\".getBytes($T.UTF_8));\n" +
                        "                    return 3;\n" +
                        "                })", StandardCharsets.class)
                        .addStatement("octetsFW.put((b, o, l) ->\n" +
                        "                {\n" +
                        "                    b.putBytes(o, \"ue1\".getBytes($T.UTF_8));\n" +
                        "                    return 3;\n" +
                        "                })", StandardCharsets.class);
                break;
        }

        methodBuilder.addStatement("$T limit = octetsFW.build().limit()", int.class)
                .addStatement("$T.assertEquals(expectedLimit, limit)", Assert.class)
                .addStatement("$T.assertEquals(expected, buffer)", Assert.class)
                .addStatement("octetsRO.wrap(buffer,  0,  limit)")
                .addStatement("$N(octetsRO)", assertSetValue());

        return methodBuilder.build();
    }

    private MethodSpec shouldFailToSetWhenExceedsMaxLimit(SetterVariant setterVariant)
    {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("shouldFailToSetUsing" +
                setterVariant + "WhenExceedsMaxLimit")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("buffer.setMemory(0,  buffer.capacity(), (byte) 0x00)")
                .beginControlFlow("try");
        switch (setterVariant)
        {
            case OCTETS:
                methodBuilder.addStatement("octetsRW.wrap(buffer, 10, 11)\n" +
                        "                .set($N(\"12\"))", asOctetsFW());
                break;
            case BUFFER:
                methodBuilder.addStatement("octetsRW.wrap(buffer, 10, 11)\n" +
                        "                .set($N(\"12\"), 0, 2)", asBuffer());
                break;
            case BYTE_ARRAY:
                methodBuilder.addStatement("octetsRW.wrap(buffer, 10, 11)\n" +
                        "                .set(\"12\".getBytes($T.UTF_8))", StandardCharsets.class);
                break;
            case VISITOR:
                methodBuilder.addStatement("octetsRW.wrap(buffer, 10, 11)\n" +
                                "                    .set((b, o, l) ->\n" +
                                "                    { b.putBytes(o, \"12\".getBytes($T.UTF_8)); return 2; })",
                        StandardCharsets.class);
                break;
        }
        methodBuilder.endControlFlow()
                .beginControlFlow("finally")
                .addStatement("$T[] bytes = new $T[2]", byte.class, byte.class)
                .addStatement("buffer.getBytes(10, bytes)")
                .addStatement("$T.assertEquals(\"Buffer shows memory was written beyond maxLimit: \" + $T.toHex(bytes),\n" +
                        "                    0, buffer.getByte(11))", Assert.class, BitUtil.class)
                .endControlFlow();
        return methodBuilder.build();
    }

    private MethodSpec shouldFailToPutWhenExceedsMaxLimit(SetterVariant setterVariant)
    {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("shouldFailToPutUsing" +
                setterVariant + "WhenExceedsMaxLimit")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("buffer.setMemory(0,  buffer.capacity(), (byte) 0x00)")
                .beginControlFlow("try");
        switch (setterVariant)
        {
            case OCTETS:
                methodBuilder.addStatement("octetsRW.wrap(buffer, 10, 11)\n" +
                                "                .put($N(\"1\"))", asOctetsFW())
                        .addStatement("octetsRW.put($N(\"2\"))", asOctetsFW());
                break;
            case BUFFER:
                methodBuilder.addStatement("octetsRW.wrap(buffer, 10, 11)\n" +
                        "                .put($N(\"1\"), 0, 1)", asBuffer())
                        .addStatement("octetsRW.put($N(\"2\"), 0, 1)", asBuffer());

                break;
            case BYTE_ARRAY:
                methodBuilder.addStatement("octetsRW.wrap(buffer, 10, 11)\n" +
                        "                .put(\"1\".getBytes($T.UTF_8))", StandardCharsets.class)
                        .addStatement("octetsRW.put(\"2\".getBytes($T.UTF_8))", StandardCharsets.class);

                break;
            case VISITOR:
                methodBuilder.addStatement("octetsRW.wrap(buffer, 10, 11)\n" +
                        "        .put((b, o, l) ->\n" +
                        "            { b.putBytes(o, \"1\".getBytes($T.UTF_8)); return 1; })", StandardCharsets.class)
                        .addStatement("octetsRW.put((b, o, l) ->\n" +
                        "                    { b.putBytes(o, \"2\".getBytes($T.UTF_8)); return 1; })",
                                StandardCharsets.class);
                break;
        }
        methodBuilder.endControlFlow()
                .beginControlFlow("finally")
                .addStatement("$T[] bytes = new $T[2]", byte.class, byte.class)
                .addStatement("buffer.getBytes(10, bytes)")
                .addStatement("$T.assertEquals(\"Buffer shows memory was written beyond maxLimit: \" + $T.toHex(bytes),\n" +
                        "                    0, buffer.getByte(11))", Assert.class, BitUtil.class)
                .endControlFlow();
        return methodBuilder.build();
    }

    private MethodSpec shouldReturnString()
    {
        return MethodSpec.methodBuilder("shouldReturnString")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addException(Exception.class)
                .addStatement("$T.assertNotNull(octetsRO.toString())", Assert.class)
                .build();
    }

    private MethodSpec setBufferValue()
    {
        return MethodSpec.methodBuilder("setBufferValue")
                .addModifiers(PUBLIC, STATIC)
                .returns(int.class)
                .addParameter(MutableDirectBuffer.class, "buffer")
                .addParameter(int.class, "offset")
                .addStatement("$T value = \"value1\"", String.class)
                .addStatement("buffer.putBytes(offset, value.getBytes($T.UTF_8), offset, value.length())",
                        StandardCharsets.class)
                .addStatement("return offset + value.length()")
                .build();
    }

    private MethodSpec asOctetsFW()
    {
        return MethodSpec.methodBuilder("asOctetsFW")
                .addModifiers(PUBLIC, STATIC)
                .returns(octetsFlyweightClassName)
                .addParameter(String.class, "value")
                .addStatement("$T buffer = new $T($T.allocateDirect($T.SIZE + value.length()))",
                        MutableDirectBuffer.class, UnsafeBuffer.class, ByteBuffer.class, Byte.class)
                .addStatement("return new $T().wrap(buffer, 0, buffer.capacity()).set(value.getBytes($T.UTF_8)).build()",
                        octetsFlyweightBuilderClassName, StandardCharsets.class)
                .build();
    }

    private MethodSpec asBuffer()
    {
        return MethodSpec.methodBuilder("asBuffer")
                .addModifiers(PUBLIC, STATIC)
                .returns(MutableDirectBuffer.class)
                .addParameter(String.class, "value")
                .addStatement("$T buffer = new $T($T.allocateDirect(value.length()))", MutableDirectBuffer.class,
                        UnsafeBuffer.class, ByteBuffer.class)
                .addStatement("buffer.putStringWithoutLengthUtf8(0, value)")
                .addStatement("return buffer")
                .build();
    }

    private MethodSpec asString()
    {
        return MethodSpec.methodBuilder("asString")
                .addModifiers(PUBLIC, STATIC)
                .returns(String.class)
                .addParameter(octetsFlyweightClassName, "octets")
                .addStatement("$T[] bytes = new $T[octets.sizeof()]", byte.class, byte.class)
                .addStatement("octets.buffer().getBytes(octets.offset(), bytes)")
                .addStatement("return new $T(bytes, $T.UTF_8)", String.class, StandardCharsets.class)
                .build();
    }

    private MethodSpec assertSetValue()
    {
        return MethodSpec.methodBuilder("assertSetValue")
                .addParameter(octetsFlyweightClassName, "octetsFW")
                .addModifiers(PRIVATE)
                .addStatement("$T.assertEquals(6, octetsFW.sizeof())", Assert.class)
                .addStatement("$T.assertEquals(\"value1\", $N(octetsFW))", Assert.class, asString())
                .build();
    }

}
