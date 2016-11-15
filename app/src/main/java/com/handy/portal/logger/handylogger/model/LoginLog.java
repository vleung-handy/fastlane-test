package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class LoginLog extends EventLog
{
    private static final String EVENT_CONTEXT = "app";

    // pin code
    public static final String TYPE_PHONE = "phone";
    public static final String TYPE_PIN = "pin_code";
    // single login token
    public static final String TYPE_PHONE_TOKEN = "phone_token";
    public static final String TYPE_TOKEN = "token";

    @SerializedName("type")
    private String mType;

    protected LoginLog(final String eventType, final String type)
    {
        super(eventType, EVENT_CONTEXT);
        mType = type;
    }

    public static class Shown extends LoginLog
    {
        private static final String EVENT_TYPE = "login_shown";

        public Shown(String type)
        {
            super(EVENT_TYPE, type);
        }
    }


    public static class login_submitted extends LoginLog
    {
        private static final String EVENT_TYPE = "login_submitted";

        public login_submitted(String type)
        {
            super(EVENT_TYPE, type);
        }
    }

    public static class Success extends LoginLog
    {
        private static final String EVENT_TYPE = "login_success";

        public Success(String type)
        {
            super(EVENT_TYPE, type);
        }
    }

    public static class Error extends LoginLog
    {
        private static final String EVENT_TYPE = "login_error";

        public Error(String type)
        {
            super(EVENT_TYPE, type);
        }
    }
}
