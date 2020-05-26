grammar RapiraLang;

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
    : variable ':=' expression
    ;

variable
    : variable '[' commaExpression ']' #variableCommaIndex
    | variable '[' leftExpr=expression? COLON rightExpr=expression? ']' #variableColonIndex
    | IDENTIFIER #variableIdentifier
    ;

callStatement
    : CALL expression procedureArguments
    | IDENTIFIER procedureArguments
    ;

functionDefinition
    : 'fun' IDENTIFIER? LPAREN functionParams? RPAREN STMT_END declarations? stmts 'end'
    ;

functionParams
    : inParam (',' inParam)*
    ;

procedureDefinition
    : 'proc' IDENTIFIER? LPAREN procedureParams? RPAREN STMT_END declarations? stmts 'end'
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
    : 'intern' COLON IDENTIFIER (',' IDENTIFIER)* STMT_END
    ;

extern
    : 'extern' COLON IDENTIFIER (',' IDENTIFIER)* STMT_END
    ;

ifStatement
    : 'if' condition=expression 'then' ifBody=stmts ('else' elseBody=stmts)? 'fi'
    ;

caseStatement
    : 'case' condition=expression multiWhenClause* ('else' elseBody=stmts)? 'esac' #conditionCaseStatement
    | 'case' singleWhenClause* ('else' elseBody=stmts)? 'esac' #conditionlessCaseStatement
    ;

multiWhenClause
    : 'when' expression (',' expression)* COLON stmts
    ;

singleWhenClause
    : 'when' expression COLON stmts
    ;

loopStatement
    : (forClause | repeatClause)? whileClause? 'do' stmts ('od' | ('until' untilExpr=expression))
    ;

forClause
    : 'for' IDENTIFIER ('from' fromExpr=expression)? ('to' toExpr=expression)? ('step' stepExpr=expression)?
    ;

repeatClause
    : 'repeat' expression
    ;

whileClause
    : 'while' expression
    ;

outputStatement
    : 'output' nlf='nlf'? (COLON expression (',' expression)*)?
    ;

inputStatement
    : 'input' inputMode=MODE_TEXT? COLON variable (',' variable)*
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
    : subopExpression '[' commaExpression ']' #indexCommaExpression
    | subopExpression '[' leftExpr=expression? COLON rightExpr=expression? ']' #indexColonExpression
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

AND : 'and' ;

OR : 'or' ;

NOT : 'not' ;

LPAREN : '(' ;

RPAREN : ')' ;

COLON : ':' ;

HASH : '#' ;

CALL
    : 'call'
    ;

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
    : [a-zA-Z][a-zA-Z0-9_]*
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
