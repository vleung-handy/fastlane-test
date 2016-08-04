package com.handy.portal.layer;

import android.util.Log;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerSyncListener;

import java.util.List;

/**
 */
public abstract class SimpleLayerSyncListener implements LayerSyncListener
{
    private static final String TAG = SimpleLayerSyncListener.class.getName();

    @Override
    public void onBeforeSync(final LayerClient layerClient, final SyncType syncType)
    {
        Log.d(TAG, "onBeforeSync: ");
    }

    @Override
    public void onSyncProgress(final LayerClient layerClient, final SyncType syncType, final int i)
    {
        Log.d(TAG, "onSyncProgress: ");
    }

    @Override
    public void onAfterSync(final LayerClient layerClient, final SyncType syncType)
    {
        Log.d(TAG, "onAfterSync: ");
    }

    @Override
    public void onSyncError(final LayerClient layerClient, final List<LayerException> list)
    {
        Log.d(TAG, "onSyncError: ");
    }
}
