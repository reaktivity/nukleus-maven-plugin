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
import static org.junit.Assert.assertNotNull;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;

public class UnionWithEnumFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final UnionWithEnumFW.Builder flyweightRW = new UnionWithEnumFW.Builder();
    private final UnionWithEnumFW flyweightRO = new UnionWithEnumFW();

    @Test
    public void shouldSetVariantOfList()
    {
        MutableDirectBuffer listBuffer = new UnsafeBuffer(allocateDirect(30));
        ListWithEnumAndVariantWithDefaultFW listWithEnumAndVariantWithDefault =
            new ListWithEnumAndVariantWithDefaultFW.Builder()
                .wrap(listBuffer, 0, listBuffer.capacity())
                .field1(EnumWithVariantOfUint64.TYPE3)
                .field2(EnumWithVariantOfUint64.TYPE4)
                .field3(1000)
                .field4(20)
                .field5(30)
                .build();

        final UnionWithEnumFW unionWithEnum = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .listWithEnumAndVariantWithDefault(listWithEnumAndVariantWithDefault)
            .build();

        assertEquals(EnumWithVariantOfUint64.TYPE2, unionWithEnum.kind());
        assertNotNull(unionWithEnum.listWithEnumAndVariantWithDefault());
        assertEquals(EnumWithVariantOfUint64.TYPE3, unionWithEnum.listWithEnumAndVariantWithDefault().field1());
    }
}
