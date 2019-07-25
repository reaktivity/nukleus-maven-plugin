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
package org.reaktivity.nukleus.maven.plugin.internal.generated;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public class BigNumberFW extends Flyweight
{
    private static final int FIELD_OFFSET_VALUE = 0;

    private static final int FIELD_SIZE_VALUE = BitUtil.SIZE_OF_LONG;

    @Override
    public int limit()
    {
        return offset() + FIELD_SIZE_VALUE;
    }

    public BigNumber get()
    {
        return BigNumber.valueOf(buffer().getLong(offset()));
    }

    @Override
    public BigNumberFW tryWrap(
        DirectBuffer buffer, int offset, int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit) || limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public BigNumberFW wrap(
        DirectBuffer buffer, int offset, int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        return maxLimit() == offset() ? "null" : get().toString();
    }

    public static final class Builder extends Flyweight.Builder<BigNumberFW>
    {
        private boolean valueSet;

        public Builder()
        {
            super(new BigNumberFW());
        }

        public Builder wrap(
            MutableDirectBuffer buffer, int offset, int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }

        public Builder set(
            BigNumberFW value)
        {
            int newLimit = offset() + value.sizeof();
            checkLimit(newLimit, maxLimit());
            buffer().putBytes(offset(), value.buffer(), value.offset(), value.sizeof());
            limit(newLimit);
            valueSet = true;
            return this;
        }

        public Builder set(
            BigNumber value)
        {
            MutableDirectBuffer buffer = buffer();
            int offset = offset();
            int newLimit = offset + FIELD_SIZE_VALUE;
            checkLimit(newLimit, maxLimit());
            buffer.putLong(offset, value.value());
            limit(newLimit);
            valueSet = true;
            return this;
        }

        @Override
        public BigNumberFW build()
        {
            if (!valueSet)
            {
                throw new IllegalStateException("BigNumber not set");
            }
            return super.build();
        }
    }
}
