package com.handy.portal.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.handy.portal.event.HandyEvent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class HandyConnectivityManager
{
    private final EventBus mBus;

    private boolean mHasConnectivity = true;
    private long mOfflineStartTime = 0;

    @Inject
    public HandyConnectivityManager(final EventBus bus)
    {
        mBus = bus;
    }

    public boolean hasConnectivity()
    {
        return mHasConnectivity;
    }

    public long getOfflineStartTime() { return mOfflineStartTime; }

    public void setHasConnectivity(final boolean hasConnectivity)
    {
        if (mHasConnectivity && !hasConnectivity)
        {
            mOfflineStartTime = System.currentTimeMillis();
        }

        mHasConnectivity = hasConnectivity;
        mBus.post(new HandyEvent.ConnectivityStatusUpdate(mHasConnectivity, mOfflineStartTime));
    }

    public void requestRefreshConnectivityStatus(Context context)
    {
        System.out.println("CSD - Someone said check your connectivity");
        //todo add a timer or something to stop a stampede
        refreshConnectivityStatus(context);
    }

    private void refreshConnectivityStatus(Context context)
    {
        boolean haveConnection = false;
        boolean hadConnection = mHasConnectivity;

        System.out.println("Refresh connection status");

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null)
        { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                // connected to wifi
                haveConnection = true;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                // connected to the mobile provider's data plan
                haveConnection = true;
            }
        }

        if (haveConnection && !hadConnection)
        {
            Toast.makeText(context, "Connection Restored", Toast.LENGTH_SHORT).show();
        }
        else if (!haveConnection && hadConnection)
        {
            Toast.makeText(context, "Connection Lost", Toast.LENGTH_SHORT).show();
        }


        System.out.println("Result of connection check " + haveConnection);
        setHasConnectivity(haveConnection);
    }
}
