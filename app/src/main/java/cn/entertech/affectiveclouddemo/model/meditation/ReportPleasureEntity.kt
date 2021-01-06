package cn.entertech.affectiveclouddemo.model.meditation;

class ReportPleasureEntity{
    var pleasureAvg:Double? = null
    var pleasureRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportPleasureEnitty(pleasureAvg=$pleasureAvg, pleasureRec=$pleasureRec)"
    }


}