// TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.experimental;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Generated;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

@Generated("reaktivity")
public final class ListFW<T extends Flyweight> extends Flyweight {
  private final T itemRO;

  private int limit;

  public ListFW(T itemRO) {
    this.itemRO = Objects.requireNonNull(itemRO);
  }

  @Override
  public int limit() {
    return limit;
  }

  @Override
  public ListFW<T> wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    for (limit = offset; limit < maxLimit; limit = itemRO.limit()) {
      itemRO.wrap(buffer, limit, maxLimit);
    }
    checkLimit(limit(), maxLimit);
    return this;
  }

  public ListFW<T> forEach(Consumer<? super T> consumer) {
    for (int offset = offset(); offset < maxLimit(); offset = itemRO.limit()) {
      itemRO.wrap(buffer(), offset, maxLimit());
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
    return "LIST";
  }

  public static final class Builder<B extends Flyweight.Builder<T>, T extends Flyweight> extends Flyweight.Builder<ListFW<T>> {
    private final B itemRW;

    public Builder(B itemRW, T itemRO) {
      super(new ListFW<T>(itemRO));
      this.itemRW = itemRW;
    }

    public Builder<B, T> wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      super.wrap(buffer, offset, maxLimit);
      super.limit(offset);
      itemRW.wrap(buffer, offset, maxLimit);
      return this;
    }

    public Builder<B, T> item(Consumer<B> mutator) {
      mutator.accept(itemRW);
      limit(itemRW.build().limit());
      itemRW.wrap(buffer(), limit(), maxLimit());
      return this;
    }
  }
}
