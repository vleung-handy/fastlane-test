package com.handy.portal.ui.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;
import com.handy.portal.model.LocationData;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.constructor.BookingDetailsActionContactPanelViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsActionPanelViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsActionRemovePanelViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsBannerViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsDateViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsJobInstructionsViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsLocationPanelViewConstructor;
import com.handy.portal.ui.constructor.BookingDetailsViewConstructor;
import com.handy.portal.ui.constructor.GoogleMapViewConstructor;
import com.handy.portal.ui.constructor.MapPlaceholderViewConstructor;
import com.handy.portal.ui.constructor.SupportActionContainerViewConstructor;
import com.handy.portal.ui.layout.SlideUpPanelContainer;
import com.handy.portal.ui.widget.BookingActionButton;
import com.handy.portal.util.SupportActionUtils;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.handy.portal.ui.fragment.ComplementaryBookingsFragment.COMPLEMENTARY_JOBS_SOURCE_NAME;

public class BookingDetailsFragment extends InjectedFragment
{
    //Layouts points for fragment, the various elements are childed to these
    @InjectView(R.id.booking_details_layout)
    protected LinearLayout detailsParentLayout;

    @InjectView(R.id.booking_details_banner_layout)
    protected LinearLayout bannerLayout;

    @InjectView(R.id.booking_details_map_layout)
    protected LinearLayout mapLayout;

    @InjectView(R.id.booking_details_date_layout)
    protected LinearLayout dateLayout;

    @InjectView(R.id.booking_details_location_layout)
    protected RelativeLayout locationLayout;

    @InjectView(R.id.booking_details_action_layout)
    protected RelativeLayout actionLayout;

    @InjectView(R.id.booking_details_contact_layout)
    protected RelativeLayout contactLayout;

    @InjectView(R.id.booking_details_job_instructions_layout)
    protected LinearLayout jobInstructionsLayout;

    @InjectView(R.id.booking_details_remove_job_layout)
    protected LinearLayout removeJobLayout;

    @InjectView(R.id.booking_details_full_details_notice_text)
    protected TextView fullDetailsNoticeText;

    @InjectView(R.id.fetch_error_view)
    protected View fetchErrorView;

    @InjectView(R.id.fetch_error_text)
    protected TextView errorText;

    @InjectView(R.id.slide_up_panel_container)
    protected SlideUpPanelContainer slideUpPanelContainer;

    @Inject
    PrefsManager prefsManager;

    private String requestedBookingId;
    private Booking associatedBooking; //used to return to correct date on jobs tab if a job action fails and the returned booking is null
    private Date associatedBookingDate;

    private static String GOOGLE_PLAY_SERVICES_INSTALL_URL = "https://play.google.com/store/apps/details?id=com.google.android.gms";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_booking_detail, null);

        ButterKnife.inject(this, view);

        if (validateRequiredArguments())
        {
            Bundle arguments = getArguments();
            this.requestedBookingId = arguments.getString(BundleKeys.BOOKING_ID);

            if (arguments.containsKey(BundleKeys.BOOKING_DATE))
            {
                long bookingDateLong = arguments.getLong(BundleKeys.BOOKING_DATE, 0L);
                this.associatedBookingDate = new Date(bookingDateLong);
            }

            requestBookingDetails(this.requestedBookingId, this.associatedBookingDate);
        }
        else
        {
            showToast(R.string.an_error_has_occurred);
            returnToTab(MainViewTab.AVAILABLE_JOBS, 0, TransitionStyle.REFRESH_TAB);
        }

        return view;
    }

    @Override
    protected List<String> requiredArguments()
    {
        return Lists.newArrayList(BundleKeys.BOOKING_ID);
    }

    private String getLoggedInUserId()
    {
        return prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }

    private void requestBookingDetails(String bookingId, Date bookingDate)
    {
        fetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestBookingDetails(bookingId, bookingDate));
    }

