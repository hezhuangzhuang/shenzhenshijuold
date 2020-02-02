package com.zxwl.szga.Constant;

/**
 * 请求地址
 */
public class RequestUrls {
    //鉴权 get
    public final static String authentication = "/api/app/authentication";
    //召开即时会议 post
    public final static String createMeeting = "/api/conference";
    //获取会议列表 get
    public final static String getMeetingList = "/api/conference?";
    //获取会议详情
    public final static String getMeetingInfo = "/api/conference/status";
    //呼叫/挂断 post
    public final static String call_OR_hangUP = "/api/conference/site/onlineStatus";
    //静音终端 post
    public final static String mute = "/api/conference/site/microphoneStatus";
    //移除与会者 delete
    public final static String delete = "/api/conference/site";
    //添加与会人 post
    public final static String add = "/api/conference/site";
    //加入会议 post
    public final static String join = "/api/conference/join";
    //结束会议 delete
    public final static String end = "/api/conference";
    //获取组织机构 get
    public final static String getOrg = "/api/organization?";
    //搜索警员 get
    public final static String getNum = "/api/police";
    //搜索警员 get
    public final static String search = "/api/police";
    //获取app安装包下载地址 get
    public final static String getapk = "/api/app";
    //app检查更新
    public final static String checkUpdate = "/api/app?";
    //关闭扬声器 post
    public final static String closeLouder = "/api/conference/site/loudspeakerStatus";
    //根据会议接入码请求会议信息
    public final static String getCurrMeetingInfo = "api/conference/confAccessCode?";
    //获取账户密码
    public final static String getUserAndPwd = "/api/conference/login-sso?";

}
