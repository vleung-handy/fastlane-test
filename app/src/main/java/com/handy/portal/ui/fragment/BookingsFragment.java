package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
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

    protected abstract HandyEvent getRequestEvent(Date day);

    protected abstract boolean showRequestedIndicator(List<Booking> bookingsForDay);

    protected abstract boolean showClaimedIndicator(List<Booking> bookingsForDay);

    protected abstract int numberOfDaysToDisplay();

    protected abstract void setupCTAButton(List<Booking> bookingsForDay, Date dateOfBookings);

    protected abstract Class<? extends BookingElementView> getBookingElementViewClass();

    //Event listeners
    public abstract void onBookingsRetrieved(T event);

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
                this.selectedDay = DateTimeUtils.getDateWithoutTime(new Date(getArguments().getLong(BundleKeys.DATE_EPOCH_TIME)));
            }
        }

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!MainActivityFragment.clearingBackStack)
        {
            initDateButtons();

            if (selectedDay != null && dateButtonMap.containsKey(selectedDay))
            {
                dateButtonMap.get(selectedDay).performClick();
            }
            else
            {
                getDatesLayout().getChildAt(0).performClick();
            }
        }
    }

    @OnClick(R.id.try_again_button)
    public void doRequestBookingsAgain()
    {
        requestBookings(selectedDay, true);
    }

    protected void requestBookings(Date day, boolean showOverlay)
    {
        fetchErrorView.setVisibility(View.GONE);
        if (showOverlay)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        }
        bus.post(getRequestEvent(day));
    }

    private void requestBookingsForOtherDays(Date dayToExclude)
    {
        for (int i = 0; i < numberOfDaysToDisplay(); i++)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, i);
            Date day = DateTimeUtils.getDateWithoutTime(calendar.getTime());

            if (!day.equals(dayToExclude))
            {
                requestBookings(day, false);
            }
        }
    }

    protected void handleBookingsRetrieved(HandyEvent.ReceiveBookingsSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        List<Booking> bookings = event.bookings;
        Collections.sort(bookings);

        DateButtonView dateButtonView = dateButtonMap.get(event.day);
        dateButtonView.showRequestedIndicator(showRequestedIndicator(bookings));
        dateButtonView.showClaimedIndicator(showClaimedIndicator(bookings));

        if (selectedDay.equals(event.day))
        {
            displayBookings(bookings, selectedDay);
            bookingsContentView.setVisibility(View.VISIBLE);
            requestBookingsForOtherDays(selectedDay);
        }
    }

    protected void handleBookingsRetrievalError(HandyEvent.ReceiveBookingsError event)
    {
        if (selectedDay.equals(event.day))
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            if (event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
            {
                errorText.setText(R.string.error_fetching_connectivity_issue);
            }
            else
            {
                errorText.setText(R.string.error_fetching_available_jobs);
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
                    requestBookings(day, true);
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
                    showBookingDetails(booking);
                }
            }
        });
    }

    private void showBookingDetails(Booking booking)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
        arguments.putLong(BundleKeys.BOOKING_DATE, booking.getStartDate().getTime());
        HandyEvent.NavigateToTab event = new HandyEvent.NavigateToTab(MainViewTab.DETAILS, arguments);
        bus.post(event);
    }

}
