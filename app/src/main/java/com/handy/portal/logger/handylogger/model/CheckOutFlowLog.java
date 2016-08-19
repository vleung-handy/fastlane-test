package com.handy.portal.logger.handylogger.model;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.util.LogUtils;
import com.handy.portal.library.util.MathUtils;
import com.handy.portal.model.LocationData;

public class CheckOutFlowLog extends EventLog
{
    @SerializedName("booking_id")
    private String mBookingId;
    @SerializedName("pro_latitude")
    private double mProLatitude;
    @SerializedName("pro_longitude")
    private double mProLongitude;
    @SerializedName("booking_latitude")
    private double mBookingLatitude;
    @SerializedName("booking_longitude")
    private double mBookingLongitude;
    @SerializedName("accuracy")
    private double mAccuracy;
    @SerializedName("distance_to_job")
    private double mDistance;

    private static final String EVENT_CONTEXT = "checkout_flow";

    public CheckOutFlowLog(final String eventType,
                           @NonNull final Booking booking,
                           final LocationData location)
    {
        super(eventType, EVENT_CONTEXT);
        mBookingId = booking.getId();
        mProLatitude = LogUtils.getLatitude(location);
        mProLongitude = LogUtils.getLongitude(location);
        mBookingLatitude = LogUtils.getLatitude(booking);
        mBookingLongitude = LogUtils.getLongitude(booking);
        mAccuracy = LogUtils.getAccuracy(location);
        mDistance = MathUtils.getDistance(mProLatitude, mProLatitude, mBookingLatitude, mBookingLongitude);
    }

    public static class CheckOutSubmitted extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "manual_checkout_submitted";

        public CheckOutSubmitted(final Booking booking, final LocationData location)
        {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class CheckOutSuccess extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "manual_checkout_success";

        public CheckOutSuccess(final Booking booking, final LocationData location)
        {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class CheckOutError extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "manual_checkout_error";

        public CheckOutError(final Booking booking, final LocationData location)
        {
            super(EVENT_TYPE, booking, location);
        }
    }
}
