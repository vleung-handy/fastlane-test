package com.handy.portal.model.logs;

import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.BuildConfig;
import com.handy.portal.core.BaseApplication;

import java.util.ArrayList;
import java.util.List;

public class EventLogBundle
{
    @SerializedName("eventBundleID")
    private String mEventBundleId;
    @SerializedName("super_properties")
    private Properties mProperties;
    @SerializedName("events")
    private List<EventLog> mEventLogs;


    public EventLogBundle(String eventBundleId)
    {
        this(eventBundleId, new ArrayList<EventLog>());
    }

    public EventLogBundle(String eventBundleId, List<EventLog> eventLogs)
    {
        mEventBundleId = eventBundleId;
        mProperties = new Properties();
        mEventLogs = eventLogs;
    }

    public void add(EventLog eventLog)
    {
        mEventLogs.add(eventLog);
    }

    public class Properties
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

        public Properties()
        {
            mProduct = PROVIDER;
            mPlatform = ANDROID;
            mOsVersion = Build.VERSION.RELEASE;
            mAppVersion = BuildConfig.VERSION_NAME;
            mDeviceId = BaseApplication.getDeviceId();
        }
    }
}
