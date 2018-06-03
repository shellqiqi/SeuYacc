package seu.lr

class LR(rules: List<Production>,
                  terminals: HashSet<String>, non_terminals: HashSet<String>,
                  startWith: String) {
    val CFG: List<Production> = rules
    val termi: HashSet<String> = terminals
    val non_termi: HashSet<String> = non_terminals
    val start: String = startWith

    init {
        if (!non_termi.contains(start)) throw Error("Definition Error - Unknown start")

    }
}