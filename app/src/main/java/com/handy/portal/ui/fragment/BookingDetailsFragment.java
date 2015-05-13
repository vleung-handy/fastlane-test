package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.event.Event;
import com.handy.portal.ui.element.BookingDetailsActionPanelView;
import com.handy.portal.ui.element.BookingDetailsDateView;
import com.handy.portal.ui.element.BookingDetailsJobInstructionsView;
import com.handy.portal.ui.element.GoogleMapView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BookingDetailsFragment extends InjectedFragment
{





    //Banner
    @InjectView(R.id.booking_details_back_button)
    protected ImageButton backButton;

    @InjectView(R.id.booking_details_banner_text)
    protected TextView bannerText;

    //Layouts for main

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_booking_detail, null);

        ButterKnife.inject(this, view);

        //possible todo, each class has a list of required key arguments to validate on transition
        Bundle arguments = this.getArguments();
        String targetBookingId = "";
        if(arguments != null && arguments.containsKey(BundleKeys.BOOKING_ID))
        {
            targetBookingId = arguments.getString(BundleKeys.BOOKING_ID);
        }
        else
        {
            System.err.println("Could not find a " + BundleKeys.BOOKING_ID + " in arguments");
        }

        initBanner();

        requestBookingDetails(targetBookingId);

        return view;
    }

    private void requestBookingDetails(String bookingId)
    {
        System.out.println("Requesting booking details : " + bookingId);
        bus.post(new Event.RequestBookingDetailsEvent(bookingId));
    }

    //Event listeners
    @Subscribe
    public void onBookingDetailsRetrieved(Event.BookingsDetailsRetrievedEvent event)
    {
        Booking booking = event.booking;

        System.out.println("Retrieved bookings details : " + booking.getId());

        boolean showFullDisplay = false;

        if(showFullDisplay)
        {
            initFullDisplay(booking);
        }
        else
        {
            initRestrictedDisplay(booking);
        }

    }

    private void initFullDisplay(Booking booking)
    {
        Context context = getActivity().getApplicationContext();

        initRestrictedDisplay(booking);

        //contact customer
        //BookingDetailsJobInstructionsView jobInstructionsView = new BookingDetailsJobInstructionsView();
        //jobInstructionsView.init(booking, jobInstructionsLayout, context);

        //additional action section
        //Remove/cancel etc

    }

    private void initRestrictedDisplay(Booking booking)
    {
        Context context = getActivity().getApplicationContext();

        //google maps
        GoogleMapView gmv = new GoogleMapView();
        gmv.init(booking, mapLayout, context);

        //date banner
        BookingDetailsDateView dateView = new BookingDetailsDateView();
        dateView.init(booking, dateLayout,context );

        //action section
        BookingDetailsActionPanelView actionPanel = new BookingDetailsActionPanelView();
        actionPanel.init(booking, actionLayout, context);

        //extra details
        //TODO : Restrict details based on showing full information, only show extras not instructions if restricted
        BookingDetailsJobInstructionsView jobInstructionsView = new BookingDetailsJobInstructionsView();
        jobInstructionsView.init(booking, jobInstructionsLayout, context);

    }


    private void initBanner()
    {
        backButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //go back
            }
        });

        bannerText.setText("BANNER TEXT");
    }


}
