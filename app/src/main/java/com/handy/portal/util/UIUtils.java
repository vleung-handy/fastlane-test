package com.handy.portal.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

/**
 * Created by cdavis on 5/21/15.
 */
public final class UIUtils
{
    public static void setPaymentInfo(TextView textView, Booking.PaymentInfo paymentInfo, String format)
    {
        if (paymentInfo != null && paymentInfo.getAdjustedAmount() > 0)
        {
            String paymentString = TextUtils.formatPrice(paymentInfo.getAdjustedAmount(), paymentInfo.getCurrencySymbol(), paymentInfo.getCurrencySuffix());
            textView.setText(String.format(format, paymentString));
        }
        else
        {
            textView.setVisibility(View.INVISIBLE);
        }
    }

    public static void setFrequencyInfo(Booking booking, TextView textView, Context parentContext)
    {
        //Frequency
        //Valid values : 1,2,4 every X weeks, 0 = non-recurring
        int frequency = booking.getFrequency();
        String bookingFrequencyFormat = getFrequencyFormatString(booking, parentContext);
        String bookingFrequency = String.format(bookingFrequencyFormat, frequency);
        textView.setText(bookingFrequency);
    }

    private static String getFrequencyFormatString(Booking booking, Context parentContext)
    {
        int frequency = booking.getFrequency();
        String bookingFrequencyFormat = null;
        //UK style regions show "One Time/Recurring" they do not specify the weeks interval
        if(booking.getAddress().isUKRegion())
        {
            if (frequency == 0)
            {
                bookingFrequencyFormat = parentContext.getString(R.string.booking_frequency_non_recurring);
            }
            else
            {
                bookingFrequencyFormat = parentContext.getString(R.string.booking_frequency_recurring_generic);
            }
        }
        else
        {
            if (frequency == 0)
            {
                bookingFrequencyFormat = parentContext.getString(R.string.booking_frequency_non_recurring);
            }
            else if (frequency == 1)
            {
                bookingFrequencyFormat = parentContext.getString(R.string.booking_frequency_every_week);
            }
            else
            {
                bookingFrequencyFormat = parentContext.getString(R.string.booking_frequency);
            }
        }
        return bookingFrequencyFormat;
    }
}