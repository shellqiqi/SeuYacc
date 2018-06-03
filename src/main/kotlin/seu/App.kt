package seu

import seu.io.YaccFile
import seu.lr.LR
import seu.lr.Production

fun main(args: Array<String>) {
    val yaccFile: YaccFile = YaccFile("resource/example.y")
    var lr: LR = LR(yaccFile.rules.keys.toList(), yaccFile.terminals, yaccFile.non_terminals, "")
}
