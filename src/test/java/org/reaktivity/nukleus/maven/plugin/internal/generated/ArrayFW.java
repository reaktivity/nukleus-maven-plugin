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

public abstract class ArrayFW<V extends VariantFW<?, ? extends Flyweight>> extends Flyweight
{
    public abstract int length();

    public abstract int fieldCount();

    public abstract void forEach(Consumer<V> consumer);

    public abstract DirectBuffer items();

    public abstract static class Builder<T extends ArrayFW<V>, B extends VariantFW.Builder<V, K, O>, V extends VariantFW<K, O>,
        K, O extends Flyweight> extends Flyweight.Builder<T>
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

        public Builder<T, B, V, K, O> item(
            O item)
        {
            maxLength = Math.max(maxLength, item.sizeof());
            checkLimit(itemRW.limit(), maxLimit());
            limit(itemRW.limit());
            fieldCount++;
            return this;
        }

        public Builder<T, B, V, K, O> items(
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

        protected void relayout()
        {
            if (maxLength > 0 && !itemRW.maxKind().equals(itemRW.kindFromLength(maxLength)))
            {
                K kind = itemRW.kindFromLength(maxLength);
                int originalPadding = 0;
                int rearrangePadding = 0;
                int originalLimit = itemRW.limit();
                for (int i = 0; i < fieldCount; i++)
                {
                    V itemRO = itemRW.build(originalLimit);
                    O originalItem = itemRO.getAs(itemRW.maxKind(), originalPadding);
                    originalPadding += originalItem.sizeof();
                    itemRW.setAs(kind, originalItem, rearrangePadding);
                    O rearrangedItem = itemRO.getAs(kind, rearrangePadding);
                    rearrangePadding += rearrangedItem.sizeof();
                }
                limit(itemRW.limit());
            }
        }
    }
}
