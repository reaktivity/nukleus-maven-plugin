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

import java.util.Objects;

public final class AstVariantCaseNode extends AstNode
{
    private final Object kind;
    private final String typeName;
    private final AstType type;
    private final AstType unsignedType;

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitVariantCase(this);
    }

    public Object kind()
    {
        return kind;
    }

    public String typeName()
    {
        return typeName;
    }

    public AstType type()
    {
        return type;
    }

    public AstType unsignedType()
    {
        return unsignedType;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(kind, typeName, type, unsignedType);
    }

    @Override
    public boolean equals(
        Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof AstVariantCaseNode))
        {
            return false;
        }

        AstVariantCaseNode that = (AstVariantCaseNode)o;
        return Objects.equals(this.kind, that.kind) &&
            Objects.equals(this.typeName, that.typeName) &&
            Objects.equals(this.type, that.type) &&
            Objects.equals(this.unsignedType, that.unsignedType);
    }

    private AstVariantCaseNode(
        Object kind,
        String typeName,
        AstType type,
        AstType unsignedType)
    {
        this.kind = kind;
        this.typeName = typeName;
        this.type = type;
        this.unsignedType = unsignedType;
    }

    @Override
    public String toString()
    {
        return String.format("CASE [kind=%s, typeName=%s, type=%s, unsignedType=%s]", kind, typeName, type, unsignedType);
    }

    public static final class Builder extends AstNode.Builder<AstVariantCaseNode>
    {
        private Object kind;
        private String typeName;
        private AstType type;
        private AstType unsignedType;

        public Builder kind(
            Object kind)
        {
            this.kind = kind;
            return this;
        }

        public Builder typeName(
            String typeName)
        {
            this.typeName = typeName;
            return this;
        }

        public Builder type(
            AstType type)
        {
            this.type = type;
            return this;
        }

        public Builder unsignedType(
            AstType unsignedType)
        {
            this.unsignedType = unsignedType;
            return this;
        }

        @Override
        public AstVariantCaseNode build()
        {
            return new AstVariantCaseNode(kind, typeName, type, unsignedType);
        }
    }
}
