package com.handy.portal.manager;


import com.handy.portal.data.DataManager;
import com.handy.portal.event.StripeEvents;
import com.handy.portal.model.payments.BankAccountInfo;
import com.handy.portal.model.payments.StripeTokenResponse;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class StripeManager //TODO: should we consolidate this with PaymentsManager?
{
    private final String STRIPE_API_KEY = "pk_AdAZ6Xac3qjOGZPIPBxAiVFxoocj4"; //TODO: move to config file
    private final Bus bus;
    private final DataManager dataManager;

    private class RequestStripeTokenKeys{
        private final static String BANK_ACCOUNT = "bank_account";
        public final static String BANK_ACCOUNT_COUNTRY = BANK_ACCOUNT + "[country]";
        public final static String BANK_ACCOUNT_CURRENCY = BANK_ACCOUNT + "[currency]";
        public final static String BANK_ACCOUNT_ROUTING_NUMBER = BANK_ACCOUNT + "[routing_number]";
        public final static String BANK_ACCOUNT_ACCOUNT_NUMBER = BANK_ACCOUNT + "[account_number]";
        public final static String API_KEY = "key";
        public final static String PAYMENT_USER_AGENT = "payment_user_agent";
    }

    @Inject
    public StripeManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
    }

    public static BankAccountInfo getTestBankAccountInfo() //TODO: test only, remove later
    {
        BankAccountInfo testBankAccountInfo = new BankAccountInfo();
        testBankAccountInfo.setCountry("US");
        testBankAccountInfo.setCurrency("usd");
        testBankAccountInfo.setRoutingNumber("110000000");
        testBankAccountInfo.setAccountNumber("000123456789");
        return testBankAccountInfo;
    }

    @Subscribe
    public void onRequestStripeToken(final StripeEvents.RequestStripeToken event)
    {
        dataManager.getStripeToken(buildParamsFromBankAccountInfo(event.bankAccountInfo), new DataManager.Callback<StripeTokenResponse>()
        {
            @Override
            public void onSuccess(StripeTokenResponse response)
            {
                bus.post(new StripeEvents.ReceiveStripeTokenSuccess(response.getStripeToken()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new StripeEvents.ReceiveStripeTokenError(error));

            }
        });
    }

    private Map<String, String> buildParamsFromBankAccountInfo(BankAccountInfo bankAccountInfo)
    {
        Map<String, String> params = new HashMap<>();
        params.put(RequestStripeTokenKeys.BANK_ACCOUNT_ACCOUNT_NUMBER, bankAccountInfo.getAccountNumber());
        params.put(RequestStripeTokenKeys.BANK_ACCOUNT_COUNTRY, bankAccountInfo.getCountry());
        params.put(RequestStripeTokenKeys.BANK_ACCOUNT_CURRENCY, bankAccountInfo.getCurrency());
        params.put(RequestStripeTokenKeys.BANK_ACCOUNT_ROUTING_NUMBER, bankAccountInfo.getRoutingNumber());
        params.put(RequestStripeTokenKeys.API_KEY, STRIPE_API_KEY);
        return params;
    }

}
