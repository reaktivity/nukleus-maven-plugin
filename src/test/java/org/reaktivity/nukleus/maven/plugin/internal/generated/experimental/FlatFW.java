 // TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.experimental;

import static java.lang.String.format;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import javax.annotation.Generated;
import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

@Generated("reaktivity")
public final class FlatFW extends Flyweight {
  public static final int FIELD_OFFSET_FIXED1 = 0;

  private static final int FIELD_SIZE_FIXED1 = BitUtil.SIZE_OF_BYTE;

  public static final int FIELD_OFFSET_FIXED2 = FIELD_OFFSET_FIXED1 + FIELD_SIZE_FIXED1;

  private static final int FIELD_SIZE_FIXED2 = BitUtil.SIZE_OF_SHORT;

  public static final int FIELD_OFFSET_STRING1 = FIELD_OFFSET_FIXED2 + FIELD_SIZE_FIXED2;

  public static final int FIELD_OFFSET_FIXED3 = 0;

  private static final int FIELD_SIZE_FIXED3 = BitUtil.SIZE_OF_INT;

  public static final int FIELD_OFFSET_STRING2 = FIELD_OFFSET_FIXED3 + FIELD_SIZE_FIXED3;

  private final StringFW string1RO = new StringFW();

  private final StringFW string2RO = new StringFW();

  public int fixed1() {
    return buffer().getByte(offset() + FIELD_OFFSET_FIXED1) & 0xFF;
  }

  public int fixed2() {
    return buffer().getShort(offset() + FIELD_OFFSET_FIXED2, ByteOrder.BIG_ENDIAN) & 0xFFFF;
  }

  public StringFW string1() {
    return string1RO;
  }

  public int fixed3() {
    return buffer().getInt(string1().limit() + FIELD_OFFSET_FIXED3);
  }

  public StringFW string2() {
    return string2RO;
  }

  @Override
  public FlatFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    string1RO.wrap(buffer, offset + FIELD_OFFSET_STRING1, maxLimit);
    string2RO.wrap(buffer, string1RO.limit() + FIELD_OFFSET_STRING2, maxLimit);
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public int limit() {
    return string2().limit();
  }

  @Override
  public String toString() {
    return String.format("FLAT [fixed1=%d, fixed2=%d, string1=%s, fixed3=%d, string2=%s]", fixed1(), fixed2(), string1RO.asString(), fixed3(), string2RO.asString());
  }

  public static final class Builder extends Flyweight.Builder<FlatFW> {
    private static final int FIELD_COUNT = 5;
    private static final int DEFAULT_FIXED2 = 222;
    private static final int DEFAULT_FIXED3 = 333;

    private static final int INDEX_FIXED1 = 0;
    private static final int INDEX_FIXED2 = 1;
    private static final int INDEX_STRING1 = 2;
    private static final int INDEX_FIXED3 = 3;
    private static final int INDEX_STRING2 = 4;

    private static final BitSet FIELDS_WITH_DEFAULTS = new BitSet(FIELD_COUNT);

    static {
        FIELDS_WITH_DEFAULTS.set(INDEX_FIXED2);
        FIELDS_WITH_DEFAULTS.set(INDEX_FIXED3);
    }

    private static final String[] FIELD_NAMES = new String[]
    {
        "fixed1",
        "fixed2",
        "string1",
        "fixed3",
        "string2"
    };

    private final BitSet fieldsSet = new BitSet(FIELD_COUNT);
    private final StringFW.Builder string1RW = new StringFW.Builder();
    private final StringFW.Builder string2RW = new StringFW.Builder();

    public Builder() {
      super(new FlatFW());
    }

    private int fixed1() {
      return buffer().getByte(offset() + FIELD_OFFSET_FIXED1) & 0xFF;
    }

    public Builder fixed1(int value) {
      prepareToSetField(INDEX_FIXED1);
      buffer().putByte(offset() + FIELD_OFFSET_FIXED1, (byte)(value & 0xFF));
      return this;
    }

