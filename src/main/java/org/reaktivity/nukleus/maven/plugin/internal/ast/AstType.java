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
package org.reaktivity.nukleus.maven.plugin.internal.ast;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

public final class AstType
{
    public static final AstType INT8 = new AstType("int8", 8);
    public static final AstType INT16 = new AstType("int16", 16);
    public static final AstType INT24 = new AstType("int24", 24);
    public static final AstType INT32 = new AstType("int32", 32);
    public static final AstType INT64 = new AstType("int64", 64);
    public static final AstType VARINT32 = new AstType("varint32");
    public static final AstType VARINT64 = new AstType("varint64");

    public static final AstType UINT8 = new AstType("uint8", 8);
    public static final AstType UINT16 = new AstType("uint16", 16);
    public static final AstType UINT24 = new AstType("uint24", 24);
    public static final AstType UINT32 = new AstType("uint32", 32);
    public static final AstType UINT64 = new AstType("uint64", 64);
    public static final AstType VARBYTEUINT32 = new AstType("varbyteuint32");

    public static final AstType OCTETS = new AstType("octets");
    public static final AstType STRING = new AstType("string");
    public static final AstType STRING8 = new AstType("string8");
    public static final AstType STRING16 = new AstType("string16");
    public static final AstType STRING32 = new AstType("string32");

    public static final AstType ARRAY = new AstType("array");
    public static final AstType FLYWEIGHT = new AstType("flyweight");
    public static final AstType LIST = new AstType("list");
    public static final AstType LIST0 = new AstType("list0");
    public static final AstType LIST8 = new AstType("list8");
    public static final AstType LIST32 = new AstType("list32");

    public static final AstType VARIANT = new AstType("variant");
    public static final AstType VARIANT_OF = new AstType("variantOf");
    public static final AstType VARIANT_ARRAY = new AstType("variantArray");
    public static final AstType VARIANT_ARRAY8 = new AstType("variantArray8");
    public static final AstType VARIANT_ARRAY16 = new AstType("variantArray16");
    public static final AstType VARIANT_ARRAY32 = new AstType("variantArray32");

    public static final AstType MAP = new AstType("map");
    public static final AstType MAP8 = new AstType("map8");
    public static final AstType MAP16 = new AstType("map16");
    public static final AstType MAP32 = new AstType("map32");

    private final String name;
    private final int bits;

    private AstType(
        String name)
    {
        this(name, -1);
    }

    private AstType(
        String name,
        int bits)
    {
        this.name = requireNonNull(name);
        this.bits = bits;
    }

    public String name()
    {
        return name;
    }

    public int bits()
    {
        return bits;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode() ^ Integer.hashCode(bits);
    }

    @Override
    public boolean equals(
        Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof AstType))
        {
            return false;
        }

        AstType that = (AstType) obj;
        return this.bits == that.bits &&
               Objects.equals(this.name, that.name);
    }

    boolean isSignedInt()
    {
        return this == INT8 || this == INT16 || this == INT24 || this == INT32 || this == INT64 ||
               this == VARINT32 || this == VARINT64;
    }

    public boolean isUnsignedInt()
    {
        return this == UINT8 || this == UINT16 || this == UINT24 || this == UINT32 || this == UINT64 || this == VARBYTEUINT32;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static AstType dynamicType(
        String scopedName)
    {
        return new AstType(scopedName);
    }
}
