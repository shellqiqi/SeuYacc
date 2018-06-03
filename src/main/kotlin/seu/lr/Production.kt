package seu.lr

class Production(leftStr: String, rightStr: String):Comparable<Production>{
    /* refer to A in a production A -> B, A is a non-terminal symbol */
    var left: String = leftStr
    /* refer to Bs in a production A ->B1B2...Bn, B can be either a terminal or a non-terminal symbol*/
    var right: List<String> = rightStr.split(' ')

    override fun toString(): String {
        return "Production($left -> $right)"
    }

    override fun compareTo(other: Production): Int {
        return left.compareTo(other.left)
    }
}