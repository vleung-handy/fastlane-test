package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.payments.BankAccountInfo;

public class StripeEvents
{
    public static class RequestStripeToken extends HandyEvent.RequestEvent
    {
        public final BankAccountInfo bankAccountInfo;
        public RequestStripeToken(BankAccountInfo bankAccountInfo)
        {
            this.bankAccountInfo = bankAccountInfo;
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
