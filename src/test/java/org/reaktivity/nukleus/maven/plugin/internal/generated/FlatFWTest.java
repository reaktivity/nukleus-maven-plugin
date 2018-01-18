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

    private static final int INDEX_FIXED1 = 0;

    private static final int INDEX_FIXED2 = 1;

    private static final int INDEX_STRING1 = 2;

    private static final int INDEX_FIXED3 = 3;

    private static final int INDEX_STRING2 = 4;

    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
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
        int limit = setAllRequiredValues(flatRW.wrap(buffer, 0, 100));
        flatRO.wrap(buffer,  0,  limit);
        assertEquals(0x10000001, FlatFW.TYPE_ID);
        assertEquals(0x10000001, flatRO.typeId());
    }

    @Test
    public void shouldDefaultValues() throws Exception
    {
        int limit = setAllRequiredValues(flatRW.wrap(buffer, 0, 100));
        flatRO.wrap(buffer,  0,  limit);
        assertEquals(222, flatRO.fixed2());
        assertEquals(333, flatRO.fixed3());
    }


    @Test
    public void shouldSetAllValues() throws Exception
    {
        setAllValues(flatRW.wrap(buffer,  0,  buffer.capacity()));
        flatRO.wrap(buffer,  0,  100);
        assertAllValues(flatRO);
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
        assertAllValues(flatRO);
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
        assertAllValues(flatRO);
    }

    @Test
    public void shouldSetString1ValuesToNull() throws Exception
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

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetFixed1WithInsufficientSpace()
    {
        flatRW.wrap(buffer, 10, 10)
                .fixed1(10);
    }

    @Test
    public void shouldFailToResetFixed1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        setAllRequiredFields(flatRW.wrap(buffer, 0, 100), INDEX_FIXED2)
                .fixed1(101)
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

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetFixed1WithLowerValue()
    {
        flatRW.wrap(buffer, 10, 10)
                .fixed1(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetFixed2WithInsufficientSpace()
    {
        setAllRequiredFields(flatRW.wrap(buffer, 10, 8), INDEX_FIXED2)
                .fixed2(20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetFixed2WithHigherValue()
    {
        setAllRequiredFields(flatRW.wrap(buffer, 10, 20), INDEX_FIXED2)
                .fixed2(65536);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetFixed2WithLowerValue()
    {
        setAllRequiredFields(flatRW.wrap(buffer, 10, 20), INDEX_FIXED2)
                .fixed2(-1);
    }

    @Test
    public void shouldFailToSetFixed2BeforeFixed1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
                .fixed2(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WhenDefaultingFixed2ExceedsMaxLimit()
    {
        setAllRequiredFields(flatRW.wrap(buffer, 10, 12), INDEX_FIXED2)
                .string1("");
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
    public void shouldFailToBuildWhenString1NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        setAllRequiredFields(flatRW.wrap(buffer, 0, 100), INDEX_STRING1)
                .build();
    }

    @Test
    public void shouldFailToResetString1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        setAllRequiredFields(flatRW.wrap(buffer, 0, 100), INDEX_STRING1)
                .string1("string1")
                .string1("another value")
                .build();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WhenExceedsMaxLimit()
    {
        setAllRequiredFields(flatRW.wrap(buffer, 10, 14), INDEX_STRING1)
                .string1("1234");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetFixed3WithInsufficientSpace()
    {
        setAllRequiredFields(flatRW.wrap(buffer, 10, 15), INDEX_FIXED3)
                .fixed3(30);
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
        setAllRequiredFields(flatRW.wrap(buffer, 0, 100), INDEX_STRING1)
                .string2("value1");
    }

    @Test
    public void shouldFailToBuildWhenString2NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string2");
        setAllRequiredFields(flatRW.wrap(buffer, 0, 100), INDEX_STRING2)
                .build();
    }

    static int setAllRequiredValues(FlatFW.Builder builder)
    {
        return builder.fixed1(10)
                .string1("value1")
                .string2("value2")
                .build()
                .limit();

    }

    static FlatFW.Builder setAllRequiredFields(FlatFW.Builder builder, int fieldIndex)
    {
        switch (fieldIndex)
        {
            case INDEX_FIXED2:
            case INDEX_STRING1: builder.fixed1(10);
                break;
            case INDEX_STRING2: builder.fixed1(10);
                builder.string1("value1");
                break;
            case INDEX_FIXED3: builder.fixed1(10);
                builder.string1("value1");
                builder.string2("value2");
                break;
        }

        return builder;
    }

    static void setAllValues(FlatFW.Builder builder)
    {
        builder.fixed1(10)
                .fixed2(20)
                .string1("value1")
                .fixed3(30)
                .string2("value2")
                .build();
    }

    static void assertAllValues(FlatFW flatFW)
    {
        assertEquals(10, flatFW.fixed1());
        assertEquals(20, flatFW.fixed2());
        assertEquals("value1", flatFW.string1().asString());
        assertEquals(30, flatFW.fixed3());
        assertEquals("value2", flatFW.string2().asString());
    }
}
