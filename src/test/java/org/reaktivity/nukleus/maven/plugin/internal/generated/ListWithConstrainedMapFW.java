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
    private static final int INDEX_CONSTRAINED_MAP = 0;

    private static final long MASK_CONSTRAINED_MAP = 1 << INDEX_CONSTRAINED_MAP;

    private static final byte MISSING_FIELD_BYTE = VariantOfListFW.MISSING_FIELD_PLACEHOLDER;

    private static final int MISSING_FIELD_BYTE_SIZE = BitUtil.SIZE_OF_BYTE;

    private ConstrainedMapFW<VariantWithoutOfFW> constrainedMapRO =
        new ConstrainedMapFW<>(new VariantEnumKindWithString32FW(), new VariantWithoutOfFW());

    private VariantOfListFW variantOfListRO = new VariantOfListFW();

    private long bitmask;

    public ConstrainedMapFW<VariantWithoutOfFW> constrainedMap()
    {
        assert (bitmask & MASK_CONSTRAINED_MAP) != 0L : "Field \"constrainedMap\" is not set";
        return constrainedMapRO;
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
        for (int field = INDEX_CONSTRAINED_MAP; field < fieldCount; field++)
        {
            checkLimit(fieldLimit + BitUtil.SIZE_OF_BYTE, limit);
            switch (field)
            {
            case INDEX_CONSTRAINED_MAP:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    constrainedMapRO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = constrainedMapRO.limit();
                    bitmask |= 1 << INDEX_CONSTRAINED_MAP;
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
        for (int field = INDEX_CONSTRAINED_MAP; field < fieldCount; field++)
        {
            if (fieldLimit + BitUtil.SIZE_OF_BYTE > limit)
            {
                return null;
            }
            switch (field)
            {
            case INDEX_CONSTRAINED_MAP:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    if (constrainedMapRO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = constrainedMapRO.limit();
                    bitmask |= 1 << INDEX_CONSTRAINED_MAP;
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
        Object constrainedMap = null;
        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_CONSTRAINED_MAP [bitmask={0}");
        if ((bitmask & MASK_CONSTRAINED_MAP) != 0L)
        {
            format.append(", constrainedMap={1}");
            constrainedMap = constrainedMap();
        }
        format.append("]");
        return MessageFormat.format(format.toString(), String.format("0x%16X", bitmask), constrainedMap);
    }

    public static final class Builder extends Flyweight.Builder<ListWithConstrainedMapFW>
    {
        private final ConstrainedMapFW.Builder<VariantWithoutOfFW, VariantWithoutOfFW.Builder> constrainedMapRW =
            new ConstrainedMapFW.Builder<>(new VariantEnumKindWithString32FW(), new VariantWithoutOfFW(),
                new VariantEnumKindWithString32FW.Builder(), new VariantWithoutOfFW.Builder());

        private int lastFieldSet = -1;

        private final VariantOfListFW.Builder variantOfListRW = new VariantOfListFW.Builder();

        public Builder()
        {
            super(new ListWithConstrainedMapFW());
        }

        public Builder constrainedMap(
            ConstrainedMapFW<VariantWithoutOfFW> value)
        {
            assert lastFieldSet < INDEX_CONSTRAINED_MAP : "Field \"constrainedMap\" cannot be set out of order";
            variantOfListRW.field((b, o, m) ->
            {
                ConstrainedMapFW.Builder<VariantWithoutOfFW, VariantWithoutOfFW.Builder> constrainedMap =
                    constrainedMapRW.wrap(b, o, m);
                constrainedMap.entries(value.entries(), 0, value.entries().capacity(), value.fieldCount());
                return constrainedMap.build().sizeof();
            });
            lastFieldSet = INDEX_CONSTRAINED_MAP;
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
