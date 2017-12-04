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
package org.reaktivity.nukleus.maven.plugin.internal.ast.visit;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Set;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstStructNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionNode;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeResolver;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeSpecGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.test.StructFlyweightTestGenerator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public class StructTestVisitor extends AstNode.Visitor<Collection<TypeSpecGenerator<?>>>
{
    private final StructFlyweightTestGenerator generator;
    private final TypeResolver resolver;
    private final Set<TypeSpecGenerator<?>> defaultResult;

    public StructTestVisitor(
        StructFlyweightTestGenerator generator,
        TypeResolver resolver)
    {
        this.generator = generator;
        this.resolver = resolver;
        defaultResult = singleton(generator);
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitStruct(
        AstStructNode structNode)
    {
        String supertype = structNode.supertype();
        if (supertype != null)
        {
            AstStructNode superNode = resolver.resolve(supertype);
            visitStruct(superNode);
        }
        super.visitStruct(structNode);
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitEnum(
        AstEnumNode enumNode)
    {
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitUnion(
        AstUnionNode unionNode)
    {
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitMember(
        AstMemberNode memberNode)
    {
        // TODO prepare for recursion (nested structs/enums/unions)
        String memberName = memberNode.name();
        AstType memberType = memberNode.type();
        AstType memberUnsignedType = memberNode.unsignedType();
        int size = memberNode.size();
        String sizeName = memberNode.sizeName();
        TypeName sizeTypeName = resolver.resolveType(memberNode.sizeType());
        boolean usedAsSize = memberNode.usedAsSize();
        Object defaultValue = memberNode.defaultValue();
        AstByteOrder byteOrder = memberNode.byteOrder();
        if (memberType == AstType.LIST || memberType == AstType.ARRAY)
        {
            ClassName rawType = resolver.resolveClass(memberType);
            TypeName[] typeArguments = memberNode.types()
                .stream()
                .skip(1)
                .map(resolver::resolveType)
                .collect(toList())
                .toArray(new TypeName[0]);
            ParameterizedTypeName memberTypeName = ParameterizedTypeName.get(rawType, typeArguments);
            TypeName memberUnsignedTypeName = resolver.resolveType(memberUnsignedType);
            generator.addMember(
                memberType,
                memberName,
                memberTypeName,
                memberUnsignedTypeName,
                size,
                sizeName,
                sizeTypeName,
                false,
                defaultValue,
                byteOrder);
        }
        else
        {
            TypeName memberTypeName = resolver.resolveType(memberType);
            if (memberTypeName == null)
            {
                throw new IllegalArgumentException(String.format(
                    " Unable to resolve type %s for field %s", memberType, memberName));
            }
            TypeName memberUnsignedTypeName = resolver.resolveType(memberUnsignedType);
            generator.addMember(
                memberType,
                memberName,
                memberTypeName,
                memberUnsignedTypeName,
                size,
                sizeName,
                sizeTypeName,
                usedAsSize,
                defaultValue,
                byteOrder);
        }
        return defaultResult();
    }

    @Override
    protected Collection<TypeSpecGenerator<?>> defaultResult()
    {
        return defaultResult;
    }
}
