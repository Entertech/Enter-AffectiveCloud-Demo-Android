package cn.entertech.affectiveclouddemo.utils

import android.content.Context
import android.media.MediaPlayer
import cn.entertech.affectiveclouddemo.R

class MeditationStatusPlayer(context: Context) {
    var mContext: Context? = null
    var mediaPlayer: MediaPlayer? = null

    init {
        mContext = context
        mediaPlayer = MediaPlayer.create(mContext, R.raw.meditation_reconnect)
    }

    fun playConnectAudio() {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(mContext, R.raw.meditation_reconnect)
        mediaPlayer?.setVolume(1f, 1f)
        mediaPlayer?.start()
    }

    fun playDisconnectAudio() {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(mContext, R.raw.meditation_disconnect_1)
        mediaPlayer?.setVolume(1f, 1f)
        mediaPlayer?.start()
    }

    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}