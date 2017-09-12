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

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.reaktor.internal.test.types.inner.FlatFW;
import org.reaktivity.reaktor.internal.test.types.StringFW;

public class FlatFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xFF);
        }
    };
    private final FlatFW.Builder flatRW = new FlatFW.Builder();
    private final FlatFW flatRO = new FlatFW();
    private final StringFW.Builder stringRW = new StringFW.Builder();
    private final MutableDirectBuffer valueBuffer = new UnsafeBuffer(allocateDirect(100));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldProvideTypeId() throws Exception
    {
        int limit = flatRW.wrap(buffer, 0, 100)
                .fixed1(10)
                .string1("value1")
                .string2("value2")
                .build()
                .limit();
        flatRO.wrap(buffer,  0,  limit);
        assertEquals(0x10000001, flatRO.TYPE_ID);
        assertEquals(0x10000001, flatRO.typeId());
    }

    @Test
    public void shouldDefaultValues() throws Exception
    {
        int limit = flatRW.wrap(buffer, 0, 100)
                .fixed1(10)
                .string1("value1")
                .string2("value2")
                .build()
                .limit();
        flatRO.wrap(buffer,  0,  limit);
        assertEquals(222, flatRO.fixed2());
        assertEquals(333, flatRO.fixed3());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetFixed1WithInsufficientSpace()
    {
        flatRW.wrap(buffer, 10, 10)
               .fixed1(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetFixed2WithInsufficientSpace()
    {
        flatRW.wrap(buffer, 10, 12)
                .fixed1(10)
                .fixed2(20);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WhenDefaultingFixed2ExceedsMaxLimit()
    {
        flatRW.wrap(buffer, 10, 12)
                .fixed1(10)
                .string1("");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WhenExceedsMaxLimit()
    {
        flatRW.wrap(buffer, 10, 14)
                .fixed1(0x01)
                .fixed2(0x0101)
                .string1("1234");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetFixed3WithInsufficientSpace()
    {
        flatRW.wrap(buffer, 10, 15)
                .fixed1(10)
                .fixed2(20)
                .string1("")
                .fixed3(30);
    }

    @Test
    public void shouldFailToSetFixed2BeforeFixed1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
                .fixed2(10);
    }

    @Test
    public void shouldFailToSetString1BeforeFixed1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
                .string1("value1");
    }

    @Test
    public void shouldFailToSetString2BeforeFixed1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
                .string2("value1");
    }

    @Test
    public void shouldFailToSetString2BeforeString1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        flatRW.wrap(buffer, 0, 100)
                .fixed1(10)
                .string2("value1");
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
            .fixed1(10)
            .fixed2(111)
            .string1("value1")
            .string1("another value")
            .build();
    }

    @Test
    public void shouldFailToBuildWhenFixed1NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
            .build();
    }

    @Test
    public void shouldFailToBuildWhenString1NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        flatRW.wrap(buffer, 0, 100)
            .fixed1(10)
            .build();
    }

    @Test
    public void shouldFailToBuildWhenString2NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string2");
        flatRW.wrap(buffer, 0, 100)
            .fixed1(10)
            .fixed2(111)
            .string1("value1")
            .fixed3(33)
            .build();
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        flatRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(10)
                .fixed2(20)
                .string1("value1")
                .fixed3(30)
                .string2("value2")
                .build();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(10, flatRO.fixed1());
        assertEquals(20, flatRO.fixed2());
        assertEquals("value1", flatRO.string1().asString());
        assertEquals(30, flatRO.fixed3());
        assertEquals("value2", flatRO.string2().asString());
    }

    @Test
    public void shouldSetStringValuesUsingStringFW() throws Exception
    {
        FlatFW.Builder builder = flatRW.wrap(buffer, 0, buffer.capacity());
        builder.fixed1(10)
               .fixed2(20);
        StringFW value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
               .set("value1", UTF_8)
               .build();
        builder.string1(value)
               .fixed3(30);
        value = stringRW.wrap(valueBuffer,  0, valueBuffer.capacity())
               .set("value2", UTF_8)
               .build();
        builder.string2(value)
               .build();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(10, flatRO.fixed1());
        assertEquals(20, flatRO.fixed2());
        assertEquals("value1", flatRO.string1().asString());
        assertEquals(30, flatRO.fixed3());
        assertEquals("value2", flatRO.string2().asString());
    }

    @Test
    public void shouldSetStringValuesUsingBuffer() throws Exception
    {
        valueBuffer.putStringWithoutLengthUtf8(0, "value1");
        valueBuffer.putStringWithoutLengthUtf8(10, "value2");
        flatRW.wrap(buffer, 0, buffer.capacity())
            .fixed1(10)
            .fixed2(20)
            .string1(valueBuffer, 0, 6)
            .fixed3(30)
            .string2(valueBuffer, 10, 6)
            .build();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(10, flatRO.fixed1());
        assertEquals(20, flatRO.fixed2());
        assertEquals("value1", flatRO.string1().asString());
        assertEquals(30, flatRO.fixed3());
        assertEquals("value2", flatRO.string2().asString());
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailSetStringValuesToNull() throws Exception
    {
        flatRW.wrap(buffer, 0, buffer.capacity())
            .fixed1(10)
            .fixed2(20)
            .string1((String) null);
    }

    @Test
    public void shouldSetStringValuesToEmptyString() throws Exception
    {
        int limit = flatRW.wrap(buffer, 0, buffer.capacity())
            .fixed1(10)
            .fixed2(20)
            .string1("")
            .fixed3(30)
            .string2("")
            .build()
            .limit();
        flatRO.wrap(buffer,  0,  limit);
        assertEquals("", flatRO.string1().asString());
        assertEquals("", flatRO.string2().asString());
    }

}
