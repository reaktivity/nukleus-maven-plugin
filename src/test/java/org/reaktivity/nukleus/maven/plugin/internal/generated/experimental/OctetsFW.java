// TODO: license
package org.reaktivity.nukleus.maven.plugin.internal.generated.experimental;

import javax.annotation.Generated;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

@Generated("reaktivity")
public final class OctetsFW extends Flyweight {
  public <T> T get(Flyweight.Visitor<T> visitor) {
    DirectBuffer buffer = buffer();
    int offset = offset();
    int limit = limit();
    return visitor.visit(buffer, offset, limit);
  }

  @Override
  public int limit() {
    return maxLimit();
  }

  @Override
  public OctetsFW wrap(DirectBuffer buffer, int offset, int maxLimit) {
    super.wrap(buffer, offset, maxLimit);
    checkLimit(limit(), maxLimit);
    return this;
  }

  @Override
  public String toString() {
    return String.format("octets[%d]", sizeof());
  }

  public static final class Builder extends Flyweight.Builder<OctetsFW> {
    public Builder() {
      super(new OctetsFW());
    }

    public Builder wrap(MutableDirectBuffer buffer, int offset, int maxLimit) {
      super.wrap(buffer, offset, maxLimit);
      return this;
    }

    public Builder reset() {
      limit(offset());
      return this;
    }

    public Builder set(OctetsFW value) {
      buffer().putBytes(offset(), value.buffer(), value.offset(), value.sizeof());
      limit(offset() + value.sizeof());
      return this;
    }

    public Builder set(DirectBuffer value, int offset, int length) {
      buffer().putBytes(offset(), value, offset, length);
      limit(offset() + length);
      return this;
    }

    public Builder set(byte[] value) {
      buffer().putBytes(offset(), value);
      limit(offset() + value.length);
      return this;
    }

    public Builder set(Flyweight.Builder.Visitor visitor) {
      int length = visitor.visit(buffer(), offset(), maxLimit());
      limit(offset() + length);
      return this;
    }

    public Builder put(OctetsFW value) {
      buffer().putBytes(limit(), value.buffer(), value.offset(), value.sizeof());
      limit(limit() + value.sizeof());
      return this;
    }

    public Builder put(DirectBuffer value, int offset, int length) {
      buffer().putBytes(limit(), value, offset, length);
      limit(limit() + length);
      return this;
    }

    public Builder put(byte[] value) {
      buffer().putBytes(limit(), value);
      limit(limit() + value.length);
      return this;
    }

    public Builder put(Flyweight.Builder.Visitor visitor) {
      int length = visitor.visit(buffer(), limit(), maxLimit());
      limit(limit() + length);
      return this;
    }
  }
}
