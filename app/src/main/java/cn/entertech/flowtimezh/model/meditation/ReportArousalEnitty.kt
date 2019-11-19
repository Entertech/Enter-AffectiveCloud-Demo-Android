package cn.entertech.flowtime.mvp.model.meditation

class ReportArousalEnitty{
    var arousalAvg:Double? = null
    var arousalRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportArousalEnitty(arousalAvg=$arousalAvg, arousalRec=$arousalRec)"
    }

}