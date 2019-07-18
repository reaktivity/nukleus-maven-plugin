/**
 * Copyright 2016-2019 The Reaktivity Project
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
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeSpec.enumBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

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
        this.builder.addField(INT, "value", Modifier.PRIVATE, Modifier.FINAL);
        this.builder.addMethod(MethodSpec.constructorBuilder()
                        .addParameter(INT, "value")
                        .addStatement("this.$L = $L", "value", "value")
                        .build())
                    .addMethod(MethodSpec.methodBuilder("value")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(int.class)
                        .addStatement("return $L", "value")
                        .build());
        this.valueOfMethod = new ValueOfMethodGenerator(enumTypeName);
    }

    public TypeSpecGenerator<ClassName> addValue(
        String name, int value)
    {
        nameConstant.addValue(name, value);
        valueOfMethod.addValue(name, value);

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
            String name, int value)
        {
            builder.addEnumConstant(name, TypeSpec.anonymousClassBuilder("$L", value).build());
            return this;
        }
    }

    private final class ValueOfMethodGenerator extends MethodSpecGenerator
    {
        private final List<String> values = new LinkedList<>();
        private final Map<String, Integer> valueAssociatedWithConstant = new HashMap<>();

        private ValueOfMethodGenerator(
            ClassName enumName)
        {
            super(methodBuilder("valueOf")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(int.class, "value")
                    .returns(enumName));
        }

        public ValueOfMethodGenerator addValue(
            String name, int value)
        {
            values.add(name);
            valueAssociatedWithConstant.put(name, value);
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            builder.beginControlFlow("switch ($L)", "value");

            for (int index=0; index < values.size(); index++)
            {
                String enumConstant = values.get(index);
                builder.beginControlFlow("case $L:", valueAssociatedWithConstant.get(enumConstant))
                       .addStatement("return $N", enumConstant)
                       .endControlFlow();
            }

            builder.endControlFlow()
                   .addStatement("throw new IllegalArgumentException(String.format($S, value))",
                           "Unrecognized value: %d");

            return builder.build();
        }

    }
}
