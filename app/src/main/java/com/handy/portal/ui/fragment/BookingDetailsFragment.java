package com.handy.portal.ui.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.SupportActionType;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.constant.WarningButtonsText;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ZipClusterManager;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;
import com.handy.portal.model.Booking.BookingType;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.CheckoutRequest;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.ProBookingFeedback;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.constructor.BookingDetailsActionContactPanelViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsActionPanelViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsActionRemovePanelViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsDateViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsJobInstructionsViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsLocationPanelViewConstructor;
import com.handy.portal.ui.element.SupportActionContainerView;
import com.handy.portal.ui.element.bookings.ProxyLocationView;
import com.handy.portal.ui.fragment.dialog.ClaimTargetDialogFragment;
import com.handy.portal.ui.fragment.dialog.RateBookingDialogFragment;
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
    @Bind(R.id.booking_details_layout)
    LinearLayout detailsParentLayout;
    @Bind(R.id.booking_details_map_layout)
    ViewGroup mapLayout;
    @Bind(R.id.booking_details_date_layout)
    LinearLayout dateLayout;
    @Bind(R.id.booking_details_title_layout)
    RelativeLayout titleLayout;
    @Bind(R.id.booking_details_action_layout)
    RelativeLayout actionLayout;
    @Bind(R.id.booking_details_contact_layout)
    RelativeLayout contactLayout;
    @Bind(R.id.booking_details_job_instructions_layout)
    LinearLayout jobInstructionsLayout;
    @Bind(R.id.booking_details_location_layout)
    ViewGroup locationLayout;
    @Bind(R.id.booking_details_remove_job_layout)
    LinearLayout removeJobLayout;
    @Bind(R.id.booking_details_full_details_notice_text)
    TextView fullDetailsNoticeText;
    @Bind(R.id.fetch_error_view)
    View fetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView errorText;
    @Bind(R.id.slide_up_panel_container)
    SlideUpPanelLayout mSlideUpPanelLayout;

    @Bind(R.id.booking_details_scroll_view)
    ScrollView mScrollView;

    @Inject
    PrefsManager prefsManager;
    @Inject
    ZipClusterManager mZipClusterManager;
    @Inject
    ConfigManager mConfigManager;

    private String requestedBookingId;
    private BookingType requestedBookingType;
    private Booking associatedBooking; //used to return to correct date on jobs tab if a job action fails and the returned booking is null
    private Date associatedBookingDate;
    private boolean mIsForPayments;
    private MainViewTab currentTab;

    private static final String BOOKING_PROXY_ID_PREFIX = "P";

    private boolean mHaveTrackedSeenBookingInstructions;

    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    private static final float TRACK_JOB_INSTRUCTIONS_SEEN_PERCENT_VIEW_THRESHOLD = 0.5f; //50% of booking instructions view visible on screen


    @Override
    protected MainViewTab getTab()
    {
        return currentTab;
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
            this.requestedBookingId = arguments.getString(BundleKeys.BOOKING_ID);
            this.requestedBookingType = BookingType.valueOf(arguments.getString(BundleKeys.BOOKING_TYPE));
            this.currentTab = (MainViewTab) arguments.getSerializable(BundleKeys.TAB);

            this.mIsForPayments = arguments.getBoolean(BundleKeys.IS_FOR_PAYMENTS, false);

            if (arguments.containsKey(BundleKeys.BOOKING_DATE))
            {
                long bookingDateLong = arguments.getLong(BundleKeys.BOOKING_DATE, 0L);
                this.associatedBookingDate = new Date(bookingDateLong);
            }
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
                    float percentVis = UIUtils.getPercentViewVisibleInScrollView(jobInstructionsLayout, mScrollView);
                    if(percentVis >= TRACK_JOB_INSTRUCTIONS_SEEN_PERCENT_VIEW_THRESHOLD)
                    {
                        //flip flag so we don't spam this event
                        mHaveTrackedSeenBookingInstructions = true; //not guaranteed to stay flipped throughout session just on screen
                        //track event
                        bus.post(new LogEvent.AddLogEvent(
                                mEventLogFactory.createBookingInstructionsSeenLog(associatedBooking)));
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
        if (associatedBooking != null)
        {
            int titleStringId = 0;
            String bookingIdPrefix = associatedBooking.isProxy() ? BOOKING_PROXY_ID_PREFIX : "";
            String jobLabel = getActivity().getString(R.string.job_num) + bookingIdPrefix + associatedBooking.getId();

            if (this.mIsForPayments)
            {
                titleStringId = R.string.previous_job;
                menu.findItem(R.id.action_job_label).setTitle(jobLabel);
            }
            else
            {
                Booking.BookingStatus bookingStatus = associatedBooking.inferBookingStatus(getLoggedInUserId());
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
            requestBookingDetails(this.requestedBookingId, this.requestedBookingType, this.associatedBookingDate);
        }
    }

    @Override
    protected List<String> requiredArguments()
    {
        return Lists.newArrayList(BundleKeys.BOOKING_ID, BundleKeys.BOOKING_TYPE);
    }

    private String getLoggedInUserId()
    {
        return prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
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
        //clear existing elements out of our fragment's display
        clearLayouts();
        constructBookingDisplayElements(booking);
        invalidateOptionsMenu();
        //TODO: This is confusing. We should create the buttons in constructBookingDisplayElements()
        createAllowedActionButtons(booking);

        //I do not like having these button linkages here, strongly considering having buttons generate events we listen for so the fragment doesn't init them
        initCancelNoShowButton();
    }

    //We use view constructors instead of views so to clear the views just remove all children of layouts
    private void clearLayouts()
    {
        for (int i = 0; i < detailsParentLayout.getChildCount(); i++)
        {
            ViewGroup vg = (ViewGroup) detailsParentLayout.getChildAt(i);
            if (vg != null)
            {
                vg.removeAllViews();
            }
        }
    }

    //Use view constructors on layouts to generate the elements inside the layouts, we do not currently maintain a linkage to the resulting view
    private void constructBookingDisplayElements(Booking booking)
    {
        //Construct the views for each layout
        setLayouts(booking);

        //Full Details Notice , technically we should move this to its own view panel
        BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());
        fullDetailsNoticeText.setVisibility(!booking.isProxy() && booking.getServiceInfo().isHomeCleaning()
                && bookingStatus == BookingStatus.AVAILABLE ? View.VISIBLE : View.GONE);
    }

    private void setLayouts(Booking booking)
    {
        BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING_STATUS, bookingStatus);
        arguments.putBoolean(BundleKeys.IS_FOR_PAYMENTS, mIsForPayments);


        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        if (mIsForPayments)
        {
            mapLayout.setVisibility(View.GONE);
        }
        else
        {
            //show either the real map or a placeholder image depending on if we have google play services
            if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()))
            {
                BookingMapFragment fragment = BookingMapFragment.newInstance(associatedBooking,
                        bookingStatus, mZipClusterManager.getCachedPolygons(associatedBooking.getZipClusterId()));
                transaction.replace(mapLayout.getId(), fragment).commit();
            }
            else
            {
                UIUtils.replaceView(mapLayout, new MapPlaceholderView(getContext()));
            }
        }

        new BookingDetailsDateViewConstructor(getActivity(), arguments).create(dateLayout, booking);
        new BookingDetailsLocationPanelViewConstructor(getActivity(), arguments).create(titleLayout, booking);
        if (booking.isProxy())
        {
            UIUtils.replaceView(locationLayout, new ProxyLocationView(getContext(), booking.getZipCluster()));
        }
        new BookingDetailsJobInstructionsViewConstructor(getActivity(), arguments).create(jobInstructionsLayout, booking);

        if (!this.mIsForPayments)
        {
            new BookingDetailsActionPanelViewConstructor(getActivity(), arguments).create(actionLayout, booking);
            new BookingDetailsActionContactPanelViewConstructor(getActivity(), arguments).create(contactLayout, booking);
            new BookingDetailsActionRemovePanelViewConstructor(getActivity(), arguments).create(removeJobLayout, booking);
        }
    }

    @OnClick(R.id.try_again_button)
    public void onClickRequestDetails()
    {
        requestBookingDetails(this.requestedBookingId, this.requestedBookingType, this.associatedBookingDate);
    }

    private void initCancelNoShowButton()
    {
        ViewGroup cancelNoShowButton = (ViewGroup) actionLayout.findViewById(R.id.cancel_no_show_button);
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
        for (Booking.Action action : associatedBooking.getAllowedActions())
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
            ViewGroup buttonParentLayout = getParentLayoutForButtonActionType(UIUtils.getAssociatedActionType(action));

            if (buttonParentLayout == null)
            {
                Crashlytics.log("Could not find parent layout for " + UIUtils.getAssociatedActionType(action).getActionName());
            }
            else
            {
                int newChildIndex = buttonParentLayout.getChildCount(); //new index is equal to the old count since the new count is +1
                BookingActionButton bookingActionButton = (BookingActionButton)
                        ((ViewGroup) getActivity().getLayoutInflater().inflate(UIUtils.getAssociatedActionType(action).getLayoutTemplateId(), buttonParentLayout)).getChildAt(newChildIndex);
                bookingActionButton.init(booking, this, action); //not sure if this is the better way or to have buttons dispatch specific events the fragment catches, for now this will suffice
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
            case HELP:
            {
                return (ViewGroup) actionLayout.findViewById(R.id.booking_details_action_panel_button_layout);
            }

            case CONTACT_PHONE:
            {
                return (ViewGroup) contactLayout.findViewById(R.id.booking_details_contact_action_button_layout_slot_1);
            }

            case CONTACT_TEXT:
            {
                return (ViewGroup) contactLayout.findViewById(R.id.booking_details_contact_action_button_layout_slot_2);
            }

            case REMOVE:
            {
                return (ViewGroup) removeJobLayout.findViewById(R.id.booking_details_action_panel_button_layout);
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
                requestClaimJob(this.associatedBooking);
            }
            break;

            case ON_MY_WAY:
            {
                requestNotifyOnMyWayJob(this.associatedBooking.getId(), locationData);
            }
            break;

            case CHECK_IN:
            {
                requestNotifyCheckInJob(this.associatedBooking.getId(), locationData);
            }
            break;

            case HELP:
            {
                showHelpOptions();
            }
            break;

            case CHECK_OUT:
            {
                boolean showCheckoutRatingFlow = false;
                if (mConfigManager.getConfigurationResponse() != null)
                {
                    showCheckoutRatingFlow = mConfigManager.getConfigurationResponse().isCheckoutRatingFlowEnabled();
                }

                if (showCheckoutRatingFlow)
                {
                    RateBookingDialogFragment rateBookingDialogFragment = new RateBookingDialogFragment();
                    Bundle arguments = new Bundle();
                    arguments.putSerializable(BundleKeys.BOOKING, this.associatedBooking);
                    rateBookingDialogFragment.setArguments(arguments);
                    rateBookingDialogFragment.show(getFragmentManager(), RateBookingDialogFragment.FRAGMENT_TAG);
                }
                else
                {
                    CheckoutRequest checkoutRequest = new CheckoutRequest(locationData, new ProBookingFeedback(-1, ""));
                    requestNotifyCheckOutJob(this.associatedBooking.getId(), checkoutRequest, locationData);
                }
            }
            break;

            case REMOVE:
            {
                requestRemoveJob(this.associatedBooking);
            }
            break;

            case CONTACT_PHONE:
            {
                bus.post(new HandyEvent.CallCustomerClicked());
                callPhoneNumber(this.associatedBooking.getBookingPhone());
            }
            break;

            case CONTACT_TEXT:
            {
                bus.post(new HandyEvent.TextCustomerClicked());
                textPhoneNumber(this.associatedBooking.getBookingPhone());
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
        //TODO: Ugly defensive programming against bad timing on butterknife, root issue still there
        if (mSlideUpPanelLayout != null)
        {
            LinearLayout layout = UIUtils.createLinearLayout(getContext(), LinearLayout.VERTICAL);
            layout.addView(new SupportActionContainerView(
                    getContext(), SupportActionUtils.ETA_ACTION_NAMES, associatedBooking));
            layout.addView(new SupportActionContainerView(
                    getContext(), SupportActionUtils.ISSUE_ACTION_NAMES, associatedBooking));
            mSlideUpPanelLayout.showPanel(R.string.on_the_job_support, layout);
        }
    }

    //Check if the current booking data for a given action type has an associated warning to display
    private boolean checkShowWarningDialog(BookingActionButtonType actionType)
    {
        boolean showingWarningDialog = false;

        List<Booking.Action> allowedActions = this.associatedBooking.getAllowedActions();

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
        bus.post(new LogEvent.AddLogEvent(
                mEventLogFactory.createRemoveJobClickedLog(associatedBooking, warning)));
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
                                bus.post(new LogEvent.AddLogEvent(mEventLogFactory
                                        .createRemoveJobConfirmedLog(associatedBooking, warning)));

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
    //
    private void requestClaimJob(Booking booking)
    {
        String source = "";
        if (getArguments().containsKey(BundleKeys.BOOKING_SOURCE))
        {
            source = getArguments().getString(BundleKeys.BOOKING_SOURCE, "");
        }

        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestClaimJob(booking, source));
    }

    private void requestRemoveJob(Booking booking)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestRemoveJob(booking));
    }

    private void requestNotifyOnMyWayJob(String bookingId, LocationData locationData)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobOnMyWay(bookingId, locationData));
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createOnMyWayLog(associatedBooking, locationData)));
    }

    private void requestNotifyCheckInJob(String bookingId, LocationData locationData)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobCheckIn(bookingId, locationData));
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createCheckInLog(associatedBooking, locationData)));
    }

    private void requestNotifyUpdateArrivalTime(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption)
    {
        //TODO: Ugly defensive programming against bad timing on butterknife, root issue still there
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
        //TODO: Ugly defensive programming against bad timing on butterknife, root issue still there
        if (mSlideUpPanelLayout != null)
        {
            mSlideUpPanelLayout.hidePanel();
        }
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestReportNoShow(associatedBooking.getId(), getLocationData()));
    }

    private void requestCancelNoShow()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestCancelNoShow(associatedBooking.getId(), getLocationData()));
    }

    private void requestNotifyCheckOutJob(String bookingId, CheckoutRequest checkoutRequest, LocationData locationData)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobCheckOut(bookingId, checkoutRequest));
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createCheckOutLog(associatedBooking, locationData)));
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
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)), this.getActivity());
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
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)), this.getActivity());
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
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.HELP, arguments));
    }

