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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.nukleus.maven.plugin.internal.generated.handcrafted.IntegerFixedArraysFW;

public class IntegerFixedArraysFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xFF);
        }
    };
    private final IntegerFixedArraysFW.Builder flyweightRW = new IntegerFixedArraysFW.Builder();
    private final IntegerFixedArraysFW flyweightRO = new IntegerFixedArraysFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldSetUnsigned8ToMaximumValue()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
               .unsigned8(0, 0xFF)
               .unsigned16(1, 0)
               .unsigned32(2, 0)
               .unsigned64(3, 0)
               .signed8(0, (byte) 0)
               .signed16(1, (short) 0)
               .signed32(2, 0)
               .signed64(3, 0)
               .build()
               .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(0xFF, flyweightRO.unsigned8(0));
    }

    @Test
    public void shouldSetUnsigned8ToMinimumValue()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
               .unsigned8(0, 0)
               .unsigned16(1, 0)
               .unsigned32(2, 0)
               .unsigned64(3, 0)
               .signed8(0, (byte) 0)
               .signed16(1, (short) 0)
               .signed32(2, 0)
               .signed64(3, 0)
               .build()
               .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(0, flyweightRO.unsigned8(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUnsigned8WithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 10)
               .unsigned8(0, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUnsigned8WithIndexToHigh()
    {
        flyweightRW.wrap(buffer, 10, 10)
               .unsigned8(-1, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUnsigned8WithIndexTooLow()
    {
        flyweightRW.wrap(buffer, 10, 10)
               .unsigned8(1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetUnsigned8WithValueTooHigh()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
               .unsigned8(0, 0xFF + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetUnsigned8WithValueTooLow()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
               .unsigned8(0, -1);
    }

    @Test
    public void shouldSetUnsigned16ToMaximumValue()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
               .unsigned8(1, 0)
               .unsigned16(1, 0xFFFF)
               .unsigned32(2, 0)
               .unsigned64(3, 0)
               .signed8(0, (byte) 0)
               .signed16(1, (short) 0)
               .signed32(2, 0)
               .signed64(3, 0)
               .build()
               .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(0xFFFF, flyweightRO.unsigned16(1));
    }

    @Test
    public void shouldSetUnsigned16ToMinimumValue()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
               .unsigned8(1, 0)
               .unsigned16(1, 0)
               .unsigned32(2, 0)
               .unsigned64(3, 0)
               .signed8(0, (byte) 0)
               .signed16(1, (short) 0)
               .signed32(2, 0)
               .signed64(3, 0)
               .build()
               .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(0, flyweightRO.unsigned16(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUnsigned16WithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 12)
               .unsigned8(1, 1)
               .unsigned16(1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetUnsigned16WithValueTooHigh()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
               .unsigned16(1, 0xFFFF + 1);
    }

    @Test
    public void shouldFailToSetUnsigned16WithValueTooLow()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("unsigned16");
        flyweightRW.wrap(buffer, 0, buffer.capacity())
               .unsigned16(1, -1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToBuildWithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 30)
            .unsigned8(0, 10)
            .unsigned16(1, 20)
            .unsigned32(2, 30)
            .unsigned64(3, 40)
            .signed8(0, (byte) -10)
            .signed16(1, (short) -20)
            .signed32(2, -30)
            .signed64(3, -40)
            .build();
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
                .unsigned8(0, 10)
                .unsigned16(1, 20)
                .unsigned32(2, 30)
                .unsigned64(3, 40)
                .signed8(0, (byte) -10)
                .signed16(1, (short) -20)
                .signed32(2, -30)
                .signed64(3, -40)
                .build();
        flyweightRO.wrap(buffer,  0,  100);
        assertEquals(10, flyweightRO.unsigned8(0));
        assertEquals(0L, flyweightRO.signed64(2));
        assertEquals(-40L, flyweightRO.signed64(3));
    }

}
