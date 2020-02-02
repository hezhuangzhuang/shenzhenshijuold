package com.zxwl.szga.bean;

/**
 * 与会人信息
 */
public class SiteStatus {
    public static final String STATE_ONLINE = "2.0";
    public static final String STATE_OFFLINE = "3.0";
    public static final String STATE_MUTE = "0.0";
    public static final String STATE_MUTE_CANCEL = "1.0";

    private String siteUri;
    private String siteName;
    private String siteType;
    private String siteStatus;
    private String microphoneStatus;
    private String loudspeakerStatus;

    public SiteStatus() {
    }

    public SiteStatus(String siteUri, String siteName, String siteType, String siteStatus, String microphoneStatus, String loudspeakerStatus) {
        this.siteUri = siteUri;
        this.siteName = siteName;
        this.siteType = siteType;
        this.siteStatus = siteStatus;
        this.microphoneStatus = microphoneStatus;
        this.loudspeakerStatus = loudspeakerStatus;
    }

    public String getSiteUri() {
        return siteUri;
    }

    public void setSiteUri(String siteUri) {
        this.siteUri = siteUri;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(String siteStatus) {
        this.siteStatus = siteStatus;
    }

    public String getMicrophoneStatus() {
        return microphoneStatus;
    }

    public void setMicrophoneStatus(String microphoneStatus) {
        this.microphoneStatus = microphoneStatus;
    }

    public String getLoudspeakerStatus() {
        return loudspeakerStatus;
    }

    public void setLoudspeakerStatus(String loudspeakerStatus) {
        this.loudspeakerStatus = loudspeakerStatus;
    }
}
