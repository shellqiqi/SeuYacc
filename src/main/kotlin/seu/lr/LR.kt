package seu.lr

import java.util.*

class LR(rules: List<Production>, start: String) {
    /* context free grammar stuff */
    val productions: ArrayList<Production> = rules as ArrayList<Production>
    val parsingTable = ParsingTable()
    lateinit var startProduction: Production

    init {
        toAugment(start)
        fillParsingTable()
    }

    /**
     * transfer to augmented grammar
     */
    private fun toAugment(start: String) {
        startProduction = Production(Symbol.START, arrayListOf(Symbol(Symbol.NON_TERMINAL, start)))
        productions.add(startProduction)
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

    fun fillParsingTable() {
        val startState = closure(arrayListOf(Item(startProduction, 0, Symbol.END)))
        parsingTable.initState(startState)

        val symbolStack = Stack<Symbol>()
        symbolStack.addAll(startState.getNext())

        while (!symbolStack.empty()) {
            if (symbolStack.peek() == Symbol.START) {
                symbolStack.pop()
                val acceptState = closure(startState.shiftIn(Symbol.START))
                parsingTable.initState(acceptState)
                parsingTable[acceptState, Symbol.END] = ParsingTable.Entry(ParsingTable.Entry.ACCEPT, null)
            } else
                fillParsingTable(startState, symbolStack.pop())
        }
    }

    fun fillParsingTable(parent: State, symbol: Symbol) {
        val newState = closure(parent.shiftIn(symbol))
        val symbolStack = Stack<Symbol>()
        if (!parsingTable.hasState(newState)) {
            parsingTable.initState(newState)
            symbolStack.addAll(newState.getNext())
        }
        parsingTable[parent, symbol] = ParsingTable.Entry(ParsingTable.Entry.SHIFT, newState)

        val reducibleItems = newState.getReducible()
        reducibleItems.forEach { item ->
            parsingTable[newState, item.forward] = ParsingTable.Entry(ParsingTable.Entry.REDUCE, item.production)
        }

        while (!symbolStack.empty()) {
            fillParsingTable(newState, symbolStack.pop())
        }
    }
}