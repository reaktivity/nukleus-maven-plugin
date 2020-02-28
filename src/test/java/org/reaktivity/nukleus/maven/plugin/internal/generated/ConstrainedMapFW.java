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
import java.util.function.Function;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public final class ConstrainedMapFW<V extends Flyweight> extends MapFW<VariantEnumKindOfStringFW, V>
{
    private final VariantOfMapFW<VariantEnumKindOfStringFW, V> variantOfMapRO;

    public ConstrainedMapFW(
        VariantEnumKindOfStringFW keyRO,
        V valueRO)
    {
        variantOfMapRO = new VariantOfMapFW<>(keyRO, valueRO);
    }

    @Override
    public int length()
    {
        return variantOfMapRO.get().length();
    }

    @Override
    public int fieldCount()
    {
        return variantOfMapRO.get().fieldCount();
    }

    @Override
    public DirectBuffer entries()
    {
        return variantOfMapRO.get().entries();
    }

    @Override
    public void forEach(
        Function<VariantEnumKindOfStringFW, Consumer<V>> consumer)
    {
        variantOfMapRO.get().forEach(consumer);
    }

    @Override
    public ConstrainedMapFW<V> tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        if (variantOfMapRO.tryWrap(buffer, offset, maxLimit) == null)
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
    public ConstrainedMapFW<V> wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        variantOfMapRO.wrap(buffer, offset, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public int limit()
    {
        return variantOfMapRO.limit();
    }

    @Override
    public String toString()
    {
        return String.format("ConstrainedMap[%d, %d]", variantOfMapRO.get().length(), variantOfMapRO.get().fieldCount());
    }

    public static final class Builder<V extends Flyweight, VB extends Flyweight.Builder<V>>
        extends MapFW.Builder<ConstrainedMapFW<V>, VariantEnumKindOfStringFW, V, VariantEnumKindOfStringFW.Builder, VB>
    {
        private final VariantOfMapFW.Builder<VariantEnumKindOfStringFW, V, VariantEnumKindOfStringFW.Builder, VB>
            variantOfMapRW;

        public Builder(
            VariantEnumKindOfStringFW keyRO,
            V valueRO,
            VariantEnumKindOfStringFW.Builder keyRW,
            VB valueRW)
        {
            super(new ConstrainedMapFW<>(keyRO, valueRO));
            variantOfMapRW = new VariantOfMapFW.Builder<>(keyRO, valueRO, keyRW, valueRW);
        }

        @Override
        public Builder<V, VB> wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            variantOfMapRW.wrap(buffer, offset, maxLimit);
            return this;
        }

        @Override
        public Builder<V, VB> entry(
            Consumer<VariantEnumKindOfStringFW.Builder> key,
            Consumer<VB> value)
        {
            variantOfMapRW.entry(key, value);
            limit(variantOfMapRW.limit());
            return this;
        }

        @Override
        public Builder<V, VB> entries(
            DirectBuffer buffer,
            int srcOffset,
            int length,
            int fieldCount)
        {
            variantOfMapRW.entries(buffer, srcOffset, length, fieldCount);
            limit(variantOfMapRW.limit());
            return this;
        }

        @Override
        public ConstrainedMapFW<V> build()
        {
            limit(variantOfMapRW.build().limit());
            return super.build();
        }
    }
}
