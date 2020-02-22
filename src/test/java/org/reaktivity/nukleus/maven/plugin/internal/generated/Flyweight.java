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
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public abstract class Flyweight
{
    private static byte[] emptyBytes = new byte[0];

    private DirectBuffer buffer;

    private int offset;

    private int maxLimit;

    private UnsafeBuffer compareBuffer = new UnsafeBuffer(emptyBytes);

    public final int offset()
    {
        return offset;
    }

    public final DirectBuffer buffer()
    {
        return buffer;
    }

    public abstract int limit();

    public final int sizeof()
    {
        return limit() - offset();
    }

    protected final int maxLimit()
    {
        return maxLimit;
    }

    public Flyweight tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (offset > maxLimit)
        {
            return null;
        }
        this.buffer = buffer;
        this.offset = offset;
        this.maxLimit = maxLimit;
        return this;
    }

    public Flyweight wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        if (offset > maxLimit)
        {
            final String msg = String.format("offset=%d is beyond maxLimit=%d", offset, maxLimit);
            throw new IndexOutOfBoundsException(msg);
        }
        this.buffer = buffer;
        this.offset = offset;
        this.maxLimit = maxLimit;
        return this;
    }

    public Flyweight wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit,
        ArrayFW array)
    {
        wrap(buffer, offset, maxLimit);
        return this;
    }

    protected static final void checkLimit(
        int limit,
        int maxLimit)
    {
        if (limit > maxLimit)
        {
            final String msg = String.format("limit=%d is beyond maxLimit=%d", limit, maxLimit);
            throw new IndexOutOfBoundsException(msg);
        }
    }

    @Override
    public boolean equals(
        Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj == null || !(obj instanceof Flyweight))
        {
            return false;
        }
        else
        {
            Flyweight that = (Flyweight) obj;
            compareBuffer.wrap(buffer, offset, sizeof());
            that.compareBuffer.wrap(that.buffer, that.offset, that.sizeof());
            return compareBuffer.equals(that.compareBuffer);
        }
    }

    @Override
    public int hashCode()
    {
        int result = 1;
        for (int i = offset; i < limit(); i++)
        {
            result = 31 * result + buffer.getByte(i);
        }
        return result;
    }

    @FunctionalInterface
    public interface Visitor<T>
    {
        T visit(
            DirectBuffer buffer,
            int offset,
            int maxLimit);
    }

    public abstract static class Builder<T extends Flyweight>
    {
        private final T flyweight;

        private MutableDirectBuffer buffer;

        private int offset;

        private int limit;

        private int maxLimit;

        protected Builder(
            T flyweight)
        {
            this.flyweight = flyweight;
        }

        public final int limit()
        {
            return limit;
        }

        public final int maxLimit()
        {
            return maxLimit;
        }

        public T build()
        {
            flyweight.wrap(buffer, offset, limit);
            return flyweight;
        }

        public Builder<T> rewrap()
        {
            this.limit = this.offset;
            return this;
        }

        protected final T flyweight()
        {
            return flyweight;
        }

        protected final MutableDirectBuffer buffer()
        {
            return buffer;
        }

        protected final int offset()
        {
            return offset;
        }

        public int sizeof()
        {
            return limit - offset;
        }

        protected final void limit(
            int limit)
        {
            this.limit = limit;
        }

        public Builder<T> wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            this.buffer = buffer;
            this.offset = offset;
            this.limit = offset;
            this.maxLimit = maxLimit;
            return this;
        }

        public Builder<T> wrap(
            ArrayFW.Builder<? extends ArrayFW<T>, ? extends Flyweight.Builder<T>, T> array)
        {
            this.buffer = array.buffer();
            this.offset = array.limit();
            this.limit = array.limit();
            this.maxLimit = array.maxLimit();
            return this;
        }

        public <E> Builder<T> iterate(
            Iterable<E> iterable,
            Consumer<E> action)
        {
            iterable.forEach(action);
            return this;
        }

        public T rebuild(
            T item,
            int maxLength)
        {
            return item;
        }

        @FunctionalInterface
        public interface Visitor
        {
            int visit(
                MutableDirectBuffer buffer,
                int offset,
                int maxLimit);
        }
    }
}
