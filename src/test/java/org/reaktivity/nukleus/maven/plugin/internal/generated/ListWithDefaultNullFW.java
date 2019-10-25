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

import java.text.MessageFormat;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfUint32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithInt32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithString32FW;

public class ListWithDefaultNullFW extends Flyweight
{
    public static final int PHYSICAL_LENGTH_OFFSET = 0;

    private static final int PHYSICAL_LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    public static final int LOGICAL_LENGTH_OFFSET = PHYSICAL_LENGTH_OFFSET + PHYSICAL_LENGTH_SIZE;

    private static final int LOGICAL_LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    private static final byte DEFAULT_NULL_VALUE = 0x40;

    private static final int FIRST_FIELD_OFFSET = LOGICAL_LENGTH_OFFSET + LOGICAL_LENGTH_SIZE;

    private static final int FIELD_INDEX_VARIANT_OF_STRING1 = 0;

    private static final int FIELD_INDEX_VARIANT_OF_STRING2 = 1;

    private static final int FIELD_INDEX_VARIANT_OF_UINT = 2;

    private static final int FIELD_INDEX_VARIANT_OF_INT = 3;

    private static final long FIELD_DEFAULT_VALUE_VARIANT_OF_UINT = 4000000000L;

    private VariantEnumKindWithString32FW variantOfString1RO = new VariantEnumKindWithString32FW();

    private VariantEnumKindWithString32FW variantOfString2RO = new VariantEnumKindWithString32FW();

    private VariantEnumKindOfUint32FW variantOfUintRO = new VariantEnumKindOfUint32FW();

    private VariantEnumKindWithInt32FW variantOfIntRO = new VariantEnumKindWithInt32FW();

    public int length()
    {
        return buffer().getInt(offset() + LOGICAL_LENGTH_OFFSET);
    }

    public String variantOfString1()
    {
        return variantOfString1RO.get();
    }

    private int fieldLimitVariantOfString1()
    {
        return variantOfString1RO.limit();
    }

    public String variantOfString2()
    {
        assert variantOfString2IsSet() : "Field \"variantOfString2\" is not set";
        return variantOfString2RO.get();
    }

    private int fieldLimitVariantOfString2()
    {
        return variantOfString2IsSet() ? variantOfString2RO.limit() : fieldLimitVariantOfString1() + 1;
    }

    private boolean variantOfString2IsSet()
    {
        return length() > FIELD_INDEX_VARIANT_OF_STRING2 && buffer().getByte(fieldLimitVariantOfString1()) != DEFAULT_NULL_VALUE;
    }

    public long variantOfUint()
    {
        return variantOfUintIsSet() ? variantOfUintRO.get() : FIELD_DEFAULT_VALUE_VARIANT_OF_UINT;
    }

    private int fieldLimitVariantOfUint()
    {
        return variantOfUintIsSet() ? variantOfUintRO.limit() : fieldLimitVariantOfString2() + 1;
    }

    private boolean variantOfUintIsSet()
    {
        return length() > FIELD_INDEX_VARIANT_OF_UINT && buffer().getByte(fieldLimitVariantOfString2()) != DEFAULT_NULL_VALUE;
    }

    public int variantOfInt()
    {
        assert variantOfIntIsSet() : "Field \"variantOfInt\" is not set";
        return variantOfIntRO.get();
    }

    private int fieldLimitVariantOfInt()
    {
        return variantOfIntIsSet() ? variantOfIntRO.limit() : fieldLimitVariantOfUint() + 1;
    }

    private boolean variantOfIntIsSet()
    {
        return length() > FIELD_INDEX_VARIANT_OF_INT && buffer().getByte(fieldLimitVariantOfUint()) != DEFAULT_NULL_VALUE;
    }

