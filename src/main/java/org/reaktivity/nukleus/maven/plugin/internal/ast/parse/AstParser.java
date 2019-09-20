/**
 * Copyright 2016-2019 The Reaktivity Project
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

import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder.NATIVE;
import static org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder.NETWORK;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

import org.antlr.v4.runtime.RuleContext;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstByteOrder;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstEnumNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstMemberNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstScopeNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstSpecificationNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstStructNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstType;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionCaseNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstUnionNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstValueNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstVariantCaseNode;
import org.reaktivity.nukleus.maven.plugin.internal.ast.AstVariantNode;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusBaseVisitor;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Array_memberContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Case_memberContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.DeclaratorContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Default_nullContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Enum_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Enum_valueContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Int16_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Int32_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Int64_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Int8_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Int_literalContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Int_member_with_defaultContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Integer_array_memberContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.KindContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.MemberContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Octets_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.OptionByteOrderContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.ScopeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Scoped_nameContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.SpecificationContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.String16_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.String32_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.String_literalContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.String_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Struct_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Type_idContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Uint16_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Uint32_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Uint64_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Uint8_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Uint_literalContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Uint_member_with_defaultContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Unbounded_memberContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Unbounded_octets_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Union_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Variant_case_memberContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Variant_int_literalContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Variant_of_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Variant_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Varint32_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Varint64_typeContext;
import org.reaktivity.nukleus.maven.plugin.internal.parser.NukleusParser.Varint_array_memberContext;


public final class AstParser extends NukleusBaseVisitor<AstNode>
{
    private final Deque<AstScopeNode.Builder> scopeBuilders;
    private final Map<String, String> qualifiedNamesByLocalName;
    private final Map<AstType, Function<RuleContext, Object>> parserByType;

    private AstSpecificationNode.Builder specificationBuilder;
    private AstStructNode.Builder structBuilder;
    private AstMemberNode.Builder memberBuilder;
    private AstUnionNode.Builder unionBuilder;
    private AstUnionCaseNode.Builder caseBuilder;
    private AstByteOrder byteOrder;

    public AstParser()
    {
        this.scopeBuilders = new LinkedList<>();
        this.qualifiedNamesByLocalName = new HashMap<>();
        this.byteOrder = NATIVE;
        this.parserByType = initParserByType();
    }

    private static Map<AstType, Function<RuleContext, Object>> initParserByType()
    {
        Map<AstType, Function<RuleContext, Object>> valueTypeByName = new HashMap<>();
        valueTypeByName.put(AstType.UINT8, AstParser::parseShort);
        valueTypeByName.put(AstType.UINT16, AstParser::parseInt);
        valueTypeByName.put(AstType.UINT32, AstParser::parseLong);
        valueTypeByName.put(AstType.UINT64, AstParser::parseLong);
        valueTypeByName.put(AstType.INT8, AstParser::parseByte);
        valueTypeByName.put(AstType.INT16, AstParser::parseShort);
        valueTypeByName.put(AstType.INT32, AstParser::parseInt);
        valueTypeByName.put(AstType.INT64, AstParser::parseLong);
        valueTypeByName.put(AstType.STRING, AstParser::parseString);
        valueTypeByName.put(AstType.STRING16, AstParser::parseString);
        valueTypeByName.put(AstType.STRING32, AstParser::parseString);
        return valueTypeByName;
    }

    @Override
    public AstSpecificationNode visitSpecification(
        SpecificationContext ctx)
    {
        specificationBuilder = new AstSpecificationNode.Builder();

        super.visitSpecification(ctx);

        return specificationBuilder.build();
    }

    @Override
    public AstScopeNode visitScope(
        ScopeContext ctx)
    {
        String name = ctx.ID().getText();

        AstScopeNode.Builder scopeBuilder = new AstScopeNode.Builder();
        scopeBuilder.depth(scopeBuilders.size());
        scopeBuilder.name(name);

        visitLocalName(name);

        AstByteOrder byteOrder = this.byteOrder;
        scopeBuilders.offer(scopeBuilder);
        super.visitScope(ctx);
        scopeBuilders.pollLast();
        this.byteOrder = byteOrder;

        AstScopeNode.Builder parent = scopeBuilders.peekLast();
        if (parent != null)
        {
            AstScopeNode scopeNode = scopeBuilder.build();
            parent.scope(scopeNode);
            return scopeNode;
        }
        else if (specificationBuilder != null)
        {
            AstScopeNode scopeNode = scopeBuilder.build();
            specificationBuilder.scope(scopeNode);
            return scopeNode;
        }
        else
        {
            return scopeBuilder.build();
        }
    }

    @Override
    public AstNode visitOptionByteOrder(
        OptionByteOrderContext ctx)
    {
        if (ctx.KW_NATIVE() != null)
        {
            byteOrder = NATIVE;
        }
        else if (ctx.KW_NETWORK() != null)
        {
            byteOrder = NETWORK;
        }
        else
        {
            throw new IllegalStateException("Unexpected byte order option");
        }

        return super.visitOptionByteOrder(ctx);
    }

    @Override
    public AstEnumNode visitEnum_type(
        Enum_typeContext ctx)
    {
        visitLocalName(ctx.ID().getText());

        AstEnumNode.Builder enumBuilder = new EnumVisitor().visitEnum_type(ctx);
        AstEnumNode enumeration = enumBuilder.build();

        AstScopeNode.Builder scopeBuilder = scopeBuilders.peekLast();
        if (scopeBuilder != null)
        {
            scopeBuilder.enumeration(enumeration);
        }

        return enumeration;
    }

    @Override
    public AstVariantNode visitVariant_type(
        Variant_typeContext ctx)
    {
        visitLocalName(ctx.ID().getText());
        AstVariantNode.Builder variantBuilder = new VariantVisitor().visitVariant_type(ctx);
        AstVariantNode variant = variantBuilder.build();

        AstScopeNode.Builder scopeBuilder = scopeBuilders.peekLast();
        if (scopeBuilder != null)
        {
            scopeBuilder.variant(variant);
        }
        return variant;
    }

    @Override
    public AstStructNode visitStruct_type(
        Struct_typeContext ctx)
    {
        structBuilder = new AstStructNode.Builder();
        structBuilder.name(ctx.ID().getText());

        visitLocalName(ctx.ID().getText());

        Scoped_nameContext scopedName = ctx.scoped_name();
        if (scopedName != null)
        {
            final String superType = scopedName.getText();
            final String qualifiedSuperTypeName = qualifiedNamesByLocalName.getOrDefault(superType, superType);
            structBuilder.supertype(qualifiedSuperTypeName);
        }

        super.visitStruct_type(ctx);

        AstScopeNode.Builder scopeBuilder = scopeBuilders.peekLast();
        if (scopeBuilder != null)
        {
            AstStructNode struct = structBuilder.build();
            scopeBuilder.struct(struct);
            return struct;
        }
        else
        {
            return structBuilder.build();
        }
    }

    @Override
    public AstMemberNode visitMember(
        MemberContext ctx)
    {
        memberBuilder = new AstMemberNode.Builder().byteOrder(byteOrder);

        super.visitMember(ctx);

        AstMemberNode member = memberBuilder.build();
        memberBuilder = null;

        if (caseBuilder != null)
        {
            caseBuilder.member(member);
        }
        else if (structBuilder != null)
        {
            structBuilder.member(member);
        }

        return member;
    }

    @Override
    public AstMemberNode visitUnbounded_member(
        Unbounded_memberContext ctx)
    {
        memberBuilder = new AstMemberNode.Builder();

        super.visitUnbounded_member(ctx);

        AstMemberNode member = memberBuilder.build();
        memberBuilder = null;

        if (caseBuilder != null)
        {
            caseBuilder.member(member);
        }
        else if (structBuilder != null)
        {
            structBuilder.member(member);
        }

        return member;
    }

    @Override
    public AstNode visitUint_member_with_default(
        Uint_member_with_defaultContext ctx)
    {
        memberBuilder.defaultValue(parseInt(ctx.uint_literal()));
        return super.visitUint_member_with_default(ctx);
    }

    @Override
    public AstNode visitInt_member_with_default(
        Int_member_with_defaultContext ctx)
    {
        memberBuilder.defaultValue(parseInt(ctx.int_literal()));
        return super.visitInt_member_with_default(ctx);
    }

    @Override
    public AstNode visitInteger_array_member(
        Integer_array_memberContext ctx)
    {
        if (ctx.positive_int_const() != null)
        {
            memberBuilder.size(Integer.parseInt(ctx.positive_int_const().getText()));
        }
        else if (ctx.ID() != null)
        {
            memberBuilder.sizeName(ctx.ID().getText());
        }
        return super.visitInteger_array_member(ctx);
    }

    @Override
    public AstNode visitArray_member(
        Array_memberContext ctx)
    {
        memberBuilder.type(AstType.ARRAY);
        return super.visitArray_member(ctx);
    }

    @Override
    public AstNode visitDefault_null(
        Default_nullContext ctx)
    {
        memberBuilder.defaultToNull();
        return super.visitDefault_null(ctx);
    }

    @Override
    public AstUnionNode visitUnion_type(
        Union_typeContext ctx)
    {
        unionBuilder = new AstUnionNode.Builder();
        unionBuilder.name(ctx.ID().getText());

        visitLocalName(ctx.ID().getText());

        super.visitUnion_type(ctx);

        AstUnionNode union = unionBuilder.build();
        unionBuilder = null;

        AstScopeNode.Builder scopeBuilder = scopeBuilders.peekLast();
        if (scopeBuilder != null)
        {
            scopeBuilder.union(union);
        }

        return union;
    }

    @Override
    public AstUnionCaseNode visitCase_member(
        Case_memberContext ctx)
    {
        caseBuilder = new AstUnionCaseNode.Builder()
                .value(Integer.decode(ctx.uint_literal().getText()));

        super.visitCase_member(ctx);

        AstUnionCaseNode caseN = caseBuilder.build();
        caseBuilder = null;

        if (unionBuilder != null)
        {
            unionBuilder.caseN(caseN);
        }

        return caseN;
    }

    @Override
    public AstNode visitDeclarator(
        DeclaratorContext ctx)
    {
        memberBuilder.name(ctx.ID().toString());
        return super.visitDeclarator(ctx);
    }

    @Override
    public AstNode visitVarint_array_member(
        Varint_array_memberContext ctx)
    {
        memberBuilder.type(AstType.ARRAY);
        return super.visitVarint_array_member(ctx);
    }

    @Override
    public AstNode visitVarint32_type(
        Varint32_typeContext ctx)
    {
        memberBuilder.type(AstType.VARINT32);
        return super.visitVarint32_type(ctx);
    }

    @Override
    public AstNode visitVarint64_type(
        Varint64_typeContext ctx)
    {
        memberBuilder.type(AstType.VARINT64);
        return super.visitVarint64_type(ctx);
    }

    @Override
    public AstNode visitInt64_type(
        Int64_typeContext ctx)
    {
        memberBuilder.type(AstType.INT64);
        return super.visitInt64_type(ctx);
    }

    @Override
    public AstNode visitInt32_type(
        Int32_typeContext ctx)
    {
        memberBuilder.type(AstType.INT32);
        return super.visitInt32_type(ctx);
    }

    @Override
    public AstNode visitInt16_type(
        Int16_typeContext ctx)
    {
        memberBuilder.type(AstType.INT16);
        return super.visitInt16_type(ctx);
    }

    @Override
    public AstNode visitInt8_type(
        Int8_typeContext ctx)
    {
        memberBuilder.type(AstType.INT8);
        return super.visitInt8_type(ctx);
    }

    @Override
    public AstNode visitUint64_type(
        Uint64_typeContext ctx)
    {
        memberBuilder.type(AstType.UINT64);
        return super.visitUint64_type(ctx);
    }

    @Override
    public AstNode visitUint32_type(
        Uint32_typeContext ctx)
    {
        memberBuilder.type(AstType.UINT32);
        return super.visitUint32_type(ctx);
    }

    @Override
    public AstNode visitUint16_type(
        Uint16_typeContext ctx)
    {
        memberBuilder.type(AstType.UINT16);
        return super.visitUint16_type(ctx);
    }

    @Override
    public AstNode visitUint8_type(
        Uint8_typeContext ctx)
    {
        memberBuilder.type(AstType.UINT8);
        return super.visitUint8_type(ctx);
    }

    @Override
    public AstNode visitString_type(
        String_typeContext ctx)
    {
        memberBuilder.type(AstType.STRING);
        return super.visitString_type(ctx);
    }

    @Override
    public AstNode visitString16_type(
            String16_typeContext ctx)
    {
        memberBuilder.type(AstType.STRING16);
        return super.visitString16_type(ctx);
    }

    @Override
    public AstNode visitString32_type(
        String32_typeContext ctx)
    {
        memberBuilder.type(AstType.STRING32);
        return super.visitString32_type(ctx);
    }

    @Override
    public AstNode visitOctets_type(
        Octets_typeContext ctx)
    {
        memberBuilder.type(AstType.OCTETS);
        if (ctx.positive_int_const() != null)
        {
            memberBuilder.size(Integer.parseInt(ctx.positive_int_const().getText()));
        }
        else if (ctx.ID() != null)
        {
            memberBuilder.sizeName(ctx.ID().getText());
        }
        return super.visitOctets_type(ctx);
    }

    @Override
    public AstNode visitUnbounded_octets_type(
        Unbounded_octets_typeContext ctx)
    {
        memberBuilder.type(AstType.OCTETS);
        return super.visitUnbounded_octets_type(ctx);
    }

    @Override
    public AstNode visitScoped_name(
        Scoped_nameContext ctx)
    {
        String typeName = ctx.getText();
        if (memberBuilder != null)
        {
            String qualifiedTypeName = qualifiedNamesByLocalName.getOrDefault(typeName, typeName);
            memberBuilder.type(AstType.dynamicType(qualifiedTypeName));
        }
        return super.visitScoped_name(ctx);
    }

    @Override
    public AstNode visitType_id(
        Type_idContext ctx)
    {
        if (structBuilder != null)
        {
            structBuilder.typeId(parseInt(ctx.uint_literal()));
        }
        return super.visitType_id(ctx);
    }

    private void visitLocalName(
        String name)
    {
        String qualifiedName = name;
        AstScopeNode.Builder scopeBuilder = scopeBuilders.peekLast();
        if (scopeBuilder != null)
        {
            String scopeName = scopeBuilder.name();
            String qualifieScopeName = qualifiedNamesByLocalName.get(scopeName);
            qualifiedName = String.format("%s::%s", qualifieScopeName, name);
        }
        qualifiedNamesByLocalName.put(name, qualifiedName);
    }

    private static byte parseByte(
        RuleContext ctx)
    {
        return parseByte(ctx.getText());
    }

    private static byte parseByte(
        String text)
    {
        return Byte.decode(text);
    }

    private static short parseShort(
        RuleContext ctx)
    {
        return parseShort(ctx.getText());
    }

    private static short parseShort(
        String text)
    {
        return Short.decode(text);
    }

    private static int parseInt(
        Int_literalContext ctx)
    {
        return ctx != null ? parseInt(ctx.getText()) : 0;
    }

    private static int parseInt(
        RuleContext ctx)
    {
        return ctx != null ? parseInt(ctx.getText()) : 0;
    }

    private static int parseInt(
        String text)
    {
        return Integer.decode(text);
    }

    private static long parseLong(
        RuleContext ctx)
    {
        return parseLong(ctx.getText());
    }

    private static long parseLong(
        String text)
    {
        return Long.decode(text.substring(0, text.length() - 1));
    }

    private static String parseString(
        RuleContext ctx)
    {
        return ctx.getText();
    }

    public final class VariantVisitor extends NukleusBaseVisitor<AstVariantNode.Builder>
    {
        private final AstVariantNode.Builder variantBuilder;

        public VariantVisitor()
        {
            this.variantBuilder = new AstVariantNode.Builder();
        }

        @Override
        public AstVariantNode.Builder visitVariant_type(
            Variant_typeContext ctx)
        {
            variantBuilder.name(ctx.ID().getText());
            return super.visitVariant_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitKind(
            KindContext ctx)
        {
            if (ctx.KW_UINT8() != null)
            {
                variantBuilder.kindType(AstType.UINT8);
            }
            return super.visitKind(ctx);
        }

        @Override
        public AstVariantNode.Builder visitScoped_name(
            Scoped_nameContext ctx)
        {
            String kindTypeName = ctx.ID(0).getText();
            String qualifiedTypeName = qualifiedNamesByLocalName.getOrDefault(kindTypeName, kindTypeName);
            variantBuilder.kindType(AstType.dynamicType(qualifiedTypeName));
            return super.visitScoped_name(ctx);
        }

        @Override
        public AstVariantNode.Builder visitVariant_of_type(
            Variant_of_typeContext ctx)
        {
            return super.visitVariant_of_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitVariant_case_member(
            Variant_case_memberContext ctx)
        {
            AstVariantCaseNode.Builder variantCaseNodeBuilder = new VariantCaseVisitor().visitVariant_case_member(ctx);

            AstVariantCaseNode caseN = variantCaseNodeBuilder.build();
            variantBuilder.caseN(caseN);

            return variantBuilder;
        }

        public final class VariantCaseVisitor extends NukleusBaseVisitor<AstVariantCaseNode.Builder>
        {
            private final AstVariantCaseNode.Builder variantCaseBuilder;

            public VariantCaseVisitor()
            {
                variantCaseBuilder = new AstVariantCaseNode.Builder();
            }

            @Override
            public AstVariantCaseNode.Builder visitVariant_case_member(
                Variant_case_memberContext ctx)
            {
                super.visitVariant_case_member(ctx);
                return variantCaseBuilder;
            }

            @Override
            public AstVariantCaseNode.Builder visitUint_literal(
                Uint_literalContext ctx)
            {
                variantCaseBuilder.value(Integer.decode(ctx.getText()));
                return super.visitUint_literal(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitDeclarator(
                DeclaratorContext ctx)
            {
                variantCaseBuilder.value(ctx.ID().getText());
                return super.visitDeclarator(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitInt8_type(
                Int8_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.INT8);
                return super.visitInt8_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitInt16_type(
                Int16_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.INT16);
                return super.visitInt16_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitInt32_type(
                Int32_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.INT32);
                return super.visitInt32_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitInt64_type(
                Int64_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.INT64);
                return super.visitInt64_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitUint8_type(
                Uint8_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.UINT8);
                return super.visitUint8_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitUint16_type(
                Uint16_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.UINT16);
                return super.visitUint16_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitUint32_type(
                Uint32_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.UINT32);
                return super.visitUint32_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitUint64_type(
                Uint64_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.UINT64);
                return super.visitUint64_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitString_type(
                String_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.STRING);
                return super.visitString_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitString16_type(
                String16_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.STRING16);
                return super.visitString16_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitString32_type(
                String32_typeContext ctx)
            {
                variantCaseBuilder.type(AstType.STRING32);
                return super.visitString32_type(ctx);
            }

            @Override
            public AstVariantCaseNode.Builder visitVariant_int_literal(
                Variant_int_literalContext ctx)
            {
                variantCaseBuilder.type(AstType.dynamicType(ctx.getText()));
                return super.visitVariant_int_literal(ctx);
            }
        }

        @Override
        public AstVariantNode.Builder visitString_type(
            String_typeContext ctx)
        {
            variantBuilder.of(AstType.STRING);
            return super.visitString_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitString16_type(
            String16_typeContext ctx)
        {
            variantBuilder.of(AstType.STRING16);
            return super.visitString16_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitString32_type(
            String32_typeContext ctx)
        {
            variantBuilder.of(AstType.STRING32);
            return super.visitString32_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitInt64_type(
            Int64_typeContext ctx)
        {
            variantBuilder.of(AstType.INT64);
            return super.visitInt64_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitInt32_type(
            Int32_typeContext ctx)
        {
            variantBuilder.of(AstType.INT32);
            return super.visitInt32_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitInt16_type(
            Int16_typeContext ctx)
        {
            variantBuilder.of(AstType.INT16);
            return super.visitInt16_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitInt8_type(
            Int8_typeContext ctx)
        {
            variantBuilder.of(AstType.INT8);
            return super.visitInt8_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitUint64_type(
            Uint64_typeContext ctx)
        {
            variantBuilder.of(AstType.UINT64);
            return super.visitUint64_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitUint32_type(
            Uint32_typeContext ctx)
        {
            variantBuilder.of(AstType.UINT32);
            return super.visitUint32_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitUint16_type(
            Uint16_typeContext ctx)
        {
            variantBuilder.of(AstType.UINT16);
            return super.visitUint16_type(ctx);
        }

        @Override
        public AstVariantNode.Builder visitUint8_type(
            Uint8_typeContext ctx)
        {
            variantBuilder.of(AstType.UINT8);
            return super.visitUint8_type(ctx);
        }

        @Override
        protected AstVariantNode.Builder defaultResult()
        {
            return variantBuilder;
        }
    }

    public final class EnumVisitor extends NukleusBaseVisitor<AstEnumNode.Builder>
    {
        private final AstEnumNode.Builder enumBuilder;
        private AstValueNode.Builder valueBuilder;

        public EnumVisitor()
        {
            this.enumBuilder = new AstEnumNode.Builder();
        }

        @Override
        public AstEnumNode.Builder visitEnum_type(
            Enum_typeContext ctx)
        {
            enumBuilder.name(ctx.ID().getText());

            return super.visitEnum_type(ctx);
        }

        @Override
        protected AstEnumNode.Builder defaultResult()
        {
            return enumBuilder;
        }

        @Override
        public AstEnumNode.Builder visitInt8_type(
            Int8_typeContext ctx)
        {
            enumBuilder.valueType(AstType.INT8);
            return super.visitInt8_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitInt16_type(
            Int16_typeContext ctx)
        {
            enumBuilder.valueType(AstType.INT16);
            return super.visitInt16_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitInt32_type(
            Int32_typeContext ctx)
        {
            enumBuilder.valueType(AstType.INT32);
            return super.visitInt32_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitInt64_type(
            Int64_typeContext ctx)
        {
            enumBuilder.valueType(AstType.INT64);
            return super.visitInt64_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitUint8_type(
            Uint8_typeContext ctx)
        {
            enumBuilder.valueType(AstType.UINT8);
            return super.visitUint8_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitUint16_type(
            Uint16_typeContext ctx)
        {
            enumBuilder.valueType(AstType.UINT16);
            return super.visitUint16_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitUint32_type(
            Uint32_typeContext ctx)
        {
            enumBuilder.valueType(AstType.UINT32);
            return super.visitUint32_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitUint64_type(
            Uint64_typeContext ctx)
        {
            enumBuilder.valueType(AstType.UINT64);
            return super.visitUint64_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitString_type(
            String_typeContext ctx)
        {
            enumBuilder.valueType(AstType.STRING);
            return super.visitString_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitString16_type(
            String16_typeContext ctx)
        {
            enumBuilder.valueType(AstType.STRING16);
            return super.visitString16_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitString32_type(
            String32_typeContext ctx)
        {
            enumBuilder.valueType(AstType.STRING32);
            return super.visitString32_type(ctx);
        }

        @Override
        public AstEnumNode.Builder visitEnum_value(
            Enum_valueContext ctx)
        {
            this.valueBuilder = new AstValueNode.Builder()
                .name(ctx.ID().getText())
                .ordinal(enumBuilder.size());

            AstEnumNode.Builder result = super.visitEnum_value(ctx);

            AstValueNode value = valueBuilder.build();
            enumBuilder.value(value);
            this.valueBuilder = null;
            return result;
        }

        @Override
        public AstEnumNode.Builder visitUint_literal(
            Uint_literalContext ctx)
        {
            return visitLiteral(ctx);
        }

        @Override
        public AstEnumNode.Builder visitString_literal(
            String_literalContext ctx)
        {
            return visitLiteral(ctx);
        }

        private AstEnumNode.Builder visitLiteral(
            RuleContext ctx)
        {
            Function<RuleContext, Object> parser = parserByType.get(enumBuilder.valueType());
            Object parsed = parser.apply(ctx);
            valueBuilder.value(parsed);
            return defaultResult();
        }
    }
}
