package com.zxwl.szga.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zxwl.szga.APP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class Utils {
    public static void showToast(String msg) {
        Toast.makeText(APP.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取当前屏幕高度
     *
     * @return
     */
    public static int getScreenHight() {
        DisplayMetrics dm = APP.getContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取当前屏幕宽度
     *
     * @return
     */
    public static int getScreenWidht() {
        DisplayMetrics dm = APP.getContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 生成文字头像
     *
     * @param width    图片宽度
     * @param height   图片高度
     * @param txtSize  文字字号
     * @param innerTxt 内容文字
     * @return
     */
    public static Bitmap createTextImage(int width, int height, int txtSize, String innerTxt, String backColor, String textColor) {
        //若使背景为透明，必须设置为Bitmap.Config.ARGB_4444
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bm);

        Paint paintBackground = new Paint();
        paintBackground.setColor(Color.parseColor(backColor));
        canvas.drawCircle(width / 2, height / 2, width / 2, paintBackground);

        Paint paintText = new Paint();
        paintText.setColor(Color.parseColor(textColor));
        paintText.setTextSize(txtSize);

        int posX = width / 2 - txtSize / 2;
        int posY = height / 2 + txtSize / 2 - height / 12;

        canvas.drawText(innerTxt, posX, posY, paintText);

        return bm;
    }

    /**
     * 生成提示窗
     *
     * @param context
     * @param title
     * @param content
     * @param NegInfo
     * @param PosInfo
     * @param Neghandler
     * @param Poshandler
     */
    public static void makeDialog(Activity context, String title, String content, String NegInfo, String PosInfo, DialogInterface.OnClickListener Neghandler, DialogInterface.OnClickListener Poshandler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton(NegInfo, Neghandler);
        builder.setPositiveButton(PosInfo, Poshandler);

        Dialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
//        builder.show();
    }


    private static Gson gson = new Gson();

    /**
     * json解析
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T jsonResolve(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    /**
     * 对象型json解析
     *
     * @param obj
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T jsonOBJ_Resolve(Object obj, Class<T> tClass) {
        return gson.fromJson(gson.toJson(obj), tClass);
    }

    /**
     * javaBean转型为JSON字符串
     *
     * @param obj
     * @return
     */
    public static String Bean2Json(Object obj) {
        return gson.toJson(obj);
    }

    private static ProgressDialog dialog;

    /**
     * 创建等待窗
     *
     * @param context
     */
    public static void showProgressDialog(Activity context) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(context);
                dialog.setMessage("数据加载中...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除等待窗
     */
    public static void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    //判断当前界面显示的是哪个Activity
    public static String getTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//        Log.d("Chunna.zheng", "pkg:"+cn.getPackageName());//包名
//        Log.d("Chunna.zheng", "cls:"+cn.getClassName());//包名加类名
        return cn.getClassName();
    }


    /**
     * 复制资源文件到sd卡
     * @param context
     * @param assetpath
     * @param SDpath
     */
    public static void AssetsToSD(Context context, String assetpath, String SDpath) {
        AssetManager asset = context.getAssets();
        //循环的读取asset下的文件，并且写入到SD卡
        String[] filenames = null;
        FileOutputStream out = null;
        InputStream in = null;
        try {
            filenames = asset.list(assetpath);
            File SDFlie = new File(SDpath);

            if (!SDFlie.exists()) {
                SDFlie.createNewFile();
            }
            //将内容写入到文件中
            in = asset.open(assetpath);
            out = new FileOutputStream(SDFlie);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteCount);
            }
            out.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /**
     * 判断文件是否存在
     * @return
     */
    public static boolean isFileExists(String path){
        File file = new File(path);
        if (file.exists()){
            return true;
        }
        return false;
    }
}
