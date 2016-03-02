package com.handy.portal.model.logs;


public class PerformanceLog extends EventLog
{
    private static final String EVENT_CONTEXT = "performance";

    public PerformanceLog(String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class FeedbackTappedLog extends PerformanceLog
    {
        private static final String EVENT_TYPE = "feedback_tapped";

        public FeedbackTappedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class FiveStarReviewsTappedLog extends PerformanceLog
    {
        private static final String EVENT_TYPE = "five_star_reviews_tapped";

        public FiveStarReviewsTappedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class TierTappedLog extends PerformanceLog
    {
        private static final String EVENT_TYPE = "tier_tapped";

        public TierTappedLog()
        {
            super(EVENT_TYPE);
        }
    }
}
