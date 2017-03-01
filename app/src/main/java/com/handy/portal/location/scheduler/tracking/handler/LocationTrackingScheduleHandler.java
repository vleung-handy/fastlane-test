package com.handy.portal.location.scheduler.tracking.handler;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.handy.portal.location.LocationEvent;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.handler.ScheduleHandler;
import com.handy.portal.location.scheduler.tracking.model.LocationTrackingScheduleStrategy;

import java.util.LinkedList;

/**
 * TODO: at testing stage. totally needs refactoring
 * <p/>
 * does whatever needs to be done given a location query schedule
 */
public class LocationTrackingScheduleHandler
        extends ScheduleHandler<LocationTrackingScheduleStrategyHandler, LocationTrackingScheduleStrategy>
        implements LocationTrackingScheduleStrategyHandler.LocationStrategyCallbacks {
    private Handler mHandler = new Handler();
    private static final int ALARM_REQUEST_CODE = 1;
    private static final String LOCATION_TRACKING_ALARM_BROADCAST_ACTION = "LOCATION_TRACKING_ALARM_BROADCAST_ACTION";
    private final static String BUNDLE_EXTRA_LOCATION_TRACKING_STRATEGY = "BUNDLE_EXTRA_LOCATION_TRACKING_STRATEGY";

    private GoogleApiClient mGoogleApiClient;

    public LocationTrackingScheduleHandler(@NonNull final LinkedList<LocationTrackingScheduleStrategy> locationQuerySchedule, @NonNull final GoogleApiClient googleApiClient, @NonNull final Context context) {
        super(locationQuerySchedule, context);
        mGoogleApiClient = googleApiClient;
    }

    @Override
    protected int getWakeupAlarmRequestCode() {
        return ALARM_REQUEST_CODE;
    }

    @Override
    protected String getWakeupAlarmBroadcastAction() {
        return LOCATION_TRACKING_ALARM_BROADCAST_ACTION;
    }

    @Override
    public LocationTrackingScheduleStrategyHandler createStrategyHandler(final LocationTrackingScheduleStrategy locationTrackerStrategy) {
        return new LocationTrackingScheduleStrategyHandler(
                locationTrackerStrategy,
                this,
                mHandler,
                mContext);
    }

    @Override
    protected Parcelable.Creator<LocationTrackingScheduleStrategy> getStrategyCreator() {
        return LocationTrackingScheduleStrategy.CREATOR;
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    protected String getStrategyBundleExtraKey() {
        return BUNDLE_EXTRA_LOCATION_TRACKING_STRATEGY;
    }

    /**
     * called by location service when network reconnected event is broadcast
     * <p>
     * don't want to subscribe to event here, because this object does not have a strict lifecycle
     * unlike the location service, so it is harder to guarantee that the bus will be unregistered
     * when we no longer care about this object (ex. what if this object loses its reference?)
     */
    public void onNetworkReconnected() {
        super.onNetworkReconnected();
        restartActiveStrategies();
    }

    public void onLocationUpdate(final Location location) {
        mBus.post(new LocationEvent.LocationUpdated(location));
    }

    public void onLocationBatchUpdateReady(final LocationBatchUpdate locationBatchUpdate) {
        mBus.post(new LocationEvent.SendGeolocationRequest(locationBatchUpdate));
    }
}
