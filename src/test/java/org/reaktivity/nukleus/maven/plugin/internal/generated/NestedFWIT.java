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
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.NestedFW;

public class NestedFWIT
{
    NestedFW.Builder nestedRW = new NestedFW.Builder();
    NestedFW nestedRO = new NestedFW();
    MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100));

    @Test
    public void shouldDefaultValues() throws Exception
    {
        // Set an explicit value first in the same memory to make sure it really
        // gets set to the default value next time round
        nestedRW.wrap(buffer, 0, buffer.capacity())
                .fixed4(14)
                .flat(flat ->
                    flat
                    .fixed1(10)
                    .fixed2(20)
                    .string1("value1")
                    .fixed3(33)
                    .string2("value2")
                    .build()
                )
                .fixed5(55)
                .build();
        nestedRO.wrap(buffer,  0,  100);
        assertEquals(14, nestedRO.fixed4());
        assertEquals(20, nestedRO.flat().fixed2());

        nestedRW.wrap(buffer, 0, buffer.capacity())
        .fixed4(14)
        .flat(flat ->
            flat
            .fixed1(10)
            .fixed2(20)
            .string1("value1")
            .fixed3(33)
            .string2("value2")
            .build()
        )
        .fixed5(55)
        .build();
        nestedRO.wrap(buffer,  0,  100);
        assertEquals(444, nestedRO.fixed4());
        assertEquals(222, nestedRO.flat().fixed2());
    }

    @Test // TODO (expected = UnsupportedOperationException.class)
    public void shouldFailToSetMemberFollowingUnsetRequiredMemberflatfixed5() throws Exception
    {
        nestedRW.wrap(buffer, 0, 100)
                .fixed5(10);
    }

    @Test //TODO: expected...
    public void shouldFailToBuildIfRequiredMemberNotSetflat() throws Exception
    {
        nestedRW.wrap(buffer, 0, 100)
            .build();
    }

    @Test //TODO: expected...
    public void shouldFailToBuildIfRequiredMemberNotSetfixed5() throws Exception
    {
        nestedRW.wrap(buffer, 0, 100)
            .flat(flat ->
            {
                flat
                .fixed1(10)
                .string1("value1")
                .fixed3(33)
                .string2("value2")
                .build();
            })
            .build();
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        nestedRW.wrap(buffer, 0, buffer.capacity())
        .fixed4(14)
        .flat(flat ->
        {
            flat
            .fixed1(10)
            .fixed2(20)
            .string1("value1")
            .fixed3(33)
            .string2("value2");
        })
        .fixed5(55)
        .build();
        nestedRO.wrap(buffer,  0,  100);
        assertEquals(14, nestedRO.fixed4());
        assertEquals(20, nestedRO.flat().fixed2());
    }

}
