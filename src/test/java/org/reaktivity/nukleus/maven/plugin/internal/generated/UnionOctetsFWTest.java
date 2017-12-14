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
import org.reaktivity.reaktor.internal.test.types.inner.UnionOctetsFW;

public class UnionOctetsFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final UnionOctetsFW.Builder unionRW = new UnionOctetsFW.Builder();
    private final UnionOctetsFW unionRO = new UnionOctetsFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldSetOctets4()
    {
        int limit = unionRW.wrap(buffer, 0, buffer.capacity())
               .octets4(b -> b.put("1234".getBytes(UTF_8)))
               .build()
               .limit();
        unionRO.wrap(buffer,  0,  limit);
        assertEquals("1234", unionRO.octets4().get((b, o, m) -> b.getStringWithoutLengthUtf8(o, m - o)));
        assertEquals(0, unionRO.octets16().sizeof());
        assertEquals(null, unionRO.string1().asString());
    }

    @Test
    public void shouldSetOctets16()
    {
        int limit = unionRW.wrap(buffer, 0, buffer.capacity())
               .octets16(b -> b.put("1234567890123456".getBytes(UTF_8)))
               .build()
               .limit();
        unionRO.wrap(buffer,  0,  limit);
        assertEquals("1234567890123456", unionRO.octets16().get((b, o, m) -> b.getStringWithoutLengthUtf8(o, m - o)));
        assertEquals(0, unionRO.octets4().sizeof());
        assertEquals(null, unionRO.string1().asString());
    }

    @Test
    public void shouldSetString1()
    {
        int limit = unionRW.wrap(buffer, 0, buffer.capacity())
            .string1("valueOfString1")
            .build()
            .limit();
        unionRO.wrap(buffer,  0,  limit);
        assertEquals("valueOfString1", unionRO.string1().asString());
        assertEquals(0, unionRO.octets4().sizeof());
        assertEquals(0, unionRO.octets16().sizeof());
    }

    @Test
    public void shouldSetStringWithValueNull()
    {
        int limit = unionRW.wrap(buffer, 10, buffer.capacity())
            .string1(null).build().limit();
        unionRO.wrap(buffer,  0,  limit);
        assertEquals(null, unionRO.string1().asString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetOctets4WithInsufficientSpace()
    {
        unionRW.wrap(buffer, 10, 14)
               .octets4(b -> b.put("1234".getBytes(UTF_8)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetOctets4WithValueTooLong()
    {
        unionRW.wrap(buffer, 10, buffer.capacity())
               .octets4(b -> b.put("12345".getBytes(UTF_8)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetOctets16WithInsufficientSpace()
    {
        unionRW.wrap(buffer, 10, 26)
               .octets16(b -> b.put("1234567890123456".getBytes(UTF_8)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailToSetOctets16WithValueToLong()
    {
        unionRW.wrap(buffer, 10, buffer.capacity())
               .octets16(b -> b.put("12345678901234567".getBytes(UTF_8)));
    }

    @Test
    public void shouldBuildWithNothingSet()
    {
        int limit = unionRW.wrap(buffer, 10, buffer.capacity())
            .build()
            .limit();
        unionRO.wrap(buffer,  0,  limit);
        assertEquals(0, unionRO.octets16().sizeof());
        assertEquals(0, unionRO.octets4().sizeof());
    }

}
