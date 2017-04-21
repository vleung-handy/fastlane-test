package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DismissableBookingElementView extends BookingElementView {
    @BindView(R.id.available_booking_container)
    ViewGroup mAvailableBookingContainer;
    @BindView(R.id.dismissible_booking_aux_text)
    TextView mAuxText;
    @BindView(R.id.dismissible_booking_aux_divider)
    View mAuxDivider;

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

        if (booking.getAuxiliaryInfo() != null) {
            mAuxText.setVisibility(View.VISIBLE);
            mAuxDivider.setVisibility(View.VISIBLE);
            mAuxText.setText(booking.getAuxiliaryInfo().getText());
            mAuxText.setCompoundDrawablesWithIntrinsicBounds(booking.getAuxiliaryInfo().getIconDrawableRes(), 0, 0, 0);
        }
        else {
            mAuxText.setVisibility(View.GONE);
            mAuxDivider.setVisibility(View.GONE);
        }

        mAssociatedView = convertView;
        return convertView;
    }
}
