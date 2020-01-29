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

public final class BoundedOctets16FW extends BoundedOctetsFW
{
    private static final int LENGTH_SIZE = BitUtil.SIZE_OF_SHORT;

    private static final int LENGTH_OFFSET = 0;

    private static final int VALUE_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;

    public <T> T get(
        Flyweight.Visitor<T> visitor)
    {
        return visitor.visit(buffer(), offset() + VALUE_OFFSET, limit());
    }

    public int length()
    {
        return buffer().getShort(offset() + LENGTH_OFFSET) & 0xFFFF;
    }

    @Override
    public BoundedOctets16FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public BoundedOctets16FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public int limit()
    {
        return offset() + LENGTH_SIZE + length();
    }

    @Override
    public String toString()
    {
        return String.format("boundedOctets16[%d]", length());
    }

    public static final class Builder extends BoundedOctetsFW.Builder<BoundedOctets16FW>
    {
        public Builder()
        {
            super(new BoundedOctets16FW());
        }

        @Override
        public Builder set(
            BoundedOctetsFW value)
        {
            int newLimit = offset() + LENGTH_SIZE + value.length();
            checkLimit(newLimit, maxLimit());
            buffer().putShort(offset() + LENGTH_OFFSET, (short) (value.length() & 0xFFFF));
            buffer().putBytes(offset() + VALUE_OFFSET, value.buffer(), value.offset() + VALUE_OFFSET, value.length());
            limit(newLimit);
            return this;
        }

        @Override
        public Builder set(
            DirectBuffer value,
            int offset,
            int length)
        {
            int newLimit = offset() + LENGTH_SIZE + length;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(offset() + LENGTH_OFFSET, (short) (length & 0xFFFF));
            buffer().putBytes(offset() + VALUE_OFFSET, value, offset, length);
            limit(newLimit);
            return this;
        }

        @Override
        public Builder set(
            byte[] value)
        {
            int newLimit = offset() + LENGTH_SIZE + value.length;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(offset() + LENGTH_OFFSET, (short) (value.length & 0xFFFF));
            buffer().putBytes(offset() + VALUE_OFFSET, value);
            limit(newLimit);
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            checkLimit(offset + LENGTH_SIZE, maxLimit);
            super.wrap(buffer, offset, maxLimit);
            return this;
        }
    }
}
