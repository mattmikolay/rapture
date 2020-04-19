grammar RapiraLang;

// Parser rules

dialogUnit
    : statement STMT_END EOF
    | routineDefinition STMT_END EOF
    ;

fileInput
    : ((statement STMT_END) | (routineDefinition STMT_END) | STMT_END)* EOF
    ;

routineDefinition
    : procedureDefinition
    | functionDefinition
    ;

stmts
    : (statement STMT_END)*
    ;

statement
    : assignStatement
    | callStatement
    | ifStatement
    | caseStatement
    | loopStatement
    | outputStatement
    | inputStatement
    | LOOP_EXIT
    | RETURN expression?
    ;

assignStatement
    : variable ':=' expression
    ;

variable
    : IDENTIFIER (indexExpression)*
    ;

callStatement
    : 'call' expression procedureArguments
    | IDENTIFIER procedureArguments
    ;

functionDefinition
    : 'fun' IDENTIFIER? '(' functionParams? ')' STMT_END declarations? stmts 'end'
    ;

functionParams
    : '=>'? IDENTIFIER (',' '=>'? IDENTIFIER)*
    ;

procedureDefinition
    : 'proc' IDENTIFIER? '(' procedureParams? ')' STMT_END declarations? stmts 'end'
    ;

procedureParams
    : ('=>' | '<=')? IDENTIFIER (',' ('=>' | '<=')? IDENTIFIER)*
    ;

declarations
    : intern extern?
    | extern intern?
    ;

intern
    : 'intern' ':' IDENTIFIER (',' IDENTIFIER)* STMT_END
    ;

extern
    : 'extern' ':' IDENTIFIER (',' IDENTIFIER)* STMT_END
    ;

ifStatement
    : 'if' expression 'then' stmts ('else' stmts)? 'fi'
    ;

caseStatement
    : (
        ('case' expression ('when' expression (',' expression)* ':' stmts)*) |
        ('case' ('when' expression ':' stmts)*)
      ) ('else' stmts)? 'esac'
    ;

loopStatement
    : (forClause | repeatClause)? whileClause? 'do' stmts ('od' | ('until' expression))
    ;

forClause
    : 'for' IDENTIFIER ('from' expression)? ('to' expression)? ('step' expression)?
    ;

repeatClause
    : 'repeat' expression
    ;

whileClause
    : 'while' expression
    ;

outputStatement
    : 'output' 'nlf'? (':' expression (',' expression)*)?
    ;

inputStatement
    : 'input' 'text'? ':' IDENTIFIER (',' IDENTIFIER)*
    ;

expression
    : logicalExpression
    ;

logicalExpression
    : logicalExpression 'and' logicalExpression
    | logicalExpression 'or' logicalExpression
    | 'not' comparisonExpression
    | comparisonExpression
    ;

comparisonExpression
    : comparisonExpression ('<' | '>' | '>=' | '<=') comparisonExpression
    | comparisonExpression ('=' | '/=') comparisonExpression
    | arithmeticExpression
    ;

arithmeticExpression
    : arithmeticExpression '**' arithmeticExpression #exponentiationExpression
    | arithmeticExpression ('*' | '/' | '//' | '/%') arithmeticExpression #multiplicationExpression
    | arithmeticExpression op=(PLUS | MINUS) arithmeticExpression #additionExpression
    | op=(PLUS | MINUS) subopExpression #unaryExpression
    | subopExpression #unaryExpression
    ;

subopExpression
    : subopExpression (indexExpression | functionArguments)
    | '#' subopExpression
    | baseExpression
    ;

baseExpression
    : IDENTIFIER #identifierValue
    | TEXT #textValue
    | UNSIGNED_INT #intValue
    | UNSIGNED_REAL #realValue
    | procedureDefinition #procedureValue
    | functionDefinition #functionValue
    | LARROW (commaExpression)? RARROW #sequenceValue
    | '(' expression ')' #parentheticalExpression
    ;

commaExpression
    : expression (',' expression)*
    ;

indexExpression
    : '[' commaExpression ']'
    | '[' expression? ':' expression? ']'
    ;

procedureArguments
    : '(' (('<=' variable | '=>'? expression))? (',' ('<=' variable | '=>'? expression))* ')'
    ;

functionArguments
    : '(' ( '=>'? expression )? (',' '=>'? expression)* ')'
    ;

// Lexer rules

LARROW : '<*' ;

RARROW : '*>' ;

PLUS : '+' ;

MINUS : '-' ;

LOOP_EXIT
    : 'exit'
    ;

RETURN
    : 'return'
    ;

UNSIGNED_INT
    : [0-9]+
    ;

UNSIGNED_REAL
    : [0-9]+ 'e' (PLUS | MINUS)? UNSIGNED_INT
    | [0-9]+ ('.' [0-9]+ ('e' (PLUS | MINUS)? UNSIGNED_INT)?)
    ;

IDENTIFIER
    : [a-z][a-z0-9_]*
    ;

TEXT
    : '"' (~[\r\n"] | '""')* '"'
    ;

STMT_END
    : ';'
    | NEWLINE+
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
