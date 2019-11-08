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

public final class VariantOfListFW extends ListFW
{
    public static final EnumWithInt8 KIND_ONE = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_TWO = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_THREE = EnumWithInt8.THREE;

    public static final byte MISSING_FIELD_PLACEHOLDER = 0x40;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final List32FW list32RO = new List32FW();

    private final List8FW list8RO = new List8FW();

    private final List0FW list0RO = new List0FW();

    private List32FW getAsList32()
    {
        return list32RO;
    }

    private List8FW getAsList8()
    {
        return list8RO;
    }

    private List0FW getAsList0()
    {
        return list0RO;
    }

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
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

    public DirectBuffer fields()
    {
        return get().fields();
    }

    public ListFW get()
    {
        switch (kind())
        {
        case ONE:
            return getAsList32();
        case TWO:
            return getAsList8();
        case THREE:
            return getAsList0();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantOfListFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        Flyweight enumWithInt8 = enumWithInt8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            list32RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case TWO:
            list8RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case THREE:
            list0RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        default:
            break;
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public VariantOfListFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        Flyweight enumWithInt8 = enumWithInt8RO.tryWrap(buffer, offset, maxLimit);
        if (enumWithInt8 == null)
        {
            return null;
        }
        switch (kind())
        {
        case ONE:
            if (list32RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case TWO:
            if (list8RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case THREE:
            if (list0RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
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
    public int limit()
    {
        return get().limit();
    }

    public static final class Builder extends Flyweight.Builder<VariantOfListFW>
    {
        private final List32FW.Builder list32RW = new List32FW.Builder();

        private final List8FW.Builder list8RW = new List8FW.Builder();

        private final List0FW.Builder list0FW = new List0FW.Builder();

        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        public Builder()
        {
            super(new VariantOfListFW());
        }

        private Builder kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }

        public Builder field(
            Flyweight.Builder.Visitor mutator)
        {
            list32RW.field(mutator);
            limit(list32RW.limit());
            return this;
        }

        public Builder setAsList32(
            ListFW list)
        {
            kind(KIND_ONE);
            List32FW.Builder list32 = list32RW.wrap(buffer(), limit(), maxLimit());
            list32.set(list);
            limit(list32.build().limit());
            return this;
        }

        public Builder setAsList8(
            ListFW list)
        {
            kind(KIND_TWO);
            List8FW.Builder list8 = list8RW.wrap(buffer(), limit(), maxLimit());
            list8.set(list);
            limit(list8.build().limit());
            return this;
        }

        public Builder setAsList0(
            ListFW list)
        {
            kind(KIND_THREE);
            List0FW.Builder list0 = list0FW.wrap(buffer(), limit(), maxLimit());
            limit(list0.build().limit());
            return this;
        }

        public Builder set(
            ListFW list)
        {
            int length = Math.max(list.length(), list.fieldCount());
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
                setAsList8(list);
                break;
            case 1:
            case 2:
            case 3:
                setAsList32(list);
                break;
            case 8:
                setAsList0(list);
                break;
            default:
                throw new IllegalArgumentException("Illegal length: " + length);
            }
            return this;
        }

        private int setList32Fields(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            List32FW list32 = list32RW.build();
            final DirectBuffer fields = list32.fields();
            buffer.putBytes(offset, fields, 0, fields.capacity());
            return fields.capacity();
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            kind(KIND_ONE);
            list32RW.wrap(buffer, limit(), maxLimit);
            return this;
        }

        @Override
        public VariantOfListFW build()
        {
            EnumWithInt8FW kind = enumWithInt8RW.build();
            if (kind.get() == KIND_ONE)
            {
                List32FW list32 = list32RW.build();
                long length = Math.max(list32.length(), list32.fieldCount());
                int highestByteIndex = Long.numberOfTrailingZeros(Long.highestOneBit(length)) >> 3;
                switch (highestByteIndex)
                {
                case 0:
                    enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
                    enumWithInt8RW.set(KIND_TWO);
                    list8RW.wrap(buffer(), enumWithInt8RW.limit(), maxLimit());
                    list8RW.fields(list32.fieldCount(), this::setList32Fields);
                    limit(list8RW.build().limit());
                    break;
                case 1:
                case 2:
                case 3:
                    limit(list32.limit());
                    break;
                case 8:
                    enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
                    enumWithInt8RW.set(KIND_THREE);
                    list0FW.wrap(buffer(), enumWithInt8RW.limit(), maxLimit());
                    limit(list0FW.build().limit());
                    break;
                default:
                    throw new IllegalArgumentException("Illegal length: " + length);
                }
            }
            return super.build();
        }
    }
}
