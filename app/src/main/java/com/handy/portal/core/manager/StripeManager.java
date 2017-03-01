package com.handy.portal.core.manager;


import android.content.Context;

import com.handy.portal.core.constant.Country;
import com.handy.portal.core.event.StripeEvent;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.payments.model.BankAccountInfo;
import com.handy.portal.payments.model.DebitCardInfo;
import com.handy.portal.payments.model.StripeTokenResponse;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class StripeManager //TODO: should we consolidate this with PaymentsManager?
{
    private final String STRIPE_API_KEY_US;
    private final String STRIPE_API_KEY_GB;
    private final String STRIPE_API_KEY_CA;
    private final EventBus bus;
    private final DataManager dataManager;


    private final class RequestStripeTokenKeys {
        private static final String API_KEY = "key";


        private final class BankAccount {
            private static final String BANK_ACCOUNT = "bank_account";
            private static final String BANK_ACCOUNT_COUNTRY = BANK_ACCOUNT + "[country]";
            private static final String BANK_ACCOUNT_CURRENCY = BANK_ACCOUNT + "[currency]";
            private static final String BANK_ACCOUNT_ROUTING_NUMBER = BANK_ACCOUNT + "[routing_number]";
            private static final String BANK_ACCOUNT_ACCOUNT_NUMBER = BANK_ACCOUNT + "[account_number]";
        }


        private final class DebitCard {
            private static final String CARD = "card";
            private static final String CARD_NUMBER = CARD + "[number]";
            private static final String CARD_EXP_MONTH = CARD + "[exp_month]";
            private static final String CARD_EXP_YEAR = CARD + "[exp_year]";
            private static final String CARD_CVC = CARD + "[cvc]";
            private static final String CARD_CURRENCY_CODE = CARD + "[currency]";
        }
    }

    @Inject
    public StripeManager(final Context context, final EventBus bus, final DataManager dataManager) {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;

        STRIPE_API_KEY_US = PropertiesReader.getConfigProperties(context).getProperty("stripe_api_key_us");
        STRIPE_API_KEY_GB = PropertiesReader.getConfigProperties(context).getProperty("stripe_api_key_gb");
        STRIPE_API_KEY_CA = PropertiesReader.getConfigProperties(context).getProperty("stripe_api_key_ca");
    }

    @Subscribe
    public void onRequestStripeTokenFromBankAccount(final StripeEvent.RequestStripeTokenFromBankAccount event) {
        dataManager.getStripeToken(buildParamsFromBankAccountInfo(event.bankAccountInfo), new DataManager.Callback<StripeTokenResponse>() {
            @Override
            public void onSuccess(StripeTokenResponse response) {
                bus.post(new StripeEvent.ReceiveStripeTokenFromBankAccountSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                bus.post(new StripeEvent.ReceiveStripeTokenFromBankAccountError(error));

            }
        });
    }

    @Subscribe
    public void onRequestStripeTokenFromDebitCard(final StripeEvent.RequestStripeTokenFromDebitCard event) {
        dataManager.getStripeToken(buildParamsFromDebitCardInfo(event.debitCardInfo), new DataManager.Callback<StripeTokenResponse>() {
            @Override
            public void onSuccess(StripeTokenResponse response) {
                bus.post(new StripeEvent.ReceiveStripeTokenFromDebitCardSuccess(response, event.requestIdentifier));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                bus.post(new StripeEvent.ReceiveStripeTokenFromDebitCardError(error));

            }
        });
    }

    @Subscribe
    public void onRequestStripeChargeToken(final StripeEvent.RequestStripeChargeToken event) {
        final String stripeApiKey = pickStripeApiKey(event.getCountry().toLowerCase());
        new Stripe().createToken(event.getCard(), stripeApiKey, new TokenCallback() {
            @Override
            public void onSuccess(final Token token) {
                bus.post(new StripeEvent.ReceiveStripeChargeTokenSuccess(token));
            }

            @Override
            public void onError(final Exception error) {
                bus.post(new StripeEvent.ReceiveStripeChargeTokenError(error));
            }
        });
    }

    private Map<String, String> buildParamsFromDebitCardInfo(DebitCardInfo debitCardInfo) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestStripeTokenKeys.DebitCard.CARD_NUMBER, debitCardInfo.getCardNumber());
        params.put(RequestStripeTokenKeys.DebitCard.CARD_EXP_MONTH, debitCardInfo.getExpMonth());
        params.put(RequestStripeTokenKeys.DebitCard.CARD_EXP_YEAR, debitCardInfo.getExpYear());
        params.put(RequestStripeTokenKeys.DebitCard.CARD_CVC, debitCardInfo.getCvc());
        params.put(RequestStripeTokenKeys.DebitCard.CARD_CURRENCY_CODE, debitCardInfo.getCurrencyCode());
        params.put(RequestStripeTokenKeys.API_KEY, STRIPE_API_KEY_US);
        return params;
    }

    private Map<String, String> buildParamsFromBankAccountInfo(BankAccountInfo bankAccountInfo) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestStripeTokenKeys.BankAccount.BANK_ACCOUNT_ACCOUNT_NUMBER, bankAccountInfo.getAccountNumber());
        params.put(RequestStripeTokenKeys.BankAccount.BANK_ACCOUNT_COUNTRY, bankAccountInfo.getCountry());
        params.put(RequestStripeTokenKeys.BankAccount.BANK_ACCOUNT_CURRENCY, bankAccountInfo.getCurrency());
        params.put(RequestStripeTokenKeys.BankAccount.BANK_ACCOUNT_ROUTING_NUMBER, bankAccountInfo.getRoutingNumber());
        params.put(RequestStripeTokenKeys.API_KEY, pickStripeApiKey(bankAccountInfo.getCountry()));
        return params;
    }

    private String pickStripeApiKey(final String country) {
        if (Country.GB.equalsIgnoreCase(country)) { return STRIPE_API_KEY_GB; }
        else if (Country.CA.equalsIgnoreCase(country)) { return STRIPE_API_KEY_CA; }
        else { return STRIPE_API_KEY_US; }
    }

}
