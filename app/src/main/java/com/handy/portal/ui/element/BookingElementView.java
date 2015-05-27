package com.handy.portal.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.util.TextUtils;

import java.text.SimpleDateFormat;

/**
 * Created by cdavis on 5/6/15.
 */
public class BookingElementView
{
    private static final String DATE_FORMAT = "h:mma";

    private BookingElementMediator mediator;
    public View associatedView;

    public BookingElementView(BookingElementMediator mediator)
    {
        this.mediator = mediator;
    }

    public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        if (booking == null)
        {
            System.err.println("Can not fill cell based on null booking");
            return null;
        }

        boolean isRequested = booking.getIsRequested();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parentContext).inflate(R.layout.element_booking_list_entry, parent, false);
        }

        //Payment
        TextView paymentText = (TextView) convertView.findViewById(R.id.booking_entry_payment_text);
        Booking.PaymentInfo paymentInfo = booking.getPaymentToProvider();
        setPaymentInfo(paymentText, paymentInfo);

        //Bonus Payment
        TextView bonusPaymentText = (TextView) convertView.findViewById(R.id.booking_entry_payment_bonus_text);
        Booking.PaymentInfo bonusPaymentInfo = booking.getBonusPaymentToProvider();
        setPaymentInfo(bonusPaymentText, bonusPaymentInfo);

        //Area
        TextView bookingAreaTextView = (TextView) convertView.findViewById(R.id.booking_entry_area_text);
        String bookingArea = booking.getAddress().getShortRegion();
        bookingAreaTextView.setText(bookingArea);

        //Frequency
        TextView frequencyTextView = (TextView) convertView.findViewById(R.id.booking_entry_frequency_text);
        setFrequencyInfo(booking, frequencyTextView, parentContext);

        //Requested Provider
        LinearLayout requestedIndicator = (LinearLayout) convertView.findViewById(R.id.booking_entry_requested_indicator_layout);
        requestedIndicator.setVisibility(isRequested ? View.VISIBLE : View.GONE);

        //Date and Time
        SimpleDateFormat timeOfDayFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedStartDate = timeOfDayFormat.format(booking.getStartDate());
        String formattedEndDate = timeOfDayFormat.format(booking.getEndDate());
        TextView startTimeText = (TextView) convertView.findViewById(R.id.booking_entry_start_date_text);
        TextView endTimeText = (TextView) convertView.findViewById(R.id.booking_entry_end_date_text);
        startTimeText.setText(formattedStartDate);
        endTimeText.setText(formattedEndDate);

        this.associatedView = convertView;

        return convertView;
    }

    private void setPaymentInfo(TextView textView, Booking.PaymentInfo paymentInfo)
    {
        if(paymentInfo != null && paymentInfo.getAdjustedAmount() > 0)
        {
            String paymentString = TextUtils.formatPrice(paymentInfo.getAdjustedAmount(), paymentInfo.getCurrencySymbol(), paymentInfo.getCurrencySuffix());
            textView.setText(paymentString);
        }
        else
        {
            textView.setVisibility(View.INVISIBLE);
        }
    }

    private void setFrequencyInfo(Booking booking, TextView textView, Context parentContext)
    {
        //Frequency
        //Valid values : 1,2,4 every X weeks, 0 = non-recurring
        int frequency = booking.getFrequency();
        String bookingFrequencyFormat;

        if(frequency == 0)
        {
            bookingFrequencyFormat = parentContext.getResources().getString(R.string.booking_frequency_non_recurring);
        }
        else if(frequency == 1)
        {
            bookingFrequencyFormat = parentContext.getResources().getString(R.string.booking_frequency_every_week);
        }
        else
        {
            bookingFrequencyFormat = parentContext.getResources().getString(R.string.booking_frequency);
        }

        String bookingFrequency = String.format(bookingFrequencyFormat, frequency);
        textView.setText(bookingFrequency);
    }

}