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
import org.reaktivity.nukleus.maven.plugin.internal.generated.Flyweight.Builder;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public final class VariantWithFourTypesFW extends Flyweight
{
    public static final EnumWithInt8 KIND_ONE = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_TWO = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_THREE = EnumWithInt8.THREE;

    public static final EnumWithInt8 KIND_FOUR = EnumWithInt8.FOUR;

    public static final EnumWithInt8 KIND_FIVE = EnumWithInt8.FIVE;

    public static final EnumWithInt8 KIND_SIX = EnumWithInt8.SIX;

    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private final VariantOfListFW variantOfListRO = new VariantOfListFW();

    private final VariantEnumKindOfStringFW variantEnumKindOfStringRO = new VariantEnumKindOfStringFW();

    private VariantOfMapFW<VariantWithFourTypesFW, VariantWithFourTypesFW> variantOfMapRO;

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public VariantOfListFW getAsVariantOfList()
    {
        return variantOfListRO;
    }

    public VariantEnumKindOfStringFW getAsVariantEnumKindOfString()
    {
        return variantEnumKindOfStringRO;
    }

    public VariantOfMapFW<VariantWithFourTypesFW, VariantWithFourTypesFW> variantOfMapRO()
    {
        if (variantOfMapRO == null)
        {
            variantOfMapRO = new VariantOfMapFW<>(new VariantWithFourTypesFW(), new VariantWithFourTypesFW());
        }
        return variantOfMapRO;
    }

    @Override
    public VariantWithFourTypesFW tryWrap(
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
            if (variantOfListRO.tryWrap(buffer, offset, maxLimit) == null)
            {
                return null;
            }
            break;
        case FOUR:
        case FIVE:
        case SIX:
            if (variantOfMapRO().tryWrap(buffer, offset, maxLimit) == null)
            {
                return null;
            }
            break;
        case NINE:
        case TEN:
        case ELEVEN:
            if (variantEnumKindOfStringRO.tryWrap(buffer, offset, maxLimit) == null)
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
    public VariantWithFourTypesFW wrap(
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
            variantOfListRO.wrap(buffer, offset, maxLimit);
            break;
        case FOUR:
        case FIVE:
        case SIX:
            variantOfMapRO().wrap(buffer, offset, maxLimit);
            break;
        case NINE:
        case TEN:
        case ELEVEN:
            variantEnumKindOfStringRO.wrap(buffer, offset, maxLimit);
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
            return variantOfListRO.limit();
        case FOUR:
        case FIVE:
        case SIX:
            return variantOfMapRO().limit();
        case NINE:
        case TEN:
        case ELEVEN:
            return variantEnumKindOfStringRO.limit();
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
            return variantOfListRO.toString();
        case FOUR:
        case FIVE:
        case SIX:
            return variantOfMapRO().toString();
        case NINE:
        case TEN:
        case ELEVEN:
            return variantEnumKindOfStringRO.toString();
        default:
            return String.format("VARIANT_WITH_FOUR_TYPES [unknown]");
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantWithFourTypesFW>
    {
        private Flyweight.Builder typeBuilder;

        private final VariantOfListFW.Builder variantOfListRW = new VariantOfListFW.Builder();

        private VariantOfMapFW.Builder<VariantWithFourTypesFW, VariantWithFourTypesFW, VariantWithFourTypesFW.Builder,
            VariantWithFourTypesFW.Builder> variantOfMapRW;

        private VariantEnumKindOfStringFW.Builder variantEnumKindOfStringRW = new VariantEnumKindOfStringFW.Builder();

        public Builder()
        {
            super(new VariantWithFourTypesFW());
        }

        private VariantOfMapFW.Builder<VariantWithFourTypesFW, VariantWithFourTypesFW, VariantWithFourTypesFW.Builder,
            VariantWithFourTypesFW.Builder> variantOfMapRW()
        {
            if (variantOfMapRW == null)
            {
                variantOfMapRW = new VariantOfMapFW.Builder<>(new VariantWithFourTypesFW(), new VariantWithFourTypesFW(),
                    new VariantWithFourTypesFW.Builder(), new VariantWithFourTypesFW.Builder());
            }
            return variantOfMapRW;
        }

        public Builder setAsVariantOfList(
            VariantOfListFW value)
        {
            VariantOfListFW.Builder variantOfList = variantOfListRW.wrap(buffer(), offset(), maxLimit());
            variantOfList.set(value.get());
            limit(variantOfList.build().limit());
            return this;
        }

        public Builder setAsVariantOfMap(
            VariantOfMapFW<VariantWithFourTypesFW, VariantWithFourTypesFW> value)
        {
            VariantOfMapFW.Builder<VariantWithFourTypesFW, VariantWithFourTypesFW, VariantWithFourTypesFW.Builder,
                VariantWithFourTypesFW.Builder>
                variantOfMap = variantOfMapRW().wrap(buffer(), offset(), maxLimit());
            variantOfMap.entries(value.entries(), 0, value.entries().capacity(), value.fieldCount());
            limit(variantOfMap.build().limit());
            return this;
        }

        public Builder setAsVariantEnumKindOfString(
            VariantEnumKindOfStringFW value)
        {
            VariantEnumKindOfStringFW.Builder variantOfEnumKindOfString =
                variantEnumKindOfStringRW.wrap(buffer(), offset(), maxLimit());
            variantOfEnumKindOfString.set(value.get());
            limit(variantOfEnumKindOfString.build().limit());
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
