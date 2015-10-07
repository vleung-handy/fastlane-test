package com.handy.portal.manager;


import com.handy.portal.data.DataManager;
import com.handy.portal.event.StripeEvents;
import com.handy.portal.model.payments.BankAccountInfo;
import com.handy.portal.model.payments.DebitCardInfo;
import com.handy.portal.model.payments.StripeTokenResponse;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class StripeManager //TODO: should we consolidate this with PaymentsManager?
{
    private final static String STRIPE_API_KEY = "pk_AdAZ6Xac3qjOGZPIPBxAiVFxoocj4"; //TODO: move to config file
    private final Bus bus;
    private final DataManager dataManager;

    private class RequestStripeTokenKeys{//TODO: clean this up/refactor
        private final static String BANK_ACCOUNT = "bank_account";
        public final static String BANK_ACCOUNT_COUNTRY = BANK_ACCOUNT + "[country]";
        public final static String BANK_ACCOUNT_CURRENCY = BANK_ACCOUNT + "[currency]";
        public final static String BANK_ACCOUNT_ROUTING_NUMBER = BANK_ACCOUNT + "[routing_number]";
        public final static String BANK_ACCOUNT_ACCOUNT_NUMBER = BANK_ACCOUNT + "[account_number]";

        private final static String CARD = "card";
        public final static String CARD_NUMBER = CARD + "[number]";
        public final static String CARD_EXP_MONTH = CARD + "[exp_month]";
        public final static String CARD_EXP_YEAR = CARD + "[exp_year]";
        public final static String CARD_CVC = CARD + "[cvc]";

        public final static String API_KEY = "key";
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
    public void onRequestStripeTokenFromBankAccount(final StripeEvents.RequestStripeTokenFromBankAccount event)
    {
        dataManager.getStripeToken(buildParamsFromBankAccountInfo(event.bankAccountInfo), new DataManager.Callback<StripeTokenResponse>()
        {
            @Override
            public void onSuccess(StripeTokenResponse response)
            {
                bus.post(new StripeEvents.ReceiveStripeTokenFromBankAccountSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new StripeEvents.ReceiveStripeTokenFromBankAccountError(error));

            }
        });
    }

    @Subscribe
    public void onRequestStripeTokenFromDebitCard(final StripeEvents.RequestStripeTokenFromDebitCard event)
    {
        dataManager.getStripeToken(buildParamsFromDebitCardInfo(event.debitCardInfo), new DataManager.Callback<StripeTokenResponse>()
        {
            @Override
            public void onSuccess(StripeTokenResponse response)
            {
                bus.post(new StripeEvents.ReceiveStripeTokenFromDebitCardSuccess(response, event.requestIdentifier));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new StripeEvents.ReceiveStripeTokenFromDebitCardError(error));

            }
        });
    }

    private Map<String, String> buildParamsFromDebitCardInfo(DebitCardInfo debitCardInfo)
    {
        Map<String, String> params = new HashMap<>();
        params.put(RequestStripeTokenKeys.CARD_NUMBER, debitCardInfo.getCardNumber());
        params.put(RequestStripeTokenKeys.CARD_EXP_MONTH, debitCardInfo.getExpMonth());
        params.put(RequestStripeTokenKeys.CARD_EXP_YEAR, debitCardInfo.getExpYear());
        params.put(RequestStripeTokenKeys.CARD_CVC, debitCardInfo.getCvc());
        params.put(RequestStripeTokenKeys.API_KEY, STRIPE_API_KEY);
        return params;
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
