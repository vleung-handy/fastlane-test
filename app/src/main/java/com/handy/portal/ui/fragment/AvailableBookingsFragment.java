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

import com.handy.portal.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class AvailableBookingsFragment extends InjectedFragment {

    @InjectView(R.id.availableJobsListView)
    ListView availableJobsListView;

    @InjectView(R.id.datesScrollViewLayout)
    LinearLayout datesScrollViewLayout;

    private static int DAYS_TO_DISPLAY = 7;

    public AvailableBookingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_available_bookings, null);
        ButterKnife.inject(this, view);
        addDateButtons(datesScrollViewLayout);
        testBookings();
        return view;
    }

    private void addDateButtons(LinearLayout scrollViewLayout) {
        Context c = getActivity().getApplicationContext();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat ft = new SimpleDateFormat("E\nd");
        for (int i = 0; i < DAYS_TO_DISPLAY; i++) {
            LayoutInflater.from(c).inflate(R.layout.element_date, scrollViewLayout);
            Button dateButton = ((Button) (datesScrollViewLayout.getChildAt(i)));
            Date calendarTime = calendar.getTime();
            String formattedDate = ft.format(calendarTime);
            dateButton.setText(formattedDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }


    private void testBookings()
    {
        ArrayList<TestBooking> testBookings = new ArrayList<>();

        Calendar calendar = new GregorianCalendar(2015, 1, 28, 12, 00, 00);

        Random r = new Random();

        for(int i = 0; i < 5; i++)
        {
            int hour = (r.nextInt(12) + 8);
            int duration = r.nextInt(4) + 1;
            calendar.set(Calendar.HOUR, hour);
            Date start = calendar.getTime();
            calendar.set(Calendar.HOUR, hour + duration);
            Date end = calendar.getTime();

            TestBooking tb = new TestBooking(i, "aaa" + (Integer.toString(i)), start, end);
            if(r.nextBoolean())
            {
                tb.isRequested = true;
            }
            testBookings.add(tb);


        }

        displayBookings(testBookings);
    }



    private void displayBookings(ArrayList<TestBooking> bookings) {
        //create an entry for each booking

        BookingElementAdapter itemsAdapter =
                new BookingElementAdapter(getActivity().getApplicationContext(), bookings);

        availableJobsListView.setAdapter(itemsAdapter);

        availableJobsListView.setOnItemClickListener(
                //todo :
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                        TestBooking booking = (TestBooking) adapter.getItemAtPosition(position);
                        System.out.println("clicked on booking with id " + Integer.toString(booking.id));
                    }
                }
        );
    }

    class User
    {
        String name;
        int id;
    }


    class TestBooking
    {
        int id;
        User user;
        boolean isRequested;

        Date startDate;
        Date endDate;

        String area;

        public TestBooking(int i, String n, Date startDate, Date endDate)
        {
            this.id = i;
            this.user = new User();
            this.user.name = n;
            this.area = n;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

//mediators push information to the view telling them what to display
    //they listen for signals from and intended for the associated view
        //views do 0 logic, they just display information
    class BookingElementMediator
    {
        private BookingElementView view;
        private TestBooking booking;
        private View convertView;
        private ViewGroup parent;

        public BookingElementMediator(Context parentContext, TestBooking booking, View convertView, ViewGroup parent)
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
            //initView(parentContext, booking, convertView, parent);
        }

        public View initView(Context parentContext, TestBooking booking, View convertView, ViewGroup parent)
        {
            boolean isRequested = booking.isRequested;

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null)
            {
                convertView = LayoutInflater.from(parentContext).inflate(R.layout.element_booking, parent, false);
            }

            // Lookup view for data population
            TextView bookingAreaTextView = (TextView) convertView.findViewById(R.id.bookingArea);
            // Populate the data into the template view using the data object

            //System.out.println("See entry : " + booking.name + " : " + booking.id);

            //String bookingName = booking.name;
            //int bookingId = booking.id;

            String bookingArea = booking.area;

            //bookingIdTextView.setText(Integer.toString(bookingId));
            bookingAreaTextView.setText(bookingArea);

            // Return the completed view to render on screen

            LinearLayout requestedIndicator  = (LinearLayout) convertView.findViewById(R.id.requested_indicator);
            requestedIndicator.setVisibility(isRequested ? View.VISIBLE : View.GONE);

            Date startDate = booking.startDate;

            SimpleDateFormat ft =
                    new SimpleDateFormat ("hh:mma");

            String formattedStartDate = ft.format(startDate);
            String formattedEndDate = ft.format(booking.endDate);

            TextView startTimeText = (TextView) convertView.findViewById(R.id.bookingStartDate);
            TextView endTimeText = (TextView) convertView.findViewById(R.id.bookingEndDate);

            startTimeText.setText(formattedStartDate);
            endTimeText.setText(formattedEndDate);

            this.associatedView = convertView;

            return convertView;
        }

    }




    class BookingElementAdapter extends ArrayAdapter<TestBooking>
    {
        public BookingElementAdapter(Context context, ArrayList<TestBooking> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            TestBooking booking = getItem(position);
            BookingElementMediator bem = new BookingElementMediator(getContext(), booking, convertView, parent);
            return bem.getAssociatedView();
        }

    }


}
