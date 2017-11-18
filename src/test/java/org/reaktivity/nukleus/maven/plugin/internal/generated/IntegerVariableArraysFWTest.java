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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
            setMemory(0, capacity(), (byte) 0xFF);
        }
    };
    private final MutableDirectBuffer expected = new UnsafeBuffer(allocateDirect(100))
    {
        {
            setMemory(0, capacity(), (byte) 0xFF);
        }
    };
    private final IntegerVariableArraysFW.Builder flyweightRW = new IntegerVariableArraysFW.Builder();
    private final IntegerVariableArraysFW flyweightRO = new IntegerVariableArraysFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToBuildWithInsufficientSpace()
    {
        flyweightRW.wrap(buffer, 10, 15)
            .appendSigned16Array((short) 0)
            .build();
    }

    @Test
    public void shouldDefaultAllValuesWithDefaults()
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
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());

        flyweightRO.wrap(buffer,  0,  buffer.capacity());
        assertEquals(0, flyweightRO.fixed1());
        assertEquals(0, flyweightRO.fixed2());
        assertNull(flyweightRO.unsigned64Array());
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        flyweightRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(11)
                .fixed2((short) 22)
                .appendUnsigned64Array(10)
                .appendUnsigned64Array(112345)
                .appendUnsigned64Array(11234567)
                .appendSigned16Array((short) 2)
                .appendSigned16Array((short) -500)
                .build();
        expected.putByte(0, (byte) 11); // fixed1
        expected.putInt(1, 3); // lengthUnsigned64
        expected.putShort(5, (short) 22); // fixed2
        expected.putInt(7, 0); // varint32Array
        expected.putLong(11, 10); // unsigned64Array
        expected.putLong(19, 112345); // unsigned64Array
        expected.putLong(27, 11234567); // unsigned64Array
        expected.putByte(35, (byte) 2); // lengthSigned16
        expected.putShort(36,  (short) 2); // signed16Array
        expected.putShort(38,  (short) -500); // signed16Array
        expected.putInt(40, 0); // varint64Array
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());

        flyweightRO.wrap(buffer,  0,  buffer.capacity());
        PrimitiveIterator.OfLong unsigned64 = flyweightRO.unsigned64Array();
        assertEquals(11, flyweightRO.fixed1());
        assertEquals(10L, unsigned64.nextLong());
        assertEquals(112345, unsigned64.nextLong());
        assertEquals(11234567, unsigned64.nextLong());
        PrimitiveIterator.OfInt signed16 = flyweightRO.signed16Array();
        assertEquals(2, signed16.nextInt());
        assertEquals(-500, signed16.nextInt());
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
        System.out.println(flyweightRO);
        System.out.println(flyweightRO);
        assertTrue(flyweightRO.toString().contains("unsigned64Array=[10, 1112345, 11234567]"));
        assertTrue(flyweightRO.toString().contains("signed16Array=[2, -500]"));
    }

}
