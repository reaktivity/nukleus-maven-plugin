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

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class TestGenerateMojoRule extends MojoRule
{

    private PlexusConfiguration configuration;
    private TestGenerateMojo testMojo;

    public TestGenerateMojoRule() throws Exception
    {
        File pom = new File("src/test/resources/test-project/pom.xml");
        configuration = extractPluginConfiguration("nukleus-maven-plugin", pom);
    }

    TestGenerateMojoRule scopeNames(String scopeNames)
    {
        configuration.addChild("scopeNames", scopeNames);
        return this;
    }

    TestGenerateMojoRule packageName(String packageName)
    {
        configuration.addChild("packageName", packageName);
        return this;
    }

    TestGenerateMojoRule inputDirectory(String inputDirectory)
    {
        configuration.addChild("inputDirectory", inputDirectory);
        return this;
    }

    TestGenerateMojoRule outputTestDirectory(String outputDirectory)
    {
        configuration.addChild("outputTestDirectory", outputDirectory);
        return this;
    }

    public void generate() throws Exception
    {
        configureMojo(testMojo, configuration);
        testMojo.execute();
    }

    @Override
    public Statement apply(
        Statement base,
        Description description)
    {
        Statement myStatement = new Statement()
        {

            @Override
            public void evaluate() throws Throwable
            {
                MavenProject project = readMavenProject(new File("src/test/resources/test-project"));
                testMojo = (TestGenerateMojo) lookupConfiguredMojo(project, "test-generate");
                assertNotNull(testMojo);
                base.evaluate();
            }

        };
        return super.apply(myStatement, description);

    }
}
