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

import static org.agrona.BitUtil.SIZE_OF_BYTE;
import static org.reaktivity.nukleus.maven.plugin.internal.generated.VariantOfListFW.MISSING_FIELD_PLACEHOLDER;

import java.text.MessageFormat;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfUint32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithInt32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithString32FW;

public final class ListFromVariantOfListFW extends ListFW
{
    private static final byte MISSING_FIELD_BYTE = VariantOfListFW.MISSING_FIELD_PLACEHOLDER;

    private static final int MISSING_FIELD_BYTE_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int INDEX_VARIANT_OF_STRING1 = 0;

    private static final long MASK_VARIANT_OF_STRING1 = 1 << INDEX_VARIANT_OF_STRING1;

    private static final int INDEX_VARIANT_OF_STRING2 = 1;

    private static final long MASK_VARIANT_OF_STRING2 = 1 << INDEX_VARIANT_OF_STRING2;

    private static final int INDEX_VARIANT_OF_UINT = 2;

    private static final long MASK_VARIANT_OF_UINT = 1 << INDEX_VARIANT_OF_UINT;

    private static final long DEFAULT_VALUE_VARIANT_OF_UINT = 4000000000L;

    private static final int INDEX_VARIANT_OF_INT = 3;

    private static final long MASK_VARIANT_OF_INT = 1 << INDEX_VARIANT_OF_INT;

    private VariantOfListFW variantOfListRO = new VariantOfListFW();

    private VariantEnumKindWithString32FW variantOfString1RO = new VariantEnumKindWithString32FW();

    private VariantEnumKindWithString32FW variantOfString2RO = new VariantEnumKindWithString32FW();

    private VariantEnumKindOfUint32FW variantOfUintRO = new VariantEnumKindOfUint32FW();

    private VariantEnumKindWithInt32FW variantOfIntRO = new VariantEnumKindWithInt32FW();

    private long bitmask;

    @Override
    public int length()
    {
        return variantOfListRO.length();
    }

    @Override
    public int fieldCount()
    {
        return variantOfListRO.fieldCount();
    }

    @Override
    public DirectBuffer fields()
    {
        return variantOfListRO.fields();
    }

    public String variantOfString1()
    {
        assert (bitmask & MASK_VARIANT_OF_STRING1) != 0L : "Field \"variantOfString1\" is not set";
        return variantOfString1RO.get();
    }

    public String variantOfString2()
    {
        assert (bitmask & MASK_VARIANT_OF_STRING2) != 0L : "Field \"variantOfString2\" is not set";
        return variantOfString2RO.get();
    }

    public long variantOfUint()
    {
        return (bitmask & MASK_VARIANT_OF_UINT) != 0L ? variantOfUintRO.get() : DEFAULT_VALUE_VARIANT_OF_UINT;
    }

    public int variantOfInt()
    {
        assert (bitmask & MASK_VARIANT_OF_INT) != 0L : "Field \"variantOfInt\" is not set";
        return variantOfIntRO.get();
    }

