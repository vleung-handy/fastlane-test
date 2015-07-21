package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;

import butterknife.InjectView;

public class BookingDetailsBannerViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.booking_details_banner_text)
    protected TextView bannerText;

    @InjectView(R.id.booking_details_banner_job_id_text)
    protected TextView jobIdText;

    public BookingDetailsBannerViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_banner;
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking booking)
    {
        Booking.BookingStatus bookingStatus = (Booking.BookingStatus) getArguments().getSerializable(BundleKeys.BOOKING_STATUS);
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
                jobIdText.setText(getContext().getString(R.string.job_num) + booking.getId());
            }
            break;

            case UNAVAILABLE:
            {
                bannerText.setText(R.string.unavailable_job);
            }
            break;
        }
        return true;
    }
}
