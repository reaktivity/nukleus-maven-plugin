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

public final class IntegerVariableArraysFW extends Flyweight {
  public static final int FIELD_OFFSET_FIXED1 = 0;

  private static final int FIELD_SIZE_FIXED1 = BitUtil.SIZE_OF_BYTE;

  public static final int FIELD_OFFSET_UNSIGNED64_LENGTH = FIELD_OFFSET_FIXED1 + FIELD_SIZE_FIXED1;

  private static final int FIELD_SIZE_UNSIGNED64_LENGTH = BitUtil.SIZE_OF_INT;

  public static final int FIELD_OFFSET_UNSIGNED64 = FIELD_OFFSET_UNSIGNED64_LENGTH + FIELD_SIZE_UNSIGNED64_LENGTH;

  private static final int FIELD_SIZE_UNSIGNED64 = BitUtil.SIZE_OF_LONG;

  public static final int FIELD_OFFSET_SIGNED16_LENGTH = FIELD_OFFSET_UNSIGNED64 + FIELD_SIZE_UNSIGNED64;

  private static final int FIELD_SIZE_SIGNED16_LENGTH = BitUtil.SIZE_OF_BYTE;

  public static final int FIELD_OFFSET_SIGNED16 = FIELD_OFFSET_SIGNED16_LENGTH + FIELD_SIZE_SIGNED16_LENGTH;

  private static final int FIELD_SIZE_SIGNED16 = BitUtil.SIZE_OF_SHORT;

  private int unsigned64Limit;
  private int signed16Limit;

  public int fixed1() {
    return buffer().getByte(offset() + FIELD_OFFSET_FIXED1) & 0xFF;
  }

  public long unsigned64Length() {
    return buffer().getInt(offset() + FIELD_OFFSET_UNSIGNED64_LENGTH, ByteOrder.BIG_ENDIAN) & 0xFFFF_FFFF;
  }

  public long unsigned64(int index) {
    return buffer().getLong(offset() + FIELD_OFFSET_UNSIGNED64 + index * FIELD_SIZE_UNSIGNED64);
  }

  public int signed16Length() {
    return buffer().getByte(offset() + unsigned64Limit + FIELD_OFFSET_SIGNED16_LENGTH) & 0xFF;
  }

  public short signed16(int index) {
    return buffer().getShort(offset() + FIELD_OFFSET_SIGNED16);
  }

  @Override
  public IntegerVariableArraysFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    unsigned64Limit = offset + FIELD_OFFSET_FIXED1 + FIELD_SIZE_FIXED1 + (int) unsigned64Length() * FIELD_SIZE_UNSIGNED64;
    signed16Limit = unsigned64Limit + FIELD_OFFSET_SIGNED16 + FIELD_SIZE_SIGNED16 + signed16Length() * FIELD_SIZE_SIGNED16;
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public int limit() {
    return signed16Limit;
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
    return String.format("INTEGER_FIXED_ARRAYS [fixed1=%d, unsigned64Length=%d, unsigned64=%s, signed16Length=%d, signed16=%s]",
            fixed1(), unsigned64Length(), toStringLongArray(this::unsigned64, (int) unsigned64Length()),
            signed16Length(), toStringShortArray(this::signed16, signed16Length()));
  }

  public static final class Builder extends Flyweight.Builder<IntegerVariableArraysFW> {
    private static final int INDEX_FIXED1 = 0;

    private static final int DEFAULT_FIXED1 = 0;

    private static final int INDEX_UNSIGNED64_LENGTH = 1;

    private static final long DEFAULT_UNSIGNED64_LENGTH = 0;

    private static final int INDEX_UNSIGNED64 = 2;

    private static final int INDEX_SIGNED16_LENGTH = 3;

    private static final int DEFAULT_SIGNED16_LENGTH = 0;

    private static final int INDEX_SIGNED16 = 4;

    private static final int FIELD_COUNT = 5;

    @SuppressWarnings("serial")
    private static final BitSet FIELDS_WITH_DEFAULTS = new BitSet(FIELD_COUNT)  {
        {
        set(INDEX_FIXED1);
        set(INDEX_UNSIGNED64_LENGTH);
        set(INDEX_SIGNED16_LENGTH);
      }
    }
    ;

