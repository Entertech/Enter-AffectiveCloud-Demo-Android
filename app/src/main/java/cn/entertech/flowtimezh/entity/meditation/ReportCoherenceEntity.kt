package cn.entertech.flowtimezh.entity.meditation;

class ReportCoherenceEntity{
    var coherenceAvg:Double? = 0.0
    var coherenceDuration:Double? = 0.0
    var coherenceFlag:ArrayList<Double>? = ArrayList()
    var coherenceRec:ArrayList<Double>? = ArrayList()
    override fun toString(): String {
        return "ReportCoherenceEnitty(coherenceAvg=$coherenceAvg, coherenceDuration=$coherenceDuration, coherenceFlag=$coherenceFlag, coherenceRec=$coherenceRec)"
    }


}