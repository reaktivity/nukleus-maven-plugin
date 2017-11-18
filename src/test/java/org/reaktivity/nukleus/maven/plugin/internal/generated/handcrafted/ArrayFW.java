// TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.handcrafted;

import java.nio.ByteOrder;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public final class ArrayFW<T extends Flyweight> extends Flyweight {
    private static final int FIELD_SIZE_LENGTH = Integer.BYTES;

    private final ByteOrder byteOrder;
    private final T itemRO;

  public ArrayFW(T itemRO) {
    this.itemRO = Objects.requireNonNull(itemRO);
    this.byteOrder = ByteOrder.nativeOrder();
  }

  public ArrayFW(T itemRO, ByteOrder byteOrder) {
      this.itemRO = Objects.requireNonNull(itemRO);
      this.byteOrder = byteOrder;
    }

  @Override
  public int limit() {
    return offset() + FIELD_SIZE_LENGTH + length0();
  }

  @Override
  public ArrayFW<T> wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    checkLimit(limit(), maxLimit);
    return this;
  }

  public ArrayFW<T> forEach(Consumer<? super T> consumer) {
    for (int offset = offset() + FIELD_SIZE_LENGTH; offset < limit(); offset = itemRO.limit()) {
      itemRO.wrap(buffer(), offset, limit());
      consumer.accept(itemRO);
    }
    return this;
  }

  public boolean anyMatch(Predicate<? super T> predicate) {
    for (int offset = offset(); offset < maxLimit(); offset = itemRO.limit()) {
      itemRO.wrap(buffer(), offset, maxLimit());
      if (predicate.test(itemRO)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("ARRAY containing %d bytes of data", length0());
  }

  private int length0() {
      int length = buffer().getInt(offset(), byteOrder);
      if (length < 0)
      {
          throw new IllegalArgumentException(String.format("Invalid list at offset %d: size < 0", offset()));
      }
      return length;
    }

  public static final class Builder<B extends Flyweight.Builder<T>, T extends Flyweight> extends Flyweight.Builder<ArrayFW<T>> {
    private final ByteOrder byteOrder;
    private final B itemRW;

    public Builder(B itemRW, T itemRO) {
      super(new ArrayFW<T>(itemRO));
      this.itemRW = itemRW;
      this.byteOrder = ByteOrder.nativeOrder();
    }

    public Builder(B itemRW, T itemRO, ByteOrder byteOrder) {
        super(new ArrayFW<T>(itemRO));
        this.itemRW = itemRW;
        this.byteOrder = byteOrder;
      }

    @Override
    public Builder<B, T> wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      super.wrap(buffer, offset, maxLimit);
      int newLimit = offset + FIELD_SIZE_LENGTH;
      checkLimit(newLimit, maxLimit);
      super.limit(newLimit);
      return this;
    }

    public Builder<B, T> item(Consumer<B> mutator) {
      itemRW.wrap(buffer(), limit(), maxLimit());
      mutator.accept(itemRW);
      limit(itemRW.build().limit());
      return this;
    }

    @Override
    public ArrayFW<T> build()
    {
        int sizeInBytes = limit() - offset() - FIELD_SIZE_LENGTH;
        buffer().putInt(offset(), sizeInBytes, byteOrder);
        return super.build();
    }

  }
}
