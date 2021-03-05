package cn.entertech.affectiveclouddemo.utils.reportfileutils

import android.os.Handler
import android.os.HandlerThread
import cn.entertech.affectiveclouddemo.model.meditation.ReportMeditationDataEntity
import cn.entertech.flowtime.utils.reportfileutils.MeditaionInterruptManager

class FragmentBuffer {
    private val handlerThread: HandlerThread = HandlerThread("file_fragment_buffer")
    private val handler: Handler
    private val brainWaveFileUtil: BrainWaveFileUtil = BrainWaveFileUtil()
    var fileName: String? = null


    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    //写文件后清除
    var meditationReportFileFragment: FileFragment<MeditationReportDataAnalyzed>? = null


    fun appendMeditationReport(
        reportMeditationDataEntity: ReportMeditationDataEntity,
        startTime: Long,
        interruptTimeStamps: ArrayList<MeditaionInterruptManager.MeditationInterrupt>
    ) {
        var state = FileFragment.Status.NORMAL

        if (null == meditationReportFileFragment) {
            meditationReportFileFragment = FileFragment(state, FileFragmentContent())
        }

        reportMeditationDataEntity.let {
            meditationReportFileFragment?.content?.append(
                MeditationReportDataAnalyzed(
                    reportMeditationDataEntity,
                    startTime,
                    interruptTimeStamps
                )
            )
        }

        handler.post {
            writeMeditationReport()
        }
    }

    @Synchronized
    private fun writeMeditationReport() {
        meditationReportFileFragment?.let {
            //            Logger.d(it.content.content.size)
            brainWaveFileUtil.writeFragment(fileName!!, it)
        }
        meditationReportFileFragment = null
    }
}