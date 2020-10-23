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

import java.nio.charset.Charset;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.ArrayFW;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.String16FW;
import org.reaktivity.reaktor.internal.test.types.String32FW;
import org.reaktivity.reaktor.internal.test.types.String8FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantEnumKindOfStringFW extends StringFW
{
    public static final EnumWithInt8 KIND_STRING8 = EnumWithInt8.NINE;

    public static final EnumWithInt8 KIND_STRING16 = EnumWithInt8.TEN;

    public static final EnumWithInt8 KIND_STRING32 = EnumWithInt8.ELEVEN;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final String8FW string8RO = new String8FW();

    private final String16FW string16RO = new String16FW();

    private final String32FW string32RO = new String32FW();

    private EnumWithInt8 kind;

    @Override
    public int fieldSizeLength()
    {
        return get().fieldSizeLength();
    }

    @Override
    public String asString()
    {
        return get().asString();
    }

    @Override
    public int length()
    {
        return get().length();
    }

    public EnumWithInt8 kind()
    {
        return kind;
    }

    public StringFW get()
    {
        switch (kind())
        {
        case NINE:
            return string8RO;
        case TEN:
            return string16RO;
        case ELEVEN:
            return string32RO;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantEnumKindOfStringFW tryWrap(
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
        kind = enumWithInt8.get();
        switch (kind)
        {
        case NINE:
            if (string8RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case TEN:
            if (string16RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        case ELEVEN:
            if (string32RO.tryWrap(buffer, offset + enumWithInt8.sizeof(), maxLimit) == null)
            {
                return null;
            }
            break;
        default:
            return null;
        }
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public VariantEnumKindOfStringFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        final EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, offset, maxLimit);
        kind = enumWithInt8.get();
        switch (kind)
        {
        case NINE:
            string8RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case TEN:
            string16RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        case ELEVEN:
            string32RO.wrap(buffer, offset + enumWithInt8.sizeof(), maxLimit);
            break;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind);
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public VariantEnumKindOfStringFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit,ArrayFW array)
    {
        final int fieldsOffset = array.fieldsOffset();
        super.wrap(buffer, fieldsOffset, maxLimit);
        final EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, fieldsOffset, maxLimit);
        final int kindPadding = offset == fieldsOffset ? 0 : offset - fieldsOffset - enumWithInt8.sizeof();
        if (kind == null)
        {
            kind = enumWithInt8.get();
        }
        switch (kind())
        {
        case NINE:
            string8RO.wrap(buffer, enumWithInt8.limit() + kindPadding, maxLimit);
            break;
        case TEN:
            string16RO.wrap(buffer, enumWithInt8.limit() + kindPadding, maxLimit);
            break;
        case ELEVEN:
            string32RO.wrap(buffer, enumWithInt8.limit() + kindPadding, maxLimit);
            break;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind);
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        switch (kind())
        {
        case NINE:
            return String.format("VARIANT_ENUM_KIND_OF_STRING [string8=%s]", string8RO.asString());
        case TEN:
            return String.format("VARIANT_ENUM_KIND_OF_STRING [string16=%s]", string16RO.asString());
        case ELEVEN:
            return String.format("VARIANT_ENUM_KIND_OF_STRING [string32=%s]", string32RO.asString());
        default:
            return String.format("VARIANT_ENUM_KIND_OF_STRING [unknown]");
        }
    }

    @Override
    public int limit()
    {
        return get().limit();
    }

    public static final class Builder extends StringFW.Builder<VariantEnumKindOfStringFW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private ArrayFW.Builder<? extends ArrayFW<VariantEnumKindOfStringFW>,
            ? extends Flyweight.Builder<VariantEnumKindOfStringFW>, VariantEnumKindOfStringFW> array;

        private final String8FW.Builder string8RW = new String8FW.Builder();

        private final String16FW.Builder string16RW = new String16FW.Builder();

        private final String32FW.Builder string32RW = new String32FW.Builder();

        public Builder()
        {
            super(new VariantEnumKindOfStringFW());
        }

        @Override
        public int sizeof()
        {
            final int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            return limit() - offset;
        }

        @Override
        public VariantEnumKindOfStringFW rebuild(
            VariantEnumKindOfStringFW item,
            int maxLength)
        {
            assert array != null;
            EnumWithInt8 rebuildKind = minKind(maxLength);
            VariantEnumKindOfStringFW newItem = item;
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

        public VariantEnumKindOfStringFW.Builder setAsString8(
            StringFW value)
        {
            kind(KIND_STRING8);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String8FW string8 = string8RW.wrap(buffer(), offset, maxLimit()).set(value).build();
            limit(string8.limit());
            return this;
        }

        public VariantEnumKindOfStringFW.Builder setAsString8(
            DirectBuffer srcBuffer,
            int srcOffset,
            int length)
        {
            kind(KIND_STRING8);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String8FW string8 = string8RW.wrap(buffer(), offset, maxLimit()).set(srcBuffer, srcOffset, length).build();
            limit(string8.limit());
            return this;
        }

        public VariantEnumKindOfStringFW.Builder setAsString8(
            String value,
            Charset charset)
        {
            kind(KIND_STRING8);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String8FW string8 = string8RW.wrap(buffer(), offset, maxLimit()).set(value, charset).build();
            limit(string8.limit());
            return this;
        }

        public VariantEnumKindOfStringFW.Builder setAsString16(
            StringFW value)
        {
            kind(KIND_STRING16);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String16FW string16 = string16RW.wrap(buffer(), offset, maxLimit()).set(value).build();
            limit(string16.limit());
            return this;
        }

        public VariantEnumKindOfStringFW.Builder setAsString16(
            DirectBuffer srcBuffer,
            int srcOffset,
            int length)
        {
            kind(KIND_STRING16);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String16FW string16 = string16RW.wrap(buffer(), offset, maxLimit()).set(srcBuffer, srcOffset, length).build();
            limit(string16.limit());
            return this;

        }

        public VariantEnumKindOfStringFW.Builder setAsString16(
            String value,
            Charset charset)
        {
            kind(KIND_STRING16);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String16FW string16 = string16RW.wrap(buffer(), offset, maxLimit()).set(value, charset).build();
            limit(string16.limit());
            return this;
        }

        public VariantEnumKindOfStringFW.Builder setAsString32(
            StringFW value)
        {
            kind(KIND_STRING32);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String32FW string32 = string32RW.wrap(buffer(), offset, maxLimit()).set(value).build();
            limit(string32.limit());
            return this;
        }

        public VariantEnumKindOfStringFW.Builder setAsString32(
            DirectBuffer srcBuffer,
            int srcOffset,
            int length)
        {
            kind(KIND_STRING32);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String32FW string32 = string32RW.wrap(buffer(), offset, maxLimit()).set(srcBuffer, srcOffset, length).build();
            limit(string32.limit());
            return this;
        }

        public VariantEnumKindOfStringFW.Builder setAsString32(
            String value,
            Charset charset)
        {
            kind(KIND_STRING32);
            int offset = array == null || array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String32FW string32 = string32RW.wrap(buffer(), offset, maxLimit()).set(value, charset).build();
            limit(string32.limit());
            return this;
        }

        public EnumWithInt8 maxKind()
        {
            return KIND_STRING32;
        }

        public VariantEnumKindOfStringFW.Builder setAs(
            EnumWithInt8 kind,
            StringFW value)
        {
            switch (kind)
            {
            case NINE:
                setAsString8(value);
                break;
            case TEN:
                setAsString16(value);
                break;
            case ELEVEN:
                setAsString32(value);
                break;
            }
            return this;
        }

        @Override
        public VariantEnumKindOfStringFW.Builder set(
            StringFW value)
        {
            int length = value.length();
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
            case 4:
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

        @Override
        public VariantEnumKindOfStringFW.Builder set(
            DirectBuffer srcBuffer,
            int srcOffset,
            int length)
        {
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
            case 4:
                setAsString8(srcBuffer, srcOffset, length);
                break;
            case 1:
                setAsString16(srcBuffer, srcOffset, length);
                break;
            case 2:
            case 3:
                setAsString32(srcBuffer, srcOffset, length);
                break;
            default:
                throw new IllegalArgumentException("Illegal length: " + length);
            }
            return this;
        }

        @Override
        public VariantEnumKindOfStringFW.Builder set(
            String value,
            Charset charset)
        {
            int length = value.length();
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
            case 4:
                setAsString8(value, charset);
                break;
            case 1:
                setAsString16(value, charset);
                break;
            case 2:
            case 3:
                setAsString32(value, charset);
                break;
            default:
                throw new IllegalArgumentException("Illegal value: " + value);
            }
            return this;
        }

        @Override
        public VariantEnumKindOfStringFW.Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            this.array = null;
            return this;
        }

        @Override
        public VariantEnumKindOfStringFW.Builder wrap(
            ArrayFW.Builder<? extends ArrayFW<VariantEnumKindOfStringFW>, ? extends Flyweight.Builder<VariantEnumKindOfStringFW>,
                VariantEnumKindOfStringFW> array)
        {
            super.wrap(array.buffer(), array.fieldsOffset(), array.maxLimit());
            limit(array.limit());
            this.array = array;
            return this;
        }

        @Override
        public void reset(
            ArrayFW.Builder<? extends ArrayFW<VariantEnumKindOfStringFW>, ? extends Flyweight.Builder<VariantEnumKindOfStringFW>,
                VariantEnumKindOfStringFW> array)
        {
            flyweight().kind = null;
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

        private VariantEnumKindOfStringFW.Builder kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }
    }
}

