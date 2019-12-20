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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.String16FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.VariantArray16FW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithString32FW;

public class VariantArray16FWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(150000))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final VariantArray16FW.Builder
        <VariantEnumKindWithString32FW.Builder, VariantEnumKindWithString32FW, EnumWithInt8, StringFW>
        flyweightRW = new VariantArray16FW.Builder<>(new VariantEnumKindWithString32FW.Builder(),
        new VariantEnumKindWithString32FW());
    private final VariantArray16FW<VariantEnumKindWithString32FW> flyweightRO =
        new VariantArray16FW<>(new VariantEnumKindWithString32FW());

    private final int lengthSize = Short.BYTES;
    private final int fieldCountSize = Short.BYTES;
    private final int arrayItemKindSize = Byte.BYTES;

    private int setAllItems(
        MutableDirectBuffer buffer,
        int offset)
    {
        String item1 = String.format("%1000s", "0");
        String item2 = String.format("%1000s", "1");
        int itemLengthSize = Short.BYTES;
        int physicalLength = fieldCountSize + arrayItemKindSize + itemLengthSize + item1.length() + itemLengthSize +
            item2.length();
        int logicalLength = 2;
        buffer.putShort(offset, (short) physicalLength);
        int offsetFieldCount = offset + lengthSize;
        buffer.putShort(offsetFieldCount, (short) logicalLength);

        int offsetArrayItemKind = offsetFieldCount + fieldCountSize;
        buffer.putByte(offsetArrayItemKind, EnumWithInt8.TWO.value());

        int offsetItem1Length = offsetArrayItemKind + Byte.BYTES;
        buffer.putShort(offsetItem1Length, (short) item1.length());
        int offsetItem1 = offsetItem1Length + itemLengthSize;
        buffer.putBytes(offsetItem1, item1.getBytes());

        int offsetItem2Length = offsetItem1 + item1.length();
        buffer.putShort(offsetItem2Length, (short) item2.length());
        int offsetItem2 = offsetItem2Length + itemLengthSize;
        buffer.putBytes(offsetItem2, item2.getBytes());

        return physicalLength + lengthSize;
    }

    static void assertAllTestValuesRead(
        VariantArray16FW<VariantEnumKindWithString32FW> flyweight,
        int offset)
    {
        String item1 = String.format("%1000s", "0");
        String item2 = String.format("%1000s", "1");
        List<String> arrayItems = new ArrayList<>();
        flyweight.forEach(v -> arrayItems.add(v.get().asString()));
        assertEquals(2, arrayItems.size());
        assertEquals(item1, arrayItems.get(0));
        assertEquals(item2, arrayItems.get(1));
        assertEquals(2007, flyweight.length());
        assertEquals(2, flyweight.fieldCount());
        assertEquals(offset + 2009, flyweight.limit());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int length = 18;
        setAllItems(buffer, offset);
        for (int maxLimit = offset; maxLimit <= length; maxLimit++)
        {
            flyweightRO.wrap(buffer, offset, maxLimit);
        }
    }

    @Test
    public void shouldNotTryWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int length = 18;
        setAllItems(buffer, offset);
        for (int maxLimit = offset; maxLimit <= length; maxLimit++)
        {
            assertNull(flyweightRO.tryWrap(buffer, offset, maxLimit));
        }
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int size = setAllItems(buffer, offset);
        final VariantArray16FW array = flyweightRO.wrap(buffer, offset, buffer.capacity());

        assertSame(flyweightRO, array);
        assertAllTestValuesRead(array, offset);
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int size = setAllItems(buffer, offset);
        final VariantArray16FW array = flyweightRO.tryWrap(buffer, offset, buffer.capacity());

        assertNotNull(array);
        assertSame(flyweightRO, array);
        assertAllTestValuesRead(array, offset);
    }

    @Test
    public void shouldWrapAndReadItems() throws Exception
    {
        final int offset = 10;
        int size = setAllItems(buffer, offset);
        final VariantArray16FW array = flyweightRO.wrap(buffer, offset, buffer.capacity());
        assertEquals(offset + size, array.limit());

        assertAllTestValuesRead(array, offset);
    }

    @Test
    public void shouldReadEmptyList() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .build()
            .limit();

        final VariantArray16FW<VariantEnumKindWithString32FW> array = flyweightRO.wrap(buffer,  0,  limit);

        List<String> arrayItems = new ArrayList<>();
        array.forEach(v -> arrayItems.add(v.get().asString()));

        assertEquals(lengthSize + fieldCountSize, array.limit());
        assertEquals(0, arrayItems.size());
    }

    @Test
    public void shouldSetItemsUsingItemMethod() throws Exception
    {
        String item1 = String.format("%1000s", "0");
        String item2 = String.format("%1000s", "1");
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .item(asStringFW(item1))
            .item(asStringFW(item2))
            .build()
            .limit();

        final VariantArray16FW array = flyweightRO.wrap(buffer,  0,  limit);

        assertAllTestValuesRead(array, 0);
    }

    private static StringFW asStringFW(
        String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Integer.SIZE + value.length()));
        return new String16FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}
