package com.handy.portal.bookings.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.MapsInitializer;
import com.google.common.base.Strings;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.constant.BookingActionButtonType;
import com.handy.portal.bookings.constant.BookingProgress;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.BookingDetailsJobInstructionsSectionView;
import com.handy.portal.bookings.ui.element.BookingDetailsProRequestInfoView;
import com.handy.portal.bookings.ui.element.BookingMapView;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingActionDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingClaimDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.SwapBookingClaimDialogFragment;
import com.handy.portal.chat.AuthenticationProvider;
import com.handy.portal.chat.ChatEvent;
import com.handy.portal.chat.LayerAuthenticationProvider;
import com.handy.portal.chat.LayerHelper;
import com.handy.portal.chat.LayerResponseWrapper;
import com.handy.portal.chat.PushNotificationReceiver;
import com.handy.portal.chat.SimpleRecyclerCallback;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.ui.view.MapPlaceholderView;
import com.handy.portal.library.ui.view.RoundedTextView;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Address;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.Provider;
import com.handy.portal.payments.model.PaymentInfo;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.fragment.TimerActionBarFragment;
import com.handy.portal.ui.view.FlowLayout;
import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerConnectionListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.sdk.query.SortDescriptor;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * fragment for handling bookings that are
 * not in progress i.e. ready for claim, check-in, on my way, etc
 */
public class BookingFragment extends TimerActionBarFragment implements LayerConnectionListener
{
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    @Named("layerAppId")
    String mLayerAppId;
    @Inject
    ProviderManager mProviderManager;
    @Inject
    DataManager mDataManager;

    @Inject
    LayerClient mLayerClient;

    @Inject
    LayerHelper mLayerHelper;

    @BindView(R.id.booking_details_display_message_layout)
    BookingDetailsProRequestInfoView mBookingDetailsProRequestInfoView;
    @BindView(R.id.booking_scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.booking_no_show_banner_text)
    View mNoShowBanner;
    @BindView(R.id.booking_map_view)
    BookingMapView mBookingMapView;
    @BindView(R.id.booking_customer_contact_layout)
    ViewGroup mBookingCustomerContactLayout;
    @BindView(R.id.booking_customer_name_text)
    TextView mCustomerNameText;
    @BindView(R.id.booking_address_title_text)
    TextView mBookingAddressTitleText;
    @BindView(R.id.booking_address_text)
    TextView mBookingAddressText;
    @BindView(R.id.booking_address_location_description_text)
    TextView mBookingAddressLocationDescriptionText;
    @BindView(R.id.booking_call_customer_view)
    ImageView mCallCustomerView;
    @BindView(R.id.booking_message_customer_view)
    ImageView mMessageCustomerView;
    @BindView(R.id.booking_get_directions_layout)
    ViewGroup mGetDirectionsLayout;
    @BindView(R.id.booking_job_date_text)
    TextView mJobDateText;
    @BindView(R.id.booking_job_time_text)
    TextView mJobTimeText;
    @BindView(R.id.booking_job_payment_text)
    TextView mJobPaymentText;
    @BindView(R.id.booking_job_payment_bonus_text)
    TextView mJobPaymentBonusText;
    @BindView(R.id.booking_frequency_text)
    TextView mFrequencyTest;
    @BindView(R.id.booking_support_button)
    Button mSupportButton;
    @BindView(R.id.booking_action_helper_text)
    TextView mBookingDetailsActionHelperText;
    @BindView(R.id.booking_job_instructions_list_layout)
    LinearLayout mInstructionsLayout;
    @BindView(R.id.booking_reveal_notice_text)
    TextView mRevealNoticeText;
    @BindView(R.id.booking_nearby_transit_layout)
    ViewGroup mBookingNearbyTransitLayout;
    @BindView(R.id.nearby_transits)
    FlowLayout mNearbyTransits;
    @BindView(R.id.booking_job_number_text)
    TextView mJobNumberText;
    @BindView(R.id.booking_action_button)
    Button mActionButton;
    @BindView(R.id.booking_layer_message_customer_view)
    ImageView mImageChat;

