grammar Grammar;

VB
    : '|'
    ;

SEMICOL
    : ';'
    ;

BACKQUOT
    : '`'
    ;

DOUBLEQUOT
    : '"'
    ;

SINGLEQUOT
    : '\''
    ;

LT
    : '<'
    ;

GT
    : '>'
    ;

UNQUOTED
    : [^ \n'"`;|<>]
    ;

WS
    : [ \n]+
    ;

DQC
    : [^\n'"`]+
    ;

command
    : pipe
    | seq
    | call
    ;

pipe
    : call '|' call
    | pipe '|' call
    ;

seq
    : command ';' command
    ;

/* call
    : (NONKW | quoted)*
    ;
*/

call
    : WS (redirect WS)* arg (WS atom)* WS
    ;

atom
    : redirect | arg
    ;

arg
    : (quoted | UNQUOTED)+
    ;

redirect
    : LT WS arg | GT WS arg
    ;

quoted
 	: single | double | back
 	;

single
    : SINGLEQUOT ~('\n')* SINGLEQUOT
 	;

back
	: BACKQUOT ~('\n')* BACKQUOT
    ;

double
 	: DOUBLEQUOT (back | DQC) DOUBLEQUOT
 	;