package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.element.AvailableBookingElementView;
import com.handy.portal.ui.element.BookingElementMediator;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ComplementaryBookingsFragment extends InjectedFragment
{
    @InjectView(R.id.loading_overlay)
    View loadingOverlay;
    @InjectView(R.id.available_bookings_empty)
    View noBookingsView;
    @InjectView(R.id.complementary_bookings_banner_close_button)
    View closeButton;
    @InjectView(R.id.complementary_bookings)
    ViewGroup complementaryBookingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

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
        String bookingId = getArguments().getString(BundleKeys.BOOKING_ID);
        bus.post(new HandyEvent.RequestComplementaryBookings(bookingId));
    }

    @Subscribe
    public void onReceiveComplementaryBookingsSuccess(HandyEvent.ReceiveComplementaryBookingsSuccess event)
    {
        loadingOverlay.setVisibility(View.GONE);
        if (event.bookings.isEmpty())
        {
            noBookingsView.setVisibility(View.VISIBLE);
        }
        else
        {
            displayBookings(event.bookings);
        }
    }

    private void displayBookings(List<Booking> bookings)
    {
        for (Booking booking : bookings)
        {
            BookingElementMediator bem = new BookingElementMediator(getActivity(), booking, null, complementaryBookingView, AvailableBookingElementView.class);
            complementaryBookingView.addView(bem.getAssociatedView());
        }
    }
}
