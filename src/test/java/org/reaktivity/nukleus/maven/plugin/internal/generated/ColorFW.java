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

import java.nio.charset.Charset;

// TODO: Will be removed
public final class ColorFW extends Flyweight
{
    private static final int FIELD_OFFSET_VALUE = 0;

    private static final int FIELD_SIZE_VALUE = BitUtil.SIZE_OF_BYTE;

    private final StringFW stringRO = new StringFW();

    public StringFW string()
    {
        return stringRO;
    }

    @Override
    public int limit()
    {
        return stringRO.limit();
    }

    public Color get()
    {
        return stringRO.asString() != null ? Color.valueOf(stringRO.asString().toUpperCase()) : null;
    }

    @Override
    public ColorFW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        if (null == stringRO.tryWrap(buffer, offset, maxLimit))
        {
            return null;
        }
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public ColorFW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        stringRO.wrap(buffer, offset, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        return maxLimit() == offset() ? "null" : get().toString();
    }

    public static final class Builder extends Flyweight.Builder<ColorFW>
    {
        private final StringFW.Builder stringRW = new StringFW.Builder();

        private boolean valueSet;

        public Builder()
        {
            super(new ColorFW());
        }

        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            stringRW.wrap(buffer, offset, maxLimit);
            super.wrap(buffer, offset, maxLimit);
            return this;
        }

        public Builder set(
            ColorFW value)
        {
            stringRW.set(value.string());
            limit(stringRW.build().limit());
            valueSet = true;
            return this;
        }

        public Builder set(
            Color value,
            Charset charset)
        {
            stringRW.set(value.value(), charset);
            limit(stringRW.build().limit());
            valueSet = true;
            return this;
        }

        @Override
        public ColorFW build()
        {
            if (!valueSet)
            {
                throw new IllegalStateException("Color not set");
            }
            return super.build();
        }
    }
}
