package com.handy.portal.location.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.util.DateTimeUtils;

import java.util.Date;

//TODO: at testing stage. not finalized, needs lots of refactoring


/**
 * part of the LocationQuerySchedule model received from the server
 * <p/>
 * the location service uses this to determine when and at what accuracy we should get location updates
 */
public class LocationQueryStrategy implements Parcelable
{
    public static final Creator<LocationQueryStrategy> CREATOR = new Creator<LocationQueryStrategy>()
    {
        @Override
        public LocationQueryStrategy createFromParcel(Parcel in)
        {
            return new LocationQueryStrategy(in);
        }

        @Override
        public LocationQueryStrategy[] newArray(int size)
        {
            return new LocationQueryStrategy[size];
        }
    };
    @SerializedName("date_start")
    Date mStartDate;
    @SerializedName("date_end")
    Date mEndDate;
    @SerializedName("frequency")
    int mLocationPollingIntervalSeconds; //every N seconds
    @SerializedName("frequency")
    int mServerPollingIntervalSeconds; //every N seconds
    @SerializedName("accuracy") //priority level
    int mLocationAccuracyPriority;
    @SerializedName("distance_filter")
    int mDistanceFilterMeters;
    @SerializedName("booking_id")
    String mBookingId;

    public static final int ACCURACY_BALANCED_POWER_PRIORITIY = 1;
    public static final int ACCURACY_HIGH_PRIORITY = 2;
    public LocationQueryStrategy()
    {
        //TODO: REMOVE, FOR TESTING ONLY
        mStartDate = new Date();
        mEndDate = new Date(mStartDate.getTime() + DateTimeUtils.MILLISECONDS_IN_HOUR);
        mDistanceFilterMeters = 0;
        mLocationPollingIntervalSeconds = 1;
        mLocationAccuracyPriority = ACCURACY_HIGH_PRIORITY;
    }

    public void setDistanceFilterMeters(final int distanceFilterMeters)
    {
        this.mDistanceFilterMeters = distanceFilterMeters;
    }

    public int getDistanceFilterMeters()
    {
        return mDistanceFilterMeters;
    }

    public String getBookingId()
    {
        return mBookingId;
    }

    /**
     * TODO: REMOVE, FOR TESTING ONLY
     *
     * @param startDate
     * @param durationMinutes
     * @param locationPollingIntervalSeconds
     * @param locationAccuracyPriority
     */
    public LocationQueryStrategy(
            String tag,
            Date startDate,
                                 long durationMinutes,
                                 int locationPollingIntervalSeconds,
                                 int serverPollingIntervalSeconds,
                                 int locationAccuracyPriority)
    {
        mBookingId = tag;
        mStartDate = startDate;
        mEndDate = new Date(startDate.getTime() + durationMinutes * DateTimeUtils.MILLISECONDS_IN_MINUTE);
        mLocationPollingIntervalSeconds = locationPollingIntervalSeconds;
        mServerPollingIntervalSeconds = serverPollingIntervalSeconds;
        mLocationAccuracyPriority = locationAccuracyPriority;
    }

    public LocationQueryStrategy setServerPollingIntervalSeconds(final int serverPollingIntervalSeconds)
    {
        mServerPollingIntervalSeconds = serverPollingIntervalSeconds;
        return this;
    }

    protected LocationQueryStrategy(Parcel in)
    {
        mStartDate = new Date(in.readLong());
        mEndDate = new Date(in.readLong());
        mServerPollingIntervalSeconds = in.readInt();
        mLocationPollingIntervalSeconds = in.readInt();
        mDistanceFilterMeters = in.readInt();
        mLocationAccuracyPriority = in.readInt();
    }

    public int getServerPollingIntervalSeconds()
    {
        return mServerPollingIntervalSeconds;
    }

    public int getLocationAccuracyPriority()
    {
        return mLocationAccuracyPriority;
    }

    public LocationQueryStrategy setLocationAccuracyPriority(final int locationAccuracyPriority)
    {
        mLocationAccuracyPriority = locationAccuracyPriority;
        return this;
    }

    public int getLocationPollingIntervalSeconds()
    {
        return mLocationPollingIntervalSeconds;
    }

    public LocationQueryStrategy setLocationPollingIntervalSeconds(final int locationPollingIntervalSeconds)
    {
        mLocationPollingIntervalSeconds = locationPollingIntervalSeconds;
        return this;
    }

    public Date getEndDate()
    {
        return mEndDate;
    }

    public LocationQueryStrategy setEndDate(final Date endDate)
    {
        mEndDate = endDate;
        return this;
    }

    public Date getStartDate()
    {
        return mStartDate;
    }

    public LocationQueryStrategy setStartDate(final Date startDate)
    {
        mStartDate = startDate;
        return this;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeLong(mStartDate.getTime());
        dest.writeLong(mEndDate.getTime());
        dest.writeInt(mServerPollingIntervalSeconds);
        dest.writeInt(mLocationPollingIntervalSeconds);
        dest.writeInt(mDistanceFilterMeters);
        dest.writeInt(mLocationAccuracyPriority);
    }

    /**
     * for debugging purposes only
     *
     * @return
     */
    @Override
    public String toString()
    {
        return "start date: " + mStartDate.toString()
                + "\nend date: " + mEndDate.toString()
                + "\nserver posting frequency (s): " + mServerPollingIntervalSeconds
                + "\npolling frequency (s): " + mLocationPollingIntervalSeconds
                + "\ndistance filter (m): " + mDistanceFilterMeters
                + "\nlocation accuracy: " + mLocationAccuracyPriority;
    }
}
