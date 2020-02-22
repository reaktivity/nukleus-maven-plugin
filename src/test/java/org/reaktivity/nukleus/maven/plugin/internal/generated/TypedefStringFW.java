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

public final class TypedefStringFW extends Flyweight
{
    public static final EnumWithInt8 KIND_STRING8 = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_STRING16 = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_STRING32 = EnumWithInt8.THREE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final String8FW string8RO = new String8FW();

    private final String16FW string16RO = new String16FW();

    private final String32FW string32RO = new String32FW();

    private EnumWithInt8 kind;

    public EnumWithInt8 kind()
    {
        return kind;
    }

    public StringFW get()
    {
        switch (kind())
        {
        case ONE:
            return string8RO;
        case TWO:
            return string16RO;
        case THREE:
            return string32RO;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public TypedefStringFW tryWrap(
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
        kind = enumWithInt8.get();

        switch (kind)
        {
        case ONE:
            if (string8RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case TWO:
            if (string16RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case THREE:
            if (string32RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
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
    public TypedefStringFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, offset, maxLimit);
        kind = enumWithInt8.get();

        switch (kind)
        {
        case ONE:
            string8RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case TWO:
            string16RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case THREE:
            string32RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        default:
            break;
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public TypedefStringFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit,
        ArrayFW array)
    {
        final int fieldsOffset = array.fieldsOffset();
        super.wrap(buffer, fieldsOffset, maxLimit);
        final EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, fieldsOffset, maxLimit);
        final int kindPadding = offset == fieldsOffset ? 0 :
            offset - fieldsOffset - enumWithInt8.sizeof();
        if (kind == null)
        {
            kind = enumWithInt8.get();
        }
        switch (kind())
        {
        case ONE:
            string8RO.wrap(buffer, enumWithInt8.limit() + kindPadding, maxLimit);
            break;
        case TWO:
            string16RO.wrap(buffer, enumWithInt8.limit() + kindPadding, maxLimit);
            break;
        case THREE:
            string32RO.wrap(buffer, enumWithInt8.limit() + kindPadding, maxLimit);
            break;
        default:
            break;
        }
        return this;
    }

    @Override
    public String toString()
    {
        switch (kind())
        {
        case ONE:
            return String.format("TYPEDEF_STRING [string8=%s]", string8RO.asString());
        case TWO:
            return String.format("TYPEDEF_STRING [string16=%s]", string16RO.asString());
        case THREE:
            return String.format("TYPEDEF_STRING [string32=%s]", string32RO.asString());
        default:
            return String.format("TYPEDEF_STRING [unknown]");
        }
    }

    @Override
    public int limit()
    {
        return get().limit();
    }

    public static final class Builder extends Flyweight.Builder<TypedefStringFW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final String8FW.Builder string8RW = new String8FW.Builder();

        private final String16FW.Builder string16RW = new String16FW.Builder();

        private final String32FW.Builder string32RW = new String32FW.Builder();

        private ArrayFW.Builder<? extends ArrayFW<TypedefStringFW>,
            ? extends Flyweight.Builder<TypedefStringFW>, TypedefStringFW> array;

        public Builder()
        {
            super(new TypedefStringFW());
        }

        public EnumWithInt8 maxKind()
        {
            return KIND_STRING32;
        }

        public Builder set(
            StringFW value)
        {
            int length = value.length();
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
                setAsString8(value);
                break;
            case 1:
                setAsString16(value);
                break;
            case 2:
            case 3:
                setAsString32(value);
                break;
            default:
                throw new IllegalArgumentException("Illegal value: " + value);
            }
            return this;
        }

        public Builder setAs(
            EnumWithInt8 kind,
            StringFW value)
        {
            switch (kind)
            {
            case ONE:
                setAsString8(value);
                break;
            case TWO:
                setAsString16(value);
                break;
            case THREE:
                setAsString32(value);
                break;
            }
            return this;
        }

        public Builder setAsString8(
            StringFW value)
        {
            kind(KIND_STRING8);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String8FW string8 = string8RW.wrap(buffer(), offset, maxLimit())
                .set(value)
                .build();
            limit(string8.limit());
            return this;
        }

        public Builder setAsString16(
            StringFW value)
        {
            kind(KIND_STRING16);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String16FW string16 = string16RW.wrap(buffer(), offset, maxLimit())
                .set(value)
                .build();
            limit(string16.limit());
            return this;
        }

        public Builder setAsString32(
            StringFW value)
        {
            kind(KIND_STRING32);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String32FW string32 = string32RW.wrap(buffer(), offset, maxLimit())
                .set(value)
                .build();
            limit(string32.limit());
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            this.array = null;
            return this;
        }

        @Override
        public Builder wrap(
            ArrayFW.Builder<? extends ArrayFW<TypedefStringFW>, ? extends Flyweight.Builder<TypedefStringFW>,
                TypedefStringFW> array)
        {
            super.wrap(array.buffer(), array.fieldsOffset(), array.maxLimit());
            limit(array.limit());
            this.array = array;
            return this;
        }

        @Override
        public int sizeof()
        {
            final int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            return limit() - offset;
        }

        @Override
        public TypedefStringFW rebuild(
            TypedefStringFW item,
            int maxLength)
        {
            assert array != null;
            EnumWithInt8 rebuildKind = minKind(maxLength);
            TypedefStringFW newItem = item;
            StringFW value = item.get();
            final int valueOffset = array.limit() == array.fieldsOffset() ?
                array.fieldsOffset() + item.enumWithInt8RO.sizeof() : array.limit();

            if (value.offset() != valueOffset || !item.kind().equals(rebuildKind))
            {
                setAs(rebuildKind, value);
                newItem = flyweight().wrap(buffer(), array.limit(), limit(), array.flyweight());
            }
            return newItem;
        }

        private EnumWithInt8 minKind(
            int length)
        {
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
                return KIND_STRING8;
            case 1:
                return KIND_STRING16;
            case 2:
            case 3:
                return KIND_STRING32;
            default:
                throw new IllegalArgumentException("Illegal length: " + length);
            }
        }

        private void kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
        }
    }
}
