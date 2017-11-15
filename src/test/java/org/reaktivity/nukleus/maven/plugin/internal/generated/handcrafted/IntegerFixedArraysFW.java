// TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.handcrafted;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.reaktivity.reaktor.internal.test.types.Flyweight;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.IntegerVariableArraysFW.Builder;

public final class IntegerFixedArraysFW extends Flyweight {
  public static final int FIELD_OFFSET_UINT8_ARRAY = 0;

  private static final int FIELD_SIZE_UINT8_ARRAY = BitUtil.SIZE_OF_BYTE;

  private static final int ARRAY_SIZE_UINT8_ARRAY = 1;

  public static final int FIELD_OFFSET_UINT16_ARRAY = FIELD_OFFSET_UINT8_ARRAY + (FIELD_SIZE_UINT8_ARRAY * ARRAY_SIZE_UINT8_ARRAY);

  private static final int FIELD_SIZE_UINT16_ARRAY = BitUtil.SIZE_OF_SHORT;

  private static final int ARRAY_SIZE_UINT16_ARRAY = 2;

  public static final int FIELD_OFFSET_UINT32_ARRAY = FIELD_OFFSET_UINT16_ARRAY + (FIELD_SIZE_UINT16_ARRAY * ARRAY_SIZE_UINT16_ARRAY);

  private static final int FIELD_SIZE_UINT32_ARRAY = BitUtil.SIZE_OF_INT;

  private static final int ARRAY_SIZE_UINT32_ARRAY = 3;

  public static final int FIELD_OFFSET_UINT64_ARRAY = FIELD_OFFSET_UINT32_ARRAY + (FIELD_SIZE_UINT32_ARRAY * ARRAY_SIZE_UINT32_ARRAY);

  private static final int FIELD_SIZE_UINT64_ARRAY = BitUtil.SIZE_OF_LONG;

  private static final int ARRAY_SIZE_UINT64_ARRAY = 4;

  public static final int FIELD_OFFSET_ANCHOR = FIELD_OFFSET_UINT64_ARRAY + (FIELD_SIZE_UINT64_ARRAY * ARRAY_SIZE_UINT64_ARRAY);

  public static final int FIELD_OFFSET_INT8_ARRAY = 0;

  private static final int FIELD_SIZE_INT8_ARRAY = BitUtil.SIZE_OF_BYTE;

  private static final int ARRAY_SIZE_INT8_ARRAY = 1;

  public static final int FIELD_OFFSET_INT16_ARRAY = FIELD_OFFSET_INT8_ARRAY + (FIELD_SIZE_INT8_ARRAY * ARRAY_SIZE_INT8_ARRAY);

  private static final int FIELD_SIZE_INT16_ARRAY = BitUtil.SIZE_OF_SHORT;

  private static final int ARRAY_SIZE_INT16_ARRAY = 2;

  public static final int FIELD_OFFSET_INT32_ARRAY = FIELD_OFFSET_INT16_ARRAY + (FIELD_SIZE_INT16_ARRAY * ARRAY_SIZE_INT16_ARRAY);

  private static final int FIELD_SIZE_INT32_ARRAY = BitUtil.SIZE_OF_INT;

  private static final int ARRAY_SIZE_INT32_ARRAY = 3;

  public static final int FIELD_OFFSET_INT64_ARRAY = FIELD_OFFSET_INT32_ARRAY + (FIELD_SIZE_INT32_ARRAY * ARRAY_SIZE_INT32_ARRAY);

  private static final int FIELD_SIZE_INT64_ARRAY = BitUtil.SIZE_OF_LONG;

  private static final int ARRAY_SIZE_INT64_ARRAY = 4;

  private IntPrimitiveIterator iteratorUint8Array;

  private IntPrimitiveIterator iteratorUint16Array;

  private LongPrimitiveIterator iteratorUint32Array;

  private LongPrimitiveIterator iteratorUint64Array;

  private final StringFW anchorRO = new StringFW();

  private IntPrimitiveIterator iteratorInt8Array;

  private IntPrimitiveIterator iteratorInt16Array;

  private IntPrimitiveIterator iteratorInt32Array;

  private LongPrimitiveIterator iteratorInt64Array;

  public PrimitiveIterator.OfInt uint8Array() {
    iteratorUint8Array.index = 0;
    return iteratorUint8Array;
  }

  public PrimitiveIterator.OfInt uint16Array() {
    iteratorUint16Array.index = 0;
    return iteratorUint16Array;
  }

  public PrimitiveIterator.OfLong uint32Array() {
    iteratorUint32Array.index = 0;
    return iteratorUint32Array;
  }

  public PrimitiveIterator.OfLong uint64Array() {
    iteratorUint64Array.index = 0;
    return iteratorUint64Array;
  }

  public StringFW anchor() {
    return anchorRO;
  }

  public PrimitiveIterator.OfInt int8Array() {
    iteratorInt8Array.index = 0;
    return iteratorInt8Array;
  }

  public PrimitiveIterator.OfInt int16Array() {
    iteratorInt16Array.index = 0;
    return iteratorInt16Array;
  }

