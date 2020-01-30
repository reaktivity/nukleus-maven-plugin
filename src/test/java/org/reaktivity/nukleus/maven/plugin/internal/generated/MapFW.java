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
import java.util.function.Function;

import org.agrona.DirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public abstract class MapFW<K extends Flyweight, V extends Flyweight> extends Flyweight
{
    public abstract int length();

    public abstract int fieldCount();

    public abstract void forEach(Function<K, Consumer<V>> consumer);

    public abstract DirectBuffer entries();

    public abstract static class Builder<T extends MapFW, K extends Flyweight, V extends Flyweight,
        KB extends Flyweight.Builder<K>, VB extends Flyweight.Builder<V>> extends Flyweight.Builder<T>
    {
        private int fieldCount;

        protected final KB keyRW;

        protected final VB valueRW;

        public Builder(
            T flyweight,
            KB keyRW,
            VB valueRW)
        {
            super(flyweight);
            this.keyRW = keyRW;
            this.valueRW = valueRW;
        }

        public Builder<T, K, V, KB, VB> entry(
            Consumer<KB> key,
            Consumer<VB> value)
        {
            keyRW.wrap(buffer(), limit(), maxLimit());
            key.accept(keyRW);
            checkLimit(keyRW.limit(), maxLimit());
            limit(keyRW.limit());
            fieldCount++;
            valueRW.wrap(buffer(), limit(), maxLimit());
            value.accept(valueRW);
            checkLimit(valueRW.limit(), maxLimit());
            limit(valueRW.limit());
            fieldCount++;
            return this;
        }

        public Builder<T, K, V, KB, VB> entries(
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
    }
}
