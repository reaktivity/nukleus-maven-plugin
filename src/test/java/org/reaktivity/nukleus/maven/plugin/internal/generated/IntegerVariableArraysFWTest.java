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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.reaktor.internal.test.types.inner.IntegerVariableArraysFW;

public class IntegerVariableArraysFWTest
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
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final IntegerVariableArraysFW.Builder flyweightRW = new IntegerVariableArraysFW.Builder();
    private final IntegerVariableArraysFW flyweightRO = new IntegerVariableArraysFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    static int setAllTestValues(MutableDirectBuffer buffer, int offset)
    {
        buffer.putByte(offset + 0, (byte) 11); // fixed1
        buffer.putInt(offset + 1, 3); // lengthUnsigned64
        buffer.putShort(offset + 5, (short) 22); // fixed2
        buffer.putInt(offset + 7, 2); // varint32Array
        buffer.putByte(offset + 11, (byte) 1);
        buffer.putByte(offset + 12, (byte) 2);
        buffer.putLong(offset + 13, 10); // unsigned64Array
        buffer.putLong(offset + 21, 112345); // unsigned64Array
        buffer.putLong(offset + 29, 11234567); // unsigned64Array
        buffer.putByte(offset + 37, (byte) 2); // lengthSigned16
        buffer.putShort(offset + 38,  (short) 2); // signed16Array
        buffer.putShort(offset + 40,  (short) -500); // signed16Array
        buffer.putInt(offset + 42, 1); // varint64Array
        buffer.putByte(offset + 46, (byte) 0x18);
        buffer.putByte(offset + 47, (byte) 1);
        buffer.putInt(offset + 48, 123);
        buffer.putShort(offset + 52, (short) 1);
        buffer.putInt(offset + 54, 124);
        return 54 + Integer.BYTES;
    }

    static void assertAllTestValuesRead(IntegerVariableArraysFW flyweight)
    {
        PrimitiveIterator.OfLong unsigned64 = flyweight.unsigned64Array();
        assertEquals(11, flyweight.fixed1());
        List<Integer> varint32 = new ArrayList<Integer>();
        flyweight.varint32Array().forEach(v -> varint32.add(v.value()));
        assertEquals(Arrays.asList(-1, 1), varint32);
        assertEquals(10L, unsigned64.nextLong());
        assertEquals(112345, unsigned64.nextLong());
        assertEquals(11234567, unsigned64.nextLong());
        PrimitiveIterator.OfInt signed16 = flyweight.signed16Array();
        assertEquals(2, signed16.nextInt());
        assertEquals(-500, signed16.nextInt());
        List<Long> varint64 = new ArrayList<Long>();
        flyweight.varint64Array().forEach(v -> varint64.add(v.value()));
        assertEquals(Arrays.asList(12L), varint64);
        PrimitiveIterator.OfInt arrayWithInt8Size = flyweight.arrayWithInt8Size();
        assertEquals(123, arrayWithInt8Size.nextInt());
        PrimitiveIterator.OfInt arrayWithInt16Size = flyweight.arrayWithInt16Size();
        assertEquals(124, arrayWithInt16Size.nextInt());
    }

    @Test
    public void shouldNotTryWrapWhenIncomplete()
    {
        int size = setAllTestValues(buffer, 10);
        for (int maxLimit=10; maxLimit < 10 + size; maxLimit++)
        {
            assertNull(flyweightRO.tryWrap(buffer,  10, maxLimit));
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
                fail("Exception not thrown");
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
    public void shouldTryWrapWhenLengthSufficient()
    {
        int size = setAllTestValues(buffer, 10);
        assertSame(flyweightRO, flyweightRO.tryWrap(buffer, 10, 10 + size));
    }

    @Test
    public void shouldWrapWhenLengthSufficient()
    {
        int size = setAllTestValues(buffer, 10);
        assertSame(flyweightRO, flyweightRO.wrap(buffer, 10, 10 + size));
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
    public void shouldSetUnsigned64ToMaximumValue()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
               .appendUnsigned64Array(Long.MAX_VALUE)
               .appendSigned16Array((short) 0)
               .build()
               .limit();
        expected.putByte(0, (byte) 0); // fixed1
        expected.putInt(1, 1); // lengthUnsigned64
        expected.putShort(5, (short) 0); // fixed2
        expected.putInt(7, 0); // varint64Array
        expected.putLong(11, Long.MAX_VALUE); // unsigned64Array
        expected.putByte(19, (byte) 1); // lengthSigned16
        expected.putShort(20,  (short) 0); // signed16Array
        expected.putInt(22, 0); // varint64Array
        expected.putByte(26,  (byte) -1);
        expected.putShort(27,  (short) -1);
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());

        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(Long.MAX_VALUE, flyweightRO.unsigned64Array().nextLong());
    }

    @Test
    public void shouldSetUnsigned64ToMinimumValue()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
               .appendUnsigned64Array(0L)
               .appendSigned16Array((short) 0)
               .build()
               .limit();
        expected.putByte(0, (byte) 0); // fixed1
        expected.putInt(1, 1); // lengthUnsigned64
        expected.putShort(5, (short) 0); // fixed2
        expected.putInt(7, 0); // varint32Array
        expected.putLong(11, 0L); // unsigned64Array
        expected.putByte(19, (byte) 1); // lengthSigned16
        expected.putShort(20,  (short) 0); // signed16Array
        expected.putInt(22, 0); // varint64Array
        expected.putByte(26,  (byte) -1);
        expected.putShort(27,  (short) -1);
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());

        flyweightRO.wrap(buffer,  0,  limit);
        assertEquals(0L, flyweightRO.unsigned64Array().nextLong());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetUnsigned64WithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 10 + 5 + 32);
        for (int i=0; i < 5; i++)
        {
           flyweightRW.appendUnsigned64Array(i);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetUnsigned64WithValueTooLow()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
               .appendUnsigned64Array(-1L);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetSigned16WithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 10 + 6 + 4);
        for (int i=0; i < 5; i++)
        {
           flyweightRW.appendSigned16Array((short) i);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSetSigned16ToNull()
    {
        flyweightRW.wrap(buffer, 10, 10 + 6 + 4 + 4 + 4);
        flyweightRW.signed16Array(null);
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToAppendUnsigned64ArrayWhenFollowingFieldsAreSet()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
        .appendSigned16Array((short) 0)
        .appendUnsigned64Array(12L)
        .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToAppendSigned64ArrayWhenFollowingFieldsAreSet()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
        .appendArrayWithInt8Size(12)
        .appendSigned16Array((short) 0)
        .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToAppendArraytWithInt8SizeWhenFollowingFieldsAreSet()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
        .appendSigned16Array((short) 0)
        .appendArrayWithInt16Size(12)
        .appendArrayWithInt8Size(12)
        .build();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToBuildWithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 15)
            .appendSigned16Array((short) 0)
            .build();
    }

    @Test
    public void shouldDefaultAllFieldsWithDefaults()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
            .appendSigned16Array((short) 0)
            .build();

        expected.putByte(0, (byte) 0); // fixed1
        expected.putInt(1, -1); // lengthUnsigned64
        expected.putShort(5, (short) 0); // fixed2
        expected.putInt(7, 0); // varint32Array
        expected.putByte(11, (byte) 1); // lengthSigned16
        expected.putShort(12,  (short) 0); // signed16Array
        expected.putInt(14, 0); // varint64Array
        expected.putByte(18, (byte) -1);
        expected.putShort(19, (short) -1);

        assertEquals(expected.byteBuffer(), buffer.byteBuffer());

        buffer.setMemory(0, buffer.capacity(), (byte) 0xab);
        flyweightRW.wrap(buffer, 0, buffer.capacity())
        .appendSigned16Array((short) 0)
        .build();

        assertEquals(expected.byteBuffer(), buffer.byteBuffer());

        flyweightRO.wrap(buffer,  0,  buffer.capacity());
        assertEquals(0, flyweightRO.fixed1());
        assertEquals(0, flyweightRO.fixed2());
        assertNull(flyweightRO.unsigned64Array());
        assertNull(flyweightRO.arrayWithInt8Size());
        assertNull(flyweightRO.arrayWithInt16Size());
    }

    @Test
    public void shouldDefaultValuesAfterVarintArray()
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
            .appendSigned16Array((short) 0)
            .varint64Array(a -> a.item(b -> b.set(12L)))
            .build();

        expected.putByte(0, (byte) 0); // fixed1
        expected.putInt(1, -1); // lengthUnsigned64
        expected.putShort(5, (short) 0); // fixed2
        expected.putInt(7, 0); // varint32Array
        expected.putByte(11, (byte) 1); // lengthSigned16
        expected.putShort(12,  (short) 0); // signed16Array
        expected.putInt(14, 1); // varint64Array
        expected.putByte(18, (byte) 0x18);
        expected.putByte(19, (byte) -1);
        expected.putShort(20, (short) -1);

        assertEquals(expected.byteBuffer(), buffer.byteBuffer());

        flyweightRO.wrap(buffer,  0,  buffer.capacity());
        assertEquals(0, flyweightRO.fixed1());
        assertEquals(0, flyweightRO.fixed2());
        assertNull(flyweightRO.arrayWithInt8Size());
        assertNull(flyweightRO.arrayWithInt16Size());
        List<Long> varint64 = new ArrayList<Long>();
        flyweightRO.varint64Array().forEach(v -> varint64.add(v.value()));
        assertEquals(Arrays.asList(12L), varint64);

    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(11)
                .fixed2((short) 22)
                .varint32Array(a -> a.item(b -> b.set(-1))
                                     .item(b -> b.set(1)))
                .appendUnsigned64Array(10)
                .appendUnsigned64Array(112345)
                .appendUnsigned64Array(11234567)
                .appendSigned16Array((short) 2)
                .appendSigned16Array((short) -500)
                .varint64Array(a -> a.item(b -> b.set(12L)))
                .appendArrayWithInt8Size(123)
                .appendArrayWithInt16Size(124)
                .build();
        setAllTestValues(expected, 0);

        assertEquals(expected.byteBuffer(), buffer.byteBuffer());

        flyweightRO.wrap(buffer,  0,  buffer.capacity());
        assertAllTestValuesRead(flyweightRO);
    }

    @Test
    public void shouldReadAllValues() throws Exception
    {
        setAllTestValues(buffer, 20);
        flyweightRO.wrap(buffer, 20,  buffer.capacity());
        assertAllTestValuesRead(flyweightRO);
    }

    @Test
    public void shouldConvertToString() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
        .fixed1(11)
        .appendUnsigned64Array(10)
        .appendUnsigned64Array(1112345)
        .appendUnsigned64Array(11234567)
        .appendSigned16Array((short) 2)
        .appendSigned16Array((short) -500)
        .build();
        flyweightRO.wrap(buffer, 0, 100);
        assertTrue(flyweightRO.toString().contains("unsigned64Array=[10, 1112345, 11234567]"));
        assertTrue(flyweightRO.toString().contains("signed16Array=[2, -500]"));
    }

}
