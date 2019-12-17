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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantOfVariantArrayFW<V extends VariantFW<?, O>, O extends Flyweight>
    extends VariantFW<EnumWithInt8, VariantArrayFW<V>>
{
    public static final EnumWithInt8 KIND_ARRAY32 = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_ARRAY8 = EnumWithInt8.TWO;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final VariantArray32FW<V> array32RO;

    private final VariantArray8FW<V> array8RO;

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public VariantOfVariantArrayFW(
        V type)
    {
        array32RO = new VariantArray32FW<>(type);
        array8RO = new VariantArray8FW<>(type);
    }

    public VariantArrayFW<V> get()
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
    public VariantOfVariantArrayFW<V, O> tryWrap(
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
    public VariantOfVariantArrayFW<V, O> wrap(
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

    public static final class Builder<B extends VariantFW.Builder<V, K, O>, V extends VariantFW<K, O>,
        K, O extends Flyweight> extends VariantFW.Builder<VariantOfVariantArrayFW<V, O>, EnumWithInt8, VariantArrayFW<V>>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final VariantArray32FW.Builder<B, V, K, O> array32RW;

        private final VariantArray8FW.Builder<B, V, K, O> array8RW;

        protected Builder(
            B itemRW,
            V itemRO)
        {
            super(new VariantOfVariantArrayFW<>(itemRO));
            array32RW = new VariantArray32FW.Builder<>(itemRW, itemRO);
            array8RW = new VariantArray8FW.Builder<>(itemRW, itemRO);
        }

        @Override
        public Builder<B, V, K, O> wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            kind(KIND_ARRAY32);
            array32RW.wrap(buffer, limit(), maxLimit);
            return this;
        }

        public Builder<B, V, K, O> kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }

        public Builder<B, V, K, O> item(
            O item)
        {
            array32RW.item(item);
            limit(array32RW.limit());
            return this;
        }

        @Override
        public VariantOfVariantArrayFW<V, O> build()
        {
            VariantArray32FW array32 = array32RW.build();
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

        public VariantOfVariantArrayFW<V, O> build(
            int maxLimit)
        {
            flyweight().wrap(buffer(), offset(), maxLimit);
            return flyweight();
        }
    }
}
