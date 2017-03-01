package com.handy.portal.logger.handylogger.model;


import com.google.gson.annotations.SerializedName;

public abstract class FeedbackLog extends EventLog {
    private static final String EVENT_CONTEXT = "feedback";

    public FeedbackLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class VideoSelected extends FeedbackLog {
        private static final String EVENT_TYPE = "video_selected";

        @SerializedName("section")
        private String mSection;

        public VideoSelected(final String section) {
            super(EVENT_TYPE);
            mSection = section;
        }
    }


    public static class VideoLibrarySelected extends FeedbackLog {
        private static final String EVENT_TYPE = "video_library_selected";

        public VideoLibrarySelected() {
            super(EVENT_TYPE);
        }
    }
}
