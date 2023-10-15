package org.oXML.xpath.parser;

import java_cup.runtime.Symbol;

%%
%cup

ALPHA=[A-Za-z]
ALPHANUM=[0-9A-Za-z]
DIGIT=[0-9]
NONNEWLINE_WHITE_SPACE_CHAR=[\ \t\b\012]
WHITE_SPACE_CHAR=[\n\ \t\b\012]
STRING_TEXT=(\\\'|[^\n\']|\\{WHITE_SPACE_CHAR}+\\)*

%eofval{
    return new Symbol(sym.EOF);
%eofval} 
%%

"," { return new Symbol(sym.COMMA); }
"!=" { return new Symbol(sym.NOTEQUALS); }
"=" { return new Symbol(sym.EQUALS); }
"(" { return new Symbol(sym.LPAREN); }
")" { return new Symbol(sym.RPAREN); }
"/" { return new Symbol(sym.SLASH); }
"//" { return new Symbol(sym.SLASHSLASH); }
"." { return new Symbol(sym.DOT); }
".." { return new Symbol(sym.DOTDOT); }
":" { return new Symbol(sym.COLON); }
"::" { return new Symbol(sym.DOUBLECOLON); }
"@" { return new Symbol(sym.AT); }
"*" { return new Symbol(sym.ASTERISK); }
"[" { return new Symbol(sym.LANGLE); }
"]" { return new Symbol(sym.RANGLE); }
"'" { return new Symbol(sym.QUOTE); }
"\"" { return new Symbol(sym.DOUBLEQUOTE); }
"processing-instruction" { return new Symbol(sym.PROCESSING_INSTRUCTION); }
"ancestor" { return new Symbol(sym.ANCESTOR); }
"ancestor-or-self" { return new Symbol(sym.ANCESTOR_OR_SELF); }
"attribute" { return new Symbol(sym.ATTRIBUTE); }
"child" { return new Symbol(sym.CHILD); }
"descendant" { return new Symbol(sym.DESCENDANT); }
"child" { return new Symbol(sym.CHILD); }
"descendant-or-self" { return new Symbol(sym.DESCENDANT_OR_SELF); }
"following" { return new Symbol(sym.FOLLOWING); }
"following-sibling" { return new Symbol(sym.FOLLOWING_SIBLING); }
"namespace" { return new Symbol(sym.NAMESPACE); }
"parent" { return new Symbol(sym.PARENT); }
"preceding" { return new Symbol(sym.PRECEDING); }
"preceding-sibling" { return new Symbol(sym.PRECEDING_SIBLING); }
"self" { return new Symbol(sym.SELF); }

{DIGIT}*(".")?{DIGIT}* { return new Symbol(sym.NUMBER, new Integer(yytext())); }
"'"{STRING_TEXT}"'" { return new Symbol(sym.LITERAL, new String(yytext())); }
"\""{STRING_TEXT}"\"" { return new Symbol(sym.LITERAL, new String(yytext())); }


[ \t\r\n\f] { /* ignore white space. */ }
. { System.err.println("Illegal character: "+yytext()); }
