package com.handy.portal.ui.element.bookings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.widget.CheckListItemView;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomerRequestsView extends FrameLayout
{
    @Bind(R.id.booking_details_job_instructions_checklist_title_text)
    TextView mSectionTitleText;
    @Bind(R.id.booking_details_job_instructions_checklist_icon)
    ImageView mSectionIcon;
    @Bind(R.id.booking_details_job_instructions_checklist_entries_layout)
    LinearLayout mEntriesLayout;

    private List<CheckListItemView> mCheckBoxEntries = new LinkedList<>();

    public CustomerRequestsView(
            final Context context, final String sectionTitle, @Nullable final Integer sectionIconId,
            @NonNull final List<Booking.BookingInstruction> entries)
    {
        super(context);
        init();
        setDisplay(sectionTitle, sectionIconId, entries);
    }

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

    public void setDisplay(String sectionTitle, @Nullable Integer sectionIconId,
                           @NonNull List<Booking.BookingInstruction> instructions)
    {
        mSectionTitleText.setText(sectionTitle);
        if (sectionIconId != null)
        {
            mSectionIcon.setImageResource(sectionIconId);
        }

        for (Booking.BookingInstruction instruction : instructions)
        {
            CheckListItemView checkListItemView = new CheckListItemView(getContext());
            checkListItemView.refreshDisplay(
                    instruction.isFinished(), instruction.getTitle(), instruction.getDescription());
            mEntriesLayout.addView(checkListItemView);
            mCheckBoxEntries.add(checkListItemView);
        }
    }

    public boolean isChecked()
    {
        for (CheckListItemView checkListItemView : mCheckBoxEntries)
        {
            if (checkListItemView.isChecked()) { return true; }
        }
        return false;
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_booking_details_job_instructions_checklist, this);
        ButterKnife.bind(this);
    }
}