    @Override
    public ListWithDefaultNullFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        final int length = length();
        for (int field = FIELD_INDEX_VARIANT_OF_STRING1; field < length; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_VARIANT_OF_STRING1:
                variantOfString1RO.wrap(buffer, fieldLimit, maxLimit);
                fieldLimit = variantOfString1RO.limit();
                break;
            case FIELD_INDEX_VARIANT_OF_STRING2:
                if (buffer().getByte(fieldLimit) != DEFAULT_NULL_VALUE)
                {
                    variantOfString2RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfString2RO.limit();
                }
                else
                {
                    fieldLimit++;
                }
                break;
            case FIELD_INDEX_VARIANT_OF_UINT:
                if (buffer().getByte(fieldLimit) != DEFAULT_NULL_VALUE)
                {
                    variantOfUintRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfUintRO.limit();
                }
                else
                {
                    fieldLimit++;
                }
                break;
            case FIELD_INDEX_VARIANT_OF_INT:
                if (buffer().getByte(fieldLimit) != DEFAULT_NULL_VALUE)
                {
                    variantOfIntRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfIntRO.limit();
                }
                else
                {
                    fieldLimit++;
                }
                break;
            }
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public ListWithDefaultNullFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        final int length = length();
        for (int field = FIELD_INDEX_VARIANT_OF_STRING1; field < length; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_VARIANT_OF_STRING1:
                if (variantOfString1RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                {
                    return null;
                }
                fieldLimit = variantOfString1RO.limit();
                break;
            case FIELD_INDEX_VARIANT_OF_STRING2:
                if (buffer().getByte(fieldLimit) != DEFAULT_NULL_VALUE)
                {
                    if (variantOfString2RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfString2RO.limit();
                }
                else
                {
                    fieldLimit++;
                }
                break;
            case FIELD_INDEX_VARIANT_OF_UINT:
                if (buffer().getByte(fieldLimit) != DEFAULT_NULL_VALUE)
                {
                    if (variantOfUintRO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfUintRO.limit();
                }
                else
                {
                    fieldLimit++;
                }
                break;
            case FIELD_INDEX_VARIANT_OF_INT:
                if (buffer().getByte(fieldLimit) != DEFAULT_NULL_VALUE)
                {
                    if (variantOfIntRO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfIntRO.limit();
                }
                else
                {
                    fieldLimit++;
                }
                break;
            }
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
        return offset() + buffer().getInt(offset() + PHYSICAL_LENGTH_OFFSET);
    }

    @Override
    public String toString()
    {
        boolean variantOfString2IsSet = length() > FIELD_INDEX_VARIANT_OF_STRING2 &&
            buffer().getByte(variantOfString1RO.limit()) != DEFAULT_NULL_VALUE;
        boolean variantOfIntIsSet = length() > FIELD_INDEX_VARIANT_OF_INT &&
            buffer().getByte(variantOfUintRO.limit()) != DEFAULT_NULL_VALUE;

        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_DEFAULT_NULL [variantOfString1={0}");
        if (variantOfString2IsSet)
        {
            format.append(", variantOfString2={1}");
        }
        format.append(", variantOfUint={2}");
        if (variantOfIntIsSet)
        {
            format.append(", variantOfInt={3}");
        }

        format.append("]");
        return MessageFormat.format(format.toString(),
            variantOfString1(),
            variantOfString2IsSet ? variantOfString2() : null,
            variantOfUint(),
            variantOfIntIsSet ? variantOfInt() : null);
    }

    public static final class Builder extends Flyweight.Builder<ListWithDefaultNullFW>
    {
        private final VariantEnumKindWithString32FW.Builder variantOfString32RW = new VariantEnumKindWithString32FW.Builder();

        private final VariantEnumKindOfUint32FW.Builder variantOfUint32RW = new VariantEnumKindOfUint32FW.Builder();

        private final VariantEnumKindWithInt32FW.Builder variantOfInt32RW = new VariantEnumKindWithInt32FW.Builder();

        private int lastFieldSet = -1;

        public Builder()
        {
            super(new ListWithDefaultNullFW());
        }

        public Builder variantOfString1(
            String value)
        {
            assert lastFieldSet < FIELD_INDEX_VARIANT_OF_STRING1 : "Field \"variantOfString1\" cannot be set out of order";
            VariantEnumKindWithString32FW.Builder variantOfString1RW = this.variantOfString32RW.wrap(buffer(), limit(),
                maxLimit());
            variantOfString1RW.set(value);
            lastFieldSet = FIELD_INDEX_VARIANT_OF_STRING1;
            limit(variantOfString1RW.build().limit());
            return this;
        }

        public Builder variantOfString2(
            String value)
        {
            assert lastFieldSet < FIELD_INDEX_VARIANT_OF_STRING2 : "Field \"variantOfString2\" cannot be set out of order";
            assert lastFieldSet == FIELD_INDEX_VARIANT_OF_STRING1 : "Prior required field \"variantOfString1\" is not set";
            VariantEnumKindWithString32FW.Builder variantOfString2RW = this.variantOfString32RW.wrap(buffer(), limit(),
                maxLimit());
            variantOfString2RW.set(value);
            lastFieldSet = FIELD_INDEX_VARIANT_OF_STRING2;
            limit(variantOfString2RW.build().limit());
            return this;
        }

        private Builder variantOfString2()
        {
            assert lastFieldSet == FIELD_INDEX_VARIANT_OF_STRING1 : "Prior required field \"variantOfString1\" is not set";
            int newLimit = limit() + SIZE_OF_BYTE;
            checkLimit(limit(), newLimit);
            buffer().putByte(limit(), DEFAULT_NULL_VALUE);
            lastFieldSet = FIELD_INDEX_VARIANT_OF_STRING2;
            limit(newLimit);
            return this;
        }

        public Builder variantOfUint(
            long value)
        {
            assert lastFieldSet < FIELD_INDEX_VARIANT_OF_UINT : "Field \"variantOfUint\" cannot be set out of order";
            if (lastFieldSet < FIELD_INDEX_VARIANT_OF_STRING2)
            {
                variantOfString2();
            }
            VariantEnumKindOfUint32FW.Builder variantOfUintRW = this.variantOfUint32RW.wrap(buffer(), limit(), maxLimit());
            variantOfUintRW.set(value);
            lastFieldSet = FIELD_INDEX_VARIANT_OF_UINT;
            limit(variantOfUintRW.build().limit());
            return this;
        }

        private Builder variantOfUint()
        {
            if (lastFieldSet < FIELD_INDEX_VARIANT_OF_STRING2)
            {
                variantOfString2();
            }
            int newLimit = limit() + SIZE_OF_BYTE;
            checkLimit(limit(), newLimit);
            buffer().putByte(limit(), DEFAULT_NULL_VALUE);
            lastFieldSet = FIELD_INDEX_VARIANT_OF_UINT;
            limit(newLimit);
            return this;
        }

        public Builder variantOfInt(
            int value)
        {
            assert lastFieldSet < FIELD_INDEX_VARIANT_OF_INT : "Field \"variantOfInt\" cannot be set out of order";
            if (lastFieldSet < FIELD_INDEX_VARIANT_OF_UINT)
            {
                variantOfUint();
            }
            VariantEnumKindWithInt32FW.Builder variantOfIntRW = this.variantOfInt32RW.wrap(buffer(), limit(), maxLimit());
            variantOfIntRW.set(value);
            lastFieldSet = FIELD_INDEX_VARIANT_OF_INT;
            limit(variantOfIntRW.build().limit());
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
            int newLimit = limit() + FIRST_FIELD_OFFSET;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            return this;
        }

        @Override
        public ListWithDefaultNullFW build()
        {
            assert lastFieldSet >= FIELD_INDEX_VARIANT_OF_STRING1 : "Required field \"variantOfString1\" is not set";
            buffer().putInt(offset() + PHYSICAL_LENGTH_OFFSET, limit() - offset());
            buffer().putInt(offset() + LOGICAL_LENGTH_OFFSET, lastFieldSet + 1);
            return super.build();
        }
    }
}
