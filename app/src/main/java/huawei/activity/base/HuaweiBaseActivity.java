package huawei.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.util.LogUtil;

import huawei.Constant.UIConstants;


public abstract class HuaweiBaseActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Add a log to load the layout
//        ActivityStack.getIns().push(this);

        initializeData();
        initializeComposition();
        initBackView();
        LogUtil.i(UIConstants.DEMO_TAG, "Activity onCreate: " + getClass().getSimpleName());
    }

    public abstract void initializeComposition();

    public abstract void initializeData();

    protected void initBackView()
    {
//        initBackView(R.id.back_iv);
    }

    protected void initBackView(int resource)
    {
        View backView = findViewById(resource);
        if (null != backView)
        {
            backView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onBack();
                }
            });
        }
    }

    protected void onBack()
    {
//        ActivityStack.getIns().popup(this);
        // onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
//        ActivityStack.getIns().popup(this);
        super.onDestroy();
    }

    public void showToast(int resId)
    {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }
}
