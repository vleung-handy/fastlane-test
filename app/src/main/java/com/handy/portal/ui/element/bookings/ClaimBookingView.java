package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClaimBookingView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.booking_claim)
    Button mActionButton;

    private Booking mBooking;

    public ClaimBookingView(final Context context)
    {
        super(context);
        init();
    }

    public ClaimBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ClaimBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClaimBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setBooking(Booking booking)
    {
        mBooking = booking;

        Booking.Action action = mBooking.getAction(Booking.Action.ACTION_CLAIM);
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

    @OnClick(R.id.booking_claim)
    public void claim()
    {
        requestClaimJob(mBooking);
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_claim_booking, this);
        ButterKnife.bind(this);
        Utils.inject(getContext(), this);
    }

    private void requestClaimJob(Booking booking)
    {
        // TODO: handle source and source extras
        mBus.post(new LogEvent.AddLogEvent(
                new AvailableJobsLog.ClaimSubmitted(booking, null, null, 0.0f)));
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new HandyEvent.RequestClaimJob(booking, null, null));
    }
}
