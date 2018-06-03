package seu.io

import seu.lr.Production
import seu.lr.Symbol
import java.io.BufferedReader
import java.io.FileReader

class YaccFile(filePath: String) {

    private var reader: BufferedReader = BufferedReader(FileReader(filePath))

    var headers: StringBuffer = StringBuffer()
    var instructions: HashMap<String, String> = HashMap()
    var rules: HashMap<Production, String?> = HashMap()
    var userSeg: StringBuffer = StringBuffer()

    init {
        readInstructions()
        readRules()
        readUserSeg()
    }

    private fun readInstructions() {
        while (true) {
            val lineOfReader = reader.readLine() ?: throw Exception("Lex format error - miss macro definitions")
            when {
                lineOfReader.startsWith("%%") -> return
                lineOfReader.startsWith("%{") -> readHeaders()
                else -> {
                    val split = lineOfReader.split(' ')
                    val tag = split[0]
                    split.subList(1, split.size)
                            .filter { s: String -> s.isNotEmpty() }
                            .forEach { s: String ->
                                kotlin.run {
                                    instructions[s] = tag
                                }
                            }
                }
            }
        }
    }

    private fun readHeaders() {
        while (true) {
            val lineOfReader = reader.readLine() ?: throw Exception("Lex format error - miss another \"%}\"")
            if (lineOfReader.startsWith("%}")) return
            else headers.append(lineOfReader).append("\n")
        }
    }

    private fun readRules() {
        var left: String? = null
        lineRead@ while (true) {
            val lineOfReader = reader.readLine()?.trim() ?: throw Exception("Lex format error - miss user segment")
            if (lineOfReader.startsWith("%%")) return
            else {
                var remain: String?
                var right: String
                var action: String? = null
                when {
                    lineOfReader.startsWith(";") -> {
                        left = null
                        continue@lineRead
                    }
                    lineOfReader.startsWith("|") ->
                        remain = lineOfReader.drop(1).trim()
                    else -> {
                        left = lineOfReader.substringBefore(":").trim()
                        remain = lineOfReader.substringAfter(":").trim()
                    }
                }
                var indexOfLeftBrace = remain.indexOf('{', 0)
                while (indexOfLeftBrace != -1 && remain[indexOfLeftBrace - 1] == '\'' && remain[indexOfLeftBrace + 1] == '\'') {
                    indexOfLeftBrace = remain.indexOf('{', indexOfLeftBrace + 1)
                }

                when (indexOfLeftBrace) {
                    -1 -> right = remain
                    else -> {
                        action = remain.substring(indexOfLeftBrace, remain.length).trim()
                        right = remain.substring(0, indexOfLeftBrace - 1).trim()
                    }
                }

                if (left == null || left.isEmpty() || right.isEmpty())
                    throw Exception("Lex format error - wrong production input")
                else {
                    val symbols = ArrayList<Symbol>()
                    right.split(' ')
                            .filter { s: String -> s.isNotEmpty() }
                            .forEach { s: String ->
                                symbols.add(Symbol(
                                        if (s.startsWith("'") && s.endsWith("'") || instructions.containsKey(s))
                                            Symbol.TERMINAL
                                        else
                                            Symbol.NON_TERMINAL, s))
                            }
                    val pro = Production(Symbol(Symbol.NON_TERMINAL, left), symbols)
                    rules[pro] = action
                }
            }
        }
    }

    private fun readUserSeg() {
        while (true) {
            val lineOfReader = reader.readLine() ?: return
            userSeg.append(lineOfReader).append("\n")
        }
    }
}