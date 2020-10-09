package cn.entertech.flowtimezh.entity.meditation;
class ReportSleepEnitty{
    var sleepPoint:Double? = 0.0
    var sleepLatency:Double? = 0.0
    var soberDuration:Double? = 0.0
    var lightDuration:Double? = 0.0
    var deepDuration:Double? = 0.0
    var sleepCurve:ArrayList<Double>? = ArrayList()
    override fun toString(): String {
        return "ReportSleepEnitty(sleepPoint=$sleepPoint, sleepLatency=$sleepLatency, soberDuration=$soberDuration, lightDuration=$lightDuration, deepDuration=$deepDuration, sleepCurve=$sleepCurve)"
    }


}