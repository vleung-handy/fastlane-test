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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.consts.MainViewTab;
import com.handy.portal.consts.TransitionStyle;
import com.handy.portal.core.LoginManager;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;
import com.handy.portal.event.Event;
import com.handy.portal.ui.element.BookingDetailsActionContactPanelViewConstructor;
import com.handy.portal.ui.element.BookingDetailsActionPanelViewConstructor;
import com.handy.portal.ui.element.BookingDetailsActionRemovePanelViewConstructor;
import com.handy.portal.ui.element.BookingDetailsBannerViewConstructor;
import com.handy.portal.ui.element.BookingDetailsDateViewConstructor;
import com.handy.portal.ui.element.BookingDetailsJobInstructionsViewConstructor;
import com.handy.portal.ui.element.BookingDetailsLocationPanelViewConstructor;
import com.handy.portal.ui.element.BookingDetailsViewConstructor;
import com.handy.portal.ui.element.GoogleMapViewConstructor;
import com.handy.portal.ui.widget.BookingActionButton;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

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

    @Inject
    SecurePreferences prefs;

    private Booking associatedBooking; //used to return to correct date on jobs tab if a job action fails and the returned booking is null

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_booking_detail, null);

        ButterKnife.inject(this, view);

        if (validateRequiredArguments())
        {
            requestBookingDetails(this.getArguments().getString(BundleKeys.BOOKING_ID));
        }

        return view;
    }

    @Override
    protected List<String> requiredArguments()
    {
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add(BundleKeys.BOOKING_ID);
        return requiredArguments;
    }

    private String getLoggedInUserId()
    {
        return prefs.getString(LoginManager.USER_CREDENTIALS_ID_KEY, null);
    }

    private void requestBookingDetails(String bookingId)
    {
        bus.post(new Event.RequestBookingDetailsEvent(bookingId));
    }

    //Display
    private void updateDisplayForBooking(Booking booking)
    {
        //clear existing elements out of our fragment's display
        List<Booking.ActionButtonData> allowedActions = booking.getAllowedActions();
        clearLayouts();
        constructBookingDisplayElements(booking, allowedActions);
        createAllowedActionButtons(allowedActions);
        initBackButton();
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
    private void constructBookingDisplayElements(Booking booking, List<Booking.ActionButtonData> allowedActions)
    {
        Activity activity = getActivity();

        BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING_STATUS, bookingStatus);

        //Construct the views for each layout
        Map<ViewGroup, BookingDetailsViewConstructor> viewConstructors = getViewConstructorsForLayouts();
        Iterator it = viewConstructors.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            ViewGroup layout = (ViewGroup) pair.getKey();
            BookingDetailsViewConstructor constructor = (BookingDetailsViewConstructor) pair.getValue();
            constructor.constructView(booking, allowedActions, arguments, layout, activity);
        }

        //Full Details Notice , technically we should move this to its own view panel
        fullDetailsNoticeText.setVisibility(bookingStatus == BookingStatus.AVAILABLE ? View.VISIBLE : View.GONE);
    }

    //A listing of all the view constructors we use to populate the layouts
    //We don't maintain references to these constructors / the view, we always create anew from a booking
    private Map<ViewGroup, BookingDetailsViewConstructor> getViewConstructorsForLayouts()
    {
        Map<ViewGroup, BookingDetailsViewConstructor> views = new HashMap<>();
        views.put(bannerLayout, new BookingDetailsBannerViewConstructor());
        views.put(mapLayout, new GoogleMapViewConstructor());
        views.put(dateLayout, new BookingDetailsDateViewConstructor());
        views.put(locationLayout, new BookingDetailsLocationPanelViewConstructor());
        views.put(actionLayout, new BookingDetailsActionPanelViewConstructor());
        views.put(contactLayout, new BookingDetailsActionContactPanelViewConstructor());
        views.put(jobInstructionsLayout, new BookingDetailsJobInstructionsViewConstructor());
        views.put(removeJobLayout, new BookingDetailsActionRemovePanelViewConstructor());
        return views;
    }

    //TODO: the only button on this page that is not an action button, clean this up eventually?
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

    //instead of the element views handling the buttons we are going to have a specialized helper that inserts buttons into the relevant areas and handles their click functionality
    private void createAllowedActionButtons(List<Booking.ActionButtonData> allowedActions)
    {
        for (Booking.ActionButtonData data : allowedActions)
        {
            if (data.getAssociatedActionType() == null)
            {
                System.err.println("Received an unsupported action type : " + data.getActionName());
                continue;
            }

            //the client knows what layout to insert a given button into, this should never come from the server
            ViewGroup buttonParentLayout = getParentLayoutForButtonActionType(data.getAssociatedActionType());

            if (buttonParentLayout == null)
            {
                System.err.println("Could not find parent layout for " + data.getAssociatedActionType().getActionName());
            } else
            {
                int newChildIndex = buttonParentLayout.getChildCount(); //new index is equal to the old count since the new count is +1
                BookingActionButton bookingActionButton = (BookingActionButton)
                        ((ViewGroup) getActivity().getLayoutInflater().inflate(data.getAssociatedActionType().getLayoutTemplateId(), buttonParentLayout)).getChildAt(newChildIndex);
                bookingActionButton.init(this, data); //not sure if this is the better way or to have buttons dispatch specifc events the fragment catches, for now this will suffice
                bookingActionButton.setEnabled(data.isEnabled());
            }
        }
    }

    //Mapping for ButtonActionType to Parent Layout, used when adding Action Buttons dynamically
    private ViewGroup getParentLayoutForButtonActionType(Booking.ButtonActionType bat)
    {
        switch (bat)
        {
            case CLAIM:
            {
                return (ViewGroup) actionLayout.findViewById(R.id.booking_details_action_panel_button_layout);
            }
            case ON_MY_WAY:
            {
                return (ViewGroup) actionLayout.findViewById(R.id.booking_details_action_panel_button_layout);
            }
            case CHECK_IN:
            {
                return (ViewGroup) actionLayout.findViewById(R.id.booking_details_action_panel_button_layout);
            }
            case ETA:
            {
                return (ViewGroup) actionLayout.findViewById(R.id.booking_details_action_panel_button_layout);
            }
            //TODO: Will have to have sorting so phone always comes before text without relying on server sending it in a certain order
            case CONTACT_PHONE:
            {
                return (ViewGroup) contactLayout.findViewById(R.id.booking_details_contact_action_button_layout);
            }
            case CONTACT_TEXT:
            {
                return (ViewGroup) contactLayout.findViewById(R.id.booking_details_contact_action_button_layout);
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
    public void onActionButtonClick(Booking.ButtonActionType actionType)
    {
        takeAction(actionType, false);
    }

    //Take an action type, checking for warnings + showing warning dialog as needed
    protected void takeAction(Booking.ButtonActionType actionType, boolean hasBeenWarned)
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

        switch (actionType)
        {
            case CLAIM:
            {
                requestClaimJob(this.associatedBooking.getId());
            }
            break;

            case ON_MY_WAY:
            {
                requestNotifyOnMyWayJob(this.associatedBooking.getId());
            }
            break;

            case ETA:
            {
                showUpdateArrivalTimeDialog(this.associatedBooking);
            }
            break;

            case CHECK_IN:
            {
                requestNotifyCheckInJob(this.associatedBooking.getId());
            }
            break;

            case CHECK_OUT:
            {
                requestNotifyCheckOutJob(this.associatedBooking.getId());
            }
            break;

            case REMOVE:
            {
                requestRemoveJob(this.associatedBooking.getId());
            }
            break;

            case CONTACT_PHONE:
            {
                callPhoneNumber(this.associatedBooking.getBookingPhone());
            }
            break;

            case CONTACT_TEXT:
            {
                textPhoneNumber(this.associatedBooking.getBookingPhone());
            }
            break;

            default:
            {
                System.err.println("Could not find associated behavior for : " + actionType.getActionName());
            }
        }
    }

    //Check if the current booking data for a given action type has an associated warning to display
    private boolean checkShowWarningDialog(Booking.ButtonActionType actionType)
    {
        boolean showingWarningDialog = false;

        List<Booking.ActionButtonData> allowedActions = this.associatedBooking.getAllowedActions();

        //crawl through our list of allowed actions to retrieve the data from the booking for this allowed action
        for (Booking.ActionButtonData abd : allowedActions)
        {
            if (abd.getAssociatedActionType() == actionType)
            {
                if (abd.getWarningText() != null && !abd.getWarningText().isEmpty())
                {
                    showingWarningDialog = true;
                    showWarningDialog(abd.getWarningText(), abd.getAssociatedActionType());
                }
            }
        }
        return showingWarningDialog;
    }

    //Show a warning dialog for a button action, confirming triggers the original action
    private void showWarningDialog(String warning, final Booking.ButtonActionType actionType)
    {
        //specific booking error, show an alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set dialog message
        alertDialogBuilder
                .setTitle(R.string.are_you_sure)
                .setMessage(warning)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                //proceed with action
                                takeAction(actionType, true);
                            }
                        }
                )
                .setNegativeButton(R.string.cancel, null);
        ;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

