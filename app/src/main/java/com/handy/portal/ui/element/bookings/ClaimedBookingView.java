package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.model.Address;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClaimedBookingView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.customer_name_text)
    TextView mCustomerNameText;
    @Bind(R.id.address_line_one_text)
    TextView mAddressLineOneText;
    @Bind(R.id.address_line_two_text)
    TextView mAddressLineTwoText;
    @Bind(R.id.job_date_text)
    TextView mJobDateText;
    @Bind(R.id.job_time_text)
    TextView mJobTimeText;
    @Bind(R.id.job_payment_text)
    TextView mJobPaymentText;
    @Bind(R.id.paid_extras_text)
    TextView mPaidExtrasText;
    @Bind(R.id.booking_action_button)
    Button mActionButton;

    private Booking mBooking;
    private boolean mCheckedIn;

    public ClaimedBookingView(final Context context)
    {
        super(context);
        init();
    }

    public ClaimedBookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ClaimedBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClaimedBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setCheckedIn(boolean checkedIn)
    {
        mCheckedIn = checkedIn;
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

        String firstName = mBooking.getUser().getFirstName();
        mCustomerNameText.setText(firstName);

        Address address = mBooking.getAddress();
        mAddressLineOneText.setText(address.getAddress1());
        mAddressLineTwoText.setText(address.getAddress2());

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

//        mPaidExtrasText.setText();
        if (mCheckedIn)
        {
            mActionButton.setText(getContext().getString(R.string.check_in));
        }
    }

    @OnClick(R.id.booking_action_button)
    public void bookingAction()
    {
        LocationData locationData = getLocationData();

        if (mCheckedIn)
        {
            mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckIn(mBooking, locationData)));
            mBus.post(new HandyEvent.RequestNotifyJobCheckIn(mBooking.getId(), locationData));
        }
        else
        {
            mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWay(mBooking, locationData)));
            mBus.post(new HandyEvent.RequestNotifyJobOnMyWay(mBooking.getId(), locationData));
        }
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_claimed_booking, this);
        ButterKnife.bind(this);
        Utils.inject(getContext(), this);

        mCheckedIn = false;
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getContext());
    }
}
