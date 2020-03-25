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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantWithVariantCaseFW  extends Flyweight
{
    public static final EnumWithInt8 KIND_ONE = EnumWithInt8.ONE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final VariantUint8KindOfUint64FW variantUint8KindOfUint64RO = new VariantUint8KindOfUint64FW();

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public VariantUint8KindOfUint64FW getAsVariantUint8KindOfUint64()
    {
        return variantUint8KindOfUint64RO;
    }

    public long get()
    {
        switch (kind())
        {
        case ONE:
            return getAsVariantUint8KindOfUint64().get();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantWithVariantCaseFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        final EnumWithInt8FW enumWithInt8 = enumWithInt8RO.tryWrap(buffer, offset, maxLimit);
        if (enumWithInt8 == null)
        {
            return null;
        }

        switch (kind())
        {
        case ONE:
            if (variantUint8KindOfUint64RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
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
    public VariantWithVariantCaseFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        final EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            variantUint8KindOfUint64RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
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
        switch (kind())
        {
        case ONE:
            return variantUint8KindOfUint64RO.toString();
        default:
            return String.format("VARIANT_WITH_VARIANT_CASE [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ONE:
            return variantUint8KindOfUint64RO.limit();
        default:
            return enumWithInt8RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantWithVariantCaseFW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final VariantUint8KindOfUint64FW.Builder variantUint8KindOfUint64RW = new VariantUint8KindOfUint64FW.Builder();

        public Builder()
        {
            super(new VariantWithVariantCaseFW());
        }

        public Builder setAsVariantUint8KindOfUint64(
            long value)
        {
            kind(KIND_ONE);
            variantUint8KindOfUint64RW.wrap(buffer(), limit(), maxLimit());
            variantUint8KindOfUint64RW.set(value);
            limit(variantUint8KindOfUint64RW.limit());
            return this;
        }

        public Builder set(
            long value)
        {
            int highestByteIndex = Long.numberOfTrailingZeros(Long.highestOneBit(value)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                setAsVariantUint8KindOfUint64(value);
                break;
            default:
                throw new IllegalArgumentException("Illegal value: " + value);
            }
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }

        private Builder kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }
    }
}
