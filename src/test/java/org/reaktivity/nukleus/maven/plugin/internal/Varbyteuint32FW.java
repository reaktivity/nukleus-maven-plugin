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
package org.reaktivity.nukleus.maven.plugin.internal;

import static java.lang.Byte.MAX_VALUE;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public class Varbyteuint32FW extends Flyweight
{
    private static final int BYTE_MASK = 0xFF;

    private static final int CONTINUATION_BIT = 0x80;

    private static final int MAX_INPUT = 0x0FFFFFFF;

    private static final int MAX_MULTIPLIER = 0x200000;

    private int size;

    public static void main(String[] args)
    {
        Varbyteuint32FW.Builder varbyteint32RW = new Varbyteuint32FW.Builder();
        MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[8192]);
        // 0
        Varbyteuint32FW varbyteint32 = varbyteint32RW.wrap(writeBuffer, 0, writeBuffer.capacity())
                .set(0)
                .build();
        int value = varbyteint32.value();
        System.out.printf("value=%d, bytes=%d\n", value, varbyteint32.length0());

        // 127
        varbyteint32 = varbyteint32RW.wrap(writeBuffer, 0, writeBuffer.capacity())
                .set(127)
                .build();
        value = varbyteint32.value();
        System.out.printf("value=%d, bytes=%d\n", value, varbyteint32.length0());

        // 16383
        varbyteint32 = varbyteint32RW.wrap(writeBuffer, 0, writeBuffer.capacity())
                .set(16383)
                .build();
        value = varbyteint32.value();
        System.out.printf("value=%d, bytes=%d\n", value, varbyteint32.length0());

        // 2097151
        varbyteint32 = varbyteint32RW.wrap(writeBuffer, 0, writeBuffer.capacity())
                .set(2097151)
                .build();
        value = varbyteint32.value();
        System.out.printf("value=%d, bytes=%d\n", value, varbyteint32.length0());

        // 268435455
        varbyteint32 = varbyteint32RW.wrap(writeBuffer, 0, writeBuffer.capacity())
                .set(268435455)
                .build();
        value = varbyteint32.value();
        System.out.printf("value=%d, bytes=%d\n", value, varbyteint32.length0());

        final int largeInput = 268435456;
        // final int largeInput = 2147483647;
        try
        {
            Varbyteuint32FW tooBig = varbyteint32RW.wrap(writeBuffer, 0, writeBuffer.capacity())
                    .set(largeInput)
                    .build();
        }
        catch (IllegalArgumentException iae)
        {
            System.out.printf("Input was too large: %d, error=\"%s\"\n", largeInput, iae);
        }
    }

    @Override
    public int limit()
    {
        return offset() + size;
    }

    public int value()
    {
        int value = 0;
        int multiplier = 1;
        int pos  = offset();
        int encodedByte;
        do
        {
            encodedByte = buffer().getByte(pos++);
            value += (encodedByte & MAX_VALUE) * multiplier;
            if (multiplier > MAX_MULTIPLIER)
            {
                throw new IllegalArgumentException(String.format("varbyteint32 value=%d is too long", value));
            }
            multiplier *= CONTINUATION_BIT;
        }
        while ((encodedByte & CONTINUATION_BIT) != 0);
        return value;
    }

    @Override
    public Varbyteuint32FW tryWrap(DirectBuffer buffer, int offset, int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit) || maxLimit - offset  < 1)
        {
            return null;
        }
        size = length0();
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public Varbyteuint32FW wrap(DirectBuffer buffer, int offset, int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(offset + 1, maxLimit);
        size = length0();
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        return Integer.toString(value());
    }

    private int length0()
    {
        int pos = offset();
        byte b = (byte) 0;
        final int maxPos = Math.min(pos + 5,  maxLimit());
        while (pos < maxPos && ((b = buffer().getByte(pos)) & 0x80) != 0)
        {
            pos++;
        }
        int size = 1 + pos - offset();
        int mask = size < 5 ? 0x80 : 0xF0;
        if ((b & mask) != 0 && size >= 5)
        {
            throw new IllegalArgumentException(String.format("varbyteint32 value at offset %d exceeds 32 bits", offset()));
        }
        return size;
    }

    public static final class Builder extends Flyweight.Builder<Varbyteuint32FW>
    {
        private boolean valueSet;

        public Builder()
        {
            super(new Varbyteuint32FW());
        }

        @Override
        public Varbyteuint32FW.Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit)
        {
            checkLimit(offset + 1, maxLimit);
            super.wrap(buffer, offset, maxLimit);
            this.valueSet = false;
            return this;
        }

        public Varbyteuint32FW.Builder set(int value)
        {
            if (value > MAX_INPUT)
            {
                throw new IllegalArgumentException("Input value too long");
            }
            int pos = offset();
            int varint = 0;
            int i = 0;
            do
            {
                int encodedByte = value % CONTINUATION_BIT;
                value = value / CONTINUATION_BIT;
                if (value > 0)
                {
                    encodedByte = encodedByte | CONTINUATION_BIT;
                }
                varint |= ((varint & CONTINUATION_BIT) > 0 ? encodedByte << (8 * i) : encodedByte) | varint;
                buffer().putByte(pos++, (byte) (encodedByte & BYTE_MASK));
                i++;
            }
            while (value > 0);
            int bits = 1 + Integer.numberOfTrailingZeros(Integer.highestOneBit(varint));
            int size = bits / 7;
            if (size * 7 < bits)
            {
                size++;
            }
            int newLimit = pos + size;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            valueSet = true;
            return this;
        }

        @Override
        public Varbyteuint32FW build()
        {
            if (!valueSet)
            {
                throw new IllegalArgumentException("value not set");
            }
            return super.build();
        }
    }
}
