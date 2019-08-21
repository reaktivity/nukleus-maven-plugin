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
public final class VariantUint8KindWithUint64TypeFW extends Flyweight
{
    private static final int FIELD_SIZE_KIND = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_OFFSET_KIND = 0;

    public static final int KIND_UINT64 = 128;

    private static final int FIELD_OFFSET_UINT64 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT64 = BitUtil.SIZE_OF_LONG;

    public static final int KIND_UINT8 = 83;

    private static final int FIELD_OFFSET_UINT8 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT8 = BitUtil.SIZE_OF_SHORT;

    public static final int KIND_ZERO = 68;

    private static final int FIELD_VALUE_ZERO = 0;

    public static final int KIND_ONE = 1;

    private static final int FIELD_VALUE_ONE = 1;

    public static final int KIND_UINT32 = 112;

    private static final int FIELD_OFFSET_UINT32 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT32 = BitUtil.SIZE_OF_LONG;

    public static final int KIND_UINT16 = 96;

    private static final int FIELD_OFFSET_UINT16 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT16 = BitUtil.SIZE_OF_INT;

    public long getAsUint64()
    {
        return buffer().getLong(offset() + FIELD_OFFSET_UINT64);
    }

    public short getAsUint8()
    {
        return buffer().getShort(offset() + FIELD_OFFSET_UINT8);
    }

    public byte getAsZero()
    {
        return FIELD_VALUE_ZERO;
    }

    public byte getAsOne()
    {
        return FIELD_VALUE_ONE;
    }

    public long getAsUint32()
    {
        return buffer().getLong(offset() + FIELD_OFFSET_UINT32);
    }

    public int getAsUint16()
    {
        return buffer().getInt(offset() + FIELD_OFFSET_UINT16);
    }

    public int kind()
    {
        return buffer().getByte(offset() + FIELD_OFFSET_KIND) & 0xFF;
    }

    public long get()
    {
        switch (kind())
        {
        case KIND_UINT64:
            return getAsUint64();
        case KIND_UINT8:
            return getAsUint8();
        case KIND_ZERO:
            return getAsZero();
        case KIND_ONE:
            return getAsOne();
        case KIND_UINT32:
            return getAsUint32();
        case KIND_UINT16:
            return getAsUint16();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantUint8KindWithUint64TypeFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case KIND_UINT64:
            break;
        case KIND_UINT8:
            break;
        case KIND_ZERO:
            break;
        case KIND_ONE:
            break;
        case KIND_UINT32:
            break;
        case KIND_UINT16:
            break;
        default:
            break;
        }
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public VariantUint8KindWithUint64TypeFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case KIND_UINT64:
            break;
        case KIND_UINT8:
            break;
        case KIND_ZERO:
            break;
        case KIND_ONE:
            break;
        case KIND_UINT32:
            break;
        case KIND_UINT16:
            break;
        default:
            break;
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        switch (kind())
        {
        case KIND_UINT64:
            return String.format("VARIANTUINT8KINDWITHUINT64TYPE [uint64=%d]", getAsUint64());
        case KIND_UINT8:
            return String.format("VARIANTUINT8KINDWITHUINT64TYPE [uint8=%d]", getAsUint8());
        case KIND_ZERO:
            return String.format("VARIANTUINT8KINDWITHUINT64TYPE [zero=%d]", getAsZero());
        case KIND_ONE:
            return String.format("VARIANTUINT8KINDWITHUINT64TYPE [one=%d]", getAsOne());
        case KIND_UINT32:
            return String.format("VARIANTUINT8KINDWITHUINT64TYPE [uint32=%d]", getAsUint32());
        case KIND_UINT16:
            return String.format("VARIANTUINT8KINDWITHUINT64TYPE [uint16=%d]", getAsUint16());
        default:
            return String.format("VARIANTUINT8KINDWITHUINT64TYPE [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case KIND_UINT64:
            return offset() + FIELD_OFFSET_UINT64 + FIELD_SIZE_UINT64;
        case KIND_UINT8:
            return offset() + FIELD_OFFSET_UINT8 + FIELD_SIZE_UINT8;
        case KIND_ZERO:
            return offset();
        case KIND_ONE:
            return offset();
        case KIND_UINT32:
            return offset() + FIELD_OFFSET_UINT32 + FIELD_SIZE_UINT32;
        case KIND_UINT16:
            return offset() + FIELD_OFFSET_UINT16 + FIELD_SIZE_UINT16;
        default:
            return offset();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantUint8KindWithUint64TypeFW>
    {
        public Builder()
        {
            super(new VariantUint8KindWithUint64TypeFW());
        }

        private Builder kind(
            int value)
        {
            buffer().putByte(offset() + FIELD_OFFSET_KIND, (byte)(value & 0xFF));
            return this;
        }

        public Builder setAsUint64(
            long value)
        {
            int newLimit = offset() + FIELD_OFFSET_UINT64 + FIELD_SIZE_UINT64;
            checkLimit(newLimit, maxLimit());
            kind(KIND_UINT64);
            buffer().putLong(offset() + FIELD_OFFSET_UINT64, value);
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
            limit(newLimit);
            return this;
        }

        public Builder setAsOne()
        {
            int newLimit = offset() + FIELD_SIZE_KIND;
            checkLimit(newLimit, maxLimit());
            kind(KIND_ONE);
            limit(newLimit);
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

        public Builder setAsUint16(
            int value)
        {
            int newLimit = offset() + FIELD_OFFSET_UINT16 + FIELD_SIZE_UINT16;
            checkLimit(newLimit, maxLimit());
            kind(KIND_UINT16);
            buffer().putInt(offset() + FIELD_OFFSET_UINT16, value);
            limit(newLimit);
            return this;
        }

        public Builder set(
            long value)
        {
            int highestByteIndex = Long.numberOfTrailingZeros(Long.highestOneBit(value)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
                switch ((int) value)
                {
                case 1:
                    setAsOne();
                    break;
                default:
                    setAsUint8((short) value);
                    break;
                }
                break;
            case 1:
                setAsUint16((int) value);
                break;
            case 2:
            case 3:
                setAsUint32(value);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                setAsUint64(value);
                break;
            case 8:
                setAsZero();
                break;
            default:
                throw new IllegalArgumentException("Illegal value: " + value);
            }
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
