package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.BookingEvent;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.ui.element.BookingElementView;
import com.handy.portal.ui.element.BookingListView;
import com.handy.portal.ui.element.DateButtonView;
import com.handy.portal.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class BookingsFragment<T extends HandyEvent.ReceiveBookingsSuccess> extends ActionBarFragment
{
    @Inject
    protected EventLogFactory mEventLogFactory;
    @Bind(R.id.fetch_error_view)
    View fetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView errorText;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    protected abstract int getFragmentResourceId();

    protected abstract BookingListView getBookingListView();

    protected abstract ViewGroup getNoBookingsView();

    protected abstract LinearLayout getDatesLayout();

    @NonNull
    protected abstract String getTrackingType();

    protected abstract HandyEvent getRequestEvent(List<Date> dates, boolean useCachedIfPresent);

    protected abstract boolean shouldShowRequestedIndicator(List<Booking> bookingsForDay);

    protected abstract boolean shouldShowClaimedIndicator(List<Booking> bookingsForDay);

    protected abstract int numberOfDaysToDisplay();

    protected abstract void beforeRequestBookings();

    protected abstract void afterDisplayBookings(List<Booking> bookingsForDay, Date dateOfBookings);

    protected abstract Class<? extends BookingElementView> getBookingElementViewClass();

    //Event listeners
    public abstract void onBookingsRetrieved(T event);

    //should use date without time for these entries, see Utils.getDateWithoutTime
    private Map<Date, DateButtonView> dateButtonMap;
    protected Date selectedDay;
    protected List<Booking> bookingsForSelectedDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getFragmentResourceId(), null);
        ButterKnife.bind(this, view);

        //Optional param, needs to be validated
        if (getArguments() != null && getArguments().containsKey(BundleKeys.DATE_EPOCH_TIME))
        {
            long targetDateTime = getArguments().getLong(BundleKeys.DATE_EPOCH_TIME);
            if (targetDateTime > 0)
            {
                this.selectedDay = DateTimeUtils.getDateWithoutTime(new Date(getArguments().getLong(BundleKeys.DATE_EPOCH_TIME)));
            }
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                requestBookingsForSelectedDay(false);
            }
        });
        refreshLayout.setColorSchemeResources(R.color.handy_blue);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!MainActivityFragment.clearingBackStack)
        {
            bus.post(new HandyEvent.RequestProviderInfo());

            initDateButtons();

            if (selectedDay == null || !dateButtonMap.containsKey(selectedDay))
            {
                selectedDay = DateTimeUtils.getDateWithoutTime(new Date());
            }

            if (dateButtonMap.containsKey(selectedDay))
            {
                dateButtonMap.get(selectedDay).setChecked(true);
            }

            requestAllBookings();
        }
    }

    @OnClick(R.id.try_again_button)
    public void doRequestBookingsAgain()
    {
        requestBookingsForSelectedDay(true);
    }

    private void requestAllBookings()
    {
        requestBookingsForSelectedDay(true);

        requestBookingsForOtherDays(selectedDay);
    }

    private void requestBookingsForSelectedDay(boolean showOverlay)
    {
        requestBookings(Lists.newArrayList(selectedDay), showOverlay, false);
    }

    private void requestBookingsForOtherDays(Date dayToExclude)
    {
        List<Date> dates = Lists.newArrayList();
        for (int i = 0; i < numberOfDaysToDisplay(); i++)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, i);
            Date day = DateTimeUtils.getDateWithoutTime(calendar.getTime());

            if (!day.equals(dayToExclude))
            {
                dates.add(day);
            }
        }
        requestBookings(dates, false, true);
    }

    private void requestBookings(List<Date> dates, boolean showOverlay, boolean useCachedIfPresent)
    {
        Crashlytics.log("Requesting bookings for the following dates" + dates.toString());
        if (fetchErrorView == null)
        {
            Crashlytics.logException(
                    new NullPointerException("All views are null due to ButterKnife unbind."));
            return;
        }
        fetchErrorView.setVisibility(View.GONE);
        if (showOverlay)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        }
        bus.post(getRequestEvent(dates, useCachedIfPresent));
    }

    protected void handleBookingsRetrieved(HandyEvent.ReceiveBookingsSuccess event)
    {
        refreshLayout.setRefreshing(false);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        List<Booking> bookings = event.bookings;
        Collections.sort(bookings);

        for (Booking b : bookings)
        {
            if (b.getZipClusterId() != null)
            {
                bus.post(new BookingEvent.RequestZipClusterPolygons(b.getZipClusterId()));
            }
        }

        DateButtonView dateButtonView = dateButtonMap.get(event.day);
        if (dateButtonView != null)
        {
            dateButtonView.showRequestedIndicator(shouldShowRequestedIndicator(bookings));
            dateButtonView.showClaimedIndicator(shouldShowClaimedIndicator(bookings));
        }
        else
        {
            Crashlytics.logException(new RuntimeException("Date button for " + event.day + " not found"));
        }

        if (selectedDay.equals(event.day))
        {
            displayBookings(bookings, selectedDay);
        }
    }

    protected void handleBookingsRetrievalError(HandyEvent.ReceiveBookingsError event, int errorStateStringId)
    {
        refreshLayout.setRefreshing(false);
        if (event.days.contains(selectedDay))
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            if (event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
            {
                errorText.setText(R.string.error_fetching_connectivity_issue);
            }
            else
            {
                errorText.setText(errorStateStringId);
            }
            fetchErrorView.setVisibility(View.VISIBLE);
        }
    }

    private void initDateButtons()
    {
        LinearLayout datesLayout = getDatesLayout();
        datesLayout.removeAllViews();

        dateButtonMap = new HashMap<>(numberOfDaysToDisplay());

        Context context = getActivity();

        for (int i = 0; i < numberOfDaysToDisplay(); i++)
        {
            LayoutInflater.from(context).inflate(R.layout.element_date_button, datesLayout);
            final DateButtonView dateButtonView = (DateButtonView) datesLayout.getChildAt(datesLayout.getChildCount() - 1);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, i);
            final Date day = DateTimeUtils.getDateWithoutTime(calendar.getTime());

            dateButtonView.init(day);
            dateButtonView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    bus.post(new HandyEvent.DateClicked(getTrackingType(), day));
                    selectDay(day);
                    beforeRequestBookings();
                    requestBookings(Lists.newArrayList(day), true, true);
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
        bookingsForSelectedDay = bookings;
        getBookingListView().populateList(bookings, getBookingElementViewClass());
        initListClickListener();
        getNoBookingsView().setVisibility(bookings.size() > 0 ? View.GONE : View.VISIBLE);
        afterDisplayBookings(bookings, dateOfBookings);
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
                    int oneBasedIndex = position + 1;
                    if (getTrackingType().equalsIgnoreCase(getString(R.string.available_job)))
                    {
                        bus.post(new LogEvent.AddLogEvent(mEventLogFactory
                                .createAvailableJobClickedLog(booking, oneBasedIndex)));
                    }
                    else if (getTrackingType().equalsIgnoreCase(getString(R.string.scheduled_job)))
                    {
                        bus.post(new LogEvent.AddLogEvent(mEventLogFactory
                                .createScheduledJobClickedLog(booking, oneBasedIndex)));
                    }
                    bus.post(new HandyEvent.BookingSelected(getTrackingType(), booking.getId()));
                    showBookingDetails(booking);
                }
            }
        });
    }

    private void showBookingDetails(Booking booking)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putString(BundleKeys.BOOKING_TYPE, booking.getType().toString());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
        HandyEvent.NavigateToTab event = new HandyEvent.NavigateToTab(MainViewTab.DETAILS, arguments);
        bus.post(event);
    }

}
