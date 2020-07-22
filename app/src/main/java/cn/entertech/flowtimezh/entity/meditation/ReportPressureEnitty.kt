package cn.entertech.flowtimezh.entity.meditation;

class ReportPressureEnitty{
    var pressureAvg:Double? = 0.0
    var pressureRec:ArrayList<Double>? = ArrayList()
    override fun toString(): String {
        return "ReportPressureEnitty(pressureAvg=$pressureAvg, pressureRec=$pressureRec)"
    }

}