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
package org.reaktivity.nukleus.maven.plugin.internal;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class GenerateMojoTest
{
    @Rule
    public GenerateMojoRule rule = new GenerateMojoRule();

    @Test(expected = ParseCancellationException.class)
    public void shouldNotGenerateInvalidStructOctetsNotLast()
        throws Exception
    {
        rule.scopeNames("invalidOctetsNotLast")
            .packageName("org.reaktivity.reaktor.internal.test.types")
            .inputDirectory("src/test/resources/test-project")
            .outputDirectory("target/generated-test-sources/test-reaktivity")
            .execute();
    }

    @Test // TODO: (expected = ParseCancellationException.class)
    public void shouldNotGenerateUnrecognizedType()
        throws Exception
    {
        rule.scopeNames("invalidUnrecognizedType")
            .packageName("org.reaktivity.reaktor.internal.test.types")
            .inputDirectory("src/test/resources/test-project")
            .outputDirectory("target/generated-test-sources/test-reaktivity")
            .execute();
    }

    @Test(expected = ParseCancellationException.class)
    @Ignore("TODO: validate this in the grammar by defining unbounded_struct_type")
    public void shouldNotGenerateInvalidStructOctetsNotLastNested()
        throws Exception
        {
            rule.scopeNames("invalidOctetsNotLastNested")
                .packageName("org.reaktivity.reaktor.internal.test.types")
                .inputDirectory("src/test/resources/test-project")
                .outputDirectory("target/generated-test-sources/test-reaktivity")
                .execute();
    }
}
