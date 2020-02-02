package com.zxwl.szga.bean;

import java.util.List;

/**
 * 会场信息
 */
public class MeetingInfo {
    public final static String STATE_ORDER = "2.0";
    public final static String STATE_MEETING = "3.0";

    private String smcConfId;
    private String confName;
    private String confStatus;
    private String creatorUri;
    private String creatorName;
    private String beginTime;
    private String endTime;
    private String accessCode;
    private List<SiteStatus> siteStatusInfoList;


    public String getSmcConfId() {
        return smcConfId;
    }


    public String getConfName() {
        return confName;
    }


    public String getConfStatus() {
        return confStatus;
    }

    public void setConfStatus(String confStatus) {
        this.confStatus = confStatus;
    }

    public String getCreatorUri() {
        return creatorUri;
    }

    public void setCreatorUri(String creatorUri) {
        this.creatorUri = creatorUri;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public List<SiteStatus> getSiteStatusInfoList() {
        return siteStatusInfoList;
    }

    public void setSiteStatusInfoList(List<SiteStatus> siteStatusInfoList) {
        this.siteStatusInfoList = siteStatusInfoList;
    }

    @Override
    public String toString() {
        return "MeetingInfo{" +
                "smcConfId='" + smcConfId + '\'' +
                ", confName='" + confName + '\'' +
                ", confStatus='" + confStatus + '\'' +
                ", creatorUri='" + creatorUri + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", accessCode='" + accessCode + '\'' +
                ", siteStatusInfoList=" + siteStatusInfoList +
                '}';
    }
}
