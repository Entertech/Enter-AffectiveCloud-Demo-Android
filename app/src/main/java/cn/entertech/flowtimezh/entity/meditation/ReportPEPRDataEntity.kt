package cn.entertech.flowtimezh.entity.meditation;

class ReportPEPRDataEntity{
    var hrAvg:Double? = 0.0
    var hrMax:Double? = 0.0
    var hrMin:Double? = 0.0
    var hrvAvg:Double? = 0.0
    var rrAvg:Double? = 0.0
    var rrRec:ArrayList<Double>? = ArrayList()
    var hrRec:ArrayList<Double>? = ArrayList()
    var hrvRec:ArrayList<Double>? = ArrayList()
    override fun toString(): String {
        return "ReportPEPRDataEntity(hrAvg=$hrAvg, hrMax=$hrMax, hrMin=$hrMin, hrvAvg=$hrvAvg, rrAvg=$rrAvg, rrRec=$rrRec, hrRec=$hrRec, hrvRec=$hrvRec)"
    }
}