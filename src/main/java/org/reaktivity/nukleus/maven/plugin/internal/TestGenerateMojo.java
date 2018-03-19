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

import org.apache.maven.plugins.annotations.Mojo;
import java.io.IOException;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_TEST_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

@Mojo(name = "test-generate",
      defaultPhase = GENERATE_TEST_SOURCES,
      requiresDependencyResolution = TEST,
      requiresProject = true)
public final class TestGenerateMojo extends AbstractGenerateMojo
{
    @Override
    protected void executeImpl() throws IOException
    {
        Generator generator = new Generator();
        generator.debug(getLog()::debug);
        generator.error(getLog()::error);
        generator.warn(getLog()::warn);
        generator.setPackageName(packageName);
        generator.setInputDirectory(inputDirectory);
        generator.setOutputDirectory(outputTestDirectory);
        generator.setOutputTestDirectory(outputTestDirectory);
        generator.setScopeNames(scopeNames);
        generator.testGenerate(createLoader());
        project.addTestCompileSourceRoot(outputTestDirectory.getPath());
    }
}
