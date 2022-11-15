package cn.entertech.flowtimezh.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import cn.entertech.flowtimezh.R
import cn.entertech.uicomponentsdk.utils.dp


@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("Recycle")
class CommonButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : RelativeLayout(context, attributeSet, def) {

    companion object {
        const val BUTTON_SIZE_LARGE = 0
        const val BUTTON_SIZE_SMALL = 1
    }

    private var onButtonClickListener: (() -> Unit)? = null

    // 不可点击时颜色
    var btnDisableColor: Int = Color.parseColor("#878894")
        set(value) {
            field = value
            initView()
        }

    // 可点击时填充色
    var btnFillColor: Int = Color.parseColor("#ffffff")
        set(value) {
            field = value
            initView()
        }

    // 可点击时边框颜色
    var btnStrokeColor: Int = Color.parseColor("#080A0E")
        set(value) {
            field = value
            initView()
        }

    // 尺寸类型
    var btnSizeMode: Int = BUTTON_SIZE_LARGE
        set(value) {
            field = value
            initView()
        }

    // 图标
    var btnIcon: Drawable? = null
        set(value) {
            field = value
            initView()
        }

    // 文字颜色
    var btnTextColor: Int = Color.parseColor("#080A0E")
        set(value) {
            field = value
            initView()
        }

    // 文字
    var btnText: String? = ""
        set(value) {
            field = value
            initView()
        }

    // 是否可点击
    var btnEnable: Boolean = true
        set(value) {
            field = value
            initView()
        }

    var bgCornerRadius = 0f

    @SuppressLint("InflateParams")
    val selfView: View = LayoutInflater.from(context).inflate(R.layout.view_common_button, null)

    init {
        val typeArray = context.obtainStyledAttributes(
            attributeSet, R.styleable.CommonButton, def, 0
        )
        btnText = typeArray.getString(R.styleable.CommonButton_cb_btnText)
        btnTextColor = typeArray.getColor(R.styleable.CommonButton_cb_btnTextColor, btnTextColor)
        btnIcon = typeArray.getDrawable(R.styleable.CommonButton_cb_btnIcon)
        btnSizeMode = typeArray.getInt(R.styleable.CommonButton_cb_btnSizeMode, BUTTON_SIZE_LARGE)
        btnStrokeColor =
            typeArray.getColor(R.styleable.CommonButton_cb_btnStrokeColor, btnStrokeColor)
        btnFillColor = typeArray.getColor(R.styleable.CommonButton_cb_btnFillColor, btnFillColor)
        btnDisableColor =
            typeArray.getColor(R.styleable.CommonButton_cb_btnDisableColor, btnDisableColor)
        btnEnable = typeArray.getBoolean(R.styleable.CommonButton_cb_btnEnable, btnEnable)
        val layoutParams = if (btnSizeMode == BUTTON_SIZE_LARGE) {
            ViewGroup.LayoutParams(MATCH_PARENT, 46f.dp().toInt())
        } else {
            ViewGroup.LayoutParams(WRAP_CONTENT, 35f.dp().toInt())
        }
        selfView.layoutParams = layoutParams
        addView(selfView)
        if (btnSizeMode == BUTTON_SIZE_SMALL){
            bgCornerRadius = 8f.dp()
            selfView.setPadding(24f.dp().toInt(),8f.dp().toInt(),24f.dp().toInt(),8f.dp().toInt())
        }else{
            bgCornerRadius = 12f.dp()
        }
        initView()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("CutPasteId")
    fun initView() {
        val layoutParams = if (btnSizeMode == BUTTON_SIZE_LARGE) {
            LayoutParams(MATCH_PARENT, 46f.dp().toInt())
        } else {
            LayoutParams(WRAP_CONTENT, 35f.dp().toInt())
        }
        selfView.layoutParams = layoutParams
        selfView.requestLayout()
        selfView.findViewById<TextView>(R.id.tv_btn_text).text = btnText
        selfView.findViewById<TextView>(R.id.tv_btn_text).setTextColor(btnTextColor)
        if (btnIcon == null) {
            selfView.findViewById<ImageView>(R.id.iv_icon).visibility = View.GONE
        } else {
            selfView.findViewById<ImageView>(R.id.iv_icon).visibility = View.VISIBLE
            selfView.findViewById<ImageView>(R.id.iv_icon).setImageDrawable(btnIcon)
        }
        val rlBg = selfView.findViewById<RelativeLayout>(R.id.rl_bg)
        if (btnEnable) {
            rlBg.background = ContextCompat.getDrawable(context, R.drawable.shape_common_btn_enable)
            val drawable = rlBg.background as GradientDrawable
            drawable.setColor(btnFillColor)
            drawable.setStroke(1, btnStrokeColor)
            drawable.cornerRadius = bgCornerRadius
            val outValue = TypedValue()
            getContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            rlBg.foreground = ContextCompat.getDrawable(context,outValue.resourceId)
            rlBg.setOnClickListener {
                onButtonClickListener?.invoke()
            }
        } else {
            rlBg.background =
                ContextCompat.getDrawable(context, R.drawable.shape_common_btn_disable)
            val drawable =
                selfView.findViewById<RelativeLayout>(R.id.rl_bg).background as GradientDrawable
            drawable.setColor(btnDisableColor)
            drawable.cornerRadius = bgCornerRadius
            rlBg.foreground = null
            rlBg.setOnClickListener {
            }
        }
    }

    fun setOnClickListener(onClickListener:()->Unit){
        this.onButtonClickListener = onClickListener
    }
}