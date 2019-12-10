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

public final class UnionChildFW extends Flyweight
{
    private static final int FIELD_SIZE_FIXED1 = 8;

    public static final int FIELD_OFFSET_FIXED1 = 0;

    private static final int FIELD_SIZE_KIND = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_OFFSET_KIND = FIELD_OFFSET_FIXED1 + FIELD_SIZE_FIXED1;

    public static final int KIND_WIDTH8 = 8;

    private static final int FIELD_OFFSET_WIDTH8 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_WIDTH8 = BitUtil.SIZE_OF_BYTE;

    public static final int KIND_WIDTH16 = 16;

    private static final int FIELD_OFFSET_WIDTH16 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_WIDTH16 = BitUtil.SIZE_OF_SHORT;

    public static final int KIND_WIDTH32 = 32;

    private static final int FIELD_OFFSET_WIDTH32 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    private static final int FIELD_SIZE_WIDTH32 = BitUtil.SIZE_OF_INT;

    public long fixed1()
    {
        return buffer().getLong(offset() + FIELD_OFFSET_FIXED1);
    }

    public byte width8()
    {
        return buffer().getByte(offset() + FIELD_OFFSET_WIDTH8);
    }

    public short width16()
    {
        return buffer().getShort(offset() + FIELD_OFFSET_WIDTH16);
    }

    public int width32()
    {
        return buffer().getInt(offset() + FIELD_OFFSET_WIDTH32);
    }

    public int kind()
    {
        return buffer().getByte(offset() + FIELD_OFFSET_KIND) & 0xFF;
    }

    @Override
    public UnionChildFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        switch (kind())
        {
        case KIND_WIDTH8:
            break;
        case KIND_WIDTH16:
            break;
        case KIND_WIDTH32:
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
    public UnionChildFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case KIND_WIDTH8:
            break;
        case KIND_WIDTH16:
            break;
        case KIND_WIDTH32:
            break;
        default:
            break;
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case KIND_WIDTH8:
            return offset() + FIELD_OFFSET_WIDTH8 + FIELD_SIZE_WIDTH8;
        case KIND_WIDTH16:
            return offset() + FIELD_OFFSET_WIDTH16 + FIELD_SIZE_WIDTH16;
        case KIND_WIDTH32:
            return offset() + FIELD_OFFSET_WIDTH32 + FIELD_SIZE_WIDTH32;
        default:
            return offset();
        }
    }

    @Override
    public String toString()
    {
        switch (kind())
        {
        case KIND_WIDTH8:
            return String.format("UNIONCHILD [fixed1=%d, width8=%d]", fixed1(), width8());
        case KIND_WIDTH16:
            return String.format("UNIONCHILD [fixed1=%d, width16=%d]", fixed1(), width16());
        case KIND_WIDTH32:
            return String.format("UNIONCHILD [fixed1=%d, width32=%d]", fixed1(), width32());
        default:
            return String.format("UNIONCHILD [unknown]");
        }
    }

    public static final class Builder extends Flyweight.Builder<UnionChildFW>
    {
        private static final int INDEX_FIXED1 = 0;

        private int lastFieldSet = -1;

        public Builder()
        {
            super(new UnionChildFW());
        }

        public Builder fixed1(
            long value)
        {
            if (value < 0L)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"fixed1\"", value));
            }
            assert lastFieldSet == INDEX_FIXED1 - 1;
            int newLimit = limit() + FIELD_SIZE_FIXED1;
            checkLimit(newLimit, maxLimit());
            buffer().putLong(limit(), value);
            lastFieldSet = INDEX_FIXED1;
            limit(newLimit);
            return this;
        }

        private Builder kind(
            int value)
        {
            assert lastFieldSet == INDEX_FIXED1 : "Field \"fixed1\" is not set";
            buffer().putByte(offset() + FIELD_OFFSET_KIND, (byte) (value & 0xFF));
            return this;
        }

        public Builder width8(
            byte value)
        {
            kind(KIND_WIDTH8);
            buffer().putByte(offset() + FIELD_OFFSET_WIDTH8, value);
            limit(offset() + FIELD_OFFSET_WIDTH8 + FIELD_SIZE_WIDTH8);
            return this;
        }

        public Builder width16(
            short value)
        {
            kind(KIND_WIDTH16);
            buffer().putShort(offset() + FIELD_OFFSET_WIDTH16, value);
            limit(offset() + FIELD_OFFSET_WIDTH16 + FIELD_SIZE_WIDTH16);
            return this;
        }

        public Builder width32(
            int value)
        {
            kind(KIND_WIDTH32);
            buffer().putInt(offset() + FIELD_OFFSET_WIDTH32, value);
            limit(offset() + FIELD_OFFSET_WIDTH32 + FIELD_SIZE_WIDTH32);
            return this;
        }

        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            lastFieldSet = -1;
            return this;
        }
    }
}
