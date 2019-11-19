package cn.entertech.flowtime.mvp.model.meditation

class ReportPressureEnitty{
    var pressureAvg:Double? = null
    var pressureRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportPressureEnitty(pressureAvg=$pressureAvg, pressureRec=$pressureRec)"
    }

}