package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DismissableBookingElementView extends BookingElementView {
    @BindView(R.id.available_booking_container)
    ViewGroup mAvailableBookingContainer;

    @Override
    public View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent) {
        View availableBookingView;
        if (convertView != null) {
            ButterKnife.bind(this, convertView);
            availableBookingView = mAvailableBookingContainer.getChildAt(0);
        }
        else {
            convertView = LayoutInflater.from(parentContext)
                    .inflate(R.layout.element_dismissable_booking_list_entry, parent, false);
            ButterKnife.bind(this, convertView);
            availableBookingView = null;
        }
        final BookingElementMediator availableBookingElementMediator = new BookingElementMediator(
                parent.getContext(),
                booking,
                availableBookingView,
                mAvailableBookingContainer,
                AvailableBookingElementView.class);
        if (mAvailableBookingContainer.getChildCount() == 0) {
            mAvailableBookingContainer.addView(availableBookingElementMediator.getAssociatedView());
        }
        mAssociatedView = convertView;
        return convertView;
    }
}
