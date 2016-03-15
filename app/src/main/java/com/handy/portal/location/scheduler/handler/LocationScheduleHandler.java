package com.handy.portal.location.scheduler.handler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.GoogleApiClient;
import com.handy.portal.location.LocationEvent;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.model.LocationStrategy;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import javax.inject.Inject;

/**
 * TODO: at testing stage. totally needs refactoring
 * <p/>
 * does whatever needs to be done given a location query schedule
 */
public abstract class LocationScheduleHandler<T extends LocationStrategyHandler, V extends LocationStrategy> extends BroadcastReceiver
{
    @Inject
    Bus mBus;

    protected LinkedList<V> mSortedLocationStrategies;
    protected GoogleApiClient mGoogleApiClient;
    AlarmManager mAlarmManager;
    protected Context mContext;

    Set<T> mActiveLocationRequestStrategies = new HashSet<>();

    public LocationScheduleHandler(@NonNull LinkedList<V> sortedLocationStrategies,
                                   @NonNull GoogleApiClient googleApiClient,
                                   @NonNull Context context)
    {
        Utils.inject(context, this);

        mSortedLocationStrategies = sortedLocationStrategies;
        mGoogleApiClient = googleApiClient;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mContext = context;

//        LocalBroadcastManager.getInstance(mContext).registerReceiver(this, getAlarmIntentFilter());
        mContext.registerReceiver(this, getAlarmIntentFilter());
    }

    protected abstract int getAlarmRequestCode();
    protected abstract String getAlarmBroadcastId();
    protected IntentFilter getAlarmIntentFilter()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getAlarmBroadcastId());
        return intentFilter;
    }

    public final void start()
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

    protected abstract void stopStrategy(T t);
    /**
     * assume schedule sorted by date asc
     * <p/>
     * TODO: super crude, clean up
     */
    protected final void scanSchedule()
    {
        //look for any strategies within scope and starts them if not already started
        ListIterator<V> locationQueryStrategyListIterator
                = mSortedLocationStrategies.listIterator();
        while (locationQueryStrategyListIterator.hasNext())
        {
            V strategy = locationQueryStrategyListIterator.next();

            locationQueryStrategyListIterator.remove(); //strategy is handled, don't need to handle again

            //TODO: use a util instead
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis >= strategy.getStartDate().getTime()
                    && currentTimeMillis < strategy.getEndDate().getTime()) //current time is within start/end date bounds
            {
                //starts this strategy
                startStrategy(strategy);
            }
            else if (currentTimeMillis < strategy.getStartDate().getTime()) //current time is before start date
            {
                //start date is in the future
                scheduleAlarm(strategy);
                Log.d(getClass().getName(), "Scheduled an alarm for " + strategy.getStartDate().toString());
                break; //only want one alarm a time and don't need to look further into future
            }
        }

        //TODO: when the schedule is completely expired, we want to request a schedule for the next N days in case the user never opens the app
    }

    public final void startStrategy(@NonNull final V v) throws SecurityException, IllegalStateException
    {
        Log.d(getClass().getName(), "starting strategy...");
        T t = createStrategyHandler(v);
        mActiveLocationRequestStrategies.add(t); //only needed for removing updates on destroy

        t.requestUpdates(mGoogleApiClient);
    }

    public abstract T createStrategyHandler(V v);
    public final void stopAllActiveStrategies()
    {
        for (T t : mActiveLocationRequestStrategies)
        {
            stopStrategy(t);
        }
        mActiveLocationRequestStrategies.clear();
    }
    /**
     * TODO: need to consolidate with below function
     * <p/>
     * called when this handler is destroyed
     * sends out all queued location updates to the server
     */
    protected final void sendAllQueuedLocationUpdates()
    {
        Log.d(getClass().getName(), "sending out all queued location updates");
        Iterator<T> locationStrategyHandlerIterator = mActiveLocationRequestStrategies.iterator();
        while (locationStrategyHandlerIterator.hasNext())
        {
            LocationStrategyHandler locationScheduleStrategyHandler = locationStrategyHandlerIterator.next();
            if (locationScheduleStrategyHandler.isStrategyExpired())
            {
                //remove, just in case it wasn't properly removed before
                locationStrategyHandlerIterator.remove();
            }
            else
            {
                locationScheduleStrategyHandler.buildBatchUpdateAndNotifyReady();
            }
        }
    }

    /**
     * TODO: make this right
     */
    public final void destroy()
    {
        try
        {
            //cancels scheduled alarms
            Intent intent = new Intent(getAlarmBroadcastId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, getAlarmRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mAlarmManager.cancel(pendingIntent);

            //sends all location updates that are supposed to be sent to server
            sendAllQueuedLocationUpdates();

            //stops all location updates
            stopAllActiveStrategies();

            //TODO: consider not putting this here
            //unregister the alarm receiver
            mContext.unregisterReceiver(this);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    /**
     * TODO: clean
     *
     * @param strategy
     */
    private final void scheduleAlarm(@NonNull V strategy)
    {
        /**
         * passing byte array to avoid exception
         *
         * http://blog.nocturnaldev.com/blog/2013/09/01/parcelable-in-pendingintent/
         */
        Parcel strategyParcel = Parcel.obtain();
        strategy.writeToParcel(strategyParcel, 0);
        strategyParcel.setDataPosition(0);

        Bundle args = new Bundle();
        args.putByteArray(getStrategyBundleExtraKey(), strategyParcel.marshall());

        Intent intent = new Intent(getAlarmBroadcastId());
        intent.setAction(getAlarmBroadcastId()); //probably redundant, test this
        intent.setPackage(mContext.getPackageName());
        intent.putExtras(args);
        PendingIntent operation = PendingIntent.getBroadcast(mContext, getAlarmRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, strategy.getStartDate().getTime(), operation);
    }

    protected abstract String getStrategyBundleExtraKey();
    public final void onStrategyAlarmTriggered(V v)
    {
        if (v == null) { return; }
        Log.d(getClass().getName(), "Got location strategy " + v.toString());
        try
        {
            startStrategy(v);
            scanSchedule();
        }
        catch (Exception e)
        {
            //in case it throws security exception or google client not connected
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }


    /**
     *
     * TODO override if you want it to do something
     * called by location service when network reconnected event is broadcast
     *
     * don't want to subscribe to event here, because this object does not have a strict lifecycle
     * unlike the location service, so it is harder to guarantee that the bus will be unregistered
     * when we no longer care about this object (ex. what if this object loses its reference?)
     */
    public void onNetworkReconnected()
    {
        //maybe do something
    }

    public void onLocationBatchUpdateReady(final LocationBatchUpdate locationBatchUpdate)
    {
        mBus.post(new LocationEvent.SendGeolocationRequest(locationBatchUpdate));
    }

    public void onLocationUpdate(final Location location)
    {
        mBus.post(new LocationEvent.LocationUpdated(location));
    }
}
