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

import static java.util.Collections.unmodifiableList;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class AstUnionNode extends AstNamedNode
{
    private final List<AstUnionCaseNode> cases;
    private final AstType superType;

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitUnion(this);
    }

    @Override
    public AstNamedNode withName(
        String name)
    {
        return new AstUnionNode(name, cases, superType);
    }

    @Override
    public Kind getKind()
    {
        return Kind.UNION;
    }

    public List<AstUnionCaseNode> cases()
    {
        return cases;
    }

    public AstType superType()
    {
        return superType;
    }

    @Override
    public int hashCode()
    {
        return superType != null
            ? (superType.hashCode() << 11) ^ (name.hashCode() << 7) ^ cases.hashCode()
            : (name.hashCode() << 7) ^ cases.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof AstUnionNode))
        {
            return false;
        }

        AstUnionNode that = (AstUnionNode) o;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.cases, that.cases) &&
                Objects.equals(this.superType, that.superType);
    }

    private AstUnionNode(
        String name,
        List<AstUnionCaseNode> cases,
        AstType superType)
    {
        super(name);
        this.cases = unmodifiableList(cases);
        this.superType = superType;
    }

    public static final class Builder extends AstNamedNode.Builder<AstUnionNode>
    {
        private List<AstUnionCaseNode> cases;
        private AstType superType;

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

        public Builder caseN(
            AstUnionCaseNode caseN)
        {
            this.cases.add(caseN);
            return this;
        }

        public Builder superType(
            AstType superType)
        {
            this.superType = superType;
            return this;
        }

        @Override
        public AstUnionNode build()
        {
            return new AstUnionNode(name, cases, superType);
        }
    }
}
