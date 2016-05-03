package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.payments.model.BankAccountInfo;
import com.handy.portal.payments.model.DebitCardInfo;
import com.handy.portal.payments.model.StripeTokenResponse;

public abstract class StripeEvent extends HandyEvent
{
    public static class RequestStripeTokenFromBankAccount extends RequestEvent
    {
        public final BankAccountInfo bankAccountInfo;

        public RequestStripeTokenFromBankAccount(BankAccountInfo bankAccountInfo)
        {
            this.bankAccountInfo = bankAccountInfo;
        }
    }

    public static class ReceiveStripeTokenFromBankAccountSuccess extends ReceiveSuccessEvent
    {
        public final StripeTokenResponse stripeTokenResponse;

        public ReceiveStripeTokenFromBankAccountSuccess(StripeTokenResponse stripeTokenResponse)
        {
            this.stripeTokenResponse = stripeTokenResponse;
        }
    }

    public static class ReceiveStripeTokenFromBankAccountError extends ReceiveErrorEvent
    {
        public ReceiveStripeTokenFromBankAccountError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestStripeTokenFromDebitCard extends RequestEvent
    {
        public final int requestIdentifier;//TODO: refactor - might remove this later
        public final DebitCardInfo debitCardInfo;

        public RequestStripeTokenFromDebitCard(DebitCardInfo debitCardInfo, int requestIdentifier)
        {
            this.debitCardInfo = debitCardInfo;
            this.requestIdentifier = requestIdentifier;
        }
    }

    public static class ReceiveStripeTokenFromDebitCardSuccess extends ReceiveSuccessEvent
    {
        public final int requestIdentifier;
        public final StripeTokenResponse stripeTokenResponse;

        public ReceiveStripeTokenFromDebitCardSuccess(StripeTokenResponse stripeTokenResponse, int requestIdentifier)
        {
            this.stripeTokenResponse = stripeTokenResponse;
            this.requestIdentifier = requestIdentifier;
        }
    }

    public static class ReceiveStripeTokenFromDebitCardError extends ReceiveErrorEvent
    {
        public ReceiveStripeTokenFromDebitCardError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
