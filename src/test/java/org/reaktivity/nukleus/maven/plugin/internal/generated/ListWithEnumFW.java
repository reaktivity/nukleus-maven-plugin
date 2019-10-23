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

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt64;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt64FW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithString;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithStringFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithUint16;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithUint16FW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithUint32;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithUint32FW;
import org.reaktivity.reaktor.internal.test.types.inner.Roll;
import org.reaktivity.reaktor.internal.test.types.inner.RollFW;

public final class ListWithEnumFW extends Flyweight
{
    private static final int PHYSICAL_LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int LOGICAL_LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int BIT_MASK_SIZE = BitUtil.SIZE_OF_LONG;

    private static final int PHYSICAL_LENGTH_OFFSET = 0;

    private static final int LOGICAL_LENGTH_OFFSET = PHYSICAL_LENGTH_OFFSET + PHYSICAL_LENGTH_SIZE;

    private static final int BIT_MASK_OFFSET = LOGICAL_LENGTH_OFFSET + LOGICAL_LENGTH_SIZE;

    private static final int FIRST_FIELD_OFFSET = BIT_MASK_OFFSET + BIT_MASK_SIZE;

    private static final int FIELD_INDEX_ROLL = 0;

    private static final int FIELD_INDEX_ENUM_WITH_INT8 = 1;

    private static final int FIELD_INDEX_ENUM_WITH_INT64 = 2;

    private static final int FIELD_INDEX_ENUM_WITH_UINT16 = 3;

    private static final int FIELD_INDEX_ENUM_WITH_UINT32 = 4;

    private static final int FIELD_INDEX_ENUM_WITH_STRING = 5;

    private final RollFW rollRO = new RollFW();

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final EnumWithInt64FW enumWithInt64RO = new EnumWithInt64FW();

    private final EnumWithUint16FW enumWithUint16RO = new EnumWithUint16FW();

    private final EnumWithUint32FW enumWithUint32RO = new EnumWithUint32FW();

    private final EnumWithStringFW enumWithStringRO = new EnumWithStringFW();

    private final int[] optionalOffsets = new int[FIELD_INDEX_ENUM_WITH_STRING + 1];

    public int length()
    {
        return buffer().getByte(offset() + LOGICAL_LENGTH_OFFSET);
    }

    private long bitmask()
    {
        return buffer().getLong(offset() + BIT_MASK_OFFSET);
    }

    public Roll roll()
    {
        return rollRO.get();
    }

    public EnumWithInt8 enumWithInt8()
    {
        assert (bitmask() & (1 << FIELD_INDEX_ENUM_WITH_INT8)) != 0 : "Field \"enumWithInt8\" is not set";
        return enumWithInt8RO.get();
    }

    public EnumWithInt64 enumWithInt64()
    {
        return (bitmask() & (1 << FIELD_INDEX_ENUM_WITH_INT64)) == 0 ? EnumWithInt64.TEN : enumWithInt64RO.get();
    }

    public EnumWithUint16 enumWithUint16()
    {
        return enumWithUint16RO.get();
    }

    public EnumWithUint32 enumWithUint32()
    {
        return (bitmask() & (1 << FIELD_INDEX_ENUM_WITH_UINT32)) == 0 ? EnumWithUint32.SAN : enumWithUint32RO.get();
    }

    public EnumWithString enumWithString()
    {
        assert (bitmask() & (1 << FIELD_INDEX_ENUM_WITH_STRING)) != 0 : "Field \"enumWithString\" is not set";
        return enumWithStringRO.get();
    }

