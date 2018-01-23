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
package org.reaktivity.nukleus.maven.plugin.internal.ast;

import static java.util.stream.Collectors.reducing;
import java.util.stream.Stream;

public abstract class AstNode
{
    public abstract <R> R accept(
        Visitor<R> visitor);

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object o);

    public abstract static class Visitor<R>
    {
        public R visitSpecification(
            AstSpecificationNode specificationNode)
        {
            return visitScope(specificationNode.scope());
        }

        public R visitScope(
            AstScopeNode scopeNode)
        {
            return Stream.concat(
                      Stream.concat(
                          Stream.concat(
                              scopeNode.scopes()
                                       .stream()
                                       .map(this::visitNestedScope),
                              scopeNode.structs()
                                       .stream()
                                       .map(this::visitStruct)),
                        scopeNode.enums()
                                 .stream()
                                 .map(this::visitEnum)),
                      scopeNode.unions()
                               .stream()
                               .map(this::visitUnion))
                   .collect(reducing(defaultResult(), this::aggregateResult));
        }

        public R visitNestedScope(
            AstScopeNode scopeNode)
        {
            return visitScope(scopeNode);
        }

        public R visitEnum(
            AstEnumNode enumNode)
        {
            return enumNode.values()
                           .stream()
                           .map(this::visitValue)
                           .collect(reducing(defaultResult(), this::aggregateResult));
        }

        public R visitValue(
            AstValueNode valueNode)
        {
            return defaultResult();
        }

        public R visitStruct(
            AstStructNode structNode)
        {
            // TODO this effectively looses the information that a member can have other members
            return structNode.members()
                             .stream()
                             .map(this::visitMember)
                             .collect(reducing(defaultResult(), this::aggregateResult));
        }

        public R visitMember(
            AstMemberNode memberNode)
        {
            return defaultResult();
        }

        public R visitUnion(
            AstUnionNode unionNode)
        {
            return unionNode.cases()
                    .stream()
                    .map(this::visitCase)
                    .collect(reducing(defaultResult(), this::aggregateResult));
        }

        public R visitCase(
            AstCaseNode caseNode)
        {
            return defaultResult();
        }

        protected R defaultResult()
        {
            return null;
        }

        protected R aggregateResult(
            R aggregate,
            R nextResult)
        {
            return nextResult;
        }
    }

    public abstract static class Builder<T extends AstNode>
    {
        public abstract T build();
    }
}
