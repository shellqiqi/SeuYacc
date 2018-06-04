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
        if (table[state] == null) throw Exception("Parsing table error - Missing state.")
        table[state]?.set(symbol, value)
    }

    fun initState(state: State) {
        table[state] = HashMap()
    }

    fun hasState(state: State): Boolean {
        return table.containsKey(state)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for ((state, row) in table.entries) {
            stringBuilder.append(state)
            for ((symbol, entry) in row) {
                stringBuilder.append("\n\t$symbol ┆ ${when (entry.label) {
                    Entry.REDUCE -> "reduce"
                    Entry.ACCEPT -> "accept"
                    Entry.SHIFT -> "shift-in"
                    else -> throw Exception("Unknown label in ParsingTable.Entry")
                }} ┆ ${entry.target}")
            }
            stringBuilder.append('\n')
        }
        return "ParsingTable\n$stringBuilder"
    }
}