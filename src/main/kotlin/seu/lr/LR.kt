package seu.lr

class LR(rules: List<Production>,
         val terminals: HashSet<String>, val non_terminals: HashSet<String>,
         var start: String) {
    /* context free grammar stuff */
    val productions: ArrayList<Production> = rules as ArrayList<Production>

    init {
        toAugment()
    }

    /**
     * transfer to augmented grammar
     */
    private fun toAugment(){

    }


}