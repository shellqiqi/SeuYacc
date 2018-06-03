package seu.lr

class LrItem(pro: Production, itemNum: Int, forward: List<String>) {
    private val production = pro
    private val posOfDot = itemNum
    private val forwardList = forward

    fun moveDot(): LrItem {
        if(posOfDot >= production.right.size) throw Error("LrItem Error - dot already reach the end")
        return LrItem(production, posOfDot + 1, forwardList)
    }
}