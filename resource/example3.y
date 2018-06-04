%{
#include <ctype.h>
%}
%token c d
%start S
%%
S   : C C
    ;
C   : c C
    | d
    ;
%%
yylex() {
    int c;
    c = getchar();
    if (isdigit(c)) {
        yylval = c-'0';
        return DIGIT;
    }
    return c;
}
