package com.handy.portal.payments;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.payments.model.AnnualPaymentSummaries;
import com.handy.portal.payments.model.CreateDebitCardResponse;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.model.PaymentFlow;
import com.handy.portal.payments.model.PaymentOutstandingFees;
import com.stripe.android.model.Token;

import java.util.Date;

public abstract class PaymentEvent extends HandyEvent
{
    public static class RequestPaymentBatches extends RequestEvent
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


    public static class ReceivePaymentBatchesSuccess extends ReceiveSuccessEvent
    {
        private final Date requestStartDate; //if this batch is empty, we can use this to keep track of pagination
        private final Date requestEndDate; //use this to prevent invalid responses from being used in corner cases
        public final boolean isFromInitialBatchRequest;
        private final PaymentBatches paymentBatches;
        private final int callerIdentifier;

        public Date getRequestEndDate()
        {
            return requestEndDate;
        }

        public Date getRequestStartDate()
        {
            return requestStartDate;
        }

        public ReceivePaymentBatchesSuccess(PaymentBatches paymentBatches, Date requestStartDate, Date requestEndDate, boolean isFromInitialBatchRequest, int callerIdentifier)
        {

            this.paymentBatches = paymentBatches;
            this.requestStartDate = requestStartDate;
            this.requestEndDate = requestEndDate;
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


    public static class ReceivePaymentBatchesError extends ReceiveErrorEvent
    {
        public ReceivePaymentBatchesError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestAnnualPaymentSummaries extends RequestEvent
    {
        public RequestAnnualPaymentSummaries() { }
    }


    public static class ReceiveAnnualPaymentSummariesSuccess extends ReceiveSuccessEvent
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


    public static class ReceiveAnnualPaymentSummariesError extends ReceiveErrorEvent
    {
        public ReceiveAnnualPaymentSummariesError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestPaymentOutstandingFees extends RequestEvent
    {
        public RequestPaymentOutstandingFees() { }
    }


    public static class ReceivePaymentOutstandingFeesSuccess extends ReceiveSuccessEvent
    {
        private final PaymentOutstandingFees paymentOutstandingFees;

        public ReceivePaymentOutstandingFeesSuccess(PaymentOutstandingFees paymentOutstandingFees)
        {
            this.paymentOutstandingFees = paymentOutstandingFees;
        }

        public PaymentOutstandingFees getPaymentOutstandingFees()
        {
            return paymentOutstandingFees;
        }
    }


    public static class ReceivePaymentOutstandingFeesError extends ReceiveErrorEvent
    {
        public ReceivePaymentOutstandingFeesError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class ReceiveShouldUserUpdatePaymentInfoSuccess extends ReceiveSuccessEvent
    {
        public final boolean shouldUserUpdatePaymentInfo;

        public ReceiveShouldUserUpdatePaymentInfoSuccess(boolean shouldUserUpdatePaymentInfo)
        {
            this.shouldUserUpdatePaymentInfo = shouldUserUpdatePaymentInfo;
        }
    }


    public static class ReceiveShouldUserUpdatePaymentInfoError extends ReceiveErrorEvent
    {
        public ReceiveShouldUserUpdatePaymentInfoError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestShouldUserUpdatePaymentInfo extends RequestEvent
    {

    }


    public static class ReceiveCreateBankAccountSuccess extends ReceiveSuccessEvent
    {
        public final boolean successfullyCreated;

        public ReceiveCreateBankAccountSuccess(boolean successfullyCreated)
        {
            this.successfullyCreated = successfullyCreated;
        }
    }


    public static class ReceiveCreateBankAccountError extends ReceiveErrorEvent
    {
        public ReceiveCreateBankAccountError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestCreateBankAccount extends RequestEvent
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


    public static class ReceiveCreateDebitCardRecipientSuccess extends ReceiveSuccessEvent
    {
        public final boolean successfullyCreated;

        public ReceiveCreateDebitCardRecipientSuccess(boolean succesfullyCreated)
        {
            this.successfullyCreated = succesfullyCreated;
        }
    }


    public static class ReceiveCreateDebitCardRecipientError extends ReceiveErrorEvent
    {
        public ReceiveCreateDebitCardRecipientError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestCreateDebitCardRecipient extends RequestEvent
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


    public static class ReceiveCreateDebitCardForChargeSuccess extends ReceiveSuccessEvent
    {
        public final CreateDebitCardResponse response;

        public ReceiveCreateDebitCardForChargeSuccess(CreateDebitCardResponse response)
        {
            this.response = response;
        }
    }


    public static class ReceiveCreateDebitCardForChargeError extends ReceiveErrorEvent
    {
        public ReceiveCreateDebitCardForChargeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestCreateDebitCardForCharge extends RequestEvent
    {
        public final String stripeToken;

        public RequestCreateDebitCardForCharge(String stripeToken)
        {
            this.stripeToken = stripeToken;
        }
    }


    public static class ReceivePaymentFlowSuccess extends ReceiveSuccessEvent
    {
        public final PaymentFlow paymentFlow;

        public ReceivePaymentFlowSuccess(PaymentFlow paymentFlow)
        {
            this.paymentFlow = paymentFlow;
        }
    }


    public static class ReceivePaymentFlowError extends ReceiveErrorEvent
    {
        public ReceivePaymentFlowError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestPaymentFlow extends RequestEvent {}


    public static class RequestUpdateCreditCard extends RequestEvent
    {
        private Token mToken;

        public RequestUpdateCreditCard(final Token token)
        {
            mToken = token;
        }

        public Token getToken()
        {
            return mToken;
        }
    }


    public static class ReceiveUpdateCreditCardSuccess extends ReceiveSuccessEvent {}


    public static class ReceiveUpdateCreditCardError extends ReceiveErrorEvent
    {
        public ReceiveUpdateCreditCardError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
