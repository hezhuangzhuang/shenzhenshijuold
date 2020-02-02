package huawei.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.szga.APP;
import com.zxwl.szga.Constant.Constant;
import com.zxwl.szga.R;
import com.zxwl.szga.bean.MeetingInfo;
import com.zxwl.szga.util.MeetingControl;
import com.zxwl.szga.util.PreferenceUtil;
import com.zxwl.szga.util.SPUtils;
import com.zxwl.szga.util.Utils;
import com.zxwl.szga.view.PopMeetingControl;

import huawei.Constant.UIConstants;
import huawei.activity.base.BaseLibActivity;
import huawei.logic.CallFunc;
import huawei.util.DensityUtil;
import huawei.util.StatusBarUtils;
import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 视频界面
 */
public class VideoActivity extends BaseLibActivity implements LocBroadcastReceiver, View.OnClickListener, CancelAdapt {
    public static VideoActivity mInstance;
    private TextView tvTopTitle;
    private ImageView ivRightOperate;
    /*顶部按钮--end*/

    /*视频界面--*/
    private FrameLayout mRemoteView;
    private FrameLayout mLocalView;
    private FrameLayout mHideView;
    /*视频界面--end*/

    /*会控按钮--*/
    private TextView tvHangUp;
    private TextView tvMic;
    private TextView tvMute;
    private ImageView meetingControl;
    private ImageView switchVideo;
    private ImageView localVideoState;
    /*会控按钮--end*/

    private static final int ADD_LOCAL_VIEW = 101;

    private String[] mActions = new String[]{
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ADD_LOCAL_VIEW,
            CustomBroadcastConstants.DEL_LOCAL_VIEW
    };

    private CallInfo mCallInfo;
    private int mCallID;
    private Object thisVideoActivity = this;

    private int mCameraIndex = CallConstant.FRONT_CAMERA;

    private CallMgr mCallMgr;
    private CallFunc mCallFunc;
    private MeetingMgr instance;

    /*会控顶部*/
    private ImageView ivBg;
    private RelativeLayout llTopControl;
    private LinearLayout llBottomControl;
    /*会控顶部-end*/

    private boolean showControl = true;//是否显示控制栏
    private boolean mute = false;//true代表静音

    private String currConfID;
    private String createUri;
    private boolean isCreater = false;
    private MeetingInfo info;

    @Override
    protected void findViews() {
        Log.d("123123", "VideoAct");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        int navBarHeight = getNavigationBarHeight(this);
        mInstance = this;
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivRightOperate = (ImageView) findViewById(R.id.iv_right_operate);
        mRemoteView = (FrameLayout) findViewById(R.id.conf_share_layout);
        mLocalView = (FrameLayout) findViewById(R.id.conf_video_small_logo);
        mHideView = (FrameLayout) findViewById(R.id.hide_video_view);
        tvHangUp = (TextView) findViewById(R.id.tv_hang_up);
        tvMic = (TextView) findViewById(R.id.tv_mic);
        tvMute = (TextView) findViewById(R.id.tv_mute);
        meetingControl = findViewById(R.id.meetingControl);
        switchVideo = findViewById(R.id.switchVideo);
        localVideoState = findViewById(R.id.localVideoState);

        llTopControl = (RelativeLayout) findViewById(R.id.ll_top_control);

        llBottomControl = (LinearLayout) findViewById(R.id.ll_bottom_control);

        ivBg = (ImageView) findViewById(R.id.iv_bg);
    }

    @Override
    protected void initData() {
        StatusBarUtils.setTransparent(this);

        ivRightOperate.setVisibility(View.GONE);
        ivRightOperate.setImageResource(R.mipmap.icon_add);

        Intent intent = getIntent();
        //是否是会议
//        mCallInfo = (CallInfo) intent.getSerializableExtra(UIConstants.CALL_INFO);
//
//        if (mCallInfo == null){
//            mCallInfo = Utils.jsonResolve((String) SPUtils.get(APP.getContext(),UIConstants.CALL_INFO_STR,null),CallInfo.class);
//        }
        mCallInfo = PreferenceUtil.getCallInfo(LocContext.getContext());


        this.mCallID = mCallInfo.getCallID();

        mCallMgr = CallMgr.getInstance();
        mCallFunc = CallFunc.getInstance();
        instance = MeetingMgr.getInstance();


//        //是否静音
        setMicStatus();

        initMeetingControl();
    }

