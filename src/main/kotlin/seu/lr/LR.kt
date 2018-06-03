package seu.lr

class LR(rules: List<Production>,
         val terminals: HashSet<String>, val non_terminals: HashSet<String>,
         var start: String) {
    val productions: ArrayList<Production> = rules as ArrayList<Production>

    /**
     * transfer to augmented grammar
     */
    init {
        if (!non_terminals.contains(start)) throw Error("Definition Error - Unknown start")
//        productions.add(Production("", start))
        start = ""
    }


}