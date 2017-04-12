package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.model.Address;
import com.handy.portal.payments.model.PaymentInfo;

import java.util.Date;

public abstract class JobsLog extends EventLog {
    public static final String JOB_DETAILS_EVENT_CONTEXT = "job_details";

    @SerializedName("booking_id")
    private String mBookingId;
    @SerializedName("booking_type")
    private String mBookingType;
    @SerializedName("service_id")
    private String mServiceId;
    @SerializedName("region_id")
    private int mRegionId;
    @SerializedName("zipcode")
    private String mZipCode;
    @SerializedName("requested")
    private boolean mRequested;
    @SerializedName("date_start")
    private Date mDateStart;
    @SerializedName("frequency")
    private int mFrequency;
    @SerializedName("hours")
    private float mHours;
    @SerializedName("min_hours")
    private float mMinimumHours;
    @SerializedName("payment_to_provider")
    private int mPaymentToProvider;
    @SerializedName("hourly_rate")
    private int mHourlyRate;
    @SerializedName("bonus")
    private int mBonus;
    @SerializedName("currency_code")
    private String mCurrencyCode;
    @SerializedName("schedule_swap")
    private boolean mIsScheduleSwap;
    @SerializedName("schedule_conflict_booking_id")
    private String mScheduleConflictBookingId;
    @SerializedName("schedule_conflict_booking_type")
    private String mScheduleConflictBookingType;

    public JobsLog(final String eventType, final String eventContext, final Booking booking) {
        super(eventType, eventContext);
        mBookingId = booking.getId();
        mBookingType = booking.getType().name().toLowerCase();
        mServiceId = booking.getService();
        mRegionId = booking.getRegionId();
        mZipCode = getZipCode(booking.getAddress());
        mRequested = booking.isRequested();
        mDateStart = booking.getStartDate();
        mFrequency = booking.getFrequency();
        mHours = booking.getHours();
        mMinimumHours = booking.getMinimumHours();
        final PaymentInfo paymentToProvider = booking.getPaymentToProvider();
        if (paymentToProvider != null) {
            mPaymentToProvider = paymentToProvider.getAmount();
            mCurrencyCode = paymentToProvider.getCurrencyCode();
        }
        final PaymentInfo hourlyRate = booking.getHourlyRate();
        if (hourlyRate != null) {
            mHourlyRate = hourlyRate.getAmount();
            mCurrencyCode = hourlyRate.getCurrencyCode(); // just to be safe
        }
        if (booking.getBonusPaymentToProvider() != null) {
            mBonus = booking.getBonusPaymentToProvider().getAmount();
        }
        if (booking.canSwap()) {
            mIsScheduleSwap = true;
            final Booking swappableBooking = booking.getSwappableBooking();
            mScheduleConflictBookingId = swappableBooking.getId();
            mScheduleConflictBookingType = swappableBooking.getType().name().toLowerCase();
        }
    }

    private static String getZipCode(final Address address) {
        if (address != null) {
            return address.getZip();
        }
        return null;
    }
}
