/**
 * Copyright 2016-2020 The Reaktivity Project
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
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstNamedNode.Kind.TYPEDEF;

import java.util.Collection;
import java.util.Set;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstAbstractMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstListMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstListNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNamedNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstStructNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstTypedefNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstVariantNode;
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
        AstListNode listNode)
    {
        super.visitList(listNode);
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitStruct(
        AstStructNode structNode)
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
        AstType arrayItemType = listMemberNode.arrayType();
        AstType arrayItemTypeName = listMemberNode.arrayTypeName();
        AstType mapKeyType = listMemberNode.mapKeyType();
        AstType mapValueType = listMemberNode.mapValueType();
        AstVariantNode namedNode;
        AstType arrayItemOfType = null;
        AstType arrayItemKindType = null;
        AstType mapKeyKindType = null;
        AstType mapKeyOfType = null;
        AstType mapValueKindType = null;
        AstType mapValueOfType = null;

        if (AstType.VARIANT.equals(arrayItemType))
        {
            namedNode = (AstVariantNode) resolver.resolve(arrayItemTypeName.name());
            arrayItemOfType = namedNode.of();
            arrayItemKindType = namedNode.kindType();
        }
        if (mapKeyType != null)
        {
            AstVariantNode mapKeyNode;
            AstVariantNode mapValueNode;
            AstNamedNode mapKeyNamedNode = resolver.resolve(mapKeyType.name());
            if (mapKeyNamedNode.getKind() == TYPEDEF)
            {
                AstNamedNode originalNode = resolver.resolve(((AstTypedefNode) mapKeyNamedNode).originalType().name());
                mapKeyNode = (AstVariantNode) originalNode;
            }
            else
            {
                mapKeyNode =  (AstVariantNode) mapKeyNamedNode;
            }
            mapKeyKindType = mapKeyNode.kindType();
            mapKeyOfType = mapKeyNode.of();
            AstNamedNode mapValueNamedNode = resolver.resolve(mapValueType.name());
            if (mapValueNamedNode.getKind() == TYPEDEF)
            {
                AstNamedNode originalNode = resolver.resolve(((AstTypedefNode) mapValueNamedNode).originalType().name());
                mapValueNode = (AstVariantNode) originalNode;
            }
            else
            {
                mapValueNode =  (AstVariantNode) mapValueNamedNode;
            }
            mapValueKindType = mapValueNode.kindType();
            mapValueOfType = mapValueNode.of();
        }
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
        generator.addMember(memberName, memberType, memberTypeName, memberUnsignedTypeName, size, sizeTypeName, usedAsSize,
            defaultValue, byteOrder, listMemberNode.isRequired(), arrayItemType, arrayItemTypeName, arrayItemOfType,
            arrayItemKindType, mapKeyType, mapKeyKindType, mapKeyOfType, mapValueType, mapValueKindType, mapValueOfType);
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
    public Collection<TypeSpecGenerator<?>> visitVariant(
        AstVariantNode variantNode)
    {
        return defaultResult();
    }

    @Override
    protected Collection<TypeSpecGenerator<?>> defaultResult()
    {
        return defaultResult;
    }
}
