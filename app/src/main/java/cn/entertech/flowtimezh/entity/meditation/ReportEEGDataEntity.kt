package cn.entertech.flowtimezh.entity.meditation;

class ReportEEGDataEntity{
    var alphaCurve:ArrayList<Double>? = ArrayList()
    var betaCurve:ArrayList<Double>? = ArrayList()
    var thetaCurve:ArrayList<Double>? = ArrayList()
    var deltaCurve:ArrayList<Double>? = ArrayList()
    var gammaCurve:ArrayList<Double>? = ArrayList()

    override fun toString(): String {
        return "ReportEEGDataEntity(alphaCurve=$alphaCurve, betaCurve=$betaCurve, thetaCurve=$thetaCurve, deltaCurve=$deltaCurve, gammaCurve=$gammaCurve)"
    }


}