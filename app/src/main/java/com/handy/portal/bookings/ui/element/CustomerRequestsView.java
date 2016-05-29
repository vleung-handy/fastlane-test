package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.ui.widget.InstructionCheckItemView;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomerRequestsView extends FrameLayout
{
    @Bind(R.id.booking_details_job_instructions_checklist_entries_layout)
    LinearLayout mEntriesLayout;

    private List<InstructionCheckItemView> mCheckBoxEntries = new LinkedList<>();

    public CustomerRequestsView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CustomerRequestsView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public void setDisplay(@NonNull List<Booking.BookingInstructionUpdateRequest> customerPreferences)
    {
        for (Booking.BookingInstructionUpdateRequest instruction : customerPreferences)
        {
            InstructionCheckItemView checkItem = new InstructionCheckItemView(getContext());
            checkItem.refreshDisplay(instruction);
            mEntriesLayout.addView(checkItem);
            mCheckBoxEntries.add(checkItem);
        }
    }

    @Override
    public void setEnabled(final boolean enabled)
    {
        super.setEnabled(enabled);
        for (InstructionCheckItemView entry : mCheckBoxEntries)
        {
            entry.setEnabled(enabled);
        }
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_booking_details_job_instructions_checklist, this);
        ButterKnife.bind(this);
    }
}
