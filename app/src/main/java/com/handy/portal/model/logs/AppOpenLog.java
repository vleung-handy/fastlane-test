package com.handy.portal.model.logs;

public class AppOpenLog extends EventLog
{
    private static final String EVENT_CONTEXT = "app";
    private static final String EVENT_TYPE = "app_open";

    public AppOpenLog(String userId, @Flavor String flavor)
    {
        super(userId, flavor, EVENT_CONTEXT, EVENT_TYPE);
    }
}
