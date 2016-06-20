package com.handy.portal.manager;

import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by cdavis on 8/19/15.
 *
 * This is a class that acts as a patch for problems we are having with deep links.
 * It will catch and hold NavigateToTab events until the MainActivityFragment is available/resumed
 * This is not an optimal solution but is better than fragment knowing about deep links.
 *
 * Ideally we come up with a better way, like the mainactivityfragment is not responsible for directly handling fragment swapping and navigation
 * and whatever service is responsible also keeps an eye on the fragment stack but that refactor is out of scope right now for push notifications
 *
 */

public class MainActivityFragmentNavigationHelper
{
    private boolean mainActivityFragmentActive = false;

    private final Bus bus;

    private NavigationEvent.NavigateToPage storedEvent;

    @Inject
    public MainActivityFragmentNavigationHelper(final Bus bus)
    {
        this.bus = bus;
        this.bus.register(this);
    }

    @Subscribe
    public void onNavigateToPageEvent(NavigationEvent.NavigateToPage event)
    {
        if(!this.mainActivityFragmentActive)
        {
            setStoredEvent(event);
        }
    }

    private void setStoredEvent(NavigationEvent.NavigateToPage storedEvent)
    {
        this.storedEvent = storedEvent;
    }

    @Subscribe
    public void onMainActivityFragmentResumed(HandyEvent.UpdateMainActivityFragmentActive event)
    {
        updateFragmentActiveStatus(event.active);
    }

    private void updateFragmentActiveStatus(boolean active)
    {
        this.mainActivityFragmentActive = active;
        if(this.mainActivityFragmentActive)
        {
            repostStoredEvent();
        }
    }

    private void repostStoredEvent()
    {
        if(this.storedEvent != null)
        {
            bus.post(this.storedEvent);
        }
        this.storedEvent = null;
    }






}
