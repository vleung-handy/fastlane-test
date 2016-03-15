package com.handy.portal.location.scheduler.handler;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by vleung on 3/16/16.
 */
public abstract class LocationStrategyHandler
{
    protected abstract boolean isStrategyExpired();
    protected abstract void buildBatchUpdateAndNotifyReady();
    protected abstract void requestUpdates(GoogleApiClient googleApiClient);
}
