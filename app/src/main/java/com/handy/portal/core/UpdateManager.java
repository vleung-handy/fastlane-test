package com.handy.portal.core;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public final class UpdateManager
{
    private final Bus bus;
    private DataManager dataManager;

    private String downloadURL =  "";

    @Inject
    UpdateManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    @Subscribe
    public void onUpdateCheckRequest(Event.UpdateCheckEvent event)
    {
        dataManager.checkForUpdates(event.appFlavor, event.versionCode, new DataManager.Callback<UpdateDetails>()
                {
                    @Override
                    public void onSuccess(final UpdateDetails updateDetails)
                    {
                        downloadURL = updateDetails.getDownloadUrl();
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
