/**
 * Copyright 2016-2021 The Reaktivity Project
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
package org.reaktivity.maven.plugins.nukleus.internal.generated;

import static java.nio.ByteBuffer.allocateDirect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.Varbyteuint32FW;

public class Varbyteuint32FWTest
{
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

    private final Varbyteuint32FW.Builder varbyteuint32RW = new Varbyteuint32FW.Builder();
    private final Varbyteuint32FW varbyteuint32RO = new Varbyteuint32FW();

    @Test
    public void shouldNotTryWrapZeroLengthBuffer() throws Exception
    {
        assertNull(varbyteuint32RO.tryWrap(buffer,  10,  10));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotWrapZeroLengthBuffer() throws Exception
    {
        varbyteuint32RO.wrap(buffer,  10,  10);
    }

    @Test
    public void shouldNotTryWrapIncompleteValue() throws Exception
    {
        // Set up buffer so it will give index out of bounds if the implementation attempts to compute
        // the varint value without respecting maxLimit
        MutableDirectBuffer buffer = new UnsafeBuffer(new byte[2]);
        buffer.putByte(0, (byte) 0x81);
        buffer.putByte(1, (byte) 0x81);
        assertNull(varbyteuint32RO.tryWrap(buffer, 0, buffer.capacity()));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotWrapIncompleteValue() throws Exception
    {
        // Set up buffer so it will overflow if the implementation attempts to compute
        // the varint value without respecting maxLimit
        MutableDirectBuffer buffer = new UnsafeBuffer(new byte[2]);
        buffer.putByte(0, (byte) 0x81);
        buffer.putByte(1, (byte) 0x81);
        varbyteuint32RO.wrap(buffer, 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotWrapValueWith33bits() throws Exception
    {
        buffer.putByte(50, (byte) 0xFF);
        buffer.putByte(51, (byte) 0xFF);
        buffer.putByte(52, (byte) 0xFF);
        buffer.putByte(53, (byte) 0xFF);
        buffer.putByte(54, (byte) 0x80);
        varbyteuint32RO.wrap(buffer,  50,  buffer.capacity());
        assertEquals(Integer.MAX_VALUE, varbyteuint32RO.value());
    }

    @Test
    public void shouldTryWrap() throws Exception
    {
        // Actual value is 128
        buffer.putByte(50, (byte) 0x80);
        buffer.putByte(51, (byte) 0x01);
        Varbyteuint32FW result = varbyteuint32RO.tryWrap(buffer, 50, 52);
        assertEquals(128, result.value());
    }

    @Test
    public void shouldWrap() throws Exception
    {
        // Actual value is 128
        buffer.putByte(50, (byte) 0x80);
        buffer.putByte(51, (byte) 0x01);
        varbyteuint32RO.wrap(buffer, 50, 52);
    }

    @Test
    public void shouldReadOneByteValue() throws Exception
    {
        // Actual value is 24 = 0x18
        // Encoded value is 24 = 0x18
        int offset = 13;
        buffer.putByte(offset,  (byte) 0x18);
        assertEquals(offset + 1, varbyteuint32RO.wrap(buffer,  offset,  21).limit());
        assertEquals(24, varbyteuint32RO.value());
    }

    @Test
    public void shouldReadTwoByteValue() throws Exception
    {
        // Actual value is 16383
        // Encoded value is 32767 = 0x7FFF
        buffer.putByte(50, (byte) 0xFF);
        buffer.putByte(51, (byte) 0x7F);
        assertEquals(52, varbyteuint32RO.wrap(buffer,  50,  buffer.capacity()).limit());
        assertEquals(16383, varbyteuint32RO.value());
    }

    @Test
    public void shouldReadMaximumValue() throws Exception
    {
        // Actual value is 268435455 = 0x0FFFFFFF (31 bits set)
        // Encoded value is 217483647 = 0x7FFFFFFF
        buffer.putByte(50, (byte) 0xFF);
        buffer.putByte(51, (byte) 0xFF);
        buffer.putByte(52, (byte) 0xFF);
        buffer.putByte(53, (byte) 0x7F);
        assertEquals(54, varbyteuint32RO.wrap(buffer,  50,  buffer.capacity()).limit());
        assertEquals(268435455, varbyteuint32RO.value());
    }

    @Test
    public void shouldReadMinimumValue() throws Exception
    {
        // Actual value is 0 = 0x00
        // Encoded value is 0x00
        buffer.putByte(50, (byte) 0x00);
        varbyteuint32RO.wrap(buffer,  50,  buffer.capacity());
        assertEquals(0, varbyteuint32RO.value());
    }

    @Test
    public void shouldSetMaximumValue() throws Exception
    {
        // Actual value is 268435455 = 0x0FFFFFFF (31 bits set)
        // Encoded value is 217483647 = 0x7FFFFFFF
        expected.putByte(50, (byte) 0xFF);
        expected.putByte(51, (byte) 0xFF);
        expected.putByte(52, (byte) 0xFF);
        expected.putByte(53, (byte) 0x7F);
        varbyteuint32RW.wrap(buffer,  50,  buffer.capacity())
            .set(268435455)
            .build();
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldSetMinimumValue() throws Exception
    {
        // Actual value is 0 = 0x00
        // Encoded value is 0x00
        expected.putByte(50, (byte) 0x00);
        varbyteuint32RW.wrap(buffer,  50,  buffer.capacity())
            .set(0)
            .build();
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldSetOneByteValue() throws Exception
    {
        expected.putByte(10, (byte) 0x01);
        varbyteuint32RW.wrap(buffer, 10, 21)
            .set(1)
            .build();
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldSetTwoByteValue() throws Exception
    {
        // Actual value is 16383
        // Encoded value is 32767 = 0x7FFF
        expected.putByte(0, (byte) 0xFF);
        expected.putByte(1, (byte) 0x7F);
        varbyteuint32RW.wrap(buffer, 0, buffer.capacity())
            .set(16383)
            .build();
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldReportAsString() throws Exception
    {
        // Actual value is 268435455 = 0x0FFFFFFF (31 bits set)
        // Encoded value is 217483647 = 0x7FFFFFFF
        buffer.putByte(50, (byte) 0xFF);
        buffer.putByte(51, (byte) 0xFF);
        buffer.putByte(52, (byte) 0xFF);
        buffer.putByte(53, (byte) 0x7F);
        varbyteuint32RO.wrap(buffer,  50,  buffer.capacity());
        assertEquals(Integer.toString(268435455), varbyteuint32RO.toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotBuildWithZeroLengthBuffer() throws Exception
    {
        expected.putByte(10, (byte) 0x18);
        varbyteuint32RW.wrap(buffer, 10, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotSetValueWithInsufficientSpace() throws Exception
    {
        expected.putByte(10, (byte) 0x18);
        varbyteuint32RW.wrap(buffer, 10, 11)
            .set(268435455);
    }
}
