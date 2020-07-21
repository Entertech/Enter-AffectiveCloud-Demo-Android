package cn.entertech.flowtimezh.ui.activity

import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.View
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.UNGUIDE_COURSE_ID
import cn.entertech.flowtimezh.app.Constant.Companion.UNGUIDE_LESSON_ID
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.model.meditation.ReportMeditationDataEntity
import cn.entertech.flowtimezh.ui.view.MeditationExitView
import cn.entertech.flowtimezh.ui.view.ProgressPlayButton
import cn.entertech.flowtimezh.utils.ToastUtil
import cn.entertech.flowtimezh.utils.getCurrentTimeFormat
import kotlinx.android.synthetic.main.activity_meditation1.*

class MeditationWithoutGuideActivity : MeditationBaseActivity() {
    private var startTime: Long? = null
    private var closeTime: Long? = null
    private var mTimeCountDown: CountDownTimer? = null

    var totalSecond = 0
    var endSoundMediaPlayer: MediaPlayer? = null
    override fun resumeMeditation() {
        ppb_play_unguide.play()
        biomoduleBleManager.startHeartAndBrainCollection()
        startTimer()
    }

    override fun pauseMeditation() {
        ppb_play_unguide.pause()
        pauseTimer()
        biomoduleBleManager.stopHeartAndBrainCollection()
    }

    override fun init() {
        lessonName = "Unguided Meditation"
        courseName = "unguide"
        lessonId = UNGUIDE_LESSON_ID
        courseId = UNGUIDE_COURSE_ID
        iv_unguide_image_bg.visibility = View.VISIBLE
        tv_lesson_index.visibility = View.GONE
        rl_wave_loading.visibility = View.GONE
        ll_meditation_no_guide.visibility = View.VISIBLE
        initPlayButton()
        SettingManager.getInstance().isMeditationTimeReset = true

        meditationTotalMins = if (SettingManager.getInstance().isCustomMeditationTime) {
            SettingManager.getInstance().customMeditationTime
        } else {
            SettingManager.getInstance().meditationTime
        }
        initTimer()
    }
    fun setTimeCountText(time:Int){
        var hour = time / 3600L
        var min = (time- hour * 3600) / 60
        var second =time - hour * 3600 - min * 60
        var formatTime: String
        formatTime = if (hour == 0L) {
            String.format("%02d:%02d", min, second)
        } else {
            String.format("%02d:%02d:%02d", hour, min, second)
        }
        tv_time_count_down.text = formatTime
    }

    fun initTimer() {
        totalSecond = if (SettingManager.getInstance().isCustomMeditationTime) {
            SettingManager.getInstance().customMeditationTime
        } else {
            SettingManager.getInstance().meditationTime
        } * 60
        var currentSecond = 0
        setTimeCountText(currentSecond)
        ppb_play_unguide.setProgress(currentSecond.toFloat())
        tv_time_count_down.setOnChronometerTickListener {
            setTimeCountText(++currentSecond)
            if (currentSecond <= totalSecond){
                ppb_play_unguide.setProgress(currentSecond.toFloat())
            }
            if (currentSecond == totalSecond){
                if (SettingManager.getInstance().meditationEndSoundRes != 0) {
                    endSoundMediaPlayer = MediaPlayer.create(
                        this@MeditationWithoutGuideActivity,
                        SettingManager.getInstance().meditationEndSoundRes
                    )
                    endSoundMediaPlayer?.start()
                }
            }
        }

        ppb_play_unguide.setMax(totalSecond)
        startTime = System.currentTimeMillis()
    }

    fun initPlayButton() {
        ppb_play_unguide.visibility = View.VISIBLE
        ppb_play_unguide.play()
        ppb_play_unguide.setOnButtonStateCallback(object :
            ProgressPlayButton.IOnButtonStateCallback {
            override fun onPlay() {
                resumeMeditation()
            }

            override fun onPause() {
                pauseMeditation()
            }

        })
    }

    fun startTimer() {
        tv_time_count_down.start()
    }

