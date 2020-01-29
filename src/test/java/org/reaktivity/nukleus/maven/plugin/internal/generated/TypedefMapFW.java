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
import org.reaktivity.reaktor.internal.test.types.VariantFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class TypedefMapFW<VV extends Flyweight> extends VariantFW<EnumWithInt8, MapFW>
{
    public static final EnumWithInt8 KIND_MAP32 = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_MAP16 = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_MAP8 = EnumWithInt8.THREE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final Map32FW<VariantEnumKindWithString32FW, VV> map32RO;

    private final Map16FW<VariantEnumKindWithString32FW, VV> map16RO;

    private final Map8FW<VariantEnumKindWithString32FW, VV> map8RO;

    public TypedefMapFW(
        VariantEnumKindWithString32FW keyType,
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
    public MapFW<VariantEnumKindWithString32FW, VV> get()
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
    public MapFW<VariantEnumKindWithString32FW, VV> getAs(
        EnumWithInt8 kind,
        int kindPadding)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypedefMapFW<VV> tryWrap(
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
    public TypedefMapFW<VV> wrap(
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
    public TypedefMapFW<VV> wrapWithKindPadding(
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

    public static final class Builder<VB extends Flyweight.Builder<VV>, VV extends Flyweight>
        extends VariantFW.Builder<TypedefMapFW<VV>, EnumWithInt8, MapFW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final Map32FW.Builder<VariantEnumKindWithString32FW.Builder, VariantEnumKindWithString32FW, VB, VV> map32RW;

        private final Map16FW.Builder<VariantEnumKindWithString32FW.Builder, VariantEnumKindWithString32FW, VB, VV> map16RW;

        private final Map8FW.Builder<VariantEnumKindWithString32FW.Builder, VariantEnumKindWithString32FW, VB, VV> map8RW;

        public Builder(
            VariantEnumKindWithString32FW.Builder keyRW,
            VariantEnumKindWithString32FW keyRO,
            VB valueRW,
            VV valueRO)
        {
            super(new TypedefMapFW<>(keyRO, valueRO));
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

        public Builder<VB, VV> entry(
            Consumer<VariantEnumKindWithString32FW.Builder> key,
            Consumer<VB> value)
        {
            map32RW.entry(key, value);
            limit(map32RW.limit());
            return this;
        }

        @Override
        public TypedefMapFW<VV> build(
            int maxLimit)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<VB, VV> setAs(
            EnumWithInt8 kind,
            MapFW value,
            int kindPadding)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<VB, VV> set(
            MapFW value)
        {
            map32RW.entries(value.entries(), 0, value.entries().capacity(), value.fieldCount());
            limit(map32RW.limit());
            return this;
        }

        @Override
        public Builder<VB, VV> wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            kind(KIND_MAP32);
            map32RW.wrap(buffer, limit(), maxLimit);
            return this;
        }

        @Override
        public TypedefMapFW<VV> build()
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
                map8RW.entries(map32.entries(), 0, map32.entries().capacity(), fieldCount);
                limit(map8RW.build().limit());
                break;
            case 1:
                enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
                enumWithInt8RW.set(KIND_MAP16);
                map16RW.wrap(buffer(), enumWithInt8RW.limit(), maxLimit());
                map16RW.entries(map32.entries(), 0, map32.entries().capacity(), fieldCount);
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
        public Builder<VB, VV> kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }
    }
}