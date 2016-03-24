package com.handy.portal.ui.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.SupportActionType;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.constant.WarningButtonsText;
import com.handy.portal.event.BookingEvent;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.logger.handylogger.model.CheckInFlowLog;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;
import com.handy.portal.model.Booking.BookingType;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.LocationData;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.element.SupportActionContainerView;
import com.handy.portal.ui.element.bookings.BookingDetailsActionContactPanelView;
import com.handy.portal.ui.element.bookings.BookingDetailsActionPanelView;
import com.handy.portal.ui.element.bookings.BookingDetailsDateView;
import com.handy.portal.ui.element.bookings.BookingDetailsJobInstructionsView;
import com.handy.portal.ui.element.bookings.BookingDetailsTitleView;
import com.handy.portal.ui.element.bookings.ProxyLocationView;
import com.handy.portal.ui.fragment.dialog.ClaimTargetDialogFragment;
import com.handy.portal.ui.layout.SlideUpPanelLayout;
import com.handy.portal.ui.view.MapPlaceholderView;
import com.handy.portal.ui.widget.BookingActionButton;
import com.handy.portal.util.SupportActionUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingDetailsFragment extends ActionBarFragment
{
    public static final String SOURCE_LATE_DISPATCH = "late_dispatch";
    @Bind(R.id.booking_details_map_layout)
    ViewGroup mapLayout; // Maybe use fragment instead of view group?
    @Bind(R.id.booking_details_date_view)
    BookingDetailsDateView mDateView;
    @Bind(R.id.booking_details_title_view)
    BookingDetailsTitleView mTitleView;
    @Bind(R.id.booking_details_action_view)
    BookingDetailsActionPanelView mActionPanelView;
    @Bind(R.id.booking_details_contact_view)
    BookingDetailsActionContactPanelView mContactPanelView;
    @Bind(R.id.booking_details_job_instructions_view)
    BookingDetailsJobInstructionsView mJobInstructionsView;
    @Bind(R.id.booking_details_location_view)
    ProxyLocationView mProxyLocationView;
    @Bind(R.id.booking_details_support_button)
    BookingActionButton mSupportButton;
    @Bind(R.id.booking_details_full_details_notice_text)
    TextView mFullDetailsNoticeText;
    @Bind(R.id.fetch_error_view)
    View fetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mErrorText;
    @Bind(R.id.slide_up_panel_container)
    SlideUpPanelLayout mSlideUpPanelLayout;
    @Bind(R.id.booking_details_scroll_view)
    ScrollView mScrollView;

    @Inject
    PrefsManager mPrefsManager;

    private static final String BOOKING_PROXY_ID_PREFIX = "P";
    private static final float TRACK_JOB_INSTRUCTIONS_SEEN_PERCENT_VIEW_THRESHOLD = 0.5f; //50% of booking instructions view visible on screen
    private static final Gson GSON = new Gson();

    private String mRequestedBookingId;
    private BookingType mRequestedBookingType;
    private Date mAssociatedBookingDate;
    private boolean mFromPaymentsTab;
    private MainViewTab mCurrentTab;
    private boolean mHaveTrackedSeenBookingInstructions;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    private String mSource;
    private Bundle mSourceExtras;
    private Booking mAssociatedBooking;

    @Override
    protected MainViewTab getTab()
    {
        return mCurrentTab;
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setOptionsMenuEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_booking_detail, container, false);

        ButterKnife.bind(this, view);

        if (validateRequiredArguments())
        {
            Bundle arguments = getArguments();
            mRequestedBookingId = arguments.getString(BundleKeys.BOOKING_ID);
            final String bookingType = arguments.getString(BundleKeys.BOOKING_TYPE);
            if (bookingType != null)
            {
                mRequestedBookingType = BookingType.valueOf(bookingType.toUpperCase());
            }
            mCurrentTab = (MainViewTab) arguments.getSerializable(BundleKeys.TAB);

            mFromPaymentsTab = arguments.getBoolean(BundleKeys.IS_FOR_PAYMENTS, false);

            if (arguments.containsKey(BundleKeys.BOOKING_DATE))
            {
                long bookingDateLong = arguments.getLong(BundleKeys.BOOKING_DATE, 0L);
                mAssociatedBookingDate = new Date(bookingDateLong);
            }

            setSourceInfo();
        }
        else
        {
            showToast(R.string.an_error_has_occurred);
            returnToTab(MainViewTab.AVAILABLE_JOBS, 0, TransitionStyle.REFRESH_TAB);
        }

        //tracking for when user scrolls to various sections
        initScrollViewListener();

        return view;
    }

    private void setSourceInfo()
    {
        if (getArguments().containsKey(BundleKeys.BOOKING_SOURCE))
        {
            mSource = getArguments().getString(BundleKeys.BOOKING_SOURCE);
        }
        else if (getArguments().containsKey(BundleKeys.DEEPLINK))
        {
            mSource = SOURCE_LATE_DISPATCH;
            mSourceExtras = getArguments();
        }
    }

    //tracking for when user scrolls to various sections
    private void initScrollViewListener()
    {
        mOnScrollChangedListener = (new ViewTreeObserver.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged()
            {
                if (!mHaveTrackedSeenBookingInstructions &&
                        mScrollView != null)
                {
                    float percentVis = UIUtils.getPercentViewVisibleInScrollView(mJobInstructionsView, mScrollView);
                    if (percentVis >= TRACK_JOB_INSTRUCTIONS_SEEN_PERCENT_VIEW_THRESHOLD)
                    {
                        //flip flag so we don't spam this event
                        mHaveTrackedSeenBookingInstructions = true; //not guaranteed to stay flipped throughout session just on screen
                        //track event
                        bus.post(new LogEvent.AddLogEvent(
                                new ScheduledJobsLog.BookingInstructionsSeen(mAssociatedBooking.getId())));
                    }
                }
            }
        });
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
    }

    @Override
    public void onDestroyView()
    {
        mScrollView.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_booking_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateActionBar(Menu menu) //passing menu as argument because there is no way to get a reference to it programmatically
    {
        if (mAssociatedBooking != null)
        {
            int titleStringId = 0;
            String bookingIdPrefix = mAssociatedBooking.isProxy() ? BOOKING_PROXY_ID_PREFIX : "";
            String jobLabel = getActivity().getString(R.string.job_num) + bookingIdPrefix + mAssociatedBooking.getId();

            if (mFromPaymentsTab)
            {
                titleStringId = R.string.previous_job;
                menu.findItem(R.id.action_job_label).setTitle(jobLabel);
            }
            else
            {
                Booking.BookingStatus bookingStatus = mAssociatedBooking.inferBookingStatus(getLoggedInUserId());
                if (bookingStatus != null)
                {
                    switch (bookingStatus)
                    {
                        case AVAILABLE:
                        {
                            titleStringId = R.string.available_job;
                        }
                        break;

                        case CLAIMED:
                        {
                            menu.findItem(R.id.action_job_label).setTitle(jobLabel);
                            titleStringId = R.string.your_job;
                        }
                        break;

                        case UNAVAILABLE:
                        {
                            titleStringId = R.string.unavailable_job;
                        }
                        break;
                    }
                }
            }
            setActionBar(titleStringId, true);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) //this gets called after invalidateOptionsMenu()/every time menu is shown
    {
        super.onPrepareOptionsMenu(menu);
        updateActionBar(menu);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!MainActivityFragment.clearingBackStack)
        {
            requestBookingDetails(mRequestedBookingId, mRequestedBookingType, mAssociatedBookingDate);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mAssociatedBooking != null && mAssociatedBooking.isCheckedIn())
        {
            List<Booking.BookingInstructionUpdateRequest> checklist = mAssociatedBooking.getCustomerPreferences();
            if (checklist != null)
            {
                mPrefsManager.setBookingInstructions(mAssociatedBooking.getId(), GSON.toJson(checklist));
            }
        }
    }

    @Override
    protected List<String> requiredArguments()
    {
        return Lists.newArrayList(BundleKeys.BOOKING_ID, BundleKeys.BOOKING_TYPE);
    }

    private String getLoggedInUserId()
    {
        return mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }

    private void requestBookingDetails(String bookingId, BookingType type, Date bookingDate)
    {
        fetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestBookingDetails(bookingId, type, bookingDate));
    }

