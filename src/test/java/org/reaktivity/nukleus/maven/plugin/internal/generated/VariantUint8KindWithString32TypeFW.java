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

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.String16FW;
import org.reaktivity.reaktor.internal.test.types.String32FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;

import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class VariantUint8KindWithString32TypeFW extends Flyweight
{
    private static final int FIELD_SIZE_KIND = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_OFFSET_KIND = 0;

    public static final int KIND_STRING = 0xa1;

    private static final int FIELD_OFFSET_STRING = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    public static final int KIND_STRING16 = 0x16;

    private static final int FIELD_OFFSET_STRING16 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

    public static final int KIND_STRING32 = 0xb1;

    private static final int FIELD_OFFSET_STRING32 = FIELD_OFFSET_KIND + FIELD_SIZE_KIND;

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

    public int kind()
    {
        return buffer().getByte(offset() + FIELD_OFFSET_KIND) & 0xFF;
    }

    public String get()
    {
        switch (kind())
        {
        case KIND_STRING:
            return getAsString().asString();
        case KIND_STRING16:
            return getAsString16().asString();
        case KIND_STRING32:
            return getAsString32().asString();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantUint8KindWithString32TypeFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.tryWrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case KIND_STRING:
            if (null == stringRO.tryWrap(buffer, offset + FIELD_OFFSET_STRING, maxLimit))
            {
                return null;
            }
            break;
        case KIND_STRING16:
            if (null == string16RO.tryWrap(buffer, offset + FIELD_OFFSET_STRING16, maxLimit))
            {
                return null;
            }
            break;
        case KIND_STRING32:
            if (null == string32RO.tryWrap(buffer, offset + FIELD_OFFSET_STRING32, maxLimit))
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
    public VariantUint8KindWithString32TypeFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case KIND_STRING:
            stringRO.wrap(buffer, offset + FIELD_OFFSET_STRING, maxLimit);
            break;
        case KIND_STRING16:
            string16RO.wrap(buffer, offset + FIELD_OFFSET_STRING16, maxLimit);
            break;
        case KIND_STRING32:
            string32RO.wrap(buffer, offset + FIELD_OFFSET_STRING32, maxLimit);
            break;
        default:
            break;
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case KIND_STRING:
            return getAsString().limit();
        case KIND_STRING16:
            return getAsString16().limit();
        case KIND_STRING32:
            return getAsString32().limit();
        default:
            return offset();
        }
    }

    @Override
    public String toString()
    {
        switch (kind())
        {
        case KIND_STRING:
            return String.format("VARIANTUINT8WITHSTRING32TYPEFW [string=%s]", stringRO.asString());
        case KIND_STRING16:
            return String.format("VARIANTUINT8WITHSTRING32TYPEFW [getAsString16=%s]", string16RO.asString());
        case KIND_STRING32:
            return String.format("VARIANTUINT8WITHSTRING32TYPEFW [string32=%s]", string32RO.asString());
        default:
            return String.format("VARIANTUINT8WITHSTRING32TYPEFW [unknown]");
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantUint8KindWithString32TypeFW>
    {
        private final StringFW.Builder stringRW = new StringFW.Builder();

        private final String16FW.Builder string16RW = new String16FW.Builder();

        private final String32FW.Builder string32RW = new String32FW.Builder();

        public Builder()
        {
            super(new VariantUint8KindWithString32TypeFW());
        }

        private Builder kind(
            int value)
        {
            buffer().putByte(offset() + FIELD_OFFSET_KIND, (byte)(value & 0xFF));
            return this;
        }

        public Builder setAsString(
            String value)
        {
            if (value == null)
            {
                limit(offset() + FIELD_OFFSET_STRING);
            }
            else
            {
                int newLimit = maxLimit();
                checkLimit(newLimit, maxLimit());
                kind(KIND_STRING);
                StringFW.Builder string = stringRW.wrap(buffer(), offset() + FIELD_OFFSET_STRING, newLimit);
                string.set(value, StandardCharsets.UTF_8);
                limit(string.build().limit());
            }
            return this;
        }

        public Builder setAsString16(
            String value)
        {
            if (value == null)
            {
                limit(offset() + FIELD_OFFSET_STRING16);
            }
            else
            {
                int newLimit = maxLimit();
                checkLimit(newLimit, maxLimit());
                kind(KIND_STRING16);
                String16FW.Builder string16 = string16RW.wrap(buffer(), offset() + FIELD_OFFSET_STRING16, newLimit);
                string16.set(value, StandardCharsets.UTF_8);
                limit(string16.build().limit());
            }
            return this;
        }

        public Builder setAsString32(
            String value)
        {
            if (value == null)
            {
                limit(offset() + FIELD_OFFSET_STRING32);
            }
            else
            {
                int newLimit = maxLimit();
                checkLimit(newLimit, maxLimit());
                kind(KIND_STRING32);
                String32FW.Builder string32 = string32RW.wrap(buffer(), offset() + FIELD_OFFSET_STRING32, newLimit);
                string32.set(value, StandardCharsets.UTF_8);
                limit(string32.build().limit());
            }
            return this;
        }

        public Builder set(
            String value)
        {
            byte[] charBytes = value.getBytes(UTF_8);
            int byteLength = charBytes.length;
            int highestOneBit = Integer.numberOfTrailingZeros(Integer.highestOneBit(byteLength));
            switch (highestOneBit)
            {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                setAsString(value);
                break;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                setAsString16(value);
                break;
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
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
