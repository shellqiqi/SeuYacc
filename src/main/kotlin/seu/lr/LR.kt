package seu.lr

class LR(rules: List<Production>,
         val terminals: HashSet<String>, val non_terminals: HashSet<String>,
         var start: String) {
    /* context free grammar stuff */
    val productions: ArrayList<Production> = rules as ArrayList<Production>

    /**
     * transfer to augmented grammar
     */
    init {
        toAugment()
    }

    private fun toAugment(){
        if (!non_terminals.contains(start)) throw Error("Definition Error - Unknown start")
        productions.add(Production("", start))
        start = ""
    }


}