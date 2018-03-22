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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.stream.IntStream;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.inner.ContiguousSizeFieldsFW;

public class ContiguousSizeFieldsFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[100]);
    private final MutableDirectBuffer expected = new UnsafeBuffer(new byte[100]);

    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            buffer.setMemory(0, buffer.capacity(), (byte) 0xab);
            expected.setMemory(0, expected.capacity(), (byte) 0xab);
        }
    }

    private final ContiguousSizeFieldsFW.Builder builder = new ContiguousSizeFieldsFW.Builder();
    private final ContiguousSizeFieldsFW flyweight = new ContiguousSizeFieldsFW();

    @Test
    public void shouldSetAllValues() throws Exception
    {
        int limit = builder.wrap(buffer, 0, buffer.capacity())
                .array1(IntStream.of(1).iterator())
                .array2(IntStream.of(2).iterator())
                .string1("value1")
                .array3(IntStream.of(3).iterator())
                .array4(IntStream.of(4).iterator())
                .build()
                .limit();

        int pos = 0;
        expected.putByte(pos, (byte) 1); // length1
        expected.putByte(pos += 1, (byte) 1); // length2
        expected.putByte(pos += 1, (byte) 1); // array1
        expected.putByte(pos += 1, (byte) 2); // array1
        expected.putInt(pos += 1, (byte) 6); // length string1
        expected.putStringWithoutLengthUtf8(pos += 1,  "value1");
        expected.putByte(pos += 6, (byte) 1); // length3
        expected.putByte(pos += 1, (byte) 1); // length4
        expected.putByte(pos += 1, (byte) 3); // array3
        expected.putByte(pos += 1, (byte) 4); // array4

        assertEquals(pos + 1, limit);
        assertEquals(expected.byteBuffer(), buffer.byteBuffer());

        flyweight.wrap(buffer,  0,  limit);
        assertEquals(1, flyweight.array1().next().intValue());
        assertEquals(2, flyweight.array2().next().intValue());
        assertEquals("value1", flyweight.string1().asString());
        assertEquals(3, flyweight.array3().next().intValue());
        assertEquals(4, flyweight.array4().next().intValue());
    }

    @Test
    public void shouldDefaultValues() throws Exception
    {
        int limit = builder.wrap(buffer, 0, 100)
                .string1("value1")
                .build()
                .limit();

        int pos = 0;
        expected.putByte(pos, (byte) -1); // length1
        expected.putByte(pos += 1, (byte) -1); // length2
        expected.putByte(pos += 1, (byte) 6);   // length string1
        expected.putStringWithoutLengthUtf8(pos += 1,  "value1");
        expected.putByte(pos += 6, (byte) -1); // length3
        expected.putByte(pos += 1, (byte) -1); // length4

        assertEquals(pos + 1, limit);
        assertArrayEquals(expected.byteArray(), buffer.byteArray());

        flyweight.wrap(buffer, 0, 100);
        assertNull(flyweight.array1());
        assertNull(flyweight.array2());
        assertNull(flyweight.array3());
        assertNull(flyweight.array4());
    }

    @Test(expected =  AssertionError.class)
    public void shouldFailToBuildWithoutString1()
    {
        builder.wrap(buffer, 10, 10)
               .build();
    }

}
