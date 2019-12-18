package cn.entertech.flowtime.mvp.model.meditation

class ReportAttentionEnitty{
    var attentionAvg:Double? = null
    var attentionRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportAttentionEnitty(attentionAvg=$attentionAvg, attentionRec=$attentionRec)"
    }

}