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

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.reaktivity.reaktor.internal.test.types.inner.NestedFW;

public class NestedFWTest
{
    private final NestedFW.Builder nestedRW = new NestedFW.Builder();
    private final NestedFW nestedRO = new NestedFW();
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldDefaultValues() throws Exception
    {
        // Set an explicit value first in the same memory to make sure it really
        // gets set to the default value next time round
        int limit1 = nestedRW.wrap(buffer, 0, buffer.capacity())
                .fixed4(40)
                .flat(flat -> flat
                    .fixed1(10)
                    .fixed2(20)
                    .string1("value1")
                    .fixed3(30)
                    .string2("value2")
                 )
                .fixed5(50)
                .build()
                .limit();
        nestedRO.wrap(buffer,  0,  limit1);
        assertEquals(40, nestedRO.fixed4());
        assertEquals(20, nestedRO.flat().fixed2());

        int limit2 = nestedRW.wrap(buffer, 0, 100)
                .flat(flat -> flat
                    .fixed1(10)
                    .string1("value1")
                    //.fixed3(30)
                    .string2("value2")
                )
                .fixed5(50)
                .build()
                .limit();
        nestedRO.wrap(buffer,  0,  limit2);
        assertEquals(444, nestedRO.fixed4());
        assertEquals(222, nestedRO.flat().fixed2());
        assertEquals(333, nestedRO.flat().fixed3());
        assertEquals(limit1, limit2);
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetFixed2BeforeFixed1() throws Exception
    {
        nestedRW.wrap(buffer, 0, 100)
                .flat(flat -> flat
                    .fixed2(10)
                )
                .build()
                .limit();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToSetFixed5BeforeFlatFixed1() throws Exception
    {
        nestedRW.wrap(buffer, 0, 100)
                .fixed5(50);
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToResetFlat() throws Exception
    {
        nestedRW.wrap(buffer, 0, 100)
            .flat(flat -> flat
                .fixed1(10)
                .string1("value1")
                .string2("value2")
            )
            .flat(flat ->
            { })
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToResetFixed4() throws Exception
    {
        nestedRW.wrap(buffer, 0, 100)
            .fixed4(40)
            .fixed4(4)
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToBuildWhenFlatIsNotSet() throws Exception
    {
        nestedRW.wrap(buffer, 0, 100)
            .fixed5(12L)
            .build();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailToBuildWhenFixed5IsNotSet() throws Exception
    {
        nestedRW.wrap(buffer, 0, 100)
            .flat(flat -> flat
                .fixed1(10)
                .string1("value1")
                .string2("value2")
            )
            .build();
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        int limit = nestedRW.wrap(buffer, 0, buffer.capacity())
                .fixed4(40)
                .flat(flat -> flat
                    .fixed1(10)
                    .fixed2(20)
                    .string1("value1")
                    .fixed3(30)
                    .string2("value2")
                 )
                .fixed5(50)
                .build()
                .limit();
        nestedRO.wrap(buffer,  0,  limit);
        assertEquals(40, nestedRO.fixed4());
        assertEquals(10, nestedRO.flat().fixed1());
        assertEquals(20, nestedRO.flat().fixed2());
        assertEquals("value1", nestedRO.flat().string1().asString());
        assertEquals(30, nestedRO.flat().fixed3());
        assertEquals("value2", nestedRO.flat().string2().asString());
        assertEquals(50, nestedRO.fixed5());
        assertEquals(nestedRO.flat().string2().limit() + 8, limit);
    }

}
