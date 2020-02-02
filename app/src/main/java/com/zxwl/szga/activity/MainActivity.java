package com.zxwl.szga.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.loginmgr.LoginParam;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.szga.Constant.Constant;
import com.zxwl.szga.Constant.RequestUrls;
import com.zxwl.szga.R;
import com.zxwl.szga.adapter.MeetingListAdapter;
import com.zxwl.szga.bean.AccountInfo;
import com.zxwl.szga.bean.MeetingInfo;
import com.zxwl.szga.bean.SiteStatus;
import com.zxwl.szga.util.MeetingControl;
import com.zxwl.szga.util.NetWorkUtils;
import com.zxwl.szga.util.Rom;
import com.zxwl.szga.util.SPUtils;
import com.zxwl.szga.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huawei.Constant.UIConstants;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements LocBroadcastReceiver {
    private LinearLayout back;
    private RecyclerView meeting_list;
    private RefreshLayout mRefreshLayout;
    private MeetingListAdapter adapter;
    private List<MeetingInfo> data;
    private Context context;
    private Gson gson;
    private Intent intent;

    private String[] mActions = new String[]{CustomBroadcastConstants.LOGIN_SUCCESS, CustomBroadcastConstants.LOGIN_FAILED,
            CustomBroadcastConstants.LOGOUT};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        gson = new Gson();
        data = new ArrayList<>();

        //获取启动参数
        intent = getIntent();

        initView();
        setclick();
        initList();

        requestPermissions();

        Utils.showProgressDialog(MainActivity.this);

        initResFile();

        //注册登陆状态广播监听
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
        initDatefromHost();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(LoginMgr.getInstance().getSipNumber())) {
            updataDate();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.hideProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.hideProgress();
        try {
            //退出插件，登出华为
            LoginMgr.getInstance().logout();
            LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出资源文件（针对部分机型的容错机制）
     */
    private void initResFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String CameraBlackpath = Environment.getExternalStorageDirectory().toString() + "/CameraBlack.BMP";
                String ringbackpath = Environment.getExternalStorageDirectory().toString() + "/ring_back.wav";
                String ringingpath = Environment.getExternalStorageDirectory().toString() + "/ringing.wav";
                if (!Utils.isFileExists(CameraBlackpath) || !Utils.isFileExists(ringbackpath) || !Utils.isFileExists(ringingpath)) {
                    Utils.AssetsToSD(context, "CameraBlack.BMP", CameraBlackpath);
                    Utils.AssetsToSD(context, "ring_back.wav", ringbackpath);
                    Utils.AssetsToSD(context, "ringing.wav", ringingpath);
                }


            }
        }).start();
    }

    /**
     * 申请权限
     */
    private void requestPermissions() {
        EasyPermissions.requestPermissions(
                MainActivity.this,
                "申请权限",
                0,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 通过宿主app传递的参数进行初始化
     */
    private void initDatefromHost() {
        //解析从宿主app传递的信息
        final Intent intent = getIntent();
        //获取鉴权信息
        String appPackageName = intent.getStringExtra("appPackageName");
        String secretKey = intent.getStringExtra("secretKey");
        String backServerUrl = intent.getStringExtra("backServerUrl");

        if (TextUtils.isEmpty(backServerUrl)) {
            Utils.showToast("请使用深云2.0启动插件");
            finish();
            return;
        }

//        if (!backServerUrl.endsWith("/")) {
//            backServerUrl = backServerUrl + "/";
//        }

        Constant.SERVER_URL = backServerUrl;

        Map<String, String> params = new HashMap<>();

        params.put("appPackageName", appPackageName);
        params.put("secretKey", secretKey);

        NetWorkUtils.getServerforResult(RequestUrls.authentication, params, new RxSubscriber<BaseData>() {
            @Override
            public void onSuccess(BaseData baseData) {
//                String str = "{\"code\":0,\"msg\":\"success\",\"data\":null}";
//                baseData = gson.fromJson(str, BaseData.class);

                if (baseData.getCode() == 0) {
                    //鉴权成功，开始登陆华为平台并加载数据
                    HuaweiLogin();

                } else {
                    Utils.hideProgress();
                    finish();
                    if (TextUtils.isEmpty(baseData.getMsg())) {
                        Utils.showToast("插件鉴权失败，服务器内部错误");
                    } else {
                        Utils.showToast(baseData.getMsg());
                    }
                }
            }

            @Override
            protected void onError(ResponeThrowable responeThrowable) {
                if (Rom.isMiui() && Boolean.TRUE.equals(SPUtils.get(context, "PermissionDialog", true))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("请授予后台弹出弹窗界面和读写手机储存权限已保证插件的正常运行");
                    builder.setNegativeButton("已经设置，不再提示", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SPUtils.put(context, "PermissionDialog", false);
                            System.exit(0);
                        }
                    });
                    builder.setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Rom.MIUI_openAppPermission(MainActivity.this, getPackageName());
                            System.exit(0);
                        }
                    });
                    builder.show();
                } else {
                    Utils.hideProgress();
                    finish();
                }
                Utils.showToast("插件鉴权失败，请稍后重试");
            }
        });
    }

    /**
     * 登录华为账号
     */
    private void HuaweiLogin() {
        Intent intent = getIntent();

        //用户账号，同时也是uuid
        final String userName = intent.getStringExtra("userName");
        //用户显示名称
        String displayName = intent.getStringExtra("displayName");
        final String password = intent.getStringExtra("password");
        String UserTicket = intent.getStringExtra("UserTicket");
        final String huaweiServerUrl = intent.getStringExtra("huaweiServerUrl");
        final String huaweiServerPort = intent.getStringExtra("huaweiServerPort");

        Constant.siteUri = userName;
        Constant.siteName = displayName;

        final LoginParam loginParam = new LoginParam();

        if (!TextUtils.isEmpty(password)) {
            //账号密码型
            loginParam.setServerUrl(huaweiServerUrl);
            loginParam.setServerPort(Integer.parseInt(huaweiServerPort));
            loginParam.setUserName(userName);
            loginParam.setPassword(password);

            loginParam.setVPN(false);

            LoginMgr.getInstance().login(loginParam);
        } else {
            //单点模式暂不可用，使用替换方法
            if (false) {
                //使用向后台服务器请求账号密码，备用方案
                Map<String, String> params = new HashMap<>();
                params.put("uuid", userName);
                params.put("token", UserTicket);
                NetWorkUtils.getServerforResult(RequestUrls.getUserAndPwd, params, new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (baseData.getCode() == 0) {
                            AccountInfo accountInfo = Utils.jsonOBJ_Resolve(baseData.getData(), AccountInfo.class);

                            LoginParam loginParam = new LoginParam();
                            loginParam.setServerUrl(huaweiServerUrl);
                            loginParam.setServerPort(Integer.parseInt(huaweiServerPort));
                            loginParam.setUserName(accountInfo.getAccount());
                            loginParam.setUserName(accountInfo.getPassword());
                            //临时调试数据
//                            loginParam.setPassword("Huawei12#$");

                            loginParam.setVPN(false);
                            LoginMgr.getInstance().login(loginParam);
                        } else {
                            Utils.showToast("请求账户信息错误，服务器返回异常");
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        Utils.showToast("请求账户信息错误，请稍候重试");
                        handlerHuaweiLogingFail();
                    }
                });
            } else {
                //SSO单点登陆型
                loginParam.setServerUrl(huaweiServerUrl);
                loginParam.setServerPort(Integer.parseInt(huaweiServerPort));
                loginParam.setUserName(userName);
                loginParam.setPassword("");

                loginParam.setVPN(false);

                LoginMgr.getInstance().login(loginParam, UserTicket);
            }
        }
    }

    /**
     * 处理华为登录失败事件
     */
    private void handlerHuaweiLogingFail() {
        Utils.makeDialog(MainActivity.this,
                "平台登录失败",
                "是否重试？",
                "退出",
                "重试"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        System.exit(0);
                        Utils.hideProgress();
                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HuaweiLogin();
                    }
                });
    }

    /**
     * 处理宿主发送事件
     */
    private void handlerHuaweiLogingSuccess() {
        //MIUI系统权限特殊处理
//        if (Rom.isMiui() && Boolean.TRUE.equals(SPUtils.get(context, "PermissionDialog", true))) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("提示");
//            builder.setMessage("检测到当前正在使用MIUI系统，请打开后台弹出界面权限以保证来电提示正常工作");
//            builder.setNegativeButton("已经设置，不再提示", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    SPUtils.put(context, "PermissionDialog", false);
//                }
//            });
//            builder.setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Rom.MIUI_openAppPermission(MainActivity.this, getPackageName());
//                }
//            });
//            builder.show();
//        }


        //登录成功，更新本次存放的账户信息
//        Constant.siteName = LoginMgr.getInstance().getAccount();
//        Constant.siteUri = LoginMgr.getInstance().getSipNumber();

//        Intent intent = getIntent();

        if (intent == null) {
            return;
        }
        int type = intent.getIntExtra("type", -1);
        switch (type) {
            case 0://召集会议
                String confName = intent.getStringExtra("confName");
                String duration = intent.getStringExtra("duration");
                String sites = intent.getStringExtra("sites");

                //自动添加召集者本身
                if (!sites.contains(Constant.siteUri)) {
                    sites = sites + "," + Constant.siteUri;
                }

                //修正只有召集者自己时，参数异常问题
                if (sites.startsWith(",")) {
                    sites = sites.substring(1);
                }

                MeetingControl.createMeeting(confName, duration, sites, new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (baseData.getCode() == 0) {
                            Utils.showToast("会议召集成功，等待进入会场");
//                            Utils.showProgressDialog(MainActivity.this);
                            MeetingInfo info = Utils.jsonOBJ_Resolve(baseData.getData(), MeetingInfo.class);
                            //会议接入码前缀默认为 9000
                            String acc = info.getAccessCode().startsWith("9000") ? info.getAccessCode().substring(4) : info.getAccessCode();
                            Constant.joinConfAcc = acc;
                        } else {
                            if (TextUtils.isEmpty(baseData.getMsg())) {
                                Utils.showToast("会议召集失败，服务器内部错误");
                            } else {
                                Utils.showToast(baseData.getMsg());
                            }
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        Utils.showToast("会议召集失败，请稍后重试");
                    }
                });
                break;
            case 1://自己加入会议
                String smcConfId = intent.getStringExtra("smcConfId");
                MeetingControl.join(smcConfId, Constant.siteUri, new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (baseData.getCode() == 0) {
                            Constant.joinConfAcc = baseData.getData().toString();
                            Utils.showToast("会议加入成功，等待进入会场");
//                            Utils.showProgressDialog(MainActivity.this);
                        } else {
                            if (TextUtils.isEmpty(baseData.getMsg())) {
                                Utils.showToast("会议加入失败，服务器内部错误");
                            } else {
                                Utils.showToast(baseData.getMsg());
                            }
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        Utils.showToast("会议加入失败，请稍后重试");
                    }
                });
                break;
        }
        intent = null;

        //完成启动响应事件后，刷新首页列表
        updataDate();
    }


    /**
     * 更新数据列表
     */
    private void updataDate() {
        Map<String, String> map = new HashMap<>();
        map.put("siteUri", Constant.siteUri);
        NetWorkUtils.getServerforResult(RequestUrls.getMeetingList, map, new RxSubscriber<BaseData>() {
            @Override
            public void onSuccess(BaseData baseData) {
//                String str = "{\"code\":0,\"msg\":\"success\",\"data\":[{\"smcConfId\":\"1234\",\"confName\":\" APP_测试会议\",\"confStatus\":3,\"creatorUri\":null,\"creatorName\":null,\"beginTime\":\"2019-05-16 15:36:03\",\"endTime\":\"2019-05-16 17:36:03\",\"accessCode\":\"900002710015\",\"siteStatusInfoList\":[{\"siteUri\":\"027991\",\"siteName\":\"027991\",\"siteType\":0,\"siteStatus\":0,\"microphoneStatus\":0,\"loudspeakerStatus\":0},{\"siteUri\":\"027992\",\"siteName\":\"027992\",\"siteType\":0,\"siteStatus\":0,\"microphoneStatus\":0,\"loudspeakerStatus\":0}]},{\"smcConfId\":\"1234\",\"confName\":\" APP_测试会议\",\"confStatus\":3,\"creatorUri\":null,\"creatorName\":null,\"beginTime\":\"2019-05-16 15:36:03\",\"endTime\":\"2019-05-16 17:36:03\",\"accessCode\":\"900002710015\",\"siteStatusInfoList\":[{\"siteUri\":\"027991\",\"siteName\":\"027991\",\"siteType\":0,\"siteStatus\":0,\"microphoneStatus\":0,\"loudspeakerStatus\":0},{\"siteUri\":\"027992\",\"siteName\":\"027992\",\"siteType\":0,\"siteStatus\":0,\"microphoneStatus\":0,\"loudspeakerStatus\":0}]}]}";
//                baseData = gson.fromJson(str, BaseData.class);
                //以上是模拟代码

                Log.d(Constant.tag, "获取列表数据信息" + baseData.toString());
                //刷新数据前先清空
                data.clear();
                if (baseData.getCode() == 0) {
                    List<LinkedTreeMap> datalist = (List<LinkedTreeMap>) baseData.getData();
                    for (LinkedTreeMap temp : datalist) {
//                        MeetingInfo info = gson.fromJson(gson.toJson(temp), MeetingInfo.class);
                        MeetingInfo info = Utils.jsonOBJ_Resolve(temp, MeetingInfo.class);
                        data.add(info);

                        String currSip = LoginMgr.getInstance().getSipNumber();
                        for (SiteStatus status : info.getSiteStatusInfoList()) {
                            if (currSip != null && currSip.equals(status.getSiteUri()) && status.getSiteStatus().endsWith(SiteStatus.STATE_ONLINE) && !Constant.selfEndConf) {
                                Utils.makeDialog(MainActivity.this, "提示", "检测到你当前在会议中，可能需要进行修复才能正常入会", "取消", "修复"
                                        , new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                LoginMgr.getInstance().logout();
                                                HuaweiLogin();
                                            }
                                        });
                                break;
                            }
                        }
                    }
                } else {
//                    Utils.showToast("获取列表数据失败，列表可能为空");
                }
                adapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh(true);
            }

            @Override
            protected void onError(ResponeThrowable responeThrowable) {
                responeThrowable.printStackTrace();
                Log.d(Constant.tag, "fail");
                Utils.showToast("获取列表数据失败,请稍后再试");
                mRefreshLayout.finishRefresh(true);
            }
        });


    }

    /**
     * 初始化列表
     */
    private void initList() {
        adapter = new MeetingListAdapter(R.layout.list_meeting_item, data);
        meeting_list.setLayoutManager(new LinearLayoutManager(context));
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
//                    Utils.showToast("加入" + position);
//                Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
//                intent.putExtra("smcConfId", data.get(position).getSmcConfId());
//                intent.putExtra("confName", data.get(position).getConfName());
//                startActivity(intent);
                if (MeetingInfo.STATE_ORDER.equals(data.get(position).getConfStatus())) {
                    return;
                }

                MeetingControl.join(data.get(position).getSmcConfId(), Constant.siteUri, new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (baseData.getCode() == 0) {
                            Constant.joinConfAcc = data.get(position).getAccessCode();
                            Utils.showToast("正在加入会议，请稍后...");
                        } else {
                            if (TextUtils.isEmpty(baseData.getMsg())) {
                                Utils.showToast("加入会议失败，服务器内部错误");
                            } else {
                                Utils.showToast(baseData.getMsg());
                            }
                            updataDate();
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        Utils.showToast("加入会议失败，请稍后重试");
                    }
                });

