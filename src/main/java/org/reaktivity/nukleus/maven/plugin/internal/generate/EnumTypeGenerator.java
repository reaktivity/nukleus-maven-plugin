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

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
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
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public final class EnumTypeGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder builder;
    private final NameConstantGenerator nameConstant;
    private final ValueOfMethodGenerator valueOfMethod;
    private final ValueMethodGenerator valueMethod;
    private final ConstructorGenerator constructor;
    private final TypeName valueTypeName;

    public EnumTypeGenerator(
        ClassName enumTypeName,
        TypeName valueTypeName)
    {
        super(enumTypeName);

        this.builder = enumBuilder(enumTypeName).addModifiers(PUBLIC);
        this.nameConstant = new NameConstantGenerator(enumTypeName, builder);
        this.valueOfMethod = new ValueOfMethodGenerator(enumTypeName);
        this.valueMethod = new ValueMethodGenerator();
        this.constructor = new ConstructorGenerator();
        this.valueTypeName = valueTypeName;
    }

    public TypeSpecGenerator<ClassName> addValue(
        String name,
        Object value)
    {
        nameConstant.addValue(name, value);
        valueOfMethod.addValue(name, value);

        return this;
    }

    @Override
    public TypeSpec generate()
    {
        nameConstant.build();
        if (valueTypeName != null)
        {
            builder.addField(typeName(), "value", Modifier.PRIVATE, Modifier.FINAL)
                   .addMethod(constructor.generate())
                   .addMethod(valueMethod.generate());
            if (!valueTypeName.isPrimitive())
            {
                return builder.build();
            }
        }

        return builder.addMethod(valueOfMethod.generate())
                      .build();
    }

    private TypeName typeName()
    {
        return valueTypeName.isPrimitive() ? INT : TypeName.get(String.class);
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
            String name, Object value)
        {
            if (value == null)
            {
                builder.addEnumConstant(name);
            }
            else
            {
                builder.addEnumConstant(name, TypeSpec.anonymousClassBuilder("$L", value).build());
            }
            return this;
        }
    }

    private final class ConstructorGenerator extends MethodSpecGenerator
    {
        private ConstructorGenerator()
        {
            super(constructorBuilder().addStatement("this.$L = $L", "value", "value"));
        }

        @Override
        public MethodSpec generate()
        {
            return builder.addParameter(typeName(), "value")
                          .build();
        }
    }

    private final class ValueMethodGenerator extends MethodSpecGenerator
    {
        private ValueMethodGenerator()
        {
            super(methodBuilder("value")
                .addModifiers(PUBLIC)
                .addStatement("return $L", "value"));
        }

        @Override
        public MethodSpec generate()
        {
            return builder.returns(valueTypeName.isPrimitive() ? int.class : String.class)
                          .build();
        }
    }

    private final class ValueOfMethodGenerator extends MethodSpecGenerator
    {
        private final List<String> constantNames = new LinkedList<>();
        private final Map<String, Object> valueByConstantName = new HashMap<>();

        private ValueOfMethodGenerator(
            ClassName enumName)
        {
            super(methodBuilder("valueOf")
                    .addModifiers(PUBLIC, STATIC)
                    .returns(enumName));
        }

        public ValueOfMethodGenerator addValue(
            String name,
            Object value)
        {
            constantNames.add(name);
            if (value != null)
            {
                valueByConstantName.put(name, value);
            }
            return this;
        }

        @Override
        public MethodSpec generate()
        {
            final String discriminant = valueTypeName != null ? "value" : "ordinal";
            builder.addParameter(int.class, discriminant);
            builder.beginControlFlow("switch ($L)", discriminant);

            for (int index = 0; index < constantNames.size(); index++)
            {
                String enumConstant = constantNames.get(index);
                int kind = valueByConstantName.get(enumConstant) == null ? index :
                    (int) valueByConstantName.get(enumConstant);
                builder.beginControlFlow("case $L:", kind)
                       .addStatement("return $N", enumConstant)
                       .endControlFlow();
            }

            builder.endControlFlow().addStatement("return null");

            return builder.build();
        }

    }
}
