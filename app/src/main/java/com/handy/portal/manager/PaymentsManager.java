package com.handy.portal.manager;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.PaymentEvents;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.model.payments.PaymentGroup;
import com.handy.portal.model.payments.RequiresPaymentInfoUpdate;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

public class PaymentsManager
{
    private final Bus bus;
    private final DataManager dataManager;
    //TODO: add caching when new payments, pagination api comes out

    private long timestampRequestPaymentInfoUpdateNeeded = 0;
    private final long intervalPromptUpdatePaymentInfoMs = DateTimeUtils.MILLISECONDS_IN_HOUR;
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
        if (System.currentTimeMillis() - timestampRequestPaymentInfoUpdateNeeded > intervalPromptUpdatePaymentInfoMs)
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
                bus.post(new PaymentEvents.ReceivePaymentBatchesSuccess(paymentBatches, startDate, event.callerIdentifier));

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


}
