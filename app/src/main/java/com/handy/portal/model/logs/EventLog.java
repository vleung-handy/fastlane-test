package com.handy.portal.model.logs;

import android.os.Build;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.BuildConfig;
import com.handy.portal.core.BaseApplication;

public abstract class EventLog
{
    public static final String BETA = "beta";
    public static final String STABLE = "stable";

    @StringDef({BETA, STABLE})
    @interface Flavor {}

    private static final String ANDROID = "Android";
    private static final String PRODUCT = "Pro";

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
    @SerializedName("flavor")
    private String mFlavor;
    @SerializedName("event_type")
    private String mEventType;
    @SerializedName("event_context")
    private String mEventContext;

    public EventLog(String providerId, @Flavor String flavor, String eventContext, String eventType)
    {
        mProduct = PRODUCT;
        mPlatform = ANDROID;
        mOsVersion = Build.VERSION.RELEASE;
        mAppVersion = BuildConfig.VERSION_NAME;
        mDeviceId = BaseApplication.getDeviceId();
        mTimestamp = System.currentTimeMillis();
        mProviderId = providerId;
        mFlavor = flavor;
        mEventType = eventType;
        mEventContext = eventContext;
    }
}
