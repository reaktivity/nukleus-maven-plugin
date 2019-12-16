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

import java.util.function.Consumer;

import org.agrona.DirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public abstract class ArrayFW<T extends VariantFW<? extends Flyweight, ?>> extends Flyweight
{
    public abstract int length();

    public abstract int fieldCount();

    public abstract void forEach(Consumer<T> consumer);

    public abstract DirectBuffer items();

    public abstract static class Builder<B extends VariantFW.Builder<O, ?, V>,
        O extends Flyweight, V extends VariantFW<O, ?>, T extends ArrayFW<V>> extends Flyweight.Builder<T>
    {
        protected final B itemRW;

        private int maxLength;

        private int fieldCount;

        public Builder(
            T flyweight,
            B itemRW)
        {
            super(flyweight);
            this.itemRW = itemRW;
        }

        public Builder item(
            O item)
        {
            maxLength = Math.max(maxLength, item.sizeof());
            checkLimit(itemRW.limit(), maxLimit());
            limit(itemRW.limit());
            fieldCount++;
            return this;
        }

        public Builder items(
            DirectBuffer buffer,
            int srcOffset,
            int length,
            int fieldCount)
        {
            this.fieldCount = fieldCount;
            return this;
        }

        public int fieldCount()
        {
            return fieldCount;
        }

        public int maxLength()
        {
            return maxLength;
        }
    }
}
