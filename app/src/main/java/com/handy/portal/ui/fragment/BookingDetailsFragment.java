package com.handy.portal.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.consts.MainViewTab;
import com.handy.portal.consts.TransitionStyle;
import com.handy.portal.core.LoginManager;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.event.Event;
import com.handy.portal.ui.element.BookingDetailsActionPanelView;
import com.handy.portal.ui.element.BookingDetailsDateView;
import com.handy.portal.ui.element.BookingDetailsJobInstructionsView;
import com.handy.portal.ui.element.GoogleMapView;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
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

    @InjectView(R.id.booking_details_action_layout)
    protected RelativeLayout actionLayout;

    @InjectView(R.id.booking_details_contact_layout)
    protected RelativeLayout contactLayout;

    @InjectView(R.id.booking_details_job_instructions_layout)
    protected LinearLayout jobInstructionsLayout;

    @InjectView(R.id.booking_details_full_details_notice_text)
    protected TextView fullDetailsNoticeText;

    @Inject
    SecurePreferences prefs;

    private static final String NO_PROVIDER_ASSIGNED = "0";

    public enum BookingStatus
    {
        AVAILABLE,
        CLAIMED,
        UNAVAILABLE
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_booking_detail, null);

        ButterKnife.inject(this, view);

        bannerText.setText("");

        if(validateRequiredArguments())
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

    //Events and Event Handling
    @Subscribe
    public void onBookingDetailsRetrieved(Event.BookingsDetailsRetrievedEvent event)
    {
        if(event.success)
        {
            updateDisplayForBooking(event.booking);
        }
        else
        {
            //TODO: Show a display state that involves re-requesting booking details
        }
    }


    @Subscribe
    public void onClaimJobRequestReceived(Event.ClaimJobRequestReceivedEvent event)
    {
        Booking booking = event.booking;

        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));

        if(event.success)
        {
            if(event.booking.getProviderId().equals(getLoggedInUserId()))
            {
                //TODO: Turn this back on when finished implementing skinning the "claimed" state of booking details, for now opening the available jobs tab again
                if(false)
                {
                    updateDisplayForBooking(event.booking);
                }
                else
                {
                    //Return to available jobs with success
                    Bundle arguments = new Bundle();
                    Calendar c = Calendar.getInstance();
                    c.setTime(event.booking.getStartDate());
                    int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
                    arguments.putInt(BundleKeys.ACTIVE_DAY_OF_YEAR, dayOfYear);
                    //Return to available jobs on that day
                    bus.post(new Event.NavigateToTabEvent(MainViewTab.JOBS, arguments, TransitionStyle.JOB_CLAIM_SUCCESS));
                }
            }
            else
            {
                //Return to available jobs on failure with failure transition
                Bundle arguments = new Bundle();
                Calendar c = Calendar.getInstance();
                c.setTime(event.booking.getStartDate());
                int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
                arguments.putInt(BundleKeys.ACTIVE_DAY_OF_YEAR, dayOfYear);
                //Return to available jobs on that day
                bus.post(new Event.NavigateToTabEvent(MainViewTab.JOBS, arguments, TransitionStyle.JOB_CLAIM_FAIL));
            }
        }
        else
        {
            //the base error handle pops up a toast with the error message if the event itself fails, no need for additional popup
            //re-enable the button so they can try again for network errors
            getActionButton().setEnabled(true);
        }

    }

    private void requestBookingDetails(String bookingId)
    {
        bus.post(new Event.RequestBookingDetailsEvent(bookingId));
    }

    private void requestClaimJob(String bookingId)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(true));
        bus.post(new Event.RequestClaimJobEvent(bookingId));
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
        for(int i = 0; i < detailsParentLayout.getChildCount(); i++)
        {
            ViewGroup vg = (ViewGroup) detailsParentLayout.getChildAt(i);
            if(vg != null)
            {
                vg.removeAllViews();
            }
        }
    }

    private void initBookingDisplayElements(Booking booking)
    {
        Activity activity = getActivity();

        BookingStatus bookingStatus = inferBookingStatus(booking, getLoggedInUserId());
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING_STATUS, bookingStatus);

        //Banner
        setBannerText(bookingStatus);

        //google maps
        GoogleMapView gmv = new GoogleMapView();
        gmv.init(booking, arguments, mapLayout, activity);

        //date banner
        BookingDetailsDateView dateView = new BookingDetailsDateView();
        dateView.init(booking, new Bundle(), dateLayout, activity );

        //action section
        BookingDetailsActionPanelView actionPanel = new BookingDetailsActionPanelView();
        actionPanel.init(booking, arguments, actionLayout, activity);
        initActionButtonListener(actionPanel.getActionButton(), bookingStatus, getLoggedInUserId(), booking.getId());

        //extra details
        //TODO : Restrict details based on showing full information, only show extras not instructions if restricted
        BookingDetailsJobInstructionsView jobInstructionsView = new BookingDetailsJobInstructionsView();
        jobInstructionsView.init(booking, arguments, jobInstructionsLayout, activity);

        //Full details notice
        fullDetailsNoticeText.setVisibility(bookingStatus == BookingStatus.AVAILABLE ? View.VISIBLE : View.GONE);
    }

    private void setBannerText(BookingStatus bookingStatus)
    {
        switch(bookingStatus)
        {
            case AVAILABLE:
            {
                bannerText.setText(R.string.available_job);
            }
            break;
            case CLAIMED:
            {
                bannerText.setText(R.string.claimed_job);
            }
            break;
            case UNAVAILABLE:
            {
                bannerText.setText(R.string.unavailable_job);
            }
            break;
        }
    }

    @OnClick(R.id.booking_details_back_button)
    public void onBackButtonClick(View v)
    {
        getActivity().onBackPressed();
    }

    private Button getActionButton()
    {
        return ((Button) getView().findViewById(R.id.booking_details_action_button));
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
                switch(bookingStatus)
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

    //Helpers

    //providerId = 0, no one assigned can claim, otherwise is already claimed
    //going to add providerstatus to track coming going etc
    private BookingStatus inferBookingStatus(Booking booking, String userId)
    {
        String assignedProvider = booking.getProviderId();

        if(assignedProvider.equals(NO_PROVIDER_ASSIGNED))
        {
            //TODO: If booking is in the past change status
            return BookingStatus.AVAILABLE;
        }
        else if(booking.getProviderId().equals(userId))
        {
            //TODO: Depending on time to booking change status
            return BookingStatus.CLAIMED;
        }
        else
        {
            return BookingStatus.UNAVAILABLE;
        }
    }

    private String getLoggedInUserId()
    {
        return prefs.getString(LoginManager.USER_CREDENTIALS_ID_KEY, null);
    }

}
