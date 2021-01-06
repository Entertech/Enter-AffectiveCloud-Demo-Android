package cn.entertech.affectiveclouddemo.model.meditation;

class ReportAttentionEntity{
    var attentionAvg:Double? = null
    var attentionRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportAttentionEnitty(attentionAvg=$attentionAvg, attentionRec=$attentionRec)"
    }

}