//                Intent intent = new Intent(MainActivity.this, testpopActivity.class);
//                intent.putExtra("smcConfId", data.get(position).getSmcConfId());
//                startActivity(intent);

            }
        });
        //设置异常情况
        ImageView emptyView = new ImageView(context);
        emptyView.setImageResource(R.drawable.state_empty);
        adapter.setEmptyView(emptyView);

        meeting_list.setAdapter(adapter);
        //初始化刷新组件
        mRefreshLayout.setRefreshHeader(new MaterialHeader(context));
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mRefreshLayout.finishRefresh(false);
                updataDate();
            }
        });
    }

    /**
     * 控件初始化
     */
    private void initView() {
        back = findViewById(R.id.back);
        meeting_list = findViewById(R.id.meeting_list);
        mRefreshLayout = findViewById(R.id.refreshLayout);
    }

    /**
     * 给部分控件绑定点击事件
     */
    private void setclick() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideProgress();
                finish();
            }
        });
    }

    /**
     * 登录状态监听
     *
     * @param broadcastName Indicates registered broadcast name
     *                      注册的广播名称
     * @param obj           Indicates sent data
     */
    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success");
                handlerHuaweiLogingSuccess();
                Utils.hideProgress();
                break;

            case CustomBroadcastConstants.LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login fail");
                handlerHuaweiLogingFail();
                break;

            case CustomBroadcastConstants.LOGOUT:
                break;
            default:
                break;
        }
    }
}
