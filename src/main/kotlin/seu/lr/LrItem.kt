package seu.lr

class LrItem(val production: Production, val position: Int, val forwardList: Symbol) {

    fun shiftIn(): LrItem {
        if (!hasNext()) throw Error("LrItem Error - dot already reach the end")
        return LrItem(production, position + 1, forwardList)
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
}