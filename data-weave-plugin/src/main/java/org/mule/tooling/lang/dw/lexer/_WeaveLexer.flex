package org.mule.tooling.lang.dw.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static org.mule.tooling.lang.dw.parser.psi.WeaveTypes.*;
import static org.mule.tooling.lang.dw.parser.WeaveParserDefinition.*;

%%

%{
  public _WeaveLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _WeaveLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

DOT="."

LINE_COMMENT = "//" [^\r\n]*
MULTILINE_COMMENT = "/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?

DOUBLE_QUOTED_STRING=\"([^\\\"\r\n]|\\[^\r\n])*\"?
SINGLE_QUOTED_STRING='([^\\'\r\n]|\\[^\r\n])*'?
BACKTIKED_QUOTED_STRING=`([^\\`\r\n]|\\[^\r\n])*`?


RULE_MIME_TYPE=("text"|"audio"|"video"|"application"|"multipart"|"image") "/"[a-zA-Z\-_]+

NAMESPACE_URI=[a-z]+"://"[a-zA-Z0-9/:\.\-_?=&]+ | "urn:"[a-zA-Z0-9:\-_\.?=&]+

ID=({ALPHA}[:jletterdigit:]*) | "++" | "--"


DOLLAR_VARIABLE=[\$]+

RULE_ANY_REGEX=\/[^ ]([^\\\/\r\n]|\\[^\r\n])+\/
RULE_ANY_DATE="|"([^| \t\r\n] )+"|"

DIGIT=[0-9]
ALPHA=[:letter:]

INTEGER_LITERAL=(0|([1-9]({DIGIT})*))
DOUBLE_LITERAL=({FLOATING_POINT_LITERAL1})|({FLOATING_POINT_LITERAL3})

FLOATING_POINT_LITERAL1=({DIGIT})+{DOT}({DIGIT})*({EXPONENT_PART})?
FLOATING_POINT_LITERAL3=({DIGIT})+({EXPONENT_PART})
EXPONENT_PART=[Ee]["+""-"]?({DIGIT})*
AT_SPACE="@"[\ \t\f\n]
CARET_SPACE="^"[\ \t\f\n]

%%
<YYINITIAL> {
  {CARET_SPACE}                 {return CARET_SPACE;}
  {AT_SPACE}                 {return AT_SPACE;}
  {WHITE_SPACE}               { return com.intellij.psi.TokenType.WHITE_SPACE; }
  "("                         { return L_PARREN; }
  ")"                         { return R_PARREN; }
  "{|"                         {return OPEN_CLOSE_KEYWORD;}
  "{-|"                         {return OPEN_CLOSE_ORDERED_KEYWORD;}
  "{-"                          {return OPEN_ORDERED_KEYWORD;}
  "{"                         { return L_CURLY; }
  "}"                         { return R_CURLY; }
  "|}"                         { return CLOSE_CLOSE_KEYWORD; }
  "|-}"                         { return CLOSE_CLOSE_ORDERED_KEYWORD; }
  "-}"                         { return CLOSE_ORDERED_KEYWORD; }
  "["                         { return L_BRACKET; }
  "]"                         { return R_BRACKET; }
  "<:"                        { return SUB_TYPE;}
  ","                         { return COMMA; }
  "::"                         { return PACKAGE_SEPARATOR; }
  ":"                         { return COLON; }
  "using"                     { return USING; }
  "default"                   { return DEFAULT; }
  "as"                        { return AS; }
  "is"                        { return IS; }
  "if"                        { return IF;}
  "not"                        { return NOT_KEYWORD;}
  "unless"                    { return UNLESS;}
  "else"                      { return ELSE;}
  "---"                       { return DOCUMENT_SEPARATOR; }
  "!="                        { return NOT_EQUAL; }
  "~="                        { return SIMILAR; }
  "~"                         { return TILDE; }
  "=="                        { return EQUAL; }
  "="                         { return EQ; }
  "<"                         { return LESS; }
  "<="                        { return LESS_EQUAL; }
  ">"                         { return GREATER; }
  ">="                        { return GREATER_EQUAL; }
  "+"                         { return PLUS; }
  "-"                         { return MINUS; }
  "_"                         { return UNDERSCORE; }
  "*"                         { return MULTIPLY; }
  "/"                         { return DIVISION; }
  "%"                         { return MODULO; }
  "@"                         { return AT; }
  "?"                         { return QUESTION; }
  "!"                         { return ESCLAMATION; }
  "#"                         { return HASH; }
  "and"                       { return AND_KEYWORD; }
  "or"                        { return OR_KEYWORD; }
  "|"                         { return OR; }
  "^"                         { return XOR; }
  "&"                         { return AND; }
  "true"                      { return TRUE_LITERAL;}
  "do"                         { return DO_KEYWORD;}
  "from"                     { return FROM_KEYWORD;}
  "false"                     { return FALSE_LITERAL;}
  "null"                      { return NULL_LITERAL_KEYWORD;}
  "match"                     { return MATCH_KEYWORD;}
  "update"                     { return UPDATE_KEYWORD;}
  "at"                     { return AT_KEYWORD;}
  "matches"                     { return MATCHES_KEYWORD;}
  "->"                        { return ARROW_TOKEN;}
  "=>"                        { return FAT_ARROW;}

  "%dw"                       { return VERSION_DIRECTIVE_KEYWORD;}
  "input"                    { return INPUT_DIRECTIVE_KEYWORD;}
  "output"                   { return OUTPUT_DIRECTIVE_KEYWORD;}
  "annotation"                   { return ANNOTATION_DIRECTIVE_KEYWORD;}
  "ns"                       { return NAMESPACE_DIRECTIVE_KEYWORD;}
  "type"                     { return TYPE_DIRECTIVE_KEYWORD;}
  "var"                      { return VAR_DIRECTIVE_KEYWORD;}
  "fun"                      { return FUNCTION_DIRECTIVE_KEYWORD;}
  "import"                   { return IMPORT_DIRECTIVE_KEYWORD;}
  "case"                      { return CASE_KEYWORD;}

  {LINE_COMMENT}              { return LINE_COMMENT;}
  {MULTILINE_COMMENT}         { return MULTILINE_COMMENT; }
  {NAMESPACE_URI}             { return NAMESPACE_URI;}
  {RULE_MIME_TYPE}            { return MIME_TYPE_KEYWORD;}

  {RULE_ANY_DATE}             { return RULE_ANY_DATE; }
  {RULE_ANY_REGEX}            { return RULE_ANY_REGEX; }

  {DOUBLE_LITERAL}            { return DOUBLE_LITERAL; }
  {INTEGER_LITERAL}           { return INTEGER_LITERAL; }
  {DOLLAR_VARIABLE}           { return DOLLAR_VARIABLE;}

  {DOUBLE_QUOTED_STRING}      { return DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING}      { return SINGLE_QUOTED_STRING; }
  {BACKTIKED_QUOTED_STRING}      { return BACKTIKED_QUOTED_STRING; }

  {ID}                        { return ID; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
