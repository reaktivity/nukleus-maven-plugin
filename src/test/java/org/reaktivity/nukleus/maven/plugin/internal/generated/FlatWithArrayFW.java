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

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public final class FlatWithArrayFW extends Flyweight
{
    public static final int FIELD_OFFSET_FIXED1 = 0;

    private static final int FIELD_SIZE_FIXED1 = 8;

    public static final int FIELD_OFFSET_STRING1 = FIELD_OFFSET_FIXED1 + FIELD_SIZE_FIXED1;

    public static final int FIELD_OFFSET_ARRAY1 = 0;

    public static final int FIELD_OFFSET_FIXED2 = 0;

    private static final int FIELD_SIZE_FIXED2 = 4;

    private final String8FW string1RO = new String8FW();

    private final Array32FW<String8FW> array1RO = new Array32FW<>(new String8FW());

    public long fixed1()
    {
        return buffer().getLong(offset() + FIELD_OFFSET_FIXED1);
    }

    public String8FW string1()
    {
        return string1RO;
    }

    public ArrayFW<String8FW> array1()
    {
        return array1RO;
    }

    public int fixed2()
    {
        return buffer().getInt(array1RO.limit() + FIELD_OFFSET_FIXED2);
    }

    @Override
    public FlatWithArrayFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        string1RO.wrap(buffer, offset + FIELD_OFFSET_STRING1, maxLimit);
        array1RO.wrap(buffer, string1RO.limit() + FIELD_OFFSET_ARRAY1, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public FlatWithArrayFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        if (null == string1RO.tryWrap(buffer, offset + FIELD_OFFSET_STRING1, maxLimit))
        {
            return null;
        }
        if (null == array1RO.tryWrap(buffer, string1RO.limit() + FIELD_OFFSET_ARRAY1, maxLimit))
        {
            return null;
        }
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public int limit()
    {
        return array1RO.limit() + FIELD_OFFSET_FIXED2 + FIELD_SIZE_FIXED2;
    }

    @Override
    public String toString()
    {
        return String.format("FLAT_WITH_ARRAY [fixed1=%d, string1=%s, array1=%s, fixed2=%d]", fixed1(), string1RO.asString(),
            array1(), fixed2());
    }

    public static final class Builder extends Flyweight.Builder<FlatWithArrayFW>
    {
        private static final int INDEX_FIXED1 = 0;

        private static final long DEFAULT_FIXED1 = 111;

        private static final int INDEX_STRING1 = 1;

        private static final int INDEX_ARRAY1 = 2;

        private static final int INDEX_FIXED2 = 3;

        private static final int DEFAULT_FIXED2 = 5;

        private static final int FIELD_COUNT = 4;

        private final String8FW.Builder string1RW = new String8FW.Builder();

        private final Array32FW.Builder<String8FW.Builder, String8FW> array1RW =
            new Array32FW.Builder<>(new String8FW.Builder(), new String8FW());

        private int lastFieldSet = -1;

        public Builder()
        {
            super(new FlatWithArrayFW());
        }

        public Builder fixed1(long value)
        {
            if (value < 0L)
            {
                throw new IllegalArgumentException(String.format("Value %d too low for field \"fixed1\"", value));
            }
            assert lastFieldSet == INDEX_FIXED1 - 1;
            int newLimit = limit() + FIELD_SIZE_FIXED1;
            checkLimit(newLimit, maxLimit());
            buffer().putLong(limit(), value);
            lastFieldSet = INDEX_FIXED1;
            limit(newLimit);
            return this;
        }

        private String8FW.Builder string1()
        {
            if (lastFieldSet < INDEX_FIXED1)
            {
                fixed1(DEFAULT_FIXED1);
            }
            assert lastFieldSet == INDEX_STRING1 - 1;
            return string1RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder string1(String value)
        {
            String8FW.Builder string1RW = string1();
            string1RW.set(value, StandardCharsets.UTF_8);
            lastFieldSet = INDEX_STRING1;
            limit(string1RW.build().limit());
            return this;
        }

        public Builder string1(String8FW value)
        {
            String8FW.Builder string1RW = string1();
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
            String8FW.Builder string1RW = string1();
            string1RW.set(buffer, offset, length);
            lastFieldSet = INDEX_STRING1;
            limit(string1RW.build().limit());
            return this;
        }

        public Builder array1(
            Consumer<Array32FW.Builder<String8FW.Builder, String8FW>> mutator)
        {
            assert lastFieldSet == INDEX_ARRAY1 - 1;
            Array32FW.Builder<String8FW.Builder, String8FW> array1RW = this.array1RW.wrap(buffer(), limit(), maxLimit());
            mutator.accept(array1RW);
            limit(array1RW.build().limit());
            lastFieldSet = INDEX_ARRAY1;
            return this;
        }

        public Builder array1Item(
            Consumer<String8FW.Builder> mutator)
        {
            assert lastFieldSet >= INDEX_ARRAY1 - 1;
            if (lastFieldSet < INDEX_ARRAY1)
            {
                array1RW.wrap(buffer(), limit(), maxLimit());
            }
            array1RW.item(mutator);
            limit(array1RW.build().limit());
            lastFieldSet = INDEX_ARRAY1;
            return this;
        }

        public Builder fixed2(
            int value)
        {
            if (lastFieldSet < INDEX_ARRAY1)
            {
                array1(b ->
                {
                });
            }
            assert lastFieldSet == INDEX_FIXED2 - 1;
            int newLimit = limit() + FIELD_SIZE_FIXED2;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), value);
            lastFieldSet = INDEX_FIXED2;
            limit(newLimit);
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            lastFieldSet = -1;
            super.wrap(buffer, offset, maxLimit);
            limit(offset);
            return this;
        }

        @Override
        public Builder rewrap()
        {
            super.rewrap();
            return this;
        }

        @Override
        public FlatWithArrayFW build()
        {
            if (lastFieldSet < INDEX_FIXED2)
            {
                fixed2(DEFAULT_FIXED2);
            }
            assert lastFieldSet == FIELD_COUNT - 1;
            lastFieldSet = -1;
            return super.build();
        }
    }
}
