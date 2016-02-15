package com.handy.portal.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationQuerySchedule;
import com.handy.portal.location.model.LocationQueryStrategy;
import com.handy.portal.location.model.LocationUpdate;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import javax.inject.Inject;

/**
 * TODO: at testing stage. totally needs refactoring
 *
 * does whatever needs to be done given a location query schedule
 */
public class LocationScheduleHandler extends BroadcastReceiver
    implements OnLocationBatchUpdateReadyListener
{
    @Inject
    Bus bus;

    LocationQuerySchedule mLocationQuerySchedule;
    GoogleApiClient mGoogleApiClient;
    AlarmManager mAlarmManager;
    Context mContext;
    Handler mHandler = new Handler();
//    private static final int DEFAULT_SMALLEST_DISPLACEMENT_METERS = 25;
    private static final int ALARM_REQUEST_CODE = 1;
    private static final String LOCATION_SCHEDULE_ALARM_BROADCAST_ID = "LOCATION_SCHEDULE_ALARM_BROADCAST_ID";
    private final static String BUNDLE_EXTRA_LOCATION_STRATEGY = "LOCATION_STRATEGY";


    //TODO: temporary, can remove once we don't have overlapping schedules
    Set<LocationListener> mActiveLocationListeners = new HashSet<>();
    public LocationScheduleHandler(@NonNull LocationQuerySchedule locationQuerySchedule,
                                   @NonNull GoogleApiClient googleApiClient,
                                   @NonNull Context context)
    {
        Utils.inject(context, this);

        mLocationQuerySchedule = locationQuerySchedule;
        mGoogleApiClient = googleApiClient;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mContext = context;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationScheduleHandler.LOCATION_SCHEDULE_ALARM_BROADCAST_ID);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(this, intentFilter);
    }

    public void start()
    {
        scanSchedule();
    }

    /**
     *
     * assume schedule sorted by date asc
     *
     * TODO: super crude, clean up
     */
    private void scanSchedule() //TODO: rename this
    {
        //look for any strategies within scope and starts them if not already started
        ListIterator<LocationQueryStrategy> locationQueryStrategyListIterator
                = mLocationQuerySchedule.getLocationQueryStrategies().listIterator();
        while(locationQueryStrategyListIterator.hasNext())
        {
            LocationQueryStrategy strategy = locationQueryStrategyListIterator.next();

            //TODO: assuming listiterator.remove() is constant time for linked lists. need to verify
            locationQueryStrategyListIterator.remove(); //strategy is handled, don't need to handle again

            //TODO: use a util instead
            long currentTimeMillis = System.currentTimeMillis();
            if(currentTimeMillis >= strategy.getStartDate().getTime()
                    && currentTimeMillis < strategy.getEndDate().getTime()) //current time is within start/end date bounds
            {
                //starts this strategy
                startStrategy(strategy);
            }
            else //outside of scope
            {
                if(currentTimeMillis < strategy.getStartDate().getTime()) //current time is before start date
                {
                    //start date is in the future
                    scheduleAlarm(strategy);
                    Log.i(getClass().getName(), "Scheduled an alarm for " + strategy.getStartDate().toString());
                    break; //only want one alarm a time and don't need to look further into future
                }
                else //current time past end date
                {
                }
            }

        }
    }

    /**
     * TODO: if we just use one listener, does requestLocationUpdates override the previous location update request?
     */
    public void startStrategy(@NonNull final LocationQueryStrategy locationQueryStrategy)
    {
        final LocationRequestStrategy locationRequestStrategy = new LocationRequestStrategy(locationQueryStrategy);
        /*
        TODO: TEST ONLY. seriously refactor this. don't know how server is going to send accuracy codes yet
         */
        int priority = 0;
        switch(locationQueryStrategy.getLocationAccuracyPriority())
        {
            case 0:
                priority = LocationRequest.PRIORITY_LOW_POWER;
                break;
            case 1:
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                break;
            case 2:
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                break;
            default:
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                break;
        }
        long pollingIntervalMs = locationQueryStrategy.getLocationPollingIntervalSeconds() * DateTimeUtils.MILLISECONDS_IN_SECOND;
        long expirationDurationMs = locationQueryStrategy.getEndDate().getTime() - System.currentTimeMillis();
        LocationRequest locationRequest = new LocationRequest()
//                .setSmallestDisplacement(DEFAULT_SMALLEST_DISPLACEMENT_METERS) //disabled for local testing
                .setPriority(priority)
                .setExpirationDuration(expirationDurationMs)
                .setMaxWaitTime(locationQueryStrategy.getServerPollingIntervalSeconds())
                .setInterval(pollingIntervalMs)
                .setFastestInterval(pollingIntervalMs) //TODO: change this, test only
                ;


        //TODO: can remove once we don't have overlapping schedules
                        /*
                            we are creating a new listener for each location request
                            because according to the documentation, any previous location
                            requests registered on the same listener are removed
                         */
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location)
            {
                locationRequestStrategy.onNewLocationUpdate(LocationUpdate.from(location),
                        LocationScheduleHandler.this);
            }
        };
        mActiveLocationListeners.add(locationListener);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationRequest,
                locationListener);

        //TODO: REFACTOR THIS. how else can i get a callback when the strategy expires?
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Log.i(getClass().getName(), "strategy expired, posting remaining location update objects in queue...");
                //strategy expired, we want to post any remaining update objects in the queue
                locationRequestStrategy.buildBatchUpdateAndNotifyReady(LocationScheduleHandler.this);
            }
        }, expirationDurationMs);
    }

    public void stopLocationUpdates()
    {
        for(LocationListener locationListener : mActiveLocationListeners)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
        }
        mActiveLocationListeners.clear();

    }

    /**
     * TODO: make this right
     */
    public void destroy()
    {
        //cancel the alarm
        Intent intent =  new Intent(LOCATION_SCHEDULE_ALARM_BROADCAST_ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.cancel(pendingIntent);

        stopLocationUpdates();
        try
        {
            //TODO: consider not putting this here
            mContext.unregisterReceiver(this);
        }
        catch (IllegalArgumentException e)
        {
            //not registered
        }
    }

    /**
     * TODO: super crude, clean up
     * @param strategy
     */
    private void scheduleAlarm(@NonNull LocationQueryStrategy strategy)
    {
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_EXTRA_LOCATION_STRATEGY, strategy);
        Intent intent =  new Intent(LOCATION_SCHEDULE_ALARM_BROADCAST_ID);
        intent.setAction(LOCATION_SCHEDULE_ALARM_BROADCAST_ID); //probably redundant, test this
        intent.setPackage(mContext.getPackageName());
        intent.putExtras(args);
        PendingIntent operation = PendingIntent.getBroadcast(mContext, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, strategy.getStartDate().getTime(), operation);
    }

    /**
     * receives wake ups from alarm manager
     * TODO: clean up
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        Bundle args = intent.getExtras();
        if(args == null)
        {
            //shouldn't happen
            Log.e(getClass().getName(), "Args is null");
            return;
        }
        if(intent.getAction() == null)
        {
            return;
        }

        switch (intent.getAction())
        {
            //TODO: refactor this
            case LOCATION_SCHEDULE_ALARM_BROADCAST_ID:
                Log.i(getClass().getName(), "Woke up");
                LocationQueryStrategy locationQueryStrategy = args.getParcelable(BUNDLE_EXTRA_LOCATION_STRATEGY);
                if(locationQueryStrategy != null)
                {
                    Log.i(getClass().getName(), "Got location strategy " + locationQueryStrategy.toString());
                    startStrategy(locationQueryStrategy);
                    scanSchedule();
                }
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:

                //TODO: the below line doesn't actually work as expected
                boolean hasConnectivity = !args.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
                Log.i(getClass().getName(), "has connectivity: " + hasConnectivity);
                if(hasConnectivity)
                {
                    //TODO: post everything in the failed queue
                }

                break;
        }

    }

    @Override
    public void onLocationBatchUpdateReady(final LocationBatchUpdate locationBatchUpdate)
    {
//        Integer.parseInt("blah"); //TEST EXCEPTION ONLY
        bus.post(new LocationEvent.SendGeolocationRequest(locationBatchUpdate));
    }

//    @Override
//    public void onLocationChanged(final Location location)
//    {
//        bus.post(new LocationEvent.LocationChanged(location));
//    }
}
