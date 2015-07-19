package com.handy.portal.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.model.Booking;

public final class UIUtils
{
    public static void setPaymentInfo(TextView textView, Booking.PaymentInfo paymentInfo, String format)
    {
        if (paymentInfo != null && paymentInfo.getAdjustedAmount() > 0)
        {
            String paymentString = TextUtils.formatPrice(paymentInfo.getAdjustedAmount(), paymentInfo.getCurrencySymbol(), paymentInfo.getCurrencySuffix());
            textView.setText(String.format(format, paymentString));
            textView.setVisibility(View.VISIBLE);
        }
        else
        {
            textView.setVisibility(View.INVISIBLE);
        }
    }

    public static String getFrequencyInfo(Booking booking, Context parentContext)
    {
        //Frequency
        //Valid values : 1,2,4 every X weeks, 0 = non-recurring
        int frequency = booking.getFrequency();
        String bookingFrequencyFormat = getFrequencyFormatString(booking, parentContext);
        return String.format(bookingFrequencyFormat, frequency);
    }

    public static void setFrequencyInfo(Booking booking, TextView textView, Context parentContext)
    {
        String bookingFrequency = getFrequencyInfo(booking, parentContext);
        textView.setText(bookingFrequency);
    }

    private static String getFrequencyFormatString(Booking booking, Context parentContext)
    {
        int frequency = booking.getFrequency();
        String bookingFrequencyFormat;

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
        return bookingFrequencyFormat;
    }

    //Map action button data to a booking action button type
    public static BookingActionButtonType getAssociatedActionType(Booking.Action data)
    {
        String actionName = data.getActionName();
        for (BookingActionButtonType bat : BookingActionButtonType.values())
        {
            if (actionName.equals(bat.getActionName()))
            {
                return bat;
            }
        }
        return null;
    }

}
