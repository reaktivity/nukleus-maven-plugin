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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantWithoutOfFW extends VariantFW<EnumWithInt8>
{
    public static final EnumWithInt8 KIND_ONE = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_TWO = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_THREE = EnumWithInt8.THREE;

    public static final EnumWithInt8 KIND_FOUR = EnumWithInt8.FOUR;

    public static final EnumWithInt8 KIND_FIVE = EnumWithInt8.FIVE;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final VariantOfInt32FW variantOfInt32RO = new VariantOfInt32FW();

    private final VariantEnumKindOfStringFW variantEnumKindWithString32RO = new VariantEnumKindOfStringFW();

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public VariantOfInt32FW getAsVariantOfInt32()
    {
        return variantOfInt32RO;
    }

    public VariantEnumKindOfStringFW getAsVariantEnumKindWithString32()
    {
        return variantEnumKindWithString32RO;
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
        case TWO:
        case THREE:
            if (variantEnumKindWithString32RO.tryWrap(buffer, offset, maxLimit) == null)
            {
                return null;
            }
            break;
        case FOUR:
        case FIVE:
            if (variantOfInt32RO.tryWrap(buffer, offset, maxLimit) == null)
            {
                return null;
            }
            break;
        default:
            break;
        }
        if (limit() > maxLimit)
        {
            return null;
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
        case TWO:
        case THREE:
            variantEnumKindWithString32RO.wrap(buffer, offset, maxLimit);
            break;
        case FOUR:
        case FIVE:
            variantOfInt32RO.wrap(buffer, offset, maxLimit);
            break;
        default:
            break;
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ONE:
        case TWO:
        case THREE:
            return variantEnumKindWithString32RO.limit();
        case FOUR:
        case FIVE:
            return variantOfInt32RO.limit();
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
        case TWO:
        case THREE:
            return variantEnumKindWithString32RO.toString();
        case FOUR:
        case FIVE:
            return variantOfInt32RO.toString();
        default:
            return String.format("VARIANT_WITHOUT_OF [unknown]");
        }
    }

    public static final class Builder extends VariantFW.Builder<VariantWithoutOfFW, EnumWithInt8>
    {
        private final VariantOfInt32FW.Builder variantOfInt32RW = new VariantOfInt32FW.Builder();

        private final VariantEnumKindOfStringFW.Builder variantEnumKindWithString32RW =
            new VariantEnumKindOfStringFW.Builder();

        public Builder()
        {
            super(new VariantWithoutOfFW());
        }

        public Builder setAsVariantOfInt32(
            VariantOfInt32FW value)
        {
            VariantOfInt32FW.Builder variantOfInt32 = variantOfInt32RW.wrap(buffer(), offset(),
                maxLimit());
            variantOfInt32.set(value.get());
            limit(variantOfInt32.build().limit());
            return this;
        }

        public Builder setAsVariantEnumKindWithString32(
            VariantEnumKindOfStringFW value)
        {
            VariantEnumKindOfStringFW.Builder variantEnumKindWithString32 = variantEnumKindWithString32RW.wrap(buffer(),
                offset(), maxLimit());
            variantEnumKindWithString32.set(value.get());
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
