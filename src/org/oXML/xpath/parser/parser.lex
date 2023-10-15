package org.oXML.xpath.parser;

import java_cup.runtime.Symbol;
import java.math.BigInteger;
import java.math.BigDecimal;

%%
%cup
%unicode
%class GeneratedLexer
%yyeof

%eofval{
return new Symbol(sym.EOF);
%eofval}

%yylexthrow{
ParserException
%yylexthrow}

Digit=[0-9]
Letter=[A-Za-z]
NCNameChar=({Letter}|{Digit}|"-"|"_")
NCName=({Letter}|"_")({NCNameChar})*
%%

"-" { return new Symbol(sym.MINUS); }
"+" { return new Symbol(sym.PLUS); }
"<" { return new Symbol(sym.LT); }
">" { return new Symbol(sym.GT); }
"<=" { return new Symbol(sym.LTE); }
">=" { return new Symbol(sym.GTE); }
"$" { return new Symbol(sym.DOLLAR); }
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
"@" { return new Symbol(sym.AT); }
"*" { return new Symbol(sym.ASTERISK); }
"[" { return new Symbol(sym.LANGLE); }
"]" { return new Symbol(sym.RANGLE); }
"|" { return new Symbol(sym.BAR); }
"node" { return new Symbol(sym.NODE); }
"text" { return new Symbol(sym.TEXT); }
"comment" { return new Symbol(sym.COMMENT); }
"processing-instruction" { return new Symbol(sym.PROCESSING_INSTRUCTION); }
"ancestor::" { return new Symbol(sym.ANCESTOR); }
"ancestor-or-self::" { return new Symbol(sym.ANCESTOR_OR_SELF); }
"attribute::" { return new Symbol(sym.ATTRIBUTE); }
"child::" { return new Symbol(sym.CHILD); }
"descendant::" { return new Symbol(sym.DESCENDANT); }
"child::" { return new Symbol(sym.CHILD); }
"descendant-or-self::" { return new Symbol(sym.DESCENDANT_OR_SELF); }
"following::" { return new Symbol(sym.FOLLOWING); }
"following-sibling::" { return new Symbol(sym.FOLLOWING_SIBLING); }
"namespace::" { return new Symbol(sym.NAMESPACE); }
"parent::" { return new Symbol(sym.PARENT); }
"preceding::" { return new Symbol(sym.PRECEDING); }
"preceding-sibling::" { return new Symbol(sym.PRECEDING_SIBLING); }
"self::" { return new Symbol(sym.SELF); }
"div" { return new Symbol(sym.DIV); }
"mod" { return new Symbol(sym.MOD); }
"and" { return new Symbol(sym.AND); }
"or" { return new Symbol(sym.OR); }
\"[^\"]*\"               { return new Symbol(sym.LITERAL, yytext().substring(1, yytext().length() - 1)); }
\'[^\']*\'               { return new Symbol(sym.LITERAL, yytext().substring(1, yytext().length() - 1)); }
{Digit}*"."{Digit}* 	 { return new Symbol(sym.NUMBER, new Double(yytext())); }
{Digit}+               	 { return new Symbol(sym.NUMBER, new Double(yytext())); }
{NCName}                 { return new Symbol(sym.NC_NAME, yytext()); }
[ \t\r\n\f]              { /* ignore white space. */ }
.                        { throw new ParserException("illegal character", yytext()); }
