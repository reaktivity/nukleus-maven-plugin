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

public class VariantUint8KindWithInt64TypeFW extends Flyweight
{
    private static final int FIELD_SIZE_KIND = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_OFFSET_KIND = 0;

    public static final int KIND_INT32 = 113;

    private static final int FIELD_OFFSET_INT32 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_INT32 = BitUtil.SIZE_OF_INT;

    public static final int KIND_INT64 = 129;

    private static final int FIELD_OFFSET_INT64 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_INT64 = BitUtil.SIZE_OF_LONG;

    public static final int KIND_INT8 = 81;

    private static final int FIELD_OFFSET_INT8 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_INT8 = BitUtil.SIZE_OF_BYTE;

    public static final int KIND_INT16 = 97;

    private static final int FIELD_OFFSET_INT16 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_INT16 = BitUtil.SIZE_OF_SHORT;

    private static final long BIT_MASK_INT8 = 0xffffffffffffff00L;

    private static final long BIT_MASK_INT16 = 0xffffffffffff0000L;

    private static final long BIT_MASK_INT32 = 0xffffffff00000000L;


    public int getAsInt32()
    {
        return buffer().getInt(offset() + FIELD_OFFSET_INT32);
    }

    public long getAsInt64()
    {
        return buffer().getLong(offset() + FIELD_OFFSET_INT64);
    }

    public byte getAsInt8()
    {
        return buffer().getByte(offset() + FIELD_OFFSET_INT8);
    }

    public short getAsInt16()
    {
        return buffer().getShort(offset() + FIELD_OFFSET_INT16);
    }

    public int kind()
    {
        return buffer().getByte(offset() + FIELD_OFFSET_KIND) & 0xFF;
    }

    public long get()
    {
        switch (kind())
        {
        case KIND_INT32:
            return getAsInt32();
        case KIND_INT64:
            return getAsInt64();
        case KIND_INT8:
            return getAsInt8();
        case KIND_INT16:
            return getAsInt16();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantUint8KindWithInt64TypeFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case KIND_INT32:
            break;
        case KIND_INT64:
            break;
        case KIND_INT8:
            break;
        case KIND_INT16:
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
    public VariantUint8KindWithInt64TypeFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case KIND_INT32:
            break;
        case KIND_INT64:
            break;
        case KIND_INT8:
            break;
        case KIND_INT16:
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
        case KIND_INT32:
            return String.format("VARIANTUINT8KINDWITHINT64TYPE [int32=%d]", getAsInt32());
        case KIND_INT64:
            return String.format("VARIANTUINT8KINDWITHINT64TYPE [int64=%d]", getAsInt64());
        case KIND_INT8:
            return String.format("VARIANTUINT8KINDWITHINT64TYPE [int8=%d]", getAsInt8());
        case KIND_INT16:
            return String.format("VARIANTUINT8KINDWITHINT64TYPE [int16=%d]", getAsInt16());
        default:
            return String.format("VARIANTUINT8KINDWITHINT64TYPE [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case KIND_INT32:
            return offset() + FIELD_OFFSET_INT32 + FIELD_SIZE_INT32;
        case KIND_INT64:
            return offset() + FIELD_OFFSET_INT64 + FIELD_SIZE_INT64;
        case KIND_INT8:
            return offset() + FIELD_OFFSET_INT8 + FIELD_SIZE_INT8;
        case KIND_INT16:
            return offset() + FIELD_OFFSET_INT16 + FIELD_SIZE_INT16;
        default:
            return offset();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantUint8KindWithInt64TypeFW>
    {
        public Builder()
        {
            super(new VariantUint8KindWithInt64TypeFW());
        }

        private Builder kind(
            int value)
        {
            buffer().putByte(offset() + FIELD_OFFSET_KIND, (byte)(value & 0xFF));
            return this;
        }

        public Builder setAsInt32(
            int value)
        {
            int newLimit = offset() + FIELD_OFFSET_INT32 + FIELD_SIZE_INT32;
            checkLimit(newLimit, maxLimit());
            kind(KIND_INT32);
            buffer().putInt(offset() + FIELD_OFFSET_INT32, value);
            limit(newLimit);
            return this;
        }

        public Builder setAsInt64(
            long value)
        {
            int newLimit = offset() + FIELD_OFFSET_INT64 + FIELD_SIZE_INT64;
            checkLimit(newLimit, maxLimit());
            kind(KIND_INT64);
            buffer().putLong(offset() + FIELD_OFFSET_INT64, value);
            limit(newLimit);
            return this;
        }

        public Builder setAsInt8(
            byte value)
        {
            int newLimit = offset() + FIELD_OFFSET_INT8 + FIELD_SIZE_INT8;
            checkLimit(newLimit, maxLimit());
            kind(KIND_INT8);
            buffer().putByte(offset() + FIELD_OFFSET_INT8, value);
            limit(newLimit);
            return this;
        }

        public Builder setAsInt16(
            short value)
        {
            int newLimit = offset() + FIELD_OFFSET_INT16 + FIELD_SIZE_INT16;
            checkLimit(newLimit, maxLimit());
            kind(KIND_INT16);
            buffer().putShort(offset() + FIELD_OFFSET_INT16, value);
            limit(newLimit);
            return this;
        }

        public Builder set(
            long value)
        {
            int highestBitIndex = Long.numberOfTrailingZeros(Long.highestOneBit(value)) + 1;
            switch (highestBitIndex >> 3)
            {
            case 0:
                setAsInt8((byte) value);
                break;
            case 1:
                setAsInt16((short) value);
                break;
            case 2:
            case 3:
                setAsInt32((int) value);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                setAsInt64((long) value);
                break;
            case 8:
                if ((value & BIT_MASK_INT8) == value)
                {
                    setAsInt8((byte) value);
                }
                else if ((value & BIT_MASK_INT16) == value)
                {
                    setAsInt16((short) value);
                }
                else if ((value & BIT_MASK_INT32) == value)
                {
                    setAsInt32((int) value);
                }
                else
                {
                    setAsInt64((long) value);
                }
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
