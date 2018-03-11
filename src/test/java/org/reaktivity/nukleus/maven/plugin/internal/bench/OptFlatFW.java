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
import java.util.BitSet;
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

        @SuppressWarnings("serial")
        private static final BitSet FIELDS_WITH_DEFAULTS = new BitSet(FIELD_COUNT)
        {
            {
                set(INDEX_FIXED2);
                set(INDEX_FIXED3);
            }
        };

        private static final String[] FIELD_NAMES =
        {"fixed1", "fixed2", "string1", "fixed3", "string2"};

        private final StringFW.Builder string1RW = new StringFW.Builder();

        private final StringFW.Builder string2RW = new StringFW.Builder();

        private final BitSet fieldsSet = new BitSet(FIELD_COUNT);

        public Builder()
        {
            super(new OptFlatFW());
        }

        public Builder fixed1(
            long value)
        {
            checkFieldNotSet(INDEX_FIXED1);
            if (value < 0L)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"fixed1\"", value));
            }
            checkFieldsSet(0, INDEX_FIXED1);
            int newLimit = limit() + FIELD_SIZE_FIXED1;
            checkLimit(newLimit, maxLimit());
            buffer().putLong(limit(), value);
            fieldsSet.set(INDEX_FIXED1);
            limit(newLimit);
            return this;
        }

        public Builder fixed2(
            int value)
        {
            checkFieldNotSet(INDEX_FIXED2);
            if (value < 0)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"fixed2\"", value));
            }
            if (value > 0xFFFF)
            {
                throw new IllegalArgumentException(String.format("Value %d too high for field \"fixed2\"", value));
            }
            checkFieldsSet(0, INDEX_FIXED2);
            int newLimit = limit() + FIELD_SIZE_FIXED2;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(limit(), (short) (value & 0xFFFF));
            fieldsSet.set(INDEX_FIXED2);
            limit(newLimit);
            return this;
        }

        private StringFW.Builder string1()
        {
            checkFieldNotSet(INDEX_STRING1);
            if (!fieldsSet.get(INDEX_FIXED2))
            {
                fixed2(DEFAULT_FIXED2);
            }
            checkFieldsSet(0, INDEX_STRING1);
            return string1RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder string1(
            String value)
        {
            StringFW.Builder string1RW = string1();
            string1RW.set(value, StandardCharsets.UTF_8);
            fieldsSet.set(INDEX_STRING1);
            limit(string1RW.build().limit());
            return this;
        }

        public Builder string1(
            StringFW value)
        {
            StringFW.Builder string1RW = string1();
            string1RW.set(value);
            fieldsSet.set(INDEX_STRING1);
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
            fieldsSet.set(INDEX_STRING1);
            limit(string1RW.build().limit());
            return this;
        }

        public Builder fixed3(
            int value)
        {
            checkFieldNotSet(INDEX_FIXED3);
            checkFieldsSet(0, INDEX_FIXED3);
            int newLimit = limit() + FIELD_SIZE_FIXED3;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), value);
            fieldsSet.set(INDEX_FIXED3);
            limit(newLimit);
            return this;
        }

        private StringFW.Builder string2()
        {
            checkFieldNotSet(INDEX_STRING2);
            if (!fieldsSet.get(INDEX_FIXED3))
            {
                fixed3(DEFAULT_FIXED3);
            }
            checkFieldsSet(0, INDEX_STRING2);
            return string2RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder string2(
            String value)
        {
            StringFW.Builder string2RW = string2();
            string2RW.set(value, StandardCharsets.UTF_8);
            fieldsSet.set(INDEX_STRING2);
            limit(string2RW.build().limit());
            return this;
        }

        public Builder string2(
            StringFW value)
        {
            StringFW.Builder string2RW = string2();
            string2RW.set(value);
            fieldsSet.set(INDEX_STRING2);
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
            fieldsSet.set(INDEX_STRING2);
            limit(string2RW.build().limit());
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            fieldsSet.clear();
            super.wrap(buffer, offset, maxLimit);
            limit(offset);
            return this;
        }

        @Override
        public OptFlatFW build()
        {
            checkFieldsSet(0, FIELD_COUNT);
            fieldsSet.clear();
            return super.build();
        }

        private void checkFieldNotSet(
            int index)
        {
            if (fieldsSet.get(index))
            {
                throw new IllegalStateException(String.format("Field \"%s\" has already been set", FIELD_NAMES[index]));
            }
        }

        private void checkFieldsSet(
            int fromIndex,
            int toIndex)
        {
            int fieldNotSet = fromIndex - 1;
            do
            {
                fieldNotSet = fieldsSet.nextClearBit(fieldNotSet + 1);
            }
            while (fieldNotSet < toIndex && FIELDS_WITH_DEFAULTS.get(fieldNotSet));
            if (fieldNotSet < toIndex)
            {
                throw new IllegalStateException(
                        String.format("Required field \"%s\" is not set", FIELD_NAMES[fieldNotSet]));
            }
        }
    }
}
