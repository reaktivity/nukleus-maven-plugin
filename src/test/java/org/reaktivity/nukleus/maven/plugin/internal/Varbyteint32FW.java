package org.reaktivity.nukleus.maven.plugin.internal;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

import java.nio.charset.MalformedInputException;

import static java.lang.Byte.MAX_VALUE;

public class Varbyteint32FW extends Flyweight {
    private final static int BYTE_MASK = 0xFF;

    private final static int CONTINUATION_BIT = 0x80;

    private final static int MAX_INPUT = 0x0FFFFFFF;

    private final static int MAX_MULTIPLIER = 0x200000;

    private int size;

    @Override
    public int limit() {
        return offset() + size;
    }

    public int value() {
        int value = 0;
        int i = 0;;
        int b;
        int pos  = offset();
        while (((b = buffer().getByte(pos++)) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            if (i > 35) {
                throw new IllegalArgumentException("varbyteint32 value too long");
            }
        }
        int unsigned = value  | (b << i);;
        int result = (((unsigned << 31) >> 31) ^ unsigned) >> 1;
        result = result ^ (unsigned & (1 << 31));
        return result;
    }

    @Override
    public Varbyteint32FW tryWrap(DirectBuffer buffer, int offset, int maxLimit) {
        if (null == super.tryWrap(buffer, offset, maxLimit) || maxLimit - offset  < 1) {
            return null;
        }
        size = length0();
        if (limit() > maxLimit) {
            return null;
        }
        return this;
    }

    @Override
    public Varbyteint32FW wrap(DirectBuffer buffer, int offset, int maxLimit) {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(offset + 1, maxLimit);
        size = length0();
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString() {
        return Integer.toString(value());
    }

    private int length0() {
        int pos = offset();
        byte b = (byte) 0;
        final int maxPos = Math.min(pos + 5,  maxLimit());
        while (pos < maxPos && ((b = buffer().getByte(pos)) & 0x80) != 0) {
            pos++;
        }
        int size = 1 + pos - offset();
        int mask = size < 5 ? 0x80 : 0xf0;
        if ((b & mask) != 0 && size >= 5) {
            throw new IllegalArgumentException(String.format("varint32 value at offset %d exceeds 32 bits", offset()));
        }
        return size;
    }

    public static final class Builder extends Flyweight.Builder<Varbyteint32FW> {
        private boolean valueSet;

        public Builder() {
            super(new Varbyteint32FW());
        }

        @Override
        public Varbyteint32FW.Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
            checkLimit(offset + 1, maxLimit);
            super.wrap(buffer, offset, maxLimit);
            this.valueSet = false;
            return this;
        }

        public Varbyteint32FW.Builder set(int value) {
            int encoded = encode(value);
            int pos = offset();
            int bits = 1 + Integer.numberOfTrailingZeros(Integer.highestOneBit(encoded));
            int size = bits / 7;
            if (size * 7 < bits) {
                size++;
            }
            int newLimit = pos + size;
            checkLimit(newLimit, maxLimit());
            while ((encoded & 0x7FFFFFFF) != 0) {
                buffer().putByte(pos++, (byte) ((encoded & 0x7F) | 0x80));
                encoded >>>= 8;
            }
            buffer().putByte(pos, (byte) (encoded & 0x7F));
            limit(newLimit);
            valueSet = true;
            return this;
        }

        private static int encode(int input) {
            if (input > MAX_INPUT) {
                return -1;
            }
            int varint = 0;
            int i = 0;
            while (input > 0) {
                int encodedByte = input % CONTINUATION_BIT;
                input = input / CONTINUATION_BIT;
                if (input > 0) {
                    encodedByte = encodedByte | CONTINUATION_BIT;
                }
                varint |= ((varint & CONTINUATION_BIT) > 0 ? encodedByte << (8*i) : encodedByte) | varint;
                i++;
            }
            return varint;
        }

        private static int decode(int input) throws MalformedInputException {
            int multiplier = 1;
            int value = 0;
            int encodedByte;
            do {
                encodedByte = input & BYTE_MASK;
                value += (encodedByte & MAX_VALUE) * multiplier;
                if (multiplier > MAX_MULTIPLIER) {
                    throw new MalformedInputException(multiplier);
                }
                multiplier *= CONTINUATION_BIT;
                input >>>= 8;
            }
            while ((encodedByte & CONTINUATION_BIT) != 0);
            return value;
        }

        @Override
        public Varbyteint32FW build() {
            if (!valueSet) {
                throw new IllegalArgumentException("value not set");
            }
            return super.build();
        }
    }
}
