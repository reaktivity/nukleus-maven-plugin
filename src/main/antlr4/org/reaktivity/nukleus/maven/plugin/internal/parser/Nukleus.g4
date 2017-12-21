/*
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
grammar Nukleus;

specification
   : scope
   ;

scope
   : KW_SCOPE ID LEFT_BRACE option * definition * RIGHT_BRACE
   ;

option
   : KW_OPTION optionByteOrder SEMICOLON
   ;

optionByteOrder
   : KW_BYTEORDER (KW_NATIVE | KW_NETWORK)
   ;

scoped_name
   : (DOUBLE_COLON)? ID (DOUBLE_COLON ID)*
   ;

definition
   : type_decl
   | scope
   ;

positive_int_const
   : HEX_LITERAL
   | UNSIGNED_INTEGER_LITERAL
   ;

type_decl
   : constr_type_spec
   ;

type_declarator
   : type_spec declarators
   ;

type_spec
   : simple_type_spec
   | constr_type_spec
   ;

simple_type_spec
   : base_type_spec
   | scoped_name
   ;

base_type_spec
   : integer_type
   | octets_type
   | string_type
   | string16_type
   ;

constr_type_spec
   : enum_type
   | struct_type
   | union_type
   ;

declarators
   : declarator (COMMA declarator)*
   ;

declarator
   : ID
   ;

integer_type
   : signed_integer_type
   | unsigned_integer_type
   ;

signed_integer_type
   : int8_type
   | int16_type
   | int32_type
   | int64_type
   | varint32_type
   | varint64_type
   ;

unsigned_integer_type
   : uint8_type
   | uint16_type
   | uint32_type
   | uint64_type
   ;

int8_type
   : KW_INT8
   ;

int16_type
   : KW_INT16
   ;

int32_type
   : KW_INT32
   ;

int64_type
   : KW_INT64
   ;

uint8_type
   : KW_UINT8
   ;

uint16_type
   : KW_UINT16
   ;

uint32_type
   : KW_UINT32
   ;

uint64_type
   : KW_UINT64
   ;
   
varint32_type
   : KW_VARINT32
   ;
   
varint64_type
   : KW_VARINT64
   ;

octets_type
   : KW_OCTETS LEFT_SQUARE_BRACKET (positive_int_const | ID) RIGHT_SQUARE_BRACKET
   ;

unbounded_octets_type
   : KW_OCTETS
   ;

enum_type
   : KW_ENUM ID LEFT_BRACE enum_values RIGHT_BRACE
   ;

enum_values
   : enum_value_non_terminal * enum_value_terminal
   ;

enum_value_non_terminal
   : enum_value COMMA
   ;

enum_value_terminal
   : enum_value
   ;

enum_value
   : ID
   ;

struct_type
   : KW_STRUCT ID (KW_EXTENDS scoped_name)? (LEFT_SQUARE_BRACKET type_id RIGHT_SQUARE_BRACKET)? LEFT_BRACE member_list RIGHT_BRACE
   ;

type_id
   : uint_literal
   ;
   
member_list
   : member * unbounded_member?
   ;

member
   : type_spec declarators SEMICOLON
   | uint_member_with_default SEMICOLON
   | int_member_with_default SEMICOLON
   | octets_member_with_default SEMICOLON
   | integer_array_member SEMICOLON
   | varint_array_member SEMICOLON
   | list_member SEMICOLON
   ;
   
uint_member_with_default
   : unsigned_integer_type declarator EQUALS uint_literal
   ;
   
int_member_with_default 
   : signed_integer_type declarator EQUALS int_literal
   ;
   
octets_member_with_default
   : octets_type declarator default_null
   ;
   
integer_array_member
   : int8_type LEFT_SQUARE_BRACKET (positive_int_const | ID) RIGHT_SQUARE_BRACKET declarator default_null?
   | int16_type LEFT_SQUARE_BRACKET (positive_int_const | ID) RIGHT_SQUARE_BRACKET declarator default_null?
   | int32_type LEFT_SQUARE_BRACKET (positive_int_const | ID) RIGHT_SQUARE_BRACKET declarator default_null?
   | int64_type LEFT_SQUARE_BRACKET (positive_int_const | ID) RIGHT_SQUARE_BRACKET declarator default_null?
   | uint8_type LEFT_SQUARE_BRACKET (positive_int_const | ID) RIGHT_SQUARE_BRACKET declarator default_null?
   | uint16_type LEFT_SQUARE_BRACKET (positive_int_const | ID) RIGHT_SQUARE_BRACKET declarator default_null?
   | uint32_type LEFT_SQUARE_BRACKET (positive_int_const | ID) RIGHT_SQUARE_BRACKET declarator default_null?
   | uint64_type LEFT_SQUARE_BRACKET (positive_int_const | ID) RIGHT_SQUARE_BRACKET declarator default_null?
   ;

varint_array_member
   : varint32_type LEFT_SQUARE_BRACKET RIGHT_SQUARE_BRACKET declarator default_null?
   | varint64_type LEFT_SQUARE_BRACKET RIGHT_SQUARE_BRACKET declarator default_null?
   ;

list_member
   : list_type declarators
   ;

default_null
   : '= null'
   ;
   
unbounded_member
   : unbounded_octets_member
   ;
   
unbounded_octets_member
   : unbounded_octets_type declarators SEMICOLON
   ;

union_type
   : KW_UNION ID KW_SWITCH LEFT_BRACKET KW_UINT8 RIGHT_BRACKET LEFT_BRACE case_list RIGHT_BRACE
   ;

case_list
   : case_member *
   ;

case_member
   : KW_CASE uint_literal COLON member
   ;

list_type 
   : KW_LIST LEFT_ANG_BRACKET simple_type_spec RIGHT_ANG_BRACKET
   ;

string_type
   : /* KW_STRING LEFT_ANG_BRACKET positive_int_const RIGHT_ANG_BRACKET
   | */ KW_STRING
   ;

