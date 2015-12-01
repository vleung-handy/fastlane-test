package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

public class EventLog
{
    private static final String ANDROID = "Android";
    private static final String PROVIDER = "pro";

    @SerializedName("product_type")
    private String mProduct;
    @SerializedName("platform")
    private String mPlatform;
    @SerializedName("os_version")
    private String mOsVersion;
    @SerializedName("app_version")
    private String mAppVersion;
    @SerializedName("device_id")
    private String mDeviceId;
    @SerializedName("timestamp")
    private long mTimestamp;
    @SerializedName("provider_id")
    private String mProviderId;
    @SerializedName("version_track")
    private String mVersionTrack;
    @SerializedName("event_type")
    private String mEventType;
    @SerializedName("event_context")
    private String mEventContext;

    protected EventLog(String osVersion, String appVersion, String deviceId, long timestamp,
                       String providerId, String versionTrack, String eventType, String eventContext)
    {
        mProduct = PROVIDER;
        mPlatform = ANDROID;
        mOsVersion = osVersion;
        mAppVersion = appVersion;
        mDeviceId = deviceId;
        mTimestamp = timestamp;
        mProviderId = providerId;
        mVersionTrack = versionTrack;
        mEventType = eventType;
        mEventContext = eventContext;
    }

}
