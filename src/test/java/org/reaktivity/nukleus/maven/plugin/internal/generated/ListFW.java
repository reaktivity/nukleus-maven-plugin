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
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public abstract class ListFW extends Flyweight
{
    public abstract int length();

    public abstract int fieldCount();

    public abstract DirectBuffer fields();

    public abstract static class Builder<T extends ListFW> extends Flyweight.Builder<T>
    {
        private int fieldCount;

        public Builder(T flyweight)
        {
            super(flyweight);
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            fieldCount = 0;
            return this;
        }

        public Builder field(
            Flyweight.Builder.Visitor visitor)
        {
            int length = visitor.visit(buffer(), limit(), maxLimit());
            fieldCount++;
            int newLimit = limit() + length;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            return this;
        }

        public Builder fields(
            int fieldCount,
            Flyweight.Builder.Visitor visitor)
        {
            int length = visitor.visit(buffer(), limit(), maxLimit());
            this.fieldCount += fieldCount;
            int newLimit = limit() + length;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            return this;
        }

        public Builder fields(
            int fieldCount,
            DirectBuffer buffer,
            int index,
            int length)
        {
            this.fieldCount += fieldCount;
            int newLimit = limit() + length;
            checkLimit(newLimit, maxLimit());
            buffer().putBytes(limit(), buffer, index, length);
            limit(newLimit);
            return this;
        }

        protected int fieldsCount()
        {
            return fieldCount;
        }
    }
}
