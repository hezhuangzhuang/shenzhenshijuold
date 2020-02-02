package com.zxwl.szga.util;

import android.content.Intent;
import android.util.Log;

import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.szga.APP;
import com.zxwl.szga.Constant.Constant;
import com.zxwl.szga.Constant.RequestUrls;
import com.zxwl.szga.bean.MeetingInfo;

import java.util.HashMap;
import java.util.Map;

import huawei.Constant.IntentConstant;
import huawei.Constant.UIConstants;
import huawei.activity.InCallActivity;
import huawei.util.ActivityUtil;

/**
 * 会控工具类
 */
public class MeetingControl {


    /**
     * 召集会议
     *
     * @param confName
     * @param duration
     * @param sites
     */
    public static void createMeeting(String confName, String duration, String sites, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("confName", confName);
        params.put("duration", duration);
        params.put("sites", sites);
        params.put("creatorUri", Constant.siteUri);
        params.put("creatorName", Constant.siteName);
        NetWorkUtils.postServerforResult(RequestUrls.createMeeting, params, handler);
    }


    /**
     * 呼叫终端
     *
     * @param smcConfId
     * @param siteUri
     * @param handler
     */
    public static void call(String smcConfId, String siteUri, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", smcConfId);
        params.put("siteUri", siteUri);
        params.put("call", "1");
        NetWorkUtils.postServerforResult(RequestUrls.call_OR_hangUP, params, handler);
    }

    /**
     * 挂断终端
     *
     * @param smcConfId
     * @param siteUri
     * @param handler
     */
    public static void hangUp(String smcConfId, String siteUri, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", smcConfId);
        params.put("siteUri", siteUri);
        params.put("call", "0");
        NetWorkUtils.postServerforResult(RequestUrls.call_OR_hangUP, params, handler);
    }

    /**
     * 静音会场
     *
     * @param smcConfId
     * @param siteUri
     * @param handler
     */
    public static void mute(String smcConfId, String siteUri, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", smcConfId);
        params.put("siteUri", siteUri);
        params.put("mute", "1");
        NetWorkUtils.postServerforResult(RequestUrls.mute, params, handler);
    }

    /**
     * 取消静音会场
     *
     * @param smcConfId
     * @param siteUri
     * @param handler
     */
    public static void cancelmute(String smcConfId, String siteUri, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", smcConfId);
        params.put("siteUri", siteUri);
        params.put("mute", "0");
        NetWorkUtils.postServerforResult(RequestUrls.mute, params, handler);
    }


    /**
     * 移除与会者
     *
     * @param smcConfId
     * @param siteUri
     * @param handler
     */
    public static void delete(String smcConfId, String siteUri, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", smcConfId);
        params.put("siteUri", siteUri);
        NetWorkUtils.deleteServerforResult(RequestUrls.delete, params, handler);
    }

    /**
     * 添加与会人
     *
     * @param smcConfId
     * @param siteUri
     * @param handler
     */
    public static void add(String smcConfId, String siteUri, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", smcConfId);
        params.put("siteUri", siteUri);
        NetWorkUtils.postServerforResult(RequestUrls.add, params, handler);
    }

//    /**
//     * 加入会议
//     *
//     * @param accCode
//     * @param siteUri
//     * @param handler
//     */
//    public static void join(final String accCode, final String siteUri, final RxSubscriber<BaseData> handler) {
////        final Map<String, String> params = new HashMap<>();
////        params.put("accCode", accCode);
////        params.put("siteUri", siteUri);
//
//        //为了兼容多SMC服务端，传入的smcConfId其实是会议接入码
//        MeetingControl.getCurrMeetingInfo(accCode, new RxSubscriber<BaseData>() {
//            @Override
//            public void onSuccess(BaseData baseData) {
//                if (baseData.getCode() == 0) {
//                    //确认会自己的SMC
//                    MeetingInfo info = Utils.jsonOBJ_Resolve(baseData.getData(), MeetingInfo.class);
//                    Map<String, String> params = new HashMap<>();
//                    params.put("accCode", info.getSmcConfId());
//                    params.put("siteUri", siteUri);
//                    NetWorkUtils.postServerforResult(RequestUrls.join, params, handler);
//                } else {
//                    //其他人的SMC
//                    final CallInfo callInfo = PreferenceUtil.getCallInfo(LocContext.getContext());
//                    CallMgr.getInstance().startCall(accCode, true);
//                    Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);
//                    intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//                    intent.putExtra(UIConstants.CALL_INFO, callInfo);
//                    PreferenceUtil.putCallInfo(LocContext.getContext(), callInfo);
//                    ActivityUtil.startActivity(APP.getContext(), intent);
//                }
//            }
//
//            @Override
//            protected void onError(ResponeThrowable responeThrowable) {
//
//            }
//        });
//    }


    /**
     * 加入会议
     *
     * @param smcConfId
     * @param siteUri
     * @param handler
     */
    public static void join(String smcConfId, String siteUri, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", smcConfId);
        params.put("siteUri", siteUri);
        NetWorkUtils.postServerforResult(RequestUrls.join, params, handler);
    }

    /**
     * 结束会议
     *
     * @param smcConfId
     * @param handler
     */
    public static void end(String smcConfId, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", smcConfId);
        NetWorkUtils.deleteServerforResult(RequestUrls.end, params, handler);
    }

    /**
     * 变更扬声器状态
     *
     * @param smcConfId
     * @param siteUri
     * @param handler
     */
    public static void changeLouder(String smcConfId, String siteUri, String quiet, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", smcConfId);
        params.put("siteUri", siteUri);
        params.put("quiet", quiet);
        NetWorkUtils.postServerforResult(RequestUrls.closeLouder, params, handler);
    }

    /**
     * 根据会议接入码获取当前会议详情
     *
     * @param confAccessCode
     * @param handler
     */
    public static void getCurrMeetingInfo(String confAccessCode, RxSubscriber<BaseData> handler) {
        Map<String, String> params = new HashMap<>();
        params.put("confAccessCode", confAccessCode);
        NetWorkUtils.getServerforResult(RequestUrls.getCurrMeetingInfo, params, handler);
    }
}
