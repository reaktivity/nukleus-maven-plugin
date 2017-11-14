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
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;

public final class IntegerVariableArraysFW extends Flyweight {
    public static final int FIELD_OFFSET_FIXED1 = 0;

    private static final int FIELD_SIZE_FIXED1 = BitUtil.SIZE_OF_BYTE;

    public static final int FIELD_OFFSET_LENGTH_UNSIGNED64 = FIELD_OFFSET_FIXED1 + FIELD_SIZE_FIXED1;

    private static final int FIELD_SIZE_LENGTH_UNSIGNED64 = BitUtil.SIZE_OF_INT;

    public static final int FIELD_OFFSET_UNSIGNED64 = FIELD_OFFSET_LENGTH_UNSIGNED64 + FIELD_SIZE_LENGTH_UNSIGNED64;

    private static final int FIELD_SIZE_UNSIGNED64 = BitUtil.SIZE_OF_LONG;

    public static final int FIELD_OFFSET_LENGTH_SIGNED16 = 0;

    private static final int FIELD_SIZE_LENGTH_SIGNED16 = BitUtil.SIZE_OF_BYTE;

    public static final int FIELD_OFFSET_SIGNED16 = FIELD_OFFSET_LENGTH_SIGNED16 + FIELD_SIZE_LENGTH_SIGNED16;

    private static final int FIELD_SIZE_SIGNED16 = BitUtil.SIZE_OF_SHORT;

  private int limitUnsigned64Array;
  private int limitSigned16;

  private LongPrimitiveIterator iteratorUnsigned64Array;

  private IntPrimitiveIterator iteratorSigned16;

  private class LongPrimitiveIterator implements PrimitiveIterator.OfLong {
      private String fieldName;
      private final int offset;
      private final int fieldSize;
      private final int count;
      private int index;

      LongPrimitiveIterator(String fieldName, int offset, int fieldSize, int count) {
          this.fieldName = fieldName;
          this.offset = offset;
          this.fieldSize = fieldSize;
          this.count = count;
      }

      @Override
      public boolean hasNext()
      {
          return index < count;
      }

      @Override
      public long nextLong()
      {
          if (!hasNext()) {
              throw new NoSuchElementException(fieldName + ": " + index);
          }
          return buffer().getLong(offset + fieldSize * index++);
      }

    };

    private class IntPrimitiveIterator implements PrimitiveIterator.OfInt {
        private int index;
        private final int offset;
        private final int fieldSize;
        private final int count;

        IntPrimitiveIterator(int offset, int fieldSize, int count) {
            this.offset = offset;
            this.fieldSize = fieldSize;
            this.count = count;
        }

        @Override
        public boolean hasNext()
        {
            return index < count;
        }

        @Override
        public int nextInt()
        {
            if (!hasNext()) {
                throw new NoSuchElementException("unsigned64: " + index);
            }
            return buffer().getInt(offset + fieldSize * index++);
        }

    };

    public int fixed1() {
        return buffer().getByte(offset() + FIELD_OFFSET_FIXED1) & 0xFF;
    }

    public long lengthUnsigned64Array() {
      return buffer().getInt(offset() + FIELD_OFFSET_LENGTH_UNSIGNED64, ByteOrder.BIG_ENDIAN) & 0xFFFF_FFFF;
    }

    public PrimitiveIterator.OfLong unsigned64() {
      iteratorUnsigned64Array.index = 0;
      return iteratorUnsigned64Array;
    }

    public int lengthSigned16() {
      return buffer().getByte(limitUnsigned64Array + FIELD_OFFSET_LENGTH_SIGNED16) & 0xFF;
    }

    public PrimitiveIterator.OfInt signed16() {
        iteratorSigned16.index = 0;
        return iteratorSigned16;
    }

  @Override
  public IntegerVariableArraysFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    final int offsetUnsigned64Array = offset + FIELD_OFFSET_UNSIGNED64;
    iteratorUnsigned64Array = lengthUnsigned64Array() == -1 ? null : new LongPrimitiveIterator("unsigned64Array", offsetUnsigned64Array, FIELD_SIZE_UNSIGNED64, (int) lengthUnsigned64Array());
    limitUnsigned64Array = offsetUnsigned64Array + FIELD_SIZE_UNSIGNED64 * (int) lengthUnsigned64Array();

