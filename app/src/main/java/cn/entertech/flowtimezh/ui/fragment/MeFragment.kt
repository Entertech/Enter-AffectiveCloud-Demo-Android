package cn.entertech.flowtimezh.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_me.*
import android.net.Uri
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.widget.Toast
import cn.entertech.flowtimezh.R
import cn.entertech.flowtimezh.app.Constant.Companion.FEEDBACK_EMAIL_ADDRESS
import cn.entertech.flowtimezh.app.Constant.Companion.FEEDBACK_EMAIL_SUBJECT
import cn.entertech.flowtimezh.app.Constant.Companion.SOCIAL_LOGIN_TYPE_WECHAT
import cn.entertech.flowtimezh.app.SettingManager
import cn.entertech.flowtimezh.mvp.model.MessageEvent
import cn.entertech.flowtimezh.ui.activity.DevicePermissionActivity
import cn.entertech.flowtimezh.ui.activity.DeviceStatusActivity
import cn.entertech.flowtimezh.ui.activity.LoginActivity
import cn.entertech.flowtimezh.utils.TimeUtils
import cn.entertech.flowtimezh.utils.getAppVersionCode
import cn.entertech.flowtimezh.utils.getAppVersionName


class MeFragment : androidx.fragment.app.Fragment() {
    var self: View? = null
    var clickCount = 0
    var lastClickTimestamp = 0L
    var currentClickTimestamp = 0L
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        self = inflater.inflate(R.layout.fragment_me, container, false)
        EventBus.getDefault().register(this)
        return self
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    fun initView() {
        Glide.with(activity!!)
            .load(SettingManager.getInstance().socialImage)
            .into(tv_user_profile)
        when (SettingManager.getInstance().socialType) {
            SOCIAL_LOGIN_TYPE_WECHAT -> {
                iv_social_logo.setImageResource(R.drawable.vector_drawable_login_wx)
            }

        }
        tv_user_name.text = SettingManager.getInstance().socialUserName
        item_rate_app.setOnItemClick(View.OnClickListener {
        })
        item_flowtime_headhand.setOnItemClick(View.OnClickListener {
            if (SettingManager.getInstance().isConnectBefore) {
                startActivity(Intent(activity, DeviceStatusActivity::class.java))
            } else {
                startActivity(Intent(activity, DevicePermissionActivity::class.java))
            }
        })
        item_feedback.setOnItemClick(View.OnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "message/rfc822"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf(FEEDBACK_EMAIL_ADDRESS))
            i.putExtra(Intent.EXTRA_SUBJECT, FEEDBACK_EMAIL_SUBJECT)
            i.putExtra(
                Intent.EXTRA_TEXT, "\r\n\r\n${android.os.Build.MODEL}," +
                        "Android ${android.os.Build.VERSION.RELEASE},心流,${getAppVersionName(
                            activity!!
                        )}(${getAppVersionCode(
                            activity!!
                        )})，UserId:${SettingManager.getInstance().userId}"
            )
            try {
                startActivity(Intent.createChooser(i, "Send mail..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(
                    activity!!,
                    "There are no email clients installed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        item_help.setOnItemClick(View.OnClickListener {
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigHelpCenter)
            activity!!.startActivity(Intent(Intent.ACTION_VIEW, uri))
        })
        item_terms_of_service.setOnItemClick(View.OnClickListener {
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigTermsOfUser)
            activity!!.startActivity(Intent(Intent.ACTION_VIEW, uri))
        })
        item_privacy.setOnItemClick(View.OnClickListener {
//            postButtonEvent(activity!!, "510", "Me Privacy")
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigPrivacy)
            activity!!.startActivity(Intent(Intent.ACTION_VIEW, uri))
        })
//        item_redeem.setOnItemClick(View.OnClickListener {
//            postButtonEvent(activity!!, "502", "Me Redeem")
//            val intent = Intent(activity, RedeemActivity::class.java)
//            startActivity(intent)
//        })

        item_rate_app.setOnItemClick(View.OnClickListener {
//            postButtonEvent(activity!!, "505", "Me RateApp")
            val uri = Uri.parse("market://details?id=" + activity!!.packageName)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity!!.startActivity(intent)
        })
        item_reminder.setOnItemClick(View.OnClickListener {
//            postButtonEvent(activity!!, "503", "Me Reminder")
//            val intent = Intent(activity, ReminderActivity::class.java)
//            startActivity(intent)
            //todo
        })
        tv_sign_out.setOnClickListener {
//            postButtonEvent(activity!!, "511", "Me Sign Out")
            showSignOutDialog()
        }
        ll_version_info.setOnClickListener {
            currentClickTimestamp = System.currentTimeMillis()
            if (clickCount == 0) {
                lastClickTimestamp = currentClickTimestamp
            }
            if (currentClickTimestamp - lastClickTimestamp < 1000) {
                clickCount++
                lastClickTimestamp = currentClickTimestamp
                if (tv_user_id.visibility == View.VISIBLE) {
//                    if (clickCount >= 10) {
//                        clickCount = 0
//                        activity!!.startActivity(Intent(activity, HideSettingActivity::class.java))
//                    }
                } else {
                    if (clickCount >= 3) {
                        clickCount = 0
                        tv_user_id.visibility = View.VISIBLE
                    }
                }
            } else {
                clickCount = 0
            }
        }
        card_device_status.setOnClickListener {
            startActivity(Intent(activity!!, DeviceStatusActivity::class.java))
        }
        refreshPremiumFlag()
        tv_version_info.text = "${getAppVersionName(activity!!)}(${getAppVersionCode(activity!!)})"
        tv_user_id.text = "ID:${SettingManager.getInstance().userId}"
        tv_copy_right.text =
            "@${TimeUtils.getFormatTime(System.currentTimeMillis(), "yyyy")} Entertech Ltd."
        tv_user_id.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
//        FirebaseAnalytics.getInstance(activity!!).setCurrentScreen(activity!!, "Me界面", null)
        refreshPremiumFlag()
    }

    fun refreshPremiumFlag() {
//        if (SettingManager.getInstance().isPremiumAccessable) {
//            item_membership.setOnItemClick(View.OnClickListener {
////                postButtonEvent(activity!!, "501", "Me Membership")
////                startActivity(Intent(activity, PremiumDoneActivity::class.java))
//            })
//            item_membership.setLabel(getString(R.string.auth_premium))
//        } else {
//            item_membership.setLabel(getString(R.string.auth_basic))
//            item_membership.setOnItemClick(View.OnClickListener {
//                postButtonEvent(activity!!, "501", "Me Membership")
//                startActivity(Intent(activity, PremiumGoActivity::class.java))
//            })
//        }
    }

    fun showSignOutDialog() {
        val dialog = AlertDialog.Builder(activity)
            .setMessage(getString(R.string.logout_tip))
            .setNegativeButton(getString(R.string.CANCEL)) { dialog, which -> }
            .setPositiveButton(getString(R.string.OK)) { dialog, which ->
                SettingManager.getInstance().refreshToken = ""
                SettingManager.getInstance().token = ""
                activity?.finishAffinity()
                startActivity(Intent(activity, LoginActivity::class.java))
            }
            .create()

        dialog.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.messageCode == MessageEvent.MESSAGE_CODE_PURCHASE_DONE) {
            refreshPremiumFlag()
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


}
