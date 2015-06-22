package com.handy.portal.ui.element;

import android.os.Bundle;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsBannerView extends BookingDetailsView
{
    @InjectView(R.id.booking_details_banner_text)
    protected TextView bannerText;

    @InjectView(R.id.booking_details_banner_job_id_text)
    protected TextView jobIdText;

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_banner;
    }

    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        Booking.BookingStatus bookingStatus = (Booking.BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);
        setBannerTextByBookingStatus(bookingStatus);
        jobIdText.setText(activity.getString(R.string.job) + " " + booking.getId());
    }

    private void setBannerTextByBookingStatus(Booking.BookingStatus bookingStatus)
    {
        switch (bookingStatus)
        {
            case AVAILABLE:
            {
                bannerText.setText(R.string.available_job);
            }
            break;

            case CLAIMED:
            {
                bannerText.setText(R.string.your_job);
            }
            break;

            case UNAVAILABLE:
            {
                bannerText.setText(R.string.unavailable_job);
            }
            break;
        }
    }
}
