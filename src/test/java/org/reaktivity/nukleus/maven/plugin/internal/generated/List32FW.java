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
import org.agrona.concurrent.UnsafeBuffer;

public final class List32FW extends ListFW
{
    private static final int LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    private static final int FIELD_COUNT_SIZE = BitUtil.SIZE_OF_INT;

    private static final int LENGTH_OFFSET = 0;

    private static final int FIELD_COUNT_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;

    private static final int FIELDS_OFFSET = FIELD_COUNT_OFFSET + FIELD_COUNT_SIZE;

    private final DirectBuffer fieldsRO = new UnsafeBuffer(0L, 0);

    @Override
    public int limit()
    {
        return offset() + LENGTH_SIZE + length();
    }

    @Override
    public int length()
    {
        return buffer().getInt(offset() + LENGTH_OFFSET);
    }

    @Override
    public int fieldCount()
    {
        return buffer().getInt(offset() + FIELD_COUNT_OFFSET);
    }

    @Override
    public DirectBuffer fields()
    {
        return fieldsRO;
    }

    @Override
    public List32FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        final int fieldsSize = length() - FIELD_COUNT_SIZE;
        fieldsRO.wrap(buffer, offset + FIELDS_OFFSET, fieldsSize);
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public List32FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        final int fieldsSize = length() - FIELD_COUNT_SIZE;
        fieldsRO.wrap(buffer, offset + FIELDS_OFFSET, fieldsSize);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        return String.format("list32<%d, %d>", length(), fieldCount());
    }

    public static final class Builder extends ListFW.Builder<List32FW>
    {
        private int fieldCount;

        public Builder()
        {
            super(new List32FW());
        }

        @Override
        public Builder field(
            Flyweight.Builder.Visitor visitor)
        {
            int length = visitor.visit(buffer(), limit(), maxLimit());
            fieldCount++;
            int newLimit = limit() + length;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            return this;
        }

        @Override
        public Builder fields(
            int fieldCount,
            Flyweight.Builder.Visitor visitor)
        {
            int length = visitor.visit(buffer(), limit(), maxLimit());
            this.fieldCount += fieldCount;
            int newLimit = limit() + length;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            return this;
        }

        @Override
        public Builder fields(
            int fieldCount,
            DirectBuffer buffer,
            int index,
            int length)
        {
            this.fieldCount += fieldCount;
            int newLimit = limit() + length;
            checkLimit(newLimit, maxLimit());
            buffer().putBytes(limit(), buffer, index, length);
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
            int newLimit = offset + FIELDS_OFFSET;
            checkLimit(newLimit, maxLimit);
            limit(newLimit);
            return this;
        }

        @Override
        public List32FW build()
        {
            buffer().putInt(offset() + LENGTH_OFFSET, limit() - offset() - FIELD_COUNT_OFFSET);
            buffer().putInt(offset() + FIELD_COUNT_OFFSET, fieldCount);
            return super.build();
        }
    }
}

