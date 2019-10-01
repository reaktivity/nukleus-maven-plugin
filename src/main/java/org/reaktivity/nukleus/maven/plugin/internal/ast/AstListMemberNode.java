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
    private final boolean isRequired;

    AstListMemberNode(
        String name,
        List<AstType> types,
        int size,
        String sizeName,
        Object defaultValue,
        AstByteOrder byteOrder,
        boolean isRequired)
    {
        super(name, types, size, sizeName, defaultValue, byteOrder);
        this.isRequired = isRequired;
    }

    public boolean isRequired()
    {
        return isRequired;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, types, sizeName, size, defaultValue, byteOrder, isRequired);
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
        return this.size == that.size &&
            this.isRequired == that.isRequired &&
            Objects.equals(this.name, that.name) &&
            Objects.deepEquals(this.types, that.types) &&
            Objects.equals(this.sizeName, that.sizeName) &&
            Objects.equals(this.defaultValue, that.defaultValue) &&
            Objects.equals(this.byteOrder, that.byteOrder);
    }

    @Override
    public String toString()
    {
        String size = this.size == 0 ? this.sizeName : Integer.toString(this.size);
        return String.format("MEMBER [name=%s, size=%s, types=%s, defaultValue=%s, byteOrder=%s, isRequired=%s]",
            name, size, types, defaultValue, byteOrder, isRequired);
    }

    public static final class Builder extends AstAbstractMemberNode.Builder<AstListMemberNode>
    {
        private boolean isRequired;

        public Builder isRequired(
            boolean isRequired)
        {
            this.isRequired = isRequired;
            return this;
        }

        @Override
        public AstListMemberNode build()
        {
            return new AstListMemberNode(name, types, size, sizeName, defaultValue, byteOrder, isRequired);
        }
    }
}
