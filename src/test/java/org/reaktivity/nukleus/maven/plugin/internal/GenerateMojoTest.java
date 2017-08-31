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

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class GenerateMojoTest
{
    @Rule
    public MojoRule rule = new MojoRule();

    @Test
    public void shouldGenerate()
        throws Exception
    {
        MavenProject project = rule.readMavenProject(new File("src/test/resources/test-project"));
        GenerateMojo myMojo = (GenerateMojo) rule.lookupConfiguredMojo(project, "generate");
        assertNotNull(myMojo);
        myMojo.execute();
    }

    @Test // TODO: verify the appropriate warning message was locked to the build output
    public void shouldNotGenerateInvalidStructOctetsNotLast()
        throws Exception
    {
        File pom = new File("src/test/resources/test-project/pom.xml");
        MavenProject project = rule.readMavenProject(new File("src/test/resources/test-project"));
        GenerateMojo myMojo = (GenerateMojo) rule.lookupConfiguredMojo(project, "generate");
        assertNotNull(myMojo);

        PlexusConfiguration configuration = rule.extractPluginConfiguration("nukleus-maven-plugin", pom);
        configuration.addChild("scopeNames", "invalidOctetsNotLast");
        configuration.addChild("packageName", "org.reaktivity.reaktor.internal.test.types");
        configuration.addChild("inputDirectory", "src/test/resources/test-project");
        configuration.addChild("outputDirectory", "target/generated-flyweights");
        rule.configureMojo(myMojo, configuration);
        myMojo.execute();
    }

    @Test // TODO: verify the appropriate warning message was locked to the build output
    @Ignore("TODO: currently this gives a NullPointerException")
    public void shouldNotGenerateInvalidStructOctetsNotLastNested()
        throws Exception
        {
            File pom = new File("src/test/resources/test-project/pom.xml");
            MavenProject project = rule.readMavenProject(new File("src/test/resources/test-project"));
            GenerateMojo myMojo = (GenerateMojo) rule.lookupConfiguredMojo(project, "generate");
            assertNotNull(myMojo);

            PlexusConfiguration configuration = rule.extractPluginConfiguration("nukleus-maven-plugin", pom);
            configuration.addChild("scopeNames", "invalidOctetsNotLastNested");
            configuration.addChild("packageName", "org.reaktivity.reaktor.internal.test.types");
            configuration.addChild("inputDirectory", "src/test/resources/test-project");
            configuration.addChild("outputDirectory", "target/generated-flyweights");
            rule.configureMojo(myMojo, configuration);
            myMojo.execute();
    }

}