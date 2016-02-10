package com.handy.portal.event;


import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.model.dashboard.ProviderRatings;

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


    public static class ReceiveProviderEvaluationError extends ReceiveErrorEvent {}


    public static class RequestProviderFiveStarRatings extends RequestEvent {}


    public static class ReceiveProviderFiveStarRatingsSuccess extends ReceiveSuccessEvent
    {
        public ProviderRatings providerRatings;

        public ReceiveProviderFiveStarRatingsSuccess(ProviderRatings providerRatings)
        {
            this.providerRatings = providerRatings;
        }
    }


    public static class ReceiveProviderFiveStarRatingsError extends ReceiveErrorEvent {}


    public static class RequestProviderFeedback extends RequestEvent {}


    public static class ReceiveProviderFeedbackSuccess extends ReceiveSuccessEvent
    {
        public ProviderFeedback providerFeedback;

        public ReceiveProviderFeedbackSuccess(ProviderFeedback providerFeedback)
        {
            this.providerFeedback = providerFeedback;
        }
    }


    public static class ReceiveProviderFeedbackError extends ReceiveErrorEvent {}
}
