// TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.experimental;

import java.nio.charset.Charset;
import javax.annotation.Generated;
import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

@Generated("reaktivity")
public final class StringFW extends Flyweight {
  private static final int FIELD_OFFSET_LENGTH = 0;

  private static final int FIELD_SIZE_LENGTH = BitUtil.SIZE_OF_BYTE;

  @Override
  public int limit() {
    return maxLimit() == offset() ? offset() : offset() + FIELD_SIZE_LENGTH + length0();
  }

  public String asString() {
    if (maxLimit() == offset()) {
      return null;
    }
    return buffer().getStringWithoutLengthUtf8(offset() + FIELD_SIZE_LENGTH, length0());
  }

  @Override
  public StringFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public String toString() {
    return maxLimit() == offset() ? "null" : String.format("\"%s\"", asString());
  }

  private int length0() {
    return buffer().getByte(offset() + FIELD_OFFSET_LENGTH) & 0xFF;
  }

  public static final class Builder extends Flyweight.Builder<StringFW> {
    private boolean valueSet = false;

    public Builder() {
      super(new StringFW());
    }

    @Override
    public Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      checkLimit(offset + FIELD_OFFSET_LENGTH + FIELD_SIZE_LENGTH, maxLimit);
      super.wrap(buffer, offset, maxLimit);
      return this;
    }

    public Builder set(StringFW value) {
      checkLimit(offset() + value.sizeof(), maxLimit());
      buffer().putBytes(offset(), value.buffer(), value.offset(), value.sizeof());
      valueSet = true;
      return this;
    }

    public Builder set(DirectBuffer srcBuffer, int srcOffset, int length) {
      checkLimit(offset() + length + FIELD_SIZE_LENGTH, maxLimit());
      buffer().putByte(offset(), (byte) length);
      buffer().putBytes(offset() + 1, srcBuffer, srcOffset, length);
      valueSet = true;
      return this;
    }

    public Builder set(String value, Charset charset) {
      byte[] charBytes = value.getBytes(charset);
      MutableDirectBuffer buffer = buffer();
      int offset = offset();
      checkLimit(offset + charBytes.length + FIELD_SIZE_LENGTH, maxLimit());
      buffer.putByte(offset, (byte) charBytes.length);
      buffer.putBytes(offset + 1, charBytes);
      valueSet = true;
      return this;
    }

    @Override
    public StringFW build()
    {
        if (!valueSet)
        {
            buffer().putByte(offset(), (byte) 0);
        }
        return super.build();
    }
  }
}
