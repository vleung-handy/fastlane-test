package com.handy.portal.model.logs;

import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.BuildConfig;
import com.handy.portal.core.BaseApplication;

public abstract class EventLog
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
    private long mTimestampMillis;
    @SerializedName("provider_id")
    private String mProviderId;
    @SerializedName("version_track")
    private String mVersionTrack;
    @SerializedName("event_type")
    private String mEventType;
    @SerializedName("event_context")
    private String mEventContext;

    public EventLog(String providerId, String versionTrack, String eventType, String eventContext)
    {
        mProduct = PROVIDER;
        mPlatform = ANDROID;
        mOsVersion = Build.VERSION.RELEASE;
        mAppVersion = BuildConfig.VERSION_NAME;
        mDeviceId = BaseApplication.getDeviceId();
        mTimestampMillis = System.currentTimeMillis();
        mProviderId = providerId;
        mVersionTrack = versionTrack;
        mEventType = eventType;
        mEventContext = eventContext;
    }

}
