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
package org.reaktivity.nukleus.maven.plugin.internal.generated.experimental;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import javax.xml.bind.DatatypeConverter;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StringFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xF);
        }
    };
    private final StringFW.Builder stringRW = new StringFW.Builder();
    private final StringFW stringRO = new StringFW();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldDefaultValues() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, buffer.capacity())
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(1, stringRO.sizeof());

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailWrapWithInsufficientLength()
    {
        stringRW.wrap(buffer, 10, 10);
    }

    @Test
    public void shouldWrapWithSufficientLength()
    {
        stringRW.wrap(buffer, 10, 11);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldFailWhenSettingValueExceedsMaxLimit()
    {
        buffer.setMemory(0,  buffer.capacity(), (byte) 0x00);
        try
        {
            stringRW.wrap(buffer, 10, 11)
                .set("1", UTF_8);
        }
        finally
        {
            byte[] bytes = new byte[2];
            buffer.getBytes(10, bytes);
            // Make sure memory was not written beyond maxLimit
            assertEquals("Buffer shows memory was written beyond maxLimit: " + DatatypeConverter.printHexBinary(bytes),
                         0, buffer.getByte(1));
        }
    }

    @Test
    public void shouldSetValueUsingString() throws Exception
    {
        int limit = stringRW.wrap(buffer, 0, buffer.capacity())
                .set("value1", UTF_8)
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(7, stringRO.sizeof());
        assertEquals("value1", stringRO.asString());
    }

    @Test
    public void shouldSetValuesUsingStringFW() throws Exception
    {
        StringFW value = new StringFW.Builder()
                .wrap(buffer, 50, buffer.capacity())
                .set("value1", UTF_8)
                .build();
        int limit = stringRW.wrap(buffer, 0, 50)
                .set(value)
                .build()
                .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(7, stringRO.sizeof());
        assertEquals("value1", stringRO.asString());
    }

    @Test
    public void shouldSetStringValuesUsingBuffer() throws Exception
    {
        buffer.putStringWithoutLengthUtf8(50, "value1");
        int limit = stringRW.wrap(buffer, 0, 50)
            .set(buffer, 50, 6)
            .build()
            .limit();
        stringRO.wrap(buffer,  0,  limit);
        assertEquals(7, stringRO.sizeof());
        assertEquals("value1", stringRO.asString());
    }

}
