package com.handy.portal.logger.handylogger.model;


public class PerformanceLog extends EventLog
{
    private static final String EVENT_CONTEXT = "performance";

    public PerformanceLog(String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class FeedbackSelected extends PerformanceLog
    {
        private static final String EVENT_TYPE = "feedback_selected";

        public FeedbackSelected()
        {
            super(EVENT_TYPE);
        }
    }


    public static class FiveStarReviewsSelected extends PerformanceLog
    {
        private static final String EVENT_TYPE = "five_star_reviews_selected";

        public FiveStarReviewsSelected()
        {
            super(EVENT_TYPE);
        }
    }


    public static class TierSelected extends PerformanceLog
    {
        private static final String EVENT_TYPE = "tier_selected";

        public TierSelected()
        {
            super(EVENT_TYPE);
        }
    }


    public static class LifetimeRatingsShown extends PerformanceLog
    {
        private static final String EVENT_TYPE = "lifetime_ratings_shown";

        public LifetimeRatingsShown()
        {
            super(EVENT_TYPE);
        }
    }


    public static class RollingRatingsShown extends PerformanceLog
    {
        private static final String EVENT_TYPE = "rolling_ratings_shown";

        public RollingRatingsShown()
        {
            super(EVENT_TYPE);
        }
    }
}
