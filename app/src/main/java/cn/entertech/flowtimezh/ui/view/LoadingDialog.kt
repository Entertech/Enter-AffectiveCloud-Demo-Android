package cn.entertech.flowtimezh.ui.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.utils.ScreenUtil
import com.airbnb.lottie.LottieAnimationView

class LoadingDialog(context: Context) {

    private var view: View?

    private var dialog: AlertDialog

    private var mContext: Context = context

    init {
        view = LayoutInflater.from(context).inflate(R.layout.layout_loading_view, null)
        dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
    }

    fun loading(text: String = "Loading") {
        dialog.show()
        val decorView = dialog.getWindow()?.getDecorView()
        if (decorView == null){
            return
        }
        val paddingTop = decorView.getPaddingTop()
        val paddingBottom = decorView.getPaddingBottom()
        val paddingLeft = decorView.getPaddingLeft()
        val paddingRight = decorView.getPaddingRight()
        val width = ScreenUtil.dip2px(mContext, 150f) + paddingLeft + paddingRight
        val height = ScreenUtil.dip2px(mContext, 130f) + paddingTop + paddingBottom
        dialog.getWindow()!!.setLayout(width, height)
        view?.findViewById<ImageView>(R.id.iv_load_failed_icon)?.visibility = View.GONE
        view?.findViewById<TextView>(R.id.tv_text)?.text = text
        view?.findViewById<LottieAnimationView>(R.id.lav_loading_icon)?.visibility = View.VISIBLE
    }

    fun loadFailed() {
        dialog.show()
        view?.findViewById<ImageView>(R.id.iv_load_failed_icon)?.visibility = View.VISIBLE
        view?.findViewById<LottieAnimationView>(R.id.lav_loading_icon)?.visibility = View.GONE
    }

    fun dismiss() {
        dialog.dismiss()
    }
}