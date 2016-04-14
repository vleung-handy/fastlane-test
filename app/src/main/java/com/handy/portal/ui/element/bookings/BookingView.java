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
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.BookingEvent;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.Booking;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.fragment.bookings.BookingMapFragment;
import com.handy.portal.ui.view.InjectedBusView;
import com.handy.portal.ui.view.MapPlaceholderView;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingView extends InjectedBusView
{
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.booking_no_show_banner_text)
    View mNoShowBanner;
    @Bind(R.id.booking_map_layout)
    FrameLayout mMapLayout;
    @Bind(R.id.booking_customer_contact_layout)
    ViewGroup mBookingCustomerContactLayout;
    @Bind(R.id.booking_customer_name_text)
    TextView mCustomerNameText;
    @Bind(R.id.booking_address_text)
    TextView mBookingAddressText;
    @Bind(R.id.booking_call_customer_view)
    ImageView mCallCustomerView;
    @Bind(R.id.booking_message_customer_view)
    ImageView mMessageCustomerView;
    @Bind(R.id.booking_get_directions_layout)
    ViewGroup mGetDirectionsLayout;
    @Bind(R.id.booking_job_date_text)
    TextView mJobDateText;
    @Bind(R.id.booking_job_time_text)
    TextView mJobTimeText;
    @Bind(R.id.booking_job_payment_text)
    TextView mJobPaymentText;
    @Bind(R.id.booking_job_payment_bonus_text)
    TextView mJobPaymentBonusText;
    @Bind(R.id.booking_recurrence_text)
    TextView mRecurrenceText;
    @Bind(R.id.booking_support_button)
    Button mSupportButton;
    @Bind(R.id.booking_action_helper_text)
    TextView mBookingDetailsActionHelperText;
    @Bind(R.id.booking_job_instructions_list_layout)
    LinearLayout mInstructionsLayout;
    @Bind(R.id.booking_proxy_location_layout)
    ViewGroup mBookingProxyLocationlayout;
    @Bind(R.id.booking_details_location_view)
    ProxyLocationView mProxyLocationView;
    @Bind(R.id.booking_job_number_text)
    TextView mJobNumberText;
    @Bind(R.id.booking_action_button)
    Button mActionButton;

    private static final String BOOKING_PROXY_ID_PREFIX = "P";

    private Booking mBooking;
    private String mSource;
    private Bundle mSourceExtras;
    private Intent mGetDirectionsIntent;

    public BookingView(
            final Context context, @NonNull Booking booking, String source, Bundle sourceExtras,
            OnClickListener onSupportClickListener, boolean noShowReported, boolean fromPaymentsTab)
    {
        super(context);
        init();
        setDisplay(booking, source, sourceExtras, onSupportClickListener, noShowReported, fromPaymentsTab);
    }

    public BookingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BookingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingView(final Context context, final AttributeSet attrs, final int defStyleAttr,
                       final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setDisplay(
            @NonNull Booking booking, String source, Bundle sourceExtras,
            OnClickListener onSupportClickListener, boolean noShowReported, boolean fromPaymentsTab)
    {
        mBooking = booking;
        mSource = source;
        mSourceExtras = sourceExtras;
        mSupportButton.setOnClickListener(onSupportClickListener);

        if (!fromPaymentsTab)
        { initMapLayout(); }

        mCallCustomerView.setEnabled(false);
        mCallCustomerView.setAlpha(0.5f);
        mMessageCustomerView.setEnabled(false);
        mMessageCustomerView.setAlpha(0.5f);

        // Booking actions
        List<Booking.Action> allowedActions = booking.getAllowedActions();
        for (Booking.Action action : allowedActions)
        {
            enableActionsIfNeeded(action);
        }

        if (mBooking.getUser() != null)
        {
            mCustomerNameText.setText(mBooking.getUser().getFullName());
        }
        else
        {
            mBookingCustomerContactLayout.setVisibility(GONE);
        }

        mBookingAddressText.setText(mBooking.getLocationName());
        Address address = mBooking.getAddress();
        if (address != null)
        {
            if (fromPaymentsTab || mBooking.isProxy())
            {
                mBookingAddressText.setText(address.getShortRegion());
            }
            else
            {
                mBookingAddressText.setText(getResources().getString(R.string.two_lines_formatted,
                        address.getAddress1(), address.getCityStateZip()));
                initGetDirections(address);
            }
        }

        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();
        String formattedDate = DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER.format(startDate);
        String formattedTime = DateTimeUtils.formatDateTo12HourClock(startDate) + " "
                + getResources().getString(R.string.dash) + " "
                + DateTimeUtils.formatDateTo12HourClock(endDate);

        mJobDateText.setText(getTodayTomorrowStringByStartDate(startDate) + formattedDate);
        mJobTimeText.setText(formattedTime.toUpperCase());

        if (booking.isProxy() && booking.getZipCluster() != null &&
                ((booking.getZipCluster().getTransitDescription() != null
                        && !booking.getZipCluster().getTransitDescription().isEmpty()) ||
                        (booking.getZipCluster().getLocationDescription() != null
                                && !booking.getZipCluster().getLocationDescription().isEmpty())))
        {
            mBookingProxyLocationlayout.setVisibility(VISIBLE);
            mProxyLocationView.refreshDisplay(mBooking);
        }

        String bookingIdPrefix = mBooking.isProxy() ? BOOKING_PROXY_ID_PREFIX : "";
        mJobNumberText.setText(getResources().getString(R.string.job_number_formatted, bookingIdPrefix + mBooking.getId()));

        PaymentInfo paymentInfo = mBooking.getPaymentToProvider();
        if (paymentInfo != null)
        {
            String paymentText = CurrencyUtils.formatPriceWithCents(paymentInfo.getAmount(),
                    paymentInfo.getCurrencySymbol());
            mJobPaymentText.setText(paymentText);
        }

        PaymentInfo bonusInfo = mBooking.getBonusPaymentToProvider();
        if (bonusInfo != null && bonusInfo.getAdjustedAmount() > 0)
        {
            String bonusText = getResources().getString(R.string.bonus_payment_value,
                    CurrencyUtils.formatPriceWithCents(bonusInfo.getAmount(), bonusInfo.getCurrencySymbol()));
            mJobPaymentBonusText.setText(bonusText);
        }

        mRecurrenceText.setText(UIUtils.getFrequencyInfo(mBooking, getContext()));

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

        // Hide map and customer contact if coming from payments tab
        if (fromPaymentsTab)
        {
            mMapLayout.setVisibility(GONE);
            mBookingCustomerContactLayout.setVisibility(GONE);
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

    @OnClick(R.id.booking_get_directions_layout)
    public void getDirections()
    {
        if (mGetDirectionsIntent != null)
        {
            Utils.safeLaunchIntent(mGetDirectionsIntent, getContext());
        }
    }

    @OnClick(R.id.booking_call_customer_view)
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

    @OnClick(R.id.booking_message_customer_view)
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

    public void hideButtons()
    {
        mActionButton.setVisibility(GONE);
        mSupportButton.setVisibility(GONE);
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_booking, this);
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
    private String getTodayTomorrowStringByStartDate(Date bookingStartDate)
    {
        String prepend = "";

        Calendar calendar = Calendar.getInstance();

        Date currentTime = calendar.getTime();

        if (DateTimeUtils.equalCalendarDates(currentTime, bookingStartDate))
        {
            prepend = (getContext().getString(R.string.today) + ", ");
        }

        calendar.add(Calendar.DATE, 1);
        Date tomorrowTime = calendar.getTime();
        if (DateTimeUtils.equalCalendarDates(tomorrowTime, bookingStartDate))
        {
            prepend = (getContext().getString(R.string.tomorrow) + ", ");
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
            case CLAIM:
            {
                mActionButton.setText(R.string.claim);
                mActionButton.setVisibility(action.isEnabled() ? VISIBLE : GONE);
                mActionButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                        mBus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ClaimSubmitted(
                                mBooking, mSource, mSourceExtras, 0.0f)));
                        mBus.post(new HandyEvent.RequestClaimJob(mBooking, mSource, mSourceExtras));
                    }
                });
                break;
            }
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
                        mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWaySubmitted(
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
                if (mMapLayout.getVisibility() == VISIBLE)
                {
                    mMapLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) getResources().getDimension(
                                    R.dimen.check_in_booking_details_map_height)));
                }

                mActionButton.setText(R.string.check_in);
                mActionButton.setVisibility(action.isEnabled() ? VISIBLE : GONE);
                mActionButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                        mBus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckInSubmitted(
                                mBooking, getLocationData())));
                        mBus.post(new HandyEvent.RequestNotifyJobCheckIn(
                                mBooking.getId(), getLocationData()));
                    }
                });

                initHelperText(action);
                break;
            }
            case CHECK_OUT:
            {
                mActionButton.setText(R.string.check_out);
                mActionButton.setVisibility(action.isEnabled() ? VISIBLE : GONE);
                mActionButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BundleKeys.BOOKING, mBooking);
                        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.SEND_RECEIPT_CHECKOUT, bundle, true));
                    }
                });
                initHelperText(action);
                break;
            }
            case CONTACT_PHONE:
            {
                if (action.isEnabled())
                {
                    mCallCustomerView.setEnabled(true);
                    mCallCustomerView.setAlpha(1.0f);
                }
                break;
            }
            case CONTACT_TEXT:
            {
                if (action.isEnabled())
                {
                    mMessageCustomerView.setEnabled(true);
                    mMessageCustomerView.setAlpha(1.0f);
                }
                break;
            }
        }
    }

    private void initHelperText(Booking.Action action)
    {
        if (!TextUtils.isNullOrEmpty(action.getHelperText()))
        {
            mBookingDetailsActionHelperText.setVisibility(View.VISIBLE);
            mBookingDetailsActionHelperText.setText(action.getHelperText());
        }
    }
}

