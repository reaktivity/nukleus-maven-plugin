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
import org.reaktivity.reaktor.internal.test.types.String8FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.TypedefStringFW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithString32FW;

public class Map16FWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final Map16FW.Builder<TypedefStringFW.Builder, TypedefStringFW, EnumWithInt8, StringFW,
        VariantEnumKindWithString32FW.Builder, VariantEnumKindWithString32FW, EnumWithInt8, StringFW>
        flyweightRW = new Map16FW.Builder<>(new TypedefStringFW.Builder(), new TypedefStringFW(),
        new VariantEnumKindWithString32FW.Builder(), new VariantEnumKindWithString32FW());

    private final Map16FW<TypedefStringFW, VariantEnumKindWithString32FW> flyweightRO =
        new Map16FW<>(new TypedefStringFW(), new VariantEnumKindWithString32FW());

    private final int lengthSize = Short.BYTES;
    private final int fieldCountSize = Short.BYTES;

    private int setAllItems(
        MutableDirectBuffer buffer,
        int offset)
    {
        int length = 50;
        int fieldCount = 4;
        String entry1Key = "entry1Key";
        String entry1Value = "entry1Value";
        String entry2Key = "entry2Key";
        String entry2Value = "entry2Value";

        buffer.putShort(offset, (short) length);
        int offsetFieldCount = offset + lengthSize;
        buffer.putShort(offsetFieldCount, (short) fieldCount);

        int offsetMapEntry1KeyKind = offsetFieldCount + fieldCountSize;
        buffer.putByte(offsetMapEntry1KeyKind, EnumWithInt8.ONE.value());
        int offsetEntry1KeyLength = offsetMapEntry1KeyKind + Byte.BYTES;
        buffer.putByte(offsetEntry1KeyLength, (byte) entry1Key.length());
        int offsetEntry1Key = offsetEntry1KeyLength + Byte.BYTES;
        buffer.putBytes(offsetEntry1Key, entry1Key.getBytes());

        int offsetMapEntry1ValueKind = offsetEntry1Key + entry1Key.length();
        buffer.putByte(offsetMapEntry1ValueKind, EnumWithInt8.ONE.value());
        int offsetEntry1ValueLength = offsetMapEntry1ValueKind + Byte.BYTES;
        buffer.putByte(offsetEntry1ValueLength, (byte) entry1Value.length());
        int offsetEntry1Value = offsetEntry1ValueLength + Byte.BYTES;
        buffer.putBytes(offsetEntry1Value, entry1Value.getBytes());

        int offsetMapEntry2KeyKind = offsetEntry1Value + entry1Value.length();
        buffer.putByte(offsetMapEntry2KeyKind, EnumWithInt8.ONE.value());
        int offsetEntry2KeyLength = offsetMapEntry2KeyKind + Byte.BYTES;
        buffer.putByte(offsetEntry2KeyLength, (byte) entry2Key.length());
        int offsetEntry2Key = offsetEntry2KeyLength + Byte.BYTES;
        buffer.putBytes(offsetEntry2Key, entry2Key.getBytes());

        int offsetMapEntry2ValueKind = offsetEntry2Key + entry2Key.length();
        buffer.putByte(offsetMapEntry2ValueKind, EnumWithInt8.ONE.value());
        int offsetEntry2ValueLength = offsetMapEntry2ValueKind + Byte.BYTES;
        buffer.putByte(offsetEntry2ValueLength, (byte) entry2Value.length());
        int offsetEntry2Value = offsetEntry2ValueLength + Byte.BYTES;
        buffer.putBytes(offsetEntry2Value, entry2Value.getBytes());

        return length + lengthSize;
    }

    static void assertAllTestValuesRead(
        Map16FW<TypedefStringFW, VariantEnumKindWithString32FW> flyweight,
        int offset)
    {
        List<String> mapItems = new ArrayList<>();
        flyweight.forEach(kv -> vv ->
        {
            mapItems.add(kv.get().asString());
            mapItems.add(vv.get().asString());
        });
        assertEquals(4, mapItems.size());
        assertEquals("entry1Key", mapItems.get(0));
        assertEquals("entry1Value", mapItems.get(1));
        assertEquals("entry2Key", mapItems.get(2));
        assertEquals("entry2Value", mapItems.get(3));
        assertEquals(50, flyweight.length());
        assertEquals(4, flyweight.fieldCount());
        assertEquals(offset + 52, flyweight.limit());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int length = 50;
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
        int length = 50;
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
        Map16FW<TypedefStringFW, VariantEnumKindWithString32FW> map = flyweightRO.wrap(buffer, offset, buffer.capacity());

        assertSame(flyweightRO, map);
        assertAllTestValuesRead(map, offset);
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int size = setAllItems(buffer, offset);
        Map16FW<TypedefStringFW, VariantEnumKindWithString32FW> map = flyweightRO.tryWrap(buffer, offset, buffer.capacity());

        assertNotNull(map);
        assertSame(flyweightRO, map);
        assertAllTestValuesRead(map, offset);
    }

    @Test
    public void shouldWrapAndReadItems() throws Exception
    {
        final int offset = 10;
        int size = setAllItems(buffer, offset);
        Map16FW<TypedefStringFW, VariantEnumKindWithString32FW> map = flyweightRO.wrap(buffer, offset, buffer.capacity());
        assertEquals(offset + size, map.limit());

        assertAllTestValuesRead(map, offset);
    }

    @Test
    public void shouldReadEmptyMap() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .build()
            .limit();

        final Map16FW<TypedefStringFW, VariantEnumKindWithString32FW> map = flyweightRO.wrap(buffer,  0,  limit);

        List<String> mapItems = new ArrayList<>();
        map.forEach(kv -> vv ->
        {
            mapItems.add(kv.get().asString());
            mapItems.add(vv.get().asString());
        });

        assertEquals(lengthSize + fieldCountSize, map.limit());
        assertEquals(0, mapItems.size());
    }

    @Test
    public void shouldSetKeyValueEntrys() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .entry(asStringFW("entry1Key"), asStringFW("entry1Value"))
            .entry(asStringFW("entry2Key"), asStringFW("entry2Value"))
            .build()
            .limit();

        final Map16FW map = flyweightRO.wrap(buffer,  0,  limit);

        assertAllTestValuesRead(map, 0);
    }

    private static StringFW asStringFW(
        String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new String8FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}
