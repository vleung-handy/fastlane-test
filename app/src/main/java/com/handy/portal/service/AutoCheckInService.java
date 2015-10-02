package com.handy.portal.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.LocationData;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class AutoCheckInService extends IntentService
{
    @Inject
    Bus bus;

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
        String action = intent.getStringExtra(BundleKeys.CHECK_IN_ACTION_ID);
        String bookingId = intent.getStringExtra(BundleKeys.BOOKING_ID);

        if (action != null && bookingId != null)
        {
            handleAction(action, bookingId, getLastKnownLocation());
        }
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

    private Location getLastKnownLocation()
    {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = locationManager.getBestProvider(criteria, true);
        return locationManager.getLastKnownLocation(locationProvider);
    }
}
