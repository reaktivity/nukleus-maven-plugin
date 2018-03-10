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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ClassSpecGenerator;

import java.nio.ByteBuffer;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.*;

public final class FlyweightTestGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder classBuilder;
    private final ClassName flyweightType;

    public FlyweightTestGenerator(
        ClassName flyweightType)
    {
        super(flyweightType.peerClass("FlyweightTest"));
        this.flyweightType = flyweightType;
        this.classBuilder = classBuilder(thisName).addModifiers(PUBLIC, FINAL);
    }

    @Override
    public TypeSpec generate()
    {
        return classBuilder.addField(fieldBuffer())
                .addField(fieldExpectedException())
                .addType(testFlyweight())
                .addField(fieldFlyweightReadOnly())
                .addMethod(shouldFailToWrapWhenOffsetExceedsMaxLimit())
                .build();
    }

    private FieldSpec fieldFlyweightReadOnly()
    {
        return FieldSpec.builder(flyweightType.peerClass("FlyweightTest.TestFlyweight"), "flyweightRO", PRIVATE, FINAL)
                .initializer("new $T()", flyweightType.peerClass("FlyweightTest.TestFlyweight"))
                .build();
    }

    private FieldSpec fieldBuffer()
    {
        return FieldSpec.builder(MutableDirectBuffer.class, "buffer", PRIVATE, FINAL)
            .initializer("new $T($T.allocateDirect(150)); \n" +
                "{\n" +
                "    buffer.setMemory(0, buffer.capacity(), (byte) 0xF);\n" +
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

    private TypeSpec testFlyweight()
    {
        MethodSpec limitMethodBuilder = MethodSpec.methodBuilder("limit")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addStatement("return maxLimit()")
                .build();

        MethodSpec wrapMethodBuilder = MethodSpec.methodBuilder("wrap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(DirectBuffer.class, "buffer")
                .addParameter(int.class, "offset")
                .addParameter(int.class, "maxLimit")
                .returns(flyweightType.peerClass("Flyweight"))
                .addStatement("return super.wrap(buffer, offset, maxLimit)")
                .build();

        return TypeSpec.classBuilder("TestFlyweight")
                .addModifiers(PRIVATE, FINAL)
                .superclass(flyweightType.peerClass("Flyweight"))
                .addMethod(limitMethodBuilder)
                .addMethod(wrapMethodBuilder)
                .build();
    }

    private MethodSpec shouldFailToWrapWhenOffsetExceedsMaxLimit()
    {
        return MethodSpec.methodBuilder("shouldFailToWrapWhenOffsetExceedsMaxLimit")
                .addAnnotation(Test.class)
                .addModifiers(PUBLIC)
                .addException(Exception.class)
                .addStatement("expectedException.expect($T.class)", IndexOutOfBoundsException.class)
                .addStatement("expectedException.expectMessage(\"offset\")")
                .addStatement("flyweightRO.wrap(buffer,  4,  1)")
                .build();
    }

}
