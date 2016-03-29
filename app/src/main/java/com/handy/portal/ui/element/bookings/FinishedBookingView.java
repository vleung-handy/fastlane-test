package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.model.booking.Booking;

import butterknife.ButterKnife;

public class FinishedBookingView extends FrameLayout
{
    private Booking mBooking;

    public FinishedBookingView(final Context context)
    {
        super(context);
        init();
    }

    public FinishedBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FinishedBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FinishedBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setBooking(Booking booking)
    {
        mBooking = booking;
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_finished_booking, this);
        ButterKnife.bind(this);
    }
}
