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

import java.util.function.Consumer;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.ArrayFW;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.Roll;
import org.reaktivity.reaktor.internal.test.types.inner.RollFW;

public final class StructWithEnumDefaultFW extends Flyweight
{
    public static final int FIELD_OFFSET_ROLL = 0;

    public static final int FIELD_OFFSET_FIELD2 = 0;

    private static final int FIELD_SIZE_FIELD2 = 1;

    private final RollFW rollRO = new RollFW();

    public RollFW roll()
    {
        return rollRO;
    }

    public byte field2()
    {
        return buffer().getByte(rollRO.limit() + FIELD_OFFSET_FIELD2);
    }

    @Override
    public StructWithEnumDefaultFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        rollRO.wrap(buffer, offset + FIELD_OFFSET_ROLL, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public StructWithEnumDefaultFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        if (null == rollRO.tryWrap(buffer, offset + FIELD_OFFSET_ROLL, maxLimit))
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
        return rollRO.limit() + FIELD_OFFSET_FIELD2 + FIELD_SIZE_FIELD2;
    }

    @Override
    public String toString()
    {
        return String.format("STRUCT_WITH_ENUM_DEFAULT [roll=%s, field2=%d]", roll(), field2());
    }

    public static final class Builder extends Flyweight.Builder<StructWithEnumDefaultFW>
    {
        private static final int INDEX_ROLL = 0;

        private static final int INDEX_FIELD2 = 1;

        private static final Roll DEFAULT_ROLL = Roll.SPRING;

        private static final int FIELD_COUNT = 2;

        private final RollFW.Builder rollRW = new RollFW.Builder();

        private int lastFieldSet = -1;

        public Builder()
        {
            super(new StructWithEnumDefaultFW());
        }

        public StructWithEnumDefaultFW.Builder roll(Consumer<RollFW.Builder> mutator)
        {
            assert lastFieldSet == INDEX_ROLL - 1;
            RollFW.Builder rollRW = this.rollRW.wrap(buffer(), limit(), maxLimit());
            mutator.accept(rollRW);
            limit(rollRW.build().limit());
            lastFieldSet = INDEX_ROLL;
            return this;
        }

        public StructWithEnumDefaultFW.Builder field2(byte value)
        {
            if (lastFieldSet < INDEX_ROLL)
            {
                roll(b -> b.set(DEFAULT_ROLL));
            }
            assert lastFieldSet == INDEX_FIELD2 - 1;
            int newLimit = limit() + FIELD_SIZE_FIELD2;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(limit(), value);
            lastFieldSet = INDEX_FIELD2;
            limit(newLimit);
            return this;
        }

        @Override
        public StructWithEnumDefaultFW.Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            lastFieldSet = -1;
            limit(offset);
            return this;
        }

        @Override
        public StructWithEnumDefaultFW.Builder wrap(ArrayFW.Builder<? extends ArrayFW<StructWithEnumDefaultFW>,
            ? extends Flyweight.Builder<StructWithEnumDefaultFW>, StructWithEnumDefaultFW> array)
        {
            super.wrap(array);
            lastFieldSet = -1;
            return this;
        }

        @Override
        public StructWithEnumDefaultFW.Builder rewrap()
        {
            super.rewrap();
            return this;
        }

        @Override
        public StructWithEnumDefaultFW build()
        {
            assert lastFieldSet == FIELD_COUNT - 1;
            lastFieldSet = -1;
            return super.build();
        }
    }
}
