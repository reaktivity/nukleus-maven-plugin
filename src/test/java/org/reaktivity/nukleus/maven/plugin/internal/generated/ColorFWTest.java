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
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ColorFWTest
{
    private static final int LENGTH_SIZE = BitUtil.SIZE_OF_BYTE;

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
    private final ColorFW.Builder flyweightRW = new ColorFW.Builder();
    private final ColorFW flyweightRO = new ColorFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    static int setAllTestValues(MutableDirectBuffer buffer, final int offset, String value)
    {
        int pos = offset;
        byte[] charBytes = value.getBytes(UTF_8);
        buffer.putByte(pos, (byte) charBytes.length);
        buffer.putBytes(pos + 1, charBytes);
        return charBytes.length + LENGTH_SIZE;
    }

    void assertAllTestValuesRead(ColorFW flyweight)
    {
        assertEquals(Color.BLUE, flyweight.get());
    }

    @Test
    public void shouldNotTryWrapWhenIncomplete()
    {
        int size = setAllTestValues(buffer, 10, "blue");
        for (int maxLimit=10; maxLimit < 10 + size; maxLimit++)
        {
            assertNull("at maxLimit " + maxLimit, flyweightRO.tryWrap(buffer,  10, maxLimit));
        }
    }

    @Test
    public void shouldNotWrapWhenIncomplete()
    {
        int size = setAllTestValues(buffer, 10, "blue");
        for (int maxLimit=10; maxLimit < 10 + size; maxLimit++)
        {
            try
            {
                flyweightRO.wrap(buffer,  10, maxLimit);
                fail("Exception not thrown for maxLimit " + maxLimit);
            }
            catch(Exception e)
            {
                if (!(e instanceof IndexOutOfBoundsException))
                {
                    fail("Unexpected exception " + e);
                }
            }
        }
    }

    @Test
    public void shouldTryWrapAndReadAllValues() throws Exception
    {
        final int offset = 1;
        setAllTestValues(buffer, offset, "blue");
        assertNotNull(flyweightRO.tryWrap(buffer, offset, buffer.capacity()));
        assertAllTestValuesRead(flyweightRO);
    }

    @Test
    public void shouldWrapAndReadAllValues() throws Exception
    {
        int size = setAllTestValues(buffer, 10, "blue");
        int limit = flyweightRO.wrap(buffer,  10,  buffer.capacity()).limit();
        assertEquals(10 + size, limit);
        assertAllTestValuesRead(flyweightRO);
    }

    @Test
    public void shouldNotTryInvalidValue() throws Exception
    {
        final int offset = 0;
        byte[] charBytes = "blue".getBytes(UTF_8);
        buffer.putByte(offset,  (byte) 254);
        buffer.putBytes(offset + 1, charBytes);
        assertNull(flyweightRO.tryWrap(buffer, offset, offset + charBytes.length + LENGTH_SIZE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldWNotrapAndReadInvalidValue() throws Exception
    {
        final int offset = 0;
        byte[] charBytes = "blue".getBytes(UTF_8);
        buffer.putByte(offset,  (byte) 2);
        buffer.putBytes(offset + 1, charBytes);
        flyweightRO.wrap(buffer, offset, offset + charBytes.length + LENGTH_SIZE);
        assertNull(flyweightRO.get());
    }

    @Test
    public void shouldSetUsingEnum()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .set(Color.BLUE, UTF_8)
            .build()
            .limit();
        setAllTestValues(expected, 0, Color.BLUE.value());
        assertEquals(LENGTH_SIZE + Color.BLUE.value().getBytes(UTF_8).length, limit);
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldSetUsingColorFW()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .set(asColorFW(Color.BLUE))
            .build()
            .limit();
        flyweightRO.wrap(buffer, 0, limit);
        assertEquals(Color.BLUE, flyweightRO.get());
        assertEquals(4 + LENGTH_SIZE, flyweightRO.limit());
        assertEquals(4 + LENGTH_SIZE, flyweightRO.sizeof());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetWithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 10)
            .set(Color.BLUE, UTF_8);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUsingColorFWWithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 10)
            .set(asColorFW(Color.BLUE));
    }

    @Test
    public void shouldFailToBuildWithNothingSet()
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Color");
        flyweightRW.wrap(buffer, 10, buffer.capacity()).build();
    }

    private static ColorFW asColorFW(Color value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.value().length()));
        return new ColorFW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}