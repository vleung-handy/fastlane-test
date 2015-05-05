package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;
import com.handy.portal.R;
import com.handy.portal.core.Booking;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.data.DataManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AvailableBookingsFragment extends InjectedFragment {

    //Hardcoding this since we don't have an active user yet
    private final String HACK_HARDCODE_PROVIDER_ID = "8";

    @InjectView(R.id.availableJobsListView)
    ListView availableJobsListView;

    @InjectView(R.id.datesScrollViewLayout)
    LinearLayout datesScrollViewLayout;

    private static int DAYS_TO_DISPLAY = 7;

    private BookingCalendarDay activeDay;

    private Map<BookingCalendarDay, BookingSummary> cachedBookingSummaries;
    //private List<BookingSummary> cachedBookingSummaries;

    public AvailableBookingsFragment()
    {

    }


    /*

    GET     /api/portal/provider/:id/jobs(.:format) # available jobs
    GET     /api/portal/provider/:id/schedule(.:format) # provider's schedule
    POST   /api/portal/provider/:provider_id/bookings/:id/claim(.:format) # claim a booking
    GET     /api/portal/provider/:provider_id/bookings/:id(.:format) # booking details

     */

    //get bookings for provider api call
    //host/api/portal/#PROVIDER_ID#/jobs?apiver=1

    //http://localhost:3000/api/portal/provider/8/jobs?apiver=1

    class BookingCalendarDay
    {

        public BookingCalendarDay(Date date)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        public BookingCalendarDay(Calendar calendar) {
            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        public BookingCalendarDay(int year, int month, int day)
        {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        int year;
        int month;
        int day;

        @Override
        public int hashCode()
        {
            //TODO: make more better
            return new Integer(year * 100 + month * 100 + day * 1).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BookingCalendarDay)) {
                return false;
            }

            BookingCalendarDay compare = (BookingCalendarDay) obj;

            if (obj == this) {
                return true;
            }

            if(compare.year == this.year &&
                    compare.month == this.month &&
                        compare.day == this.day)
            {
                return true;
            }

            return false;
        }

        @Override
        public String toString()
        {
            return (Integer.toString(year) + "/" + Integer.toString(month) + "/" + Integer.toString(day));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_available_bookings, null);
        ButterKnife.inject(this, view);

        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        BookingCalendarDay defaultDay = new BookingCalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        setActiveDay(defaultDay);
        addDateButtons(datesScrollViewLayout, calendar);
        requestAvailableBookings(); //server determines how many days of data we get
        return view;
    }

    private void setActiveDay(BookingCalendarDay activeDay)
    {
        this.activeDay = activeDay;
        System.out.println("Set active day : " + activeDay);
        //refresh display
        displayActiveDayBookings();
    }

    private void addDateButtons(LinearLayout scrollViewLayout, Calendar calendar) {
        Context c = getActivity().getApplicationContext();
        SimpleDateFormat ft = new SimpleDateFormat("E\nd");

        for (int i = 0; i < DAYS_TO_DISPLAY; i++) {
            LayoutInflater.from(c).inflate(R.layout.element_date, scrollViewLayout);

            Button dateButton = ((Button) (datesScrollViewLayout.getChildAt(i)));
            final BookingCalendarDay associatedBookingCalendarDay = new BookingCalendarDay(calendar);
            String formattedDate = ft.format(calendar.getTime());
            dateButton.setText(formattedDate);
            dateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    System.out.println("Clicked on date : " + associatedBookingCalendarDay.toString() );
                    setActiveDay(associatedBookingCalendarDay);
                }
            });
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }
    }

    private void requestAvailableBookings()
    {
        //Going to get all bookings for a one week period, they are bucketed by day

        System.out.println("Requesting available bookings for provider");

        dataManager.getAvailableBookings(HACK_HARDCODE_PROVIDER_ID, new DataManager.Callback<List<BookingSummary>>() {

            @Override
            public void onSuccess(final List<BookingSummary> bookingSummaries)
            {
                System.out.println("Got some booking summaries in : " + bookingSummaries.size());

                cachedBookingSummaries = new HashMap<BookingCalendarDay, BookingSummary>();
                for(BookingSummary bs : bookingSummaries)
                {
                    BookingCalendarDay bcd = new BookingCalendarDay(bs.getDate());
                    System.out.println("Adding summary for : " + bcd + " : num bookings " + bs.getBookings().size());
                    cachedBookingSummaries.put(bcd, bs);
                }
                displayActiveDayBookings();
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                System.err.println("Failed to get available bookings " + error);
            }
        });
    }

    private void displayActiveDayBookings() {
        List<Booking> bookings = getActiveDayBookings();
        if(bookings == null) {
            //TODO: Some kind of loading/waiting display state
            System.out.println("No bookings to display for : " + activeDay.toString());
            return;
        }
        displayBookings(bookings);
    }

    private List<Booking> getActiveDayBookings()
    {
        if(cachedBookingSummaries == null) { System.out.println("Bookings data yet"); return null; }
        if(cachedBookingSummaries.containsKey(activeDay))
        {
            System.out.println("See matching key in dict " + activeDay);

            List<Booking> bookings = cachedBookingSummaries.get(activeDay).getBookings();

            if(bookings == null) {
                System.out.println("these bookings are null");
            }
            return bookings;
        }
        else
        {
            System.out.println("No matching booking in cached summaries");
        }

        return null;
    }

    private void displayBookings(List<Booking> bookings) {

        System.out.println("Displaying bookings : " + bookings.size());

        //create an entry for each booking
        BookingElementAdapter itemsAdapter =
                new BookingElementAdapter(getActivity().getApplicationContext(), bookings);

        availableJobsListView.setAdapter(itemsAdapter);

        availableJobsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                        Booking booking = (Booking) adapter.getItemAtPosition(position);
                        System.out.println("clicked on booking with id " + booking.getId());
                    }
                }
        );
    }

