package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%state COMMENT
%eofval{
  return symbol(ParserSym.EOF);
%eofval}

%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]

Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = "="
Assignation = ":="
OpenBrace = "{"
CloseBrace = "}"
OpenBracket = "("
CloseBracket = ")"
Colon = ":"
Comma  = ","
Equals = "=="
GreaterThan = ">"
LessThan = "<"
Letter = [a-zA-Z]
Digit = [0-9]

Init = "init"
Float = "Float"
Int = "Int"
String = "String"
While = "while"
Write = "write"

WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = {Digit}+
StringLiteral = \"([^\"\\]|\\.)*\"

%%

/* keywords */
<YYINITIAL> {
  /* reserved words */
  "init" { return symbol(ParserSym.INIT); }
  "Float" { return symbol(ParserSym.FLOAT); }
  "Int" { return symbol(ParserSym.INT); }
  "String" { return symbol(ParserSym.STRING); }
  "while" { return symbol(ParserSym.WHILE); }
  "write" { return symbol(ParserSym.WRITE); }

  /* start comment */
  "#+" { yybegin(COMMENT); }

  /* string literal */
  {StringLiteral} { return symbol(ParserSym.STRING_LITERAL, yytext().substring(1, yytext().length()-1)); }

  /* identifiers */
  {Identifier} { return symbol(ParserSym.IDENTIFIER, yytext()); }

  /* Constants */
  {IntegerConstant} { return symbol(ParserSym.INTEGER_CONSTANT, yytext()); }

  /* operators */
  {Equals} {return symbol(ParserSym.EQUALS); }
  {Plus} { return symbol(ParserSym.PLUS); }
  {Sub} { return symbol(ParserSym.SUB); }
  {Mult} { return symbol(ParserSym.MULT); }
  {Div} { return symbol(ParserSym.DIV); }
  {Assignation} { return symbol(ParserSym.ASSIGNATION); }
  {Assig} { return symbol(ParserSym.ASSIG); }
  {OpenBracket} { return symbol(ParserSym.OPEN_BRACKET); }
  {CloseBracket} { return symbol(ParserSym.CLOSE_BRACKET); }
  {GreaterThan} { return symbol(ParserSym.GREATER_THAN); }
  {LessThan} { return symbol(ParserSym.LESS_THAN); }
  {OpenBrace} { return symbol(ParserSym.OPEN_BRACE); }
  {CloseBrace} { return symbol(ParserSym.CLOSE_BRACE); }
  {Colon} { return symbol(ParserSym.COLON); }
  {Comma} { return symbol(ParserSym.COMMA); }

  /* whitespace */
  {WhiteSpace} { /* ignore */ }
}

/* comment */
<COMMENT> {
  [^#\n]+ { /*  ignore comment content  */ }
  "#" { /* ignore # loose  */ }
  \n { /* ignore line breaks */ }
  "\+#" { yybegin(YYINITIAL); return symbol(ParserSym.COMMENT); } /* end of comment */
}

%%

/* error fallback */
[^\r\n]+  { throw new UnknownCharacterException(yytext()); }
