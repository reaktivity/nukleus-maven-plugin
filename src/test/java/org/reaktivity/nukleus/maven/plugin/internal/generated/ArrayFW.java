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

import java.util.function.Consumer;

import org.agrona.DirectBuffer;

public abstract class ArrayFW<V extends Flyweight> extends Flyweight
{
    private int valuePadding;

    public abstract int length();

    public abstract int fieldCount();

    public abstract void forEach(Consumer<V> consumer);

    public abstract DirectBuffer items();

    public abstract int fieldsOffset();

    public final int valuePadding()
    {
        return valuePadding;
    }

    public void valuePadding(
        int valuePadding)
    {
        this.valuePadding = valuePadding;
    }

    public abstract static class Builder<T extends ArrayFW<V>, B extends Flyweight.Builder<V>,
        V extends Flyweight> extends Flyweight.Builder<T>
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

        public Builder<T, B, V> item(
            Consumer<B> consumer)
        {
            maxLength = Math.max(maxLength, itemRW.sizeWithoutKind());
            checkLimit(itemRW.limit(), maxLimit());
            limit(itemRW.limit());
            fieldCount++;
            return this;
        }

        public Builder<T, B, V> items(
            DirectBuffer buffer,
            int srcOffset,
            int length,
            int fieldCount)
        {
            this.fieldCount = fieldCount;
            return this;
        }

        public abstract int fieldsOffset();

        public int fieldCount()
        {
            return fieldCount;
        }

        protected void relayout()
        {
            int originalPadding = 0;
            int rearrangePadding = 0;
            try
            {
                for (int i = 0; i < fieldCount; i++)
                {
                    int[] padding = itemRW.relayout(this, maxLength, originalPadding, rearrangePadding);
                    originalPadding = padding[0];
                    rearrangePadding = padding[1];
                }
            }
            catch (UnsupportedOperationException ignored)
            {
            }
            limit(itemRW.limit());
        }
    }
}
