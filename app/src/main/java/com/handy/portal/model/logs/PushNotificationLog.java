package com.handy.portal.model.logs;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.constant.BundleKeys;
import com.urbanairship.push.PushMessage;

import java.util.HashMap;
import java.util.Map;

public abstract class PushNotificationLog extends EventLog
{
    @SerializedName("push_id")
    private String mPushId;
    @SerializedName("push_type")
    private String mPushType;
    @SerializedName("push_bundle")
    private Map<String, Object> mPushBundle;

    private static final String EVENT_CONTEXT = "push_notifications";

    public PushNotificationLog(final String eventType, final PushMessage pushMessage)
    {
        super(eventType, EVENT_CONTEXT);
        mPushId = pushMessage.getCanonicalPushId();
        final Bundle pushBundle = pushMessage.getPushBundle();
        mPushType = pushBundle.getString(BundleKeys.PUSH_TYPE);
        mPushBundle = new HashMap<>(pushBundle.size());
        for (final String key : pushBundle.keySet())
        {
            mPushBundle.put(key, pushBundle.get(key));
        }
    }

    public static class Received extends PushNotificationLog
    {
        public static final String EVENT_TYPE = "received";

        public Received(final PushMessage pushMessage)
        {
            super(EVENT_TYPE, pushMessage);
        }
    }


    public static class Opened extends PushNotificationLog
    {
        public static final String EVENT_TYPE = "opened";

        public Opened(final PushMessage pushMessage)
        {
            super(EVENT_TYPE, pushMessage);
        }
    }


    public static class Dismissed extends PushNotificationLog
    {
        public static final String EVENT_TYPE = "dismissed";

        public Dismissed(final PushMessage pushMessage)
        {
            super(EVENT_TYPE, pushMessage);
        }
    }
}
