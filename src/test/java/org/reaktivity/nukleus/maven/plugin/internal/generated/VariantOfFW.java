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

import org.agrona.DirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public abstract class VariantOfFW<K, O extends Flyweight> extends VariantFW<K>
{
    public abstract O get();

    public O getAs(
        K kind,
        int kindPadding)
    {
        return null;
    }

    public VariantOfFW<K, O> wrapWithKindPadding(
        DirectBuffer buffer,
        int elementsOffset,
        int maxLimit,
        int kindPadding)
    {
        return this;
    }

    public abstract static class Builder<V extends VariantOfFW<K, O>, K, O extends Flyweight> extends VariantFW.Builder<V, K>
    {
        protected Builder(
            V flyweight)
        {
            super(flyweight);
        }

        public Builder<V, K, O> setAs(
            K kind,
            O value,
            int kindPadding)
        {
            return this;
        }

        public Builder<V, K, O> set(
            O value)
        {
            return this;
        }

        public K maxKind()
        {
            return null;
        }

        public int size()
        {
            return 0;
        }

        public K kindFromLength(
            int length)
        {
            return null;
        }

        public abstract Builder<V, K, O> kind(
            K value);

        public abstract V build(
            int maxLimit);
    }
}