grammar Rapira;

// Parser rules

dialogUnit
    : statement STMT_END EOF
    | expressionStatement STMT_END EOF
    | routineDefinition STMT_END EOF
    ;

fileInput
    : fileStatement (STMT_END fileStatement)* STMT_END? EOF
    | STMT_END? EOF
    ;

fileStatement
    : statement
    | routineDefinition
    ;

routineDefinition
    : procedureDefinition
    | functionDefinition
    ;

stmts
    : (statement? STMT_END)*
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
    ;

assignStatement
    : variable ASSIGN expression
    ;

variable
    : variable LBRACKET commaExpression RBRACKET #variableCommaIndex
    | variable LBRACKET leftExpr=expression? COLON rightExpr=expression? RBRACKET #variableColonIndex
    | IDENTIFIER #variableIdentifier
    ;

callStatement
    : CALL expression procedureArguments
    | IDENTIFIER procedureArguments
    ;

functionDefinition
    : FUN IDENTIFIER? LPAREN functionParams? RPAREN STMT_END declarations? stmts END
    ;

functionParams
    : inParam (',' inParam)*
    ;

procedureDefinition
    : PROC IDENTIFIER? LPAREN procedureParams? RPAREN STMT_END declarations? stmts END
    ;

procedureParams
    : procedureParam (',' procedureParam)*
    ;

procedureParam
    : inParam
    | inOutParam
    ;

inParam
    : '=>'? IDENTIFIER
    ;

inOutParam
    : '<=' IDENTIFIER
    ;

declarations
    : intern extern?
    | extern intern?
    ;

intern
    : INTERN COLON IDENTIFIER (',' IDENTIFIER)* STMT_END
    ;

extern
    : EXTERN COLON IDENTIFIER (',' IDENTIFIER)* STMT_END
    ;

ifStatement
    : IF condition=expression THEN ifBody=stmts (ELSE elseBody=stmts)? (FI | BCE)
    ;

caseStatement
    : CASE condition=expression multiWhenClause* (ELSE elseBody=stmts)? (ESAC | BCE) #conditionCaseStatement
    | CASE singleWhenClause* (ELSE elseBody=stmts)? (ESAC | BCE) #conditionlessCaseStatement
    ;

multiWhenClause
    : WHEN expression (',' expression)* COLON stmts
    ;

singleWhenClause
    : WHEN expression COLON stmts
    ;

loopStatement
    : (forClause | repeatClause)? whileClause? DO stmts (OD | (UNTIL untilExpr=expression))
    ;

forClause
    : FOR IDENTIFIER (FROM fromExpr=expression)? (TO toExpr=expression)? (STEP stepExpr=expression)?
    ;

repeatClause
    : REPEAT expression
    ;

whileClause
    : WHILE expression
    ;

outputStatement
    : OUTPUT nlf=NLF? (COLON expression (',' expression)*)?
    ;

inputStatement
    : INPUT inputMode=MODE_TEXT? COLON variable (',' variable)*
    ;

exitStatement
    : LOOP_EXIT
    ;

returnStatement
    : RETURN expression?
    ;

expressionStatement
    : expression
    ;

expression
    : expression op=(LESS | GREATER | LESSEQ | GREATEREQ) expression #relationalExpression
    | expression op=(EQ | NEQ) expression #equalityExpression
    | NOT expression #notExpression
    | expression AND expression #andExpression
    | expression OR expression #orExpression
    | arithmeticExpression #baseComparisonExpression
    ;

arithmeticExpression
    : arithmeticExpression POWER arithmeticExpression #exponentiationExpression
    | arithmeticExpression op=(MULT | DIVIDE | INTDIVIDE | MOD) arithmeticExpression #multiplicationExpression
    | arithmeticExpression op=(PLUS | MINUS) arithmeticExpression #additionExpression
    | op=(PLUS | MINUS) subopExpression #unaryExpression
    | subopExpression #unaryExpression
    ;

subopExpression
    : subopExpression LBRACKET commaExpression RBRACKET #indexCommaExpression
    | subopExpression LBRACKET leftExpr=expression? COLON rightExpr=expression? RBRACKET #indexColonExpression
    | subopExpression functionArguments #functionInvocationExpression
    | HASH subopExpression #lengthExpression
    | baseExpression #baseSubopExpression
    ;

baseExpression
    : IDENTIFIER #identifierValue
    | TEXT #textValue
    | UNSIGNED_INT #intValue
    | UNSIGNED_REAL #realValue
    | procedureDefinition #procedureValue
    | functionDefinition #functionValue
    | LARROW (commaExpression)? RARROW #sequenceValue
    | LPAREN expression RPAREN #parentheticalExpression
    ;

commaExpression
    : expression (',' expression)*
    ;

procedureArguments
    : LPAREN procedureArgument? (',' procedureArgument)* RPAREN
    ;

procedureArgument
    : '<=' variable
    | '=>'? expression
    ;

functionArguments
    : LPAREN ( '=>'? expression )? (',' '=>'? expression)* RPAREN
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

STMT_END
    : (';' | NEWLINE)+
    ;

TEXT
    : '"' (~[\r\n"] | '""')* '"'
    ;

SKIPPED
    : (COMMENT | WHITESPACE) -> skip
    ;

fragment NEWLINE
    : [\r\n]
    ;

fragment WHITESPACE
    : [ \t]+
    ;

fragment COMMENT
    : '\\' ~[\r\n]*
    ;
