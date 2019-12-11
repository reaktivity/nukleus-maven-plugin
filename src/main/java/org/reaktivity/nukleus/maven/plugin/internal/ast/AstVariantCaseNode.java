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
    private final Object value;
    private final AstType type;
    private final int missingFieldValue;

    @Override
    public <R> R accept(
        Visitor<R> visitor)
    {
        return visitor.visitVariantCase(this);
    }

    public Object value()
    {
        return value;
    }

    public AstType type()
    {
        return type;
    }

    public int missingFieldValue()
    {
        return missingFieldValue;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value, type, missingFieldValue);
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

        AstVariantCaseNode that = (AstVariantCaseNode) o;
        return Objects.equals(this.value, that.value) &&
            Objects.equals(this.type, that.type) &&
            this.missingFieldValue == that.missingFieldValue;
    }

    private AstVariantCaseNode(
        Object value,
        AstType type,
        int missingFieldValue)
    {
        this.value = value;
        this.type = type;
        this.missingFieldValue = missingFieldValue;
    }

    @Override
    public String toString()
    {
        return String.format("CASE [value=%s, type=%s]", value, type);
    }

    public static final class Builder extends AstNode.Builder<AstVariantCaseNode>
    {
        private Object value;
        private AstType type;
        private int missingFieldValue;

        public Builder value(
            Object value)
        {
            this.value = value;
            return this;
        }

        public Builder type(
            AstType type)
        {
            this.type = type;
            return this;
        }

        public Builder missingFieldValue(
            int missingFieldValue)
        {
            this.missingFieldValue = missingFieldValue;
            return this;
        }

        @Override
        public AstVariantCaseNode build()
        {
            return new AstVariantCaseNode(value, type, missingFieldValue);
        }
    }
}
