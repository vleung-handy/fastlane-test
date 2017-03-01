package com.handy.portal.logger.handylogger.model;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.constant.BundleKeys;
import com.urbanairship.push.PushMessage;

import java.util.HashMap;
import java.util.Map;

public abstract class PushNotificationLog extends EventLog {
    @SerializedName("handy_push_uuid")
    private String mHandyPushUuid;
    @SerializedName("handy_push_type")
    private String mHandyPushType;
    @SerializedName("push_extras")
    private Map<String, Object> mPushExtras;

    private static final String EVENT_CONTEXT = "push_notifications";

    public PushNotificationLog(final String eventType, final PushMessage pushMessage) {
        super(eventType, EVENT_CONTEXT);
        final Bundle pushBundle = pushMessage.getPushBundle();
        mHandyPushUuid = pushBundle.getString(BundleKeys.HANDY_PUSH_UUID);
        mHandyPushType = pushBundle.getString(BundleKeys.HANDY_PUSH_TYPE);
        mPushExtras = new HashMap<>(pushBundle.size());
        for (final String key : pushBundle.keySet()) {
            mPushExtras.put(key, pushBundle.get(key));
        }
    }

    public static class Received extends PushNotificationLog {
        public static final String EVENT_TYPE = "received";

        public Received(final PushMessage pushMessage) {
            super(EVENT_TYPE, pushMessage);
        }
    }


    public static class Opened extends PushNotificationLog {
        public static final String EVENT_TYPE = "opened";

        public Opened(final PushMessage pushMessage) {
            super(EVENT_TYPE, pushMessage);
        }
    }


    public static class Dismissed extends PushNotificationLog {
        public static final String EVENT_TYPE = "dismissed";

        public Dismissed(final PushMessage pushMessage) {
            super(EVENT_TYPE, pushMessage);
        }
    }
}