//Service request bus posts
    //
    private void requestClaimJob(String bookingId)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(true));
        bus.post(new Event.RequestClaimJobEvent(bookingId));
    }

    private void requestRemoveJob(String bookingId)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(true));
        bus.post(new Event.RequestRemoveJobEvent(bookingId));
    }

    private void requestNotifyOnMyWayJob(String bookingId)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(true));
        bus.post(new Event.RequestNotifyOnMyWayJobEvent(bookingId));
    }

    private void requestNotifyCheckInJob(String bookingId)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(true));
        bus.post(new Event.RequestNotifyCheckInJobEvent(bookingId));
    }

    private void requestNotifyCheckOutJob(String bookingId)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(true));
        bus.post(new Event.RequestNotifyCheckOutJobEvent(bookingId));
    }

    private void requestNotifyUpdateArrivalTime(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(true));
        bus.post(new Event.RequestNotifyUpdateArrivalTimeEvent(bookingId, arrivalTimeOption));
    }

    //Show a radio button option dialog to select arrival time for the ETA action
    private void showUpdateArrivalTimeDialog(final Booking booking)
    {
        final String bookingId = booking.getId();

        //Text for options
        int numArrivalTimeOptions = Booking.ArrivalTimeOption.values().length;
        final CharSequence[] arrivalTimeOptionStrings =  new CharSequence[numArrivalTimeOptions];
        Booking.ArrivalTimeOption[] arrivalTimeOptions = Booking.ArrivalTimeOption.values();
        for(int i = 0; i < arrivalTimeOptions.length; i++)
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
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Booking.ArrivalTimeOption[] arrivalTimeOptions = Booking.ArrivalTimeOption.values();
                                Booking.ArrivalTimeOption chosenOption = arrivalTimeOptions[0];
                                int checkedItemPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                if (checkedItemPosition < 0 || checkedItemPosition >= arrivalTimeOptions.length)
                                {
                                    System.err.println("Invalid checked item position " + checkedItemPosition + " can not proceeed");
                                } else
                                {
                                    chosenOption = arrivalTimeOptions[checkedItemPosition];
                                    requestNotifyUpdateArrivalTime(bookingId, chosenOption);
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.cancel, null)
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
            System.err.println("Calling a Phone Number failed" + activityException);
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
            System.err.println("Texting a Phone Number failed" + activityException);
        }
    }

