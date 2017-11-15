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
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstCaseNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstStructNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionNode;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeResolver;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeSpecGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.UnionFlyweightGenerator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public final class UnionVisitor extends AstNode.Visitor<Collection<TypeSpecGenerator<?>>>
{
    private final UnionFlyweightGenerator generator;
    private final TypeResolver resolver;
    private final Set<TypeSpecGenerator<?>> defaultResult;

    public UnionVisitor(
        UnionFlyweightGenerator generator,
        TypeResolver resolver)
    {
        this.generator = generator;
        this.resolver = resolver;
        this.defaultResult = singleton(generator);
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitStruct(
        AstStructNode structNode)
    {
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
        return super.visitUnion(unionNode);
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitCase(
        AstCaseNode caseNode)
    {
        int value = caseNode.value();
        AstMemberNode memberNode = caseNode.member();
        String memberName = memberNode.name();
        AstType memberType = memberNode.type();
        int size = memberNode.size();
        String sizeName = memberNode.sizeName();
        AstType memberUnsignedType = memberNode.unsignedType();
        AstByteOrder byteOrder = memberNode.byteOrder();

        if (memberType == AstType.LIST)
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
            generator.addMember(value, memberName, memberTypeName, memberUnsignedTypeName, size, sizeName, byteOrder);
        }
        else
        {
            TypeName memberTypeName = resolver.resolveType(memberType);
            TypeName memberUnsignedTypeName = resolver.resolveType(memberUnsignedType);
            generator.addMember(value, memberName, memberTypeName, memberUnsignedTypeName, size, sizeName, byteOrder);
        }

        return defaultResult();
    }

    @Override
    protected Collection<TypeSpecGenerator<?>> defaultResult()
    {
        return defaultResult;
    }
}
