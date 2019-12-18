package cn.entertech.affectiveclouddemo.utils.reportfileutils

import android.os.Handler
import android.os.HandlerThread

/**
 * Created by EnterTech on 2017/11/15.
 */
class BrainDataBuffer(private var cnt:Int = 5) {

    private var handlerThread: HandlerThread
    private var handler: Handler
    private val buffer = mutableListOf<Byte>()
    private val NUM_IN_PACK = 5 * 3

    init {
        handlerThread = HandlerThread("brain_buffer")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        cnt *= NUM_IN_PACK
    }

    fun isCanPop(): Boolean {
        return buffer.size >= cnt
    }

    fun push(data: Byte, pop: (ByteArray)->Unit) {
//        val rawDataByte = data
        handler.post {
            buffer.add(data)
            if (isCanPop()) {
                pop(pop())
            }
        }
    }

    fun pop(data: Byte) {
        handler.post {
            buffer.remove(data)
        }
    }

    private fun pop(): ByteArray {
        val list = ByteArray(cnt)
        var curIdx = 0
        val iterator = buffer.iterator()
        while (iterator.hasNext()) {
            list[curIdx] = (iterator.next())
            iterator.remove()
            if (++curIdx >= cnt)
                break
        }

        return list
    }
}