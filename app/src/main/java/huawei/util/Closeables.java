package huawei.util;

import android.util.Log;


import java.io.Closeable;
import java.io.IOException;

import huawei.Constant.UIConstants;


public class Closeables
{
    public static void closeCloseable(Closeable closeable)
    {
        if (closeable == null)
        {
            return;
        }

        try
        {
            closeable.close();
        }
        catch (IOException e)
        {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }
}
