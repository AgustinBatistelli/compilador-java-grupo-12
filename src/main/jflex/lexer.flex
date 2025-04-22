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

  private static final int MAX_STRING_LENGTH = 50;
  private static final int MAX_IDENTIFIER_LENGTH = 50;
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]

// Operators:
Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = "="
Assignation = ":="
ArithmeticAssig = "=:"
Equals = "=="
GreaterThan = ">"
LessThan = "<"

// Symbols
OpenBrace = "{"
CloseBrace = "}"
OpenParentheses  = "("
CloseParentheses = ")"
OpenBracket = "["
CloseBracket = "]"
Colon = ":"
Comma  = ","

Letter = [a-zA-Z]
Digit = [0-9]

// Reserved Words
Init = "init"
Int = "Int"
FloatType = "Float"
String = "String"
If = "if"
Else = "else"
While = "while"
Read = "read"
Write = "write"
Reorder = "reorder"
NegativeCalculation = "negativeCalculation"


// Logic Operators
OR = "OR"
AND = "AND"
NOT = "NOT"

WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
FloatConstant = ("-")? (({Digit}+\.{Digit}+) | ({Digit}+\. ) | ( \.{Digit}+ ))
IntegerConstant = {Digit}+
StringLiteral = (\"([^\"\\]|\\.)*\") | (\“([^\“\\]|\\.)*\”)

%%

/* Main rules */
<YYINITIAL> {
  /* Handle '#' alone or unexpected */
  "#" { throw new UnknownCharacterException("Unexpected '#' without content"); }

  /* Reserved words */
  "init" { return symbol(ParserSym.INIT); }
  "Float" { return symbol(ParserSym.FLOAT_TYPE); }
  "Int" { return symbol(ParserSym.INT); }
  "String" { return symbol(ParserSym.STRING); }
  "if" { return symbol(ParserSym.IF); }
  "else" { return symbol(ParserSym.ELSE); }
  "while" { return symbol(ParserSym.WHILE); }
  "read" { return symbol(ParserSym.READ); }
  "write" { return symbol(ParserSym.WRITE); }
  "OR" { return symbol(ParserSym.OR); }
  "AND" { return symbol(ParserSym.AND); }
  "NOT" { return symbol(ParserSym.NOT); }
  "reorder" { return symbol(ParserSym.REORDER); }
  "negativeCalculation" { return symbol(ParserSym.NEGATIVE_CALCULATION); }

  /* Start multi-line comment */
  "#+" { yybegin(COMMENT); }

  /* Single-line comment */
  "#" [^\n]* { /* Ignore single-line comment */ }

  /* C-style comment (/* ... */) */
  "/*" ([^*]|\*+[^*/])*\*+ "/" { /* ignore C-style comment */ }

  /* Operators */
  {Equals} { return symbol(ParserSym.EQUALS); }
  {Plus} { return symbol(ParserSym.PLUS); }
  {Sub} { return symbol(ParserSym.SUB); }
  {Mult} { return symbol(ParserSym.MULT); }
  {Div} { return symbol(ParserSym.DIV); }
  {Assignation} { return symbol(ParserSym.ASSIGNATION); }
  {Assig} { return symbol(ParserSym.ASSIG); }
  {ArithmeticAssig} { return symbol(ParserSym.ARITHMETIC_ASSIG); }
  {OpenParentheses} { return symbol(ParserSym.OPEN_PARENTHESES); }
  {CloseParentheses} { return symbol(ParserSym.CLOSE_PARENTHESES); }
  {OpenBracket} { return symbol(ParserSym.OPEN_BRACKET); }
  {CloseBracket} { return symbol(ParserSym.CLOSE_BRACKET); }
  {OpenBrace} { return symbol(ParserSym.OPEN_BRACE); }
  {CloseBrace} { return symbol(ParserSym.CLOSE_BRACE); }
  {GreaterThan} { return symbol(ParserSym.GREATER_THAN); }
  {LessThan} { return symbol(ParserSym.LESS_THAN); }
  {Colon} { return symbol(ParserSym.COLON); }
  {Comma} { return symbol(ParserSym.COMMA); }

  /* Constants */
  {IntegerConstant} {
    String text = yytext();
    String digitsOnly = text.endsWith("L") || text.endsWith("l")
                            ? text.substring(0, text.length() - 1)
                            : text;

    if (digitsOnly.length() > 10) {
      throw new InvalidIntegerException(text);
    }

    try {
      int value = Integer.parseInt(digitsOnly);
    } catch (NumberFormatException e) {
      throw new InvalidIntegerException(text);
    }

    return symbol(ParserSym.INTEGER_CONSTANT, text);
  }

  /* String literal */
  {StringLiteral} {
    if (yytext().length() - 2 > MAX_STRING_LENGTH) {
      throw new InvalidLengthException("StringLiteral too long");
    }
    return symbol(ParserSym.STRING_LITERAL, yytext().substring(1, yytext().length() - 1));
  }

  /* Float */
  {FloatConstant} {
    System.out.println("Float: " + yytext());
    return symbol(ParserSym.FLOAT_CONSTANT, yytext());
  }

  /* Identifiers */
  {Identifier} {
    if (yytext().length() > MAX_IDENTIFIER_LENGTH) {
      throw new InvalidLengthException("Identifier too long");
    }
    return symbol(ParserSym.IDENTIFIER, yytext());
  }

  /* Whitespace */
  {WhiteSpace} { /* ignore */ }
}

/* Multi-line comment */
<COMMENT> {
  [^#\n]+ { /* ignore content */ }
  "#" { /* ignore standalone '#' inside comment */ }
  "\"" { /* ignore regular double quotes inside comment */ }
  "“" { /* ignore opening curly quotes inside comment */ }
  "”" { /* ignore closing curly quotes inside comment */ }
  \n { /* ignore newlines inside comment */ }
  "+#" { yybegin(YYINITIAL); /* End of multi-line comment */ }
}
%%



/* Error fallback */
. { throw new UnknownCharacterException(yytext()); }

