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

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class AstMemberNode extends AstNode
{
    private final String name;
    private final List<AstType> types;
    private final int size;
    private final String sizeName;
    private final AstType unsignedType;
    private final Integer defaultValue;

    private AstMemberNode(
        String name,
        List<AstType> types,
        int size,
        String sizeName,
        AstType unsignedType,
        Integer defaultValue)
    {
        this.name = requireNonNull(name);
        this.types = unmodifiableList(requireNotEmpty(requireNonNull(types)));
        this.size = size;
        this.sizeName = sizeName;
        this.unsignedType = unsignedType;
        this.defaultValue = defaultValue;
    }

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitMember(this);
    }

    public String name()
    {
        return name;
    }

    public AstType type()
    {
        return types.get(0);
    }

    public AstType unsignedType()
    {
        return unsignedType;
    }

    public List<AstType> types()
    {
        return types;
    }

    public int size()
    {
        return size;
    }

    public String sizeName()
    {
        return sizeName;
    }

    public Integer defaultValue()
    {
        return defaultValue;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, types, unsignedType, sizeName, size);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof AstMemberNode))
        {
            return false;
        }

        AstMemberNode that = (AstMemberNode)o;
        return this.size == that.size &&
                Objects.equals(this.name, that.name) &&
                Objects.deepEquals(this.types, that.types) &&
                Objects.equals(this.unsignedType, that.unsignedType) &&
                Objects.equals(this.sizeName, that.sizeName) &&
                Objects.equals(this.defaultValue, that.defaultValue);
    }

    @Override
    public String toString()
    {
        String size = this.size == 0 ? this.sizeName : Integer.toString(this.size);
        return String.format("MEMBER [name=%s, size=%s, types=%s, unsignedType=%s, defaultValue=%s]",
                name, size, types, unsignedType, defaultValue);
    }

    private static <T extends Collection<?>> T requireNotEmpty(
        T c)
    {
        if (c.isEmpty())
        {
            throw new IllegalArgumentException();
        }

        return c;
    }

    public static final class Builder extends AstNode.Builder<AstMemberNode>
    {
        private String name;
        private List<AstType> types;
        private int size;
        private String sizeName;
        private AstType unsignedType;
        private Integer defaultValue;

        public Builder()
        {
            this.types = new LinkedList<>();
            this.size = -1;
        }

        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        public Builder type(AstType type)
        {
            types.add(requireNonNull(type));
            return this;
        }

        public Builder size(int size)
        {
            this.size = size;
            return this;
        }

        public Builder sizeName(
            String sizeName)
        {
            this.sizeName = sizeName;
            return this;
        }

        public Builder unsignedType(AstType unsignedType)
        {
            this.unsignedType = requireNonNull(unsignedType);
            return this;
        }

        public  Builder defaultValue(
            int defaultValue)
        {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public AstMemberNode build()
        {
            return new AstMemberNode(name, types, size, sizeName, unsignedType, defaultValue);
        }
    }
}
