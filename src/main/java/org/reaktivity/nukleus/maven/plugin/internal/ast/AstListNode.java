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

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class AstListNode extends AstNode
{
    public static final Object NULL_DEFAULT = new Object();

    private final String name;
    private final List<AstListMemberNode> members;
    private final AstType physicalLengthSize;
    private final AstType logicalLengthSize;

    public String name()
    {
        return name;
    }

    public List<AstListMemberNode> members()
    {
        return members;
    }

    public AstType physicalLengthSize()
    {
        return physicalLengthSize;
    }

    public AstType logicalLengthSize()
    {
        return logicalLengthSize;
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
        return Objects.hash(name, members, physicalLengthSize, logicalLengthSize);
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
            Objects.equals(this.physicalLengthSize, that.physicalLengthSize) &&
            Objects.equals(this.logicalLengthSize, that.logicalLengthSize);
    }

    private AstListNode(
        String name,
        List<AstListMemberNode> members,
        AstType physicalLengthSize,
        AstType logicalLengthSize)
    {
        this.name = requireNonNull(name);
        this.members = unmodifiableList(members);
        this.physicalLengthSize = physicalLengthSize;
        this.logicalLengthSize = logicalLengthSize;
    }

    public static final class Builder extends AstNode.Builder<AstListNode>
    {
        private String name;
        private List<AstListMemberNode> members;
        private AstType physicalLengthSize;
        private AstType logicalLengthSize;

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
            if (member.sizeName() != null)
            {
                final String target = member.sizeName();
                Optional<AstListMemberNode> sizeField = members.stream().filter(n -> target.equals(n.name())).findFirst();
                if (sizeField.isPresent())
                {
                    AstListMemberNode size = sizeField.get();
                    member.sizeType(size.type());

                    if (size.defaultValue() != null)
                    {
                        throw new IllegalArgumentException(format(
                            "Size field \"%s\" for field \"%s\" must not have a default value's",
                            member.sizeName(), member.name()));
                    }
                    boolean defaultsToNull = member.defaultValue() == NULL_DEFAULT;
                    boolean sizeIsVarint = size.type() == AstType.VARINT32 || size.type() == AstType.VARINT64;
                    if (sizeIsVarint)
                    {
                        if (member.type() == AstType.OCTETS)
                        {
                            size.usedAsSize(true);
                        }
                        else
                        {
                            throw new IllegalArgumentException(format(
                                "Size field \"%s\" for field \"%s\" may not be of type varint",
                                member.sizeName(), member.name()));
                        }
                    }
                    else if (defaultsToNull && !size.type().isSignedInt())
                    {
                        Object sizeType = size.type();
                        throw new IllegalArgumentException(format(
                            "Size field \"%s\" for field \"%s\" defaulting to null must be a signed integer type",
                            member.sizeName(), member.name()));
                    }
                    else if (!defaultsToNull && !size.type().isUnsignedInt())
                    {
                        throw new IllegalArgumentException(format(
                            "Size field \"%s\" for field \"%s\" must be an unsigned integer type",
                            member.sizeName(), member.name()));
                    }
                    else
                    {
                        size.usedAsSize(true);
                    }
                }
                else
                {
                    throw new IllegalArgumentException(format("Size field \"%s\" not found for field \"%s\"",
                        member.sizeName(), member.name()));
                }
            }
            this.members.add(member);
            return this;
        }

        public Builder physicalLengthSize(
            AstType physicalLengthSize)
        {
            this.physicalLengthSize = physicalLengthSize;
            return this;
        }

        public Builder logicalLengthSize(
            AstType logicalLengthSize)
        {
            this.logicalLengthSize = logicalLengthSize;
            return this;
        }

        @Override
        public AstListNode build()
        {
            return new AstListNode(name, members, physicalLengthSize, logicalLengthSize);
        }
    }
}
