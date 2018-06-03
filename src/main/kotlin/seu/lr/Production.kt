package seu.lr

/**
 * In a production A -> B1 B2 ... Bn
 *
 * @param leftSymbol A
 * @param rightSymbols Bs
 */
class Production(val leftSymbol: Symbol, val rightSymbols: ArrayList<Symbol>) {

    override fun toString(): String {
        return "Production($leftSymbol -> $rightSymbols)"
    }
}