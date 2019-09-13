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

    public long physicalLength()
    {
        return buffer().getInt(offset() + PHYSICAL_LENGTH_OFFSET) & 0xFFFF_FFFFL;
    }

    public long logicalLength()
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

    public Long field1()
    {
        if (optionalOffsets[FIELD_INDEX_FIELD1] == 0)
        {
            return null;
        }
        return buffer().getInt(optionalOffsets[FIELD_INDEX_FIELD1]) & 0xFFFF_FFFFL;
    }

    public StringFW field2()
    {
        if ((bitmask() & (1 << FIELD_INDEX_FIELD2)) == 0)
        {
            return null;
        }
        return field2RO;
    }

    @Override
    public ListWithPhysicalAndLogicalLengthFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        long bitmask = bitmask();
        if ((bitmask & 0x01) == 0)
        {
            throw new IllegalArgumentException("Field \"fixed0\" is required but not set");
        }
        int previousPresentFieldOffset = offset + FIELD_OFFSET_FIELD0;
        field0RO.wrap(buffer, previousPresentFieldOffset, maxLimit);
        previousPresentFieldOffset += field0RO.sizeof();
        bitmask >>= 1;
        for (int i = FIELD_INDEX_FIELD1; i < FIELD_INDEX_FIELD2 + 1; i++)
        {
            if ((bitmask & 1) != 0)
            {
                switch (i)
                {
                case FIELD_INDEX_FIELD1:
                    optionalOffsets[FIELD_INDEX_FIELD1] = previousPresentFieldOffset;
                    previousPresentFieldOffset += FIELD_SIZE_FIELD1;
                    break;
                case FIELD_INDEX_FIELD2:
                    field2RO.wrap(buffer, previousPresentFieldOffset, maxLimit);
                    previousPresentFieldOffset += field2RO.sizeof();
                    break;
                }
            }
            bitmask >>= 1;
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
        long bitmask = bitmask();
        int previousPresentFieldOffset = offset + FIELD_OFFSET_FIELD0;
        if (null == field0RO.tryWrap(buffer, previousPresentFieldOffset, maxLimit))
        {
            return null;
        }
        previousPresentFieldOffset += field0RO.sizeof();
        for (int i = FIELD_INDEX_FIELD1; i < FIELD_INDEX_FIELD2 + 1; i++)
        {
            if ((bitmask & 1) != 0)
            {
                switch (i)
                {
                case FIELD_INDEX_FIELD1:
                    optionalOffsets[FIELD_INDEX_FIELD1] = previousPresentFieldOffset;
                    previousPresentFieldOffset += FIELD_SIZE_FIELD1;
                    break;
                case FIELD_INDEX_FIELD2:
                    if (null == field2RO.tryWrap(buffer, previousPresentFieldOffset, maxLimit))
                    {
                        return null;
                    }
                    previousPresentFieldOffset += field2RO.sizeof();
                    break;
                }
            }
            bitmask >>= 1;
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
        return (int) physicalLength() + BIT_MASK_SIZE;
    }

    @Override
    public String toString()
    {
        return String.format("LIST_WITH_PHYSICAL_AND_LOGICAL_LENGTH [field0=%s, field1=%d, field2=%s]",
            field0() != null ? field0RO.asString() : null, field1(), field2() != null ? field2RO.asString() : null);
    }

    public static final class Builder extends Flyweight.Builder<ListWithPhysicalAndLogicalLengthFW>
    {
        private static final int INDEX_PHYSICAL_LENGTH = 0;

        private static final int INDEX_LOGICAL_LENGTH = 1;

        private final StringFW.Builder field0RW = new StringFW.Builder();

        private final StringFW.Builder field2RW = new StringFW.Builder();

        private long lastFieldSet = -1;

        private int currentBit = 0;

        private int initialOffset;

        public Builder()
        {
            super(new ListWithPhysicalAndLogicalLengthFW());
        }

        public Builder physicalLength(
            long value)
        {
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for physicalLength", value));
            }
            assert (value & 0xffff_ffff_0000_0000L) == 0L : "Value out of range for field \"physicalLength\"";

            assert lastFieldSet == INDEX_PHYSICAL_LENGTH - 1;
            int newLimit = limit() + PHYSICAL_LENGTH_SIZE;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            lastFieldSet = INDEX_PHYSICAL_LENGTH;
            limit(newLimit);
            return this;
        }

        public Builder logicalLength(
            long value)
        {
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for logicalLength", value));
            }
            assert (value & 0xffff_ffff_0000_0000L) == 0L : "Value out of range for field \"logicalLength\"";
            assert lastFieldSet == INDEX_LOGICAL_LENGTH - 1;
            int newLimit = limit() + LOGICAL_LENGTH_SIZE + BIT_MASK_SIZE;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            lastFieldSet = INDEX_LOGICAL_LENGTH;
            limit(newLimit);
            return this;
        }

        private StringFW.Builder field0()
        {
            assert lastFieldSet == INDEX_LOGICAL_LENGTH;
            int newBit = currentBit | (1 << FIELD_INDEX_FIELD0);
            assert newBit == (newBit & 0x01) : "Value out of order for field \"field0\" in the list";
            return field0RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder field0(
            String value)
        {
            StringFW.Builder field0RW = field0();
            field0RW.set(value, StandardCharsets.UTF_8);
            currentBit |= 1 << FIELD_INDEX_FIELD0;
            limit(field0RW.build().limit());
            return this;
        }

        public Builder field0(
            StringFW value)
        {
            StringFW.Builder field0RW = field0();
            field0RW.set(value);
            currentBit |= 1 << FIELD_INDEX_FIELD0;
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
            currentBit |= 1 << FIELD_INDEX_FIELD0;
            limit(field0RW.build().limit());
            return this;
        }

        public Builder field1(
            long value)
        {
            assert lastFieldSet == INDEX_LOGICAL_LENGTH;
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"field1\"", value));
            }
            assert (value & 0xffff_ffff_0000_0000L) == 0L : "Value out of range for field \"field1\"";
            int newBit = currentBit | (1 << FIELD_INDEX_FIELD1);
            assert newBit == (newBit & 0x03) : "Value out of order for field \"field1\" in the list";
            int newLimit = limit() + FIELD_SIZE_FIELD1;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            currentBit |= 1 << FIELD_INDEX_FIELD1;
            limit(newLimit);
            return this;
        }

        private StringFW.Builder field2()
        {
            assert lastFieldSet == INDEX_LOGICAL_LENGTH;
            int newBit = currentBit | (1 << FIELD_INDEX_FIELD2);
            assert newBit == (newBit & 0x07) : "Value out of order for field \"field2\" in the list";
            return field2RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder field2(
            String value)
        {
            StringFW.Builder field2RW = field2();
            field2RW.set(value, StandardCharsets.UTF_8);
            currentBit |= 1 << FIELD_INDEX_FIELD2;
            limit(field2RW.build().limit());
            return this;
        }

        public Builder field2(
            StringFW value)
        {
            StringFW.Builder field2RW = field2();
            field2RW.set(value);
            currentBit |= 1 << FIELD_INDEX_FIELD2;
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
            currentBit |= 1 << FIELD_INDEX_FIELD2;
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
            lastFieldSet = -1;
            currentBit = 0;
            initialOffset = offset;
            return this;
        }

        @Override
        public ListWithPhysicalAndLogicalLengthFW build()
        {
            assert lastFieldSet == INDEX_LOGICAL_LENGTH;
            assert (currentBit & 0x01) != 0 : "Required field \"field0\" is not populated";
            buffer().putInt(initialOffset + BIT_MASK_OFFSET, (int) (currentBit & 0xFFFF_FFFFL));
            lastFieldSet = -1;
            currentBit = 0;
            return super.build();
        }
    }
}
