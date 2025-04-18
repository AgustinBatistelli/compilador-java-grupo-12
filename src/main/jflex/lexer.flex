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
Equals = "=="
GreaterThan = ">"
LessThan = "<"

// Symbols
OpenBrace = "{"
CloseBrace = "}"
OpenBracket = "("
CloseBracket = ")"
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

// Logic Operators
OR = "OR"
AND = "AND"
NOT = "NOT"

WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
Float = (Digit+\.Digit*)|(\.Digit+)
IntegerConstant = {Digit}+


StringLiteral = \"([^\"\\]|\\.)*\"

%%

/* keywords */
<YYINITIAL> {
  /* reserved words */
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

  /* start comment:  #+ ... +# */
   "#+" { yybegin(COMMENT); }

   /* start comment: /* ... */ */
   "/*" ([^*] | \*+[^*/])* \*+ "/" { /* ignore C-style comment */ }

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

   /* Constants */
  {IntegerConstant}  {
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
    /* string literal */
   {StringLiteral} {
     if (yytext().length() - 2 > MAX_STRING_LENGTH) {
       throw new InvalidLengthException("StringLiteral too long");
     }
     return symbol(ParserSym.STRING_LITERAL, yytext().substring(1, yytext().length()-1));
   }

   /* float */
   {Float} { return new Symbol(ParserSym.FLOAT, yytext()); }

   /* identifiers */
   {Identifier} {
     if (yytext().length() > MAX_IDENTIFIER_LENGTH) {
       throw new InvalidLengthException("Identifier too long");
     }
     return symbol(ParserSym.IDENTIFIER, yytext());
   }

  /* whitespace */
  {WhiteSpace} { /* ignore */ }
}

/* comment */
"/*" ([^*] | \*+[^*/])* \*+ "/" { /* ignore C-style comment */ }
<COMMENT> {
  [^#\n]+ { /*  ignore comment content  */ }
  "#" { /* ignore # loose  */ }
  \n { /* ignore line breaks */ }
  "\+#" { yybegin(YYINITIAL); return symbol(ParserSym.COMMENT); } /* end of comment */
}

%%

/* error fallback */
[^\r\n]+  { throw new UnknownCharacterException(yytext()); }

"#" { throw new UnknownCharacterException(yytext()); }

