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
package org.reaktivity.flyweight.maven.plugin.internal.ast.parse;

import static org.junit.Assert.assertEquals;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import org.reaktivity.maven.plugin.internal.ast.AstMemberNode;
import org.reaktivity.maven.plugin.internal.ast.AstScopeNode;
import org.reaktivity.maven.plugin.internal.ast.AstStructNode;
import org.reaktivity.maven.plugin.internal.ast.AstType;
import org.reaktivity.maven.plugin.internal.ast.parse.AstParser;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusLexer;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.MemberContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.ScopeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Struct_typeContext;

public class AstParserTest
{
    @Test
    public void shouldParseScope()
    {
        NukleusParser parser = newParser("scope common { }");
        ScopeContext ctx = parser.scope();
        AstScopeNode actual = new AstParser().visitScope(ctx);

        AstScopeNode expected = new AstScopeNode.Builder()
                .name("common")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNestedScopes()
    {
        NukleusParser parser = newParser("scope common { scope control { } scope stream { } }");
        ScopeContext ctx = parser.scope();
        AstScopeNode actual = new AstParser().visitScope(ctx);

        AstScopeNode expected = new AstScopeNode.Builder()
                .name("common")
                .scope(new AstScopeNode.Builder().depth(1).name("control").build())
                .scope(new AstScopeNode.Builder().depth(1).name("stream").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseStructWithoutMembers()
    {
        NukleusParser parser = newParser("struct Person { }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("Person")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseStructWithMembers()
    {
        NukleusParser parser = newParser("struct Person { string firstName; string lastName; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("Person")
                .member(new AstMemberNode.Builder().type(AstType.STRING).name("firstName").build())
                .member(new AstMemberNode.Builder().type(AstType.STRING).name("lastName").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseStructWithExtends()
    {
        NukleusParser parser = newParser("struct Employee extends common::Person { }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("Employee")
                .supertype("common::Person")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseInt64Member()
    {
        NukleusParser parser = newParser("int64 field;");
        MemberContext ctx = parser.member();
        AstMemberNode actual = new AstParser().visitMember(ctx);

        AstMemberNode expected = new AstMemberNode.Builder()
                .type(AstType.INT64)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseUint8Member()
    {
        NukleusParser parser = newParser("uint8 field;");
        MemberContext ctx = parser.member();
        AstMemberNode actual = new AstParser().visitMember(ctx);

        AstMemberNode expected = new AstMemberNode.Builder()
                .type(AstType.UINT8)
                .unsignedType(AstType.INT32)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseUint16Member()
    {
        NukleusParser parser = newParser("uint16 field;");
        MemberContext ctx = parser.member();
        AstMemberNode actual = new AstParser().visitMember(ctx);

        AstMemberNode expected = new AstMemberNode.Builder()
                .type(AstType.UINT16)
                .unsignedType(AstType.INT32)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseStringMember()
    {
        NukleusParser parser = newParser("string field;");
        MemberContext ctx = parser.member();
        AstMemberNode actual = new AstParser().visitMember(ctx);

        AstMemberNode expected = new AstMemberNode.Builder()
                .type(AstType.STRING)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseListMember()
    {
        NukleusParser parser = newParser("list<string> field;");
        MemberContext ctx = parser.member();
        AstMemberNode actual = new AstParser().visitMember(ctx);

        AstMemberNode expected = new AstMemberNode.Builder()
                .type(AstType.LIST)
                .type(AstType.STRING)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    private static NukleusParser newParser(
        String input)
    {
        ANTLRInputStream ais = new ANTLRInputStream(input);
        NukleusLexer lexer = new NukleusLexer(ais);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        NukleusParser parser = new NukleusParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        parser.removeErrorListeners();
        return parser;
    }
}
