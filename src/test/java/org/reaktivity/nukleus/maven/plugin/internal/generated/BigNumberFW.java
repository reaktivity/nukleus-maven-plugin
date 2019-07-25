package org.reaktivity.nukleus.maven.plugin.internal.generated;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public class BigNumberFW extends Flyweight
{
    private static final int FIELD_OFFSET_VALUE = 0;

    private static final int FIELD_SIZE_VALUE = BitUtil.SIZE_OF_LONG;

    @Override
    public int limit()
    {
        return offset() + FIELD_SIZE_VALUE;
    }

    public BigNumber get()
    {
        return BigNumber.valueOf(buffer().getLong(offset()));
    }

    @Override
    public BigNumberFW tryWrap(
        DirectBuffer buffer, int offset, int maxLimit)
    {
        if (null == super.tryWrap(buffer, offset, maxLimit) || limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public BigNumberFW wrap(
        DirectBuffer buffer, int offset, int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        return maxLimit() == offset() ? "null" : get().toString();
    }

    public static final class Builder extends Flyweight.Builder<BigNumberFW>
    {
        private boolean valueSet;

        public Builder()
        {
            super(new BigNumberFW());
        }

        public Builder wrap(
            MutableDirectBuffer buffer, int offset, int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }

        public Builder set(
            BigNumberFW value)
        {
            int newLimit = offset() + value.sizeof();
            checkLimit(newLimit, maxLimit());
            buffer().putBytes(offset(), value.buffer(), value.offset(), value.sizeof());
            limit(newLimit);
            valueSet = true;
            return this;
        }

        public Builder set(
            BigNumber value)
        {
            MutableDirectBuffer buffer = buffer();
            int offset = offset();
            int newLimit = offset + FIELD_SIZE_VALUE;
            checkLimit(newLimit, maxLimit());
            buffer.putLong(offset, value.value());
            limit(newLimit);
            valueSet = true;
            return this;
        }

        @Override
        public BigNumberFW build()
        {
            if (!valueSet)
            {
                throw new IllegalStateException("Number not set");
            }
            return super.build();
        }
    }
}
