// TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.handcrafted;

import java.util.function.Consumer;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public abstract class Flyweight {
  private DirectBuffer buffer;

  private int offset;

  private int maxLimit;

  public final int offset() {
    return offset;
  }

  public final DirectBuffer buffer() {
    return buffer;
  }

  public abstract int limit();

  public final int sizeof() {
    return limit() - offset();
  }

  protected final int maxLimit() {
    return maxLimit;
  }

  protected Flyweight wrap(DirectBuffer buffer, int offset, int maxLimit) {
    this.buffer = buffer;
    this.offset = offset;
    this.maxLimit = maxLimit;
    return this;
  }

  protected static final void checkLimit(int limit, int maxLimit) {
    if (limit > maxLimit) {
      final String msg = String.format("limit=%d is beyond maxLimit=%d", limit, maxLimit);
      throw new IndexOutOfBoundsException(msg);
    }
  }

  @FunctionalInterface
  public interface Visitor<T> {
    T visit(DirectBuffer buffer, int offset, int maxLimit);
  }

  public abstract static class Builder<T extends Flyweight> {
    private final T flyweight;

    private MutableDirectBuffer buffer;

    private int offset;

    private int limit;

    private int maxLimit;

    protected Builder(T flyweight) {
      this.flyweight = flyweight;
    }

    public final int limit() {
      return limit;
    }

    public final int maxLimit() {
      return maxLimit;
    }

    public T build() {
      flyweight.wrap(buffer, offset, limit);
      return flyweight;
    }

    protected final T flyweight() {
      return flyweight;
    }

    protected final MutableDirectBuffer buffer() {
      return buffer;
    }

    protected final int offset() {
      return offset;
    }

    protected final void limit(int limit) {
      this.limit = limit;
    }

    protected Builder<T> wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      this.buffer = buffer;
      this.offset = offset;
      this.limit = offset;
      this.maxLimit = maxLimit;
      return this;
    }

    public <E> Builder<T> iterate(Iterable<E> iterable, Consumer<E> action) {
      iterable.forEach(action);
      return this;
    }

    @FunctionalInterface
    public interface Visitor {
      int visit(MutableDirectBuffer buffer, int offset, int maxLimit);
    }
  }
}
