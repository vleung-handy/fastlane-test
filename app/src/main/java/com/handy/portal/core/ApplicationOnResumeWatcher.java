package com.handy.portal.core;

import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;

import javax.inject.Inject;

public class ApplicationOnResumeWatcher
{
    private final Bus bus;

    private Date lastOnPause = null;
    private final static long TRANSITION_THRESHOLD = 1000;

    @Inject
    ApplicationOnResumeWatcher(final Bus bus)
    {
        this.bus = bus;
        this.bus.register(this);
    }

    @Subscribe
    public void onActivityResume(Event.ActivityResumed event)
    {
        Date now = new Date();
        if(lastOnPause == null) {
            bus.post(new Event.ApplicationResumed(event.sender));
        } else if (Math.abs(now.getTime() - lastOnPause.getTime()) > TRANSITION_THRESHOLD) {
            bus.post(new Event.ApplicationResumed(event.sender));
        }
    }

    @Subscribe
    public void onActivityPaused(Event.ActivityPaused event)
    {
        lastOnPause = new Date();
    }

}