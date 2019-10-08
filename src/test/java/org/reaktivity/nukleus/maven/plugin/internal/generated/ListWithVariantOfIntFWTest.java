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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt16;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;

public class ListWithVariantOfIntFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final ListWithVariantOfIntFW.Builder listWithVariantOfIntRW = new ListWithVariantOfIntFW.Builder();
    private final ListWithVariantOfIntFW listWithVariantOfIntRO = new ListWithVariantOfIntFW();
    private final int physicalLengthSize = Byte.BYTES;
    private final int logicalLengthSize = Byte.BYTES;
    private final int bitmaskSize = Long.BYTES;

    @Test
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        byte physicalLength = 27;
        byte logicalLength = 6;
        long bitmask = 0x3F;
        int offsetPhysicalLength = 10;
        buffer.putByte(offsetPhysicalLength, physicalLength);
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putByte(offsetLogicalLength, logicalLength);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putLong(offsetBitMask, bitmask);

        int offsetIntField1 = offsetBitMask + bitmaskSize;
        buffer.putByte(offsetIntField1, (byte) 1);
        int offsetVariantOfInt64Uint8Kind = offsetIntField1 + Byte.BYTES;
        buffer.putByte(offsetVariantOfInt64Uint8Kind, (byte) 113);
        int offsetVariantOfInt64 = offsetVariantOfInt64Uint8Kind + Byte.BYTES;
        buffer.putInt(offsetVariantOfInt64, 100000);
        int offsetVariantOfInt8EnumKind = offsetVariantOfInt64 + Integer.BYTES;
        buffer.putByte(offsetVariantOfInt8EnumKind, EnumWithInt8.ONE.value());
        int offsetVariantOfInt8 = offsetVariantOfInt8EnumKind + Byte.BYTES;
        buffer.putByte(offsetVariantOfInt8, (byte) 100);
        int offsetIntField2 = offsetVariantOfInt8 + Byte.BYTES;
        buffer.putShort(offsetIntField2, (short) 30000);
        int offsetVariantOfInt16EnumKind = offsetIntField2 + Short.BYTES;
        buffer.putShort(offsetVariantOfInt16EnumKind, EnumWithInt16.THREE.value());
        int offsetVariantOfInt16 = offsetVariantOfInt16EnumKind + Short.BYTES;
        buffer.putShort(offsetVariantOfInt16, (short) 2000);
        int offsetVariantOfInt32EnumKind = offsetVariantOfInt16 + Short.BYTES;
        buffer.putByte(offsetVariantOfInt32EnumKind, EnumWithInt8.TWO.value());
        int offsetVariantOfInt32 = offsetVariantOfInt32EnumKind + Byte.BYTES;
        buffer.putShort(offsetVariantOfInt32, (short) -500);

        for (int maxLimit = 10; maxLimit <= physicalLength; maxLimit++)
        {
            try
            {
                listWithVariantOfIntRO.wrap(buffer,  10, maxLimit);
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
        byte physicalLength = 27;
        byte logicalLength = 6;
        long bitmask = 0x3F;
        int offsetPhysicalLength = 10;
        buffer.putByte(offsetPhysicalLength, physicalLength);
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putByte(offsetLogicalLength, logicalLength);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putLong(offsetBitMask, bitmask);

        int offsetIntField1 = offsetBitMask + bitmaskSize;
        buffer.putByte(offsetIntField1, (byte) 1);
        int offsetVariantOfInt64Uint8Kind = offsetIntField1 + Byte.BYTES;
        buffer.putByte(offsetVariantOfInt64Uint8Kind, (byte) 113);
        int offsetVariantOfInt64 = offsetVariantOfInt64Uint8Kind + Byte.BYTES;
        buffer.putInt(offsetVariantOfInt64, 100000);
        int offsetVariantOfInt8EnumKind = offsetVariantOfInt64 + Integer.BYTES;
        buffer.putByte(offsetVariantOfInt8EnumKind, EnumWithInt8.ONE.value());
        int offsetVariantOfInt8 = offsetVariantOfInt8EnumKind + Byte.BYTES;
        buffer.putByte(offsetVariantOfInt8, (byte) 100);
        int offsetIntField2 = offsetVariantOfInt8 + Byte.BYTES;
        buffer.putShort(offsetIntField2, (short) 30000);
        int offsetVariantOfInt16EnumKind = offsetIntField2 + Short.BYTES;
        buffer.putShort(offsetVariantOfInt16EnumKind, EnumWithInt16.THREE.value());
        int offsetVariantOfInt16 = offsetVariantOfInt16EnumKind + Short.BYTES;
        buffer.putShort(offsetVariantOfInt16, (short) 2000);
        int offsetVariantOfInt32EnumKind = offsetVariantOfInt16 + Short.BYTES;
        buffer.putByte(offsetVariantOfInt32EnumKind, EnumWithInt8.TWO.value());
        int offsetVariantOfInt32 = offsetVariantOfInt32EnumKind + Byte.BYTES;
        buffer.putShort(offsetVariantOfInt32, (short) -500);

        for (int maxLimit = 10; maxLimit <= physicalLength; maxLimit++)
        {
            assertNull(listWithVariantOfIntRO.tryWrap(buffer,  offsetPhysicalLength, maxLimit));
        }
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        byte physicalLength = 27;
        byte logicalLength = 6;
        long bitmask = 0x3F;
        int offsetPhysicalLength = 10;
        buffer.putByte(offsetPhysicalLength, physicalLength);
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putByte(offsetLogicalLength, logicalLength);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putLong(offsetBitMask, bitmask);

        int offsetIntField1 = offsetBitMask + bitmaskSize;
        buffer.putByte(offsetIntField1, (byte) 1);
        int offsetVariantOfInt64Uint8Kind = offsetIntField1 + Byte.BYTES;
        buffer.putByte(offsetVariantOfInt64Uint8Kind, (byte) 113);
        int offsetVariantOfInt64 = offsetVariantOfInt64Uint8Kind + Byte.BYTES;
        buffer.putInt(offsetVariantOfInt64, 100000);
        int offsetVariantOfInt8EnumKind = offsetVariantOfInt64 + Integer.BYTES;
        buffer.putByte(offsetVariantOfInt8EnumKind, EnumWithInt8.ONE.value());
        int offsetVariantOfInt8 = offsetVariantOfInt8EnumKind + Byte.BYTES;
        buffer.putByte(offsetVariantOfInt8, (byte) 100);
        int offsetIntField2 = offsetVariantOfInt8 + Byte.BYTES;
        buffer.putShort(offsetIntField2, (short) 30000);
        int offsetVariantOfInt16EnumKind = offsetIntField2 + Short.BYTES;
        buffer.putShort(offsetVariantOfInt16EnumKind, EnumWithInt16.THREE.value());
        int offsetVariantOfInt16 = offsetVariantOfInt16EnumKind + Short.BYTES;
        buffer.putShort(offsetVariantOfInt16, (short) 2000);
        int offsetVariantOfInt32EnumKind = offsetVariantOfInt16 + Short.BYTES;
        buffer.putByte(offsetVariantOfInt32EnumKind, EnumWithInt8.TWO.value());
        int offsetVariantOfInt32 = offsetVariantOfInt32EnumKind + Byte.BYTES;
        buffer.putShort(offsetVariantOfInt32, (short) -500);

        assertSame(listWithVariantOfIntRO, listWithVariantOfIntRO.wrap(buffer, offsetPhysicalLength,
            offsetPhysicalLength + physicalLength));
        assertEquals(physicalLength, listWithVariantOfIntRO.limit() - offsetPhysicalLength);
        assertEquals(logicalLength, listWithVariantOfIntRO.length());
        assertEquals(bitmask, listWithVariantOfIntRO.bitmask());
        assertEquals(1, listWithVariantOfIntRO.intField1());
        assertEquals(100000, listWithVariantOfIntRO.variantOfInt64Uint8Kind().get());
        assertEquals(EnumWithInt8.ONE, listWithVariantOfIntRO.variantOfInt8EnumKind().kind());
        assertEquals(100, listWithVariantOfIntRO.variantOfInt8EnumKind().get());
        assertEquals(30000, listWithVariantOfIntRO.intField2());
        assertEquals(EnumWithInt16.THREE, listWithVariantOfIntRO.variantOfInt16EnumKind().kind());
        assertEquals(2000, listWithVariantOfIntRO.variantOfInt16EnumKind().get());
        assertEquals(EnumWithInt8.TWO, listWithVariantOfIntRO.variantOfInt32EnumKind().kind());
        assertEquals(-500, listWithVariantOfIntRO.variantOfInt32EnumKind().get());
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        byte physicalLength = 27;
        byte logicalLength = 6;
        long bitmask = 0x3F;
        int offsetPhysicalLength = 10;
        buffer.putByte(offsetPhysicalLength, physicalLength);
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putByte(offsetLogicalLength, logicalLength);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putLong(offsetBitMask, bitmask);

        int offsetIntField1 = offsetBitMask + bitmaskSize;
        buffer.putByte(offsetIntField1, (byte) 1);
        int offsetVariantOfInt64Uint8Kind = offsetIntField1 + Byte.BYTES;
        buffer.putByte(offsetVariantOfInt64Uint8Kind, (byte) 113);
        int offsetVariantOfInt64 = offsetVariantOfInt64Uint8Kind + Byte.BYTES;
        buffer.putInt(offsetVariantOfInt64, 100000);
        int offsetVariantOfInt8EnumKind = offsetVariantOfInt64 + Integer.BYTES;
        buffer.putByte(offsetVariantOfInt8EnumKind, EnumWithInt8.ONE.value());
        int offsetVariantOfInt8 = offsetVariantOfInt8EnumKind + Byte.BYTES;
        buffer.putByte(offsetVariantOfInt8, (byte) 100);
        int offsetIntField2 = offsetVariantOfInt8 + Byte.BYTES;
        buffer.putShort(offsetIntField2, (short) 30000);
        int offsetVariantOfInt16EnumKind = offsetIntField2 + Short.BYTES;
        buffer.putShort(offsetVariantOfInt16EnumKind, EnumWithInt16.THREE.value());
        int offsetVariantOfInt16 = offsetVariantOfInt16EnumKind + Short.BYTES;
        buffer.putShort(offsetVariantOfInt16, (short) 2000);
        int offsetVariantOfInt32EnumKind = offsetVariantOfInt16 + Short.BYTES;
        buffer.putByte(offsetVariantOfInt32EnumKind, EnumWithInt8.TWO.value());
        int offsetVariantOfInt32 = offsetVariantOfInt32EnumKind + Byte.BYTES;
        buffer.putShort(offsetVariantOfInt32, (short) -500);

        assertSame(listWithVariantOfIntRO, listWithVariantOfIntRO.tryWrap(buffer, offsetPhysicalLength,
            offsetPhysicalLength + physicalLength));
        assertEquals(physicalLength, listWithVariantOfIntRO.limit() - offsetPhysicalLength);
        assertEquals(logicalLength, listWithVariantOfIntRO.length());
        assertEquals(bitmask, listWithVariantOfIntRO.bitmask());
        assertEquals(1, listWithVariantOfIntRO.intField1());
        assertEquals(100000, listWithVariantOfIntRO.variantOfInt64Uint8Kind().get());
        assertEquals(EnumWithInt8.ONE, listWithVariantOfIntRO.variantOfInt8EnumKind().kind());
        assertEquals(100, listWithVariantOfIntRO.variantOfInt8EnumKind().get());
        assertEquals(30000, listWithVariantOfIntRO.intField2());
        assertEquals(EnumWithInt16.THREE, listWithVariantOfIntRO.variantOfInt16EnumKind().kind());
        assertEquals(2000, listWithVariantOfIntRO.variantOfInt16EnumKind().get());
        assertEquals(EnumWithInt8.TWO, listWithVariantOfIntRO.variantOfInt32EnumKind().kind());
        assertEquals(-500, listWithVariantOfIntRO.variantOfInt32EnumKind().get());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WithInsufficientSpace()
    {
        listWithVariantOfIntRW.wrap(buffer, 10, 18)
            .variantOfInt64Uint8Kind(100000L);
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetSameFieldTwice() throws Exception
    {
        listWithVariantOfIntRW.wrap(buffer, 0, buffer.capacity())
            .variantOfInt64Uint8Kind(100000L)
            .variantOfInt64Uint8Kind(100000L)
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetFieldsInWrongOrder() throws Exception
    {
        listWithVariantOfIntRW.wrap(buffer, 0, buffer.capacity())
            .variantOfInt64Uint8Kind(1000000L)
            .intField1((byte) 5)
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldAssertErrorWhenAccessValueNotPresent() throws Exception
    {
        int limit = listWithVariantOfIntRW.wrap(buffer, 0, buffer.capacity())
            .variantOfInt64Uint8Kind(100000L)
            .variantOfInt8EnumKind(100)
            .variantOfInt16EnumKind(2000)
            .variantOfInt32EnumKind(-500)
            .build()
            .limit();
        listWithVariantOfIntRO.wrap(buffer,  0,  limit);
        assertEquals(5, listWithVariantOfIntRO.intField1());
    }

    @Test
    public void shouldSetSomeValues() throws Exception
    {
        int limit = listWithVariantOfIntRW.wrap(buffer, 0, buffer.capacity())
            .variantOfInt64Uint8Kind(100000L)
            .variantOfInt8EnumKind(100)
            .variantOfInt16EnumKind(2000)
            .variantOfInt32EnumKind(-500)
            .build()
            .limit();
        listWithVariantOfIntRO.wrap(buffer,  0,  limit);
        assertEquals(100000L, listWithVariantOfIntRO.variantOfInt64Uint8Kind().get());
        assertEquals(100, listWithVariantOfIntRO.variantOfInt8EnumKind().get());
        assertEquals(EnumWithInt8.ONE, listWithVariantOfIntRO.variantOfInt8EnumKind().kind());
        assertEquals(2000, listWithVariantOfIntRO.variantOfInt16EnumKind().get());
        assertEquals(EnumWithInt16.THREE, listWithVariantOfIntRO.variantOfInt16EnumKind().kind());
        assertEquals(-500, listWithVariantOfIntRO.variantOfInt32EnumKind().get());
        assertEquals(EnumWithInt8.TWO, listWithVariantOfIntRO.variantOfInt32EnumKind().kind());
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        int limit = listWithVariantOfIntRW.wrap(buffer, 0, buffer.capacity())
            .intField1((byte) 5)
            .variantOfInt64Uint8Kind(100000L)
            .variantOfInt8EnumKind(100)
            .intField2((short) 30000)
            .variantOfInt16EnumKind(2000)
            .variantOfInt32EnumKind(-500)
            .build()
            .limit();
        listWithVariantOfIntRO.wrap(buffer,  0,  limit);
        assertEquals(5, listWithVariantOfIntRO.intField1());
        assertEquals(100000L, listWithVariantOfIntRO.variantOfInt64Uint8Kind().get());
        assertEquals(100, listWithVariantOfIntRO.variantOfInt8EnumKind().get());
        assertEquals(EnumWithInt8.ONE, listWithVariantOfIntRO.variantOfInt8EnumKind().kind());
        assertEquals(30000, listWithVariantOfIntRO.intField2());
        assertEquals(2000, listWithVariantOfIntRO.variantOfInt16EnumKind().get());
        assertEquals(EnumWithInt16.THREE, listWithVariantOfIntRO.variantOfInt16EnumKind().kind());
        assertEquals(-500, listWithVariantOfIntRO.variantOfInt32EnumKind().get());
        assertEquals(EnumWithInt8.TWO, listWithVariantOfIntRO.variantOfInt32EnumKind().kind());
    }
}
