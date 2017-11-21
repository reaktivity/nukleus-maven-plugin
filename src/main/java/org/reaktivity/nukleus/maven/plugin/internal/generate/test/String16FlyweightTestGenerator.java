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
        return classBuilder.addField(fieldConstantLengthSize())
            .addField(fieldBuffer())
            .addField(fieldString16Builder())
            .addField(fieldString16ReadOnly())
            .addMethod(values())
            .addMethod(shouldDefaultAfterRewrap())
            .addMethod(shouldDefaultToEmpty())
            .addMethod(shouldFailToWrapWithInsufficientLength())
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
            .initializer("new $T($T.allocateDirect(100000)) \n" +
                "{\n"+
                "    {\n"+
                "        // Make sure the code is not secretly relying upon memory being initialized to 0\n" +
                "        setMemory(0, capacity(), (byte) 0xF);\n" +
                "    }\n" +
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
            .addStatement(
                "  int limit = stringRW.wrap(buffer, 0, buffer.capacity())\n" +
                "    .set(\"Hello, world\", $T.UTF_8)\n" +
                "    .build()\n" +
                "    .limit()", StandardCharsets.class)
            .addStatement("String16FW string = stringRW.wrap(buffer, 0, limit).build()")
            .addStatement("$T.assertNull(string.asString())", Assert.class)
            .addStatement("$T.assertEquals(LENGTH_SIZE, string.limit())", Assert.class)
            .addStatement("$T.assertEquals(LENGTH_SIZE, string.sizeof())", Assert.class)
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
                .addStatement("$T.assertEquals(LENGTH_SIZE, stringRO.limit())", Assert.class)
                .addStatement("$T.assertEquals(LENGTH_SIZE, stringRO.sizeof())", Assert.class)
                .build();
    }

    private MethodSpec shouldFailToWrapWithInsufficientLength()
    {
        AnnotationSpec testAnnotation = AnnotationSpec.builder(Test.class)
                .addMember("expected", "$L", "IndexOutOfBoundsException.class")
                .build();

        return MethodSpec.methodBuilder("shouldFailToWrapWithInsufficientLength")
                .addModifiers(PUBLIC)
                .addAnnotation(testAnnotation)
                .addStatement("stringRW.wrap(buffer, 10, 10)")
                .build();
    }

}
