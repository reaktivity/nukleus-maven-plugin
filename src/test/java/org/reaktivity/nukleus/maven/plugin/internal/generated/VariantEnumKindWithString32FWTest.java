/**
 * Copyright 2016-2019 The Reaktivity Project
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
package org.reaktivity.nukleus.maven.plugin.internal.generated;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;

public class VariantEnumKindWithString32FWTest
{
    private static final int LENGTH_SIZE_STRING = 1;
    private static final int LENGTH_SIZE_STRING16 = 2;
    private static final int LENGTH_SIZE_STRING32 = 4;
    private static final int KIND_SIZE = 1;

    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final MutableDirectBuffer expected = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final VariantEnumKindWithString32FW.Builder flyweightRW = new VariantEnumKindWithString32FW.Builder();
    private final VariantEnumKindWithString32FW flyweightRO = new VariantEnumKindWithString32FW();


    static int setAllTestValues(
        MutableDirectBuffer buffer,
        final int offset)
    {
        int pos = offset;
        buffer.putByte(pos,  (byte) EnumWithInt8.ONE.value());
        buffer.putByte(pos += 1, (byte) "valueOfString1".length());
        buffer.putStringWithoutLengthUtf8(pos += 1, "valueOfString1");
        return pos - offset + "valueOfString1".length();
    }

    @Test
    public void shouldNotTryWrapWhenIncompleteCase()
    {
        int size = setAllTestValues(buffer, 10);
        for (int maxLimit = 10; maxLimit < 10 + size; maxLimit++)
        {
            assertNull(flyweightRO.tryWrap(buffer,  10, maxLimit));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotWrapWhenIncompleteCase()
    {
        int size = setAllTestValues(buffer, 10);
        for (int maxLimit = 10; maxLimit < 10 + size; maxLimit++)
        {
            flyweightRO.wrap(buffer,  10, maxLimit);
        }
    }

    @Test
    public void shouldTryWrapWhenLengthSufficient()
    {
        int size = setAllTestValues(buffer, 10);
        assertSame(flyweightRO, flyweightRO.tryWrap(buffer, 10, 10 + size));
    }

    @Test
    public void shouldWrapWhenLengthSufficientCase()
    {
        int size = setAllTestValues(buffer, 10);
        assertSame(flyweightRO, flyweightRO.wrap(buffer, 10, 10 + size));
    }

    @Test
    public void shouldTryWrapAndReadAllValues() throws Exception
    {
        final int offset = 1;
        setAllTestValues(buffer, offset);
        VariantEnumKindWithString32FW flyweight = flyweightRO.tryWrap(buffer, offset, buffer.capacity());
        assertNotNull(flyweight);
        assertEquals("valueOfString1", flyweight.get().asString());
        assertEquals(EnumWithInt8.ONE, flyweight.kind());
    }

    @Test
    public void shouldWrapAndReadAllValues() throws Exception
    {
        final int offset = 1;
        setAllTestValues(buffer, offset);
        VariantEnumKindWithString32FW flyweight = flyweightRO.wrap(buffer, offset, buffer.capacity());
        assertEquals("valueOfString1", flyweight.get().asString());
        assertEquals(EnumWithInt8.ONE, flyweight.kind());
    }

    @Test
    public void shouldSetAsString32()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAsString32(asStringFW("value1"))
            .build()
            .limit();
        VariantEnumKindWithString32FW flyweight = flyweightRO.wrap(buffer, 0, limit);
        assertEquals(KIND_SIZE + LENGTH_SIZE_STRING32 + 6, flyweight.limit());
        assertEquals("value1", flyweight.get().asString());
        assertEquals(EnumWithInt8.THREE, flyweight.kind());
    }

    @Test
    public void shouldSetAsString16()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAsString16(asStringFW("value1"))
            .build()
            .limit();
        VariantEnumKindWithString32FW flyweight = flyweightRO.wrap(buffer, 0, limit);
        assertEquals(KIND_SIZE + LENGTH_SIZE_STRING16 + 6, flyweight.limit());
        assertEquals("value1", flyweight.get().asString());
        assertEquals(EnumWithInt8.TWO, flyweight.kind());
    }

    @Test
    public void shouldSetAsString()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAsString8(asStringFW("value1"))
            .build()
            .limit();
        VariantEnumKindWithString32FW flyweight = flyweightRO.wrap(buffer, 0, limit);
        assertEquals(KIND_SIZE + LENGTH_SIZE_STRING + 6, flyweight.limit());
        assertEquals("value1", flyweight.get().asString());
        assertEquals(EnumWithInt8.ONE, flyweight.kind());
    }

    @Test
    public void shouldSetString()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .set(asStringFW("value1"))
            .build()
            .limit();
        VariantEnumKindWithString32FW flyweight = flyweightRO.wrap(buffer, 0, limit);
        assertEquals(KIND_SIZE + LENGTH_SIZE_STRING + 6, flyweight.limit());
        assertEquals("value1", flyweight.get().asString());
    }

    @Test
    public void shouldSetAsString8Kind()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAs(EnumWithInt8.ONE, asStringFW("value1"))
            .build()
            .limit();
        VariantEnumKindWithString32FW flyweight = flyweightRO.wrap(buffer, 0, limit);
        assertEquals(KIND_SIZE + LENGTH_SIZE_STRING + 6, flyweight.limit());
        assertEquals(EnumWithInt8.ONE, flyweight.kind());
        assertEquals("value1", flyweight.get().asString());
    }

    @Test
    public void shouldSetAsString16Kind()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAs(EnumWithInt8.TWO, asStringFW("value1"))
            .build()
            .limit();
        VariantEnumKindWithString32FW flyweight = flyweightRO.wrap(buffer, 0, limit);
        assertEquals(KIND_SIZE + 2 + "value1".length(), flyweight.limit());
        assertEquals(EnumWithInt8.TWO, flyweight.kind());
        assertEquals("value1", flyweight.get().asString());
    }

    @Test
    public void shouldSetAsString32Kind()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAs(EnumWithInt8.THREE, asStringFW("value1"))
            .build()
            .limit();
        VariantEnumKindWithString32FW flyweight = flyweightRO.wrap(buffer, 0, limit);
        assertEquals(KIND_SIZE + 4 + 6, flyweight.limit());
        assertEquals(EnumWithInt8.THREE, flyweight.kind());
        assertEquals("value1", flyweight.get().asString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetAsString8KindWithLargeString()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAs(EnumWithInt8.ONE, asStringFW(String.format("%65535s", "0")))
            .build();
    }

    private static StringFW asStringFW(
        String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new String8FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}
