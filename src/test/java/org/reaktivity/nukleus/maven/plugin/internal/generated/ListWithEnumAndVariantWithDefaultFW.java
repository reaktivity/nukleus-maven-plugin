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
import org.reaktivity.reaktor.internal.test.types.ListFW;
import org.reaktivity.reaktor.internal.test.types.ListFW.Builder;
import org.reaktivity.reaktor.internal.test.types.inner.VariantOfInt32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantOfListFW;

public final class ListWithEnumAndVariantWithDefaultFW extends ListFW
{
    private static final int INDEX_FIELD1 = 0;

    private static final long MASK_FIELD1 = 1 << INDEX_FIELD1;

    private static final int INDEX_FIELD2 = 1;

    private static final long MASK_FIELD2 = 1 << INDEX_FIELD2;

    private static final int INDEX_FIELD3 = 2;

    private static final long MASK_FIELD3 = 1 << INDEX_FIELD3;

    private static final int INDEX_FIELD4 = 3;

    private static final long MASK_FIELD4 = 1 << INDEX_FIELD4;

    private static final int INDEX_FIELD5 = 4;

    private static final long MASK_FIELD5 = 1 << INDEX_FIELD5;

    private static final EnumWithVariantOfUint64 DEFAULT_VALUE_FIELD1 = EnumWithVariantOfUint64.TYPE3;

    private static final int DEFAULT_VALUE_FIELD3 = 100;

    private static final int DEFAULT_VALUE_FIELD4 = 1;

    private static final byte MISSING_FIELD_BYTE = VariantOfListFW.MISSING_FIELD_PLACEHOLDER;

    private static final int MISSING_FIELD_BYTE_SIZE = BitUtil.SIZE_OF_BYTE;

    private EnumWithVariantOfUint64FW field1RO = new EnumWithVariantOfUint64FW();

    private EnumWithVariantOfUint64FW field2RO = new EnumWithVariantOfUint64FW();

    private VariantOfInt32FW field3RO = new VariantOfInt32FW();

    private VariantEnumKindOfUint8FW field4RO = new VariantEnumKindOfUint8FW();

    private VariantEnumKindOfUint8FW field5RO = new VariantEnumKindOfUint8FW();

    private VariantOfListFW variantOfListRO = new VariantOfListFW();

    private long bitmask;

    public EnumWithVariantOfUint64 field1()
    {
        return (bitmask & MASK_FIELD1) != 0L ? field1RO.get() : DEFAULT_VALUE_FIELD1;
    }

    public EnumWithVariantOfUint64 field2()
    {
        assert (bitmask & MASK_FIELD2) != 0L : "Field \"field2\" is not set";
        return field2RO.get();
    }

    public int field3()
    {
        return (bitmask & MASK_FIELD3) != 0L ? field3RO.get() : DEFAULT_VALUE_FIELD3;
    }

    public int field4()
    {
        return (bitmask & MASK_FIELD4) != 0L ? field4RO.get() : DEFAULT_VALUE_FIELD4;
    }

    public int field5()
    {
        assert (bitmask & MASK_FIELD5) != 0L : "Field \"field5\" is not set";
        return field5RO.get();
    }

    public boolean hasField1()
    {
        return (bitmask & MASK_FIELD1) != 0L;
    }

    public boolean hasField3()
    {
        return (bitmask & MASK_FIELD3) != 0L;
    }

    public boolean hasField4()
    {
        return (bitmask & MASK_FIELD4) != 0L;
    }

