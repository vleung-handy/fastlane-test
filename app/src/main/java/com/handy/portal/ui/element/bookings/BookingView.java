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
import android.text.Html;
import android.text.Spanned;
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
import com.handy.portal.constant.BookingProgress;
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

import java.util.ArrayList;
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
    @Bind(R.id.booking_frequency_text)
    TextView mFrequencyTest;
    @Bind(R.id.booking_support_button)
    Button mSupportButton;
    @Bind(R.id.booking_action_helper_text)
    TextView mBookingDetailsActionHelperText;
    @Bind(R.id.booking_job_instructions_list_layout)
    LinearLayout mInstructionsLayout;
    @Bind(R.id.booking_details_location_view)
    ProxyLocationView mProxyLocationView;
    @Bind(R.id.booking_reveal_notice_text)
    TextView mRevealNoticeText;
    @Bind(R.id.booking_job_number_text)
    TextView mJobNumberText;
    @Bind(R.id.booking_action_button)
    Button mActionButton;

    private static final String BOOKING_PROXY_ID_PREFIX = "P";

    private Booking mBooking;
    private String mSource;
    private Bundle mSourceExtras;
    private Intent mGetDirectionsIntent;
    private boolean mFromPaymentsTab;

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
        mFromPaymentsTab = fromPaymentsTab;
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

        int bookingProgress = mBooking.getBookingProgress(mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID));
        if (bookingProgress == BookingProgress.UNAVAILABLE ||
                bookingProgress == BookingProgress.READY_FOR_CLAIM ||
                mBooking.getUser() == null || fromPaymentsTab)
        {
            mBookingCustomerContactLayout.setVisibility(GONE);
        }
        else
        {
            mCustomerNameText.setText(mBooking.getUser().getFullName());
        }

        mSupportButton.setVisibility(shouldShowSupportButton() ? View.VISIBLE : View.GONE);

        Address address = mBooking.getAddress();
        if (address != null)
        {
            if (bookingProgress == BookingProgress.UNAVAILABLE ||
                    bookingProgress == BookingProgress.READY_FOR_CLAIM ||
                    fromPaymentsTab || mBooking.isProxy())
            {
                mBookingAddressText.setText(mBooking.isUK() ?
                        getResources().getString(R.string.comma_formatted,
                                address.getShortRegion(), address.getZip()) :
                        address.getShortRegion());
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

        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();
        String formattedDate = DateTimeUtils.DAY_OF_WEEK_MONTH_DAY_FORMATTER.format(startDate);
        String formattedTime = getResources().getString(R.string.dash_formatted,
                DateTimeUtils.formatDateTo12HourClock(startDate), DateTimeUtils.formatDateTo12HourClock(endDate));
        String dateTimeText = getTodayTomorrowStringByStartDate(startDate) + formattedDate;
        mJobDateText.setText(dateTimeText);
        mJobTimeText.setText(formattedTime.toLowerCase());

        if (booking.isProxy() && booking.getZipCluster() != null &&
                ((booking.getZipCluster().getTransitDescription() != null
                        && !booking.getZipCluster().getTransitDescription().isEmpty()) ||
                        (booking.getZipCluster().getLocationDescription() != null
                                && !booking.getZipCluster().getLocationDescription().isEmpty())))
        {
            mProxyLocationView.setVisibility(VISIBLE);
            mProxyLocationView.refreshDisplay(mBooking);
        }

        String bookingIdPrefix = mBooking.isProxy() ? BOOKING_PROXY_ID_PREFIX : "";
        mJobNumberText.setText(getResources().getString(R.string.job_number_formatted, bookingIdPrefix + mBooking.getId()));

        final PaymentInfo paymentInfo = mBooking.getPaymentToProvider();
        final PaymentInfo hourlyRate = booking.getHourlyRate();
        if (mBooking.hasFlexPayRate())
        {
            final float minimumHours = booking.getMinimumHours();
            final float maximumHours = booking.getHours();
            final String currencySymbol = hourlyRate.getCurrencySymbol();
            final String minimumPaymentFormatted = CurrencyUtils.formatPriceWithCents(
                    (int) (hourlyRate.getAmount() * minimumHours), currencySymbol);
            final String maximumPaymentFormatted = CurrencyUtils.formatPriceWithCents(
                    (int) (hourlyRate.getAmount() * maximumHours), currencySymbol);
            String paymentText = getResources().getString(R.string.dash_formatted,
                    minimumPaymentFormatted, maximumPaymentFormatted);
            mJobPaymentText.setText(paymentText);

            if (booking.getRevealDate() != null && booking.isClaimedByMe())
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
            NewBookingDetailsJobInstructionsSectionView descriptionSectionView =
                    new NewBookingDetailsJobInstructionsSectionView(getContext());
            descriptionSectionView.setDisplay(getContext().getString(R.string.description),
                    mBooking.getDescription());
            mInstructionsLayout.setVisibility(View.VISIBLE);
            mInstructionsLayout.addView(descriptionSectionView);
        }

        //Special section for "Supplies" extras (UK only)
        List<Booking.ExtraInfoWrapper> cleaningSuppliesExtrasInfo =
                booking.getExtrasInfoByMachineName(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES);
        if (booking.isUK() && cleaningSuppliesExtrasInfo.size() > 0)
        {
            List<String> entries = new ArrayList<>();
            entries.add(getContext().getString(R.string.bring_cleaning_supplies));

            NewBookingDetailsJobInstructionsSectionView suppliesSectionView =
                    new NewBookingDetailsJobInstructionsSectionView(getContext());
            suppliesSectionView.setDisplay(getContext().getString(R.string.supplies), entries);

            mInstructionsLayout.setVisibility(View.VISIBLE);
            mInstructionsLayout.addView(suppliesSectionView);
        }

        //Extras - excluding Supplies instructions
        if (booking.getExtrasInfo() != null && booking.getExtrasInfo().size() > 0)
        {
            List<String> entries = new ArrayList<>();
            for (int i = 0; i < booking.getExtrasInfo().size(); i++)
            {
                Booking.ExtraInfo extra = booking.getExtrasInfo().get(i).getExtraInfo();
                if (!extra.getMachineName().equals(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES))
                {
                    entries.add(extra.getName());
                }
            }

            if (entries.size() > 0)
            {
                NewBookingDetailsJobInstructionsSectionView suppliesSectionView =
                        new NewBookingDetailsJobInstructionsSectionView(getContext());
                suppliesSectionView.setDisplay(getContext().getString(R.string.supplies), entries);

                mInstructionsLayout.setVisibility(View.VISIBLE);
                mInstructionsLayout.addView(suppliesSectionView);
            }
        }

        // Booking Instructions
        if (fromPaymentsTab || !isHomeCleaning ||
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
                        NewBookingDetailsJobInstructionsSectionView sectionView =
                                new NewBookingDetailsJobInstructionsSectionView(getContext());
                        sectionView.setDisplay(group.getLabel(), group.getInstructions());
                        mInstructionsLayout.setVisibility(View.VISIBLE);
                        mInstructionsLayout.addView(sectionView);
                    }
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
}

