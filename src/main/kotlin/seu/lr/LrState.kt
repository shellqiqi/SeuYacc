package seu.lr

class LrState(pros: ArrayList<Production>, start:Int = 0) {
    private var lrItems : ArrayList<LrItem> = arrayListOf()

    init {
        lrItems.add(LrItem(pros[start], 0, arrayListOf()))

    }
}