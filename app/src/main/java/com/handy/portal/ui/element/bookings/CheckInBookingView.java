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
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckInBookingView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.booking_check_in)
    Button mActionButton;

    private Booking mBooking;

    public CheckInBookingView(final Context context)
    {
        super(context);
        init();
    }

    public CheckInBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CheckInBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckInBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setBooking(Booking booking)
    {
        mBooking = booking;

        Booking.Action action = mBooking.getAction(Booking.Action.ACTION_CHECK_IN);
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

    @OnClick(R.id.booking_check_in)
    public void checkIn()
    {
        LocationData locationData = getLocationData();
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckIn(mBooking, locationData)));
        mBus.post(new HandyEvent.RequestNotifyJobCheckIn(mBooking.getId(), locationData));
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_check_in_booking, this);
        ButterKnife.bind(this);
        Utils.inject(getContext(), this);
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getContext());
    }
}
