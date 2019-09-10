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
import java.util.HashMap;
import java.util.Map;

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

    private static final StringFW FIELD0RO = new StringFW();

    private static final StringFW FIELD2RO = new StringFW();

    private static final Map<Integer, Object> FIELD_BY_INDEX;

    private Map<Integer, Integer> primitiveFieldOffsets = new HashMap<>();

    static
    {
        Map<Integer, Object> fieldByIndex = new HashMap<>();
        fieldByIndex.put(FIELD_INDEX_FIELD0, FIELD0RO);
        fieldByIndex.put(FIELD_INDEX_FIELD1, FIELD_SIZE_FIELD1);
        fieldByIndex.put(FIELD_INDEX_FIELD2, FIELD2RO);
        FIELD_BY_INDEX = fieldByIndex;
    }

    public long physicalLength()
    {
        return (long) (buffer().getInt(offset() + PHYSICAL_LENGTH_OFFSET) & 0xFFFF_FFFFL);
    }

    public long logicalLength()
    {
        return (long) (buffer().getInt(offset() + LOGICAL_LENGTH_OFFSET) & 0xFFFF_FFFFL);
    }

    public long bitMask()
    {
        return (long) (buffer().getInt(offset() + BIT_MASK_OFFSET) & 0xFFFF_FFFFL);
    }

    public StringFW field0()
    {
        return FIELD0RO;
    }

    public Long field1()
    {
        if (primitiveFieldOffsets.get(FIELD_INDEX_FIELD1) == null)
        {
            return null;
        }
        return (long) (buffer().getInt(primitiveFieldOffsets.get(FIELD_INDEX_FIELD1)) & 0xFFFF_FFFFL);
    }

    public StringFW field2()
    {
        if ((bitMask() & (1 << FIELD_INDEX_FIELD2)) == 0)
        {
            return null;
        }
        return FIELD2RO;
    }

    @Override
    public ListWithPhysicalAndLogicalLengthFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        long bitMask = bitMask();
        int previousPresentFieldOffset = offset + FIELD_OFFSET_FIELD0;
        for (int i = 0; i < 3; i++)
        {
            if (Long.lowestOneBit(bitMask) == 1)
            {
                if (FIELD_BY_INDEX.get(i) instanceof Flyweight)
                {
                    Flyweight flyweight = (Flyweight) FIELD_BY_INDEX.get(i);
                    flyweight.wrap(buffer, previousPresentFieldOffset, maxLimit);
                    previousPresentFieldOffset += flyweight.sizeof();
                }
                else
                {
                    primitiveFieldOffsets.put(i, previousPresentFieldOffset);
                    previousPresentFieldOffset += (int) FIELD_BY_INDEX.get(i);
                }
            }
            else if (i == FIELD_INDEX_FIELD0)
            {
                throw new IllegalArgumentException("Field \"fixed0\" is required");
            }
            bitMask >>= 1;
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
        long bitMask = bitMask();
        int previousPresentFieldOffset = offset + FIELD_OFFSET_FIELD0;
        for (int i = 0; i < FIELD_BY_INDEX.size(); i++)
        {
            if (Long.lowestOneBit(bitMask) == 1)
            {
                if (FIELD_BY_INDEX.get(i) instanceof Flyweight)
                {
                    Flyweight flyweight = (Flyweight) FIELD_BY_INDEX.get(i);
                    if (null == flyweight.tryWrap(buffer, previousPresentFieldOffset, maxLimit))
                    {
                        return null;
                    }
                    previousPresentFieldOffset += flyweight.sizeof();
                }
                else
                {
                    primitiveFieldOffsets.put(i, previousPresentFieldOffset);
                    previousPresentFieldOffset += (int) FIELD_BY_INDEX.get(i);
                }
            }
            else if (i == FIELD_INDEX_FIELD0)
            {
                return null;
            }
            bitMask >>= 1;
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
        return (int) physicalLength();
    }

    @Override
    public String toString()
    {
        return String.format("LIST_WITH_PHYSICAL_AND_LOGICAL_LENGTH [field0=%s, field1=%d, field2=%s]",
            field0() != null ? FIELD0RO.asString() : null, field1(), field2() != null ? FIELD2RO.asString() : null);
    }

    public static final class Builder extends Flyweight.Builder<ListWithPhysicalAndLogicalLengthFW>
    {
        private static final int INDEX_PHYSICAL_LENGTH = 0;

        private static final int INDEX_LOGICAL_LENGTH = 1;

        private static final int INDEX_BITMASK = 2;

        private final StringFW.Builder field0RW = new StringFW.Builder();

        private final StringFW.Builder field2RW = new StringFW.Builder();

        private long lastFieldSet = -1;

        private int currentBit = 0;

        private long bitMask;

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
            if (value > 0xFFFFFFFFL)
            {
                throw new IllegalArgumentException(String.format("Value %d too high for physicalLength", value));
            }
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
            if (value > 0xFFFFFFFFL)
            {
                throw new IllegalArgumentException(String.format("Value %d too high for logicalLength", value));
            }
            assert lastFieldSet == INDEX_LOGICAL_LENGTH - 1;
            int newLimit = limit() + LOGICAL_LENGTH_SIZE;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            lastFieldSet = INDEX_LOGICAL_LENGTH;
            limit(newLimit);
            return this;
        }

        public Builder bitMask(
            long value)
        {
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for bitMask", value));
            }
            if (value > 0xFFFFFFFFL)
            {
                throw new IllegalArgumentException(String.format("Value %d too high for bitMask", value));
            }
            if ((value & 1 << FIELD_INDEX_FIELD0) == 0)
            {
                throw new IllegalArgumentException("Value is required for field \"fixed0\"");
            }
            assert lastFieldSet == INDEX_BITMASK - 1;
            int newLimit = limit() + BIT_MASK_SIZE;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            lastFieldSet = INDEX_BITMASK;
            bitMask = value;
            limit(newLimit);
            return this;
        }

        private StringFW.Builder field0()
        {
            assert (bitMask & (1 << FIELD_INDEX_FIELD0)) != 0;
            assert currentBit + (1 << FIELD_INDEX_FIELD0) == (bitMask & 0x01);
            return field0RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder field0(
            String value)
        {
            if (value == null)
            {
                throw new IllegalArgumentException("Value cannot be null for field \"fixed0\"");
            }
            else
            {
                StringFW.Builder field0RW = field0();
                field0RW.set(value, StandardCharsets.UTF_8);
                currentBit += 1 << FIELD_INDEX_FIELD0;
                limit(field0RW.build().limit());
            }
            return this;
        }

        public Builder field0(
            StringFW value)
        {
            if (value == null)
            {
                throw new IllegalArgumentException("Value cannot be null for field \"fixed0\"");
            }
            else
            {
                StringFW.Builder field0RW = field0();
                field0RW.set(value);
                currentBit += 1 << FIELD_INDEX_FIELD0;
                limit(field0RW.build().limit());
            }
            return this;
        }

        public Builder field0(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            if (buffer == null)
            {
                throw new IllegalArgumentException("Buffer cannot be null for field \"fixed0\"");
            }
            else
            {
                StringFW.Builder field0RW = field0();
                field0RW.set(buffer, offset, length);
                currentBit += 1 << FIELD_INDEX_FIELD0;
                limit(field0RW.build().limit());
            }
            return this;
        }

        public Builder field1(
            long value)
        {
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"field1\"", value));
            }
            if (value > 0xFFFFFFFFL)
            {
                throw new IllegalArgumentException(String.format("Value %d too high for field \"field1\"", value));
            }
            assert (bitMask & (1 << FIELD_INDEX_FIELD1)) != 0;
            assert currentBit + (1 << FIELD_INDEX_FIELD1) == (bitMask & 0x03);
            int newLimit = limit() + FIELD_SIZE_FIELD1;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            currentBit += 1 << FIELD_INDEX_FIELD1;
            limit(newLimit);
            return this;
        }

        private StringFW.Builder field2()
        {
            assert (bitMask & (1 << FIELD_INDEX_FIELD2)) != 0;
            assert currentBit + (1 << FIELD_INDEX_FIELD2) == (bitMask & 0x07);
            return field2RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder field2(
            String value)
        {
            StringFW.Builder field2RW = field2();
            field2RW.set(value, StandardCharsets.UTF_8);
            currentBit += 1 << FIELD_INDEX_FIELD2;
            limit(field2RW.build().limit());
            return this;
        }

        public Builder field2(
            StringFW value)
        {
            StringFW.Builder field2RW = field2();
            field2RW.set(value);
            currentBit += 1 << FIELD_INDEX_FIELD2;
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
            currentBit += 1 << FIELD_INDEX_FIELD2;
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
            super.wrap(buffer, offset, maxLimit);
            limit(offset);
            return this;
        }

        @Override
        public ListWithPhysicalAndLogicalLengthFW build()
        {
            assert lastFieldSet == INDEX_BITMASK;
            assert currentBit == bitMask;
            lastFieldSet = -1;
            currentBit = 0;
            return super.build();
        }
    }
}