    private int fixed2() {
      return buffer().getShort(offset() + FIELD_OFFSET_FIXED2, ByteOrder.BIG_ENDIAN) & 0xFFFF;
    }

    public Builder fixed2(int value) {
      prepareToSetField(INDEX_FIXED2);
      buffer().putShort(offset() + FIELD_OFFSET_FIXED2, (short)(value & 0xFFFF), ByteOrder.BIG_ENDIAN);
      return this;
    }

    private StringFW.Builder string1() {
      return string1RW.wrap(buffer(), offset() + FIELD_OFFSET_STRING1, maxLimit());
    }


    public Builder string1(String value) {
      prepareToSetField(INDEX_STRING1);
      StringFW.Builder string1 = string1();
      string1.set(value, StandardCharsets.UTF_8);
      limit(string1().build().limit());
      return this;
    }

    public Builder string1(StringFW value) {
      prepareToSetField(INDEX_STRING1);
      StringFW.Builder string1 = string1();
      string1.set(value);
      limit(string1.build().limit());
      return this;
    }

    public Builder string1(DirectBuffer buffer, int offset, int length) {
      prepareToSetField(INDEX_STRING1);
      StringFW.Builder string1 = string1();
      string1.set(buffer, offset, length);
      limit(string1.build().limit());
      return this;
    }

    private int fixed3() {
      return buffer().getInt(string1().limit() + FIELD_OFFSET_FIXED3);
    }

    public Builder fixed3(int value) {
      prepareToSetField(INDEX_FIXED3);
      buffer().putInt(string1().build().limit() + FIELD_OFFSET_FIXED3, value);
      return this;
    }

    private StringFW.Builder string2() {
      int anchor = string1().build().limit() + FIELD_OFFSET_STRING2;
      return string2RW.wrap(buffer(), anchor, maxLimit());
    }

    public Builder string2(String value) {
      prepareToSetField(INDEX_STRING2);
      if (value == null) {
        limit(string1().build().limit() + FIELD_OFFSET_STRING2);
      } else {
        string2().set(value, StandardCharsets.UTF_8);
        limit(string2().build().limit());
      }
      return this;
    }

    public Builder string2(StringFW value) {
      prepareToSetField(INDEX_STRING2);
      StringFW.Builder $string2 = string2();
      $string2.set(value);
      limit($string2.build().limit());
      return this;
    }

    public Builder string2(DirectBuffer buffer, int offset, int length) {
      prepareToSetField(INDEX_STRING2);
      StringFW.Builder string2 = string2();
      string2.set(buffer, offset, length);
      limit(string2.build().limit());
      return this;
    }

    @Override
    public Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      fieldsSet.clear();
      super.wrap(buffer, offset, maxLimit);
      if (offset + FIELD_OFFSET_STRING1 > maxLimit) {
          final String msg = String.format("offset=%d, maxLimit=%d leaves insufficient space", offset, maxLimit);
          throw new IndexOutOfBoundsException(msg);
      }
      return this;
    }

    @Override
    public FlatFW build()
    {
        setDefaults();
        for (int i=0; i < FIELD_COUNT; i++) {
            if (!fieldsSet.get(i))
            {
                throw new IllegalStateException(format("Required field \"%s\" is not set", FIELD_NAMES[i]));
            }
        }
        fieldsSet.clear();
        return super.build();
    }

    private void prepareToSetField(int index) {
        if (fieldsSet.get(index))
        {
            throw new IllegalStateException(format("Field \"%s\" has already been set", FIELD_NAMES[index]));
        }
        for (int i=0; i < index; i++) {
            if (!fieldsSet.get(i) && !FIELDS_WITH_DEFAULTS.get(i)) {
                throw new IllegalStateException(format("Required field \"%s\" is not set", FIELD_NAMES[i]));
            }
        }
        fieldsSet.set(index);
    }

    private void setDefaults()
    {
        if (!fieldsSet.get(INDEX_FIXED2)) {
            fixed2(DEFAULT_FIXED2);
        }
        if (!fieldsSet.get(INDEX_FIXED3)) {
            fixed3(DEFAULT_FIXED3);
        }
    }
  }
}
