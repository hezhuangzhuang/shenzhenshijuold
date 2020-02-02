package com.zxwl.szga;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.CrashUtil;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.ConfConvertUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.servicemgr.ServiceMgr;
import com.zxwl.szga.Constant.Constant;
import com.zxwl.szga.activity.MainActivity;
import com.zxy.recovery.core.Recovery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import huawei.Constant.UIConstants;
import huawei.logic.CallFunc;
import huawei.logic.ConfFunc;
import huawei.logic.LoginFunc;
import huawei.util.FileUtil;
import huawei.util.ZipUtil;

public class APP extends Application {
    private static Context context;

    private static final int EXPECTED_FILE_LENGTH = 7;

    private static final String FRONT_PKG = "com.zxwl.szga";

    private int mIdoProtocol = 0;

    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();

        //初始化异常处理
        initcrashHandler();

        if (!isFrontProcess(this, FRONT_PKG)) {
            LocContext.init(this);
            CrashUtil.getInstance().init(this);
            Log.i("SDKDemo", "onCreate: PUSH Process.");
            return;
        }

        Log.d(Constant.tag,"MyApp_开始初始化");
        String appPath = getApplicationInfo().dataDir + "/lib";
        MeetingMgr.getInstance().setConfProtocol(ConfConvertUtil.convertConfctrlProtocol(mIdoProtocol));
        ServiceMgr.getServiceMgr().startService(this, appPath, mIdoProtocol);

        LoginMgr.getInstance().regLoginEventNotification(LoginFunc.getInstance());
        CallMgr.getInstance().regCallServiceNotification(CallFunc.getInstance());
        MeetingMgr.getInstance().regConfServiceNotification(ConfFunc.getInstance());

        initResourceFile();

        Log.d(Constant.tag,"MyApp_初始化完毕");
    }

    public static Context getContext() {
        return context;
    }

    private void initResourceFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initDataConfRes();
            }
        }).start();
    }

    private void initDataConfRes() {
        String path = LocContext.
                getContext().getFilesDir() + "/AnnoRes";
        File file = new File(path);
        if (file.exists()) {
            LogUtil.i(UIConstants.DEMO_TAG, file.getAbsolutePath());
            File[] files = file.listFiles();
            if (null != files && EXPECTED_FILE_LENGTH == files.length) {
                return;
            } else {
                FileUtil.deleteFile(file);
            }
        }
        try {
            InputStream inputStream = getAssets().open("AnnoRes.zip");
            ZipUtil.unZipFile(inputStream, path);
        } catch (IOException e) {
            LogUtil.i(UIConstants.DEMO_TAG, "close...Exception->e" + e.toString());
        }
    }

    /**
     * 异常处理
     */
    private void initcrashHandler() {
//         /本地提示
        Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity.class)
                .recoverEnabled(true)
//                .callback(new MyCrashCallback())
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
//                .skip(TestActivity.class)
                .init(this);
    }

    private static boolean isFrontProcess(Context context, String frontPkg) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        if (infos == null || infos.isEmpty()) {
            return false;
        }

        final int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid) {
                Log.i(UIConstants.DEMO_TAG, "processName-->" + info.processName);
                return frontPkg.equals(info.processName);
            }
        }

        return false;
    }

    @Override
    public void onTerminate() {

        super.onTerminate();

        ServiceMgr.getServiceMgr().stopService();

    }
}
