package com.handy.portal.ui.fragment;

import android.app.Activity;
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
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.SupportAction;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.constant.WarningButtonsText;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;
import com.handy.portal.model.LocationData;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.element.BookingDetailsActionContactPanelViewConstructor;
import com.handy.portal.ui.element.BookingDetailsActionPanelViewConstructor;
import com.handy.portal.ui.element.BookingDetailsActionRemovePanelViewConstructor;
import com.handy.portal.ui.element.BookingDetailsBannerViewConstructor;
import com.handy.portal.ui.element.BookingDetailsDateViewConstructor;
import com.handy.portal.ui.element.BookingDetailsJobInstructionsViewConstructor;
import com.handy.portal.ui.element.BookingDetailsLocationPanelViewConstructor;
import com.handy.portal.ui.element.SupportActionContainerViewConstructor;
import com.handy.portal.ui.element.BookingDetailsViewConstructor;
import com.handy.portal.ui.element.GoogleMapViewConstructor;
import com.handy.portal.ui.element.MapPlaceholderViewConstructor;
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

    @Inject
    PrefsManager prefsManager;

    private String requestedBookingId;
    private Booking associatedBooking; //used to return to correct date on jobs tab if a job action fails and the returned booking is null

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
            this.requestedBookingId = getArguments().getString(BundleKeys.BOOKING_ID);
            requestBookingDetails(this.requestedBookingId);
        }
        else
        {
            showToast(R.string.an_error_has_occurred);
            returnToTab(MainViewTab.JOBS, 0, TransitionStyle.REFRESH_TAB);
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
        return prefsManager.getString(PrefsKey.USER_CREDENTIALS_ID);
    }

    private void requestBookingDetails(String bookingId)
    {
        fetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestBookingDetails(bookingId));
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
        List<Booking.Action> allowedActions = booking.getAllowedActions();
        Activity activity = getActivity();

        BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING_STATUS, bookingStatus);

        //Construct the views for each layout
        Map<ViewGroup, BookingDetailsViewConstructor> viewConstructors = getViewConstructorsForLayouts();
        for (Map.Entry<ViewGroup, BookingDetailsViewConstructor> viewConstructorEntry : viewConstructors.entrySet())
        {
            ViewGroup layout = viewConstructorEntry.getKey();
            BookingDetailsViewConstructor constructor = viewConstructorEntry.getValue();
            constructor.constructView(booking, allowedActions, arguments, layout, activity);
        }

        //Full Details Notice , technically we should move this to its own view panel
        fullDetailsNoticeText.setVisibility(bookingStatus == BookingStatus.AVAILABLE ? View.VISIBLE : View.GONE);
    }

    //A listing of all the view constructors we use to populate the layouts
    //We don't maintain references to these constructors / the view, we always create anew from a booking
    private Map<ViewGroup, BookingDetailsViewConstructor> getViewConstructorsForLayouts()
    {
        Map<ViewGroup, BookingDetailsViewConstructor> viewConstructors = new HashMap<>();
        viewConstructors.put(bannerLayout, new BookingDetailsBannerViewConstructor());

        //show either the real map or a placeholder image depending on if we have google play services
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()))
        {
            viewConstructors.put(mapLayout, new GoogleMapViewConstructor());
        }
        else
        {
            viewConstructors.put(mapLayout, new MapPlaceholderViewConstructor());
        }

        viewConstructors.put(dateLayout, new BookingDetailsDateViewConstructor());
        viewConstructors.put(locationLayout, new BookingDetailsLocationPanelViewConstructor());
        viewConstructors.put(actionLayout, new BookingDetailsActionPanelViewConstructor());
        viewConstructors.put(contactLayout, new BookingDetailsActionContactPanelViewConstructor());
        viewConstructors.put(jobInstructionsLayout, new BookingDetailsJobInstructionsViewConstructor());
        viewConstructors.put(removeJobLayout, new BookingDetailsActionRemovePanelViewConstructor());
        return viewConstructors;
    }

    @OnClick(R.id.try_again_button)
    public void onClickRequestDetails()
    {
        requestBookingDetails(this.requestedBookingId);
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
            } else
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

        LocationData locationData = Utils.getCurrentLocation((BaseActivity) getActivity());

        switch (actionType)
        {
            case CLAIM:
            {
                requestClaimJob(this.associatedBooking.getId());
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
                requestRemoveJob(this.associatedBooking.getId());
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

            default:
            {
                Crashlytics.log("Could not find associated behavior for : " + actionType.getActionName());
            }
        }
    }

    private void showHelpOptions()
    {
        bus.post(new HandyEvent.ShowSlideUpPanel(new SlideUpPanelContainer.ContentInitializer()
        {
            @Override
            public void initialize(ViewGroup panel)
            {
                new SupportActionContainerViewConstructor(bus, SupportActionUtils.ETA_ACTION_NAMES)
                        .create(getActivity(), panel, associatedBooking);
                new SupportActionContainerViewConstructor(bus, SupportActionUtils.ISSUE_ACTION_NAMES)
                        .create(getActivity(), panel, associatedBooking);
            }
        }));
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
                .setTitle(R.string.are_you_sure)
                .setMessage(warning)
                .setPositiveButton(warningButtonsText.getPositiveStringId(), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                //proceed with action, we have accepted the warning
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
        switch(actionType)
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
    private void requestClaimJob(String bookingId)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestClaimJob(bookingId));
    }

    private void requestRemoveJob(String bookingId)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestRemoveJob(bookingId));
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
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestNotifyJobUpdateArrivalTime(bookingId, arrivalTimeOption));
    }

    //Show a radio button option dialog to select arrival time for the ETA action
    private void showUpdateArrivalTimeDialog(final Booking booking)
    {
        final String bookingId = booking.getId();

        //Text for options
        int numArrivalTimeOptions = Booking.ArrivalTimeOption.values().length;
        final CharSequence[] arrivalTimeOptionStrings = new CharSequence[numArrivalTimeOptions];
        Booking.ArrivalTimeOption[] arrivalTimeOptions = Booking.ArrivalTimeOption.values();
        for (int i = 0; i < arrivalTimeOptions.length; i++)
        {
            Booking.ArrivalTimeOption arrivalTimeOption = arrivalTimeOptions[i];
            arrivalTimeOptionStrings[i] = (getString(arrivalTimeOption.getStringId()));
        }

        //specific booking error, show an alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set dialog message
        alertDialogBuilder
                .setTitle(R.string.notify_customer)
                .setSingleChoiceItems(arrivalTimeOptionStrings, 0, null)
                .setPositiveButton(R.string.send_update, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Booking.ArrivalTimeOption[] arrivalTimeOptions = Booking.ArrivalTimeOption.values();
                                int checkedItemPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                if (checkedItemPosition < 0 || checkedItemPosition >= arrivalTimeOptions.length)
                                {
                                    Crashlytics.log("Invalid checked item position " + checkedItemPosition + " can not proceed");
                                } else
                                {
                                    Booking.ArrivalTimeOption chosenOption = arrivalTimeOptions[checkedItemPosition];
                                    requestNotifyUpdateArrivalTime(bookingId, chosenOption);
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.back, null)
        ;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    //use native functionality to trigger a phone call
    private void callPhoneNumber(final String phoneNumber)
    {
        try
        {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
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
            TransitionStyle transitionStyle = (event.booking.isRecurring() ? TransitionStyle.SERIES_CLAIM_SUCCESS : TransitionStyle.JOB_CLAIM_SUCCESS);
            returnToTab(MainViewTab.JOBS, event.booking.getStartDate().getTime(), transitionStyle);
        } else
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
            returnToTab(MainViewTab.SCHEDULE, event.booking.getStartDate().getTime(), transitionStyle);
        } else
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
        returnToTab(MainViewTab.SCHEDULE, this.associatedBooking.getStartDate().getTime(), TransitionStyle.REFRESH_TAB);

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
    public void onSupportActionTriggered(HandyEvent.TriggerSupportAction event)
    {
        SupportAction supportAction = SupportActionUtils.getSupportAction(event.action);
        switch (supportAction)
        {
            case NOTIFY_EARLY:
                break;
            case NOTIFY_LATE:
                break;
            case REPORT_NO_SHOW:
                break;
            case ISSUE_UNSAFE:
            case ISSUE_HOURS:
            case ISSUE_OTHER:
                bus.post(new HandyEvent.HideSlideUpPanel());
                Bundle arguments = new Bundle();
                arguments.putString(BundleKeys.TARGET_URL, event.action.getDeepLink());
                bus.post(new HandyEvent.NavigateToTab(MainViewTab.HELP, arguments, TransitionStyle.NATIVE_TO_WEBVIEW));
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
        } else
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
        } else
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
                        bus.post(new HandyEvent.NavigateToTab(MainViewTab.SCHEDULE, arguments, TransitionStyle.REFRESH_TAB));
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
        } else
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
            returnToTab(MainViewTab.JOBS, 0, TransitionStyle.REFRESH_TAB);
        }
        else
        {
            errorText.setText(R.string.error_fetching_connectivity_issue);
            //allow try again
            fetchErrorView.setVisibility(View.VISIBLE);
        }

    }

}
