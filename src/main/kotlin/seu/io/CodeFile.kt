package seu.io

import seu.lr.*

import java.io.BufferedWriter
import java.io.FileWriter

class CodeFile(private val yaccFile: YaccFile, private val lr: LR) {

    private lateinit var writer: BufferedWriter

    private val indexedState = HashMap<State, Int>()
    private val indexedNonTerminalSymbol = HashMap<Symbol, Int>()

    init {
        var count = 0
        for (state in lr.parsingTable.table.keys)
            indexedState[state] = count++
        count = 0
        for (symbol in lr.productions.map { p -> p.leftSymbol }.toHashSet())
            indexedNonTerminalSymbol[symbol] = count++
    }

    fun writeFile(filePath: String) {
        writer = BufferedWriter(FileWriter(filePath))
        writeHeaders()
        writer.write(generate())
        writeUserSeg()
        writer.close()
    }

    private fun writeHeaders() {
        writer.write(yaccFile.headers.toString())
    }

    private fun writeUserSeg() {
        writer.write(yaccFile.userSeg.toString())
    }

    fun generate(): String {
        return head() + '\n' +
                token() + '\n' +
                symbol() + '\n' +
                productions() + '\n' +
                actions() + '\n' +
                parsingTable() + '\n' +
                gotoTable() + '\n' +
                parse() + '\n'
    }

    fun head(): String {
        return """
            #include <iostream>
            #include <unordered_map>
            #include <string>
            #include <vector>
            #include <stack>
            #include "yy.tab.h"
            #define $$ -1
            using namespace std;
            extern FILE* yyin;
            extern FILE* yyout;
            extern string yytext;
            extern int column;
            void yyerror();
        """.trimIndent()
    }

    fun token(): String {
        val builder = StringBuilder("""
            class Token {
            public:
                string name;
                vector<string> values;
                int length;
                Token(string name = "", vector<string> values = vector<string>(), int length = 0)
                    :name(name), values(values), length(length) {}
            };
            stack<Token> tokenStack;
        """.trimIndent())
        return builder.toString()
    }

    fun symbol(): String {
        val builder = StringBuilder("""
            class SymbolTable {
            public:
                    vector<string> type;
                    vector<string> name;
                    void add(string type, vector<string> ids) {
                        for(int i = 0; i < ids.size(); i++) {
                            this -> type.push_back(type);
                            this -> name.push_back(ids[i]);
                        }
                    }
            };
            vector<SymbolTable> symbolTableStack;
        """.trimIndent())
        return builder.toString()
    }

    fun productions(): String {
        val builder = StringBuilder("""
            class Production {
            public:
                int l;
                int rl;
                Production(int l, int rl) : l(l), rl(rl) {}
            };
        """.trimIndent()).append('\n')
        builder.append("vector<Production> productions {")
        for (production in lr.productions) {
            builder.append("\n\t")
            builder.append("{${indexedNonTerminalSymbol[production.leftSymbol]}, ${production.rightSymbols.size}},")
        }
        builder.deleteCharAt(builder.lastIndex)
        builder.append("\n};")
        return builder.toString()
    }

    fun actions(): String {
        val builder = StringBuilder()
        for (i in lr.productions.indices) {
            builder.append("void r$i() {")
            lr.productions[i].rightSymbols.forEachIndexed { index, _ ->
                builder.append("\n\t").append("""
                        Token t${lr.productions[i].rightSymbols.size - index} = tokenStack.top(); tokenStack.pop();
                    """.trimIndent())
            }
            builder.append("\n").append("""
                    Token newToken;
                    ${(lr.productions[i].action ?: "")
                    .replace("$$", "newToken")
                    .replace("$", "t")}
                    tokenStack.push(newToken);
                }
            """.trimIndent()).append('\n')
        }
        builder.append("vector<void(*)()> actions { ")
        for (i in lr.productions.indices) {
            builder.append("r$i,")
        }
        builder.deleteCharAt(builder.lastIndex)
        builder.append(" };")
        return builder.toString()
    }

