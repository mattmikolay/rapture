grammar RapiraLang;

// Parser rules

dialog_unit
    : statement
    ;

statement
    : number
    ;

number
    : INT
    | REAL
    ;

// Lexer rules

INT
    : UNSIGNED_INT
    | SIGNED_INT
    ;

UNSIGNED_INT
    : [0-9]+
    ;

SIGNED_INT
    : ('+' | '-') UNSIGNED_INT
    ;

REAL
    : UNSIGNED_REAL
    | SIGNED_REAL
    ;

UNSIGNED_REAL
    : [0-9]+ 'e' INT
    | [0-9]+ ('.' [0-9]+ ('e' INT)?)
    ;

SIGNED_REAL
    : ('+' | '-') UNSIGNED_REAL
    ;

COMMENT
    : '\\' ~[\r\n]* -> skip
    ;

WHITESPACE
    : [ \r\t\n]+ -> skip
    ;