//Display Creation / Updating

    @VisibleForTesting
    protected void updateDisplayForBooking(Booking booking)
    {
        //clear existing elements out of our fragment's display
        clearLayouts();
        constructBookingDisplayElements(booking);
        createAllowedActionButtons(booking);

        //I do not like having these button linkages here, strongly considering having buttons generate events we listen for so the fragment doesn't init them
        initBackButton();
        initCancelNoShowButton();
        initMapsPlaceHolderButton();
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

        //banner layout is not under the details layout as it sticks to the top
        bannerLayout.removeAllViews();
    }

    //Use view constructors on layouts to generate the elements inside the layouts, we do not currently maintain a linkage to the resulting view
    private void constructBookingDisplayElements(Booking booking)
    {
        //Construct the views for each layout
        Map<ViewGroup, BookingDetailsViewConstructor> viewConstructors = getViewConstructorsForLayouts(booking);
        for (Map.Entry<ViewGroup, BookingDetailsViewConstructor> viewConstructorEntry : viewConstructors.entrySet())
        {
            ViewGroup layout = viewConstructorEntry.getKey();
            BookingDetailsViewConstructor constructor = viewConstructorEntry.getValue();
            constructor.create(layout, booking);
        }

        //Full Details Notice , technically we should move this to its own view panel
        BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());
        fullDetailsNoticeText.setVisibility(booking.getServiceInfo().isHomeCleaning() && bookingStatus == BookingStatus.AVAILABLE ? View.VISIBLE : View.GONE);
    }

    //A listing of all the view constructors we use to populate the layouts
    //We don't maintain references to these constructors / the view, we always create anew from a booking
    private Map<ViewGroup, BookingDetailsViewConstructor> getViewConstructorsForLayouts(Booking booking)
    {
        BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING_STATUS, bookingStatus);

        Map<ViewGroup, BookingDetailsViewConstructor> viewConstructors = new HashMap<>();
        viewConstructors.put(bannerLayout, new BookingDetailsBannerViewConstructor(getActivity(), arguments));

        //show either the real map or a placeholder image depending on if we have google play services
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()))
        {
            viewConstructors.put(mapLayout, new GoogleMapViewConstructor(getActivity(), arguments));
        }
        else
        {
            viewConstructors.put(mapLayout, new MapPlaceholderViewConstructor(getActivity(), arguments));
        }

        viewConstructors.put(dateLayout, new BookingDetailsDateViewConstructor(getActivity(), arguments));
        viewConstructors.put(locationLayout, new BookingDetailsLocationPanelViewConstructor(getActivity(), arguments));
        viewConstructors.put(actionLayout, new BookingDetailsActionPanelViewConstructor(getActivity(), arguments));
        viewConstructors.put(contactLayout, new BookingDetailsActionContactPanelViewConstructor(getActivity(), arguments));
        viewConstructors.put(jobInstructionsLayout, new BookingDetailsJobInstructionsViewConstructor(getActivity(), arguments));
        viewConstructors.put(removeJobLayout, new BookingDetailsActionRemovePanelViewConstructor(getActivity(), arguments));
        return viewConstructors;
    }

    @OnClick(R.id.try_again_button)
    public void onClickRequestDetails()
    {
        requestBookingDetails(this.requestedBookingId, this.associatedBookingDate);
    }

    //Can not use @onclick b/c the button does not exist at injection time
    //TODO: Figure out better way to link click listeners sections
    private void initBackButton()
    {
        ImageButton backButton = (ImageButton) bannerLayout.findViewById(R.id.booking_details_back_button);
        if (backButton != null)
        {
            backButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getActivity().onBackPressed();
                }
            });
        }
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

    //Can not use @onclick b/c the button does not exist at injection time
    //TODO: Figure out better way to link click listeners sections
    private void initMapsPlaceHolderButton()
    {
        Button mapsInstallButton = (Button) mapLayout.findViewById(R.id.map_placeholder_install_button);
        //will fail if we didn't use the placeholder version
        if (mapsInstallButton != null)
        {
            mapsInstallButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_SERVICES_INSTALL_URL));
                    startActivity(browserIntent);
                }
            });
        }
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
                requestNotifyCheckOutJob(this.associatedBooking.getId(), locationData);
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
        slideUpPanelContainer.showPanel(R.string.on_the_job_support, new SlideUpPanelContainer.ContentInitializer()
        {
            @Override
            public void initialize(ViewGroup panel)
            {
                new SupportActionContainerViewConstructor(getActivity(), SupportActionUtils.ETA_ACTION_NAMES)
                        .create(panel, associatedBooking);
                new SupportActionContainerViewConstructor(getActivity(), SupportActionUtils.ISSUE_ACTION_NAMES)
                        .create(panel, associatedBooking);
            }
        });
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
    private void showBookingActionWarningDialog(String warning, final BookingActionButtonType actionType)
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
    }

    private void requestNotifyCheckInJob(String bookingId, LocationData locationData)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobCheckIn(bookingId, locationData));
    }

    private void requestNotifyCheckOutJob(String bookingId, LocationData locationData)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobCheckOut(bookingId, locationData));
    }

    private void requestNotifyUpdateArrivalTime(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption)
    {
        slideUpPanelContainer.hidePanel();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobUpdateArrivalTime(bookingId, arrivalTimeOption));
    }

    private void requestReportNoShow()
    {
        slideUpPanelContainer.hidePanel();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestReportNoShow(associatedBooking.getId(), getLocationData()));
    }

    private void requestCancelNoShow()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestCancelNoShow(associatedBooking.getId(), getLocationData()));
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
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)));
        } catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Calling a Phone Number failed", activityException));
        }
    }

    //use native functionality to trigger a text message interface
    private void textPhoneNumber(final String phoneNumber)
    {
        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
        } catch (ActivityNotFoundException activityException)
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
        if (event.booking.getProviderId().equals(getLoggedInUserId()))
        {
            MainViewTab targetTab = event.source != null && event.source.equals(COMPLEMENTARY_JOBS_SOURCE_NAME) ? MainViewTab.SCHEDULED_JOBS : MainViewTab.AVAILABLE_JOBS;
            TransitionStyle transitionStyle = (event.booking.isRecurring() ? TransitionStyle.SERIES_CLAIM_SUCCESS : TransitionStyle.JOB_CLAIM_SUCCESS);
            returnToTab(targetTab, event.booking.getStartDate().getTime(), transitionStyle);
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
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        handleBookingClaimError(event.error.getMessage());
    }

    @Subscribe
    public void onReceiveRemoveJobSuccess(final HandyEvent.ReceiveRemoveJobSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        if (event.booking.getProviderId().equals(Booking.NO_PROVIDER_ASSIGNED))
        {
            //TODO: can't currently remove series using portal endpoint so only removing the single job
            TransitionStyle transitionStyle = TransitionStyle.JOB_REMOVE_SUCCESS;
            returnToTab(MainViewTab.SCHEDULED_JOBS, event.booking.getStartDate().getTime(), transitionStyle);
        }
        else
        {
            //Something has gone very wrong, show a generic error and return to date based on original associated booking
            handleBookingRemoveError(getString(R.string.job_remove_error), R.string.job_remove_error_generic, R.string.return_to_schedule, this.associatedBooking.getStartDate());
        }
    }

    @Subscribe
    public void onReceiveRemoveJobError(final HandyEvent.ReceiveRemoveJobError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
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
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //refresh the page with the new booking
        this.associatedBooking = event.booking;
        updateDisplayForBooking(event.booking);

        showToast(R.string.check_in_success, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onReceiveNotifyJobCheckInError(final HandyEvent.ReceiveNotifyJobCheckInError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        handleNotifyCheckInError(event);
    }

    @Subscribe
    public void onNotifyCheckOutJobRequestReceived(final HandyEvent.ReceiveNotifyJobCheckoutSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //return to schedule page
        returnToTab(MainViewTab.SCHEDULED_JOBS, this.associatedBooking.getStartDate().getTime(), TransitionStyle.REFRESH_TAB);

        showToast(R.string.check_out_success, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onNotifyCheckOutJobError(final HandyEvent.ReceiveNotifyJobCheckoutError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        handleNotifyCheckOutError(event);
    }

    @Subscribe
    public void onNotifyUpdateArrivalRequestReceived(final HandyEvent.ReceiveNotifyJobUpdateArrivalTimeSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //refresh the page with the new booking
        this.associatedBooking = event.booking;
        updateDisplayForBooking(event.booking);

        showToast(R.string.eta_success, Toast.LENGTH_LONG);
    }

    @Subscribe
    public void onNotifyUpdateArrivalError(final HandyEvent.ReceiveNotifyJobUpdateArrivalTimeError event)
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

    private void handleNotifyCheckOutError(final HandyEvent.ReceiveNotifyJobCheckoutError event)
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
