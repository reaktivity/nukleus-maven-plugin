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

public class Array32FW<T extends Flyweight & VariantFW> extends ArrayFW<T, Array32FW>
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

    public Array32FW forEach(
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
        return this;
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

    public static final class Builder<B extends Flyweight.Builder & VariantFW.Builder<O, K>,
        T extends Flyweight & VariantFW<O>, O extends Flyweight, K> extends ArrayFW.Builder<B, O, K, Array32FW>
    {
        private int kindPadding;

        public Builder(
            B itemRW,
            T itemRO)
        {
            super(new Array32FW<>(itemRO), itemRW);
        }

        public Builder<B, T, O, K> item(
            O item)
        {
            itemRW().wrapArrayElement(buffer(), offset() + FIELDS_OFFSET, maxLimit(), kindPadding);
            super.item(item);
            kindPadding = itemRW().kindPadding();
            return this;
        }

        public Builder<B, T, O, K> items(
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
        public Builder<B, T, O, K> wrap(
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
                int currentPadding = 0;
                for (int i = 0; i < fieldCount(); i++)
                {
                    itemRW().kindPadding(currentPadding);
                    T itemRO = (T) itemRW().build();
                    currentPadding += itemRO.get().sizeof();
                    itemRW().setAsWithoutKind(kind, itemRO.get());
                }
                itemRW().kind(kind);
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
