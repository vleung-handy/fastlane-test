package com.handy.portal.location.scheduler.geofences.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.location.scheduler.model.ScheduleStrategy;

import java.util.Date;

public class BookingGeofenceStrategy extends ScheduleStrategy
{
    @SerializedName("booking_id")
    String mBookingId; //booking id this geofence is associated with
    @SerializedName("start_date")
    Date mStartDate; //start date of the geofence active period
    @SerializedName("end_date")
    Date mEndDate; //end date of the geofence active period
    @SerializedName("longitude")
    double mLongitude; //longitude of the geofence center, in degrees
    @SerializedName("latitude")
    double mLatitude; //latitude of the geofence center, in degrees
    @SerializedName("radius")
    float mRadius; //radius of the geofence, in meters

    protected BookingGeofenceStrategy(Parcel in)
    {
        mStartDate = new Date(in.readLong());
        mEndDate = new Date(in.readLong());
        mBookingId = in.readString();
        mLongitude = in.readDouble();
        mLatitude = in.readDouble();
        mRadius = in.readFloat();
    }

    public static final Creator<BookingGeofenceStrategy> CREATOR = new Creator<BookingGeofenceStrategy>()
    {
        @Override
        public BookingGeofenceStrategy createFromParcel(Parcel in)
        {
            return new BookingGeofenceStrategy(in);
        }

        @Override
        public BookingGeofenceStrategy[] newArray(int size)
        {
            return new BookingGeofenceStrategy[size];
        }
    };

    public String getBookingId()
    {
        return mBookingId;
    }

    public Date getStartDate()
    {
        return mStartDate;
    }

    public Date getEndDate()
    {
        return mEndDate;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public float getRadius()
    {
        return mRadius;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeLong(getStartDate().getTime());
        dest.writeLong(getEndDate().getTime());
        dest.writeString(mBookingId);
        dest.writeDouble(mLongitude);
        dest.writeDouble(mLatitude);
        dest.writeFloat(mRadius);
    }

    @Override
    public String toString()
    {
        return "start date: " + mStartDate.toString()
                + "\nend date: " + mEndDate.toString()
                + "\nlatitude: " + mLatitude
                + "\nlongitude: " + mLongitude
                + "\nradius: " + mRadius
                + "\nbooking id: " + mBookingId;
    }
}
