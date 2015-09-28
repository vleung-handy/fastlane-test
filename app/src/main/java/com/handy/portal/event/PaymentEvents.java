package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.PaymentBatches;

import java.util.Date;

public class PaymentEvents
{
    public static class RequestPaymentBatches extends HandyEvent.RequestEvent
    {
        public final Date startDate;
        public final Date endDate;
        public final int callerIdentifier;
        public RequestPaymentBatches(Date startDate, Date endDate, int callerIdentifier)
        {
            this.callerIdentifier = callerIdentifier;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    public static class ReceivePaymentBatchesSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private final PaymentBatches paymentBatches;
        private final int callerIdentifier;
        public Date getRequestStartDate()
        {
            return requestStartDate;
        }

        private final Date requestStartDate; //if this batch is empty, we can use this to keep track of pagination
        public ReceivePaymentBatchesSuccess(PaymentBatches paymentBatches, Date requestStartDate, int callerIdentifier)
        {
            this.paymentBatches = paymentBatches;
            this.requestStartDate = requestStartDate;
            this.callerIdentifier = callerIdentifier;
        }
        public int getCallerIdentifier()
        {
            return callerIdentifier;
        }
        public PaymentBatches getPaymentBatches()
        {
            return paymentBatches;
        }
    }

    public static class ReceivePaymentBatchesError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceivePaymentBatchesError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestAnnualPaymentSummaries extends HandyEvent.RequestEvent
    {
        public RequestAnnualPaymentSummaries()
        {
        }
    }

    public static class ReceiveAnnualPaymentSummariesSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private final AnnualPaymentSummaries annualPaymentSummaries;
        public ReceiveAnnualPaymentSummariesSuccess(AnnualPaymentSummaries annualPaymentSummaries)
        {
            this.annualPaymentSummaries = annualPaymentSummaries;
        }
        public AnnualPaymentSummaries getAnnualPaymentSummaries()
        {
            return annualPaymentSummaries;
        }
    }

    public static class ReceiveAnnualPaymentSummariesError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveAnnualPaymentSummariesError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class ReceiveShouldUserUpdatePaymentInfoSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final boolean shouldUserUpdatePaymentInfo;
        public ReceiveShouldUserUpdatePaymentInfoSuccess(boolean shouldUserUpdatePaymentInfo)
        {
            this.shouldUserUpdatePaymentInfo = shouldUserUpdatePaymentInfo;
        }
    }

    public static class ReceiveShouldUserUpdatePaymentInfoError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveShouldUserUpdatePaymentInfoError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestShouldUserUpdatePaymentInfo extends HandyEvent.RequestEvent
    {

    }
}