    private static final String[] FIELD_NAMES = {
      "fixed1",
      "unsigned64Length",
      "unsigned64",
      "signed16Length",
      "signed16"
    };

    private long dynamicOffsetUnsigned64Length;

    private int dynamicOffsetSigned16Length;

    private final BitSet fieldsSet = new BitSet(FIELD_COUNT);

    public Builder() {
      super(new IntegerVariableArraysFW());
    }

    public Builder fixed1(int value) {
      checkFieldNotSet(INDEX_FIXED1);
      if (value < 0) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"fixed1\"", value));
      }
      if (value > 0XFF) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"fixed1\"", value));
      }
      checkFieldsSet(0, INDEX_FIXED1);
      int newLimit = limit() + FIELD_SIZE_FIXED1;
      checkLimit(newLimit, maxLimit());
      buffer().putByte(limit(), (byte)(value & 0xFF));
      fieldsSet.set(INDEX_FIXED1);
      limit(newLimit);
      return this;
    }

    private Builder unsigned64Length(long value) {
      if (value < 0) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"unsigned64Length\"", value));
      }
      if (value > 0xFFFFFFFFL) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"unsigned64Length\"", value));
      }
      if (!fieldsSet.get(INDEX_FIXED1)) {
        fixed1(DEFAULT_FIXED1);
      }
      checkFieldsSet(0, INDEX_UNSIGNED64_LENGTH);
      int newLimit = limit() + FIELD_SIZE_UNSIGNED64_LENGTH;
      checkLimit(newLimit, maxLimit());
      buffer().putInt(limit(), (int)(value & 0xFFFF_FFFF), ByteOrder.BIG_ENDIAN);
      dynamicOffsetUnsigned64Length = limit();
      fieldsSet.set(INDEX_UNSIGNED64_LENGTH);
      limit(newLimit);
      return this;
    }

    public Builder unsigned64(long value) {
      checkFieldNotSet(INDEX_UNSIGNED64);
      if (value < 0L) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"unsigned64\"", value));
      }
      if (value > Long.MAX_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"unsigned64\"", value));
      }
      if (!fieldsSet.get(INDEX_UNSIGNED64_LENGTH)) {
        unsigned64Length(DEFAULT_UNSIGNED64_LENGTH);
      }
      checkFieldsSet(0, INDEX_UNSIGNED64);
      int newLimit = limit() + FIELD_SIZE_UNSIGNED64;
      checkLimit(newLimit, maxLimit());
      buffer().putLong(limit(), value);
      fieldsSet.set(INDEX_UNSIGNED64);
      limit(newLimit);
      return this;
    }

    private Builder signed16Length(int value) {
      if (value < 0) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"signed16Length\"", value));
      }
      if (value > 0XFF) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"signed16Length\"", value));
      }
      checkFieldsSet(0, INDEX_SIGNED16_LENGTH);
      int newLimit = limit() + FIELD_SIZE_SIGNED16_LENGTH;
      checkLimit(newLimit, maxLimit());
      buffer().putByte(limit(), (byte)(value & 0xFF));
      dynamicOffsetSigned16Length = limit();
      fieldsSet.set(INDEX_SIGNED16_LENGTH);
      limit(newLimit);
      return this;
    }

    public Builder signed16(short value) {
      checkFieldNotSet(INDEX_SIGNED16);
      if (value < Short.MIN_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"signed16\"", value));
      }
      if (value > Short.MAX_VALUE) {
        throw new IllegalArgumentException(String.format("Value %d too high for field \"signed16\"", value));
      }
      if (!fieldsSet.get(INDEX_SIGNED16_LENGTH)) {
        signed16Length(DEFAULT_SIGNED16_LENGTH);
      }
      checkFieldsSet(0, INDEX_SIGNED16);
      int newLimit = limit() + FIELD_SIZE_SIGNED16;
      checkLimit(newLimit, maxLimit());
      buffer().putShort(limit(), value);
      fieldsSet.set(INDEX_SIGNED16);
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
    public IntegerVariableArraysFW build() {
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
  }
}
