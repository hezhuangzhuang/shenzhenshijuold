package huawei.activity.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.zxwl.szga.util.PreferenceUtil;

import java.lang.reflect.Method;

/**
 * author：Thinkpad
 * data:2017/11/15 14:56
 */

public abstract class BaseLibActivity extends AppCompatActivity {
    /**
     * 初始化view
     */
    protected abstract void findViews();

    /**
     * 初始化view的数据
     */
    protected abstract void initData();

    /**
     * 设置view的监听事件
     */
    protected abstract void setListener();

    /**
     * 获得布局layout id
     *
     * @return
     */
    protected abstract int getLayoutId();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏显示
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(getLayoutId());

        //添加Activity到管理栈中
//        AppManager.getInstance().addActivity(this);
        findViews();
        initData();
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从堆栈中移除
//        AppManager.getInstance().finishActivity(this);
    }

    protected void showProgressDialog(String message) {
//        DialogUtils.showProgressDialog(this, message);
    }

    protected void dismissProgressDialog() {
//        DialogUtils.dismissProgressDialog(this);
    }

    //获取虚拟按键的高度
    public  int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context `   * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public  boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
             return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     * @return
       */
    private  String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
        }

}
