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
import org.reaktivity.reaktor.internal.test.types.inner.VariantOfListFW;

public final class ListWithEnumAndVariantWithDefaultFW extends ListFW
{
    private static final int INDEX_FIELD1 = 0;

    private static final long MASK_FIELD1 = 1 << INDEX_FIELD1;

    private static final int INDEX_FIELD2 = 1;

    private static final long MASK_FIELD2 = 1 << INDEX_FIELD2;

    private static final EnumWithVariantOfUint64 DEFAULT_VALUE_FIELD1 = EnumWithVariantOfUint64.TYPE3;

    private static final int DEFAULT_VALUE_FIELD2 = 1;

    private static final byte MISSING_FIELD_BYTE = VariantOfListFW.MISSING_FIELD_PLACEHOLDER;

    private static final int MISSING_FIELD_BYTE_SIZE = BitUtil.SIZE_OF_BYTE;

    private EnumWithVariantOfUint64FW field1RO = new EnumWithVariantOfUint64FW();

    private VariantEnumKindOfUint8FW field2RO = new VariantEnumKindOfUint8FW();

    private VariantOfListFW variantOfListRO = new VariantOfListFW();

    private long bitmask;

    public EnumWithVariantOfUint64 field1()
    {
        return (bitmask & MASK_FIELD1) != 0L ? field1RO.get() : DEFAULT_VALUE_FIELD1;
    }

    public int field2()
    {
        return (bitmask & MASK_FIELD2) != 0L ? field2RO.get() : DEFAULT_VALUE_FIELD2;
    }

    public boolean hasField1()
    {
        return (bitmask & MASK_FIELD1) != 0L;
    }

    public boolean hasField2()
    {
        return (bitmask & MASK_FIELD2) != 0L;
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
    public ListWithEnumAndVariantWithDefaultFW tryWrap(
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
            case INDEX_FIELD2:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    if (field2RO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = field2RO.limit();
                    bitmask |= 1 << INDEX_FIELD2;
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
    public ListWithEnumAndVariantWithDefaultFW wrap(
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
            case INDEX_FIELD2:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    field2RO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = field2RO.limit();
                    bitmask |= 1 << INDEX_FIELD2;
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
    public int limit()
    {
        return variantOfListRO.limit();
    }

    @Override
    public String toString()
    {
        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_TYPEDEF [bitmask={0}");
        format.append(", field1={1}");
        format.append(", field2={2}");
        format.append("]");
        return MessageFormat.format(format.toString(), String.format("0x%16X", bitmask), field1(), field2());
    }

    public static final class Builder extends Flyweight.Builder<ListWithEnumAndVariantWithDefaultFW>
    {
        private final EnumWithVariantOfUint64FW.Builder field1RW = new EnumWithVariantOfUint64FW.Builder();

        private final VariantEnumKindOfUint8FW.Builder field2RW = new VariantEnumKindOfUint8FW.Builder();

        private int lastFieldSet = -1;

        private final VariantOfListFW.Builder variantOfListRW = new VariantOfListFW.Builder();

        public Builder()
        {
            super(new ListWithEnumAndVariantWithDefaultFW());
        }

        public Builder field1(
            EnumWithVariantOfUint64 value)
        {
            assert lastFieldSet < INDEX_FIELD1 : "Field \"field1\" cannot be set out of order";
            variantOfListRW.field((b, o, m) -> field1RW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_FIELD1;
            return this;
        }

        public Builder field2(
            int value)
        {
            assert lastFieldSet < INDEX_FIELD2 : "Field \"field2\" cannot be set out of order";
            if (lastFieldSet < INDEX_FIELD1)
            {
                defaultField1();
            }
            variantOfListRW.field((b, o, m) -> field2RW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_FIELD2;
            return this;
        }

        private Builder defaultField1()
        {
            variantOfListRW.field(Builder::missingField);
            lastFieldSet = INDEX_FIELD1;
            return this;
        }

        private static int missingField(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            buffer.putByte(offset, MISSING_FIELD_BYTE);
            return MISSING_FIELD_BYTE_SIZE;
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
        public ListWithEnumAndVariantWithDefaultFW build()
        {
            limit(variantOfListRW.build().limit());
            return super.build();
        }
    }
}
