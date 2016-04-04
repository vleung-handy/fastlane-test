package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.common.collect.ImmutableSet;
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
    @Bind(R.id.get_directions_layout)
    ViewGroup mGetDirectionsLayout;
    @Bind(R.id.job_date_text)
    TextView mJobDateText;
    @Bind(R.id.job_time_text)
    TextView mJobTimeText;
    @Bind(R.id.job_payment_text)
    TextView mJobPaymentText;
    @Bind(R.id.paid_extras_text)
    TextView mPaidExtrasText;
    @Bind(R.id.booking_support_button)
    Button mSupportButton;
    @Bind(R.id.booking_details_action_helper_text)
    TextView mBookingDetailsActionHelperText;
    @Bind(R.id.job_number_text)
    TextView mJobNumberText;
    @Bind(R.id.booking_action_button)
    Button mActionButton;

    private Booking mBooking;
    private String mSource;
    private Bundle mSourceExtras;
    private CountDownTimer mCounter;
    private ActionBar mActionBar;
    private Intent mGetDirectionsIntent;

    private static final String DATE_FORMAT = "E, MMM d";
    private static final String INTERPUNCT = "\u00B7";

    private static final ImmutableSet<BookingActionButtonType> ASSOCIATED_BUTTON_ACTION_TYPES =
            ImmutableSet.of(
                    BookingActionButtonType.ON_MY_WAY,
                    BookingActionButtonType.CHECK_IN
            );


    public ClaimedBookingView(
            final Context context, @NonNull Booking booking, String source, Bundle sourceExtras,
            ActionBar actionBar, OnClickListener onSupportClickListener)
    {
        super(context);
        init();
        setDisplay(booking, source, sourceExtras, actionBar, onSupportClickListener);
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
    public ClaimedBookingView(final Context context, final AttributeSet attrs, final int defStyleAttr,
                              final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setDisplay(@NonNull Booking booking, String source, Bundle sourceExtras,
                           ActionBar actionBar, OnClickListener onSupportClickListener)
    {
        mBooking = booking;
        mSource = source;
        mSourceExtras = sourceExtras;
        mActionBar = actionBar;
        mSupportButton.setOnClickListener(onSupportClickListener);

        if (DateTimeUtils.isTimeWithinXHoursFromNow(booking.getStartDate(), 3))
        {
            setCountDownTimer(booking.getStartDate().getTime() - System.currentTimeMillis());
        }

        initMapLayout();

        // Booking actions
        List<Booking.Action> allowedActions = booking.getAllowedActions();
        for (Booking.Action action : allowedActions)
        {
            enableActionsIfNeeded(action);
        }

        if (mBooking.getUser() != null)
        {
            String firstName = mBooking.getUser().getFirstName();
            mCustomerNameText.setText(firstName);
        }

        Address address = mBooking.getAddress();
        if (address != null)
        {
            mAddressLineOneText.setText(address.getAddress1());
            mAddressLineTwoText.setText(address.getCityStateZip());

            initGetDirections(address);
        }

        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String formattedDate = dateFormat.format(startDate);
        String formattedTime = DateTimeUtils.formatDateTo12HourClock(startDate) + " - " + DateTimeUtils.formatDateTo12HourClock(endDate);

        mJobDateText.setText(getPrependByStartDate(startDate) + formattedDate);
        mJobTimeText.setText(formattedTime.toUpperCase());
        mJobNumberText.setText(getResources().getString(R.string.job_number_formatted, mBooking.getId()));

        PaymentInfo paymentInfo = mBooking.getPaymentToProvider();
        if (paymentInfo != null)
        {
            String paymentText = paymentInfo.getCurrencySymbol() + paymentInfo.getAdjustedAmount();
            mJobPaymentText.setText(paymentText);
        }

//        mPaidExtrasText.setText();
    }

    @Subscribe
    public void onReceiveZipClusterPolygonsSuccess(final BookingEvent.ReceiveZipClusterPolygonsSuccess event)
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

    @OnClick(R.id.get_directions_layout)
    public void getDirections()
    {
        if (mGetDirectionsIntent != null)
        {
            getContext().startActivity(mGetDirectionsIntent);
        }
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

    private void init()
    {
        inflate(getContext(), R.layout.view_claimed_booking, this);
        ButterKnife.bind(this);
        Utils.inject(getContext(), this);
    }

    private void initMapLayout()
    {
        //show either the real map or a placeholder image depending on if we have google play services
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
                FragmentTransaction transaction =
                        ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(mMapLayout.getId(), fragment).commit();
            }
        }
        else
        {
            UIUtils.replaceView(mMapLayout, new MapPlaceholderView(getContext()));
        }
    }

    private void initGetDirections(Address address)
    {
        // Create a Uri from an intent string. Use the result to create an Intent.
        String latitude = Float.toString(address.getLatitude());
        String longitude = Float.toString(address.getLongitude());

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address.getAddress1() + " " + address.getCityStateZip());
        Intent getDirectionsIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Use default maps app
        if (getDirectionsIntent.resolveActivity(getContext().getPackageManager()) != null)
        {
            mGetDirectionsIntent = getDirectionsIntent;
            mGetDirectionsLayout.setVisibility(VISIBLE);
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

    private void enableActionsIfNeeded(Booking.Action action)
    {
        BookingActionButtonType buttonActionType = UIUtils.getAssociatedActionType(action);
        if (buttonActionType == null)
        {
            Crashlytics.log("Could not find action type for " + action.getActionName());
            return;
        }

        switch (buttonActionType)
        {
            case ON_MY_WAY:
            {
                mActionButton.setVisibility(VISIBLE);
                mActionButton.setText(R.string.on_my_way);
                mActionButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                        mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWay(
                                mBooking, getLocationData())));
                        mBus.post(new HandyEvent.RequestNotifyJobOnMyWay(
                                mBooking.getId(), getLocationData()));
                    }
                });

                initHelperText(action);
                break;
            }
            case CHECK_IN:
            {
                mActionButton.setVisibility(VISIBLE);
                mActionButton.setText(R.string.check_in);
                mActionButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                        mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckIn(
                                mBooking, getLocationData())));
                        mBus.post(new HandyEvent.RequestNotifyJobCheckIn(
                                mBooking.getId(), getLocationData()));
                    }
                });

                initHelperText(action);
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

    private void initHelperText(Booking.Action action)
    {
        if (action.getHelperText() != null && !action.getHelperText().isEmpty())
        {
            mBookingDetailsActionHelperText.setVisibility(View.VISIBLE);
            mBookingDetailsActionHelperText.setText(action.getHelperText());
        }
    }
}

