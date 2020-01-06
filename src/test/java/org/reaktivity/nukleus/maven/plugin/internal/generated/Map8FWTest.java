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

public class Map8FWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final Map8FW.Builder<TypedefStringFW.Builder, TypedefStringFW, EnumWithInt8, StringFW,
        VariantEnumKindWithString32FW.Builder, VariantEnumKindWithString32FW, EnumWithInt8, StringFW>
        flyweightRW = new Map8FW.Builder<>(new TypedefStringFW.Builder(), new TypedefStringFW(),
        new VariantEnumKindWithString32FW.Builder(), new VariantEnumKindWithString32FW());

    private final Map8FW<TypedefStringFW, VariantEnumKindWithString32FW> flyweightRO =
        new Map8FW<>(new TypedefStringFW(), new VariantEnumKindWithString32FW());

    private final int lengthSize = Byte.BYTES;
    private final int fieldCountSize = Byte.BYTES;

    private int setAllItems(
        MutableDirectBuffer buffer,
        int offset)
    {
        int length = 45;
        int fieldCount = 4;
        String pair1Key = "pair1Key";
        String pair1Value = "pair1Value";
        String pair2Key = "pair2Key";
        String pair2Value = "pair2Value";

        buffer.putByte(offset, (byte) length);
        int offsetFieldCount = offset + lengthSize;
        buffer.putByte(offsetFieldCount, (byte) fieldCount);

        int offsetMapPair1KeyKind = offsetFieldCount + fieldCountSize;
        buffer.putByte(offsetMapPair1KeyKind, EnumWithInt8.ONE.value());
        int offsetPair1KeyLength = offsetMapPair1KeyKind + Byte.BYTES;
        buffer.putByte(offsetPair1KeyLength, (byte) pair1Key.length());
        int offsetPair1Key = offsetPair1KeyLength + Byte.BYTES;
        buffer.putBytes(offsetPair1Key, pair1Key.getBytes());

        int offsetMapPair1ValueKind = offsetPair1Key + pair1Key.length();
        buffer.putByte(offsetMapPair1ValueKind, EnumWithInt8.ONE.value());
        int offsetPair1ValueLength = offsetMapPair1ValueKind + Byte.BYTES;
        buffer.putByte(offsetPair1ValueLength, (byte) pair1Value.length());
        int offsetPair1Value = offsetPair1ValueLength + Byte.BYTES;
        buffer.putBytes(offsetPair1Value, pair1Value.getBytes());

        int offsetMapPair2KeyKind = offsetPair1Value + pair1Value.length();
        buffer.putByte(offsetMapPair2KeyKind, EnumWithInt8.ONE.value());
        int offsetPair2KeyLength = offsetMapPair2KeyKind + Byte.BYTES;
        buffer.putByte(offsetPair2KeyLength, (byte) pair2Key.length());
        int offsetPair2Key = offsetPair2KeyLength + Byte.BYTES;
        buffer.putBytes(offsetPair2Key, pair2Key.getBytes());

        int offsetMapPair2ValueKind = offsetPair2Key + pair2Key.length();
        buffer.putByte(offsetMapPair2ValueKind, EnumWithInt8.ONE.value());
        int offsetPair2ValueLength = offsetMapPair2ValueKind + Byte.BYTES;
        buffer.putByte(offsetPair2ValueLength, (byte) pair2Value.length());
        int offsetPair2Value = offsetPair2ValueLength + Byte.BYTES;
        buffer.putBytes(offsetPair2Value, pair2Value.getBytes());

        return length + lengthSize;
    }

    static void assertAllTestValuesRead(
        Map8FW<TypedefStringFW, VariantEnumKindWithString32FW> flyweight,
        int offset)
    {
        List<String> mapItems = new ArrayList<>();
        flyweight.forEach(kv -> vv ->
        {
            mapItems.add(kv.get().asString());
            mapItems.add(vv.get().asString());
        });
        assertEquals(4, mapItems.size());
        assertEquals("pair1Key", mapItems.get(0));
        assertEquals("pair1Value", mapItems.get(1));
        assertEquals("pair2Key", mapItems.get(2));
        assertEquals("pair2Value", mapItems.get(3));
        assertEquals(45, flyweight.length());
        assertEquals(4, flyweight.fieldCount());
        assertEquals(offset + 46, flyweight.limit());
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
        Map8FW<TypedefStringFW, VariantEnumKindWithString32FW> map = flyweightRO.wrap(buffer, offset, buffer.capacity());

        assertSame(flyweightRO, map);
        assertAllTestValuesRead(map, offset);
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int size = setAllItems(buffer, offset);
        Map8FW<TypedefStringFW, VariantEnumKindWithString32FW> map = flyweightRO.tryWrap(buffer, offset, buffer.capacity());

        assertNotNull(map);
        assertSame(flyweightRO, map);
        assertAllTestValuesRead(map, offset);
    }

    @Test
    public void shouldWrapAndReadItems() throws Exception
    {
        final int offset = 10;
        int size = setAllItems(buffer, offset);
        Map8FW<TypedefStringFW, VariantEnumKindWithString32FW> map = flyweightRO.wrap(buffer, offset, buffer.capacity());
        assertEquals(offset + size, map.limit());

        assertAllTestValuesRead(map, offset);
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetKeyTwiceWithoutSettingValue() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
            .key(asStringFW("pair1Key"))
            .key(asStringFW("pair2Key"))
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetWithOutKeyValuePair() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
            .key(asStringFW("pair2Key"))
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetPairsInWrongOrder() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
            .value(asStringFW("pair1Value"))
            .key(asStringFW("pair2Key"))
            .value(asStringFW("pair2Value"))
            .build();
    }

    @Test
    public void shouldReadEmptyMap() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .build()
            .limit();

        final Map8FW<TypedefStringFW, VariantEnumKindWithString32FW> map = flyweightRO.wrap(buffer,  0,  limit);

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
    public void shouldSetKeyValuePairs() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .key(asStringFW("pair1Key"))
            .value(asStringFW("pair1Value"))
            .key(asStringFW("pair2Key"))
            .value(asStringFW("pair2Value"))
            .build()
            .limit();

        final Map8FW map = flyweightRO.wrap(buffer,  0,  limit);

        assertAllTestValuesRead(map, 0);
    }

    private static StringFW asStringFW(
        String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new String8FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}
