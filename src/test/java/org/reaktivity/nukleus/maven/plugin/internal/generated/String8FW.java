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

    public String8FW(
        String value)
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
    public int limit()
    {
        return offset() + FIELD_SIZE_LENGTH + Math.max(length0(), 0);
    }

    @Override
    public String asString()
    {
        if (maxLimit() == offset() || length0() == -1)
        {
            return null;
        }
        return buffer().getStringWithoutLengthUtf8(offset() + FIELD_SIZE_LENGTH, length0());
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
        int length0 = length0();
        if (length0 != -1)
        {
            valueRO.wrap(buffer, offset + FIELD_SIZE_LENGTH, length0);
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
        int length0 = length0();
        if (length0 != -1)
        {
            valueRO.wrap(buffer, offset + FIELD_SIZE_LENGTH, length0);
        }
        return this;
    }

    public DirectBuffer value()
    {
        return length0() == -1 ? null : valueRO;
    }

    @Override
    public String toString()
    {
        return String.format("\"%s\"", asString());
    }

    private int length0()
    {
        int length = buffer().getByte(offset()) & 0xFF;
        return length == 255 ? -1 : length;
    }

    public static final class Builder extends StringFW.Builder<String8FW>
    {

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
            return this;
        }

        @Override
        public Builder set(
            String8FW value)
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
                int newLimit = offset() + value.sizeof();
                checkLimit(newLimit, maxLimit());
                buffer().putBytes(offset(), value.buffer(), value.offset(), value.sizeof());
                limit(newLimit);
            }
            super.set(value);
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
            super.set(srcBuffer, srcOffset, length);
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
            super.set(value, charset);
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
            return super.build();
        }
    }
}
