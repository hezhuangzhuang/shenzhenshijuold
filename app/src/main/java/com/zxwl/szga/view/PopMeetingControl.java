package com.zxwl.szga.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.szga.Constant.Constant;
import com.zxwl.szga.Constant.RequestUrls;
import com.zxwl.szga.R;
import com.zxwl.szga.adapter.MeetingControlListAdapter;
import com.zxwl.szga.bean.MeetingInfo;
import com.zxwl.szga.bean.SiteStatus;
import com.zxwl.szga.util.MeetingControl;
import com.zxwl.szga.util.NetWorkUtils;
import com.zxwl.szga.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会控popwindow
 */
public class PopMeetingControl {
    private Gson gson;
    private PopupWindow popupWindow;
    private RecyclerView recycleListView;
    private TextView add;
    private MeetingControlListAdapter adapter;
    private List<SiteStatus> data;
    private String currsmcConfId;
    private Activity context;

    public PopMeetingControl(Activity context, String currsmcConfId) {
        gson = new Gson();
        this.currsmcConfId = currsmcConfId;
        this.context = context;

        data = new ArrayList<>();

        initPopwindow();
    }

    /**
     * 弹出会议控制选项菜单
     */
    public void show() {
        Map<String, String> params = new HashMap<>();
        params.put("smcConfId", currsmcConfId);
        NetWorkUtils.getServerforResult(RequestUrls.getMeetingInfo, params, new RxSubscriber<BaseData>() {
            @Override
            public void onSuccess(BaseData baseData) {
//                String str = "{\"code\":0,\"msg\":\"success\",\"data\":{\"smcConfId\":\"1234\",\"confName\":\" APP_测试会议\",\"confStatus\":3,\"creatorUri\":null,\"creatorName\":null,\"beginTime\":\"2019-05-16 15:36:03\",\"endTime\":\"2019-05-16 17:36:03\",\"accessCode\":\"900002710015\",\"siteStatusInfoList\":[{\"siteUri\":\"027991\",\"siteName\":\"027991\",\"siteType\":0,\"siteStatus\":2,\"microphoneStatus\":0,\"loudspeakerStatus\":0},{\"siteUri\":\"027992\",\"siteName\":\"027992\",\"siteType\":0,\"siteStatus\":3,\"microphoneStatus\":0,\"loudspeakerStatus\":0}]}}";
//                baseData = gson.fromJson(str, BaseData.class);

                //以下为正常解析逻辑
                if (baseData.getCode() == 0) {
                    //获取与会人列表
//                    MeetingInfo info = gson.fromJson(gson.toJson(baseData.getData()), MeetingInfo.class);
                    MeetingInfo info = Utils.jsonOBJ_Resolve(baseData.getData(),MeetingInfo.class);
                    Log.d(Constant.tag, "与会人列表：" + info.getSiteStatusInfoList().size());

                    //移除会议创建者自己
                    List<SiteStatus> list = info.getSiteStatusInfoList();
                    SiteStatus self = null;
                    for(SiteStatus temp : list){
                        if (temp.getSiteUri().equals(LoginMgr.getInstance().getSipNumber())){
                            self = temp;
                        }
                    }
                    list.remove(self);

                    //更新列表状态
                    data.clear();
                    data.addAll(list);

                    //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
                    if (!popupWindow.isShowing()) {
                        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                    }

                    adapter.notifyDataSetChanged();

                } else {
                    Utils.showToast("会控数据请求失败");
                }
            }

            @Override
            protected void onError(ResponeThrowable responeThrowable) {
                Utils.showToast("会控数据请求失败");
            }
        });
    }