    fun parsingTable(): String {
        val builder = StringBuilder("""
            class Entry {
            public:
                int label;
                int target;
                Entry(int label, int target) : label(label), target(target) {}
            };
        """.trimIndent()).append('\n')
        builder.append("unordered_map<int, unordered_map<int, Entry>> parsingTable = {")
        for ((state, index) in indexedState) {
            builder.append("\n\t")
            builder.append("{ $index, { ")
            val row = lr.parsingTable.table[state] ?: HashMap()
            for ((symbol, entry) in row) {
                if (symbol.isNonTerminal()) continue
                builder.append("{ ${symbol.name}, Entry(${entry.label}, ${when (entry.target) {
                    is State -> indexedState[entry.target]
                    is Production -> lr.productions.indexOf(entry.target)
                    null -> -1
                    else -> throw Exception("Never reached")
                }}) },")
            }
            builder.deleteCharAt(builder.lastIndex)
            builder.append(" } },")
        }
        builder.deleteCharAt(builder.lastIndex)
        builder.append("};")
        return builder.toString()
    }

    fun gotoTable(): String {
        val builder = StringBuilder()
        builder.append("unordered_map<int, unordered_map<int, int>> gotoTable = {")
        for ((state, index) in indexedState) {
            builder.append("\n\t")
            builder.append("{ $index, { ")
            val row = lr.parsingTable.table[state] ?: HashMap()
            for ((symbol, entry) in row) {
                if (symbol.isTerminal()) continue
                builder.append("{ ${indexedNonTerminalSymbol[symbol]}, ${when (entry.target) {
                    is State -> indexedState[entry.target]
                    else -> throw Exception("Never reached")
                }} },")
            }
            builder.deleteCharAt(builder.lastIndex)
            builder.append(" } },")
        }
        builder.deleteCharAt(builder.lastIndex)
        builder.append("};")
        return builder.toString()
    }

    fun parse(): String {
        return """
            int yylex_() {
                int t = 0;
                do {
                    t = yylex();
                    if (yytext == "\n") {
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 100; j++)
                                cout << " ";
                            cout << endl;
                        }
                        cout << "现在的符号表：";
                        for (int j = 0; j < 100; j++)
                            cout << " ";
                        cout << endl;
                        for (int i = 0; i < symbolTableStack.size(); i++) {
                            SymbolTable st = symbolTableStack[i];
                            for (int j = 0; j < st.name.size(); j++) {
                                cout << " name: " << st.name[j] + " type: " << st.type[j];
                            }
                            for (int j = 0; j < 100; j++)
                                cout << " ";
                            cout << endl;
                        }
                        coord.Y++;
                        SetConsoleCursorPosition(GetStdHandle(STD_OUTPUT_HANDLE), coord);
                    }
                } while (t == 0);
                return t;
            }
            void yyparse() {
                coord.Y = 1;
                symbolTableStack.push_back(SymbolTable());
                int a = yylex_();
                stack<int> stack;
                stack.push(${indexedState[lr.startState]});
                while (true) {
                    try
                    {
                        int s = stack.top();
                        Entry e = parsingTable.at(s).at(a);
                        if (e.label == 0) {
                            stack.push(e.target);
                            tokenStack.push(Token(yytext));
                            a = yylex_();
                        }
                        else if (e.label == 1) {
                            for (size_t i = 0; i < productions.at(e.target).rl; i++)
                            {
                                stack.pop();
                            }
                            int t = stack.top();
                            stack.push(gotoTable.at(t).at(productions.at(e.target).l));
                            actions.at(e.target)();
                        }
                        else if (e.label == 2) {
                            break;
                        }
                    }
                    catch (const std::out_of_range& oor)
                    {
                        yyerror();
                        return;
                    }
                    catch (const std::exception&)
                    {
                        cerr << "fatal error" << endl;
                        return;
                    }
                }
            }
        """.trimIndent()
    }
}
