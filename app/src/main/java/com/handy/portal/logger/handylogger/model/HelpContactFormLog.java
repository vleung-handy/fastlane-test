package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public abstract class HelpContactFormLog extends EventLog
{
    private static String EVENT_CONTEXT = "help";

    @SerializedName("path")
    private String mPath;
    @SerializedName("help_node_id")
    private int mHelpNodeId;
    @SerializedName("help_node_title")
    private String mHelpNodeTitle;

    public HelpContactFormLog(final String eventType,
                              final String path,
                              final int helpNodeId,
                              final String helpNodeTitle)
    {
        super(eventType, EVENT_CONTEXT);
        mPath = path;
        mHelpNodeId = helpNodeId;
        mHelpNodeTitle = helpNodeTitle;
    }

    public static class Submitted extends HelpContactFormLog
    {
        private static String EVENT_TYPE = "contact_form_submitted";

        public Submitted(final String path,
                         final int helpNodeId,
                         final String helpNodeTitle)
        {
            super(EVENT_TYPE, path, helpNodeId, helpNodeTitle);
        }
    }


    public static class Success extends HelpContactFormLog
    {
        private static String EVENT_TYPE = "contact_form_success";

        public Success(final String path,
                       final int helpNodeId,
                       final String helpNodeTitle)
        {
            super(EVENT_TYPE, path, helpNodeId, helpNodeTitle);
        }
    }


    public static class Error extends HelpContactFormLog
    {
        private static String EVENT_TYPE = "contact_form_error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public Error(final String path,
                     final int helpNodeId,
                     final String helpNodeTitle,
                     final String errorMessage)
        {
            super(EVENT_TYPE, path, helpNodeId, helpNodeTitle);
            mErrorMessage = errorMessage;
        }
    }
}