    /**
     * 创建popwindow
     */
    private void initPopwindow() {
        //配置弹出框的组件
        if (popupWindow == null) {
            //初始化pop
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vPopupWindow = inflater.inflate(R.layout.popupwindow_control, null, false);

            //增加与会人
            add = vPopupWindow.findViewById(R.id.add2Meeting);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    PopAddMeetingNumber popAddMeetingNumber = new PopAddMeetingNumber(context, currsmcConfId);
                    popAddMeetingNumber.show();
                }
            });

            //会控列表
            recycleListView = vPopupWindow.findViewById(R.id.number_list);
            recycleListView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MeetingControlListAdapter(R.layout.list_control_item, data);

            //配置会控按钮点击事件
            adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    switch (view.getId()) {
                        case R.id.call:
//                            Utils.showToast("call" + position);
                            if (SiteStatus.STATE_ONLINE.equals(data.get(position).getSiteStatus())) {
                                Utils.showToast("挂断会场...");
                                hangup(data.get(position).getSiteUri());
                            } else {
                                Utils.showToast("开始呼叫...");
                                call(data.get(position).getSiteUri());
                            }
                            break;

                        case R.id.delete:
                            Utils.showToast("开始删除参会人员...");
                            delete(data.get(position).getSiteUri());
                            break;

                        case R.id.mute:
//                            Utils.showToast("mute" + position);
                            if (SiteStatus.STATE_ONLINE.equals(data.get(position).getSiteStatus())) {
                                if (SiteStatus.STATE_MUTE.equals(data.get(position).getMicrophoneStatus())) {
                                    Utils.showToast("取消静音会场...");
                                    cancelmute(data.get(position).getSiteUri());
                                } else {
                                    Utils.showToast("静音会场...");
                                    mute(data.get(position).getSiteUri());
                                }
                            }
                            break;
                    }
                }
            });
            recycleListView.setAdapter(adapter);
            //引入弹窗布局
            popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, Utils.getScreenHight() / 2, true);
        }
    }

//    /**
//     * 通用事件处理
//     */
//    RxSubscriber<BaseData> commonHandler = new RxSubscriber<BaseData>() {
//        @Override
//        public void onSuccess(BaseData baseData) {
//            if (baseData.getCode() == 0) {
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //请求操作成功，刷新数据列表
//                        show();
//                    }
//                }, 1500);
//
//            } else {
//                Utils.showToast("会控请求失败");
//            }
//        }
//
//        @Override
//        protected void onError(ResponeThrowable responeThrowable) {
//            Utils.showToast("会控请求失败");
//        }
//    };

    /**
     * 拨打电话
     *
     * @param siteurl
     */
    private void call(String siteurl) {
//        MeetingControl.call(currsmcConfId, siteurl, commonHandler);
        MeetingControl.call(currsmcConfId, siteurl, new CommonHandler());
    }

    /**
     * 挂断
     *
     * @param siteurl
     */
    private void hangup(String siteurl) {
//        MeetingControl.hangUp(currsmcConfId, siteurl, commonHandler);
        MeetingControl.hangUp(currsmcConfId, siteurl, new CommonHandler());
    }

    /**
     * 移除与会人
     *
     * @param siteurl
     */
    private void delete(String siteurl) {
//        MeetingControl.delete(currsmcConfId, siteurl, commonHandler);
//        if (siteurl.equals(Constant.siteUri)) {
//            Utils.showToast("无法将自己移除会场");
//            return;
//        }
        MeetingControl.delete(currsmcConfId, siteurl, new CommonHandler());
    }

    /**
     * 静音会场
     *
     * @param siteurl
     */
    private void mute(String siteurl) {
//        MeetingControl.mute(currsmcConfId, siteurl, commonHandler);
        MeetingControl.mute(currsmcConfId, siteurl, new CommonHandler());
    }

    /**
     * 取消静音会场
     *
     * @param siteurl
     */
    private void cancelmute(String siteurl) {
        MeetingControl.cancelmute(currsmcConfId, siteurl, new CommonHandler());
    }


    /**
     * 会控通用事件处理
     */
    class CommonHandler extends com.zxwl.network.callback.RxSubscriber {

        @Override
        protected void onError(ResponeThrowable responeThrowable) {
            responeThrowable.printStackTrace();
            Utils.showToast("会控请求失败，请稍后重试");
        }

        @Override
        public void onSuccess(Object o) {
//            com.zxwl.network.bean.BaseData baseData = gson.fromJson(gson.toJson(o), com.zxwl.network.bean.BaseData.class);
            com.zxwl.network.bean.BaseData baseData = Utils.jsonOBJ_Resolve(o,com.zxwl.network.bean.BaseData.class);
            if (baseData.getCode() == 0) {
                popupWindow.dismiss();
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //请求操作成功，刷新数据列表
//                        show();
//                    }
//                }, 1500);
            } else {
                Utils.showToast("会控请求失败，参数异常");
            }
        }
    }
}
