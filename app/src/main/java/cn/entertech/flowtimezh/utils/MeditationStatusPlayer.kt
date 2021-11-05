package cn.entertech.flowtimezh.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.rtp.AudioStream
import cn.entertech.flowtimezh.R

class MeditationStatusPlayer(context: Context) {
    var mContext: Context? = null
    var mediaPlayer: MediaPlayer? = null

    init {
        mContext = context
        mediaPlayer = MediaPlayer.create(mContext, R.raw.label_reocrd_start,AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM).build(),0)
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM).build()
        )
    }

    fun playRecordStartAudio() {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(mContext, R.raw.label_reocrd_start,AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM).build(),0)
        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM).build()
        )
        mediaPlayer?.setVolume(0.1f, 0.1f)
        mediaPlayer?.start()
    }

    fun playRecordEndAudio() {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(mContext, R.raw.label_record_end,AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM).build(),0)
        mediaPlayer?.setVolume(0.1f, 0.1f)
        mediaPlayer?.start()
    }

    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}