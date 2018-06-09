package seu.io

import seu.lr.Production
import seu.lr.Symbol
import java.io.BufferedReader
import java.io.FileReader

/**
 * Construct YaccFile by given path.
 * It split file into several segments for further processing.
 *
 * @param filePath where yacc file is.
 */
class YaccFile(filePath: String) {

    private var reader: BufferedReader = BufferedReader(FileReader(filePath))

    /* Segment before the first %% */
    var headers = StringBuffer()
    /* Terminal symbols defined by %token */
    var tokens = HashSet<String>()
    /* The start symbol defined by %start */
    lateinit var start: Symbol
    /* Rules defined between %% */
    var rules = HashSet<Production>()
    /* Segment after the last %% */
    var userSeg = StringBuffer()

    init {
        readInstructions()
        readRules()
        readUserSeg()
    }

    /**
     * Read instructions like %start and %token.
     * Segment between %{ and %} should be reserved.
     */
    private fun readInstructions() {
        readLine@ while (true) {
            val lineOfReader = reader.readLine() ?: throw Exception("Lex format error - miss macro definitions")
            when {
                lineOfReader.startsWith("%%") -> return
                lineOfReader.startsWith("%{") -> readHeaders()
                else -> {
                    val split = lineOfReader.split(' ').filter { s: String -> s.isNotEmpty() }
                    val tag = split.firstOrNull() ?: continue@readLine
                    when (tag) {
                        "%token" -> split.subList(1, split.size)
                                .forEach { s: String -> tokens.add(s) }
                        "%start" -> start = Symbol(Symbol.NON_TERMINAL, split.getOrNull(1)
                                ?: throw Exception("Lex format error - lost definition after %start"))
                    }
                }
            }
        }
    }

    /**
     * Read segment between %{ and %}
     */
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
            if (lineOfReader.isBlank()) continue
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
                    lineOfReader.startsWith(":") ->
                        remain = lineOfReader.drop(1).trim()
                    !lineOfReader.contains(":") -> {
                        left = lineOfReader
                        continue@lineRead
                    }
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
                                        if (s.startsWith("'") && s.endsWith("'") || tokens.contains(s))
                                            Symbol.TERMINAL
                                        else
                                            Symbol.NON_TERMINAL, s))
                            }
                    val pro = Production(Symbol(Symbol.NON_TERMINAL, left), symbols, action)
                    rules.add(pro)
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