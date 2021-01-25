/**
 * Copyright 2016-2021 The Reaktivity Project
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
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstNamedNode.Kind.VARIANT;

import java.util.Collection;
import java.util.Set;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNamedNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstStructNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstVariantCaseNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstVariantNode;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeResolver;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeSpecGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.VariantFlyweightGenerator;

import com.squareup.javapoet.TypeName;

public final class VariantVisitor extends AstNode.Visitor<Collection<TypeSpecGenerator<?>>>
{
    private final VariantFlyweightGenerator generator;
    private final TypeResolver resolver;
    private final Set<TypeSpecGenerator<?>> defaultResult;

    public VariantVisitor(
        VariantFlyweightGenerator generator,
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
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitVariant(
        AstVariantNode variantNode)
    {
        return super.visitVariant(variantNode);
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitVariantCase(
        AstVariantCaseNode variantCaseNode)
    {
        Object value = variantCaseNode.value();
        AstType memberType = variantCaseNode.type();
        AstType mapKeyType = null;
        AstType mapValueType = null;
        AstNamedNode memberTypeNode = memberType == null ? null : resolver.resolve(memberType.name());
        if (memberTypeNode != null)
        {
            if (memberTypeNode.getKind() == VARIANT)
            {
                AstVariantNode varNode = (AstVariantNode) memberTypeNode;
                AstType ofType = varNode.of();
                if (AstType.MAP.equals(ofType))
                {
                    mapKeyType = variantCaseNode.typeParams().get(0);
                    mapValueType = variantCaseNode.typeParams().get(1);
                }
            }
        }

        int missingFieldValue = variantCaseNode.missingFieldValue();
        TypeName typeName = resolver.resolveType(memberType);
        TypeName unsignedTypeName = resolver.resolveUnsignedType(memberType);
        String memberTypeName = memberType != null ? memberType.name() : null;
        generator.addMember(value, memberTypeName, memberType, typeName, unsignedTypeName, missingFieldValue, mapKeyType,
            mapValueType);
        return defaultResult();
    }

    @Override
    protected Collection<TypeSpecGenerator<?>> defaultResult()
    {
        return defaultResult;
    }
}
