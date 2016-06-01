package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Date;

public class PendingBookingElementView extends AvailableBookingElementView
{
    @Override
    public View initView(final Context parentContext, final Booking booking, final View convertView,
                         final ViewGroup parent)
    {
        final View view = super.initView(parentContext, booking, convertView, parent);
        final Date startDate = booking.getStartDate();
        mBookingServiceTextView.setText(DateTimeUtils.formatDayOfWeekMonthDateYear(startDate));
        return view;
    }
}
