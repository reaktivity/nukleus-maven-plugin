/**
 * Copyright 2016-2018 The Reaktivity Project
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.FlatWithListFW;

public class FlatWithListFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final MutableDirectBuffer expected = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final FlatWithListFW.Builder flatRW = new FlatWithListFW.Builder();
    private final FlatWithListFW flyweightRO = new FlatWithListFW();
    private final StringFW.Builder stringRW = new StringFW.Builder();
    private final MutableDirectBuffer valueBuffer = new UnsafeBuffer(allocateDirect(100));

    static int setAllTestValues(MutableDirectBuffer buffer, final int offset)
    {
        int pos = offset;
        buffer.putLong(pos,  10);
        buffer.putByte(pos += 8, (byte) 6);
        buffer.putStringWithoutLengthUtf8(pos += 1,  "value1");
        buffer.putInt(pos += "value1".length(), 1 + "listItem1".length());
        buffer.putByte(pos += 4, (byte) "listItem1".length());
        buffer.putStringWithoutLengthUtf8(pos += 1,  "listItem1");
        buffer.putInt(pos += "listItem1".length(), 11);

        return pos - offset + 4;
    }

    void assertAllTestValuesRead(FlatWithListFW flyweight)
    {
        assertEquals(10, flyweight.fixed1());
        assertEquals("value1", flyweight.string1().asString());
        final String listValue[] = new String[1];
        flyweight.list1().forEach((s) -> listValue[0] = s.asString());
        assertEquals("listItem1", listValue[0]);
        assertEquals(11, flyweight.fixed2());
    }

    @Test
    public void shouldNotTryWrapWhenIncomplete()
    {
        int size = setAllTestValues(buffer, 10);
        for (int maxLimit=10; maxLimit < 10 + size; maxLimit++)
        {
            assertNull("at maxLimit " + maxLimit, flyweightRO.tryWrap(buffer,  10, maxLimit));
        }
    }

    @Test
    public void shouldNotWrapWhenIncomplete()
    {
        int size = setAllTestValues(buffer, 10);
        for (int maxLimit=10; maxLimit < 10 + size; maxLimit++)
        {
            try
            {
                flyweightRO.wrap(buffer,  10, maxLimit);
                fail("Exception not thrown for maxLimit " + maxLimit);
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
    public void shouldTryWrapAndReadAllValues() throws Exception
    {
        final int offset = 1;
        setAllTestValues(buffer, offset);
        assertNotNull(flyweightRO.tryWrap(buffer, offset, buffer.capacity()));
        assertAllTestValuesRead(flyweightRO);
    }

    @Test
    public void shouldWrapAndReadAllValues() throws Exception
    {
        int size = setAllTestValues(buffer, 10);
        flyweightRO.wrap(buffer, 10,  buffer.capacity());
        assertEquals(10 + size, flyweightRO.limit());
        assertAllTestValuesRead(flyweightRO);
    }

    @Test
    public void shouldDefaultValues() throws Exception
    {
        int limit = flatRW.wrap(buffer, 0, 100)
                .string1("value1")
                .build()
                .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(111, flyweightRO.fixed1());
        AtomicInteger listSize = new AtomicInteger(0);
        flyweightRO.list1().forEach(s -> listSize.incrementAndGet());
        assertEquals(0, listSize.get());
        assertTrue(flyweightRO.list1().isEmpty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetFixed1WithInsufficientSpace()
    {
        flatRW.wrap(buffer, 10, 10)
               .fixed1(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WithInsufficientSpaceToDefaultPriorField()
    {
        flatRW.wrap(buffer, 10, 11)
                .string1("");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WithInsufficientSpace()
    {
        flatRW.wrap(buffer, 10, 18)
                .fixed1(10)
                .string1("1234");
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetList1BeforeString1() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
                .list1(b ->
                {
                });
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToResetFixed1() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
            .fixed1(10)
            .fixed1(101)
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToResetString1() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
            .string1("value1")
            .string1("another value");
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToResetList1() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
            .string1("value1")
            .list1(b -> b.item(i -> i.set("listItem1", UTF_8)))
            .list1(b -> b.item(i -> i.set("updatedListItem1", UTF_8)));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetList1AfterSettingList1Items() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
            .string1("value1")
            .list1Item(b -> b.set("item1", UTF_8))
            .list1(b ->
                   { });
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToBuildWhenString1NotSet() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
            .build();
    }

    @Test
    public void shouldAddList1Items() throws Exception
    {
        int limit = flatRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(10)
                .string1("value1")
                .list1Item(b -> b.set("item1", UTF_8))
                .list1Item(b -> b.set("item2", UTF_8))
                .list1Item(b -> b.set("item3", UTF_8))
                .build()
                .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertFalse(flyweightRO.list1().isEmpty());
        assertEquals(10, flyweightRO.fixed1());
        assertEquals("value1", flyweightRO.string1().asString());
        final List<String> listValues = new ArrayList<>();
        flyweightRO.list1().forEach((s) -> listValues.add(s.asString()));
        assertEquals(Arrays.asList("item1", "item2", "item3"), listValues);
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        FlatWithListFW flyweight = flatRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(10)
                .string1("value1")
                .list1(b -> b.item(i -> i.set("listItem1", UTF_8)))
                .fixed2(11)
                .build();
        int size = setAllTestValues(expected, 0);
        assertEquals(0 + size, flyweight.limit());
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());
    }

    @Test
    public void shouldSetStringValuesUsingStringFW() throws Exception
    {
        FlatWithListFW.Builder builder = flatRW.wrap(buffer, 0, buffer.capacity());
        builder.fixed1(10);
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
               .set("value1", UTF_8)
               .build();
        builder.string1(value)
                .list1(b ->
                       { })
               .build();
        flyweightRO.wrap(buffer,  0,  builder.limit());
        assertEquals(10, flyweightRO.fixed1());
        assertEquals("value1", flyweightRO.string1().asString());
    }

    @Test
    public void shouldSetStringValuesUsingBuffer() throws Exception
    {
        valueBuffer.putStringWithoutLengthUtf8(0, "value1");
        int limit = flatRW.wrap(buffer, 0, buffer.capacity())
            .fixed1(10)
            .string1(valueBuffer, 0, 6)
            .list1(b ->
            { })
            .build()
            .limit();
        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(10, flyweightRO.fixed1());
        assertEquals("value1", flyweightRO.string1().asString());
    }

}
