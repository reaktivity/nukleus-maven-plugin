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

// TODO: To be deleted
public final class VariantUnsignedIntFW extends Flyweight
{
    private static final int FIELD_SIZE_KIND = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_OFFSET_KIND = 0;

    public static final int KIND_UINT32 = 0x04;

    private static final int FIELD_OFFSET_UINT32 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT32 = BitUtil.SIZE_OF_LONG;

    public static final int KIND_UINT8 = 0x01;

    private static final int FIELD_OFFSET_UINT8 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT8 = BitUtil.SIZE_OF_SHORT;

    public static final int KIND_ZERO = 0x00;

    private static final byte FIELD_VALUE_ZERO = 0;

    private static final long UINT8_MAX = 255L;

    private static final long UINT32_MAX = 4294967295L;

    public long get()
    {
        switch (kind())
        {
        case KIND_UINT32:
            return getAsUint32();
        case KIND_UINT8:
            return getAsUint8();
        case KIND_ZERO:
            return getAsZero();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    public short getAsUint8()
    {
        return buffer().getShort(offset() + FIELD_OFFSET_UINT8);
    }

    public long getAsUint32()
    {
        return buffer().getLong(offset() + FIELD_OFFSET_UINT32);
    }

    public byte getAsZero()
    {
        return FIELD_VALUE_ZERO;
    }

    public int kind()
    {
        return buffer().getByte(offset() + FIELD_OFFSET_KIND) & 0xFF;
    }

    @Override
    public VariantUnsignedIntFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public Flyweight wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        switch (kind())
        {
        case KIND_UINT32:
            return String.format("VARIANTUNSIGNEDINT [uint32=%s]", getAsUint32());
        case KIND_UINT8:
            return String.format("VARIANTUNSIGNEDINT [uint8=%s]", getAsUint8());
        case KIND_ZERO:
            return String.format("VARIANTUNSIGNEDINT [zero=%s]", getAsZero());
        default:
            return String.format("VARIANTUNSIGNEDINT [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case KIND_UINT32:
            return offset() + FIELD_OFFSET_UINT32 + FIELD_SIZE_UINT32;
        case KIND_UINT8:
            return offset() + FIELD_OFFSET_UINT8 + FIELD_SIZE_UINT8;
        case KIND_ZERO:
            return offset();
        default:
            return offset();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantUnsignedIntFW>
    {
        public Builder()
        {
            super(new VariantUnsignedIntFW());
        }

        private Builder kind(
            int value)
        {
            buffer().putByte(offset() + FIELD_OFFSET_KIND, (byte)(value & 0xFF));
            return this;
        }

        public Builder set(
            long value)
        {
            if (value == FIELD_VALUE_ZERO)
            {
                setAsZero();
                return this;
            }
            if (value <= UINT8_MAX)
            {
                setAsUint8((short) value);
                return this;
            }
            if (value <= UINT32_MAX)
            {
                setAsUint32(value);
                return this;
            }
            return this;
        }

        public Builder setAsUint32(
            long value)
        {
            int newLimit = offset() + FIELD_OFFSET_UINT32 + FIELD_SIZE_UINT32;
            checkLimit(newLimit, maxLimit());
            kind(KIND_UINT32);
            buffer().putLong(offset() + FIELD_OFFSET_UINT32, value);
            limit(newLimit);
            return this;
        }

        public Builder setAsUint8(
            short value)
        {
            int newLimit = offset() + FIELD_OFFSET_UINT8 + FIELD_SIZE_UINT8;
            checkLimit(newLimit, maxLimit());
            kind(KIND_UINT8);
            buffer().putShort(offset() + FIELD_OFFSET_UINT8, value);
            limit(newLimit);
            return this;
        }

        public Builder setAsZero()
        {
            int newLimit = offset() + FIELD_SIZE_KIND;
            checkLimit(newLimit, maxLimit());
            kind(KIND_ZERO);
            limit(offset());
            return this;
        }

        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }
    }
}
