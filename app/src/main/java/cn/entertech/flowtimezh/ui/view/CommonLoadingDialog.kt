package cn.entertech.flowtimezh.ui.view

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.utils.ScreenUtil


class CommonLoadingDialog(context: Context) {

    private val DIALOG_DISMISS_DELAY_TIME: Long = 2000L
    private var anim: ObjectAnimator? = null

    private var self =
        LayoutInflater.from(context).inflate(R.layout.dialog_common_loading, null)
    var handler: Handler? = null
    private var dialog: AlertDialog = AlertDialog.Builder(context)
        .setView(self)
        .create()

    private var mContext: Context = context

    init {
        handler = Handler(Looper.getMainLooper())
        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                mContext,
                R.drawable.shape_popwindow_bg
            )
        )

        var decorViewLp = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        decorViewLp.gravity = Gravity.CENTER
        dialog.window?.decorView?.layoutParams = decorViewLp
        dialog.window?.setDimAmount(0f)
    }


    fun loading(text: String = mContext.getString(R.string.loading), isCancelable: Boolean = false) {
        if (!(mContext as Activity).isFinishing) {
            try {
                self.findViewById<ImageView>(R.id.iv_load_failed_icon).visibility = View.GONE
                self.findViewById<ImageView>(R.id.iv_load_success_icon).visibility = View.GONE
                self.findViewById<TextView>(R.id.tv_text).text = text
                self.findViewById<ImageView>(R.id.iv_load_loading).visibility =
                    View.VISIBLE
                rotateViewAnim(self.findViewById<ImageView>(R.id.iv_load_loading))
                dialog.setCancelable(isCancelable)
                dialog.show()
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window!!.attributes)
                lp.width = ScreenUtil.dip2px(mContext, 130f)
                lp.height = ScreenUtil.dip2px(mContext, 130f)
                lp.gravity = Gravity.CENTER
                dialog.window!!.attributes = lp
            }catch (_:Exception){
            }
        }
    }

    private fun rotateViewAnim(view: View) {
        anim = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        anim?.interpolator = LinearInterpolator()
        anim?.repeatCount = Animation.INFINITE
        anim?.duration = 500
        anim?.start()
    }

    fun error(msg: String) {
        if (!(mContext as Activity).isFinishing) {
            try {
                self.findViewById<TextView>(R.id.tv_text).text = msg
                self.findViewById<ImageView>(R.id.iv_load_failed_icon).visibility = View.VISIBLE
                self.findViewById<ImageView>(R.id.iv_load_success_icon).visibility = View.GONE
                self.findViewById<ImageView>(R.id.iv_load_loading).visibility = View.GONE
                handler?.postDelayed({ dismiss() }, DIALOG_DISMISS_DELAY_TIME)
                dialog.setCancelable(false)
                dialog.show()
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window!!.attributes)
                lp.width = ScreenUtil.dip2px(mContext, 130f)
                lp.height = ScreenUtil.dip2px(mContext, 130f)
                lp.gravity = Gravity.CENTER
                dialog.window!!.attributes = lp
            }catch (_:Exception){
            }
        }
    }

    fun success(msg: String,callback:(()->Unit)? = null) {
        if (!(mContext as Activity).isFinishing) {
            try {
                self.findViewById<TextView>(R.id.tv_text).text = msg
                self.findViewById<ImageView>(R.id.iv_load_failed_icon).visibility = View.GONE
                self.findViewById<ImageView>(R.id.iv_load_success_icon).visibility = View.VISIBLE
                self.findViewById<ImageView>(R.id.iv_load_success_icon)
                    .setImageResource(R.drawable.vector_drawable_loading_icon_done)
                self.findViewById<ImageView>(R.id.iv_load_loading).visibility = View.GONE
                handler?.postDelayed({
                    dismiss()
                    callback?.invoke()
                }, DIALOG_DISMISS_DELAY_TIME)
                dialog.setCancelable(false)
                dialog.show()
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window!!.attributes)
                lp.width = ScreenUtil.dip2px(mContext, 130f)
                lp.height = ScreenUtil.dip2px(mContext, 130f)
                lp.gravity = Gravity.CENTER
                dialog.window!!.attributes = lp
            }catch (_:Exception){
            }
        }
    }

    fun dismiss() {
        if (!(mContext as Activity).isFinishing) {
            if (anim != null && anim!!.isRunning) {
                anim!!.cancel()
            }
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }
}