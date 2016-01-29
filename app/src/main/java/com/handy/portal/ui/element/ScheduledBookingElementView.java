package com.handy.portal.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

//Not setting up a clean UI hierarchy with AvailableBookingElement because ScheduledBookingElement is going to radically change soon to a google calendar style view


public class ScheduledBookingElementView extends BookingElementView
{
    @Bind(R.id.booking_entry_address_text)
    protected TextView mAddressTextView;

    @Bind(R.id.booking_entry_claimed_indicator_layout)
    protected LinearLayout mClaimedIndicatorLayout;

    @Bind(R.id.booking_entry_completed_text)
    protected TextView mCompletedText;

    @Bind(R.id.booking_entry_completed_indicator)
    protected ImageView mCompletedIndicator;

    @Bind(R.id.booking_entry_service_text)
    protected TextView mBookingServiceTextView;

    @Bind(R.id.booking_entry_time_window_text)
    protected TextView mTimeWindowText;

    @Bind(R.id.booking_entry_start_date_text)
    protected TextView mStartTimeText;

    @Bind(R.id.booking_entry_end_date_text)
    protected TextView mEndTimeText;

    public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parentContext).inflate(R.layout.element_scheduled_booking_list_entry, parent, false);
        }

        ButterKnife.bind(this, convertView);

        //Location
        mAddressTextView.setText(booking.getFormattedLocation(Booking.BookingStatus.CLAIMED));

        //Claimed
        mClaimedIndicatorLayout.setVisibility(booking.isEnded() ? View.GONE : View.VISIBLE);

        //Completed
        mCompletedText.setVisibility(booking.isEnded() ? View.VISIBLE : View.GONE);
        mCompletedIndicator.setVisibility(booking.isEnded() ? View.VISIBLE : View.INVISIBLE);

        //Date and Time
        final String formattedStartDate = TIME_OF_DAY_FORMAT.format(booking.getStartDate());
        final String formattedEndDate = TIME_OF_DAY_FORMAT.format(booking.getEndDate());
        mStartTimeText.setText(formattedStartDate.toLowerCase());
        mEndTimeText.setText(formattedEndDate.toLowerCase());

        //Service or frequency for home cleaning jobs
        UIUtils.setService(mBookingServiceTextView, booking);

        //Time window
        UIUtils.setTimeWindow(mTimeWindowText, booking.getMinimumHours(), booking.getHours());

        this.associatedView = convertView;

        return convertView;
    }

}
