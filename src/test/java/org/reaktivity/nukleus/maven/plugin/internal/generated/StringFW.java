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

import java.nio.charset.Charset;

import org.agrona.DirectBuffer;

public abstract class StringFW extends Flyweight
{
    public abstract int fieldSizeLength();

    public abstract String asString();

    public abstract int length();

    public abstract static class Builder<T extends StringFW> extends Flyweight.Builder<T>
    {
        public Builder(
            T flyweight)
        {
            super(flyweight);
        }

        public abstract Builder set(StringFW value);

        public abstract Builder set(DirectBuffer srcBuffer, int srcOffset, int length);

        public abstract Builder set(String value, Charset charset);
    }
}
