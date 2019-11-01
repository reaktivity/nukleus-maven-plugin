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
import static org.junit.Assert.assertSame;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

public class List32FWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final List32FW.Builder list32RW = new List32FW.Builder();
    private final List32FW list32RO = new List32FW();
    private final int physicalLengthSize = Integer.BYTES;
    private final int logicalLengthSize = Integer.BYTES;

    private void setPhysicalAndLogicalLength(
        MutableDirectBuffer buffer)
    {
        int physicalLength = 276;
        int logicalLength = 10;
        int offsetPhysicalLength = 10;
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putInt(offsetPhysicalLength, physicalLength);
        buffer.putInt(offsetLogicalLength, logicalLength);
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int physicalLength = 276;
        int logicalLength = 10;
        int maxSize = physicalLengthSize + logicalLengthSize;
        int offsetPhysicalLength = 10;
        setPhysicalAndLogicalLength(buffer);
        assertSame(list32RO, list32RO.wrap(buffer, offsetPhysicalLength, offsetPhysicalLength + maxSize));
        assertEquals(physicalLength, list32RO.physicalLength());
        assertEquals(logicalLength, list32RO.logicalLength());
        assertEquals(offsetPhysicalLength + maxSize, list32RO.limit());
    }

    @Test
    public void shouldSetPhysicalAndLogicalLength() throws Exception
    {
        int limit = list32RW.wrap(buffer, 0, buffer.capacity())
            .physicalLength(276)
            .logicalLength(10)
            .build()
            .limit();
        list32RO.wrap(buffer,  0,  limit);
        assertEquals(276, list32RO.physicalLength());
        assertEquals(10, list32RO.logicalLength());
        assertEquals(8, list32RO.limit());
    }
}
