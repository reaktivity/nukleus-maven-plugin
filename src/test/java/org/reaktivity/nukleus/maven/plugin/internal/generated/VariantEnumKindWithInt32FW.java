package org.reaktivity.nukleus.maven.plugin.internal.generated;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8FW;

public class VariantEnumKindWithInt32FW extends Flyweight
{
    private final EnumWithInt8FW enumWithInt8RO = new EnumWithInt8FW();

    private static final int FIELD_SIZE_INT8 = BitUtil.SIZE_OF_BYTE;

    private static final int FIELD_SIZE_INT16 = BitUtil.SIZE_OF_SHORT;

    private static final int FIELD_SIZE_INT32 = BitUtil.SIZE_OF_INT;

    private static final int INT8_MAX = 127;

    private static final long INT16_MAX = 32767;

    private static final int INT32_MAX = 2147483647;

    public static final EnumWithInt8 KIND_INT8 = EnumWithInt8.ONE;

    public static final EnumWithInt8 KIND_INT16 = EnumWithInt8.TWO;

    public static final EnumWithInt8 KIND_INT32 = EnumWithInt8.THREE;

    public EnumWithInt8 kind()
    {
        return enumWithInt8RO.get();
    }

    public byte getAsInt8()
    {
        return buffer().getByte(enumWithInt8RO.limit());
    }

    public short getAsInt16()
    {
        return buffer().getShort(enumWithInt8RO.limit());
    }

    public int getAsInt32()
    {
        return buffer().getInt(enumWithInt8RO.limit());
    }

    public long get()
    {
        switch (kind())
        {
        case ONE:
            return getAsInt8();
        case TWO:
            return getAsInt16();
        case THREE:
            return getAsInt32();
        default:
            throw new IllegalStateException("Unrecognized kind: " + kind());
        }
    }

    @Override
    public VariantEnumKindWithInt32FW tryWrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            break;
        case TWO:
            break;
        case THREE:
            break;
        default:
            break;
        }
        if (limit() > maxLimit)
        {
            return null;
        }
        return this;
    }

    @Override
    public VariantEnumKindWithInt32FW wrap(
        DirectBuffer buffer,
        int offset,
        int maxLimit)
    {
        super.wrap(buffer, offset, maxLimit);
        switch (kind())
        {
        case ONE:
            break;
        case TWO:
            break;
        case THREE:
            break;
        default:
            break;
        }
        checkLimit(limit(), maxLimit);
        return this;
    }

    @Override
    public String toString()
    {
        switch (kind())
        {
        case ONE:
            return String.format("VARIANTENUMKINDWITHINT32 [int8=%d]", getAsInt8());
        case TWO:
            return String.format("VARIANTENUMKINDWITHINT32 [int16=%d]", getAsInt16());
        case THREE:
            return String.format("VARIANTENUMKINDWITHINT32 [int32=%d]", getAsInt32());
        default:
            return String.format("VARIANTUINT8KINDWITHINT64TYPE [unknown]");
        }
    }

    @Override
    public int limit()
    {
        switch (kind())
        {
        case ONE:
            return enumWithInt8RO.limit() + FIELD_SIZE_INT8;
        case TWO:
            return enumWithInt8RO.limit() + FIELD_SIZE_INT16;
        case THREE:
            return enumWithInt8RO.limit() + FIELD_SIZE_INT32;
        default:
            return enumWithInt8RO.limit();
        }
    }

    public static final class Builder extends Flyweight.Builder<VariantEnumKindWithInt32FW>
    {
        private final EnumWithInt8FW.Builder enumWithInt8RW = new EnumWithInt8FW.Builder();

        public Builder()
        {
            super(new VariantEnumKindWithInt32FW());
        }

        private Builder kind(
            EnumWithInt8 value)
        {
            enumWithInt8RW.wrap(buffer(), offset(), maxLimit());
            enumWithInt8RW.set(value);
            limit(enumWithInt8RW.limit());
            return this;
        }

        public Builder setAsInt8(
            byte value)
        {
            kind(KIND_INT8);
            int newLimit = limit() + FIELD_SIZE_INT8;
            checkLimit(newLimit, maxLimit());
            buffer().putByte(limit(), value);
            limit(newLimit);
            return this;
        }

        public Builder setAsInt16(
            short value)
        {
            kind(KIND_INT16);
            int newLimit = limit() + FIELD_SIZE_INT16;
            checkLimit(newLimit, maxLimit());
            buffer().putShort(limit(), value);
            limit(newLimit);
            return this;
        }

        public Builder setAsInt32(
            int value)
        {
            kind(KIND_INT32);
            int newLimit = limit() + FIELD_SIZE_INT32;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), value);
            limit(newLimit);
            return this;
        }

        public Builder set(
            int value)
        {
            if (value <= INT8_MAX)
            {
                setAsInt8((byte) value);
                return this;
            }
            if (value <= INT16_MAX)
            {
                setAsInt16((short) value);
                return this;
            }
            if (value <= INT32_MAX)
            {
                setAsInt32((int) value);
                return this;
            }
            return this;
        }

        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }
    }
}
