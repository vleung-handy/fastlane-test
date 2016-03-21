package com.handy.portal.location.scheduler.tracking.handler;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.handy.portal.location.LocationEvent;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.handler.ScheduleHandler;
import com.handy.portal.location.scheduler.tracking.model.LocationTrackingScheduleStrategy;
import com.handy.portal.util.ParcelableUtils;

import java.util.LinkedList;

/**
 * TODO: at testing stage. totally needs refactoring
 * <p/>
 * does whatever needs to be done given a location query schedule
 */
public class LocationTrackingScheduleHandler
        extends ScheduleHandler<LocationTrackingStrategyHandler, LocationTrackingScheduleStrategy>
        implements LocationTrackingStrategyHandler.LocationStrategyCallbacks
{
    Handler mHandler = new Handler();
    private static final int ALARM_REQUEST_CODE = 1;
    private static final String LOCATION_SCHEDULE_ALARM_BROADCAST_ID = "LOCATION_SCHEDULE_ALARM_BROADCAST_ID";
    private final static String BUNDLE_EXTRA_LOCATION_STRATEGY = "LOCATION_STRATEGY";

    private GoogleApiClient mGoogleApiClient;

    public LocationTrackingScheduleHandler(@NonNull final LinkedList<LocationTrackingScheduleStrategy> locationQuerySchedule, @NonNull final GoogleApiClient googleApiClient, @NonNull final Context context)
    {
        super(locationQuerySchedule, context);
        mGoogleApiClient = googleApiClient;
    }

    @Override
    protected int getWakeupAlarmRequestCode()
    {
        return ALARM_REQUEST_CODE;
    }

    @Override
    protected String getWakeupAlarmBroadcastAction()
    {
        return LOCATION_SCHEDULE_ALARM_BROADCAST_ID;
    }

    @Override
    public LocationTrackingStrategyHandler createStrategyHandler(final LocationTrackingScheduleStrategy locationTrackerStrategy)
    {
        return new LocationTrackingStrategyHandler(
                        locationTrackerStrategy,
                        this,
                        mHandler,
                        mContext);
    }

    /**
     * receives wake ups from alarm manager
     * TODO: clean up
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        Bundle args = intent.getExtras();
        if (args == null)
        {
            //shouldn't happen
            Log.e(getClass().getName(), "Args is null on receive alarm");
            return;
        }
        if (intent.getAction() == null)
        {
            Log.e(getClass().getName(), "Intent action is null on receive alarm");
            return;
        }

        switch (intent.getAction())
        {
            //TODO: refactor this
            case LOCATION_SCHEDULE_ALARM_BROADCAST_ID: //todo how can i make the base class handle this
                Log.d(getClass().getName(), "Woke up");

                /**
                 * using byte array to avoid exception
                 *
                 * http://blog.nocturnaldev.com/blog/2013/09/01/parcelable-in-pendingintent/
                 */
                Parcel parcel = ParcelableUtils.unmarshall(args, getStrategyBundleExtraKey());
                if(parcel == null) return;
                LocationTrackingScheduleStrategy locationTrackerStrategy = LocationTrackingScheduleStrategy.CREATOR.createFromParcel(parcel);
                onStrategyAlarmTriggered(locationTrackerStrategy);
                break;
        }
    }

    @Override
    public GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }

    @Override
    protected String getStrategyBundleExtraKey()
    {
        return BUNDLE_EXTRA_LOCATION_STRATEGY;
    }

    /**
     * called by location service when network reconnected event is broadcast
     *
     * don't want to subscribe to event here, because this object does not have a strict lifecycle
     * unlike the location service, so it is harder to guarantee that the bus will be unregistered
     * when we no longer care about this object (ex. what if this object loses its reference?)
     */
    public void onNetworkReconnected()
    {
        super.onNetworkReconnected();
        restartActiveStrategies();
    }

    public void onLocationUpdate(final Location location)
    {
        mBus.post(new LocationEvent.LocationUpdated(location));
    }

    public void onLocationBatchUpdateReady(final LocationBatchUpdate locationBatchUpdate)
    {
        mBus.post(new LocationEvent.SendGeolocationRequest(locationBatchUpdate));
    }
}
