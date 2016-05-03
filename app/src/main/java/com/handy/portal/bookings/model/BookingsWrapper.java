package com.handy.portal.bookings.model;

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class BookingsWrapper
{
    @SerializedName("date")
    private Date date;
    @SerializedName("priority_access")
    private PriorityAccessInfo mPriorityAccessInfo;
    @SerializedName("jobs")
    private List<Booking> bookings;

    public static class PriorityAccessInfo implements Serializable
    {
        @SerializedName("minimum_keep_rate")
        private Integer mMinimumKeepRate; //percentage out of 100. range is 0-100 inclusive
        @SerializedName("current_keep_rate")
        private Integer mCurrentKeepRate; //percentage out of 100. range is 0-100 inclusive
        @SerializedName("status")
        private String mBookingsForDayStatus;
        @SerializedName("title")
        private String mMessageTitle;
        @SerializedName("description")
        private String mMessageDescription;

        public Integer getMinimumKeepRate()
        {
            return mMinimumKeepRate;
        }

        public Integer getCurrentKeepRate()
        {
            return mCurrentKeepRate;
        }

        @Nullable
        public BookingsForDayPriorityAccessStatus getBookingsForDayStatus()
        {
            if(mBookingsForDayStatus == null) return null;
            try
            {
                return BookingsForDayPriorityAccessStatus.valueOf(mBookingsForDayStatus.toUpperCase());
            }
            catch (Exception e)
            {
                //IllegalArgumentException if mBookingsForDayStatus can't be converted to an enum.
                // This should never expected to happen, so log it
                Crashlytics.logException(e);
                return null;
            }
        }

        /**
         * the access status for the bookings for the date associated with this object
         */
        public enum BookingsForDayPriorityAccessStatus
        {
            LOCKED,
            UNLOCKED,
            NEW_PRO
        }

        /**
         * title of the user-facing message that may be displayed based on the access status
         * @return
         */
        public String getMessageTitle()
        {
            return mMessageTitle;
        }

        /**
         * description of the user-facing message that may be displayed based on the access status
         * @return
         */
        public String getMessageDescription()
        {
            return mMessageDescription;
        }
    }

    public final Date getDate()
    {
        return date;
    }

    public PriorityAccessInfo getPriorityAccessInfo()
    {
        return mPriorityAccessInfo;
    }

    public final List<Booking> getBookings()
    {
        return bookings;
    }
}
