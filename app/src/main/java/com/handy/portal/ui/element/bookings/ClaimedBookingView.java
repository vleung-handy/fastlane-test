package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;
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

    @Bind(R.id.no_show_banner_text)
    View mNoShowBanner;
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
    @Bind(R.id.booking_support_button)
    Button mSupportButton;
    @Bind(R.id.booking_details_action_helper_text)
    TextView mBookingDetailsActionHelperText;
    @Bind(R.id.booking_details_job_instructions_list_layout)
    LinearLayout mInstructionsLayout;
    @Bind(R.id.job_number_text)
    TextView mJobNumberText;
    @Bind(R.id.booking_action_button)
    Button mActionButton;

    private Booking mBooking;
    private String mSource;
    private Bundle mSourceExtras;
    private Intent mGetDirectionsIntent;

    private static final String DATE_FORMAT = "E, MMM d";
    private static final String INTERPUNCT = "\u00B7";

    public ClaimedBookingView(
            final Context context, @NonNull Booking booking, String source, Bundle sourceExtras,
            OnClickListener onSupportClickListener, boolean noShowReported)
    {
        super(context);
        init();
        setDisplay(booking, source, sourceExtras, onSupportClickListener, noShowReported);
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

    public void setDisplay(
            @NonNull Booking booking, String source, Bundle sourceExtras,
            OnClickListener onSupportClickListener, boolean noShowReported)
    {
        mBooking = booking;
        mSource = source;
        mSourceExtras = sourceExtras;
        mSupportButton.setOnClickListener(onSupportClickListener);

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
            String paymentText = paymentInfo.getCurrencySymbol() +
                    TextUtils.DECIMAL_FORMAT_TWO_ZERO.format(paymentInfo.getAdjustedAmount());
            mJobPaymentText.setText(paymentText);
        }

        // Booking Instructions
        List<Booking.BookingInstructionGroup> bookingInstructionGroups = mBooking.getBookingInstructionGroups();
        if (bookingInstructionGroups != null && bookingInstructionGroups.size() > 0)
        {
            for (Booking.BookingInstructionGroup group : bookingInstructionGroups)
            {
                if (!Booking.BookingInstructionGroup.GROUP_PREFERENCES.equals(group.getGroup()))
                {
                    NewBookingDetailsJobInstructionsSectionView sectionView =
                            new NewBookingDetailsJobInstructionsSectionView(getContext());
                    sectionView.setDisplay(group.getLabel(), group.getInstructions());
                    mInstructionsLayout.addView(sectionView);
                }
            }
        }

        if (noShowReported)
        {
            mNoShowBanner.setVisibility(VISIBLE);
        }
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

    public void hideButtons(){
        mActionButton.setVisibility(GONE);
        mSupportButton.setVisibility(GONE);
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
                mActionButton.setText(R.string.on_my_way);
                mActionButton.setVisibility(action.isEnabled() ? VISIBLE : GONE);
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
                mActionButton.setText(R.string.check_in);
                mActionButton.setVisibility(action.isEnabled() ? VISIBLE : GONE);
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

