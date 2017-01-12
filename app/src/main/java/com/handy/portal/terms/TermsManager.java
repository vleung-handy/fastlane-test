package com.handy.portal.terms;

import com.handy.portal.data.DataManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class TermsManager
{
    private final DataManager dataManager;
    private final EventBus bus;

    @Inject
    public TermsManager(final EventBus bus, final DataManager dataManager)
    {
        this.dataManager = dataManager;
        this.bus = bus;
        this.bus.register(this);
    }

    @Subscribe
    public void onAcceptTerms(final TermsEvent.AcceptTerms event)
    {
        dataManager.acceptTerms(event.getTermsDetails().getCode(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(Void response)
                    {
                        bus.post(new TermsEvent.AcceptTermsSuccess(event.getTermsDetails().getCode()));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new TermsEvent.AcceptTermsError());
                    }
                });
    }
}
