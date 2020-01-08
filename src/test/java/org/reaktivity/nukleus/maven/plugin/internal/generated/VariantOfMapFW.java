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
import org.reaktivity.reaktor.internal.test.types.VariantFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantOfMapFW<KV extends VariantFW<?, ?>, VV extends VariantFW<?, ?>>
    extends VariantFW<EnumWithInt8, MapFW>
{
    public static final EnumWithInt8 KIND_MAP32 = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_MAP16 = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_MAP8 = EnumWithInt8.THREE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final Map32FW<KV, VV> map32RO;

    private final Map16FW<KV, VV> map16RO;

    private final Map8FW<KV, VV> map8RO;

    public VariantOfMapFW(
        KV keyType,
        VV valueType)
    {
        map32RO = new Map32FW<>(keyType, valueType);
        map16RO = new Map16FW<>(keyType, valueType);
        map8RO = new Map8FW<>(keyType, valueType);
    }

    @Override
    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    @Override
    public MapFW<KV, VV> get()
    {
        switch (kind())
        {
        case ONE:
            return map32RO;
        case TWO:
            return map16RO;
        case THREE:
            return map8RO;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public MapFW<KV, VV> getAs(
        EnumWithInt8 kind,
        int kindPadding)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public VariantOfMapFW<KV, VV> tryWrap(
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
            if (map32RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case TWO:
            if (map16RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case THREE:
            if (map8RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
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
    public VariantOfMapFW<KV, VV> wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            map32RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case TWO:
            map16RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case THREE:
            map8RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        default:
            break;
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public VariantOfMapFW<KV, VV> wrapWithKindPadding(
        DirectBuffer buffer,
        int elementsOffset,
        int maxLimit,
        int kindPadding)
    {
        throw new UnsupportedOperationException();
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

    public static final class Builder
        <KB extends VariantFW.Builder<KV, KK, KO>, KV extends VariantFW<KK, KO>, KK, KO extends Flyweight,
        VB extends VariantFW.Builder<VV, VK, VO>, VV extends VariantFW<VK, VO>, VK, VO extends Flyweight>
        extends VariantFW.Builder<VariantOfMapFW<KV, VV>, EnumWithInt8, MapFW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final Map32FW.Builder<KB, KV, KK, KO, VB, VV, VK, VO> map32RW;

        private final Map16FW.Builder<KB, KV, KK, KO, VB, VV, VK, VO> map16RW;

        private final Map8FW.Builder<KB, KV, KK, KO, VB, VV, VK, VO> map8RW;

        public Builder(
            KB keyRW,
            KV keyRO,
            VB valueRW,
            VV valueRO)
        {
            super(new VariantOfMapFW<>(keyRO, valueRO));
            map32RW = new Map32FW.Builder<>(keyRW, keyRO, valueRW, valueRO);
            map16RW = new Map16FW.Builder<>(keyRW, keyRO, valueRW, valueRO);
            map8RW = new Map8FW.Builder<>(keyRW, keyRO, valueRW, valueRO);
        }

        @Override
        public EnumWithInt8 maxKind()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public EnumWithInt8 kindFromLength(
            int length)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public VariantOfMapFW<KV, VV> build(
            int maxLimit)
        {
            throw new UnsupportedOperationException();
        }

        public Builder<KB, KV, KK, KO, VB, VV, VK, VO> pair(
            KO key,
            VO value)
        {
            map32RW.pair(key, value);
            limit(map32RW.limit());
            return this;
        }

        @Override
        public Builder<KB, KV, KK, KO, VB, VV, VK, VO> wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            kind(KIND_MAP32);
            map32RW.wrap(buffer, limit(), maxLimit);
            return this;
        }

        public VariantOfMapFW<KV, VV> build()
        {
            Map32FW map32 = map32RW.build();
            long length = Math.max(map32.length(), map32.fieldCount());
            int highestByteIndex = Long.numberOfTrailingZeros(Long.highestOneBit(length)) >> 3;
            int fieldCount = map32.fieldCount();
            switch (highestByteIndex)
            {
            case 0:
            case 8:
                enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
                enumWithInt8RW.set(KIND_MAP8);
                map8RW.wrap(buffer(), enumWithInt8RW.limit(), maxLimit());
                map8RW.pairs(map32.pairs(), 0, map32.pairs().capacity(), fieldCount);
                limit(map8RW.build().limit());
                break;
            case 1:
                enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
                enumWithInt8RW.set(KIND_MAP16);
                map16RW.wrap(buffer(), enumWithInt8RW.limit(), maxLimit());
                map16RW.pairs(map32.pairs(), 0, map32.pairs().capacity(), fieldCount);
                limit(map16RW.build().limit());
                break;
            case 2:
            case 3:
                limit(map32.limit());
                break;
            default:
                throw new IllegalArgumentException("Illegal length: " + length);
            }
            return super.build();
        }

        @Override
        public Builder<KB, KV, KK, KO, VB, VV, VK, VO> kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }
    }
}
