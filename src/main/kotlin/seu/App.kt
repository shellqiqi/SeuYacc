package seu

import seu.io.YaccFile
import seu.lr.LR
import seu.lr.Production

fun main(args: Array<String>) {
    val yaccFile: YaccFile = YaccFile("resource/example.y")
    val termi : HashSet<String> = HashSet()
    val non_termi : HashSet<String> = HashSet()
    yaccFile.rules.forEach{(t,u)->termi.add(t.left)}
    var lr: LR = LR(yaccFile.rules.keys.toList(), termi,non_termi , "" )
}
