package seu.io

import seu.lr.LR

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

class CodeFile(private val yaccFile: YaccFile, private val lr: LR) {

    private lateinit var writer: BufferedWriter

    @Throws(IOException::class)
    fun writeFile(filePath: String) {
        writer = BufferedWriter(FileWriter(filePath))
        writeHeaders()
        writer.write(CodeFileUtil(lr).generate())
        writeUserSeg()
        writer.close()
    }

    @Throws(IOException::class)
    fun writeHeaders() {
        writer.write(yaccFile.headers.toString())
    }

    @Throws(IOException::class)
    fun writeUserSeg() {
        writer.write(yaccFile.userSeg.toString())
    }

}
