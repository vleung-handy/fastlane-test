package com.handy.portal.event;


import com.handy.portal.data.DataManager;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.model.dashboard.ProviderRating;

import java.util.List;

public abstract class ProviderDashboardEvent extends HandyEvent
{
    public static class RequestProviderEvaluation extends RequestEvent {}


    public static class ReceiveProviderEvaluationSuccess extends ReceiveSuccessEvent
    {
        public ProviderEvaluation providerEvaluation;

        public ReceiveProviderEvaluationSuccess(ProviderEvaluation providerEvaluation)
        {
            this.providerEvaluation = providerEvaluation;
        }
    }


    public static class ReceiveProviderEvaluationError extends ReceiveErrorEvent
    {
        public ReceiveProviderEvaluationError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestProviderFiveStarRatings extends RequestEvent {}


    public static class ReceiveProviderFiveStarRatingsSuccess extends ReceiveSuccessEvent
    {
        private List<ProviderRating> mProviderRatings;

        public ReceiveProviderFiveStarRatingsSuccess(List<ProviderRating> providerRatings)
        {
            mProviderRatings = providerRatings;
        }

        public List<ProviderRating> getProviderRatings()
        {
            return mProviderRatings;
        }
    }


    public static class ReceiveProviderFiveStarRatingsError extends ReceiveErrorEvent
    {
        public ReceiveProviderFiveStarRatingsError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestProviderFeedback extends RequestEvent {}


    public static class ReceiveProviderFeedbackSuccess extends ReceiveSuccessEvent
    {
        public ProviderFeedback providerFeedback;

        public ReceiveProviderFeedbackSuccess(ProviderFeedback providerFeedback)
        {
            this.providerFeedback = providerFeedback;
        }
    }


    public static class ReceiveProviderFeedbackError extends ReceiveErrorEvent
    {
        public ReceiveProviderFeedbackError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
