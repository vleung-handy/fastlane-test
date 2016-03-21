package com.handy.portal.location.scheduler.geofences.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.location.scheduler.model.ScheduleStrategy;

import java.util.Date;

public class BookingGeofenceStrategy extends ScheduleStrategy
{
    @SerializedName("booking_id")
    String mBookingId;
    @SerializedName("start_date")
    Date mStartDate;
    @SerializedName("end_date")
    Date mEndDate;
    @SerializedName("longitude")
    double mLongitude;
    @SerializedName("latitude")
    double mLatitude;
    @SerializedName("radius")
    float mRadius;

    /**
     * TODO REMOVE THIS, FOR TEST PURPOSES ONLY
     * @param bookingId
     * @param startDate
     * @param endDate
     * @param latitude
     * @param longitude
     * @param radius
     */
    public BookingGeofenceStrategy(String bookingId, Date startDate, Date endDate, double latitude, double longitude, float radius)
    {
        mBookingId = bookingId;
        mStartDate = startDate;
        mEndDate = endDate;
        mLatitude = latitude;
        mLongitude = longitude;
        mRadius = radius;
    }

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