//Display Creation / Updating

    @VisibleForTesting
    protected void updateDisplayForBooking(Booking booking)
    {
        refreshDisplayElements(booking);
        invalidateOptionsMenu();
        //TODO: This is confusing. We should create the buttons in refreshDisplayElements()
        createAllowedActionButtons(booking);

        //I do not like having these button linkages here, strongly considering having buttons generate events we listen for so the fragment doesn't initialize them
        initCancelNoShowButton();
    }

    private void refreshDisplayElements(Booking booking)
    {
        BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING_STATUS, bookingStatus);
        arguments.putBoolean(BundleKeys.IS_FOR_PAYMENTS, mFromPaymentsTab);

        if (mFromPaymentsTab)
        {
            mapLayout.setVisibility(View.GONE);
        }
        else
        {
            initMapLayout();
        }

        mDateView.refreshDisplay(booking);
        mTitleView.refreshDisplay(booking, mFromPaymentsTab, bookingStatus);

        mActionPanelView.refreshDisplay(booking);
        if (!mFromPaymentsTab)
        {
            mContactPanelView.refreshDisplay(booking);
        }

        mJobInstructionsView.refreshDisplay(booking, mFromPaymentsTab, bookingStatus);
        mProxyLocationView.refreshDisplay(booking);

        if (!mFromPaymentsTab && bookingStatus == BookingStatus.CLAIMED)
        {
            mSupportButton.init(booking, this, BookingActionButtonType.HELP);
            mSupportButton.setVisibility(View.VISIBLE);
        }
        else
        {
            mSupportButton.setVisibility(View.GONE);
        }

        mFullDetailsNoticeText.setVisibility(!booking.isProxy() && booking.getServiceInfo().isHomeCleaning()
                && bookingStatus == BookingStatus.AVAILABLE ? View.VISIBLE : View.GONE);
    }

    private void initMapLayout()
    {//show either the real map or a placeholder image depending on if we have google play services
        BookingStatus bookingStatus = mAssociatedBooking.inferBookingStatus(getLoggedInUserId());
        if (ConnectionResult.SUCCESS ==
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()))
        {
            final String zipClusterId = mAssociatedBooking.getZipClusterId();
            if (zipClusterId != null)
            {
                requestZipClusterPolygons(zipClusterId);
            }
            else
            {
                BookingMapFragment fragment = BookingMapFragment.newInstance(
                        mAssociatedBooking,
                        mSource,
                        bookingStatus
                );
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(mapLayout.getId(), fragment).commit();
            }
        }
        else
        {
            UIUtils.replaceView(mapLayout, new MapPlaceholderView(getContext()));
        }
    }

    private void requestZipClusterPolygons(final String zipClusterId)
    {
        bus.post(new BookingEvent.RequestZipClusterPolygons(zipClusterId));
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
        if (mAssociatedBooking != null)
        {
            BookingStatus bookingStatus = mAssociatedBooking.inferBookingStatus(getLoggedInUserId());
            BookingMapFragment fragment = BookingMapFragment.newInstance(
                    mAssociatedBooking,
                    mSource,
                    bookingStatus,
                    event.zipClusterPolygons
            );
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(mapLayout.getId(), fragment).commit();
        }
    }

    @OnClick(R.id.try_again_button)
    public void onClickRequestDetails()
    {
        requestBookingDetails(mRequestedBookingId, mRequestedBookingType, mAssociatedBookingDate);
    }

    private void initCancelNoShowButton()
    {
        ViewGroup cancelNoShowButton = (ViewGroup) mActionPanelView.findViewById(R.id.cancel_no_show_button);
        if (cancelNoShowButton != null && isActionRetractNoShowAllowed())
        {
            cancelNoShowButton.setVisibility(View.VISIBLE);
            cancelNoShowButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    takeAction(BookingActionButtonType.RETRACT_NO_SHOW, false);
                }
            });
        }
    }

    private boolean isActionRetractNoShowAllowed()
    {
        for (Booking.Action action : mAssociatedBooking.getAllowedActions())
        {
            if (action.getActionName().equals(Booking.Action.ACTION_RETRACT_NO_SHOW))
            {
                return true;
            }
        }
        return false;
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
                bookingActionButton.init(booking, this, action);
            }
        }
    }

    //Mapping for ButtonActionType to Parent Layout, used when adding Action Buttons dynamically
    private ViewGroup getParentLayoutForButtonActionType(BookingActionButtonType buttonActionType)
    {
        switch (buttonActionType)
        {
            case CLAIM:
            case ON_MY_WAY:
            case CHECK_IN:
            case CHECK_OUT:
            {
                return (ViewGroup) mActionPanelView.findViewById(R.id.booking_details_action_panel_button_layout);
            }
            case CONTACT_PHONE:
            {
                return (ViewGroup) mContactPanelView.findViewById(R.id.booking_details_contact_action_button_layout_slot_1);
            }
            case CONTACT_TEXT:
            {
                return (ViewGroup) mContactPanelView.findViewById(R.id.booking_details_contact_action_button_layout_slot_2);
            }
            default:
            {
                return null;
            }
        }
    }

    //Click Actions
    //The button onclick tells us what action to look up in our booking for additional data
    //The associated booking remains the supreme data source for us
    public void onActionButtonClick(BookingActionButtonType actionType)
    {
        takeAction(actionType, false);
    }

    //Take an action type, checking for warnings + showing warning dialog as needed
    protected void takeAction(BookingActionButtonType actionType, boolean hasBeenWarned)
    {
        boolean allowAction = true;

        if (!hasBeenWarned)
        {
            allowAction = !checkShowWarningDialog(actionType);
        }

        if (!allowAction)
        {
            return;
        }

        LocationData locationData = getLocationData();

        bus.post(new HandyEvent.ActionTriggered(actionType));
        switch (actionType)
        {
            case CLAIM:
            {
                requestClaimJob(mAssociatedBooking);
            }
            break;

            case ON_MY_WAY:
            {
                requestNotifyOnMyWayJob(mAssociatedBooking.getId(), locationData);
            }
            break;

            case CHECK_IN:
            {
                requestNotifyCheckInJob(mAssociatedBooking.getId(), locationData);
            }
            break;

            case HELP:
            {
                showHelpOptions();
            }
            break;

            case CHECK_OUT:
            {
                final boolean proReportedNoShow = mAssociatedBooking.getAction(Booking.Action.ACTION_RETRACT_NO_SHOW) != null;
                if (proReportedNoShow || mAssociatedBooking.isAnyPreferenceChecked())
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BundleKeys.BOOKING, mAssociatedBooking);
                    bus.post(new NavigationEvent.NavigateToTab(MainViewTab.SEND_RECEIPT_CHECKOUT, bundle));
                }
                else
                {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    showToast(R.string.check_customer_preferences, Toast.LENGTH_LONG, Gravity.TOP);
                }
            }
            break;

            case REMOVE:
            {
                requestRemoveJob(mAssociatedBooking);
            }
            break;

            case CONTACT_PHONE:
            {
                bus.post(new HandyEvent.CallCustomerClicked());
                callPhoneNumber(mAssociatedBooking.getBookingPhone());
            }
            break;

            case CONTACT_TEXT:
            {
                bus.post(new HandyEvent.TextCustomerClicked());
                textPhoneNumber(mAssociatedBooking.getBookingPhone());
            }
            break;

            case RETRACT_NO_SHOW:
            {
                requestCancelNoShow();
            }
            break;

            default:
            {
                Crashlytics.log("Could not find associated behavior for : " + actionType.getActionName());
            }
        }
    }

    private LocationData getLocationData()
    {
        return Utils.getCurrentLocation((BaseActivity) getActivity());
    }

    private void showHelpOptions()
    {
        String bookingId = mAssociatedBooking.getId();
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.SupportSelected(bookingId)));
        //TODO: We might no longer need this null check since we no longer do ButterKnife.unbind()
        if (mSlideUpPanelLayout != null)
        {
            LinearLayout layout = UIUtils.createLinearLayout(getContext(), LinearLayout.VERTICAL);
            layout.addView(new SupportActionContainerView(
                    getContext(), SupportActionUtils.ETA_ACTION_NAMES, mAssociatedBooking));
            layout.addView(new SupportActionContainerView(
                    getContext(), SupportActionUtils.ISSUE_ACTION_NAMES, mAssociatedBooking));
            mSlideUpPanelLayout.showPanel(R.string.job_support, layout);
        }
        else
        {
            Crashlytics.log("SlideUpPanelLayout is null in: " + getClass().getSimpleName());
        }
    }

    //Check if the current booking data for a given action type has an associated warning to display
    private boolean checkShowWarningDialog(BookingActionButtonType actionType)
    {
        boolean showingWarningDialog = false;

        List<Booking.Action> allowedActions = mAssociatedBooking.getAllowedActions();

        //crawl through our list of allowed actions to retrieve the data from the booking for this allowed action
        for (Booking.Action action : allowedActions)
        {
            if (UIUtils.getAssociatedActionType(action) == actionType)
            {
                if (action.getWarningText() != null && !action.getWarningText().isEmpty())
                {
                    showingWarningDialog = true;
                    showBookingActionWarningDialog(action.getWarningText(), UIUtils.getAssociatedActionType(action));
                }
            }
        }
        return showingWarningDialog;
    }

    //Show a warning dialog for a button action, confirming triggers the original action
    private void showBookingActionWarningDialog(final String warning, final BookingActionButtonType actionType)
    {
        trackShowActionWarning(actionType);
        //specific booking error, show an alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        WarningButtonsText warningButtonsText = WarningButtonsText.forAction(actionType);

        // set dialog message
        alertDialogBuilder
                .setTitle(warningButtonsText.getTitleStringId())
                .setMessage(warning)
                .setPositiveButton(warningButtonsText.getPositiveStringId(), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                //proceed with action, we have accepted the warning
                                bus.post(new HandyEvent.ActionWarningAccepted(actionType));
                                takeAction(actionType, true);
                            }
                        }
                )
                .setNegativeButton(warningButtonsText.getNegativeStringId(), null)
        ;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void trackShowActionWarning(final BookingActionButtonType actionType)
    {
        switch (actionType)
        {
            case REMOVE:
            {
                bus.post(new HandyEvent.ShowConfirmationRemoveJob());
            }
            break;
        }
    }

    //Service request bus posts

    private void requestClaimJob(Booking booking)
    {
        bus.post(new LogEvent.AddLogEvent(
                new AvailableJobsLog.ClaimSubmitted(booking, mSource, mSourceExtras, 0.0f)));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestClaimJob(booking, mSource, mSourceExtras));
    }

    private void requestRemoveJob(@NonNull Booking booking)
    {
        final Booking.Action removeAction = booking.getAction(Booking.Action.ACTION_REMOVE);
        String warning = (removeAction != null) ? removeAction.getWarningText() : null;
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobConfirmed(booking, warning, null)));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestRemoveJob(booking));
    }

    private void requestNotifyOnMyWayJob(String bookingId, LocationData locationData)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobOnMyWay(bookingId, locationData));
        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.OnMyWay(mAssociatedBooking, locationData)));
    }

    private void requestNotifyCheckInJob(String bookingId, LocationData locationData)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobCheckIn(bookingId, locationData));
        bus.post(new LogEvent.AddLogEvent(new CheckInFlowLog.CheckIn(mAssociatedBooking, locationData)));
    }

    private void requestNotifyUpdateArrivalTime(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption)
    {
        //TODO: We might no longer need this null check since we no longer do ButterKnife.unbind()
        if (mSlideUpPanelLayout != null)
        {
            mSlideUpPanelLayout.hidePanel();
        }
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobUpdateArrivalTime(bookingId, arrivalTimeOption));
    }

    private void requestReportNoShow()
    {
        //TODO: Crash #608, this is null sometimes and crashing, butterknife timing?
        //TODO: We might no longer need this null check since we no longer do ButterKnife.unbind()
        if (mSlideUpPanelLayout != null)
        {
            mSlideUpPanelLayout.hidePanel();
        }
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestReportNoShow(mAssociatedBooking.getId(), getLocationData()));
    }

    private void requestCancelNoShow()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestCancelNoShow(mAssociatedBooking.getId(), getLocationData()));
    }

    //Show a radio button option dialog to select arrival time for the ETA action
    private void showUpdateArrivalTimeDialog(Booking booking, int titleStringId, final List<Booking.ArrivalTimeOption> options)
    {
        final String bookingId = booking.getId();

        String[] optionStrings = Collections2.transform(options, new Function<Booking.ArrivalTimeOption, String>()
        {
            @Override
            public String apply(Booking.ArrivalTimeOption input)
            {
                return getString(input.getStringId());
            }
        }).toArray(new String[options.size()]);

        new AlertDialog.Builder(getActivity())
                .setTitle(titleStringId)
                .setSingleChoiceItems(optionStrings, 0, null)
                .setPositiveButton(R.string.send_update, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                int checkedItemPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                if (checkedItemPosition >= 0 && checkedItemPosition < options.size())
                                {
                                    requestNotifyUpdateArrivalTime(bookingId, options.get(checkedItemPosition));
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.back, null)
                .create()
                .show();
    }

    private void showCustomerNoShowDialog(final Booking.Action action)
    {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.report_customer_no_show)
                .setMessage(action.getWarningText())
                .setPositiveButton(R.string.report_no_show, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which)
                    {
                        bus.post(new HandyEvent.ActionWarningAccepted(action));
                        requestReportNoShow();
                    }
                })
                .setNegativeButton(R.string.back, null)
                .create()
                .show();
    }

    //use native functionality to trigger a phone call
    private void callPhoneNumber(final String phoneNumber)
    {
        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)), getActivity());
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Calling a Phone Number failed", activityException));
        }
    }

    //use native functionality to trigger a text message interface
    private void textPhoneNumber(final String phoneNumber)
    {
        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)), getActivity());
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Texting a Phone Number failed", activityException));
        }
    }

    private void goToHelpCenter(String helpNodeId)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.HELP_NODE_ID, helpNodeId);
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.HELP, arguments));
    }

