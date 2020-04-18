grammar RapiraLang;

// Parser rules

dialog_unit
    : statement EOF
    | routine_definition EOF
    ;

routine_definition
    : procedure
    | function
    ;

stmts
    : (statement (';' | '\r' | '\n'))*
    ;

statement
    : assignment
    | call
    // TODO if
    // TODO case
    // TODO loop
    | output
    | input
    | LOOP_EXIT
    | RETURN expression?
    ;

assignment
    : variable ':=' expression
    ;

variable
    : IDENTIFIER (index_expr)*
    ;

call
    : 'call' expression actual_proc_param
    | IDENTIFIER actual_proc_param
    ;

function
    : 'fun' IDENTIFIER? '(' function_params? ')' declarations? stmts 'end'
    ;

function_params
    : '=>'? IDENTIFIER (',' '=>'? IDENTIFIER)*
    ;

procedure
    : 'proc' IDENTIFIER? '(' procedure_params? ')' declarations? stmts 'end'
    ;

procedure_params
    : ('=>' | '<=')? IDENTIFIER (',' ('=>' | '<=')? IDENTIFIER)*
    ;

declarations
    : intern extern?
    | extern intern?
    ;

intern
    : 'intern' ':' IDENTIFIER (',' IDENTIFIER)*
    ;

extern
    : 'extern' ':' IDENTIFIER (',' IDENTIFIER)*
    ;

output
    : 'output' 'nlf'? (':' expression (',' expression)*)?
    ;

input
    : 'input' 'text'? ':' IDENTIFIER (',' IDENTIFIER)*
    ;

expression
    : or_expr
    ;

or_expr
    : and_expr ('or' and_expr)*
    ;

and_expr
    : not_expr ('and' not_expr)*
    ;

not_expr
    : ('not')? eq_expr
    ;

eq_expr
    : rel_expr (('=' | '/=') rel_expr)*
    ;

rel_expr
    : add_expr (('<' | '>' | '>=' | '<=') add_expr)*
    ;

add_expr
    : ('+' | '-')? product_expr (('+' | '-') product_expr)*
    ;

product_expr
    : exponent_expr (('*' | '/' | '//' | '/%') exponent_expr)*
    ;

exponent_expr
    : len_expr ('**' len_expr)*
    ;

len_expr
    : ('#')? subop_expr
    ;

subop_expr
    : base_expr (index_expr | actual_fun_param)*
    ;

base_expr
    : IDENTIFIER
    | TEXT
    | UNSIGNED_INT
    | UNSIGNED_REAL
    | procedure
    | function
    | LARROW (comma_expr)? RARROW
    | '(' expression ')'
    ;

comma_expr
    : expression (',' expression)*
    ;

index_expr
    : '[' comma_expr ']'
    | '[' expression? ':' expression? ']'
    ;

actual_proc_param
    : '(' (('<=' variable | '=>'? expression))? (',' ('<=' variable | '=>'? expression))* ')'
    ;

actual_fun_param
    : '(' ( '=>'? expression )? (',' '=>'? expression)* ')'
    ;

// Lexer rules

LARROW
    : '<*'
    ;

RARROW
    : '*>'
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
    : [0-9]+ 'e' ('+' | '-')? UNSIGNED_INT
    | [0-9]+ ('.' [0-9]+ ('e' ('+' | '-')? UNSIGNED_INT)?)
    ;

IDENTIFIER
    : [a-z][a-z0-9_]*
    ;

TEXT
    : '"' (~[\r\n"] | '""')* '"'
    ;

COMMENT
    : '\\' ~[\r\n]* -> skip
    ;

WHITESPACE
    : [ \r\t\n]+ -> skip
    ;
