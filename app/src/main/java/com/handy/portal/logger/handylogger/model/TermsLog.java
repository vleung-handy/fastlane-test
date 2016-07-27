package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class TermsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "terms";

    protected TermsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Shown extends TermsLog
    {
        private static final String EVENT_TYPE = "shown";

        @SerializedName("terms_code")
        private String mTermsCode;

        public Shown(final String termsCode)
        {
            super(EVENT_TYPE);
            mTermsCode = termsCode;
        }
    }


    public static class Accepted extends TermsLog
    {
        private static final String EVENT_TYPE = "accepted";

        @SerializedName("terms_code")
        private String mTermsCode;

        public Accepted(final String termsCode)
        {
            super(EVENT_TYPE);
            mTermsCode = termsCode;
        }
    }


    public static class Error extends TermsLog
    {
        private static final String EVENT_TYPE = "error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public Error(final String errorMessage)
        {
            super(EVENT_TYPE);
            mErrorMessage = errorMessage;
        }
    }

}
