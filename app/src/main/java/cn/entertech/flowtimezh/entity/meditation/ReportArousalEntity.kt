package cn.entertech.flowtimezh.entity.meditation;
class ReportArousalEntity{
    var arousalAvg:Double? = 0.0
    var arousalRec:ArrayList<Double>? = ArrayList()
    override fun toString(): String {
        return "ReportArousalEnitty(arousalAvg=$arousalAvg, arousalRec=$arousalRec)"
    }

}