    final int offsetSigned16 = limitUnsigned64Array + FIELD_OFFSET_SIGNED16;
    iteratorSigned16 = new IntPrimitiveIterator(offsetSigned16, FIELD_SIZE_LENGTH_SIGNED16, lengthSigned16());
    limitSigned16 = offsetSigned16 + FIELD_SIZE_SIGNED16 * lengthSigned16();
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public int limit() {
    return limitSigned16;
  }

  private String toString(PrimitiveIterator.OfLong value)
  {
      StringBuffer result = new StringBuffer().append("[");
      boolean first = true;
      while(value.hasNext()) {
          if (!first) {
              result.append(", ");
          }
          result.append(value.nextLong());
      }
      result.append("]");
      return result.toString();
  }

  private String toString(PrimitiveIterator.OfInt value)
  {
      StringBuffer result = new StringBuffer().append("[");
      boolean first = true;
      while(value.hasNext()) {
          if (!first) {
              result.append(", ");
          }
          result.append(value.nextInt());
      }
      result.append("]");
      return result.toString();
  }

  @Override
  public String toString() {
    return String.format("INTEGER_FIXED_ARRAYS [fixed1=%d, unsigned64Length=%d, unsigned64=%s, lengthSigned16=%d, signed16=%s]",
            fixed1(), lengthUnsigned64Array(), toString(unsigned64()), lengthSigned16(), toString(signed16()));
  }

public static final class Builder extends Flyweight.Builder<IntegerVariableArraysFW> {
    private static final int INDEX_FIXED1 = 0;

    private static final int DEFAULT_FIXED1 = 0;

    private static final int INDEX_LENGTH_UNSIGNED64 = 1;

    private static final int INDEX_UNSIGNED64_ARRAY = 2;

    private static final PrimitiveIterator.OfLong DEFAULT_UNSIGNED64_ARRAY = null;

    private static final int INDEX_LENGTH_SIGNED16 = 3;

    private static final int INDEX_SIGNED16_ARRAY = 4;

    private static final int FIELD_COUNT = 5;

    @SuppressWarnings("serial")
    private static final BitSet FIELDS_WITH_DEFAULTS = new BitSet(FIELD_COUNT)  {
        {
        set(INDEX_FIXED1);
        set(INDEX_UNSIGNED64_ARRAY);
      }
    }
    ;

    private static final String[] FIELD_NAMES = {
      "fixed1",
      "lengthUnsigned64Array",
      "unsigned64Array",
      "lengthSigned16",
      "signed16Array"
    };

      private int dynamicOffsetLengthUnsigned64Array;

      private int dynamicOffsetUnsigned64Array;

      private int dynamicOffsetLengthSigned16;

      private int dynamicOffsetSigned16Array;

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

      private Builder lengthUnsigned64Array(long value) {
        if (value < 0) {
          throw new IllegalArgumentException(String.format("Value %d too low for field \"lengthUnsigned64Array\"", value));
        }
        if (value > 0xFFFFFFFFL) {
          throw new IllegalArgumentException(String.format("Value %d too high for field \"lengthUnsigned64Array\"", value));
        }
        if (!fieldsSet.get(INDEX_FIXED1)) {
          fixed1(DEFAULT_FIXED1);
        }
        checkFieldsSet(0, INDEX_LENGTH_UNSIGNED64);
        int newLimit = limit() + FIELD_SIZE_LENGTH_UNSIGNED64;
        checkLimit(newLimit, maxLimit());
        buffer().putInt(limit(), (int)(value & 0xFFFF_FFFF), ByteOrder.BIG_ENDIAN);
        dynamicOffsetLengthUnsigned64Array = limit();
        fieldsSet.set(INDEX_LENGTH_UNSIGNED64);
        limit(newLimit);
        return this;
      }

