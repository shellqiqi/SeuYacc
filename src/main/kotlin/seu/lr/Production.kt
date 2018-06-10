package seu.lr

/**
 * In a production A -> B1 B2 ... Bn, with its action.
 *
 * @param leftSymbol A
 * @param rightSymbols Bs
 * @param action things to do when the production is reduced
 */
class Production(val leftSymbol: Symbol, val rightSymbols: ArrayList<Symbol>, val action: String?) {

    override fun toString(): String {
        return "$leftSymbol -> $rightSymbols".replace("[", "")
                .replace("]", "")
                .replace(",", "")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Production

        if (leftSymbol != other.leftSymbol) return false
        if (rightSymbols != other.rightSymbols) return false

        return true
    }

    override fun hashCode(): Int {
        var result = leftSymbol.hashCode()
        result = 31 * result + rightSymbols.hashCode()
        return result
    }
}