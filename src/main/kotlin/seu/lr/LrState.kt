package seu.lr

class LrState(lrItems: List<LrItem>) {
    val lrItems = HashSet(lrItems)

    fun equals(other: LrState): Boolean {
        if (this === other) return true
        return lrItems == other.lrItems
    }

    override fun toString(): String {
        return "LrState(lrItems=${lrItems.toList()})"
    }


}