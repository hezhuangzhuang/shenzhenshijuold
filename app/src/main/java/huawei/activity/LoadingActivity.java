package huawei.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxwl.szga.R;

import huawei.activity.base.BaseMediaActivity;

/**
 * 等待页，处理接听呼出事件
 */
public class LoadingActivity extends BaseMediaActivity {
//public class LoadingActivity extends BaseActivity {
    private TextView tvName;
    private ImageView ivLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏显示
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_loading);

        findViews();
        initData();
//        joinMeeting();
    }

    protected void findViews() {
        tvName = (TextView) findViewById(R.id.tv_name);
        ivLoading = (ImageView) findViewById(R.id.iv_loading);
    }

    ObjectAnimator ra = null;

    protected void initData() {
        Intent intent = getIntent();

        String confName = intent.getStringExtra("confName");
        tvName.setText(confName);

        ra = ObjectAnimator.ofFloat(ivLoading, "rotation", 0f, 360f);
        ra.setDuration(1500);
        ra.setRepeatCount(ObjectAnimator.INFINITE);
        ra.setInterpolator(new LinearInterpolator());
        ra.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
