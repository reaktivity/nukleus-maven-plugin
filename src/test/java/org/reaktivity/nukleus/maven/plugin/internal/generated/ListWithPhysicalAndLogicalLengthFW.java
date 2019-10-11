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
import org.reaktivity.reaktor.internal.test.types.StringFW;

public class ListWithPhysicalAndLogicalLengthFW extends Flyweight
{
    public static final int PHYSICAL_LENGTH_OFFSET = 0;

    private static final int PHYSICAL_LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    public static final int LOGICAL_LENGTH_OFFSET = PHYSICAL_LENGTH_OFFSET + PHYSICAL_LENGTH_SIZE;

    private static final int LOGICAL_LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    public static final int BIT_MASK_OFFSET = LOGICAL_LENGTH_OFFSET + LOGICAL_LENGTH_SIZE;

    private static final int BIT_MASK_SIZE = BitUtil.SIZE_OF_LONG;

    public static final int FIRST_FIELD_OFFSET = BIT_MASK_OFFSET + BIT_MASK_SIZE;

    private static final int FIELD_SIZE_FIELD1 = BitUtil.SIZE_OF_INT;

    private static final int FIELD_INDEX_FIELD0 = 0;

    private static final int FIELD_INDEX_FIELD1 = 1;

    private static final int FIELD_INDEX_FIELD2 = 2;

    private final StringFW field0RO = new StringFW();

    private final StringFW field2RO = new StringFW();

    private final int[] optionalOffsets = new int[FIELD_INDEX_FIELD2 + 1];

    public int length()
    {
        return buffer().getInt(offset() + LOGICAL_LENGTH_OFFSET);
    }

    private long bitmask()
    {
        return buffer().getLong(offset() + BIT_MASK_OFFSET);
    }

    public StringFW field0()
    {
        return field0RO;
    }

    public long field1()
    {
        assert (bitmask() & (1 << FIELD_INDEX_FIELD1)) != 0 : "Field \"field1\" is not set";
        return buffer().getInt(optionalOffsets[FIELD_INDEX_FIELD1]) & 0xFFFF_FFFFL;
    }

    public StringFW field2()
    {
        assert (bitmask() & (1 << FIELD_INDEX_FIELD2)) != 0 : "Field \"field2\" is not set";
        return field2RO;
    }