//Event Subscription and Handling

    @Subscribe
    public void onReceiveBookingDetailsSuccess(HandyEvent.ReceiveBookingDetailsSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        this.associatedBooking = event.booking;
        updateDisplayForBooking(event.booking);
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

        if (bookingClaimDetails.getBooking().isClaimedByMe() || bookingClaimDetails.getBooking().getProviderId().equals(getLoggedInUserId()))
        {
            bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createAvailableJobClaimSuccessLog(
                    bookingClaimDetails.getBooking(), event.source)));

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
            handleBookingClaimError(getString(R.string.job_claim_error), R.string.job_claim_error_generic, R.string.return_to_available_jobs, this.associatedBooking.getStartDate());
        }
    }

    @Subscribe
    public void onReceiveClaimJobError(final HandyEvent.ReceiveClaimJobError event)
    {
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createAvailableJobClaimErrorLog(
                event.getBooking(), event.getSource())));
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
                    R.string.return_to_schedule, this.associatedBooking.getStartDate());
            bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createRemoveJobErrorLog(associatedBooking)));

        }
    }

    @Subscribe
    public void onReceiveRemoveJobError(final HandyEvent.ReceiveRemoveJobError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createRemoveJobErrorLog(associatedBooking)));
        handleBookingRemoveError(event);
    }

    @Subscribe
    public void onReceiveNotifyJobOnMyWaySuccess(final HandyEvent.ReceiveNotifyJobOnMyWaySuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //refresh the page with the new booking
        this.associatedBooking = event.booking;
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
            this.associatedBooking = event.booking;
            updateDisplayForBooking(event.booking);

            showToast(R.string.check_in_success, Toast.LENGTH_LONG);
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
    public void onReceiveNotifyJobCheckOutSuccess(final HandyEvent.ReceiveNotifyJobCheckOutSuccess event)
    {
        if (!event.isAutoCheckIn)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

            //return to schedule page
            returnToTab(MainViewTab.SCHEDULED_JOBS, this.associatedBooking.getStartDate().getTime(), TransitionStyle.REFRESH_TAB);

            showToast(R.string.check_out_success, Toast.LENGTH_LONG);
        }
    }

    @Subscribe
    public void onReceiveNotifyJobCheckOutError(final HandyEvent.ReceiveNotifyJobCheckOutError event)
    {
        if (!event.isAuto)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            handleNotifyCheckOutError(event);
        }
    }

    @Subscribe
    public void onReceiveNotifyJobUpdateArrivalTimeSuccess(final HandyEvent.ReceiveNotifyJobUpdateArrivalTimeSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //refresh the page with the new booking
        this.associatedBooking = event.booking;
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
        this.associatedBooking = event.booking;
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
        this.associatedBooking = event.booking;
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
        switch (supportActionType)
        {
            case NOTIFY_EARLY:
                showUpdateArrivalTimeDialog(associatedBooking, R.string.notify_customer_of_earliness, Booking.ArrivalTimeOption.earlyValues());
                break;
            case NOTIFY_LATE:
                showUpdateArrivalTimeDialog(associatedBooking, R.string.notify_customer_of_lateness, Booking.ArrivalTimeOption.lateValues());
                break;
            case REPORT_NO_SHOW:
                showCustomerNoShowDialog(event.action);
                break;
            case ISSUE_UNSAFE:
            case ISSUE_HOURS:
            case ISSUE_OTHER:
                goToHelpCenter(event.action.getDeepLinkData());
                break;
            case REMOVE:
                takeAction(BookingActionButtonType.REMOVE, false);
                break;
        }
    }

    private void returnToTab(MainViewTab targetTab, long epochTime, TransitionStyle transitionStyle)
    {
        //Return to available jobs with success
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        bus.post(new HandyEvent.NavigateToTab(targetTab, arguments, transitionStyle));
    }

//Handle Action Response Errors

    private void handleBookingClaimError(String errorMessage)
    {
        handleBookingClaimError(errorMessage, R.string.job_claim_error, R.string.return_to_available_jobs, this.associatedBooking.getStartDate());
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
        handleBookingRemoveError(event.error.getMessage(), R.string.job_remove_error, R.string.return_to_schedule, this.associatedBooking.getStartDate());
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

    private void showErrorDialogReturnToAvailable(String title, String errorMessage, String option1, final long returnDateEpochTime)
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
                        bus.post(new HandyEvent.NavigateToTab(MainViewTab.SCHEDULED_JOBS, arguments, TransitionStyle.REFRESH_TAB));
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

    private void handleNotifyCheckOutError(final HandyEvent.ReceiveNotifyJobCheckOutError event)
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
            errorText.setText(R.string.error_fetching_connectivity_issue);
            //allow try again
            fetchErrorView.setVisibility(View.VISIBLE);
        }

    }
}
