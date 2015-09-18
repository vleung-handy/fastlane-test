package com.handy.portal.manager;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.PaymentEvents;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.PaymentBatches;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;

import javax.inject.Inject;

public class PaymentsManager
{
    private final Bus bus;
    private final DataManager dataManager;

    //TODO: add caching

    @Inject
    public PaymentsManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;

    }

    @Subscribe
    public void onRequestPaymentBatches(final PaymentEvents.RequestPaymentBatches event)
    {
        Date startDate = event.startDate;
        Date endDate = event.endDate;
        dataManager.getPaymentBatches(startDate, endDate, new DataManager.Callback<PaymentBatches>()
        {
            @Override
            public void onSuccess(PaymentBatches paymentBatches)
            {
                if(paymentBatches!=null)
                {
                    //for now, filter payment batches to remove empty groups until server side changes are made
                    bus.post(new PaymentEvents.ReceivePaymentBatchesSuccess(paymentBatches));
                }
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvents.ReceivePaymentBatchesError()); //TODO: fill with error
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
                if (annualPaymentSummaries != null)
                    bus.post(new PaymentEvents.ReceiveAnnualPaymentSummariesSuccess(annualPaymentSummaries));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new PaymentEvents.ReceiveAnnualPaymentSummariesError()); //TODO: fill with error
            }
        });
    }


}
