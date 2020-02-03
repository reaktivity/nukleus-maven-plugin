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
package org.reaktivity.nukleus.maven.plugin.internal.ast;

import static java.util.Collections.unmodifiableList;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class AstVariantNode extends AstNamedNode
{
    private final AstType ofType;
    private final AstType kindType;
    private final List<AstVariantCaseNode> cases;

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitVariant(this);
    }

    @Override
    public Kind getKind()
    {
        return Kind.VARIANT;
    }

    public AstType of()
    {
        return ofType;
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
    public AstNamedNode withName(
        String name)
    {
        return new AstVariantNode(name, ofType, kindType, cases);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, ofType, kindType, cases);
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

        AstVariantNode that = (AstVariantNode) o;
        return Objects.equals(this.name, that.name) &&
            Objects.equals(this.cases, that.cases) &&
            Objects.equals(this.ofType, that.ofType) &&
            Objects.equals(this.kindType, that.kindType);
    }

    private AstVariantNode(
        String name,
        AstType ofType,
        AstType kindType,
        List<AstVariantCaseNode> cases)
    {
        super(name);
        this.ofType = ofType;
        this.kindType = kindType;
        this.cases = unmodifiableList(cases);
    }

    public static final class Builder extends AstNamedNode.Builder<AstVariantNode>
    {
        private AstType ofType;
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

        public Builder of(
            AstType ofType)
        {
            this.ofType = ofType;
            return this;
        }

        public Builder kindType(
            AstType kindType)
        {
            this.kindType = kindType;
            return this;
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
            return new AstVariantNode(name, ofType, kindType, cases);
        }
    }
}
