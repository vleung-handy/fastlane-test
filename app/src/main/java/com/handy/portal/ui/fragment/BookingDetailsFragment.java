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
import com.handy.portal.ui.element.BookingDetailsActionPanelViewConstructor;
import com.handy.portal.ui.element.BookingDetailsActionRemovePanelViewConstructor;
import com.handy.portal.ui.element.BookingDetailsBannerViewConstructor;
import com.handy.portal.ui.element.BookingDetailsContactPanelViewConstructor;
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

    public void setAssociatedBooking(Booking b)
    {
        this.associatedBooking = b;
    }

    public Booking getAssociatedBooking()
    {
        return this.associatedBooking;
    }

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
        clearLayouts();
        constructBookingDisplayElements(booking);
        processAllowedActions(booking.getAllowedActions());
    }

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

    private void constructBookingDisplayElements(Booking booking)
    {
        Activity activity = getActivity();

        BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING_STATUS, bookingStatus);

        //Construct the views for each layout
        Map<ViewGroup, BookingDetailsViewConstructor> viewConstructors = getViewConstructorsForLayouts();
        Iterator it = viewConstructors.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ViewGroup layout = (ViewGroup) pair.getKey();
            BookingDetailsViewConstructor constructor = (BookingDetailsViewConstructor) pair.getValue();
            constructor.constructView(booking, arguments, layout, activity);
        }

        //Full Details Notice
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
        views.put(contactLayout, new BookingDetailsContactPanelViewConstructor());
        views.put(jobInstructionsLayout, new BookingDetailsJobInstructionsViewConstructor());
        views.put(removeJobLayout, new BookingDetailsActionRemovePanelViewConstructor());
        return views;
    }

    //instead of the element views handling the buttons we are going to have a specialized helper that inserts buttons into the relevant areas and handles their click functionality
    private void processAllowedActions(List<Booking.ActionButtonData> allowedActions)
    {
        for(Booking.ActionButtonData data : allowedActions)
        {
            if(data.getAssociatedActionType() == null)
            {
                System.err.println("Received an unsupported action type : " + data.getActionName());
                continue;
            }

            //the client knows what layout to insert a given button into, this should never come from the server
            ViewGroup buttonParentLayout = getParentLayoutForButtonActionType(data.getAssociatedActionType());

            if(buttonParentLayout == null)
            {
                System.err.println("Could not find parent layout for " + data.getAssociatedActionType().getActionName());
            }
            else
            {
                int newChildIndex = buttonParentLayout.getChildCount(); //new index is equal to the old count since the new count is +1
                BookingActionButton bookingActionButton = (BookingActionButton)
                        ((ViewGroup) getActivity().getLayoutInflater().inflate(data.getAssociatedActionType().getLayoutTemplateId(), buttonParentLayout)).getChildAt(newChildIndex);
                bookingActionButton.init(this, data); //not sure if this is the better way or to have buttons dispatch specifc events the fragment catches, for now this will suffice
                bookingActionButton.setEnabled(data.isEnabled());
            }
        }
    }

    private ViewGroup getParentLayoutForButtonActionType(Booking.ButtonActionType bat)
    {
        switch(bat)
        {
            case CLAIM: { return (ViewGroup) actionLayout.findViewById(R.id.booking_details_action_panel_button_layout); }
            case ON_MY_WAY: { return (ViewGroup) actionLayout.findViewById(R.id.booking_details_action_panel_button_layout); }
            case CHECK_IN: { return (ViewGroup) actionLayout.findViewById(R.id.booking_details_action_panel_button_layout); }
            case ETA: { return (ViewGroup) actionLayout.findViewById(R.id.booking_details_action_panel_button_layout); }
        //todo: Will have to have sorting so phone always comes before text without relying on server sending it in a certain order
            case CONTACT_PHONE: { return  (ViewGroup) contactLayout.findViewById(R.id.booking_details_contact_action_button_layout); }
            case CONTACT_TEXT: { return (ViewGroup) contactLayout.findViewById(R.id.booking_details_contact_action_button_layout); }
            case REMOVE: { return (ViewGroup) removeJobLayout.findViewById(R.id.booking_details_action_panel_button_layout); }
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
        switch(actionType)
        {
            case CLAIM:
            {
                requestClaimJob(this.associatedBooking.getId());
            }
            break;

            case REMOVE:
            {
                requestRemoveJob(this.associatedBooking.getId());
            }
            break;

            case CONTACT_PHONE:
            {
                callPhoneNumber(this.associatedBooking.getUser().getPhoneNumberString());
            }
            break;

            case CONTACT_TEXT:
            {
                textPhoneNumber(this.associatedBooking.getUser().getPhoneNumberString());
            }
            break;

            default:
            {
                System.err.println("Could not find associated behavior for : " + actionType.getActionName());
            }
        }
    }

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

    private void callPhoneNumber(final String phoneNumber)
    {
        try
        {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
        catch (ActivityNotFoundException activityException)
        {
            System.err.println("Calling a Phone Number failed" + activityException);
        }
    }

    private void textPhoneNumber(final String phoneNumber)
    {
        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
        }
        catch (ActivityNotFoundException activityException)
        {
            System.err.println("Calling a Phone Number failed" + activityException);
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
        }
        else
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
            }
            else
            {
                //Something has gone very wrong, the claim came back as success but the data shows not claimed, show a generic error and return to date based on original associated booking
                handleBookingClaimError(getString(R.string.job_claim_error), R.string.job_claim_error_generic, R.string.return_to_available_jobs, this.associatedBooking.getStartDate());
            }
        }
        else
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
            }
            else
            {
                //Something has gone very wrong, show a generic error and return to date based on original associated booking
                handleBookingRemoveError(getString(R.string.job_remove_error), R.string.job_remove_error_generic, R.string.return_to_schedule, this.associatedBooking.getStartDate());
            }
        }
        else
        {
            handleBookingRemoveError(event);
        }
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
        }
        else
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
        }
        else
        {
            handleNetworkError();
        }
    }

    private void showReturnToAvailableErrorDialog(String title, String errorMessage, String option1, final long returnDateEpochTime)
    {
        //specific booking error, show an alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
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
        //re-enable the button so they can try again for network errors
        //buttonToReactivate.setEnabled(true);

        //todo: turn relevant buttons back on
        reenableButtons();
    }

    //todo: turn relevant buttons back on if a network call fails
    private void reenableButtons()
    {
        System.err.println("Still need to fix, in the event of a network error re-enable relevant buttons");
    }

}
