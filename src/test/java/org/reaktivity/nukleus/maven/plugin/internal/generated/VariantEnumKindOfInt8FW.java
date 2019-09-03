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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantEnumKindOfInt8FW extends Flyweight
{
    public static final EnumWithInt8 KIND_INT8 = EnumWithInt8.ONE;

    private static final int FIELD_SIZE_INT8 = BitUtil.SIZE_OF_BYTE;

    private static final int BIT_MASK_INT8 = -256;

    public static final EnumWithInt8 KIND_ZERO = EnumWithInt8.TWO;

    private static final int FIELD_VALUE_ZERO = 0;

    public static final EnumWithInt8 KIND_ONE = EnumWithInt8.THREE;

    private static final int FIELD_VALUE_ONE = 1;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    public int getAsInt8()
    {
        return buffer().getByte(enumWithInt8RO.limit());
    }

    public int getAsZero()
    {
        return FIELD_VALUE_ZERO;
    }

    public int getAsOne()
    {
        return FIELD_VALUE_ONE;
    }

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public int get()
    {
        switch (kind())
        {
        case ONE:
            return getAsInt8();
        case TWO:
            return getAsZero();
        case THREE:
            return getAsOne();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantEnumKindOfInt8FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        if (null == enumWithInt8RO.tryWrap(buffer, offset, maxLimit))
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
    public VariantEnumKindOfInt8FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        enumWithInt8RO.wrap(buffer, offset, maxLimit);
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
            return String.format("VARIANTENUMKINDOFINT8 [int8=%d]", getAsInt8());
        case TWO:
            return String.format("VARIANTENUMKINDOFINT8 [zero=%d]", getAsZero());
        case THREE:
            return String.format("VARIANTENUMKINDOFINT8 [one=%d]", getAsOne());
        default:
            return String.format("VARIANTENUMKINDOFINT8 [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ONE:
            return enumWithInt8RO.limit() + FIELD_SIZE_INT8;
        case TWO:
            return enumWithInt8RO.limit();
        case THREE:
            return enumWithInt8RO.limit();
        default:
            return enumWithInt8RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantEnumKindOfInt8FW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        public Builder()
        {
            super(new VariantEnumKindOfInt8FW());
        }

        private Builder kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
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

        public Builder setAsZero()
        {
            kind(KIND_ZERO);
            return this;
        }

        public Builder setAsOne()
        {
            kind(KIND_ONE);
            return this;
        }

        public Builder set(int value)
        {
            int highestByteIndex = (Integer.numberOfTrailingZeros(Integer.highestOneBit(value)) + 1)  >> 3;
            switch (highestByteIndex)
            {
            case 0:
                switch (value)
                {
                case 1:
                    setAsOne();
                    break;
                default:
                    setAsInt8(value);
                    break;
                }
                break;
            case 4:
                if (value == 0)
                {
                    setAsZero();
                }
                else
                {
                    setAsInt8(value);
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
