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
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.model.BookingSummary;
import com.handy.portal.model.Booking;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.element.BookingElementView;
import com.handy.portal.ui.element.DateButtonView;
import com.handy.portal.ui.element.BookingListView;
import com.handy.portal.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public abstract class BookingsFragment<T extends HandyEvent.ReceiveBookingsSuccess> extends InjectedFragment
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

    protected abstract String getTrackingType();

    protected abstract HandyEvent getRequestEvent();

    protected abstract boolean showRequestedIndicator(List<Booking> bookingsForDay);

    protected abstract boolean showClaimedIndicator(List<Booking> bookingsForDay);

    protected abstract void setupCTAButton(List<Booking> bookingsForDay, Date dateOfBookings);

    //Event listeners
    public abstract void onBookingsRetrieved(T event);

    private int previousDatesScrollPosition;

    //should use date without time for these entries, see Utils.getDateWithoutTime
    private Map<Date, DateButtonView> dateButtonMap;
    protected Date selectedDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getFragmentResourceId(), null);
        ButterKnife.inject(this, view);

        //Optional param, needs to be validated
        if (getArguments() != null && getArguments().containsKey(BundleKeys.DATE_EPOCH_TIME))
        {
            long targetDateTime = getArguments().getLong(BundleKeys.DATE_EPOCH_TIME);
            if (targetDateTime > 0)
            {
                this.selectedDay = new Date(getArguments().getLong(BundleKeys.DATE_EPOCH_TIME));
                this.selectedDay = Utils.getDateWithoutTime(this.selectedDay);
            }
        }

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
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(getRequestEvent());
    }

    protected void handleBookingsRetrieved(HandyEvent.ReceiveBookingsSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        List<BookingSummary> bookingSummaries = event.bookingSummaries;
        initDateButtons(bookingSummaries);

        bookingsContentView.setVisibility(View.VISIBLE);

        if (selectedDay != null && dateButtonMap.containsKey(selectedDay))
        {
            dateButtonMap.get(selectedDay).performClick();
            scrollDatesToPreviousPosition();
        } else if (getDatesLayout().getChildCount() > 0)
        {
            getDatesLayout().getChildAt(0).performClick();
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

            Collections.sort(bookingsForDay); //date, ascending

            boolean requestedJobsThisDay = showRequestedIndicator(bookingsForDay);
            boolean claimedJobsThisDay = showClaimedIndicator(bookingsForDay);

            final Date day = bookingSummary.getDate();
            dateButtonView.init(day, requestedJobsThisDay, claimedJobsThisDay);
            dateButtonView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    bus.post(new HandyEvent.DateClicked(getTrackingType(), day));
                    selectDay(day);
                    displayBookings(bookingsForDay, day);
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

    private void displayBookings(List<Booking> bookings, Date dateOfBookings)
    {
        getBookingListView().populateList(bookings, getBookingElementViewClass());
        initListClickListener();
        getNoBookingsView().setVisibility(bookings.size() > 0 ? View.GONE : View.VISIBLE);
        setupCTAButton(bookings, dateOfBookings);
    }

    protected abstract Class<? extends BookingElementView> getBookingElementViewClass();

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
                    bus.post(new HandyEvent.BookingSelected(getTrackingType(), booking.getId()));
                    previousDatesScrollPosition = ((HorizontalScrollView) getDatesLayout().getParent()).getScrollX();
                    showBookingDetails(booking);
                }
            }
        });
    }

    private void showBookingDetails(Booking booking)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());

        HandyEvent.NavigateToTab event = new HandyEvent.NavigateToTab(MainViewTab.DETAILS, arguments);

        bus.post(event);
    }

}
