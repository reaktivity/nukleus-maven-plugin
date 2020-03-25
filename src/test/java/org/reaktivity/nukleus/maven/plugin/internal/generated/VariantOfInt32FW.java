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

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantOfInt32FW extends Flyweight
{
    public static final EnumWithInt8 KIND_INT32 = EnumWithInt8.FOUR;

    private static final int FIELD_SIZE_INT32 = BitUtil.SIZE_OF_INT;

    public static final EnumWithInt8 KIND_INT8 = EnumWithInt8.FIVE;

    private static final int FIELD_SIZE_INT8 = BitUtil.SIZE_OF_BYTE;

    private static final int BIT_MASK_INT8 = -128;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    public int getAsInt32()
    {
        return buffer().getInt(enumWithInt8RO.limit());
    }

    public int getAsInt8()
    {
        return buffer().getByte(enumWithInt8RO.limit());
    }

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public int get()
    {
        switch (kind())
        {
        case FOUR:
            return getAsInt32();
        case FIVE:
            return getAsInt8();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantOfInt32FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.tryWrap(buffer, offset, maxLimit);
        if (enumWithInt8 == null)
        {
            return null;
        }
        switch (kind())
        {
        case FOUR:
            break;
        case FIVE:
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
    public VariantOfInt32FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case FOUR:
            break;
        case FIVE:
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
        case FOUR:
            return String.format("VARIANT_OF_INT32 [int32=%d]", getAsInt32());
        case FIVE:
            return String.format("VARIANT_OF_INT32 [int8=%d]", getAsInt8());
        default:
            return String.format("VARIANT_OF_INT32 [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case FOUR:
            return enumWithInt8RO.limit() + FIELD_SIZE_INT32;
        case FIVE:
            return enumWithInt8RO.limit() + FIELD_SIZE_INT8;
        default:
            return enumWithInt8RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantOfInt32FW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        public Builder()
        {
            super(new VariantOfInt32FW());
        }

        public Builder setAsInt32(
            int value)
        {
            kind(KIND_INT32);
            int newLimit = limit() + FIELD_SIZE_INT32;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), value);
            limit(newLimit);
            return this;
        }

        public Builder setAsInt8(
            int value)
        {
            kind(KIND_INT8);
            int newLimit = limit() + FIELD_SIZE_INT8;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(limit(), (byte) value);
            limit(newLimit);
            return this;
        }

        public Builder set(
            int value)
        {
            int highestByteIndex = (Integer.numberOfTrailingZeros(Integer.highestOneBit(value)) + 1) >> 3;
            switch (highestByteIndex)
            {
            case 0:
                setAsInt8(value);
                break;
            case 1:
            case 2:
            case 3:
                setAsInt32(value);
                break;
            case 4:
                if ((value & BIT_MASK_INT8) == BIT_MASK_INT8)
                {
                    setAsInt8(value);
                }
                else
                {
                    setAsInt32(value);
                }
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
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }
    }
}
