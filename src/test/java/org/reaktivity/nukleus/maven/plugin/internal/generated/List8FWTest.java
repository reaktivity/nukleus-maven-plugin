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

import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

public class List8FWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final List8FW.Builder list8RW = new List8FW.Builder();
    private final List8FW list8RO = new List8FW();
    private final int physicalLengthSize = Byte.BYTES;
    private final int logicalLengthSize = Byte.BYTES;

    private void setPhysicalAndLogicalLength(
        MutableDirectBuffer buffer)
    {
        int physicalLength = 19;
        int logicalLength = 5;
        int offsetPhysicalLength = 10;
        int offsetLogicalLength = offsetPhysicalLength + BitUtil.SIZE_OF_BYTE;
        buffer.putByte(offsetPhysicalLength, (byte) physicalLength);
        buffer.putByte(offsetLogicalLength, (byte) logicalLength);
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int physicalLength = 19;
        int logicalLength = 5;
        int maxSize = physicalLengthSize + logicalLengthSize;
        int offsetPhysicalLength = 10;
        setPhysicalAndLogicalLength(buffer);
        assertSame(list8RO, list8RO.wrap(buffer, offsetPhysicalLength, offsetPhysicalLength + maxSize));
        assertEquals(physicalLength, list8RO.physicalLength());
        assertEquals(logicalLength, list8RO.logicalLength());
        assertEquals(offsetPhysicalLength + maxSize, list8RO.limit());
    }

    @Test
    public void shouldSetPhysicalAndLogicalLength() throws Exception
    {
        int limit = list8RW.wrap(buffer, 0, buffer.capacity())
            .physicalLength(19)
            .logicalLength(5)
            .build()
            .limit();
        list8RO.wrap(buffer,  0,  limit);
        assertEquals(19, list8RO.physicalLength());
        assertEquals(5, list8RO.logicalLength());
        assertEquals(2, list8RO.limit());
    }
}
