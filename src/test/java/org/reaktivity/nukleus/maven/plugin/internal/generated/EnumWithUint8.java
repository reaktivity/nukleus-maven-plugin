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

public enum EnumWithUint8
{
    ICHI(201),

    NI(202),

    SAN(203);

    private final int value;

    EnumWithUint8(
        int value)
    {
        this.value = value;
    }

    public int value()
    {
        return value;
    }

    public static EnumWithUint8 valueOf(
        int value)
    {
        switch (value)
        {
        case 201:
            return ICHI;
        case 202:
            return NI;
        case 203:
            return SAN;
        }
        return null;
    }
}
