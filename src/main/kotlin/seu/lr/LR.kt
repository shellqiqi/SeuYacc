package seu.lr

class LR(rules: List<Production>, var start: String) {
    /* context free grammar stuff */
    val productions: ArrayList<Production> = rules as ArrayList<Production>

    init {
        toAugment()
    }

    /**
     * transfer to augmented grammar
     */
    private fun toAugment() {

    }

    fun closure(lrItems: List<LrItem>): LrState {
        val items = ArrayList(lrItems)
        var iterator = 0
        while (true) {
            if (items.size <= iterator)
                break
            val item = items[iterator++]
            val next = item.getNext()
            if (next?.isTerminal() != false) continue
            for (production in productions) {
                if (production.leftSymbol == next)
                    for (forwardSymbol in first(item.getNextNext(), item.forward)){
                        val newItem = LrItem(production, 0, forwardSymbol)
                        if(!items.contains(newItem))
                            items.add(newItem)
                    }
            }
        }
        return LrState(items)
    }

    private fun first(next: Symbol?, nextNext: Symbol): List<Symbol> {
        var result: ArrayList<Symbol> = arrayListOf()
        val symbol = next ?: nextNext
        if (symbol.isTerminal()) result.add(symbol)
        else {
            productions.forEach { production ->
                if (production.leftSymbol == next)
                    result.addAll(first(null, production.rightSymbols[0]))
            }
        }
        return result
    }

}