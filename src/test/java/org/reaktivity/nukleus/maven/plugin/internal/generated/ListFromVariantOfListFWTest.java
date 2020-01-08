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

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.String8FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithUint32;
import org.reaktivity.reaktor.internal.test.types.inner.ListFromVariantOfListFW;

public class ListFromVariantOfListFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final ListFromVariantOfListFW.Builder listFromVariantOfListRW = new ListFromVariantOfListFW.Builder();
    private final ListFromVariantOfListFW listFromVariantOfListRO = new ListFromVariantOfListFW();
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

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        int physicalLength = 43;
        setAllFields(buffer);
        for (int maxLimit = 10; maxLimit <= physicalLength; maxLimit++)
        {
            listFromVariantOfListRO.wrap(buffer,  10, maxLimit);
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
            assertNull(listFromVariantOfListRO.tryWrap(buffer,  offsetPhysicalLength, maxLimit));
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

        final ListFromVariantOfListFW listFromVariantOfList =
            listFromVariantOfListRO.wrap(buffer, offsetPhysicalLength, maxLimit);

        assertSame(listFromVariantOfListRO, listFromVariantOfList);
        assertEquals(physicalLength, listFromVariantOfList.length());
        assertEquals(logicalLength, listFromVariantOfList.fieldCount());
        assertEquals("string1", listFromVariantOfList.variantOfString1().asString());
        assertEquals("string2", listFromVariantOfList.variantOfString2().asString());
        assertEquals(4000000000L, listFromVariantOfList.variantOfUint());
        assertEquals(-2000000000, listFromVariantOfList.variantOfInt());
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

        final ListFromVariantOfListFW listFromVariantOfList =
            listFromVariantOfListRO.tryWrap(buffer, offsetPhysicalLength, maxLimit);

        assertSame(listFromVariantOfListRO, listFromVariantOfList);
        assertEquals(physicalLength, listFromVariantOfList.length());
        assertEquals(logicalLength, listFromVariantOfList.fieldCount());
        assertEquals("string1", listFromVariantOfList.variantOfString1().asString());
        assertEquals("string2", listFromVariantOfList.variantOfString2().asString());
        assertEquals(4000000000L, listFromVariantOfList.variantOfUint());
        assertEquals(-2000000000, listFromVariantOfList.variantOfInt());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WithInsufficientSpace() throws Exception
    {
        listFromVariantOfListRW.wrap(buffer, 10, 17)
            .variantOfString1(asStringFW("string1"));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailWhenFieldIsSetOutOfOrder() throws Exception
    {
        listFromVariantOfListRW.wrap(buffer, 0, buffer.capacity())
            .variantOfString1(asStringFW("string1"))
            .variantOfUint(4000000000L)
            .variantOfString2(asStringFW("string2"))
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailWhenSameFieldIsSetMoreThanOnce() throws Exception
    {
        listFromVariantOfListRW.wrap(buffer, 0, buffer.capacity())
            .variantOfString1(asStringFW("string1"))
            .variantOfString1(asStringFW("string2"))
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailWhenRequiredFieldIsNotSet() throws Exception
    {
        listFromVariantOfListRW.wrap(buffer, 0, buffer.capacity())
            .variantOfString2(asStringFW("string2"))
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldAssertErrorWhenValueNotPresent() throws Exception
    {
        int limit = listFromVariantOfListRW.wrap(buffer, 0, buffer.capacity())
            .variantOfString1(asStringFW("string1"))
            .build()
            .limit();

        final ListFromVariantOfListFW listFromVariantOfList = listFromVariantOfListRO.wrap(buffer, 0, limit);

        assertEquals("string2", listFromVariantOfList.variantOfString2().asString());
    }

    @Test
    public void shouldSetOnlyRequiredFields() throws Exception
    {
        int limit = listFromVariantOfListRW.wrap(buffer, 0, buffer.capacity())
            .variantOfString1(asStringFW("string1"))
            .build()
            .limit();

        final ListFromVariantOfListFW listFromVariantOfList = listFromVariantOfListRO.wrap(buffer, 0, limit);

        assertEquals("string1", listFromVariantOfList.variantOfString1().asString());
        assertEquals(4000000000L, listFromVariantOfList.variantOfUint());
    }

    @Test
    public void shouldSetSomeFields() throws Exception
    {
        int limit = listFromVariantOfListRW.wrap(buffer, 0, buffer.capacity())
            .variantOfString1(asStringFW("string1"))
            .variantOfUint(4000000000L)
            .build()
            .limit();

        final ListFromVariantOfListFW listFromVariantOfList = listFromVariantOfListRO.wrap(buffer, 0, limit);

        assertEquals("string1", listFromVariantOfList.variantOfString1().asString());
        assertEquals(4000000000L, listFromVariantOfList.variantOfUint());
    }

    @Test
    public void shouldSetAllFields() throws Exception
    {
        int limit = listFromVariantOfListRW.wrap(buffer, 0, buffer.capacity())
            .variantOfString1(asStringFW("string1"))
            .variantOfString2(asStringFW("string2"))
            .variantOfUint(4000000000L)
            .variantOfInt(-2000000000)
            .build()
            .limit();

        final ListFromVariantOfListFW listFromVariantOfList = listFromVariantOfListRO.wrap(buffer, 0, limit);

        assertEquals("string1", listFromVariantOfList.variantOfString1().asString());
        assertEquals("string2", listFromVariantOfList.variantOfString2().asString());
        assertEquals(4000000000L, listFromVariantOfList.variantOfUint());
        assertEquals(-2000000000, listFromVariantOfList.variantOfInt());
    }

    private static StringFW asStringFW(
        String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new String8FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}
