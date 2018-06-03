package seu.lr

import org.junit.Test

import org.junit.Assert.*

class LrStateTest {

    @Test
    fun equalsTest() {
        val items1 = ArrayList<LrItem>()
        val items2 = ArrayList<LrItem>()
        val pro = Production(Symbol.END, ArrayList())
        items1.add(LrItem(pro, 0, Symbol.END))
        items2.add(LrItem(pro, 0, Symbol.END))
        val o1 = LrState(items1)
        val o2 = LrState(items2)
        assert(o1.equals(o2))
    }
}