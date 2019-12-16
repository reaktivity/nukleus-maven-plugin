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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;

public final class ArrayWithLength8FW extends ArrayFW<VariantEnumKindWithString32FW>
{
    private static final int LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_COUNT_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int LENGTH_OFFSET = 0;

    private static final int FIELD_COUNT_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;

    private static final int FIELDS_OFFSET = FIELD_COUNT_OFFSET + FIELD_COUNT_SIZE;

    private static final int LENGTH_MAX_VALUE = 0xFF;

    private final VariantEnumKindWithString32FW itemRO = new VariantEnumKindWithString32FW();

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
    public DirectBuffer items()
    {
        return null;
    }

    public void forEach(
        Consumer<VariantEnumKindWithString32FW> consumer)
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
    public ArrayWithLength8FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public ArrayWithLength8FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
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
        return offset() + LENGTH_SIZE + length();
    }

    public static final class Builder extends Flyweight.Builder<ArrayWithLength8FW>
    {
        private final VariantEnumKindWithString32FW.Builder itemRW = new VariantEnumKindWithString32FW.Builder();

        private int kindPadding;

        private int maxLength;

        private int fieldCount;

        protected Builder()
        {
            super(new ArrayWithLength8FW());
        }

        public Builder item(
            StringFW item)
        {
            itemRW.wrap(buffer(), offset() + FIELDS_OFFSET, maxLimit());
            itemRW.setAs(itemRW.maxKind(), item, kindPadding);
            maxLength = Math.max(maxLength, item.sizeof());
            checkLimit(itemRW.limit(), maxLimit());
            limit(itemRW.limit());
            fieldCount++;
            kindPadding += itemRW.size();
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
        public ArrayWithLength8FW build()
        {
            if (maxLength > 0 && !itemRW.maxKind().equals(itemRW.kindFromLength(maxLength)))
            {
                EnumWithInt8 kind = itemRW.kindFromLength(maxLength);
                int originalPadding = 0;
                int rearrangePadding = 0;
                int originalLimit = itemRW.limit();
                for (int i = 0; i < fieldCount; i++)
                {
                    VariantEnumKindWithString32FW itemRO = itemRW.build(originalLimit);
                    StringFW originalItem = itemRO.getAs(itemRW.maxKind(), originalPadding);
                    originalPadding += originalItem.sizeof();
                    itemRW.setAs(kind, originalItem, rearrangePadding);
                    StringFW rearrangedItem = itemRO.getAs(kind, rearrangePadding);
                    rearrangePadding += rearrangedItem.sizeof();
                }
                limit(itemRW.limit());
            }
            int length = limit() - offset() - FIELD_COUNT_OFFSET;
            assert length <= LENGTH_MAX_VALUE : "Length is too large";
            assert fieldCount <= LENGTH_MAX_VALUE : "Field count is too large";
            buffer().putByte(offset() + LENGTH_OFFSET, (byte) length);
            buffer().putByte(offset() + FIELD_COUNT_OFFSET, (byte) fieldCount);
            return super.build();
        }
    }
}
