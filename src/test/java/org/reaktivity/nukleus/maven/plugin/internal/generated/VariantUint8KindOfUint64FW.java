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

import java.nio.ByteOrder;

import org.agrona.BitUtil;
import org.agrona.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public final class VariantUint8KindOfUint64FW extends Flyweight
{
    private static final int FIELD_SIZE_KIND = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_OFFSET_KIND = 0;

    public static final int KIND_UINT64 = 128;

    private static final int FIELD_OFFSET_UINT64 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT64 = BitUtil.SIZE_OF_LONG;

    public static final int KIND_UINT8 = 83;

    private static final int FIELD_OFFSET_UINT8 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT8 = BitUtil.SIZE_OF_BYTE;

    public static final int KIND_ZERO = 68;

    private static final int FIELD_VALUE_ZERO = 0;

    public static final int KIND_ONE = 1;

    private static final int FIELD_VALUE_ONE = 1;

    public static final int KIND_UINT32 = 112;

    private static final int FIELD_OFFSET_UINT32 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT32 = BitUtil.SIZE_OF_INT;

    public static final int KIND_UINT16 = 96;

    private static final int FIELD_OFFSET_UINT16 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT16 = BitUtil.SIZE_OF_SHORT;

    public static final int KIND_UINT24 = 80;

    private static final int FIELD_OFFSET_UINT24 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_UINT24 = BitUtil.SIZE_OF_SHORT + BitUtil.SIZE_OF_BYTE;

    public long getAsUint64()
    {
        return buffer().getLong(offset() + FIELD_OFFSET_UINT64);
    }

    public int getAsUint8()
    {
        return buffer().getByte(offset() + FIELD_OFFSET_UINT8) & 0xFF;
    }

    public int getAsZero()
    {
        return FIELD_VALUE_ZERO;
    }

    public int getAsOne()
    {
        return FIELD_VALUE_ONE;
    }

    public long getAsUint32()
    {
        return buffer().getInt(offset() + FIELD_OFFSET_UINT32) & 0xFFFF_FFFFL;
    }

    public int getAsUint16()
    {
        return buffer().getShort(offset() + FIELD_OFFSET_UINT16) & 0xFFFF;
    }

    public int getAsUint24()
    {
        int offset = offset() + FIELD_OFFSET_UINT24;
        int bits = (buffer().getByte(offset) & 0xff) << 16 |
            (buffer().getByte(offset + 1) & 0xff) << 8 |
            (buffer().getByte(offset + 2) & 0xff);
        if (BufferUtil.NATIVE_BYTE_ORDER != ByteOrder.BIG_ENDIAN)
        {
            bits = (buffer().getByte(offset) & 0xff) |
                (buffer().getByte(offset + 1) & 0xff) << 8 |
                (buffer().getByte(offset + 2) & 0xff) << 16;
        }
        return bits;
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
        case KIND_UINT24:
            return getAsUint24();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantUint8KindOfUint64FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
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
        case KIND_UINT24:
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
    public VariantUint8KindOfUint64FW wrap(
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
        case KIND_UINT24:
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
            return String.format("VARIANT_UINT8_KIND_OF_UINT64 [uint64=%d]", getAsUint64());
        case KIND_UINT8:
            return String.format("VARIANT_UINT8_KIND_OF_UINT64 [uint8=%d]", getAsUint8());
        case KIND_ZERO:
            return String.format("VARIANT_UINT8_KIND_OF_UINT64 [zero=%d]", getAsZero());
        case KIND_ONE:
            return String.format("VARIANT_UINT8_KIND_OF_UINT64 [one=%d]", getAsOne());
        case KIND_UINT32:
            return String.format("VARIANT_UINT8_KIND_OF_UINT64 [uint32=%d]", getAsUint32());
        case KIND_UINT16:
            return String.format("VARIANT_UINT8_KIND_OF_UINT64 [uint16=%d]", getAsUint16());
        case KIND_UINT24:
            return String.format("VARIANT_UINT8_KIND_OF_UINT64 [uint24=%d]", getAsUint24());
        default:
            return String.format("VARIANT_UINT8_KIND_OF_UINT64 [unknown]");
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
        case KIND_UINT24:
            return offset() + FIELD_OFFSET_UINT24 + FIELD_SIZE_UINT24;
        default:
            return offset();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantUint8KindOfUint64FW>
    {
        public Builder()
        {
            super(new VariantUint8KindOfUint64FW());
        }

        public Builder setAsUint64(
            long value)
        {
            kind(KIND_UINT64);
            int newLimit = offset() + FIELD_OFFSET_UINT64 + FIELD_SIZE_UINT64;
            checkLimit(newLimit, maxLimit());
            buffer().putLong(offset() + FIELD_OFFSET_UINT64, value);
            limit(newLimit);
            return this;
        }

        public Builder setAsUint8(
            int value)
        {
            kind(KIND_UINT8);
            int newLimit = offset() + FIELD_OFFSET_UINT8 + FIELD_SIZE_UINT8;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(offset() + FIELD_OFFSET_UINT8, (byte)(value & 0xFF));
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
            kind(KIND_UINT32);
            int newLimit = offset() + FIELD_OFFSET_UINT32 + FIELD_SIZE_UINT32;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(offset() + FIELD_OFFSET_UINT32, (int)(value & 0xFFFF_FFFFL));
            limit(newLimit);
            return this;
        }

        public Builder setAsUint16(
            int value)
        {
            kind(KIND_UINT16);
            int newLimit = offset() + FIELD_OFFSET_UINT16 + FIELD_SIZE_UINT16;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(offset() + FIELD_OFFSET_UINT16, (short)(value & 0xFFFF));
            limit(newLimit);
            return this;
        }

        public Builder setAsUint24(
            int value)
        {
            kind(KIND_UINT24);
            int newLimit = offset() + FIELD_OFFSET_UINT24 + FIELD_SIZE_UINT24;
            checkLimit(newLimit, maxLimit());
            int offset = offset() + FIELD_OFFSET_UINT24;
            if (BufferUtil.NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN)
            {
                buffer().putByte(offset, (byte) (value >> 16));
                buffer().putByte(offset + 1, (byte) (value >> 8));
                buffer().putByte(offset + 2, (byte) value);
            }
            else
            {
                buffer().putByte(offset, (byte) value);
                buffer().putByte(offset + 1, (byte) (value >> 8));
                buffer().putByte(offset + 2, (byte) (value >> 16));
            }
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
                    setAsUint8((int) value);
                    break;
                }
                break;
            case 1:
                setAsUint16((int) value);
                break;
            case 2:
                setAsUint24((int) value);
                break;
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

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }

        public Builder kind(
            int value)
        {
            buffer().putByte(offset() + FIELD_OFFSET_KIND, (byte) (value & 0xFF));
            return this;
        }
    }
}

