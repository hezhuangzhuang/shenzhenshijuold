package huawei.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.szga.Constant.Constant;
import com.zxwl.szga.R;
import com.zxwl.szga.bean.MeetingInfo;
import com.zxwl.szga.util.MeetingControl;
import com.zxwl.szga.util.PreferenceUtil;
import com.zxwl.szga.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

import huawei.Constant.IntentConstant;
import huawei.Constant.UIConstants;
import huawei.activity.base.BaseMediaActivity;
import huawei.util.ActivityUtil;
import okhttp3.Call;
import okhttp3.internal.Util;

public class InCallActivity extends BaseMediaActivity {
    private TextView hang_up;
    private TextView answer;
    private TextView tv_number;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isAutoAnswer = mCallNumber.equals(Constant.joinConfAcc);

        if (isAutoAnswer == true) {
            Constant.joinConfAcc = "";
            CallMgr.getInstance().stopPlayRingBackTone();
            CallMgr.getInstance().stopPlayRingingTone();

            setContentView(R.layout.activity_loading);
            ObjectAnimator ra = null;
            ra = ObjectAnimator.ofFloat(findViewById(R.id.iv_loading), "rotation", 0f, 360f);
            ra.setDuration(1500);
            ra.setRepeatCount(ObjectAnimator.INFINITE);
            ra.setInterpolator(new LinearInterpolator());
            ra.start();

            answer();
            return;
        }
        setContentView(R.layout.activity_caller_in);


        initView();
        setListener();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CallMgr.getInstance().stopPlayRingingTone();
                CallMgr.getInstance().stopPlayRingBackTone();

                if (0 == mConfToCallHandle) {
                    CallMgr.getInstance().endCall(mCallID);
                } else {
                    MeetingMgr.getInstance().rejectConf();
                }

                timer.cancel();

                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }, 20000);
    }

    private void initView() {
        hang_up = findViewById(R.id.tv_hang_up);
        answer = findViewById(R.id.tv_answer);
        tv_number = findViewById(R.id.tv_number);

//        tv_number.setText("会议");
        MeetingControl.getCurrMeetingInfo(mCallNumber, new RxSubscriber<BaseData>() {
            @Override
            public void onSuccess(BaseData baseData) {
                if (baseData.getCode() == 0) {
                    MeetingInfo info = Utils.jsonOBJ_Resolve(baseData.getData(), MeetingInfo.class);
                    tv_number.setText(info.getConfName());
                }
            }

            @Override
            protected void onError(ResponeThrowable responeThrowable) {

            }
        });
    }

    private void setListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null){
                    timer.cancel();
                }
                switch (v.getId()) {
                    case R.id.tv_hang_up:
                        hangUp();
                        break;
                    case R.id.tv_answer:
                        answer();
                        break;
                }
            }
        };

        hang_up.setOnClickListener(listener);
        answer.setOnClickListener(listener);
    }

    private void hangUp() {
        if (0 == mConfToCallHandle) {
            CallMgr.getInstance().endCall(mCallID);
            finish();
        } else {
            CallMgr.getInstance().stopPlayRingingTone();
            CallMgr.getInstance().stopPlayRingBackTone();

            MeetingMgr.getInstance().rejectConf();
            finish();
        }
    }

    int tryCount = 0;

    private void answer() {
        final CallInfo callInfo = PreferenceUtil.getCallInfo(LocContext.getContext());
        switch (3) {
            case 1:
                CallMgr.getInstance().endCall(mCallID);
                CallMgr.getInstance().startCall(callInfo.getPeerNumber(), true);

//                if (!"huawei.activity.VideoActivity".endsWith(Utils.getTopActivity(LocContext.getContext()))) {
//                    Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);
//                    intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//                    intent.putExtra(UIConstants.CALL_INFO, callInfo);
//                    PreferenceUtil.putCallInfo(LocContext.getContext(), callInfo);
//                    ActivityUtil.startActivity(InCallActivity.this, intent);
//                }

                break;
            case 2:
                CallMgr.getInstance().answerCall(mCallID, false);
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        boolean result = CallMgr.getInstance().addVideo(mCallID);
                        if (tryCount >= 15 || result) {
                            timer.cancel();
                            tryCount = 0;
                        }
                        tryCount++;
                    }
                }, 1000, 500);
                break;
            case 3:
                if (0 == mConfToCallHandle) {
                    CallMgr.getInstance().answerCall(mCallID, true);

//                    CallMgr.getInstance().stopPlayRingingTone();
//                    CallMgr.getInstance().stopPlayRingBackTone();
//                    CallMgr.getInstance().endCall(mCallID);
//                    CallMgr.getInstance().startCall(callInfo.getPeerNumber(), true);
                } else {
                    CallMgr.getInstance().stopPlayRingingTone();
                    CallMgr.getInstance().stopPlayRingBackTone();

//                    MeetingMgr.getInstance().rejectConf();
//                    CallMgr.getInstance().startCall(("0755666" + callInfo.getPeerNumber()).trim(), true);

                    MeetingMgr.getInstance().acceptConf(true);
//                    finish();

                }
                break;
        }

//        //跳转等待页
//        Intent intent = new Intent(InCallActivity.this, LoadingActivity.class);
//        startActivity(intent);
    }

}