    private void initMeetingControl() {
        MeetingControl.getCurrMeetingInfo(mCallInfo.getPeerNumber(), new RxSubscriber<BaseData>() {
            @Override
            public void onSuccess(BaseData baseData) {
                if (baseData.getCode() == 0) {
//                    info = gson.fromJson(gson.toJson(baseData.getData()), MeetingInfo.class);
                    info = Utils.jsonOBJ_Resolve(baseData.getData(), MeetingInfo.class);
                    currConfID = info.getSmcConfId();
                    createUri = info.getCreatorUri();
                    isCreater = Constant.siteUri.equals(createUri);
                    if (isCreater) {
                        meetingControl.setVisibility(View.VISIBLE);
                    } else {
                        meetingControl.setVisibility(View.GONE);
                    }

                    MeetingControl.cancelmute(currConfID, Constant.siteUri, new RxSubscriber<BaseData>() {
                        @Override
                        public void onSuccess(BaseData baseData) {
                            Log.d(Constant.tag, "本地会场已取消静音");
                        }

                        @Override
                        protected void onError(ResponeThrowable responeThrowable) {

                        }
                    });


                } else {
                    Utils.showToast("获取会议详情失败，参数异常");
                }
            }

            @Override
            protected void onError(ResponeThrowable responeThrowable) {
                Utils.showToast("获取会议详情失败，请稍后重试");
            }
        });
    }


    @Override
    protected void setListener() {
        tvHangUp.setOnClickListener(this);
        tvMic.setOnClickListener(this);
        tvMute.setOnClickListener(this);

        ivRightOperate.setOnClickListener(this);
        ivBg.setOnClickListener(this);
        meetingControl.setOnClickListener(this);
        switchVideo.setOnClickListener(this);
        mLocalView.setOnClickListener(this);
        localVideoState.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conf_new;
    }

    @Override
    protected void onResume() {
        super.onResume();

        showControl = llBottomControl.getVisibility() == View.VISIBLE && llTopControl.getVisibility() == View.VISIBLE;

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
        addSurfaceView(false);

        //是否开启画面自动旋转
        setAutoRotation(this, true, "148");

        huaweiOpenSpeaker();

        //如果不是扬声器则切换成扬声器
        setLoudSpeaker();

//        //设置扬声器的状态
        setSpeakerStatus();

        setLocalView();
    }

    private void setLocalView() {
        int virtualBarHeigh = 0;
        virtualBarHeigh = DensityUtil.getNavigationBarHeight(this) + DensityUtil.dip2px(20);
        //显示
        mLocalView.animate().translationX(0 - virtualBarHeigh).setDuration(100).start();
        localVideoState.animate().translationX(0 - virtualBarHeigh).setDuration(100).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInstance = null;
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        mHandler.removeCallbacksAndMessages(null);
        setAutoRotation(this, false, "163");
//        mCallMgr.endCall(mCallID);
    }


