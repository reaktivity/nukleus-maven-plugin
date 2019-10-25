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
    private static final int PHYSICAL_LENGTH_OFFSET = 0;

    private static final int PHYSICAL_LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    private static final int LOGICAL_LENGTH_OFFSET = PHYSICAL_LENGTH_OFFSET + PHYSICAL_LENGTH_SIZE;

    private static final int LOGICAL_LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    private static final byte NULL_VALUE = 0x40;

    private static final int FIRST_FIELD_OFFSET = LOGICAL_LENGTH_OFFSET + LOGICAL_LENGTH_SIZE;

    private static final int INDEX_VARIANT_OF_STRING1 = 0;

    private static final long MASK_VARIANT_OF_STRING1 = 1 << INDEX_VARIANT_OF_STRING1;

    private static final int INDEX_VARIANT_OF_STRING2 = 1;

    private static final long MASK_VARIANT_OF_STRING2 = 1 << INDEX_VARIANT_OF_STRING2;

    private static final int INDEX_VARIANT_OF_UINT = 2;

    private static final long MASK_VARIANT_OF_UINT = 1 << INDEX_VARIANT_OF_UINT;

    private static final int INDEX_VARIANT_OF_INT = 3;

    private static final long MASK_VARIANT_OF_INT = 1 << INDEX_VARIANT_OF_INT;

    private static final long DEFAULT_VARIANT_OF_UINT = 4000000000L;

    private static final int NULL_VALUE_SIZE = BitUtil.SIZE_OF_BYTE;

    private VariantEnumKindWithString32FW variantOfString1RO = new VariantEnumKindWithString32FW();

    private VariantEnumKindWithString32FW variantOfString2RO = new VariantEnumKindWithString32FW();

    private VariantEnumKindOfUint32FW variantOfUintRO = new VariantEnumKindOfUint32FW();

    private VariantEnumKindWithInt32FW variantOfIntRO = new VariantEnumKindWithInt32FW();

    private long bitmask;

    public int length()
    {
        return buffer().getInt(offset() + LOGICAL_LENGTH_OFFSET);
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
        return (bitmask & MASK_VARIANT_OF_UINT) != 0L ? variantOfUintRO.get() : DEFAULT_VARIANT_OF_UINT;
    }

    public int variantOfInt()
    {
        assert (bitmask & MASK_VARIANT_OF_INT) != 0L : "Field \"variantOfInt\" is not set";
        return variantOfIntRO.get();
    }

    @Override
    public ListWithDefaultNullFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        bitmask = 0;
        final int length = length();
        for (int field = INDEX_VARIANT_OF_STRING1; field < length; field++)
        {
            switch (field)
            {
            case INDEX_VARIANT_OF_STRING1:
                variantOfString1RO.wrap(buffer, fieldLimit, maxLimit);
                fieldLimit = variantOfString1RO.limit();
                bitmask |= 1 << INDEX_VARIANT_OF_STRING1;
                break;
            case INDEX_VARIANT_OF_STRING2:
                if (buffer().getByte(fieldLimit) != NULL_VALUE)
                {
                    variantOfString2RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfString2RO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_STRING2;
                }
                else
                {
                    fieldLimit += NULL_VALUE_SIZE;
                }
                break;
            case INDEX_VARIANT_OF_UINT:
                if (buffer().getByte(fieldLimit) != NULL_VALUE)
                {
                    variantOfUintRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfUintRO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_UINT;
                }
                else
                {
                    fieldLimit += NULL_VALUE_SIZE;
                }
                break;
            case INDEX_VARIANT_OF_INT:
                if (buffer().getByte(fieldLimit) != NULL_VALUE)
                {
                    variantOfIntRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfIntRO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_INT;
                }
                else
                {
                    fieldLimit += NULL_VALUE_SIZE;
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
        bitmask = 0;
        final int length = length();
        for (int field = INDEX_VARIANT_OF_STRING1; field < length; field++)
        {
            switch (field)
            {
            case INDEX_VARIANT_OF_STRING1:
                if (variantOfString1RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                {
                    return null;
                }
                fieldLimit = variantOfString1RO.limit();
                bitmask |= 1 << INDEX_VARIANT_OF_STRING1;
                break;
            case INDEX_VARIANT_OF_STRING2:
                if (buffer().getByte(fieldLimit) != NULL_VALUE)
                {
                    if (variantOfString2RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfString2RO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_STRING2;
                }
                else
                {
                    fieldLimit += NULL_VALUE_SIZE;
                }
                break;
            case INDEX_VARIANT_OF_UINT:
                if (buffer().getByte(fieldLimit) != NULL_VALUE)
                {
                    if (variantOfUintRO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfUintRO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_UINT;
                }
                else
                {
                    fieldLimit += NULL_VALUE_SIZE;
                }
                break;
            case INDEX_VARIANT_OF_INT:
                if (buffer().getByte(fieldLimit) != NULL_VALUE)
                {
                    if (variantOfIntRO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfIntRO.limit();
                    bitmask |= 1 << INDEX_VARIANT_OF_INT;
                }
                else
                {
                    fieldLimit += NULL_VALUE_SIZE;
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
        Object variantOfString2 = null;
        Object variantOfInt = null;

        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_DEFAULT_NULL [");
        format.append("variantOfString1={0}");
        if ((bitmask & MASK_VARIANT_OF_STRING2) != 0L)
        {
            format.append(", variantOfString2={1}");
            variantOfString2 = variantOfString2();
        }
        format.append(", variantOfUint={2}");
        if ((bitmask & MASK_VARIANT_OF_INT) != 0L)
        {
            format.append(", variantOfInt={3}");
            variantOfInt = variantOfInt();
        }
        format.append("]");
        return MessageFormat.format(format.toString(),
            variantOfString1(),
            variantOfString2,
            variantOfUint(),
            variantOfInt);
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
            assert lastFieldSet < INDEX_VARIANT_OF_STRING1 : "Field \"variantOfString1\" cannot be set out of order";
            VariantEnumKindWithString32FW.Builder variantOfString1RW = this.variantOfString32RW.wrap(buffer(), limit(),
                maxLimit());
            variantOfString1RW.set(value);
            lastFieldSet = INDEX_VARIANT_OF_STRING1;
            limit(variantOfString1RW.build().limit());
            return this;
        }

        public Builder variantOfString2(
            String value)
        {
            assert lastFieldSet < INDEX_VARIANT_OF_STRING2 : "Field \"variantOfString2\" cannot be set out of order";
            assert lastFieldSet == INDEX_VARIANT_OF_STRING1 : "Prior required field \"variantOfString1\" is not set";
            VariantEnumKindWithString32FW.Builder variantOfString2RW = this.variantOfString32RW.wrap(buffer(), limit(),
                maxLimit());
            variantOfString2RW.set(value);
            lastFieldSet = INDEX_VARIANT_OF_STRING2;
            limit(variantOfString2RW.build().limit());
            return this;
        }

        private Builder defaultVariantOfString2()
        {
            assert lastFieldSet == INDEX_VARIANT_OF_STRING1 : "Prior required field \"variantOfString1\" is not set";
            int newLimit = limit() + SIZE_OF_BYTE;
            checkLimit(limit(), newLimit);
            buffer().putByte(limit(), NULL_VALUE);
            lastFieldSet = INDEX_VARIANT_OF_STRING2;
            limit(newLimit);
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
            VariantEnumKindOfUint32FW.Builder variantOfUintRW = this.variantOfUint32RW.wrap(buffer(), limit(), maxLimit());
            variantOfUintRW.set(value);
            lastFieldSet = INDEX_VARIANT_OF_UINT;
            limit(variantOfUintRW.build().limit());
            return this;
        }

        private Builder defaultVariantOfUint()
        {
            if (lastFieldSet < INDEX_VARIANT_OF_STRING2)
            {
                defaultVariantOfString2();
            }
            int newLimit = limit() + SIZE_OF_BYTE;
            checkLimit(limit(), newLimit);
            buffer().putByte(limit(), NULL_VALUE);
            lastFieldSet = INDEX_VARIANT_OF_UINT;
            limit(newLimit);
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
            VariantEnumKindWithInt32FW.Builder variantOfIntRW = this.variantOfInt32RW.wrap(buffer(), limit(), maxLimit());
            variantOfIntRW.set(value);
            lastFieldSet = INDEX_VARIANT_OF_INT;
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
            assert lastFieldSet >= INDEX_VARIANT_OF_STRING1 : "Required field \"variantOfString1\" is not set";
            buffer().putInt(offset() + PHYSICAL_LENGTH_OFFSET, limit() - offset());
            buffer().putInt(offset() + LOGICAL_LENGTH_OFFSET, lastFieldSet + 1);
            return super.build();
        }
    }
}