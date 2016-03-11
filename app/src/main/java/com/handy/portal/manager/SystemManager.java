package com.handy.portal.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.handy.portal.event.SystemEvent;
import com.handy.portal.logger.handylogger.EventLogFactory;
import com.handy.portal.logger.handylogger.LogEvent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * keeps track of system events like network reconnected, battery level low
 */
public class SystemManager extends BroadcastReceiver
{
    private final Bus mBus;
    private final Context mContext;
    private EventLogFactory mEventLogFactory;

    private boolean mPreviouslyHadNetworkConnectivity = true;

    @Inject
    public SystemManager(@NonNull Context context, @NonNull final Bus bus,
                         @NonNull EventLogFactory eventLogFactory)
    {
        mBus = bus;
        mBus.register(this);
        mContext = context;
        mEventLogFactory = eventLogFactory;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);

        /*
        not using ConnectivityManager.OnNetworkActiveListener because that is only available in API 21+
         */

        mContext.registerReceiver(this, intentFilter);
    }

    /**
     * broadcasts the NetworkReconnected event when network goes from disconnected to connected
     * @param context
     */
    private void onConnectivityChanged(@NonNull Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean hasConnectivity = networkInfo != null
                && networkInfo.isConnected()
                && networkInfo.isAvailable();

                /*
                NOTE: if i have both data and wifi on, and then turn wifi off,
                hasConnectivity will still be true
                 */
        Log.d(getClass().getName(), "has network connectivity: " + hasConnectivity);
        if(hasConnectivity != mPreviouslyHadNetworkConnectivity)
        //network connected and couldn't connect before or vice versa.
        //need second variable to prevent multiple triggers due to multiple network providers
        {
            if(hasConnectivity) //reconnected
            {
                mBus.post(new LogEvent.AddLogEvent(
                        mEventLogFactory.createNetworkReconnectedLog()));
                mBus.post(new SystemEvent.NetworkReconnected());
            }
            else //disconnected
            {
                mBus.post(new LogEvent.AddLogEvent(
                        mEventLogFactory.createNetworkDisconnectedLog()));
            }
        }

        //this is a hack to prevent multiple network reconnected triggers, should think of a better solution
        mPreviouslyHadNetworkConnectivity = hasConnectivity;
    }

    private void onBatteryLevelLow()
    {
        //TODO do something
        Log.d(getClass().getName(), "battery level low");
    }

    private void onBatteryLevelOkay()
    {
        //TODO do something
        Log.d(getClass().getName(), "battery level okay");
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        switch (intent.getAction())
        {
            case ConnectivityManager.CONNECTIVITY_ACTION:
                onConnectivityChanged(context);
                break;
            case Intent.ACTION_BATTERY_LOW:
                onBatteryLevelLow();
                break;
            case Intent.ACTION_BATTERY_OKAY:
                /** From Intent.ACTION_BATTERY_OKAY documentation:
                 * Broadcast Action:  Indicates the battery is now okay after being low.
                 * This will be sent after {@link #ACTION_BATTERY_LOW} once the battery has
                 * gone back up to an okay state.
                 **/
                onBatteryLevelOkay();
                break;
        }
    }
}