    @Override
    public ListWithPhysicalAndLogicalLengthFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        final long bitmask = bitmask();
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        for (int field = FIELD_INDEX_FIELD0; field < FIELD_INDEX_FIELD2 + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_FIELD0:
                if ((bitmask & (1 << FIELD_INDEX_FIELD0)) == 0)
                {
                    throw new IllegalArgumentException("Field \"field0\" is required but not set");
                }
                field0RO.wrap(buffer, fieldLimit, maxLimit);
                fieldLimit = field0RO.limit();
                break;
            case FIELD_INDEX_FIELD1:
                if ((bitmask & (1 << FIELD_INDEX_FIELD1)) != 0)
                {
                    optionalOffsets[FIELD_INDEX_FIELD1] = fieldLimit;
                    fieldLimit += FIELD_SIZE_FIELD1;
                }
                break;
            case FIELD_INDEX_FIELD2:
                if ((bitmask & (1 << FIELD_INDEX_FIELD2)) != 0)
                {
                    field2RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = field2RO.limit();
                }
                break;
            }
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public ListWithPhysicalAndLogicalLengthFW tryWrap(
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
        for (int field = FIELD_INDEX_FIELD0; field < FIELD_INDEX_FIELD2 + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_FIELD0:
                if ((bitmask & (1 << FIELD_INDEX_FIELD0)) == 0)
                {
                    return null;
                }
                final StringFW field0 = field0RO.tryWrap(buffer, fieldLimit, maxLimit);
                if (field0 == null)
                {
                    return null;
                }
                fieldLimit = field0.limit();
                break;
            case FIELD_INDEX_FIELD1:
                if ((bitmask & (1 << FIELD_INDEX_FIELD1)) != 0)
                {
                    optionalOffsets[FIELD_INDEX_FIELD1] = fieldLimit;
                    fieldLimit += FIELD_SIZE_FIELD1;
                }
                break;
            case FIELD_INDEX_FIELD2:
                if ((bitmask & (1 << FIELD_INDEX_FIELD2)) != 0)
                {
                    final StringFW field2 = field2RO.tryWrap(buffer, fieldLimit, maxLimit);
                    if (field2 == null)
                    {
                        return null;
                    }
                    fieldLimit = field2RO.limit();
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
        return offset() + buffer().getInt(offset() + PHYSICAL_LENGTH_OFFSET);
    }

    @Override
    public String toString()
    {
        final long bitmask = bitmask();
        boolean fixed1IsSet = (bitmask & (1 << FIELD_INDEX_FIELD1)) != 0;
        boolean field2IsSet = (bitmask & (1 << FIELD_INDEX_FIELD2)) != 0;
        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_PHYSICAL_AND_LOGICAL_LENGTH [bitmask={0}");
        format.append(", field0={1}");
        if (fixed1IsSet)
        {
            format.append(", field1={2}");
        }
        if (field2IsSet)
        {
            format.append(", field1={3}");
        }
        format.append("]");
        return MessageFormat.format(format.toString(),
            String.format("0x%02X", bitmask),
            field0(),
            fixed1IsSet ? field1() : null,
            field2IsSet ? field2() : null);
    }

    public static final class Builder extends Flyweight.Builder<ListWithPhysicalAndLogicalLengthFW>
    {
        private final StringFW.Builder field0RW = new StringFW.Builder();

        private final StringFW.Builder field2RW = new StringFW.Builder();

        private long fieldsMask;

        public Builder()
        {
            super(new ListWithPhysicalAndLogicalLengthFW());
        }

        private StringFW.Builder field0()
        {
            return field0RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder field0(
            String value)
        {
            assert (fieldsMask & ~0x00) == 0 : "Field \"field0\" cannot be set out of order";
            StringFW.Builder field0RW = field0();
            field0RW.set(value, StandardCharsets.UTF_8);
            fieldsMask |= 1 << FIELD_INDEX_FIELD0;
            limit(field0RW.build().limit());
            return this;
        }

        public Builder field0(
            StringFW value)
        {
            assert (fieldsMask & ~0x00) == 0 : "Field \"field0\" cannot be set out of order";
            StringFW.Builder field0RW = field0();
            field0RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_FIELD0;
            limit(field0RW.build().limit());
            return this;
        }

        public Builder field0(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            assert (fieldsMask & ~0x00) == 0 : "Field \"field0\" cannot be set out of order";
            StringFW.Builder field0RW = field0();
            field0RW.set(buffer, offset, length);
            fieldsMask |= 1 << FIELD_INDEX_FIELD0;
            limit(field0RW.build().limit());
            return this;
        }

        public Builder field1(
            long value)
        {
            assert (fieldsMask & ~0x01) == 0 : "Field \"field1\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"field0\" is not set";
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"field1\"", value));
            }
            assert (value & 0xffff_ffff_0000_0000L) == 0L : "Value out of range for field \"field1\"";
            int newLimit = limit() + FIELD_SIZE_FIELD1;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            fieldsMask |= 1 << FIELD_INDEX_FIELD1;
            limit(newLimit);
            return this;
        }

        private StringFW.Builder field2()
        {
            return field2RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder field2(
            String value)
        {
            assert (fieldsMask & ~0x03) == 0 : "Field \"field2\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"field0\" is not set";
            StringFW.Builder field2RW = field2();
            field2RW.set(value, StandardCharsets.UTF_8);
            fieldsMask |= 1 << FIELD_INDEX_FIELD2;
            limit(field2RW.build().limit());
            return this;
        }

        public Builder field2(
            StringFW value)
        {
            assert (fieldsMask & ~0x03) == 0 : "Field \"field2\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"field0\" is not set";
            StringFW.Builder field2RW = field2();
            field2RW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_FIELD2;
            limit(field2RW.build().limit());
            return this;
        }

        public Builder field2(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            assert (fieldsMask & ~0x03) == 0 : "Field \"field2\" cannot be set out of order";
            assert (fieldsMask & 0x01) != 0 : "Prior required field \"field0\" is not set";
            StringFW.Builder field2RW = field2();
            field2RW.set(buffer, offset, length);
            fieldsMask |= 1 << FIELD_INDEX_FIELD2;
            limit(field2RW.build().limit());
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
        public ListWithPhysicalAndLogicalLengthFW build()
        {
            assert (fieldsMask & 0x01) != 0 : "Required field \"field0\" is not set";
            buffer().putInt(offset() + PHYSICAL_LENGTH_OFFSET, limit() - offset());
            buffer().putInt(offset() + LOGICAL_LENGTH_OFFSET, Long.bitCount(fieldsMask));
            buffer().putLong(offset() + BIT_MASK_OFFSET, fieldsMask);
            return super.build();
        }
    }
}
