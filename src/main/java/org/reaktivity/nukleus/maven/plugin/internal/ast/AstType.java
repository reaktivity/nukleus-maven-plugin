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
package org.reaktivity.nukleus.maven.plugin.internal.ast;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

public final class AstType
{
    public static final AstType INT8 = new AstType("int8", false);
    public static final AstType INT16 = new AstType("int16", false);
    public static final AstType INT32 = new AstType("int32", false);
    public static final AstType INT64 = new AstType("int64", false);
    public static final AstType VARINT32 = new AstType("varint32", false);
    public static final AstType VARINT64 = new AstType("varint64", false);

    public static final AstType UINT8 = new AstType("uint8", false);
    public static final AstType UINT16 = new AstType("uint16", false);
    public static final AstType UINT32 = new AstType("uint32", false);
    public static final AstType UINT64 = new AstType("uint64", false);

    public static final AstType OCTETS = new AstType("octets", false);
    public static final AstType STRING = new AstType("string", false);
    public static final AstType STRING16 = new AstType("string16", false);

    public static final AstType LIST = new AstType("list", false);
    public static final AstType ARRAY = new AstType("array", false);
    public static final AstType STRUCT = new AstType("struct", false);

    private final String name;
    private final boolean isDynamic;

    private AstType(
        String name,
        boolean isDynamic)
    {
        this.name = requireNonNull(name);
        this.isDynamic = isDynamic;
    }

    public String name()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
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

        AstType that = (AstType)obj;
        return Objects.equals(this.name, that.name);
    }

    boolean isSignedInteger()
    {
        return this == INT8 || this == INT16 || this == INT32 || this == INT64 || this == VARINT32 || this == VARINT64;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static AstType dynamicType(
        String scopedName)
    {
        return new AstType(scopedName, true);
    }

    public boolean isDynamic()
    {
        return isDynamic;
    }
}
