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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantOfOctetsFW extends VariantOfFW<EnumWithInt8, BoundedOctetsFW>
{
    public static final EnumWithInt8 KIND_BOUNDED_OCTETS32 = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_BOUNDED_OCTETS16 = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_BOUNDED_OCTETS8 = EnumWithInt8.THREE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final BoundedOctets32FW boundedOctets32RO = new BoundedOctets32FW();

    private final BoundedOctets16FW boundedOctets16RO = new BoundedOctets16FW();

    private final BoundedOctets8FW boundedOctets8RO = new BoundedOctets8FW();

    @Override
    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    @Override
    public BoundedOctetsFW get()
    {
        switch (kind())
        {
        case ONE:
            return boundedOctets32RO;
        case TWO:
            return boundedOctets16RO;
        case THREE:
            return boundedOctets8RO;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantOfOctetsFW tryWrap(
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
            if (boundedOctets32RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case TWO:
            if (boundedOctets16RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case THREE:
            if (boundedOctets8RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
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
    public VariantOfOctetsFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            boundedOctets32RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case TWO:
            boundedOctets16RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case THREE:
            boundedOctets8RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
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

    public static final class Builder extends VariantOfFW.Builder<VariantOfOctetsFW, EnumWithInt8, BoundedOctetsFW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final BoundedOctets32FW.Builder boundedOctets32RW = new BoundedOctets32FW.Builder();

        private final BoundedOctets16FW.Builder boundedOctets16RW = new BoundedOctets16FW.Builder();

        private final BoundedOctets8FW.Builder boundedOctets8RW = new BoundedOctets8FW.Builder();

        public Builder()
        {
            super(new VariantOfOctetsFW());
        }

        public Builder setAsBoundedOctets32(
            BoundedOctetsFW value)
        {
            kind(KIND_BOUNDED_OCTETS32);
            BoundedOctets32FW.Builder boundedOctets32 = boundedOctets32RW.wrap(buffer(), limit(), maxLimit());
            boundedOctets32.set(value);
            limit(boundedOctets32.build().limit());
            return this;
        }

        public Builder setAsBoundedOctets16(
            BoundedOctetsFW value)
        {
            kind(KIND_BOUNDED_OCTETS16);
            BoundedOctets16FW.Builder boundedOctets16 = boundedOctets16RW.wrap(buffer(), limit(), maxLimit());
            boundedOctets16.set(value);
            limit(boundedOctets16.build().limit());
            return this;
        }

        public Builder setAsBoundedOctets8(
            BoundedOctetsFW value)
        {
            kind(KIND_BOUNDED_OCTETS8);
            BoundedOctets8FW.Builder boundedOctets8 = boundedOctets8RW.wrap(buffer(), limit(), maxLimit());
            boundedOctets8.set(value);
            limit(boundedOctets8.build().limit());
            return this;
        }

        @Override
        public Builder set(
            BoundedOctetsFW value)
        {
            int length = value.length();
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
                setAsBoundedOctets8(value);
                break;
            case 1:
                setAsBoundedOctets16(value);
                break;
            case 2:
            case 3:
                setAsBoundedOctets32(value);
                break;
            default:
                throw new IllegalArgumentException("Illegal value: " + value);
            }
            return this;
        }

        @Override
        public Builder setAs(
            EnumWithInt8 kind,
            BoundedOctetsFW value,
            ArrayFW.Builder array)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public EnumWithInt8 maxKind()
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
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }

        @Override
        public Builder kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }
    }
}
