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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantOfArrayFW<T extends Flyweight & VariantFW> extends ArrayFW<T, VariantOfArrayFW> implements VariantFW
{
    public static final EnumWithInt8 KIND_ARRAY32 = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_ARRAY8 = EnumWithInt8.TWO;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final Array32FW<T> array32RO;

    private final Array8FW<T> array8RO;

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public VariantOfArrayFW(
        T type)
    {
        array32RO = new Array32FW<>(type);
        array8RO = new Array8FW<>(type);
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
    public VariantOfArrayFW forEach(Consumer<T> consumer)
    {
        return null;
    }

    @Override
    public DirectBuffer items()
    {
        return get().items();
    }

    public ArrayFW<T, ? extends ArrayFW> get()
    {
        switch (kind())
        {
        case ONE:
            return array32RO;
        case TWO:
            return array8RO;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantOfArrayFW<T> tryWrap(
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
        case ONE:
            if (array32RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case TWO:
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
    public VariantOfArrayFW<T> wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            array32RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case TWO:
            array8RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
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
        return get().limit();
    }

    public static final class Builder<B extends Flyweight.Builder & VariantFW.Builder<T, O, EnumWithInt8>,
        T extends Flyweight & VariantFW<O, EnumWithInt8>, O extends Flyweight>
        extends Flyweight.Builder<VariantOfArrayFW> implements VariantFW.Builder<T, O, EnumWithInt8>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final Array32FW.Builder<B, T, O, EnumWithInt8> array32RW;

        private final Array8FW.Builder<B, T, O, EnumWithInt8> array8RW;

        protected Builder(
            B itemRW,
            T itemRO)
        {
            super(new VariantOfArrayFW<>(itemRO));
            array32RW = new Array32FW.Builder<>(itemRW, itemRO);
            array8RW = new Array8FW.Builder<>(itemRW, itemRO);
        }

        @Override
        public Builder<B, T, O> wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            kind(KIND_ARRAY32);
            array32RW.wrap(buffer, limit(), maxLimit);
            return this;
        }

        public Builder kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }

        public Builder<B, T, O> item(
            O item)
        {
            array32RW.item(item);
            limit(array32RW.limit());
            return this;
        }

        @Override
        public VariantOfArrayFW build()
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
                array8RW.items(array32.items(), 0, array32.items().capacity(), fieldCount);
                limit(array8RW.build().limit());
                break;
            case 1:
            case 2:
            case 3:
                limit(array32.limit());
                break;
            default:
                throw new IllegalArgumentException("Illegal length: " + length);
            }
            return super.build();
        }
    }
}
