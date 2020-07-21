package cn.entertech.flowtimezh.mvp.model.meditation

class ReportCoherenceEnitty{
    var coherenceAvg:Double? = null
    var coherenceRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportCoherenceEnitty(coherenceAvg=$coherenceAvg, coherenceRec=$coherenceRec)"
    }
}