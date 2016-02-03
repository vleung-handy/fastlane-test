package com.handy.portal.location.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

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

    public int getBookingId()
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
    @SerializedName("booking_id")
    int mBookingId;

    public LocationUpdate(double latitude,
                          double longitude,
                          float accuracyMeters,
                          double altitudeMeters,
                          float speed,
                          float bearingDegrees,
                          String eventName,
                          Date capturedTimestamp,
                          float batteryLevelPercent,
                          int bookingId)
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

    /**
     * TODO: for testing/debugging purposes only
     * @return
     */
    @Override
    public String toString()
    {
        String result = "lat: " + mLatitude
                + "\nlong: " + mLongitude
                + "\naccuracy: " + mAccuracyMeters
                + "\nalt: "+ mAltitudeMeters
                + "\nspeed: " + mSpeed
                + "\nbearing: " + mBearingDegrees
                + "\nevent name: " + mEventName
                + "\ntimestamp: " + mCapturedTimestamp.toString()
                + "\nbattery level: " + mBatteryLevelPercent
                + "\nbooking id: " + mBookingId;
        return result;
    }
}
