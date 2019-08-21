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
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.String16FW;
import org.reaktivity.reaktor.internal.test.types.String32FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantEnumKindWithString32FW extends Flyweight
{
    public static final EnumWithInt8 KIND_STRING = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_STRING16 = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_STRING32 = EnumWithInt8.THREE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final StringFW stringRO = new StringFW();

    private final String16FW string16RO = new String16FW();

    private final String32FW string32RO = new String32FW();

    public StringFW getAsString()
    {
        return stringRO;
    }

    public String16FW getAsString16()
    {
        return string16RO;
    }

    public String32FW getAsString32()
    {
        return string32RO;
    }

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public String get()
    {
        switch (kind())
        {
        case ONE:
            return getAsString().asString();
        case TWO:
            return getAsString16().asString();
        case THREE:
            return getAsString32().asString();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantEnumKindWithString32FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        enumWithInt8RO.tryWrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            if (null == stringRO.tryWrap(buffer, offset + enumWithInt8RO.sizeof(), maxLimit))
            {
                return null;
            }
            break;
        case TWO:
            if (null == string16RO.tryWrap(buffer, offset + enumWithInt8RO.sizeof(), maxLimit))
            {
                return null;
            }
            break;
        case THREE:
            if (null == string32RO.tryWrap(buffer, offset + enumWithInt8RO.sizeof(), maxLimit))
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
    public VariantEnumKindWithString32FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        enumWithInt8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            stringRO.wrap(buffer, offset + enumWithInt8RO.sizeof(), maxLimit);
            break;
        case TWO:
            string16RO.wrap(buffer, offset + enumWithInt8RO.sizeof(), maxLimit);
            break;
        case THREE:
            string32RO.wrap(buffer, offset + enumWithInt8RO.sizeof(), maxLimit);
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
            return String.format("VARIANTENUMKINDWITHSTRING32 [string=%s]", stringRO.asString());
        case TWO:
            return String.format("VARIANTENUMKINDWITHSTRING32 [string16=%s]", string16RO.asString());
        case THREE:
            return String.format("VARIANTENUMKINDWITHSTRING32 [string32=%s]", string32RO.asString());
        default:
            return String.format("VARIANTENUMKINDWITHSTRING32 [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ONE:
            return getAsString().limit();
        case TWO:
            return getAsString16().limit();
        case THREE:
            return getAsString32().limit();
        default:
            return enumWithInt8RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantEnumKindWithString32FW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        private final StringFW.Builder stringRW = new StringFW.Builder();

        private final String16FW.Builder string16RW = new String16FW.Builder();

        private final String32FW.Builder string32RW = new String32FW.Builder();

        public Builder()
        {
            super(new VariantEnumKindWithString32FW());
        }

        private Builder kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.build().limit());
            return this;
        }

        public Builder setAsString(
            String value)
        {
            kind(KIND_STRING);
            StringFW.Builder string = stringRW.wrap(buffer(), limit(), maxLimit());
            string.set(value, StandardCharsets.UTF_8);
            limit(string.build().limit());
            return this;
        }

        public Builder setAsString16(
            String value)
        {
            kind(KIND_STRING16);
            String16FW.Builder string16 = string16RW.wrap(buffer(), limit(), maxLimit());
            string16.set(value, StandardCharsets.UTF_8);
            limit(string16.build().limit());
            return this;
        }

        public Builder setAsString32(
            String value)
        {
            kind(KIND_STRING32);
            String32FW.Builder string32 = string32RW.wrap(buffer(), limit(), maxLimit());
            string32.set(value, StandardCharsets.UTF_8);
            limit(string32.build().limit());
            return this;
        }

        public Builder set(
            String value)
        {
            byte[] charBytes = value.getBytes(StandardCharsets.UTF_8);
            int byteLength = charBytes.length;
            int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(byteLength)) >> 3;
            switch (highestByteIndex)
            {
            case 0:
                setAsString(value);
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

        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }
    }
}
