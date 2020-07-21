package cn.entertech.flowtimezh.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.entertech.flowtimezh.wxapi.uikit.MessageEvent;
import cn.entertech.flowtimezh.wxapi.uikit.NetworkUtil;

import static cn.entertech.flowtimezh.app.Constant.WX_LOGIN_APP_ID;
import static cn.entertech.flowtimezh.app.Constant.WX_LOGIN_APP_SECRET;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static String TAG = "MicroMsg.WXEntryActivity";

    private IWXAPI api;
    private MyHandler handler;

    private class MyHandler extends Handler {
        private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;

        String accessToken;
        String openId;

        public MyHandler(WXEntryActivity wxEntryActivity) {
            wxEntryActivityWeakReference = new WeakReference<WXEntryActivity>(wxEntryActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            int tag = msg.what;
            switch (tag) {
                case NetworkUtil.GET_TOKEN: {
                    Bundle data = msg.getData();
                    JSONObject json = null;
                    try {
                        json = new JSONObject(data.getString("result"));
                        String refreshToken, scope;
                        openId = json.getString("openid");
                        accessToken = json.getString("access_token");
                        NetworkUtil.sendWxAPI(this, String.format("https://api.weixin.qq.com/sns/userinfo?" +
                                "access_token=%s&openid=%s", accessToken, openId
                        ), NetworkUtil.GET_INFO);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                case NetworkUtil.GET_INFO:
                    Bundle data = msg.getData();
                    JSONObject json = null;
                    try {
                        json = new JSONObject(data.getString("result"));
                        String nickname, headimgurl;
                        nickname = json.getString("nickname");
                        headimgurl = json.getString("headimgurl");
                        Logger.d("wx login success:" + accessToken + "::" + nickname + "::" + headimgurl);
                        MessageEvent messageEvent = new MessageEvent();
                        messageEvent.setWXAcessToken(accessToken);
                        messageEvent.setWXImage(headimgurl);
                        messageEvent.setWXName(nickname);
                        messageEvent.setWXUserId(openId);
                        messageEvent.setMessageCode(111);
                        EventBus.getDefault().post(messageEvent);
                        WXEntryActivity.this.finish();
                    } catch (JSONException e) {
                        WXEntryActivity.this.finish();
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                default:
                    WXEntryActivity.this.finish();
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WX_LOGIN_APP_ID, false);
        handler = new MyHandler(this);

        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                    SendAuth.Resp authResp = (SendAuth.Resp) resp;
                    final String code = authResp.code;
                    NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                                    "appid=%s&secret=%s&code=%s&grant_type=authorization_code", WX_LOGIN_APP_ID,
                            WX_LOGIN_APP_SECRET, code), NetworkUtil.GET_TOKEN);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}