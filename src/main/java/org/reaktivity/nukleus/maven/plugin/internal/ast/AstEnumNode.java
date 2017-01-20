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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class AstEnumNode extends AstNode
{
    private final String name;
    private final List<AstValueNode> values;

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitEnum(this);
    }

    public String name()
    {
        return name;
    }

    public List<AstValueNode> values()
    {
        return values;
    }

    @Override
    public int hashCode()
    {
        return (name.hashCode() << 3) ^ values.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof AstEnumNode))
        {
            return false;
        }

        AstEnumNode that = (AstEnumNode)o;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.values, that.values);
    }

    @Override
    public String toString()
    {
        return String.format("ENUM [name=%s, values=%s]", name, values);
    }

    private AstEnumNode(
        String name,
        List<AstValueNode> values)
    {
        this.name = requireNonNull(name);
        this.values = unmodifiableList(values);
    }

    public static final class Builder extends AstNode.Builder<AstEnumNode>
    {
        private String name;
        private List<AstValueNode> values;

        public Builder()
        {
            this.values = new LinkedList<>();
        }

        public Builder name(
            String name)
        {
            this.name = name;
            return this;
        }

        public Builder value(
            AstValueNode value)
        {
            this.values.add(value);
            return this;
        }

        public int size()
        {
            return values.size();
        }

        @Override
        public AstEnumNode build()
        {
            return new AstEnumNode(name, values);
        }
    }
}
