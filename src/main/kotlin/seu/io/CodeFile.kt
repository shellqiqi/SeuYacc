package seu.io

import seu.lr.LR

import java.io.BufferedWriter
import java.io.FileWriter

class CodeFile(private val yaccFile: YaccFile, private val lr: LR) {

    private lateinit var writer: BufferedWriter

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
}
