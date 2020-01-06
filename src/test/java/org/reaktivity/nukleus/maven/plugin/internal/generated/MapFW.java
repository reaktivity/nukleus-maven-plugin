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

import org.reaktivity.reaktor.internal.test.types.Flyweight;

public abstract class MapFW extends Flyweight
{
    public abstract int length();

    public abstract int fieldCount();

    public abstract static class Builder<T extends MapFW,
        KB extends VariantFW.Builder<KV, KK, KO>, KV extends VariantFW<KK, KO>, KK, KO extends Flyweight,
        VB extends VariantFW.Builder<VV, VK, VO>, VV extends VariantFW<VK, VO>, VK, VO extends Flyweight>
        extends Flyweight.Builder<T>
    {
        private int fieldCount;
        private boolean keyIsSet;
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

        public Builder<T, KB, KV, KK, KO, VB, VV, VK, VO> key(
            KO key)
        {
            assert !keyIsSet : "Key is already set";
            keyRW.wrap(buffer(), limit(), maxLimit());
            keyRW.set(key);
            checkLimit(keyRW.limit(), maxLimit());
            limit(keyRW.limit());
            fieldCount++;
            keyIsSet = true;
            return this;
        }

        public Builder<T, KB, KV, KK, KO, VB, VV, VK, VO> value(
            VO value)
        {
            assert keyIsSet : "Key needs to be set first";
            valueRW.wrap(buffer(), limit(), maxLimit());
            valueRW.set(value);
            checkLimit(valueRW.limit(), maxLimit());
            limit(valueRW.limit());
            fieldCount++;
            keyIsSet = false;
            return this;
        }

        public int fieldCount()
        {
            return fieldCount;
        }

        public boolean iskeySet()
        {
            return keyIsSet;
        }
    }
}
