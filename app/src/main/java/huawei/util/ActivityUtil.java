package huawei.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import huawei.Constant.IntentConstant;
import huawei.Constant.UIConstants;

public final class ActivityUtil
{
    private ActivityUtil()
    {
    }

    public static void startActivity(Context context, String action, String[] categorys)
    {
        try
        {
            Intent intent = new Intent(action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            for (int i = 0; i < categorys.length; i++)
            {
                intent.addCategory(categorys[i]);
            }
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivity(Context context, String action)
    {
        try
        {
            Intent intent = new Intent(action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivity(Context context, Intent intent)
    {
        try
        {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode)
    {
        try
        {
            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
            activity.startActivityForResult(intent, requestCode);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivityForResult(Activity activity, String action, int requestCode)
    {
        try
        {
            Intent intent = new Intent(action);
            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
            activity.startActivityForResult(intent, requestCode);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }
}