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

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstValueNode;
import org.reaktivity.nukleus.maven.plugin.internal.generate.EnumFlyweightGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.EnumTypeGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeSpecGenerator;

public final class EnumVisitor extends AstNode.Visitor<Collection<TypeSpecGenerator<?>>>
{
    private final EnumTypeGenerator typeGenerator;
    private final List<TypeSpecGenerator<?>> defaultResult;

    public EnumVisitor(
        EnumTypeGenerator typeGenerator,
        EnumFlyweightGenerator flyweightGenerator)
    {
        this.typeGenerator = typeGenerator;
        this.defaultResult = asList(flyweightGenerator, typeGenerator);
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitEnum(
        AstEnumNode enumNode)
    {
        super.visitEnum(enumNode);
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitUnion(
        AstUnionNode unionNode)
    {
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitValue(
        AstValueNode valueNode)
    {
        String valueName = valueNode.name();

        typeGenerator.addValue(valueName);

        return defaultResult();
    }

    @Override
    protected Collection<TypeSpecGenerator<?>> defaultResult()
    {
        return defaultResult;
    }
}
