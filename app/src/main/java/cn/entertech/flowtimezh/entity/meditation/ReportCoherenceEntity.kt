package cn.entertech.flowtimezh.entity.meditation;

class ReportCoherenceEntity {
    var coherenceAvg: Double? = 0.0
    var coherenceRec: ArrayList<Double>? = ArrayList()
    override fun toString(): String {
        return "ReportCoherenceEntity(coherenceAvg=$coherenceAvg, coherenceRec=$coherenceRec)"
    }
}