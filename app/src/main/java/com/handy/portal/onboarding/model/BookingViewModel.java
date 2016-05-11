package com.handy.portal.onboarding.model;

import android.text.TextUtils;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.payments.model.PaymentInfo;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;

/**
 * <p/>
 * This is a view model to bridge the gap between the booking and what needs to be displayed on the
 * onboard claim job screen.
 * <p/>
 * <p/>
 */
public class BookingViewModel
{

    private Booking mBooking;
    public boolean mSelected;
    private String mDefaultSubTitle;

    public BookingViewModel(final Booking booking, String defaultSubtitle)
    {
        mBooking = booking;
        mDefaultSubTitle = defaultSubtitle;

        //we want to default the jobs to selected, and allow the user to unselect
        mSelected = true;
    }

    public boolean isSelected()
    {
        return mSelected;
    }

    public void setSelected(final boolean selected)
    {
        this.mSelected = selected;
    }

    public String getTitle()
    {
        if (mBooking.isProxy())
        {
            return mBooking.getLocationName();
        }
        else
        {
            return mBooking.getAddress().getShortRegion();
        }
    }

    public String getSubTitle()
    {
        String startTime = DateTimeUtils.formatDateTo12HourClock(mBooking.getStartDate());
        String endTime = DateTimeUtils.formatDateTo12HourClock(mBooking.getEndDate());

        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime))
        {
            return startTime.toLowerCase() + " - " + endTime.toLowerCase();
        }
        else
        {
            return mDefaultSubTitle;
        }
    }

    public Booking getBooking()
    {
        return mBooking;
    }

    public String getFormattedPrice()
    {
        PaymentInfo p = mBooking.getPaymentToProvider();

        if (p != null)
        {
            return CurrencyUtils.formatPrice(p.getAdjustedAmount(), p.getCurrencySymbol());
        }
        else
        {
            return "";
        }
    }

    public float getBookingAmount()
    {
        return mBooking.getPaymentToProvider().getAdjustedAmount();
    }

    public String getCurrencySymbol()
    {
        return mBooking.getPaymentToProvider().getCurrencySymbol();
    }
}
