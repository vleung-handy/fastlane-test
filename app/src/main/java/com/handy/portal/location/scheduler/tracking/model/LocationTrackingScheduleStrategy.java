package com.handy.portal.location.scheduler.tracking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.location.scheduler.model.ScheduleStrategy;

import java.util.Date;

/**
 * part of the LocationScheduleStrategies model received from the server
 * <p>
 * this model defines the parameters of a LocationRequest, the time range during which it should be active,
 * and how often updates should be posted to the server
 */
public class LocationTrackingScheduleStrategy extends ScheduleStrategy implements Parcelable {
    public static final Creator<LocationTrackingScheduleStrategy> CREATOR = new Creator<LocationTrackingScheduleStrategy>() {
        @Override
        public LocationTrackingScheduleStrategy createFromParcel(Parcel in) {
            return new LocationTrackingScheduleStrategy(in);
        }

        @Override
        public LocationTrackingScheduleStrategy[] newArray(int size) {
            return new LocationTrackingScheduleStrategy[size];
        }
    };
    @SerializedName("start_date")
    Date mStartDate;
    @SerializedName("end_date")
    Date mEndDate;
    @SerializedName("poll_frequency")
    int mLocationPollingIntervalSeconds; //every N seconds
    @SerializedName("post_frequency")
    int mServerPollingIntervalSeconds; //every N seconds
    @SerializedName("accuracy") //how accurate we want the location updates to be
            int mAccuracy;
    @SerializedName("distance_filter")
    int mDistanceFilterMeters;

    public int getDistanceFilterMeters() {
        return mDistanceFilterMeters;
    }

    protected LocationTrackingScheduleStrategy(Parcel in) {
        mStartDate = new Date(in.readLong());
        mEndDate = new Date(in.readLong());
        mServerPollingIntervalSeconds = in.readInt();
        mLocationPollingIntervalSeconds = in.readInt();
        mDistanceFilterMeters = in.readInt();
        mAccuracy = in.readInt();
    }

    public int getServerPollingIntervalSeconds() {
        return mServerPollingIntervalSeconds;
    }

    public int getAccuracy() {
        return mAccuracy;
    }

    public int getLocationPollingIntervalSeconds() {
        return mLocationPollingIntervalSeconds;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(mStartDate.getTime());
        dest.writeLong(mEndDate.getTime());
        dest.writeInt(mServerPollingIntervalSeconds);
        dest.writeInt(mLocationPollingIntervalSeconds);
        dest.writeInt(mDistanceFilterMeters);
        dest.writeInt(mAccuracy);
    }

    /**
     * for debugging purposes only
     *
     * @return
     */
    @Override
    public String toString() {
        return "start date: " + mStartDate.toString()
                + "\nend date: " + mEndDate.toString()
                + "\nserver posting frequency (s): " + mServerPollingIntervalSeconds
                + "\npolling frequency (s): " + mLocationPollingIntervalSeconds
                + "\ndistance filter (m): " + mDistanceFilterMeters
                + "\nlocation accuracy: " + mAccuracy;
    }
}
