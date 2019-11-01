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

import org.agrona.BitUtil;
import org.agrona.MutableDirectBuffer;

public final class List32FW extends ListFW
{
    private static final int PHYSICAL_LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    private static final int LOGICAL_LENGTH_SIZE = BitUtil.SIZE_OF_INT;

    private static final int PHYSICAL_LENGTH_OFFSET = 0;

    private static final int LOGICAL_LENGTH_OFFSET = PHYSICAL_LENGTH_OFFSET + PHYSICAL_LENGTH_SIZE;

    @Override
    public int limit()
    {
        return offset() + PHYSICAL_LENGTH_SIZE + LOGICAL_LENGTH_SIZE;
    }

    @Override
    public int physicalLength()
    {
        return buffer().getInt(offset() + PHYSICAL_LENGTH_OFFSET);
    }

    @Override
    public int logicalLength()
    {
        return buffer().getInt(offset() + LOGICAL_LENGTH_OFFSET);
    }

    @Override
    public int lengthSize()
    {
        return PHYSICAL_LENGTH_SIZE;
    }

    public static final class Builder extends ListFW.Builder<List32FW>
    {
        public Builder()
        {
            super(new List32FW());
        }

        public Builder physicalLength(
            int physicalLength)
        {
            int newLimit = limit() + BitUtil.SIZE_OF_INT;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), physicalLength);
            limit(newLimit);
            return this;
        }

        public Builder logicalLength(
            int logicalLength)
        {
            int newLimit = limit() + BitUtil.SIZE_OF_INT;
            checkLimit(newLimit, maxLimit());
            buffer().putInt(limit(), logicalLength);
            limit(newLimit);
            return this;
        }

        @Override
        public Builder wrap(
            MutableDirectBuffer buffer,
            int offset,
            int maxLimit)
        {
            super.wrap(buffer, offset, maxLimit);
            return this;
        }
    }
}
