package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

//Not setting up a clean UI hierarchy with AvailableBookingElement because ScheduledBookingElement is going to radically change soon to a google calendar style view


public class ScheduledBookingElementView extends BookingElementView
{
    @Bind(R.id.booking_entry_address_text)
    TextView mAddressTextView;

    @Bind(R.id.booking_entry_claimed_indicator_layout)
    LinearLayout mClaimedIndicatorLayout;

    @Bind(R.id.booking_entry_completed_text)
    TextView mCompletedText;

    @Bind(R.id.booking_entry_completed_indicator)
    ImageView mCompletedIndicator;

    @Bind(R.id.booking_entry_service_text)
    TextView mBookingServiceTextView;

    @Bind(R.id.booking_entry_date_text)
    TextView mTimeText;

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
        mTimeText.setText(parentContext.getString(R.string.booking_time,
                formattedStartDate.toLowerCase(), formattedEndDate.toLowerCase()));

        //Service or frequency for home cleaning jobs
        UIUtils.setService(mBookingServiceTextView, booking);

        mAssociatedView = convertView;

        return convertView;
    }

}
