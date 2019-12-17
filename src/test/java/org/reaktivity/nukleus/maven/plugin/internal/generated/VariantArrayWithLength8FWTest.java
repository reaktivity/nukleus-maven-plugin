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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;

public class VariantArrayWithLength8FWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final VariantArrayWithLength8FW.Builder flyweightRW = new VariantArrayWithLength8FW.Builder();
    private final VariantArrayWithLength8FW flyweightRO = new VariantArrayWithLength8FW();
    private final int lengthSize = Byte.BYTES;
    private final int fieldCountSize = Byte.BYTES;

    private int setAllItems(
        MutableDirectBuffer buffer,
        int offset)
    {
        int physicalLength = 18;
        int logicalLength = 2;
        buffer.putByte(offset, (byte) physicalLength);
        int offsetFieldCount = offset + lengthSize;
        buffer.putByte(offsetFieldCount, (byte) logicalLength);

        int offsetArrayItemKind = offsetFieldCount + fieldCountSize;
        buffer.putByte(offsetArrayItemKind, EnumWithInt8.ONE.value());

        int offsetItem1Length = offsetArrayItemKind + Byte.BYTES;
        buffer.putByte(offsetItem1Length, (byte) "symbolA".length());
        int offsetItem1 = offsetItem1Length + Byte.BYTES;
        buffer.putBytes(offsetItem1, "symbolA".getBytes());

        int offsetItem2Length = offsetItem1 + "symbolA".length();
        buffer.putByte(offsetItem2Length, (byte) "symbolB".length());
        int offsetItem2 = offsetItem2Length + Byte.BYTES;
        buffer.putBytes(offsetItem2, "symbolB".getBytes());

        return physicalLength + lengthSize;
    }

    static void assertAllTestValuesRead(
        VariantArrayWithLength8FW flyweight,
        int offset)
    {
        List<String> arrayItems = new ArrayList<>();
        flyweight.forEach(v -> arrayItems.add(v.get().asString()));
        assertEquals(2, arrayItems.size());
        assertEquals("symbolA", arrayItems.get(0));
        assertEquals("symbolB", arrayItems.get(1));
        assertEquals(18, flyweight.length());
        assertEquals(2, flyweight.fieldCount());
        assertEquals(offset + 19, flyweight.limit());
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
        final VariantArrayWithLength8FW array = flyweightRO.wrap(buffer, offset, buffer.capacity());

        assertSame(flyweightRO, array);
        assertAllTestValuesRead(array, offset);
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int size = setAllItems(buffer, offset);
        final VariantArrayWithLength8FW array = flyweightRO.tryWrap(buffer, offset, buffer.capacity());

        assertNotNull(array);
        assertSame(flyweightRO, array);
        assertAllTestValuesRead(array, offset);
    }

    @Test
    public void shouldWrapAndReadItems() throws Exception
    {
        final int offset = 10;
        int size = setAllItems(buffer, offset);
        final VariantArrayWithLength8FW array = flyweightRO.wrap(buffer, offset, buffer.capacity());
        assertEquals(offset + size, array.limit());

        assertAllTestValuesRead(array, offset);
    }

    @Test
    public void shouldReadEmptyList() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .build()
            .limit();

        final VariantArrayWithLength8FW array = flyweightRO.wrap(buffer,  0,  limit);

        List<String> arrayItems = new ArrayList<>();
        array.forEach(v -> arrayItems.add(v.get().asString()));

        assertEquals(2, array.limit());
        assertEquals(0, arrayItems.size());
    }

    @Test
    public void shouldSetItemsUsingItemMethod() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .item(asStringFW("symbolA"))
            .item(asStringFW("symbolB"))
            .build()
            .limit();

        final VariantArrayWithLength8FW array = flyweightRO.wrap(buffer,  0,  limit);

        assertAllTestValuesRead(array, 0);
    }

    private static StringFW asStringFW(
        String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new String8FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}
