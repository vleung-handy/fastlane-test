package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class PaymentsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "payments";

    public PaymentsLog(String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BatchSelected extends PaymentsLog
    {
        private static final String EVENT_TYPE = "batch_selected";

        @SerializedName("current_week")
        private boolean mCurrentWeek;
        @SerializedName("list_index")
        private int mListIndex;

        public BatchSelected(boolean currentWeek, int listIndex)
        {
            super(EVENT_TYPE);
            mCurrentWeek = currentWeek;
            mListIndex = listIndex;
        }
    }


    public static class DetailSelected extends PaymentsLog
    {
        private static final String EVENT_TYPE = "detail_selected";

        @SerializedName("payment_type")
        private String mPaymentType;

        public DetailSelected(String paymentType)
        {
            super(EVENT_TYPE);
            mPaymentType = paymentType;
        }
    }


    public static class HelpSlideUpSelected extends PaymentsLog
    {
        private static final String EVENT_TYPE = "help_slide_up_selected";

        public HelpSlideUpSelected()
        {
            super(EVENT_TYPE);
        }
    }


    public static class HelpItemSelected extends PaymentsLog
    {
        private static final String EVENT_TYPE = "help_item_selected";

        @SerializedName("help_item_label")
        private String mHelpItemLabel;

        public HelpItemSelected(String helpLabelLabel)
        {
            super(EVENT_TYPE);
            mHelpItemLabel = helpLabelLabel;
        }
    }

}