//Event Subscription and Handling

    @Subscribe
    public void onBookingDetailsRetrieved(Event.BookingsDetailsRetrievedEvent event)
    {
        if (event.success)
        {
            this.associatedBooking = event.booking;
            updateDisplayForBooking(event.booking);
        } else
        {
            //TODO: Show a display state that involves re-requesting booking details, could have been a network error or other?
        }
    }

    @Subscribe
    public void onClaimJobRequestReceived(final Event.ClaimJobRequestReceivedEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));

        if (event.success)
        {
            if (event.booking.getProviderId().equals(getLoggedInUserId()))
            {
                bus.post(new Event.ClaimJobSuccessEvent());
                TransitionStyle transitionStyle = (event.booking.isRecurring() ? TransitionStyle.SERIES_CLAIM_SUCCESS : TransitionStyle.JOB_CLAIM_SUCCESS);
                returnToAvailableBookings(event.booking.getStartDate().getTime(), transitionStyle);
            } else
            {
                //Something has gone very wrong, the claim came back as success but the data shows not claimed, show a generic error and return to date based on original associated booking
                handleBookingClaimError(getString(R.string.job_claim_error), R.string.job_claim_error_generic, R.string.return_to_available_jobs, this.associatedBooking.getStartDate());
            }
        } else
        {
            handleBookingClaimError(event);
        }
    }

    @Subscribe
    public void onRemoveJobRequestReceived(final Event.RemoveJobRequestReceivedEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));

        if (event.success)
        {
            if (event.booking.getProviderId().equals(Booking.NO_PROVIDER_ASSIGNED))
            {
                bus.post(new Event.RemoveJobSuccessEvent());
                TransitionStyle transitionStyle = (event.booking.isRecurring() ? TransitionStyle.SERIES_REMOVE_SUCCESS : TransitionStyle.JOB_REMOVE_SUCCESS);
                returnToAvailableBookings(event.booking.getStartDate().getTime(), transitionStyle);
            } else
            {
                //Something has gone very wrong, show a generic error and return to date based on original associated booking
                handleBookingRemoveError(getString(R.string.job_remove_error), R.string.job_remove_error_generic, R.string.return_to_schedule, this.associatedBooking.getStartDate());
            }
        } else
        {
            handleBookingRemoveError(event);
        }
    }

    @Subscribe
    public void onNotifyOnMyWayJobRequestReceived(final Event.NotifyOnMyWayJobRequestReceivedEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));

        //refresh the page with the new booking
        this.associatedBooking = event.booking;
        updateDisplayForBooking(event.booking);

        bus.post(new Event.NotifyOnMyWayJobSuccessEvent());
    }

    @Subscribe
    public void onNotifyOnMyWayJobError(final Event.NotifyOnMyWayJobErrorEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));
        handleNotifyOnMyWayError(event);
    }

    @Subscribe
    public void onNotifyCheckInJobRequestReceived(final Event.NotifyCheckInJobRequestReceivedEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));

        //refresh the page with the new booking
        this.associatedBooking = event.booking;
        updateDisplayForBooking(event.booking);

        bus.post(new Event.NotifyCheckInJobSuccessEvent());
    }

    @Subscribe
    public void onNotifyCheckInJobError(final Event.NotifyCheckInJobErrorEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));
        handleNotifyCheckInError(event);
    }

    @Subscribe
    public void onNotifyCheckOutJobRequestReceived(final Event.NotifyCheckOutJobRequestReceivedEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));

        //refresh the page with the new booking
        this.associatedBooking = event.booking;
        updateDisplayForBooking(event.booking);

        bus.post(new Event.NotifyCheckOutJobSuccessEvent());
    }

    @Subscribe
    public void onNotifyCheckOutJobError(final Event.NotifyCheckOutJobErrorEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));
        handleNotifyCheckOutError(event);
    }

    @Subscribe
    public void onNotifyUpdateArrivalRequestReceived(final Event.NotifyUpdateArrivalRequestReceivedEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));

        //refresh the page with the new booking
        this.associatedBooking = event.booking;
        updateDisplayForBooking(event.booking);

        bus.post(new Event.NotifyCheckOutJobSuccessEvent());
    }

    @Subscribe
    public void onNotifyUpdateArrivalError(final Event.NotifyUpdateArrivalErrorEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));
        handleNotifyUpdateArrivalError(event);
    }


    private void returnToAvailableBookings(long epochTime, TransitionStyle transitionStyle)
    {
        //Return to available jobs with success
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, epochTime);
        //Return to available jobs on that day
        bus.post(new Event.NavigateToTabEvent(MainViewTab.JOBS, arguments, transitionStyle));
    }


