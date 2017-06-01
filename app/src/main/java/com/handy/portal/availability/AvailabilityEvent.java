package com.handy.portal.availability;

import com.handy.portal.availability.model.Availability;

public abstract class AvailabilityEvent {

    public static class AdhocTimelineUpdated {
        private Availability.AdhocTimeline mTimeline;

        public AdhocTimelineUpdated(final Availability.AdhocTimeline timeline) {
            mTimeline = timeline;
        }

        public Availability.AdhocTimeline getTimeline() {
            return mTimeline;
        }
    }


    public static class TemplateTimelineUpdated {
        private Availability.TemplateTimeline mTimeline;

        public TemplateTimelineUpdated(final Availability.TemplateTimeline timeline) {
            mTimeline = timeline;
        }

        public Availability.TemplateTimeline getTimeline() {
            return mTimeline;
        }
    }
}
