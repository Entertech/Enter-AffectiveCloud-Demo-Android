package cn.entertech.flowtimezh.utils

import android.content.Context
import android.media.MediaPlayer
import cn.entertech.flowtimezh.R

class LabelRecordPlayer(context: Context) {
    var mContext: Context? = null
    var mediaPlayer: MediaPlayer? = null

    init {
        mContext = context
        mediaPlayer = MediaPlayer.create(mContext, R.raw.label_record_start)
    }

    fun playStartRecord() {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(mContext, R.raw.label_record_start)
        mediaPlayer?.setVolume(1f, 1f)
        mediaPlayer?.start()
    }

    fun playEndRecord() {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(mContext, R.raw.label_record_end)
        mediaPlayer?.setVolume(1f, 1f)
        mediaPlayer?.start()
    }

    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}