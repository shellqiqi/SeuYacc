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
                syntaxTree() + '\n' +
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
            #include <deque>
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

    fun syntaxTree(): String {
        return """
            class SyntaxTree;
            class Node;

            class Node {
            public:
                string type;
                string name;
                SyntaxTree * child;

                Node(string type = "", string name = "", SyntaxTree * child = nullptr) :type(type), name(name), child(child) {}

                bool hasChild() { return !(child == nullptr); }
                string toString() { return string("(").append(type).append(",").append(name).append(")"); }
            };

            class SyntaxTree {
            public:
                Node * parent;
                deque<Node> nodes;

                SyntaxTree(Node * parent = nullptr) : parent(parent) {}

                bool hasParent() { return !(parent == nullptr); }
                void output() {
                    deque<bool> d;
                    output(this, d);
                }
            private:
                void mcout(deque<bool> deep, string s) {
                    for (size_t i = 0; i < deep.size() - 1; i++)
                        if (deep.at(i))
                            cout << "│ ";
                        else
                            cout << "  ";
                    if (deep.back())
                        cout << "├─";
                    else
                        cout << "└─";
                    cout << s << endl;
                }
                void output(SyntaxTree* syntaxTree, deque<bool> &deep) {
                    deep.push_back(true);
                    for (size_t i = 0; i < syntaxTree->nodes.size(); i++) {
                        if (i == syntaxTree->nodes.size() - 1) {
                            deep.pop_back();
                            deep.push_back(false);
                        }
                        mcout(deep, syntaxTree->nodes.at(i).toString());
                        if (syntaxTree->nodes.at(i).hasChild())
                            output(syntaxTree->nodes.at(i).child, deep);
                    }
                    deep.pop_back();
                }
            };
        """.trimIndent()
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
        val builder = StringBuilder("""
            SyntaxTree syntaxTree;
        """.trimIndent()).append('\n')
        for (i in lr.productions.indices) {
            builder.append("""
                void r$i() {
                    SyntaxTree *temp = new SyntaxTree();
                    Node *pNode = new Node("${lr.productions[i].leftSymbol}", "${lr.productions[i].rightSymbols}", temp);
                    temp->parent = pNode;
                    for (int i = 0; i < ${lr.productions[i].rightSymbols.size}; i++) {
                        temp->nodes.push_front(syntaxTree.nodes.back());
                        syntaxTree.nodes.pop_back();
                    }
                    syntaxTree.nodes.push_back(*pNode);
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
                } while (t == 0);
                return t;
            }
            void yyparse() {
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
                            syntaxTree.nodes.push_back(Node(to_string(a), yytext));
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
