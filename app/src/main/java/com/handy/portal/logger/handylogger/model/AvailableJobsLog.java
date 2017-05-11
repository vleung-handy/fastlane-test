package com.handy.portal.logger.handylogger.model;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

import java.util.HashMap;
import java.util.Map;

public abstract class AvailableJobsLog extends EventLog {
    private static final String EVENT_CONTEXT = "available_jobs";

    protected AvailableJobsLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }


    public static class UnavailableJobNoticeShown extends AvailableJobsLog {
        private static final String EVENT_TYPE = "unavailable_job_notice_shown";

        @SerializedName("extras")
        private Map<String, Object> mExtras;

        public UnavailableJobNoticeShown(@Nullable final Bundle extras) {
            super(EVENT_TYPE);
            if (extras != null) {
                mExtras = new HashMap<>(extras.size());
                for (final String key : extras.keySet()) {
                    mExtras.put(key, extras.get(key));
                }
            }
        }
    }

    // Booking-specific events


    public static class Clicked extends JobsLog {
        private static final String EVENT_TYPE = "job_selected";

        @SerializedName("list_index")
        private int mListIndex;

        public Clicked(final Booking booking, final int listIndex) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
            mListIndex = listIndex;
        }
    }

    // Job claim events


    public static class ConfirmClaimShown extends AvailableJobsLog {
        private static final String EVENT_TYPE = "confirm_claim_shown";

        public ConfirmClaimShown() {
            super(EVENT_TYPE);
        }
    }


    public static class ConfirmClaimDetailsShown extends AvailableJobsLog {
        private static final String EVENT_TYPE = "confirm_claim_details_shown";

        public ConfirmClaimDetailsShown() {
            super(EVENT_TYPE);
        }
    }


    public static class ConfirmClaimConfirmed extends AvailableJobsLog {
        private static final String EVENT_TYPE = "confirm_claim_confirmed";

        public ConfirmClaimConfirmed() {
            super(EVENT_TYPE);
        }
    }
}
