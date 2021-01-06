package cn.entertech.affectiveclouddemo.model.meditation;

class ReportArousalEntity{
    var arousalAvg:Double? = null
    var arousalRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportArousalEnitty(arousalAvg=$arousalAvg, arousalRec=$arousalRec)"
    }

}