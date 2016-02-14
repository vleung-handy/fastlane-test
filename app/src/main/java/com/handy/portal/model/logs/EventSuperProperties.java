package com.handy.portal.model.logs;

import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.BuildConfig;
import com.handy.portal.core.BaseApplication;

public class EventSuperProperties
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
    @SerializedName("device_model")
    private String mDeviceModel;
    @SerializedName("provider_id")
    private int mProviderId;

    public EventSuperProperties(final int providerId)
    {
        mProduct = PROVIDER;
        mPlatform = ANDROID;
        mOsVersion = Build.VERSION.RELEASE;
        mAppVersion = BuildConfig.VERSION_NAME;
        mDeviceId = BaseApplication.getDeviceId();
        mDeviceModel = BaseApplication.getDeviceModel();
        mProviderId = providerId;
    }
}