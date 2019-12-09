package cn.entertech.flowtimezh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import cn.entertech.affectivecloudsdk.utils.MD5Encode
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Application
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.entity.AuthEntity
import cn.entertech.flowtimezh.entity.UserNameEntity
import cn.entertech.flowtimezh.server.presenter.AuthPresenter
import cn.entertech.flowtimezh.server.view.AuthView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_experiment_setting.*
import kotlinx.android.synthetic.main.layout_common_title.*

class AuthActivity : BaseActivity() {
    private var settingManager: SettingManager? = null
    private var authPresenter: AuthPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_experiment_setting)
        initPresenter()
        initView()
    }

    var authView = object : AuthView {
        override fun onSuccess(authEntity: AuthEntity?) {
            SettingManager.getInstance().token = authEntity?.token
            startActivity(
                Intent(
                    this@AuthActivity,
                    MainActivity::class.java
                )
            )
            finish()
        }

        override fun onError(error: String) {
            Toast.makeText(this@AuthActivity, error, Toast.LENGTH_SHORT).show()
        }

    }

    fun initPresenter() {
        authPresenter = AuthPresenter(Application.getInstance())
        authPresenter?.onCreate()
        authPresenter?.attachView(authView)
    }

    private fun initView() {
        iv_back.visibility = View.INVISIBLE
        tv_title.visibility = View.VISIBLE
        tv_title.text = "实验设置"
        tv_menu_text.visibility = View.VISIBLE
        tv_menu_text.text = "确定"
        tv_menu_text.setOnClickListener {
            auth()
        }

        settingManager = SettingManager.getInstance()
        var oldAppKey = settingManager?.appKey
        var oldAppSecret = settingManager?.appSecret
        if (oldAppKey != "" && oldAppSecret != "") {
            et_app_key.setText(oldAppKey)
            et_app_secret.setText(oldAppSecret)
        }
    }

    private fun auth() {
        if (isEditTextNull()) {
            Toast.makeText(this, "请输入正确的app key 或 app secret!", Toast.LENGTH_SHORT).show()
        } else {
            var appKey = et_app_key.text.toString()
            var appSecret = et_app_secret.text.toString()
            var timestamp = "${System.currentTimeMillis()}"
            var md5Params =
                "app_key=$appKey&app_secret=$appSecret&timestamp=$timestamp&user_id=data_collector"
            var sign = MD5Encode(md5Params).toUpperCase()
            var userNameEntity = UserNameEntity()
            userNameEntity.app_key = appKey
            userNameEntity.sign = sign
            userNameEntity.timestamp = timestamp
            userNameEntity.user_id = "data_collector"
            var gson = Gson().toJson(userNameEntity)
            var userName = gson
            var password = sign
            settingManager?.appKey = appKey
            settingManager?.appSecret = appSecret
            authPresenter?.auth(userName, password)
        }
    }

    private fun isEditTextNull(): Boolean {
        return (et_app_key.text == null || et_app_key.text.toString() == "" || et_app_secret.text == null || et_app_secret.text.toString() == "")
    }

}
