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

import java.util.Collection;
import java.util.Set;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstAbstractMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstListMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNamedNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ListFlyweightGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeResolver;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeSpecGenerator;

import com.squareup.javapoet.TypeName;

public final class ListVisitor extends AstNode.Visitor<Collection<TypeSpecGenerator<?>>>
{
    private final ListFlyweightGenerator generator;
    private final TypeResolver resolver;
    private final Set<TypeSpecGenerator<?>> defaultResult;

    public ListVisitor(
        ListFlyweightGenerator generator,
        TypeResolver resolver)
    {
        this.generator = generator;
        this.resolver = resolver;
        this.defaultResult = singleton(generator);
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitList(
        AstNamedNode listNode)
    {
        super.visitList(listNode);
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitStruct(
        AstNamedNode structNode)
    {
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitMember(
        AstAbstractMemberNode memberNode)
    {
        AstListMemberNode listMemberNode = (AstListMemberNode) memberNode;
        String memberName = listMemberNode.name();
        AstType memberType = listMemberNode.type();
        int size = listMemberNode.size();
        TypeName sizeTypeName = listMemberNode.sizeType() == null ? null : listMemberNode.sizeType().isUnsignedInt() ?
            resolver.resolveUnsignedType(listMemberNode.sizeType()) : resolver.resolveType(listMemberNode.sizeType());
        boolean usedAsSize = listMemberNode.usedAsSize();
        Object defaultValue = listMemberNode.defaultValue();
        AstByteOrder byteOrder = listMemberNode.byteOrder();

        TypeName memberTypeName = resolver.resolveType(memberType);
        if (memberTypeName == null)
        {
            throw new IllegalArgumentException(String.format(
                " Unable to resolve type %s for field %s", memberType, memberName));
        }
        TypeName memberUnsignedTypeName = memberType.isUnsignedInt() ? resolver.resolveUnsignedType(memberType) : null;
        generator.addMember(memberName, memberType, memberTypeName, memberUnsignedTypeName, size, sizeTypeName,
            usedAsSize, defaultValue, byteOrder, listMemberNode.isRequired());
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitEnum(
        AstNamedNode enumNode)
    {
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitUnion(
        AstNamedNode unionNode)
    {
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitVariant(
        AstNamedNode variantNode)
    {
        return defaultResult();
    }

    @Override
    protected Collection<TypeSpecGenerator<?>> defaultResult()
    {
        return defaultResult;
    }
}
