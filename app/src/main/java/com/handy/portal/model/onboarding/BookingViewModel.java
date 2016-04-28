package com.handy.portal.model.onboarding;

import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
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
    public Booking booking;
    public boolean selected;

    public BookingViewModel(final Booking booking)
    {
        this.booking = booking;

        //we want to default the jobs to selected, and allow the user to unselect
        selected = true;
    }

    public String getTitle()
    {
        return booking.getLocationName();
    }

    public String getSubTitle()
    {
        String startTime = DateTimeUtils.formatDateTo12HourClock(booking.getStartDate());
        String endTime = DateTimeUtils.formatDateTo12HourClock(booking.getEndDate());
        return startTime.toLowerCase() + " - " + endTime.toLowerCase();
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
