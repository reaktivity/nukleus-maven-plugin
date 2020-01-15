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
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfInt8FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithString32FW;

public final class VariantWithoutOfFW extends Flyweight
{
    private static final int FIELD_SIZE_KIND = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_OFFSET_KIND = 0;

    public static final EnumWithInt8 KIND_ONE = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_TWO = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_THREE = EnumWithInt8.THREE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final VariantEnumKindOfInt8FW variantEnumKindOfInt8RO = new VariantEnumKindOfInt8FW();

    private final VariantEnumKindWithString32FW variantEnumKindWithString32RO = new VariantEnumKindWithString32FW();

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public long getAsVariantEnumKindOfInt8()
    {
        return variantEnumKindOfInt8RO.get();
    }

    public StringFW getAsVariantEnumKindWithString32()
    {
        return variantEnumKindWithString32RO.get();
    }

    @Override
    public VariantWithoutOfFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (super.tryWrap(buffer, offset, maxLimit) == null)
        {
            return null;
        }
        EnumWithInt8FW enumWithInt8 = enumWithInt8RO.tryWrap(buffer, offset, maxLimit);
        if (enumWithInt8 == null)
        {
            return null;
        }
        switch (kind())
        {
        case ONE:
            variantEnumKindOfInt8RO.tryWrap(buffer, offset, maxLimit);
            break;
        case TWO:
        case THREE:
            variantEnumKindWithString32RO.tryWrap(buffer, offset, maxLimit);
            break;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
        return this;
    }

    @Override
    public VariantWithoutOfFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        enumWithInt8RO.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            variantEnumKindOfInt8RO.wrap(buffer, offset, maxLimit);
            break;
        case TWO:
        case THREE:
            variantEnumKindWithString32RO.wrap(buffer, offset, maxLimit);
            break;
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
        return this;
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ONE:
            return variantEnumKindOfInt8RO.limit();
        case TWO:
        case THREE:
            return variantEnumKindWithString32RO.limit();
        default:
            return offset();
        }
    }

    @Override
    public String toString()
    {
        switch (kind())
        {
        case ONE:
            return variantEnumKindOfInt8RO.toString();
        case TWO:
        case THREE:
            return variantEnumKindWithString32RO.toString();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantWithoutOfFW>
    {
        private final VariantEnumKindOfInt8FW.Builder variantEnumKindOfInt8RW = new VariantEnumKindOfInt8FW.Builder();

        private final VariantEnumKindWithString32FW.Builder variantEnumKindWithString32RW =
            new VariantEnumKindWithString32FW.Builder();

        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        public Builder()
        {
            super(new VariantWithoutOfFW());
        }

        public Builder setAsVariantEnumKindOfUint32(
            int value)
        {
            VariantEnumKindOfInt8FW.Builder variantEnumKindOfInt8 = variantEnumKindOfInt8RW.wrap(buffer(), offset(),
                maxLimit());
            variantEnumKindOfInt8.set(value);
            limit(variantEnumKindOfInt8.build().limit());
            return this;
        }

        public Builder setAsVariantEnumKindWithString32(
            StringFW value)
        {
            VariantEnumKindWithString32FW.Builder variantEnumKindWithString32 = variantEnumKindWithString32RW.wrap(buffer(),
                offset(), maxLimit());
            variantEnumKindWithString32.set(value);
            limit(variantEnumKindWithString32.build().limit());
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }
    }
}
