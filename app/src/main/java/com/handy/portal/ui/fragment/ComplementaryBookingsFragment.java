package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.element.AvailableBookingElementView;
import com.handy.portal.ui.element.BookingElementMediator;
import com.handy.portal.ui.element.BookingElementView;
import com.handy.portal.ui.element.ScheduledBookingElementView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ComplementaryBookingsFragment extends InjectedFragment
{
    @InjectView(R.id.loading_overlay)
    View loadingOverlay;
    @InjectView(R.id.complementary_bookings_empty)
    View noBookingsView;
    @InjectView(R.id.complementary_bookings_banner_text)
    TextView bannerText;
    @InjectView(R.id.complementary_bookings_banner_close_button)
    View closeButton;
    @InjectView(R.id.earlier_bookings)
    ViewGroup earlierBookingsContainer;
    @InjectView(R.id.later_bookings)
    ViewGroup laterBookingsContainer;
    @InjectView(R.id.claimed_bookings)
    ViewGroup claimedBookingsContainer;
    @InjectView(R.id.fetch_error_view)
    View errorView;
    @InjectView(R.id.fetch_error_text)
    TextView errorText;

    private Booking claimedBooking;

    private static final String BOOKING_SOURCE = "matching jobs";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        claimedBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);

        View view = inflater.inflate(R.layout.fragment_complementary_bookings, container, false);
        ButterKnife.inject(this, view);

        loadingOverlay.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        requestComplementaryBookings();
    }

    @OnClick(R.id.all_jobs_button)
    public void onGoToAllJobsClicked()
    {
        Bundle arguments = new Bundle();
        arguments.putLong(BundleKeys.DATE_EPOCH_TIME, claimedBooking.getStartDate().getTime());
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS, arguments));
    }

    @OnClick(R.id.try_again_button)
    public void onTryAgainClicked()
    {
        requestComplementaryBookings();
    }

    private void requestComplementaryBookings()
    {
        errorView.setVisibility(View.GONE);
        loadingOverlay.setVisibility(View.VISIBLE);
        bus.post(new HandyEvent.RequestComplementaryBookings(claimedBooking));
    }

    @Subscribe
    public void onReceiveComplementaryBookingsSuccess(HandyEvent.ReceiveComplementaryBookingsSuccess event)
    {
        loadingOverlay.setVisibility(View.GONE);
        if (event.bookings.isEmpty())
        {
            bannerText.setText(R.string.no_matching_jobs);
            noBookingsView.setVisibility(View.VISIBLE);
        }
        else
        {
            bannerText.setText(getString(R.string.n_matching_jobs, event.bookings.size()));
            displayBookings(Lists.newArrayList(event.bookings));
        }
    }

    @Subscribe
    public void onReceiveComplementaryBookingsError(HandyEvent.ReceiveComplementaryBookingsError event)
    {
        loadingOverlay.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorText.setText(R.string.error_fetching_connectivity_issue);
    }

    private void displayBookings(List<Booking> bookings)
    {
        claimedBookingsContainer.removeAllViews();
        earlierBookingsContainer.removeAllViews();
        laterBookingsContainer.removeAllViews();

        View claimedBookingEntryView = createBookingEntryView(claimedBooking, claimedBookingsContainer, ScheduledBookingElementView.class);
        claimedBookingsContainer.addView(claimedBookingEntryView);
        claimedBookingEntryView.setOnClickListener(new ShowBookingDetailsClickListener(bus, claimedBooking));
        claimedBookingEntryView.findViewById(R.id.booking_entry_claimed_indicator).setVisibility(View.VISIBLE);

        Collections.sort(bookings);
        for (Booking booking : bookings)
        {
            ViewGroup container = booking.getStartDate().before(claimedBooking.getStartDate()) ? earlierBookingsContainer : laterBookingsContainer;
            View bookingEntryView = createBookingEntryView(booking, container, AvailableBookingElementView.class);
            container.addView(bookingEntryView);

            bookingEntryView.setOnClickListener(new ShowBookingDetailsClickListener(bus, booking));
        }
    }

    private View createBookingEntryView(Booking booking, ViewGroup container, Class<? extends BookingElementView> viewClass)
    {
        BookingElementMediator mediator = new BookingElementMediator(getActivity(), booking, null, container, viewClass);
        return mediator.getAssociatedView();
    }

    private static class ShowBookingDetailsClickListener implements View.OnClickListener
    {
        private final Bus bus;
        private final Booking booking;

        ShowBookingDetailsClickListener(Bus bus, Booking booking)
        {
            this.bus = bus;
            this.booking = booking;
        }

        @Override
        public void onClick(View view)
        {
            Bundle arguments = new Bundle();
            arguments.putString(BundleKeys.BOOKING_ID, booking.getId());
            arguments.putString(BundleKeys.BOOKING_SOURCE, BOOKING_SOURCE);
            bus.post(new HandyEvent.NavigateToTab(MainViewTab.DETAILS, arguments));
        }
    }
}
