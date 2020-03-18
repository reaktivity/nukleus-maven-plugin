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

import org.agrona.collections.Long2ObjectHashMap;

public enum EnumWithVariantOfUint64
{
    TYPE1(0x10L),

    TYPE2(0x11L),

    TYPE3(0x12L),

    TYPE4(0x13L),

    TYPE5(0x14L);

    private static final Long2ObjectHashMap<EnumWithVariantOfUint64> VALUE_BY_LONG;

    static
    {
        Long2ObjectHashMap<EnumWithVariantOfUint64> valueByLong = new Long2ObjectHashMap<>();
        valueByLong.put(0x10L, TYPE1);
        valueByLong.put(0x11L, TYPE2);
        valueByLong.put(0x12L, TYPE3);
        valueByLong.put(0x13L, TYPE4);
        valueByLong.put(0x14L, TYPE5);
        VALUE_BY_LONG = valueByLong;
    }

    private final long value;

    EnumWithVariantOfUint64(
        long value)
    {
        this.value = value;
    }

    public long value()
    {
        return value;
    }

    public static EnumWithVariantOfUint64 valueOf(
        long value)
    {
        return VALUE_BY_LONG.get(value);
    }
}
