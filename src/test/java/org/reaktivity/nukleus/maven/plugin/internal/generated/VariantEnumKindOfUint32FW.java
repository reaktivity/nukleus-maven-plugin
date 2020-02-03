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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithUint32;

public final class VariantEnumKindOfUint32FW extends Flyweight
{
    public static final EnumWithUint32 KIND_ZERO = EnumWithUint32.ICHI;

    private static final int FIELD_VALUE_ZERO = 0;

    public static final EnumWithUint32 KIND_UINT32 = EnumWithUint32.NI;

    private static final int FIELD_SIZE_UINT32 = BitUtil.SIZE_OF_INT;

    public static final EnumWithUint32 KIND_ONE = EnumWithUint32.SAN;

    private static final int FIELD_VALUE_ONE = 1;

    private final EnumWithUint32FW enumWithUint32RO = new EnumWithUint32FW();

    public int getAsZero()
    {
        return FIELD_VALUE_ZERO;
    }

    public long getAsUint32()
    {
        return buffer().getInt(enumWithUint32RO.limit()) & 0xFFFF_FFFFL;
    }

    public int getAsOne()
    {
        return FIELD_VALUE_ONE;
    }

    public EnumWithUint32 kind()
    {
        return enumWithUint32RO.get();
    }

    public long get()
    {
        switch (kind())
        {
        case ICHI:
            return getAsZero();
        case NI:
            return getAsUint32();
        case SAN:
            return getAsOne();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantEnumKindOfUint32FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        EnumWithUint32FW enumWithUint32 = enumWithUint32RO.tryWrap(buffer, offset, maxLimit);
        if (enumWithUint32 == null)
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
    public VariantEnumKindOfUint32FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        EnumWithUint32FW enumWithUint32 = enumWithUint32RO.wrap(buffer, offset, maxLimit);
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
            return String.format("VARIANT_ENUM_KIND_OF_UINT32 [zero=%d]", getAsZero());
        case NI:
            return String.format("VARIANT_ENUM_KIND_OF_UINT32 [uint32=%d]", getAsUint32());
        case SAN:
            return String.format("VARIANT_ENUM_KIND_OF_UINT32 [one=%d]", getAsOne());
        default:
            return String.format("VARIANT_ENUM_KIND_OF_UINT32 [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ICHI:
            return enumWithUint32RO.limit();
        case NI:
            return enumWithUint32RO.limit() + FIELD_SIZE_UINT32;
        case SAN:
            return enumWithUint32RO.limit();
        default:
            return enumWithUint32RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantEnumKindOfUint32FW>
    {
        private final EnumWithUint32FW.Builder enumWithUint32RW = new EnumWithUint32FW.Builder();

        public Builder()
        {
            super(new VariantEnumKindOfUint32FW());
        }

        public Builder setAsZero()
        {
            kind(KIND_ZERO);
            return this;
        }

        public Builder setAsUint32(
            long value)
        {
            kind(KIND_UINT32);
            int newLimit = limit() + FIELD_SIZE_UINT32;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            limit(newLimit);
            return this;
        }

        public Builder setAsOne()
        {
            kind(KIND_ONE);
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
                    setAsUint32((int) value);
                    break;
                }
                break;
            case 1:
            case 2:
            case 3:
                setAsUint32(value);
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
            EnumWithUint32 value)
        {
            enumWithUint32RW.wrap(buffer(), offset(), maxLimit());
            enumWithUint32RW.set(value);
            limit(enumWithUint32RW.build().limit());
            return this;
        }
    }
}
