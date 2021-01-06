package cn.entertech.affectiveclouddemo.model.meditation;

class ReportHRDataEntity{
    var hrAvg:Double? = null
    var hrMax:Double? = null
    var hrMin:Double? = null
    var hrvAvg:Double? = null
    var hrRec:ArrayList<Double>? = null
    var hrvRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportHRDataEntity(hrAvg=$hrAvg, hrMax=$hrMax, hrMix=$hrMin, hrvAvg=$hrvAvg, hrRec=$hrRec, hrvRec=$hrvRec)"
    }

}