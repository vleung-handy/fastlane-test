package com.handy.portal.location.scheduler.handler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.location.scheduler.model.ScheduleStrategy;
import com.handy.portal.util.ParcelableUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import javax.inject.Inject;

/**
 *
 * does whatever needs to be done given a schedule that contains strategies, sorted by start date
 * TODO: try to not have to define the StrategyHandler class type, because that's supposed to be implied from the strategy type
 */
public abstract class ScheduleHandler<StrategyHandlerType extends StrategyHandler, ScheduleStrategyType extends ScheduleStrategy>
        extends BroadcastReceiver
        implements StrategyHandler.StrategyCallbacks<StrategyHandlerType>
{
    @Inject
    protected Bus mBus;

    protected LinkedList<ScheduleStrategyType> mSortedStrategies;
    private AlarmManager mAlarmManager;
    protected Context mContext;

    private Set<StrategyHandlerType> mActiveStrategies = new HashSet<>();

    public ScheduleHandler(@NonNull LinkedList<ScheduleStrategyType> sortedByDateAscendingStrategies,
                           @NonNull Context context)
    {
        Utils.inject(context, this);

        mSortedStrategies = sortedByDateAscendingStrategies;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mContext = context;

        //TODO: investigate using this later. need to refactor other stuff before this can be used
//        LocalBroadcastManager.getInstance(mContext).registerReceiver(this, getAlarmIntentFilter());
        mContext.registerReceiver(this, getAlarmIntentFilter());
    }

    /**
     * the request code for the wake-up alarm
     * @return
     */
    protected abstract int getWakeupAlarmRequestCode();
    protected abstract String getWakeupAlarmBroadcastAction();
    protected IntentFilter getAlarmIntentFilter()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getWakeupAlarmBroadcastAction());
        return intentFilter;
    }

    /**
     * starts handling this schedule
     */
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

    /**
     * assume schedule sorted by date asc
     * <p/>
     * TODO: stop the service when schedule is completely handled
     */
    protected final void scanSchedule()
    {
        //look for any strategies within scope and starts them if not already started
        ListIterator<ScheduleStrategyType> strategyListIterator
                = mSortedStrategies.listIterator();
        while (strategyListIterator.hasNext())
        {
            ScheduleStrategyType strategy = strategyListIterator.next();

            strategyListIterator.remove(); //strategy is handled, don't need to handle again

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

    public final void startStrategy(@NonNull final ScheduleStrategyType scheduleStrategyType) throws SecurityException, IllegalStateException
    {
        Log.d(getClass().getName(), "starting strategy...");
        StrategyHandlerType strategyHandlerType = createStrategyHandler(scheduleStrategyType);
        mActiveStrategies.add(strategyHandlerType); //only needed for removing updates on destroy
        strategyHandlerType.startStrategy();
    }

    public abstract StrategyHandlerType createStrategyHandler(ScheduleStrategyType scheduleStrategyType);
    public void stopAllActiveStrategies()
    {
        for (StrategyHandlerType strategyHandlerType : mActiveStrategies)
        {
            strategyHandlerType.stopStrategy();
        }
        mActiveStrategies.clear();
    }
    /**
     * TODO: need to consolidate with below function
     * <p/>
     * called when this handler is destroyed
     * sends out all queued location updates to the server
     */
    protected final void sendAllQueuedStrategyUpdates()
    {
        Log.d(getClass().getName(), "sending out all queued location updates");
        Iterator<StrategyHandlerType> locationStrategyHandlerIterator = mActiveStrategies.iterator();
        while (locationStrategyHandlerIterator.hasNext())
        {
            StrategyHandler locationScheduleStrategyHandler = locationStrategyHandlerIterator.next();
            if (locationScheduleStrategyHandler.isStrategyExpired())
            {
                //remove, just in case it wasn't properly removed before
                locationStrategyHandlerIterator.remove();
            }
            else
            {
                locationScheduleStrategyHandler.buildStrategyBatchUpdatesAndNotifyReady();
            }
        }
    }

    public final void destroy()
    {
        try
        {
            //cancels scheduled alarms
            Intent intent = new Intent(getWakeupAlarmBroadcastAction());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, getWakeupAlarmRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mAlarmManager.cancel(pendingIntent);

            //sends all location updates that are supposed to be sent to server
            sendAllQueuedStrategyUpdates();

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
     * TODO refactor this later to not use system alarms. investigate alternative
     * Schedules an alarm to wake this broadcast receiver up with the strategy, for the strategy start date
     * @param strategy
     */
    private final void scheduleAlarm(@NonNull ScheduleStrategyType strategy)
    {
        /**
         * passing byte array to avoid exception
         *
         * http://blog.nocturnaldev.com/blog/2013/09/01/parcelable-in-pendingintent/
         */
        byte[] byteArray = ParcelableUtils.marshall(strategy);
        Bundle bundle = new Bundle();
        bundle.putByteArray(getStrategyBundleExtraKey(), byteArray);

        Intent intent = new Intent(getWakeupAlarmBroadcastAction());
        intent.setAction(getWakeupAlarmBroadcastAction()); //probably redundant, test this
        intent.setPackage(mContext.getPackageName());
        intent.putExtras(bundle);
        PendingIntent operation = PendingIntent.getBroadcast(mContext, getWakeupAlarmRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, strategy.getStartDate().getTime(), operation);
    }

    /**
     * the bundle key used to marshall and unmarshall the strategy parcel
     * which is used to pass the strategy to the alarm manager
     * @return
     */
    protected abstract String getStrategyBundleExtraKey();

    /**
     * called when the wake-up alarm for the given strategy is triggered
     * will start the strategy and then scan the schedule
     * @param scheduleStrategyType
     */
    public final void onStrategyAlarmTriggered(ScheduleStrategyType scheduleStrategyType)
    {
        if (scheduleStrategyType == null) { return; }
        Log.d(getClass().getName(), "Got strategy " + scheduleStrategyType.toString());
        try
        {
            startStrategy(scheduleStrategyType);
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

    /**
     * remove the expired strategy from the active strategies list and post all pending updates for it
     * @param strategyHandler
     */
    public void onStrategyExpired(StrategyHandlerType strategyHandler)
    {
        try
        {
            Log.d(getClass().getName(), "strategy expired, posting remaining update objects in queue...");
            //strategy expired, we want to post any remaining update objects in the queue
            strategyHandler.buildStrategyBatchUpdatesAndNotifyReady();
            mActiveStrategies.remove(strategyHandler);
        }
        catch (Exception e)
        {
            //not trusting post delayed
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    /**
     * might want to call this when the network is reconnected
     */
    protected void restartActiveStrategies()
    {
        Log.d(getClass().getName(), "restarting active strategies");
        Iterator<StrategyHandlerType> strategyHandlerIterator = mActiveStrategies.iterator();
        while (strategyHandlerIterator.hasNext())
        {
            StrategyHandlerType locationLocationTrackingStrategyHandler = strategyHandlerIterator.next();
            if (locationLocationTrackingStrategyHandler.isStrategyExpired())
            {
                //remove, just in case it wasn't properly removed before
                strategyHandlerIterator.remove();
            }
            else
            {
                locationLocationTrackingStrategyHandler.startStrategy();
            }
        }
    }
}
