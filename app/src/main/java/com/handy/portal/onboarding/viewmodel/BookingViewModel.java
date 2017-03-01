package com.handy.portal.onboarding.viewmodel;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.TextUtils;

public class BookingViewModel {
    private Booking mBooking;
    public boolean mSelected;

    public BookingViewModel(final Booking booking) {
        mBooking = booking;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(final boolean selected) {
        this.mSelected = selected;
    }

    public String getTitle() {
        if (mBooking.isProxy()) {
            return mBooking.getLocationName();
        }
        else {
            return mBooking.getAddress().getShortRegion();
        }
    }

    public String getSubTitle() {
        String startTime = DateTimeUtils.formatDateTo12HourClock(mBooking.getStartDate());
        String endTime = DateTimeUtils.formatDateTo12HourClock(mBooking.getEndDate());

        if (!TextUtils.isNullOrEmpty(startTime) && !TextUtils.isNullOrEmpty(endTime)) {
            return startTime.toLowerCase() + " - " + endTime.toLowerCase();
        }
        else {
            return "";
        }
    }

    public Booking getBooking() {
        return mBooking;
    }
}
