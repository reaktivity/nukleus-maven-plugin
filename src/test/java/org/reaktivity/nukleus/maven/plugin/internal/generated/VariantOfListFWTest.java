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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;

public class VariantOfListFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final VariantOfListFW.Builder variantOfListRW = new VariantOfListFW.Builder();
    private final VariantOfListFW variantOfListRO = new VariantOfListFW();
    private final int kindSize = Byte.BYTES;
    private final int physicalLengthSize = Integer.BYTES;
    private final int logicalLengthSize = Integer.BYTES;

    private void setPhysicalAndLogicalLength(
        MutableDirectBuffer buffer)
    {
        EnumWithInt8 kind = EnumWithInt8.ONE;
        int physicalLength = 276;
        int logicalLength = 10;
        int offsetKind = 10;
        int offsetPhysicalLength = offsetKind + kindSize;
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putByte(offsetKind, kind.value());
        buffer.putInt(offsetPhysicalLength, physicalLength);
        buffer.putInt(offsetLogicalLength, logicalLength);
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int physicalLength = 276;
        int logicalLength = 10;
        int maxSize = kindSize + physicalLengthSize + logicalLengthSize;
        int offsetPhysicalLength = 10;
        setPhysicalAndLogicalLength(buffer);
        assertSame(variantOfListRO, variantOfListRO.wrap(buffer, offsetPhysicalLength, offsetPhysicalLength + maxSize));
        assertEquals(physicalLength, variantOfListRO.get().physicalLength());
        assertEquals(logicalLength, variantOfListRO.get().logicalLength());
        assertEquals(offsetPhysicalLength + maxSize, variantOfListRO.limit());
    }

    @Test
    public void shouldSetAllFields() throws Exception
    {
        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .set(276, 10)
            .build()
            .limit();
        variantOfListRO.wrap(buffer,  0,  limit);
        assertEquals(EnumWithInt8.ONE, variantOfListRO.kind());
        assertEquals(276, variantOfListRO.get().physicalLength());
        assertEquals(10, variantOfListRO.get().logicalLength());
    }
}
