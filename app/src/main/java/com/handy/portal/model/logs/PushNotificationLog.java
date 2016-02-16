package com.handy.portal.model.logs;

import com.urbanairship.push.PushMessage;

public abstract class PushNotificationLog extends EventLog
{
    private static final String EVENT_CONTEXT = "push_notifications";

    public PushNotificationLog(final String eventType, final PushMessage pushMessage)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Received extends PushNotificationLog
    {
        public Received(final PushMessage pushMessage)
        {
            super("received", pushMessage);
        }
    }


    public static class Opened extends PushNotificationLog
    {
        public Opened(final PushMessage pushMessage)
        {
            super("opened", pushMessage);
        }
    }


    public static class Dismissed extends PushNotificationLog
    {
        public Dismissed(final PushMessage pushMessage)
        {
            super("dismissed", pushMessage);
        }
    }
}
