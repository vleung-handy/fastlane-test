package com.handy.portal.updater;

import android.support.annotation.NonNull;

import com.handy.portal.event.HandyEvent;
import com.handy.portal.updater.model.UpdateDetails;
import com.squareup.otto.Subscribe;

/**
 * currently used by BaseActivity as a bus event listener
 * because BaseActivity is abstract and cannot be registered to the bus
 */
public class AppUpdateEventListener
{
    private AppUpdateFlowLauncher mAppUpdateFlowLauncher;
    public AppUpdateEventListener(@NonNull AppUpdateFlowLauncher appUpdateFlowLauncher)
    {
        mAppUpdateFlowLauncher = appUpdateFlowLauncher;
    }

    @Subscribe
    public void onReceiveUpdateAvailableSuccess(AppUpdateEvent.ReceiveUpdateAvailableSuccess event)
    {
        //TODO: splash activity and please update activity currently override the launch app updater function to make it do nothing
        //we should have a more elegant way of disabling that flow

        UpdateDetails updateDetails = event.updateDetails;
        if (updateDetails.getSuccess() && updateDetails.getShouldUpdate()) //TODO: there seems to be a lot of redundant updateDetails.getShouldUpdate() calls. clean this up
        {
            if(updateDetails.isUpdateBlocking())
            {
                mAppUpdateFlowLauncher.launchAppUpdater();
            }
            else
            {
                mAppUpdateFlowLauncher.showAppUpdateAvailableDialog();
            }
        }
    }

    @Subscribe
    public void onReceiveUpdateAvailableError(AppUpdateEvent.ReceiveUpdateAvailableError event)
    {
        String message = event.error.getMessage();
        if (message != null)
        {
            mAppUpdateFlowLauncher.showAppUpdateFlowError(message);
        }
    }

    @Subscribe
    public void onRequestEnableRequiredUpdateFlowApplication(HandyEvent.RequestEnableApplication event)
    {
        mAppUpdateFlowLauncher.launchEnableRequiredUpdateFlowApplicationIntent(event.packageName, event.infoMessage);
    }
}
