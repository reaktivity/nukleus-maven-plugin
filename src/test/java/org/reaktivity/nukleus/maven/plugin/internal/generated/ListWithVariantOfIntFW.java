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

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfInt16FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfInt8FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithInt32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantUint8KindWithInt64TypeFW;

public final class ListWithVariantOfIntFW extends Flyweight
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

    public long bitmask()
    {
        return buffer().getLong(offset() + BIT_MASK_OFFSET);
    }

    public int intField1()
    {
        assert (bitmask() & (1 << FIELD_INDEX_INTFIELD1)) != 0 : "Field \"intField1\" is not set";
        return buffer().getByte(optionalOffsets[FIELD_INDEX_INTFIELD1]);
    }

    public VariantUint8KindWithInt64TypeFW variantOfInt64Uint8Kind()
    {
        assert (bitmask() & (1 << FIELD_INDEX_VARIANTOFINT64OFUINT8KIND)) != 0 : "Field \"variantOfInt64Uint8Kind\" is not set";
        return variantOfInt64Uint8KindRO;
    }

    public VariantEnumKindOfInt8FW variantOfInt8EnumKind()
    {
        assert (bitmask() & (1 << FIELD_INDEX_VARIANTOFINT8ENUMKIND)) != 0 : "Field \"variantOfInt8EnumKind\" is not set";
        return variantOfInt8EnumKindRO;
    }

    public int intField2()
    {
        assert (bitmask() & (1 << FIELD_INDEX_INTFIELD2)) != 0 : "Field \"intField2\" is not set";
        return buffer().getShort(optionalOffsets[FIELD_INDEX_INTFIELD2]);
    }

    public VariantEnumKindOfInt16FW variantOfInt16EnumKind()
    {
        assert (bitmask() & (1 << FIELD_INDEX_VARIANTOFINT16ENUMKIND)) != 0 : "Field \"variantOfInt16EnumKind\" is not set";
        return variantOfInt16EnumKindRO;
    }

    public VariantEnumKindWithInt32FW variantOfInt32EnumKind()
    {
        assert (bitmask() & (1 << FIELD_INDEX_VARIANTOFINT32ENUMKIND)) != 0 : "Field \"variantOfInt32EnumKind\" is not set";
        return variantOfInt32EnumKindRO;
    }


    @Override
    public ListWithVariantOfIntFW wrap(
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
    public ListWithVariantOfIntFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit))
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
                    if (null == variantOfInt64Uint8KindRO.tryWrap(buffer, fieldLimit, maxLimit))
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt64Uint8KindRO.limit();
                }
                break;
            case FIELD_INDEX_VARIANTOFINT8ENUMKIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT8ENUMKIND)) != 0)
                {
                    if (null == variantOfInt8EnumKindRO.tryWrap(buffer, fieldLimit, maxLimit))
                    {
                        return null;
                    }
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
                    if (null == variantOfInt16EnumKindRO.tryWrap(buffer, fieldLimit, maxLimit))
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt16EnumKindRO.limit();
                }
                break;
            case FIELD_INDEX_VARIANTOFINT32ENUMKIND:
                if ((bitmask & (1 << FIELD_INDEX_VARIANTOFINT32ENUMKIND)) != 0)
                {
                    if (null == variantOfInt32EnumKindRO.tryWrap(buffer, fieldLimit, maxLimit))
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt32EnumKindRO.limit();
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
        return String.format("LIST_WITH_VARIANT_OF_INT [bitmask=%s%s%s%s%s%s%s]",
            String.format("0x%02X", bitmask),
            (bitmask & (1 << FIELD_INDEX_INTFIELD1)) != 0 ? String.format(", intField1=%s", intField1()) : "",
            (bitmask & (1 << FIELD_INDEX_VARIANTOFINT64OFUINT8KIND)) != 0 ? String.format(", variantOfInt64Uint8Kind=%s",
                variantOfInt64Uint8Kind()) : "",
            (bitmask & (1 << FIELD_INDEX_VARIANTOFINT8ENUMKIND)) != 0 ? String.format(", variantOfInt8EnumKind=%s",
                variantOfInt8EnumKind()) : "",
            (bitmask & (1 << FIELD_INDEX_INTFIELD2)) != 0 ? String.format(", intField2=%s", intField2()) : "",
            (bitmask & (1 << FIELD_INDEX_VARIANTOFINT16ENUMKIND)) != 0 ? String.format(", variantOfInt16EnumKind=%s",
                variantOfInt16EnumKind()) : "",
            (bitmask & (1 << FIELD_INDEX_VARIANTOFINT32ENUMKIND)) != 0 ? String.format(", variantOfInt32EnumKind=%s",
                variantOfInt32EnumKind()) : "");
    }

    public static final class Builder extends Flyweight.Builder<ListWithVariantOfIntFW>
    {
        private final VariantUint8KindWithInt64TypeFW.Builder variantOfInt64Uint8KindRW =
            new VariantUint8KindWithInt64TypeFW.Builder();

        private final VariantEnumKindOfInt8FW.Builder variantOfInt8EnumKindRW = new VariantEnumKindOfInt8FW.Builder();

        private final VariantEnumKindOfInt16FW.Builder variantOfInt16EnumKindRW = new VariantEnumKindOfInt16FW.Builder();

        private final VariantEnumKindWithInt32FW.Builder variantOfInt32EnumKindRW = new VariantEnumKindWithInt32FW.Builder();

        private long fieldsMask;
        public Builder()
        {
            super(new ListWithVariantOfIntFW());
        }

        public Builder intField1(
            byte value)
        {
            assert (fieldsMask & ~0x00) == 0 : "Field \"intField1\" is already set or subsequent fields are already set";
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
                "Field \"variantOfInt64Uint8Kind\" is already set or subsequent fields are already set";
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
                "Field \"variantOfInt8EnumKind\" is already set or subsequent fields are already set";
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
            assert (fieldsMask & ~0x07) == 0 : "Field \"intField2\" is already set or subsequent fields are already set";
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
                "Field \"variantOfInt16EnumKind\" is already set or subsequent fields are already set";
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
                "Field \"variantOfInt32EnumKind\" is already set or subsequent fields are already set";
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
        public ListWithVariantOfIntFW build()
        {
            buffer().putByte(offset() + PHYSICAL_LENGTH_OFFSET, (byte) (limit() - offset()));
            buffer().putByte(offset() + LOGICAL_LENGTH_OFFSET, (byte) (Long.bitCount(fieldsMask)));
            buffer().putLong(offset() + BIT_MASK_OFFSET, fieldsMask);
            return super.build();
        }
    }
}
