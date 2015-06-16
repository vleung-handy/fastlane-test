package com.handy.portal.core;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.handy.portal.data.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class UpdateManager
{
    private final Bus bus;
    private final BuildConfigWrapper buildConfigWrapper;
    private DataManager dataManager;


    private String downloadURL =  "";

    @Inject
    UpdateManager(final Bus bus, final DataManager dataManager, final BuildConfigWrapper buildConfigWrapper)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
        this.buildConfigWrapper = buildConfigWrapper;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    @Subscribe
    public void onUpdateCheckRequest(Event.UpdateCheckEvent event)
    {

        PackageInfo pkgInfo = getPackageInfoFromActivity(event.sender);

        dataManager.checkForUpdates(buildConfigWrapper.getFlavor(), pkgInfo.versionCode, new DataManager.Callback<UpdateDetails>()
            {
                @Override
                public void onSuccess(final UpdateDetails updateDetails)
                {
                    downloadURL = updateDetails.getDownloadUrl();
                    if (updateDetails != null && updateDetails.getShouldUpdate()) {
                        bus.post(new Event.UpdateAvailable());
                    }

                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    //TODO: ERROR MESSAGE
                }
            }
        );
    }

    private PackageInfo getPackageInfoFromActivity(Activity sender) {
        PackageInfo pInfo = null;
        try
        {
            pInfo = sender.getPackageManager().getPackageInfo(sender.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException();
        }
        return pInfo;
    }


}
