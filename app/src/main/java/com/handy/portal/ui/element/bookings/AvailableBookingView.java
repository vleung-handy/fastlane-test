package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AvailableBookingView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.map_view)
    MapView mMapView;
    @Bind(R.id.job_location_text)
    TextView mJobLocationText;
    @Bind(R.id.job_date_text)
    TextView mJobDateText;
    @Bind(R.id.job_time_text)
    TextView mJobTimeText;
    @Bind(R.id.job_payment_text)
    TextView mJobPaymentText;
    @Bind(R.id.booking_claim)
    Button mActionButton;

    private Booking mBooking;

    public AvailableBookingView(final Context context)
    {
        super(context);
        init();
    }

    public AvailableBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public AvailableBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AvailableBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
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

        mJobLocationText.setText(mBooking.getAddress().getShortRegion());
        mJobDateText.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(mBooking.getStartDate()));

        String startTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getStartDate());
        String endTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getEndDate());
        mJobTimeText.setText(getContext().getString(R.string.time_interval_formatted, startTime, endTime));

        PaymentInfo paymentInfo = mBooking.getPaymentToProvider();
        if (paymentInfo != null)
        {
            String paymentText = paymentInfo.getCurrencySymbol() + paymentInfo.getAdjustedAmount();
            mJobPaymentText.setText(paymentText);
        }
    }

    @OnClick(R.id.booking_claim)
    public void claim()
    {
        requestClaimJob(mBooking);
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_available_booking, this);
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
