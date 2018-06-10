package seu

import seu.io.CodeFile
import seu.io.YaccFile
import seu.lr.LR

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Please input a file.")
        return
    }
    val yaccFile = YaccFile(args[0])
    val lr = LR(yaccFile.rules.toList(), yaccFile.start)
    val codeFile = CodeFile(yaccFile, lr)
    codeFile.writeFile(args[1])
}
