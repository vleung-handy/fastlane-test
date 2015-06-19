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
import com.handy.portal.ui.element.BookingDetailsActionPanelView;
import com.handy.portal.ui.element.BookingDetailsActionRemovePanelView;
import com.handy.portal.ui.element.BookingDetailsContactPanelView;
import com.handy.portal.ui.element.BookingDetailsDateView;
import com.handy.portal.ui.element.BookingDetailsJobInstructionsView;
import com.handy.portal.ui.element.BookingDetailsLocationPanelView;
import com.handy.portal.ui.element.GoogleMapView;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class BookingDetailsFragment extends InjectedFragment
{
    @InjectView(R.id.booking_details_banner_text)
    protected TextView bannerText;

    //Layouts points for fragment, the various element are childed to these
    @InjectView(R.id.booking_details_layout)
    protected LinearLayout detailsParentLayout;

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

        bannerText.setText("");

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

//Event Posts
    private void requestBookingDetails(String bookingId)
    {
        bus.post(new Event.RequestBookingDetailsEvent(bookingId));
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

//Display
    private void updateDisplayForBooking(Booking booking)
    {
        //clear existing elements out of our fragment's display
        clearLayouts();
        initBookingDisplayElements(booking);
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

    private void initBookingDisplayElements(Booking booking)
    {
        Activity activity = getActivity();

        BookingStatus bookingStatus = booking.inferBookingStatus(getLoggedInUserId());
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING_STATUS, bookingStatus);

        //Banner
        setBannerTextByBookingStatus(bookingStatus);

        //google maps
        GoogleMapView gmv = new GoogleMapView();
        gmv.init(booking, arguments, mapLayout, activity);

        //date banner
        BookingDetailsDateView dateView = new BookingDetailsDateView();
        dateView.init(booking, new Bundle(), dateLayout, activity);

        //Location panel
        BookingDetailsLocationPanelView locationPanel = new BookingDetailsLocationPanelView();
        locationPanel.init(booking, arguments, locationLayout, activity);

        //action section
        BookingDetailsActionPanelView actionPanel = new BookingDetailsActionPanelView();
        actionPanel.init(booking, arguments, actionLayout, activity);
        initActionButtonListener(actionPanel.getActionButton(), bookingStatus, getLoggedInUserId(), booking.getId());

        //customer contact section
        BookingDetailsContactPanelView contactPanel = new BookingDetailsContactPanelView();
        contactPanel.init(booking, arguments, contactLayout, activity);
        initContactButtonListeners(contactPanel.getCallButton(), contactPanel.getTextButton(), booking.getUser().getPhoneNumberString());

        //extra details
        //TODO : Restrict details based on showing full information, only show extras not instructions if restricted
        BookingDetailsJobInstructionsView jobInstructionsView = new BookingDetailsJobInstructionsView();
        jobInstructionsView.init(booking, arguments, jobInstructionsLayout, activity);

        //Remove job action panel
        BookingDetailsActionRemovePanelView removeJobView = new BookingDetailsActionRemovePanelView();
        removeJobView.init(booking, arguments, removeJobLayout, activity);
        initActionRemoveButtonListener(removeJobView.getActionButton(), bookingStatus, getLoggedInUserId(), booking.getId());

        //Full details notice
        fullDetailsNoticeText.setVisibility(bookingStatus == BookingStatus.AVAILABLE ? View.VISIBLE : View.GONE);
    }

    private void setBannerTextByBookingStatus(BookingStatus bookingStatus)
    {
        switch (bookingStatus)
        {
            case AVAILABLE:
            {
                bannerText.setText(R.string.available_job);
            }
            break;

            case CLAIMED:
            case CLAIMED_WITHIN_HOUR:
            case CLAIMED_WITHIN_DAY:
            case CLAIMED_PAST:
            case CLAIMED_IN_PROGRESS_CHECKED_IN:
            case CLAIMED_IN_PROGRESS:
            {
                bannerText.setText(R.string.your_job);
            }
            break;

            case UNAVAILABLE:
            {
                bannerText.setText(R.string.unavailable_job);
            }
            break;
        }
    }


//Buttons and click listeners
    private Button getActionButton()
    {
        return ((Button) actionLayout.findViewById(R.id.booking_details_action_button));
    }

    private Button getActionRemoveButton()
    {
        return ((Button) removeJobLayout.findViewById(R.id.booking_details_action_button));
    }

    @OnClick(R.id.booking_details_back_button)
    public void onBackButtonClick(View v)
    {
        getActivity().onBackPressed();
    }

    private void initActionButtonListener(final Button button, final BookingStatus bookingStatus, final String userId, final String bookingId)
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //TODO: can take various actions based on booking status
                //claim
                //on my way
                //check in
                //check out
                switch (bookingStatus)
                {
                    case AVAILABLE:
                    {
                        requestClaimJob(bookingId);
                    }
                    break;
                    //TODO: more status actions
                }
                button.setEnabled(false); //prevent multi clicks, turn off button when an action is taken, if action fails re-enable butotn
            }
        });
    }

    private void initActionRemoveButtonListener(final Button button, final BookingStatus bookingStatus, final String userId, final String bookingId)
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                requestRemoveJob(bookingId);
                button.setEnabled(false); //prevent multi clicks, turn off button when an action is taken, if action fails re-enable butotn
            }
        });
    }

    private void initContactButtonListeners(final Button callButton, final Button textButton, final String phoneNumber)
    {
        callButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
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
        });

        textButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
                }
                catch (ActivityNotFoundException activityException)
                {
                    System.err.println("Texting a Phone Number failed" + activityException);
                }
            }
        });
    }











//Events and Event Handling

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


//Handle Action Errors

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
            handleNetworkError(getActionButton());
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
            handleNetworkError(getActionRemoveButton());
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

    private void handleNetworkError(Button buttonToReactivate)
    {
        //generic network problem
        //show a toast about connectivity issues
        showErrorToast(R.string.error_connectivity, Toast.LENGTH_LONG);
        //re-enable the button so they can try again for network errors
        buttonToReactivate.setEnabled(true);
    }

}
