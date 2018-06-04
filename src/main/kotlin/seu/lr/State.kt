package seu.lr

/**
 * A state of LR consist of items, each item cannot be duplicated with each other.
 *
 * @param items initial items, if duplicate elements contained, they will be deleted during initializing
 */
class State(items: List<Item>) {
    val items = HashSet(items)

    /**
     * Get next of each item if it has
     *
     * @return a list of symbol
     */
    fun getNext(): List<Symbol> {
        val result = HashSet<Symbol>()
        items.forEach { i -> i.next?.let { result.add(it) } }
        return result.toList()
    }

    /**
     * Make each of item shift in if its next matches the input symbol
     *
     * @param symbol the symbol to be matched
     * @return a list of items after shifting in
     */
    fun shiftIn(symbol: Symbol): List<Item> {
        val result = arrayListOf<Item>()
        items.forEach { i -> if (i.next == symbol) result.add(i.shiftIn()) }
        return result
    }

    fun equals(other: State): Boolean {
        if (this === other) return true
        return items == other.items
    }

    override fun toString(): String {
        return "State(items=${items.toList()})"
    }
}