// TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.handcrafted;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public final class Varint64FW extends Flyweight {
  private int size;

  @Override
  public int limit() {
    return offset() + size;
  }

  public long value() {
    long value = 0L;
    int i = 0;;
    long b;
    int pos  = offset();
    while (((b = buffer().getByte(pos++)) & 0x80L) != 0) {
      value |= (b & 0x7F) << i;
      i += 7;
      if (i > 65) {
        throw new IllegalArgumentException("varint64 value too long");
      }
    }
    long unsigned = value  | (b << i);;
    long result = (((unsigned << 63) >> 63) ^ unsigned) >> 1;
    result = result ^ (unsigned & (1L << 63));
    return result;
  }

  @Override
  public Varint64FW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    size = length0();
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public String toString() {
    return Long.toString(value());
  }

  private int length0() {
    int pos = offset();
    byte b = (byte) 0;
    final int maxPos = Math.max(pos + 10,  maxLimit());
    while (pos <= maxPos && ((b = buffer().getByte(pos)) & 0x80L) != 0) {
      pos++;
    }
    int size = 1 + pos - offset();
    int mask = size < 10 ? 0x80 : 0x02;
    if ((b & mask) != 0) {
      throw new IllegalArgumentException(String.format("(varint64 value at offset %d exceeds 64 bits", offset()));
    }
    return size;
  }

  public static final class Builder extends Flyweight.Builder<Varint64FW> {
    private boolean valueSet;

    public Builder() {
      super(new Varint64FW());
    }

    @Override
    public Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      checkLimit(offset + 1, maxLimit);
      super.wrap(buffer, offset, maxLimit);
      this.valueSet = false;
      return this;
    }

    public Builder set(long value) {
      long zigzagged = (value << 1) ^ (value >> 63);
      int pos = offset();
      int bits = 1 + Long.numberOfTrailingZeros(Long.highestOneBit(zigzagged));
      int size = bits / 7;
      if (size * 7 < bits) {
        size++;
      }
      int newLimit = pos + size;
      checkLimit(newLimit, maxLimit());
      while ((zigzagged & 0xFFFFFFFF_FFFFFF80L) != 0L) {
        buffer().putByte(pos++, (byte) ((zigzagged & 0x7FL) | 0x80L));
        zigzagged >>>= 7;
      }
      buffer().putByte(pos, (byte) (zigzagged & 0x7FL));
      limit(newLimit);
      valueSet = true;
      return this;
    }

    @Override
    public Varint64FW build() {
      if (!valueSet) {
        throw new IllegalArgumentException("value not set");
      }
      return super.build();
    }
  }
}
