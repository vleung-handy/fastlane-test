package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.util.DateTimeUtils;

public class PostCheckoutRequestedBookingElementView extends AvailableBookingElementView
{
    @Override
    public View initView(final Context parentContext, final Booking booking, final View convertView,
                         final ViewGroup parent)
    {
        final View view = super.initView(parentContext, booking, convertView, parent);
        mBookingMessageTitleView.setVisibility(View.GONE);
        mBookingServiceTextView.setVisibility(View.GONE);
        mLeftStripIndicator.setVisibility(View.GONE);
        mBookingAreaTextView.setText(
                DateTimeUtils.formatDayOfWeekMonthDate(booking.getStartDate()));
        view.setBackground(null);
        return view;
    }
}
