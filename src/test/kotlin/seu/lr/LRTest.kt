package seu.lr

import org.junit.Test

import org.junit.BeforeClass
import seu.io.YaccFile

class LRTest {


    companion object {
        private lateinit var lr1: LR
        private lateinit var lr2: LR
        @BeforeClass
        @JvmStatic fun constructor() {
            val yaccFile = YaccFile("resource/example.y")
            val yaccFile2 = YaccFile("resource/example2.y")
            lr1 = LR(yaccFile.rules.keys.toList(),"")
            lr2 = LR(yaccFile2.rules.keys.toList(),"")
        }
    }

    @Test
    fun closureTest() {
        /*val item = LrItem(lr1.productions[5],0, Symbol.END)
        println(item.toString() + '\n')
        lr1.closure(arrayListOf(item)).lrItems.forEach { i -> println(i) }*/

        val item2 = LrItem(lr2.productions[0],0, Symbol.END)
        println(item2.toString() + '\n')
        lr2.closure(arrayListOf(item2)).lrItems.forEach { i -> println(i) }
    }
}