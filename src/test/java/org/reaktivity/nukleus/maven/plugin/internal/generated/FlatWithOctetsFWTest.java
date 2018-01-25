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
import static org.junit.Assert.*;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.reaktor.internal.test.types.OctetsFW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.FlatWithOctetsFW;
import org.reaktivity.reaktor.internal.test.types.inner.FlatWithOctetsFW.Builder;

public class FlatWithOctetsFWTest
{
    private static final int INDEX_FIXED1 = 0;
    private static final int INDEX_OCTETS1 = 1;
    private static final int INDEX_LENGTH_OCTETS2 = 2;
    private static final int INDEX_STRING1 = 3;
    private static final int INDEX_OCTETS2 = 4;
    private static final int INDEX_LENGTH_OCTETS3 = 5;
    private static final int INDEX_LENGTH_OCTETS4 = 7;

    private static final String[] FIELD_NAMES = {
      "fixed1",
      "octets1",
      "lengthOctets2",
      "string1",
      "octets2",
      "lengthOctets3",
      "octets3",
      "lengthOctets4",
      "octets4",
      "extension"
    };

    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100));
    {
        buffer.setMemory(0, buffer.capacity(), (byte) 0xab);
    }
    MutableDirectBuffer expected = new UnsafeBuffer(allocateDirect(100));
    {
        expected.setMemory(0, expected.capacity(), (byte) 0xab);
    }
    private final FlatWithOctetsFW.Builder flatWithOctetsRW = new FlatWithOctetsFW.Builder();
    private final FlatWithOctetsFW flatWithOctetsRO = new FlatWithOctetsFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldDefaultValues() throws Exception
    {
        final int offset = 11;
        int expectedLimit = setAllRequiredBufferValues(expected, offset);

        Builder builder = flatWithOctetsRW.wrap(buffer, offset, expectedLimit);
        int limit =  setAllRequiredValues(builder).build().limit();

        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
    }

    @Test
    public void shouldExplicitlySetOctetsValuesWithNullDefaultToNull() throws Exception
    {
        int limit = flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .octets1(asOctetsFW("1234567890"))
                .string1("value1")
                .octets2(asOctetsFW("12345678901"))
                .lengthOctets3(-1)
                .octets3((OctetsFW) null)
                .octets4((OctetsFW) null)
                .build()
                .limit();
        flatWithOctetsRO.wrap(buffer,  0,  limit);
        assertEquals(11, flatWithOctetsRO.fixed1());
        assertNull(flatWithOctetsRO.octets3());
    }

    @Test
    public void shouldAutomaticallySetLength() throws Exception
    {
        final int offset = 11;
        int limit = setAllRequiredBufferValues(buffer, offset);
        assertSame(flatWithOctetsRO, flatWithOctetsRO.wrap(buffer,  offset,  limit));
        assertRequiredValuesAndDefaults(flatWithOctetsRO);
        System.out.println(flatWithOctetsRO.toString());
    }

    @Test
    public void shouldReadAllValues() throws Exception
    {
        final int offset = 11;
        int expectedLimit = setAllBufferValues(buffer, offset);
        assertSame(flatWithOctetsRO, flatWithOctetsRO.wrap(buffer, offset, expectedLimit));
        assertAllValues(flatWithOctetsRO);
    }

    @Test
    public void shouldSetAllValuesVariant1() throws Exception
    {
        final int offset = 11;
        int expectedLimit = setAllBufferValues(expected, offset);
        Builder builder = flatWithOctetsRW.wrap(buffer, offset, expectedLimit);
        int limit = setAllValues(builder).build().limit();
        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
    }

    @Test
    public void shouldSetAllValuesVariant2() throws Exception
    {
        final int offset = 11;
        int expectedLimit = setAllBufferValues(expected, offset);
        Builder builder = flatWithOctetsRW.wrap(buffer, offset, expectedLimit);
        int limit = setAllValues(builder).build().limit();
        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
    }

    @Test
    public void shouldSetAllValuesVariant3() throws Exception
    {
        final int offset = 11;
        int expectedLimit = setAllBufferValues(expected, offset);
        Builder builder = flatWithOctetsRW.wrap(buffer, offset, expectedLimit);
        int limit = setAllValues(builder).build().limit();
        assertEquals(expectedLimit, limit);
        assertEquals(expected, buffer);
    }

    @Test
    public void shouldReadNullValues()
    {
        int offset = 0;
        int expectedLimit = setAllRequiredBufferValues(buffer, offset);
        FlatWithOctetsFW wrapped = flatWithOctetsRO.wrap(buffer, 0, expectedLimit);

        assertNull(wrapped.octets3());
        assertNull(wrapped.octets4());
    }

    @Test
    public void shouldReportAllFieldValuesInToString() throws Exception
    {
        final int offset = 11;
        int limit = setAllBufferValues(buffer, offset);
        String result = flatWithOctetsRO.wrap(buffer, offset, limit).toString();
        assertNotNull(result);
        for (String fieldName : FIELD_NAMES)
        {
            assertTrue(String.format("toString is missing %s", fieldName), result.contains(fieldName));
        }
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
    public void shouldFailToSetOctets1WithInsufficientSpaceVariant1()
    {
        flatWithOctetsRW.wrap(buffer, 10, 16)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetOctets1WithInsufficientSpaceVariant2()
    {
        flatWithOctetsRW.wrap(buffer, 10, 16)
                .octets1(asOctetsFW("1234567890"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetOctets1WithValueLongerThanSize()
    {
        flatWithOctetsRW.wrap(buffer, 0, 100)
                .octets1(b -> b.put("12345678901".getBytes(UTF_8)));
    }

    @Test
    public void shouldFailToBuildWhenString1NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        flatWithOctetsRW.wrap(buffer, 0, 100)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .build();
    }

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
    public void shouldSetAllValuesUsingOctetsFW() throws Exception
    {
        int limit = flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(5)
                .octets1(asOctetsFW("1234567890"))
                .string1("value1")
                .octets2(asOctetsFW("12345"))
                .lengthOctets3(3)
                .octets3(asOctetsFW("678"))
                .octets4(asOctetsFW("910"))
                .extension(asOctetsFW("octetsValue"))
                .build()
                .limit();
        flatWithOctetsRO.wrap(buffer,  0,  limit);
        assertEquals(5, flatWithOctetsRO.fixed1());
        assertEquals("value1", flatWithOctetsRO.string1().asString());
        final String octets3 = flatWithOctetsRO.octets3().get(
                (buffer, offset, limit2) ->  buffer.getStringWithoutLengthUtf8(offset,  limit2 - offset));
        assertEquals("678", octets3);
        final String octets4 = flatWithOctetsRO.octets4().get(
                (buffer, offset, limit2) ->  buffer.getStringWithoutLengthUtf8(offset,  limit2 - offset));
        assertEquals("910", octets4);
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
                .lengthOctets3(12)
                .octets3(asBuffer("octets3Value"), 0, "octets3Value".length())
                .octets4(asBuffer("octets4Value"), 0, "octets4Value".length())
                .extension(asBuffer("octetsValue"), 0, "octetsValue".length())
                .build()
                .limit();
        flatWithOctetsRO.wrap(buffer,  0,  limit);
        assertEquals(5, flatWithOctetsRO.fixed1());
        assertEquals("value1", flatWithOctetsRO.string1().asString());
        final String octetsValue = flatWithOctetsRO.extension().get(
                (buffer, offset, limit2) ->  buffer.getStringWithoutLengthUtf8(offset,  limit2 - offset));
        assertEquals("octetsValue", octetsValue);
        final String octets3Value = flatWithOctetsRO.octets3().get(
                (buffer, offset, limit3) ->  buffer.getStringWithoutLengthUtf8(offset,  limit3 - offset));
        assertEquals("octets3Value", octets3Value);
        final String octets4Value = flatWithOctetsRO.octets4().get(
                (buffer, offset, limit3) ->  buffer.getStringWithoutLengthUtf8(offset,  limit3 - offset));
        assertEquals("octets4Value", octets4Value);
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

    @Test
    public void shouldFailToSetOctets1WithValueShorterThanSizeUsingMutator()
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("9 instead of 10");
        flatWithOctetsRW.wrap(buffer, 0, 100)
                .octets1(b -> b.put("123456789".getBytes(UTF_8)));
    }

    @Test
    public void shouldFailToSetOctets1WithValueShorterThanSizeUsingOctets()
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("9 instead of 10");
        flatWithOctetsRW.wrap(buffer, 0, 100)
                .octets1(asOctetsFW("123456789"));
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

    @Test
    public void shouldFailToSetOctets2ToNull() throws Exception
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("octets2");
        flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .octets1(asOctetsFW("1234567890"))
                .string1("value1")
                .octets2((OctetsFW) null);
    }

    @Test
    public void shouldFailToSetOctets3WithLengthTooLong() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("lengthOctets3");
        flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(5)
                .octets1(asOctetsFW("1234567890"))
                .string1("value1")
                .octets2(asOctetsFW("12345"))
                .lengthOctets3(4)  // too long, should be 3
                .octets3(asOctetsFW("678"));
    }

    @Test
    public void shouldFailToSetOctets3ToNullWithLengthNotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("lengthOctets3");
        flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(5)
                .octets1(asOctetsFW("1234567890"))
                .string1("value1")
                .octets2(asOctetsFW("12345"))
                .octets3((OctetsFW) null);
    }

    @Test
    public void shouldFailToSetOctets3WithLengthTooLongVariant1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("lengthOctets3");
        flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(5)
                .octets1(asOctetsFW("1234567890"))
                .string1("value1")
                .octets2(asOctetsFW("12345"))
                .lengthOctets3(4)  // too long, should be 3
                .octets3(b -> b.set("678".getBytes(UTF_8)));
    }

    @Test
    public void shouldFailToSetOctets3WithLengthTooLongVariant2() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("lengthOctets3");
        flatWithOctetsRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(5)
                .octets1(asOctetsFW("1234567890"))
                .string1("value1")
                .octets2(asBuffer("12345"), 0, "12345".length())
                .lengthOctets3(4)  // too long, should be 3
                .octets3(asBuffer("678"), 0, "678".length());
    }

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

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetFixed1WithValueTooLow()
    {
        flatWithOctetsRW.wrap(buffer, 10, 11)
                .fixed1(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetFixed1WithValueTooHigh()
    {
        flatWithOctetsRW.wrap(buffer, 10, 10000)
                .fixed1(4294967296L);
    }

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

    @Test
    public void shouldFailToBuildWhenOctets1NotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("octets1");
        flatWithOctetsRW.wrap(buffer, 0, 100)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetLengthOctets2WithValueTooHigh()
    {
        MutableDirectBuffer valueBuffer = new UnsafeBuffer(allocateDirect(65536));
        valueBuffer.putStringWithoutLengthUtf8(0, "x");

        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(65581));
        flatWithOctetsRW.wrap(buffer, 0, 65581)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .string1("value1")
                .octets2(valueBuffer, 0, 65536);
    }

    static DirectBuffer asBuffer(String value)
    {
        MutableDirectBuffer valueBuffer = new UnsafeBuffer(allocateDirect(value.length()));
        valueBuffer.putStringWithoutLengthUtf8(0, value);
        return valueBuffer;
    }

    private static OctetsFW asOctetsFW(String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new OctetsFW.Builder().wrap(buffer, 0, buffer.capacity()).set(value.getBytes(UTF_8)).build();
    }

    private static StringFW asStringFW(String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new StringFW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }

    static int setAllBufferValues(MutableDirectBuffer buffer, int offset)
    {
        buffer.putInt(offset,  11);
        // TODO: allocate and fill a byte array to deal with
        //       arbitrary long fixed length
        buffer.putBytes(offset += 4,  "1234567890".getBytes(UTF_8));
        buffer.putShort(offset += 10, (short) 2);
        buffer.putShort(offset += 2, (byte) "value string1".length());
        buffer.putBytes(offset += 1, "value string1".getBytes(UTF_8));
        buffer.putByte(offset += "value string1".length(), (byte) 'x');
        buffer.putByte(offset += 1, (byte) 'y');
        buffer.putByte(offset += 1,  (byte) 4); // varint(2)
        buffer.putByte(offset += 1, (byte) 'x');
        buffer.putByte(offset += 1, (byte) 'y');
        buffer.putInt(offset += 1, 2);  // lengthOctets4 null default
        buffer.putByte(offset += 4, (byte) 'x');
        buffer.putByte(offset += 1, (byte) 'y');
        buffer.putByte(offset += 1, (byte) 'x');
        buffer.putByte(offset += 1, (byte) 'y');

        return offset+1;
    }

    static int setAllRequiredBufferValues(MutableDirectBuffer buffer, int offset)
    {
        return setRequiredBufferValues(buffer, offset, Integer.MAX_VALUE);
    }

    static int setRequiredBufferValues(MutableDirectBuffer buffer, int offset, int toFieldIndex)
    {
        if (toFieldIndex > INDEX_FIXED1)
        {
            buffer.putInt(offset,  11);
        }
        if (toFieldIndex > INDEX_OCTETS1)
        {
            buffer.putBytes(offset += 4,  "1234567890".getBytes(UTF_8));
        }
        if (toFieldIndex > INDEX_LENGTH_OCTETS2)
        {
            buffer.putShort(offset += 10, (short) 1);
        }
        if (toFieldIndex > INDEX_STRING1)
        {
            buffer.putByte(offset += 2, (byte) "value string1".length());
            buffer.putBytes(offset += 1, "value string1".getBytes(UTF_8));
        }
        if (toFieldIndex > INDEX_OCTETS2)
        {
            buffer.putByte(offset += "value string1".length(), (byte) 'x');
        }
        if (toFieldIndex > INDEX_LENGTH_OCTETS3)
        {
            buffer.putByte(offset += 1,  (byte) 1); // varint(-1) null default
        }
        if (toFieldIndex > INDEX_LENGTH_OCTETS4)
        {
            buffer.putInt(offset += 1, -1);  // lengthOctets4 null default
        }
        return offset + 4;
    }

    static FlatWithOctetsFW.Builder setAllValues(FlatWithOctetsFW.Builder builder)
    {
        return builder.fixed1(11)
                .octets1(b -> b.put("1234567890".getBytes(UTF_8)))
                .string1("value string1")
                .octets2(b -> b.put("xy".getBytes(UTF_8)))
                .lengthOctets3(2)
                .octets3(b -> b.put("xy".getBytes(UTF_8)))
                .octets4(b -> b.put("xy".getBytes(UTF_8)))
                .extension(b -> b.put("xy".getBytes(UTF_8)));
    }

    static FlatWithOctetsFW.Builder setAllRequiredValues(FlatWithOctetsFW.Builder builder)
    {
        return setRequiredValues(builder, Integer.MAX_VALUE);
    }

    static FlatWithOctetsFW.Builder setRequiredValues(FlatWithOctetsFW.Builder builder, int toFieldIndex)
    {
        if (toFieldIndex > INDEX_FIXED1)
        {
            builder.fixed1(11);
        }
        if (toFieldIndex > INDEX_OCTETS1)
        {
            builder.octets1(b -> b.put("1234567890".getBytes(UTF_8)));
        }
        if (toFieldIndex > INDEX_STRING1)
        {
            builder.string1("value string1");
        }
        if (toFieldIndex > INDEX_OCTETS2)
        {
            builder.octets2(b -> b.put("x".getBytes(UTF_8)));
        }

        return builder;
    }

    static void assertOctetsEquals(String expected, OctetsFW octets)
    {
        assertOctetsEquals(expected.getBytes(UTF_8), octets);
    }

    static void assertOctetsEquals(byte[] expected, OctetsFW octets)
    {
        assertEquals(expected.length, octets.sizeof());
        byte[] actual = new byte[expected.length];
        octets.buffer().getBytes(octets.offset(), actual);
        assertArrayEquals(expected, actual);
    }

    static void assertAllValues(FlatWithOctetsFW flyweight)
    {
        assertEquals(11, flyweight.fixed1());
        assertOctetsEquals("1234567890", flyweight.octets1());
        assertEquals((short) 2, flyweight.lengthOctets2());
        assertEquals("value string1", flyweight.string1().asString());
        assertOctetsEquals("xy", flyweight.octets2());
        assertEquals(2, flyweight.lengthOctets3());
        assertOctetsEquals("xy", flyweight.octets3());
        assertEquals(2, flyweight.lengthOctets4());
        assertOctetsEquals("xy", flyweight.octets4());
        assertOctetsEquals("xy", flyweight.extension());
    }

    static void assertRequiredValuesAndDefaults(FlatWithOctetsFW flyweight)
    {
        assertEquals(11, flyweight.fixed1());
        assertOctetsEquals("1234567890", flyweight.octets1());
        assertEquals((short) 1, flyweight.lengthOctets2());
        assertEquals("value string1", flyweight.string1().asString());
        assertOctetsEquals("x", flyweight.octets2());
        assertEquals(-1, flyweight.lengthOctets3());
        assertNull(flyweight.octets3());
        assertEquals(-1, flyweight.lengthOctets4());
        assertNull(flyweight.octets4());
        assertOctetsEquals("", flyweight.extension());
    }
}