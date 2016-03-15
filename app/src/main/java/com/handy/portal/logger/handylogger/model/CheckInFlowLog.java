package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class CheckInFlowLog extends EventLog
{
    private static final String EVENT_CONTEXT = "scheduled_jobs";

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

    public CheckInFlowLog(
            final String eventType, final String bookingId, final double proLatitude,
            final double proLongitude, final double bookingLatitude, final double bookingLongitude,
            final double accuracy, final double distance)
    {
        super(eventType, EVENT_CONTEXT);
        mBookingId = bookingId;
        mProLatitude = proLatitude;
        mProLongitude = proLongitude;
        mBookingLatitude = bookingLatitude;
        mBookingLongitude = bookingLongitude;
        mAccuracy = accuracy;
        mDistance = distance;
    }

    public static class OnMyWay extends CheckInFlowLog
    {
        private static final String EVENT_TYPE = "on_my_way_submitted";

        public OnMyWay(
                final String bookingId, final double proLatitude, final double proLongitude,
                final double bookingLatitude, final double bookingLongitude, final double accuracy,
                final double distance)
        {
            super(EVENT_TYPE, bookingId, proLatitude, proLongitude, bookingLatitude,
                    bookingLongitude, accuracy, distance);
        }
    }


    public static class CheckIn extends CheckInFlowLog
    {
        private static final String EVENT_TYPE = "manual_checkin_submitted";

        public CheckIn(
                final String bookingId, final double proLatitude, final double proLongitude,
                final double bookingLatitude, final double bookingLongitude, final double accuracy,
                double distance)
        {
            super(EVENT_TYPE, bookingId, proLatitude, proLongitude, bookingLatitude,
                    bookingLongitude, accuracy, distance);
        }
    }


    public static class CheckOut extends CheckInFlowLog
    {
        private static final String EVENT_TYPE = "manual_checkout_submitted";

        public CheckOut(
                final String bookingId, final double proLatitude, final double proLongitude,
                final double bookingLatitude, final double bookingLongitude, final double accuracy,
                final double distance)
        {
            super(EVENT_TYPE, bookingId, proLatitude, proLongitude, bookingLatitude,
                    bookingLongitude, accuracy, distance);
        }
    }
}
