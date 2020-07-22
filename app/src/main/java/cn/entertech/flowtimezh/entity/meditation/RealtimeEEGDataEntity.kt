package cn.entertech.flowtimezh.entity.meditation;

class RealtimeEEGDataEntity {
    var leftwave: ArrayList<Double>? = ArrayList()
    var rightwave: ArrayList<Double>? = ArrayList()
    var alphaPower: Double? = 0.0
    var betaPower: Double? = 0.0
    var thetaPower: Double? = 0.0
    var deltaPower: Double? = 0.0
    var gammaPower: Double? = 0.0
    var progress: Double? = 0.0
    override fun toString(): String {
        return "RealtimeEEGDataEntity(leftwave=$leftwave, rightwave=$rightwave, alphaPower=$alphaPower, betaPower=$betaPower, thetaPower=$thetaPower, deltaPower=$deltaPower, gammaPower=$gammaPower, progress=$progress)"
    }


}