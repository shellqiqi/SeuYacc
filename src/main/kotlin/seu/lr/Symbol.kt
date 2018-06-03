package seu.lr

class Symbol(val label :Int, val name :String) {
    companion object {
        const val TERMINAL = 0
        const val NON_TERMINAL = 1
    }

    fun isTerminal(): Boolean {
        return label == TERMINAL
    }

    fun isNonTermianl(): Boolean {
        return label == NON_TERMINAL
    }
}