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

import static java.util.Objects.requireNonNull;

import java.util.Objects;

public final class AstValueNode extends AstNode
{
    private final String name;
    private final int ordinal;

    private AstValueNode(
        String name,
        int ordinal)
    {
        this.name = requireNonNull(name);
        this.ordinal = ordinal;
    }

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitValue(this);
    }

    public String name()
    {
        return name;
    }

    public int size()
    {
        return ordinal;
    }

    @Override
    public int hashCode()
    {
        return (name.hashCode() << 3) ^ ordinal;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof AstValueNode))
        {
            return false;
        }

        AstValueNode that = (AstValueNode)o;
        return this.ordinal == that.ordinal &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public String toString()
    {
        return String.format("VALUE [name=%s, ordinal=%d]", name, ordinal);
    }

    public static final class Builder extends AstNode.Builder<AstValueNode>
    {
        private String name;
        private int ordinal;

        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        public Builder ordinal(int ordinal)
        {
            this.ordinal = ordinal;
            return this;
        }

        @Override
        public AstValueNode build()
        {
            return new AstValueNode(name, ordinal);
        }
    }
}
