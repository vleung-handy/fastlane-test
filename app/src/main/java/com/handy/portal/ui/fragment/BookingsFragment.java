package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.BookingCalendarDay;
import com.handy.portal.event.Event;
import com.handy.portal.ui.element.DateButtonView;
import com.handy.portal.ui.form.BookingListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.ButterKnife;

public abstract class BookingsFragment extends InjectedFragment
{
    protected BookingCalendarDay activeDay; //what day are we currently displaying bookings for?
    protected Map<BookingCalendarDay, BookingSummary> bookingSummariesByDay;

    protected abstract int getFragmentResourceId();

    protected abstract BookingListView getRequestedBookingListView();
    protected abstract BookingListView getUnrequestedBookingListView();

    protected abstract LinearLayout getDatesLayout();

    protected abstract void requestBookings();

    protected abstract void initListClickListener();

    public enum BookingListType
    {
        REQUESTED,
        UNREQUESTED
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getFragmentResourceId(), null);
        ButterKnife.inject(this, view);
            //System.out.println("Bookings fragment being created" + view.toString());
        requestBookings();
        initListClickListener();
        return view;
    }

    //Event listeners
        //Can't subscribe in an abstract class?
    public abstract void onBookingsRetrieved(Event.BookingsRetrievedEvent event);

    protected void handleBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
        if(event.success)
        {
            bookingSummariesByDay = event.bookingSummaries;
            updateDateButtons();
            displayActiveDayBookings();
        }
        else
        {
            //TODO: Handle a failed state? A resend / restart button?
        }
    }

    protected void requestBookingDetails(String bookingId)
    {
        bus.post(new Event.RequestBookingDetailsEvent(bookingId));
    }

    private void setActiveDay(BookingCalendarDay activeDay)
    {
        this.activeDay = activeDay;
        displayActiveDayBookings();
    }

    private void displayActiveDayBookings()
    {
        Map<BookingListType,List<Booking>> bookings = getActiveDayBookings();

        if (bookings == null)
        {
            //TODO: Some kind of loading/waiting/please try again display state
            System.err.println("No bookings retrieved?");
            return;
        }

        getRequestedBookingListView().populateList(bookings.get(BookingListType.REQUESTED));

        getUnrequestedBookingListView().populateList(bookings.get(BookingListType.UNREQUESTED));
    }

    private Map<BookingListType, List<Booking>> getActiveDayBookings()
    {
        return getBookingsForDay(activeDay);
    }

    private Map<BookingListType, List<Booking>> getBookingsForDay(BookingCalendarDay day)
    {
        Map<BookingListType, List<Booking>> activeDayBookings = new HashMap<>();

        if (bookingSummariesByDay == null)
        {
            System.err.println("No bookings data yet");
            return null;
        }

        if (bookingSummariesByDay.containsKey(day))
        {
            //Filter the bookings into two lists, a requested and non-requested list and sort those bookings by time
            List<Booking> unrequestedBookings = new ArrayList(bookingSummariesByDay.get(day).getBookings());
            List<Booking> requestedBookings = new ArrayList<>();

            //Remove all requested bookings from unrequested and add them to requested
            Iterator<Booking> bookingsIterator = unrequestedBookings.iterator();
            while (bookingsIterator.hasNext()) {
                Booking b = bookingsIterator.next();
                if (b.getIsRequested())
                {
                    requestedBookings.add(b);
                    bookingsIterator.remove();
                }
            }

            Collections.sort(requestedBookings);
            Collections.sort(unrequestedBookings);

            activeDayBookings.put(BookingListType.REQUESTED, requestedBookings);
            activeDayBookings.put(BookingListType.UNREQUESTED, unrequestedBookings);

            return activeDayBookings;
        }

        System.err.println("Could not find day : " + day);
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
        if (bookingSummariesByDay == null)
        {
            System.err.println("No bookings data yet");
            return 0;
        }
        return bookingSummariesByDay.size();
    }

    private DateButtonView selectedDateButtonView;

    private void refreshDateButtons(LinearLayout scrollViewLayout, Calendar calendar, int numDaysToDisplay)
    {
        //remove existing date buttons
        scrollViewLayout.removeAllViews();
        selectedDateButtonView = null;

        Context context = getActivity();

        for (int i = 0; i < numDaysToDisplay; i++)
        {
            LayoutInflater.from(context).inflate(R.layout.element_date_button, scrollViewLayout);
            final DateButtonView dateButtonView = ((DateButtonView) (scrollViewLayout.getChildAt(i)));

            final BookingCalendarDay associatedBookingCalendarDay = new BookingCalendarDay(calendar);

            boolean requestedJobsThisDay = getBookingsForDay(associatedBookingCalendarDay).get(BookingListType.REQUESTED).size() > 0;
            dateButtonView.init(calendar, requestedJobsThisDay);
            dateButtonView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    selectDate(dateButtonView);
                    setActiveDay(associatedBookingCalendarDay);
                }
            });

            if (i == 0)
            {
                this.activeDay = associatedBookingCalendarDay; //by default point to first day of data as active day
                selectDate(dateButtonView);
            }

            //next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void selectDate(DateButtonView dateButtonView)
    {
        if (selectedDateButtonView != dateButtonView)
        {
            if (selectedDateButtonView != null)
            {
                selectedDateButtonView.setChecked(false);
            }
            dateButtonView.setChecked(true);
            selectedDateButtonView = dateButtonView;
        }
    }

}
