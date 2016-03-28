package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.constant.LocationKey;
import com.handy.portal.model.Address;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.util.MathUtils;

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

    public CheckInFlowLog(final String eventType, final Booking booking, final LocationData location)
    {
        super(eventType, EVENT_CONTEXT);
        mBookingId = booking.getId();
        mProLatitude = getLatitude(location);
        mProLongitude = getLongitude(location);
        mBookingLatitude = getLatitude(booking);
        mBookingLongitude = getLongitude(booking);
        mAccuracy = getAccuracy(location);
        mDistance = MathUtils.getDistance(mProLatitude, mProLatitude, mBookingLatitude, mBookingLongitude);
    }

    public static class OnMyWay extends CheckInFlowLog
    {
        private static final String EVENT_TYPE = "on_my_way_submitted";

        public OnMyWay(final Booking booking, final LocationData location)
        {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class CheckIn extends CheckInFlowLog
    {
        private static final String EVENT_TYPE = "manual_checkin_submitted";

        public CheckIn(final Booking booking, final LocationData location)
        {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class CheckOut extends CheckInFlowLog
    {
        private static final String EVENT_TYPE = "manual_checkout_submitted";

        public CheckOut(final Booking booking, final LocationData location)
        {
            super(EVENT_TYPE, booking, location);
        }
    }

    private double getLatitude(final Booking booking)
    {
        final Address address = booking.getAddress();
        final Booking.Coordinates midpoint = booking.getMidpoint();
        if (address != null)
        {
            return address.getLatitude();
        }
        else if (midpoint != null)
        {
            return midpoint.getLatitude();
        }
        else
        {
            return 0;
        }
    }

    private double getLongitude(final Booking booking)
    {
        final Address address = booking.getAddress();
        final Booking.Coordinates midpoint = booking.getMidpoint();
        if (address != null)
        {
            return address.getLongitude();
        }
        else if (midpoint != null)
        {
            return midpoint.getLongitude();
        }
        else
        {
            return 0;
        }
    }

    private double getLatitude(LocationData location)
    {
        try
        {
            return Double.parseDouble(location.getLocationMap().get(LocationKey.LATITUDE));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private double getLongitude(LocationData location)
    {
        try
        {
            return Double.parseDouble(location.getLocationMap().get(LocationKey.LONGITUDE));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private double getAccuracy(LocationData location)
    {
        try
        {
            return Double.parseDouble(location.getLocationMap().get(LocationKey.ACCURACY));
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}
