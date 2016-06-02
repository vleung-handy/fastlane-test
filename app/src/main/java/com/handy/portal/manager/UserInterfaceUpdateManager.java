package com.handy.portal.manager;

import com.handy.portal.event.HandyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * manager for keeping track of UI update request events
 * to prevent funny UI bugs like loading screens getting stuck
 */
public class UserInterfaceUpdateManager
{
    private final EventBus mBus;
    private Boolean mLoadingOverlayVisibility;

    public UserInterfaceUpdateManager(final EventBus bus)
    {
        mBus = bus;
//        mBus.register(this);
    }

    @Subscribe
    public HandyEvent.SetLoadingOverlayVisibility produceSetLoadingOverlayVisibilityEvent()
    {
        if (mLoadingOverlayVisibility != null)
        {
            return new HandyEvent.SetLoadingOverlayVisibility(mLoadingOverlayVisibility);
        }
        return null;
    }

    @Subscribe
    public void onRequestSetLoadingOverlayVisibility(HandyEvent.SetLoadingOverlayVisibility event)
    {
        mLoadingOverlayVisibility = event.isVisible;
    }
}
