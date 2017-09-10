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
package org.reaktivity.nukleus.maven.plugin.internal.generator;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.reaktivity.nukleus.maven.plugin.internal.GenerateMojo;

public class Generator
{
    class MojoTestCase extends AbstractMojoTestCase
    {
        @Override
        public void setUp() throws Exception
        {
            super.setUp();
        }
    }

    private final MojoTestCase testCase = new MojoTestCase();
    public MojoRule rule = new MojoRule(testCase);

    public static void main(
        String[] args) throws Exception
    {
        new Generator().generate();
    }

    public void generate()
        throws Exception
    {
        testCase.setUp();
        MavenProject project = rule.readMavenProject(new File("src/test/resources/test-project"));
        GenerateMojo myMojo = (GenerateMojo) rule.lookupConfiguredMojo(project, "generate");
        assertNotNull(myMojo);
        myMojo.execute();
    }

}
