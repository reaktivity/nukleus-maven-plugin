/**
 * Copyright 2016-2020 The Reaktivity Project
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
package org.reaktivity.nukleus.maven.plugin.internal.generated;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public final class EnumWithVariantOfUint64FW extends Flyweight
{
    private final VariantWithVariantCaseFW variantWithVariantCaseRO = new VariantWithVariantCaseFW();

    public EnumWithVariantOfUint64 get()
    {
        return EnumWithVariantOfUint64.valueOf(variantWithVariantCaseRO.get());
    }

    @Override
    public EnumWithVariantOfUint64FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null || limit() > maxLimit)
        {
            return null;
        }
        if (variantWithVariantCaseRO.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        return this;
    }

    @Override
    public EnumWithVariantOfUint64FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        variantWithVariantCaseRO.wrap(buffer, offset, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        return maxLimit() == offset() ? "null" : get().toString();
    }

    @Override
    public int limit()
    {
        return variantWithVariantCaseRO.limit();
    }

    public static final class Builder extends Flyweight.Builder<EnumWithVariantOfUint64FW>
    {
        private final VariantWithVariantCaseFW.Builder variantWithVariantCaseRW = new VariantWithVariantCaseFW.Builder();

        private boolean valueSet;

        public Builder()
        {
            super(new EnumWithVariantOfUint64FW());
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            variantWithVariantCaseRW.wrap(buffer, offset, maxLimit);
            return this;
        }

        public Builder set(
            EnumWithVariantOfUint64FW value)
        {
            variantWithVariantCaseRW.set(value.get().value());
            limit(variantWithVariantCaseRW.limit());
            valueSet = true;
            return this;
        }

        public Builder set(
            EnumWithVariantOfUint64 value)
        {
            variantWithVariantCaseRW.set(value.value());
            limit(variantWithVariantCaseRW.limit());
            valueSet = true;
            return this;
        }

        @Override
        public EnumWithVariantOfUint64FW build()
        {
            if (!valueSet)
            {
                throw new IllegalStateException("EnumWithInt8 not set");
            }
            return super.build();
        }
    }
}
