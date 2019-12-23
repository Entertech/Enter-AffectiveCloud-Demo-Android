package cn.entertech.flowtimezh.utils

class MeditationTimeManager {
    /*体验时长，单位毫秒*/
    var timeCount = 0L

    companion object {
        @Volatile
        var mInstance: MeditationTimeManager? = null

        fun getInstance(): MeditationTimeManager {
            if (mInstance == null) {
                synchronized(MeditationTimeManager::class.java) {
                    if (mInstance == null) {
                        mInstance = MeditationTimeManager()
                    }
                }
            }
            return mInstance!!
        }
    }


    fun timeIncrease() {
        timeCount += 360
    }

    fun timeReset() {
        timeCount = 0
    }

    fun currentTimeMs(): Long {
        return timeCount
    }
}