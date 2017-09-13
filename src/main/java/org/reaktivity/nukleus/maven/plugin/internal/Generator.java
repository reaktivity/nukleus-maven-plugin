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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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

public class Generator
{
    private String scopeNames = "test";
    private File inputDirectory = new File("src/test/resources/test-project");
    private File outputDirectory = new File("target/generated-test-sources/test-reaktivity");
    private String packageName = "org.reaktivity.reaktor.internal.test.types";

    private Parser parser = new Parser()
            .debug(System.out::println)
            .error(System.out::println)
            .warn(System.out::println);

    public static void main(
        String[] args) throws IOException
    {
        Generator generator = new Generator();
        if (args.length > 0)
        {
            generator.setOutputDirectory(new File(args[0]));
        }
        generator.generate();
    }

    void generate() throws IOException
    {
        generate(createClassLoader());
    }

    void generate(ClassLoader loader) throws IOException
    {
            List<String> targetScopes = unmodifiableList(asList(scopeNames.split("\\s+")));
            List<AstSpecificationNode> specifications = parser.parseAST(targetScopes, loader);

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

            System.out.println("Generating to " + outputDirectory);
            for (TypeSpecGenerator<?> typeSpec : typeSpecs)
            {
                JavaFile sourceFile = JavaFile.builder(typeSpec.className().packageName(), typeSpec.generate())
                        .addFileComment("TODO: license")
                        .skipJavaLangImports(true)
                        .build();
                sourceFile.writeTo(outputDirectory);
            }
    }

    public void setScopeNames(
        String scopeNames)
    {
        this.scopeNames = scopeNames;
    }

    public void setPackageName(
        String packageName)
    {
        this.packageName = packageName;
    }

    public void setInputDirectory(
        File inputDirectory)
    {
        this.inputDirectory = inputDirectory;
    }

    public void setOutputDirectory(
        File outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    private ClassLoader createClassLoader() throws MalformedURLException
    {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        return new URLClassLoader(new URL[]{inputDirectory.getAbsoluteFile().toURI().toURL()}, parent);
    }

}