//Event Subscription and Handling

    @Subscribe
    public void onReceiveBookingDetailsSuccess(HandyEvent.ReceiveBookingDetailsSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mAssociatedBooking = event.booking;
        final BookingStatus bookingStatus = mAssociatedBooking.inferBookingStatus(getLoggedInUserId());
        if (!mFromPaymentsTab && bookingStatus == BookingStatus.UNAVAILABLE)
        {
            final Bundle arguments = new Bundle();
            arguments.putString(BundleKeys.MESSAGE, getString(R.string.job_no_longer_available));
            arguments.putBundle(BundleKeys.EXTRAS, getArguments());
            returnToTab(MainViewTab.AVAILABLE_JOBS, 0, TransitionStyle.REFRESH_TAB, arguments);
        }
        else
        {
            updateDisplayForBooking(event.booking);
        }
    }

    @Subscribe
    public void onReceiveBookingDetailsError(HandyEvent.ReceiveBookingDetailsError event)
    {
        handleBookingDetailsError(event.error.getMessage());
    }

    @Subscribe
    public void onReceiveClaimJobSuccess(final HandyEvent.ReceiveClaimJobSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        BookingClaimDetails bookingClaimDetails = event.bookingClaimDetails;
        bus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ClaimSuccess(bookingClaimDetails.getBooking(), mSource, mSourceExtras, 0.0f)));

        if (bookingClaimDetails.getBooking().isClaimedByMe() || bookingClaimDetails.getBooking().getProviderId().equals(getLoggedInUserId()))
        {
            if (bookingClaimDetails.shouldShowClaimTarget())
            {
                BookingClaimDetails.ClaimTargetInfo claimTargetInfo = bookingClaimDetails.getClaimTargetInfo();
                ClaimTargetDialogFragment claimTargetDialogFragment = new ClaimTargetDialogFragment();
                claimTargetDialogFragment.setDisplayData(claimTargetInfo); //wrong way to pass argument to a fragment
                claimTargetDialogFragment.show(getFragmentManager(), ClaimTargetDialogFragment.FRAGMENT_TAG);

                returnToTab(MainViewTab.SCHEDULED_JOBS, bookingClaimDetails.getBooking().getStartDate().getTime(), null);
            }
            else
            {
                TransitionStyle transitionStyle = (bookingClaimDetails.getBooking().isRecurring() ? TransitionStyle.SERIES_CLAIM_SUCCESS : TransitionStyle.JOB_CLAIM_SUCCESS);
                returnToTab(MainViewTab.SCHEDULED_JOBS, bookingClaimDetails.getBooking().getStartDate().getTime(), transitionStyle);
            }
        }
        else
        {
            //Something has gone very wrong, the claim came back as success but the data shows not claimed, show a generic error and return to date based on original associated booking
            handleBookingClaimError(getString(R.string.job_claim_error), R.string.job_claim_error_generic, R.string.return_to_available_jobs, mAssociatedBooking.getStartDate());
        }
    }

    @Subscribe
    public void onReceiveClaimJobError(final HandyEvent.ReceiveClaimJobError event)
    {
        bus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ClaimError(event.getBooking(), mSource, mSourceExtras, 0.0f, event.error.getMessage())));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        handleBookingClaimError(event.error.getMessage());
    }

    @Subscribe
    public void onReceiveRemoveJobSuccess(final HandyEvent.ReceiveRemoveJobSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (!event.booking.isClaimedByMe() || event.booking.getProviderId().equals(Booking.NO_PROVIDER_ASSIGNED))
        {
            //TODO: can't currently remove series using portal endpoint so only removing the single job
            TransitionStyle transitionStyle = TransitionStyle.JOB_REMOVE_SUCCESS;
            returnToTab(MainViewTab.SCHEDULED_JOBS, event.booking.getStartDate().getTime(), transitionStyle);
        }
        else
        {
            //Something has gone very wrong, show a generic error and return to date based on original associated booking
            handleBookingRemoveError(getString(R.string.job_remove_error), R.string.job_remove_error_generic,
                    R.string.return_to_schedule, mAssociatedBooking.getStartDate());
            bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobError(mAssociatedBooking)));

        }
    }

    @Subscribe
    public void onReceiveRemoveJobError(final HandyEvent.ReceiveRemoveJobError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobError(mAssociatedBooking)));
        handleBookingRemoveError(event);
    }

    @Subscribe
    public void onReceiveNotifyJobOnMyWaySuccess(final HandyEvent.ReceiveNotifyJobOnMyWaySuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //refresh the page with the new booking
        mAssociatedBooking = event.booking;
        updateDisplayForBooking(event.booking);

        showToast(R.string.omw_success, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveNotifyJobOnMyWayError(final HandyEvent.ReceiveNotifyJobOnMyWayError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        handleNotifyOnMyWayError(event);
    }

    @Subscribe
    public void onReceiveNotifyJobCheckInSuccess(final HandyEvent.ReceiveNotifyJobCheckInSuccess event)
    {
        if (!event.isAuto)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

            //refresh the page with the new booking
            mAssociatedBooking = event.booking;
            updateDisplayForBooking(event.booking);

            if (mAssociatedBooking.getCustomerPreferences() != null)
            {
                showToast(R.string.read_customer_preferences, Toast.LENGTH_LONG, Gravity.TOP);
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
            else
            {
                showToast(R.string.check_in_success, Toast.LENGTH_LONG);
            }
        }
    }

    @Subscribe
    public void onReceiveNotifyJobCheckInError(final HandyEvent.ReceiveNotifyJobCheckInError event)
    {
        if (!event.isAuto)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            handleNotifyCheckInError(event);
        }
    }

    @Subscribe
    public void onReceiveNotifyJobUpdateArrivalTimeSuccess(final HandyEvent.ReceiveNotifyJobUpdateArrivalTimeSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //refresh the page with the new booking
        mAssociatedBooking = event.booking;
        updateDisplayForBooking(event.booking);

        showToast(R.string.eta_success, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveNotifyJobUpdateArrivalTimeError(final HandyEvent.ReceiveNotifyJobUpdateArrivalTimeError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        handleNotifyUpdateArrivalError(event);
    }

    @Subscribe
    public void onReceiveReportNoShowSuccess(HandyEvent.ReceiveReportNoShowSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mAssociatedBooking = event.booking;
        updateDisplayForBooking(event.booking);
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.thanks_for_reporting)
                .setMessage(R.string.customer_no_show_recorded)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }

    @Subscribe
    public void onReceiveReportNoShowError(HandyEvent.ReceiveReportNoShowError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.unable_to_report_no_show, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveCancelNoShowSuccess(HandyEvent.ReceiveCancelNoShowSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mAssociatedBooking = event.booking;
        updateDisplayForBooking(event.booking);
        showToast(R.string.customer_no_show_cancelled, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveCancelNoShowError(HandyEvent.ReceiveCancelNoShowError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.unable_to_cancel_no_show, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onSupportActionTriggered(HandyEvent.SupportActionTriggered event)
    {
        SupportActionType supportActionType = SupportActionUtils.getSupportActionType(event.action);
        String bookingId = mAssociatedBooking.getId();
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.HelpItemSelected(bookingId, event.action.getActionName())));

        switch (supportActionType)
        {
            case NOTIFY_EARLY:
                showUpdateArrivalTimeDialog(mAssociatedBooking, R.string.notify_customer_of_earliness, Booking.ArrivalTimeOption.earlyValues());
                break;
            case NOTIFY_LATE:
                showUpdateArrivalTimeDialog(mAssociatedBooking, R.string.notify_customer_of_lateness, Booking.ArrivalTimeOption.lateValues());
                break;
            case REPORT_NO_SHOW:
                showCustomerNoShowDialog(event.action);
                break;
            case ISSUE_UNSAFE:
            case ISSUE_HOURS:
            case ISSUE_OTHER:
            case RESCHEDULE:
            case CANCELLATION_POLICY:
                goToHelpCenter(event.action.getDeepLinkData());
                break;
            case REMOVE:
                removeJob(event.action);
                break;
            case UNASSIGN_FLOW:
                unassignJob(event.action);
                break;
        }
    }

    private void removeJob(@NonNull Booking.Action removeAction)
    {
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobClicked(mAssociatedBooking, removeAction.getWarningText())));

        String bookingId = mAssociatedBooking.getId();
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveConfirmationShown(bookingId, ScheduledJobsLog.RemoveConfirmationShown.POPUP)));
        takeAction(BookingActionButtonType.REMOVE, false);

    }

    private void unassignJob(@NonNull Booking.Action removeAction)
    {
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobClicked(mAssociatedBooking, removeAction.getWarningText())));

        String bookingId = mAssociatedBooking.getId();
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveConfirmationShown(bookingId, ScheduledJobsLog.RemoveConfirmationShown.REASON_FLOW)));
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, mAssociatedBooking);
        arguments.putSerializable(BundleKeys.BOOKING_ACTION, removeAction);
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.CANCELLATION_REQUEST, arguments));
    }

    private void returnToTab(MainViewTab targetTab, long epochTime, TransitionStyle transitionStyle)
    {
        returnToTab(targetTab, epochTime, transitionStyle, null);
    }

    private void returnToTab(MainViewTab targetTab, long epochTime, TransitionStyle transitionStyle, Bundle additionalArguments)
    {
        //Return to available jobs with success
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        if (additionalArguments != null)
        {
            arguments.putAll(additionalArguments);
        }
        bus.post(new NavigationEvent.NavigateToTab(targetTab, arguments, transitionStyle));
    }

