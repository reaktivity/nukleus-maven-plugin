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


import static java.nio.charset.StandardCharsets.UTF_8;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class TypedefStringFW extends VariantOfFW<EnumWithInt8, StringFW>
{
    public static final EnumWithInt8 KIND_STRING8 = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_STRING16 = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_STRING32 = EnumWithInt8.THREE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final String8FW string8RO = new String8FW();

    private final String16FW string16RO = new String16FW();

    private final String32FW string32RO = new String32FW();

    private EnumWithInt8 kind;

    @Override
    public EnumWithInt8 kind()
    {
        return kind;
    }

    @Override
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

    public static final class Builder extends VariantOfFW.Builder<TypedefStringFW, EnumWithInt8, StringFW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final String8FW.Builder string8RW = new String8FW.Builder();

        private final String16FW.Builder string16RW = new String16FW.Builder();

        private final String32FW.Builder string32RW = new String32FW.Builder();

        public Builder()
        {
            super(new TypedefStringFW());
        }

        @Override
        public EnumWithInt8 maxKind()
        {
            return KIND_STRING32;
        }

        @Override
        public int sizeof(
            ArrayFW.Builder array)
        {
            final int offset = array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            return limit() - offset;
        }

        @Override
        public EnumWithInt8 kindFromLength(
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

        @Override
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

        public Builder setAsString8(
            StringFW value)
        {
            kind(KIND_STRING8);
            String8FW.Builder string8 = string8RW.wrap(buffer(), enumWithInt8RW.limit(), maxLimit());
            string8.set(value.asString(), UTF_8);
            String8FW string8RO = string8.build();
            limit(string8RO.limit());
            return this;
        }

        public Builder setAsString16(
            StringFW value)
        {
            kind(KIND_STRING16);
            String16FW.Builder string16 = string16RW.wrap(buffer(), enumWithInt8RW.limit(), maxLimit());
            string16.set(value.asString(), UTF_8);
            String16FW string16RO = string16.build();
            limit(string16RO.limit());
            return this;
        }

        public Builder setAsString32(
            StringFW value)
        {
            kind(KIND_STRING32);
            String32FW.Builder string32 = string32RW.wrap(buffer(), enumWithInt8RW.limit(), maxLimit());
            string32.set(value.asString(), UTF_8);
            String32FW string32RO = string32.build();
            limit(string32RO.limit());
            return this;
        }

        public Builder setAs(
            EnumWithInt8 kind,
            StringFW value,
            ArrayFW.Builder array)
        {
            switch (kind)
            {
            case ONE:
                setAsString8(value, array);
                break;
            case TWO:
                setAsString16(value, array);
                break;
            case THREE:
                setAsString32(value, array);
                break;
            }
            return this;
        }

        private void setAsString8(
            StringFW value,
            ArrayFW.Builder array)
        {
            kind(KIND_STRING8);
            int offset = array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String8FW string8 = string8RW.wrap(buffer(), offset, maxLimit())
                                         .set(value)
                                         .build();
            limit(string8.limit());
        }

        private void setAsString16(
            StringFW value,
            ArrayFW.Builder array)
        {
            kind(KIND_STRING16);
            int offset = array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String16FW string16 = string16RW.wrap(buffer(), offset, maxLimit())
                                            .set(value)
                                            .build();
            limit(string16.limit());
        }

        private void setAsString32(
            StringFW value,
            ArrayFW.Builder array)
        {
            kind(KIND_STRING32);
            int offset = array.limit() == array.fieldsOffset() ? enumWithInt8RW.limit() : array.limit();
            String32FW string32 = string32RW.wrap(buffer(), offset, maxLimit())
                                            .set(value)
                                            .build();
            limit(string32.limit());
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

        public Builder wrap(
            ArrayFW.Builder array)
        {
            super.wrap(array.buffer(), array.fieldsOffset(), array.maxLimit());
            limit(array.limit());
            return this;
        }

        public TypedefStringFW rebuild(
            TypedefStringFW item,
            int maxLength,
            ArrayFW array,
            ArrayFW.Builder arrayBuilder)
        {
            EnumWithInt8 rebuildKind = kindFromLength(maxLength);
            TypedefStringFW newItem = item;
            StringFW value = item.get();
            final int valueOffset = arrayBuilder.limit() == arrayBuilder.fieldsOffset() ?
                arrayBuilder.fieldsOffset() + item.enumWithInt8RO.sizeof() : arrayBuilder.limit();

            if (value.offset() != valueOffset || !item.kind().equals(rebuildKind))
            {
                setAs(rebuildKind, value, arrayBuilder);
                newItem = build(array, arrayBuilder);
            }
            return newItem;
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
