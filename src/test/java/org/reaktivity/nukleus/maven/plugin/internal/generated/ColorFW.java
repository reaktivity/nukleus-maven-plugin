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

import java.nio.charset.Charset;

public final class ColorFW extends Flyweight
{
    private static final int FIELD_OFFSET_VALUE = 0;

    private static final int FIELD_SIZE_VALUE = BitUtil.SIZE_OF_BYTE;

    @Override
    public int limit()
    {
        return offset() + FIELD_SIZE_VALUE + Math.max(length0(), 0);
    }

    public Color get()
    {
        return Color.valueOf(buffer().getStringWithoutLengthUtf8(offset() + FIELD_SIZE_VALUE, length0()).toUpperCase());
    }

    @Override
    public ColorFW tryWrap(DirectBuffer buffer, int offset, int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit) || offset + FIELD_SIZE_VALUE > maxLimit() || limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public ColorFW wrap(DirectBuffer buffer, int offset, int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        return maxLimit() == offset() ? "null" : get().toString();
    }

    private int length0()
    {
        int length = buffer().getByte(offset()) & 0xFF;
        return length == 255 ? -1 : length;
    }

    public static final class Builder extends Flyweight.Builder<ColorFW>
    {
        private boolean valueSet;

        public Builder()
        {
            super(new ColorFW());
        }

        public Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }

        public Builder set(ColorFW value)
        {
            int newLimit = offset() + value.sizeof();
            checkLimit(newLimit, maxLimit());
            buffer().putBytes(offset(), value.buffer(), value.offset(), value.sizeof());
            limit(newLimit);
            valueSet = true;
            return this;
        }

        public Builder set(Color value, Charset charset)
        {
            MutableDirectBuffer buffer = buffer();
            byte[] charBytes = value.value().getBytes(charset);
            checkLength(charBytes.length);
            int newLimit = offset() + FIELD_SIZE_VALUE + charBytes.length;
            checkLimit(newLimit, maxLimit());
            buffer.putByte(offset(), (byte) charBytes.length);
            buffer.putBytes(offset() + 1, charBytes);
            limit(newLimit);
            valueSet = true;
            return this;
        }

        private static void checkLength(int length)
        {
            final int maxLength = 254;
            if (length > maxLength)
            {
                final String msg = String.format("length=%d is beyond maximum length=%d", length, maxLength);
                throw new IllegalArgumentException(msg);
            }
        }

        @Override
        public ColorFW build()
        {
            if (!valueSet)
            {
                throw new IllegalStateException("Color not set");
            }
            return super.build();
        }
    }
}
