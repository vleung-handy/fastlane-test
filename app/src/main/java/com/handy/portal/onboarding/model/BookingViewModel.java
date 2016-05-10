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

    private static final String NO_TIME_AVAILABLE = "No Time Available";
    public final Booking booking;
    public boolean selected;

    public BookingViewModel(final Booking booking)
    {
        this.booking = booking;

        //we want to default the jobs to selected, and allow the user to unselect
        selected = true;
    }

    public String getTitle()
    {
        if (booking.isProxy())
        {
            return booking.getLocationName();
        }
        else
        {
            return booking.getAddress().getShortRegion();
        }
    }

    public String getSubTitle()
    {
        String startTime = DateTimeUtils.formatDateTo12HourClock(booking.getStartDate());
        String endTime = DateTimeUtils.formatDateTo12HourClock(booking.getEndDate());

        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime))
        {
            return startTime.toLowerCase() + " - " + endTime.toLowerCase();
        }
        else
        {
            return NO_TIME_AVAILABLE;
        }
    }

    public String getFormattedPrice()
    {
        PaymentInfo p = booking.getPaymentToProvider();

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
        return booking.getPaymentToProvider().getAdjustedAmount();
    }

    public String getCurrencySymbol()
    {
        return booking.getPaymentToProvider().getCurrencySymbol();
    }
}
