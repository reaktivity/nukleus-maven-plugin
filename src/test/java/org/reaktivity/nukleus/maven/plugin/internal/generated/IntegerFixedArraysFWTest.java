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

import java.util.PrimitiveIterator;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.reaktor.internal.test.types.inner.IntegerFixedArraysFW;

public class IntegerFixedArraysFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xFF);
        }
    };
    private final MutableDirectBuffer expected = new UnsafeBuffer(allocateDirect(100))
    {
        {
            setMemory(0, capacity(), (byte) 0xFF);
        }
    };
    private final IntegerFixedArraysFW.Builder flyweightRW = new IntegerFixedArraysFW.Builder();
    private final IntegerFixedArraysFW flyweightRO = new IntegerFixedArraysFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToBuildWithInsufficientSpace()
    {

    }

    @Test
    public void shouldReadAllValues() throws Exception
    {
        buffer.putByte(0, (byte) 0xFF); // uint8Array[1]
        buffer.putShort(1, (short) 3); // uint16Array[2]
        buffer.putShort(3, (short) 0xFFFF);
        buffer.putInt(5, 10); // uint32Array[3]
        buffer.putInt(9, 0xFFFFFFFF);
        buffer.putInt(13, 12);
        buffer.putLong(17, 20L); // uint64Array[4]
        buffer.putLong(25, 21L);
        buffer.putLong(33, 22L);
        buffer.putLong(41, 23L);
        flyweightRO.wrap(buffer,  0,  buffer.capacity());
        PrimitiveIterator.OfInt uint8Array = flyweightRO.uint8Array();
        assertEquals(0xFF, uint8Array.nextInt());

        PrimitiveIterator.OfInt uint16Array = flyweightRO.uint16Array();
        assertEquals(3, uint16Array.nextInt());
        assertEquals(0xFFFF, uint16Array.nextInt());

        PrimitiveIterator.OfLong uint32Array = flyweightRO.uint32Array();
        assertEquals(10, uint32Array.nextLong());
        assertEquals(0xFFFFFFFFL, uint32Array.nextLong());
        assertEquals(12, uint32Array.nextLong());

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUint8ArrayBeyondLimit()
    {
//      flyweightRW.wrap(buffer, 0, buffer.capacity())
//      .appendUint8Array(1, 10)
    }

    @Test
    public void shouldSetAllValuesUsingAppend() throws Exception
    {
//        flyweightRW.wrap(buffer, 0, buffer.capacity())
//                .appendUint8Array(0, 10)
//                .unsigned16(0, 10)
//                .unsigned16(1, 20)
//                .unsigned32(1, 20)
//                .unsigned32(2, 30)
//                .unsigned64(3, 40)
//                .signed8(0, (byte) -10)
//                .signed16(1, (short) -20)
//                .signed32(2, -30)
//                .signed64(3, -40)
//                .build();
//        flyweightRO.wrap(buffer,  0,  100);

        expected.putByte(0, (byte) 0xFF); // uint8Array[1]
        expected.putShort(1, (short) 3); // uint16Array[2]
        expected.putShort(3, (short) 0xFFFF);
        expected.putInt(5, 10); // uint32Array[3]
        expected.putInt(9, 11);
        expected.putInt(13, 12);
        expected.putLong(17, 20L); // uint64Array[4]
        expected.putLong(25, 21L);
        expected.putLong(33, 22L);
        expected.putLong(41, 23L);
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldConvertToString() throws Exception
    {
//        flyweightRW.wrap(buffer, 0, buffer.capacity())
//        .unsigned8(0, 10)
//        .unsigned16(1, 20)
//        .unsigned32(2, 30)
//        .unsigned64(3, 40)
//        .signed8(0, (byte) -10)
//        .signed16(1, (short) -20)
//        .signed32(2, -30)
//        .signed64(2, -40)
//        .signed64(3, -80)
//        .build();
//        flyweightRO.wrap(buffer,  0,  100);
//        assertTrue(flyweightRO.toString().contains("unsigned32=[0, 0, 30]"));
//        assertTrue(flyweightRO.toString().contains("signed64=[0, 0, -40, -80]"));
    }

}
