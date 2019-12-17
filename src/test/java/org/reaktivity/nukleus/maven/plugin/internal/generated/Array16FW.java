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
import org.agrona.concurrent.UnsafeBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public final class Array16FW<V extends VariantFW<?, ? extends Flyweight>> extends ArrayFW<V>
{
    private static final int LENGTH_SIZE = BitUtil.SIZE_OF_SHORT;

    private static final int FIELD_COUNT_SIZE = BitUtil.SIZE_OF_SHORT;

    private static final int LENGTH_OFFSET = 0;

    private static final int FIELD_COUNT_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;

    private static final int FIELDS_OFFSET = FIELD_COUNT_OFFSET + FIELD_COUNT_SIZE;

    private static final int LENGTH_MAX_VALUE = 0xFFFF;

    private final V itemRO;

    private final DirectBuffer itemsRO = new UnsafeBuffer(0L, 0);

    Array16FW(
        V itemRO)
    {
        this.itemRO = itemRO;
    }

    @Override
    public int length()
    {
        return buffer().getShort(offset() + LENGTH_OFFSET);
    }

    @Override
    public int fieldCount()
    {
        return buffer().getShort(offset() + FIELD_COUNT_OFFSET);
    }

    @Override
    public DirectBuffer items()
    {
        return itemsRO;
    }

    public V itemRO()
    {
        return itemRO;
    }

    public void forEach(
        Consumer<V> consumer)
    {
        int offset = offset() + FIELDS_OFFSET;
        int currentPudding = 0;
        for (int i = 0; i < fieldCount(); i++)
        {
            itemRO.wrapArrayElement(buffer(), offset, limit(), currentPudding);
            consumer.accept(itemRO);
            currentPudding += itemRO.get().sizeof();
        }
    }

    @Override
    public Array16FW<V> wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        final int itemsSize = length() - FIELD_COUNT_SIZE;
        itemsRO.wrap(buffer, offset + FIELDS_OFFSET, itemsSize);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public Array16FW<V> tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        final int itemsSize = length() - FIELD_COUNT_SIZE;
        itemsRO.wrap(buffer, offset + FIELDS_OFFSET, itemsSize);
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public int limit()
    {
        return offset() + LENGTH_SIZE + length();
    }

    public static final class Builder<B extends VariantFW.Builder<V, K, O>, V extends VariantFW<K, O>, K, O extends Flyweight>
        extends ArrayFW.Builder<Array16FW<V>, B, V, K, O>
    {
        private int kindPadding;

        public Builder(
            B itemRW,
            V itemRO)
        {
            super(new Array16FW<>(itemRO), itemRW);
        }

        public Builder<B, V, K, O> item(
            O item)
        {
            itemRW.wrap(buffer(), offset() + FIELDS_OFFSET, maxLimit());
            itemRW.setAs(itemRW.maxKind(), item, kindPadding);
            super.item(item);
            kindPadding += itemRW.size();
            return this;
        }

        public Builder<B, V, K, O> items(
            DirectBuffer buffer,
            int srcOffset,
            int length,
            int fieldCount)
        {
            buffer().putBytes(offset() + FIELDS_OFFSET, buffer, srcOffset, length);
            int newLimit = offset() + FIELDS_OFFSET + length;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            super.items(buffer, srcOffset, length, fieldCount);
            return this;
        }

        @Override
        public Builder<B, V, K, O> wrap(
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
        public Array16FW<V> build()
        {
            relayout();
            int length = limit() - offset() - FIELD_COUNT_OFFSET;
            assert length <= LENGTH_MAX_VALUE : "Length is too large";
            assert fieldCount() <= LENGTH_MAX_VALUE : "Field count is too large";
            buffer().putShort(offset() + LENGTH_OFFSET, (short) length);
            buffer().putShort(offset() + FIELD_COUNT_OFFSET, (short) fieldCount());
            return super.build();
        }
    }
}
