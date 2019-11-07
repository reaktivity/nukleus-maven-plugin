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
import org.agrona.concurrent.UnsafeBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public abstract class ListFW extends Flyweight
{
    final DirectBuffer fieldsRO = new UnsafeBuffer(0L, 0);

    public abstract int physicalLength();

    public abstract int logicalLength();

    public abstract int lengthSize();

    public abstract DirectBuffer fields();

    public abstract static class Builder<T extends ListFW> extends Flyweight.Builder
    {
        private int fieldsCount;

        private int fieldsLength;

        public Builder(Flyweight flyweight)
        {
            super(flyweight);
        }

        public abstract Builder<T> set(ListFW value);

        public Builder field(
            Flyweight.Builder.Visitor visitor)
        {
            int length = visitor.visit(buffer(), limit(), maxLimit());
            fieldsCount++;
            fieldsLength += length;
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
            fieldsCount = fieldCount;
            fieldsLength = length;
            int newLimit = limit() + length;
            checkLimit(newLimit, maxLimit());
            limit(newLimit);
            return this;
        }

        public void fieldsCount(
            int fieldsCount)
        {
            this.fieldsCount = fieldsCount;
        }

        public Builder fieldsLength(
            int fieldsLength)
        {
            this.fieldsLength = fieldsLength;
            return this;
        }

        protected int fieldsCount()
        {
            return fieldsCount;
        }

        protected int fieldsLength()
        {
            return fieldsLength;
        }
    }
}
