grammar RapiraLang;

// Parser rules

dialogUnit
    : statement STMT_END EOF
    | expressionStatement STMT_END EOF
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
    | exitStatement
    | returnStatement
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
    : 'if' condition=expression 'then' ifBody=stmts ('else' elseBody=stmts)? 'fi'
    ;

caseStatement
    : 'case' condition=expression multiWhenClause* ('else' elseBody=stmts)? 'esac' #conditionCaseStatement
    | 'case' singleWhenClause* ('else' elseBody=stmts)? 'esac' #conditionlessCaseStatement
    ;

multiWhenClause
    : 'when' expression (',' expression)* ':' stmts
    ;

singleWhenClause
    : 'when' expression ':' stmts
    ;

loopStatement
    : (forClause | repeatClause)? whileClause? 'do' stmts ('od' | ('until' untilExpr=expression))
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
    : 'output' nlf='nlf'? (':' expression (',' expression)*)?
    ;

inputStatement
    : 'input' inputMode=MODE_TEXT? ':' variable (',' variable)*
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
    : logicalExpression
    ;

logicalExpression
    : logicalExpression AND logicalExpression #andExpression
    | logicalExpression OR logicalExpression #orExpression
    | NOT comparisonExpression #notExpression
    | comparisonExpression #baseLogicalExpression
    ;

comparisonExpression
    : comparisonExpression op=(LESS | GREATER | LESSEQ | GREATEREQ) comparisonExpression #relationalExpression
    | comparisonExpression op=(EQ | NEQ) comparisonExpression #equalityExpression
    | arithmeticExpression #baseComparisonExpression
    ;

arithmeticExpression
    : arithmeticExpression '**' arithmeticExpression #exponentiationExpression
    | arithmeticExpression op=(MULT | DIVIDE | INTDIVIDE | MOD) arithmeticExpression #multiplicationExpression
    | arithmeticExpression op=(PLUS | MINUS) arithmeticExpression #additionExpression
    | op=(PLUS | MINUS) subopExpression #unaryExpression
    | subopExpression #unaryExpression
    ;

subopExpression
    : subopExpression (indexExpression | functionArguments) #subopModifiedExpression
    | '#' subopExpression #lengthExpression
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
    | '(' expression ')' #parentheticalExpression
    ;

commaExpression
    : expression (',' expression)*
    ;

indexExpression
    : '[' commaExpression ']'
    | '[' leftIndex=expression? ':' rightIndex=expression? ']'
    ;

procedureArguments
    : '(' procedureArgument? (',' procedureArgument)* ')'
    ;

procedureArgument
    : '<=' variable
    | '=>'? expression
    ;

functionArguments
    : '(' ( '=>'? expression )? (',' '=>'? expression)* ')'
    ;

// Lexer rules

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

MULT : '*' ;

DIVIDE : '/' ;

INTDIVIDE : '//' ;

MOD : '/%' ;

AND : 'and' ;

OR : 'or' ;

NOT : 'not' ;

MODE_TEXT
    : 'text'
    ;

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
