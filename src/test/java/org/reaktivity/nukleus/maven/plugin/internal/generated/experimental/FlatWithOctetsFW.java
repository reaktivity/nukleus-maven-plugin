// TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.experimental;

import static java.lang.String.format;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.function.Consumer;
import javax.annotation.Generated;
import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

@Generated("reaktivity")
public final class FlatWithOctetsFW extends Flyweight {
  public static final int FIELD_OFFSET_FIXED1 = 0;

  private static final int FIELD_SIZE_FIXED1 = BitUtil.SIZE_OF_LONG;

  public static final int FIELD_OFFSET_STRING1 = FIELD_OFFSET_FIXED1 + FIELD_SIZE_FIXED1;

  public static final int FIELD_OFFSET_EXTENSION = 0;

  private final StringFW string1RO = new StringFW();

  private final OctetsFW extensionRO = new OctetsFW();

  public long fixed1() {
    return buffer().getLong(offset() + FIELD_OFFSET_FIXED1);
  }

  public StringFW string1() {
    return string1RO;
  }

  public OctetsFW extension() {
    return extensionRO;
  }

  @Override
  public FlatWithOctetsFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    string1RO.wrap(buffer, offset + FIELD_OFFSET_STRING1, maxLimit);
    extensionRO.wrap(buffer, string1RO.limit() + FIELD_OFFSET_EXTENSION, maxLimit);
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public int limit() {
    return extension().limit();
  }

  @Override
  public String toString() {
    return String.format("FLAT_WITH_OCTETS [fixed1=%d, string1=%s, extension=%s]", fixed1(), string1RO.asString(), extension());
  }

  public static final class Builder extends Flyweight.Builder<FlatWithOctetsFW> {
      private static final int FIELD_COUNT = 3;
      private static final int DEFAULT_FIXED1 = 111;

      private static final int INDEX_FIXED1 = 0;
      private static final int INDEX_STRING1 = 1;
      private static final int INDEX_EXTENSION = 2;

      private static final BitSet FIELDS_WITH_DEFAULTS = new BitSet(FIELD_COUNT);

      static {
          FIELDS_WITH_DEFAULTS.set(INDEX_FIXED1);
          FIELDS_WITH_DEFAULTS.set(INDEX_EXTENSION);
      }

      private static final String[] FIELD_NAMES = new String[]
      {
          "fixed1",
          "string1",
          "extension"
      };

    private final BitSet fieldsSet = new BitSet(FIELD_COUNT);
    private final StringFW.Builder string1RW = new StringFW.Builder();

    private final OctetsFW.Builder extensionRW = new OctetsFW.Builder();

    public Builder() {
      super(new FlatWithOctetsFW());
    }

    public Builder fixed1(long value) {
      checkFieldNotSet(INDEX_FIXED1);
      checkFieldsSet(0, INDEX_FIXED1);
      int newLimit = limit() + FIELD_SIZE_FIXED1;
      checkLimit(newLimit, maxLimit());
      buffer().putLong(offset() + FIELD_OFFSET_FIXED1, value);
      fieldsSet.set(INDEX_FIXED1);
      limit(newLimit);
      return this;
    }

    private StringFW.Builder string1() {
      checkFieldNotSet(INDEX_STRING1);
      if (!fieldsSet.get(INDEX_FIXED1))
      {
        fixed1(DEFAULT_FIXED1);
      }
      checkFieldsSet(0, INDEX_STRING1);
      return string1RW.wrap(buffer(), limit(),  maxLimit());
    }

    public Builder string1(String value) {
      StringFW.Builder string1 = string1();
      string1.set(value, StandardCharsets.UTF_8);
      fieldsSet.set(INDEX_STRING1);
      limit(string1.build().limit());
      return this;
    }

    public Builder string1(StringFW value) {
      StringFW.Builder string1 = string1();
      string1.set(value);
      fieldsSet.set(INDEX_STRING1);
      limit(string1.build().limit());
      return this;
    }

    public Builder string1(DirectBuffer buffer, int offset, int length) {
      StringFW.Builder string1 = string1();
      string1.set(buffer, offset, length);
      fieldsSet.set(INDEX_STRING1);
      limit(string1.build().limit());
      return this;
    }

    private OctetsFW.Builder extension() {
      checkFieldNotSet(INDEX_EXTENSION);
      checkFieldsSet(0, INDEX_EXTENSION);
      return extensionRW.wrap(buffer(), limit(), maxLimit()).reset();
    }

    public Builder extension(Consumer<OctetsFW.Builder> mutator) {
      OctetsFW.Builder extension = extension();
      mutator.accept(extension);
      limit(extension.build().limit());
      return this;
    }

    public Builder extension(DirectBuffer buffer, int offset, int length) {
      OctetsFW.Builder extension = extension();
      extension.set(buffer, offset, length);
      limit(extension.build().limit());
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
    public FlatWithOctetsFW build()
    {
        if (!fieldsSet.get(INDEX_EXTENSION)) {
            extension(b -> { });
        }
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
