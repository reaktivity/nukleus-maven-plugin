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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.StringFW;

public class StringFWTest
{
    private static final int LENGTH_SIZE = 1;

    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100));
    {
        buffer.setMemory(0, buffer.capacity(), (byte) 0xab);
    }
    MutableDirectBuffer expected = new UnsafeBuffer(allocateDirect(100));
    {
        expected.setMemory(0, expected.capacity(), (byte) 0xab);
    }
    private final StringFW.Builder stringRW = new StringFW.Builder();
    private final StringFW stringRO = new StringFW();

    @Test
    public void shouldSetUsingString() throws Exception
    {
        int offset = 0;
        int expectedLimit = setBufferValue(expected, offset);

        int limit = stringRW.wrap(buffer, offset, buffer.capacity())
                .set("value1", UTF_8)
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);

        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
        assertStringValue(stringRO);
    }

    @Test
    public void shouldSetUsingStringFW() throws Exception
    {
        int offset = 0;
        int expectedLimit = setBufferValue(expected, offset);

        int limit = stringRW.wrap(buffer, offset, 50)
                .set(asStringFW("value1"))
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);

        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
        assertStringValue(stringRO);
    }

    @Test
    public void shouldSetUsingBuffer() throws Exception
    {
        int offset = 0;
        int expectedLimit = setBufferValue(expected, offset);

        int limit = stringRW.wrap(buffer, offset, 50)
                .set(asBuffer("value1"), 0, 6)
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);

        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
        assertStringValue(stringRO);
    }

    @Test
    public void shouldDefaultAfterRewrap() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, buffer.capacity())
                .set("Hello, world", UTF_8)
                .build()
                .limit();

        StringFW string = stringRW.wrap(buffer, 0, limit)
                .build();
        assertLengthSize(string);
    }

    @Test
    public void shouldDefaultToEmpty() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, buffer.capacity())
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertLengthSize(stringRO);
    }

    @Test
    public void shouldSetToNull() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, buffer.capacity())
                .set(null, UTF_8)
                .build()
                .limit();
        assertEquals(1, limit);
        stringRO.wrap(buffer,  0,  limit);
        assertLengthSize(stringRO);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToWrapWithInsufficientLength()
    {
        stringRW.wrap(buffer, 10, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUsingStringWhenExceedsMaxLimitVariant1()
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
    public void shouldFailToSetUsingStringFWWhenExceedsMaxLimitVariant2()
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
    public void shouldFailToSetUsingBufferWhenExceedsMaxLimitVariant3()
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

    @Test
    public void shouldSetToNullUsingStringSetter() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, buffer.capacity())
                .set(null, UTF_8)
                .build()
                .limit();
        assertEquals(1, limit);
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(LENGTH_SIZE, stringRO.limit());
        assertEquals(LENGTH_SIZE, stringRO.sizeof());
        assertNull(stringRO.asString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToBuildLargeString() throws Exception
    {
        String str = String.format("%270s", "0");
        stringRW.wrap(buffer, 0, buffer.capacity())
                .set(str, UTF_8);
    }

    @Test
    public void shouldReturnString() throws Exception
    {
        assertNotNull(stringRO.toString());
    }

    private static MutableDirectBuffer asBuffer(String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(value.length()));
        buffer.putStringWithoutLengthUtf8(0, value);
        return buffer;
    }

    private static StringFW asStringFW(String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new StringFW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }

    static int setBufferValue(MutableDirectBuffer buffer, int offset)
    {
        buffer.putByte(offset, (byte) "value1".length());
        buffer.putBytes(offset +=1, "value1".getBytes(UTF_8));
        return offset + 6;
    }

    static void assertLengthSize(StringFW stringFW)
    {
        assertNull(stringFW.asString());
        assertEquals(LENGTH_SIZE, stringFW.limit());
        assertEquals(LENGTH_SIZE, stringFW.sizeof());
    }

    static void assertStringValue(StringFW stringFW)
    {
        assertEquals(6 + LENGTH_SIZE, stringFW.limit());
        assertEquals(6 + LENGTH_SIZE, stringFW.sizeof());
        assertEquals("value1", stringFW.asString());
    }
}
