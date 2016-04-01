package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.ui.view.InjectedBusView;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckOutBookingView extends InjectedBusView
{
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.customer_name_text)
    TextView mCustomerNameText;
    @Bind(R.id.job_start_time)
    TextView mJobStartTime;
    @Bind(R.id.call_customer_view)
    View mCallCustomerView;
    @Bind(R.id.message_customer_view)
    View mMessageCustomerView;
    @Bind(R.id.booking_action_button)
    Button mActionButton;
//    @Bind(R.id.booking_details_job_instructions_view)
//    BookingDetailsJobInstructionsView mJobInstructionsView;

    private Booking mBooking;
    private String mSource;
    private Bundle mSourceExtras;

    public CheckOutBookingView(final Context context, @NonNull Booking booking,
                               String source, Bundle sourceExtras)
    {
        super(context);
        init();
        setBooking(booking, source, sourceExtras);
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

    public void setBooking(@NonNull Booking booking, String source, Bundle sourceExtras)
    {
        mBooking = booking;
        mSource = source;
        mSourceExtras = sourceExtras;

        Booking.BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());

        // Booking actions
        List<Booking.Action> allowedActions = booking.getAllowedActions();
        for (Booking.Action action : allowedActions)
        {
            enableActionsIfNeeded(action);
        }

        Booking.User user = mBooking.getUser();
        if (user != null)
        {
            mCustomerNameText.setText(user.getFullName());
        }

        mJobStartTime.setText(DateTimeUtils.formatDateTo12HourClock(mBooking.getCheckInSummary().getCheckInTime()));
//        mJobInstructionsView.refreshDisplay(booking, false, bookingStatus);
    }

    @OnClick(R.id.booking_action_button)
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

    private String getLoggedInUserId()
    {
        return mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }

    @OnClick(R.id.call_customer_view)
    public void callCustomer()
    {
        mBus.post(new HandyEvent.CallCustomerClicked());

        String phoneNumber = mBooking.getBookingPhone();
        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)), getContext());
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Calling a Phone Number failed", activityException));
        }
    }

    @OnClick(R.id.message_customer_view)
    public void messageCustomer()
    {
        mBus.post(new HandyEvent.TextCustomerClicked());

        String phoneNumber = mBooking.getBookingPhone();
        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)), getContext());
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Texting a Phone Number failed", activityException));
        }
    }

    private void enableActionsIfNeeded(Booking.Action action)
    {
        if (UIUtils.getAssociatedActionType(action) == null)
        {
            Crashlytics.log("Received an unsupported action type : " + action.getActionName());
        }

        BookingActionButtonType buttonActionType = UIUtils.getAssociatedActionType(action);
        if (buttonActionType == null)
        {
            Crashlytics.log("Could not find action type for " + action.getActionName());
            return;
        }

        switch (buttonActionType)
        {
            case CHECK_OUT:
            {
                mActionButton.setEnabled(true);
                mActionButton.setAlpha(1.0f);
                break;
            }
            case CONTACT_PHONE:
            {
                mCallCustomerView.setVisibility(VISIBLE);
                break;
            }
            case CONTACT_TEXT:
            {
                mMessageCustomerView.setVisibility(VISIBLE);
                break;
            }
        }
    }
}
