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

    /**
     * TODO: what exactly does the server send?
     */
    @SerializedName("frequency")
    int mPollingIntervalSeconds; //every N seconds

    /**
     * TODO: what exactly does the server send?
     */
    @SerializedName("accuracy")
    int mLocationAccuracyPriority;

    public LocationQueryStrategy setStartDate(final Date startDate)
    {
        mStartDate = startDate;
        return this;
    }

    public LocationQueryStrategy setEndDate(final Date endDate)
    {
        mEndDate = endDate;
        return this;
    }

    public LocationQueryStrategy setPollingIntervalSeconds(final int pollingIntervalSeconds)
    {
        mPollingIntervalSeconds = pollingIntervalSeconds;
        return this;
    }

    public LocationQueryStrategy setLocationAccuracyPriority(final int locationAccuracyPriority)
    {
        mLocationAccuracyPriority = locationAccuracyPriority;
        return this;
    }

    public LocationQueryStrategy()
    {
        //TODO: REMOVE, FOR TESTING ONLY
        mStartDate = new Date();
        mEndDate = new Date(mStartDate.getTime() + DateTimeUtils.MILLISECONDS_IN_HOUR);
        mPollingIntervalSeconds = 1;
        mLocationAccuracyPriority = 2;
    }

    /**
     * TODO: REMOVE, FOR TESTING ONLY
     *
     * @param startDate
     * @param durationMinutes
     * @param pollingIntervalSeconds
     * @param locationAccuracyPriority
     */
    public LocationQueryStrategy(Date startDate, long durationMinutes,
                                 int pollingIntervalSeconds, int locationAccuracyPriority)
    {
        mStartDate = startDate;
        mEndDate = new Date(startDate.getTime() + durationMinutes * DateTimeUtils.MILLISECONDS_IN_MINUTE);
        mPollingIntervalSeconds = pollingIntervalSeconds;
        mLocationAccuracyPriority = locationAccuracyPriority;
    }

    protected LocationQueryStrategy(Parcel in)
    {
        mStartDate = new Date(in.readLong());
        mEndDate = new Date(in.readLong());
        mPollingIntervalSeconds = in.readInt();
        mLocationAccuracyPriority = in.readInt();
    }

    public int getLocationAccuracyPriority()
    {
        return mLocationAccuracyPriority;
    }

    public int getPollingIntervalSeconds()
    {
        return mPollingIntervalSeconds;
    }

    public Date getEndDate()
    {
        return mEndDate;
    }

    public Date getStartDate()
    {
        return mStartDate;
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
        dest.writeInt(mPollingIntervalSeconds);
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
                + "\npolling frequency: " + mPollingIntervalSeconds
                + "\nlocation accuracy: " + mLocationAccuracyPriority;
    }
}
