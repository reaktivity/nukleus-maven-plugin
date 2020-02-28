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
    public abstract int length();

    public abstract int fieldCount();

    public abstract int fieldsOffset();

    public abstract int maxLength();

    public abstract void forEach(Consumer<V> consumer);

    public abstract DirectBuffer items();

    protected abstract void maxLength(int maxLength);

    public abstract static class Builder<T extends ArrayFW<V>, B extends Flyweight.Builder<V>,
        V extends Flyweight> extends Flyweight.Builder<T>
    {
        public Builder(
            T flyweight)
        {
            super(flyweight);
        }

        public abstract Builder<T, B, V> item(Consumer<B> consumer);

        public abstract Builder<T, B, V> items(DirectBuffer buffer, int srcOffset, int length, int fieldCount, int maxLength);

        public abstract int fieldsOffset();
    }
}
