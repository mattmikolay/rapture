grammar RapiraLang;

// Parser rules

program
    : number EOF
    ;

number
    : SIGNED_INT | UNSIGNED_INT
    ;

// Lexer rules

UNSIGNED_INT
    : [0-9]+
    ;

SIGNED_INT
    : ('+' | '-') UNSIGNED_INT
    ;

COMMENT
    : '\\' ~[\r\n]* -> skip
    ;

WHITESPACE
    : [ \r\t\n]+ -> skip
    ;
