package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.payments.BankAccountInfo;
import com.handy.portal.model.payments.DebitCardInfo;
import com.handy.portal.model.payments.StripeTokenResponse;

public class StripeEvents
{
    public static class RequestStripeTokenFromBankAccount extends HandyEvent.RequestEvent
    {
        public final BankAccountInfo bankAccountInfo;
        public RequestStripeTokenFromBankAccount(BankAccountInfo bankAccountInfo)
        {
            this.bankAccountInfo = bankAccountInfo;
        }
    }

    public static class ReceiveStripeTokenFromBankAccountSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final StripeTokenResponse stripeTokenResponse;
        public ReceiveStripeTokenFromBankAccountSuccess(StripeTokenResponse stripeTokenResponse)
        {
            this.stripeTokenResponse = stripeTokenResponse;
        }
    }

    public static class ReceiveStripeTokenFromBankAccountError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveStripeTokenFromBankAccountError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestStripeTokenFromDebitCard extends HandyEvent.RequestEvent
    {
        public final DebitCardInfo debitCardInfo;
        public RequestStripeTokenFromDebitCard(DebitCardInfo debitCardInfo)
        {
            this.debitCardInfo = debitCardInfo;
        }
    }

    public static class ReceiveStripeTokenFromDebitCardSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final StripeTokenResponse stripeTokenResponse;
        public ReceiveStripeTokenFromDebitCardSuccess(StripeTokenResponse stripeTokenResponse)
        {
            this.stripeTokenResponse = stripeTokenResponse;
        }
    }

    public static class ReceiveStripeTokenFromDebitCardError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveStripeTokenFromDebitCardError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
