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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public abstract class StringFW extends Flyweight
{
    public abstract String asString();

    protected abstract int length();

    public abstract static class Builder<T extends StringFW> extends Flyweight.Builder<T>
    {
        private boolean valueSet;

        public Builder(T flyweight)
        {
            super(flyweight);
        }

        @Override
        public Builder<T> wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            this.valueSet = false;
            return this;
        }

        public Builder set(
            T value)
        {
            valueSet = true;
            return this;
        }

        public Builder set(
            DirectBuffer srcBuffer,
            int srcOffset,
            int length)
        {
            valueSet = true;
            return this;
        }

        public Builder set(
            String value,
            Charset charset)
        {
            valueSet = true;
            return this;
        }

        @Override
        public T build()
        {
            if (!valueSet)
            {
                set(null, StandardCharsets.UTF_8);
            }
            return super.build();
        }
    }
}
