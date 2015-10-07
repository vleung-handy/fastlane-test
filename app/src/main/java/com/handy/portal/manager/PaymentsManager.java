package com.handy.portal.manager;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.PaymentEvents;
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

import java.util.Date;
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
    public void onRequestShouldUserUpdatePaymentInfo(PaymentEvents.RequestShouldUserUpdatePaymentInfo event)
    {
        if (System.currentTimeMillis() - timestampRequestPaymentInfoUpdateNeeded > INTERVAL_REQUEST_PAYMENT_INFO_UPDATED_NEEDED_MS)
        {
            timestampRequestPaymentInfoUpdateNeeded = System.currentTimeMillis();
            dataManager.getNeedsToUpdatePaymentInfo(new DataManager.Callback<RequiresPaymentInfoUpdate>()
            {
                @Override
                public void onSuccess(RequiresPaymentInfoUpdate response)
                {
                    bus.post(new PaymentEvents.ReceiveShouldUserUpdatePaymentInfoSuccess(response.getNeedsUpdate()));
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    bus.post(new PaymentEvents.ReceiveShouldUserUpdatePaymentInfoError(error));
                }
            });
        }
        else
        {
            bus.post(new PaymentEvents.ReceiveShouldUserUpdatePaymentInfoSuccess(false));
        }
    }

    @Subscribe
    public void onRequestPaymentBatches(final PaymentEvents.RequestPaymentBatches event)
    {
        final Date startDate = event.startDate;
        Date endDate = event.endDate;
        //assuming startDate is inclusive and endDate is inclusive
        dataManager.getPaymentBatches(startDate, endDate, new DataManager.Callback<PaymentBatches>()
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
                bus.post(new PaymentEvents.ReceivePaymentBatchesSuccess(paymentBatches, startDate, event.isInitialBatchRequest, event.callerIdentifier));

            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvents.ReceivePaymentBatchesError(error));
            }
        });
    }

    @Subscribe
    public void onRequestAnnualPaymentSummaries(final PaymentEvents.RequestAnnualPaymentSummaries event)
    {
        dataManager.getAnnualPaymentSummaries(new DataManager.Callback<AnnualPaymentSummaries>()
        {
            @Override
            public void onSuccess(AnnualPaymentSummaries annualPaymentSummaries)
            {
                bus.post(new PaymentEvents.ReceiveAnnualPaymentSummariesSuccess(annualPaymentSummaries));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvents.ReceiveAnnualPaymentSummariesError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateBankAccount(final PaymentEvents.RequestCreateBankAccount event)
    {
        dataManager.createBankAccount(buildParamsForCreateBankAccount(event.stripeToken, event.taxId, event.accountNumberLast4Digits), new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper successWrapper)
            {
                bus.post(new PaymentEvents.ReceiveCreateBankAccountSuccess(successWrapper.getSuccess()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvents.ReceiveCreateBankAccountError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateDebitCardRecipient(final PaymentEvents.RequestCreateDebitCardRecipient event)
    {
        dataManager.createDebitCardRecipient(buildParamsForDebitCardRecipient(event.stripeToken, event.taxId, event.cardNumberLast4Digits, event.expMonth, event.expYear), new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper successWrapper)
            {
                bus.post(new PaymentEvents.ReceiveCreateDebitCardRecipientSuccess(successWrapper.getSuccess()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvents.ReceiveCreateDebitCardRecipientError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCreateDebitCardForCharge(final PaymentEvents.RequestCreateDebitCardForCharge event)
    {
        dataManager.createDebitCardForCharge(event.stripeToken, new DataManager.Callback<CreateDebitCardResponse>()
        {
            @Override
            public void onSuccess(CreateDebitCardResponse response)
            {
                bus.post(new PaymentEvents.ReceiveCreateDebitCardForChargeSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvents.ReceiveCreateDebitCardForChargeError(error));
            }
        });
    }

    //TODO: clean this up. parameterize
    private Map<String, String> buildParamsForDebitCardRecipient(String stripeToken, String taxId, String cardNumberLast4Digits, String expMonth, String expYear)
    {
        Map<String, String> params = new HashMap<>();
        params.put("token", stripeToken);
        params.put("tax_id", taxId);
        params.put("last4", cardNumberLast4Digits);
        params.put("exp_month", expMonth);
        params.put("exp_year", expYear);
        params.put("account_type", "debit_card");
        return params;
    }

    private Map<String, String> buildParamsForCreateBankAccount(String stripeToken, String taxId, String accountNumberLast4Digits)
    {
        Map<String, String> params = new HashMap<>();
        params.put("token", stripeToken);
        params.put("tax_id", taxId);
        params.put("last4", accountNumberLast4Digits);
        params.put("account_type", "bank_account");
        return params;
    }
}
