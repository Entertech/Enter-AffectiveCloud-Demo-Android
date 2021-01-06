package cn.entertech.affectiveclouddemo.model.meditation;

class ReportPressureEntity{
    var pressureAvg:Double? = null
    var pressureRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportPressureEnitty(pressureAvg=$pressureAvg, pressureRec=$pressureRec)"
    }

}