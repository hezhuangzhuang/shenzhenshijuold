package com.zxwl.szga.util;

import com.zxwl.network.api.ServerApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.szga.APP;
import com.zxwl.szga.Constant.Constant;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 二次封装网络请求，便于后期进行修饰调整
 */
public class NetWorkUtils {
    /**
     * 使用GET请求向服务器请求数据
     *
     * @param url
     * @param handler
     */
    public static void getServerforResult(String url, Map<String, String> params, RxSubscriber<BaseData> handler) {
        HttpUtils.getInstance(APP.getContext())
                .getRetofitClinet()
                .setBaseUrl(Constant.SERVER_URL)
                .builder(ServerApi.class)
                .get(url, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    /**
     * 使用POST请求向服务器请求数据
     *
     * @param url
     * @param params
     * @param handler
     */
    public static void postServerforResult(String url, Map<String, String> params, RxSubscriber<BaseData> handler) {
        HttpUtils.getInstance(APP.getContext())
                .getRetofitClinet()
                .setBaseUrl(Constant.SERVER_URL)
                .builder(ServerApi.class)
                .post(url, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    /**
     * 使用PATCH请求向服务器请求数据
     *
     * @param url
     * @param params
     * @param handler
     */
    public static void patchServerforResult(String url, Map<String, String> params, RxSubscriber<BaseData> handler) {
        HttpUtils.getInstance(APP.getContext())
                .getRetofitClinet()
                .setBaseUrl(Constant.SERVER_URL)
                .builder(ServerApi.class)
                .patch(url, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

//    /**
//     * 使用PATCH请求向服务器请求数据
//     *
//     * @param url
//     * @param params
//     * @param handler
//     */
//    public static void patchServerforResult2(String url, Map<String, String> params, RxSubscriber<RequestBody> handler) {
//        HttpUtils.getInstance(APP.getContext())
//                .getRetofitClinet()
//                .setBaseUrl(Constant.SERVER_URL)
//                .builder(ServerApi.class)
//                .patch2(url, params)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(handler);
//    }

    /**
     * 使用DELETE请求向服务器请求数据
     *
     * @param url
     * @param params
     * @param handler
     */
    public static void deleteServerforResult(String url, Map<String, String> params, RxSubscriber<BaseData> handler) {
        HttpUtils.getInstance(APP.getContext())
                .getRetofitClinet()
                .setBaseUrl(Constant.SERVER_URL)
                .builder(ServerApi.class)
                .delete(url, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }


}
