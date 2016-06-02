package com.handy.portal.manager;

import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    private final EventBus bus;

    private NavigationEvent.NavigateToTab storedEvent;

    @Inject
    public MainActivityFragmentNavigationHelper(final EventBus bus)
    {
        this.bus = bus;
        this.bus.register(this);
    }

    @Subscribe
    public void onNavigateToTabEvent(NavigationEvent.NavigateToTab event)
    {
        if(!this.mainActivityFragmentActive)
        {
            setStoredEvent(event);
        }
    }

    private void setStoredEvent(NavigationEvent.NavigateToTab storedEvent)
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
