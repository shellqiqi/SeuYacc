package seu.io

import java.io.BufferedReader
import java.io.FileReader

class YaccFile(filePath: String) {

    private var reader: BufferedReader = BufferedReader(FileReader(filePath))

    var headers: StringBuffer = StringBuffer()
    var instructions: HashMap<String, String> = HashMap()
    var rules: HashMap<String, ArrayList<String>> = HashMap()
    var userSeg: StringBuffer = StringBuffer()

    init {
        reader = BufferedReader(FileReader(filePath))
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
        while (true) {
            val lineOfReader = reader.readLine() ?: throw Exception("Lex format error - miss user segment")
            if (lineOfReader.startsWith("%%")) return
            else {
                // TODO: parse rules
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