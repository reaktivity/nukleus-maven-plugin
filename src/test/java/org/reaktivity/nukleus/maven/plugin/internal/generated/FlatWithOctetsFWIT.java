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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.FlatWithOctetsFW;

public class FlatWithOctetsFWIT
{
    FlatWithOctetsFW.Builder flatRW = new FlatWithOctetsFW.Builder();
    FlatWithOctetsFW flatRO = new FlatWithOctetsFW();
    MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100));

    @Test
    public void shouldDefaultValues() throws Exception
    {
        // Set an explicit value first in the same memory to make sure it really
        // gets set to the default value next time round
        flatRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(10)
                .extension(b -> b.reset())
                .build();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(10, flatRO.fixed1());

        flatRW.wrap(buffer, 0, 100)
                .extension(b -> b.reset())
                .build();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(111, flatRO.fixed1());
    }

    @Test //TODO: expected...
    public void shouldFailToBuildIfRequiredMemberNotSetextension() throws Exception
    {
        flatRW.wrap(buffer, 0, 100)
            .build();
    }

    @Test
    public void shouldSetAllValues() throws Exception
    {
        final byte[] extensionValue = "asdf".getBytes(UTF_8);
        flatRW.wrap(buffer, 0, buffer.capacity())
                .fixed1(10)
                .extension(b -> b.reset().put(extensionValue))
                .build();
        flatRO.wrap(buffer,  0,  100);
        assertEquals(10, flatRO.fixed1());
        byte[] extension = flatRO.extension().get((buffer, offset, maxLimit) ->
        {
            byte[] result = new byte[extensionValue.length];
            buffer.getBytes(offset, result);
            return result;
        });
        assertArrayEquals(extensionValue, extension);
    }

}
