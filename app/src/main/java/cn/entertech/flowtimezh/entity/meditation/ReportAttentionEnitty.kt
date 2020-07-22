package cn.entertech.flowtimezh.entity.meditation;

class ReportAttentionEnitty{
    var attentionAvg:Double? = 0.0
    var attentionRec:ArrayList<Double>? = ArrayList()
    override fun toString(): String {
        return "ReportAttentionEnitty(attentionAvg=$attentionAvg, attentionRec=$attentionRec)"
    }

}