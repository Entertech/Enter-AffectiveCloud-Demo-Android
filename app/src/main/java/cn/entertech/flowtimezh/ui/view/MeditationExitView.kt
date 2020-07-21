package cn.entertech.flowtimezh.ui.view

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.utils.BitmapUtils
import cn.entertech.flowtimezh.utils.ShotShareUtil

class MeditationExitView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    RelativeLayout(context, attributeSet, def) {
    private var mBitmapDrawable: BitmapDrawable? = null
    private var mExitButtonListener: (() -> Unit)? = null
    private var mState: State? = null
    private var self: View? = null

    var mediaPlayer: MediaPlayer? = null

    init {
        mediaPlayer = MediaPlayer.create(context, R.raw.meditation_disconnect_1)
        self = LayoutInflater.from(context).inflate(R.layout.view_meditation_exit_tip, null)
        var layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        self?.layoutParams = layoutParams
        addView(self)
        initView(self)
    }

    fun initView(view: View?) {
        view?.findViewById<TextView>(R.id.tv_exit)?.setOnClickListener {
            mExitButtonListener?.invoke()
        }
        view?.findViewById<Button>(R.id.btn_not_now)?.setOnClickListener {
            this@MeditationExitView.visibility = View.GONE
        }
        view?.findViewById<ImageView>(R.id.iv_voice)?.setOnClickListener {
            playDisconnectAudio()
        }

    }

    enum class State {
        COMPLETE, UNCOMPLETED, DISCONNECT
    }

    fun setBackground(view: View) {
        self?.findViewById<RelativeLayout>(R.id.rl_bg)?.background =
            BitmapDrawable(context.resources, BitmapUtils.blur(context, ShotShareUtil.shotScreenView(view), 25f))
    }

    fun setState(state: State) {
        this.mState = state
        when (mState) {
            State.COMPLETE -> {
                showCompleteView()
            }
            State.UNCOMPLETED -> {
                showUncompletedView()
            }
            State.DISCONNECT -> {
                showDisconnectView()
            }
        }
    }

    fun addExitButtonClickListener(listener: (() -> Unit)?) {
        this.mExitButtonListener = listener
    }

    fun showCompleteView() {
        self?.findViewById<LinearLayout>(R.id.ll_disconnect_view)?.visibility = View.GONE
        self?.findViewById<LinearLayout>(R.id.ll_normal_view)?.visibility = View.VISIBLE
        self?.findViewById<ImageView>(R.id.iv_icon)?.setImageResource(R.mipmap.pic_meditation_exit_done)
        self?.findViewById<TextView>(R.id.tv_tip)?.text = "You did it"
        self?.findViewById<TextView>(R.id.tv_remark)?.text = "Exit and you'll have the report."
        self?.findViewById<TextView>(R.id.tv_exit)?.text = "Not Now"
        self?.findViewById<TextView>(R.id.btn_not_now)?.text = "Exit And Get Report"
        self?.findViewById<TextView>(R.id.tv_exit)?.setOnClickListener {
            this@MeditationExitView.visibility = View.GONE
        }
        self?.findViewById<Button>(R.id.btn_not_now)?.setOnClickListener {
            mExitButtonListener?.invoke()
        }
    }

    fun showUncompletedView() {
        self?.findViewById<LinearLayout>(R.id.ll_disconnect_view)?.visibility = View.GONE
        self?.findViewById<LinearLayout>(R.id.ll_normal_view)?.visibility = View.VISIBLE
        self?.findViewById<ImageView>(R.id.iv_icon)?.setImageResource(R.mipmap.pic_meditation_exit_not_done)
        self?.findViewById<TextView>(R.id.tv_tip)?.text = "Leave now?"
        self?.findViewById<TextView>(R.id.tv_remark)?.text = "The practice is too short to have a report."
        self?.findViewById<TextView>(R.id.tv_exit)?.text = "Exit"
        self?.findViewById<TextView>(R.id.btn_not_now)?.text = "Not Now"
        self?.findViewById<TextView>(R.id.tv_exit)?.setOnClickListener {
            mExitButtonListener?.invoke()
        }
        self?.findViewById<Button>(R.id.btn_not_now)?.setOnClickListener {
            this@MeditationExitView.visibility = View.GONE
        }
    }

    fun showDisconnectView() {
        self?.findViewById<LinearLayout>(R.id.ll_disconnect_view)?.visibility = View.VISIBLE
        self?.findViewById<LinearLayout>(R.id.ll_normal_view)?.visibility = View.GONE
        self?.findViewById<TextView>(R.id.tv_exit)?.text = "Exit"
        self?.findViewById<TextView>(R.id.btn_not_now)?.text = "Not Now"
        self?.findViewById<TextView>(R.id.tv_exit)?.setOnClickListener {
            mExitButtonListener?.invoke()
        }
        self?.findViewById<Button>(R.id.btn_not_now)?.setOnClickListener {
            this@MeditationExitView.visibility = View.GONE
        }
    }

    fun playDisconnectAudio() {
        mediaPlayer?.start()
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDetachedFromWindow() {
        releaseMediaPlayer()
        super.onDetachedFromWindow()
    }

}