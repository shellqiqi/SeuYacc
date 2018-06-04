package seu.lr

class State(items: List<Item>) {
    val items = HashSet(items)

    fun getNext(): List<Symbol> {
        val result = HashSet<Symbol>()
        items.forEach { i -> i.next?.let { result.add(it) } }
        return result.toList()
    }

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