  public PrimitiveIterator.OfInt int32Array() {
    iteratorInt32Array.index = 0;
    return iteratorInt32Array;
  }

  public PrimitiveIterator.OfLong int64Array() {
    iteratorInt64Array.index = 0;
    return iteratorInt64Array;
  }

  @Override
  public IntegerFixedArraysFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    iteratorUint8Array = new IntPrimitiveIterator("uint8Array", FIELD_OFFSET_UINT8_ARRAY, FIELD_SIZE_UINT8_ARRAY, ARRAY_SIZE_UINT8_ARRAY, o -> (buffer().getByte(o) & 0xFF));
    iteratorUint16Array = new IntPrimitiveIterator("uint16Array", FIELD_OFFSET_UINT16_ARRAY, FIELD_SIZE_UINT16_ARRAY, ARRAY_SIZE_UINT16_ARRAY, o -> (buffer().getShort(o) & 0xFFFF));
    iteratorUint32Array = new LongPrimitiveIterator("uint32Array", FIELD_OFFSET_UINT32_ARRAY, FIELD_SIZE_UINT32_ARRAY, ARRAY_SIZE_UINT32_ARRAY, o -> (buffer().getInt(o) & 0xFFFF_FFFFL));
    iteratorUint64Array = new LongPrimitiveIterator("uint64Array", FIELD_OFFSET_UINT64_ARRAY, FIELD_SIZE_UINT64_ARRAY, ARRAY_SIZE_UINT64_ARRAY, o -> buffer().getLong(o));
    anchorRO.wrap(buffer, offset + FIELD_OFFSET_ANCHOR, maxLimit);
    final int offsetInt8Array = anchor().limit() + FIELD_OFFSET_INT8_ARRAY;
    iteratorInt8Array = new IntPrimitiveIterator("int8Array", offsetInt8Array, FIELD_SIZE_INT8_ARRAY, ARRAY_SIZE_INT8_ARRAY, o -> (int)(buffer().getByte(o)));
    final int offsetInt16Array = anchor().limit() + FIELD_OFFSET_INT16_ARRAY;
    iteratorInt16Array = new IntPrimitiveIterator("int16Array", offsetInt16Array, FIELD_SIZE_INT16_ARRAY, ARRAY_SIZE_INT16_ARRAY, o -> (int)(buffer().getShort(o)));
    final int offsetInt32Array = anchor().limit() + FIELD_OFFSET_INT32_ARRAY;
    iteratorInt32Array = new IntPrimitiveIterator("int32Array", offsetInt32Array, FIELD_SIZE_INT32_ARRAY, ARRAY_SIZE_INT32_ARRAY, o -> buffer().getInt(o));
    final int offsetInt64Array = anchor().limit() + FIELD_OFFSET_INT64_ARRAY;
    iteratorInt64Array = new LongPrimitiveIterator("int64Array", offsetInt64Array, FIELD_SIZE_INT64_ARRAY, ARRAY_SIZE_INT64_ARRAY, o -> buffer().getLong(o));
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public int limit() {
    return anchor().limit() + FIELD_OFFSET_INT64_ARRAY + (FIELD_SIZE_INT64_ARRAY * ARRAY_SIZE_INT64_ARRAY);
  }

  @Override
  public String toString() {
    return String.format("INTEGER_FIXED_ARRAYS [uint8Array=%s, uint16Array=%s, uint32Array=%s, uint64Array=%s, anchor=%s, int8Array=%s, int16Array=%s, int32Array=%s, int64Array=%s]", uint8Array(), uint16Array(), uint32Array(), uint64Array(), anchorRO.asString(), int8Array(), int16Array(), int32Array(), int64Array());
  }

  private final class IntPrimitiveIterator implements PrimitiveIterator.OfInt {
    private final String fieldName;

    private final int offset;

    private final int fieldSize;

    private final int count;

    private final IntUnaryOperator accessor;

    private int index;

    IntPrimitiveIterator(String fieldName, int offset, int fieldSize, int count,
        IntUnaryOperator accessor) {
      this.fieldName = fieldName;
      this.offset = offset;
      this.fieldSize = fieldSize;
      this.count = count;
      this.accessor = accessor;
    }

    @Override
    public boolean hasNext() {
      return index < count;
    }

    @Override
    public int nextInt() {
      if (!hasNext()) {
        throw new NoSuchElementException(fieldName + ": " + index);
      }
      return accessor.applyAsInt(offset + fieldSize * index++);
    }

    @Override
    public String toString() {
      StringBuffer result = new StringBuffer().append("[");
      boolean first = true;
      while(hasNext()) {
        if (!first) {
          result.append(", ");
        }
        result.append(nextInt());
        first = false;
      }
      result.append("]");
      return result.toString();
    }
  }

  private final class LongPrimitiveIterator implements PrimitiveIterator.OfLong {
    private final String fieldName;

    private final int offset;

    private final int fieldSize;

    private final int count;

    private final IntToLongFunction accessor;

    private int index;

