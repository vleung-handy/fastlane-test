package com.handy.portal.location.scheduler.tracker.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.scheduler.handler.LocationScheduleHandler;
import com.handy.portal.location.scheduler.tracker.model.LocationTrackerStrategy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * TODO: at testing stage. totally needs refactoring
 * <p/>
 * does whatever needs to be done given a location query schedule
 */
public class LocationTrackerScheduleHandler
        extends LocationScheduleHandler<LocationTrackerStrategyHandler, LocationTrackerStrategy>
        implements LocationTrackerStrategyHandler.LocationStrategyCallbacks
{
    Handler mHandler = new Handler();
    private static final int ALARM_REQUEST_CODE = 1;
    private static final String LOCATION_SCHEDULE_ALARM_BROADCAST_ID = "LOCATION_SCHEDULE_ALARM_BROADCAST_ID";
    private final static String BUNDLE_EXTRA_LOCATION_STRATEGY = "LOCATION_STRATEGY";

    //TODO: temporary, can remove once we don't have overlapping schedules
    Set<LocationTrackerStrategyHandler> mActiveLocationRequestStrategies = new HashSet<>();

    public LocationTrackerScheduleHandler(@NonNull final LinkedList<LocationTrackerStrategy> locationQuerySchedule, @NonNull final GoogleApiClient googleApiClient, @NonNull final Context context)
    {
        super(locationQuerySchedule, googleApiClient, context);
    }

    @Override
    protected int getAlarmRequestCode()
    {
        return ALARM_REQUEST_CODE;
    }

    @Override
    protected String getAlarmBroadcastId()
    {
        return LOCATION_SCHEDULE_ALARM_BROADCAST_ID;
    }

    @Override
    public LocationTrackerStrategyHandler createStrategyHandler(final LocationTrackerStrategy locationTrackerStrategy)
    {
        final LocationTrackerStrategyHandler locationTrackerStrategyHandler =
                new LocationTrackerStrategyHandler(
                        locationTrackerStrategy,
                        this,
                        mHandler,
                        mContext);
        return locationTrackerStrategyHandler;
    }

    //TODO: would rather not need this but need to remove the handler from the active strategies list for clean up purposes later
    @Override
    public void onLocationStrategyExpired(final LocationTrackerStrategyHandler locationTrackerStrategyHandler)
    {
        try
        {
            Log.d(getClass().getName(), "strategy expired, posting remaining location update objects in queue...");
            //strategy expired, we want to post any remaining update objects in the queue
            locationTrackerStrategyHandler.buildBatchUpdateAndNotifyReady();
            mActiveLocationRequestStrategies.remove(locationTrackerStrategyHandler);
        }
        catch (Exception e)
        {
            //not trusting post delayed
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void stopStrategy(final LocationTrackerStrategyHandler locationStrategyHandler)
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationStrategyHandler.getLocationListener());
    }

    /**
     * TODO: also consolidate with above function
     * <p/>
     * called when network reconnected
     * triggers immediate requests for location updates for the active strategies
     * (usually just 1, probably at most 3, until we don't have overlapping)
     */
    private void rerequestLocationUpdatesForActiveStrategies()
    {
        Log.d(getClass().getName(), "rerequesting location updates for the active strategies");
        Iterator<LocationTrackerStrategyHandler> locationStrategyHandlerIterator = mActiveLocationRequestStrategies.iterator();
        while (locationStrategyHandlerIterator.hasNext())
        {
            LocationTrackerStrategyHandler locationTrackerStrategyHandler = locationStrategyHandlerIterator.next();
            if (locationTrackerStrategyHandler.isStrategyExpired())
            {
                //remove, just in case it wasn't properly removed before
                locationStrategyHandlerIterator.remove();
            }
            else
            {
                locationTrackerStrategyHandler.requestLocationUpdates(mGoogleApiClient);
            }
        }
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
            case LOCATION_SCHEDULE_ALARM_BROADCAST_ID:
                Log.d(getClass().getName(), "Woke up");

                /**
                 * using byte array to avoid exception
                 *
                 * http://blog.nocturnaldev.com/blog/2013/09/01/parcelable-in-pendingintent/
                 */
                byte[] strategyByteArray = args.getByteArray(BUNDLE_EXTRA_LOCATION_STRATEGY);
                if (strategyByteArray == null) { break; }
                Parcel strategyParcel = Parcel.obtain();
                strategyParcel.unmarshall(strategyByteArray, 0, strategyByteArray.length);
                strategyParcel.setDataPosition(0);

                LocationTrackerStrategy locationTrackerStrategy = LocationTrackerStrategy.CREATOR.createFromParcel(strategyParcel);
                onStrategyAlarmTriggered(locationTrackerStrategy);
                break;
        }
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
        rerequestLocationUpdatesForActiveStrategies();
    }
}