      public Builder unsigned64Array(PrimitiveIterator.OfLong values)
      {
          checkFieldNotSet(INDEX_UNSIGNED64_ARRAY);
          if (values == null || !values.hasNext())
          {
              // TODO: set limit if length field is not immediately prior
              lengthUnsigned64Array(values == null ? -1 : 0);
              checkFieldsSet(0, INDEX_UNSIGNED64_ARRAY);
              fieldsSet.set(INDEX_UNSIGNED64_ARRAY);
          }
          else
          {
              while (values.hasNext())
              {
                  appendUnsigned64Array(values.nextLong());
              }
          }
          return this;
      }

      public Builder appendUnsigned64Array(long value) {
        if (value < 0L) {
          throw new IllegalArgumentException(String.format("Value %d too low for field \"unsigned64\"", value));
        }
        if (value > Long.MAX_VALUE) {
          throw new IllegalArgumentException(String.format("Value %d too high for field \"unsigned64\"", value));
        }
        if (!fieldsSet.get(INDEX_UNSIGNED64_ARRAY)) {
          fieldsSet.set(INDEX_LENGTH_UNSIGNED64);
          checkFieldsSet(0, INDEX_UNSIGNED64_ARRAY);
          dynamicOffsetUnsigned64Array = limit();
          fieldsSet.set(INDEX_UNSIGNED64_ARRAY);
        }
        int newLimit = limit() + FIELD_SIZE_UNSIGNED64;
        checkLimit(newLimit, maxLimit());
        buffer().putLong(limit(), value);
        fieldsSet.set(INDEX_UNSIGNED64_ARRAY);
        limit(dynamicOffsetLengthUnsigned64Array);
        lengthUnsigned64Array((newLimit - dynamicOffsetUnsigned64Array) / FIELD_SIZE_UNSIGNED64);
        limit(newLimit);
        return this;
      }

      private Builder lengthSigned16(int value) {
        if (value < -1) {
          throw new IllegalArgumentException(String.format("Value %d too low for field \"lengthSigned16\"", value));
        }
        if (!fieldsSet.get(INDEX_UNSIGNED64_ARRAY)) {
            unsigned64Array(DEFAULT_UNSIGNED64_ARRAY);
        }
        checkFieldsSet(0, INDEX_LENGTH_SIGNED16);
        int newLimit = limit() + FIELD_SIZE_LENGTH_SIGNED16;
        checkLimit(newLimit, maxLimit());
        buffer().putByte(limit(), (byte)(value & 0xFF));
        dynamicOffsetLengthSigned16 = limit();
        fieldsSet.set(INDEX_LENGTH_SIGNED16);
        limit(newLimit);
        return this;
      }

      public Builder signed16Array(PrimitiveIterator.OfInt values)
      {
          while (values.hasNext())
          {
              int value = values.next();
              if (value < Short.MIN_VALUE)
              {
                  throw new IllegalArgumentException(String.format("Value %d too low for field \"signed16Array\"", value));
              }
              else if (value > Short.MAX_VALUE)
              {
                  throw new IllegalArgumentException(String.format("Value %d too high for field \"signed16Array\"", value));
              }
              appendSigned16Array((short) values.nextInt());
          }
          return this;
      }

      public Builder appendSigned16Array(short value) {
        if (!fieldsSet.get(INDEX_LENGTH_SIGNED16)) {
          fieldsSet.set(INDEX_LENGTH_SIGNED16);
          checkFieldsSet(0, INDEX_SIGNED16_ARRAY);
          fieldsSet.set(INDEX_SIGNED16_ARRAY);
          dynamicOffsetSigned16Array = limit();
        }
        int newLimit = limit() + FIELD_SIZE_SIGNED16;
        checkLimit(newLimit, maxLimit());
        buffer().putShort(limit(), value);
        int arrayLength = (newLimit - dynamicOffsetSigned16Array) / FIELD_SIZE_SIGNED16;
        if (arrayLength > Byte.MAX_VALUE)
        {
            throw new ArrayIndexOutOfBoundsException("Maximum array length exceeded for field \"signed16Array\"");
        }
        limit(dynamicOffsetLengthSigned16);
        lengthSigned16((byte) arrayLength);
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
