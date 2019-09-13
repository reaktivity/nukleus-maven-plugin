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
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.StringFW;

public class ListWithPhysicalAndLogicalLengthFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final ListWithPhysicalAndLogicalLengthFW.Builder flyweightRW = new ListWithPhysicalAndLogicalLengthFW.Builder();
    private final ListWithPhysicalAndLogicalLengthFW flyweightRO = new ListWithPhysicalAndLogicalLengthFW();
    private final StringFW.Builder stringRW = new StringFW.Builder();
    private final MutableDirectBuffer valueBuffer = new UnsafeBuffer(allocateDirect(100));
    private final int physicalLengthSize = Integer.BYTES;
    private final int logicalLengthSize = Integer.BYTES;
    private final int bitmaskSize = Integer.BYTES;

    @Test
    public void shouldNotTryWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        int physicalLength = 15;
        int offsetPhysicalLength = 10;
        buffer.putInt(offsetPhysicalLength, physicalLength);
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putInt(offsetLogicalLength, 1);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putInt(offsetBitMask, 1);
        int offsetField0 = offsetBitMask + bitmaskSize;
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("longValue", UTF_8)
            .build();
        buffer.putBytes(offsetField0, value.buffer(), 0, value.sizeof());

        for (int maxLimit=10; maxLimit <= physicalLength; maxLimit++)
        {
            assertNull(flyweightRO.tryWrap(buffer,  offsetPhysicalLength, maxLimit));
        }
    }

    @Test
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        int physicalLength = 19;
        int offsetPhysicalLength = 10;
        buffer.putInt(offsetPhysicalLength, physicalLength);
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putInt(offsetLogicalLength, 1);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putInt(offsetBitMask, 1);
        int offsetField0 = offsetBitMask + bitmaskSize;
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("longValue", UTF_8)
            .build();
        buffer.putBytes(offsetField0, value.buffer(), 0, value.sizeof());

        for (int maxLimit=10; maxLimit <= physicalLength; maxLimit++)
        {
            try
            {
                flyweightRO.wrap(buffer,  10, maxLimit);
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
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int physicalLength = 30;
        int offsetPhysicalLength = 10;
        buffer.putInt(offsetPhysicalLength, physicalLength);
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putInt(offsetLogicalLength, 3);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putInt(offsetBitMask, 7);
        int offsetField0 = offsetBitMask + bitmaskSize;
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value0", UTF_8)
            .build();
        buffer.putBytes(offsetField0, value.buffer(), 0, value.sizeof());
        int offsetField1 = offsetField0 + value.sizeof();
        buffer.putInt(offsetField1, 100);
        int offsetField2 = offsetField1 + Integer.BYTES;
        value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value2", UTF_8)
            .build();
        buffer.putBytes(offsetField2, value.buffer(), 0, value.sizeof());
        assertSame(flyweightRO, flyweightRO.tryWrap(buffer, offsetPhysicalLength, offsetPhysicalLength +
            physicalLength));
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int physicalLength = 30;
        int offsetPhysicalLength = 10;
        buffer.putInt(offsetPhysicalLength, physicalLength);
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putInt(offsetLogicalLength, 3);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putInt(offsetBitMask, 7);
        int offsetField0 = offsetBitMask + bitmaskSize;
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value0", UTF_8)
            .build();
        buffer.putBytes(offsetField0, value.buffer(), 0, value.sizeof());
        int offsetField1 = offsetField0 + value.sizeof();
        buffer.putInt(offsetField1, 100);
        int offsetField2 = offsetField1 + Integer.BYTES;
        value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value2", UTF_8)
            .build();
        buffer.putBytes(offsetField2, value.buffer(), 0, value.sizeof());
        assertSame(flyweightRO, flyweightRO.wrap(buffer, offsetPhysicalLength, offsetPhysicalLength + physicalLength));
    }

    @Test
    public void shouldWrapField0AndField1()
    {
        int physicalLength = 23;
        int logicalLength = 2;
        int bitMask = 3;
        buffer.putInt(0, physicalLength);
        int offsetLogicalLength = physicalLengthSize;
        buffer.putInt(offsetLogicalLength, logicalLength);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putInt(offsetBitMask, bitMask);
        int offsetField0 = offsetBitMask + bitmaskSize;
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value0", UTF_8)
            .build();
        buffer.putBytes(offsetField0, value.buffer(), 0, value.sizeof());
        int offsetField1 = offsetField0 + value.sizeof();
        buffer.putInt(offsetField1, 100);
        assertSame(flyweightRO, flyweightRO.wrap(buffer, 0, physicalLength));
    }

    @Test
    public void shouldWrapField0AndField2()
    {
        int physicalLength = 26;
        int logicalLength = 2;
        int bitMask = 5;
        buffer.putInt(0, physicalLength);
        int offsetLogicalLength = physicalLengthSize;
        buffer.putInt(offsetLogicalLength, logicalLength);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putInt(offsetBitMask, bitMask);
        int offsetField0 = offsetBitMask + bitmaskSize;
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value0", UTF_8)
            .build();
        buffer.putBytes(offsetField0, value.buffer(), 0, value.sizeof());
        int offsetField2 = offsetField0 + value.sizeof();
        value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value2", UTF_8)
            .build();
        buffer.putBytes(offsetField2, value.buffer(), 0, value.sizeof());
        assertSame(flyweightRO, flyweightRO.wrap(buffer, 0, physicalLength));
    }

    @Test
    public void shouldWrapField0()
    {
        int physicalLength = 19;
        int logicalLength = 1;
        int bitMask = 1;
        buffer.putInt(0, physicalLength);
        int offsetLogicalLength = physicalLengthSize;
        buffer.putInt(offsetLogicalLength, logicalLength);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putInt(offsetBitMask, bitMask);
        int offsetField0 = offsetBitMask + bitmaskSize;
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value0", UTF_8)
            .build();
        buffer.putBytes(offsetField0, value.buffer(), 0, value.sizeof());
        assertSame(flyweightRO, flyweightRO.wrap(buffer, 0, physicalLength));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotWrapWhenRequiredFieldIsNotSet() throws Exception
    {
        int physicalLength = 23;
        int logicalLength = 2;
        int bitMask = 6;
        buffer.putInt(0, physicalLength);
        int offsetLogicalLength = physicalLengthSize;
        buffer.putInt(offsetLogicalLength, logicalLength);
        int offsetBitMask = offsetLogicalLength + logicalLengthSize;
        buffer.putInt(offsetBitMask, bitMask);
        int offsetField1 = offsetBitMask + bitmaskSize;
        buffer.putInt(offsetField1, 100);
        int offsetField2 = offsetField1 + Integer.BYTES;
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value2", UTF_8)
            .build();
        buffer.putBytes(offsetField2, value.buffer(), 0, value.sizeof());
        flyweightRO.wrap(buffer, 0, physicalLength);
    }

    @Test
    public void shouldSetAllValues()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .field0("value0")
            .field1(100L)
            .field2("value2")
            .build()
            .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(30, flyweightRO.physicalLength());
        assertEquals(3, flyweightRO.logicalLength());
        assertEquals("value0", flyweightRO.field0().asString());
        assertEquals(100L, (long) flyweightRO.field1());
        assertEquals("value2", flyweightRO.field2().asString());
    }

    @Test
    public void shouldSetRequiredAndOptionalValues() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .field0("value0")
            .field2("value2")
            .build()
            .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(26, flyweightRO.physicalLength());
        assertEquals(2, flyweightRO.logicalLength());
        assertEquals("value0", flyweightRO.field0().asString());
        assertEquals("value2", flyweightRO.field2().asString());
        assertNull(flyweightRO.field1());
    }

    @Test
    public void shouldSetOnlyRequiredValues() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .field0("value0")
            .build()
            .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(19, flyweightRO.physicalLength());
        assertEquals(1, flyweightRO.logicalLength());
        assertEquals("value0", flyweightRO.field0().asString());
        assertNull(flyweightRO.field1());
        assertNull(flyweightRO.field2());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetField0WithInsufficientSpace()
    {
        int maxLimit = physicalLengthSize + logicalLengthSize + bitmaskSize;
        flyweightRW.wrap(buffer, 10, maxLimit)
            .field0("value0");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetField1WithInsufficientSpace()
    {
        String field0 = "value0";
        int maxLimit = physicalLengthSize + logicalLengthSize + bitmaskSize + field0.getBytes().length;
        flyweightRW.wrap(buffer, 10, maxLimit)
            .field0(field0)
            .field1(100);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetField2WithInsufficientSpace()
    {
        String field0 = "value0";
        int maxLimit = physicalLengthSize + logicalLengthSize + bitmaskSize + field0.getBytes().length + Integer.BYTES;
        flyweightRW.wrap(buffer, 10, maxLimit)
            .field0(field0)
            .field1(100)
            .field2("value2");
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetField1BeforeField0() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
            .field1(100)
            .build();
    }

    @Test
    public void shouldSetStringFieldsUsingStringFW()
    {
        ListWithPhysicalAndLogicalLengthFW.Builder builder = flyweightRW.wrap(buffer, 0, buffer.capacity());
        StringFW value0 = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value0", UTF_8)
            .build();
        builder.field0(value0);
        StringFW value2 = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
            .set("value2", UTF_8)
            .build();
        int limit =  builder.field2(value2)
            .build()
            .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(26, flyweightRO.physicalLength());
        assertEquals(2, flyweightRO.logicalLength());
        assertEquals("value0", flyweightRO.field0().asString());
        assertNull(flyweightRO.field1());
        assertEquals("value2", flyweightRO.field2().asString());
    }

    @Test
    public void shouldSetStringFieldsUsingBuffer()
    {
        valueBuffer.putStringWithoutLengthUtf8(0, "value0");
        valueBuffer.putStringWithoutLengthUtf8(6, "value2");
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .field0(valueBuffer, 0, 6)
            .field2(valueBuffer, 6, 6)
            .build()
            .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(26, flyweightRO.physicalLength());
        assertEquals(2, flyweightRO.logicalLength());
        assertEquals("value0", flyweightRO.field0().asString());
        assertNull(flyweightRO.field1());
        assertEquals("value2", flyweightRO.field2().asString());
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetField1WhenRequiredFieldIsNotSet() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
            .field1(100)
            .field2("value2")
            .build();
    }
}
