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


import java.nio.charset.StandardCharsets;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.String16FW;
import org.reaktivity.reaktor.internal.test.types.String32FW;
import org.reaktivity.reaktor.internal.test.types.String8FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
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

    @Override
    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
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
    public StringFW getAs(
        EnumWithInt8 kind,
        int kindPadding)
    {
        switch (kind)
        {
        case ONE:
            return string8RO.wrap(buffer(), enumWithInt8RO.limit() + kindPadding, maxLimit());
        case TWO:
            return string16RO.wrap(buffer(), enumWithInt8RO.limit() + kindPadding, maxLimit());
        case THREE:
            return string32RO.wrap(buffer(), enumWithInt8RO.limit() + kindPadding, maxLimit());
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind);
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
        switch (kind())
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
        switch (kind())
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
    public TypedefStringFW wrapWithKindPadding(
        DirectBuffer buffer,
        int elementsOffset,
        int maxLimit,
        int kindPadding)
    {
        super.wrap(buffer, elementsOffset, maxLimit);
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.wrap(buffer, elementsOffset, maxLimit);
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
            return String.format("TYPEDEFSTRING [string8=%s]", string8RO.asString());
        case TWO:
            return String.format("TYPEDEFSTRING [string16=%s]", string16RO.asString());
        case THREE:
            return String.format("TYPEDEFSTRING [string32=%s]", string32RO.asString());
        default:
            return String.format("TYPEDEFSTRING [unknown]");
        }
    }

    @Override
    public int limit()
    {
        return get().limit();
    }

    public static final class Builder extends VariantOfFW.Builder<TypedefStringFW, EnumWithInt8, StringFW>
    {
        private int size;

        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final String8FW.Builder string8RW = new String8FW.Builder();

        private final String16FW.Builder string16RW = new String16FW.Builder();

        private final String32FW.Builder string32RW = new String32FW.Builder();

        public Builder()
        {
            super(new TypedefStringFW());
        }

        public Builder setAsString8(
            StringFW value,
            int kindPadding)
        {
            kind(KIND_STRING8);
            String8FW.Builder string8 = string8RW.wrap(buffer(), limit() + kindPadding, maxLimit());
            string8.set(value.asString(), StandardCharsets.UTF_8);
            String8FW string8RO = string8.build();
            size = string8RO.sizeof();
            limit(string8RO.limit());
            return this;
        }

        public Builder setAsString16(
            StringFW value,
            int kindPadding)
        {
            kind(KIND_STRING16);
            String16FW.Builder string16 = string16RW.wrap(buffer(), limit() + kindPadding, maxLimit());
            string16.set(value.asString(), StandardCharsets.UTF_8);
            String16FW string16RO = string16.build();
            size = string16RO.sizeof();
            limit(string16RO.limit());
            return this;
        }

        public Builder setAsString32(
            StringFW value,
            int kindPadding)
        {
            kind(KIND_STRING32);
            String32FW.Builder string32 = string32RW.wrap(buffer(), limit() + kindPadding, maxLimit());
            string32.set(value.asString(), StandardCharsets.UTF_8);
            String32FW string32RO = string32.build();
            size = string32RO.sizeof();
            limit(string32RO.limit());
            return this;
        }

        @Override
        public EnumWithInt8 maxKind()
        {
            return KIND_STRING32;
        }

        @Override
        public int size()
        {
            return size;
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
        public TypedefStringFW build(
            int maxLimit)
        {
            flyweight().wrap(buffer(), offset(), maxLimit);
            return flyweight();
        }

        @Override
        public Builder setAs(
            EnumWithInt8 kind,
            StringFW value,
            int kindPadding)
        {
            switch (kind)
            {
            case ONE:
            {
                setAsString8(value, kindPadding);
                break;
            }
            case TWO:
            {
                setAsString16(value, kindPadding);
                break;
            }
            case THREE:
            {
                setAsString32(value, kindPadding);
                break;
            }
            }
            return this;
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
                setAsString8(value, 0);
                break;
            case 1:
                setAsString16(value, 0);
                break;
            case 2:
            case 3:
                setAsString32(value, 0);
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

