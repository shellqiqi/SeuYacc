package seu.lr

/**
 * Item of an LrState
 *
 * @param production a production like S -> CC, further information can be found in class Production
 * @param position the position of dot, from 0 to size of production's rightSymbols
 * @param forward next symbol we might meet after the present lrItem reduced
 */
class LrItem(val production: Production, val position: Int, val forward: Symbol) {

    /**
     * Create a new item with its dot move forward, or Null if the dot reaches the end
     * for example
     * given S -> .CC , we get S -> C.C
     * given S -> CC. , we get Null
     *
     * @return an item with its dot move forward
     */
    fun shiftIn(): LrItem {
        if (!hasNext()) throw Error("LrItem Error - dot already reach the end")
        return LrItem(production, position + 1, forward)
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
    fun getNext(): Symbol? {
        return if (hasNext()) production.rightSymbols[position] else null
    }

    /**
     * If you have understood the meaning of last function, you should know this;
     * If not, go back to see.
     */
    fun getNextNext(): Symbol? {
        return if (hasNextNext()) production.rightSymbols[position + 1] else null
    }

    /**
     * @return whether there are symbols left after the dot.
     */
    fun hasNext(): Boolean {
        return position < production.rightSymbols.size
    }

    /**
     * @return whether there are more than one symbols after the dot.
     */
    fun hasNextNext(): Boolean {
        return position + 1 < production.rightSymbols.size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LrItem

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
        var right1 = ""
        for (i in 0 until position)
            right1 += production.rightSymbols[i].name + " "
        var right2 = ""
        for (i in position until production.rightSymbols.size)
            right2 += production.rightSymbols[i].name + " "
        return "LrItem(${production.leftSymbol} -> $right1. $right2\t$forward)"
    }
}