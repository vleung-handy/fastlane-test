package com.handy.portal.layer;

import android.util.Log;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerConnectionListener;

/**
 */
public class MyConnectionListener implements LayerConnectionListener
{
    private static final String TAG = "MyConnectionListener";

    @Override
    public void onConnectionConnected(LayerClient client)
    {
        Log.d(TAG, "onConnectionConnected() called with: client = [" + client + "]");
        client.authenticate();
    }

    @Override
    public void onConnectionDisconnected(LayerClient arg0)
    {
        Log.d(TAG, "onConnectionDisconnected() called with: arg0 = [" + arg0 + "]");
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnectionError(LayerClient arg0, LayerException e)
    {
        Log.d(TAG, "onConnectionError() called with: arg0 = [" + arg0 + "], e = [" + e + "]");
        // TODO Auto-generated method stub
    }

}
