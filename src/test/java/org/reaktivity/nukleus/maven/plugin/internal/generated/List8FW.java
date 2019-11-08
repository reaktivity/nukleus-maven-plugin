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
import org.agrona.concurrent.UnsafeBuffer;

public final class List8FW extends ListFW
{
    final DirectBuffer fieldsRO = new UnsafeBuffer(0L, 0);

    private static final int LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_COUNT_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int LENGTH_OFFSET = 0;

    private static final int FIELD_COUNT_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;

    private static final int FIELDS_OFFSET = FIELD_COUNT_OFFSET + FIELD_COUNT_SIZE;

    private static final int LENGTH_MAX_VALUE = Byte.MAX_VALUE + Byte.MAX_VALUE + 1;

    @Override
    public int limit()
    {
        return offset() + LENGTH_SIZE + length();
    }

    @Override
    public int length()
    {
        return buffer().getByte(offset() + LENGTH_OFFSET);
    }

    @Override
    public int fieldCount()
    {
        return buffer().getByte(offset() + FIELD_COUNT_OFFSET);
    }

    @Override
    public DirectBuffer fields()
    {
        return fieldsRO;
    }

    @Override
    public List8FW tryWrap(
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
    public List8FW wrap(
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

    public static final class Builder extends ListFW.Builder<List8FW>
    {
        public Builder()
        {
            super(new List8FW());
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
        public List8FW build()
        {
            int length = limit() - offset() - FIELD_COUNT_OFFSET;
            int fieldCount = fieldCount();
            assert length <= LENGTH_MAX_VALUE : "Physical length is too large";
            assert fieldCount <= LENGTH_MAX_VALUE : "Logical length is too large";
            buffer().putByte(offset() + LENGTH_OFFSET, (byte) length);
            buffer().putByte(offset() + FIELD_COUNT_OFFSET, (byte) fieldCount);
            return super.build();
        }
    }
}
