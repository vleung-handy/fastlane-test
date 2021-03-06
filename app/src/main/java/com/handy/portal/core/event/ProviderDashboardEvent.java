package com.handy.portal.core.event;


import com.handy.portal.dashboard.model.ProviderEvaluation;
import com.handy.portal.dashboard.model.ProviderFeedback;
import com.handy.portal.dashboard.model.ProviderRating;
import com.handy.portal.data.DataManager;

import java.util.List;

public abstract class ProviderDashboardEvent extends HandyEvent {
    public static class RequestProviderEvaluation extends RequestEvent {}


    public static class ReceiveProviderEvaluationSuccess extends ReceiveSuccessEvent {
        public final ProviderEvaluation providerEvaluation;

        public ReceiveProviderEvaluationSuccess(final ProviderEvaluation providerEvaluation) {
            this.providerEvaluation = providerEvaluation;
        }
    }


    public static class ReceiveProviderEvaluationError extends ReceiveErrorEvent {
        public ReceiveProviderEvaluationError(final DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestProviderFiveStarRatings extends RequestEvent {
        private Integer mMinStar;
        private String mUntilBookingDate;
        private String mSinceBookingDate;

        public RequestProviderFiveStarRatings(Integer minStar) {
            mMinStar = minStar;
        }

        public RequestProviderFiveStarRatings(Integer minStar, String untilBookingDate) {
            mMinStar = minStar;
            mUntilBookingDate = untilBookingDate;
        }

        public RequestProviderFiveStarRatings(Integer minStar, String untilBookingDate, String sinceBookingDate) {
            mMinStar = minStar;
            mUntilBookingDate = untilBookingDate;
            mSinceBookingDate = sinceBookingDate;
        }

        public Integer getMinStar() {
            return mMinStar;
        }

        public String getSinceBookingDate() {
            return mSinceBookingDate;
        }

        public String getUntilBookingDate() {
            return mUntilBookingDate;
        }
    }


    public static class ReceiveProviderFiveStarRatingsSuccess extends ReceiveSuccessEvent {
        private List<ProviderRating> mProviderRatings;

        public ReceiveProviderFiveStarRatingsSuccess(List<ProviderRating> providerRatings) {
            mProviderRatings = providerRatings;
        }

        public List<ProviderRating> getProviderRatings() {
            return mProviderRatings;
        }
    }


    public static class ReceiveProviderFiveStarRatingsError extends ReceiveErrorEvent {
        public ReceiveProviderFiveStarRatingsError(final DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestProviderFeedback extends RequestEvent {}


    public static class ReceiveProviderFeedbackSuccess extends ReceiveSuccessEvent {
        final public List<ProviderFeedback> providerFeedback;

        public ReceiveProviderFeedbackSuccess(final List<ProviderFeedback> providerFeedback) {
            this.providerFeedback = providerFeedback;
        }
    }


    public static class ReceiveProviderFeedbackError extends ReceiveErrorEvent {
        public ReceiveProviderFeedbackError(final DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class AnimateFiveStarPercentageGraph extends HandyEvent {}
}
