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

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfInt16FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfInt8FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithInt32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantUint8KindWithInt64TypeFW;

public final class ListWithVariantFW extends Flyweight
{
    private static final int PHYSICAL_LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int LOGICAL_LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

    private static final int BIT_MASK_SIZE = BitUtil.SIZE_OF_LONG;

    private static final int PHYSICAL_LENGTH_OFFSET = 0;

    private static final int LOGICAL_LENGTH_OFFSET = PHYSICAL_LENGTH_OFFSET + PHYSICAL_LENGTH_SIZE;

    private static final int BIT_MASK_OFFSET = LOGICAL_LENGTH_OFFSET + LOGICAL_LENGTH_SIZE;

    private static final int FIRST_FIELD_OFFSET = BIT_MASK_OFFSET + BIT_MASK_SIZE;

    private static final int FIELD_SIZE_INTFIELD1 = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_SIZE_INTFIELD2 = BitUtil.SIZE_OF_SHORT;

    private static final int FIELD_INDEX_INTFIELD1 = 0;

    private static final int FIELD_INDEX_VARIANTOFINT64OFUINT8KIND = 1;

    private static final int FIELD_INDEX_VARIANTOFINT8ENUMKIND = 2;

    private static final int FIELD_INDEX_INTFIELD2 = 3;

    private static final int FIELD_INDEX_VARIANTOFINT16ENUMKIND = 4;

    private static final int FIELD_INDEX_VARIANTOFINT32ENUMKIND = 5;

    private final VariantUint8KindWithInt64TypeFW variantOfInt64Uint8KindRO = new VariantUint8KindWithInt64TypeFW();

    private final VariantEnumKindOfInt8FW variantOfInt8EnumKindRO = new VariantEnumKindOfInt8FW();

    private final VariantEnumKindOfInt16FW variantOfInt16EnumKindRO = new VariantEnumKindOfInt16FW();

    private final VariantEnumKindWithInt32FW variantOfInt32EnumKindRO = new VariantEnumKindWithInt32FW();

    private final int[] optionalOffsets = new int[FIELD_INDEX_VARIANTOFINT32ENUMKIND + 1];

    public int length()
    {
        return buffer().getByte(offset() + LOGICAL_LENGTH_OFFSET);
    }

    private long bitmask()
    {
        return buffer().getLong(offset() + BIT_MASK_OFFSET);
    }

    public int intField1()
    {
        assert (bitmask() & (1 << FIELD_INDEX_INTFIELD1)) != 0 : "Field \"intField1\" is not set";
        return buffer().getByte(optionalOffsets[FIELD_INDEX_INTFIELD1]);
    }

    public long variantOfInt64Uint8Kind()
    {
        assert (bitmask() & (1 << FIELD_INDEX_VARIANTOFINT64OFUINT8KIND)) != 0 : "Field \"variantOfInt64Uint8Kind\" is not set";
        return variantOfInt64Uint8KindRO.get();
    }

    public int variantOfInt8EnumKind()
    {
        assert (bitmask() & (1 << FIELD_INDEX_VARIANTOFINT8ENUMKIND)) != 0 : "Field \"variantOfInt8EnumKind\" is not set";
        return variantOfInt8EnumKindRO.get();
    }

    public int intField2()
    {
        assert (bitmask() & (1 << FIELD_INDEX_INTFIELD2)) != 0 : "Field \"intField2\" is not set";
        return buffer().getShort(optionalOffsets[FIELD_INDEX_INTFIELD2]);
    }

    public int variantOfInt16EnumKind()
    {
        assert (bitmask() & (1 << FIELD_INDEX_VARIANTOFINT16ENUMKIND)) != 0 : "Field \"variantOfInt16EnumKind\" is not set";
        return variantOfInt16EnumKindRO.get();
    }

    public int variantOfInt32EnumKind()
    {
        assert (bitmask() & (1 << FIELD_INDEX_VARIANTOFINT32ENUMKIND)) != 0 : "Field \"variantOfInt32EnumKind\" is not set";
        return variantOfInt32EnumKindRO.get();
    }


