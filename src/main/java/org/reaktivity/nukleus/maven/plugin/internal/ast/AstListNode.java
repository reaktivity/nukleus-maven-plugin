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

public final class AstListNode extends AstNamedNode
{
    public static final Object NULL_DEFAULT = new Object();

    private final List<AstListMemberNode> members;
    private final AstType physicalLengthType;
    private final AstType logicalLengthType;

    public List<AstListMemberNode> members()
    {
        return members;
    }

    public AstType physicalLengthType()
    {
        return physicalLengthType;
    }

    public AstType logicalLengthType()
    {
        return logicalLengthType;
    }

    @Override
    public Kind getKind()
    {
        return Kind.LIST;
    }

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitList(this);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, members, physicalLengthType, logicalLengthType);
    }

    @Override
    public boolean equals(
        Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof AstListNode))
        {
            return false;
        }

        AstListNode that = (AstListNode) o;
        return Objects.equals(this.name, that.name) &&
            Objects.equals(this.members, that.members) &&
            Objects.equals(this.physicalLengthType, that.physicalLengthType) &&
            Objects.equals(this.logicalLengthType, that.logicalLengthType);
    }

    private AstListNode(
        String name,
        List<AstListMemberNode> members,
        AstType physicalLengthType,
        AstType logicalLengthType)
    {
        super(name);
        this.members = unmodifiableList(members);
        this.physicalLengthType = physicalLengthType;
        this.logicalLengthType = logicalLengthType;
    }

    public static final class Builder extends AstNamedNode.Builder<AstListNode>
    {
        private List<AstListMemberNode> members;
        private AstType physicalLengthType;
        private AstType logicalLengthType;

        public Builder()
        {
            this.members = new LinkedList<>();
        }

        public Builder name(
            String name)
        {
            this.name = name;
            return this;
        }

        public Builder member(
            AstListMemberNode member)
        {
            this.members.add(member);
            return this;
        }

        public Builder physicalLengthType(
            AstType physicalLengthType)
        {
            this.physicalLengthType = physicalLengthType;
            return this;
        }

        public Builder logicalLengthSize(
            AstType logicalLengthType)
        {
            this.logicalLengthType = logicalLengthType;
            return this;
        }

        @Override
        public AstListNode build()
        {
            return new AstListNode(name, members, physicalLengthType, logicalLengthType);
        }
    }
}
