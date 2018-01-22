/**
 * Copyright 2016-2017 The Reaktivity Project
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
    private final FlatWithListFW.Builder flatRW = new FlatWithListFW.Builder();
    private final FlatWithListFW flatRO = new FlatWithListFW();
    private final StringFW.Builder stringRW = new StringFW.Builder();
    private final MutableDirectBuffer valueBuffer = new UnsafeBuffer(allocateDirect(100));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldDefaultValues() throws Exception
    {
        int limit = flatRW.wrap(buffer, 0, 100)
                .string1("value1")
                .build()
                .limit();
        flatRO.wrap(buffer,  0,  limit);
        assertEquals(111, flatRO.fixed1());
        AtomicInteger listSize = new AtomicInteger(0);
        flatRO.list1().forEach(s -> listSize.incrementAndGet());
        assertEquals(0, listSize.get());
        assertTrue(flatRO.list1().isEmpty());
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

    @Test
    public void shouldFailToSetList1BeforeString1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        flatRW.wrap(buffer, 0, 100)
                .list1(b ->
                {
                });
    }

    @Test
    public void shouldFailToResetFixed1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
            .fixed1(10)
            .fixed1(101)
            .build();
    }

    @Test
    public void shouldFailToResetString1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        flatRW.wrap(buffer, 0, 100)
            .string1("value1")
            .string1("another value");
    }

    @Test
    public void shouldFailToResetList1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("list1");
        flatRW.wrap(buffer, 0, 100)
            .string1("value1")
            .list1(b -> b.item(i -> i.set("listItem1", UTF_8)))
            .list1(b -> b.item(i -> i.set("updatedListItem1", UTF_8)));
    }

    @Test
    public void shouldFailToSetList1AfterSettingList1Items() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("list1");
        flatRW.wrap(buffer, 0, 100)
            .string1("value1")
            .list1Item(b -> b.set("item1", UTF_8))
            .list1(b ->
                   { });
    }

    @Test
    public void shouldFailToBuildWhenString1NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
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
        flatRO.wrap(buffer,  0,  limit);
        assertFalse(flatRO.list1().isEmpty());
        assertEquals(10, flatRO.fixed1());
        assertEquals("value1", flatRO.string1().asString());
        final List<String> listValues = new ArrayList<>();
        flatRO.list1().forEach((s) -> listValues.add(s.asString()));
        assertEquals(Arrays.asList("item1", "item2", "item3"), listValues);
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        int limit = flatRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(10)
                .string1("value1")
                .list1(b -> b.item(i -> i.set("listItem1", UTF_8)))
                .build()
                .limit();
        flatRO.wrap(buffer,  0,  limit);
        assertEquals(10, flatRO.fixed1());
        assertEquals("value1", flatRO.string1().asString());
        final String listValue[] = new String[1];
        flatRO.list1().forEach((s) -> listValue[0] = s.asString());
        assertEquals("listItem1", listValue[0]);
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
        flatRO.wrap(buffer,  0,  builder.limit());
        assertEquals(10, flatRO.fixed1());
        assertEquals("value1", flatRO.string1().asString());
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
        flatRO.wrap(buffer,  0,  limit);
        assertEquals(10, flatRO.fixed1());
        assertEquals("value1", flatRO.string1().asString());
    }

}
