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

import org.agrona.collections.Long2ObjectHashMap;

public enum EnumWithUint64
{
    ICHI(4000000001L),

    NI(4000000002L),

    SAN(4000000003L);

    private static final Long2ObjectHashMap<EnumWithUint64> VALUE_BY_LONG;

    static
    {
        Long2ObjectHashMap<EnumWithUint64> valueByLong = new Long2ObjectHashMap<>();
        valueByLong.put(4000000001L, ICHI);
        valueByLong.put(4000000002L, NI);
        valueByLong.put(4000000003L, SAN);
        VALUE_BY_LONG = valueByLong;
    }

    private final long value;

    EnumWithUint64(
        long value)
    {
        this.value = value;
    }

    public long value()
    {
        return value;
    }

    public static EnumWithUint64 valueOf(
        long value)
    {
        return VALUE_BY_LONG.get(value);
    }
}

