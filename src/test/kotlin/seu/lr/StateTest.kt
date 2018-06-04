package seu.lr

import org.junit.Test

class StateTest {

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
}