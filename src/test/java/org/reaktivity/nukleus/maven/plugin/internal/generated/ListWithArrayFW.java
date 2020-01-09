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

import java.text.MessageFormat;
import java.util.List;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.ListFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.VariantOfListFW;

public final class ListWithArrayFW extends ListFW
{
    private static final int INDEX_FIELD1 = 0;

    private static final long MASK_FIELD1 = 1 << INDEX_FIELD1;

    private static final int INDEX_ARRAY_OF_STRING = 1;

    private static final long MASK_ARRAY_OF_STRING = 1 << INDEX_ARRAY_OF_STRING;

    private static final byte MISSING_FIELD_BYTE = VariantOfListFW.MISSING_FIELD_PLACEHOLDER;

    private static final int MISSING_FIELD_BYTE_SIZE = BitUtil.SIZE_OF_BYTE;

    private VariantEnumKindWithString32FW field1RO = new VariantEnumKindWithString32FW();

    private VariantOfVariantArrayFW<VariantEnumKindWithString32FW, StringFW> arrayOfStringRO =
        new VariantOfVariantArrayFW<>(new VariantEnumKindWithString32FW());

    private VariantOfListFW variantOfListRO = new VariantOfListFW();

    private long bitmask;

    public StringFW field1()
    {
        assert (bitmask & MASK_FIELD1) != 0L : "Field \"field1\" is not set";
        return field1RO.get();
    }

    public VariantArrayFW<VariantEnumKindWithString32FW> arrayOfString()
    {
        assert (bitmask & MASK_ARRAY_OF_STRING) != 0L : "Field \"arrayOfString\" is not set";
        return arrayOfStringRO.get();
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
    public ListWithArrayFW wrap(
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
                field1RO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                fieldLimit = field1RO.limit();
                bitmask |= 1 << INDEX_FIELD1;
                break;
            case INDEX_ARRAY_OF_STRING:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    arrayOfStringRO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = arrayOfStringRO.limit();
                    bitmask |= 1 << INDEX_ARRAY_OF_STRING;
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
    public ListWithArrayFW tryWrap(
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
                if (field1RO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                {
                    return null;
                }
                fieldLimit = field1RO.limit();
                bitmask |= 1 << INDEX_FIELD1;
                break;
            case INDEX_ARRAY_OF_STRING:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    if (arrayOfStringRO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = arrayOfStringRO.limit();
                    bitmask |= 1 << INDEX_ARRAY_OF_STRING;
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
        Object arrayOfString = null;
        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_ARRAY [bitmask={0}");
        format.append(", field1={1}");
        if ((bitmask & MASK_ARRAY_OF_STRING) != 0L)
        {
            format.append(", arrayOfString={2}");
            arrayOfString = arrayOfString();
        }
        format.append("]");
        return MessageFormat.format(format.toString(), String.format("0x%16X", bitmask), field1(), arrayOfString);
    }

    public static final class Builder extends Flyweight.Builder<ListWithArrayFW>
    {
        private final VariantEnumKindWithString32FW.Builder field1RW = new VariantEnumKindWithString32FW.Builder();

        private final VariantOfVariantArrayFW.Builder
            <VariantEnumKindWithString32FW.Builder, VariantEnumKindWithString32FW, EnumWithInt8, StringFW>
            arrayOfStringRW = new VariantOfVariantArrayFW.Builder<>(new VariantEnumKindWithString32FW.Builder(),
            new VariantEnumKindWithString32FW());

        private int lastFieldSet = -1;

        private final VariantOfListFW.Builder variantOfListRW = new VariantOfListFW.Builder();

        public Builder()
        {
            super(new ListWithArrayFW());
        }

        public Builder field1(
            StringFW value)
        {
            assert lastFieldSet < INDEX_FIELD1 : "Field \"field1\" cannot be set out of order";
            variantOfListRW.field((b, o, m) -> field1RW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_FIELD1;
            return this;
        }

        public Builder arrayOfString(
            List<StringFW> values)
        {
            assert lastFieldSet < INDEX_ARRAY_OF_STRING : "Field \"arrayOfString\" cannot be set out of order";
            assert lastFieldSet == INDEX_FIELD1 : "Prior required field \"field1\" is not set";
            variantOfListRW.field((b, o, m) ->
            {
                VariantOfVariantArrayFW.Builder arrayOfString = arrayOfStringRW.wrap(b, o, m);
                for (StringFW value : values)
                {
                    arrayOfString.item(value);
                }
                return arrayOfString.build().sizeof();

            });
            lastFieldSet = INDEX_ARRAY_OF_STRING;
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
        public ListWithArrayFW build()
        {
            assert lastFieldSet >= INDEX_FIELD1 : "Required field \"field1\" is not set";
            limit(variantOfListRW.build().limit());
            return super.build();
        }
    }
}
