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

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate",
      defaultPhase = GENERATE_SOURCES,
      requiresDependencyResolution = COMPILE,
      requiresProject = true)
public final class GenerateMojo extends AbstractMojo
{
    @Parameter(defaultValue = "")
    protected String packageName;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/reaktivity")
    protected File outputDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/reaktivity")
    protected File outputTestDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            executeImpl();
        }
        catch (IOException e)
        {
            throw new MojoFailureException("Unable to generate sources", e);
        }
    }

    @Override
    protected void executeImpl() throws IOException
    {
        Generator generator = new Generator();
        generator.debug(getLog()::debug);
        generator.error(getLog()::error);
        generator.warn(getLog()::warn);
        generator.setPackageName(packageName);
        generator.setInputDirectory(inputDirectory);
        generator.setOutputDirectory(outputDirectory);
        generator.setOutputTestDirectory(outputTestDirectory);
        generator.setScopeNames(scopeNames);
        generator.generate(createLoader());
        project.addCompileSourceRoot(outputDirectory.getPath());
    }
}
