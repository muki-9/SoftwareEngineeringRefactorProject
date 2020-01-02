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
    : call PIPEOP call 
    | pipe PIPEOP call
    ;

seq
    : pipe SEMICOL command 
    | call SEMICOL command
    ;

// seq2
//     : SEMICOL command seq2
//     |
//     ; /* epsilon */ 

singlequoted
    : SQ
    ;

doublequoted
    : DQ (backquoted | ~('\n'|'"'|'`'))* DQ
    ;

call
    : WS? (redirection WS)* argument (WS (redirection|argument))* WS?
    ;

unquoted
    : UQ
    ;

backquoted
    : BQ
    ;

argument
    : (unquoted|singlequoted|doublequoted|backquoted)+
    ;

redirection
    : LT WS? argument 
    | GT WS? argument
    ;

/* 
Lexer Rules
*/

DQ
    : '"'
    ;

PIPEOP
    : '|'
    ;
SEMICOL
    : ';'
    ;

UQ
    : ~( ' ' | '\t' | '"' | '\'' | '`' | '\n' | ';' | '|' | '<' | '>' )+
    ;

LT
    : '<'
    ;

GT
    : '>'
    ;

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