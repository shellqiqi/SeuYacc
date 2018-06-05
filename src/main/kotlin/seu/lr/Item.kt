package seu.lr

/**
 * Item of an State
 *
 * @param production a production like S -> CC, further information can be found in class Production
 * @param position the position of dot, from 0 to size of production's rightSymbols
 * @param forward next symbol we might meet after the present lrItem reduced
 */
class Item(val production: Production, val position: Int, val forward: Symbol) {

    val next = getNextSymbol()
    val nextNext = getNextNextSymbol()

    /**
     * Create a new item with its dot move forward, or Null if the dot reaches the end
     * for example
     * given S -> .CC , we get S -> C.C
     * given S -> CC. , we get Null
     *
     * @return an item with its dot move forward
     */
    fun shiftIn(): Item {
        if (!hasNext()) throw Error("Item Error - dot already reach the end")
        return Item(production, position + 1, forward)
    }

    /**
     * @return the first Symbol of production's rightSymbols.
     */
    fun getFirst(): Symbol {
        return production.rightSymbols[0]
    }

    /**
     * @return the next Symbol of production's rightSymbols or Null if it doesn't have next Symbol.
     * for example
     * given S -> A.BC , we get a B;
     * given S -> ABC. , we get Null.
     */
    fun getNextSymbol(): Symbol? {
        return if (hasNext()) production.rightSymbols[position] else null
    }

    /**
     * If you have understood the meaning of last function, you should know this;
     * If not, go back to see.
     */
    fun getNextNextSymbol(): Symbol? {
        return if (hasNextNext()) production.rightSymbols[position + 1] else null
    }

    /**
     * @return whether there are symbols left after the dot.
     */
    private fun hasNext(): Boolean {
        return position < production.rightSymbols.size
    }

    /**
     * @return whether there are more than one symbols after the dot.
     */
    private fun hasNextNext(): Boolean {
        return position + 1 < production.rightSymbols.size
    }

    /**
     * @return whether the dot reach the end.
     */
    fun reachEnd(): Boolean {
        return position == production.rightSymbols.size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (production != other.production) return false
        if (position != other.position) return false
        if (forward != other.forward) return false

        return true
    }

    override fun hashCode(): Int {
        var result = production.hashCode()
        result = 31 * result + position
        result = 31 * result + forward.hashCode()
        return result
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("${production.leftSymbol} →")
        for (index in 0 until position)
            stringBuilder.append(" ${production.rightSymbols[index]}")
        stringBuilder.append(" ·")
        for (index in position..production.rightSymbols.lastIndex)
            stringBuilder.append(" ${production.rightSymbols[index]}")
        return "Item($stringBuilder‖$forward)"
    }
}