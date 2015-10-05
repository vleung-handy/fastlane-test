package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.manager.StripeManager;
import com.handy.portal.model.payments.BankAccountInfo;
import com.handy.portal.model.payments.DebitCardInfo;

import java.util.Map;

public class StripeEvents
{
    public static class RequestStripeToken extends HandyEvent.RequestEvent
    {
        public final Map<String, String> params;
        public RequestStripeToken(BankAccountInfo bankAccountInfo)
        {
            params = StripeManager.buildParamsFromBankAccountInfo(bankAccountInfo); //TODO: refactor. would rather not do this in request object
        }
        public RequestStripeToken(DebitCardInfo debitCardInfo)
        {
            params = StripeManager.buildParamsFromDebitCardInfo(debitCardInfo);
        }
    }

    public static class ReceiveStripeTokenSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private final String token;
        public ReceiveStripeTokenSuccess(String token)
        {
            this.token = token;
        }

        public String getToken()
        {
            return token;
        }
    }

    public static class ReceiveStripeTokenError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveStripeTokenError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
