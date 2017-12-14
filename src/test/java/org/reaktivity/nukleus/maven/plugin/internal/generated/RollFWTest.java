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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.reaktor.internal.test.types.inner.Roll;
import org.reaktivity.reaktor.internal.test.types.inner.RollFW;

public class RollFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final RollFW.Builder rollRW = new RollFW.Builder();
    private final RollFW rollRO = new RollFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldSetUsingEnum()
    {
        int limit = rollRW.wrap(buffer, 0, buffer.capacity())
               .set(Roll.SPRING)
               .build()
               .limit();
        rollRO.wrap(buffer,  0, limit);
        assertEquals(Roll.SPRING, rollRO.get());
        assertEquals(1, rollRO.sizeof());
    }

    @Test
    public void shouldSetUsingRollFW()
    {
        RollFW roll = new RollFW().wrap(asBuffer((byte) 0), 0, 1);
        int limit = rollRW.wrap(buffer, 10, 11)
               .set(roll)
               .build()
               .limit();
        rollRO.wrap(buffer, 10,  limit);
        assertEquals(Roll.EGG, rollRO.get());
        assertEquals(1, rollRO.sizeof());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetWithInsufficientSpace()
    {
        rollRW.wrap(buffer, 10, 10)
               .set(Roll.EGG);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUsingRollFWWithInsufficientSpace()
    {
        RollFW roll = new RollFW().wrap(asBuffer((byte) 0), 0, 1);
        rollRW.wrap(buffer, 10, 10)
              .set(roll);
    }

    @Test
    public void shouldFailToBuildWithNothingSet()
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Roll");
        rollRW.wrap(buffer, 10, buffer.capacity())
            .build();
    }

    private static DirectBuffer asBuffer(byte value)
    {
        MutableDirectBuffer valueBuffer = new UnsafeBuffer(allocateDirect(1));
        valueBuffer.putByte(0, value);
        return valueBuffer;
    }

}
