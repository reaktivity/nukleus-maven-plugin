/**
 * Copyright 2016-2017 The Reaktivity Project
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
package org.reaktivity.nukleus.maven.plugin.internal.bench;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.OctetsFW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.Varint32FW;

public final class OptFlatWithOctetsFW extends Flyweight
{
    public static final int FIELD_OFFSET_FIXED1 = 0;

    private static final int FIELD_SIZE_FIXED1 = BitUtil.SIZE_OF_INT;

    public static final int FIELD_OFFSET_OCTETS1 = FIELD_OFFSET_FIXED1 + FIELD_SIZE_FIXED1;

    public static final int FIELD_OFFSET_LENGTH_OCTETS2 = 0;

    private static final int FIELD_SIZE_LENGTH_OCTETS2 = BitUtil.SIZE_OF_SHORT;

    public static final int FIELD_OFFSET_STRING1 = FIELD_OFFSET_LENGTH_OCTETS2 + FIELD_SIZE_LENGTH_OCTETS2;

    public static final int FIELD_OFFSET_OCTETS2 = 0;

    public static final int FIELD_OFFSET_LENGTH_OCTETS3 = 0;

    public static final int FIELD_OFFSET_OCTETS3 = 0;

    public static final int FIELD_OFFSET_LENGTH_OCTETS4 = 0;

    private static final int FIELD_SIZE_LENGTH_OCTETS4 = BitUtil.SIZE_OF_INT;

    public static final int FIELD_OFFSET_OCTETS4 = FIELD_OFFSET_LENGTH_OCTETS4 + FIELD_SIZE_LENGTH_OCTETS4;

    public static final int FIELD_OFFSET_EXTENSION = 0;

    private final OctetsFW octets1RO = new OctetsFW();

    private final StringFW string1RO = new StringFW();

    private final OctetsFW octets2RO = new OctetsFW();

    private final Varint32FW lengthOctets3RO = new Varint32FW();

    private OctetsFW octets3RO = new OctetsFW();

    private OctetsFW octets4RO = new OctetsFW();

    private final OctetsFW extensionRO = new OctetsFW();

    public long fixed1()
    {
        return buffer().getInt(offset() + FIELD_OFFSET_FIXED1) & 0xFFFF_FFFFL;
    }

    public OctetsFW octets1()
    {
        return octets1RO;
    }

    public int lengthOctets2()
    {
        return buffer().getShort(octets1RO.limit() + FIELD_OFFSET_LENGTH_OCTETS2) & 0xFFFF;
    }

    public StringFW string1()
    {
        return string1RO;
    }

    public OctetsFW octets2()
    {
        return octets2RO;
    }

    public int lengthOctets3()
    {
        return lengthOctets3RO.value();
    }

    public OctetsFW octets3()
    {
        return lengthOctets3() == -1 ? null : octets3RO;
    }

    public int lengthOctets4()
    {
        return buffer().getInt(octets3RO.limit() + FIELD_OFFSET_LENGTH_OCTETS4);
    }

    public OctetsFW octets4()
    {
        return lengthOctets4() == -1 ? null : octets4RO;
    }

    public OctetsFW extension()
    {
        return extensionRO;
    }

    @Override
    public OptFlatWithOctetsFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        octets1RO.wrap(buffer, offset + FIELD_OFFSET_OCTETS1, offset + FIELD_OFFSET_OCTETS1 + 10);
        string1RO.wrap(buffer, octets1RO.limit() + FIELD_OFFSET_STRING1, maxLimit);
        octets2RO.wrap(buffer, string1RO.limit() + FIELD_OFFSET_OCTETS2,
                string1RO.limit() + FIELD_OFFSET_OCTETS2 + lengthOctets2());
        lengthOctets3RO.wrap(buffer, octets2RO.limit() + FIELD_OFFSET_LENGTH_OCTETS3, maxLimit);
        octets3RO.wrap(buffer, lengthOctets3RO.limit() + FIELD_OFFSET_OCTETS3,
                lengthOctets3RO.limit() + FIELD_OFFSET_OCTETS3 + (lengthOctets3() == -1 ? 0 : (int) lengthOctets3()));
        octets4RO.wrap(buffer, octets3RO.limit() + FIELD_OFFSET_OCTETS4,
                octets3RO.limit() + FIELD_OFFSET_OCTETS4 + (lengthOctets4() == -1 ? 0 : (int) lengthOctets4()));
        extensionRO.wrap(buffer, octets4RO.limit() + FIELD_OFFSET_EXTENSION, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public int limit()
    {
        return extensionRO.limit();
    }

    @Override
    public String toString()
    {
        return String.format(
                "FLAT_WITH_OCTETS [fixed1=%d, octets1=%s, lengthOctets2=%d, string1=%s, octets2=%s, " +
                "lengthOctets3=%s, octets3=%s, lengthOctets4=%d, octets4=%s, extension=%s]",
                fixed1(), octets1(), lengthOctets2(), string1RO.asString(), octets2(), lengthOctets3(), octets3(),
                lengthOctets4(), octets4(), extension());
    }

    public static final class Builder extends Flyweight.Builder<OptFlatWithOctetsFW>
    {
        private static final int INDEX_FIXED1 = 0;

        private static final long DEFAULT_FIXED1 = 11;

        private static final int INDEX_OCTETS1 = 1;

        private static final int DEFAULT_LENGTH_OCTETS2 = 0;

        private static final int INDEX_STRING1 = 2;

        private static final int INDEX_OCTETS2 = 3;

        private static final int INDEX_LENGTH_OCTETS3 = 4;

        private static final int INDEX_OCTETS3 = 5;

        private static final int DEFAULT_LENGTH_OCTETS4 = 0;

        private static final int INDEX_OCTETS4 = 6;

        private static final int INDEX_EXTENSION = 7;

        private static final int FIELD_COUNT = 8;

        private int lastFieldSet = -1;

        private final OctetsFW.Builder octets1RW = new OctetsFW.Builder();

        private int dynamicOffsetLengthOctets2;

        private final StringFW.Builder string1RW = new StringFW.Builder();

        private final OctetsFW.Builder octets2RW = new OctetsFW.Builder();

        private int dynamicValueLengthOctets3;

        private final Varint32FW.Builder lengthOctets3RW = new Varint32FW.Builder();

        private final OctetsFW.Builder octets3RW = new OctetsFW.Builder();

        private int dynamicOffsetLengthOctets4;

        private final OctetsFW.Builder octets4RW = new OctetsFW.Builder();

        private final OctetsFW.Builder extensionRW = new OctetsFW.Builder();

        public Builder()
        {
            super(new OptFlatWithOctetsFW());
        }

        public Builder fixed1(
            long value)
        {
            assert lastFieldSet == INDEX_FIXED1 - 1;
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"fixed1\"", value));
            }
            if (value > 0xFFFFFFFFL)
            {
                throw new IllegalArgumentException(String.format("Value %d too high for field \"fixed1\"", value));
            }
            int newLimit = limit() + FIELD_SIZE_FIXED1;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), (int) (value & 0xFFFF_FFFFL));
            lastFieldSet = INDEX_FIXED1;
            limit(newLimit);
            return this;
        }

        private OctetsFW.Builder octets1()
        {
            if (lastFieldSet < INDEX_FIXED1)
            {
                fixed1(DEFAULT_FIXED1);
            }
            assert lastFieldSet == INDEX_OCTETS1 - 1;
            int newLimit = limit() + 10;
            checkLimit(newLimit, maxLimit());
            return octets1RW.wrap(buffer(), limit(), newLimit);
        }

        public Builder octets1(
            Consumer<OctetsFW.Builder> mutator)
        {
            OctetsFW.Builder octets1RW = octets1();
            mutator.accept(octets1RW);
            int expectedLimit = octets1RW.maxLimit();
            int actualLimit = octets1RW.build().limit();
            if (actualLimit != expectedLimit)
            {
                throw new IllegalStateException(
                        String.format("%d instead of %d bytes have been set for field \"octets1\"",
                                actualLimit - limit(), expectedLimit - limit()));
            }
            limit(octets1RW.maxLimit());
            lastFieldSet = INDEX_OCTETS1;
            return this;
        }

        public Builder octets1(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            OctetsFW.Builder octets1RW = octets1();
            int fieldSize = octets1RW.maxLimit() - limit();
            if (length != fieldSize)
            {
                throw new IllegalArgumentException(
                        String.format("Invalid length %d for field \"octets1\", expected %d", length, fieldSize));
            }
            octets1RW.set(buffer, offset, length);
            limit(octets1RW.build().limit());
            lastFieldSet = INDEX_OCTETS1;
            return this;
        }

        private Builder lengthOctets2(
            int value)
        {
            if (value < 0)
            {
                throw new IllegalArgumentException(
                        String.format("Value %d too low for field \"lengthOctets2\"", value));
            }
            if (value > 0xFFFF)
            {
                throw new IllegalArgumentException(
                        String.format("Value %d too high for field \"lengthOctets2\"", value));
            }
            int newLimit = limit() + FIELD_SIZE_LENGTH_OCTETS2;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(limit(), (short) (value & 0xFFFF));
            dynamicOffsetLengthOctets2 = limit();
            limit(newLimit);
            return this;
        }

        private StringFW.Builder string1()
        {
            assert lastFieldSet == INDEX_STRING1 - 1;
            lengthOctets2(DEFAULT_LENGTH_OCTETS2);
            return string1RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder string1(
            String value)
        {
            StringFW.Builder string1RW = string1();
            string1RW.set(value, StandardCharsets.UTF_8);
            lastFieldSet = INDEX_STRING1;
            limit(string1RW.build().limit());
            return this;
        }

        public Builder string1(
            StringFW value)
        {
            StringFW.Builder string1RW = string1();
            string1RW.set(value);
            lastFieldSet = INDEX_STRING1;
            limit(string1RW.build().limit());
            return this;
        }

        public Builder string1(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            StringFW.Builder string1RW = string1();
            string1RW.set(buffer, offset, length);
            lastFieldSet = INDEX_STRING1;
            limit(string1RW.build().limit());
            return this;
        }

        private OctetsFW.Builder octets2()
        {
            assert lastFieldSet == INDEX_OCTETS2 - 1;
            return octets2RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder octets2(
            Consumer<OctetsFW.Builder> mutator)
        {
            OctetsFW.Builder octets2RW = octets2();
            mutator.accept(octets2RW);
            int newLimit = octets2RW.build().limit();
            int sizeDollar = newLimit - limit();
            limit(dynamicOffsetLengthOctets2);
            lengthOctets2(sizeDollar);
            limit(newLimit);
            lastFieldSet = INDEX_OCTETS2;
            return this;
        }

        public Builder octets2(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            OctetsFW.Builder octets2RW = octets2();
            octets2RW.set(buffer, offset, length);
            int newLimit = octets2RW.build().limit();
            int sizeDollar = newLimit - limit();
            limit(dynamicOffsetLengthOctets2);
            lengthOctets2(sizeDollar);
            limit(newLimit);
            lastFieldSet = INDEX_OCTETS2;
            return this;
        }

        public Builder lengthOctets3(
            int value)
        {
            assert lastFieldSet == INDEX_LENGTH_OCTETS3 - 1;
            Varint32FW.Builder lengthOctets3RW = this.lengthOctets3RW.wrap(buffer(), limit(), maxLimit());
            lengthOctets3RW.set(value);
            dynamicValueLengthOctets3 = value;
            limit(lengthOctets3RW.build().limit());
            lastFieldSet = INDEX_LENGTH_OCTETS3;
            return this;
        }

        private OctetsFW.Builder octets3()
        {
            assert lastFieldSet == INDEX_OCTETS3 - 1;
            return octets3RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder octets3(
            Consumer<OctetsFW.Builder> mutator)
        {
            OctetsFW.Builder octets3RW = octets3();
            mutator.accept(octets3RW);
            int newLimit = octets3RW.build().limit();
            int sizeDollar = newLimit - limit();
            if (sizeDollar > dynamicValueLengthOctets3)
            {
                throw new IllegalStateException(
                        String.format("%d bytes have been set for field \"octets3\", does not match value %d set in %s",
                                sizeDollar, dynamicValueLengthOctets3, "lengthOctets3"));
            }
            limit(newLimit);
            lastFieldSet = INDEX_OCTETS3;
            return this;
        }

        public Builder octets3(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            OctetsFW.Builder octets3RW = octets3();
            octets3RW.set(buffer, offset, length);
            int newLimit = octets3RW.build().limit();
            int sizeDollar = newLimit - limit();
            if (sizeDollar > dynamicValueLengthOctets3)
            {
                throw new IllegalStateException(
                        String.format("%d bytes have been set for field \"octets3\", does not match value %d set in %s",
                                sizeDollar, dynamicValueLengthOctets3, "lengthOctets3"));
            }
            limit(newLimit);
            lastFieldSet = INDEX_OCTETS3;
            return this;
        }

        private Builder lengthOctets4(
            int value)
        {
            if (lastFieldSet < INDEX_OCTETS3)
            {
                lengthOctets3(-1);
                lastFieldSet = INDEX_OCTETS3;
            }
            int newLimit = limit() + FIELD_SIZE_LENGTH_OCTETS4;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), value);
            dynamicOffsetLengthOctets4 = limit();
            limit(newLimit);
            return this;
        }

        private OctetsFW.Builder octets4()
        {
            assert lastFieldSet < INDEX_OCTETS4;
            lengthOctets4(DEFAULT_LENGTH_OCTETS4);
            assert lastFieldSet == INDEX_OCTETS4 - 1;
            return octets4RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder octets4(
            Consumer<OctetsFW.Builder> mutator)
        {
            OctetsFW.Builder octets4RW = octets4();
            mutator.accept(octets4RW);
            int newLimit = octets4RW.build().limit();
            int sizeDollar = newLimit - limit();
            limit(dynamicOffsetLengthOctets4);
            lengthOctets4(sizeDollar);
            limit(newLimit);
            lastFieldSet = INDEX_OCTETS4;
            return this;
        }

        public Builder octets4(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            OctetsFW.Builder octets4RW = octets4();
            octets4RW.set(buffer, offset, length);
            int newLimit = octets4RW.build().limit();
            int sizeDollar = newLimit - limit();
            limit(dynamicOffsetLengthOctets4);
            lengthOctets4(sizeDollar);
            limit(newLimit);
            lastFieldSet = INDEX_OCTETS4;
            return this;
        }

        private OctetsFW.Builder extension()
        {
            if (lastFieldSet < INDEX_OCTETS4)
            {
                lengthOctets4(-1);
                lastFieldSet = INDEX_OCTETS4;
            }
            return extensionRW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder extension(
            Consumer<OctetsFW.Builder> mutator)
        {
            OctetsFW.Builder extensionRW = extension();
            mutator.accept(extensionRW);
            limit(extensionRW.build().limit());
            lastFieldSet = INDEX_EXTENSION;
            return this;
        }

        public Builder extension(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            OctetsFW.Builder extensionRW = extension();
            extensionRW.set(buffer, offset, length);
            limit(extensionRW.build().limit());
            lastFieldSet = INDEX_EXTENSION;
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            lastFieldSet = -1;
            super.wrap(buffer, offset, maxLimit);
            limit(offset);
            return this;
        }

        @Override
        public OptFlatWithOctetsFW build()
        {

            if (lastFieldSet < INDEX_EXTENSION)
            {
                extension(b ->
                {
                });
            }
            assert lastFieldSet == FIELD_COUNT - 1;
            lastFieldSet = -1;
            return super.build();
        }

    }
}
