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
import org.agrona.DirectBuffer;
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

public final class EnumFlyweightTestGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final ClassName enumTypeName;
    private ClassName enumRO;
    private ClassName enumRW;

    public EnumFlyweightTestGenerator(
        ClassName enumName,
        ClassName flyweight,
        ClassName enumTypeName)
    {
        super(enumName);

        this.enumTypeName = enumTypeName;
        this.classBuilder = classBuilder(thisName).addModifiers(PUBLIC);
        enumRO = enumTypeName.peerClass(enumTypeName.simpleName() + "FW");
        enumRW = enumTypeName.peerClass(enumTypeName.simpleName() + "FW.Builder");
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder.addField(fieldBuffer())
                .addField(fieldRW())
                .addField(fieldRO())
                .addField(fieldExpectedException())
                .addMethod(shouldSetUsingEnum())
                .addMethod(shouldSetUsingFlyweight())
                .addMethod(shouldFailToSetUsingRollFWWithInsufficientSpace())
                .addMethod(shouldFailToBuildWithNothingSet())
                .addMethod(shouldFailToGetWithInvalidEnumNumber())
                .addMethod(asBuffer())
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

    private FieldSpec fieldRW()
    {
        return FieldSpec.builder(enumRW, "fieldRW", PRIVATE, FINAL)
                .initializer("new $T()", enumRW)
                .build();
    }

    private FieldSpec fieldRO()
    {
        return FieldSpec.builder(enumRO, "fieldRO", PRIVATE, FINAL)
                .initializer("new $T()", enumRO)
                .build();
    }

    private FieldSpec fieldExpectedException()
    {
        return FieldSpec.builder(ExpectedException.class, "expectedException", PUBLIC)
                .addAnnotation(Rule.class)
                .initializer("$T.none()", ExpectedException.class)
                .build();
    }

    private MethodSpec shouldSetUsingEnum()
    {
        return MethodSpec.methodBuilder("shouldSetUsingEnum")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .beginControlFlow("for($T enumFW : $T.values())", enumTypeName, enumTypeName)
                .addStatement("$T limit = fieldRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "              .set(enumFW)\n" +
                        "              .build()\n" +
                        "              .limit()", int.class)
                .addStatement("fieldRO.wrap(buffer,  0, limit)")
                .addStatement("$T.assertEquals(enumFW, fieldRO.get())", Assert.class)
                .addStatement("$T.assertNotNull(fieldRO.toString())", Assert.class)
                .endControlFlow()
                .build();
    }

    private MethodSpec shouldSetUsingFlyweight()
    {
        return MethodSpec.methodBuilder("shouldSetUsingFlyweight")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("$T i = 0", int.class)
                .beginControlFlow("for($T enumFW : $T.values())", enumTypeName, enumTypeName)
                .addStatement("$T roll = new $T().wrap($N((byte) i), 0, 1)", enumRO, enumRO,
                        asBuffer())
                .addStatement("$T limit = fieldRW.wrap(buffer, 0, buffer.capacity())\n" +
                        "              .set(roll)\n" +
                        "              .build()\n" +
                        "              .limit()", int.class)
                .addStatement("fieldRO.wrap(buffer,  0, limit)")
                .addStatement("$T.assertEquals(enumFW, fieldRO.get())", Assert.class)
                .addStatement("i++")
                .endControlFlow()
                .build();
    }

    private MethodSpec shouldFailToSetUsingRollFWWithInsufficientSpace()
    {
        return MethodSpec.methodBuilder("shouldFailToSetUsingRollFWWithInsufficientSpace")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("$T roll = new $T().wrap($N((byte) 0), 0, 1)", enumRO, enumRO,
                        asBuffer())
                .addStatement("fieldRW.wrap(buffer, 10, 10).set(roll)")
                .build();
    }

    private MethodSpec shouldFailToBuildWithNothingSet()
    {
        return MethodSpec.methodBuilder("shouldFailToBuildWithNothingSet")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IllegalStateException.class)
                .addStatement("fieldRW.wrap(buffer, 10, buffer.capacity()).build()")
                .build();
    }

    private MethodSpec shouldFailToGetWithInvalidEnumNumber()
    {
        return MethodSpec.methodBuilder("shouldFailToGetWithInvalidEnumNumber")
                .addModifiers(PUBLIC)
                .addAnnotation(Test.class)
                .addStatement("expectedException.expect($T.class)", IllegalArgumentException.class)
                .addStatement("fieldRO.wrap(buffer, 10, buffer.capacity())")
                .addStatement("fieldRO.get()")
                .build();
    }

    private MethodSpec asBuffer()
    {
        return MethodSpec.methodBuilder("asBuffer")
                .addModifiers(PRIVATE, STATIC)
                .addParameter(byte.class, "value")
                .returns(DirectBuffer.class)
                .addStatement("$T valueBuffer = new UnsafeBuffer($T.allocateDirect(1))", MutableDirectBuffer.class,
                        ByteBuffer.class)
                .addStatement("valueBuffer.putByte(0, value)")
                .addStatement("return valueBuffer")
                .build();
    }
}
