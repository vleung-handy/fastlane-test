package com.handy.portal.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationQuerySchedule;
import com.handy.portal.location.model.LocationQueryStrategy;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import javax.inject.Inject;

/**
 * TODO: at testing stage. totally needs refactoring
 *
 * does whatever needs to be done given a location query schedule
 */
public class LocationScheduleHandler extends BroadcastReceiver
    implements LocationStrategyHandler.LocationStrategyCallbacks
{
    @Inject
    Bus bus;

    LocationQuerySchedule mLocationQuerySchedule;
    GoogleApiClient mGoogleApiClient;
    AlarmManager mAlarmManager;
    Context mContext;
    Handler mHandler = new Handler();
    private static final int ALARM_REQUEST_CODE = 1;
    private static final String LOCATION_SCHEDULE_ALARM_BROADCAST_ID = "LOCATION_SCHEDULE_ALARM_BROADCAST_ID";
    private final static String BUNDLE_EXTRA_LOCATION_STRATEGY = "LOCATION_STRATEGY";


    /**
     * hack for preventing the network reconnected callback from being triggered
     * multiple times (due to multiple network providers)
     */
    private boolean mPreviouslyHadNetworkConnectivity = true;

    //TODO: temporary, can remove once we don't have overlapping schedules
    Set<LocationStrategyHandler> mActiveLocationRequestStrategies = new HashSet<>();
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
        //seems i have to register a receiver because the network listener is only available in 21/23

        mContext.registerReceiver(this, intentFilter);
    }

    public void start()
    {
        try
        {
            scanSchedule(); //can throw security exception, or maybe the client won't be connected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    /**
     *
     * assume schedule sorted by date asc
     *
     * TODO: super crude, clean up
     */
    private void scanSchedule()
    {
        //look for any strategies within scope and starts them if not already started
        ListIterator<LocationQueryStrategy> locationQueryStrategyListIterator
                = mLocationQuerySchedule.getLocationQueryStrategies().listIterator();
        while(locationQueryStrategyListIterator.hasNext())
        {
            LocationQueryStrategy strategy = locationQueryStrategyListIterator.next();

            locationQueryStrategyListIterator.remove(); //strategy is handled, don't need to handle again

            //TODO: use a util instead
            long currentTimeMillis = System.currentTimeMillis();
            if(currentTimeMillis >= strategy.getStartDate().getTime()
                    && currentTimeMillis < strategy.getEndDate().getTime()) //current time is within start/end date bounds
            {
                //starts this strategy
                startStrategy(strategy);
            }
            else if(currentTimeMillis < strategy.getStartDate().getTime()) //current time is before start date
            {
                //start date is in the future
                scheduleAlarm(strategy);
                Log.i(getClass().getName(), "Scheduled an alarm for " + strategy.getStartDate().toString());
                break; //only want one alarm a time and don't need to look further into future
            }


        }

        //TODO: when the schedule is completely expired, we want to request a schedule for the next N days in case the user never opens the app
    }

    //for testing
    LocationStrategyHandler mLatestActiveLocationStrategyHandler;

    @VisibleForTesting
    public LocationQueryStrategy getLatestActiveLocationStrategy()
    {
        return mLatestActiveLocationStrategyHandler.getLocationQueryStrategy();
    }

    public void startStrategy(@NonNull final LocationQueryStrategy locationQueryStrategy) throws SecurityException, IllegalStateException
    {
        Log.i(getClass().getName(), "starting strategy...");
        final LocationStrategyHandler locationStrategyHandler =
                new LocationStrategyHandler(
                        locationQueryStrategy,
                        this,
                        mHandler,
                        mContext);
        mLatestActiveLocationStrategyHandler = locationStrategyHandler; //for tests only
        mActiveLocationRequestStrategies.add(locationStrategyHandler); //only needed for removing updates on destroy

        locationStrategyHandler.requestLocationUpdates(mGoogleApiClient);
    }

    //TODO: would rather not need this but need to remove the handler from the active strategies list for clean up purposes later
    @Override
    public void onLocationStrategyExpired(final LocationStrategyHandler locationStrategyHandler)
    {
        try{
            Log.i(getClass().getName(), "strategy expired, posting remaining location update objects in queue...");
            //strategy expired, we want to post any remaining update objects in the queue
            locationStrategyHandler.buildBatchUpdateAndNotifyReady();
            mActiveLocationRequestStrategies.remove(locationStrategyHandler);
        }
        catch (Exception e)
        {
            //not trusting post delayed
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void stopLocationUpdates()
    {
        for(LocationStrategyHandler locationStrategyHandler : mActiveLocationRequestStrategies)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationStrategyHandler.getLocationListener());
        }
        mActiveLocationRequestStrategies.clear();
    }

    /**
     * TODO: need to test
     *
     * called when this handler is destroyed
     * sends out all queued location updates to the server
     */
    private void sendAllQueuedLocationUpdates()
    {
        Log.i(getClass().getName(), "sending out all queued location updates");
        Iterator<LocationStrategyHandler> locationStrategyHandlerIterator = mActiveLocationRequestStrategies.iterator();
        while(locationStrategyHandlerIterator.hasNext())
        {
            LocationStrategyHandler locationStrategyHandler = locationStrategyHandlerIterator.next();
            if(locationStrategyHandler.isStrategyExpired())
            {
                //remove, just in case it wasn't properly removed before
                locationStrategyHandlerIterator.remove();
            }
            else
            {
                locationStrategyHandler.buildBatchUpdateAndNotifyReady();
            }
        }
    }

    /**
     * TODO: need to test. also consolidate with above function
     *
     * called when network reconnected
     * triggers immediate requests for location updates for the active strategies
     * (usually just 1, probably at most 3, until we don't have overlapping)
     */
    private void rerequestLocationUpdatesForActiveStrategies()
    {
        Log.i(getClass().getName(), "rerequesting location updates for the active strategies");
        Iterator<LocationStrategyHandler> locationStrategyHandlerIterator = mActiveLocationRequestStrategies.iterator();
        while(locationStrategyHandlerIterator.hasNext())
        {
            LocationStrategyHandler locationStrategyHandler = locationStrategyHandlerIterator.next();
            if(locationStrategyHandler.isStrategyExpired())
            {
                //remove, just in case it wasn't properly removed before
                locationStrategyHandlerIterator.remove();
            }
            else
            {
                locationStrategyHandler.requestLocationUpdates(mGoogleApiClient);
            }
        }
    }

    /**
     * TODO: make this right
     */
    public void destroy()
    {
        try
        {
            Intent intent =  new Intent(LOCATION_SCHEDULE_ALARM_BROADCAST_ID);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mAlarmManager.cancel(pendingIntent);

            sendAllQueuedLocationUpdates();
            stopLocationUpdates();

            //TODO: consider not putting this here
            mContext.unregisterReceiver(this);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
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
                    try
                    {
                        startStrategy(locationQueryStrategy);
                        scanSchedule();
                    }
                    catch (Exception e)
                    {
                        //in case it throws security exception or google client not connected
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:

                //the below line doesn't actually work as expected
//                boolean hasConnectivity = !args.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY);

                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                boolean hasConnectivity = networkInfo != null
                        && networkInfo.isConnected()
                        && networkInfo.isAvailable();

                /*
                NOTE: if i have both data and wifi on, and then turn wifi off,
                hasConnectivity will still be true
                 */
                Log.i(getClass().getName(), "has network connectivity: " + hasConnectivity);
                if(hasConnectivity && !mPreviouslyHadNetworkConnectivity)
                    //network connected and couldn't connect before. need latter check to prevent multiple triggers due to multiple network providers
                {
                    bus.post(new LocationEvent.OnNetworkReconnected());
                    rerequestLocationUpdatesForActiveStrategies();
                    //immediately request location updates
                    //this will be much easier when we only have one location listener
                    //which will we do when we don't have overlapping strategies anymore
                }

                //this is a hack to prevent multiple network reconnected triggers, should think of a better solution
                mPreviouslyHadNetworkConnectivity = hasConnectivity;
                break;
        }
    }

    @Override
    public void onLocationBatchUpdateReady(final LocationBatchUpdate locationBatchUpdate)
    {
        bus.post(new LocationEvent.SendGeolocationRequest(locationBatchUpdate));
    }

    @Override
    public void onLocationUpdate(final Location location)
    {
        bus.post(new LocationEvent.LocationUpdated(location));
    }
}
