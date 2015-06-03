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

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public abstract class BookingsFragment extends InjectedFragment
{

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
        requestBookings();
        return view;
    }

    //Event listeners
    //Can't subscribe in an abstract class?
    public abstract void onBookingsRetrieved(Event.BookingsRetrievedEvent event);

    protected void handleBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
        if (event.success)
        {
            Map<BookingCalendarDay, BookingSummary> bookingSummaries = event.bookingSummaries;
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

    private void initDateButtons(Map<BookingCalendarDay, BookingSummary> bookingSummaries)
    {
        LinearLayout datesLayout = getDatesLayout();

        //remove existing date buttons
        datesLayout.removeAllViews();
        selectedDateButtonView = null;

        Context context = getActivity();

        for (final Map.Entry<BookingCalendarDay, BookingSummary> bookingSummariesEntry : bookingSummaries.entrySet())
        {
            LayoutInflater.from(context).inflate(R.layout.element_date_button, datesLayout);
            final DateButtonView dateButtonView = (DateButtonView) datesLayout.getChildAt(datesLayout.getChildCount() - 1);

            final List<Booking> bookingsForDay = bookingSummariesEntry.getValue().getBookings();

            Collections.sort(bookingsForDay);
            insertSeparator(bookingsForDay);

            boolean requestedJobsThisDay = bookingsForDay.size() > 0 && bookingsForDay.get(0).getIsRequested();
            final Calendar calendarDay = bookingSummariesEntry.getKey().toCalendar();
            dateButtonView.init(calendarDay, requestedJobsThisDay);
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

}
