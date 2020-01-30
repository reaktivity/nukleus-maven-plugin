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
import java.util.function.Function;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public final class Map8FW<K extends Flyweight, V extends Flyweight> extends MapFW<K, V>
{
    private static final int LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_COUNT_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int LENGTH_OFFSET = 0;

    private static final int FIELD_COUNT_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;

    private static final int FIELDS_OFFSET = FIELD_COUNT_OFFSET + FIELD_COUNT_SIZE;

    private static final int LENGTH_MAX_VALUE = 0xFF;

    private final K keyRO;

    private final V valueRO;

    private final DirectBuffer entriesRO = new UnsafeBuffer(0L, 0);

    public Map8FW(
        K keyRO,
        V valueRO)
    {
        this.keyRO = keyRO;
        this.valueRO = valueRO;
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
    public DirectBuffer entries()
    {
        return entriesRO;
    }

    @Override
    public void forEach(
        Function<K, Consumer<V>> consumer)
    {
        int offset = offset() + FIELDS_OFFSET;
        int fieldCount = fieldCount();
        for (int i = 0; i < fieldCount; i += 2)
        {
            keyRO.wrap(buffer(), offset, limit());
            valueRO.wrap(buffer(), keyRO.limit(), limit());
            offset = valueRO.limit();
            consumer.apply(keyRO).accept(valueRO);
        }
    }

    @Override
    public Map8FW<K, V> tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        final int itemsSize = length() - FIELD_COUNT_SIZE;
        entriesRO.wrap(buffer, offset + FIELDS_OFFSET, itemsSize);
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public Map8FW<K, V> wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        final int itemsSize = length() - FIELD_COUNT_SIZE;
        entriesRO.wrap(buffer, offset + FIELDS_OFFSET, itemsSize);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public int limit()
    {
        return offset() + LENGTH_SIZE + length();
    }

    @Override
    public String toString()
    {
        return String.format("map8<%d, %d>", length(), fieldCount());
    }

    public static final class Builder<K extends Flyweight, V extends Flyweight, KB extends Flyweight.Builder<K>,
        VB extends Flyweight.Builder<V>> extends MapFW.Builder<Map8FW, K, V, KB, VB>
    {
        public Builder(
            K keyRO,
            V valueRO,
            KB keyRW,
            VB valueRW)
        {
            super(new Map8FW<>(keyRO, valueRO), keyRW, valueRW);
        }

        @Override
        public Builder<K, V, KB, VB> wrap(
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
        public Builder<K, V, KB, VB> entries(
            DirectBuffer buffer,
            int srcOffset,
            int length,
            int fieldCount)
        {
            buffer().putBytes(offset() + FIELDS_OFFSET, buffer, srcOffset, length);
            int newLimit = offset() + FIELDS_OFFSET + length;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            super.entries(buffer, srcOffset, length, fieldCount);
            return this;
        }

        @Override
        public Map8FW build()
        {
            int length = limit() - offset() - FIELD_COUNT_OFFSET;
            assert length <= LENGTH_MAX_VALUE : "Length is too large";
            assert fieldCount() <= LENGTH_MAX_VALUE : "Field count is too large";
            buffer().putByte(offset() + LENGTH_OFFSET, (byte) length);
            buffer().putByte(offset() + FIELD_COUNT_OFFSET, (byte) fieldCount());
            return super.build();
        }
    }
}

