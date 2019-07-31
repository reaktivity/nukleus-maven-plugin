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
import static com.squareup.javapoet.TypeSpec.enumBuilder;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.reaktivity.nukleus.maven.plugin.internal.generate.TypeNames.LONG_2_OBJECT_HASH_MAP_TYPE;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public final class EnumTypeGenerator extends ClassSpecGenerator
{
    private final TypeSpec.Builder builder;
    private final NameConstantGenerator nameConstant;
    private final ValueOfMethodGenerator valueOfMethod;
    private final TypeName valueTypeName;
    private ValueMethodGenerator valueMethod;
    private ConstructorGenerator constructor;
    private LongHashMapGenerator longHashMap;

    public EnumTypeGenerator(
        ClassName enumTypeName,
        TypeName valueTypeName)
    {
        super(enumTypeName);
        this.builder = enumBuilder(enumTypeName).addModifiers(PUBLIC);
        this.nameConstant = new NameConstantGenerator(enumTypeName, builder);
        this.valueOfMethod = new ValueOfMethodGenerator(enumTypeName);
        this.valueTypeName = valueTypeName;
        if (isParameterizedType())
        {
            this.valueMethod = new ValueMethodGenerator();
            this.constructor = new ConstructorGenerator();
        }
        if (isValueTypeLong())
        {
            this.longHashMap = new LongHashMapGenerator(enumTypeName, builder);
        }
    }

    public TypeSpecGenerator<ClassName> addValue(
        String name,
        Object value)
    {
        nameConstant.addValue(name, value);
        valueOfMethod.addValue(name, value);

        if (isValueTypeLong())
        {
            longHashMap.addValue(name, value);
        }
        return this;
    }

    @Override
    public TypeSpec generate()
    {
        nameConstant.build();
        if (isParameterizedType())
        {
            if (valueTypeName.equals(TypeName.LONG))
            {
                longHashMap.generate();
            }
            if (valueTypeName.isPrimitive())
            {
                builder.addField(valueTypeName, "value", Modifier.PRIVATE, Modifier.FINAL);
            }
            else
            {
                builder.addField(String.class, "value", Modifier.PRIVATE, Modifier.FINAL);
            }
            builder.addMethod(constructor.generate())
                   .addMethod(valueMethod.generate());
        }

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

    private static final class LongHashMapGenerator extends ClassSpecMixinGenerator
    {
        private final CodeBlock.Builder longHashMapBuilder;
        private LongHashMapGenerator(
            ClassName thisType,
            TypeSpec.Builder builder)
        {
            super(thisType, builder);
            builder.addField(ParameterizedTypeName.get(LONG_2_OBJECT_HASH_MAP_TYPE, thisType), "VALUE_BY_LONG",
                Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
            longHashMapBuilder = CodeBlock.builder().addStatement("$T<$T> valueByLong = new $T<>()", LONG_2_OBJECT_HASH_MAP_TYPE,
                thisType, LONG_2_OBJECT_HASH_MAP_TYPE);
        }

        public LongHashMapGenerator addValue(
            String name,
            Object value)
        {
            longHashMapBuilder.addStatement("valueByLong.put($L, $L)", value, name);
            return this;
        }

        public TypeSpec generate()
        {
            return builder.addStaticBlock(longHashMapBuilder.addStatement("VALUE_BY_LONG = valueByLong")
                .build()).build();
        }
    }

    private final class ConstructorGenerator extends MethodSpecGenerator
    {
        private ConstructorGenerator()
        {
            super(valueTypeName.equals(TypeName.BYTE) | valueTypeName.equals(TypeName.SHORT) ?
                constructorBuilder().addStatement("this.$L = ($L) $L", "value", valueTypeName, "value") :
                constructorBuilder().addStatement("this.$L = $L", "value", "value"));
        }

        @Override
        public MethodSpec generate()
        {
            if (valueTypeName.isPrimitive())
            {
                builder.addParameter(valueTypeName.equals(TypeName.BYTE) | valueTypeName.equals(TypeName.SHORT) ? TypeName.INT
                    : valueTypeName, "value");
            }
            else
            {
                builder.addParameter(String.class, "value");
            }
            return builder.build();
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
            if (valueTypeName.isPrimitive())
            {
                builder.returns(valueTypeName);
            }
            else
            {
                builder.returns(String.class);
            }
            return builder.build();
        }
    }

    private final class ValueOfMethodGenerator extends MethodSpecGenerator
    {
        private final List<String> constantNames = new LinkedList<>();
        private final Map<String, Object> valueByConstantName = new HashMap<>();
        private final ClassName enumName;

        private ValueOfMethodGenerator(
            ClassName enumName)
        {
            super(methodBuilder("valueOf")
                    .addModifiers(PUBLIC, STATIC)
                    .returns(enumName));
            this.enumName = enumName;
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
            final String discriminant = isParameterizedType() ? "value" : "ordinal";

            builder.addParameter(isParameterizedType() ? valueTypeName : TypeName.INT, discriminant);

            if (isValueTypeLong())
            {
                builder.addStatement("return VALUE_BY_LONG.get(value)");
            }
            else
            {
                if (isValueTypeString())
                {
                    builder.addStatement("String kind = $L.asString()", discriminant);
                }
                builder.beginControlFlow("switch ($L)", isValueTypeString() ? "kind" : discriminant);

                for (int index = 0; index < constantNames.size(); index++)
                {
                    String enumConstant = constantNames.get(index);

                        Object kind =
                            valueByConstantName.get(enumConstant) == null ? index : valueByConstantName.get(enumConstant);
                        builder.beginControlFlow("case $L:", kind)
                               .addStatement("return $N", enumConstant)
                               .endControlFlow();
                }

                builder.endControlFlow().addStatement("return null");
            }
            return builder.build();
        }
    }

    private boolean isParameterizedType()
    {
        return valueTypeName != null;
    }

    private boolean isValueTypeLong()
    {
        return valueTypeName != null && valueTypeName.equals(TypeName.LONG);
    }

    private boolean isValueTypeString()
    {
        return valueTypeName != null && !valueTypeName.isPrimitive();
    }
}
