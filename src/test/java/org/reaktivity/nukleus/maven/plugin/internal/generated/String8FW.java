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
import java.nio.charset.StandardCharsets;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public final class String8FW extends StringFW
{
    private static final int FIELD_SIZE_LENGTH = BitUtil.SIZE_OF_BYTE;

    private final DirectBuffer valueRO = new UnsafeBuffer(0L, 0);

    public String8FW()
    {
    }

    public String8FW(String value)
    {
        this(value, StandardCharsets.UTF_8);
    }

    public String8FW(
        String value,
        Charset charset)
    {
        final byte[] encoded = value.getBytes(charset);
        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[FIELD_SIZE_LENGTH + encoded.length]);
        buffer.putByte(0, (byte) (encoded.length & 0xFF));
        buffer.putBytes(FIELD_SIZE_LENGTH, encoded);
        wrap(buffer, 0, buffer.capacity());
    }

    @Override
    public int fieldSizeLength()
    {
        return FIELD_SIZE_LENGTH;
    }

    @Override
    public int limit()
    {
        return offset() + FIELD_SIZE_LENGTH + Math.max(length(), 0);
    }

    @Override
    public String asString()
    {
        if (maxLimit() == offset() || length() == -1)
        {
            return null;
        }
        return buffer().getStringWithoutLengthUtf8(offset() + FIELD_SIZE_LENGTH, length());
    }

    @Override
    public String8FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit) || offset + FIELD_SIZE_LENGTH > maxLimit() || limit() > maxLimit)
        {
            return null;
        }
        int length = length();
        if (length != -1)
        {
            valueRO.wrap(buffer, offset + FIELD_SIZE_LENGTH, length);
        }
        return this;
    }

    @Override
    public String8FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(offset + FIELD_SIZE_LENGTH, maxLimit);
        checkLimit(limit(), maxLimit);
        int length = length();
        if (length != -1)
        {
            valueRO.wrap(buffer, offset + FIELD_SIZE_LENGTH, length);
        }
        return this;
    }

    public DirectBuffer value()
    {
        return length() == -1 ? null : valueRO;
    }

    @Override
    public String toString()
    {
        return String.format("\"%s\"", asString());
    }

    public int length()
    {
        int length = buffer().getByte(offset()) & 0xFF;
        return length == 255 ? -1 : length;
    }

    public static final class Builder extends StringFW.Builder<String8FW>
    {
        private boolean valueSet;

        public Builder()
        {
            super(new String8FW());
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            checkLimit(offset + FIELD_SIZE_LENGTH, maxLimit);
            super.wrap(buffer, offset, maxLimit);
            this.valueSet = false;
            return this;
        }

        @Override
        public Builder set(
            StringFW value)
        {
            if (value == null)
            {
                int newLimit = offset() + FIELD_SIZE_LENGTH;
                checkLimit(newLimit, maxLimit());
                buffer().putByte(offset(), (byte) -1);
                limit(newLimit);
            }
            else
            {
                int newLimit = offset() + FIELD_SIZE_LENGTH + value.length();
                checkLimit(newLimit, maxLimit());
                buffer().putByte(offset(), (byte) value.length());
                buffer().putBytes(offset() + 1, value.buffer(), value.offset() + value.fieldSizeLength(), value.length());
                limit(newLimit);
            }
            valueSet = true;
            return this;
        }

        @Override
        public Builder set(
            DirectBuffer srcBuffer,
            int srcOffset,
            int length)
        {
            checkLength(length);
            int offset = offset();
            int newLimit = offset + length + FIELD_SIZE_LENGTH;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(offset, (byte) length);
            buffer().putBytes(offset + 1, srcBuffer, srcOffset, length);
            limit(newLimit);
            valueSet = true;
            return this;
        }

        @Override
        public Builder set(
            String value,
            Charset charset)
        {
            if (value == null)
            {
                int newLimit = offset() + FIELD_SIZE_LENGTH;
                checkLimit(newLimit, maxLimit());
                buffer().putByte(offset(), (byte) -1);
                limit(newLimit);
            }
            else
            {
                byte[] charBytes = value.getBytes(charset);
                checkLength(charBytes.length);
                int newLimit = offset() + FIELD_SIZE_LENGTH + charBytes.length;
                checkLimit(newLimit, maxLimit());
                buffer().putByte(offset(), (byte) charBytes.length);
                buffer().putBytes(offset() + 1, charBytes);
                limit(newLimit);
            }
            valueSet = true;
            return this;
        }

        private static void checkLength(
            int length)
        {
            final int maxLength = 254;
            if (length > maxLength)
            {
                final String msg = String.format("length=%d is beyond maximum length=%d", length, maxLength);
                throw new IllegalArgumentException(msg);
            }
        }

        @Override
        public String8FW build()
        {
            if (!valueSet)
            {
                set(null, StandardCharsets.UTF_8);
            }
            return super.build();
        }
    }
}