    public boolean hasField5()
    {
        return (bitmask & MASK_FIELD5) != 0L;
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
            {
                if (field2RO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                {
                    return null;
                }
                fieldLimit = field2RO.limit();
                bitmask |= 1 << INDEX_FIELD2;
                break;
            }
            case INDEX_FIELD3:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    if (field3RO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = field3RO.limit();
                    bitmask |= 1 << INDEX_FIELD3;
                }
                else
                {
                    fieldLimit += MISSING_FIELD_BYTE_SIZE;
                }
                break;
            case INDEX_FIELD4:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    if (field4RO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = field4RO.limit();
                    bitmask |= 1 << INDEX_FIELD4;
                }
                else
                {
                    fieldLimit += MISSING_FIELD_BYTE_SIZE;
                }
                break;
            case INDEX_FIELD5:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    if (field5RO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = field5RO.limit();
                    bitmask |= 1 << INDEX_FIELD5;
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
            {
                field2RO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                fieldLimit = field2RO.limit();
                bitmask |= 1 << INDEX_FIELD2;
                break;
            }
            case INDEX_FIELD3:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    field3RO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = field3RO.limit();
                    bitmask |= 1 << INDEX_FIELD3;
                }
                else
                {
                    fieldLimit += MISSING_FIELD_BYTE_SIZE;
                }
                break;
            case INDEX_FIELD4:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    field4RO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = field4RO.limit();
                    bitmask |= 1 << INDEX_FIELD4;
                }
                else
                {
                    fieldLimit += MISSING_FIELD_BYTE_SIZE;
                }
                break;
            case INDEX_FIELD5:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_BYTE)
                {
                    field5RO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = field5RO.limit();
                    bitmask |= 1 << INDEX_FIELD5;
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
        Object field5 = null;
        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_ENUM_AND_VARIANT_WITH_DEFAULT [bitmask={0}");
        format.append(", field1={1}");
        format.append(", field2={2}");
        format.append(", field3={3}");
        format.append(", field4={4}");
        if (hasField5())
        {
            format.append(", field5={5}");
            field5 = field5();
        }
        format.append("]");
        return MessageFormat.format(format.toString(), String.format("0x%16X", bitmask), field1(), field2(), field3(), field4(),
            field5);
    }

    public static final class Builder extends ListFW.Builder<ListWithEnumAndVariantWithDefaultFW>
    {
        private final EnumWithVariantOfUint64FW.Builder field1RW = new EnumWithVariantOfUint64FW.Builder();

        private final EnumWithVariantOfUint64FW.Builder field2RW = new EnumWithVariantOfUint64FW.Builder();

        private final VariantOfInt32FW.Builder field3RW = new VariantOfInt32FW.Builder();

        private final VariantEnumKindOfUint8FW.Builder field4RW = new VariantEnumKindOfUint8FW.Builder();

        private final VariantEnumKindOfUint8FW.Builder field5RW = new VariantEnumKindOfUint8FW.Builder();

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
            EnumWithVariantOfUint64 value)
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

        public Builder field3(
            int value)
        {
            assert lastFieldSet < INDEX_FIELD3 : "Field \"field3\" cannot be set out of order";
            assert lastFieldSet == INDEX_FIELD2 : "Prior required field \"field2\" is not set";
            variantOfListRW.field((b, o, m) -> field3RW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_FIELD3;
            return this;
        }

        public Builder field4(
            int value)
        {
            assert lastFieldSet < INDEX_FIELD4 : "Field \"field4\" cannot be set out of order";
            if (lastFieldSet < INDEX_FIELD3)
            {
                defaultField3();
            }
            variantOfListRW.field((b, o, m) -> field4RW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_FIELD4;
            return this;
        }

        public Builder field5(
            int value)
        {
            assert lastFieldSet < INDEX_FIELD5 : "Field \"field5\" cannot be set out of order";
            if (lastFieldSet < INDEX_FIELD4)
            {
                defaultField4();
            }
            variantOfListRW.field((b, o, m) -> field5RW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_FIELD5;
            return this;
        }

        private Builder defaultField1()
        {
            variantOfListRW.field(Builder::missingField);
            lastFieldSet = INDEX_FIELD1;
            return this;
        }

        private Builder defaultField3()
        {
            assert lastFieldSet == INDEX_FIELD2 : "Prior required field \"field2\" is not set";
            variantOfListRW.field(Builder::missingField);
            lastFieldSet = INDEX_FIELD3;
            return this;
        }

        private Builder defaultField4()
        {
            if (lastFieldSet < INDEX_FIELD3)
            {
                defaultField3();
            }
            variantOfListRW.field(Builder::missingField);
            lastFieldSet = INDEX_FIELD4;
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
        public Builder field(
            Visitor visitor)
        {
            variantOfListRW.field(visitor);
            lastFieldSet = INDEX_FIELD2;
            return this;
        }

        @Override
        public Builder fields(
            int fieldCount,
            Visitor visitor)
        {
            variantOfListRW.fields(fieldCount, visitor);
            lastFieldSet = INDEX_FIELD2;
            return this;
        }

        @Override
        public Builder fields(
            int fieldCount,
            DirectBuffer buffer,
            int index,
            int length)
        {
            variantOfListRW.fields(fieldCount, buffer, index, length);
            lastFieldSet = INDEX_FIELD2;
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
        public ListWithEnumAndVariantWithDefaultFW build()
        {
            assert lastFieldSet >= INDEX_FIELD2 : "Required field \"field2\" is not set";
            limit(variantOfListRW.build().limit());
            return super.build();
        }
    }
}
