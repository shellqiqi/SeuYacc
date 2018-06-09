package seu.lr

import org.junit.Test

import org.junit.BeforeClass
import seu.io.YaccFile
import kotlin.test.assertEquals

class ParsingTableTest {

    companion object {
        private lateinit var state1: State
        private lateinit var state2: State
        private lateinit var state3: State
        private lateinit var production: Production
        private var table = ParsingTable()
        @BeforeClass
        @JvmStatic
        fun constructor() {
            val yaccFile1 = YaccFile("resource/example.y")
            val lr = LR(yaccFile1.rules.toList(), yaccFile1.start)
            val item1 = Item(lr.productions[5], 0, Symbol.END)
            val item2 = Item(lr.productions[4], 1, Symbol.END)
            state1 = lr.closure(arrayListOf(item1))
            state3 = lr.closure(arrayListOf(item1))
            state2 = lr.closure(arrayListOf(item2))
            production = lr.productions[0]
        }
    }

    @Test
    fun getAndSetTest() {
        table.initState(state1)
        table.initState(state2)
        table[state1, Symbol(Symbol.NON_TERMINAL, "E")] =
                ParsingTable.Entry(ParsingTable.Entry.SHIFT, state2)
        table[state1, Symbol(Symbol.TERMINAL, "F")] =
                ParsingTable.Entry(ParsingTable.Entry.ACCEPT, null)
        table[state2, Symbol(Symbol.NON_TERMINAL, "G")] =
                ParsingTable.Entry(ParsingTable.Entry.REDUCE, production)
        assertEquals(
                ParsingTable.Entry(ParsingTable.Entry.SHIFT, state2),
                table[state1, Symbol(Symbol.NON_TERMINAL, "E")])
        assertEquals(
                ParsingTable.Entry(ParsingTable.Entry.ACCEPT, null),
                table[state1, Symbol(Symbol.TERMINAL, "F")])
        assertEquals(
                ParsingTable.Entry(ParsingTable.Entry.REDUCE, production),
                table[state2, Symbol(Symbol.NON_TERMINAL, "G")])
        assertEquals(
                null,
                table[state1, Symbol(Symbol.TERMINAL, "E")])
    }

    @Test
    fun hasStateTest() {
        table.initState(state1)
        assert(table.hasState(state3))
    }
}