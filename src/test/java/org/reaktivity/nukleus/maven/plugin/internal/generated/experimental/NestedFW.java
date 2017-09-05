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
      checkFieldNotSet(INDEX_FIXED4);
      checkFieldsSet(0, INDEX_FIXED4);
      int newLimit = limit() + FIELD_SIZE_FIXED4;
      checkLimit(newLimit, maxLimit());
      buffer().putLong(offset() + FIELD_OFFSET_FIXED4, value);
      fieldsSet.set(INDEX_FIXED4);
      limit(newLimit);
      return this;
    }

    public Builder flat(Consumer<FlatFW.Builder> mutator) {
      checkFieldNotSet(INDEX_FLAT);
      if (!fieldsSet.get(INDEX_FIXED4)) {
        fixed4(DEFAULT_FIXED4);
      }
      checkFieldsSet(0, INDEX_FLAT);
      FlatFW.Builder flat = flatRW.wrap(buffer(), limit(),  maxLimit());
      mutator.accept(flat);
      limit(flat.build().limit());
      return this;
    }

    public Builder fixed5(long value) {
      checkFieldNotSet(INDEX_FIXED5);
      checkFieldsSet(0, INDEX_FIXED5);
      int newLimit = limit() + FIELD_SIZE_FIXED5;
      checkLimit(newLimit, maxLimit());
      buffer().putLong(limit(), value);
      fieldsSet.set(INDEX_FIXED5);
      limit(newLimit);
      return this;
    }

    @Override
    public Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      fieldsSet.clear();
      super.wrap(buffer, offset, maxLimit);
      return this;
    }

    @Override
    public NestedFW build() {
      checkFieldsSet(0, FIELD_COUNT);
      fieldsSet.clear();
      return super.build();
    }

    private void checkFieldNotSet(int index) {
      if (fieldsSet.get(index))
      {
        throw new IllegalStateException(format("Field \"%s\" has already been set", FIELD_NAMES[index]));
      }
    }

    private void checkFieldsSet(
        int fromIndex,
        int toIndex)
    {
      int fieldNotSet = fromIndex - 1;
      do
      {
        fieldNotSet = fieldsSet.nextClearBit(fieldNotSet + 1);
      } while (fieldNotSet < toIndex && FIELDS_WITH_DEFAULTS.get(fieldNotSet));

      if (fieldNotSet < toIndex)
      {
        throw new IllegalStateException(format("Required field \"%s\" is not set", FIELD_NAMES[fieldNotSet]));
      }
    }
  }
}
