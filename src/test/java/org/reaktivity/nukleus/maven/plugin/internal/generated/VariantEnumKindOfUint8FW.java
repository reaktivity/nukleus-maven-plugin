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

public final class VariantEnumKindOfUint8FW extends Flyweight
{
    public static final EnumWithUint8 KIND_UINT8 = EnumWithUint8.ICHI;

    private static final int FIELD_SIZE_UINT8 = BitUtil.SIZE_OF_BYTE;

    public static final EnumWithUint8 KIND_ZERO = EnumWithUint8.NI;

    private static final int FIELD_VALUE_ZERO = 0;

    public static final EnumWithUint8 KIND_ONE = EnumWithUint8.SAN;

    private static final int FIELD_VALUE_ONE = 1;

    private final EnumWithUint8FW enumWithUint8RO = new EnumWithUint8FW();

    public int getAsUint8()
    {
        return buffer().getByte(enumWithUint8RO.limit()) & 0xFF;
    }

    public int getAsZero()
    {
        return FIELD_VALUE_ZERO;
    }

    public int getAsOne()
    {
        return FIELD_VALUE_ONE;
    }

    public EnumWithUint8 kind()
    {
        return enumWithUint8RO.get();
    }

    public int get()
    {
        switch (kind())
        {
        case ICHI:
            return getAsUint8();
        case NI:
            return getAsZero();
        case SAN:
            return getAsOne();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantEnumKindOfUint8FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        EnumWithUint8FW enumWithUint8 = enumWithUint8RO.tryWrap(buffer, offset, maxLimit);
        if (enumWithUint8 == null)
        {
            return null;
        }
        switch (kind())
        {
        case ICHI:
            break;
        case NI:
            break;
        case SAN:
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
    public VariantEnumKindOfUint8FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        EnumWithUint8FW enumWithUint8 = enumWithUint8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ICHI:
            break;
        case NI:
            break;
        case SAN:
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
        case ICHI:
            return String.format("VARIANT_ENUM_KIND_OF_UINT8 [uint8=%d]", getAsUint8());
        case NI:
            return String.format("VARIANT_ENUM_KIND_OF_UINT8 [zero=%d]", getAsZero());
        case SAN:
            return String.format("VARIANT_ENUM_KIND_OF_UINT8 [one=%d]", getAsOne());
        default:
            return String.format("VARIANT_ENUM_KIND_OF_UINT8 [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ICHI:
            return enumWithUint8RO.limit() + FIELD_SIZE_UINT8;
        case NI:
            return enumWithUint8RO.limit();
        case SAN:
            return enumWithUint8RO.limit();
        default:
            return enumWithUint8RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantEnumKindOfUint8FW>
    {
        private final EnumWithUint8FW.Builder enumWithUint8RW = new EnumWithUint8FW.Builder();

        public Builder()
        {
            super(new VariantEnumKindOfUint8FW());
        }

        public Builder setAsUint8(
            int value)
        {
            kind(KIND_UINT8);
            int newLimit = limit() + FIELD_SIZE_UINT8;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(limit(), (byte) (value & 0xFF));
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

        public Builder set(
            int value)
        {
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(value)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
                switch (value)
                {
                case 1:
                    setAsOne();
                    break;
                default:
                    setAsUint8(value);
                    break;
                }
                break;
            case 4:
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
            EnumWithUint8 value)
        {
            enumWithUint8RW.wrap(buffer(), offset(), maxLimit());
            enumWithUint8RW.set(value);
            limit(enumWithUint8RW.build().limit());
            return this;
        }
    }
}
