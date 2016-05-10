package com.handy.portal.bookings.ui.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.MapsInitializer;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.constant.BookingActionButtonType;
import com.handy.portal.bookings.constant.BookingProgress;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.BookingDetailsJobInstructionsSectionView;
import com.handy.portal.bookings.ui.element.BookingMapView;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingActionDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingClaimDialogFragment;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.LocationData;
import com.handy.portal.payments.model.PaymentInfo;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.fragment.TimerActionBarFragment;
import com.handy.portal.ui.view.MapPlaceholderView;
import com.handy.portal.ui.view.RoundedTextView;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.FragmentUtils;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * fragment for handling bookings that are
 * not in progress i.e. ready for claim, check-in, on my way, etc
 */
public class BookingFragment extends TimerActionBarFragment
{
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.booking_scroll_view)
    ScrollView mScrollView;
    @Bind(R.id.booking_no_show_banner_text)
    View mNoShowBanner;
    @Bind(R.id.booking_map_view)
    BookingMapView mBookingMapView;
    @Bind(R.id.booking_customer_contact_layout)
    ViewGroup mBookingCustomerContactLayout;
    @Bind(R.id.booking_customer_name_text)
    TextView mCustomerNameText;
    @Bind(R.id.booking_address_title_text)
    TextView mBookingAddressTitleText;
    @Bind(R.id.booking_address_text)
    TextView mBookingAddressText;
    @Bind(R.id.booking_address_location_description_text)
    TextView mBookingAddressLocationDescriptionText;
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
    @Bind(R.id.booking_frequency_text)
    TextView mFrequencyTest;
    @Bind(R.id.booking_support_button)
    Button mSupportButton;
    @Bind(R.id.booking_action_helper_text)
    TextView mBookingDetailsActionHelperText;
    @Bind(R.id.booking_job_instructions_list_layout)
    LinearLayout mInstructionsLayout;
    @Bind(R.id.booking_reveal_notice_text)
    TextView mRevealNoticeText;
    @Bind(R.id.booking_nearby_transit_layout)
    ViewGroup mBookingNearbyTransitLayout;
    @Bind(R.id.nearby_transits)
    LinearLayout mNearbyTransits;
    @Bind(R.id.booking_job_number_text)
    TextView mJobNumberText;
    @Bind(R.id.booking_action_button)
    Button mActionButton;

    private static final String BOOKING_PROXY_ID_PREFIX = "P";

    private Booking mBooking;
    private String mSource;
    private Bundle mSourceExtras;
    private Intent mGetDirectionsIntent;
    private View.OnClickListener mOnSupportClickListener;
    private boolean mFromPaymentsTab;
    private boolean mHideActionButtons;

    public static BookingFragment newInstance(@NonNull final Booking booking, final String source,
                                              boolean fromPaymentsTab, boolean hideActionButtons)
    {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        args.putString(BundleKeys.BOOKING_SOURCE, source);
        args.putBoolean(BundleKeys.BOOKING_FROM_PAYMENT_TAB, fromPaymentsTab);
        args.putBoolean(BundleKeys.BOOKING_SHOULD_HIDE_ACTION_BUTTONS, hideActionButtons);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(final Context context)
    {
        super.onAttach(context);
        mOnSupportClickListener = (View.OnClickListener) getParentFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
        mSource = getArguments().getString(BundleKeys.BOOKING_SOURCE);
        mSourceExtras = getArguments();

        mFromPaymentsTab = getArguments().getBoolean(BundleKeys.BOOKING_FROM_PAYMENT_TAB);
        mHideActionButtons = getArguments().getBoolean(BundleKeys.BOOKING_SHOULD_HIDE_ACTION_BUTTONS);

        MapsInitializer.initialize(getContext());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mBookingMapView.onCreate(savedInstanceState);
        mBookingMapView.disableParentScrolling(mScrollView);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mBookingMapView.onResume();

        setDisplay();
    }

    @Override
    public void onPause()
    {
        mBookingMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        try
        {
            mBookingMapView.onDestroy();
        }
        catch (NullPointerException e)
        {
            Log.e(getClass().getSimpleName(),
                    "Error while attempting MapView.onDestroy(), ignoring exception", e);
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mBookingMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mBookingMapView.onSaveInstanceState(outState);
    }

    public void setDisplay()
    {
        setActionButtonVisibility();
        mSupportButton.setOnClickListener(mOnSupportClickListener);

        if (!mFromPaymentsTab)
        { initMapLayout(); }

        mCallCustomerView.setEnabled(false);
        mCallCustomerView.setAlpha(0.5f);
        mMessageCustomerView.setEnabled(false);
        mMessageCustomerView.setAlpha(0.5f);

        // Booking actions
        List<Booking.Action> allowedActions = mBooking.getAllowedActions();
        for (Booking.Action action : allowedActions)
        {
            enableActionsIfNeeded(action);
        }

        int bookingProgress = mBooking.getBookingProgress();
        Booking.BookingStatus bookingStatus =
                mBooking.inferBookingStatus(mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID));
        if (bookingStatus == Booking.BookingStatus.UNAVAILABLE ||
                bookingProgress == BookingProgress.READY_FOR_CLAIM ||
                mBooking.getUser() == null || mFromPaymentsTab)
        {
            mBookingCustomerContactLayout.setVisibility(View.GONE);
        }
        else
        {
            mCustomerNameText.setText(mBooking.getUser().getFullName());
        }

        mSupportButton.setVisibility(shouldShowSupportButton() ? View.VISIBLE : View.GONE);

        if (mBooking.isProxy())
        {
            mBookingAddressTitleText.setText(mBooking.getLocationName());
            mBookingAddressText.setVisibility(View.GONE);

            if (mBooking.getZipCluster() != null)
            {
                if (mBooking.getZipCluster().getTransitDescription() != null &&
                        !mBooking.getZipCluster().getTransitDescription().isEmpty())
                {
                    setNearbyTransit(mBooking.getZipCluster().getTransitDescription());
                }

                if (mBooking.getZipCluster().getLocationDescription() != null
                        && !mBooking.getZipCluster().getLocationDescription().isEmpty())
                {
                    mBookingAddressLocationDescriptionText.setText(
                            mBooking.getZipCluster().getLocationDescription());
                    mBookingAddressLocationDescriptionText.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            Address address = mBooking.getAddress();
            if (address != null)
            {
                if (bookingStatus != Booking.BookingStatus.CLAIMED || mFromPaymentsTab)
                {
                    mBookingAddressTitleText.setText(mBooking.isUK() ?
                            getResources().getString(R.string.comma_formatted,
                                    address.getShortRegion(), address.getZip()) :
                            address.getShortRegion());
                    mBookingAddressText.setVisibility(View.GONE);
                }
                else
                {
                    mBookingAddressText.setText(getResources().getString(R.string.two_lines_formatted,
                            address.getAddress1(), address.getCityStateZip()));
                    initGetDirections(address);
                }
            }
            else
            {
                mBookingAddressText.setText(mBooking.getLocationName());
            }
        }

        Date startDate = mBooking.getStartDate();
        Date endDate = mBooking.getEndDate();
        String formattedDate = DateTimeUtils.DAY_OF_WEEK_MONTH_DAY_FORMATTER.format(startDate);
        String formattedTime = getResources().getString(R.string.dash_formatted,
                DateTimeUtils.formatDateTo12HourClock(startDate), DateTimeUtils.formatDateTo12HourClock(endDate));
        String dateTimeText = getTodayTomorrowStringByStartDate(startDate) + formattedDate;
        mJobDateText.setText(dateTimeText);
        mJobTimeText.setText(formattedTime.toLowerCase());

        String bookingIdPrefix = mBooking.isProxy() ? BOOKING_PROXY_ID_PREFIX : "";
        mJobNumberText.setText(getResources().getString(R.string.job_number_formatted, bookingIdPrefix + mBooking.getId()));

        final PaymentInfo paymentInfo = mBooking.getPaymentToProvider();
        final PaymentInfo hourlyRate = mBooking.getHourlyRate();
        if (mBooking.hasFlexPayRate())
        {
            final float minimumHours = mBooking.getMinimumHours();
            final float maximumHours = mBooking.getHours();
            final String currencySymbol = hourlyRate.getCurrencySymbol();
            final String minimumPaymentFormatted = CurrencyUtils.formatPriceWithCents(
                    (int) (hourlyRate.getAmount() * minimumHours), currencySymbol);
            final String maximumPaymentFormatted = CurrencyUtils.formatPriceWithCents(
                    (int) (hourlyRate.getAmount() * maximumHours), currencySymbol);
            String paymentText = getResources().getString(R.string.dash_formatted,
                    minimumPaymentFormatted, maximumPaymentFormatted);
            mJobPaymentText.setText(paymentText);

            if (mBooking.getRevealDate() != null && mBooking.isClaimedByMe())
            {
                setRevealNoticeText(minimumHours, maximumHours, minimumPaymentFormatted, maximumPaymentFormatted);
            }
        }
        else if (paymentInfo != null)
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

        mFrequencyTest.setText(UIUtils.getFrequencyInfo(mBooking, getContext()));

        //Show description field regardless of claim status if the booking is not for cleaning (e.g. furniture assembly)
        boolean isHomeCleaning = mBooking.getServiceInfo().isHomeCleaning();
        if (!isHomeCleaning && mBooking.getDescription() != null && !mBooking.getDescription().isEmpty())
        {
            BookingDetailsJobInstructionsSectionView descriptionSectionView =
                    new BookingDetailsJobInstructionsSectionView(getContext());
            descriptionSectionView.setDisplay(getContext().getString(R.string.description),
                    mBooking.getDescription());
            mInstructionsLayout.setVisibility(View.VISIBLE);
            mInstructionsLayout.addView(descriptionSectionView);
        }

        //Special section for "Supplies" extras (UK only)
        List<Booking.ExtraInfoWrapper> cleaningSuppliesExtrasInfo =
                mBooking.getExtrasInfoByMachineName(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES);
        if (mBooking.isUK() && cleaningSuppliesExtrasInfo.size() > 0)
        {
            List<String> entries = new ArrayList<>();
            entries.add(getContext().getString(R.string.bring_cleaning_supplies));

            BookingDetailsJobInstructionsSectionView suppliesSectionView =
                    new BookingDetailsJobInstructionsSectionView(getContext());
            suppliesSectionView.setDisplay(getContext().getString(R.string.supplies), entries);

            mInstructionsLayout.setVisibility(View.VISIBLE);
            mInstructionsLayout.addView(suppliesSectionView);
        }

        //Extras - excluding Supplies instructions
        if (mBooking.getExtrasInfo() != null && mBooking.getExtrasInfo().size() > 0)
        {
            List<String> entries = new ArrayList<>();
            for (int i = 0; i < mBooking.getExtrasInfo().size(); i++)
            {
                Booking.ExtraInfo extra = mBooking.getExtrasInfo().get(i).getExtraInfo();
                if (!extra.getMachineName().equals(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES))
                {
                    entries.add(extra.getName());
                }
            }

            if (entries.size() > 0)
            {
                BookingDetailsJobInstructionsSectionView suppliesSectionView =
                        new BookingDetailsJobInstructionsSectionView(getContext());
                suppliesSectionView.setDisplay(getContext().getString(R.string.supplies), entries);

                mInstructionsLayout.setVisibility(View.VISIBLE);
                mInstructionsLayout.addView(suppliesSectionView);
            }
        }

        // Booking Instructions
        if (mFromPaymentsTab || !isHomeCleaning ||
                mBooking.inferBookingStatus(getLoggedInUserId()) == Booking.BookingStatus.CLAIMED)
        {
            List<Booking.BookingInstructionGroup> bookingInstructionGroups =
                    mBooking.getBookingInstructionGroups();
            if (bookingInstructionGroups != null && bookingInstructionGroups.size() > 0)
            {
                for (Booking.BookingInstructionGroup group : bookingInstructionGroups)
                {
                    if (!Booking.BookingInstructionGroup.GROUP_PREFERENCES.equals(group.getGroup()))
                    {
                        BookingDetailsJobInstructionsSectionView sectionView =
                                new BookingDetailsJobInstructionsSectionView(getContext());
                        sectionView.setDisplay(group.getLabel(), group.getInstructions());
                        mInstructionsLayout.setVisibility(View.VISIBLE);
                        mInstructionsLayout.addView(sectionView);
                    }
                }
            }
        }

        boolean noShowReported = mBooking.getAction(Booking.Action.ACTION_RETRACT_NO_SHOW) != null;
        if (noShowReported)
        {
            mNoShowBanner.setVisibility(View.VISIBLE);
        }

        // Hide map and customer contact if coming from payments tab
        if (mFromPaymentsTab)
        {
            mBookingMapView.setVisibility(View.GONE);
        }

        setActionBarTitle();
    }

    @Subscribe
    public void onReceiveZipClusterPolygonsSuccess(final BookingEvent.ReceiveZipClusterPolygonsSuccess event)
    {
        Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(getLoggedInUserId());
        mBookingMapView.setDisplay(mBooking, mSource, bookingStatus, event.zipClusterPolygons);
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
        bus.post(new HandyEvent.CallCustomerClicked());

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
        bus.post(new HandyEvent.TextCustomerClicked());

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

    private void setActionButtonVisibility()
    {
        if (mHideActionButtons)
        {
            mActionButton.setVisibility(View.GONE);
            mSupportButton.setVisibility(View.GONE);
        }
        //else keep the current visibility
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
                mBookingMapView.setDisplay(mBooking, mSource, bookingStatus, null);
            }
        }
        else
        {
            UIUtils.replaceView(mBookingMapView, new MapPlaceholderView(getContext()));
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
            mGetDirectionsLayout.setVisibility(View.VISIBLE);
        }
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getContext());
    }

    private void requestZipClusterPolygons(final String zipClusterId)
    {
        bus.post(new BookingEvent.RequestZipClusterPolygons(zipClusterId));
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

    private void requestClaimJob()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ClaimSubmitted(
                mBooking, mSource, mSourceExtras, 0.0f)));
        bus.post(new HandyEvent.RequestClaimJob(mBooking, mSource, mSourceExtras));
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
                mActionButton.setVisibility(action.isEnabled() ? View.VISIBLE : View.GONE);
                mActionButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        boolean confirmClaimDialogShown = showConfirmBookingClaimDialogIfNecessary();
                        if (!confirmClaimDialogShown)
                        {
                            requestClaimJob();
                        }
                    }
                });
                break;
            }
            case ON_MY_WAY:
            {
                mActionButton.setText(R.string.on_my_way);
                mActionButton.setVisibility(action.isEnabled() ? View.VISIBLE : View.GONE);
                mActionButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWaySubmitted(
                                mBooking, getLocationData())));
                        bus.post(new HandyEvent.RequestNotifyJobOnMyWay(
                                mBooking.getId(), getLocationData()));
                    }
                });

                initHelperText(action);
                break;
            }
            case CHECK_IN:
            {
                if (mBookingMapView.getVisibility() == View.VISIBLE)
                {
                    mBookingMapView.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) getResources().getDimension(
                                    R.dimen.check_in_booking_details_map_height)));
                }

                mActionButton.setText(R.string.check_in);
                mActionButton.setVisibility(action.isEnabled() ? View.VISIBLE : View.GONE);
                mActionButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckInSubmitted(
                                mBooking, getLocationData())));
                        bus.post(new HandyEvent.RequestNotifyJobCheckIn(
                                mBooking.getId(), getLocationData()));
                    }
                });

                initHelperText(action);
                break;
            }
            case CHECK_OUT:
            {
                mActionButton.setText(R.string.check_out);
                mActionButton.setVisibility(action.isEnabled() ? View.VISIBLE : View.GONE);
                mActionButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BundleKeys.BOOKING, mBooking);
                        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.SEND_RECEIPT_CHECKOUT, bundle, true));
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

    /**
     * shows the confirm booking claim dialog if the cancellation policy data is there, based on the given booking
     *
     * @return true if the confirm dialog is shown/is showing, false otherwise
     */
    private boolean showConfirmBookingClaimDialogIfNecessary()
    {
        final Booking.Action claimAction = mBooking.getAction(Booking.Action.ACTION_CLAIM);

        if (claimAction != null && claimAction.getExtras() != null)
        {
            Booking.Action.Extras.CancellationPolicy cancellationPolicy = claimAction.getExtras().getCancellationPolicy();
            if (cancellationPolicy != null)
            {
                if (getActivity().getSupportFragmentManager().findFragmentByTag(ConfirmBookingClaimDialogFragment.FRAGMENT_TAG) == null)
                {
                    ConfirmBookingActionDialogFragment confirmBookingDialogFragment = ConfirmBookingClaimDialogFragment.newInstance(mBooking);
                    confirmBookingDialogFragment.setTargetFragment(BookingFragment.this, RequestCode.CONFIRM_REQUEST);
                    FragmentUtils.safeLaunchDialogFragment(confirmBookingDialogFragment, this, ConfirmBookingClaimDialogFragment.FRAGMENT_TAG);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case RequestCode.CONFIRM_REQUEST:
                    requestClaimJob();
                    break;
            }
        }
    }

    private void initHelperText(Booking.Action action)
    {
        if (!TextUtils.isNullOrEmpty(action.getHelperText()) && !mHideActionButtons)
        {
            mBookingDetailsActionHelperText.setVisibility(View.VISIBLE);
            mBookingDetailsActionHelperText.setText(action.getHelperText());
        }
    }

    private boolean shouldShowSupportButton()
    {
        Booking.BookingStatus bookingStatus =
                mBooking.inferBookingStatus(mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID));
        return !mFromPaymentsTab && bookingStatus == Booking.BookingStatus.CLAIMED;
    }

    private void setRevealNoticeText(
            final float minimumHours, final float maximumHours,
            final String minimumPaymentFormatted, final String maximumPaymentFormatted)
    {
        Spanned noticeText;
        if (mBooking.hasFlexPayRate())
        {
            final String minimumHoursFormatted = TextUtils.formatHours(minimumHours);
            final String maximumHoursFormatted = TextUtils.formatHours(maximumHours);
            final String startDateFormatted = DateTimeUtils.formatDetailedDate(mBooking.getStartDate());
            final String endDateFormatted = DateTimeUtils.formatDetailedDate(mBooking.getEndDate());
            noticeText = Html.fromHtml(getResources()
                    .getString(R.string.full_details_and_more_available_on_date_flex,
                            minimumHoursFormatted, maximumHoursFormatted,
                            startDateFormatted, endDateFormatted,
                            minimumPaymentFormatted, minimumHoursFormatted,
                            maximumPaymentFormatted, maximumHoursFormatted
                    ));
        }
        else
        {
            noticeText = Html.fromHtml(getResources().getString(
                    R.string.full_details_and_more_available_on_date,
                    DateTimeUtils.formatDetailedDate(mBooking.getRevealDate())));
        }
        mRevealNoticeText.setText(noticeText);
        mRevealNoticeText.setVisibility(View.VISIBLE);
    }

    private void setActionBarTitle()
    {
        int bookingProgress = mBooking.getBookingProgress();
        if (bookingProgress == BookingProgress.READY_FOR_CLAIM)
        {
            setActionBarTitle(R.string.available_job);
        }
        else if (bookingProgress == BookingProgress.READY_FOR_ON_MY_WAY ||
                bookingProgress == BookingProgress.READY_FOR_CHECK_IN ||
                bookingProgress == BookingProgress.READY_FOR_CHECK_OUT)
        {
            setTimerIfNeeded(mBooking.getStartDate(), mBooking.getEndDate());
        }
        else //completed
        {
            setActionBarTitle(R.string.completed_job);
        }
    }

    private void setNearbyTransit(List<String> transitDescription)
    {
        mBookingNearbyTransitLayout.setVisibility(View.VISIBLE);
        mNearbyTransits.removeAllViews();
        for (String transitMarker : transitDescription)
        {
            RoundedTextView transitMarkerView = new RoundedTextView(getContext());
            transitMarkerView.setText(transitMarker);
            mNearbyTransits.addView(transitMarkerView);
        }
    }
}