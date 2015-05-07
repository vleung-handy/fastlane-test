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
import com.handy.portal.event.RequestScheduledBookingsEvent;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class ScheduledBookingsFragment extends InjectedFragment {

    public ScheduledBookingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_scheduled_bookings, null);
        ButterKnife.inject(this, view);
        requestClaimedBookings();
        initListClickListener();
        return view;
    }

    //Hardcoding this since we don't have an active user yet
    private final String HACK_HARDCODE_PROVIDER_ID = "11";

    @InjectView(R.id.scheduledJobsListView)
    BookingListView scheduledJobsListView;

    @InjectView(R.id.scheduledBookingsDatesScrollViewLayout)
    LinearLayout datesScrollViewLayout;

    private BookingCalendarDay activeDay; //what day are we currently displaying bookings for?
    private Map<BookingCalendarDay, BookingSummary> bookingSummariesByDay;

    //Event listeners
    @Subscribe
    public void onBookingsRetrieved(BookingsRetrievedEvent event)
    {
        Map<BookingCalendarDay, BookingSummary> bookingSummaries = event.bookingSummaries;
        bookingSummariesByDay = bookingSummaries;
        updateDateButtons();
        displayActiveDayBookings();
    }

    private void requestClaimedBookings()
    {
        bus.post(new RequestScheduledBookingsEvent(HACK_HARDCODE_PROVIDER_ID));
    }

    private void displayActiveDayBookings() {
        List<Booking> bookings = getActiveDayBookings();
        if(bookings == null) {
            //TODO: Some kind of loading/waiting display state
            System.out.println("No bookings to display for : " + activeDay.toString());
            return;
        }
        scheduledJobsListView.populateList(bookings);
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

    private void initListClickListener()
    {
        scheduledJobsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                        Booking booking = (Booking) adapter.getItemAtPosition(position);
                        System.out.println("clicked on booking with id " + booking.getId());
                    }
                }
        );
    }

    private void setActiveDay(BookingCalendarDay activeDay) {
        this.activeDay = activeDay;
        displayActiveDayBookings();
    }


    //Horiz scroll view picker with dates
    private void updateDateButtons()
    {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        //today is always the default day, the server should always send us this at minimum
        //BookingCalendarDay defaultDay = new BookingCalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        int numDaysToDisplay = getNumDaysOfBookingSummaries();
        refreshDateButtons(datesScrollViewLayout, calendar, numDaysToDisplay);
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
            Button dateButton = ((Button) (datesScrollViewLayout.getChildAt(i)));
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
                    System.out.println("Clicked on date : " + associatedBookingCalendarDay.toString() );
                    setActiveDay(associatedBookingCalendarDay);
                }
            });

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}
