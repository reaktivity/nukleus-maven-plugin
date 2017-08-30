// TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.experimental;

import static java.lang.String.format;

import java.util.BitSet;
import java.util.function.Consumer;
import javax.annotation.Generated;
import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

@Generated("reaktivity")
public final class NestedFW extends Flyweight {
  public static final int FIELD_OFFSET_FIXED4 = 0;

  private static final int FIELD_SIZE_FIXED4 = BitUtil.SIZE_OF_LONG;

  public static final int FIELD_OFFSET_FLAT = FIELD_OFFSET_FIXED4 + FIELD_SIZE_FIXED4;

  public static final int FIELD_OFFSET_FIXED5 = 0;

  private static final int FIELD_SIZE_FIXED5 = BitUtil.SIZE_OF_LONG;

  private final FlatFW flatRO = new FlatFW();

  public long fixed4() {
    return buffer().getLong(offset() + FIELD_OFFSET_FIXED4);
  }

  public FlatFW flat() {
    return flatRO;
  }

  public long fixed5() {
    return buffer().getLong(flat().limit() + FIELD_OFFSET_FIXED5);
  }

  @Override
  public NestedFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    flatRO.wrap(buffer, offset + FIELD_OFFSET_FLAT, maxLimit);
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public int limit() {
    return flat().limit() + FIELD_OFFSET_FIXED5 + FIELD_SIZE_FIXED5;
  }

  @Override
  public String toString() {
    return String.format("NESTED [fixed4=%d, flat=%s, fixed5=%d]", fixed4(), flat(), fixed5());
  }

  public static final class Builder extends Flyweight.Builder<NestedFW> {
      private static final int FIELD_COUNT = 3;
      private static final int DEFAULT_FIXED4 = 444;

      private static final int INDEX_FIXED4 = 0;
      private static final int INDEX_FLAT = 1;
      private static final int INDEX_FIXED5 = 2;

      private static final BitSet FIELDS_WITH_DEFAULTS = new BitSet(FIELD_COUNT);

      static {
          FIELDS_WITH_DEFAULTS.set(INDEX_FIXED4);
      }

      private static final String[] FIELD_NAMES = new String[]
      {
          "fixed4",
          "flat",
          "fixed5"
      };

    private final BitSet fieldsSet = new BitSet(FIELD_COUNT);
    private final FlatFW.Builder flatRW = new FlatFW.Builder();

    public Builder() {
      super(new NestedFW());
    }

    protected long fixed4() {
      return buffer().getLong(offset() + FIELD_OFFSET_FIXED4);
    }

    public Builder fixed4(long value) {
      prepareToSetField(INDEX_FIXED4);
      buffer().putLong(offset() + FIELD_OFFSET_FIXED4, value);
      return this;
    }

    private FlatFW.Builder flat() {
      return flatRW;
    }

    public Builder flat(Consumer<FlatFW.Builder> mutator) {
      prepareToSetField(INDEX_FLAT);
      FlatFW.Builder flat = flatRW.wrap(buffer(), offset() + FIELD_OFFSET_FLAT, maxLimit());
      mutator.accept(flat);
      limit(flat.build().limit());
      return this;
    }

    protected long fixed5() {
      return buffer().getLong(flatRW.limit() + FIELD_OFFSET_FIXED5);
    }

    public Builder fixed5(long value) {
      prepareToSetField(INDEX_FIXED5);
      buffer().putLong(flat().limit() + FIELD_OFFSET_FIXED5, value);
      limit(flat().limit() + FIELD_OFFSET_FIXED5 + FIELD_SIZE_FIXED5);
      return this;
    }

    @Override
    public Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      fieldsSet.clear();
      super.wrap(buffer, offset, maxLimit);
      return this;
    }

    @Override
    public NestedFW build()
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
        if (!fieldsSet.get(INDEX_FIXED4)) {
            fixed4(DEFAULT_FIXED4);
        }
    }
  }
}
