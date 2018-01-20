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
import static org.junit.Assert.assertSame;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.reaktor.internal.test.types.inner.FlatFW;
import org.reaktivity.reaktor.internal.test.types.inner.FlatFW.Builder;
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

    MutableDirectBuffer expected = new UnsafeBuffer(allocateDirect(100))
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
        int limit = setRequiredBuilderValues(flatRW.wrap(buffer, 0, 100));
        flatRO.wrap(buffer,  0,  limit);
        assertEquals(0x10000001, FlatFW.TYPE_ID);
        assertEquals(0x10000001, flatRO.typeId());
    }

    @Test
    public void shouldDefaultValues() throws Exception
    {
        final int offset = 11;
        int expectedLimit = setAllRequiredBufferValues(expected, offset);

        Builder builder = flatRW.wrap(buffer, offset, expectedLimit);
        int limit = setAllRequiredBuilderFields(builder, INDEX_FIXED3).build().limit();

        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        final int offset = 11;
        int expectedLimit = setAllBufferValues(expected, offset);

        Builder builder = flatRW.wrap(buffer, offset, expectedLimit);

        int limit = setAllBuilderValues(builder).build().limit();
        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);

    }

    @Test
    public void shouldReadDefaultedValues() throws Exception
    {
        final int offset = 11;
        int limit = setAllRequiredBufferValues(buffer, offset);
        assertSame(flatRO, flatRO.wrap(buffer,  offset,  limit));
        assertRequiredValuesAndDefaults(flatRO);
    }

    @Test
    public void shouldSetStringValuesUsingStringFW() throws Exception
    {
        final int offset = 11;
        int expectedLimit = setAllBufferValues(expected, offset);

        FlatFW.Builder builder = flatRW.wrap(buffer, offset, expectedLimit);

        StringFW value1 = stringRW.wrap(valueBuffer,  offset, expectedLimit)
                .set("value1", UTF_8)
                .build();
        StringFW value2 = stringRW.wrap(valueBuffer,  offset, expectedLimit)
                .set("value2", UTF_8)
                .build();

        int limit = setStringBuilderValuesUsingStringFW(builder, value1, value2).limit();

        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
    }

    @Test
    public void shouldSetStringValuesUsingBuffer() throws Exception
    {
        final int offset = 11;
        int expectedLimit = setAllRequiredBufferValues(expected, offset);

        valueBuffer.putStringWithoutLengthUtf8(0, "value1");
        valueBuffer.putStringWithoutLengthUtf8(6, "value2");
        int limit = flatRW.wrap(buffer, offset, expectedLimit)
                .fixed1(10)
                .fixed2(20)
                .string1(valueBuffer, 0, 6)
                .fixed3(30)
                .string2(valueBuffer, 10, 6)
                .build()
                .limit();
        
        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
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
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 0, 100), INDEX_FIXED2)
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
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 10, 8), INDEX_FIXED2)
                .fixed2(20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetFixed2WithHigherValue()
    {
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 10, 20), INDEX_FIXED2)
                .fixed2(65536);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetFixed2WithLowerValue()
    {
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 10, 20), INDEX_FIXED2)
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
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 10, 12), INDEX_FIXED2)
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
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 0, 100), INDEX_STRING1)
                .build();
    }

    @Test
    public void shouldFailToResetString1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 0, 100), INDEX_STRING1)
                .string1("string1")
                .string1("another value")
                .build();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WhenExceedsMaxLimit()
    {
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 10, 14), INDEX_STRING1)
                .string1("1234");
    }

    @Test
    public void shouldSetString1ValuesToNull() throws Exception
    {
        flatRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(10)
                .fixed2(20)
                .string1((String) null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetFixed3WithInsufficientSpace()
    {
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 10, 15), INDEX_FIXED3)
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
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 0, 100), INDEX_STRING1)
                .string2("value1");
    }

    @Test
    public void shouldFailToBuildWhenString2NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string2");
        setAllRequiredBuilderFields(flatRW.wrap(buffer, 0, 100), INDEX_STRING2)
                .build();
    }

    static int setRequiredBuilderValues(FlatFW.Builder builder)
    {
        return builder.fixed1(10)
                .string1("value1")
                .string2("value2")
                .build()
                .limit();

    }

    static int setAllRequiredBufferValues(MutableDirectBuffer buffer, int offset)
    {
        buffer.putLong(offset, 10);
        buffer.putShort(offset += 8, (short) 222);
        buffer.putByte(offset += 2, (byte) "value1".length());
        buffer.putBytes(offset += 1, "value1".getBytes(UTF_8));
        buffer.putInt(offset+=6, 333);
        buffer.putByte(offset += 4, (byte) "value2".length());
        buffer.putBytes(offset += 1, "value2".getBytes(UTF_8));

        return offset + 6;
    }

    static int setAllBufferValues(MutableDirectBuffer buffer, int offset)
    {
        buffer.putLong(offset, 10);
        buffer.putShort(offset += 8, (short) 20);
        buffer.putByte(offset += 2, (byte) "value1".length());
        buffer.putBytes(offset += 1, "value1".getBytes(UTF_8));
        buffer.putInt(offset+=6, 30);
        buffer.putByte(offset += 4, (byte) "value2".length());
        buffer.putBytes(offset += 1, "value2".getBytes(UTF_8));

        return offset + 6;
    }

    static FlatFW.Builder setAllRequiredBuilderFields(FlatFW.Builder builder, int fieldIndex)
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

    static FlatFW.Builder setAllBuilderValues(FlatFW.Builder builder)
    {
        return builder.fixed1(10)
                .fixed2(20)
                .string1("value1")
                .fixed3(30)
                .string2("value2");
    }

    static FlatFW.Builder setStringBuilderValuesUsingStringFW(FlatFW.Builder builder, StringFW string1, StringFW string2)
    {

        return builder.fixed1(10)
                .fixed2(20)
                .string1(string1)
                .fixed3(30)
                .string2(string2);
    }

    static void assertRequiredValuesAndDefaults(FlatFW flyweight)
    {
        assertEquals(222, flyweight.fixed2());
        assertEquals(333, flyweight.fixed3());
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
