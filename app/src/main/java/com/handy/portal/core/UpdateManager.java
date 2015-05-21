package com.handy.portal.core;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public final class UpdateManager implements Observer
{
    private final Bus bus;
    private DataManager dataManager;

    @Inject
    UpdateManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
    }

    @Override
    public void update(final Observable observable, final Object data)
    {
        if (observable instanceof User)
        {

        }
    }



    @Subscribe
    public void onUpdateCheckRequest(Event.UpdateCheckEvent event)
    {
        dataManager.checkForUpdates(new DataManager.Callback<UpdateDetails>()
                {
                    @Override
                    public void onSuccess(final UpdateDetails updateDetails)
                    {
                        bus.post(new Event.UpdateCheckRequestReceivedEvent(updateDetails, true));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        bus.post(new Event.UpdateCheckRequestReceivedEvent(new UpdateDetails(), false));
                    }
                }
        );
    }

}
