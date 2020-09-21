/**
 * Copyright 2016-2020 The Reaktivity Project
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
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;
import org.reaktivity.reaktor.internal.test.types.inner.ListWithArrayOfStructFW;

public class ListWithArrayOfStructFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };
    private final ListWithArrayOfStructFW.Builder listWithArrayRW = new ListWithArrayOfStructFW.Builder();
    private final ListWithArrayOfStructFW listWithArrayRO = new ListWithArrayOfStructFW();
    private final int lengthSize = Byte.BYTES;
    private final int fieldCountSize = Byte.BYTES;
    private final byte kindList8 = EnumWithInt8.TWO.value();
    private final int kindSize = Byte.BYTES;
    private final byte kindArray8 = EnumWithInt8.EIGHT.value();
    private final byte kindString8 = EnumWithInt8.NINE.value();

    @Test
    public void shouldSetAllFields() throws Exception
    {
        int limit = listWithArrayRW.wrap(buffer, 0, buffer.capacity())
            .requiredField("string0")
            .arrayFieldItem(c -> c.fixed1(1L))
            .arrayFieldItem(c -> c.fixed1(2L))
            .build()
            .limit();

        final ListWithArrayOfStructFW listWithArray = listWithArrayRO.wrap(buffer, 0, limit);

        assertEquals(48, listWithArray.limit());
        assertEquals(2, listWithArray.fieldCount());
    }
}
