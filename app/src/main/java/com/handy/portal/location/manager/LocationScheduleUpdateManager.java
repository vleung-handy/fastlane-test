package com.handy.portal.location.manager;

import com.handy.portal.event.HandyEvent;
import com.handy.portal.location.LocationEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

/**
 * mediator that fires the event to get location schedules upon receiving certain app events.
 * in other words, it controls when location schedules are fetched,
 * and in a way functions as an adapter for a booking changed push event
 *
 * this is the ONLY COMPONENT THAT FIRES THE REQUEST LOCATION SCHEDULE EVENT!
 *
 * purpose: we currently want to know when a scheduled booking has been changed/created/removed
 * so we can get updated location schedules, but we don't want to
 * fire events for that in the booking manager, to reduce confusion/coupling.
 *
 */
public class LocationScheduleUpdateManager
{
    private final EventBus mBus;

    @Inject
    public LocationScheduleUpdateManager(final EventBus bus)
    {
        mBus = bus;
        mBus.register(this);
    }

    /* listen to events in which a booking might be modified or created */
    @Subscribe
    public void onReceiveClaimJobSuccess(HandyEvent.ReceiveClaimJobSuccess event)
    {
        mBus.post(new LocationEvent.RequestLocationSchedule());
    }

    @Subscribe
    public void onReceiveClaimJobsSuccess(HandyEvent.ReceiveClaimJobsSuccess event)
    {
        mBus.post(new LocationEvent.RequestLocationSchedule());
    }

    @Subscribe
    public void onReceiveRemoveJobSuccess(HandyEvent.ReceiveRemoveJobSuccess event)
    {
        mBus.post(new LocationEvent.RequestLocationSchedule());
    }

    @Subscribe
    public void onReceiveScheduledBookingsBatchSuccess(HandyEvent.ReceiveScheduledBookingsBatchSuccess event)
    {
        mBus.post(new LocationEvent.RequestLocationSchedule());
    }

    @Subscribe
    public void onLoginSuccess(HandyEvent.ReceiveLoginSuccess event)
    {
        mBus.post(new LocationEvent.RequestLocationSchedule());
    }

    @Subscribe
    public void onLocationServiceStarted(LocationEvent.LocationServiceStarted event)
    {
        mBus.post(new LocationEvent.RequestLocationSchedule());
    }
}
