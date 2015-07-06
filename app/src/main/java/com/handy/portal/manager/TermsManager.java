package com.handy.portal.manager;

import com.handy.portal.model.TermsDetails;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class TermsManager
{
    private final DataManager dataManager;
    private final Bus bus;

    private TermsDetails newestTermsDetails;

    @Inject
    public TermsManager(final Bus bus, final DataManager dataManager)
    {
        this.dataManager = dataManager;
        this.bus = bus;
        this.bus.register(this);
    }

    public TermsDetails getNewestTermsDetails()
    {
        return newestTermsDetails;
    }

    @Subscribe
    public void onCheckTermsRequest(HandyEvent.RequestCheckTerms event)
    {
        dataManager.checkForTerms(
                new DataManager.Callback<TermsDetails>()
                {
                    @Override
                    public void onSuccess(TermsDetails termsDetails)
                    {
                        newestTermsDetails = termsDetails;
                        bus.post(new HandyEvent.ReceiveCheckTermsSuccess(termsDetails));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        newestTermsDetails = null;
                        bus.post(new HandyEvent.ReceiveCheckTermsError());
                    }
                });
    }

    @Subscribe
    public void onAcceptTerms(HandyEvent.AcceptTerms event)
    {
        dataManager.acceptTerms(event.termsDetails.getCode(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(Void response)
                    {
                        newestTermsDetails = null;
                        bus.post(new HandyEvent.AcceptTermsSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.AcceptTermsError());
                    }
                });
    }
}
