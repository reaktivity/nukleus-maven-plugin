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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.String16FW;
import org.reaktivity.reaktor.internal.test.types.String32FW;
import org.reaktivity.reaktor.internal.test.types.String8FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;

public class ListWithTypedefMapFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final ListWithTypedefMapFW.Builder flyweightRW = new ListWithTypedefMapFW.Builder();
    private final ListWithTypedefMapFW flyweightRO = new ListWithTypedefMapFW();

    private static final EnumWithInt8 KIND_MAP8 = EnumWithInt8.THREE;
    private static final EnumWithInt8 KIND_STRING8 = EnumWithInt8.ONE;
    private static final EnumWithInt8 KIND_LIST8 = EnumWithInt8.TWO;
    private final int kindSize = Byte.BYTES;
    private final int lengthSize = Byte.BYTES;
    private final int fieldCountSize = Byte.BYTES;

    private int setStringEntries(
        MutableDirectBuffer buffer,
        int offset)
    {
        byte listLength = 52;
        byte listFieldCount = 1;
        byte mapLength = 49;
        byte mapFieldCount = 4;
        String entry1Key = "entry1Key";
        String entry1Value = "entry1Value";
        String entry2Key = "entry2Key";
        String entry2Value = "entry2Value";

        buffer.putByte(offset, KIND_LIST8.value());
        int offsetListLength = offset + kindSize;
        buffer.putByte(offsetListLength, listLength);
        int offsetListFieldCount = offsetListLength + kindSize;
        buffer.putByte(offsetListFieldCount, listFieldCount);

        int offsetMapKind = offsetListFieldCount + fieldCountSize;
        buffer.putByte(offsetMapKind, KIND_MAP8.value());
        int offsetLength = offsetMapKind + kindSize;
        buffer.putByte(offsetLength, mapLength);
        int offsetFieldCount = offsetLength + lengthSize;
        buffer.putByte(offsetFieldCount, mapFieldCount);

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

        return listLength + kindSize + lengthSize;
    }

    static void assertAllTestValuesReadWithStringValues(
        ListWithTypedefMapFW flyweight,
        int offset)
    {
        assertEquals(52, flyweight.length());
        assertEquals(1, flyweight.fieldCount());
        assertEquals(offset + 54, flyweight.limit());
        List<String> mapItems = new ArrayList<>();
        flyweight.field1().forEach(kv -> vv ->
        {
            mapItems.add(kv.get().asString());
            mapItems.add(vv.getAsVariantEnumKindWithString32().asString());
        });
        assertEquals(49, flyweight.field1().length());
        assertEquals(4, flyweight.field1().fieldCount());
        assertEquals(4, mapItems.size());
        assertEquals("entry1Key", mapItems.get(0));
        assertEquals("entry1Value", mapItems.get(1));
        assertEquals("entry2Key", mapItems.get(2));
        assertEquals("entry2Value", mapItems.get(3));
    }

    static void assertAllTestValuesReadWithIntValues(
        ListWithTypedefMapFW flyweight,
        int offset)
    {
        assertEquals(33, flyweight.length());
        assertEquals(1, flyweight.fieldCount());
        assertEquals(offset + 35, flyweight.limit());
        List<String> mapKeys = new ArrayList<>();
        List<Integer> mapValues = new ArrayList<>();
        flyweight.field1().forEach(kv -> vv ->
        {
            mapKeys.add(kv.get().asString());
            mapValues.add(vv.getAsVariantOfInt32());
        });
        assertEquals(30, flyweight.field1().length());
        assertEquals(4, flyweight.field1().fieldCount());
        assertEquals(2, mapKeys.size());
        assertEquals(2, mapValues.size());
        assertEquals("entry1Key", mapKeys.get(0));
        assertEquals(100, (int) mapValues.get(0));
        assertEquals("entry2Key", mapKeys.get(1));
        assertEquals(1000, (int) mapValues.get(1));
    }

    @Test
    public void shouldNotWrapWhenLengthInsufficientForMinimumRequiredLength()
    {
        int length = setStringEntries(buffer, 10);
        for (int maxLimit = 10; maxLimit < 10 + length; maxLimit++)
        {
            try
            {
                flyweightRO.wrap(buffer, 10, maxLimit);
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
        int length = setStringEntries(buffer, 10);
        for (int maxLimit = 10; maxLimit < 10 + length; maxLimit++)
        {
            assertNull(flyweightRO.tryWrap(buffer,  10, maxLimit));
        }
    }

    @Test
    public void shouldWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int length = setStringEntries(buffer, 10);

        final ListWithTypedefMapFW listWithTypedefMap = flyweightRO.wrap(buffer, 10, 10 + length);

        assertSame(flyweightRO, listWithTypedefMap);
        assertAllTestValuesReadWithStringValues(listWithTypedefMap, 10);
    }

    @Test
    public void shouldTryWrapWhenLengthSufficientForMinimumRequiredLength()
    {
        int length = setStringEntries(buffer, 10);

        final ListWithTypedefMapFW listWithTypedefMap = flyweightRO.tryWrap(buffer, 10, 10 + length);

        assertNotNull(listWithTypedefMap);
        assertSame(flyweightRO, listWithTypedefMap);
        assertAllTestValuesReadWithStringValues(listWithTypedefMap, 10);
    }

    @Test(expected = AssertionError.class)
    public void shouldFailWhenSameFieldIsSetMoreThanOnce() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
            .field1(asMapFWWithStringValue(Arrays.asList(asStringFW("entry1Key"), asStringFW("entry2Key")),
                Arrays.asList(asStringFW("entry1Value"), asStringFW("entry2Value"))))
            .field1(asMapFWWithStringValue(Arrays.asList(asStringFW("entry1Key"), asStringFW("entry2Key")),
                Arrays.asList(asStringFW("entry1Value"), asStringFW("entry2Value"))))
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldAssertErrorWhenValueNotPresent() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .build()
            .limit();

        final ListWithTypedefMapFW listWithTypedefMap = flyweightRO.wrap(buffer, 0, limit);

        listWithTypedefMap.field1();
    }

    @Test
    public void shouldSetEntriesWithStringKeyAndStringValue() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .field1(asMapFWWithStringValue(Arrays.asList(asStringFW("entry1Key"), asStringFW("entry2Key")),
                Arrays.asList(asStringFW("entry1Value"), asStringFW("entry2Value"))))
            .build()
            .limit();

        final ListWithTypedefMapFW listWithTypedefMap = flyweightRO.wrap(buffer, 0, limit);

        assertAllTestValuesReadWithStringValues(listWithTypedefMap, 0);
    }

    @Test
    public void shouldSetEntriesWithStringKeyAndIntValue() throws Exception
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .field1(asMapFWWithIntValue(Arrays.asList(asStringFW("entry1Key"), asStringFW("entry2Key")),
                Arrays.asList(100, 1000)))
            .build()
            .limit();

        final ListWithTypedefMapFW listWithTypedefMap = flyweightRO.wrap(buffer, 0, limit);

        assertAllTestValuesReadWithIntValues(listWithTypedefMap, 0);
    }

    private static StringFW asStringFW(
        String value)
    {
        int length = value.length();
        int highestByteIndex = Integer.numberOfTrailingZeros(Integer.highestOneBit(length)) >> 3;
        MutableDirectBuffer buffer;
        switch (highestByteIndex)
        {
        case 0:
            buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
            return new String8FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
        case 1:
            buffer = new UnsafeBuffer(allocateDirect(Short.SIZE + value.length()));
            return new String16FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
        case 2:
        case 3:
            buffer = new UnsafeBuffer(allocateDirect(Integer.SIZE + value.length()));
            return new String32FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
        default:
            throw new IllegalArgumentException("Illegal value: " + value);
        }
    }

    private static MapFW<VariantEnumKindWithString32FW, VariantWithoutOfFW> asMapFWWithStringValue(
        List<StringFW> keys,
        List<StringFW> values)
    {
        TypedefMapFW.Builder<VariantWithoutOfFW, VariantWithoutOfFW.Builder> typedefMapRW =
            new TypedefMapFW.Builder<>(new VariantEnumKindWithString32FW(), new VariantWithoutOfFW(),
                new VariantEnumKindWithString32FW.Builder(), new VariantWithoutOfFW.Builder());
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100));
        typedefMapRW.wrap(buffer, 0, buffer.capacity());
        for (int i = 0; i < keys.size(); i++)
        {
            StringFW key = keys.get(i);
            StringFW value = values.get(i);
            typedefMapRW.entry(k -> k.set(key), v -> v.setAsVariantEnumKindWithString32(value));
        }
        TypedefMapFW<VariantWithoutOfFW> typedefMapRO = typedefMapRW.build();
        return typedefMapRO.get();
    }

    private static MapFW<VariantEnumKindWithString32FW, VariantWithoutOfFW> asMapFWWithIntValue(
        List<StringFW> keys,
        List<Integer> values)
    {
        TypedefMapFW.Builder<VariantWithoutOfFW, VariantWithoutOfFW.Builder> typedefMapRW =
            new TypedefMapFW.Builder<>(new VariantEnumKindWithString32FW(), new VariantWithoutOfFW(),
                new VariantEnumKindWithString32FW.Builder(), new VariantWithoutOfFW.Builder());
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100));
        typedefMapRW.wrap(buffer, 0, buffer.capacity());
        for (int i = 0; i < keys.size(); i++)
        {
            StringFW key = keys.get(i);
            int value = values.get(i);
            typedefMapRW.entry(k -> k.set(key), v -> v.setAsVariantOfInt32(value));
        }
        TypedefMapFW<VariantWithoutOfFW> typedefMapRO = typedefMapRW.build();
        return typedefMapRO.get();
    }
}
