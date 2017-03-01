package com.handy.portal.logger.handylogger.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.model.LocationData;
import com.handy.portal.library.util.LogUtils;
import com.handy.portal.library.util.MathUtils;

public class CheckInFlowLog extends EventLog {
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

    public CheckInFlowLog(final String eventType,
                          @NonNull final Booking booking,
                          final LocationData location) {
        super(eventType, EVENT_CONTEXT);
        mBookingId = booking.getId();
        mProLatitude = LogUtils.getLatitude(location);
        mProLongitude = LogUtils.getLongitude(location);
        mBookingLatitude = LogUtils.getLatitude(booking);
        mBookingLongitude = LogUtils.getLongitude(booking);
        mAccuracy = LogUtils.getAccuracy(location);
        mDistance = MathUtils.getDistance(mProLatitude, mProLatitude, mBookingLatitude, mBookingLongitude);
    }

    public static class OnMyWaySubmitted extends CheckInFlowLog {
        private static final String EVENT_TYPE = "on_my_way_submitted";

        public OnMyWaySubmitted(final Booking booking, final LocationData location) {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class OnMyWaySuccess extends CheckInFlowLog {
        private static final String EVENT_TYPE = "on_my_way_success";

        public OnMyWaySuccess(final Booking booking, final LocationData location) {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class OnMyWayFailure extends CheckInFlowLog {
        private static final String EVENT_TYPE = "on_my_way_failure";

        public OnMyWayFailure(final Booking booking, final LocationData location) {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class CheckInSubmitted extends CheckInFlowLog {
        private static final String EVENT_TYPE = "manual_checkin_submitted";

        public CheckInSubmitted(final Booking booking, final LocationData location) {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class CheckInSuccess extends CheckInFlowLog {
        private static final String EVENT_TYPE = "manual_checkin_success";

        public CheckInSuccess(final Booking booking, final LocationData location) {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class CheckInFailure extends CheckInFlowLog {
        private static final String EVENT_TYPE = "manual_checkin_failure";

        public CheckInFailure(final Booking booking, final LocationData location) {
            super(EVENT_TYPE, booking, location);
        }
    }
}
