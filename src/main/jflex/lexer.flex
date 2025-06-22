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
%state COMMENT_BLOCK
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

/* ===== Regular Definitions ===== */
LineTerminator     = \r|\n|\r\n
InputCharacter     = [^\r\n]
Identation         = [ \t\f]

Letter             = [a-zA-Z]
Digit              = [0-9]
WhiteSpace         = {LineTerminator} | {Identation}
Identifier         = {Letter}({Letter}|{Digit})*
FloatConstant      = ("-")?(({Digit}+\.{Digit}+) | ({Digit}+\. ) | ( \.{Digit}+ ))
IntegerConstant    = {Digit}+
StringLiteral      = (\"([^\"\\]|\\.)*\") | (\“([^\“\\]|\\.)*\”)

ValidCommentChar   = [a-zA-Z0-9 \t\f\r\n.,;:\-*/=!<>()\[\]{}\"'“”_áéíóúÁÉÍÓÚñÑ]

/* ===== Main Rules ===== */
%%

<YYINITIAL> {

  /* Comentario tipo #+ ... +# */
  "#+"            { yybegin(COMMENT_BLOCK); }

  /* Palabras reservadas */
  "init"          { return symbol(ParserSym.INIT); }
  "Float"         { return symbol(ParserSym.FLOAT_TYPE); }
  "Int"           { return symbol(ParserSym.INT); }
  "String"        { return symbol(ParserSym.STRING); }
  "if"            { return symbol(ParserSym.IF); }
  "else"          { return symbol(ParserSym.ELSE); }
  "while"         { return symbol(ParserSym.WHILE); }
  "read"          { return symbol(ParserSym.READ); }
  "write"         { return symbol(ParserSym.WRITE); }
  "OR"            { return symbol(ParserSym.OR); }
  "AND"           { return symbol(ParserSym.AND); }
  "NOT"           { return symbol(ParserSym.NOT); }
  "reorder"       { return symbol(ParserSym.REORDER); }
  "negativeCalculation" { return symbol(ParserSym.NEGATIVE_CALCULATION); }

  /* Operadores */
  ":="            { return symbol(ParserSym.ASSIGNATION); }
  "=:"            { return symbol(ParserSym.ARITHMETIC_ASSIG); }
  "="             { return symbol(ParserSym.ASSIG); }
  "=="            { return symbol(ParserSym.EQUALS); }
  "+"             { return symbol(ParserSym.PLUS); }
  "-"             { return symbol(ParserSym.SUB); }
  "*"             { return symbol(ParserSym.MULT); }
  "/"             { return symbol(ParserSym.DIV); }
  ">"             { return symbol(ParserSym.GREATER_THAN); }
  "<"             { return symbol(ParserSym.LESS_THAN); }

  /* Símbolos */
  "{"             { return symbol(ParserSym.OPEN_BRACE); }
  "}"             { return symbol(ParserSym.CLOSE_BRACE); }
  "("             { return symbol(ParserSym.OPEN_PARENTHESES); }
  ")"             { return symbol(ParserSym.CLOSE_PARENTHESES); }
  "["             { return symbol(ParserSym.OPEN_BRACKET); }
  "]"             { return symbol(ParserSym.CLOSE_BRACKET); }
  ":"             { return symbol(ParserSym.COLON); }
  ","             { return symbol(ParserSym.COMMA); }

  /* Constantes */
  {IntegerConstant} {
    String text = yytext();
    if (text.length() > 10) throw new InvalidIntegerException(text);
    try { Integer.parseInt(text); }
    catch (NumberFormatException e) { throw new InvalidIntegerException(text); }
    return symbol(ParserSym.INTEGER_CONSTANT, text);
  }

  {FloatConstant} {
    return symbol(ParserSym.FLOAT_CONSTANT, yytext());
  }

  {StringLiteral} {
    if (yytext().length() - 2 > MAX_STRING_LENGTH)
      throw new InvalidLengthException("StringLiteral too long");
    return symbol(ParserSym.STRING_LITERAL, yytext().substring(1, yytext().length() - 1));
  }

  {Identifier} {
    if (yytext().length() > MAX_IDENTIFIER_LENGTH)
      throw new InvalidLengthException("Identifier too long");
    return symbol(ParserSym.IDENTIFIER, yytext());
  }

  {WhiteSpace} { /* ignorar */ }

  . { throw new UnknownCharacterException(yytext()); }
}

/* ===== Comentario #+ ... +# ===== */
<COMMENT_BLOCK> {
  // Secuencia inválida como + seguido de algo que no es #
  "+" {
    int next = yycharat(1);
    if (next == '#') {
      yybegin(YYINITIAL);
      return symbol(ParserSym.COMMENT);
    }
  }

  // Caracteres válidos (comentario normal)
  {ValidCommentChar}+ { /* OK */ }

  {WhiteSpace} { /* OK */ }

  // Cualquier otro carácter inválido
  . { throw new UnknownCharacterException("Carácter inválido en comentario: " + yytext()); }
}

