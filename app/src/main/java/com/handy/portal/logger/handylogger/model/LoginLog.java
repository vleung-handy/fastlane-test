package com.handy.portal.logger.handylogger.model;

public class LoginLog extends EventLog
{
    private static final String EVENT_CONTEXT = "login";

    protected LoginLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Shown extends LoginLog
    {
        private static final String EVENT_TYPE = "shown";

        public Shown()
        {
            super(EVENT_TYPE);
        }
    }

    public static class PhoneNumberSubmitted extends LoginLog
    {
        private static final String EVENT_TYPE = "phone_number_submitted";

        public PhoneNumberSubmitted()
        {
            super(EVENT_TYPE);
        }
    }

    public static class PinCodeSubmitted extends LoginLog
    {
        private static final String EVENT_TYPE = "pin_code_submitted";

        public PinCodeSubmitted()
        {
            super(EVENT_TYPE);
        }
    }

    public static class Success extends LoginLog
    {
        private static final String EVENT_TYPE = "success";

        public Success()
        {
            super(EVENT_TYPE);
        }
    }

    public static class Error extends LoginLog
    {
        private static final String EVENT_TYPE = "error";

        public Error()
        {
            super(EVENT_TYPE);
        }
    }
}
