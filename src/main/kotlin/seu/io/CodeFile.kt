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
        return include() + '\n'
    }

    fun include(): String {
        return """
            #include <iostream>
            #include <unordered_map>
            #include <string>
            using namespace std;
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
        """.trimIndent())
        builder.append("\nvector<Production> productions {")
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
            builder.append("""
                void r$i() {
                    ${lr.productions[i].action ?: ""}
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
        val builder = StringBuilder()
        builder.append("const unordered_map<int, unordered_map<>>")
        return ""
    }
}
