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

import java.text.MessageFormat;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.ListFW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithString32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantOfListFW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantWithoutOfFW;

public final class ListWithConstrainedMapFW extends ListFW
{
    private static final int INDEX_FIELD1 = 0;

    private static final long MASK_FIELD1 = 1 << INDEX_FIELD1;

    private static final byte MISSING_FIELD_BYTE = VariantOfListFW.MISSING_FIELD_PLACEHOLDER;

    private static final int MISSING_FIELD_BYTE_SIZE = BitUtil.SIZE_OF_BYTE;

    private ConstrainedMapFW<VariantWithoutOfFW> field1RO =
        new ConstrainedMapFW<>(new VariantEnumKindWithString32FW(), new VariantWithoutOfFW());

    private VariantOfListFW variantOfListRO = new VariantOfListFW();

    private long bitmask;

    public ConstrainedMapFW<VariantWithoutOfFW> field1()
    {
        assert (bitmask & MASK_FIELD1) != 0L : "Field \"field1\" is not set";
        return field1RO;
    }

    @Override
    public int length()
    {
        return variantOfListRO.get().length();
    }

    @Override
    public int fieldCount()
    {
        return variantOfListRO.get().fieldCount();
    }

    @Override
    public DirectBuffer fields()
    {
        return variantOfListRO.get().fields();
    }

    @Override
    public ListWithConstrainedMapFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        variantOfListRO.wrap(buffer, offset, maxLimit);
        final int limit = limit();
        checkLimit(limit, maxLimit);
        final int fieldCount = fieldCount();
        bitmask = 0;
        DirectBuffer fieldsBuffer = fields();
        int fieldLimit = 0;
        for (int field = INDEX_FIELD1; field < fieldCount; field++)
        {
            checkLimit(fieldLimit + BitUtil.SIZE_OF_BYTE, limit);
            switch (field)
            {
            case INDEX_FIELD1:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    field1RO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = field1RO.limit();
                    bitmask |= 1 << INDEX_FIELD1;
                }
                else
                {
                    fieldLimit += MISSING_FIELD_BYTE_SIZE;
                }
                break;
            }
        }
        checkLimit(fieldLimit, limit);
        return this;
    }

    @Override
    public ListWithConstrainedMapFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        if (variantOfListRO.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        final int limit = limit();
        if (limit > maxLimit)
        {
            return null;
        }
        final int fieldCount = fieldCount();
        bitmask = 0;
        DirectBuffer fieldsBuffer = fields();
        int fieldLimit = 0;
        for (int field = INDEX_FIELD1; field < fieldCount; field++)
        {
            if (fieldLimit + BitUtil.SIZE_OF_BYTE > limit)
            {
                return null;
            }
            switch (field)
            {
            case INDEX_FIELD1:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    if (field1RO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = field1RO.limit();
                    bitmask |= 1 << INDEX_FIELD1;
                }
                else
                {
                    fieldLimit += MISSING_FIELD_BYTE_SIZE;
                }
                break;
            }
        }
        if (fieldLimit > limit)
        {
            return null;
        }
        return this;
    }

    @Override
    public int limit()
    {
        return variantOfListRO.limit();
    }

    @Override
    public String toString()
    {
        Object field1 = null;
        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_CONSTRAINED_MAP [bitmask={0}");
        if ((bitmask & MASK_FIELD1) != 0L)
        {
            format.append(", field1={1}");
            field1 = field1();
        }
        format.append("]");
        return MessageFormat.format(format.toString(), String.format("0x%16X", bitmask), field1);
    }

    public static final class Builder extends Flyweight.Builder<ListWithConstrainedMapFW>
    {
        private final ConstrainedMapFW.Builder<VariantWithoutOfFW, VariantWithoutOfFW.Builder> field1RW =
            new ConstrainedMapFW.Builder<>(new VariantEnumKindWithString32FW(), new VariantWithoutOfFW(),
                new VariantEnumKindWithString32FW.Builder(), new VariantWithoutOfFW.Builder());

        private int lastFieldSet = -1;

        private final VariantOfListFW.Builder variantOfListRW = new VariantOfListFW.Builder();

        public Builder()
        {
            super(new ListWithConstrainedMapFW());
        }

        public Builder field1(
            ConstrainedMapFW<VariantWithoutOfFW> value)
        {
            assert lastFieldSet < INDEX_FIELD1 : "Field \"field1\" cannot be set out of order";
            variantOfListRW.field((b, o, m) ->
            {
                ConstrainedMapFW.Builder<VariantWithoutOfFW, VariantWithoutOfFW.Builder> field1 =
                    field1RW.wrap(b, o, m);
                field1.entries(value.entries(), 0, value.entries().capacity(), value.fieldCount());
                return field1.build().sizeof();
            });
            lastFieldSet = INDEX_FIELD1;
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
            variantOfListRW.wrap(buffer, offset, maxLimit);
            return this;
        }

        @Override
        public ListWithConstrainedMapFW build()
        {
            limit(variantOfListRW.build().limit());
            return super.build();
        }
    }
}
