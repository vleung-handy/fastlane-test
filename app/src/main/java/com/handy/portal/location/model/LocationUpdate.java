package com.handy.portal.location.model;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 *
 */
public class LocationUpdate
{

    public double getLatitude()
    {
        return mLatitude;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public float getAccuracyMeters()
    {
        return mAccuracyMeters;
    }

    public double getAltitudeMeters()
    {
        return mAltitudeMeters;
    }

    public float getSpeed()
    {
        return mSpeed;
    }

    public float getBearingDegrees()
    {
        return mBearingDegrees;
    }

    public String getEventName()
    {
        return mEventName;
    }

    public Date getCapturedTimestamp()
    {
        return mCapturedTimestamp;
    }

    public float getBatteryLevelPercent()
    {
        return mBatteryLevelPercent;
    }

    public String getBookingId()
    {
        return mBookingId;
    }

    @SerializedName("latitude")
    double mLatitude;
    @SerializedName("longitude")
    double mLongitude;
    @SerializedName("accuracy")
    float mAccuracyMeters;
    @SerializedName("altitude")
    double mAltitudeMeters;
    @SerializedName("speed")
    float mSpeed;
    @SerializedName("bearing")
    float mBearingDegrees;
    @SerializedName("event_name")
    String mEventName;
    @SerializedName("captured_at")
    Date mCapturedTimestamp;
    @SerializedName("battery_level")
    float mBatteryLevelPercent; //0.95
    @SerializedName("connection_type")
    String mActiveNetworkType;
    @SerializedName("booking_id")
    String mBookingId;

    public LocationUpdate(double latitude,
                          double longitude,
                          float accuracyMeters,
                          double altitudeMeters,
                          float speed,
                          float bearingDegrees,
                          String eventName,
                          Date capturedTimestamp,
                          float batteryLevelPercent,
                          String bookingId)
    {
        mLatitude = latitude;
        mLongitude = longitude;
        mAccuracyMeters = accuracyMeters;
        mAltitudeMeters = altitudeMeters;
        mSpeed = speed;
        mBearingDegrees = bearingDegrees;
        mEventName = eventName;
        mCapturedTimestamp = capturedTimestamp;
        mBatteryLevelPercent = batteryLevelPercent;
        mBookingId = bookingId;
    }

    //TODO: move this?
    public static LocationUpdate from(@NonNull Location location,
                                      @NonNull LocationQueryStrategy locationQueryStrategy)
    {
        String eventName = locationQueryStrategy.getEventName();
        String bookingId = locationQueryStrategy.getBookingId();
        LocationUpdate locationUpdate = new LocationUpdate(
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                location.getAltitude(),
                location.getSpeed(),
                location.getBearing(),
                eventName,//event name, test only
                new Date(location.getTime()),
                0, //no battery level yet
                bookingId //no booking id yet
        );
        return locationUpdate;
    }

    /**
     * TODO: move
     *
     * @param batteryLevelPercent
     */
    public void setBatteryLevelPercent(final float batteryLevelPercent)
    {
        mBatteryLevelPercent = batteryLevelPercent;
    }

    public void setActiveNetworkType(final String activeNetworkType)
    {
        mActiveNetworkType = activeNetworkType;
    }

    /**
     * TODO: for testing/debugging purposes only
     *
     * @return
     */
    @Override
    public String toString()
    {
        String result = "lat: " + mLatitude
                + "\nlong: " + mLongitude
                + "\naccuracy: " + mAccuracyMeters
                + "\nalt: " + mAltitudeMeters
                + "\nspeed: " + mSpeed
                + "\nbearing: " + mBearingDegrees
                + "\nevent name: " + mEventName
                + "\ntimestamp: " + mCapturedTimestamp.toString()
                + "\nbattery level: " + mBatteryLevelPercent
                + "\nconnection type: " + mActiveNetworkType
                + "\nbooking id: " + mBookingId;
        return result;
    }
}