    @Override
    public ListWithEnumFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        final long bitmask = bitmask();
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        for (int field = FIELD_INDEX_ROLL; field < FIELD_INDEX_ENUM_WITH_STRING + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_ROLL:
                if ((bitmask & (1 << FIELD_INDEX_ROLL)) == 0)
                {
                    throw new IllegalArgumentException("Field \"roll\" is required but not set");
                }
                rollRO.wrap(buffer, fieldLimit, maxLimit);
                fieldLimit = rollRO.limit();
                break;
            case FIELD_INDEX_ENUM_WITH_INT8:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_INT8)) != 0)
                {
                    enumWithInt8RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = enumWithInt8RO.limit();
                }
                break;
            case FIELD_INDEX_ENUM_WITH_INT64:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_INT64)) != 0)
                {
                    enumWithInt64RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = enumWithInt64RO.limit();
                }
                break;
            case FIELD_INDEX_ENUM_WITH_UINT16:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_UINT16)) == 0)
                {
                    throw new IllegalArgumentException("Field \"enumWithUint16\" is required but not set");
                }
                enumWithUint16RO.wrap(buffer, fieldLimit, maxLimit);
                fieldLimit = enumWithUint16RO.limit();
                break;
            case FIELD_INDEX_ENUM_WITH_UINT32:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_UINT32)) != 0)
                {
                    enumWithUint32RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = enumWithUint32RO.limit();
                }
                break;
            case FIELD_INDEX_ENUM_WITH_STRING:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_STRING)) != 0)
                {
                    enumWithStringRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = enumWithStringRO.limit();
                }
                break;
            }
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public ListWithEnumFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        final long bitmask = bitmask();
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        for (int field = FIELD_INDEX_ROLL; field < FIELD_INDEX_ENUM_WITH_STRING + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_ROLL:
                if ((bitmask & (1 << FIELD_INDEX_ROLL)) == 0)
                {
                    return null;
                }
                final RollFW roll = rollRO.tryWrap(buffer, fieldLimit, maxLimit);
                if (roll == null)
                {
                    return null;
                }
                fieldLimit = roll.limit();
                break;
            case FIELD_INDEX_ENUM_WITH_INT8:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_INT8)) != 0)
                {
                    final EnumWithInt8FW enumWithInt8 = enumWithInt8RO.tryWrap(buffer, fieldLimit, maxLimit);
                    if (enumWithInt8 == null)
                    {
                        return null;
                    }
                    fieldLimit = enumWithInt8.limit();
                }
                break;
            case FIELD_INDEX_ENUM_WITH_INT64:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_INT64)) != 0)
                {
                    final EnumWithInt64FW enumWithInt64 = enumWithInt64RO.tryWrap(buffer, fieldLimit, maxLimit);
                    if (enumWithInt64 == null)
                    {
                        return null;
                    }
                    fieldLimit = enumWithInt64.limit();
                }
                break;
            case FIELD_INDEX_ENUM_WITH_UINT16:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_UINT16)) == 0)
                {
                    return null;
                }
                final EnumWithUint16FW enumWithUint16 = enumWithUint16RO.tryWrap(buffer, fieldLimit, maxLimit);
                if (enumWithUint16 == null)
                {
                    return null;
                }
                fieldLimit = enumWithUint16.limit();
                break;
            case FIELD_INDEX_ENUM_WITH_UINT32:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_UINT32)) != 0)
                {
                    final EnumWithUint32FW enumWithUint32 = enumWithUint32RO.tryWrap(buffer, fieldLimit, maxLimit);
                    if (enumWithUint32 == null)
                    {
                        return null;
                    }
                    fieldLimit = enumWithUint32.limit();
                }
                break;
            case FIELD_INDEX_ENUM_WITH_STRING:
                if ((bitmask & (1 << FIELD_INDEX_ENUM_WITH_STRING)) != 0)
                {
                    final EnumWithStringFW enumWithString = enumWithStringRO.tryWrap(buffer, fieldLimit, maxLimit);
                    if (enumWithString == null)
                    {
                        return null;
                    }
                    fieldLimit = enumWithString.limit();
                }
                break;
            }
        }
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public int limit()
    {
        return offset() + buffer().getByte(offset() + PHYSICAL_LENGTH_OFFSET);
    }

    @Override
    public String toString()
    {
        final long bitmask = bitmask() | (1 << FIELD_INDEX_ENUM_WITH_INT64) | (1 << FIELD_INDEX_ENUM_WITH_UINT32);
        boolean enumWithInt8IsSet = (bitmask & (1 << FIELD_INDEX_ENUM_WITH_INT8)) != 0;
        boolean enumWithStringIsSet = (bitmask & (1 << FIELD_INDEX_ENUM_WITH_STRING)) != 0;
        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_ENUM [bitmask={0}");
        format.append(", roll={1}");
        if (enumWithInt8IsSet)
        {
            format.append(", enumWithInt8={2}");
        }
        format.append(", enumWithInt64={3}");
        format.append(", enumWithUint16={4}");
        format.append(", enumWithUint32={5}");
        if (enumWithStringIsSet)
        {
            format.append(", enumWithString={6}");
        }
        format.append("]");
        return MessageFormat.format(format.toString(),
            String.format("0x%02X", bitmask),
            roll(),
            enumWithInt8IsSet ? enumWithInt8() : null,
            enumWithInt64(),
            enumWithUint16(),
            enumWithUint32(),
            enumWithStringIsSet ? enumWithString() : null);
    }

    public static final class Builder extends Flyweight.Builder<ListWithEnumFW>
    {
        private final RollFW.Builder rollRW = new RollFW.Builder();

        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final EnumWithInt64FW.Builder enumWithInt64RW = new EnumWithInt64FW.Builder();

        private final EnumWithUint16FW.Builder enumWithUint16RW = new EnumWithUint16FW.Builder();

        private final EnumWithUint32FW.Builder enumWithUint32RW = new EnumWithUint32FW.Builder();

        private final EnumWithStringFW.Builder enumWithStringRW = new EnumWithStringFW.Builder();

        private long fieldsMask;

        public Builder()
        {
            super(new ListWithEnumFW());
        }

        public Builder roll(
            Roll value)
        {
            assert (fieldsMask & ~0x00) == 0 : "Field \"roll\" cannot be set out of order";
            RollFW.Builder rollRW = this.rollRW.wrap(buffer(), limit(), maxLimit());
            rollRW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ROLL;
            limit(rollRW.build().limit());
            return this;
        }

        public Builder roll(
            RollFW value)
        {
            assert (fieldsMask & ~0x00) == 0 : "Field \"roll\" cannot be set out of order";
            RollFW.Builder rollRW = this.rollRW.wrap(buffer(), limit(), maxLimit());
            rollRW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ROLL;
            limit(rollRW.build().limit());
            return this;
        }

        public Builder enumWithInt8(
            EnumWithInt8 value)
        {
            assert (fieldsMask & ~0x01) == 0 : "Field \"enumWithInt8\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"roll\" is not set";
            EnumWithInt8FW.Builder enumWithInt8RW = this.enumWithInt8RW.wrap(buffer(), limit(), maxLimit());
            enumWithInt8RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_INT8;
            limit(enumWithInt8RW.build().limit());
            return this;
        }

        public Builder enumWithInt8(
            EnumWithInt8FW value)
        {
            assert (fieldsMask & ~0x01) == 0 : "Field \"enumWithInt8\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"roll\" is not set";
            EnumWithInt8FW.Builder enumWithInt8RW = this.enumWithInt8RW.wrap(buffer(), limit(), maxLimit());
            enumWithInt8RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_INT8;
            limit(enumWithInt8RW.build().limit());
            return this;
        }

        public Builder enumWithInt64(
            EnumWithInt64 value)
        {
            assert (fieldsMask & ~0x03) == 0 : "Field \"enumWithInt64\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"roll\" is not set";
            EnumWithInt64FW.Builder enumWithInt64RW = this.enumWithInt64RW.wrap(buffer(), limit(), maxLimit());
            enumWithInt64RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_INT64;
            limit(enumWithInt64RW.build().limit());
            return this;
        }

        public Builder enumWithInt64(
            EnumWithInt64FW value)
        {
            assert (fieldsMask & ~0x03) == 0 : "Field \"enumWithInt64\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"roll\" is not set";
            EnumWithInt64FW.Builder enumWithInt64RW = this.enumWithInt64RW.wrap(buffer(), limit(), maxLimit());
            enumWithInt64RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_INT64;
            limit(enumWithInt64RW.build().limit());
            return this;
        }

        public Builder enumWithUint16(
            EnumWithUint16 value)
        {
            assert (fieldsMask & ~0x07) == 0 : "Field \"enumWithUint16\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"roll\" is not set";
            EnumWithUint16FW.Builder enumWithUint16RW = this.enumWithUint16RW.wrap(buffer(), limit(), maxLimit());
            enumWithUint16RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_UINT16;
            limit(enumWithUint16RW.build().limit());
            return this;
        }

        public Builder enumWithUint16(
            EnumWithUint16FW value)
        {
            assert (fieldsMask & ~0x07) == 0 : "Field \"enumWithUint16\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"roll\" is not set";
            EnumWithUint16FW.Builder enumWithUint16RW = this.enumWithUint16RW.wrap(buffer(), limit(), maxLimit());
            enumWithUint16RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_UINT16;
            limit(enumWithUint16RW.build().limit());
            return this;
        }

        public Builder enumWithUint32(
            EnumWithUint32 value)
        {
            assert (fieldsMask & ~0x0F) == 0 : "Field \"enumWithUint32\" cannot be set out of order";
            assert (fieldsMask & 0x08) != 0 : "Prior required field \"enumWithUint16\" is not set";
            EnumWithUint32FW.Builder enumWithUint32RW = this.enumWithUint32RW.wrap(buffer(), limit(), maxLimit());
            enumWithUint32RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_UINT32;
            limit(enumWithUint32RW.build().limit());
            return this;
        }

        public Builder enumWithUint32(
            EnumWithUint32FW value)
        {
            assert (fieldsMask & ~0x0F) == 0 : "Field \"enumWithUint32\" cannot be set out of order";
            assert (fieldsMask & 0x08) != 0 : "Prior required field \"enumWithUint16\" is not set";
            EnumWithUint32FW.Builder enumWithUint32RW = this.enumWithUint32RW.wrap(buffer(), limit(), maxLimit());
            enumWithUint32RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_UINT32;
            limit(enumWithUint32RW.build().limit());
            return this;
        }

        public Builder enumWithString(
            EnumWithString value)
        {
            assert (fieldsMask & ~0x1F) == 0 : "Field \"enumWithString\" cannot be set out of order";
            assert (fieldsMask & 0x08) != 0 : "Prior required field \"enumWithUint16\" is not set";
            EnumWithStringFW.Builder enumWithStringRW = this.enumWithStringRW.wrap(buffer(), limit(), maxLimit());
            enumWithStringRW.set(value, StandardCharsets.UTF_8);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_STRING;
            limit(enumWithStringRW.build().limit());
            return this;
        }

        public Builder enumWithString(
            EnumWithStringFW value)
        {
            assert (fieldsMask & ~0x1F) == 0 : "Field \"enumWithString\" cannot be set out of order";
            assert (fieldsMask & 0x08) != 0 : "Prior required field \"enumWithUint16\" is not set";
            EnumWithStringFW.Builder enumWithStringRW = this.enumWithStringRW.wrap(buffer(), limit(), maxLimit());
            enumWithStringRW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_ENUM_WITH_STRING;
            limit(enumWithStringRW.build().limit());
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            fieldsMask = 0;
            int newLimit = limit() + FIRST_FIELD_OFFSET;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            return this;
        }

        @Override
        public ListWithEnumFW build()
        {
            assert (fieldsMask & 0x01) != 0 : "Required field \"roll\" is not set";
            assert (fieldsMask & 0x08) != 0 : "Required field \"enumWithUint16\" is not set";
            buffer().putByte(offset() + PHYSICAL_LENGTH_OFFSET, (byte) (limit() - offset()));
            buffer().putByte(offset() + LOGICAL_LENGTH_OFFSET, (byte) (Long.bitCount(fieldsMask)));
            buffer().putLong(offset() + BIT_MASK_OFFSET, fieldsMask);
            return super.build();
        }
    }
}
