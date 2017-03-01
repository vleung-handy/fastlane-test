package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.ui.widget.InstructionCheckItemView;

import java.util.LinkedList;
import java.util.List;

public class CustomerRequestsView extends LinearLayout {
    private List<InstructionCheckItemView> mCheckBoxEntries = new LinkedList<>();

    public CustomerRequestsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerRequestsView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDisplay(@NonNull List<Booking.BookingInstructionUpdateRequest> customerPreferences) {
        for (Booking.BookingInstructionUpdateRequest instruction : customerPreferences) {
            InstructionCheckItemView checkItem = new InstructionCheckItemView(getContext());
            checkItem.refreshDisplay(instruction);
            addView(checkItem);
            mCheckBoxEntries.add(checkItem);
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        for (InstructionCheckItemView entry : mCheckBoxEntries) {
            entry.setEnabled(enabled);
        }
    }
}
