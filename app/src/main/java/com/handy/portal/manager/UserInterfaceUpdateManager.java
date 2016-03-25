package com.handy.portal.manager;

import com.handy.portal.event.HandyEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

/**
 * manager for keeping track of UI update request events
 * to prevent funny UI bugs like loading screens getting stuck
 */
public class UserInterfaceUpdateManager
{
    private final Bus mBus;
    private Boolean mLoadingOverlayVisibility;

    public UserInterfaceUpdateManager(final Bus bus)
    {
        mBus = bus;
        mBus.register(this);
    }

    @Produce
    public HandyEvent.SetLoadingOverlayVisibility produceSetLoadingOverlayVisibilityEvent()
    {
        if (mLoadingOverlayVisibility != null)
        {
            return new HandyEvent.SetLoadingOverlayVisibility(mLoadingOverlayVisibility.booleanValue());
        }
        return null;
    }

    @Subscribe
    public void onRequestSetLoadingOverlayVisibility(HandyEvent.SetLoadingOverlayVisibility event)
    {
        mLoadingOverlayVisibility = event.isVisible;
    }
}
