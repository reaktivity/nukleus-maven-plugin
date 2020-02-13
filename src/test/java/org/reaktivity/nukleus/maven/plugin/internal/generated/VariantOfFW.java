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

public abstract class VariantOfFW<K, V extends Flyweight> extends VariantFW<K>
{
    public abstract V get();

    public V getAs(
        K kind,
        int kindPadding)
    {
        return null;
    }

    public VariantOfFW<K, V> wrapWithKindPadding(
        DirectBuffer buffer,
        int elementsOffset,
        int maxLimit,
        int kindPadding)
    {
        return this;
    }

    public abstract static class Builder<T extends VariantOfFW<K, V>, K, V extends Flyweight> extends VariantFW.Builder<T, K>
    {
        protected Builder(
            T flyweight)
        {
            super(flyweight);
        }

        public Builder<T, K, V> setAs(
            K kind,
            V value,
            int kindPadding)
        {
            return this;
        }

        public Builder<T, K, V> set(
            V value)
        {
            return this;
        }

        public K maxKind()
        {
            return null;
        }

        public int sizeWithoutKind()
        {
            return 0;
        }

        public K kindFromLength(
            int length)
        {
            return null;
        }

        public abstract Builder<T, K, V> kind(
            K value);

        public abstract T build(
            int maxLimit);
    }
}
