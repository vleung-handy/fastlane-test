package com.handy.portal.logger.handylogger.model;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.logger.handylogger.model.DeeplinkLog.Ignored.Reason.DeeplinkIgnoredReason;
import com.handy.portal.logger.handylogger.model.DeeplinkLog.Source.DeeplinkSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DeeplinkLog extends EventLog
{
    public static abstract class Source
    {
        public static final String PUSH_NOTIFICATION = "push_notification";
        public static final String LINK = "link";
        public static final String WEBVIEW = "webview";
        public static final String STARTUP = "startup";


        @StringDef({PUSH_NOTIFICATION, LINK, WEBVIEW, STARTUP})
        @Retention(RetentionPolicy.SOURCE)
        public @interface DeeplinkSource {}
    }


    private static final String EVENT_CONTEXT = "deeplink";

    @SerializedName("url")
    private String mUrl;
    @SerializedName("path")
    private String mPath;
    @SerializedName("host")
    private String mHost;
    @SerializedName("scheme")
    private String mScheme;
    @SerializedName("params_string")
    private String mParametersString;
    @SerializedName("extras")
    private Map<String, Object> mExtras;
    @SerializedName("source")
    private String mSource;

    public DeeplinkLog(
            @NonNull final String eventType,
            @NonNull String source,
            @NonNull final Uri data
    )
    {
        super(eventType, EVENT_CONTEXT);
        mUrl = data.toString();
        mPath = data.getPath();
        mHost = data.getHost();
        mScheme = data.getScheme();
        mParametersString = data.getQuery();
        mSource = source;
        mExtras = extractExtrasMap(data);
    }

    public DeeplinkLog(
            @NonNull final String eventType,
            @NonNull String source,
            @NonNull final Bundle extras
    )
    {
        super(eventType, EVENT_CONTEXT);
        mSource = source;
        mPath = extras.getString(BundleKeys.DEEPLINK);
        mExtras = extractExtrasMap(extras);
    }

    public static class Opened extends DeeplinkLog
    {
        private static final String EVENT_TYPE = "opened";

        public Opened(@DeeplinkSource final String source, final Uri data)
        {
            super(EVENT_TYPE, source, data);
        }

        public Opened(@DeeplinkSource final String source, final Bundle extras)
        {
            super(EVENT_TYPE, source, extras);
        }
    }


    public static class Processed extends DeeplinkLog
    {
        private static final String EVENT_TYPE = "processed";

        public Processed(@DeeplinkSource final String source, final Uri data)
        {
            super(EVENT_TYPE, source, data);
        }

        public Processed(@DeeplinkSource final String source, final Bundle extras)
        {
            super(EVENT_TYPE, source, extras);
        }

    }


    public static class Ignored extends DeeplinkLog
    {
        public static class Reason
        {
            public static final String UNRECOGNIZED = "unrecognized";
            public static final String MISSING_PARAMETERS = "missing_parameters";


            @StringDef({UNRECOGNIZED, MISSING_PARAMETERS})
            @Retention(RetentionPolicy.SOURCE)
            public @interface DeeplinkIgnoredReason {}
        }


        private static final String EVENT_TYPE = "ignored";

        @SerializedName("reason")
        private String mReason;

        public Ignored(
                @DeeplinkSource final String source,
                @DeeplinkIgnoredReason final String reason,
                final Uri data
        )
        {
            super(EVENT_TYPE, source, data);
            mReason = reason;
        }

        public Ignored(
                @DeeplinkSource final String source,
                @DeeplinkIgnoredReason final String reason,
                final Bundle extras
        )
        {
            super(EVENT_TYPE, source, extras);
            mReason = reason;
        }
    }

    @Nullable
    private static Map<String, Object> extractExtrasMap(@NonNull final Uri data)
    {
        if (!data.isHierarchical()) //Uri.getQueryParameterNames() will throw exception if uri is NOT hierarchical
        {
            return null;
        }

        final Map<String, Object> parameters = new HashMap<>();
        final Set<String> queryParameterNames = data.getQueryParameterNames();
        for (final String queryParameterName : queryParameterNames)
        {
            final String queryParameterValue = data.getQueryParameter(queryParameterName);
            parameters.put(queryParameterName, queryParameterValue);
        }
        return parameters;
    }

    private static Map<String, Object> extractExtrasMap(@NonNull final Bundle extras)
    {
        final HashMap<String, Object> extrasMap = new HashMap<>(extras.size());
        for (String key : extras.keySet())
        {
            extrasMap.put(key, extras.get(key));
        }
        return extrasMap;
    }
}