//Handle Action Response Errors

    private void handleBookingClaimError(String errorMessage)
    {
        handleBookingClaimError(errorMessage, R.string.job_claim_error, R.string.return_to_available_jobs, mAssociatedBooking.getStartDate());
    }

    private void handleBookingClaimError(String errorMessage, int titleId, int option1Id, Date returnDate)
    {
        handleBookingClaimError(errorMessage, getString(titleId), getString(option1Id), returnDate);
    }

    private void handleBookingClaimError(String errorMessage, String title, String option1, Date returnDate)
    {
        if (errorMessage != null)
        {
            bus.post(new HandyEvent.ClaimJobError(errorMessage));
            showErrorDialogReturnToAvailable(errorMessage, title, option1, returnDate.getTime());
        }
        else
        {
            showNetworkErrorToast();
        }
    }

    private void handleBookingRemoveError(final HandyEvent.ReceiveRemoveJobError event)
    {
        handleBookingRemoveError(event.error.getMessage(), R.string.job_remove_error, R.string.return_to_schedule, mAssociatedBooking.getStartDate());
    }

    private void handleBookingRemoveError(String errorMessage, int titleId, int option1Id, Date returnDate)
    {
        handleBookingRemoveError(errorMessage, getString(titleId), getString(option1Id), returnDate);
    }

    private void handleBookingRemoveError(String errorMessage, String title, String option1, Date returnDate)
    {
        if (errorMessage != null)
        {
            bus.post(new HandyEvent.RemoveJobError(errorMessage));
            showErrorDialogReturnToAvailable(errorMessage, title, option1, returnDate.getTime());
        }
        else
        {
            showNetworkErrorToast();
        }
    }

    private void showErrorDialogReturnToAvailable(String errorMessage, String title, String option1, final long returnDateEpochTime)
    {
        //specific booking error, show an alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        final Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, returnDateEpochTime);

        // set dialog message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton(option1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.SCHEDULED_JOBS, arguments, TransitionStyle.REFRESH_TAB));
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void showNetworkErrorToast()
    {
        //generic network problem
        //show a toast about connectivity issues
        showToast(R.string.error_connectivity, Toast.LENGTH_LONG);
    }

    private void handleNotifyOnMyWayError(final HandyEvent.ReceiveNotifyJobOnMyWayError event)
    {
        handleCheckInFlowError(event.error.getMessage());
    }

    private void handleNotifyCheckInError(final HandyEvent.ReceiveNotifyJobCheckInError event)
    {
        handleCheckInFlowError(event.error.getMessage());
    }

    private void handleNotifyUpdateArrivalError(final HandyEvent.ReceiveNotifyJobUpdateArrivalTimeError event)
    {
        handleCheckInFlowError(event.error.getMessage());
    }

    private void handleCheckInFlowError(String errorMessage)
    {
        if (errorMessage != null)
        {
            showToast(errorMessage);
        }
        else
        {
            showNetworkErrorToast();
        }
    }

    //if we had problems retrieving the booking show a toast and return to available bookings
    private void handleBookingDetailsError(String errorMessage)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (errorMessage != null)
        {
            showToast(errorMessage, Toast.LENGTH_LONG);
            //don't have a day to return to just return to time zero, first day in list
            returnToTab(MainViewTab.AVAILABLE_JOBS, 0, TransitionStyle.REFRESH_TAB);
        }
        else
        {
            mErrorText.setText(R.string.error_fetching_connectivity_issue);
            //allow try again
            fetchErrorView.setVisibility(View.VISIBLE);
        }

    }
}
