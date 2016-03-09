package com.handy.portal.logger.handylogger.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DeeplinkLog extends EventLog
{
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
    @SerializedName("params")
    private Map<String, String> mParameters;

    public DeeplinkLog(@NonNull final String eventType, @NonNull final Uri data)
    {
        super(eventType, EVENT_CONTEXT);
        mUrl = data.toString();
        mPath = data.getPath();
        mHost = data.getHost();
        mScheme = data.getScheme();
        mParametersString = data.getQuery();
        mParameters = extractParametersMap(data);
    }

    public static class Opened extends DeeplinkLog
    {
        private static final String EVENT_TYPE = "opened";

        public Opened(final Uri data)
        {
            super(EVENT_TYPE, data);
        }
    }


    public static class Processed extends DeeplinkLog
    {
        private static final String EVENT_TYPE = "processed";

        public Processed(final Uri data)
        {
            super(EVENT_TYPE, data);
        }
    }


    public static class Ignored extends DeeplinkLog
    {
        private static final String EVENT_TYPE = "ignored";

        public Ignored(final Uri data)
        {
            super(EVENT_TYPE, data);
        }
    }

    private static Map<String, String> extractParametersMap(@NonNull final Uri data)
    {
        final Map<String, String> parameters = new HashMap<>();
        final Set<String> queryParameterNames = data.getQueryParameterNames();
        for (final String queryParameterName : queryParameterNames)
        {
            final String queryParameterValue = data.getQueryParameter(queryParameterName);
            parameters.put(queryParameterName, queryParameterValue);
        }
        return parameters;
    }
}