    @Override
    public ListFromVariantOfListFW wrap(
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
        DirectBuffer fieldsBuffer = variantOfListRO.fields();
        int fieldLimit = 0;
        for (int field = INDEX_VARIANT_OF_STRING1; field < fieldCount; field++)
        {
            checkLimit(fieldLimit + SIZE_OF_BYTE, limit);
            switch (field)
            {
            case INDEX_VARIANT_OF_STRING1:
                variantOfString1RO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                fieldLimit = variantOfString1RO.limit();
                bitmask |= 1 << INDEX_VARIANT_OF_STRING1;
                break;
            case INDEX_VARIANT_OF_STRING2:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_PLACEHOLDER)
                {
                    variantOfString2RO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfString2RO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_STRING2;
                }
                else
                {
                    fieldLimit += SIZE_OF_BYTE;
                }
                break;
            case INDEX_VARIANT_OF_UINT:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_PLACEHOLDER)
                {
                    variantOfUintRO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfUintRO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_UINT;
                }
                else
                {
                    fieldLimit += SIZE_OF_BYTE;
                }
                break;
            case INDEX_VARIANT_OF_INT:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_PLACEHOLDER)
                {
                    variantOfIntRO.wrap(fieldsBuffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfIntRO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_INT;
                }
                else
                {
                    fieldLimit += SIZE_OF_BYTE;
                }
                break;
            }
        }
        checkLimit(fieldLimit, limit);
        return this;
    }

    @Override
    public ListFromVariantOfListFW tryWrap(
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
        DirectBuffer fieldsBuffer = variantOfListRO.fields();
        int fieldLimit = 0;
        for (int field = INDEX_VARIANT_OF_STRING1; field < fieldCount; field++)
        {
            if (fieldLimit + SIZE_OF_BYTE > limit)
            {
                return null;
            }
            switch (field)
            {
            case INDEX_VARIANT_OF_STRING1:
                if (variantOfString1RO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                {
                    return null;
                }
                fieldLimit = variantOfString1RO.limit();
                bitmask |= 1 << INDEX_VARIANT_OF_STRING1;
                break;
            case INDEX_VARIANT_OF_STRING2:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_PLACEHOLDER)
                {
                    if (variantOfString2RO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfString2RO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_STRING2;
                }
                else
                {
                    fieldLimit += SIZE_OF_BYTE;
                }
                break;
            case INDEX_VARIANT_OF_UINT:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_PLACEHOLDER)
                {
                    if (variantOfUintRO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfUintRO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_UINT;
                }
                else
                {
                    fieldLimit += SIZE_OF_BYTE;
                }
                break;
            case INDEX_VARIANT_OF_INT:
                if (fieldsBuffer.getByte(fieldLimit) != MISSING_FIELD_PLACEHOLDER)
                {
                    if (variantOfIntRO.tryWrap(fieldsBuffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfIntRO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_INT;
                }
                else
                {
                    fieldLimit += SIZE_OF_BYTE;
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
        Object variantOfString2 = null;
        Object variantOfInt = null;
        StringBuilder format = new StringBuilder();
        format.append("LIST_FROM_VARIANT_OF_LIST [bitmask={0}");
        format.append(", variantOfString1={1}");
        if ((bitmask & MASK_VARIANT_OF_STRING2) != 0L)
        {
            format.append(", variantOfString2={2}");
            variantOfString2 = variantOfString2();
        }
        format.append(", variantOfUint={3}");
        if ((bitmask & MASK_VARIANT_OF_INT) != 0L)
        {
            format.append(", variantOfInt={4}");
            variantOfInt = variantOfInt();
        }
        format.append("]");
        return MessageFormat.format(format.toString(), String.format("0x%16X", bitmask), variantOfString1(), variantOfString2,
            variantOfUint(), variantOfInt);
    }

    public static final class Builder extends Flyweight.Builder<ListFromVariantOfListFW>
    {
        private final VariantEnumKindWithString32FW.Builder variantOfString1RW = new VariantEnumKindWithString32FW.Builder();

        private final VariantEnumKindWithString32FW.Builder variantOfString2RW = new VariantEnumKindWithString32FW.Builder();

        private final VariantEnumKindOfUint32FW.Builder variantOfUintRW = new VariantEnumKindOfUint32FW.Builder();

        private final VariantEnumKindWithInt32FW.Builder variantOfIntRW = new VariantEnumKindWithInt32FW.Builder();

        private final VariantOfListFW.Builder variantOfListRW = new VariantOfListFW.Builder();

        private int lastFieldSet = -1;

        public Builder()
        {
            super(new ListFromVariantOfListFW());
        }

        public Builder variantOfString1(
            String value)
        {
            assert lastFieldSet < INDEX_VARIANT_OF_STRING1 : "Field \"variantOfString1\" cannot be set out of order";
            variantOfListRW.field((b, o, m) -> variantOfString1RW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_VARIANT_OF_STRING1;
            return this;
        }

        public Builder variantOfString2(
            String value)
        {
            assert lastFieldSet < INDEX_VARIANT_OF_STRING2 : "Field \"variantOfString2\" cannot be set out of order";
            assert lastFieldSet == INDEX_VARIANT_OF_STRING1 : "Prior required field \"variantOfString1\" is not set";
            variantOfListRW.field((b, o, m) -> variantOfString2RW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_VARIANT_OF_STRING2;
            return this;
        }

        private Builder defaultVariantOfString2()
        {
            assert lastFieldSet == INDEX_VARIANT_OF_STRING1 : "Prior required field \"variantOfString1\" is not set";
            variantOfListRW.field((b, o, m) ->
            {
                b.putByte(o, MISSING_FIELD_BYTE);
                return MISSING_FIELD_BYTE_SIZE;
            });
            lastFieldSet = INDEX_VARIANT_OF_STRING2;
            return this;
        }

        public Builder variantOfUint(
            long value)
        {
            assert lastFieldSet < INDEX_VARIANT_OF_UINT : "Field \"variantOfUint\" cannot be set out of order";
            if (lastFieldSet < INDEX_VARIANT_OF_STRING2)
            {
                defaultVariantOfString2();
            }
            variantOfListRW.field((b, o, m) -> variantOfUintRW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_VARIANT_OF_UINT;
            return this;
        }

        private Builder defaultVariantOfUint()
        {
            if (lastFieldSet < INDEX_VARIANT_OF_STRING2)
            {
                defaultVariantOfString2();
            }
            variantOfListRW.field((b, o, m) ->
            {
                b.putByte(o, MISSING_FIELD_BYTE);
                return MISSING_FIELD_BYTE_SIZE;
            });
            lastFieldSet = INDEX_VARIANT_OF_UINT;
            return this;
        }

        public Builder variantOfInt(
            int value)
        {
            assert lastFieldSet < INDEX_VARIANT_OF_INT : "Field \"variantOfInt\" cannot be set out of order";
            if (lastFieldSet < INDEX_VARIANT_OF_UINT)
            {
                defaultVariantOfUint();
            }
            variantOfListRW.field((b, o, m) -> variantOfIntRW.wrap(b, o, m).set(value).build().sizeof());
            lastFieldSet = INDEX_VARIANT_OF_INT;
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
        public ListFromVariantOfListFW build()
        {
            assert lastFieldSet >= INDEX_VARIANT_OF_STRING1 : "Required field \"variantOfString1\" is not set";
            limit(variantOfListRW.build().limit());
            return super.build();
        }
    }
}
