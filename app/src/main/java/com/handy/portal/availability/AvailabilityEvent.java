package com.handy.portal.availability;

import com.handy.portal.availability.model.Availability;

public abstract class AvailabilityEvent {

    public static class TimelineUpdated {
        private Availability.Timeline mTimeline;

        public TimelineUpdated(final Availability.Timeline timeline) {

            mTimeline = timeline;
        }

        public Availability.Timeline getTimeline() {
            return mTimeline;
        }
    }
}
