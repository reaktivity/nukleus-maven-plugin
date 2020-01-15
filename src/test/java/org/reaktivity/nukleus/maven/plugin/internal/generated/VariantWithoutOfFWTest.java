/**
 * Copyright 2016-2019 The Reaktivity Project
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
import static org.junit.Assert.assertNotNull;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.reaktivity.reaktor.internal.test.types.String8FW;
import org.reaktivity.reaktor.internal.test.types.StringFW;
import org.reaktivity.reaktor.internal.test.types.inner.EnumWithInt8;

public class VariantWithoutOfFWTest
{
    private final MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(100))
    {
        {
            // Make sure the code is not secretly relying upon memory being initialized to 0
            setMemory(0, capacity(), (byte) 0xab);
        }
    };

    private final VariantWithoutOfFW.Builder flyweightRW = new VariantWithoutOfFW.Builder();
    private final VariantWithoutOfFW flyweightRO = new VariantWithoutOfFW();
    public static final EnumWithInt8 KIND_ONE = EnumWithInt8.ONE;
    public static final EnumWithInt8 KIND_TWO = EnumWithInt8.TWO;

    @Test
    public void shouldSetAsVariantEnumKindOfUint32()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAsVariantEnumKindOfUint32(100)
            .build()
            .limit();

        VariantWithoutOfFW variantWithoutOf = flyweightRO.wrap(buffer, 0, limit);

        assertEquals(100, variantWithoutOf.getAsVariantEnumKindOfInt8());
        assertEquals(KIND_ONE, variantWithoutOf.kind());
    }

    @Test
    public void shouldSetAsVariantEnumKindWithString32()
    {
        int limit = flyweightRW.wrap(buffer, 0, buffer.capacity())
            .setAsVariantEnumKindWithString32(asStringFW("stringValue"))
            .build()
            .limit();

        VariantWithoutOfFW variantWithoutOf = flyweightRO.wrap(buffer, 0, limit);

        assertNotNull(variantWithoutOf.getAsVariantEnumKindWithString32());
        assertEquals("stringValue", variantWithoutOf.getAsVariantEnumKindWithString32().asString());
        assertEquals(KIND_TWO, variantWithoutOf.kind());
    }

    private static StringFW asStringFW(
        String value)
    {
        MutableDirectBuffer buffer = new UnsafeBuffer(allocateDirect(Byte.SIZE + value.length()));
        return new String8FW.Builder().wrap(buffer, 0, buffer.capacity()).set(value, UTF_8).build();
    }
}
