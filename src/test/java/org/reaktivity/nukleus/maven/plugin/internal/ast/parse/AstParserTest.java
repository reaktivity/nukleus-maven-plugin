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
package org.reaktivity.nukleus.maven.plugin.internal.ast.parse;

import static org.junit.Assert.assertEquals;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstAbstractMemberNode.NULL_DEFAULT;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder.NATIVE;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder.NETWORK;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstType.INT32;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstType.dynamicType;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstListMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstListNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstScopeNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstStructMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstStructNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstTypedefNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionCaseNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstValueNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstVariantCaseNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstVariantNode;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusLexer;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Enum_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.List_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.MemberContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.OptionContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.ScopeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Struct_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Typedef_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Union_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Variant_typeContext;

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
    public void shouldParseAbsoluteTypeReferences()
    {
        NukleusParser parser = newParser("scope outer { struct Outer { } scope inner { struct Inner { } struct " +
            "Type { outer::inner::Inner inner; outer::Outer outer; } } }");
        ScopeContext ctx = parser.scope();
        AstScopeNode actual = new AstParser().visitScope(ctx);
        AstScopeNode expected = new AstScopeNode.Builder()
            .name("outer")
            .struct(new AstStructNode.Builder()
                .name("Outer")
                .build())
            .scope(new AstScopeNode.Builder()
                .depth(1)
                .name("inner")
                .struct(new AstStructNode.Builder()
                    .name("Inner")
                    .build())
                .struct(new AstStructNode.Builder()
                    .name("Type")
                    .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                        .type(dynamicType("outer::inner::Inner"))
                        .name("inner")
                        .build())
                    .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                        .type(dynamicType("outer::Outer"))
                        .name("outer")
                        .build())
                    .build())
                .build())
            .build();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseRelativeTypeReferences()
    {
        NukleusParser parser = newParser("scope one { struct One { } enum OneEnum { VALUE } union OneUnion switch (uint8) " +
            "{ case 0: uint8 width; } scope two { struct Two { } enum TwoEnum { VALUE } union TwoUnion switch (uint8) " +
            "{ case 0: uint8 width; } struct Type { Two twoStruct; TwoEnum twoEnum; TwoUnion twoUnion; One oneStruct; OneEnum " +
            "oneEnum; OneUnion oneUnion; } } }");
        ScopeContext ctx = parser.scope();
        AstScopeNode actual = new AstParser().visitScope(ctx);
        AstScopeNode expected = new AstScopeNode.Builder()
                .name("one")
                .struct(
                    new AstStructNode.Builder()
                        .name("One")
                        .build())
                .enumeration(
                    new AstEnumNode.Builder()
                        .name("OneEnum")
                        .value(
                            new AstValueNode.Builder()
                                .ordinal(0)
                                .name("VALUE")
                                .build())
                        .build())
                .union(
                    new AstUnionNode.Builder()
                        .name("OneUnion")
                        .kindType(AstType.UINT8)
                        .caseN(
                            new AstUnionCaseNode.Builder()
                                .value(0)
                                .member(
                                    (AstStructMemberNode) new AstStructMemberNode.Builder()
                                        .name("width")
                                        .type(AstType.UINT8)
                                        .build())
                                .build())
                        .build())
                .scope(
                    new AstScopeNode.Builder()
                        .depth(1)
                        .name("two")
                        .struct(new AstStructNode.Builder()
                            .name("Two")
                            .build())
                        .enumeration(
                            new AstEnumNode.Builder()
                                .name("TwoEnum")
                                .value(
                                    new AstValueNode.Builder()
                                        .ordinal(0)
                                        .name("VALUE")
                                        .build())
                                .build())
                        .union(
                            new AstUnionNode.Builder()
                                .name("TwoUnion")
                                .kindType(AstType.UINT8)
                                .caseN(
                                    new AstUnionCaseNode.Builder()
                                        .value(0)
                                        .member(
                                            (AstStructMemberNode) new AstStructMemberNode.Builder()
                                                .name("width")
                                                .type(AstType.UINT8)
                                                .build())
                                        .build())
                                .build())
                        .struct(
                            new AstStructNode.Builder()
                                .name("Type")
                                .member(
                                    (AstStructMemberNode) new AstStructMemberNode.Builder()
                                        .type(dynamicType("one::two::Two"))
                                        .name("twoStruct")
                                        .build())
                                .member(
                                    (AstStructMemberNode) new AstStructMemberNode.Builder()
                                        .type(dynamicType("one::two::TwoEnum"))
                                        .name("twoEnum")
                                        .build())
                                .member(
                                    (AstStructMemberNode) new AstStructMemberNode.Builder()
                                        .type(dynamicType("one::two::TwoUnion"))
                                        .name("twoUnion")
                                        .build())
                                .member(
                                    (AstStructMemberNode) new AstStructMemberNode.Builder()
                                        .type(dynamicType("one::One"))
                                        .name("oneStruct")
                                        .build())
                                .member(
                                    (AstStructMemberNode) new AstStructMemberNode.Builder()
                                        .type(dynamicType("one::OneEnum"))
                                        .name("oneEnum")
                                        .build())
                                .member(
                                    (AstStructMemberNode) new AstStructMemberNode.Builder()
                                        .type(dynamicType("one::OneUnion"))
                                        .name("oneUnion")
                                        .build())
                                .build())
                        .build())
                .build();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseRelativeTypeReferencesWithMultipleScopes()
    {
        NukleusParser parser = newParser("scope outer { struct Outer { } scope inner { struct In { } " +
            "struct Type1 { outer::inner::In absoluteInner; outer::Outer absoluteOuter; In relativeInner; Outer" +
            " relativeOuter; } } scope inner2 { struct In { } struct Type2 { outer::inner2::In " +
            "absoluteInner2Inner; In relativeInner2Inner; } } }");

        ScopeContext ctx = parser.scope();
        AstScopeNode actual = new AstParser().visitScope(ctx);
        AstScopeNode expected = new AstScopeNode.Builder()
            .name("outer")
            .struct(
                new AstStructNode.Builder()
                    .name("Outer")
                    .build())
            .scope(
                new AstScopeNode.Builder()
                    .depth(1)
                    .name("inner")
                    .struct(
                        new AstStructNode.Builder()
                            .name("In")
                            .build())
                    .struct(
                        new AstStructNode.Builder()
                            .name("Type1")
                            .member(
                                (AstStructMemberNode) new AstStructMemberNode.Builder()
                                    .type(dynamicType("outer::inner::In"))
                                    .name("absoluteInner")
                                    .build())
                            .member(
                                (AstStructMemberNode) new AstStructMemberNode.Builder()
                                    .type(dynamicType("outer::Outer"))
                                    .name("absoluteOuter")
                                    .build())
                            .member(
                                (AstStructMemberNode) new AstStructMemberNode.Builder()
                                    .type(dynamicType("outer::inner::In"))
                                    .name("relativeInner")
                                    .build())
                            .member(
                                (AstStructMemberNode) new AstStructMemberNode.Builder()
                                    .type(dynamicType("outer::Outer"))
                                    .name("relativeOuter")
                                    .build())
                            .build())
                    .build())
            .scope(
                new AstScopeNode.Builder()
                    .depth(1)
                    .name("inner2")
                    .struct(
                        new AstStructNode.Builder()
                            .name("In")
                            .build())
                    .struct(
                        new AstStructNode.Builder()
                            .name("Type2")
                            .member(
                                (AstStructMemberNode) new AstStructMemberNode.Builder()
                                    .type(dynamicType("outer::inner2::In"))
                                    .name("absoluteInner2Inner")
                                    .build())
                            .member(
                                (AstStructMemberNode) new AstStructMemberNode.Builder()
                                    .type(dynamicType("outer::inner2::In"))
                                    .name("relativeInner2Inner")
                                    .build())
                            .build())
                    .build())
            .build();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseOptionByteOrderNetwork()
    {
        NukleusParser parser = newParser("option byteorder network;");
        OptionContext ctx = parser.option();
        new AstParser().visitOption(ctx);
    }

    @Test
    public void shouldParseOptionByteOrderNative()
    {
        NukleusParser parser = newParser("option byteorder native;");
        OptionContext ctx = parser.option();
        new AstParser().visitOption(ctx);
    }

    @Test
    public void shouldParseScopedStructWithNetworkOrderField()
    {
        NukleusParser parser = newParser("scope common { option byteorder network; struct Holder { int32 value; } }");
        ScopeContext ctx = parser.scope();
        AstScopeNode actual = new AstParser().visitScope(ctx);

        AstScopeNode expected = new AstScopeNode.Builder()
                .name("common")
                .struct(new AstStructNode.Builder()
                    .name("Holder")
                    .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                        .type(INT32).name("value").byteOrder(NETWORK).build())
                    .build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseEnumWithValues()
    {
        NukleusParser parser = newParser("enum Coin { PENNY, NICKLE, DIME, QUARTER }");
        Enum_typeContext ctx = parser.enum_type();
        AstEnumNode actual = new AstParser().visitEnum_type(ctx);

        AstEnumNode expected = new AstEnumNode.Builder()
                .name("Coin")
                .value(new AstValueNode.Builder().ordinal(0).name("PENNY").build())
                .value(new AstValueNode.Builder().ordinal(1).name("NICKLE").build())
                .value(new AstValueNode.Builder().ordinal(2).name("DIME").build())
                .value(new AstValueNode.Builder().ordinal(3).name("QUARTER").build())
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
        NukleusParser parser = newParser("struct Person { string8 firstName; string8 lastName; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("Person")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.STRING8).name("firstName").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.STRING8).name("lastName").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseStructWithArrayMember()
    {
        NukleusParser parser = newParser("struct Person { string8 lastName; string8[] foreNames; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("Person")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.STRING8).name("lastName").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.ARRAY32).type(AstType.STRING8).name("foreNames").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseStructWithUnboundedOctetsMember()
    {
        NukleusParser parser = newParser("struct Frame { string8 source; octets extension; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("Frame")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.STRING8).name("source").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.OCTETS).name("extension").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseStructWithString16Members()
    {
        NukleusParser parser = newParser("struct Person { string16 firstName; string16 lastName; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("Person")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.STRING16).name("firstName").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.STRING16).name("lastName").build())
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
            .supertype(AstType.dynamicType("common::Person"))
            .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseUnionWithUint8Case()
    {
        NukleusParser parser = newParser("union Count switch (uint8) { case 0: uint8 width1; case 1: uint16 width2; }");
        Union_typeContext ctx = parser.union_type();
        AstUnionNode actual = new AstParser().visitUnion_type(ctx);

        AstUnionNode expected = new AstUnionNode.Builder()
                .name("Count")
                .kindType(AstType.UINT8)
                .caseN(new AstUnionCaseNode.Builder()
                                      .value(0)
                                      .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                                                               .name("width1")
                                                               .type(AstType.UINT8)
                                                               .build())
                                      .build())
                .caseN(new AstUnionCaseNode.Builder()
                                       .value(1)
                                       .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                                                                .name("width2")
                                                                .type(AstType.UINT16)
                                                                .build())
                                       .build())
                .build();

        assertEquals(expected, actual);
    }


    @Test
    public void shouldParseUnionWithUint8CaseUsingHexLiteral()
    {
        NukleusParser parser = newParser("union Count switch (uint8) { case 0x00: uint8 width1; case 0x01: uint16 width2; }");
        Union_typeContext ctx = parser.union_type();
        AstUnionNode actual = new AstParser().visitUnion_type(ctx);

        AstUnionNode expected = new AstUnionNode.Builder()
                .name("Count")
                .kindType(AstType.UINT8)
                .caseN(new AstUnionCaseNode.Builder()
                                      .value(0)
                                      .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                                                               .name("width1")
                                                               .type(AstType.UINT8)
                                                               .build())
                                      .build())
                .caseN(new AstUnionCaseNode.Builder()
                                       .value(1)
                                       .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                                                                .name("width2")
                                                                .type(AstType.UINT16)
                                                                .build())
                                       .build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseVariantWithExplicitType()
    {
        NukleusParser parser = newParser("variant VariantUnsignedIntWithExplicitType switch (uint8) of uint32 " +
            "{ case 0x04: uint32; case 0x01: uint8; case 0x00: 0; }");
        Variant_typeContext ctx = parser.variant_type();
        AstVariantNode actual = new AstParser().visitVariant_type(ctx);

        AstVariantNode expected = new AstVariantNode.Builder()
            .name("VariantUnsignedIntWithExplicitType")
            .kindType(AstType.UINT8)
            .of(AstType.UINT32)
            .caseN(new AstVariantCaseNode.Builder()
                .value(0x04)
                .type(AstType.UINT32)
                .build())
            .caseN(new AstVariantCaseNode.Builder()
                .value(0x01)
                .type(AstType.UINT8)
                .build())
            .caseN(new AstVariantCaseNode.Builder()
                .value(0x00)
                .type(AstType.dynamicType("0"))
                .build())
            .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseVariantWithoutExplicitType()
    {
        NukleusParser parser = newParser("variant VariantUnsignedIntWithoutExplicitType switch (uint8) " +
            "{ case 0x70: uint32; case 0x52: uint8; case 0x43: 0; }");
        Variant_typeContext ctx = parser.variant_type();
        AstVariantNode actual = new AstParser().visitVariant_type(ctx);

        AstVariantNode expected = new AstVariantNode.Builder()
            .name("VariantUnsignedIntWithoutExplicitType")
            .kindType(AstType.UINT8)
            .caseN(new AstVariantCaseNode.Builder()
                .value(0x70)
                .type(AstType.UINT32)
                .build())
            .caseN(new AstVariantCaseNode.Builder()
                .value(0x52)
                .type(AstType.UINT8)
                .build())
            .caseN(new AstVariantCaseNode.Builder()
                .value(0x43)
                .type(AstType.dynamicType("0"))
                .build())
            .build();

        assertEquals(expected, actual);
    }

    @Test(expected = ParseCancellationException.class)
    public void shouldNotParseStructWithUnboundedArrayMemberNotLast()
    {
        NukleusParser parser = newParser("struct s {uint8[] field1; uint8 field2;");
        Struct_typeContext ctx = parser.struct_type();
        new AstParser().visitStruct_type(ctx);
    }

    @Test(expected = ParseCancellationException.class)
    public void shouldNotParseStructWithUnboundedOctetsMemberNotLast()
    {
        NukleusParser parser = newParser("struct s {octets field1; uint8 field2;");
        Struct_typeContext ctx = parser.struct_type();
        new AstParser().visitStruct_type(ctx);
    }

    @Test
    public void shouldParseList()
    {
        NukleusParser parser = newParser("list<uint32, uint32> ListWithPhysicalAndLogicalLength " +
            "{ required string8 field0; uint32 field1; string8 field2; }");

        List_typeContext ctx = parser.list_type();
        AstListNode actual = new AstParser().visitList_type(ctx);
        AstListNode expected = new AstListNode.Builder()
            .name("ListWithPhysicalAndLogicalLength")
            .lengthType(AstType.UINT32)
            .fieldCountType(AstType.UINT32)
            .member(
                (AstListMemberNode) new AstListMemberNode.Builder()
                    .isRequired(true)
                    .byteOrder(NATIVE)
                    .type(AstType.STRING8)
                    .name("field0")
                    .build())
            .member(
                (AstListMemberNode) new AstListMemberNode.Builder()
                    .byteOrder(NATIVE)
                    .type(AstType.UINT32)
                    .name("field1")
                    .build())
            .member(
                (AstListMemberNode) new AstListMemberNode.Builder()
                    .byteOrder(NATIVE)
                    .type(AstType.STRING8)
                    .name("field2")
                    .build())
            .build();
        assertEquals(expected, actual);
        assertEquals(expected.members().get(0).toString(), actual.members().get(0).toString());
        assertEquals(expected.members().get(1).toString(), actual.members().get(1).toString());
        assertEquals(expected.members().get(2).toString(), actual.members().get(2).toString());
    }

    @Test
    public void shouldParseTypedef()
    {
        NukleusParser parser = newParser("typedef Original as TypeDef;");

        Typedef_typeContext ctx = parser.typedef_type();
        AstTypedefNode actual = new AstParser().visitTypedef_type(ctx);
        AstTypedefNode expected = new AstTypedefNode.Builder()
            .name("TypeDef")
            .originalType(AstType.dynamicType("Original"))
            .build();
        assertEquals(expected.getKind(), actual.getKind());
        assertEquals(expected.originalType(), actual.originalType());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseOctetsWithUint16SizeField()
    {
        NukleusParser parser = newParser("struct octetsWithSizeField { uint16 size; octets[size] field; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("octetsWithSizeField")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.UINT16).name("size").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.OCTETS).sizeName("size").name("field").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseOctetsWithUint24SizeField()
    {
        NukleusParser parser = newParser("struct octetsWithSizeField { uint24 size; octets[size] field; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("octetsWithSizeField")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.UINT24).name("size").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.OCTETS).sizeName("size")
                    .name("field").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseOctetsWithUint32SizeField()
    {
        NukleusParser parser = newParser("struct octetsWithSizeField { uint32 size; octets[size] field; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("octetsWithSizeField")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.UINT32).name("size").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.OCTETS).sizeName("size").name("field").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseOctetsWithUint64SizeField()
    {
        NukleusParser parser = newParser("struct octetsWithSizeField { uint64 size; octets[size] field; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("octetsWithSizeField")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.UINT64).name("size").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.OCTETS).sizeName("size").name("field").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseOctetsDefaultingToNull()
    {
        NukleusParser parser = newParser("struct octetsWithSizeField { int16 size; octets[size] field = null; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("octetsWithSizeField")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.INT16)
                        .name("size").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.OCTETS).sizeName("size")
                        .name("field").defaultToNull().build())
                .build();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotParseVariableOctetsDefaultingToNullWithUnsignedSizeField()
    {
        NukleusParser parser = newParser("struct octetsWithSizeField { uint64 size; octets[size] field = null; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("octetsWithSizeField")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.UINT64).name("size").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.OCTETS).sizeName("size")
                        .name("field").defaultToNull().build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseInt64Member()
    {
        NukleusParser parser = newParser("int64 field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.INT64)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseInt64MemberWithPositiveDefaultValue()
    {
        NukleusParser parser = newParser("int64 field = 123;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.INT64)
                .name("field")
                .defaultValue(123)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseStructWithSomeMembersWithDefaultValues()
    {
        NukleusParser parser = newParser("struct Person { int16 field1; int16 field2 = -123; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("Person")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.INT16).name("field1").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.INT16).name("field2").defaultValue(-123).build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseInt64MemberWithNegativeDefaultValue()
    {
        NukleusParser parser = newParser("int64 field = -12;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.INT64)
                .name("field")
                .defaultValue(-12)
                .build();

        assertEquals(expected, actual);
        assertEquals(-12, actual.defaultValue());
    }

    @Test
    public void shouldParseUint8Member()
    {
        NukleusParser parser = newParser("uint8 field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.UINT8)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseUint8MemberWithPositiveDefaultValue()
    {
        NukleusParser parser = newParser("uint8 field = 12;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.UINT8)
                .name("field")
                .defaultValue(12)
                .build();

        assertEquals(expected, actual);
    }

    @Test(expected = ParseCancellationException.class)
    public void shouldNotParseUint8MemberWithNegativeDefaultValue()
    {
        NukleusParser parser = newParser("uint8 field = -1;");
        parser.member();
    }

    @Test
    public void shouldParseUint16Member()
    {
        NukleusParser parser = newParser("uint16 field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.UINT16)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseUint24Member()
    {
        NukleusParser parser = newParser("uint24 field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.UINT24)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseUint16FixedArrayMember()
    {
        NukleusParser parser = newParser("uint16[10] field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.UINT16)
                .name("field")
                .size(10)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseUint24FixedArrayMember()
    {
        NukleusParser parser = newParser("uint24[10] field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.UINT24)
                .name("field")
                .size(10)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseUint64VariableArrayMember()
    {
        NukleusParser parser = newParser("struct arrayField { uint16 size; uint64[size] field; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("arrayField")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.UINT16).name("size").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(AstType.UINT64).sizeName("size").name("field").build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseInt32VariableArrayMemberDefaultingToNulld()
    {
        NukleusParser parser = newParser("struct arrayField { int8 size; int32[size] field = null; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("arrayField")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.INT8)
                        .name("size").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(INT32).sizeName("size")
                        .name("field").defaultToNull().build())
                .build();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotParseInt32VariableArrayMemberDefaultingToNullWithUnsignedSizeField()
    {
        NukleusParser parser = newParser("struct arrayField { uint64 size; int32[size] field = null; }");
        Struct_typeContext ctx = parser.struct_type();
        AstStructNode actual = new AstParser().visitStruct_type(ctx);

        AstStructNode expected = new AstStructNode.Builder()
                .name("arrayField")
                .member((AstStructMemberNode) new AstStructMemberNode.Builder().type(AstType.UINT64).name("size").build())
                .member((AstStructMemberNode) new AstStructMemberNode.Builder()
                    .type(INT32).sizeName("size").name("field").defaultToNull().build())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseStringMember()
    {
        NukleusParser parser = newParser("string8 field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.STRING8)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseDefaultString8Member()
    {
        NukleusParser parser = newParser("string8 field = \"test\";");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
               .type(AstType.STRING8)
               .name("field")
               .defaultValue("\"test\"")
               .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseDefaultString16Member()
    {
        NukleusParser parser = newParser("string16 field = \"test\";");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
               .type(AstType.STRING16)
               .name("field")
               .defaultValue("\"test\"")
               .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseDefaultString32Member()
    {
        NukleusParser parser = newParser("string32 field = \"test\";");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
               .type(AstType.STRING32)
               .name("field")
               .defaultValue("\"test\"")
               .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNullString8Member()
    {
        NukleusParser parser = newParser("string8 field = null;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
               .type(AstType.STRING8)
               .name("field")
               .defaultValue(NULL_DEFAULT)
               .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNullString16Member()
    {
        NukleusParser parser = newParser("string16 field = null;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
               .type(AstType.STRING16)
               .name("field")
               .defaultValue(NULL_DEFAULT)
               .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseNullString32Member()
    {
        NukleusParser parser = newParser("string32 field = null;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
               .type(AstType.STRING32)
               .name("field")
               .defaultValue(NULL_DEFAULT)
               .build();

        assertEquals(expected, actual);
    }

    // @Test TODO: not yet supported
    public void shouldParseStringMemberWithLength()
    {
        NukleusParser parser = newParser("string8<10> field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.STRING8)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseString16Member()
    {
        NukleusParser parser = newParser("string16 field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.STRING16)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseArrayMember()
    {
        NukleusParser parser = newParser("string8[] field;");

        MemberContext ctx = parser.member();
        AstNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.ARRAY32)
                .type(AstType.STRING8)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseArrayMemberString16()
    {
        NukleusParser parser = newParser("string16[] field;");
        MemberContext ctx = parser.member();
        AstStructMemberNode actual = new AstParser().visitMember(ctx);

        AstStructMemberNode expected = (AstStructMemberNode) new AstStructMemberNode.Builder()
                .type(AstType.ARRAY32)
                .type(AstType.STRING16)
                .name("field")
                .build();

        assertEquals(expected, actual);
    }

    private static NukleusParser newParser(
        String input)
    {
        CharStream chars = CharStreams.fromString(input);
        NukleusLexer lexer = new NukleusLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        NukleusParser parser = new NukleusParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        parser.removeErrorListeners();
        return parser;
    }
}
