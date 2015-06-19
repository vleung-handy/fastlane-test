package com.handy.portal.core;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.handy.portal.data.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import javax.inject.Inject;

public class VersionManager
{
    private final Bus bus;
    private final BuildConfigWrapper buildConfigWrapper;
    private DataManager dataManager;


    private String downloadURL =  "";

    @Inject
    VersionManager(final Bus bus, final DataManager dataManager, final BuildConfigWrapper buildConfigWrapper)
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

    @Subscribe
    public void onApplicationResumed(Event.ApplicationResumed event) {

        PackageInfo pInfo = getPackageInfoFromActivity(event.sender);

        HashMap<String, String> info = new HashMap<>();
        info.put("user_id", dataManager.getProviderId());
        info.put("platform", "android");
        info.put("app_identifier", event.sender.getPackageName());
        info.put("app_version", pInfo.versionName);
        info.put("app_version_code", Integer.toString(pInfo.versionCode));
        info.put("app_flavor",buildConfigWrapper.getFlavor());
        info.put("os_version", android.os.Build.VERSION.RELEASE);
        info.put("os_version_code", Integer.toString(android.os.Build.VERSION.SDK_INT));

        dataManager.sendVersionInformation(info, new DataManager.Callback<SimpleResponse>()
                {
                    @Override
                    public void onSuccess(final SimpleResponse updateDetails)
                    {
                        //Do nothing
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        //Do nothing
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
