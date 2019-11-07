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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithUint32;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfUint32FW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithString32FW;

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

    private void setAllFields(
        MutableDirectBuffer buffer)
    {
        int physicalLength = 43;
        int logicalLength = 4;
        int offsetKind = 10;
        buffer.putByte(offsetKind, (byte) 1);
        int offsetPhysicalLength = offsetKind + Byte.BYTES;
        buffer.putInt(offsetPhysicalLength, physicalLength);
        int offsetLogicalLength = offsetPhysicalLength + physicalLengthSize;
        buffer.putInt(offsetLogicalLength, logicalLength);

        int offsetVariantOfString1Kind = offsetLogicalLength + logicalLengthSize;
        buffer.putByte(offsetVariantOfString1Kind, EnumWithInt8.ONE.value());
        int offsetVariantOfString1Length = offsetVariantOfString1Kind + Byte.BYTES;
        buffer.putByte(offsetVariantOfString1Length, (byte) "string1".length());
        int offsetVariantOfString1 = offsetVariantOfString1Length + Byte.BYTES;
        buffer.putBytes(offsetVariantOfString1, "string1".getBytes());

        int offsetVariantOfString2Kind = offsetVariantOfString1 + "string1".length();
        buffer.putByte(offsetVariantOfString2Kind, EnumWithInt8.ONE.value());
        int offsetVariantOfString2Length = offsetVariantOfString2Kind + Byte.BYTES;
        buffer.putByte(offsetVariantOfString2Length, (byte) "string2".length());
        int offsetVariantOfString2 = offsetVariantOfString2Length + Byte.BYTES;
        buffer.putBytes(offsetVariantOfString2, "string2".getBytes());

        int offsetVariantOfUintKind = offsetVariantOfString2 + "string2".length();
        buffer.putLong(offsetVariantOfUintKind, EnumWithUint32.NI.value());
        int offsetVariantOfUint = offsetVariantOfUintKind + Long.BYTES;
        buffer.putLong(offsetVariantOfUint, 4000000000L);

        int offsetVariantOfIntKind = offsetVariantOfUint + Long.BYTES;
        buffer.putByte(offsetVariantOfIntKind, EnumWithInt8.THREE.value());
        int offsetVariantOfInt = offsetVariantOfIntKind + Byte.BYTES;
        buffer.putInt(offsetVariantOfInt, -2000000000);
    }

    private String createStringWithSpecifiedSize(
        int size)
    {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++)
        {
            builder.append('a');
        }
        return builder.toString();
    }

    @Test
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        int physicalLength = 43;
        setAllFields(buffer);
        for (int maxLimit = 10; maxLimit <= physicalLength; maxLimit++)
        {
            try
            {
                variantOfListRO.wrap(buffer,  10, maxLimit);
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
        int physicalLength = 43;
        int offsetPhysicalLength = 10;
        setAllFields(buffer);
        for (int maxLimit = 10; maxLimit <= physicalLength; maxLimit++)
        {
            assertNull(variantOfListRO.tryWrap(buffer,  offsetPhysicalLength, maxLimit));
        }
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int physicalLength = 43;
        int kindSize = Byte.BYTES;
        int physicalLengthSize = Integer.BYTES;
        int logicalLength = 4;
        int offsetPhysicalLength = 10;
        int maxLimit = offsetPhysicalLength + kindSize + physicalLengthSize + physicalLength;
        setAllFields(buffer);

        assertSame(variantOfListRO, variantOfListRO.wrap(buffer, offsetPhysicalLength, maxLimit));
        assertEquals(physicalLength, variantOfListRO.physicalLength());
        assertEquals(logicalLength, variantOfListRO.logicalLength());
        assertEquals(physicalLength - logicalLength, variantOfListRO.fields().capacity());
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int physicalLength = 43;
        int kindSize = Byte.BYTES;
        int physicalLengthSize = Integer.BYTES;
        int logicalLength = 4;
        int offsetPhysicalLength = 10;
        int maxLimit = offsetPhysicalLength + kindSize + physicalLengthSize + physicalLength;
        setAllFields(buffer);

        assertSame(variantOfListRO, variantOfListRO.tryWrap(buffer, offsetPhysicalLength, maxLimit));
        assertEquals(physicalLength, variantOfListRO.physicalLength());
        assertEquals(logicalLength, variantOfListRO.logicalLength());
        assertEquals(physicalLength - logicalLength, variantOfListRO.fields().capacity());
    }

    @Test
    public void shouldSetFieldsUsingSetAsList0Method() throws Exception
    {
        ListFW.Builder listRW = new List0FW.Builder()
            .wrap(buffer, 1, buffer.capacity());

        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .setAsList0((ListFW) listRW.build())
            .build()
            .limit();
        variantOfListRO.wrap(buffer,  0,  limit);
        assertEquals(EnumWithInt8.THREE, variantOfListRO.kind());
        assertEquals(0, variantOfListRO.get().physicalLength());
        assertEquals(0, variantOfListRO.get().logicalLength());
        assertEquals(0, variantOfListRO.lengthSize());
        assertEquals(1, variantOfListRO.limit());
        assertEquals(0, variantOfListRO.physicalLength());
        assertEquals(0, variantOfListRO.logicalLength());
    }

    @Test
    public void shouldSetFieldsUsingSetAsList8Method() throws Exception
    {
        VariantEnumKindWithString32FW.Builder field1RW = new VariantEnumKindWithString32FW.Builder();
        VariantEnumKindOfUint32FW.Builder field2RW = new VariantEnumKindOfUint32FW.Builder();
        ListFW.Builder listRW = new List8FW.Builder()
            .wrap(buffer, 1, buffer.capacity())
            .field((b, o, m) -> field1RW.wrap(b, o, m).set("string1").build().sizeof())
            .field((b, o, m) -> field2RW.wrap(b, o, m).set(4000000000L).build().sizeof());

        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .setAsList8((ListFW) listRW.build())
            .build()
            .limit();
        variantOfListRO.wrap(buffer,  0,  limit);
        assertEquals(EnumWithInt8.TWO, variantOfListRO.kind());
        assertEquals(26, variantOfListRO.get().physicalLength());
        assertEquals(2, variantOfListRO.get().logicalLength());
        assertEquals(1, variantOfListRO.lengthSize());
        assertEquals(28, variantOfListRO.limit());
        assertEquals(26, variantOfListRO.physicalLength());
        assertEquals(2, variantOfListRO.logicalLength());
    }

    @Test
    public void shouldSetFieldsUsingSetAsList32Method() throws Exception
    {
        final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(300))
        {
            {
                // Make sure the code is not secretly relying upon memory being initialized to 0
                setMemory(0, capacity(), (byte) 0xab);
            }
        };
        VariantEnumKindWithString32FW.Builder field1RW = new VariantEnumKindWithString32FW.Builder();
        VariantEnumKindOfUint32FW.Builder field2RW = new VariantEnumKindOfUint32FW.Builder();
        ListFW.Builder listRW = new List32FW.Builder()
            .wrap(buffer, 1, buffer.capacity())
            .field((b, o, m) -> field1RW.wrap(b, o, m).set(createStringWithSpecifiedSize(250)).build().sizeof())
            .field((b, o, m) -> field2RW.wrap(b, o, m).set(4000000000L).build().sizeof());

        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .setAsList32((ListFW) listRW.build())
            .build()
            .limit();
        variantOfListRO.wrap(buffer,  0,  limit);
        assertEquals(EnumWithInt8.ONE, variantOfListRO.kind());
        assertEquals(272, variantOfListRO.get().physicalLength());
        assertEquals(2, variantOfListRO.get().logicalLength());
        assertEquals(4, variantOfListRO.lengthSize());
        assertEquals(277, variantOfListRO.limit());
        assertEquals(272, variantOfListRO.physicalLength());
        assertEquals(2, variantOfListRO.logicalLength());
    }

    @Test
    public void shouldSetFieldsUsingSetMethod() throws Exception
    {
        VariantEnumKindWithString32FW.Builder field1RW = new VariantEnumKindWithString32FW.Builder();
        VariantEnumKindOfUint32FW.Builder field2RW = new VariantEnumKindOfUint32FW.Builder();
        ListFW.Builder listRW = new List8FW.Builder()
            .wrap(buffer, 1, buffer.capacity())
            .field((b, o, m) -> field1RW.wrap(b, o, m).set("string1").build().sizeof())
            .field((b, o, m) -> field2RW.wrap(b, o, m).set(4000000000L).build().sizeof());

        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .set((ListFW) listRW.build())
            .build()
            .limit();
        variantOfListRO.wrap(buffer,  0,  limit);
        assertEquals(EnumWithInt8.TWO, variantOfListRO.kind());
        assertEquals(26, variantOfListRO.get().physicalLength());
        assertEquals(2, variantOfListRO.get().logicalLength());
        assertEquals(1, variantOfListRO.lengthSize());
        assertEquals(28, variantOfListRO.limit());
        assertEquals(26, variantOfListRO.physicalLength());
        assertEquals(2, variantOfListRO.logicalLength());
    }

    @Test
    public void shouldSetFieldsUsingFieldMethod() throws Exception
    {
        VariantEnumKindWithString32FW.Builder field1RW = new VariantEnumKindWithString32FW.Builder();
        VariantEnumKindOfUint32FW.Builder field2RW = new VariantEnumKindOfUint32FW.Builder();
        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .field((b, o, m) -> field1RW.wrap(b, o, m).set("string1").build().sizeof())
            .field((b, o, m) -> field2RW.wrap(b, o, m).set(4000000000L).build().sizeof())
            .build()
            .limit();
        variantOfListRO.wrap(buffer,  0,  limit);
        assertEquals(EnumWithInt8.TWO, variantOfListRO.kind());
        assertEquals(26, variantOfListRO.get().physicalLength());
        assertEquals(2, variantOfListRO.get().logicalLength());
        assertEquals(1, variantOfListRO.lengthSize());
        assertEquals(28, variantOfListRO.limit());
        assertEquals(26, variantOfListRO.physicalLength());
        assertEquals(2, variantOfListRO.logicalLength());
    }
}
