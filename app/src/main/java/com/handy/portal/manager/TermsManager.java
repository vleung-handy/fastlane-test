package com.handy.portal.manager;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.TermsDetailsGroup;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class TermsManager
{
    private final DataManager dataManager;
    private final Bus bus;

    private TermsDetailsGroup newestTermsDetailsGroup;

    @Inject
    public TermsManager(final Bus bus, final DataManager dataManager)
    {
        this.dataManager = dataManager;
        this.bus = bus;
        this.bus.register(this);
    }

    public TermsDetailsGroup getNewestTermsDetailsGroup()
    {
        return newestTermsDetailsGroup;
    }

    @Subscribe
    public void onCheckTermsRequest(HandyEvent.RequestCheckTerms event)
    {
        dataManager.checkForAllPendingTerms(
                new DataManager.Callback<TermsDetailsGroup>()
                {
                    @Override
                    public void onSuccess(TermsDetailsGroup termsDetailsGroup)
                    {
                        newestTermsDetailsGroup = termsDetailsGroup;
                        bus.post(new HandyEvent.ReceiveCheckTermsSuccess(termsDetailsGroup));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        newestTermsDetailsGroup = null;
                        bus.post(new HandyEvent.ReceiveCheckTermsError());
                    }
                });
    }

    @Subscribe
    public void onAcceptTerms(final HandyEvent.AcceptTerms event)
    {
        dataManager.acceptTerms(event.termsDetails.getCode(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(Void response)
                    {
                        bus.post(new HandyEvent.AcceptTermsSuccess(event.termsDetails.getCode()));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.AcceptTermsError());
                    }
                });
    }
}
