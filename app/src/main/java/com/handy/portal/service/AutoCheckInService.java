package com.handy.portal.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.text.format.DateUtils;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.LocationData;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.urbanairship.PendingResult;
import com.urbanairship.UAirship;

import javax.inject.Inject;

public class AutoCheckInService extends IntentService
{
    @Inject
    Bus bus;

    private static final long MAXIMUM_LOCATION_AGE_MILLIS = DateUtils.MINUTE_IN_MILLIS;

    private static final String ACTION_CHECK_IN = "check_in";
    private static final String ACTION_CHECK_OUT = "check_out";

    public AutoCheckInService()
    {
        this(AutoCheckInService.class.getSimpleName());
    }

    public AutoCheckInService(String name)
    {
        super(name);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Utils.inject(this, this);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        final String action = intent.getStringExtra(BundleKeys.CHECK_IN_ACTION_ID);
        final String bookingId = intent.getStringExtra(BundleKeys.BOOKING_ID);

        if (action != null && bookingId != null)
        {
            PendingResult<Location> pendingLocation = UAirship.shared().getLocationManager().requestSingleLocation();
            pendingLocation.onResult(new PendingResult.ResultCallback<Location>()
            {
                @Override
                public void onResult(Location location)
                {
                    if (isRecent(location))
                    {
                        handleAction(action, bookingId, location);
                    }
                }
            });
        }
    }

    private boolean isRecent(Location location)
    {
        return location != null && System.currentTimeMillis() - location.getTime() <= MAXIMUM_LOCATION_AGE_MILLIS;
    }

    private void handleAction(String action, String bookingId, Location location)
    {
        switch (action)
        {
            case ACTION_CHECK_IN:
                bus.post(new HandyEvent.RequestNotifyJobCheckIn(bookingId, true, new LocationData(location)));
                break;
            case ACTION_CHECK_OUT:
                bus.post(new HandyEvent.RequestNotifyJobCheckOut(bookingId, true, new LocationData(location)));
                break;
            default:
                break;
        }
    }

}
