package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.BookingCalendarDay;
import com.handy.portal.event.BookingsRetrievedEvent;
import com.handy.portal.event.RequestAvailableBookingsEvent;
import com.handy.portal.event.RequestBookingDetailsEvent;
import com.handy.portal.ui.form.BookingListView;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class BookingsFragment extends InjectedFragment {

    public BookingsFragment() {}

    protected BookingCalendarDay activeDay; //what day are we currently displaying bookings for?
    protected Map<BookingCalendarDay, BookingSummary> bookingSummariesByDay;

    protected abstract int getFragmentResourceId();
    protected abstract BookingListView getBookingListView();
    protected abstract LinearLayout getDatesLayout();
    protected abstract void requestBookings();
    protected abstract void initListClickListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getFragmentResourceId(), null);
        ButterKnife.inject(this, view);
        requestBookings();
        initListClickListener();
        return view;
    }

    //Event listeners
    @Subscribe
    public void onBookingsRetrieved(BookingsRetrievedEvent event)
    {
        Map<BookingCalendarDay, BookingSummary> bookingSummaries = event.bookingSummaries;
        bookingSummariesByDay = bookingSummaries;
        updateDateButtons();
        displayActiveDayBookings();
    }

    protected void requestBookingDetails(String bookingId)
    {
        bus.post(new RequestBookingDetailsEvent(bookingId));
    }

    private void setActiveDay(BookingCalendarDay activeDay) {
        this.activeDay = activeDay;
        displayActiveDayBookings();
    }

    private void displayActiveDayBookings() {
        List<Booking> bookings = getActiveDayBookings();
        if(bookings == null) {
            //TODO: Some kind of loading/waiting display state
            return;
        }
        getBookingListView().populateList(bookings);
    }

    private List<Booking> getActiveDayBookings()
    {
        if(bookingSummariesByDay == null) { System.err.println("No bookings data yet"); return null; }
        if(bookingSummariesByDay.containsKey(activeDay))
        {
            return bookingSummariesByDay.get(activeDay).getBookings();
        }
        return null;
    }

    //Horiz scroll view picker with dates
    protected void updateDateButtons()
    {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        //today is always the default day, the server should always send us this at minimum
        int numDaysToDisplay = getNumDaysOfBookingSummaries();
        refreshDateButtons(getDatesLayout(), calendar, numDaysToDisplay);
    }

    private int getNumDaysOfBookingSummaries()
    {
        if(bookingSummariesByDay == null) { System.err.println("No bookings data yet"); return 0; }
        return bookingSummariesByDay.size();
    }

    private void refreshDateButtons(LinearLayout scrollViewLayout, Calendar calendar, int numDaysToDisplay) {

        //remove existing date buttons
        scrollViewLayout.removeAllViews();

        Context context = getActivity().getApplicationContext();
        SimpleDateFormat ft = new SimpleDateFormat("E\nd");

        for (int i = 0; i < numDaysToDisplay; i++)
        {
            LayoutInflater.from(context).inflate(R.layout.element_date_button, scrollViewLayout);
            Button dateButton = ((Button) (scrollViewLayout.getChildAt(i)));
            final BookingCalendarDay associatedBookingCalendarDay = new BookingCalendarDay(calendar);

            if(i == 0)
            {
                this.activeDay = associatedBookingCalendarDay; //by default point to first day of data as active day
            }

            String formattedDate = ft.format(calendar.getTime());
            dateButton.setText(formattedDate);

            //TODO: Set this up mediator style so the button retains a link to associated day instead of use anon function
                //Java does not have delegates.....
            dateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setActiveDay(associatedBookingCalendarDay);
                }
            });

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}
