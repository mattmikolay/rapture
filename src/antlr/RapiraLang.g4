grammar RapiraLang;

// Parser rules

dialog_unit
    : statement STMT_END EOF
    | routine_definition STMT_END EOF
    ;

file_input
    : ((statement STMT_END) | (routine_definition STMT_END) | STMT_END)* EOF
    ;

routine_definition
    : procedure
    | function
    ;

stmts
    : (statement STMT_END)*
    ;

statement
    : assignment
    | call
    | if_statement
    | case_statement
    | loop_statement
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
    : 'fun' IDENTIFIER? '(' function_params? ')' STMT_END declarations? stmts 'end'
    ;

function_params
    : '=>'? IDENTIFIER (',' '=>'? IDENTIFIER)*
    ;

procedure
    : 'proc' IDENTIFIER? '(' procedure_params? ')' STMT_END declarations? stmts 'end'
    ;

procedure_params
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

if_statement
    : 'if' expression 'then' stmts ('else' stmts)? 'fi'
    ;

case_statement
    : (
        ('case' expression ('when' expression (',' expression)* ':' stmts)*) |
        ('case' ('when' expression ':' stmts)*)
      ) ('else' stmts)? 'esac'
    ;

loop_statement
    : (for_clause | repeat_clause)? while_clause? 'do' stmts ('od' | ('until' expression))
    ;

for_clause
    : 'for' IDENTIFIER ('from' expression)? ('to' expression)? ('step' expression)?
    ;

repeat_clause
    : 'repeat' expression
    ;

while_clause
    : 'while' expression
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
