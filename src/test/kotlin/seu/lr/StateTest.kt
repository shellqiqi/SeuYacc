package seu.lr

import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import seu.io.YaccFile

class StateTest {

    companion object {
        private lateinit var lr1: LR
        private lateinit var lr2: LR
        @BeforeClass
        @JvmStatic
        fun constructor() {
            val yaccFile1 = YaccFile("resource/example.y")
            val yaccFile2 = YaccFile("resource/example2.y")
            lr1 = LR(yaccFile1.rules.keys.toList(), yaccFile1.start)
            lr2 = LR(yaccFile2.rules.keys.toList(), yaccFile2.start)
        }
    }

    @Test
    fun equalsTest() {
        val items1 = ArrayList<Item>()
        val items2 = ArrayList<Item>()
        val pro = Production(Symbol.END, ArrayList())
        items1.add(Item(pro, 0, Symbol.END))
        items2.add(Item(pro, 0, Symbol.END))
        val o1 = State(items1)
        val o2 = State(items2)
        assert(o1.equals(o2))
    }

    @Test
    @Ignore
    fun getNextTest() {
        val item1 = Item(lr2.productions[0], 0, Symbol.END)
        val item2 = Item(lr2.productions[1], 0, Symbol.END)
        val state = State(arrayListOf(item1, item2))
        println(state)
        println(state.getNext())
    }

    @Test
    @Ignore
    fun shiftInTest() {
        val item1 = Item(lr1.productions[0], 1, Symbol.END)
        val item2 = Item(lr1.productions[1], 1, Symbol.END)
        val item3 = Item(lr1.productions[2], 0, Symbol.END)
        val state = State(arrayListOf(item1, item2, item3))
        val symbol = state.getNext()[0]
        println(state)
        println(symbol)
        println(state.shiftIn(symbol))
    }
}