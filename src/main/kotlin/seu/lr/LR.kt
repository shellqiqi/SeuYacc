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

    fun closure(itemList: List<Item>): State {
        val items = ArrayList(itemList)
        var index = 0
        while (true) {
            if (items.size <= index)
                break
            val item = items[index++]
            val next = item.getNext()
            if (next?.isTerminal() != false) continue
            for (production in productions) {
                if (production.leftSymbol == next)
                    for (forwardSymbol in first(item.getNextNext(), item.forward)) {
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