package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.BookingCalendarDay;
import com.handy.portal.event.Event;
import com.handy.portal.ui.form.BookingListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.ButterKnife;

public abstract class BookingsFragment extends InjectedFragment
{
    protected BookingCalendarDay activeDay; //what day are we currently displaying bookings for?
    protected Map<BookingCalendarDay, BookingSummary> bookingSummariesByDay;

    protected abstract int getFragmentResourceId();

    protected abstract BookingListView getBookingListView();

    protected abstract LinearLayout getDatesLayout();

    protected abstract void requestBookings();

    protected abstract void initListClickListener();

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
        Map<BookingCalendarDay, BookingSummary> bookingSummaries = event.bookingSummaries;
        bookingSummariesByDay = bookingSummaries;
        updateDateButtons();
        displayActiveDayBookings();
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
        List<Booking> bookings = getActiveDayBookings();
        if (bookings == null)
        {
            //TODO: Some kind of loading/waiting display state
            System.err.println("No bookings retrieved?");
            return;
        }

        BookingListView bookingListView = getBookingListView();

        if(bookingListView == null)
        {
            System.err.println("List view is null");
            return;
        }

        bookingListView.populateList(bookings);
    }

    private List<Booking> getActiveDayBookings()
    {
        if (bookingSummariesByDay == null)
        {
            System.err.println("No bookings data yet");
            return null;
        }

        if (bookingSummariesByDay.containsKey(activeDay))
        {
            return bookingSummariesByDay.get(activeDay).getBookings();
        }

        System.err.println("Could not find day : " + activeDay);

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

    private void refreshDateButtons(LinearLayout scrollViewLayout, Calendar calendar, int numDaysToDisplay)
    {
        //remove existing date buttons
        scrollViewLayout.removeAllViews();

        Context context = getActivity().getApplicationContext();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM E d");

        for (int i = 0; i < numDaysToDisplay; i++)
        {
            LayoutInflater.from(context).inflate(R.layout.element_date_button, scrollViewLayout);
            LinearLayout dateLayout = ((LinearLayout) (scrollViewLayout.getChildAt(i)));

            TextView monthText = (TextView) dateLayout.findViewById(R.id.date_month_text);
            TextView dayOfWeekText = (TextView)  dateLayout.findViewById(R.id.date_day_of_week_text);
            TextView dayOfMonthText = (TextView) dateLayout.findViewById(R.id.date_day_of_month_text);

            ImageView requestedIndicator = (ImageView) dateLayout.findViewById(R.id.provider_requested_indicator_image);
            requestedIndicator.setVisibility(View.INVISIBLE);

            ImageView selectedDayIndicator = (ImageView) dateLayout.findViewById(R.id.selected_day_indicator_image);
            selectedDayIndicator.setVisibility(View.INVISIBLE);

            final BookingCalendarDay associatedBookingCalendarDay = new BookingCalendarDay(calendar);

            if (i == 0)
            {
                this.activeDay = associatedBookingCalendarDay; //by default point to first day of data as active day
            }

            String[] formattedDate = dateFormat.format(calendar.getTime()).split(" ");

            //only display month for first day in a month
            if(Integer.parseInt(formattedDate[2]) == 1)
            {
                monthText.setText(formattedDate[0]);
            }
            else
            {
                monthText.setVisibility(View.INVISIBLE);
            }

            dayOfWeekText.setText(formattedDate[1]);
            dayOfMonthText.setText(formattedDate[2]);

            dateLayout.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    setActiveDay(associatedBookingCalendarDay);
                }
            });

            //next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}
