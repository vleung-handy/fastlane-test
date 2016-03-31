package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.BookingEvent;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.model.booking.Booking;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.fragment.booking.BookingMapFragment;
import com.handy.portal.ui.view.InjectedBusView;
import com.handy.portal.ui.view.MapPlaceholderView;
import com.handy.portal.ui.widget.BookingActionButton;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClaimedBookingView extends InjectedBusView
{
    // TODO: this file still needs to be cleaned up.
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.map_layout)
    FrameLayout mMapLayout;
    @Bind(R.id.customer_name_text)
    TextView mCustomerNameText;
    @Bind(R.id.address_line_one_text)
    TextView mAddressLineOneText;
    @Bind(R.id.address_line_two_text)
    TextView mAddressLineTwoText;
    @Bind(R.id.call_customer_view)
    ImageView mCallCustomerView;
    @Bind(R.id.message_customer_view)
    ImageView mMessageCustomerView;
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
    private String mSource;
    private Bundle mSourceExtras;
    private boolean mCheckedIn;
    private CountDownTimer mCounter;
    private ActionBar mActionBar;

    private static final String DATE_FORMAT = "E, MMM d";
    private static final String INTERPUNCT = "\u00B7";

    public ClaimedBookingView(final Context context, @NonNull Booking booking, String source,
                              Bundle sourceExtras, ActionBar actionBar)
    {
        super(context);
        init();
        setBooking(booking, source, sourceExtras, actionBar);
    }

    public ClaimedBookingView(final Context context, final AttributeSet attrs,
                              @NonNull Booking booking, String source,
                              Bundle sourceExtras, ActionBar actionBar)
    {
        super(context, attrs);
        init();
        setBooking(booking, source, sourceExtras, actionBar);
    }

    public ClaimedBookingView(final Context context, final AttributeSet attrs,
                              final int defStyleAttr, @NonNull Booking booking, String source,
                              Bundle sourceExtras, ActionBar actionBar)
    {
        super(context, attrs, defStyleAttr);
        init();
        setBooking(booking, source, sourceExtras, actionBar);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClaimedBookingView(final Context context, final AttributeSet attrs,
                              final int defStyleAttr, final int defStyleRes,
                              @NonNull Booking booking, String source,
                              Bundle sourceExtras, ActionBar actionBar)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        setBooking(booking, source, sourceExtras, actionBar);
    }

    public void setCheckedIn(boolean checkedIn)
    {
        mCheckedIn = checkedIn;
    }

    public void setBooking(@NonNull Booking booking, String source, Bundle sourceExtras,
                           ActionBar actionBar)
    {
        mBooking = booking;
        mSource = source;
        mSourceExtras = sourceExtras;
        mActionBar = actionBar;

        if (DateTimeUtils.isDateWithinXHoursFromNow(booking.getStartDate(), 3))
        {
            setCountDownTimer(booking.getStartDate().getTime() - System.currentTimeMillis());
        }

        initMapLayout();

//        Booking.Action action = mBooking.getAction(Booking.Action.ACTION_ON_MY_WAY);
//        if (action == null)
//        {
//            mActionButton.setVisibility(GONE);
//        }
//        else
//        {
//            mActionButton.setVisibility(VISIBLE);
//            mActionButton.setEnabled(action.isEnabled());
//        }

        if (mBooking.getUser() != null)
        {
            String firstName = mBooking.getUser().getFirstName();
            mCustomerNameText.setText(firstName);
        }

        Address address = mBooking.getAddress();
        if (address != null)
        {
            mAddressLineOneText.setText(address.getAddress1());
            mAddressLineTwoText.setText(address.getAddress2());
        }

        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedDate = dateFormat.format(startDate);
        String formattedTime = DateTimeUtils.formatDateTo12HourClock(startDate) + " - " + DateTimeUtils.formatDateTo12HourClock(endDate);

        mJobDateText.setText(getPrependByStartDate(startDate) + formattedDate);
        mJobTimeText.setText(formattedTime.toUpperCase());

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

//        createAllowedActionButtons();
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

    private void initMapLayout()
    {//show either the real map or a placeholder image depending on if we have google play services
        Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(getLoggedInUserId());
        if (ConnectionResult.SUCCESS ==
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()))
        {
            final String zipClusterId = mBooking.getZipClusterId();
            if (zipClusterId != null)
            {
                requestZipClusterPolygons(zipClusterId);
            }
            else
            {
                BookingMapFragment fragment = BookingMapFragment.newInstance(
                        mBooking,
                        mSource,
                        bookingStatus
                );
                FragmentTransaction transaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(mMapLayout.getId(), fragment).commit();
            }
        }
        else
        {
            UIUtils.replaceView(mMapLayout, new MapPlaceholderView(getContext()));
        }
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getContext());
    }

    private void requestZipClusterPolygons(final String zipClusterId)
    {
        mBus.post(new BookingEvent.RequestZipClusterPolygons(zipClusterId));
    }

    @Subscribe
    public void onReceiveZipClusterPolygonsSuccess(
            final BookingEvent.ReceiveZipClusterPolygonsSuccess event
    )
    {
        // There's a null check here due to a race condition with BookingsFragment.
        // BookingsFragment requests for zip clusters and the response may come back here. If the
        // result comes back before this fragment and this fragment hasn't loaded a booking, then
        // mAssociatedBooking will be null.
        if (mBooking != null)
        {
            Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(getLoggedInUserId());
            BookingMapFragment fragment = BookingMapFragment.newInstance(
                    mBooking,
                    mSource,
                    bookingStatus,
                    event.zipClusterPolygons
            );
            FragmentTransaction transaction = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
            transaction.replace(mMapLayout.getId(), fragment).commit();
        }
    }

    private String getLoggedInUserId()
    {
        return mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }

    //returns a today or tomorrow prepend as needed
    private String getPrependByStartDate(Date bookingStartDate)
    {
        String prepend = "";

        Calendar calendar = Calendar.getInstance();

        Date currentTime = calendar.getTime();

        if (DateTimeUtils.equalCalendarDates(currentTime, bookingStartDate))
        {
            prepend = (getContext().getString(R.string.today) + " " + INTERPUNCT + " ");
        }

        calendar.add(Calendar.DATE, 1);
        Date tomorrowTime = calendar.getTime();
        if (DateTimeUtils.equalCalendarDates(tomorrowTime, bookingStartDate))
        {
            prepend = (getContext().getString(R.string.tomorrow) + " " + INTERPUNCT + " ");
        }

        return prepend;
    }


    private void setCountDownTimer(long timeRemainMillis)
    {
        if (mCounter != null) { mCounter.cancel(); } // cancel the previous counter

        mCounter = DateTimeUtils.setCountDownTimer(getContext(), mActionBar, timeRemainMillis);
    }

    //Dynamically generated Action Buttons based on the allowedActions sent by the server in our booking data
    private void createAllowedActionButtons(Booking booking)
    {
        List<Booking.Action> allowedActions = booking.getAllowedActions();
        for (Booking.Action action : allowedActions)
        {
            if (UIUtils.getAssociatedActionType(action) == null)
            {
                Crashlytics.log("Received an unsupported action type : " + action.getActionName());
                continue;
            }

            //the client knows what layout to insert a given button into, this should never come from the server
            BookingActionButtonType type = UIUtils.getAssociatedActionType(action);
            ViewGroup buttonParentLayout = getParentLayoutForButtonActionType(type);

            if (buttonParentLayout == null)
            {
                Crashlytics.log("Could not find parent layout for " + action.getActionName());
            }
            else if (type == null)
            {
                Crashlytics.log("Could not find action type for " + action.getActionName());
            }
            else
            {
                int newChildIndex = buttonParentLayout.getChildCount(); //new index is equal to the old count since the new count is +1

                ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(getContext())
                        .inflate(type.getLayoutTemplateId(), buttonParentLayout);
                BookingActionButton bookingActionButton =
                        (BookingActionButton) viewGroup.getChildAt(newChildIndex);
//                bookingActionButton.init(booking, this, action);
            }
        }
    }

    //Mapping for ButtonActionType to Parent Layout, used when adding Action Buttons dynamically
    private ViewGroup getParentLayoutForButtonActionType(BookingActionButtonType buttonActionType)
    {
        switch (buttonActionType)
        {
            case ON_MY_WAY:
                mActionButton.setVisibility(VISIBLE);
                break;
            case CHECK_IN:
                mActionButton.setVisibility(VISIBLE);
                break;
            case CONTACT_PHONE:
                mCallCustomerView.setVisibility(VISIBLE);
                break;
            case CONTACT_TEXT:
                mMessageCustomerView.setVisibility(VISIBLE);
                break;
        }
        return null;
    }

}

