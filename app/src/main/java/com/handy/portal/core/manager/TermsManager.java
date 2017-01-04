package com.handy.portal.core.manager;

import com.handy.portal.core.event.HandyEvent;
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
