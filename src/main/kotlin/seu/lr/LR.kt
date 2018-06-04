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

    /**
     * Calculate the closure of each item in Items.
     * For example,
     * given item A -> α.Bβ, a (B is a non-terminal symbol)
     * add B -> .stuff , First(βa) to closure.
     * Repeat the operation for each item, include those you add in the process
     * P.S. First(βa) refer to the symbol you might meet after you reduce current item
     *
     * @param itemList a list of items to be calculate
     * @return a State with items contain itemList and its closure
     */
    fun closure(itemList: List<Item>): State {
        val items = ArrayList(itemList)
        var index = 0
        while (true) {
            if (items.size <= index)
                break
            val item = items[index++]
            val next = item.next
            if (next?.isTerminal() != false) continue
            for (production in productions) {
                if (production.leftSymbol == next)
                    for (forwardSymbol in first(item.nextNext, item.forward)) {
                        val newItem = Item(production, 0, forwardSymbol)
                        if (!items.contains(newItem))
                            items.add(newItem)
                    }
            }
        }
        return State(items)
    }

    private fun first(next: Symbol?, nextNext: Symbol): List<Symbol> {
        val result: ArrayList<Symbol> = arrayListOf()
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