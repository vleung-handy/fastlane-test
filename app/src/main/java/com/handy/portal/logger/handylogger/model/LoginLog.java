package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

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


    // Login Logs that extend this are required to have a phone number string with them
    public static class LoginPhoneNumberLog extends LoginLog
    {
        @SerializedName("phone_number")
        private String mPhoneNumberString;

        public LoginPhoneNumberLog(final String eventType, final String phoneNumberString)
        {
            super(eventType);
            mPhoneNumberString = phoneNumberString;
        }
    }


    public static class PhoneNumberSubmitted extends LoginPhoneNumberLog
    {
        private static final String EVENT_TYPE = "phone_number_submitted";

        public PhoneNumberSubmitted(String phoneNumberString)
        {
            super(EVENT_TYPE, phoneNumberString);
        }
    }


    public static class PinCodeSubmitted extends LoginPhoneNumberLog
    {
        private static final String EVENT_TYPE = "pin_code_submitted";

        public PinCodeSubmitted(String phoneNumberString)
        {
            super(EVENT_TYPE, phoneNumberString);
        }
    }


    public static class Success extends LoginPhoneNumberLog
    {
        private static final String EVENT_TYPE = "success";

        public Success(String phoneNumberString)
        {
            super(EVENT_TYPE, phoneNumberString);
        }
    }


    public static class Error extends LoginPhoneNumberLog
    {
        private static final String EVENT_TYPE = "error";

        public Error(String phoneNumberString)
        {
            super(EVENT_TYPE, phoneNumberString);
        }
    }
}
