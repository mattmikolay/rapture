grammar Rapira;

// Parser rules

fileInput
    : stmtEnd? (fileStatement (stmtEnd fileStatement)*)? stmtEnd? EOF
    ;

fileStatement
    : routineDefinition
    | statement
    ;

routineDefinition
    : procedureDefinition
    | functionDefinition
    ;

stmts
    : (statement? stmtEnd)*
    ;

stmtEnd
    : (';' | NL)+
    ;

statement
    : assignStatement
    | callStatement
    | ifStatement
    | caseStatement
    | loopStatement
    | outputStatement
    | inputStatement
    | exitStatement
    | returnStatement
    | expressionStatement
    ;

assignStatement
    : variable NL* ASSIGN NL* expression
    ;

variable
    : variable NL* LBRACKET NL* commaExpression NL* RBRACKET #variableCommaIndex
    | variable NL* LBRACKET NL* leftExpr=expression? NL* COLON NL* rightExpr=expression? NL* RBRACKET #variableColonIndex
    | IDENTIFIER #variableIdentifier
    ;

callStatement
    : CALL NL* expression NL* procedureArguments
    | IDENTIFIER NL* procedureArguments
    ;

functionDefinition
    : FUN NL* IDENTIFIER? NL* LPAREN NL* functionParams? NL* RPAREN stmtEnd declarations? stmts END
    ;

functionParams
    : inParam (NL* ',' NL* inParam)*
    ;

procedureDefinition
    : PROC NL* IDENTIFIER? NL* LPAREN NL* procedureParams? NL* RPAREN stmtEnd declarations? stmts END
    ;

procedureParams
    : procedureParam (NL* ',' NL* procedureParam)*
    ;

procedureParam
    : inParam
    | inOutParam
    ;

inParam
    : '=>'? NL* IDENTIFIER
    ;

inOutParam
    : '<=' NL* IDENTIFIER
    ;

declarations
    : intern extern?
    | extern intern?
    ;

intern
    : INTERN NL* COLON NL* IDENTIFIER (NL* ',' NL* IDENTIFIER)* stmtEnd
    ;

extern
    : EXTERN NL* COLON NL* IDENTIFIER (NL* ',' NL* IDENTIFIER)* stmtEnd
    ;

ifStatement
    : IF NL* condition=expression NL* THEN ifBody=stmts (ELSE elseBody=stmts)? (FI | BCE)
    ;

caseStatement
    : CASE NL* condition=expression NL* multiWhenClause* (ELSE elseBody=stmts)? (ESAC | BCE) #conditionCaseStatement
    | CASE NL* singleWhenClause* (ELSE elseBody=stmts)? (ESAC | BCE) #conditionlessCaseStatement
    ;

multiWhenClause
    : WHEN NL* expression (NL* ',' NL* expression)* NL* COLON stmts
    ;

singleWhenClause
    : WHEN NL* expression NL* COLON stmts
    ;

loopStatement
    : (forClause | repeatClause)? NL* whileClause? NL* DO stmts (OD | (UNTIL NL* untilExpr=expression))
    ;

forClause
    : FOR NL* IDENTIFIER NL* (FROM NL* fromExpr=expression)? NL* (TO NL* toExpr=expression)? NL* (STEP NL* stepExpr=expression)?
    ;

repeatClause
    : REPEAT NL* expression
    ;

whileClause
    : WHILE NL* expression
    ;

outputStatement
    : OUTPUT NL* nlf=NLF? (NL* COLON NL* expression (NL* ',' NL* expression)*)?
    ;

inputStatement
    : INPUT NL* inputMode=MODE_TEXT? NL* COLON NL* variable (NL* ',' NL* variable)*
    ;

exitStatement
    : LOOP_EXIT
    ;

returnStatement
    : RETURN NL* expression?
    ;

// Grammar present in "Rapira Programming Language" document does not accept
// standalone expressions as statements, but doing so here enables REPL
// functionality.
expressionStatement
    : expression
    ;

expression
    : expression NL* op=(LESS | GREATER | LESSEQ | GREATEREQ) NL* expression #relationalExpression
    | expression NL* op=(EQ | NEQ) NL* expression #equalityExpression
    | NOT NL* expression #notExpression
    | expression NL* AND NL* expression #andExpression
    | expression NL* OR NL* expression #orExpression
    | arithmeticExpression #baseComparisonExpression
    ;

arithmeticExpression
    : arithmeticExpression NL* POWER NL* arithmeticExpression #exponentiationExpression
    | arithmeticExpression NL* op=(MULT | DIVIDE | INTDIVIDE | MOD) NL* arithmeticExpression #multiplicationExpression
    | arithmeticExpression NL* op=(PLUS | MINUS) NL* arithmeticExpression #additionExpression
    | op=(PLUS | MINUS) NL* subopExpression #unaryExpression
    | subopExpression #unaryExpression
    ;

subopExpression
    : subopExpression NL* LBRACKET NL* commaExpression NL* RBRACKET #indexCommaExpression
    | subopExpression NL* LBRACKET NL* leftExpr=expression? NL* COLON NL* rightExpr=expression? NL* RBRACKET #indexColonExpression
    | subopExpression NL* functionArguments #functionInvocationExpression
    | HASH NL* subopExpression #lengthExpression
    | baseExpression #baseSubopExpression
    ;

