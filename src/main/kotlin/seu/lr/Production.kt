package seu.lr

class Production(leftStr: String, rightStr: String) {
    var left: String = leftStr
    var right: List<String> = rightStr.split(' ')

    override fun toString(): String {
        return "Production($left -> $right)"
    }

}