/**
 * Copyright 2016-2020 The Reaktivity Project
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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithUint32;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindOfUint32FW;

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
    private final int lengthSize = Integer.BYTES;
    private final int fieldCountSize = Integer.BYTES;

    private void setAllFields(
        MutableDirectBuffer buffer)
    {
        int length = 43;
        int fieldCount = 4;
        int offsetKind = 10;
        buffer.putByte(offsetKind, (byte) 1);
        int offsetLength = offsetKind + Byte.BYTES;
        buffer.putInt(offsetLength, length);
        int offsetFieldCount = offsetLength + lengthSize;
        buffer.putInt(offsetFieldCount, fieldCount);

        int offsetVariantOfString1Kind = offsetFieldCount + fieldCountSize;
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

    private StringFW createStringWithSpecifiedSize(
        int size)
    {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++)
        {
            builder.append('a');
        }
        return asStringFW(builder.toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        int length = 43;
        setAllFields(buffer);
        for (int maxLimit = 10; maxLimit <= length; maxLimit++)
        {
            variantOfListRO.wrap(buffer,  10, maxLimit);
        }
    }

    @Test
    public void shouldNotTryWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        int length = 43;
        int offsetLength = 10;
        setAllFields(buffer);
        for (int maxLimit = 10; maxLimit <= length; maxLimit++)
        {
            assertNull(variantOfListRO.tryWrap(buffer,  offsetLength, maxLimit));
        }
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int length = 43;
        int kindSize = Byte.BYTES;
        int lengthSize = Integer.BYTES;
        int fieldCount = 4;
        int offsetLength = 10;
        int maxLimit = offsetLength + kindSize + lengthSize + length;
        setAllFields(buffer);

        final VariantOfListFW variantOfList = variantOfListRO.wrap(buffer, offsetLength, maxLimit);

        assertSame(variantOfListRO, variantOfList);
        assertEquals(length, variantOfList.get().length());
        assertEquals(fieldCount, variantOfList.get().fieldCount());
        assertEquals(length - fieldCount, variantOfList.get().fields().capacity());
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int length = 43;
        int kindSize = Byte.BYTES;
        int lengthSize = Integer.BYTES;
        int fieldCount = 4;
        int offsetLength = 10;
        int maxLimit = offsetLength + kindSize + lengthSize + length;
        setAllFields(buffer);

        final VariantOfListFW variantOfList = variantOfListRO.wrap(buffer, offsetLength, maxLimit);

        assertSame(variantOfListRO, variantOfList);
        assertEquals(length, variantOfList.get().length());
        assertEquals(fieldCount, variantOfList.get().fieldCount());
        assertEquals(length - fieldCount, variantOfList.get().fields().capacity());
    }

    @Test
    public void shouldSetFieldsUsingSetAsList0Method() throws Exception
    {
        List0FW.Builder listRW = new List0FW.Builder()
            .wrap(buffer, 1, buffer.capacity());

        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .setAsList0(listRW.build())
            .build()
            .limit();

        final VariantOfListFW variantOfList = variantOfListRO.wrap(buffer,  0,  limit);

        assertEquals(EnumWithInt8.THREE, variantOfList.kind());
        assertEquals(0, variantOfList.get().length());
        assertEquals(0, variantOfList.get().fieldCount());
        assertEquals(1, variantOfList.limit());
        assertEquals(0, variantOfList.get().length());
        assertEquals(0, variantOfList.get().fieldCount());
    }

    @Test
    public void shouldSetFieldsUsingSetAsList8Method() throws Exception
    {
        VariantEnumKindOfStringFW.Builder field1RW = new VariantEnumKindOfStringFW.Builder();
        VariantEnumKindOfUint32FW.Builder field2RW = new VariantEnumKindOfUint32FW.Builder();
        ListFW.Builder listRW = new List8FW.Builder()
            .wrap(buffer, 1, buffer.capacity())
            .field((b, o, m) -> field1RW.wrap(b, o, m).set(asStringFW("string1")).build().sizeof())
            .field((b, o, m) -> field2RW.wrap(b, o, m).set(4000000000L).build().sizeof());

        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .setAsList8((ListFW) listRW.build())
            .build()
            .limit();

        final VariantOfListFW variantOfList = variantOfListRO.wrap(buffer,  0,  limit);

        assertEquals(EnumWithInt8.TWO, variantOfList.kind());
        assertEquals(18, variantOfList.get().length());
        assertEquals(2, variantOfList.get().fieldCount());
        assertEquals(20, variantOfList.limit());
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
        VariantEnumKindOfStringFW.Builder field1RW = new VariantEnumKindOfStringFW.Builder();
        VariantEnumKindOfUint32FW.Builder field2RW = new VariantEnumKindOfUint32FW.Builder();
        ListFW.Builder listRW = new List32FW.Builder()
            .wrap(buffer, 1, buffer.capacity())
            .field((b, o, m) -> field1RW.wrap(b, o, m).set(createStringWithSpecifiedSize(250)).build().sizeof())
            .field((b, o, m) -> field2RW.wrap(b, o, m).set(4000000000L).build().sizeof());

        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .setAsList32((ListFW) listRW.build())
            .build()
            .limit();

        final VariantOfListFW variantOfList = variantOfListRO.wrap(buffer,  0,  limit);

        assertEquals(EnumWithInt8.ONE, variantOfList.kind());
        assertEquals(264, variantOfList.get().length());
        assertEquals(2, variantOfList.get().fieldCount());
        assertEquals(269, variantOfList.limit());
    }

    @Test
    public void shouldSetFieldsUsingSetMethod() throws Exception
    {
        VariantEnumKindOfStringFW.Builder field1RW = new VariantEnumKindOfStringFW.Builder();
        VariantEnumKindOfUint32FW.Builder field2RW = new VariantEnumKindOfUint32FW.Builder();
        ListFW.Builder listRW = new List8FW.Builder()
            .wrap(buffer, 1, buffer.capacity())
            .field((b, o, m) -> field1RW.wrap(b, o, m).set(asStringFW("string1")).build().sizeof())
            .field((b, o, m) -> field2RW.wrap(b, o, m).set(4000000000L).build().sizeof());

        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .set((ListFW) listRW.build())
            .build()
            .limit();

        final VariantOfListFW variantOfList = variantOfListRO.wrap(buffer,  0,  limit);

        assertEquals(EnumWithInt8.TWO, variantOfList.kind());
        assertEquals(18, variantOfList.get().length());
        assertEquals(2, variantOfList.get().fieldCount());
        assertEquals(20, variantOfList.limit());
    }

    @Test
    public void shouldSetFieldsUsingFieldMethod() throws Exception
    {
        VariantEnumKindOfStringFW.Builder field1RW = new VariantEnumKindOfStringFW.Builder();
        VariantEnumKindOfUint32FW.Builder field2RW = new VariantEnumKindOfUint32FW.Builder();
        int limit = variantOfListRW.wrap(buffer, 0, buffer.capacity())
            .field((b, o, m) -> field1RW.wrap(b, o, m).set(asStringFW("string1")).build().sizeof())
            .field((b, o, m) -> field2RW.wrap(b, o, m).set(4000000000L).build().sizeof())
            .build()
            .limit();

        final VariantOfListFW variantOfList = variantOfListRO.wrap(buffer,  0,  limit);

        assertEquals(EnumWithInt8.TWO, variantOfList.kind());
        assertEquals(18, variantOfList.get().length());
        assertEquals(2, variantOfList.get().fieldCount());
        assertEquals(20, variantOfList.limit());
    }

    private static StringFW asStringFW(
        String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new String8FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}
