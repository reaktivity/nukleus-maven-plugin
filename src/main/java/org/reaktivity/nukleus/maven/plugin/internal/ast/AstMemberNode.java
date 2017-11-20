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
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder.NATIVE;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class AstMemberNode extends AstNode
{
    public static final Object NULL_DEFAULT = new Object();

    private final String name;
    private final List<AstType> types;
    private final int size;
    private final String sizeName;
    private final AstType unsignedType;
    private final Object defaultValue;
    private final AstByteOrder byteOrder;
    private final boolean isArray;

    private AstType sizeType;
    private boolean usedAsSize;


    private AstMemberNode(
        String name,
        List<AstType> types,
        int size,
        String sizeName,
        AstType unsignedType,
        Object defaultValue,
        AstByteOrder byteOrder,
        boolean isArray)
    {
        this.name = requireNonNull(name);
        this.types = unmodifiableList(requireNotEmpty(requireNonNull(types)));
        this.size = size;
        this.sizeName = sizeName;
        this.unsignedType = unsignedType;
        this.defaultValue = defaultValue;
        this.byteOrder = byteOrder;
        this.isArray = isArray;
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

    public boolean isArray()
    {
        return isArray;
    }

    public int size()
    {
        return size;
    }

    public String sizeName()
    {
        return sizeName;
    }

    public AstType sizeType()
    {
        return sizeType;
    }

    public Object defaultValue()
    {
        return defaultValue;
    }

    public AstByteOrder byteOrder()
    {
        return byteOrder;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, types, unsignedType, sizeName, size, defaultValue, byteOrder);
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
                Objects.equals(this.defaultValue, that.defaultValue) &&
                Objects.equals(this.byteOrder, that.byteOrder);
    }

    @Override
    public String toString()
    {
        String size = this.size == 0 ? this.sizeName : Integer.toString(this.size);
        return String.format("MEMBER [name=%s, size=%s, types=%s, unsignedType=%s, defaultValue=%s, byteOrder=%s]",
                name, size, types, unsignedType, defaultValue, byteOrder);
    }

    public void sizeType(AstType sizeType)
    {
        this.sizeType = sizeType;
    }

    public void usedAsSize(boolean value)
    {
        usedAsSize = value;
    }

    public boolean usedAsSize()
    {
        return usedAsSize;
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
        private boolean isArray;
        private AstType unsignedType;
        private Object defaultValue;
        private AstByteOrder byteOrder;

        public Builder()
        {
            this.types = new LinkedList<>();
            this.size = -1;
            this.byteOrder = NATIVE;
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

        public void isArray(
            boolean isArray)
        {
            this.isArray = isArray;
        }

        public Builder size(int size)
        {
            this.size = size;
            isArray(true);
            return this;
        }

        public Builder sizeName(
            String sizeName)
        {
            this.sizeName = sizeName;
            isArray(true);
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

        public  Builder defaultToNull()
        {
            this.defaultValue = NULL_DEFAULT;
            return this;
        }

        public Builder byteOrder(AstByteOrder byteOrder)
        {
            this.byteOrder = byteOrder;
            return this;
        }

        @Override
        public AstMemberNode build()
        {
            return new AstMemberNode(name, types, size, sizeName, unsignedType, defaultValue, byteOrder, isArray);
        }
    }
}
