package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckOutBookingView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.booking_check_out)
    Button mActionButton;

    private Booking mBooking;

    public CheckOutBookingView(final Context context)
    {
        super(context);
        init();
    }

    public CheckOutBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CheckOutBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckOutBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setBooking(Booking booking)
    {
        mBooking = booking;

        Booking.Action action = mBooking.getAction(Booking.Action.ACTION_CHECK_OUT);
        if (action == null)
        {
            mActionButton.setVisibility(GONE);
        }
        else
        {
            mActionButton.setVisibility(VISIBLE);
            mActionButton.setEnabled(action.isEnabled());
        }
    }

    @OnClick(R.id.booking_check_out)
    public void checkOut()
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.BOOKING, mBooking);
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.SEND_RECEIPT_CHECKOUT, bundle));
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_check_out_booking, this);
        ButterKnife.bind(this);
        Utils.inject(getContext(), this);
    }
}
