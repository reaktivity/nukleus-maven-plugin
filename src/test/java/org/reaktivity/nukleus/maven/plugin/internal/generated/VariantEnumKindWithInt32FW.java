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

public class VariantEnumKindWithInt32FW extends Flyweight
{
    private static final int FIELD_SIZE_INT8 = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_SIZE_INT16 = BitUtil.SIZE_OF_SHORT;

    private static final int FIELD_SIZE_INT32 = BitUtil.SIZE_OF_INT;

    public static final EnumWithInt8 KIND_INT8 = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_INT16 = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_INT32 = EnumWithInt8.THREE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public byte getAsInt8()
    {
        return buffer().getByte(enumWithInt8RO.limit());
    }

    public short getAsInt16()
    {
        return buffer().getShort(enumWithInt8RO.limit());
    }

    public int getAsInt32()
    {
        return buffer().getInt(enumWithInt8RO.limit());
    }

    public int get()
    {
        switch (kind())
        {
        case ONE:
            return getAsInt8();
        case TWO:
            return getAsInt16();
        case THREE:
            return getAsInt32();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantEnumKindWithInt32FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        enumWithInt8RO.tryWrap(buffer, offset, maxLimit);
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
    public VariantEnumKindWithInt32FW wrap(
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
            return String.format("VARIANTENUMKINDWITHINT32 [int8=%d]", getAsInt8());
        case TWO:
            return String.format("VARIANTENUMKINDWITHINT32 [int16=%d]", getAsInt16());
        case THREE:
            return String.format("VARIANTENUMKINDWITHINT32 [int32=%d]", getAsInt32());
        default:
            return String.format("VARIANTUINT8KINDWITHINT64TYPE [unknown]");
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
            return enumWithInt8RO.limit() + FIELD_SIZE_INT16;
        case THREE:
            return enumWithInt8RO.limit() + FIELD_SIZE_INT32;
        default:
            return enumWithInt8RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantEnumKindWithInt32FW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        public Builder()
        {
            super(new VariantEnumKindWithInt32FW());
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
            byte value)
        {
            kind(KIND_INT8);
            int newLimit = limit() + FIELD_SIZE_INT8;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(limit(), value);
            limit(newLimit);
            return this;
        }

        public Builder setAsInt16(
            short value)
        {
            kind(KIND_INT16);
            int newLimit = limit() + FIELD_SIZE_INT16;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(limit(), value);
            limit(newLimit);
            return this;
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

        public Builder set(
            int value)
        {
            int highestOneBitPosition = Long.numberOfTrailingZeros(Long.highestOneBit(value));

            switch (highestOneBitPosition)
            {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                setAsInt8((byte) value);
                break;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                setAsInt16((short) value);
                break;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
                setAsInt32((int) value);
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