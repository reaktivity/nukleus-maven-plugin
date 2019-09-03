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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt16;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt16FW;

public final class VariantEnumKindOfInt16FW extends Flyweight
{
    public static final EnumWithInt16 KIND_ZERO = EnumWithInt16.ONE;

    private static final int FIELD_VALUE_ZERO = 0;

    public static final EnumWithInt16 KIND_INT8 = EnumWithInt16.TWO;

    private static final int FIELD_SIZE_INT8 = BitUtil.SIZE_OF_BYTE;

    private static final int BIT_MASK_INT8 = -256;

    public static final EnumWithInt16 KIND_INT16 = EnumWithInt16.THREE;

    private static final int FIELD_SIZE_INT16 = BitUtil.SIZE_OF_SHORT;

    private static final int BIT_MASK_INT16 = -65536;

    private final EnumWithInt16FW enumWithInt16RO = new EnumWithInt16FW();

    public int getAsZero()
    {
        return FIELD_VALUE_ZERO;
    }

    public int getAsInt8()
    {
        return buffer().getByte(enumWithInt16RO.limit());
    }

    public int getAsInt16()
    {
        return buffer().getShort(enumWithInt16RO.limit());
    }

    public EnumWithInt16 kind()
    {
        return enumWithInt16RO.get();
    }

    public int get()
    {
        switch (kind())
        {
        case ONE:
            return getAsZero();
        case TWO:
            return getAsInt8();
        case THREE:
            return getAsInt16();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantEnumKindOfInt16FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        if (null == enumWithInt16RO.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        switch (kind())
        {
        case ONE:
            break;
        case TWO:
            break;
        case THREE:
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
    public VariantEnumKindOfInt16FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        enumWithInt16RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            break;
        case TWO:
            break;
        case THREE:
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
        case ONE:
            return String.format("VARIANTENUMKINDOFINT16 [zero=%d]", getAsZero());
        case TWO:
            return String.format("VARIANTENUMKINDOFINT16 [int8=%d]", getAsInt8());
        case THREE:
            return String.format("VARIANTENUMKINDOFINT16 [int16=%d]", getAsInt16());
        default:
            return String.format("VARIANTENUMKINDOFINT16 [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ONE:
            return enumWithInt16RO.limit();
        case TWO:
            return enumWithInt16RO.limit() + FIELD_SIZE_INT8;
        case THREE:
            return enumWithInt16RO.limit() + FIELD_SIZE_INT16;
        default:
            return enumWithInt16RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantEnumKindOfInt16FW>
    {
        private final EnumWithInt16FW.Builder enumWithInt16RW = new EnumWithInt16FW.Builder();

        public Builder()
        {
            super(new VariantEnumKindOfInt16FW());
        }

        private Builder kind(
            EnumWithInt16 value)
        {
            enumWithInt16RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt16RW.set(value);
            limit(enumWithInt16RW.build().limit());
            return this;
        }

        public Builder setAsZero()
        {
            kind(KIND_ZERO);
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

        public Builder setAsInt16(
            int value)
        {
            kind(KIND_INT16);
            int newLimit = limit() + FIELD_SIZE_INT16;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(limit(), (short) value);
            limit(newLimit);
            return this;
        }

        public Builder set(
            int value)
        {
            int highestByteIndex = (Integer.numberOfTrailingZeros(Integer.highestOneBit(value)) + 1)  >> 3;
            switch (highestByteIndex)
            {
            case 0:
                setAsInt8(value);
                break;
            case 1:
                setAsInt16(value);
                break;
            case 4:
                if (value == 0)
                {
                    setAsZero();
                }
                else if ((value & BIT_MASK_INT8) == value)
                {
                    setAsInt8(value);
                }
                else
                {
                    setAsInt16(value);
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
