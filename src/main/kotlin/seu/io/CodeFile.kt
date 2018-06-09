package seu.io

import seu.lr.*

import java.io.BufferedWriter
import java.io.FileWriter

class CodeFile(private val yaccFile: YaccFile, private val lr: LR) {

    private lateinit var writer: BufferedWriter

    private val indexedState = HashMap<State, Int>()
    private val indexedProduction = lr.productions
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

    fun parsingTable(): String {
        val builder = StringBuilder()
        builder.append("const unordered_map<int, unordered_map<>>")
        return ""
    }
}
