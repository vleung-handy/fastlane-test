package com.handy.portal.core;

import com.handy.portal.event.HandyEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;

import javax.inject.Inject;

public class ApplicationOnResumeWatcher
{
    private final Bus bus;

    private Date lastOnPause = null;
    private final static long TRANSITION_THRESHOLD = 3000;

    @Inject
    ApplicationOnResumeWatcher(final Bus bus)
    {
        this.bus = bus;
        this.bus.register(this);
    }

    @Subscribe
    public void onActivityResume(HandyEvent.ActivityResumed event)
    {
        Date now = new Date();
        if(lastOnPause == null) {
            bus.post(new HandyEvent.ApplicationResumed(event.sender));
        } else if (Math.abs(now.getTime() - lastOnPause.getTime()) > TRANSITION_THRESHOLD) {
            bus.post(new HandyEvent.ApplicationResumed(event.sender));
        }
    }

    @Subscribe
    public void onActivityPaused(HandyEvent.ActivityPaused event)
    {
        lastOnPause = new Date();
    }

}