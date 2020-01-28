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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithUint16;

public final class VariantEnumKindOfUint16FW extends Flyweight
{
    public static final EnumWithUint16 KIND_UINT16 = EnumWithUint16.ICHI;

    private static final int FIELD_SIZE_UINT16 = BitUtil.SIZE_OF_SHORT;

    public static final EnumWithUint16 KIND_UINT8 = EnumWithUint16.NI;

    private static final int FIELD_SIZE_UINT8 = BitUtil.SIZE_OF_BYTE;

    public static final EnumWithUint16 KIND_ZERO = EnumWithUint16.SAN;

    private static final int FIELD_VALUE_ZERO = 0;

    private final EnumWithUint16FW enumWithUint16RO = new EnumWithUint16FW();

    public int getAsUint16()
    {
        return buffer().getShort(enumWithUint16RO.limit()) & 0xFFFF;
    }

    public int getAsUint8()
    {
        return buffer().getByte(enumWithUint16RO.limit()) & 0xFF;
    }

    public int getAsZero()
    {
        return FIELD_VALUE_ZERO;
    }

    public EnumWithUint16 kind()
    {
        return enumWithUint16RO.get();
    }

    public int get()
    {
        switch (kind())
        {
        case ICHI:
            return getAsUint16();
        case NI:
            return getAsUint8();
        case SAN:
            return getAsZero();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantEnumKindOfUint16FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        EnumWithUint16FW enumWithUint16 = enumWithUint16RO.tryWrap(buffer, offset, maxLimit);
        if (enumWithUint16 == null)
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
    public VariantEnumKindOfUint16FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        EnumWithUint16FW enumWithUint16 = enumWithUint16RO.wrap(buffer, offset, maxLimit);
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
            return String.format("VARIANT_ENUM_KIND_OF_UINT16 [uint16=%d]", getAsUint16());
        case NI:
            return String.format("VARIANT_ENUM_KIND_OF_UINT16 [uint8=%d]", getAsUint8());
        case SAN:
            return String.format("VARIANT_ENUM_KIND_OF_UINT16 [zero=%d]", getAsZero());
        default:
            return String.format("VARIANT_ENUM_KIND_OF_UINT16 [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ICHI:
            return enumWithUint16RO.limit() + FIELD_SIZE_UINT16;
        case NI:
            return enumWithUint16RO.limit() + FIELD_SIZE_UINT8;
        case SAN:
            return enumWithUint16RO.limit();
        default:
            return enumWithUint16RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantEnumKindOfUint16FW>
    {
        private final EnumWithUint16FW.Builder enumWithUint16RW = new EnumWithUint16FW.Builder();

        public Builder()
        {
            super(new VariantEnumKindOfUint16FW());
        }

        public Builder setAsUint16(
            int value)
        {
            kind(KIND_UINT16);
            int newLimit = limit() + FIELD_SIZE_UINT16;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(limit(), (short) (value & 0xFFFF));
            limit(newLimit);
            return this;
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

        public Builder set(
            int value)
        {
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(value)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
                setAsUint8(value);
                break;
            case 1:
                setAsUint16(value);
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
            EnumWithUint16 value)
        {
            enumWithUint16RW.wrap(buffer(), offset(), maxLimit());
            enumWithUint16RW.set(value);
            limit(enumWithUint16RW.build().limit());
            return this;
        }
    }
}
