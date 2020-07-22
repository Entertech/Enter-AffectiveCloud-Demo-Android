package cn.entertech.flowtimezh.entity.meditation;

class ReportHRDataEntity{
    var hrAvg:Double? = 0.0
    var hrMax:Double? = 0.0
    var hrMin:Double? = 0.0
    var hrvAvg:Double? = 0.0
    var hrRec:ArrayList<Double>? = ArrayList()
    var hrvRec:ArrayList<Double>? = ArrayList()
    override fun toString(): String {
        return "ReportHRDataEntity(hrAvg=$hrAvg, hrMax=$hrMax, hrMix=$hrMin, hrvAvg=$hrvAvg, hrRec=$hrRec, hrvRec=$hrvRec)"
    }

}