    @Override
    public ListWithVariantFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        final long bitmask = bitmask();
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        for (int field = FIELD_INDEX_INTFIELD1; field < FIELD_INDEX_VARIANTOFINT32ENUMKIND + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_INTFIELD1:
                if ((bitmask & (1 << FIELD_INDEX_INTFIELD1)) != 0)
                {
                    optionalOffsets[FIELD_INDEX_INTFIELD1] = fieldLimit;
                    fieldLimit += FIELD_SIZE_INTFIELD1;
                }
                break;
            case FIELD_INDEX_VARIANTOFINT64OFUINT8KIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT64OFUINT8KIND)) != 0)
                {
                    variantOfInt64Uint8KindRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfInt64Uint8KindRO.limit();
                }
                break;
            case FIELD_INDEX_VARIANTOFINT8ENUMKIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT8ENUMKIND)) != 0)
                {
                    variantOfInt8EnumKindRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfInt8EnumKindRO.limit();
                }
                break;
            case FIELD_INDEX_INTFIELD2:
                if ((bitmask & (1 << FIELD_INDEX_INTFIELD2)) != 0)
                {
                    optionalOffsets[FIELD_INDEX_INTFIELD2] = fieldLimit;
                    fieldLimit += FIELD_SIZE_INTFIELD2;
                }
                break;
            case FIELD_INDEX_VARIANTOFINT16ENUMKIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT16ENUMKIND)) != 0)
                {
                    variantOfInt16EnumKindRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfInt16EnumKindRO.limit();
                }
                break;
            case FIELD_INDEX_VARIANTOFINT32ENUMKIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT32ENUMKIND)) != 0)
                {
                    variantOfInt32EnumKindRO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfInt32EnumKindRO.limit();
                }
                break;
            }
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public ListWithVariantFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        final long bitmask = bitmask();
        int fieldLimit = offset + FIRST_FIELD_OFFSET;
        for (int field = FIELD_INDEX_INTFIELD1; field < FIELD_INDEX_VARIANTOFINT32ENUMKIND + 1; field++)
        {
            switch (field)
            {
            case FIELD_INDEX_INTFIELD1:
                if ((bitmask & (1 << FIELD_INDEX_INTFIELD1)) != 0)
                {
                    optionalOffsets[FIELD_INDEX_INTFIELD1] = fieldLimit;
                    fieldLimit += FIELD_SIZE_INTFIELD1;
                }
                break;
            case FIELD_INDEX_VARIANTOFINT64OFUINT8KIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT64OFUINT8KIND)) != 0)
                {
                    final VariantUint8KindWithInt64TypeFW variantOfInt64Uint8Kind =
                        variantOfInt64Uint8KindRO.tryWrap(buffer, fieldLimit, maxLimit);
                    if (variantOfInt64Uint8Kind == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt64Uint8Kind.limit();
                }
                break;
            case FIELD_INDEX_VARIANTOFINT8ENUMKIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT8ENUMKIND)) != 0)
                {
                    final VariantEnumKindOfInt8FW variantOfInt8EnumKind =
                        variantOfInt8EnumKindRO.tryWrap(buffer, fieldLimit, maxLimit);
                    if (variantOfInt8EnumKind == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt8EnumKind.limit();
                }
                break;
            case FIELD_INDEX_INTFIELD2:
                if ((bitmask & (1 << FIELD_INDEX_INTFIELD2)) != 0)
                {
                    optionalOffsets[FIELD_INDEX_INTFIELD2] = fieldLimit;
                    fieldLimit += FIELD_SIZE_INTFIELD2;
                }
                break;
            case FIELD_INDEX_VARIANTOFINT16ENUMKIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT16ENUMKIND)) != 0)
                {
                    final VariantEnumKindOfInt16FW variantOfInt16EnumKind =
                        variantOfInt16EnumKindRO.tryWrap(buffer, fieldLimit, maxLimit);
                    if (variantOfInt16EnumKind == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt16EnumKind.limit();
                }
                break;
            case FIELD_INDEX_VARIANTOFINT32ENUMKIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT32ENUMKIND)) != 0)
                {
                    final VariantEnumKindWithInt32FW variantOfInt32EnumKind =
                        variantOfInt32EnumKindRO.tryWrap(buffer, fieldLimit, maxLimit);
                    if (variantOfInt32EnumKind == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt32EnumKind.limit();
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
        return offset() + buffer().getByte(offset() + PHYSICAL_LENGTH_OFFSET);
    }

    @Override
    public String toString()
    {
        final long bitmask = bitmask();
        boolean intField1IsSet = (bitmask & (1 << FIELD_INDEX_INTFIELD1)) != 0;
        boolean variantOfInt64Uint8KindIsSet = (bitmask & (1 << FIELD_INDEX_VARIANTOFINT64OFUINT8KIND)) != 0;
        boolean variantOfInt8EnumKindIsSet = (bitmask & (1 << FIELD_INDEX_VARIANTOFINT8ENUMKIND)) != 0;
        boolean intField2IsSet = (bitmask & (1 << FIELD_INDEX_INTFIELD2)) != 0;
        boolean variantOfInt16EnumKindIsSet = (bitmask & (1 << FIELD_INDEX_VARIANTOFINT16ENUMKIND)) != 0;
        boolean variantOfInt32EnumKindIsSet = (bitmask & (1 << FIELD_INDEX_VARIANTOFINT32ENUMKIND)) != 0;
        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_VARIANT_OF_INT [bitmask={0}");
        if (intField1IsSet)
        {
            format.append(", intField1={1}");
        }
        if (variantOfInt64Uint8KindIsSet)
        {
            format.append(", variantOfInt64Uint8Kind={2}");
        }
        if (variantOfInt8EnumKindIsSet)
        {
            format.append(", variantOfInt8EnumKind={3}");
        }
        if (intField2IsSet)
        {
            format.append(", intField2={4}");
        }
        if (variantOfInt16EnumKindIsSet)
        {
            format.append(", variantOfInt16EnumKind={5}");
        }
        if (variantOfInt32EnumKindIsSet)
        {
            format.append(", variantOfInt32EnumKind={6}");
        }
        format.append("]");
        return MessageFormat.format(format.toString(),
            String.format("0x%02X", bitmask),
            intField1IsSet ? intField1() : null,
            variantOfInt64Uint8KindIsSet ? variantOfInt64Uint8Kind() : null,
            variantOfInt8EnumKindIsSet ? variantOfInt8EnumKind() : null,
            intField2IsSet ? intField2() : null,
            variantOfInt16EnumKindIsSet ? variantOfInt16EnumKind() : null,
            variantOfInt32EnumKindIsSet ? variantOfInt32EnumKind() : null);
    }

    public static final class Builder extends Flyweight.Builder<ListWithVariantFW>
    {
        private final VariantUint8KindWithInt64TypeFW.Builder variantOfInt64Uint8KindRW =
            new VariantUint8KindWithInt64TypeFW.Builder();

        private final VariantEnumKindOfInt8FW.Builder variantOfInt8EnumKindRW = new VariantEnumKindOfInt8FW.Builder();

        private final VariantEnumKindOfInt16FW.Builder variantOfInt16EnumKindRW = new VariantEnumKindOfInt16FW.Builder();

        private final VariantEnumKindWithInt32FW.Builder variantOfInt32EnumKindRW = new VariantEnumKindWithInt32FW.Builder();

        private long fieldsMask;

        public Builder()
        {
            super(new ListWithVariantFW());
        }

        public Builder intField1(
            byte value)
        {
            assert (fieldsMask & ~0x00) == 0 : "Field \"intField1\" cannot be set out of order";
            int newLimit = limit() + FIELD_SIZE_INTFIELD1;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(limit(), value);
            fieldsMask |= 1 << FIELD_INDEX_INTFIELD1;
            limit(newLimit);
            return this;
        }

        public Builder variantOfInt64Uint8Kind(
            long value)
        {
            assert (fieldsMask & ~0x01) == 0 :
                "Field \"variantOfInt64Uint8Kind\" cannot be set out of order";
            VariantUint8KindWithInt64TypeFW.Builder variantOfInt64Uint8KindRW = this.variantOfInt64Uint8KindRW.wrap(buffer(),
                limit(), maxLimit());
            variantOfInt64Uint8KindRW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_VARIANTOFINT64OFUINT8KIND;
            limit(variantOfInt64Uint8KindRW.build().limit());
            return this;
        }

        public Builder variantOfInt8EnumKind(
            int value)
        {
            assert (fieldsMask & ~0x03) == 0 :
                "Field \"variantOfInt8EnumKind\" cannot be set out of order";
            VariantEnumKindOfInt8FW.Builder variantOfInt8EnumKindRW = this.variantOfInt8EnumKindRW.wrap(buffer(), limit(),
                maxLimit());
            variantOfInt8EnumKindRW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_VARIANTOFINT8ENUMKIND;
            limit(variantOfInt8EnumKindRW.build().limit());
            return this;
        }

        public Builder intField2(
            short value)
        {
            assert (fieldsMask & ~0x07) == 0 : "Field \"intField2\" cannot be set out of order";
            int newLimit = limit() + FIELD_SIZE_INTFIELD2;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(limit(), value);
            fieldsMask |= 1 << FIELD_INDEX_INTFIELD2;
            limit(newLimit);
            return this;
        }

        public Builder variantOfInt16EnumKind(
            int value)
        {
            assert (fieldsMask & ~0x0F) == 0 :
                "Field \"variantOfInt16EnumKind\" cannot be set out of order";
            VariantEnumKindOfInt16FW.Builder variantOfInt16EnumKindRW = this.variantOfInt16EnumKindRW.wrap(buffer(), limit(),
                maxLimit());
            variantOfInt16EnumKindRW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_VARIANTOFINT16ENUMKIND;
            limit(variantOfInt16EnumKindRW.build().limit());
            return this;
        }

        public Builder variantOfInt32EnumKind(
            int value)
        {
            assert (fieldsMask & ~0x1F) == 0 :
                "Field \"variantOfInt32EnumKind\" cannot be set out of order";
            VariantEnumKindWithInt32FW.Builder variantOfInt32EnumKindRW = this.variantOfInt32EnumKindRW.wrap(buffer(), limit(),
                maxLimit());
            variantOfInt32EnumKindRW.set(value);
            fieldsMask |= 1 << FIELD_INDEX_VARIANTOFINT32ENUMKIND;
            limit(variantOfInt32EnumKindRW.build().limit());
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            fieldsMask = 0;
            int newLimit = limit() + FIRST_FIELD_OFFSET;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            return this;
        }

        @Override
        public ListWithVariantFW build()
        {
            buffer().putByte(offset() + PHYSICAL_LENGTH_OFFSET, (byte) (limit() - offset()));
            buffer().putByte(offset() + LOGICAL_LENGTH_OFFSET, (byte) (Long.bitCount(fieldsMask)));
            buffer().putLong(offset() + BIT_MASK_OFFSET, fieldsMask);
            return super.build();
        }
    }
}