//Handle Action Response Errors

    private void handleBookingClaimError(final Event.ClaimJobRequestReceivedEvent event)
    {
        //used for onclick closure
        handleBookingClaimError(event.errorMessage, R.string.job_claim_error, R.string.return_to_available_jobs, this.associatedBooking.getStartDate());
    }

    private void handleBookingClaimError(String errorMessage, int titleId, int option1Id, Date returnDate)
    {
        handleBookingClaimError(errorMessage, getString(titleId), getString(option1Id), returnDate);
    }

    private void handleBookingClaimError(String errorMessage, String title, String option1, Date returnDate)
    {
        if (errorMessage != null)
        {
            bus.post(new Event.ClaimJobErrorEvent(errorMessage));
            showReturnToAvailableErrorDialog(errorMessage, title, option1, returnDate.getTime());
        } else
        {
            handleNetworkError();
        }
    }

    private void handleBookingRemoveError(final Event.RemoveJobRequestReceivedEvent event)
    {
        handleBookingRemoveError(event.errorMessage, R.string.job_remove_error, R.string.return_to_schedule, this.associatedBooking.getStartDate());
    }

    private void handleBookingRemoveError(String errorMessage, int titleId, int option1Id, Date returnDate)
    {
        handleBookingRemoveError(errorMessage, getString(titleId), getString(option1Id), returnDate);
    }

    private void handleBookingRemoveError(String errorMessage, String title, String option1, Date returnDate)
    {
        if (errorMessage != null)
        {
            bus.post(new Event.RemoveJobErrorEvent(errorMessage));
            showReturnToAvailableErrorDialog(errorMessage, title, option1, returnDate.getTime());
        } else
        {
            handleNetworkError();
        }
    }

    private void showReturnToAvailableErrorDialog(String title, String errorMessage, String option1, final long returnDateEpochTime)
    {
        //specific booking error, show an alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set dialog message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton(option1, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Return to available jobs, don't need overlay transition after alert dialog
                        returnToAvailableBookings(returnDateEpochTime, TransitionStyle.REFRESH_TAB);
                    }
                })
        ;
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void handleNetworkError()
    {
        //generic network problem
        //show a toast about connectivity issues
        showErrorToast(R.string.error_connectivity, Toast.LENGTH_LONG);
    }

    private void handleNotifyOnMyWayError(final Event.NotifyOnMyWayJobErrorEvent event)
    {
        handleCheckInFlowError(event.error.getMessage());
    }

    private void handleNotifyCheckInError(final Event.NotifyCheckInJobErrorEvent event)
    {
        handleCheckInFlowError(event.error.getMessage());
    }

    private void handleNotifyCheckOutError(final Event.NotifyCheckOutJobErrorEvent event)
    {
        handleCheckInFlowError(event.error.getMessage());
    }

    private void handleNotifyUpdateArrivalError(final Event.NotifyUpdateArrivalErrorEvent event)
    {
        handleCheckInFlowError(event.error.getMessage());
    }

    private void handleCheckInFlowError(String errorMessage)
    {
        if (errorMessage != null)
        {
            showErrorToast(errorMessage);
        } else
        {
            handleNetworkError();
        }
    }


}
