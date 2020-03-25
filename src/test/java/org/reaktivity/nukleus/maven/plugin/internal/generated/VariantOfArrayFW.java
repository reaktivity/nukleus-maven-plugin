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

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public class VariantOfArrayFW<V extends Flyweight> extends ArrayFW<V>
{
    public static final EnumWithInt8 KIND_ARRAY32 = EnumWithInt8.SEVEN;

    public static final EnumWithInt8 KIND_ARRAY8 = EnumWithInt8.EIGHT;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final Array32FW<V> array32RO;

    private final Array8FW<V> array8RO;

    public VariantOfArrayFW(
        V type)
    {
        array32RO = new Array32FW<>(type);
        array8RO = new Array8FW<>(type);
    }

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public ArrayFW<V> get()
    {
        switch (kind())
        {
        case SEVEN:
            return array32RO;
        case EIGHT:
            return array8RO;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantOfArrayFW<V> tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.tryWrap(buffer, offset, maxLimit);
        if (enumWithInt8 == null)
        {
            return null;
        }
        switch (kind())
        {
        case SEVEN:
            if (array32RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case EIGHT:
            if (array8RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
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
    public VariantOfArrayFW<V> wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case SEVEN:
            array32RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case EIGHT:
            array8RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        default:
            break;
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        return get().toString();
    }

    @Override
    public int limit()
    {
        return get().limit();
    }

    @Override
    public int length()
    {
        return get().length();
    }

    @Override
    public int fieldCount()
    {
        return get().fieldCount();
    }

    @Override
    public int fieldsOffset()
    {
        return get().fieldsOffset();
    }

    @Override
    public int maxLength()
    {
        return get().maxLength();
    }

    @Override
    public void forEach(
        Consumer<V> consumer)
    {
        get().forEach(consumer);
    }

    @Override
    public boolean anyMatch(
        Predicate<V> predicate)
    {
        return get().anyMatch(predicate);
    }

    @Override
    public V matchFirst(
        Predicate<V> predicate)
    {
        return get().matchFirst(predicate);
    }

    @Override
    public boolean isEmpty()
    {
        return get().isEmpty();
    }

    @Override
    public DirectBuffer items()
    {
        return get().items();
    }

    @Override
    protected void maxLength(
        int maxLength)
    {
        get().maxLength(maxLength);
    }

    public static final class Builder<B extends Flyweight.Builder<V>, V extends Flyweight>
        extends ArrayFW.Builder<VariantOfArrayFW<V>, B, V>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final Array32FW.Builder<B, V> array32RW;

        private final Array8FW.Builder<B, V> array8RW;

        public Builder(
            B itemRW,
            V itemRO)
        {
            super(new VariantOfArrayFW<>(itemRO));
            array32RW = new Array32FW.Builder<>(itemRW, itemRO);
            array8RW = new Array8FW.Builder<>(itemRW, itemRO);
        }

        @Override
        public Builder<B, V> item(
            Consumer<B> consumer)
        {
            array32RW.item(consumer);
            limit(array32RW.limit());
            return this;
        }

        @Override
        public Builder<B, V> items(
            DirectBuffer buffer,
            int srcOffset,
            int length,
            int fieldCount,
            int maxLength)
        {
            array32RW.items(buffer, srcOffset, length, fieldCount, maxLength);
            limit(array32RW.limit());
            return this;
        }

        @Override
        public int fieldsOffset()
        {
            return array32RW.fieldsOffset();
        }

        @Override
        public Builder<B, V> wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            kind(KIND_ARRAY32);
            array32RW.wrap(buffer, limit(), maxLimit);
            return this;
        }

        @Override
        public VariantOfArrayFW<V> build()
        {
            Array32FW array32 = array32RW.build();
            long length = Math.max(array32.length(), array32.fieldCount());
            int highestByteIndex = Long.numberOfTrailingZeros(Long.highestOneBit(length)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
            case 8:
                enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
                enumWithInt8RW.set(KIND_ARRAY8);
                int fieldCount = array32.fieldCount();
                array8RW.wrap(buffer(), enumWithInt8RW.limit(), maxLimit());
                array8RW.items(array32.items(), 0, array32.items().capacity(), fieldCount, array32.maxLength());
                limit(array8RW.limit());
                break;
            case 1:
            case 2:
            case 3:
                limit(array32.limit());
                break;
            default:
                throw new IllegalArgumentException("Illegal length: " + length);
            }
            final VariantOfArrayFW<V> variant = super.build();
            variant.maxLength(array32.maxLength());
            return variant;
        }

        public Builder<B, V> kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }
    }
}
