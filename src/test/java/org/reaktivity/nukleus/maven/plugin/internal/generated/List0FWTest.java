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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

public class List0FWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final List0FW.Builder list0RW = new List0FW.Builder();
    private final List0FW list0RO = new List0FW();

    @Test
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        int physicalLength = 0;
        for (int maxLimit = 0; maxLimit <= physicalLength; maxLimit++)
        {
            try
            {
                list0RO.wrap(buffer,  10, maxLimit);
                fail("Exception not thrown");
            }
            catch (Exception e)
            {
                if (!(e instanceof IndexOutOfBoundsException))
                {
                    fail("Unexpected exception " + e);
                }
            }
        }
    }

    @Test
    public void shouldNotTryWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        int physicalLength = 0;
        int offsetPhysicalLength = 10;
        for (int maxLimit = 0; maxLimit <= physicalLength; maxLimit++)
        {
            assertNull(list0RO.tryWrap(buffer,  offsetPhysicalLength, maxLimit));
        }
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int physicalLength = 0;
        int logicalLength = 0;
        int offsetPhysicalLength = 10;
        int maxLimit = offsetPhysicalLength + physicalLength;

        assertSame(list0RO, list0RO.wrap(buffer, offsetPhysicalLength, maxLimit));
        assertEquals(physicalLength, list0RO.physicalLength());
        assertEquals(logicalLength, list0RO.logicalLength());
        assertEquals(0, list0RO.fields().capacity());
        assertEquals(maxLimit, list0RO.limit());
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int physicalLength = 0;
        int logicalLength = 0;
        int offsetPhysicalLength = 10;
        int maxLimit = offsetPhysicalLength + physicalLength;

        assertSame(list0RO, list0RO.tryWrap(buffer, offsetPhysicalLength, maxLimit));
        assertEquals(physicalLength, list0RO.physicalLength());
        assertEquals(logicalLength, list0RO.logicalLength());
        assertEquals(0, list0RO.fields().capacity());
        assertEquals(maxLimit, list0RO.limit());
    }

    @Test
    public void shouldBuild() throws Exception
    {
        int limit = list0RW.wrap(buffer, 0, buffer.capacity())
            .build()
            .limit();
        list0RO.wrap(buffer,  0,  limit);
        assertEquals(0, list0RO.physicalLength());
        assertEquals(0, list0RO.logicalLength());
        assertEquals(0, list0RO.lengthSize());
        assertEquals(0, list0RO.limit());
    }
}
