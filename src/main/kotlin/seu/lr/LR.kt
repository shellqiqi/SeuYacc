package seu.lr

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

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

    private val firstOfNonTerminal = HashMap<Symbol, List<Symbol>>()

    init {
        toAugment(start)
        productions.forEach { p -> firstOfNonTerminal[p.leftSymbol] = firstInit(p.leftSymbol) }
        fillParsingTable()
    }

    /**
     * Transfer to augmented grammar.
     *
     * @param start start symbol.
     */
    private fun toAugment(start: Symbol) {
        startProduction = Production(Symbol.START, arrayListOf(start), null)
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
        val itemHashSet = HashSet(itemList)
        val itemArrayList = ArrayList(itemList)
        var index = 0
        while (true) {
            if (itemArrayList.size <= index) break
            val item = itemArrayList[index++]
            val next = item.next ?: continue
            if (next.isTerminal()) continue
            for (production in productions) {
                if (production.leftSymbol == next)
                    for (forwardSymbol in first(item.nextNext, item.forward)) {
                        val newItem = Item(production, 0, forwardSymbol)
                        if (!itemHashSet.contains(newItem)) {
                            itemHashSet.add(newItem)
                            itemArrayList.add(newItem)
                        }
                    }
            }
        }
        return State(itemArrayList)
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
        else firstOfNonTerminal.getValue(symbol)
    }

    /**
     * Calculate first of non-terminal symbols that the input symbol might meet during deduction
     * For example,
     * given Symbol S and productions
     * S -> CC | d
     * C -> c
     * we get a set {c,d}
     *
     * @param next , input symbol.
     * @return First(β a), a list of Symbol.
     */
    private fun firstInit(next: Symbol): List<Symbol> {
        val result: HashSet<Symbol> = HashSet()
        if (next.isTerminal()) result.add(next)
        else {
            productions.forEach { production ->
                if (production.leftSymbol == next)
                    if (production.rightSymbols[0] != next)
                        result.addAll(firstInit(production.rightSymbols[0]))
            }
        }
        return result.toList()
    }

    /**
     * Generate a complete parsing table.
     */
    private fun fillParsingTable() {
        val startState = closure(arrayListOf(Item(startProduction, 0, Symbol.END)))
        parsingTable.initState(startState)

        val symbolStack = Stack<Symbol>()
        symbolStack.addAll(startState.getNext())

        val threads = ArrayList<Thread>()
        while (symbolStack.isNotEmpty()) {
            val thread = Thread(Fill(this, startState, symbolStack.pop()))
            threads.add(thread)
            thread.start()
        }
        for (t in threads) t.join()
    }

    /**
     * Recursive function and multi-thread
     *
     * @param lr a lr(1) grammar analyzing table
     * @param parent parent state.
     * @param symbol edge from parent state to the next state
     */
    class Fill(private val lr: LR, private val parent: State, private val symbol: Symbol) : Runnable {
        override fun run() {
            val newState = lr.closure(parent.shiftIn(symbol))
            val symbolStack = Stack<Symbol>()

            synchronized(lr.parsingTable, {
                if (!lr.parsingTable.hasState(newState)) {
                    lr.parsingTable.initState(newState)
                    symbolStack.addAll(newState.getNext())
                }
                lr.parsingTable[parent, symbol] = ParsingTable.Entry(
                        if (symbol.isTerminal()) ParsingTable.Entry.SHIFT
                        else ParsingTable.Entry.GOTO, newState)

                newState.getReducible().forEach { item ->
                    lr.parsingTable[newState, item.forward] =
                            if (item.production.leftSymbol == Symbol.START)
                                ParsingTable.Entry(ParsingTable.Entry.ACCEPT, null)
                            else
                                ParsingTable.Entry(ParsingTable.Entry.REDUCE, item.production)
                }
            })

            val threads = ArrayList<Thread>()
            while (symbolStack.isNotEmpty()) {
                val thread = Thread(Fill(lr, newState, symbolStack.pop()))
                threads.add(thread)
                thread.start()
            }
            for (t in threads) t.join()
        }
    }
}