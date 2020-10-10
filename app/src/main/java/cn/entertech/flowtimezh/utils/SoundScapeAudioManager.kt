package cn.entertech.flowtimezh.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File


class SoundScapeAudioManager private constructor(var context: Context) {
    private var fileDuration: Int? = -1
    var mediaPlayerA: MediaPlayer? = null
    var mediaPlayerB: MediaPlayer? = null
    var mainHandler: Handler? = null

    init {
        mainHandler = Handler(Looper.getMainLooper())
    }

    companion object {
        const val MEDIA_PLAYER_A = "A"
        const val MEDIA_PLAYER_B = "B"

        @Volatile
        var mInstance: SoundScapeAudioManager? = null
        fun getInstance(context: Context): SoundScapeAudioManager {
            if (mInstance == null) {
                synchronized(SoundScapeAudioManager::class.java) {
                    if (mInstance == null) {
                        mInstance = SoundScapeAudioManager(context)
                    }
                }
            }
            return mInstance!!
        }
    }


    var mediaPlayerARunnable = object : Runnable {
        override fun run() {
            var currentPosition = mediaPlayerA?.currentPosition
            if (currentPosition != null && fileDuration != null && fileDuration != -1 && fileDuration != 0) {
                var percent = currentPosition * 1f / fileDuration!!
                mainHandler?.postDelayed(this, 50)
//                Log.d("######", "SoundScapeAudioManager a percent is $percent")
                if (percent >= (57f / 60) && !isMediaPlayerBPlaying) {
//                    Log.d("######", "SoundScapeAudioManager play b")
                    startMediaPlayerB()
                }
            }
        }
    }

    var mediaPlayerBRunnable = object : Runnable {
        override fun run() {
            var currentPosition = mediaPlayerB?.currentPosition
            if (currentPosition != null && fileDuration != null && fileDuration != -1 && fileDuration != 0) {
                var percent = currentPosition * 1f / fileDuration!!
//                Log.d("######", "SoundScapeAudioManager b percent is $percent")
                mainHandler?.postDelayed(this, 50)
                if (percent >= (57f / 60) && !isMediaPlayerAPlaying) {
//                    Log.d("######", "SoundScapeAudioManager play a")
                    startMediaPlayerA()
                }
            }
        }
    }

    fun setFile(file: File) {
        releaseMediaPlayer()
        createMediaPlayer(file)
        fileDuration = mediaPlayerA?.duration
    }

    fun setFile(resId: Int){
        releaseMediaPlayer()
        createMediaPlayer(resId)
        fileDuration = mediaPlayerA?.duration
    }

    fun createMediaPlayer(file: File) {
        mediaPlayerA = MediaPlayer.create(context, Uri.fromFile(file))
        mediaPlayerB = MediaPlayer.create(context, Uri.fromFile(file))
        mediaPlayerA?.start()
        mediaPlayerA?.pause()
        mediaPlayerB?.start()
        mediaPlayerB?.pause()
        mediaPlayerA?.setOnCompletionListener {
            isMediaPlayerAPlaying = false
            mainHandler?.removeCallbacks(mediaPlayerARunnable)
        }
        mediaPlayerB?.setOnCompletionListener {
            isMediaPlayerBPlaying = false
            mainHandler?.removeCallbacks(mediaPlayerBRunnable)
        }
    }

    fun createMediaPlayer(resId: Int) {
        mediaPlayerA = MediaPlayer.create(context, resId)
        mediaPlayerB = MediaPlayer.create(context, resId)
        mediaPlayerA?.start()
        mediaPlayerA?.pause()
        mediaPlayerB?.start()
        mediaPlayerB?.pause()
        mediaPlayerA?.setOnCompletionListener {
            isMediaPlayerAPlaying = false
            mainHandler?.removeCallbacks(mediaPlayerARunnable)
        }
        mediaPlayerB?.setOnCompletionListener {
            isMediaPlayerBPlaying = false
            mainHandler?.removeCallbacks(mediaPlayerBRunnable)
        }
    }

    fun start() {
        startMediaPlayerA()
    }

    fun pause() {
        pauseMediaPlayerA()
        pauseMediaPlayerB()
    }

    fun resume() {
        when (currentMediaPlayer) {
            MEDIA_PLAYER_A -> {
                startMediaPlayerA()
            }
            MEDIA_PLAYER_B -> {
                startMediaPlayerB()
            }
            else -> {
                startMediaPlayerA()
            }
        }
    }

    var isMediaPlayerAPlaying = false
    var isMediaPlayerBPlaying = false
    var currentMediaPlayer = ""
    private fun startMediaPlayerA() {
        currentMediaPlayer = MEDIA_PLAYER_A
        isMediaPlayerAPlaying = true
        mediaPlayerA?.start()
        mainHandler?.post(mediaPlayerARunnable)
    }

    private fun startMediaPlayerB() {
        currentMediaPlayer = MEDIA_PLAYER_B
        isMediaPlayerBPlaying = true
        mediaPlayerB?.start()
        mainHandler?.post(mediaPlayerBRunnable)
    }

    private fun pauseMediaPlayerA() {
        isMediaPlayerAPlaying = false
        mediaPlayerA?.pause()
        mainHandler?.removeCallbacks(mediaPlayerARunnable)
    }

    private fun pauseMediaPlayerB() {
        isMediaPlayerBPlaying = false
        mediaPlayerB?.pause()
        mainHandler?.removeCallbacks(mediaPlayerBRunnable)
    }

    fun stopMediaPlayer() {
        if (mediaPlayerA != null && mediaPlayerA!!.isPlaying) {
            mediaPlayerA?.stop()
        }
        if (mediaPlayerB != null && mediaPlayerB!!.isPlaying) {
            mediaPlayerB?.stop()
        }
    }

    fun releaseMediaPlayer() {
        if (mediaPlayerA != null) {
            mediaPlayerA?.release()
        }
        if (mediaPlayerB != null) {
            mediaPlayerB?.release()
        }
    }

    fun removeHandlerCallback() {
        mainHandler?.removeCallbacks(mediaPlayerARunnable)
        mainHandler?.removeCallbacks(mediaPlayerBRunnable)
    }

    fun release() {
        isMediaPlayerBPlaying = false
        isMediaPlayerAPlaying = false
        removeHandlerCallback()
        stopMediaPlayer()
        releaseMediaPlayer()
    }
}