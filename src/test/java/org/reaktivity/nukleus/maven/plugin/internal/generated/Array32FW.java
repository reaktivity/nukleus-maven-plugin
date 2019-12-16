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

public final class Array32FW<T extends VariantFW> extends ArrayFW<T>
{
    private static final int LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    private static final int FIELD_COUNT_SIZE = BitUtil.SIZE_OF_INT;

    private static final int LENGTH_OFFSET = 0;

    private static final int FIELD_COUNT_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;

    private static final int FIELDS_OFFSET = FIELD_COUNT_OFFSET + FIELD_COUNT_SIZE;

    private static final long LENGTH_MAX_VALUE = 0xFFFFFFFFL;

    private final T itemRO;

    private final DirectBuffer itemsRO = new UnsafeBuffer(0L, 0);

    Array32FW(
        T itemRO)
    {
        this.itemRO = itemRO;
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
    public DirectBuffer items()
    {
        return itemsRO;
    }

    public T itemRO()
    {
        return itemRO;
    }

    public void forEach(
        Consumer<T> consumer)
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
    public Array32FW wrap(
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
    public Array32FW tryWrap(
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

    public static final class Builder<B extends VariantFW.Builder<O, K, V>, O extends Flyweight, V extends VariantFW<O, K>, K>
        extends ArrayFW.Builder<B, O, V, Array32FW<V>>
    {
        private int kindPadding;

        public Builder(
            B itemRW,
            V itemRO)
        {
            super(new Array32FW<>(itemRO), itemRW);
        }

        public Builder<B, O, V, K> item(
            O item)
        {
            itemRW().wrap(buffer(), offset() + FIELDS_OFFSET, maxLimit());
            itemRW().setAs(itemRW().maxKind(), item, kindPadding);
            super.item(item);
            kindPadding += itemRW().size();
            return this;
        }

        public Builder<B, O, V, K> items(
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
        public Builder<B, O, V, K> wrap(
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
        public Array32FW build()
        {
            if (maxLength() > 0 && !itemRW().maxKind().equals(itemRW().kindFromLength(maxLength())))
            {
                K kind = itemRW().kindFromLength(maxLength());
                int originalPadding = 0;
                int rearrangePadding = 0;
                int originalLimit = itemRW().limit();
                for (int i = 0; i < fieldCount(); i++)
                {
                    V itemRO = itemRW().build(originalLimit);
                    O originalItem = itemRO.getAs(itemRW().maxKind(), originalPadding);
                    originalPadding += originalItem.sizeof();
                    itemRW().setAs(kind, originalItem, rearrangePadding);
                    O rearrangedItem = itemRO.getAs(kind, rearrangePadding);
                    rearrangePadding += rearrangedItem.sizeof();
                }
                limit(itemRW().limit());
            }
            int length = limit() - offset() - FIELD_COUNT_OFFSET;
            assert length <= LENGTH_MAX_VALUE : "Length is too large";
            assert fieldCount() <= LENGTH_MAX_VALUE : "Field count is too large";
            buffer().putInt(offset() + LENGTH_OFFSET, length);
            buffer().putInt(offset() + FIELD_COUNT_OFFSET, fieldCount());
            return super.build();
        }
    }
}
