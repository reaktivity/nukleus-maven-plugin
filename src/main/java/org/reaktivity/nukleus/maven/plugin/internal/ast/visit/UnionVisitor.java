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
package org.reaktivity.nukleus.maven.plugin.internal.ast.visit;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionCaseNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstStructNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstVariantNode;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeResolver;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeSpecGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.UnionFlyweightGenerator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public final class UnionVisitor extends AstNode.Visitor<Collection<TypeSpecGenerator<?>>>
{
    private static final Map<AstType, AstType> UINT_MAPPINGS;
    private final UnionFlyweightGenerator generator;
    private final TypeResolver resolver;
    private final Set<TypeSpecGenerator<?>> defaultResult;

    static
    {
        Map<AstType, AstType> uintMappings = new HashMap<>();
        uintMappings.put(AstType.UINT8, AstType.INT32);
        uintMappings.put(AstType.UINT16, AstType.INT32);
        uintMappings.put(AstType.UINT32, AstType.INT64);
        uintMappings.put(AstType.UINT64, AstType.INT64);
        UINT_MAPPINGS = unmodifiableMap(uintMappings);
    }

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
    public Collection<TypeSpecGenerator<?>> visitVariant(
        AstVariantNode variantNode)
    {
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitCase(
        AstUnionCaseNode caseNode)
    {
        int value = caseNode.value();
        AstMemberNode memberNode = caseNode.member();
        String memberName = memberNode.name();
        AstType memberType = memberNode.type();
        int size = memberNode.size();
        String sizeName = memberNode.sizeName();
        AstByteOrder byteOrder = memberNode.byteOrder();

        if (memberType == AstType.LIST)
        {
            ClassName rawType = resolver.resolveClass(memberType);
            TypeName[] memberUnsignedTypeName = new TypeName[1];
            TypeName[] typeArguments = memberNode.types()
                    .stream()
                    .skip(1)
                    .map(type ->
                    {
                        memberUnsignedTypeName[0] = resolver.resolveType(UINT_MAPPINGS.get(type));
                        return resolver.resolveType(type);
                    })
                    .collect(toList())
                    .toArray(new TypeName[0]);
            ParameterizedTypeName memberTypeName = ParameterizedTypeName.get(rawType, typeArguments);
            generator.addMember(value, memberName, memberTypeName, memberUnsignedTypeName[0], size, sizeName, byteOrder);
        }
        else
        {
            TypeName memberTypeName = resolver.resolveType(memberType);
            TypeName memberUnsignedTypeName = resolver.resolveType(UINT_MAPPINGS.get(memberType));
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
