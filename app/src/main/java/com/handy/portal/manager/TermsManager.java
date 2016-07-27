package com.handy.portal.manager;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.TermsLog;

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
                        bus.post(new LogEvent.AddLogEvent(new TermsLog.Error(error.getMessage())));
                    }
                });
    }
}
