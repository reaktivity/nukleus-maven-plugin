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

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstSpecificationNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.visit.ScopeVisitor;
import org.reaktivity.nukleus.maven.plugin.internal.generate.FlyweightGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.ListFlyweightGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.OctetsFlyweightGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.String16FlyweightGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.StringFlyweightGenerator;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeResolver;
import org.reaktivity.nukleus.maven.plugin.internal.generate.TypeSpecGenerator;

import com.squareup.javapoet.JavaFile;

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

    protected void executeImpl() throws IOException
    {
        List<String> targetScopes = unmodifiableList(asList(scopeNames.split("\\s+")));
        List<AstSpecificationNode> specifications = parseAST(targetScopes);

        TypeResolver resolver = new TypeResolver(packageName);
        specifications.forEach(resolver::visit);

        Collection<TypeSpecGenerator<?>> typeSpecs = new HashSet<>();
        for (AstSpecificationNode specification : specifications)
        {
            String scopeName = specification.scope().name();
            ScopeVisitor visitor = new ScopeVisitor(scopeName, packageName, resolver, targetScopes);
            typeSpecs.addAll(specification.accept(visitor));
        }

        typeSpecs.add(new FlyweightGenerator(resolver.resolveClass(AstType.STRUCT)));
        typeSpecs.add(new OctetsFlyweightGenerator(resolver.resolveClass(AstType.STRUCT)));
        typeSpecs.add(new StringFlyweightGenerator(resolver.resolveClass(AstType.STRUCT)));
        typeSpecs.add(new String16FlyweightGenerator(resolver.resolveClass(AstType.STRUCT)));
        typeSpecs.add(new ListFlyweightGenerator(resolver.resolveClass(AstType.STRUCT)));

        for (TypeSpecGenerator<?> typeSpec : typeSpecs)
        {
            JavaFile sourceFile = JavaFile.builder(typeSpec.className().packageName(), typeSpec.generate())
                    .addFileComment("TODO: license")
                    .skipJavaLangImports(true)
                    .build();
            sourceFile.writeTo(outputDirectory);
        }

        project.addCompileSourceRoot(outputDirectory.getPath());
    }
}
