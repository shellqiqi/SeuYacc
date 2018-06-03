package seu.lr

class LrItem(val production: Production, val position: Int, val forward: Symbol) {

    fun shiftIn(): LrItem {
        if (!hasNext()) throw Error("LrItem Error - dot already reach the end")
        return LrItem(production, position + 1, forward)
    }

    fun getFirst(): Symbol {
        return production.rightSymbols[0]
    }

    fun getNext(): Symbol? {
        return if (hasNext()) production.rightSymbols[position] else null
    }

    fun getNextNext(): Symbol?{
        return if (hasNextNext()) production.rightSymbols[position + 1] else null
    }

    fun hasNext(): Boolean {
        return position < production.rightSymbols.size
    }

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
}