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

import java.util.List;
import java.util.Objects;

public final class AstListMemberNode extends AstAbstractMemberNode
{
    private final boolean required;
    private final AstType arrayType;
    private final AstType arrayTypeName;

    private AstListMemberNode(
        String name,
        List<AstType> types,
        int size,
        String sizeName,
        Object defaultValue,
        AstByteOrder byteOrder,
        boolean required,
        AstType arrayType,
        AstType arrayTypeName)
    {
        super(name, types, size, sizeName, defaultValue, byteOrder);
        this.required = required;
        this.arrayType = arrayType;
        this.arrayTypeName = arrayTypeName;
    }

    public boolean isRequired()
    {
        return required;
    }

    public AstType arrayType()
    {
        return arrayType;
    }

    public AstType arrayTypeName()
    {
        return arrayTypeName;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, types, defaultValue, byteOrder, required, arrayType, arrayTypeName);
    }

    @Override
    public boolean equals(
        Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof AstListMemberNode))
        {
            return false;
        }

        AstListMemberNode that = (AstListMemberNode) o;
        return this.required == that.required &&
            Objects.equals(this.name, that.name) &&
            Objects.deepEquals(this.types, that.types) &&
            Objects.equals(this.defaultValue, that.defaultValue) &&
            Objects.equals(this.byteOrder, that.byteOrder) &&
            Objects.equals(this.arrayType, that.arrayType) &&
            Objects.equals(this.arrayTypeName, that.arrayTypeName);
    }

    @Override
    public String toString()
    {
        return String.format("MEMBER [name=%s, types=%s, defaultValue=%s, byteOrder=%s, required=%s, arrayType=%s]",
            name, types, defaultValue, byteOrder, required, arrayType, arrayTypeName);
    }

    public static final class Builder extends AstAbstractMemberNode.Builder<AstListMemberNode>
    {
        private boolean required;
        private AstType arrayType;
        private AstType arrayTypeName;

        public Builder isRequired(
            boolean required)
        {
            this.required = required;
            return this;
        }

        public Builder arrayType(
            AstType arrayType)
        {
            this.arrayType = arrayType;
            return this;
        }

        public Builder arrayTypeName(
            AstType arrayTypeName)
        {
            this.arrayTypeName = arrayTypeName;
            return this;
        }

        @Override
        public AstListMemberNode build()
        {
            return new AstListMemberNode(name, types, size, sizeName, defaultValue, byteOrder, required, arrayType,
                arrayTypeName);
        }
    }
}
