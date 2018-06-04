package seu.lr

import org.junit.Test

import org.junit.BeforeClass
import org.junit.Ignore
import seu.io.YaccFile

class LRTest {


    companion object {
        private lateinit var lr1: LR
        private lateinit var lr2: LR
        private lateinit var lr3: LR
        @BeforeClass
        @JvmStatic
        fun constructor() {
            val yaccFile1 = YaccFile("resource/example.y")
            val yaccFile2 = YaccFile("resource/example2.y")
            val yaccFile3 = YaccFile("resource/example3.y")

//            lr1 = LR(yaccFile1.rules.keys.toList(), "line")
//            lr2 = LR(yaccFile2.rules.keys.toList(), "E")
            lr3 = LR(yaccFile3.rules.keys.toList(), "S")

        }
    }

    @Test
    @Ignore
    fun closureTest1() {
        val item = Item(lr1.productions[5], 0, Symbol.END)
        println(item.toString() + '\n')
        lr1.closure(arrayListOf(item)).items.forEach { i -> println(i) }
    }

    @Test
    @Ignore
    fun closureTest2() {
        val item1 = Item(lr2.productions[0], 0, Symbol.END)
        println(item1.toString())
        val item2 = Item(lr2.productions[1], 0, Symbol.END)
        println(item2.toString() + '\n')
        lr2.closure(arrayListOf(item1, item2)).items.forEach { i -> println(i) }
    }

    @Test
    fun test() {
        lr3.parsingTable.table.forEach { (t, u) -> println(t) }
    }

}