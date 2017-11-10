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

import java.nio.ByteOrder;
import java.util.BitSet;
import java.util.function.IntFunction;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public final class IntegerFixedArraysFW extends Flyweight {
  public static final int FIELD_OFFSET_UNSIGNED8 = 0;

  private static final int FIELD_SIZE_UNSIGNED8 = BitUtil.SIZE_OF_BYTE;

  private static final int ARRAY_SIZE_UNSIGNED8 = 1;

  public static final int FIELD_OFFSET_UNSIGNED16 = FIELD_OFFSET_UNSIGNED8 + FIELD_SIZE_UNSIGNED8 * ARRAY_SIZE_UNSIGNED8;

  private static final int FIELD_SIZE_UNSIGNED16 = BitUtil.SIZE_OF_SHORT;

  private static final int ARRAY_SIZE_UNSIGNED16 = 2;

  public static final int FIELD_OFFSET_UNSIGNED32 = FIELD_OFFSET_UNSIGNED16 + FIELD_SIZE_UNSIGNED16 * ARRAY_SIZE_UNSIGNED16;

  private static final int FIELD_SIZE_UNSIGNED32 = BitUtil.SIZE_OF_INT;

  private static final int ARRAY_SIZE_UNSIGNED32 = 3;

  public static final int FIELD_OFFSET_UNSIGNED64 = FIELD_OFFSET_UNSIGNED32 + FIELD_SIZE_UNSIGNED32 * ARRAY_SIZE_UNSIGNED32;

  private static final int FIELD_SIZE_UNSIGNED64 = BitUtil.SIZE_OF_LONG;

  private static final int ARRAY_SIZE_UNSIGNED64 = 4;

  public static final int FIELD_OFFSET_SIGNED8 = FIELD_OFFSET_UNSIGNED64 + FIELD_SIZE_UNSIGNED64 * ARRAY_SIZE_UNSIGNED64;

  private static final int FIELD_SIZE_SIGNED8 = BitUtil.SIZE_OF_BYTE;

  private static final int ARRAY_SIZE_SIGNED8 = 1;

  public static final int FIELD_OFFSET_SIGNED16 = FIELD_OFFSET_SIGNED8 + FIELD_SIZE_SIGNED8 * ARRAY_SIZE_SIGNED8;

  private static final int FIELD_SIZE_SIGNED16 = BitUtil.SIZE_OF_SHORT;

  private static final int ARRAY_SIZE_SIGNED16 = 2;

  public static final int FIELD_OFFSET_SIGNED32 = FIELD_OFFSET_SIGNED16 + FIELD_SIZE_SIGNED16 * ARRAY_SIZE_SIGNED16;

  private static final int FIELD_SIZE_SIGNED32 = BitUtil.SIZE_OF_INT;

  private static final int ARRAY_SIZE_SIGNED32 = 3;

  public static final int FIELD_OFFSET_SIGNED64 = FIELD_OFFSET_SIGNED32 + FIELD_SIZE_SIGNED32 * ARRAY_SIZE_SIGNED32;

  private static final int FIELD_SIZE_SIGNED64 = BitUtil.SIZE_OF_LONG;

  private static final int ARRAY_SIZE_SIGNED64 = 4;

  public int unsigned8(int index) {
    checkIndex(index, 0, 4);
    return buffer().getByte(offset() + FIELD_OFFSET_UNSIGNED8 + index * FIELD_SIZE_UNSIGNED8) & 0xFF;
  }

  public int unsigned16(int index) {
    return buffer().getShort(offset() + FIELD_OFFSET_UNSIGNED16 + index * FIELD_SIZE_UNSIGNED16, ByteOrder.BIG_ENDIAN) & 0xFFFF;
  }

  public long unsigned32(int index) {
    return buffer().getInt(offset() + FIELD_OFFSET_UNSIGNED32 + index * FIELD_SIZE_UNSIGNED32, ByteOrder.BIG_ENDIAN) & 0xFFFF_FFFF;
  }

  public long unsigned64(int index) {
    return buffer().getLong(offset() + FIELD_OFFSET_UNSIGNED64 + index * FIELD_SIZE_UNSIGNED64);
  }

  public byte signed8(int index) {
    return buffer().getByte(offset() + FIELD_OFFSET_SIGNED8 + index * FIELD_SIZE_SIGNED8);
  }

  public short signed16(int index) {
    return buffer().getShort(offset() + FIELD_OFFSET_SIGNED16 + index * FIELD_SIZE_SIGNED16);
  }

  public int signed32(int index) {
    return buffer().getInt(offset() + FIELD_OFFSET_SIGNED32 + index * FIELD_SIZE_SIGNED32);
  }

  public long signed64(int index) {
    return buffer().getLong(offset() + FIELD_OFFSET_SIGNED64 + index * FIELD_SIZE_SIGNED64);
  }

  @Override
  public IntegerFixedArraysFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public int limit() {
    return offset() + FIELD_OFFSET_SIGNED64 + FIELD_SIZE_SIGNED64* ARRAY_SIZE_SIGNED64;
  }

  private String toStringByteArray(IntFunction<Byte> value, int count)
  {
      StringBuffer result = new StringBuffer().append("[");
      for (int i=0; i < count-1; i++) {
          result.append(value.apply(i));
          result.append(", ");
      }
      result.append(value.apply(count-1));
      result.append("]");
      return result.toString();
  }

  private String toStringIntArray(IntFunction<Integer> value, int count)
  {
      StringBuffer result = new StringBuffer().append("[");
      for (int i=0; i < count-1; i++) {
          result.append(value.apply(i));
          result.append(", ");
      }
      result.append(value.apply(count-1));
      result.append("]");
      return result.toString();
  }

  private String toStringLongArray(IntFunction<Long> value, int count)
  {
      StringBuffer result = new StringBuffer().append("[");
      for (int i=0; i < count-1; i++) {
          result.append(value.apply(i));
          result.append(", ");
      }
      result.append(value.apply(count-1));
      result.append("]");
      return result.toString();
  }

  private String toStringShortArray(IntFunction<Short> value, int count)
  {
      StringBuffer result = new StringBuffer().append("[");
      for (int i=0; i < count-1; i++) {
          result.append(value.apply(i));
          result.append(", ");
      }
      result.append(value.apply(count-1));
      result.append("]");
      return result.toString();
  }

  @Override
  public String toString() {
    return String.format("INTEGER_FIXED_ARRAYS [unsigned8=%s, unsigned16=%s, unsigned32=%s, unsigned64=%s, signed8=%s, signed16=%s, signed32=%s, signed64=%s]",
            toStringIntArray(this::unsigned8, 1), toStringIntArray(this::unsigned16, 2), toStringLongArray(this::unsigned32, 3), toStringLongArray(this::unsigned64, 4),
            toStringByteArray(this::signed8, 1), toStringShortArray(this::signed16, 2), toStringIntArray(this::signed32, 3), toStringLongArray(this::signed64, 4));
  }

  private void checkIndex(
    int value,
    int lowest,
    int tooHigh)
    {
        if (value < lowest || value >= tooHigh)
        {
            throw new IndexOutOfBoundsException(String.format("Array index out of range: %d" + value));
        }
    }

public static final class Builder extends Flyweight.Builder<IntegerFixedArraysFW> {
    private static final int INDEX_UNSIGNED8 = 0;

    private static final int INDEX_UNSIGNED16 = 1;

    private static final int INDEX_UNSIGNED32 = 2;

    private static final int INDEX_UNSIGNED64 = 3;

    private static final int INDEX_SIGNED8 = 4;

    private static final int INDEX_SIGNED16 = 5;

    private static final int INDEX_SIGNED32 = 6;

    private static final int INDEX_SIGNED64 = 7;

    private static final int FIELD_COUNT = 8;

    @SuppressWarnings("serial")
    private static final BitSet FIELDS_WITH_DEFAULTS = new BitSet(FIELD_COUNT)  {
        {
      }
    }
    ;

    private static final String[] FIELD_NAMES = {
      "unsigned8",
      "unsigned16",
      "unsigned32",
      "unsigned64",
      "signed8",
      "signed16",
      "signed32",
      "signed64"
    };

    private final BitSet fieldsSet = new BitSet(FIELD_COUNT);

    public Builder() {
      super(new IntegerFixedArraysFW());
    }

    public Builder unsigned8(int index, int value) {
      if (value < 0) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"unsigned8\"", value));
      }
      if (value > 0XFF) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"unsigned8\"", value));
      }
      checkFieldsSet(0, INDEX_UNSIGNED8);
      int newLimit = limit() + FIELD_SIZE_UNSIGNED8 * ARRAY_SIZE_UNSIGNED8;
      checkLimit(newLimit, maxLimit());
      initializeIfNotSet(INDEX_UNSIGNED8, limit(), newLimit);
      buffer().putByte(limit() + index * FIELD_SIZE_UNSIGNED8, (byte)(value & 0xFF));
      limit(newLimit);
      return this;
    }

    public Builder unsigned16(int index, int value) {
      if (value < 0) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"unsigned16\"", value));
      }
      if (value > 0xFFFF) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"unsigned16\"", value));
      }
      checkFieldsSet(0, INDEX_UNSIGNED16);
      int newLimit = limit() + FIELD_SIZE_UNSIGNED16 * ARRAY_SIZE_UNSIGNED16;
      checkLimit(newLimit, maxLimit());
      initializeIfNotSet(INDEX_UNSIGNED16, limit(), newLimit);
      buffer().putShort(limit() + index * FIELD_SIZE_UNSIGNED16, (short)(value & 0xFFFF), ByteOrder.BIG_ENDIAN);
      limit(newLimit);
      return this;
    }

    public Builder unsigned32(int index, long value) {
      if (value < 0) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"unsigned32\"", value));
      }
      if (value > 0xFFFFFFFFL) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"unsigned32\"", value));
      }
      checkFieldsSet(0, INDEX_UNSIGNED32);
      int newLimit = limit() + FIELD_SIZE_UNSIGNED32 * ARRAY_SIZE_UNSIGNED32;
      checkLimit(newLimit, maxLimit());
      initializeIfNotSet(INDEX_UNSIGNED32, limit(), newLimit);
      buffer().putInt(limit()+ index * FIELD_SIZE_UNSIGNED32, (int)(value & 0xFFFF_FFFF), ByteOrder.BIG_ENDIAN);
      limit(newLimit);
      return this;
    }

    public Builder unsigned64(int index, long value) {
      if (value < 0L) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"unsigned64\"", value));
      }
      if (value > Long.MAX_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"unsigned64\"", value));
      }
      checkFieldsSet(0, INDEX_UNSIGNED64);
      int newLimit = limit() + FIELD_SIZE_UNSIGNED64 * ARRAY_SIZE_UNSIGNED64;
      checkLimit(newLimit, maxLimit());
      initializeIfNotSet(INDEX_UNSIGNED64, limit(), newLimit);
      buffer().putLong(limit() + index * FIELD_SIZE_UNSIGNED64, value);
      limit(newLimit);
      return this;
    }

    public Builder signed8(int index, byte value) {
      if (value < Byte.MIN_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"signed8\"", value));
      }
      if (value > Byte.MAX_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"signed8\"", value));
      }
      checkFieldsSet(0, INDEX_SIGNED8);
      int newLimit = limit() + FIELD_SIZE_SIGNED8 * ARRAY_SIZE_SIGNED8;
      checkLimit(newLimit, maxLimit());
      initializeIfNotSet(INDEX_SIGNED8, limit(), newLimit);
      buffer().putByte(limit() + index * FIELD_SIZE_SIGNED8, value);
      limit(newLimit);
      return this;
    }

    public Builder signed16(int index, short value) {
      if (value < Short.MIN_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"signed16\"", value));
      }
      if (value > Short.MAX_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"signed16\"", value));
      }
      checkFieldsSet(0, INDEX_SIGNED16);
      int newLimit = limit() + FIELD_SIZE_SIGNED16 * ARRAY_SIZE_SIGNED16;
      checkLimit(newLimit, maxLimit());
      initializeIfNotSet(INDEX_SIGNED16, limit(), newLimit);
      buffer().putShort(limit() + index * FIELD_SIZE_SIGNED16, value);
      fieldsSet.set(INDEX_SIGNED16);
      limit(newLimit);
      return this;
    }

    public Builder signed32(int index, int value) {
      if (value < Integer.MIN_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"signed32\"", value));
      }
      if (value > Integer.MAX_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"signed32\"", value));
      }
      checkFieldsSet(0, INDEX_SIGNED32);
      int newLimit = limit() + FIELD_SIZE_SIGNED32 * ARRAY_SIZE_SIGNED32;
      checkLimit(newLimit, maxLimit());
      initializeIfNotSet(INDEX_SIGNED32, limit(), newLimit);
      buffer().putInt(limit() + index * FIELD_SIZE_SIGNED32, value);
      fieldsSet.set(INDEX_SIGNED32);
      limit(newLimit);
      return this;
    }

    public Builder signed64(int index, long value) {
      if (value < Long.MIN_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"signed64\"", value));
      }
      if (value > Long.MAX_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"signed64\"", value));
      }
      checkFieldsSet(0, INDEX_SIGNED64);
      int newLimit = limit() + FIELD_SIZE_SIGNED64 * ARRAY_SIZE_SIGNED64;
      checkLimit(newLimit, maxLimit());
      initializeIfNotSet(INDEX_SIGNED64, limit(), newLimit);
      buffer().putLong(limit() + index * FIELD_SIZE_SIGNED64, value);
      fieldsSet.set(INDEX_SIGNED64);
      limit(newLimit);
      return this;
    }

    @Override
    public Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      fieldsSet.clear();
      super.wrap(buffer, offset, maxLimit);
      limit(offset);
      return this;
    }

    @Override
    public IntegerFixedArraysFW build() {
      checkFieldsSet(0, FIELD_COUNT);
      fieldsSet.clear();
      return super.build();
    }

    private void checkFieldNotSet(int index) {
      if (fieldsSet.get(index)) {
        throw new IllegalStateException(String.format("Field \"%s\" has already been set", FIELD_NAMES[index]));
      }
    }

    private void checkFieldsSet(int fromIndex, int toIndex) {
      int fieldNotSet = fromIndex - 1;
      do {
        fieldNotSet = fieldsSet.nextClearBit(fieldNotSet + 1);
      } while (fieldNotSet < toIndex && FIELDS_WITH_DEFAULTS.get(fieldNotSet));
      if (fieldNotSet < toIndex) {
        throw new IllegalStateException(String.format("Required field \"%s\" is not set", FIELD_NAMES[fieldNotSet]));
      }
    }

    private void initializeIfNotSet(
        int index,
        int limit,
        int newLimit)
    {
        if (!fieldsSet.get(index)) {
            buffer().setMemory(limit, newLimit - limit, (byte) 0);
            fieldsSet.set(index);
        }
    }
  }
}
