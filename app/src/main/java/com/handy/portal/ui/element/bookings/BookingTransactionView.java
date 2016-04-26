package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.handy.portal.R;

import butterknife.ButterKnife;

public class BookingTransactionView extends FrameLayout
{
    public BookingTransactionView(final Context context)
    {
        super(context);
    }

    public BookingTransactionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingTransactionView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingTransactionView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_booking_tansaction, this);
        ButterKnife.bind(this);
    }
}
