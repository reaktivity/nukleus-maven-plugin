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

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.StringFW;

public final class OptFlatFW extends Flyweight
{
    public static final int FIELD_OFFSET_FIXED1 = 0;

    private static final int FIELD_SIZE_FIXED1 = BitUtil.SIZE_OF_LONG;

    public static final int FIELD_OFFSET_FIXED2 = FIELD_OFFSET_FIXED1 + FIELD_SIZE_FIXED1;

    private static final int FIELD_SIZE_FIXED2 = BitUtil.SIZE_OF_SHORT;

    public static final int FIELD_OFFSET_STRING1 = FIELD_OFFSET_FIXED2 + FIELD_SIZE_FIXED2;

    public static final int FIELD_OFFSET_FIXED3 = 0;

    private static final int FIELD_SIZE_FIXED3 = BitUtil.SIZE_OF_INT;

    public static final int FIELD_OFFSET_STRING2 = FIELD_OFFSET_FIXED3 + FIELD_SIZE_FIXED3;

    public static final int TYPE_ID = 0x10000001;

    private final StringFW string1RO = new StringFW();

    private final StringFW string2RO = new StringFW();

    public long fixed1()
    {
        return buffer().getLong(offset() + FIELD_OFFSET_FIXED1);
    }

    public int fixed2()
    {
        return buffer().getShort(offset() + FIELD_OFFSET_FIXED2) & 0xFFFF;
    }

    public StringFW string1()
    {
        return string1RO;
    }

    public int fixed3()
    {
        return buffer().getInt(string1RO.limit() + FIELD_OFFSET_FIXED3);
    }

    public StringFW string2()
    {
        return string2RO;
    }

    public int typeId()
    {
        return TYPE_ID;
    }

    @Override
    public OptFlatFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        string1RO.wrap(buffer, offset + FIELD_OFFSET_STRING1, maxLimit);
        string2RO.wrap(buffer, string1RO.limit() + FIELD_OFFSET_STRING2, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public int limit()
    {
        return string2RO.limit();
    }

    @Override
    public String toString()
    {
        return String.format("FLAT [fixed1=%d, fixed2=%d, string1=%s, fixed3=%d, string2=%s]", fixed1(), fixed2(),
                string1RO.asString(), fixed3(), string2RO.asString());
    }

    public static final class Builder extends Flyweight.Builder<OptFlatFW>
    {
        private static final int INDEX_FIXED1 = 0;

        private static final int INDEX_FIXED2 = 1;

        private static final int DEFAULT_FIXED2 = 222;

        private static final int INDEX_STRING1 = 2;

        private static final int INDEX_FIXED3 = 3;

        private static final int DEFAULT_FIXED3 = 333;

        private static final int INDEX_STRING2 = 4;

        private static final int FIELD_COUNT = 5;

        private int lastFieldSet = -1;

        private final StringFW.Builder string1RW = new StringFW.Builder();

        private final StringFW.Builder string2RW = new StringFW.Builder();

        public Builder()
        {
            super(new OptFlatFW());
        }

        public Builder fixed1(
            long value)
        {
            assert lastFieldSet == INDEX_FIXED1 - 1;
            if (value < 0L)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"fixed1\"", value));
            }
            int newLimit = limit() + FIELD_SIZE_FIXED1;
            checkLimit(newLimit, maxLimit());
            buffer().putLong(limit(), value);
            lastFieldSet = INDEX_FIXED1;
            limit(newLimit);
            return this;
        }

        public Builder fixed2(
            int value)
        {
            assert lastFieldSet == INDEX_FIXED2 - 1;
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"fixed2\"", value));
            }
            if (value > 0xFFFF)
            {
                throw new IllegalArgumentException(String.format("Value %d too high for field \"fixed2\"", value));
            }
            int newLimit = limit() + FIELD_SIZE_FIXED2;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(limit(), (short) (value & 0xFFFF));
            lastFieldSet = INDEX_FIXED2;
            limit(newLimit);
            return this;
        }

        private StringFW.Builder string1()
        {
            assert lastFieldSet < INDEX_STRING1;
            if (lastFieldSet < INDEX_FIXED2)
            {
                fixed2(DEFAULT_FIXED2);
            }
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

        public Builder fixed3(
            int value)
        {
            assert lastFieldSet == INDEX_FIXED3 - 1;
            int newLimit = limit() + FIELD_SIZE_FIXED3;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), value);
            lastFieldSet = INDEX_FIXED3;
            limit(newLimit);
            return this;
        }

        private StringFW.Builder string2()
        {
            assert lastFieldSet < INDEX_STRING2;
            if (lastFieldSet < INDEX_FIXED3)
            {
                fixed3(DEFAULT_FIXED3);
            }
            return string2RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder string2(
            String value)
        {
            StringFW.Builder string2RW = string2();
            string2RW.set(value, StandardCharsets.UTF_8);
            lastFieldSet = INDEX_STRING2;
            limit(string2RW.build().limit());
            return this;
        }

        public Builder string2(
            StringFW value)
        {
            StringFW.Builder string2RW = string2();
            string2RW.set(value);
            lastFieldSet = INDEX_STRING2;
            limit(string2RW.build().limit());
            return this;
        }

        public Builder string2(
            DirectBuffer buffer,
            int offset,
            int length)
        {
            StringFW.Builder string2RW = string2();
            string2RW.set(buffer, offset, length);
            lastFieldSet = INDEX_STRING2;
            limit(string2RW.build().limit());
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
        public OptFlatFW build()
        {
            assert lastFieldSet == FIELD_COUNT - 1;
            lastFieldSet = -1;
            return super.build();
        }
    }
}
