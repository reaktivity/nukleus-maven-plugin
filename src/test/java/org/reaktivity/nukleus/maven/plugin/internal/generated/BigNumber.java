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

public enum BigNumber
{
    TEN(0x10L),

    ELEVEN(0x20L),

    TWELVE(0x30L);

    private final long value;

    BigNumber(
        long value)
    {
        this.value = value;
    }

    private static final Long2ObjectHashMap<BigNumber> BIGNUMBER_SUPPLIER;

    static
    {
        Long2ObjectHashMap<BigNumber> bigNumberSupplier = new Long2ObjectHashMap<>();
        bigNumberSupplier.put(0x10L, TEN);
        bigNumberSupplier.put(0x11L, ELEVEN);
        bigNumberSupplier.put(0x12L, TWELVE);
        BIGNUMBER_SUPPLIER = bigNumberSupplier;
    }

    public long value()
    {
        return value;
    }

    public static BigNumber valueOf(
        long value)
    {
        BigNumber enumValue = BIGNUMBER_SUPPLIER.get(value);
        if (enumValue == null)
        {
            throw new IllegalArgumentException(String.format("Unrecognized value: %d", value));
        }
        return enumValue;
    }
}
