package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.PaymentBatches;

import java.util.Date;

public class PaymentEvents
{
    public static class RequestPaymentBatches extends HandyEvent.RequestEvent
    {
        //need to remember if request was initial to handle corner cases related to fragment not listening to events after onPause
        public final boolean isInitialBatchRequest;
        public final Date startDate;
        public final Date endDate;
        public final int callerIdentifier;
        public RequestPaymentBatches(Date startDate, Date endDate, boolean isInitialRequest, int callerIdentifier)
        {
            this.callerIdentifier = callerIdentifier;
            this.startDate = startDate;
            this.endDate = endDate;
            this.isInitialBatchRequest = isInitialRequest;
        }
    }

    public static class ReceivePaymentBatchesSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final boolean isFromInitialBatchRequest;
        private final PaymentBatches paymentBatches;
        private final int callerIdentifier;
        public Date getRequestStartDate()
        {
            return requestStartDate;
        }

        private final Date requestStartDate; //if this batch is empty, we can use this to keep track of pagination
        public ReceivePaymentBatchesSuccess(PaymentBatches paymentBatches, Date requestStartDate, boolean isFromInitialBatchRequest, int callerIdentifier)
        {

            this.paymentBatches = paymentBatches;
            this.requestStartDate = requestStartDate;
            this.callerIdentifier = callerIdentifier;
            this.isFromInitialBatchRequest = isFromInitialBatchRequest;
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

    public static class ReceiveCreateBankAccountSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final boolean successfullyCreated;
        public ReceiveCreateBankAccountSuccess(boolean succesfullyCreated)
        {
            this.successfullyCreated = succesfullyCreated;
        }
    }

    public static class ReceiveCreateBankAccountError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveCreateBankAccountError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestCreateBankAccount extends HandyEvent.RequestEvent
    {
        public final String stripeToken;
        public final String taxId;
        public final String accountNumberLast4Digits;
        public RequestCreateBankAccount(String stripeToken, String taxId, String accountNumberLast4Digits)
        {
            this.stripeToken = stripeToken;
            this.taxId = taxId;
            this.accountNumberLast4Digits = accountNumberLast4Digits;
        }
    }


    public static class ReceiveCreateDebitCardRecipientSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final boolean successfullyCreated;
        public ReceiveCreateDebitCardRecipientSuccess(boolean succesfullyCreated)
        {
            this.successfullyCreated = succesfullyCreated;
        }
    }

    public static class ReceiveCreateDebitCardRecipientError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveCreateDebitCardRecipientError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestCreateDebitCardRecipient extends HandyEvent.RequestEvent
    {
        public final String stripeToken;
        //TODO: refactor. wrap in object?
        public final String taxId;
        public final String cardNumberLast4Digits;
        public final String expMonth;
        public final String expYear;
        public RequestCreateDebitCardRecipient(String stripeToken, String taxId, String accountNumberLast4Digits, String expMonth, String expYear)
        {
            this.stripeToken = stripeToken;
            this.taxId = taxId;
            this.cardNumberLast4Digits = accountNumberLast4Digits;
            this.expMonth = expMonth;
            this.expYear = expYear;
        }
    }
}
