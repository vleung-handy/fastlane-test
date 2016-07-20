package com.handy.portal.manager;

import android.content.Context;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.handy.portal.event.HandyEvent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class ConnectivityManager
{
    private final EventBus mBus;

    private boolean hasConnectivity = true;

    @Inject
    public ConnectivityManager(final EventBus bus)
    {
        mBus = bus;
    }

    public boolean hasConnectivity()
    {
        return hasConnectivity;
    }

    public void setHasConnectivity(final boolean hasConnectivity)
    {
        this.hasConnectivity = hasConnectivity;
        mBus.post(new HandyEvent.ConnectivityStatusUpdate(this.hasConnectivity()));
    }

    public void requestRefreshConnectivityStatus(Context context)
    {
        System.out.println("Someone said check your connectivity");
        //todo add a timer or something to stop a stampede
        refreshConnectivityStatus(context);

    }

    private void refreshConnectivityStatus(Context context)
    {
        boolean haveConnection = false;

        System.out.println("Refresh connection status");

        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null)
        { // connected to the internet
            if (activeNetwork.getType() == android.net.ConnectivityManager.TYPE_WIFI)
            {
                // connected to wifi
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                haveConnection = true;
            }
            else if (activeNetwork.getType() == android.net.ConnectivityManager.TYPE_MOBILE)
            {
                // connected to the mobile provider's data plan
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                haveConnection = true;
            }
            else
            {
                Toast.makeText(context, "I aint got no internets, or at least no matching network type", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            // not connected to the internet
            Toast.makeText(context, "I aint got no internets", Toast.LENGTH_SHORT).show();
        }

        System.out.println("Result of connection check " + haveConnection);
        //todo : update member var of connection status

        setHasConnectivity(haveConnection);


    }
}
