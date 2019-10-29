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
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfUint16FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfUint32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfUint8FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithInt32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithString32FW;
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

    private static final int INDEX_INT_FIELD1 = 0;

    private static final long MASK_INT_FIELD1 = 1 << INDEX_INT_FIELD1;

    private static final int INDEX_VARIANT_OF_INT64 = 1;

    private static final long MASK_VARIANT_OF_INT64 = 1 << INDEX_VARIANT_OF_INT64;

    private static final int INDEX_VARIANT_OF_INT8 = 2;

    private static final long MASK_VARIANT_OF_INT8 = 1 << INDEX_VARIANT_OF_INT8;

    private static final int INDEX_INT_FIELD2 = 3;

    private static final long MASK_INT_FIELD2 = 1 << INDEX_INT_FIELD2;

    private static final int INDEX_VARIANT_OF_INT16 = 4;

    private static final long MASK_VARIANT_OF_INT16 = 1 << INDEX_VARIANT_OF_INT16;

    private static final int INDEX_VARIANT_OF_INT32 = 5;

    private static final long MASK_VARIANT_OF_INT32 = 1 << INDEX_VARIANT_OF_INT32;

    private static final int INDEX_VARIANT_OF_UINT8 = 6;

    private static final long MASK_VARIANT_OF_UINT8 = 1 << INDEX_VARIANT_OF_UINT8;

    private static final int INDEX_VARIANT_OF_UINT16 = 7;

    private static final long MASK_VARIANT_OF_UINT16 = 1 << INDEX_VARIANT_OF_UINT16;

    private static final int DEFAULT_VALUE_VARIANT_OF_UINT16 = 60000;

    private static final int INDEX_VARIANT_OF_UINT32 = 8;

    private static final long MASK_VARIANT_OF_UINT32 = 1 << INDEX_VARIANT_OF_UINT32;

    private static final long DEFAULT_VALUE_VARIANT_OF_UINT32 = 0;

    private static final int INDEX_VARIANT_OF_STRING32 = 9;

    private static final long MASK_VARIANT_OF_STRING32 = 1 << INDEX_VARIANT_OF_STRING32;

    private VariantUint8KindWithInt64TypeFW variantOfInt64RO = new VariantUint8KindWithInt64TypeFW();

    private VariantEnumKindOfInt8FW variantOfInt8RO = new VariantEnumKindOfInt8FW();

    private VariantEnumKindOfInt16FW variantOfInt16RO = new VariantEnumKindOfInt16FW();

    private VariantEnumKindWithInt32FW variantOfInt32RO = new VariantEnumKindWithInt32FW();

    private VariantEnumKindOfUint8FW variantOfUint8RO = new VariantEnumKindOfUint8FW();

    private VariantEnumKindOfUint16FW variantOfUint16RO = new VariantEnumKindOfUint16FW();

    private VariantEnumKindOfUint32FW variantOfUint32RO = new VariantEnumKindOfUint32FW();

    private VariantEnumKindWithString32FW variantOfString32RO = new VariantEnumKindWithString32FW();

    private final int[] optionalOffsets = new int[INDEX_VARIANT_OF_STRING32 + 1];

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
        assert (bitmask() & MASK_INT_FIELD1) != 0L : "Field \"intField1\" is not set";
        return buffer().getByte(optionalOffsets[INDEX_INT_FIELD1]);
    }

    public long variantOfInt64()
    {
        assert (bitmask() & MASK_VARIANT_OF_INT64) != 0L : "Field \"variantOfInt64Uint8Kind\" is not set";
        return variantOfInt64RO.get();
    }

    public int variantOfInt8()
    {
        assert (bitmask() & MASK_VARIANT_OF_INT8) != 0L : "Field \"variantOfInt8EnumKind\" is not set";
        return variantOfInt8RO.get();
    }

    public int intField2()
    {
        assert (bitmask() & MASK_INT_FIELD2) != 0L : "Field \"intField2\" is not set";
        return buffer().getShort(optionalOffsets[INDEX_INT_FIELD2]);
    }

    public int variantOfInt16()
    {
        assert (bitmask() & MASK_VARIANT_OF_INT16) != 0L : "Field \"variantOfInt16EnumKind\" is not set";
        return variantOfInt16RO.get();
    }

    public int variantOfInt32()
    {
        assert (bitmask() & MASK_VARIANT_OF_INT32) != 0L : "Field \"variantOfInt32EnumKind\" is not set";
        return variantOfInt32RO.get();
    }

    public int variantOfUint8()
    {
        assert (bitmask() & MASK_VARIANT_OF_UINT8) != 0L : "Field \"variantOfUint8\" is not set";
        return variantOfUint8RO.get();
    }

    public int variantOfUint16()
    {
        return (bitmask() & MASK_VARIANT_OF_UINT16) == 0 ? DEFAULT_VALUE_VARIANT_OF_UINT16 : variantOfUint16RO.get();
    }

    public long variantOfUint32()
    {
        return (bitmask() & MASK_VARIANT_OF_UINT32) == 0 ? DEFAULT_VALUE_VARIANT_OF_UINT32 : variantOfUint32RO.get();
    }

    public String variantOfString32()
    {
        assert (bitmask() & INDEX_VARIANT_OF_STRING32) != 0 : "Field \"variantOfString32\" is not set";
        return variantOfString32RO.get();
    }

    @Override
    public ListWithVariantFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(offset + PHYSICAL_LENGTH_OFFSET + PHYSICAL_LENGTH_SIZE, maxLimit);
        final int limit = limit();
        checkLimit(limit, maxLimit);
        final long bitmask = bitmask();
        int fieldLimit = offset + BIT_MASK_OFFSET + BIT_MASK_SIZE;
        for (int field = INDEX_INT_FIELD1; field < INDEX_VARIANT_OF_STRING32 + 1; field++)
        {
            switch (field)
            {
            case INDEX_INT_FIELD1:
                if ((bitmask & MASK_INT_FIELD1) != 0)
                {
                    optionalOffsets[INDEX_INT_FIELD1] = fieldLimit;
                    fieldLimit += FIELD_SIZE_INTFIELD1;
                }
                break;
            case INDEX_VARIANT_OF_INT64:
                if ((bitmask & MASK_VARIANT_OF_INT64) != 0)
                {
                    variantOfInt64RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfInt64RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_INT8:
                if ((bitmask & MASK_VARIANT_OF_INT8) != 0)
                {
                    variantOfInt8RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfInt8RO.limit();
                }
                break;
            case INDEX_INT_FIELD2:
                if ((bitmask & MASK_INT_FIELD2) != 0)
                {
                    optionalOffsets[INDEX_INT_FIELD2] = fieldLimit;
                    fieldLimit += FIELD_SIZE_INTFIELD2;
                }
                break;
            case INDEX_VARIANT_OF_INT16:
                if ((bitmask & MASK_VARIANT_OF_INT16) != 0)
                {
                    variantOfInt16RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfInt16RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_INT32:
                if ((bitmask & MASK_VARIANT_OF_INT32) != 0)
                {
                    variantOfInt32RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfInt32RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_UINT8:
                if ((bitmask & MASK_VARIANT_OF_UINT8) == 0)
                {
                    throw new IllegalArgumentException("Field \"variantOfUint8\" is required but not set");
                }
                variantOfUint8RO.wrap(buffer, fieldLimit, maxLimit);
                fieldLimit = variantOfUint8RO.limit();
                break;
            case INDEX_VARIANT_OF_UINT16:
                if ((bitmask & MASK_VARIANT_OF_UINT16) != 0)
                {
                    variantOfUint16RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfUint16RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_UINT32:
                if ((bitmask & MASK_VARIANT_OF_UINT32) != 0)
                {
                    variantOfUint32RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfUint32RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_STRING32:
                if ((bitmask & MASK_VARIANT_OF_STRING32) != 0)
                {
                    variantOfString32RO.wrap(buffer, fieldLimit, maxLimit);
                    fieldLimit = variantOfString32RO.limit();
                }
                break;
            }
        }
        checkLimit(fieldLimit, limit);
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
        if (offset + PHYSICAL_LENGTH_OFFSET + PHYSICAL_LENGTH_SIZE > maxLimit)
        {
            return null;
        }
        final int limit = limit();
        if (limit > maxLimit)
        {
            return null;
        }
        final long bitmask = bitmask();
        int fieldLimit = offset + BIT_MASK_OFFSET + BIT_MASK_SIZE;
        for (int field = INDEX_INT_FIELD1; field < INDEX_VARIANT_OF_STRING32 + 1; field++)
        {
            switch (field)
            {
            case INDEX_INT_FIELD1:
                if ((bitmask & MASK_INT_FIELD1) != 0)
                {
                    optionalOffsets[INDEX_INT_FIELD1] = fieldLimit;
                    fieldLimit += FIELD_SIZE_INTFIELD1;
                }
                break;
            case INDEX_VARIANT_OF_INT64:
                if ((bitmask & MASK_VARIANT_OF_INT64) != 0)
                {
                    if (variantOfInt64RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt64RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_INT8:
                if ((bitmask & MASK_VARIANT_OF_INT8) != 0)
                {
                    if (variantOfInt8RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt8RO.limit();
                }
                break;
            case INDEX_INT_FIELD2:
                if ((bitmask & MASK_INT_FIELD2) != 0)
                {
                    optionalOffsets[INDEX_INT_FIELD2] = fieldLimit;
                    fieldLimit += FIELD_SIZE_INTFIELD2;
                }
                break;
            case INDEX_VARIANT_OF_INT16:
                if ((bitmask & MASK_VARIANT_OF_INT16) != 0)
                {
                    if (variantOfInt16RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt16RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_INT32:
                if ((bitmask & MASK_VARIANT_OF_INT32) != 0)
                {
                    if (variantOfInt32RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfInt32RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_UINT8:
                if ((bitmask & MASK_VARIANT_OF_UINT8) == 0)
                {
                    return null;
                }
                if (variantOfUint8RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                {
                    return null;
                }
                fieldLimit = variantOfUint8RO.limit();
                break;
            case INDEX_VARIANT_OF_UINT16:
                if ((bitmask & MASK_VARIANT_OF_UINT16) != 0)
                {
                    if (variantOfUint16RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfUint16RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_UINT32:
                if ((bitmask & MASK_VARIANT_OF_UINT32) != 0)
                {
                    if (variantOfUint32RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfUint32RO.limit();
                }
                break;
            case INDEX_VARIANT_OF_STRING32:
                if ((bitmask & MASK_VARIANT_OF_STRING32) != 0)
                {
                    if (variantOfString32RO.tryWrap(buffer, fieldLimit, maxLimit) == null)
                    {
                        return null;
                    }
                    fieldLimit = variantOfString32RO.limit();
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
        return offset() + buffer().getByte(offset() + PHYSICAL_LENGTH_OFFSET);
    }

    @Override
    public String toString()
    {
        final long bitmask = bitmask();
        Object intField1 = null;
        Object variantOfInt64 = null;
        Object variantOfInt8 = null;
        Object intField2 = null;
        Object variantOfInt16 = null;
        Object variantOfInt32 = null;
        Object variantOfString32 = null;

        StringBuilder format = new StringBuilder();
        format.append("LIST_WITH_VARIANT_OF_INT [bitmask={0}");
        if ((bitmask & MASK_INT_FIELD1) != 0L)
        {
            format.append(", intField1={1}");
            intField1 = intField1();
        }
        if ((bitmask & MASK_VARIANT_OF_INT64) != 0L)
        {
            format.append(", variantOfInt64={2}");
            variantOfInt64 = variantOfInt64();
        }
        if ((bitmask & MASK_VARIANT_OF_INT8) != 0)
        {
            format.append(", variantOfInt8={3}");
            variantOfInt8 = variantOfInt8();
        }
        if ((bitmask & MASK_INT_FIELD2) != 0)
        {
            format.append(", intField2={4}");
            intField2 = intField2();
        }
        if ((bitmask & MASK_VARIANT_OF_INT16) != 0)
        {
            format.append(", variantOfInt16={5}");
            variantOfInt16 = variantOfInt16();
        }
        if ((bitmask & MASK_VARIANT_OF_INT32) != 0)
        {
            format.append(", variantOfInt32={6}");
            variantOfInt32 = variantOfInt32();
        }
        format.append(", variantOfUint8={7}");
        format.append(", variantOfUint16={8}");
        format.append(", variantOfUint32={9}");
        if ((bitmask & MASK_VARIANT_OF_STRING32) != 0)
        {
            format.append(", variantOfString32={10}");
            variantOfString32 = variantOfString32();
        }
        format.append("]");
        return MessageFormat.format(format.toString(),
            String.format("0x%16x", bitmask),
            intField1,
            variantOfInt64,
            variantOfInt8,
            intField2,
            variantOfInt16,
            variantOfInt32,
            variantOfUint8(),
            variantOfUint16(),
            variantOfUint32(),
            variantOfString32);
    }

    public static final class Builder extends Flyweight.Builder<ListWithVariantFW>
    {
        private final VariantUint8KindWithInt64TypeFW.Builder variantOfInt64RW =
            new VariantUint8KindWithInt64TypeFW.Builder();

        private final VariantEnumKindOfInt8FW.Builder variantOfInt8RW = new VariantEnumKindOfInt8FW.Builder();

        private final VariantEnumKindOfInt16FW.Builder variantOfInt16RW = new VariantEnumKindOfInt16FW.Builder();

        private final VariantEnumKindWithInt32FW.Builder variantOfInt32RW = new VariantEnumKindWithInt32FW.Builder();

        private final VariantEnumKindOfUint8FW.Builder variantOfUint8RW = new VariantEnumKindOfUint8FW.Builder();

        private final VariantEnumKindOfUint16FW.Builder variantOfUint16RW = new VariantEnumKindOfUint16FW.Builder();

        private final VariantEnumKindOfUint32FW.Builder variantOfUint32RW = new VariantEnumKindOfUint32FW.Builder();

        private final VariantEnumKindWithString32FW.Builder variantOfString32RW = new VariantEnumKindWithString32FW.Builder();

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
            fieldsMask |= MASK_INT_FIELD1;
            limit(newLimit);
            return this;
        }

        private VariantUint8KindWithInt64TypeFW.Builder variantOfInt64()
        {
            assert (fieldsMask & ~0x01) == 0 : "Field \"variantOfInt64\" cannot be set out of order";
            return variantOfInt64RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder variantOfInt64(
            long value)
        {
            VariantUint8KindWithInt64TypeFW.Builder variantOfInt64RW = variantOfInt64();
            variantOfInt64RW.set(value);
            fieldsMask |= MASK_VARIANT_OF_INT64;
            limit(variantOfInt64RW.build().limit());
            return this;
        }

        private VariantEnumKindOfInt8FW.Builder variantOfInt8()
        {
            assert (fieldsMask & ~0x03) == 0 : "Field \"variantOfInt8\" cannot be set out of order";
            return variantOfInt8RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder variantOfInt8(
            int value)
        {
            VariantEnumKindOfInt8FW.Builder variantOfInt8RW = variantOfInt8();
            variantOfInt8RW.set(value);
            fieldsMask |= MASK_VARIANT_OF_INT8;
            limit(variantOfInt8RW.build().limit());
            return this;
        }

        public Builder intField2(
            short value)
        {
            assert (fieldsMask & ~0x07) == 0 : "Field \"intField2\" cannot be set out of order";
            int newLimit = limit() + FIELD_SIZE_INTFIELD2;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(limit(), value);
            fieldsMask |= MASK_INT_FIELD2;
            limit(newLimit);
            return this;
        }

        private VariantEnumKindOfInt16FW.Builder variantOfInt16()
        {
            assert (fieldsMask & ~0x0F) == 0 : "Field \"variantOfInt16\" cannot be set out of order";
            return variantOfInt16RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder variantOfInt16(
            int value)
        {
            VariantEnumKindOfInt16FW.Builder variantOfInt16RW = variantOfInt16();
            variantOfInt16RW.set(value);
            fieldsMask |= MASK_VARIANT_OF_INT16;
            limit(variantOfInt16RW.build().limit());
            return this;
        }

        private VariantEnumKindWithInt32FW.Builder variantOfInt32()
        {
            assert (fieldsMask & ~0x1F) == 0 : "Field \"variantOfInt32\" cannot be set out of order";
            return variantOfInt32RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder variantOfInt32(
            int value)
        {
            VariantEnumKindWithInt32FW.Builder variantOfInt32RW = variantOfInt32();
            variantOfInt32RW.set(value);
            fieldsMask |= MASK_VARIANT_OF_INT32;
            limit(variantOfInt32RW.build().limit());
            return this;
        }

        private VariantEnumKindOfUint8FW.Builder variantOfUint8()
        {
            assert (fieldsMask & ~0x3F) == 0 : "Field \"variantOfUint8\" cannot be set out of order";
            return variantOfUint8RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder variantOfUint8(
            int value)
        {
            VariantEnumKindOfUint8FW.Builder variantOfUint8RW = variantOfUint8();
            variantOfUint8RW.set(value);
            fieldsMask |= MASK_VARIANT_OF_UINT8;
            limit(variantOfUint8RW.build().limit());
            return this;
        }

        private VariantEnumKindOfUint16FW.Builder variantOfUint16()
        {
            assert (fieldsMask & ~0x7F) == 0 : "Field \"variantOfUint16\" cannot be set out of order";
            assert (fieldsMask & 0x40) != 0 : "Prior required field \"variantOfUint8\" is not set";
            return variantOfUint16RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder variantOfUint16(
            int value)
        {
            VariantEnumKindOfUint16FW.Builder variantOfUint16RW = variantOfUint16();
            variantOfUint16RW.set(value);
            fieldsMask |= MASK_VARIANT_OF_UINT16;
            limit(variantOfUint16RW.build().limit());
            return this;
        }

        private VariantEnumKindOfUint32FW.Builder variantOfUint32()
        {
            assert (fieldsMask & ~0xFF) == 0 : "Field \"variantOfUint32\" cannot be set out of order";
            assert (fieldsMask & 0x40) != 0 : "Prior required field \"variantOfUint8\" is not set";
            return variantOfUint32RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder variantOfUint32(
            long value)
        {
            VariantEnumKindOfUint32FW.Builder variantOfUint32RW = variantOfUint32();
            variantOfUint32RW.set(value);
            fieldsMask |= MASK_VARIANT_OF_UINT32;
            limit(variantOfUint32RW.build().limit());
            return this;
        }

        private VariantEnumKindWithString32FW.Builder variantOfString32()
        {
            assert (fieldsMask & ~0x1FF) == 0 : "Field \"variantOfString32\" cannot be set out of order";
            assert (fieldsMask & 0x40) != 0 : "Prior required field \"variantOfUint8\" is not set";
            return variantOfString32RW.wrap(buffer(), limit(), maxLimit());
        }

        public Builder variantOfString32(
            String value)
        {
            VariantEnumKindWithString32FW.Builder variantOfString32RW = variantOfString32();
            variantOfString32RW.set(value);
            fieldsMask |= MASK_VARIANT_OF_STRING32;
            limit(variantOfString32RW.build().limit());
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
