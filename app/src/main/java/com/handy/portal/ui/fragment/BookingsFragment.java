package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.event.Event;
import com.handy.portal.ui.element.DateButtonView;
import com.handy.portal.ui.form.BookingListView;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

public abstract class BookingsFragment extends InjectedFragment
{

    protected abstract int getFragmentResourceId();

    protected abstract BookingListView getBookingListView();

    protected abstract LinearLayout getDatesLayout();

    protected abstract void requestBookings();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getFragmentResourceId(), null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        requestBookings();
    }

    //Event listeners
    //Can't subscribe in an abstract class?
    public abstract void onBookingsRetrieved(Event.BookingsRetrievedEvent event);

    protected void handleBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
        if (event.success)
        {
            List<BookingSummary> bookingSummaries = event.bookingSummaries;
            initDateButtons(bookingSummaries);

            if (getDatesLayout().getChildCount() > 0)
            {
                getDatesLayout().getChildAt(0).performClick();
            }
        }
        else
        {
            //TODO: Handle a failed state? A resend / restart button?
        }
    }

    private void initDateButtons(List<BookingSummary> bookingSummaries)
    {
        LinearLayout datesLayout = getDatesLayout();

        //remove existing date buttons
        datesLayout.removeAllViews();
        selectedDateButtonView = null;

        Context context = getActivity();

        for (final BookingSummary bookingSummary : bookingSummaries)
        {
            LayoutInflater.from(context).inflate(R.layout.element_date_button, datesLayout);
            final DateButtonView dateButtonView = (DateButtonView) datesLayout.getChildAt(datesLayout.getChildCount() - 1);

            final List<Booking> bookingsForDay = bookingSummary.getBookings();

            Collections.sort(bookingsForDay);
            insertSeparator(bookingsForDay);

            boolean requestedJobsThisDay = bookingsForDay.size() > 0 && bookingsForDay.get(0).getIsRequested();
            dateButtonView.init(bookingSummary.getDate(), requestedJobsThisDay);
            dateButtonView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    if (selectedDateButtonView != dateButtonView)
                    {
                        if (selectedDateButtonView != null)
                        {
                            selectedDateButtonView.setChecked(false);
                        }
                        dateButtonView.setChecked(true);
                        selectedDateButtonView = dateButtonView;
                        displayBookings(bookingsForDay);
                    }
                }
            });
        }
    }

    private DateButtonView selectedDateButtonView;

    private void displayBookings(List<Booking> bookings)
    {
        getBookingListView().populateList(bookings);
        initListClickListener();
    }

    private void initListClickListener()
    {
        getBookingListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                Booking booking = (Booking) adapter.getItemAtPosition(position);
                if (booking != null)
                {
                    showBookingDetails(booking);
                }
            }
        });
    }

    private void insertSeparator(List<Booking> bookings)
    {
        for (int i = 1; i < bookings.size(); i++)
        {
            Booking previousBooking = bookings.get(i - 1);
            Booking booking = bookings.get(i);

            if (previousBooking.getIsRequested() && !booking.getIsRequested())
            {
                bookings.add(i, null);
                return;
            }
        }
    }

    private void showBookingDetails(Booking booking)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        BookingDetailsFragment bookingDetailsFragment = new BookingDetailsFragment();
        bookingDetailsFragment.setArguments(arguments);

        transaction.replace(R.id.main_container, bookingDetailsFragment).addToBackStack(null).commit();
    }

}
