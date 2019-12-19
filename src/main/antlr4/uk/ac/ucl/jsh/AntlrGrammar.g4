grammar AntlrGrammar;

/*
Parser Rules
*/

start
    : command
    | 
    ;
    
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
    : WS? (redirection WS)* argument (WS (redirection|argument))* WS?
    ;

unquoted
    : UQ
    ;

argument
    : (quoted|unquoted)+
    ;

redirection
    : LT WS? argument 
    | GT WS? argument
    ;

/* 
Lexer Rules
*/

UQ
    : ~( ' ' | '\t' | '"' | '\'' | '`' | '\n' | ';' | '|' | '<' | '>' )+
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

/*
WS
    : (' ' | '\t' )+ -> skip
    ; //whitespace
*/

WS
    : (' ' | '\t' )+
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
    : '`' ~('\n'|'`')* '`'
    ; 

fragment DQC
    : ~('\n'|'"'|'`')
    ;