    /**
     * 设置为扬声器
     */
    public void setLoudSpeaker() {
        //获取扬声器状态
        //如果不是扬声器则切换成扬声器
        if ((CallConstant.TYPE_LOUD_SPEAKER != CallMgr.getInstance().getCurrentAudioRoute())) {
            CallMgr.getInstance().switchAudioRoute();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_LOCAL_VIEW:
                    addSurfaceView(true);
//                    setAutoRotation(thisVideoActivity, true, "184");
                    setAutoRotation(thisVideoActivity, true, "184");
                    break;

                default:
                    break;
            }
        }
    };

    boolean micState = false;

    /**
     * 更新状态
     */
    public void muteMic() {
        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        if (CallMgr.getInstance().muteMic(mCallID, !currentMuteStatus)) {
            mCallFunc.setMuteStatus(!currentMuteStatus);
            setMicStatus();
        }
    }

    private void setMicStatus() {

        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        //更新状态静音按钮状态
        tvMic.setCompoundDrawablesWithIntrinsicBounds(0, currentMuteStatus ? R.mipmap.icon_mic_close : R.mipmap.icon_mic, 0, 0);
    }

    /**
     * 设置扬声器的图片
     */
    private void setSpeakerStatus() {
        tvMute.setCompoundDrawablesWithIntrinsicBounds(0, mute ? R.mipmap.icon_mute : R.mipmap.icon_unmute, 0, 0);
    }

    public void videoToAudio() {
        CallMgr.getInstance().delVideo(mCallID);
    }

    public void holdVideo() {
        CallMgr.getInstance().holdVideoCall(mCallID);
    }

    public void videoDestroy() {
        if (null != CallMgr.getInstance().getVideoDevice()) {
            LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed destroy.");
            CallMgr.getInstance().videoDestroy();
        }
    }

    public void switchCamera() {
        mCameraIndex = CallConstant.FRONT_CAMERA == mCameraIndex ?
                CallConstant.BACK_CAMERA : CallConstant.FRONT_CAMERA;
        CallMgr.getInstance().switchCamera(mCallID, mCameraIndex);
    }

    public void switchCameraStatus(boolean isCameraClose) {
        if (isCameraClose) {
            CallMgr.getInstance().closeCamera(mCallID);
        } else {
            CallMgr.getInstance().openCamera(mCallID);
        }
    }

    public SurfaceView getHideVideoView() {
        return VideoMgr.getInstance().getLocalHideView();
    }

    public SurfaceView getLocalVideoView() {
        return VideoMgr.getInstance().getLocalVideoView();
    }

    public SurfaceView getRemoteVideoView() {
        return VideoMgr.getInstance().getRemoteVideoView();
    }

    public void setAutoRotation(Object object, boolean isOpen, String line) {
        LogUtil.i(UIConstants.DEMO_TAG, "setAutoRotation-->" + line);
//        VideoMgr.getInstance().setAutoRotation(object, isOpen, 1);
        VideoMgr.getInstance().setAutoRotation(object, isOpen, 2);
    }

    private void addSurfaceView(ViewGroup container, SurfaceView child) {
        if (child == null) {
            return;
        }
        if (child.getParent() != null) {
            ViewGroup vGroup = (ViewGroup) child.getParent();
            vGroup.removeAllViews();
        }
        container.addView(child);
    }

    private void addSurfaceView(boolean onlyLocal) {
        if (!onlyLocal) {
            addSurfaceView(mRemoteView, getRemoteVideoView());
        }
        addSurfaceView(mLocalView, getLocalVideoView());
        addSurfaceView(mHideView, getHideVideoView());
    }

    /**
     * On call closed.
     */
    private void callClosed() {
        LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed enter.");
//        executorShutDown();
        videoDestroy();
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.ACTION_CALL_END:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callClosed();
                        finish();
                    }
                });
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                mHandler.sendEmptyMessage(ADD_LOCAL_VIEW);
                break;

            case CustomBroadcastConstants.DEL_LOCAL_VIEW:
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.tv_hang_up == v.getId()) {
            Constant.selfEndConf = true;
            //结束会议
            if (isCreater) {
                Utils.makeDialog(VideoActivity.this, "提示", "您需要离开会议还是结束会议？", "离开会议", "结束会议", new DialogInterface.OnClickListener() {
                    //离开会议
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallMgr.endCall(mCallID);
                    }
                }, new DialogInterface.OnClickListener() {
                    //结束会议
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MeetingControl.end(currConfID, new RxSubscriber<BaseData>() {
                            @Override
                            public void onSuccess(BaseData baseData) {
                                if (baseData.getCode() == 0) {
                                    Utils.showToast("会议已结束");
                                } else {
                                    if (TextUtils.isEmpty(baseData.getMsg())) {
                                        Utils.showToast("结束会议失败，服务器内部错误");
                                    } else {
                                        Utils.showToast(baseData.getMsg());
                                    }
                                }
                            }

                            @Override
                            protected void onError(ResponeThrowable responeThrowable) {
                                Utils.showToast("结束会议异常，请稍后重试");
                            }
                        });
                    }
                });
            } else {
                mCallMgr.endCall(mCallID);
            }

        } else if (R.id.tv_mute == v.getId()) {
            //是否静音喇叭
            //是否静音喇叭
            if (mute) {
                huaweiOpenSpeaker();
            } else {
                huaweiCloseSpeaker();
            }
//            if (isOpenSpeaker()) {
//                CloseSpeaker();
//            } else {
//                OpenSpeaker();
//            }
        } else if (R.id.tv_mic == v.getId()) {
            //静音
            muteMic();
        } else if (R.id.iv_bg == v.getId()) {
            if (showControl) {
                hideControl();
            } else {
                showControl();
            }
        } else if (R.id.meetingControl == v.getId()) {
            PopMeetingControl popMeetingControl = new PopMeetingControl(VideoActivity.this, String.valueOf(currConfID));
            popMeetingControl.show();
        } else if (R.id.switchVideo == v.getId()) {
            switchCameraState();
        } else if (R.id.conf_video_small_logo == v.getId()) {
            switchVideo();
        } else if(R.id.localVideoState == v.getId()){
            if (videoSwitch){
                //如果切换过远近端显示，则先重置屏幕
                switchVideo();
            }
            if (mLocalView.getVisibility() == View.VISIBLE){
                mLocalView.setVisibility(View.INVISIBLE);
                VideoMgr.getInstance().getLocalVideoView().setVisibility(View.INVISIBLE);
                localVideoState.setImageResource(R.drawable.icon_window_open);
            }else {
                mLocalView.setVisibility(View.VISIBLE);
                VideoMgr.getInstance().getLocalVideoView().setVisibility(View.VISIBLE);
                localVideoState.setImageResource(R.drawable.icon_window_close);
            }
        }
    }

    boolean cameraSwitch = true;

    private void switchCameraState() {
        if (cameraSwitch) {
            CallMgr.getInstance().closeCamera(mCallID);
            switchVideo.setImageResource(R.drawable.ic_camera_on);
        } else {
            CallMgr.getInstance().openCamera(mCallID);
            switchVideo.setImageResource(R.drawable.ic_camera_off);
        }
        cameraSwitch = !cameraSwitch;
    }

    boolean videoSwitch = false;

    /**
     * 远端和近端画面
     */
    private void switchVideo() {
        mRemoteView.removeAllViews();
        mLocalView.removeAllViews();
        if (videoSwitch) {
            VideoMgr.getInstance().getRemoteVideoView().setZOrderMediaOverlay(false);
            VideoMgr.getInstance().getLocalVideoView().setZOrderMediaOverlay(true);

            addSurfaceView(mRemoteView, VideoMgr.getInstance().getRemoteVideoView());
            addSurfaceView(mLocalView, VideoMgr.getInstance().getLocalVideoView());
        } else {
            VideoMgr.getInstance().getRemoteVideoView().setZOrderMediaOverlay(true);
            VideoMgr.getInstance().getLocalVideoView().setZOrderMediaOverlay(false);

            addSurfaceView(mLocalView, VideoMgr.getInstance().getRemoteVideoView());
            addSurfaceView(mRemoteView, VideoMgr.getInstance().getLocalVideoView());
        }
        videoSwitch = !videoSwitch;
    }

