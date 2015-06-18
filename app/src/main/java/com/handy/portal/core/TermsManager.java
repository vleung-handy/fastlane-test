package com.handy.portal.core;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class TermsManager
{
    private final DataManager dataManager;
    private final Bus bus;

    private TermsDetails newestTermsDetails;

    @Inject
    TermsManager(final Bus bus, final DataManager dataManager)
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
    public void onCheckTermsRequest(Event.CheckTermsRequestEvent event)
    {
        dataManager.checkForTerms(
                new DataManager.Callback<TermsDetails>()
                {
                    @Override
                    public void onSuccess(TermsDetails termsDetails)
                    {
                        newestTermsDetails = termsDetails;
                        bus.post(new Event.CheckTermsResponseEvent(termsDetails));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new Event.CheckTermsErrorEvent());
                    }
                });
    }

    @Subscribe
    public void onAcceptTerms(Event.AcceptTermsEvent event)
    {
        dataManager.acceptTerms(event.termsDetails.getCode(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(Void response)
                    {
                        newestTermsDetails = null;
                        bus.post(new Event.AcceptTermsSuccessEvent());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new Event.AcceptTermsErrorEvent());
                    }
                });
    }
}
