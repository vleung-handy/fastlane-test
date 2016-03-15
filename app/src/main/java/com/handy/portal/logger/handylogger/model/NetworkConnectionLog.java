package com.handy.portal.logger.handylogger.model;

public abstract class NetworkConnectionLog extends EventLog
{
    private static final String EVENT_CONTEXT = "network_connection";

    public NetworkConnectionLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Reconnected extends NetworkConnectionLog
    {
        private static final String EVENT_TYPE = "reconnected";

        public Reconnected()
        {
            super(EVENT_TYPE);
        }
    }


    public static class Disconnected extends NetworkConnectionLog
    {
        private static final String EVENT_TYPE = "disconnected";

        public Disconnected()
        {
            super(EVENT_TYPE);
        }
    }
}
