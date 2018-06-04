%{
#include <ctype.h>
%}
%token and or b
%%
E   : E and F
    | F
    ;
F   : F or G
    | G
    ;
G   : b
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
