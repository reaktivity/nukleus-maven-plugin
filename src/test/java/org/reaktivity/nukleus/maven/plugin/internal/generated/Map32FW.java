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

import java.nio.ByteOrder;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public final class Map32FW<K extends Flyweight, V extends Flyweight> extends MapFW<K, V>
{
    private static final int LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    private static final int FIELD_COUNT_SIZE = BitUtil.SIZE_OF_INT;

    private static final int LENGTH_OFFSET = 0;

    private static final int FIELD_COUNT_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;

    private static final int FIELDS_OFFSET = FIELD_COUNT_OFFSET + FIELD_COUNT_SIZE;

    private static final long LENGTH_MAX_VALUE = 0xFFFFFFFFL;

    private final ByteOrder byteOrder;

    private final K keyRO;

    private final V valueRO;

    private final DirectBuffer entriesRO = new UnsafeBuffer(0L, 0);

    public Map32FW(
        K keyRO,
        V valueRO)
    {
        this.keyRO = keyRO;
        this.valueRO = valueRO;
        this.byteOrder = ByteOrder.nativeOrder();
    }

    public Map32FW(
        K keyRO,
        V valueRO,
        ByteOrder byteOrder)
    {
        this.keyRO = keyRO;
        this.valueRO = valueRO;
        this.byteOrder = byteOrder;
    }

    @Override
    public int length()
    {
        return buffer().getInt(offset() + LENGTH_OFFSET, byteOrder);
    }

    @Override
    public int fieldCount()
    {
        return buffer().getInt(offset() + FIELD_COUNT_OFFSET, byteOrder);
    }

    @Override
    public void forEach(
        BiConsumer<K, V> consumer)
    {
        int offset = offset() + FIELDS_OFFSET;
        int fieldCount = fieldCount();
        for (int i = 0; i < fieldCount; i += 2)
        {
            K key = (K) keyRO.wrap(buffer(), offset, limit());
            V value = (V) valueRO.wrap(buffer(), key.limit(), limit());
            consumer.accept(key, value);
            offset = value.limit();
        }
    }

    @Override
    public DirectBuffer entries()
    {
        return entriesRO;
    }

    @Override
    public Map32FW<K, V> tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }

        int entryOffset = offset + FIELDS_OFFSET;
        int fieldCount = fieldCount();
        for (int i = 0; i < fieldCount; i += 2)
        {
            Flyweight key = keyRO.tryWrap(buffer, entryOffset, maxLimit);
            if (key == null)
            {
                return null;
            }
            V value = (V) valueRO.tryWrap(buffer, key.limit(), maxLimit);
            if (value == null)
            {
                return null;
            }
            entryOffset = value.limit();
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
    public Map32FW<K, V> wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);

        int entryOffset = offset + FIELDS_OFFSET;
        int fieldCount = fieldCount();
        for (int i = 0; i < fieldCount; i += 2)
        {
            K key = (K) keyRO.wrap(buffer, entryOffset, maxLimit);
            V value = (V) valueRO.wrap(buffer, key.limit(), maxLimit);
            entryOffset = value.limit();
        }

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
        return String.format("map32<%d, %d>", length(), fieldCount());
    }

    public static final class Builder<K extends Flyweight, V extends Flyweight, KB extends Flyweight.Builder<K>,
        VB extends Flyweight.Builder<V>> extends MapFW.Builder<Map32FW, K, V, KB, VB>
    {
        private final ByteOrder byteOrder;

        private final KB keyRW;

        private final VB valueRW;

        private int fieldCount;

        public Builder(
            K keyRO,
            V valueRO,
            KB keyRW,
            VB valueRW)
        {
            super(new Map32FW<>(keyRO, valueRO));
            this.keyRW = keyRW;
            this.valueRW = valueRW;
            this.byteOrder = ByteOrder.nativeOrder();
        }

        public Builder(
            K keyRO,
            V valueRO,
            KB keyRW,
            VB valueRW,
            ByteOrder byteOrder)
        {
            super(new Map32FW<>(keyRO, valueRO));
            this.keyRW = keyRW;
            this.valueRW = valueRW;
            this.byteOrder = byteOrder;
        }

        @Override
        public Map32FW.Builder<K, V, KB, VB> wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            int newLimit = offset + FIELDS_OFFSET;
            checkLimit(newLimit, maxLimit);
            limit(newLimit);
            fieldCount = 0;
            return this;
        }

        @Override
        public Map32FW.Builder<K, V, KB, VB> entry(
            Consumer<KB> key,
            Consumer<VB> value)
        {
            keyRW.wrap(buffer(), limit(), maxLimit());
            key.accept(keyRW);
            checkLimit(keyRW.limit(), maxLimit());
            limit(keyRW.limit());
            fieldCount++;
            valueRW.wrap(buffer(), limit(), maxLimit());
            value.accept(valueRW);
            checkLimit(valueRW.limit(), maxLimit());
            limit(valueRW.limit());
            fieldCount++;
            return this;
        }

        @Override
        public Map32FW.Builder<K, V, KB, VB> entries(
            DirectBuffer buffer,
            int srcOffset,
            int length,
            int fieldCount)
        {
            buffer().putBytes(offset() + FIELDS_OFFSET, buffer, srcOffset, length);
            int newLimit = offset() + FIELDS_OFFSET + length;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            this.fieldCount = fieldCount;
            return this;
        }

        @Override
        public Map32FW build()
        {
            int length = limit() - offset() - FIELD_COUNT_OFFSET;
            buffer().putInt(offset() + LENGTH_OFFSET, length, byteOrder);
            buffer().putInt(offset() + FIELD_COUNT_OFFSET, fieldCount, byteOrder);
            return super.build();
        }
    }
}
