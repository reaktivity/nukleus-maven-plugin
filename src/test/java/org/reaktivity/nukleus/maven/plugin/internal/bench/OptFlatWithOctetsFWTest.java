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
package org.reaktivity.nukleus.maven.plugin.internal.bench;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.reaktor.internal.test.types.StringFW;

public class OptFlatWithOctetsFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final OptFlatWithOctetsFW.Builder flatWithOctetsRW = new OptFlatWithOctetsFW.Builder();
    private final OptFlatWithOctetsFW flatWithOctetsRO = new OptFlatWithOctetsFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldDefaultValues() throws Exception
    {
        int limit = flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .string1("value1")
                .octets2(b -> b.put("12345678901".getBytes(UTF_8)))
                .build()
                .limit();
        flatWithOctetsRO.wrap(buffer,  0,  limit);
        assertEquals(11, flatWithOctetsRO.fixed1());
        assertNull(flatWithOctetsRO.octets3());
    }

    @Test
    public void shouldAutomaticallySetLength() throws Exception
    {
        int limit = flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .string1("value1")
                .octets2(b -> b.put("123456".getBytes(UTF_8)))
                .build()
                .limit();
        flatWithOctetsRO.wrap(buffer,  0,  limit);
        assertEquals(6, flatWithOctetsRO.lengthOctets2());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetFixed1WithInsufficientSpace()
    {
        flatWithOctetsRW.wrap(buffer, 10, 11)
               .fixed1(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WithInsufficientSpaceToDefaultPriorField()
    {
        flatWithOctetsRW.wrap(buffer, 10, 11)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .string1("");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetString1WithInsufficientSpace()
    {
        flatWithOctetsRW.wrap(buffer, 10, 18)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .string1("1234");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetOctets1WithInsufficientSpace()
    {
        flatWithOctetsRW.wrap(buffer, 10, 16)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetOctets1WithValueLongerThanSize()
    {
        flatWithOctetsRW.wrap(buffer, 0, 100)
                .octets1(b -> b.put("12345678901".getBytes(UTF_8)));
    }

    @Test
    public void shouldFailToSetOctets1WithValueShorterThanSize()
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("9 instead of 10");
        flatWithOctetsRW.wrap(buffer, 0, 100)
                .octets1(b -> b.put("123456789".getBytes(UTF_8)));
    }

    @Test
    public void shouldFailToSetOctets1WithValueLongerThanSizeUsingBuffer()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("10");
        flatWithOctetsRW.wrap(buffer, 0, 100)
                .octets1(asBuffer("12345678901"), 0, 11);
    }

    @Test
    public void shouldFailToSetOctets1WithValueShorterThanSizeUsingBuffer()
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("octets1");
        flatWithOctetsRW.wrap(buffer, 0, 100)
                .fixed1(0)
                .octets1(asBuffer("123456789"), 0, 9);
    }

    @Ignore
    @Test
    public void shouldFailToResetFixed1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatWithOctetsRW.wrap(buffer, 0, 100)
            .fixed1(10)
            .fixed1(101)
            .build();
    }

    @Ignore
    @Test
    public void shouldFailToResetString1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        flatWithOctetsRW.wrap(buffer, 0, 100)
            .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
            .string1("value1")
            .string1("another value")
            .build();
    }

    @Ignore
    @Test
    public void shouldFailToBuildWhenOctets1NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("octets1");
        flatWithOctetsRW.wrap(buffer, 0, 100)
            .build();
    }

    @Ignore
    @Test
    public void shouldFailToBuildWhenString1NotSet() throws Exception
    {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("string1");
        flatWithOctetsRW.wrap(buffer, 0, 100)
            .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
            .build();
    }

    @Ignore
    @Test
    public void shouldFailToBuildWhenOctets2NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("octets2");
        flatWithOctetsRW.wrap(buffer, 0, 100)
            .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
            .string1("value1")
            .build();
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        int limit = flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(5)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .string1("value1")
                .octets2(b -> b.put("12345".getBytes(UTF_8)))
                .lengthOctets3(3)
                .octets3(b -> b.put("678".getBytes(UTF_8)))
                .extension(b -> b.put("octetsValue".getBytes(UTF_8)))
                .build()
                .limit();
        flatWithOctetsRO.wrap(buffer,  0,  limit);
        assertEquals(5, flatWithOctetsRO.fixed1());
        assertEquals("value1", flatWithOctetsRO.string1().asString());
        final String octets3 = flatWithOctetsRO.octets3().get(
                (buffer, offset, limit2) ->  buffer.getStringWithoutLengthUtf8(offset,  limit2 - offset));
        assertEquals("678", octets3);
        final String extension = flatWithOctetsRO.extension().get(
                (buffer, offset, limit2) ->  buffer.getStringWithoutLengthUtf8(offset,  limit2 - offset));
        assertEquals("octetsValue", extension);
    }

    @Test
    public void shouldSetOctetsValuesUsingBuffer() throws Exception
    {
        int limit = flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(5)
                .octets1(asBuffer("1234567890"), 0, 10)
                .string1("value1")
                .octets2(asBuffer("12345"), 0, 5)
                .extension(asBuffer("octetsValue"), 0, "octetsValue".length())
                .build()
                .limit();
        flatWithOctetsRO.wrap(buffer,  0,  limit);
        assertEquals(5, flatWithOctetsRO.fixed1());
        assertEquals("value1", flatWithOctetsRO.string1().asString());
        final String octetsValue = flatWithOctetsRO.extension().get(
                (buffer, offset, limit2) ->  buffer.getStringWithoutLengthUtf8(offset,  limit2 - offset));
        assertEquals("octetsValue", octetsValue);
    }

    @Test
    public void shouldSetStringValuesUsingStringFW() throws Exception
    {
        int limit = flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(5)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .string1(asStringFW("value1"))
                .octets2(b -> b.put("12345".getBytes(UTF_8)))
                .extension(b -> b.put("octetsValue".getBytes(UTF_8)))
                .build()
                .limit();
        flatWithOctetsRO.wrap(buffer,  0,  limit);
        assertEquals(5, flatWithOctetsRO.fixed1());
        assertEquals("value1", flatWithOctetsRO.string1().asString());
    }

    @Test
    public void shouldSetStringValuesUsingBuffer() throws Exception
    {
        int limit = flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(5)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .string1(asBuffer("value1"), 0, "value1".length())
                .octets2(b -> b.put("12345".getBytes(UTF_8)))
                .extension(b -> b.put("octetsValue".getBytes(UTF_8)))
                .build()
                .limit();
        flatWithOctetsRO.wrap(buffer,  0,  limit);
        assertEquals(5, flatWithOctetsRO.fixed1());
        assertEquals("value1", flatWithOctetsRO.string1().asString());
    }

    private static DirectBuffer asBuffer(String value)
    {
        MutableDirectBuffer valueBuffer = new UnsafeBuffer(allocateDirect(value.length()));
        valueBuffer.putStringWithoutLengthUtf8(0, value);
        return valueBuffer;
    }

    private static StringFW asStringFW(String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new StringFW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }

}
