package cn.entertech.affectiveclouddemo.model.meditation;

class ReportRelaxationEntity{
    var relaxationAvg:Double? = null
    var relaxationRec:ArrayList<Double>? = null
    override fun toString(): String {
        return "ReportRelaxationEnitty(relaxationAvg=$relaxationAvg, relaxationRec=$relaxationRec)"
    }

}