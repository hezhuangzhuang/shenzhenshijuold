package huawei.logic;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.loginmgr.ILoginEventNotifyUI;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.zxwl.szga.util.Utils;

import huawei.Constant.UIConstants;


public class LoginFunc implements ILoginEventNotifyUI, LocBroadcastReceiver
{
    private static final int LOGIN_SUCCESS = 100;
    private static final int LOGIN_FAILED = 101;
    private static final int LOGOUT = 102;
    private static final int FIREWALL_DETECT_FAILED = 103;
    private static final int BUILD_STG_FAILED = 104;

    private static LoginFunc INSTANCE = new LoginFunc();

    private String[] broadcastNames = new String[]{CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT};

    private LoginFunc()
    {
        LocBroadcast.getInstance().registerBroadcast(this, broadcastNames);
    }

    public static ILoginEventNotifyUI getInstance()
    {
        return INSTANCE;
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            LogUtil.i(UIConstants.DEMO_TAG, "what:" + msg.what);
            parallelHandleMessage(msg);
        }
    };

    private void sendHandlerMessage(int what, Object object)
    {
        if (mMainHandler == null)
        {
            return;
        }
        Message msg = mMainHandler.obtainMessage(what, object);
        mMainHandler.sendMessage(msg);
    }

    @Override
    public void onLoginEventNotify(LoginConstant.LoginUIEvent evt, int reason, String description)
    {
        switch (evt)
        {
            case VOIP_LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success");
                sendHandlerMessage(LOGIN_SUCCESS, description);
                break;
            case LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login fail");
                sendHandlerMessage(LOGIN_FAILED, description);
                break;
            case FIREWALL_DETECT_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "firewall detect fail");
                sendHandlerMessage(FIREWALL_DETECT_FAILED, description);
                break;
            case BUILD_STG_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "build stg fail");
                sendHandlerMessage(BUILD_STG_FAILED, description);
                break;

            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout");
                sendHandlerMessage(LOGOUT, description);
                break;
            default:
                break;
        }
    }

    /**
     * handle message
     * @param msg
     */
    private void parallelHandleMessage(Message msg)
    {
        switch (msg.what)
        {
            case LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success,notify UI!");
                Utils.showToast("华为平台登录成功");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_SUCCESS, null);
                break;
            case LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login failed,notify UI!");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_FAILED, null);
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout success,notify UI!");
//                ActivityStack.getIns().popupAbove(LoginActivity.class);
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGOUT, null);
//                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case FIREWALL_DETECT_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "firewall detect failed,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case BUILD_STG_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "build stg failed,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT:
//                List<TsdkContactsInfo> selfInfo = (List<TsdkContactsInfo>) obj;
//                TsdkContactsInfo contactInfo = selfInfo.get(0);
//
//                LoginMgr.getInstance().setSelfInfo(contactInfo);
//                if (null != contactInfo.getTerminal() && !contactInfo.getTerminal().equals("")) {
//                    LoginMgr.getInstance().setTerminal(contactInfo.getTerminal());
//                }
//                else {
//                    LoginMgr.getInstance().setTerminal(contactInfo.getTerminal2());
//                }
                break;
            default:
                break;
        }
    }
}
