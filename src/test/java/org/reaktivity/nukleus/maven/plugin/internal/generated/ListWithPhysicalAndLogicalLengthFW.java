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

    private static final int BIT_MASK_SIZE = BitUtil.SIZE_OF_INT;

    public static final int FIELD_OFFSET_FIELD0 = BIT_MASK_OFFSET + BIT_MASK_SIZE;

    private static final int FIELD_SIZE_FIELD1 = BitUtil.SIZE_OF_INT;

    private static final int FIELD_INDEX_FIELD0 = 0;

    private static final int FIELD_INDEX_FIELD1 = 1;

    private static final int FIELD_INDEX_FIELD2 = 2;

    private final StringFW field0RO = new StringFW();

    private final StringFW field2RO = new StringFW();

    private final int[] optionalOffsets = new int[FIELD_INDEX_FIELD2 + 1];

    public long length()
    {
        return buffer().getInt(offset() + LOGICAL_LENGTH_OFFSET) & 0xFFFF_FFFFL;
    }

    private long bitmask()
    {
        return buffer().getInt(offset() + BIT_MASK_OFFSET) & 0xFFFF_FFFFL;
    }

    public StringFW field0()
    {
        return field0RO;
    }

    public long field1()
    {
        assert (bitmask() & (1 << FIELD_INDEX_FIELD1)) != 0 : "Field \"fixed1\" is not set";
        return buffer().getInt(optionalOffsets[FIELD_INDEX_FIELD1]) & 0xFFFF_FFFFL;
    }

    public StringFW field2()
    {
        assert (bitmask() & (1 << FIELD_INDEX_FIELD2)) != 0 : "Field \"fixed2\" is not set";
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
        int fieldLimit = offset + FIELD_OFFSET_FIELD0;
        for (int field = FIELD_INDEX_FIELD0; field < FIELD_INDEX_FIELD2 + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_FIELD0:
                if ((bitmask & (1 << FIELD_INDEX_FIELD0)) == 0)
                {
                    throw new IllegalArgumentException("Field \"fixed0\" is required but not set");
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
        if (null == super.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        final long bitmask = bitmask();
        int fieldLimit = offset + FIELD_OFFSET_FIELD0;
        for (int field = FIELD_INDEX_FIELD0; field < FIELD_INDEX_FIELD2 + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_FIELD0:
                if ((bitmask & (1 << FIELD_INDEX_FIELD0)) == 0)
                {
                    return null;
                }
                if (null == field0RO.tryWrap(buffer, fieldLimit, maxLimit))
                {
                    return null;
                }
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
                    if (null == field2RO.tryWrap(buffer, fieldLimit, maxLimit))
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
        return offset() + (int) (buffer().getInt(offset() + PHYSICAL_LENGTH_OFFSET) & 0xFFFF_FFFFL);
    }

    @Override
    public String toString()
    {
        return String.format("LIST_WITH_PHYSICAL_AND_LOGICAL_LENGTH [field0=%s, field1=%d, field2=%s]",
            field0() != null ? field0RO.asString() : null, field1(), field2() != null ? field2RO.asString() : null);
    }

    public static final class Builder extends Flyweight.Builder<ListWithPhysicalAndLogicalLengthFW>
    {
        private final StringFW.Builder field0RW = new StringFW.Builder();

        private final StringFW.Builder field2RW = new StringFW.Builder();

        private int fieldsMask;

        public Builder()
        {
            super(new ListWithPhysicalAndLogicalLengthFW());
        }

        private StringFW.Builder field0()
        {
            int newBit = fieldsMask | (1 << FIELD_INDEX_FIELD0);
            assert newBit == (newBit & 0x01) : "Value out of order for field \"field0\" in the list";
            return field0RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder field0(
            String value)
        {
            StringFW.Builder field0RW = field0();
            field0RW.set(value, StandardCharsets.UTF_8);
            fieldsMask |= 1 << FIELD_INDEX_FIELD0;
            limit(field0RW.build().limit());
            return this;
        }

        public Builder field0(
            StringFW value)
        {
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
            StringFW.Builder field0RW = field0();
            field0RW.set(buffer, offset, length);
            fieldsMask |= 1 << FIELD_INDEX_FIELD0;
            limit(field0RW.build().limit());
            return this;
        }

        public Builder field1(
            long value)
        {
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"field1\"", value));
            }
            assert (value & 0xffff_ffff_0000_0000L) == 0L : "Value out of range for field \"field1\"";
            int newBit = fieldsMask | (1 << FIELD_INDEX_FIELD1);
            assert newBit == (newBit & 0x03) : "Value out of order for field \"field1\" in the list";
            int newLimit = limit() + FIELD_SIZE_FIELD1;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            fieldsMask |= 1 << FIELD_INDEX_FIELD1;
            limit(newLimit);
            return this;
        }

        private StringFW.Builder field2()
        {
            int newBit = fieldsMask | (1 << FIELD_INDEX_FIELD2);
            assert newBit == (newBit & 0x07) : "Value out of order for field \"field2\" in the list";
            return field2RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder field2(
            String value)
        {
            StringFW.Builder field2RW = field2();
            field2RW.set(value, StandardCharsets.UTF_8);
            fieldsMask |= 1 << FIELD_INDEX_FIELD2;
            limit(field2RW.build().limit());
            return this;
        }

        public Builder field2(
            StringFW value)
        {
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
            int newLimit = limit() + FIELD_OFFSET_FIELD0;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            return this;
        }

        @Override
        public ListWithPhysicalAndLogicalLengthFW build()
        {
            assert (fieldsMask & 0x01) != 0 : "Required field \"field0\" is not populated";
            buffer().putInt(offset() + PHYSICAL_LENGTH_OFFSET, (int) (limit() & 0xFFFF_FFFFL));
            buffer().putInt(offset() + LOGICAL_LENGTH_OFFSET, (int) (Integer.bitCount(fieldsMask) & 0xFFFF_FFFFL));
            buffer().putInt(offset() + BIT_MASK_OFFSET, (int) (fieldsMask & 0xFFFF_FFFFL));
            fieldsMask = 0;
            return super.build();
        }
    }
}
