package cn.entertech.flowtime.mvp.model.meditation

class ReportRelaxationEnitty{
    var relaxationAvg:Double? = null
    var relaxationRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportRelaxationEnitty(relaxationAvg=$relaxationAvg, relaxationRec=$relaxationRec)"
    }

}