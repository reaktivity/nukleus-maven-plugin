/**
 * Copyright 2016-2017 The Reaktivity Project
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
package org.reaktivity.nukleus.maven.plugin.internal.generated.handcrafted;

import static java.lang.Integer.highestOneBit;
import static java.lang.Integer.numberOfTrailingZeros;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public final class VarintFW extends Flyweight {
  private int size;

  @Override
  public int limit() {
    return offset() + size;
  }

  public int value() {
      int value = 0;
      int i = 0;
      int b;
      int pos = offset();
      while (((b = buffer().getByte(pos++)) & 0x80) != 0) {
          value |= (b & 0x7F) << i;
          i += 7;
          if (i > 35) {
              throw new IllegalArgumentException("varint value too long");
          }
      }
      int unsigned = value | (b << i);
      int result = (((unsigned << 31) >> 31) ^ unsigned) >> 1;
      result = result ^ (unsigned & (1 << 31));;
      return result;
  }

  @Override
  public VarintFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    size = length0();
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public String toString() {
    return Integer.toString(value());
  }

  private int length0() {
      int pos = offset();
      byte b = (byte) 0;
      final int maxPos = Math.max(pos + 5,  maxLimit());
      while (pos <= maxPos && ((b = buffer().getByte(pos)) & 0x80L) != 0)
      {
          pos++;
      }
      if ((b & 0x80L) != 0)
      {
          throw new IllegalArgumentException(String.format("varint value at offset %d exceeds 32 bits", offset()));
      }
      int size = 1 + pos - offset();
      return size;
  }

  public static final class Builder extends Flyweight.Builder<VarintFW> {
    private boolean valueSet;

    public Builder() {
      super(new VarintFW());
    }

    @Override
    public Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      checkLimit(offset + 1, maxLimit);
      super.wrap(buffer, offset, maxLimit);
      this.valueSet = false;
      return this;
    }

    public Builder set(int value) {
        int zigzagged = (value << 1) ^ (value >> 31);
        int pos = offset();
        int bits = 1 + numberOfTrailingZeros(highestOneBit(zigzagged));
        int size = bits / 7;
        if (size * 7 < bits)
        {
            size++;
        }
        int newLimit = pos + size;
        checkLimit(newLimit, maxLimit());
        while ((zigzagged & 0xFFFFFF80) != 0L) {
            buffer().putByte(pos++, (byte) ((zigzagged & 0x7F) | 0x80));
            zigzagged >>>= 7;
        }
        buffer().putByte(pos, (byte) (zigzagged & 0x7F));
        limit(newLimit);
        valueSet = true;
        return this;
    }

    @Override
    public VarintFW build() {
      if (!valueSet) {
        throw new IllegalStateException("value not set");
      }
      return super.build();
    }
  }
}
