package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.consts.MainViewTab;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.event.Event;
import com.handy.portal.ui.element.DateButtonView;
import com.handy.portal.ui.form.BookingListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public abstract class BookingsFragment extends InjectedFragment
{
     @InjectView(R.id.bookings_content)
    protected View bookingsContentView;

    @InjectView(R.id.fetch_error_view)
    protected View fetchErrorView;

    @InjectView(R.id.fetch_error_text)
    protected TextView errorText;

    protected abstract int getFragmentResourceId();

    protected abstract BookingListView getBookingListView();

    protected abstract ViewGroup getNoBookingsView();

    protected abstract LinearLayout getDatesLayout();

    protected abstract int getErrorTextResId();

    protected abstract String getTrackingType();

    protected abstract Event getRequestEvent();

    private int previousDatesScrollPosition;

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

    @OnClick(R.id.try_again_button)
    public void doRequestBookingsAgain()
    {
        requestBookings();
    }

    protected void requestBookings()
    {
        fetchErrorView.setVisibility(View.GONE);
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(true));
        bus.post(getRequestEvent());
    }

    //Event listeners
    //Can't subscribe in an abstract class?
    public abstract void onBookingsRetrieved(Event.BookingsRetrievedEvent event);

    protected void handleBookingsRetrieved(Event.BookingsRetrievedEvent event)
    {
        bus.post(new Event.SetLoadingOverlayVisibilityEvent(false));
        if (event.success)
        {
            List<BookingSummary> bookingSummaries = event.bookingSummaries;
            initDateButtons(bookingSummaries);

            bookingsContentView.setVisibility(View.VISIBLE);

            if (selectedDay != null && dateButtonMap.containsKey(selectedDay))
            {
                dateButtonMap.get(selectedDay).performClick();
                scrollDatesToPreviousPosition();
            }
            else if (getDatesLayout().getChildCount() > 0)
            {
                getDatesLayout().getChildAt(0).performClick();
            }

        }
        else
        {
            errorText.setText(getErrorTextResId());
            fetchErrorView.setVisibility(View.VISIBLE);
        }
    }

    private void scrollDatesToPreviousPosition()
    {
        final HorizontalScrollView scrollView = (HorizontalScrollView) getDatesLayout().getParent();
        scrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                scrollView.scrollTo(previousDatesScrollPosition, 0);
            }
        });
    }

    private void initDateButtons(List<BookingSummary> bookingSummaries)
    {
        LinearLayout datesLayout = getDatesLayout();

        //remove existing date buttons
        datesLayout.removeAllViews();

        dateButtonMap = new HashMap<>(bookingSummaries.size());

        Context context = getActivity();

        for (final BookingSummary bookingSummary : bookingSummaries)
        {
            LayoutInflater.from(context).inflate(R.layout.element_date_button, datesLayout);
            final DateButtonView dateButtonView = (DateButtonView) datesLayout.getChildAt(datesLayout.getChildCount() - 1);

            final List<Booking> bookingsForDay = new ArrayList<>(bookingSummary.getBookings());

            Collections.sort(bookingsForDay);
            insertSeparator(bookingsForDay);

            boolean requestedJobsThisDay = bookingsForDay.size() > 0 && bookingsForDay.get(0).getIsRequested();
            final Date day = bookingSummary.getDate();
            dateButtonView.init(day, requestedJobsThisDay);
            dateButtonView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    bus.post(new Event.DateClickedEvent(getTrackingType(), day));
                    selectDay(day);
                    displayBookings(bookingsForDay);
                }
            });

            dateButtonMap.put(day, dateButtonView);
        }
    }

    private void selectDay(Date day)
    {
        DateButtonView selectedDateButtonView = dateButtonMap.get(selectedDay);
        if (selectedDateButtonView != null)
        {
            selectedDateButtonView.setChecked(false);
        }
        dateButtonMap.get(day).setChecked(true);
        selectedDay = day;
    }

    private Map<Date, DateButtonView> dateButtonMap;
    private Date selectedDay;

    private void displayBookings(List<Booking> bookings)
    {
        getBookingListView().populateList(bookings);
        initListClickListener();
        getNoBookingsView().setVisibility(bookings.size() > 0 ? View.GONE : View.VISIBLE);
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
                    bus.post(new Event.BookingSelectedEvent(getTrackingType(), booking.getId()));
                    previousDatesScrollPosition = ((HorizontalScrollView) getDatesLayout().getParent()).getScrollX();
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

        Event.NavigateToTabEvent event = new Event.NavigateToTabEvent(MainViewTab.DETAILS, arguments);

        bus.post(event);
    }

}
