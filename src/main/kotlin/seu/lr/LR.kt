package seu.lr

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet

/**
 * Construct LR and its parsing table by given rules.
 *
 * @param rules rules defined in yacc file.
 * @param start start symbol.
 */
class LR(rules: List<Production>, start: Symbol) {
    /* Productions that yacc file defines */
    val productions: ArrayList<Production> = rules as ArrayList<Production>
    /* Parsing table */
    val parsingTable = ParsingTable()
    /* The start production */
    lateinit var startProduction: Production

    val firstOfNonterminal = HashMap<Symbol, List<Symbol>>()

    init {
        toAugment(start)
        productions.forEach { p -> firstOfNonterminal[p.leftSymbol] = firstInit(p.leftSymbol) }
        fillParsingTable()
    }

    /**
     * Transfer to augmented grammar.
     *
     * @param start start symbol.
     */
    private fun toAugment(start: Symbol) {
        startProduction = Production(Symbol.START, arrayListOf(start))
        productions.add(startProduction)
    }

    /**
     * Calculate the closure of each item in Items.
     * For example,
     * given item A -> α·B β, a (B is a non-terminal symbol)
     * add B -> ·stuff , First(β a) to closure.
     * Repeat the operation for each item, include those you add in the process
     * P.S. First(β a) refer to the symbol you might meet after you reduce current item
     *
     * @param itemList a list of items to be calculate
     * @return a State with items contain itemList and its closure
     */
    fun closure(itemList: List<Item>): State {
        val items = LinkedHashSet(itemList)
        var index = 0
        while (true) {
            if (items.size <= index) break
            val item = items.elementAt(index++)
            val next = item.next ?: continue
            if (next.isTerminal()) continue
            for (production in productions) {
                if (production.leftSymbol == next)
                    for (forwardSymbol in first(item.nextNext, item.forward)) {
                        val newItem = Item(production, 0, forwardSymbol)
                        if (!items.contains(newItem))
                            items.add(newItem)
                    }
            }
        }
        return State(items.toList())
    }

    /**
     * Get First(β a) in A -> α·B β, a.
     *
     * @param next β, following symbol.
     * @param nextNext a, terminal symbol.
     * @return First(β a), a list of Symbol.
     */
    private fun first(next: Symbol?, nextNext: Symbol): List<Symbol> {
        val symbol = next ?: nextNext
        return if (symbol.isTerminal()) arrayListOf(symbol)
        else firstOfNonterminal.getValue(symbol)
    }

    /**
     * Get First(β a) in A -> α·B β, a.
     *
     * @param next β, following symbol.
     * @param nextNext a, terminal symbol.
     * @return First(β a), a list of Symbol.
     */
    private fun firstInit(next: Symbol): List<Symbol> {
        val result: HashSet<Symbol> = HashSet()
        if (next.isTerminal()) result.add(next)
        else {
            productions.forEach { production ->
                if (production.leftSymbol == next)
                    if(production.rightSymbols[0] != next)
                        result.addAll(firstInit(production.rightSymbols[0]))
            }
        }
        return result.toList()
    }

    /**
     * Generate a complete parsing table.
     */
    fun fillParsingTable() {
        val startState = closure(arrayListOf(Item(startProduction, 0, Symbol.END)))
        parsingTable.initState(startState)

        val symbolStack = Stack<Symbol>()
        symbolStack.addAll(startState.getNext())

        while (symbolStack.isNotEmpty()) fillParsingTable(startState, symbolStack.pop())
    }

    /**
     * Recursive function.
     *
     * @param parent parent state.
     * @param symbol edge from parent state to the next state
     */
    private fun fillParsingTable(parent: State, symbol: Symbol) {
        val newState = closure(parent.shiftIn(symbol))
        val symbolStack = Stack<Symbol>()
        if (!parsingTable.hasState(newState)) {
            parsingTable.initState(newState)
            symbolStack.addAll(newState.getNext())
        }
        parsingTable[parent, symbol] = ParsingTable.Entry(ParsingTable.Entry.SHIFT, newState)

        newState.getReducible().forEach { item ->
            parsingTable[newState, item.forward] =
                    ParsingTable.Entry(
                            if (item.production.leftSymbol == Symbol.START)
                                ParsingTable.Entry.ACCEPT
                            else
                                ParsingTable.Entry.REDUCE, item.production)
        }

        while (symbolStack.isNotEmpty()) fillParsingTable(newState, symbolStack.pop())
    }
}