baseExpression
    : IDENTIFIER #identifierValue
    | TEXT #textValue
    | UNSIGNED_INT #intValue
    | UNSIGNED_REAL #realValue
    | procedureDefinition #procedureValue
    | functionDefinition #functionValue
    | LARROW NL* (commaExpression)? NL* RARROW #sequenceValue
    | LPAREN NL* expression NL* RPAREN #parentheticalExpression
    ;

commaExpression
    : expression (NL* ',' NL* expression)*
    ;

procedureArguments
    : LPAREN NL* procedureArgument? (NL* ',' NL* procedureArgument)* NL* RPAREN
    ;

procedureArgument
    : '<=' NL* variable
    | '=>'? NL* expression
    ;

functionArguments
    : LPAREN NL* ('=>'? NL* expression)? (NL* ',' NL* '=>'? NL* expression)* NL* RPAREN
    ;

// Lexer rules

ASSIGN : ':=' ;

LARROW : '<*' ;

RARROW : '*>' ;

LESS : '<' ;

GREATER : '>' ;

LESSEQ : '<=' ;

GREATEREQ : '>=' ;

EQ : '=' ;

NEQ : '/=' ;

PLUS : '+' ;

MINUS : '-' ;

POWER : '**' ;

MULT : '*' ;

DIVIDE : '/' ;

INTDIVIDE : '//' ;

MOD : '/%' ;

AND
    : 'and'
    | 'и'
    ;

END
    : 'end'
    | 'конец'
    ;

OR
    : 'or'
    | 'или'
    ;

NOT
    : 'not'
    | 'не'
    ;

LPAREN : '(' ;

RPAREN : ')' ;

LBRACKET : '[' ;

RBRACKET : ']' ;

COLON : ':' ;

HASH : '#' ;

FUN
    : 'fun'
    | 'функ'
    ;

PROC
    : 'proc'
    | 'проц'
    ;

INTERN
    : 'intern'
    | 'свои'
    ;

EXTERN
    : 'extern'
    | 'чужие'
    ;

CALL
    : 'call'
    | 'вызов'
    ;

IF
    : 'if'
    | 'если'
    ;

THEN
    : 'then'
    | 'то'
    ;

FI
    : 'fi'
    ;

ELSE
    : 'else'
    | 'иначе'
    ;

CASE
    : 'case'
    | 'выбор'
    ;

ESAC
    : 'esac'
    ;

// "Rapira Programming Language" document lists "все" as the terminating lexeme
// for both if statements and case statements in the Russian variant of Rapira,
// whereas the English variant has dedicated "fi" and "esac" lexemes.
//
// ReRap2 (mistakenly?) translates "fi" as "кесли" and "esac" as "квыбор".
//
// https://github.com/freeduke33/rerap2/blob/master/lexer.cpp
// http://ershov-arc.iis.nsk.su/archive/eaimage.asp?did=7651&fileid=106841
// http://ershov-arc.iis.nsk.su/archive/eaimage.asp?did=7651&fileid=106815
BCE
    : 'все'
    ;

WHEN
    : 'when'
    | 'при'
    ;

DO
    : 'do'
    | 'цикл'
    ;

OD
    : 'od'
    | 'кц'
    ;

// "Rapira Programming Language" document lists "кц по" as the Russian lexeme
// for "until". ReRap2 uses only "по".
// http://ershov-arc.iis.nsk.su/archive/eaimage.asp?did=7651&fileid=106815
UNTIL
    : 'until'
    | 'кц по'
    ;

FOR
    : 'for'
    | 'для'
    ;

FROM
    : 'from'
    | 'от'
    ;

STEP
    : 'step'
    | 'шаг'
    ;

WHILE
    : 'while'
    | 'пока'
    ;

OUTPUT
    : 'output'
    | 'вывод'
    ;

NLF
    : 'nlf'
    | 'бпс'
    ;

INPUT
    : 'input'
    | 'ввод'
    ;

MODE_TEXT
    : 'text'
    | 'текста'
    ;

LOOP_EXIT
    : 'exit'
    | 'выход'
    ;

RETURN
    : 'return'
    | 'возврат'
    ;

REPEAT
    : 'repeat'
    | 'повтор'
    ;

TO
    : 'to'
    | 'до'
    ;

UNSIGNED_INT
    : [0-9]+
    ;

UNSIGNED_REAL
    : [0-9]+ 'e' (PLUS | MINUS)? UNSIGNED_INT
    | [0-9]+ ('.' [0-9]+ ('e' (PLUS | MINUS)? UNSIGNED_INT)?)
    ;

IDENTIFIER
    : [a-zA-Z\p{Cyrillic}][a-zA-Z\p{Cyrillic}0-9_]*
    ;

TEXT
    : '"' (~[\r\n"] | '""')* '"'
    ;

SKIPPED
    : (COMMENT | WHITESPACE) -> skip
    ;

NL
    : [\r\n]
    ;

ILLEGAL_TOKEN
    : .
    ;

fragment WHITESPACE
    : [ \t]+
    ;

fragment COMMENT
    : '\\' ~[\r\n]*
    ;
