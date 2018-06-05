package seu.lr

/**
 * Construct a symbol with label and its name.
 * Label describes whether the symbol is a terminal symbol or a non-terminal symbol.
 * In addition, we define END and START for convenient.
 *
 * @param label terminal or non-terminal.
 * @param name what it calls.
 */
class Symbol(val label: Int, val name: String) {
    companion object {
        const val TERMINAL = 0
        const val NON_TERMINAL = 1
        val END = Symbol(TERMINAL, "$$")
        val START = Symbol(NON_TERMINAL, "##")
    }

    fun isTerminal(): Boolean {
        return label == TERMINAL
    }

    fun isNonTerminal(): Boolean {
        return label == NON_TERMINAL
    }

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Symbol

        if (label != other.label) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label
        result = 31 * result + name.hashCode()
        return result
    }
}