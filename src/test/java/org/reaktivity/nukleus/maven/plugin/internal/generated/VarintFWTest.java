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
import static org.junit.Assert.assertEquals;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.nukleus.maven.plugin.internal.generated.handcrafted.VarintFW;

public class VarintFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xF);
        }
    };
    private final MutableDirectBuffer expected = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xF);
        }
    };

    private final VarintFW.Builder varintRW = new VarintFW.Builder();
    private final VarintFW varintRO = new VarintFW();

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotReadFromZeroLengthBuffer() throws Exception
    {
        buffer.putByte(10,  (byte) 0x18);
        varintRO.wrap(buffer,  10,  10);
    }

    @Test
    public void shouldReadOneByteValue() throws Exception
    {
        buffer.putByte(10,  (byte) 0x18);
        varintRO.wrap(buffer,  10,  21);
        assertEquals(12, varintRO.value());
    }

    @Test
    public void shouldReadTwoByteValue() throws Exception
    {
        // Actual value is -66, zigzagged value is 132-1 = 131 = 0x83
        buffer.putByte(50, (byte) 0x83);
        buffer.putByte(51, (byte) 0x01);
        varintRO.wrap(buffer,  50,  buffer.capacity());
        assertEquals(-66, varintRO.value());
    }

    @Test
    public void shouldReadMostPositiveValue() throws Exception
    {
        // Actual value is Integer.MAX_VALUE = 0x7ffffffe (31 bits set)
        // Zig-zagged value is 0xfffffffe
        // 7-bit values are 3f 7f 7f 7f 7f (which must be reversed)
        buffer.putByte(50, (byte) 0xfe);
        buffer.putByte(51, (byte) 0xff);
        buffer.putByte(52, (byte) 0xff);
        buffer.putByte(53, (byte) 0xff);
        buffer.putByte(54, (byte) 0x7f);
        varintRO.wrap(buffer,  50,  buffer.capacity());
        assertEquals(Integer.MAX_VALUE, varintRO.value());
    }

    @Test
    public void shouldReadMostNegativeValue() throws Exception
    {
        // Actual value is Integer.MIN_VALUE = -2147483648
        // Zig-zagged value is 0xffffffff
        // 7-bit values are 7f 7f 7f 7f 7e (which must be reversed)
        buffer.putByte(50, (byte) 0xff);
        buffer.putByte(51, (byte) 0xff);
        buffer.putByte(52, (byte) 0xff);
        buffer.putByte(53, (byte) 0xff);
        buffer.putByte(54, (byte) 0x7f);
        varintRO.wrap(buffer,  50,  buffer.capacity());
        assertEquals(Integer.MIN_VALUE, varintRO.value());
    }

    @Test
    public void shouldSetOneByteValue() throws Exception
    {
        expected.putByte(10, (byte) 0x18);
        varintRW.wrap(buffer, 10, 21)
            .set(12)
            .build();
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldSetTwoByteValue() throws Exception
    {
        // Actual value is -66, zigzagged value is 132-1 = 131 = 0x83
        expected.putByte(0, (byte) 0x83);
        expected.putByte(1, (byte) 0x01);
        varintRW.wrap(buffer, 0, buffer.capacity())
            .set(-66)
            .build();
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotBuildWithZeroLengthBuffer() throws Exception
    {
        expected.putByte(10, (byte) 0x18);
        varintRW.wrap(buffer, 10, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotSetValueWithInsufficientSpace() throws Exception
    {
        expected.putByte(10, (byte) 0x18);
        varintRW.wrap(buffer, 10, 11)
            .set(70);
    }
}
