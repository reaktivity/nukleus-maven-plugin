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

import java.util.function.Consumer;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.UnionOctetsFW;

public final class ListWithUnionFW extends Flyweight
{
    private static final int PHYSICAL_LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int LOGICAL_LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int BIT_MASK_SIZE = BitUtil.SIZE_OF_LONG;

    private static final int PHYSICAL_LENGTH_OFFSET = 0;

    private static final int LOGICAL_LENGTH_OFFSET = PHYSICAL_LENGTH_OFFSET + PHYSICAL_LENGTH_SIZE;

    private static final int BIT_MASK_OFFSET = LOGICAL_LENGTH_OFFSET + LOGICAL_LENGTH_SIZE;

    private static final int FIRST_FIELD_OFFSET = BIT_MASK_OFFSET + BIT_MASK_SIZE;

    private static final int FIELD_SIZE_FIELD1 = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_DEFAULT_VALUE_FIELD1 = 1;

    private static final int FIELD_INDEX_UNION_OCTETS = 0;

    private static final int FIELD_INDEX_FIELD1 = 1;

    private final UnionOctetsFW unionOctetsRO = new UnionOctetsFW();

    private final int[] optionalOffsets = new int[FIELD_INDEX_FIELD1 + 1];

    public int length()
    {
        return buffer().getByte(offset() + LOGICAL_LENGTH_OFFSET);
    }

    public long bitmask()
    {
        return buffer().getLong(offset() + BIT_MASK_OFFSET);
    }

    public UnionOctetsFW unionOctets()
    {
        assert (bitmask() & (1 << FIELD_INDEX_UNION_OCTETS)) != 0 : "Field \"unionOctets\" is not set";
        return unionOctetsRO;
    }

    public int field1()
    {
        return (bitmask() & (1 << FIELD_INDEX_FIELD1)) == 0 ? FIELD_DEFAULT_VALUE_FIELD1 :
            buffer().getByte(optionalOffsets[FIELD_INDEX_FIELD1]) & 0xFFFF;
    }

    @Override
    public ListWithUnionFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        final long bitmask = bitmask();
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        for (int field = FIELD_INDEX_UNION_OCTETS; field < FIELD_INDEX_FIELD1 + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_UNION_OCTETS:
                if ((bitmask & (1 << FIELD_INDEX_UNION_OCTETS)) != 0)
                {
                    unionOctetsRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = unionOctetsRO.limit();
                }
                break;
            case FIELD_INDEX_FIELD1:
                if ((bitmask & (1 << FIELD_INDEX_FIELD1)) != 0)
                {
                    optionalOffsets[FIELD_INDEX_FIELD1] = fieldLimit;
                    fieldLimit += FIELD_SIZE_FIELD1;
                }
                break;
            }
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public ListWithUnionFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        final long bitmask = bitmask();
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        for (int field = FIELD_INDEX_UNION_OCTETS; field < FIELD_INDEX_FIELD1 + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_UNION_OCTETS:
                if ((bitmask & (1 << FIELD_INDEX_UNION_OCTETS)) != 0)
                {
                    if (null == unionOctetsRO.tryWrap(buffer, fieldLimit, maxLimit))
                    {
                        return null;
                    }
                    fieldLimit = unionOctetsRO.limit();
                }
                break;
            case FIELD_INDEX_FIELD1:
                if ((bitmask & (1 << FIELD_INDEX_FIELD1)) != 0)
                {
                    optionalOffsets[FIELD_INDEX_FIELD1] = fieldLimit;
                    fieldLimit += FIELD_SIZE_FIELD1;
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
        final long bitmask = bitmask();
        return String.format("LIST_WITH_UNION [bitmask=%s%s, field1=%d]",
            String.format("0x%04X", bitmask),
            (bitmask & (1 << FIELD_INDEX_UNION_OCTETS)) != 0 ? String.format(", unionOctets=%s", unionOctets()) : "",
            field1());
    }

    public static final class Builder extends Flyweight.Builder<ListWithUnionFW>
    {
        private final UnionOctetsFW.Builder unionOctetsRW = new UnionOctetsFW.Builder();
        private long fieldsMask;

        protected Builder()
        {
            super(new ListWithUnionFW());
        }

        public Builder unionOctets(
            UnionOctetsFW value)
        {
            assert (fieldsMask & ~0x00) == 0 : "Field \"unionOctets\" is already set or subsequent fields are already set";
            int newLimit = limit() + value.sizeof();
            checkLimit(newLimit, maxLimit());
            buffer().putBytes(limit(), value.buffer(), value.offset(), value.sizeof());
            fieldsMask |= 1 << FIELD_INDEX_UNION_OCTETS;
            limit(newLimit);
            return this;
        }

        public Builder unionOctets(
            Consumer<UnionOctetsFW.Builder> mutator)
        {
            assert (fieldsMask & ~0x00) == 0 : "Field \"unionOctets\" is already set or subsequent fields are already set";
            UnionOctetsFW.Builder unionOctetsRW = this.unionOctetsRW.wrap(buffer(), limit(), maxLimit());
            mutator.accept(unionOctetsRW);
            fieldsMask |= 1 << FIELD_INDEX_UNION_OCTETS;
            limit(unionOctetsRW.build().limit());
            return this;
        }

        public Builder field1(
            byte value)
        {
            assert (fieldsMask & ~0x01) == 0 : "Field \"field1\" is already set or subsequent fields are already set";
            int newLimit = limit() + FIELD_SIZE_FIELD1;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(limit(), value);
            fieldsMask |= 1 << FIELD_INDEX_FIELD1;
            limit(newLimit);
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
        public ListWithUnionFW build()
        {
            buffer().putByte(offset() + PHYSICAL_LENGTH_OFFSET, (byte) (limit() - offset()));
            buffer().putByte(offset() + LOGICAL_LENGTH_OFFSET, (byte) (Long.bitCount(fieldsMask)));
            buffer().putLong(offset() + BIT_MASK_OFFSET, fieldsMask);
            return super.build();
        }
    }
}
