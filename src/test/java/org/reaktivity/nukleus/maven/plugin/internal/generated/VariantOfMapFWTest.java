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
import org.reaktivity.nukleus.maven.plugin.internal.generated.VariantOfMapFW.Builder;
import org.reaktivity.reaktor.internal.test.types.String8FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.TypedefStringFW;
import org.reaktivity.reaktor.internal.test.types.inner.VariantEnumKindWithString32FW;

public class VariantOfMapFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final VariantOfMapFW.Builder<VariantEnumKindWithString32FW.Builder, VariantEnumKindWithString32FW, EnumWithInt8,
        StringFW, TypedefStringFW.Builder, TypedefStringFW, EnumWithInt8, StringFW> flyweightRW =
        new Builder<>(new VariantEnumKindWithString32FW.Builder(), new VariantEnumKindWithString32FW(),
            new TypedefStringFW.Builder(), new TypedefStringFW());
    private final VariantOfMapFW<VariantEnumKindWithString32FW, TypedefStringFW> flyweightRO =
        new VariantOfMapFW<>(new VariantEnumKindWithString32FW(), new TypedefStringFW());
    private static final EnumWithInt8 KIND_MAP8 = EnumWithInt8.THREE;
    private final int kindSize = Byte.BYTES;
    private final int lengthSize = Byte.BYTES;
    private final int fieldCountSize = Byte.BYTES;

    private int setTwoPairs(
        MutableDirectBuffer buffer,
        int offset)
    {
        int length = 45;
        int fieldCount = 4;
        String pair1Key = "pair1Key";
        String pair1Value = "pair1Value";
        String pair2Key = "pair2Key";
        String pair2Value = "pair2Value";

        buffer.putByte(offset, KIND_MAP8.value());
        int offsetLength = offset + kindSize;
        buffer.putByte(offsetLength, (byte) length);
        int offsetFieldCount = offsetLength + lengthSize;
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

        return length + kindSize + lengthSize;
    }

    static void assertAllTestValuesRead(
        VariantOfMapFW<VariantEnumKindWithString32FW, TypedefStringFW> flyweight,
        int offset)
    {
        List<String> mapItems = new ArrayList<>();
        flyweight.get().forEach(kv -> vv ->
        {
            mapItems.add(kv.get().asString());
            mapItems.add(vv.get().asString());
        });
        assertEquals(4, mapItems.size());
        assertEquals("pair1Key", mapItems.get(0));
        assertEquals("pair1Value", mapItems.get(1));
        assertEquals("pair2Key", mapItems.get(2));
        assertEquals("pair2Value", mapItems.get(3));
        assertEquals(45, flyweight.get().length());
        assertEquals(4, flyweight.get().fieldCount());
        assertEquals(offset + 47, flyweight.limit());
        assertEquals(KIND_MAP8, flyweight.kind());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int length = 45;
        setTwoPairs(buffer, offset);
        for (int maxLimit = offset; maxLimit <= length; maxLimit++)
        {
            flyweightRO.wrap(buffer, offset, maxLimit);
        }
    }

    @Test
    public void shouldNotTryWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int length = 45;
        setTwoPairs(buffer, offset);
        for (int maxLimit = offset; maxLimit <= length; maxLimit++)
        {
            assertNull(flyweightRO.tryWrap(buffer, offset, maxLimit));
        }
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int size = setTwoPairs(buffer, offset);
        final VariantOfMapFW<VariantEnumKindWithString32FW, TypedefStringFW> variantOfMap = flyweightRO.wrap(buffer, offset,
            buffer.capacity());

        assertSame(flyweightRO, variantOfMap);
        assertAllTestValuesRead(variantOfMap, offset);
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        final int offset = 10;
        int size = setTwoPairs(buffer, offset);
        final VariantOfMapFW<VariantEnumKindWithString32FW, TypedefStringFW> variantOfMap =
            flyweightRO.tryWrap(buffer, offset, buffer.capacity());

        assertNotNull(variantOfMap);
        assertSame(flyweightRO, variantOfMap);
        assertAllTestValuesRead(variantOfMap, offset);
    }

    @Test
    public void shouldWrapAndReadItems() throws Exception
    {
        final int offset = 10;
        int size = setTwoPairs(buffer, offset);
        final VariantOfMapFW<VariantEnumKindWithString32FW, TypedefStringFW> variantOfMap =
            flyweightRO.wrap(buffer, offset, buffer.capacity());
        assertEquals(offset + size, variantOfMap.limit());

        assertAllTestValuesRead(variantOfMap, offset);
    }

    @Test
    public void shouldReadEmptyList() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .build()
            .limit();

        final VariantOfMapFW<VariantEnumKindWithString32FW, TypedefStringFW> variantOfMap =
            flyweightRO.wrap(buffer, 0, limit);

        List<String> mapItems = new ArrayList<>();
        variantOfMap.get().forEach(kv -> vv ->
        {
            mapItems.add(kv.get().asString());
            mapItems.add(vv.get().asString());
        });

        assertEquals(3, variantOfMap.limit());
        assertEquals(0, variantOfMap.get().fieldCount());
        assertEquals(1, variantOfMap.get().length());
        assertEquals(0, mapItems.size());
    }

    @Test
    public void shouldSetPairs() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .pair(asStringFW("pair1Key"), asStringFW("pair1Value"))
            .pair(asStringFW("pair2Key"), asStringFW("pair2Value"))
            .build()
            .limit();

        final VariantOfMapFW<VariantEnumKindWithString32FW, TypedefStringFW> variantOfMap =
            flyweightRO.wrap(buffer, 0, limit);

        assertAllTestValuesRead(variantOfMap, 0);
    }

    private static StringFW asStringFW(
        String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new String8FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}
