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

import java.util.ArrayList;
import java.util.List;

import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.reaktor.internal.test.types.ArrayFW;
import org.reaktivity.reaktor.internal.test.types.Varint64FW;

public class ArrayFWTest
{
    private static final int LENGTH_SIZE = 4;

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

    private final ArrayFW.Builder<Varint64FW.Builder, Varint64FW> arrayRW =
                        new ArrayFW.Builder<>(new Varint64FW.Builder(), new Varint64FW());

    private final ArrayFW<Varint64FW> arrayRO = new ArrayFW<>(new Varint64FW());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldBuildEmptyList() throws Exception
    {
        int offset = 12;
        int limit = arrayRW.wrap(buffer, offset, buffer.capacity())
                .build()
                .limit();
        assertEquals(offset + 4, limit);
        expected.putInt(offset, 0);
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldReadEmptyList() throws Exception
    {
        buffer.putInt(10,  0);
        arrayRO.wrap(buffer, 10, buffer.capacity());
        assertEquals(14, arrayRO.limit());
        List<Long> contents = new ArrayList<Long>();
        arrayRO.forEach(v -> contents.add(v.value()));
        assertEquals(0, contents.size());
    }

    @Test
    public void shouldFailWrapWhenListSizeIsNegative() throws Exception
    {
        buffer.putInt(10,  -1);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("size < 0");
        arrayRO.wrap(buffer, 10, buffer.capacity());
    }

    @Test
    public void shouldSetItems() throws Exception
    {
        final int offset = 0;
        final int limit = arrayRW.wrap(buffer, offset, buffer.capacity())
                .item(b -> b.set(1L))
                .item(b -> b.set(-1L))
                .item(b -> b.set(12L))
                .build()
                .limit();
        final int expectedSizeInBytes = LENGTH_SIZE + 3;
        assertEquals(offset + expectedSizeInBytes, limit);
        expected.putInt(offset, 3);
        expected.putByte(offset + 4, (byte) 2);
        expected.putByte(offset + 5, (byte) 1);
        expected.putByte(offset + 6, (byte) 0x18);
        byte[] bytes = new byte[buffer.capacity()];
        expected.getBytes(0, bytes);
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldReadItems() throws Exception
    {
        final int offset = 23;
        buffer.putInt(offset, 3);
        buffer.putByte(offset + 4, (byte) 2);
        buffer.putByte(offset + 5, (byte) 1);
        buffer.putByte(offset + 6, (byte) 0x18);

        arrayRO.wrap(buffer, offset, buffer.capacity());
        assertEquals(offset + LENGTH_SIZE + 3, arrayRO.limit());
        List<Long> contents = new ArrayList<Long>();
        arrayRO.forEach(v -> contents.add(v.value()));
        assertEquals(3, contents.size());
        assertEquals(1L, contents.get(0).longValue());
        assertEquals(-1L, contents.get(1).longValue());
        assertEquals(12L, contents.get(2).longValue());
    }

    @Test
    public void shouldDefaultToEmptyAfterRewrap() throws Exception
    {
        int offset = 10;
        int limit = arrayRW.wrap(buffer, offset, buffer.capacity())
                .item(b -> b.set(12L))
                .build()
                .limit();

        ArrayFW<Varint64FW> array = arrayRW.wrap(buffer, offset, limit)
                .build();

        assertEquals(offset + LENGTH_SIZE, array.limit());
        assertEquals(LENGTH_SIZE, array.sizeof());
        List<Long> contents = new ArrayList<Long>();
        array.forEach(v -> contents.add(v.value()));
        assertEquals(0, contents.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToWrapWithInsufficientLength()
    {
        arrayRW.wrap(buffer, 10, 13);
    }

    @Test
    public void shouldWrapWithSufficientLength()
    {
        int limit = arrayRW.wrap(buffer, 10, 10 + LENGTH_SIZE).limit();
        assertEquals(14, limit);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToAddItemWhenExceedsMaxLimit()
    {
        buffer.setMemory(0,  buffer.capacity(), (byte) 0x00);
        final int offset = 10;
        try
        {
            arrayRW.wrap(buffer, offset, offset + LENGTH_SIZE + 4)
                .item(b -> b.set(Integer.MAX_VALUE)); // takes 5 bytes
        }
        finally
        {
            byte[] bytes = new byte[1 + LENGTH_SIZE];
            buffer.getBytes(offset, bytes);
            // Make sure memory was not written beyond maxLimit
            assertEquals("Buffer shows memory was written beyond maxLimit: " + BitUtil.toHex(bytes),
                         0, buffer.getByte(10 + LENGTH_SIZE));
        }
    }

    @Test
    public void shouldDisplayAsString() throws Exception
    {
        int offset = 0;
        ArrayFW<Varint64FW> array = arrayRW.wrap(buffer, offset, buffer.capacity())
                .item(b -> b.set(1L))
                .item(b -> b.set(-1L))
                .item(b -> b.set(123L))
                .build();
        System.out.println(array.toString());
    }

}
