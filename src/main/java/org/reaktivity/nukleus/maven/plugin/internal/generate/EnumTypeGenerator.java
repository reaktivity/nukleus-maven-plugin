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
package org.reaktivity.nukleus.maven.plugin.internal.generate;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.enumBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import java.util.LinkedList;
import java.util.List;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public final class EnumTypeGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder builder;
    private final NameConstantGenerator nameConstant;
    private final ValueOfMethodGenerator valueOfMethod;

    public EnumTypeGenerator(
        ClassName enumTypeName)
    {
        super(enumTypeName);

        this.builder = enumBuilder(enumTypeName).addModifiers(PUBLIC);
        this.nameConstant = new NameConstantGenerator(enumTypeName, builder);
        this.valueOfMethod = new ValueOfMethodGenerator(enumTypeName);
    }

    public TypeSpecGenerator<ClassName> addValue(
        String name)
    {
        nameConstant.addValue(name);
        valueOfMethod.addValue(name);

        return this;
    }

    @Override
    public TypeSpec generate()
    {
        nameConstant.build();

        return builder.addMethod(valueOfMethod.generate())
                      .build();
    }

    private static final class NameConstantGenerator extends ClassSpecMixinGenerator
    {
        private NameConstantGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
        }

        public NameConstantGenerator addValue(
            String name)
        {
            builder.addEnumConstant(name);
            return this;
        }
    }

    private final class ValueOfMethodGenerator extends MethodSpecGenerator
    {
        private final List<String> values = new LinkedList<>();

        private ValueOfMethodGenerator(
            ClassName enumName)
        {
            super(methodBuilder("valueOf")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(int.class, "ordinal")
                    .returns(enumName));
        }

        public ValueOfMethodGenerator addValue(
            String name)
        {
            values.add(name);
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            builder.beginControlFlow("switch ($L)", "ordinal");

            for (int index=0; index < values.size(); index++)
            {
                builder.beginControlFlow("case $L:", index)
                       .addStatement("return $N", values.get(index))
                       .endControlFlow();
            }

            builder.endControlFlow()
                   .addStatement("throw new IllegalArgumentException(String.format($S, ordinal))",
                           "Unrecognized value: %d");

            return builder.build();
        }

    }
}
