package huawei.util;

import android.util.Log;


import huawei.Constant.UIConstants;

public class ExceptionHandler extends CustomExceptionHandler
{
    @Override
    public void uncaughtException(Thread thread, Throwable throwable)
    {
        Log.e(UIConstants.DEMO_TAG, throwable.getMessage());
        super.uncaughtException(thread, throwable);
    }
}
