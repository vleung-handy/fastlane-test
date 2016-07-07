package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.constant.BookingProgress;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.util.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

//Not setting up a clean UI hierarchy with AvailableBookingElement because ScheduledBookingElement is going to radically change soon to a google calendar style view


public class ScheduledBookingElementView extends BookingElementView
{
    @BindView(R.id.booking_entry_address_text)
    TextView mAddressTextView;
    @BindView(R.id.booking_entry_claimed_indicator)
    ImageView mClaimedIndicator;
    @BindView(R.id.booking_entry_status_text)
    TextView mStatusText;
    @BindView(R.id.booking_entry_completed_indicator)
    ImageView mCompletedIndicator;
    @BindView(R.id.booking_entry_service_text)
    TextView mBookingServiceTextView;
    @BindView(R.id.booking_entry_date_text)
    TextView mTimeText;

    public View initView(Context context, Booking booking, View convertView, ViewGroup parent)
    {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.element_scheduled_booking_list_entry, parent, false);
        }

        ButterKnife.bind(this, convertView);

        //Location
        mAddressTextView.setText(booking.getFormattedLocation(Booking.BookingStatus.CLAIMED));

        // Status
        setBookingStatusText(context, booking);

        //Date and Time
        final String formattedStartDate = TIME_OF_DAY_FORMAT.format(booking.getStartDate());
        final String formattedEndDate = TIME_OF_DAY_FORMAT.format(booking.getEndDate());
        mTimeText.setText(context.getString(R.string.booking_time,
                formattedStartDate.toLowerCase(), formattedEndDate.toLowerCase()));

        //Service or frequency for home cleaning jobs
        UIUtils.setService(mBookingServiceTextView, booking);

        mAssociatedView = convertView;

        return convertView;
    }

    private void setBookingStatusText(Context context, Booking booking)
    {
        mCompletedIndicator.setVisibility(View.GONE);
        mClaimedIndicator.setVisibility(View.GONE);
        switch (booking.getBookingProgress())
        {
            case BookingProgress.READY_FOR_ON_MY_WAY:
                mStatusText.setText(R.string.claimed);
                mStatusText.setTextColor(ContextCompat.getColor(context, R.color.handyman_teal));
                mClaimedIndicator.setVisibility(View.VISIBLE);
                break;
            case BookingProgress.READY_FOR_CHECK_IN:
            case BookingProgress.READY_FOR_CHECK_OUT:
                mStatusText.setText(R.string.in_progress);
                mStatusText.setTextColor(ContextCompat.getColor(context, R.color.handyman_teal));
                mClaimedIndicator.setVisibility(View.VISIBLE);
                break;
            case BookingProgress.FINISHED:
                mStatusText.setText(R.string.completed);
                mStatusText.setTextColor(ContextCompat.getColor(context, R.color.black_pressed));
                mCompletedIndicator.setVisibility(View.VISIBLE);
                break;
            default:
                mStatusText.setVisibility(View.GONE);
        }
    }

}
