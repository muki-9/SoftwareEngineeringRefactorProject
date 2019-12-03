grammar AntlrGrammar;

/*
Parser Rules
*/


command
    : pipe  
    | seq 
    | call 
    | NL
    ;

pipe
    : call '|' call 
    | pipe '|' call
    ;

seq
    : pipe ';' command seq2
    | call ';' command seq2
    ;

seq2
    : ';' command seq2
    |
    ; /* epsilon */ 

quoted
    : SQ
    | DQ 
    | BQ
    ;

call
    :  (redirection)* commandtoken (atom)*
    ;

ctoken
    : CTOKEN
    ;

atom
    : redirection 
    | argument
    ;

argument
    : (quoted|UQ)+
    ;

redirection
    : LT WS? argument 
    | GT WS? argument
    ;

/* 
Lexer Rules
*/

CTOKEN
	: 'echo' | 'cd' | 'grep' | 'pwd' | 'ls' | 'cat' |'head' | 'tail' | 'sed' | 'wc' | 'find'
	;

UQ
    : ~( ' ' | '\t'  | '"' | '\'' | '`' | '\n' | ';' | '|' | '<' | '>' )+
    ;

LT
    : '<'
    ;

GT
    : '>'
    ;

DQ
    : '"' (BackQuote | DQC)* '"'
    ;

WS
    : (' ' | '\t' )+ -> skip
    ; //whitespace

NKW
    : ~('\n' | '\'' | '"' | '`' | ';' | '|')
    ; // non keyword

BQ
    : '`' ~('\n' | '`' )* '`'
    ; 

SQ
    : '\'' ~('\n'|'\'')* '\''
    ; 

NL
    : '\n'
    ;


fragment BackQuote
    : '`' ~('\n' | '`' )* '`'
    ; 

fragment DQC
    : ~('\n' | '"'| '`')
    ;