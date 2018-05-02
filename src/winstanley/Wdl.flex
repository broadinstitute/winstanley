package winstanley;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import winstanley.psi.WdlTypes;
import com.intellij.psi.TokenType;

// Regenerate from IntelliJ using Flex plugin and COMMAND-SHIFT-G

%%

%class WdlLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}
%{
  private String wdlVersion = "draft-2";
%}

D_QUOTE_CHAR=\"
D_QUOTE_STRING_CHAR=[^\"]
S_QUOTE_CHAR=\'
S_QUOTE_STRING_CHAR=[^\']
ESCAPE_SEQUENCE=\\.
BOOLEAN="true"|"false"

NUMBER="0"|"1"|"2"|"3"|"4"|"5"|"6"|"7"|"8"|"9"

WHITE_SPACE= \n|\r|\r\n|\ |\t|\f

PRIMITIVE_TYPE="Boolean"|"Int"|"Float"|"File"|"String"
OBJECT_TYPE="Object"
ARRAY_TYPE="Array"
PAIR_TYPE="Pair"
MAP_TYPE="Map"

LBRACE="{"
RBRACE="}"
TRIPLE_ANGLE_OPEN="<<<"
TRIPLE_ANGLE_CLOSE=">>>"
LPAREN="("
RPAREN=")"
LSQUARE="["
RSQUARE="]"
COLON=":"
DOUBLE_EQUAL="=="
NOT_EQUAL="!="
EQUAL="="
DOT="."
QMARK="?"
PLUS="+"
DASH="-"
ASTERISK=\*
SLASH=\/
PERCENT=\%
NEW_PLACEHOLDER_OPENER=\~\{
DEPRECATED_PLACEHOLDER_OPENER=\$\{
LOGICAL_NOT="!"
COMMA=","
DOUBLE_PIPE=\|\|
DOUBLE_AMPERSAND="&&"
LESS_THAN=\<
LESS_EQUAL=\<\=
MORE_THAN=\>
MORE_EQUAL=\>\=
COMMENT=("#")[^\r\n]*
IMPORT="import"
ALIAS="alias"
WORKFLOW="workflow"
CALL="call"
AS="as"
INPUT="input"
OUTPUT="output"
WHILE="while"
IF="if"
THEN="then"
ELSE="else"
SCATTER="scatter"
IN="in"
TASK="task"
COMMAND="command"
RUNTIME="runtime"
PARAMETER_META="parameter_meta"
META="meta"
OBJECT="object"
IDENTIFIER=[:letter:]([:letter:]|[:digit:]|\_)*
ATTR_SEP="sep"
ATTR_DEFAULT="default"
ATTR_TRUE="true="|"true ="
ATTR_FALSE="false="|"false ="
VERSION="version"
VERSION_IDENTIFIER="draft-3"|"1.0"
STRUCT="struct"

%state WAITING_WORKFLOW_VERSION
%state WAITING_WORKFLOW_IDENTIFIER_DECL
%state WAITING_TASK_IDENTIFIER_DECL
%state WAITING_STRUCT_DECL
%state WAITING_COMMAND
%state COMMAND1
%state COMMAND2
%state COMMAND1_VAR
%state COMMAND2_VAR

%state D_QUOTE
%state S_QUOTE
%state COMMAND1_VAR_D_QUOTE
%state COMMAND1_VAR_S_QUOTE
%state COMMAND2_VAR_D_QUOTE
%state COMMAND2_VAR_S_QUOTE

%%
<D_QUOTE, S_QUOTE, COMMAND1_VAR_D_QUOTE, COMMAND1_VAR_S_QUOTE, COMMAND2_VAR_D_QUOTE,
    COMMAND2_VAR_S_QUOTE> {ESCAPE_SEQUENCE}            { return WdlTypes.ESCAPE_SEQUENCE; }

<YYINITIAL> {COMMAND}                                  { yybegin(WAITING_COMMAND); return WdlTypes.COMMAND; }
<WAITING_COMMAND> {LBRACE}                             { yybegin(COMMAND1); return WdlTypes.COMMAND_DELIMITER_OPEN; }
<WAITING_COMMAND> {TRIPLE_ANGLE_OPEN}                  { yybegin(COMMAND2); return WdlTypes.COMMAND_DELIMITER_OPEN; }
<COMMAND1> {RBRACE}                                    { yybegin(YYINITIAL); return WdlTypes.COMMAND_DELIMITER_CLOSE; }
<COMMAND2> {TRIPLE_ANGLE_CLOSE}                        { yybegin(YYINITIAL); return WdlTypes.COMMAND_DELIMITER_CLOSE; }
<COMMAND1> {DEPRECATED_PLACEHOLDER_OPENER}             { yybegin(COMMAND1_VAR); if (wdlVersion == "draft-2") { return WdlTypes.COMMAND_VAR_OPENER; } else { return WdlTypes.DEPRECATED_COMMAND_VAR_OPENER; } }
<COMMAND1> {NEW_PLACEHOLDER_OPENER}                    { yybegin(COMMAND1_VAR); return WdlTypes.COMMAND_VAR_OPENER; }
<COMMAND2> {DEPRECATED_PLACEHOLDER_OPENER}             { if (wdlVersion == "draft-2") { yybegin(COMMAND2_VAR); return WdlTypes.COMMAND_VAR_OPENER; } else { return WdlTypes.COMMAND_CHAR; } }
<COMMAND2> {NEW_PLACEHOLDER_OPENER}                    { yybegin(COMMAND2_VAR); return WdlTypes.COMMAND_VAR_OPENER; }
<COMMAND1_VAR> {RBRACE}                                { yybegin(COMMAND1); return WdlTypes.RBRACE; }
<COMMAND2_VAR> {RBRACE}                                { yybegin(COMMAND2); return WdlTypes.RBRACE; }
<COMMAND1_VAR, COMMAND2_VAR> {ATTR_DEFAULT}            { return WdlTypes.COMMAND_ATTR_DEFAULT; }
<COMMAND1_VAR, COMMAND2_VAR> {ATTR_SEP}                { return WdlTypes.COMMAND_ATTR_SEP; }
<COMMAND1_VAR, COMMAND2_VAR> {ATTR_TRUE}               { return WdlTypes.COMMAND_ATTR_TRUE; }
<COMMAND1_VAR, COMMAND2_VAR> {ATTR_FALSE}              { return WdlTypes.COMMAND_ATTR_FALSE; }
<COMMAND1, COMMAND2> .                                 { return WdlTypes.COMMAND_CHAR; }

<YYINITIAL> {D_QUOTE_CHAR}                             { yybegin(D_QUOTE); return WdlTypes.QUOTE; }
<YYINITIAL> {S_QUOTE_CHAR}                             { yybegin(S_QUOTE); return WdlTypes.QUOTE; }
<COMMAND1_VAR> {D_QUOTE_CHAR}                          { yybegin(COMMAND1_VAR_D_QUOTE); return WdlTypes.QUOTE; }
<COMMAND1_VAR> {S_QUOTE_CHAR}                          { yybegin(COMMAND1_VAR_S_QUOTE); return WdlTypes.QUOTE; }
<COMMAND2_VAR> {D_QUOTE_CHAR}                          { yybegin(COMMAND2_VAR_D_QUOTE); return WdlTypes.QUOTE; }
<COMMAND2_VAR> {S_QUOTE_CHAR}                          { yybegin(COMMAND2_VAR_S_QUOTE); return WdlTypes.QUOTE; }
<D_QUOTE, COMMAND1_VAR_D_QUOTE, COMMAND2_VAR_D_QUOTE> {D_QUOTE_STRING_CHAR}   { return WdlTypes.QUOTE_STRING_CHAR; }
<S_QUOTE, COMMAND1_VAR_S_QUOTE, COMMAND2_VAR_S_QUOTE> {S_QUOTE_STRING_CHAR}   { return WdlTypes.QUOTE_STRING_CHAR; }
<D_QUOTE> {D_QUOTE_CHAR}                               { yybegin(YYINITIAL); return WdlTypes.QUOTE; }
<S_QUOTE> {S_QUOTE_CHAR}                               { yybegin(YYINITIAL); return WdlTypes.QUOTE; }
<COMMAND1_VAR_D_QUOTE> {D_QUOTE_CHAR}                  { yybegin(COMMAND1_VAR); return WdlTypes.QUOTE; }
<COMMAND1_VAR_S_QUOTE> {S_QUOTE_CHAR}                  { yybegin(COMMAND1_VAR); return WdlTypes.QUOTE; }
<COMMAND2_VAR_D_QUOTE> {D_QUOTE_CHAR}                  { yybegin(COMMAND2_VAR); return WdlTypes.QUOTE; }
<COMMAND2_VAR_S_QUOTE> {S_QUOTE_CHAR}                  { yybegin(COMMAND2_VAR); return WdlTypes.QUOTE; }

<YYINITIAL> {PRIMITIVE_TYPE}                           { return WdlTypes.PRIMITIVE_TYPE; }
<YYINITIAL> {OBJECT_TYPE}                              { return WdlTypes.OBJECT_TYPE; }
<YYINITIAL> {MAP_TYPE}                                 { return WdlTypes.MAP_TYPE; }
<YYINITIAL> {ARRAY_TYPE}                               { return WdlTypes.ARRAY_TYPE; }
<YYINITIAL> {PAIR_TYPE}                                { return WdlTypes.PAIR_TYPE; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {LBRACE}       { return WdlTypes.LBRACE; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {RBRACE}       { return WdlTypes.RBRACE; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {LPAREN}       { return WdlTypes.LPAREN; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {RPAREN}       { return WdlTypes.RPAREN; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {LSQUARE}      { return WdlTypes.LSQUARE; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {RSQUARE}      { return WdlTypes.RSQUARE; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {COLON}        { return WdlTypes.COLON; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {DOUBLE_EQUAL} { return WdlTypes.DOUBLE_EQUAL; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {NOT_EQUAL}    { return WdlTypes.NOT_EQUAL; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {EQUAL}        { return WdlTypes.EQUAL; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {DOT}          { return WdlTypes.DOT; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {QMARK}        { return WdlTypes.QMARK; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {PLUS}         { return WdlTypes.PLUS; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {DASH}         { return WdlTypes.DASH; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {ASTERISK}     { return WdlTypes.ASTERISK; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {SLASH}        { return WdlTypes.SLASH; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {PERCENT}      { return WdlTypes.PERCENT; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {LOGICAL_NOT}  { return WdlTypes.LOGICAL_NOT; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {COMMA}        { return WdlTypes.COMMA; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {DOUBLE_PIPE}  { return WdlTypes.DOUBLE_PIPE; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {DOUBLE_AMPERSAND}  { return WdlTypes.DOUBLE_AMPERSAND; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {LESS_EQUAL}   { return WdlTypes.LESS_EQUAL; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {LESS_THAN}    { return WdlTypes.LESS_THAN; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {MORE_EQUAL}   { return WdlTypes.MORE_EQUAL; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {MORE_THAN}    { return WdlTypes.MORE_THAN; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {IF}           { return WdlTypes.IF; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {THEN}         { return WdlTypes.THEN; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {ELSE}         { return WdlTypes.ELSE; }
<YYINITIAL> {COMMENT}                                  { return WdlTypes.COMMENT; }
<YYINITIAL> {IMPORT}                                   { return WdlTypes.IMPORT; }
<YYINITIAL> {ALIAS}                                    { return WdlTypes.ALIAS; }
<YYINITIAL> {VERSION}                                  { yybegin(WAITING_WORKFLOW_VERSION); return WdlTypes.VERSION; }
// For now, any file that is versioned is treated like a 1.0 file. That will have to probably change in the future:
<WAITING_WORKFLOW_VERSION> {VERSION_IDENTIFIER}        { wdlVersion = "1.0"; yybegin(YYINITIAL); return WdlTypes.VERSION_IDENTIFIER; }
<YYINITIAL> {WORKFLOW}                                 { yybegin(WAITING_WORKFLOW_IDENTIFIER_DECL); return WdlTypes.WORKFLOW; }
<WAITING_WORKFLOW_IDENTIFIER_DECL> {IDENTIFIER}        { yybegin(YYINITIAL); return WdlTypes.WORKFLOW_IDENTIFIER_DECL; }
<YYINITIAL> {CALL}                                     { return WdlTypes.CALL; }
<YYINITIAL> {AS}                                       { return WdlTypes.AS; }
<YYINITIAL> {INPUT}                                    { return WdlTypes.INPUT; }
<YYINITIAL> {OUTPUT}                                   { return WdlTypes.OUTPUT; }
<YYINITIAL> {WHILE}                                    { return WdlTypes.WHILE; }
<YYINITIAL> {SCATTER}                                  { return WdlTypes.SCATTER; }
<YYINITIAL> {IN}                                       { return WdlTypes.IN; }
<YYINITIAL> {TASK}                                     { yybegin(WAITING_TASK_IDENTIFIER_DECL); return WdlTypes.TASK; }
<YYINITIAL> {STRUCT}                                   { yybegin(WAITING_STRUCT_DECL); return WdlTypes.STRUCT; }
<WAITING_STRUCT_DECL> {IDENTIFIER}                     { yybegin(YYINITIAL); return WdlTypes.STRUCT_IDENTIFIER_DECL; }
<WAITING_TASK_IDENTIFIER_DECL> {IDENTIFIER}            { yybegin(YYINITIAL); return WdlTypes.TASK_IDENTIFIER_DECL; }
<YYINITIAL> {RUNTIME}                                  { return WdlTypes.RUNTIME; }
<YYINITIAL> {PARAMETER_META}                           { return WdlTypes.PARAMETER_META; }
<YYINITIAL> {META}                                     { return WdlTypes.META; }
<YYINITIAL> {OBJECT}                                   { return WdlTypes.OBJECT; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {BOOLEAN}      { return WdlTypes.BOOLEAN; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {NUMBER}       { return WdlTypes.NUMBER; }
<YYINITIAL, COMMAND1_VAR, COMMAND2_VAR> {IDENTIFIER}   { return WdlTypes.IDENTIFIER; }

{WHITE_SPACE}+                                         { return TokenType.WHITE_SPACE; }

.                                                      { return TokenType.BAD_CHARACTER; }
