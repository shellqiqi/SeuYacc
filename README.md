# SeuYacc

Yet another compiler-compiler written in Kotlin.
The target language is C++.

Its core tech is LR(1) optimized with hash and multi-thread programming.

## Build

Use maven to package.

## Usage

We need you to write a yacc file.
Here is an [example yacc file](resource/example4.y) of ANSI C.

Running JAR need two arguments

```sh
InputFile OutputFile
```

The input file is your yacc file.
The output file is the C++ parser code we generated.

### What function we provide

We provide function `void yyparse(void)` to start parsing.

We do have declared variables and included a head file below

```cpp
#include "yy.tab.h"

extern FILE* yyin;
extern FILE* yyout;
extern string yytext;
extern int column;
```

But these are generated by [SeuLex](https://github.com/shellqiqi/SeuLex).
**Do not implement them yourself.**

### What you should implement

You need to implement function `void yyerror(void)` to deal with grammatical errors.

## Defects

We prefer shift-in instead of reduce to solve IF-ELSE ambiguity.
We did not consider priorities and integration.

And a too big output C++ file.