    LongPrimitiveIterator(String fieldName, int offset, int fieldSize, int count,
        IntToLongFunction accessor) {
      this.fieldName = fieldName;
      this.offset = offset;
      this.fieldSize = fieldSize;
      this.count = count;
      this.accessor = accessor;
    }

    @Override
    public boolean hasNext() {
      return index < count;
    }

    @Override
    public long nextLong() {
      if (!hasNext()) {
        throw new NoSuchElementException(fieldName + ": " + index);
      }
      return accessor.applyAsLong(offset + fieldSize * index++);
    }

    @Override
    public String toString() {
      StringBuffer result = new StringBuffer().append("[");
      boolean first = true;
      while(hasNext()) {
        if (!first) {
          result.append(", ");
        }
        result.append(nextLong());
        first = false;
      }
      result.append("]");
      return result.toString();
    }
  }

  public static final class Builder extends Flyweight.Builder<IntegerFixedArraysFW> {
    private static final int INDEX_UINT8_ARRAY = 0;

    private static final int INDEX_UINT16_ARRAY = 1;

    private static final int INDEX_UINT32_ARRAY = 2;

    private static final int INDEX_UINT64_ARRAY = 3;

    private static final int INDEX_ANCHOR = 4;

    private static final int INDEX_INT8_ARRAY = 5;

    private static final int INDEX_INT16_ARRAY = 6;

    private static final int INDEX_INT32_ARRAY = 7;

    private static final int INDEX_INT64_ARRAY = 8;

    private int dynamicOffsetUint8Array;

    private int dynamicOffsetUint16Array;

    private int dynamicOffsetUint32Array;

    private int dynamicOffsetUint64Array;

    private int dynamicOffsetInt8Array;

    private int dynamicOffsetInt16Array;

    private int dynamicOffsetInt32Array;

    private int dynamicOffsetInt64Array;

    private static final int FIELD_COUNT = 9;

    @SuppressWarnings("serial")
    private static final BitSet FIELDS_WITH_DEFAULTS = new BitSet(FIELD_COUNT)  {
        {
      }
    }
    ;

    private static final String[] FIELD_NAMES = {
      "uint8Array",
      "uint16Array",
      "uint32Array",
      "uint64Array",
      "anchor",
      "int8Array",
      "int16Array",
      "int32Array",
      "int64Array"
    };

    private final StringFW.Builder anchorRW = new StringFW.Builder();

    private final BitSet fieldsSet = new BitSet(FIELD_COUNT);

    public Builder() {
      super(new IntegerFixedArraysFW());
    }

    public Builder uint8Array(PrimitiveIterator.OfInt values) {
      checkFieldNotSet(INDEX_UINT8_ARRAY);
      if (values == null)
      {
          throw new IllegalArgumentException("uint8Array cannot be null");
      }
      int count = 0;
      while (values.hasNext()) {
          int value = values.nextInt();
          appendUint8Array((short) value);
          count++;
      }
      if (count < ARRAY_SIZE_INT8_ARRAY)
      {
          throw new IllegalArgumentException("Not enough values for uint8Array");
      }
      return this;
    }

    public Builder appendUint8Array(short value) {
      if (value < 0L) {
        throw new IllegalArgumentException(String.format("Value %d too low for field \"uint8Array\"", value));
      }
      if (!fieldsSet.get(INDEX_UINT8_ARRAY)) {
          // Set prior default if any
        checkFieldsSet(0, INDEX_UINT8_ARRAY);
        fieldsSet.set(INDEX_UINT8_ARRAY);
        dynamicOffsetInt8Array = limit();
      }
      int newLimit = limit() + FIELD_SIZE_UINT16_ARRAY;
      checkLimit(newLimit, maxLimit());
      int newSize = newLimit - dynamicOffsetInt8Array / FIELD_SIZE_UINT16_ARRAY;
      if (newSize > ARRAY_SIZE_INT8_ARRAY)
      {
          throw new IndexOutOfBoundsException();
      }
      buffer().putLong(limit(), value);
      limit(newLimit);
      return this;
    }

    private StringFW.Builder anchor() {
      checkFieldNotSet(INDEX_ANCHOR);
      checkFieldsSet(0, INDEX_ANCHOR);
      return anchorRW.wrap(buffer(), limit(), maxLimit());
    }

    public Builder anchor(String value) {
      StringFW.Builder anchorRW = anchor();
      anchorRW.set(value, StandardCharsets.UTF_8);
      fieldsSet.set(INDEX_ANCHOR);
      limit(anchorRW.build().limit());
      return this;
    }

    public Builder anchor(StringFW value) {
      StringFW.Builder anchorRW = anchor();
      anchorRW.set(value);
      fieldsSet.set(INDEX_ANCHOR);
      limit(anchorRW.build().limit());
      return this;
    }

    public Builder anchor(DirectBuffer buffer, int offset, int length) {
      StringFW.Builder anchorRW = anchor();
      anchorRW.set(buffer, offset, length);
      fieldsSet.set(INDEX_ANCHOR);
      limit(anchorRW.build().limit());
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
  }
}
