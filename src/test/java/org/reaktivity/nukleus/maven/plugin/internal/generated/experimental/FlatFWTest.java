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

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FlatFWTest
{
    private final FlatFW.Builder flatRW = new FlatFW.Builder();
    private final FlatFW flatRO = new FlatFW();
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100));
    private final StringFW.Builder stringRW = new StringFW.Builder();
    private final MutableDirectBuffer valueBuffer = new UnsafeBuffer(allocateDirect(100));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldDefaultValues() throws Exception
    {
        // Set an explicit value first in the same memory to make sure it really
        // gets set to the default value next time round
        int limit1 = flatRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(10)
                .fixed2(20)
                .string1("value1")
                .fixed3(30)
                .string2("value2")
                .build()
                .limit();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(20, flatRO.fixed2());
        assertEquals(30, flatRO.fixed3());

        int limit2 = flatRW.wrap(buffer, 0, 100)
                .fixed1(10)
                .string1("value1")
                .string2("value2")
                .build()
                .limit();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(222, flatRO.fixed2());
        assertEquals(333, flatRO.fixed3());
        assertEquals(limit1, limit2);
    }

    @Test
    public void shouldFailToSetFixed2WhenFixed1IsNotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
                .fixed2(10);
    }

    @Test
    public void shouldFailToSetstring1Whenfixed1IsNotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
                .string1("value1");
    }

    @Test
    public void shouldFailToSetString2Whenfixed1IsNotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
                .string2("value1");
    }

    @Test
    public void shouldFailToSetstring2Whenstring1IsNotSet() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        flatRW.wrap(buffer, 0, 100)
                .fixed1(10)
                .string2("value1");
    }

    @Test
    public void shouldFailToResetfixed1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
            .fixed1(10)
            .fixed1(101)
            .build();
    }

    @Test
    public void shouldFailToResetstring1() throws Exception
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
    public void shouldFailToBuildIfRequiredMemberNotSetfixed1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("fixed1");
        flatRW.wrap(buffer, 0, 100)
            .build();
    }

    @Test
    public void shouldFailToBuildIfRequiredMemberNotSetstring1() throws Exception
    {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("string1");
        flatRW.wrap(buffer, 0, 100)
            .fixed1(10)
            .build();
    }

    @Test
    public void shouldFailToBuildIfRequiredMemberNotSetstring2() throws Exception
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

    @Test
    public void shouldSetStringValuesToNull() throws Exception
    {
        flatRW.wrap(buffer, 0, buffer.capacity())
            .fixed1(10)
            .fixed2(20)
            .string1((String) null)
            .fixed3(30)
            .string2((String) null)
            .build();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(10, flatRO.fixed1());
        assertEquals(20, flatRO.fixed2());
        assertEquals("", flatRO.string1().asString());
        assertEquals(30, flatRO.fixed3());
        assertEquals("", flatRO.string2().asString());
    }

}