    private static final String BOOKING_PROXY_ID_PREFIX = "P";
    //    private static final String CONVERSATION_ID = "layer:///conversations/636a2014-dbd2-4e4e-a430-4131b18d56a9";
    private static final String TAG = BookingFragment.class.getSimpleName();

    private Booking mBooking;
    private String mSource;
    private Bundle mSourceExtras;
    private Intent mGetDirectionsIntent;
    private View.OnClickListener mOnSupportClickListener;
    private boolean mHideActionButtons;
    private RecyclerViewController<Conversation> mQueryController;

    public static BookingFragment newInstance(@NonNull final Booking booking, final String source,
                                              boolean hideActionButtons)
    {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        args.putString(BundleKeys.BOOKING_SOURCE, source);
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
        setHasOptionsMenu(true);
        mBookingMapView.onCreate(savedInstanceState);
        mBookingMapView.disableParentScrolling(mScrollView);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);

        updateDisplayWithBookingProRequestDisplayAttributes();
    }

    /**
     * shows the booking details message view
     * with the message in the booking's pro request display model
     * if the message is present and someone in the proxy/booking requested the pro
     * <p/>
     * TODO ugly! would be nice if the display attributes were generic but
     * since there's not enough time to fully generalize this
     * we're making it specific to pro request for now
     */
    private void updateDisplayWithBookingProRequestDisplayAttributes()
    {
        if (mBooking.isRequested()) //ideally should be decoupled
        {
            Booking.DisplayAttributes displayAttributes = mBooking.getProviderRequestDisplayAttributes();
            if (displayAttributes != null
                    && (displayAttributes.getDetailsBody() != null
                    || displayAttributes.getDetailsTitle() != null))
            {
                mBookingDetailsProRequestInfoView.setVisibility(View.VISIBLE); //GONE by default
                mBookingDetailsProRequestInfoView.setDisplayModel(displayAttributes);
                if (mBooking.canSwap())
                {
                    mBookingDetailsProRequestInfoView.showSwapIcon();
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        initLayer();

        mBookingMapView.onResume();

        setDisplay();
    }

    @Override
    public void onPause()
    {
        mBookingMapView.onPause();
        bus.unregister(this);
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
    public void onPrepareOptionsMenu(final Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        try
        {
            /*
                similar to the exception thrown by mBookingMapView.onDestroy()
                not caused by mBookingMapView = null
             */
            mBookingMapView.onSaveInstanceState(outState);
        }
        catch (Exception e)
        {
            Crashlytics.log("Error while attempting MapView.onSaveInstanceState(). Ignoring exception: " + e.getMessage());
        }
    }

    public void setDisplay()
    {
        setActionButtonVisibility();
        mSupportButton.setOnClickListener(mOnSupportClickListener);

        initMapLayout();

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
                mBooking.getUser() == null)
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
                if (bookingStatus != Booking.BookingStatus.CLAIMED)
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
        String dateTimeText = DateTimeUtils.getTodayTomorrowStringByStartDate(startDate, getContext()) + formattedDate;
        mJobDateText.setText(dateTimeText);
        mJobTimeText.setText(formattedTime.toLowerCase());

        String bookingIdPrefix = mBooking.isProxy() ? BOOKING_PROXY_ID_PREFIX : "";
        mJobNumberText.setText(getResources().getString(R.string.job_number_formatted, bookingIdPrefix + mBooking.getId()));

        final PaymentInfo paymentInfo = mBooking.getPaymentToProvider();
        final PaymentInfo hourlyRate = mBooking.getHourlyRate();
        if (hourlyRate != null)
        {
            if (mBooking.hasFlexibleHours())
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
                    setRevealNoticeText(minimumHours, maximumHours, minimumPaymentFormatted,
                            maximumPaymentFormatted);
                }
            }
            else
            {
                final String paymentFormatted = CurrencyUtils.formatPriceWithCents(
                        (int) (hourlyRate.getAmount() * mBooking.getHours()),
                        hourlyRate.getCurrencySymbol());
                mJobPaymentText.setText(paymentFormatted);
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
        if (!isHomeCleaning ||
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

        setActionBarTitle();
    }

    @Subscribe
    public void onReceiveZipClusterPolygonsSuccess(final BookingEvent.ReceiveZipClusterPolygonsSuccess event)
    {
        Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(getLoggedInUserId());
        mBookingMapView.setDisplay(mBooking, bookingStatus, event.zipClusterPolygons);
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
        if (phoneNumber == null)
        {
            showInvalidPhoneNumberToast();
            Crashlytics.logException(new Exception("Phone number is null for booking " + mBooking.getId()));
            return;
        }

        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)), getContext());
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),
                    getString(R.string.unable_to_call_customer), Toast.LENGTH_SHORT).show();
            Crashlytics.logException(new RuntimeException("Calling a Phone Number failed", e));
        }
    }

    @OnClick(R.id.booking_message_customer_view)
    public void messageCustomer()
    {
        bus.post(new HandyEvent.TextCustomerClicked());

        String phoneNumber = mBooking.getBookingPhone();
        if (phoneNumber == null)
        {
            showInvalidPhoneNumberToast();
            Crashlytics.logException(new Exception("Phone number is null for booking " + mBooking.getId()));
            return;
        }

        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)), getContext());
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),
                    getString(R.string.unable_to_text_customer), Toast.LENGTH_SHORT).show();
            Crashlytics.logException(new RuntimeException("Texting a Phone Number failed", e));
        }
    }

    @OnClick(R.id.booking_layer_message_customer_view)
    public void layerMessageCustomer()
    {
        if (!Strings.isNullOrEmpty(mBooking.getConversationId()))
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable(PushNotificationReceiver.LAYER_CONVERSATION_KEY,
                    Uri.parse(mBooking.getConversationId()));
            bundle.putString(BundleKeys.BOOKING_USER, mBooking.getUser().getFirstName());
            bus.post(new NavigationEvent.NavigateToPage(MainViewPage.MESSAGES_LIST,
                    bundle, TransitionStyle.SEND_VERIFICAITON_SUCCESS, true));
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
                mBookingMapView.setDisplay(mBooking, bookingStatus, null);
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
                mActionButton.setText(mBooking.canSwap() ? R.string.upgrade_job : R.string.claim_job);
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
                        if (isUserInRangeOfBooking())
                        {
                            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                            bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckInSubmitted(
                                    mBooking, getLocationData())));
                            bus.post(new HandyEvent.RequestNotifyJobCheckIn(
                                    mBooking.getId(), getLocationData()));
                        }
                        else
                        {
                            showToast(R.string.too_far);
                            bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckInFailure(
                                    mBooking, getLocationData()
                            )));
                        }
                    }
                });

                initHelperText(action);
                break;
            }
            case CHECK_OUT:
            {
                mActionButton.setText(R.string.continue_to_check_out);
                mActionButton.setVisibility(action.isEnabled() ? View.VISIBLE : View.GONE);
                mActionButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BundleKeys.BOOKING, mBooking);
                        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.SEND_RECEIPT_CHECKOUT, bundle, true));
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

    private boolean isUserInRangeOfBooking()
    {
        Booking.Action checkInAction = mBooking.getAction(Booking.Action.ACTION_CHECK_IN);
        Location userLocation = ((BaseActivity) getActivity()).getLastLocation();
        Address address = mBooking.getAddress();

        if (checkInAction == null || checkInAction.getCheckInConfig() == null ||
                userLocation == null || address == null)
        {
            return true;
        }

        Booking.Action.CheckInConfig config = checkInAction.getCheckInConfig();
        int maxDistanceInMeters = config.getMaxDistanceInMeters();
        int toleranceInMeters = config.getToleranceInMeters();

        Location bookingLocation = new Location("");
        bookingLocation.setLatitude(address.getLatitude());
        bookingLocation.setLongitude(address.getLongitude());
        float userAccuracyInMeters = userLocation.getAccuracy();
        float userDistanceInMeters = userLocation.distanceTo(bookingLocation);

        return userDistanceInMeters <= maxDistanceInMeters && userAccuracyInMeters <= toleranceInMeters;
    }

    /**
     * shows the confirm booking claim dialog if the cancellation policy data is there, based on the given booking
     *
     * @return true if the confirm dialog is shown/is showing, false otherwise
     */
    private boolean showConfirmBookingClaimDialogIfNecessary()
    {
        final Booking.Action claimAction = mBooking.getAction(Booking.Action.ACTION_CLAIM);
        if (mBooking.canSwap())
        {
            if (getChildFragmentManager()
                    .findFragmentByTag(SwapBookingClaimDialogFragment.FRAGMENT_TAG) == null)
            {
                final SwapBookingClaimDialogFragment dialogFragment =
                        SwapBookingClaimDialogFragment.newInstance(mBooking);
                dialogFragment.setTargetFragment(BookingFragment.this, RequestCode.CONFIRM_SWAP);
                FragmentUtils.safeLaunchDialogFragment(dialogFragment, this,
                        SwapBookingClaimDialogFragment.FRAGMENT_TAG);
            }
            return true;
        }
        else if (claimAction != null && claimAction.getExtras() != null)
        {
            Booking.Action.Extras.CancellationPolicy cancellationPolicy = claimAction.getExtras().getCancellationPolicy();
            if (cancellationPolicy != null)
            {
                if (getChildFragmentManager().findFragmentByTag(ConfirmBookingClaimDialogFragment.FRAGMENT_TAG) == null)
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
                case RequestCode.CONFIRM_SWAP:
                case RequestCode.CONFIRM_REQUEST:
                    requestClaimJob();
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
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
        return bookingStatus == Booking.BookingStatus.CLAIMED;
    }

    private void setRevealNoticeText(
            final float minimumHours, final float maximumHours,
            final String minimumPaymentFormatted, final String maximumPaymentFormatted)
    {
        Spanned noticeText;
        if (mBooking.hasFlexibleHours())
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
            if (!Strings.isNullOrEmpty(transitMarker))
            {
                transitMarkerView.setText(transitMarker.trim());
                mNearbyTransits.addView(transitMarkerView);
            }
        }
    }

    private void showInvalidPhoneNumberToast()
    {
        Toast.makeText(getContext(),
                getString(R.string.invalid_phone_number), Toast.LENGTH_LONG).show();
    }

    private void initLayer()
    {
        if ((mLayerClient != null) && mLayerClient.isAuthenticated())
        {
            Log.d(TAG, "initLayer: Already logged in");
            syncConversation();
        }
        else
        {
            mLayerHelper.authenticate(
                    new LayerAuthenticationProvider.Credentials(mLayerAppId, mProviderManager.getCachedActiveProvider().getId()),
                    new AuthenticationProvider.Callback()
                    {
                        @Override
                        public void onSuccess(AuthenticationProvider provider, String userId)
                        {
                            Log.d(TAG, "AUTH onSuccess: ");
                            syncConversation();
                        }

                        @Override
                        public void onError(AuthenticationProvider provider, final String error)
                        {
                            Log.e(TAG, "Failed to authenticate as `" +
                                    mProviderManager.getCachedActiveProvider().getFullName() + "`: " + error);
                            //TODO: JIA: this should never happen, so if it does, then log something
                        }
                    }
            );

        }
    }

    private void syncConversation()
    {
        Log.d(TAG, "syncConversation() called");
        Conversation conversation = mLayerClient.getConversation(Uri.parse(mBooking.getConversationId()));

        if (conversation != null)
        {
            Log.d(TAG, "syncConversation: conversation is not null");
            Conversation.HistoricSyncStatus status = conversation.getHistoricSyncStatus();

            if (status == Conversation.HistoricSyncStatus.MORE_AVAILABLE)
            {
                Log.d(TAG, "syncConversation: There is more messages available for synching");
                conversation.syncMoreHistoricMessages(20);
            }

            if (getActivity() != null)
            {
                Log.d(TAG, "syncConversation: making chat icon visible on UI thread");
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mImageChat.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
        else
        {
            //the conversations haven't been initialized yet. Initialize them.
            Log.d(TAG, "syncConversation: conversation is NULL");
            initConversations();
        }
    }

    private void initConversations()
    {
        Log.d(TAG, "initConversations: ");
        if (mQueryController != null)
        {
            //only do this once, so we don't get ourselves into an infinite loop type scenario
            Log.d(
                    TAG,
                    "initConversations: query controller has already been created, not doing it again"
            );
            return;
        }
        Query<Conversation> query = Query.builder(Conversation.class)
                /* Only show conversations we're still a member of */
                .predicate(new Predicate(
                        Conversation.Property.PARTICIPANT_COUNT,
                        Predicate.Operator.GREATER_THAN,
                        1
                ))

                /* Sort by the last Message's receivedAt time */
                .sortDescriptor(new SortDescriptor(
                        Conversation.Property.LAST_MESSAGE_RECEIVED_AT,
                        SortDescriptor.Order.DESCENDING
                ))
                .build();

        mQueryController = mLayerClient.newRecyclerViewController(
                query,
                null,
                new SimpleRecyclerCallback()
                {
                    @Override
                    public void onQueryItemInserted(
                            final RecyclerViewController recyclerViewController,
                            final int i
                    )
                    {
                        Log.d(
                                TAG,
                                "onQueryItemInserted() called with: recyclerViewController = [" + recyclerViewController + "], i = [" + i + "]"
                        );
                        syncConversation();
                    }
                }
        );

        Log.d(TAG, "initConversations: executing query, look out for events coming back");
        mQueryController.execute();
    }

    @Override
    public void onConnectionConnected(final LayerClient layerClient)
    {
        if (!Strings.isNullOrEmpty(mBooking.getConversationId()))
        {
            if ((mLayerClient != null) && mLayerClient.isAuthenticated())
            {
                Log.d(TAG, "initLayer: Already logged in");
                syncConversation();
            }
            else
            {
                Log.d(TAG, "initLayer: Not logged in");
                final Provider loggedInProvider = mProviderManager.getCachedActiveProvider();
                if (loggedInProvider != null)
                {
                    mLayerHelper.authenticate(
                            new LayerAuthenticationProvider.Credentials(mLayerAppId,
                                    loggedInProvider.getId()),
                            new AuthenticationProvider.Callback()
                            {
                                @Override
                                public void onSuccess(AuthenticationProvider provider, String userId)
                                {
                                    Log.d(TAG, "AUTH onSuccess: ");
                                    syncConversation();
                                }

                                @Override
                                public void onError(AuthenticationProvider provider, final String error)
                                {
                                    Log.e(TAG, "Failed to authenticate as `" +
                                            loggedInProvider.getFullName() + "`: " + error);
                                    //TODO: JIA: this should never happen, so if it does, then log something
                                }
                            }
                    );
                }

            }
        }
        else
        {
            mImageChat.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionDisconnected(final LayerClient layerClient)
    {

    }

    @Override
    public void onConnectionError(final LayerClient layerClient, final LayerException e)
    {

    }

    @Subscribe
    public void onReceiveLayerAuthTokenSuccess(ChatEvent.ReceiveLayerAuthTokenSuccessEvent event)
    {
        Log.d(TAG, "respondToChallengeSuccess: ");
        mLayerClient.answerAuthenticationChallenge(event.identityToken);
    }

    @Subscribe
    public void onReceiveLayerAuthTokenError(ChatEvent.ReceiveLayerAuthTokenErrorEvent event)
    {
        Log.d(TAG, "respondToChallengeError: ");
    }

    @Subscribe
    public void onRequestLayerAuthToken(ChatEvent.RequestLayerAuthTokenEvent event)
    {
        mDataManager.getLayerAuthToken(
                event.userId,
                event.nonce,
                new DataManager.Callback<LayerResponseWrapper>()
                {
                    @Override
                    public void onSuccess(final LayerResponseWrapper response)
                    {
                        bus.post(new ChatEvent.ReceiveLayerAuthTokenSuccessEvent(response));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        bus.post(new ChatEvent.ReceiveLayerAuthTokenErrorEvent(error));
                    }
                }
        );
    }
}
