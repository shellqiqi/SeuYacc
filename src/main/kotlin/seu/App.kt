package seu

import seu.io.YaccFile
import seu.lr.LR

fun main(args: Array<String>) {
    val yaccFile = YaccFile("resource/example.y")
//    var lr = LR(yaccFile.rules.keys.toList(), yaccFile.terminals, yaccFile.nonTerminals, "")
}
