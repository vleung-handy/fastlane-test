package com.handy.portal.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.activity.BaseActivity;

import java.text.DecimalFormat;

public final class UIUtils
{
    public static ViewGroup getParent(View view)
    {
        return (ViewGroup)view.getParent();
    }

    public static void removeView(View view) {
        ViewGroup parent = getParent(view);
        if(parent != null)
        {
            parent.removeView(view);
        }
    }

    public static void replaceView(View currentView, View newView)
    {
        ViewGroup parent = getParent(currentView);
        if(parent == null)
        {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }

    public static void replaceViewWithFragment(Context context, ViewGroup view, Fragment fragment)
    {
        FragmentManager fragmentManager = ((BaseActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(view.getId(), fragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();

    }

    public static void setPaymentInfo(TextView dollarTextView, TextView centsTextView, Booking.PaymentInfo paymentInfo, String format)
    {
        if (paymentInfo != null && paymentInfo.getAmount() > 0)
        {
            int amount = paymentInfo.getAmount();
            double centsAmount = (amount % 100) * 0.01;
            int dollarAmount = amount / 100;

            if (centsTextView != null)
            {
                if (centsAmount > 0)
                {
                    centsTextView.setText(new DecimalFormat(".00").format(centsAmount));
                    centsTextView.setVisibility(View.VISIBLE);
                }
                else
                {
                    centsTextView.setVisibility(View.INVISIBLE);
                }
            }

            String paymentString = TextUtils.formatPrice(dollarAmount, paymentInfo.getCurrencySymbol(), paymentInfo.getCurrencySuffix());
            dollarTextView.setText(String.format(format, paymentString));
            dollarTextView.setVisibility(View.VISIBLE);
        }
        else
        {
            dollarTextView.setVisibility(View.INVISIBLE);
            if (centsTextView != null)
            {
                centsTextView.setVisibility(View.INVISIBLE);
            }
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
