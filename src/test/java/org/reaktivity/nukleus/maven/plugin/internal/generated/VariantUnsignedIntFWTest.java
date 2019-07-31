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
import static org.agrona.BitUtil.SIZE_OF_SHORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

public class VariantUnsignedIntFWTest
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
    private final VariantUnsignedIntFW.Builder flyweightRW = new VariantUnsignedIntFW.Builder();
    private final VariantUnsignedIntFW flyweigthRO = new VariantUnsignedIntFW();

    static int setAllTestValuesCaseUInt8(MutableDirectBuffer buffer, final int offset)
    {
        int pos = offset;
        buffer.putByte(pos, (byte) 1);
        buffer.putShort(pos += 1, (short) 200);
        return pos - offset + SIZE_OF_SHORT;
    }

    @Test
    public void shouldNotTryWrapWhenIncompleteCaseUInt8()
    {
        int size = setAllTestValuesCaseUInt8(buffer, 10);
        for (int maxLimit=10; maxLimit < 10 + size; maxLimit++)
        {
            assertNull(flyweigthRO.tryWrap(buffer,  10, maxLimit));
        }
    }

    @Test
    public void shouldNotWrapWhenIncompleteCaseUInt8()
    {
        int size = setAllTestValuesCaseUInt8(buffer, 10);
        for (int maxLimit=10; maxLimit < 10 + size; maxLimit++)
        {
            try
            {
                flyweigthRO.wrap(buffer,  10, maxLimit);
                fail("Exception not thrown");
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
    public void shouldTryWrapWhenLengthSufficientCaseUInt8()
    {
        int size = setAllTestValuesCaseUInt8(buffer, 10);
        assertSame(flyweigthRO, flyweigthRO.tryWrap(buffer, 10, 10 + size));
    }

    @Test
    public void shouldWrapWhenLengthSufficientCaseUInt8()
    {
        int size = setAllTestValuesCaseUInt8(buffer, 10);
        assertSame(flyweigthRO, flyweigthRO.wrap(buffer, 10, 10 + size));
    }

    @Test
    public void shouldTryWrapAndReadAllValuesCaseUInt8() throws Exception
    {
        final int offset = 1;
        setAllTestValuesCaseUInt8(buffer, offset);
        assertNotNull(flyweigthRO.tryWrap(buffer, offset, buffer.capacity()));
        assertEquals(200, flyweigthRO.getAsUInt8());
        assertEquals(200, flyweigthRO.get());
        assertEquals(0x01, flyweigthRO.kind());
    }

    @Test
    public void shouldWrapAndReadAllValuesCaseUInt8() throws Exception
    {
        final int offset = 1;
        setAllTestValuesCaseUInt8(buffer, offset);
        flyweigthRO.wrap(buffer, offset, buffer.capacity());
        assertEquals(200, flyweigthRO.getAsUInt8());
        assertEquals(200, flyweigthRO.get());
        assertEquals(0x01, flyweigthRO.kind());
    }

    @Test
    public void shouldSetUInt32UsingSet()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .set(12345L)
            .build()
            .limit();
        flyweigthRO.wrap(buffer, 0, limit);
        assertEquals(12345L, flyweigthRO.getAsUInt32());
        assertEquals(12345L, flyweigthRO.get());
        assertEquals(0x04, flyweigthRO.kind());
    }

    @Test
    public void shouldSetUInt8UsingSet()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .set(200)
            .build()
            .limit();
        flyweigthRO.wrap(buffer, 0, limit);
        assertEquals(200, flyweigthRO.getAsUInt8());
        assertEquals(200, flyweigthRO.get());
        assertEquals(0x01, flyweigthRO.kind());
    }

    @Test
    public void shouldSetZeroUsingSet()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .set(0)
            .build()
            .limit();
        flyweigthRO.wrap(buffer, 0, limit);
        assertEquals(0, flyweigthRO.getAsZero());
        assertEquals(0, flyweigthRO.get());
        assertEquals(0x00, flyweigthRO.kind());
    }

    @Test
    public void shouldSetUInt32()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAsUInt32(12345L)
            .build()
            .limit();
        flyweigthRO.wrap(buffer, 0, limit);
        assertEquals(12345L, flyweigthRO.getAsUInt32());
        assertEquals(12345L, flyweigthRO.get());
        assertEquals(0x04, flyweigthRO.kind());
    }

    @Test
    public void shouldSetUInt8()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAsUInt8((short) 200)
            .build()
            .limit();
        flyweigthRO.wrap(buffer, 0, limit);
        assertEquals(200, flyweigthRO.getAsUInt8());
        assertEquals(200, flyweigthRO.get());
        assertEquals(0x01, flyweigthRO.kind());
    }

    @Test
    public void shouldSetZero()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAsZero()
            .build()
            .limit();
        flyweigthRO.wrap(buffer, 0, limit);
        assertEquals(0, flyweigthRO.getAsZero());
        assertEquals(0, flyweigthRO.get());
        assertEquals(0x00, flyweigthRO.kind());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUInt32WithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 18)
            .setAsUInt32(12345);
    }
}
