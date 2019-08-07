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
    private final AstType wideType;
    private final List<AstCaseNode> cases;

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

    public AstType wideType()
    {
        return wideType;
    }

    public List<AstCaseNode> cases()
    {
        return cases;
    }

    @Override
    public int hashCode()
    {
        return (name.hashCode() << 7) ^ cases.hashCode(); // TODO: why << 7 ?
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
            Objects.equals(this.cases, that.cases);
    }

    private AstVariantNode(
        String name,
        AstType wideType,
        List<AstCaseNode> cases)
    {
        this.name = requireNonNull(name);
        this.wideType = wideType;
        this.cases = unmodifiableList(cases);
    }

    public static final class Builder extends AstNode.Builder<AstVariantNode>
    {
        private String name;
        private AstType wideType;
        private boolean hasWideType;
        private List<AstCaseNode> cases;

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

        public Builder wideType(
            AstType wideType)
        {
            this.wideType = wideType;
            return this;
        }

        public AstType wideType()
        {
            return wideType;
        }

        public Builder hasWideType(
            boolean hasWideType)
        {
            this.hasWideType = hasWideType;
            return this;
        }

        public boolean hasWideType()
        {
            return hasWideType;
        }

        public Builder caseN(
            AstCaseNode caseN)
        {
            this.cases.add(caseN);
            return this;
        }

        @Override
        public AstVariantNode build()
        {
            return new AstVariantNode(name, wideType, cases);
        }
    }
}