    fun pauseTimer() {
        tv_time_count_down.stop()
//        mTimeCountDown?.cancel()
    }
    override fun onResume() {
        super.onResume()
        if (!needToCheckSensor){
            resumeMeditation()
        }
        if (SettingManager.getInstance().isMeditationTimeReset) {
//            resetTimeCountDown()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseMeditation()
    }

    var currentMin = 0f
    var meditationTotalMins = 0
    private fun resetTimeCountDown() {
        currentMin = 0f
        mTimeCountDown?.cancel()
        meditationTotalMins = if (SettingManager.getInstance().isCustomMeditationTime) {
            SettingManager.getInstance().customMeditationTime
        } else {
            SettingManager.getInstance().meditationTime
        }
        ppb_play_unguide.setMax(meditationTotalMins)
        mTimeCountDown = object : CountDownTimer((meditationTotalMins * 60 * 1000).toLong(), 1000) {
            override fun onFinish() {
                tv_time_count_down.text = "00:00"
                if (SettingManager.getInstance().meditationEndSoundRes != 0) {
                    endSoundMediaPlayer = MediaPlayer.create(
                        this@MeditationWithoutGuideActivity,
                        SettingManager.getInstance().meditationEndSoundRes
                    )
                    endSoundMediaPlayer?.start()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                currentMin += 1 / 60f
                ppb_play_unguide.setProgress(currentMin)
                var totalSecond = millisUntilFinished / 1000
                var hour = totalSecond / 3600
                var min = (millisUntilFinished / 1000 - hour * 3600) / 60
                var second = millisUntilFinished / 1000 - hour * 3600 - min * 60
                var formatTime: String
                formatTime = if (hour == 0L) {
                    String.format("%02d:%02d", min, second)
                } else {
                    String.format("%02d:%02d:%02d", hour, min, second)
                }
                tv_time_count_down.text = formatTime
            }
        }
        mTimeCountDown?.start()
        startTime = System.currentTimeMillis()
        SettingManager.getInstance().isMeditationTimeReset = false
    }

    override fun showExitView() {
        if (exit_view.visibility == View.VISIBLE) {
            return
        }
        exit_view.setBackground(cl_bg)
        exit_view.visibility = View.VISIBLE
        closeTime = System.currentTimeMillis()
        if ((closeTime!! - startTime!!) < 3 * 60 * 1000) {
            exit_view.setState(MeditationExitView.State.UNCOMPLETED)
            exit_view.addExitButtonClickListener {
                ToastUtil.toastShort(
                    this,
                    getString(R.string.meditation_time_less_5_min_tip_unguide)
                )
                finish()
            }
        } else if (goodQualityRate < 0.2f || meditationStartTime != null && (!enterAffectiveCloudManager!!.isWebSocketOpen() || !biomoduleBleManager.isConnected())) {
            exit_view.setState(MeditationExitView.State.DISCONNECT)
            exit_view.addExitButtonClickListener {
                loadingDialog?.loading()
                startFinishTimer()
                reportMeditationData = ReportMeditationDataEntity()
                meditationEndTime = getCurrentTimeFormat()
                biomoduleBleManager.stopHeartAndBrainCollection()
                biomoduleBleManager.stopBrainCollection()
                saveUserLessonInDB()
                postRecord()
            }
        } else {
            exit_view.setState(MeditationExitView.State.COMPLETE)
            exit_view.addExitButtonClickListener {
                loadingDialog?.loading()
                startFinishTimer()
                reportMeditationData = ReportMeditationDataEntity()
                meditationEndTime = getCurrentTimeFormat()
                biomoduleBleManager.stopHeartAndBrainCollection()
                biomoduleBleManager.stopBrainCollection()
                if (meditationStartTime == null) {
                    saveUserLessonInDB()
                    postRecord()
                } else {
                    report()
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        endSoundMediaPlayer?.stop()
        endSoundMediaPlayer?.release()
        mTimeCountDown?.cancel()
    }
}
