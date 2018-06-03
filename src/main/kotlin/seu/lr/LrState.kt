package seu.lr

class LrState(lrItems: HashSet<LrItem>) {
    val lrItems = lrItems.clone()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        return true
    }
}