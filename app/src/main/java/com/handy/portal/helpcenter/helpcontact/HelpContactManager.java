package com.handy.portal.helpcenter.helpcontact;

import com.handy.portal.data.DataManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import okhttp3.RequestBody;

public class HelpContactManager
{
    private final Bus bus;
    private final DataManager dataManager;

    @Inject
    public HelpContactManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
    }

    @Subscribe
    public void onRequestNotifyHelpContact(HelpContactEvent.RequestNotifyHelpContact event)
    {
        RequestBody body = event.body;

        dataManager.createHelpCase(body, new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(Void response)
            {
                bus.post(new HelpContactEvent.ReceiveNotifyHelpContactSuccess());
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HelpContactEvent.ReceiveNotifyHelpContactError(error));
            }
        });
    }
}
