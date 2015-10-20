package com.handy.portal.manager;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.PaymentEvent;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.CreateDebitCardResponse;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.model.payments.PaymentGroup;
import com.handy.portal.model.payments.RequiresPaymentInfoUpdate;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class PaymentsManager
{
    private final Bus bus;
    private final DataManager dataManager;
    //TODO: add caching when new payments, pagination api comes out

    private long timestampRequestPaymentInfoUpdateNeeded = 0;
    private final long INTERVAL_REQUEST_PAYMENT_INFO_UPDATED_NEEDED_MS = DateTimeUtils.MILLISECONDS_IN_HOUR;
    //TODO: use a formal/common system? find a better place to put this

    @Inject
    public PaymentsManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;

    }

    @Subscribe
    public void onRequestShouldUserUpdatePaymentInfo(PaymentEvent.RequestShouldUserUpdatePaymentInfo event)
    {
        if (System.currentTimeMillis() - timestampRequestPaymentInfoUpdateNeeded > INTERVAL_REQUEST_PAYMENT_INFO_UPDATED_NEEDED_MS)
        {
            timestampRequestPaymentInfoUpdateNeeded = System.currentTimeMillis();
            dataManager.getNeedsToUpdatePaymentInfo(new DataManager.Callback<RequiresPaymentInfoUpdate>()
            {
                @Override
                public void onSuccess(RequiresPaymentInfoUpdate response)
                {
                    bus.post(new PaymentEvent.ReceiveShouldUserUpdatePaymentInfoSuccess(response.getNeedsUpdate()));
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    bus.post(new PaymentEvent.ReceiveShouldUserUpdatePaymentInfoError(error));
                }
            });
        }
        else
        {
            bus.post(new PaymentEvent.ReceiveShouldUserUpdatePaymentInfoSuccess(false));
        }
    }

    @Subscribe
    public void onRequestPaymentBatches(final PaymentEvent.RequestPaymentBatches event)
    {
        //assuming startDate is inclusive and endDate is inclusive
        dataManager.getPaymentBatches(event.startDate, event.endDate, new DataManager.Callback<PaymentBatches>()
        {
            @Override
            public void onSuccess(PaymentBatches paymentBatches)
            {
                //for now, filter non-legacy payment batches to remove empty groups until server side changes are made
                NeoPaymentBatch neoPaymentBatches[] = paymentBatches.getNeoPaymentBatches();
                for (int i = 0; i < neoPaymentBatches.length; i++)
                {
                    PaymentGroup paymentGroups[] = neoPaymentBatches[i].getPaymentGroups();
                    List<PaymentGroup> paymentGroupList = new LinkedList<PaymentGroup>();
                    for (int j = 0; j < paymentGroups.length; j++)
                    {
                        if (paymentGroups[j].getPayments() != null && paymentGroups[j].getPayments().length > 0)
                        {
                            paymentGroupList.add(paymentGroups[j]);
                        }
                    }
                    neoPaymentBatches[i].setPaymentGroups(paymentGroupList.toArray(new PaymentGroup[]{}));

                }
                bus.post(new PaymentEvent.ReceivePaymentBatchesSuccess(paymentBatches, event.startDate, event.endDate, event.isInitialBatchRequest, event.callerIdentifier));

            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvent.ReceivePaymentBatchesError(error));
            }
        });
    }

    @Subscribe
    public void onRequestAnnualPaymentSummaries(final PaymentEvent.RequestAnnualPaymentSummaries event)
    {
        dataManager.getAnnualPaymentSummaries(new DataManager.Callback<AnnualPaymentSummaries>()
        {
            @Override
            public void onSuccess(AnnualPaymentSummaries annualPaymentSummaries)
            {
                bus.post(new PaymentEvent.ReceiveAnnualPaymentSummariesSuccess(annualPaymentSummaries));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvent.ReceiveAnnualPaymentSummariesError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateBankAccount(final PaymentEvent.RequestCreateBankAccount event)
    {
        dataManager.createBankAccount(buildParamsForCreateBankAccount(event.stripeToken, event.taxId, event.accountNumberLast4Digits), new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper successWrapper)
            {
                bus.post(new PaymentEvent.ReceiveCreateBankAccountSuccess(successWrapper.getSuccess()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvent.ReceiveCreateBankAccountError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateDebitCardRecipient(final PaymentEvent.RequestCreateDebitCardRecipient event)
    {
        dataManager.createDebitCardRecipient(buildParamsForDebitCardRecipient(event.stripeToken, event.taxId, event.cardNumberLast4Digits, event.expMonth, event.expYear), new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper successWrapper)
            {
                bus.post(new PaymentEvent.ReceiveCreateDebitCardRecipientSuccess(successWrapper.getSuccess()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvent.ReceiveCreateDebitCardRecipientError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateDebitCardForCharge(final PaymentEvent.RequestCreateDebitCardForCharge event)
    {
        dataManager.createDebitCardForCharge(event.stripeToken, new DataManager.Callback<CreateDebitCardResponse>()
        {
            @Override
            public void onSuccess(CreateDebitCardResponse response)
            {
                bus.post(new PaymentEvent.ReceiveCreateDebitCardForChargeSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvent.ReceiveCreateDebitCardForChargeError(error));
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
