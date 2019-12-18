package cn.entertech.flowtime.utils.reportfileutils

class MeditaionInterruptManager {

    var interruptList = arrayListOf<String>()
    var interruptTimestampList = arrayListOf<MeditationInterrupt>()


    class MeditationInterrupt {
        var interruptStartTime:Long? = null
        var interruptEndTime:Long? = null
    }

    companion object {
        const val INTERRUPT_TYPE_DEVICE = "device"
        const val INTERRUPT_TYPE_NET = "net"
        var mInstance: MeditaionInterruptManager? = null
        fun getInstance(): MeditaionInterruptManager {
            if (mInstance == null) {
                synchronized(MeditaionInterruptManager::class.java) {
                    if (mInstance == null) {
                        mInstance = MeditaionInterruptManager()
                    }
                }
            }
            return mInstance!!
        }
    }

    var meditationInterrupt:MeditationInterrupt? = null
    fun pushInterrupt(interruptType: String) {
        if (!interruptList.contains(interruptType)) {
            interruptList.add(interruptType)
            if (interruptList.size == 1) {
                meditationInterrupt = MeditationInterrupt()
                meditationInterrupt?.interruptStartTime = System.currentTimeMillis()
            }
        }
    }

    fun popInterrupt(interruptType: String) {
        if (interruptList.contains(interruptType)) {
            interruptList.remove(interruptType)
            if (interruptList.size == 0) {
                meditationInterrupt?.interruptEndTime = System.currentTimeMillis()
                if (meditationInterrupt != null){
                    interruptTimestampList.add(meditationInterrupt!!)
                }
            }
        }
    }

    fun clear() {
        interruptList.clear()
        interruptTimestampList.clear()
    }

    fun getInterruptTimestamps(): ArrayList<MeditationInterrupt> {
        return interruptTimestampList
    }

}