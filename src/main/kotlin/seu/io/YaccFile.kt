package seu.io

import seu.lr.Production
import java.io.BufferedReader
import java.io.FileReader

class YaccFile(filePath: String) {

    private var reader: BufferedReader = BufferedReader(FileReader(filePath))

    var headers: StringBuffer = StringBuffer()
    var instructions: HashMap<String, String> = HashMap()
    var rules: HashMap<Production, String?> = HashMap()
    var terminals: HashSet<String> = HashSet()
    var nonTerminals: HashSet<String> = HashSet()
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
                    val token = split[1]
                    terminals.add(token)
                    instructions[token] = tag
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

                when {
                    indexOfLeftBrace == -1 -> right = remain
                    else -> {
                        action = remain.substring(indexOfLeftBrace, remain.length).trim()
                        right = remain.substring(0, indexOfLeftBrace - 1).trim()
                    }
                }

                if (left.isNullOrEmpty() || right.isEmpty())
                    throw Exception("Lex format error - wrong production input")
                else {
                    val pro = Production(left!!, right)
                    rules[pro] = action
                    nonTerminals.add(pro.left)
                    pro.right.forEach { t: String? ->  if(t!!.startsWith("'")&&t.endsWith("'")) terminals.add(t)}
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