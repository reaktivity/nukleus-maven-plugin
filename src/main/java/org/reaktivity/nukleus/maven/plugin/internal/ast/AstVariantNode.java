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
package org.reaktivity.nukleus.maven.plugin.internal.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

public final class AstVariantNode extends AstNode
{
    private final String name;
    private final AstType explicitType;
    private final AstType unsignedExplicitType;
    private final AstType kindType;
    private final List<AstVariantCaseNode> cases;

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitVariant(this);
    }

    public String name()
    {
        return name;
    }

    public AstType explicitType()
    {
        return explicitType;
    }

    public AstType unsignedExplicitType()
    {
        return unsignedExplicitType;
    }

    public AstType kindType()
    {
        return kindType;
    }

    public List<AstVariantCaseNode> cases()
    {
        return cases;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, explicitType, unsignedExplicitType, kindType, cases);
    }

    @Override
    public boolean equals(
        Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof AstVariantNode))
        {
            return false;
        }

        AstVariantNode that = (AstVariantNode)o;
        return Objects.equals(this.name, that.name) &&
            Objects.equals(this.cases, that.cases) &&
            Objects.equals(this.explicitType, that.explicitType) &&
            Objects.equals(this.unsignedExplicitType, that.unsignedExplicitType) &&
            Objects.equals(this.kindType, that.kindType);
    }

    private AstVariantNode(
        String name,
        AstType explicitType,
        AstType unsignedExplicitType,
        AstType kindType,
        List<AstVariantCaseNode> cases)
    {
        this.name = requireNonNull(name);
        this.explicitType = explicitType;
        this.unsignedExplicitType = unsignedExplicitType;
        this.kindType = kindType;
        this.cases = unmodifiableList(cases);
    }

    public static final class Builder extends AstNode.Builder<AstVariantNode>
    {
        private String name;
        private AstType explicitType;
        private AstType unsignedExplicitType;
        private boolean hasExplicitType;
        private List<AstVariantCaseNode> cases;
        private AstType kindType;

        public Builder()
        {
            this.cases = new LinkedList<>();
        }

        public Builder name(
            String name)
        {
            this.name = name;
            return this;
        }

        public Builder explicitType(
            AstType explicitType)
        {
            this.explicitType = explicitType;
            return this;
        }

        public Builder unsignedExplicitType(
            AstType unsignedExplicitType)
        {
            this.unsignedExplicitType = unsignedExplicitType;
            return this;
        }

        public Builder kindType(
            AstType kindType)
        {
            this.kindType = kindType;
            return this;
        }

        public AstType kindType()
        {
            return kindType;
        }

        public Builder hasExplicitType(
            boolean hasExplicitType)
        {
            this.hasExplicitType = hasExplicitType;
            return this;
        }

        public boolean hasExplicitType()
        {
            return hasExplicitType;
        }

        public Builder caseN(
            AstVariantCaseNode caseN)
        {
            this.cases.add(caseN);
            return this;
        }

        @Override
        public AstVariantNode build()
        {
            return new AstVariantNode(name, explicitType, unsignedExplicitType, kindType, cases);
        }
    }
}