string16_type
   : /* KW_STRING16 LEFT_ANG_BRACKET positive_int_const RIGHT_ANG_BRACKET
   | */ KW_STRING16
   ;

int_literal
   : MINUS ? uint_literal
   ;
   
MINUS
   : '-'
   ;

uint_literal
   : UNSIGNED_INTEGER_LITERAL
   | HEX_LITERAL
   ;

UNSIGNED_INTEGER_LITERAL
   : ('0' | '1' .. '9' '0' .. '9'*) INTEGER_TYPE_SUFFIX?
   ;

HEX_LITERAL
   : '0' ('x' | 'X') HEX_DIGIT + INTEGER_TYPE_SUFFIX?
   ;


fragment HEX_DIGIT
   : ('0' .. '9' | 'a' .. 'f' | 'A' .. 'F')
   ;


fragment INTEGER_TYPE_SUFFIX
   : ('l' | 'L')
   ;


fragment LETTER
   : '\u0024' | '\u0041' .. '\u005a' | '\u005f' | '\u0061' .. '\u007a' | '\u00c0' .. '\u00d6' | '\u00d8' .. '\u00f6' | '\u00f8' .. '\u00ff' | '\u0100' .. '\u1fff' | '\u3040' .. '\u318f' | '\u3300' .. '\u337f' | '\u3400' .. '\u3d2d' | '\u4e00' .. '\u9fff' | '\uf900' .. '\ufaff'
   ;


fragment ID_DIGIT
   : '\u0030' .. '\u0039' | '\u0660' .. '\u0669' | '\u06f0' .. '\u06f9' | '\u0966' .. '\u096f' | '\u09e6' .. '\u09ef' | '\u0a66' .. '\u0a6f' | '\u0ae6' .. '\u0aef' | '\u0b66' .. '\u0b6f' | '\u0be7' .. '\u0bef' | '\u0c66' .. '\u0c6f' | '\u0ce6' .. '\u0cef' | '\u0d66' .. '\u0d6f' | '\u0e50' .. '\u0e59' | '\u0ed0' .. '\u0ed9' | '\u1040' .. '\u1049'
   ;


SEMICOLON
   : ';'
   ;


COLON
   : ':'
   ;


COMMA
   : ','
   ;

EQUALS
   : '='
   ;


LEFT_BRACE
   : '{'
   ;


RIGHT_BRACE
   : '}'
   ;


LEFT_SQUARE_BRACKET
   : '['
   ;


RIGHT_SQUARE_BRACKET
   : ']'
   ;


LEFT_BRACKET
   : '('
   ;


RIGHT_BRACKET
   : ')'
   ;


SLASH
   : '/'
   ;


LEFT_ANG_BRACKET
   : '<'
   ;


RIGHT_ANG_BRACKET
   : '>'
   ;


DOUBLE_COLON
   : '::'
   ;


KW_STRING
   : 'string'
   ;

KW_STRING16
   : 'string16'
   ;


KW_SWITCH
   : 'switch'
   ;


KW_CASE
   : 'case'
   ;


KW_DEFAULT
   : 'default'
   ;


KW_LIST
   : 'list'
   ;


KW_OCTETS
   : 'octets'
   ;


KW_ENUM
   : 'enum'
   ;


KW_STRUCT
   : 'struct'
   ;


KW_EXTENDS
   : 'extends'
   ;


KW_READONLY
   : 'readonly'
   ;


KW_INT8
   : 'int8'
   ;


KW_INT16
   : 'int16'
   ;


KW_INT32
   : 'int32'
   ;


KW_INT64
   : 'int64'
   ;


KW_UINT8
   : 'uint8'
   ;


KW_UINT16
   : 'uint16'
   ;


KW_UINT32
   : 'uint32'
   ;


KW_UINT64
   : 'uint64'
   ;


KW_VARINT32
   : 'varint32'
   ;


KW_VARINT64
   : 'varint64'
   ;


KW_UNION
   : 'union'
   ;


KW_SCOPE
   : 'scope'
   ;

KW_OPTION
   : 'option'
   ;

KW_BYTEORDER
   : 'byteorder'
   ;

KW_NATIVE
   : 'native'
   ;

KW_NETWORK
   : 'network'
   ;

ID
   : LETTER (LETTER | ID_DIGIT)*
   ;


WS
   : (' ' | '\r' | '\t' | '\u000C' | '\n') -> channel (HIDDEN)
   ;


COMMENT
   : '/*' .*? '*/' -> channel (HIDDEN)
   ;


LINE_COMMENT
   : '//' ~ ('\n' | '\r')* '\r'? '\n' -> channel (HIDDEN)
   ;
