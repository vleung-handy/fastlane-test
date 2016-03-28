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

public class OnMyWayBookingView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.booking_on_my_way)
    Button mActionButton;

    Booking mBooking;

    public OnMyWayBookingView(final Context context)
    {
        super(context);
        init();
    }

    public OnMyWayBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public OnMyWayBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OnMyWayBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setBooking(Booking booking)
    {
        mBooking = booking;

        Booking.Action action = mBooking.getAction(Booking.Action.ACTION_ON_MY_WAY);
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

    @OnClick(R.id.booking_on_my_way)
    public void onMyWay()
    {
        requestNotifyOnMyWayJob(mBooking.getId(), getLocationData());
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_on_my_way_booking, this);
        ButterKnife.bind(this);
        Utils.inject(getContext(), this);
    }

    private void requestNotifyOnMyWayJob(String bookingId, LocationData locationData)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWay(mBooking, locationData)));
        mBus.post(new HandyEvent.RequestNotifyJobOnMyWay(bookingId, locationData));
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getContext());
    }
}
