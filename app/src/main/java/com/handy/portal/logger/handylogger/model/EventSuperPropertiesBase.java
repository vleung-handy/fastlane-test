package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class EventSuperPropertiesBase
{
    private static final String ANDROID = "android";
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
    @SerializedName("installation_id")
    private String mInstallationId;

    public EventSuperPropertiesBase(String osVersion, String appVersion, String deviceId,
                                    String deviceModel, String installationId)
    {
        mProduct = PROVIDER;
        mPlatform = ANDROID;
        mOsVersion = osVersion;
        mAppVersion = appVersion;
        mDeviceId = deviceId;
        mDeviceModel = deviceModel;
        mInstallationId = installationId;
    }
}
