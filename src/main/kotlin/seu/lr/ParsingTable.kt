package seu.lr

class ParsingTable {

    val table = HashMap<State, HashMap<Symbol, Entry>>()

    class Entry(val label: Int, val target: Any?) {
        companion object {
            const val SHIFT = 0
            const val REDUCE = 1
            const val ACCEPT = 2
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Entry

            if (label != other.label) return false
            if (target != other.target) return false

            return true
        }

        override fun hashCode(): Int {
            var result = label
            result = 31 * result + (target?.hashCode() ?: 0)
            return result
        }
    }

    operator fun get(state: State, symbol: Symbol): Entry? {
        return table[state]?.get(symbol)
    }

    operator fun set(state: State, symbol: Symbol, value: Entry) {
        if (table[state] == null) table[state] = HashMap()
        table[state]?.set(symbol, value)
    }
}