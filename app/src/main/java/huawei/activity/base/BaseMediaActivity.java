package huawei.activity.base;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.zxwl.szga.APP;
import com.zxwl.szga.Constant.Constant;
import com.zxwl.szga.util.PreferenceUtil;
import com.zxwl.szga.util.SPUtils;
import com.zxwl.szga.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

import huawei.Constant.IntentConstant;
import huawei.Constant.UIConstants;
import huawei.logic.CallFunc;
import huawei.util.ActivityUtil;
import okhttp3.internal.Util;

/**
 * This class is about base media activity.
 */
public class BaseMediaActivity extends HuaweiBaseActivity implements LocBroadcastReceiver {
    private static final int CALL_CONNECTED = 100;
    private static final int CALL_UPGRADE = 101;
    private static final int HOLD_CALL_SUCCESS = 102;
    private static final int VIDEO_HOLD_CALL_SUCCESS = 103;
    private static final int MEDIA_CONNECTED = 104;
    private static final int BLD_TRANSFER_SUCCESS = 105;
    private static final int BLD_TRANSFER_FAILED = 106;

    protected String mCallNumber;
    protected String mDisplayName;
    protected boolean mIsVideoCall;
    protected int mCallID;
    protected String mConfID;
    protected boolean mIsConfCall;

    protected int mConfToCallHandle;

//    protected SecondDialPlateControl mPlateControl;

    private String[] mActions = new String[]{CustomBroadcastConstants.ACTION_CALL_CONNECTED,
            CustomBroadcastConstants.CALL_MEDIA_CONNECTED,
            CustomBroadcastConstants.CONF_CALL_CONNECTED,
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.CALL_UPGRADE_ACTION,
            CustomBroadcastConstants.HOLD_CALL_RESULT,
            CustomBroadcastConstants.BLD_TRANSFER_RESULT,
            CustomBroadcastConstants.CALL_TRANSFER_TO_CONFERENCE};
    private LinearLayout mSpeakerButton;
    private LinearLayout mUpgradeVideoArea;

    private CallFunc mCallFunc;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEDIA_CONNECTED:
//                    mPlateButton.setVisibility(View.VISIBLE);
                    break;

                case CALL_CONNECTED:
                    //boolean isVideoCall = (boolean) msg.obj;
//                    mAudioAcceptCallArea.setVisibility(View.GONE);
//                    mVideoAcceptCallArea.setVisibility(View.GONE);
//                    mDivertCallArea.setVisibility(View.GONE);

                    if (msg.obj instanceof CallInfo) {
                        final CallInfo callInfo = (CallInfo) msg.obj;

                        //自动重启暂时解决方案
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                if (TsdkManager.getInstance().getCallManager().getCallByCallId(callInfo.getCallID()) == null)
//                                {
//                                    return;
//                                }
//                                if (callInfo.isVideoCall() && mConfToCallHandle == 0) {
//                                    Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                    intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//
//                                    intent.putExtra(UIConstants.CALL_INFO, callInfo);
////                            SPUtils.put(APP.getContext(),UIConstants.CALL_INFO_STR,Utils.Bean2Json(callInfo));
//
//                                    PreferenceUtil.putCallInfo(LocContext.getContext(), callInfo);
//
//                                    ActivityUtil.startActivity(BaseMediaActivity.this, intent);
//                                    finish();
//                                }
//                            }
//                        },2000);
                        if (callInfo.isVideoCall() && mConfToCallHandle == 0) {
                            Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);

                            intent.putExtra(UIConstants.CALL_INFO, callInfo);
//                            SPUtils.put(APP.getContext(),UIConstants.CALL_INFO_STR,Utils.Bean2Json(callInfo));

                            PreferenceUtil.putCallInfo(LocContext.getContext(), callInfo);

                            ActivityUtil.startActivity(BaseMediaActivity.this, intent);
                            finish();
                        }
                    }

                    break;
                case CALL_UPGRADE:
//                    mDialog = DialogUtil.generateDialog(BaseMediaActivity.this, R.string.ntf_upgrade_videocall,
//                            R.string.accept, R.string.reject,
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    cancelDisDiaTimer();
//                                    Executors.newSingleThreadExecutor().execute(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (null == Looper.myLooper()) {
//                                                Looper.prepare();
//                                            }
//                                            //CallMgr.getInstance().agreeUpgradeVideoControl();
//                                            CallMgr.getInstance().acceptAddVideo(mCallID);
//                                        }
//                                    });
//                                }
//                            }, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    cancelDisDiaTimer();
//                                    // CallMgr.getInstance().rejectUpgradeVideo();
//                                    CallMgr.getInstance().rejectAddVideo(mCallID);
//                                }
//                            });
//                    mDialog.show();
//                    startDismissDiaLogTimer();
                    break;
                case HOLD_CALL_SUCCESS: {
//                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
//                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
//                    if ("Hold".equals(mCallNumberTv.getTag())) {
//                        textCallNumber = textCallNumber + "Holding";
//                    }
//                    mCallNameTv.setText(textDisplayName);
//                    mCallNumberTv.setText(textCallNumber);
                }
                break;
                case VIDEO_HOLD_CALL_SUCCESS: {
//                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
//                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
//                    textCallNumber = textCallNumber + "Holding";
//                    mCallNameTv.setText(textDisplayName);
//                    mCallNumberTv.setText(textCallNumber);
//                    mHoldCallText.setText(R.string.un_hold_call);
                }
                break;
                case BLD_TRANSFER_SUCCESS:
                    Toast.makeText(BaseMediaActivity.this, "Blind transfer success", Toast.LENGTH_SHORT).show();
                    break;
                case BLD_TRANSFER_FAILED:
                    Toast.makeText(BaseMediaActivity.this, "Blind transfer failed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void initializeComposition() {

    }

    @Override
    public void initializeData() {
        mCallFunc = CallFunc.getInstance();

        Intent intent = getIntent();
//        CallInfo callInfo = (CallInfo) intent.getSerializableExtra(UIConstants.CALL_INFO);
//
//        if (callInfo == null){
//            callInfo = Utils.jsonResolve((String) SPUtils.get(APP.getContext(),UIConstants.CALL_INFO_STR,null),CallInfo.class);
//        }

        CallInfo callInfo = PreferenceUtil.getCallInfo(LocContext.getContext());

        mCallNumber = callInfo.getPeerNumber();
        mDisplayName = callInfo.getPeerDisplayName();
        mIsVideoCall = callInfo.isVideoCall();
        mCallID = callInfo.getCallID();
        mConfID = callInfo.getConfID();
        mIsConfCall = callInfo.isFocus();
        if ((null != mConfID) && (!callInfo.getConfID().equals(""))) {
            mConfToCallHandle = Integer.parseInt(callInfo.getConfID());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
    }

    protected void dismissDialog(AlertDialog dialog) {
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    @Override
    public void onReceive(final String broadcastName, final Object obj)
    {
        switch (broadcastName)
        {
            case CustomBroadcastConstants.ACTION_CALL_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(CALL_CONNECTED, obj));
                break;
            case CustomBroadcastConstants.CALL_MEDIA_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(MEDIA_CONNECTED, obj));
                break;

            case CustomBroadcastConstants.CONF_CALL_CONNECTED:
                finish();
                break;
            case CustomBroadcastConstants.ACTION_CALL_END:
                finish();
                break;
            case CustomBroadcastConstants.CALL_UPGRADE_ACTION:
                mHandler.sendEmptyMessage(CALL_UPGRADE);
                break;
            case CustomBroadcastConstants.HOLD_CALL_RESULT:
                if ("HoldSuccess".equals(obj))
                {
//                    mCallNumberTv.setTag("Hold");
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                }else if ("UnHoldSuccess".equals(obj))
                {
//                    mCallNumberTv.setTag("UnHold");
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                }else if ("VideoHoldSuccess".equals(obj))
                {
                    mHandler.sendEmptyMessage(VIDEO_HOLD_CALL_SUCCESS);
                }
                break;
            case CustomBroadcastConstants.BLD_TRANSFER_RESULT:
                if ("BldTransferSuccess".equals(obj))
                {
                    mHandler.sendEmptyMessage(BLD_TRANSFER_SUCCESS);
                }
                else if ("BldTransferFailed".equals(obj))
                {
                    mHandler.sendEmptyMessage(BLD_TRANSFER_FAILED);
                }
                break;

            case CustomBroadcastConstants.CALL_TRANSFER_TO_CONFERENCE:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(CallMgr.getInstance().isResumeHold()){
                            CallMgr.getInstance().unHoldCall(CallMgr.getInstance().getOriginal_CallId());
                        }
                    }
                },20000);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