//mediators push information to the view telling them what to display
    //they listen for signals from and intended for the associated view
        //views do 0 logic, they just display information
    class BookingElementMediator
    {
        private BookingElementView view;
        private Booking booking;
        private View convertView;
        private ViewGroup parent;

        public BookingElementMediator(Context parentContext, Booking booking, View convertView, ViewGroup parent)
        {
            this.booking = booking;
            this.convertView = convertView;
            this.parent = parent;

            this.view = new BookingElementView(this);
            this.view.initView(parentContext, booking, convertView, parent);

        }

        public View getAssociatedView()
        {
            return this.view.associatedView;
        }

        //

        public BookingElementView getView()
        {
            return view;
        }

    }

    class BookingElementView
    {
        private BookingElementMediator mediator;
        public View associatedView;

        public BookingElementView(BookingElementMediator mediator)
        {
            this.mediator = mediator;
        }

        public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent)
        {
            if(booking == null)
            {
                System.err.println("The booking is null, worthless!");
                return null;
            }

            boolean isRequested = booking.getIsRequested();

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null)
            {
                convertView = LayoutInflater.from(parentContext).inflate(R.layout.element_booking, parent, false);
            }

            TextView bookingAreaTextView = (TextView) convertView.findViewById(R.id.bookingArea);
            String bookingArea = booking.getAddress().getShortRegion();

            bookingAreaTextView.setText(bookingArea);

            LinearLayout requestedIndicator  = (LinearLayout) convertView.findViewById(R.id.requested_indicator);
            requestedIndicator.setVisibility(isRequested ? View.VISIBLE : View.GONE);

            Date startDate = booking.getStartDate();

            SimpleDateFormat ft = new SimpleDateFormat ("hh:mma");

            String formattedStartDate = ft.format(startDate);
            String formattedEndDate = ft.format(booking.getEndDate());

            TextView startTimeText = (TextView) convertView.findViewById(R.id.bookingStartDate);
            TextView endTimeText = (TextView) convertView.findViewById(R.id.bookingEndDate);

            startTimeText.setText(formattedStartDate);
            endTimeText.setText(formattedEndDate);

            this.associatedView = convertView;

            return convertView;
        }
    }

    class BookingElementAdapter extends ArrayAdapter<Booking>
    {
        public BookingElementAdapter(Context context, List<Booking> bookings) {
            super(context, 0, bookings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Booking booking = getItem(position);
            BookingElementMediator bem = new BookingElementMediator(getContext(), booking, convertView, parent);
            return bem.getAssociatedView();
        }

    }


}
