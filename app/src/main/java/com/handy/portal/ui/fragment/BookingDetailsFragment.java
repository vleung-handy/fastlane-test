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
import com.handy.portal.core.booking.Booking;
import com.handy.portal.event.Event;
import com.handy.portal.ui.element.BookingDetailsActionPanelView;
import com.handy.portal.ui.element.BookingDetailsDateView;
import com.handy.portal.ui.element.GoogleMapView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BookingDetailsFragment extends InjectedFragment
{

    final static String HACK_BOOKING_ID = "4"; //hardcoded, to get from intent


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



    //extract from the intent
    protected Booking selectedBooking;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        System.out.println("Creating booking details fragment");

        View view = inflater.inflate(R.layout.fragment_booking_detail, null);
        ButterKnife.inject(this, view);

        initBanner();


        requestBookingDetails(HACK_BOOKING_ID);

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

        this.selectedBooking = booking;

        initElements(booking);
    }


    //assemble the view out of the associated fragments

    private void initElements(Booking booking)
    {
        //take in a booking from the intent data?

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

        //contact customer


        //extra details


        //additional action section



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
