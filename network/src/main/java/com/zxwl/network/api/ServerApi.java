package com.zxwl.network.api;


import com.zxwl.network.bean.BaseData;

import java.util.Map;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

public interface ServerApi {
    /**
     * 使用get向服务器请求数据
     */
    @GET
    Observable<BaseData> get(@Url String url, @QueryMap Map<String, String> param);

    @POST
    Observable<BaseData> post(@Url String url, @QueryMap Map<String, String> param);

    @PATCH
    Observable<BaseData> patch(@Url String url, @QueryMap Map<String, String> param);

    @DELETE
    Observable<BaseData> delete(@Url String url, @QueryMap Map<String, String> param);
}
