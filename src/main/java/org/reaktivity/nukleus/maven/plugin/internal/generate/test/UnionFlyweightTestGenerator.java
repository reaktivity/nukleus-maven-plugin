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
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ClassSpecGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ClassSpecMixinGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.MethodSpecGenerator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.*;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.DIRECT_BUFFER_TYPE;

public final class UnionFlyweightTestGenerator extends ClassSpecGenerator
{
    private final String baseName;
    private final TypeSpec.Builder builder;
    private final ClassName unionName;
    private final ClassName unionRO;
    private final ClassName unionRW;

    private final ShouldSetFieldTestMethod shouldSetFieldTestMethod;
    private final ShouldBuildWithNothingSetTestMethod shouldBuildWithNothingSetTestMethod;

    public UnionFlyweightTestGenerator(
        ClassName unionName,
        ClassName flyweightName,
        String baseName)
    {
        super(unionName);

        this.baseName = baseName;
        this.unionName = unionName;
        this.builder = classBuilder(unionName.simpleName() + "Test").addModifiers(PUBLIC, FINAL);
        unionRO = unionName.peerClass(unionName.simpleName());
        unionRW = unionName.peerClass(unionName.simpleName() + ".Builder");

        shouldSetFieldTestMethod = new ShouldSetFieldTestMethod(unionName, builder);
        shouldBuildWithNothingSetTestMethod = new ShouldBuildWithNothingSetTestMethod();
    }

    public UnionFlyweightTestGenerator addMember(
        int value,
        String name,
        TypeName type,
        TypeName unsignedType,
        int size,
        String sizeName,
        AstByteOrder byteOrder)
    {
        shouldSetFieldTestMethod.addMember(name, type, size, sizeName);
        return this;
    }

    @Override
    public TypeSpec generate()
    {
        shouldSetFieldTestMethod.build();

        return builder.addField(fieldBuffer())
                .addField(fieldRW())
                .addField(fieldRO())
                .addField(fieldExpectedException())
                .addMethod(shouldBuildWithNothingSetTestMethod.generate())
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
        return FieldSpec.builder(unionRW, "fieldRW", PRIVATE, FINAL)
                .initializer("new $T()", unionRW)
                .build();
    }

    private FieldSpec fieldRO()
    {
        return FieldSpec.builder(unionRO, "fieldRO", PRIVATE, FINAL)
                .initializer("new $T()", unionRO)
                .build();
    }

    private FieldSpec fieldExpectedException()
    {
        return FieldSpec.builder(ExpectedException.class, "expectedException", PUBLIC)
                .addAnnotation(Rule.class)
                .initializer("$T.none()", ExpectedException.class)
                .build();
    }

    private static final class ShouldSetFieldTestMethod extends ClassSpecMixinGenerator
    {
        private ShouldSetFieldTestMethod(
                ClassName thisType,
                TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public ShouldSetFieldTestMethod addMember(
                String name,
                TypeName type,
                int size,
                String sizeName)
        {
            if (DIRECT_BUFFER_TYPE.equals(type))
            {

            }
            else if (!type.isPrimitive())
            {
                if (size >= 0)
                {
                    String value = "";
                    for(int i = 0; i<size; i++)
                    {
                        value += "1";
                    }
                    builder.addMethod(methodBuilder("shouldSet" + name)
                            .addModifiers(PUBLIC)
                            .addAnnotation(Test.class)
                            .addStatement("$T limit = fieldRW.wrap(buffer, 0, buffer.capacity())\n" +
                                    "               .$L(b -> b.put(\"$L\".getBytes($T.UTF_8)))\n" +
                                    "               .build()\n" +
                                    "               .limit()", int.class, name, value, StandardCharsets.class)
                            .addStatement("fieldRO.wrap(buffer,  0,  limit)")
                            .addStatement("$T.assertEquals(\"$L\"," +
                                    " fieldRO.$L().get((b, o, m) -> b.getStringWithoutLengthUtf8(o, m - o)))",
                                    Assert.class, value, name)
                            .addStatement("$T.assertTrue(fieldRO.toString().contains(\"$L\"))", Assert.class, name)
                            .build());
                }
                else if (sizeName != null)
                {

                }
                else
                {
                    builder.addMethod(methodBuilder("shouldSet" + name)
                            .addModifiers(PUBLIC)
                            .addAnnotation(Test.class)
                            .addStatement("$T limit = fieldRW.wrap(buffer, 0, buffer.capacity())\n" +
                                    "               .$L(\"valueOf\")\n" +
                                    "               .build()\n" +
                                    "               .limit()", int.class, name)
                            .addStatement("fieldRO.wrap(buffer,  0,  limit)")
                            .addStatement("$T.assertEquals(\"valueOf\"," +
                                            " fieldRO.$L().asString())",
                                    Assert.class, name)
                            .addStatement("$T.assertTrue(fieldRO.toString().contains(\"$L\"))", Assert.class, name)
                            .build());

                    builder.addMethod(methodBuilder("shouldSet" + name + "WithNull")
                            .addModifiers(PUBLIC)
                            .addAnnotation(Test.class)
                            .addStatement("$T limit = fieldRW.wrap(buffer, 0, buffer.capacity())\n" +
                                    "               .$L(null)\n" +
                                    "               .build()\n" +
                                    "               .limit()", int.class, name)
                            .addStatement("fieldRO.wrap(buffer,  0,  limit)")
                            .addStatement("$T.assertEquals(null, fieldRO.$L().asString())", Assert.class, name)
                            .build());
                }
            }
            return this;
        }
    }

    private final class ShouldBuildWithNothingSetTestMethod extends MethodSpecGenerator
    {
        private ShouldBuildWithNothingSetTestMethod()
        {
            super(methodBuilder("shouldBuildWithNothingSet")
                    .addAnnotation(Test.class)
                    .addModifiers(PUBLIC)
                    .addStatement("$T limit = fieldRW.wrap(buffer, 10, buffer.capacity())\n" +
                            "            .build()\n" +
                            "            .limit()", int.class)
                    .addStatement("fieldRO.wrap(buffer,  0,  limit)")
                    .addStatement("$T.assertTrue(fieldRO.toString().contains(\"unknown\"))", Assert.class));
        }

        @Override
        public MethodSpec generate()
        {
            return builder.build();
        }
    }

    private static String constant(
        String fieldName)
    {
        return fieldName.replaceAll("([^_A-Z])([A-Z])", "$1_$2").toUpperCase();
    }
}