//    /**
//     * true代表静音
//     *
//     * @return
//     */
//    private boolean isMuteSpeakStatus() {
//        if (0 != mCallID) {
//            return CallMgr.getInstance().getMuteSpeakStatus(mCallID);
//        } else {
//            return false;
//        }
//    }

    /**
     * 扬声器静音
     */
    private void huaweiCloseSpeaker() {

        TsdkManager.getInstance().getCallManager().setSpeakVolume(0);

        if (0 != mCallID) {
            boolean muteSpeak = CallMgr.getInstance().muteSpeak(mCallID, true);

            CloseSpeaker();

//            TsdkManager.getInstance().getCallManager().setSpeakVolume(0);

//            MeetingControl.changeLouder(currConfID, Constant.siteUri, "1", new RxSubscriber<BaseData>() {
//                @Override
//                public void onSuccess(BaseData baseData) {
//                    Log.d(Constant.tag,"关闭扬声器");
//                }
//
//                @Override
//                protected void onError(ResponeThrowable responeThrowable) {
//
//                }
//            });

            if (muteSpeak) {
                mute = true;
            } else {
                mute = false;
            }
//            CallMgr.getInstance().muteSpeak(mCallID, muteSpeak);
        }

        setSpeakerStatus();
    }


    private void huaweiOpenSpeaker() {

        if (0 != mCallID) {
            boolean muteSpeak = CallMgr.getInstance().muteSpeak(mCallID, false);

            OpenSpeaker();
//            TsdkManager.getInstance().getCallManager().setSpeakVolume(60);

            //CallMgr.getInstance().switchAudioRoute();

//            MeetingControl.changeLouder(currConfID, Constant.siteUri, "0", new RxSubscriber<BaseData>() {
//                @Override
//                public void onSuccess(BaseData baseData) {
//                    Log.d(Constant.tag,"关闭扬声器");
//                }
//
//                @Override
//                protected void onError(ResponeThrowable responeThrowable) {
//
//                }
//            });
//            CallMgr.getInstance().setMuteSpeakStatus(mCallID, muteSpeak);

            if (muteSpeak) {
                mute = false;
            } else {
                mute = true;
            }
        }

        setSpeakerStatus();
    }


    private void showControl() {
        llTopControl.setVisibility(View.VISIBLE);
        getViewAlphaAnimator(llTopControl, 1).start();
        llBottomControl.setVisibility(View.VISIBLE);
        getViewAlphaAnimator(llBottomControl, 1).start();
        switchVideo.setVisibility(View.VISIBLE);
        getViewAlphaAnimator(switchVideo, 1).start();
        if (isCreater) {
            meetingControl.setVisibility(View.VISIBLE);
            getViewAlphaAnimator(meetingControl, 1).start();
        }
    }

    private void hideControl() {
        getViewAlphaAnimator(llBottomControl, 0).start();
        getViewAlphaAnimator(llTopControl, 0).start();

        if (isCreater) {
            getViewAlphaAnimator(meetingControl, 0).start();
        }
        getViewAlphaAnimator(switchVideo, 0).start();

    }

    private ViewPropertyAnimator getViewAlphaAnimator(final View view, final float alpha) {
        ViewPropertyAnimator viewPropertyAnimator = view.animate().alpha(alpha).setDuration(300);
        viewPropertyAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(alpha > 0 ? View.VISIBLE : View.GONE);
                showControl = alpha > 0 ? true : false;
            }
        });
        return viewPropertyAnimator;
    }

    /*添加会场*/
    private BottomSheetDialog mBottomSheetDialog;
    private TextView tvAddCancle;
    private TextView tvAddConfirm;

    private RecyclerView rvAddAttendees;
//    private AddSiteAdapter addSiteAdapter;
    /*添加会场--end*/

    /**
     * 判断扬声器是否打开
     *
     * @return
     */
    private boolean isOpenSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            return audioManager.isSpeakerphoneOn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    int currVolume = 20;

    public void OpenSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        currVolume,
                        AudioManager.STREAM_VOICE_CALL);
                tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_unmute, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭扬声器
    public void CloseSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);

                    tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_mute, 0, 0);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
//            Log.d(TAG, "onKeyDown()");
        }

        return super.onKeyDown(keyCode, event);
    }
}
