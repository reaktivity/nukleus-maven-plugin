/**
 * Copyright 2016-2017 The Reaktivity Project
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

import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.String16FW;

public class String16FWTest
{
    private static final int LENGTH_SIZE = 2;

    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xF);
        }
    };
    private final String16FW.Builder stringRW = new String16FW.Builder();
    private final String16FW stringRO = new String16FW();

    @Test
    public void shouldDefaultToEmpty() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, buffer.capacity())
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(LENGTH_SIZE, stringRO.limit());
        assertEquals(LENGTH_SIZE, stringRO.sizeof());

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToWrapWithInsufficientLength()
    {
        stringRW.wrap(buffer, 10, 10);
    }

    @Test
    public void shouldWrapWithSufficientLength()
    {
        stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUsingStringWhenExceedsMaxLimit()
    {
        buffer.setMemory(0,  buffer.capacity(), (byte) 0x00);
        try
        {
            stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)
                .set("1", UTF_8);
        }
        finally
        {
            byte[] bytes = new byte[1 + LENGTH_SIZE];
            buffer.getBytes(10, bytes);
            // Make sure memory was not written beyond maxLimit
            assertEquals("Buffer shows memory was written beyond maxLimit: " + BitUtil.toHex(bytes),
                         0, buffer.getByte(10 + LENGTH_SIZE));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUsingStringFWWhenExceedsMaxLimit()
    {
        buffer.setMemory(0,  buffer.capacity(), (byte) 0x00);
        try
        {
            stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)
                .set(asStringFW("1"));
        }
        finally
        {
            byte[] bytes = new byte[1 + LENGTH_SIZE];
            buffer.getBytes(10, bytes);
            // Make sure memory was not written beyond maxLimit
            assertEquals("Buffer shows memory was written beyond maxLimit: " + BitUtil.toHex(bytes),
                         0, buffer.getByte(10 + LENGTH_SIZE));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUsingBufferWhenExceedsMaxLimit()
    {
        buffer.setMemory(0,  buffer.capacity(), (byte) 0x00);
        buffer.putStringWithoutLengthUtf8(0, "1");
        try
        {
            stringRW.wrap(buffer, 10, 10 + LENGTH_SIZE)
                .set(buffer, 0, 1);
        }
        finally
        {
            byte[] bytes = new byte[1 + LENGTH_SIZE];
            buffer.getBytes(10, bytes);
            // Make sure memory was not written beyond maxLimit
            assertEquals("Buffer shows memory was written beyond maxLimit: " + BitUtil.toHex(bytes),
                         0, buffer.getByte(10 + LENGTH_SIZE));
        }
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailToSetToNull() throws Exception
    {
        stringRW.wrap(buffer, 0, buffer.capacity())
                .set(null, UTF_8);
    }

    @Test
    public void shouldSetToEmptyString() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, buffer.capacity())
                .set("", UTF_8)
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(LENGTH_SIZE, stringRO.limit());
        assertEquals(LENGTH_SIZE, stringRO.sizeof());
        assertEquals("", stringRO.asString());
    }

    @Test
    public void shouldSetUsingString() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, buffer.capacity())
                .set("value1", UTF_8)
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(6 + LENGTH_SIZE, stringRO.limit());
        assertEquals(6 + LENGTH_SIZE, stringRO.sizeof());
        assertEquals("value1", stringRO.asString());
    }

    @Test
    public void shouldSetUsingStringFW() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, 50)
                .set(asStringFW("value1"))
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(6 + LENGTH_SIZE, stringRO.limit());
        assertEquals(6 + LENGTH_SIZE, stringRO.sizeof());
        assertEquals("value1", stringRO.asString());
    }

    @Test
    public void shouldSetUsingBuffer() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, 50)
            .set(asBuffer("value1"), 0, 6)
            .build()
            .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(6 + LENGTH_SIZE, stringRO.limit());
        assertEquals(6 + LENGTH_SIZE, stringRO.sizeof());
        assertEquals("value1", stringRO.asString());
    }

    private static MutableDirectBuffer asBuffer(String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(value.length()));
        buffer.putStringWithoutLengthUtf8(0, value);
        return buffer;
    }

    private static String16FW asStringFW(String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new String16FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }

}
