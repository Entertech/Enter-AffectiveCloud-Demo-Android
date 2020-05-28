package cn.entertech.affectiveclouddemo.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.entertech.affectiveclouddemo.R
import cn.entertech.affectiveclouddemo.app.SettingManager
import kotlinx.android.synthetic.main.fragment_me.*

class MeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rl_help_center.setOnClickListener {
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigHelpCenter)
            activity!!.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        rl_term_of_use.setOnClickListener {
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigTermsOfUser)
            activity!!.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        rl_privacy.setOnClickListener {
            var uri = Uri.parse(SettingManager.getInstance().remoteConfigPrivacy)
            activity!!.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

}
