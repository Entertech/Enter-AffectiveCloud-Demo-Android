package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import cn.entertech.flowtimezh.MainActivity
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.Constant
import cn.entertech.flowtimezh.app.Constant.Companion.CLIENT_ID
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.model.AuthSocialEntity
import cn.entertech.flowtimezh.mvp.presenter.AuthSocialPresenter
import cn.entertech.flowtimezh.mvp.view.AuthSocialView
import cn.entertech.flowtimezh.ui.view.LoadingDialog
import cn.entertech.flowtimezh.wxapi.uikit.MessageEvent
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginActivity : BaseActivity() {

    private var loadingDialog: LoadingDialog? = null
    lateinit var mAuthSocialPresenter: AuthSocialPresenter

    companion object {
        const val ACITIVTY_REQUEST_CODE_GOOGLE_LOGIN = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initFullScreenDisplay()
//        setStatusBarLight()
        Logger.addLogAdapter(AndroidLogAdapter())
        initPresenter()
        initView()
        EventBus.getDefault().register(this)
        loadingDialog = LoadingDialog(this)
    }

    override fun onResume() {
        super.onResume()
    }

    fun initView() {
//        if (getDarkModeStatus(this)){
//            rl_image_bg.setBackgroundResource(R.mipmap.pic_login_bg_dark)
//        }else{
//            rl_image_bg.setBackgroundResource(R.mipmap.pic_login_bg_light)
//        }
        btnLogin.setOnClickListener {
            var req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = "wechat_sdk_微信登录"
            Application.getInstance().getWXApi()?.sendReq(req)
            Application.getInstance().getWXApi()?.unregisterApp()
        }
        initTip()
    }

    fun initTip() {
        var spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(getText(R.string.loginTip))
        var termsColorSpan = ForegroundColorSpan(getColor(R.color.colorThemeBlue))
        var privacyColorSpan = ForegroundColorSpan(getColor(R.color.colorThemeBlue))
        spannableStringBuilder.setSpan(termsColorSpan, 79, 85, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableStringBuilder.setSpan(privacyColorSpan, 89, 96, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        var termsClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                var uri = Uri.parse(SettingManager.getInstance().remoteConfigTermsOfUser)
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
        var privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                var uri = Uri.parse(SettingManager.getInstance().remoteConfigPrivacy)
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
        spannableStringBuilder.setSpan(termsClickableSpan, 79, 85, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableStringBuilder.setSpan(privacyClickableSpan, 89, 96, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        tv_tip.text = spannableStringBuilder
        tv_tip.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun initPresenter() {
        mAuthSocialPresenter = AuthSocialPresenter(applicationContext)
        mAuthSocialPresenter.onCreate()
        mAuthSocialPresenter.attachView(authSocialView)

    }

    var authSocialView = object : AuthSocialView {
        override fun onError(error: String) {
            loadingDialog?.dismiss()
            Logger.d("授权失败：$error")
        }

        override fun onSuccess(authSocialEntity: AuthSocialEntity) {
            loadingDialog?.dismiss()
            SettingManager.getInstance().userId = authSocialEntity.uid
            Logger.d("授权成功：${authSocialEntity?.access_token}")
            SettingManager.getInstance().token = authSocialEntity?.access_token
            SettingManager.getInstance().refreshToken = authSocialEntity?.refresh_token
//            Toast.makeText(this@LoginActivity, "授权成功：${authSocialEntity?.access_token}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.messageCode == 111) {
            SettingManager.getInstance().socialType = Constant.SOCIAL_LOGIN_TYPE_WECHAT
            SettingManager.getInstance().socialImage = event.wxImage
            var name =  String(event.wxName.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            SettingManager.getInstance().socialUserName = name
            loadingDialog?.loading()
            mAuthSocialPresenter.authSocial(
                CLIENT_ID,
                event.wxUserId,
                event.wxName,
                SettingManager.getInstance().socialType,
                event.wxAcessToken
            )
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        mAuthSocialPresenter.onStop()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
