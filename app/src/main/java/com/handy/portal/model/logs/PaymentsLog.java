package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

public class PaymentsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "profile";

    public PaymentsLog(String providerId, String versionTrack, String eventType)
    {
        super(providerId, versionTrack, eventType, EVENT_CONTEXT);
    }

    public static class BatchSelected extends PaymentsLog
    {
        private static final String EVENT_TYPE = "batch_selected";

        @SerializedName("current_week")
        private boolean mCurrentWeek;
        @SerializedName("list_number")
        private int mListNumber;

        public BatchSelected(String providerId, String versionTrack, boolean currentWeek, int listNumber)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mCurrentWeek = currentWeek;
            mListNumber = listNumber;
        }
    }

    public static class DetailSelected extends PaymentsLog
    {
        private static final String EVENT_TYPE = "detail_selected";

        @SerializedName("payment_type")
        private String mPaymentType;

        public DetailSelected(String providerId, String versionTrack, String paymentType)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mPaymentType = paymentType;
        }
    }

    public static class HelpSlideUpSelected extends PaymentsLog
    {
        private static final String EVENT_TYPE = "help_slide_up_selected";

        public HelpSlideUpSelected(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class HelpItemSelected extends PaymentsLog
    {
        private static final String EVENT_TYPE = "help_item_selected";

        @SerializedName("help_item_label")
        private String mHelpItemLabel;

        public HelpItemSelected(String providerId, String versionTrack, String helpLabelLabel)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mHelpItemLabel = helpLabelLabel;
        }
    }

}
