package com.handy.portal.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
            if (filterExtrasByMachineName(booking, Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES).size() > 0)
            {
                bookingFrequencyFormat += " \u22C5 " + parentContext.getString(R.string.supplies);
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

    private static Collection<Booking.ExtraInfoWrapper> filterExtrasByMachineName(Booking booking, final String machineName)
    {
        ArrayList<Booking.ExtraInfoWrapper> extrasInfo = booking.getExtrasInfo();
        if (extrasInfo != null)
        {
            return Collections2.filter(extrasInfo, new Predicate<Booking.ExtraInfoWrapper>()
            {
                @Override
                public boolean apply(Booking.ExtraInfoWrapper input)
                {
                    return machineName.equals(input.getExtraInfo().getMachineName());
                }
            });

        }
        return Collections.emptyList();
    }

}
