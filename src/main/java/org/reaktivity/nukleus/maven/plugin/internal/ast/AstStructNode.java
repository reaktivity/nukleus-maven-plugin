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

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstMemberNode.NULL_DEFAULT;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class AstStructNode extends AstNode
{
    private final String name;
    private final int typeId;
    private final String supertype;
    private final List<AstMemberNode> members;

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitStruct(this);
    }

    public String name()
    {
        return name;
    }

    public int typeId()
    {
        return typeId;
    }

    public String supertype()
    {
        return supertype;
    }

    public List<AstMemberNode> members()
    {
        return members;
    }

    @Override
    public int hashCode()
    {
        return supertype != null
               ? (name.hashCode() << 11) ^ supertype.hashCode() << 7 ^ members.hashCode() << 3 ^ typeId
               : (name.hashCode() << 11) ^ members.hashCode() << 3 ^ typeId;

    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof AstStructNode))
        {
            return false;
        }

        AstStructNode that = (AstStructNode)o;
        return Objects.equals(this.name, that.name) &&
                this.typeId == that.typeId &&
                Objects.equals(this.supertype, that.supertype) &&
                Objects.equals(this.members, that.members);
    }

    private AstStructNode(
        String name,
        int typeId,
        String supertype,
        List<AstMemberNode> members)
    {
        this.name = requireNonNull(name);
        this.typeId = typeId;
        this.supertype = supertype;
        this.members = unmodifiableList(members);
    }

    public static final class Builder extends AstNode.Builder<AstStructNode>
    {
        private String name;
        private String supertype;
        private int typeId;
        private List<AstMemberNode> members;

        public Builder()
        {
            this.members = new LinkedList<>();
        }

        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        public Builder typeId(int typeId)
        {
            this.typeId = typeId;
            return this;
        }

        public Builder supertype(String supertype)
        {
            this.supertype = supertype;
            return this;
        }

        public Builder member(AstMemberNode member)
        {
            if (member.sizeName() != null)
            {
                final String target = member.sizeName();
                Optional<AstMemberNode> sizeField = members.stream().filter(n -> target.equals(n.name())).findFirst();
                if (sizeField.isPresent())
                {
                    AstMemberNode size = sizeField.get();
                    member.sizeType(size.unsignedType() != null ? size.unsignedType() : size.type());
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
                    else if (defaultsToNull && !size.type().isSignedInteger())
                    {
                        Object sizeType = size.type();
                        throw new IllegalArgumentException(format(
                                "Size field \"%s\" for field \"%s\" defaulting to null must be a signed integer type",
                                member.sizeName(), member.name()));
                    }
                    else if (!defaultsToNull && size.unsignedType() == null)
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

        @Override
        public AstStructNode build()
        {
            return new AstStructNode(name, typeId, supertype, members);
        }
    }
}
