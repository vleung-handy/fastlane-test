package com.handy.portal.location.scheduler.geofences.handler;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationUpdate;
import com.handy.portal.location.scheduler.geofences.model.BookingGeofenceStrategy;
import com.handy.portal.location.scheduler.handler.LocationScheduleHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO CLEAN UP!!!
 */
public class BookingGeofenceScheduleHandler
        extends LocationScheduleHandler<BookingGeofenceStrategyHandler, BookingGeofenceStrategy>
    implements BookingGeofenceStrategyHandler.BookingGeofenceStrategyCallbacks
{
    private static final int ALARM_REQUEST_CODE = 2;
    private static final String WAKEUP_ALARM_BROADCAST_ID = "GEOFENCE_WAKEUP_ALARM_BROADCAST_ID";
    private final static String BUNDLE_EXTRA_BOOKING_GEOFENCE = "BUNDLE_EXTRA_BOOKING_GEOFENCE";

    public BookingGeofenceScheduleHandler(@NonNull final LinkedList<BookingGeofenceStrategy> locationQuerySchedule, @NonNull final GoogleApiClient googleApiClient, @NonNull final Context context)
    {
        super(locationQuerySchedule, googleApiClient, context);
        stopAllActiveStrategies();
    }

    @Override
    protected int getAlarmRequestCode()
    {
        return ALARM_REQUEST_CODE;
    }

    @Override
    protected String getAlarmBroadcastId()
    {
        return WAKEUP_ALARM_BROADCAST_ID;
    }

    @Override
    protected IntentFilter getAlarmIntentFilter()
    {
        IntentFilter intentFilter = super.getAlarmIntentFilter();
        intentFilter.addAction(GEOFENCE_TRIGGERED_BROADCAST_ID);
        return intentFilter;
    }

    @Override
    protected void stopStrategy(final BookingGeofenceStrategyHandler bookingGeofenceStrategyHandler)
    {
        Log.d(getClass().getName(), "stopping strategy: " + bookingGeofenceStrategyHandler.toString());
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getPendingIntent(bookingGeofenceStrategyHandler.getBookingGeofenceStrategy()));
        List<String> requestIdsList = new LinkedList<>();
        for(BookingGeofenceStrategy b : mSortedLocationStrategies)
        {
            requestIdsList.add(b.getBookingId());
        }
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, requestIdsList);
    }

    @Override
    public BookingGeofenceStrategyHandler createStrategyHandler(final BookingGeofenceStrategy bookingGeofenceStrategy)
    {
        return new BookingGeofenceStrategyHandler(bookingGeofenceStrategy, this, mContext);
    }

    @Override
    protected String getStrategyBundleExtraKey()
    {
        return BUNDLE_EXTRA_BOOKING_GEOFENCE;
    }

    @Override
    public void onNetworkReconnected()
    {
        //todo do something
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        Log.d(getClass().getName(), "geofence broadcast receiver got something");
        Bundle args = intent.getExtras();
        if (intent.getAction() == null)
        {
            Log.e(getClass().getName(), "Intent action is null on receive alarm");
            return;
        }


        //todo put in function in superclass
        Log.d(getClass().getName(), "intent action: " + intent.getAction());

        switch (intent.getAction())
        {
            //TODO: refactor this
            case WAKEUP_ALARM_BROADCAST_ID:
                Log.d(getClass().getName(), "Woke up");

                /**
                 * using byte array to avoid exception
                 *
                 * http://blog.nocturnaldev.com/blog/2013/09/01/parcelable-in-pendingintent/
                 */
                if(args == null) return;
                byte[] strategyByteArray = args.getByteArray(getStrategyBundleExtraKey());
                if (strategyByteArray == null) { return; }
                Parcel strategyParcel = Parcel.obtain();
                strategyParcel.unmarshall(strategyByteArray, 0, strategyByteArray.length);
                strategyParcel.setDataPosition(0);

                BookingGeofenceStrategy strategy = BookingGeofenceStrategy.CREATOR.createFromParcel(strategyParcel);

                onStrategyAlarmTriggered(strategy);
                break;
            case GEOFENCE_TRIGGERED_BROADCAST_ID:

                handleGeofenceIntent(intent);
                break;
        }
    }

    public void handleGeofenceIntent(Intent intent)
    {
        Log.d(getClass().getName(), "got geofence intent");

        //todo put in function in superclass
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError())
        {
            Log.e(getClass().getName(), "error getting geofence event: "+ geofencingEvent.getErrorCode());
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Location location = geofencingEvent.getTriggeringLocation();
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            Log.i(getClass().getName(), "got geofence transition: " + geofenceTransition);
            String eventName = getLocationUpdateEventNameFromGeofenceTransition(geofenceTransition);
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            List<LocationUpdate> locationUpdateList = new ArrayList<>();
            for(Geofence geofence : triggeringGeofences)
            {
                LocationUpdate locationUpdate = LocationUpdate.from(location);
                locationUpdate.setBookingId(geofence.getRequestId());
                locationUpdate.setEventName(eventName);
                locationUpdateList.add(locationUpdate);
            }

            LocationBatchUpdate locationBatchUpdate = new LocationBatchUpdate(locationUpdateList.toArray(new LocationUpdate[locationUpdateList.size()]));

            Log.d(getClass().getName(), "location batch update: " + locationBatchUpdate.toString());
            Toast.makeText(mContext, "Geofence triggered: " + geofenceTransition, Toast.LENGTH_SHORT).show();
            onLocationBatchUpdateReady(locationBatchUpdate); //don't actually post during test phase todo enable this later
        }
    }

    private String getLocationUpdateEventNameFromGeofenceTransition(int geofenceTransition)
    {
        switch (geofenceTransition)
        {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "BOOKING_GEOFENCE_ENTERED"; //todo no magic strings
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "BOOKING_GEOFENCE_EXITED";
        }
        return "";
    }

    static final int PENDING_INTENT_REQUEST_CODE = 3;
    static final String GEOFENCE_TRIGGERED_BROADCAST_ID = "GEOFENCE_TRIGGERED_BROADCAST_ID";
    @Override
    public PendingIntent getPendingIntent(BookingGeofenceStrategy strategy)
    {
        //also add the booking strategy

        Log.d(getClass().getName(), "creating pending intent...");
        Intent intent = new Intent(GEOFENCE_TRIGGERED_BROADCAST_ID);
        intent.setAction(GEOFENCE_TRIGGERED_BROADCAST_ID); //probably redundant, test this
//        intent.setPackage(mContext.getPackageName());

        //todo put in function in superclass
        return PendingIntent.getBroadcast(mContext, PENDING_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
