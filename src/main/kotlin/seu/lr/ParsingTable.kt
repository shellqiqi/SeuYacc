package seu.lr

class ParsingTable {
    /* ┌─────┬────────────────────┬┄ *
     * │     │       Symbol       │  *
     * ├─────┼────────────────────┼┄ *
     * │State│Entry(label, target)│  *
     * ├─────┼────────────────────┼┄ */
    val table = HashMap<State, HashMap<Symbol, Entry>>()

    /**
     * Entry of parsing table. It stores an action and its target.
     * Actions can be shift-in, reduce and accept. Targets can be a state, a production or nothing like null.
     * Shift-in and move to state.
     * Reduce according to the production.
     * Accept with no target.
     *
     * @param label action should be taken
     * @param target a state, a production or null. It depends on the label.
     */
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

    /**
     * Get the entry of the parsing table.
     *
     * @param state state.
     * @param symbol symbol.
     * @return entry if exist, else null.
     */
    operator fun get(state: State, symbol: Symbol): Entry? {
        return table[state]?.get(symbol)
    }

    /**
     * Set the entry if the state exists in the parsing table, else throws an exception.
     *
     * @param state state.
     * @param symbol symbol.
     * @param value entry.
     * @throws Exception missing state in parsing table.
     */
    operator fun set(state: State, symbol: Symbol, value: Entry) {
        if (table[state] == null) throw Exception("Parsing table error - Missing state.")
        table[state]?.set(symbol, value)
    }

    /**
     * Init a state. If state exists, clear it.
     *
     * @param state state to init.
     */
    fun initState(state: State) {
        table[state] = HashMap()
    }

    /**
     * Whether the state exists in the parsing table.
     *
     * @param state state to test.
     * @return
     */
    fun hasState(state: State): Boolean {
        return table.containsKey(state)
    }

    fun hasStateAndCombine(state: State): Boolean {
        val stateSimple = state.toSimple()
        for ((s, v) in table){
            if(s.toSimple() == stateSimple){
                if(s == state)
                    return true
                table.remove(state)
                Item.finishIgnore() //TODO: miss method
                state.items.addAll(s.items)
                s.items.addAll(state.items)
                table[state] = v
                return true
            }
        }
        return false
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