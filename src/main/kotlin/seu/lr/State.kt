package seu.lr

class State(items: List<Item>) {
    val items = HashSet(items)

    fun equals(other: State): Boolean {
        if (this === other) return true
        return items == other.items
    }

    override fun toString(): String {
        return "State(items=${items.toList()})"
    }
}