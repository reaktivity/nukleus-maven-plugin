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

import java.util.Collection;
import java.util.List;

import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNodeLocator;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstStructNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionNode;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeResolver;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeSpecGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.test.StructFlyweightTestGenerator;

import com.squareup.javapoet.ClassName;

public class ScopeTestVisitor extends ScopeVisitor
{
    private final AstNodeLocator astNodeLocator;

    public ScopeTestVisitor(
        String scopeName,
        String packageName,
        TypeResolver resolver,
        List<String> targetScopes,
        AstNodeLocator astNodeLocator)
    {
        super(scopeName, packageName, resolver, targetScopes);
        this.astNodeLocator = astNodeLocator;
    }

    @Override
    protected ScopeVisitor nestedScopeVisitor(
        String subscopeName,
        String subpackageName)
    {
        return new ScopeTestVisitor(subscopeName, subpackageName, resolver, targetScopes, astNodeLocator);
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitStruct(AstStructNode structNode)
    {
        if (!targetScopes.stream().anyMatch(this::shouldVisit))
        {
            return defaultResult();
        }
        String baseName = structNode.name();
        AstType structTypeTest = AstType.dynamicType(String.format("%s::%s", scopeName, baseName + "Test"));
        ClassName structNameTest = resolver.resolveClass(structTypeTest);
        StructFlyweightTestGenerator generator = new StructFlyweightTestGenerator(
            structNameTest,
            baseName,
            astNodeLocator);
        generator.typeId(findTypeId(structNode));
        return new StructTestVisitor(
            generator,
            resolver).visitStruct(structNode);
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitEnum(AstEnumNode enumNode)
    {
        return defaultResult();
    }

    @Override
    public Collection<TypeSpecGenerator<?>> visitUnion(AstUnionNode unionNode)
    {
        return defaultResult();
    }
}
