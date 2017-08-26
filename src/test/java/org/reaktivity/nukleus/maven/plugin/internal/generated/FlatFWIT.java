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
import org.reaktivity.reaktor.internal.test.types.FlatFW;

public class FlatFWIT
{
    FlatFW.Builder flatRW = new FlatFW.Builder();
    FlatFW flatRO = new FlatFW();
    MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100));

    @Test
    public void shouldFailIfRequiredFixedMemberNotSet() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
                .fixed2(10)
                .variable1("value")
                .extension(b -> b.reset())
                .build();
    }

    @Test
    public void shouldFailIfRequiredVarableMemberNotSet() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
                .fixed1(10)
                .extension(b -> b.reset())
                .build();
    }

    @Test
    public void shouldFailIfRequiredOctetsMemberNotSet() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
                .fixed1(10)
                .variable1("value")
                .build();
    }

    @Test
    public void shouldDefaultValues() throws Exception
    {
        // Set an explicit value first in the same memory to make sure it really gets reset
        flatRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(10)
                .fixed2(111)
                .variable1("value")
                .extension(b -> b.reset())
                .build();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(111, flatRO.fixed2());

        flatRW.wrap(buffer, 0, 100)
                .variable1("value")
                .extension(b -> b.reset())
                .build();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(222, flatRO.fixed2());
    }

}
