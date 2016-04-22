package com.handy.portal.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.PaymentEvent;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.CreateDebitCardResponse;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.model.payments.PaymentGroup;
import com.handy.portal.model.payments.PaymentOutstandingFees;
import com.handy.portal.model.payments.RequiresPaymentInfoUpdate;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class PaymentsManager
{
    private final Bus mBus;
    private final DataManager mDataManager;

    //TODO: We're using a cache for what is currently one value, maybe look into Guava Suppliers in future
    private Cache<String, Boolean> mNeedsUpdatedPaymentInformationCache;
    private static final String NEEDS_UPDATED_PAYMENT_CACHE_KEY = "needs_updated_payment";

    public boolean HACK_directAccessCacheNeedsPayment()
    {
        Boolean cachedValue = mNeedsUpdatedPaymentInformationCache.getIfPresent(NEEDS_UPDATED_PAYMENT_CACHE_KEY);
        if (cachedValue != null)
        {
            return cachedValue;
        }
        return false;
    }

    @Inject
    public PaymentsManager(final Bus bus, final DataManager dataManager)
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mNeedsUpdatedPaymentInformationCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
    }

    @Subscribe
    public void onRequestShouldUserUpdatePaymentInfo(PaymentEvent.RequestShouldUserUpdatePaymentInfo event)
    {
        Boolean cachedValue = mNeedsUpdatedPaymentInformationCache.getIfPresent(NEEDS_UPDATED_PAYMENT_CACHE_KEY);
        if (cachedValue != null)
        {
            mBus.post(new PaymentEvent.ReceiveShouldUserUpdatePaymentInfoSuccess(cachedValue));
        }
        else
        {
            mDataManager.getNeedsToUpdatePaymentInfo(new DataManager.Callback<RequiresPaymentInfoUpdate>()
            {
                @Override
                public void onSuccess(RequiresPaymentInfoUpdate response)
                {
                    mBus.post(new PaymentEvent.ReceiveShouldUserUpdatePaymentInfoSuccess(response.getNeedsUpdate()));
                    mNeedsUpdatedPaymentInformationCache.put(NEEDS_UPDATED_PAYMENT_CACHE_KEY, response.getNeedsUpdate());
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    mBus.post(new PaymentEvent.ReceiveShouldUserUpdatePaymentInfoError(error));
                }
            });
        }
    }

    @Subscribe
    public void onRequestPaymentBatches(final PaymentEvent.RequestPaymentBatches event)
    {
        //assuming startDate is inclusive and endDate is inclusive
        mDataManager.getPaymentBatches(event.startDate, event.endDate, new DataManager.Callback<PaymentBatches>()
        {
            @Override
            public void onSuccess(PaymentBatches paymentBatches)
            {
                //for now, filter non-legacy payment batches to remove empty groups until server side changes are made
                NeoPaymentBatch neoPaymentBatches[] = paymentBatches.getNeoPaymentBatches();
                for (int i = 0; i < neoPaymentBatches.length; i++)
                {
                    PaymentGroup paymentGroups[] = neoPaymentBatches[i].getPaymentGroups();
                    List<PaymentGroup> paymentGroupList = new LinkedList<>();
                    for (int j = 0; j < paymentGroups.length; j++)
                    {
                        if (paymentGroups[j].getPayments() != null && paymentGroups[j].getPayments().length > 0)
                        {
                            paymentGroupList.add(paymentGroups[j]);
                        }
                    }
                    neoPaymentBatches[i].setPaymentGroups(paymentGroupList.toArray(new PaymentGroup[paymentGroupList.size()]));
                }
                mBus.post(new PaymentEvent.ReceivePaymentBatchesSuccess(paymentBatches, event.startDate, event.endDate, event.isInitialBatchRequest, event.callerIdentifier));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new PaymentEvent.ReceivePaymentBatchesError(error));
            }
        });
    }

    @Subscribe
    public void onRequestAnnualPaymentSummaries(final PaymentEvent.RequestAnnualPaymentSummaries event)
    {
        mDataManager.getAnnualPaymentSummaries(new DataManager.Callback<AnnualPaymentSummaries>()
        {
            @Override
            public void onSuccess(AnnualPaymentSummaries annualPaymentSummaries)
            {
                mBus.post(new PaymentEvent.ReceiveAnnualPaymentSummariesSuccess(annualPaymentSummaries));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new PaymentEvent.ReceiveAnnualPaymentSummariesError(error));
            }
        });
    }

    @Subscribe
    public void onRequestPaymentOutstandingFees(final PaymentEvent.RequestPaymentOutstandingFees event)
    {
        mDataManager.getPaymentOutstandingFees(new DataManager.Callback<PaymentOutstandingFees>()
        {
            @Override
            public void onSuccess(final PaymentOutstandingFees response)
            {
                mBus.post(new PaymentEvent.ReceivePaymentOutstandingFeesSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new PaymentEvent.ReceivePaymentOutstandingFeesError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateBankAccount(final PaymentEvent.RequestCreateBankAccount event)
    {
        mDataManager.createBankAccount(buildParamsForCreateBankAccount(event.stripeToken, event.taxId, event.accountNumberLast4Digits), new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper successWrapper)
            {
                mBus.post(new PaymentEvent.ReceiveCreateBankAccountSuccess(successWrapper.getSuccess()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new PaymentEvent.ReceiveCreateBankAccountError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateDebitCardRecipient(final PaymentEvent.RequestCreateDebitCardRecipient event)
    {
        mDataManager.createDebitCardRecipient(buildParamsForDebitCardRecipient(event.stripeToken, event.taxId, event.cardNumberLast4Digits, event.expMonth, event.expYear), new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper successWrapper)
            {
                mBus.post(new PaymentEvent.ReceiveCreateDebitCardRecipientSuccess(successWrapper.getSuccess()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new PaymentEvent.ReceiveCreateDebitCardRecipientError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateDebitCardForCharge(final PaymentEvent.RequestCreateDebitCardForCharge event)
    {
        mDataManager.createDebitCardForCharge(event.stripeToken, new DataManager.Callback<CreateDebitCardResponse>()
        {
            @Override
            public void onSuccess(CreateDebitCardResponse response)
            {
                mBus.post(new PaymentEvent.ReceiveCreateDebitCardForChargeSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new PaymentEvent.ReceiveCreateDebitCardForChargeError(error));
            }
        });
    }

    private final class ParamKeys
    {
        static final String STRIPE_TOKEN = "token";
        static final String TAX_ID = "tax_id";
        static final String ACCOUNT_NUMBER_LAST4_DIGITS = "last4";
        static final String EXP_MONTH = "exp_month";
        static final String EXP_YEAR = "exp_year";
        static final String ACCOUNT_TYPE = "account_type";
    }


    private final class PaymentMethodAccountType
    {
        static final String DEBIT_CARD = "debit_card";
        static final String BANK_ACCOUNT = "bank_account";
    }

    private Map<String, String> buildParamsForDebitCardRecipient(String stripeToken, String taxId, String cardNumberLast4Digits, String expMonth, String expYear)
    {
        Map<String, String> params = new HashMap<>();
        params.put(ParamKeys.STRIPE_TOKEN, stripeToken);
        params.put(ParamKeys.TAX_ID, taxId);
        params.put(ParamKeys.ACCOUNT_NUMBER_LAST4_DIGITS, cardNumberLast4Digits);
        params.put(ParamKeys.EXP_MONTH, expMonth);
        params.put(ParamKeys.EXP_YEAR, expYear);
        params.put(ParamKeys.ACCOUNT_TYPE, PaymentMethodAccountType.DEBIT_CARD);
        return params;
    }

    private Map<String, String> buildParamsForCreateBankAccount(String stripeToken, String taxId, String accountNumberLast4Digits)
    {
        Map<String, String> params = new HashMap<>();
        params.put(ParamKeys.STRIPE_TOKEN, stripeToken);
        params.put(ParamKeys.TAX_ID, taxId);
        params.put(ParamKeys.ACCOUNT_NUMBER_LAST4_DIGITS, accountNumberLast4Digits);
        params.put(ParamKeys.ACCOUNT_TYPE, PaymentMethodAccountType.BANK_ACCOUNT);
        return params;
    }
}
